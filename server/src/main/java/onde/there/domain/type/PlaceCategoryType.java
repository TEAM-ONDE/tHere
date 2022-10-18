package onde.there.domain.type;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import onde.there.place.exception.PlaceException;
import onde.there.place.exception.type.PlaceErrorCode;

@Getter
@RequiredArgsConstructor
public enum PlaceCategoryType {
	NATURE("자연"),
	ACCOMMODATION("숙박"),
	RESTAURANT("음식점"),
	LEISURE("레저"),
	THEME_PARK("테마파크"),
	SHOPPING("쇼핑"),
	HISTORICAL_SITE("유적지"),
	MUSEUM("박물관"),
	CONCERT("공연"),
	EXHIBITION("전시회"),
	CAMPING("캠핑"),
	KIDS("키즈"),
	ECT("기타"),
	;
	private final String description;

	public static PlaceCategoryType toPlaceCategoryType(String s) {
		return Arrays.stream(PlaceCategoryType.values())
			.filter(x -> s.equals(x.getDescription())).findFirst()
			.orElseThrow(() -> new PlaceException(PlaceErrorCode.MISMATCH_PLACE_CATEGORY_TYPE));
	}
}
