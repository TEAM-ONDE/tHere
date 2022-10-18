package onde.there.place.exception;


import lombok.Getter;
import lombok.Setter;
import onde.there.place.exception.type.PlaceErrorCode;

@Getter
@Setter
public class PlaceException extends RuntimeException{

	private PlaceErrorCode placeErrorCode;
	private String errorMessage;

	public PlaceException(PlaceErrorCode placeErrorCode) {
		this.placeErrorCode = placeErrorCode;
		this.errorMessage = placeErrorCode.getDescription();
	}
}
