package onde.there.place.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import onde.there.domain.Journey;
import onde.there.domain.Place;
import onde.there.journey.repository.JourneyRepository;
import onde.there.place.exception.PlaceException;
import onde.there.place.exception.type.PlaceErrorCode;
import onde.there.place.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

	private final PlaceRepository placeRepository;

	private final JourneyRepository journeyRepository;

	public Place getPlace(Long placeId) {
		return placeRepository.findById(placeId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_PLACE));
	}

	public List<Place> list(Long journeyId) {
		Journey journey = journeyRepository.findById(journeyId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_JOURNEY));

		return placeRepository.findAllByJourneyOrderByPlaceTimeAsc(journey);
	}

	@Transactional
	public boolean delete(Long placeId) {
		boolean exists = placeRepository.existsById(placeId);
		if (!exists) {
			throw new PlaceException(PlaceErrorCode.NOT_FOUND_PLACE);
		}

		placeRepository.deleteById(placeId);

		//TODO: image 제거 로직 -> 이미지 추가 삭제 부분 머지후 구현 예정

		return true;
	}

	@Transactional
	public boolean deleteAll(Long journeyId) {
		journeyRepository.findById(journeyId)
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.NOT_FOUND_JOURNEY));
		//TODO : 장소에 포함된 모든 댓글 좋아요 이미지 삭제 구현 필요

		Integer result = placeRepository.deleteAllByJourneyId(journeyId);
		if (result == 0) {
			throw new PlaceException(PlaceErrorCode.DELETED_NOTING);
		}

		return true;
	}
}
