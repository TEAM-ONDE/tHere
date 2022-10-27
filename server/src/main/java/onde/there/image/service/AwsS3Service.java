package onde.there.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Place;
import onde.there.exception.PlaceException;
import onde.there.exception.type.ErrorCode;
import onde.there.image.exception.ImageErrorCode;
import onde.there.image.exception.ImageException;
import onde.there.place.repository.PlaceImageRepository;
import onde.there.place.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

	private final AmazonS3 amazonS3;
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.baseUrl}")
	private String baseUrl;

	private final PlaceRepository placeRepository;
	private final PlaceImageRepository placeImageRepository;

	@Transactional
	public List<String> uploadFiles(List<MultipartFile> multipartFiles) {
		List<String> urlList = new ArrayList<>();
		if (multipartFiles.isEmpty()) {
			throw new ImageException(ImageErrorCode.EMPTY_FILE);
		}
		multipartFiles.forEach(file -> {
			String fileName = createFileName(file.getOriginalFilename());
			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(file.getSize());
			objectMetadata.setContentType(file.getContentType());

			try (InputStream inputStream = file.getInputStream()) {
				amazonS3.putObject(
					new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
						.withCannedAcl(CannedAccessControlList.PublicRead));
			} catch (IOException e) {
				log.info(fileName + " 서버에 저장 실패");
				throw new ImageException(ImageErrorCode.FAILED_UPLOAD);
			}
			log.info(fileName + " 서버에 저장 완료");
			urlList.add(baseUrl + fileName);
		});

		return urlList;
	}

	public void deleteFile(String url) {
		String fileName = url.replaceAll(baseUrl, "");
		amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
	}

	private String createFileName(String fileName) {
		return UUID.randomUUID().toString().concat(getFileExtension(fileName));
	}

	private String getFileExtension(String fileName) {
		try {
			String extension = fileName.substring(fileName.lastIndexOf("."));
			if (extension.equals(".png") || extension.equals(".jpg")) {
				return extension;
			}
			throw new ImageException(ImageErrorCode.NOT_IMAGE_EXTENSION);
		} catch (StringIndexOutOfBoundsException e) {
			throw new ImageException(ImageErrorCode.INVALID_FORMAT_FILE);
		}
	}


	public List<String> findFile(Long id) {
		List<String> imageUrls = new ArrayList<>();
		Place place = placeRepository.findById(id).orElseThrow(() -> new PlaceException(ErrorCode.NOT_FOUND_PLACE));
		placeImageRepository.findAllByPlaceId(place.getId()).forEach(placeImage -> imageUrls.add(placeImage.getUrl()));
		return imageUrls;
	}

	public List<ResponseEntity<byte[]>> getImageFiles(List<String> imageUrls) throws IOException {
		List<ResponseEntity<byte[]>> result = new ArrayList<>();
		HttpHeaders httpHeaders = new HttpHeaders();
		for (String imageUrl : imageUrls) {
			String url = imageUrl.replaceAll(baseUrl, "");
			S3Object o = amazonS3.getObject(new GetObjectRequest(bucket, url));
			S3ObjectInputStream objectInputStream = o.getObjectContent();
			byte[] bytes = IOUtils.toByteArray(objectInputStream);

			String fileName = URLEncoder.encode(url, "UTF-8").replaceAll("\\+", "%20");

			httpHeaders.setContentType(MediaType.IMAGE_PNG);
			httpHeaders.setContentLength(bytes.length);
			httpHeaders.setContentDispositionFormData("attachment", fileName);
			result.add(new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK));
		}

		return result;
	}
}
