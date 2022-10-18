package onde.there.place.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlaceErrorCode {
	NOT_FOUND_JOURNEY("존재하지 않는 여정입니다."),
	NOT_FOUND_PLACE("존재하지 않는 장소입니다."),
	BAD_REQUEST("입력 값을 확인해 주세요!"),
	MISMATCH_PLACE_CATEGORY_TYPE("일치하는 카테고리 타입이 없습니다."),
	DELETED_NOTING("이 여정에는 삭제할 장소가 없습니다."),
	DATE_ERROR("잘못된 날짜입니다."),
	;
	private final String description;
}
