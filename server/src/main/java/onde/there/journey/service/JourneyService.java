package onde.there.journey.service;

import static onde.there.domain.type.RegionType.findByRegion;
import static onde.there.journey.exception.JourneyErrorCode.DATE_ERROR;
import static onde.there.journey.exception.JourneyErrorCode.NOT_FOUND_JOURNEY;
import static onde.there.journey.exception.JourneyErrorCode.NOT_FOUND_MEMBER;
import static onde.there.journey.exception.JourneyErrorCode.YOU_ARE_NOT_THE_AUTHOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onde.there.domain.Journey;
import onde.there.domain.JourneyTheme;
import onde.there.domain.Member;
import onde.there.domain.Place;
import onde.there.domain.type.JourneyThemeType;
import onde.there.dto.journy.JourneyDto;
import onde.there.dto.journy.JourneyDto.DetailResponse;
import onde.there.dto.journy.JourneyDto.FilteringResponse;
import onde.there.dto.journy.JourneyDto.JourneyListResponse;
import onde.there.dto.journy.JourneyDto.MyListResponse;
import onde.there.dto.journy.JourneyDto.UpdateRequest;
import onde.there.dto.journy.JourneyDto.UpdateResponse;
import onde.there.image.service.AwsS3Service;
import onde.there.journey.exception.JourneyException;
import onde.there.journey.repository.JourneyRepository;
import onde.there.journey.repository.JourneyThemeRepository;
import onde.there.member.repository.MemberRepository;
import onde.there.place.exception.PlaceErrorCode;
import onde.there.place.exception.PlaceException;
import onde.there.place.repository.PlaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class JourneyService {

	private final JourneyRepository journeyRepository;
	private final JourneyThemeRepository journeyThemeRepository;
	private final MemberRepository memberRepository;
	private final AwsS3Service awsS3Service;
	private final PlaceRepository placeRepository;

	@Transactional
	public JourneyDto.CreateResponse createJourney(
		JourneyDto.CreateRequest request, MultipartFile thumbnail,
		String memberId) {

		log.info("createJourney() : ??????");

		Member checkMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new JourneyException(NOT_FOUND_MEMBER));

		if (request.getEndDate().isBefore(request.getStartDate())) {
			throw new JourneyException(DATE_ERROR);
		}

		List<String> imageUrls = awsS3Service.uploadFiles(
			Collections.singletonList(thumbnail));

		log.info("createJourney() : ?????? thumbnail ????????? ??????, "
			+ "(?????? thumbnail URL : " + imageUrls.get(0) + ")");

		Journey journey = Journey.builder()
			.member(checkMember)
			.title(request.getTitle())
			.startDate(request.getStartDate())
			.endDate(request.getEndDate())
			.journeyThumbnailUrl(imageUrls.get(0))
			.disclosure(request.getDisclosure())
			.introductionText(request.getIntroductionText())
			.numberOfPeople(request.getNumberOfPeople())
			.region(findByRegion(request.getRegion()))
			.build();

		journeyRepository.save(journey);
		log.info("createJourney() : ?????? ?????? ??????,  JourneyId : " + journey.getId());

		List<String> inputJourneyThemes = request.getJourneyThemes();
		for (String inputJourneyTheme : inputJourneyThemes) {
			JourneyTheme journeyTheme = JourneyTheme.builder()
				.journey(journey)
				.journeyThemeName(
					JourneyThemeType.findByTheme(inputJourneyTheme))
				.build();
			journeyThemeRepository.save(journeyTheme);
			log.info(
				"createJourney() : journeyTheme ?????? ??????, journeyThemeId : "
					+ journeyTheme.getId());
		}

		log.info("createJourney() : ??????");
		return JourneyDto.CreateResponse.fromEntity(journey,
			inputJourneyThemes);
	}

	public List<JourneyDto.JourneyListResponse> list() {

		log.info("list() : ??????");

		List<JourneyDto.JourneyListResponse> list = new ArrayList<>();
		List<Journey> journeyList = journeyRepository
			.findAllByDisclosure("public");

		log.info("list() : ??????");

		return getList(list, journeyList);
	}

	@Transactional
	public Page<JourneyDto.MyListResponse> myList(
		String memberId, Pageable pageable) {

		log.info("myList() : ??????");

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new JourneyException(NOT_FOUND_MEMBER));

		Page<Journey> journeys = journeyRepository.myList(memberId, pageable);

		log.info("myList() : ?????? ??????");

		return journeys.map(
			MyListResponse::fromEntity
		);
	}

	@Transactional
	public Page<FilteringResponse> filteredList(
		JourneyDto.FilteringRequest filteringRequest, Pageable pageable) {

		log.info("filteredList() : ??????");

		Page<Journey> journeys = journeyRepository.searchAll(filteringRequest,
			pageable);

		log.info("filteredList() : ??????");

		return journeys.map(
			FilteringResponse::fromEntity
		);

	}


	private List<JourneyListResponse> getList(List<JourneyListResponse> list,
		List<Journey> journeyList) {

		log.info("getList() : ??????");

		for (Journey journey : journeyList) {
			List<String> journeyThemeTypeList = journeyThemeRepository
				.findAllByJourneyId(journey.getId())
				.stream().map(journeyTheme -> journeyTheme
					.getJourneyThemeName()
					.getThemeName())
				.collect(Collectors.toList());

			list.add(
				JourneyListResponse.fromEntity(journey, journeyThemeTypeList));
		}

		log.info("getList() : ??????");

		return list;
	}

	public DetailResponse journeyDetail(Long journeyId) {

		log.info("journeyDetail() : ??????");

		Journey journey = journeyRepository.findById(journeyId)
			.orElseThrow(() -> new JourneyException(NOT_FOUND_JOURNEY));

		List<String> journeyThemeTypeList = journeyThemeRepository
			.findAllByJourneyId(journey.getId())
			.stream().map(journeyTheme -> journeyTheme
				.getJourneyThemeName()
				.getThemeName())
			.collect(Collectors.toList());
		log.info(
			"journeyDetail() : journey ?????? ??????, journeyId : " + journey.getId());

		log.info("journeyDetail() : ??????");

		return DetailResponse.fromEntity(journey, journeyThemeTypeList);
	}

	@Transactional
	public void deleteJourney(Long journeyId, String memberId) {

		log.info("deleteJourney() : ??????");

		Journey journey = journeyRepository.findById(journeyId)
			.orElseThrow(() -> new JourneyException(NOT_FOUND_JOURNEY));

		String Author = journey.getMember().getId();
		if (!Objects.equals(Author, memberId)) {
			throw new JourneyException(YOU_ARE_NOT_THE_AUTHOR);
		}

		List<JourneyTheme> journeyThemeTypeList = journeyThemeRepository
			.findAllByJourneyId(journey.getId());

		List<Place> list = placeRepository.findAllByJourneyIdOrderByPlaceTimeAsc(journeyId);

		if (list.size() == 0) {
			throw new PlaceException(PlaceErrorCode.DELETED_NOTING);
		}

		placeRepository.deleteAll(list);
		awsS3Service.deleteFile(journey.getJourneyThumbnailUrl());
		journeyThemeRepository.deleteAll(journeyThemeTypeList);
		journeyRepository.delete(journey);

		log.info("deleteJourney() : ?????? ?????? ??????, journeyId : " + journey.getId());
		log.info("deleteJourney() : ??????");

	}

	@Transactional
	public UpdateResponse updateJourney(UpdateRequest request,
		MultipartFile thumbnail, String memberId) {

		log.info("updateJourney() : ??????");

		Journey journey = journeyRepository.findById(request.getJourneyId())
			.orElseThrow(() -> new JourneyException(NOT_FOUND_JOURNEY));

		String Author = journey.getMember().getId();
		if (!Objects.equals(Author, memberId)) {
			throw new JourneyException(YOU_ARE_NOT_THE_AUTHOR);
		}

		awsS3Service.deleteFile(journey.getJourneyThumbnailUrl());
		List<String> imageUrls = awsS3Service.uploadFiles(
			Collections.singletonList(thumbnail));

		List<JourneyTheme> journeyThemes = journeyThemeRepository
			.findAllByJourneyId(journey.getId());

		journeyThemeRepository.deleteAll(journeyThemes);
		List<String> inputJourneyThemes = request.getJourneyThemes();
		for (String inputJourneyTheme : inputJourneyThemes) {
			JourneyTheme journeyTheme = JourneyTheme.builder()
				.journey(journey)
				.journeyThemeName(
					JourneyThemeType.findByTheme(inputJourneyTheme))
				.build();
			journeyThemeRepository.save(journeyTheme);
		}
		log.info("updateJourney() : journeyTheme ?????? ??????");

		journey.setTitle(request.getTitle());
		journey.setStartDate(request.getStartDate());
		journey.setEndDate(request.getEndDate());
		journey.setNumberOfPeople(request.getNumberOfPeople());
		journey.setDisclosure(request.getDisclosure());
		journey.setIntroductionText(request.getIntroductionText());
		journey.setJourneyThumbnailUrl(imageUrls.get(0));
		journey.setRegion(findByRegion(request.getRegion()));
		log.info("updateJourney() : journey ?????? ??????, journeyId : "
			+ journey.getId());

		log.info("updateJourney() : ??????");

		return JourneyDto.UpdateResponse
			.fromEntity(journey, inputJourneyThemes);
	}
}
