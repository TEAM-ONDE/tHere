package onde.there.place.exception;

import lombok.*;
import onde.there.place.exception.type.PlaceErrorCode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PlaceErrorResponse {

	private PlaceErrorCode placeErrorCode;
	private String errorMessage;
}
