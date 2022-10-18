package onde.there.exception;

import lombok.extern.slf4j.Slf4j;
import onde.there.exception.type.ErrorCode;
import onde.there.place.exception.PlaceErrorResponse;
import onde.there.place.exception.PlaceException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(
		MethodArgumentNotValidException e) {
		ErrorResponse errorResponse = new ErrorResponse(ErrorCode.BAD_REQUEST,
			e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException e) {
		ErrorResponse errorResponse = new ErrorResponse(ErrorCode.BAD_REQUEST,
			e.getMessage());
		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(PlaceException.class)
	public ResponseEntity<?> handlerPlaceException(PlaceException e) {

		return ResponseEntity.badRequest()
			.body(PlaceErrorResponse.builder()
				.placeErrorCode(e.getPlaceErrorCode())
				.errorMessage(e.getErrorMessage())
				.build());
	}

	@ExceptionHandler(JourneyException.class)
	public ResponseEntity<?> handleJourneyException(JourneyException e) {
		log.error("{} is occurred.", e.getErrorCode());

		return ResponseEntity.badRequest()
			.body(new ErrorResponse(e.getErrorCode(), e.getErrorMessage()));
	}
}
