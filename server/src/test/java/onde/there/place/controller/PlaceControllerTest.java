package onde.there.place.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import onde.there.domain.Journey;
import onde.there.domain.Place;
import onde.there.domain.type.PlaceCategoryType;
import onde.there.exception.type.ErrorCode;
import onde.there.place.exception.PlaceException;
import onde.there.place.exception.type.PlaceErrorCode;
import onde.there.place.service.PlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PlaceController.class)
class PlaceControllerTest {

	@MockBean
	private PlaceService placeService;

	@InjectMocks
	private PlaceController placeController;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;


	@DisplayName("01_00. /place/get?placeId=1  success")
	@Test
	public void test_01_00() throws Exception {
		//given
		given(placeService.getPlace(any())).willReturn(testPlace(1L));

		//when
		mvc.perform(get("/place/get?placeId=1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.placeId").value(1L))
			.andExpect(jsonPath("$.title").value("장소 테스트 제목"))
			.andExpect(jsonPath("$.text").value("장소 테스트 본문"))
			.andExpect(jsonPath("$.addressName").value("장소 테스르 전체 주소"))
			.andExpect(jsonPath("$.placeHeartSum").value(0))
			.andExpect(jsonPath("$.placeCategory").value("기타"))
			.andDo(print());
		//then
	}

	@DisplayName("01_01. /place/get?placeId=1  fail not found place")
	@Test
	public void test_01_01() throws Exception {
		//given
		given(placeService.getPlace(any())).willThrow(
			new PlaceException(PlaceErrorCode.NOT_FOUND_PLACE));

		System.out.println(ErrorCode.NOT_FOUND_PLACE.getDescription());
		//when
		mvc.perform(get("/place/get?placeId=1"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.placeErrorCode").value(PlaceErrorCode.NOT_FOUND_PLACE.toString()))
			.andExpect(jsonPath("$.errorMessage").value(PlaceErrorCode.NOT_FOUND_PLACE.getDescription()))
			.andDo(print());
		//then
	}

	@DisplayName("02_00. /place/list success")
	@Test
	public void test_02_00() throws Exception {
		//given
		given(placeService.list(any())).willReturn(List.of(
			testPlace(1L),
			testPlace(2L),
			testPlace(3L)
		));

		//when
		mvc.perform(get("/place/list?journeyId=1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(3))
			.andDo(print());

		//then
	}

	@DisplayName("02_01. /place/list fail not found journey")
	@Test
	public void test_02_01() throws Exception {
		//given
		given(placeService.list(any())).willThrow(new PlaceException(PlaceErrorCode.NOT_FOUND_JOURNEY));

		//when
		mvc.perform(get("/place/list?journeyId=1"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.placeErrorCode").value(PlaceErrorCode.NOT_FOUND_JOURNEY.toString()))
			.andExpect(
				jsonPath("$.errorMessage").value(PlaceErrorCode.NOT_FOUND_JOURNEY.getDescription()))
			.andDo(print());
		//then
	}

	@DisplayName("03_00. /place/delete success")
	@Test
	public void test_03_00() throws Exception {
		//given
		given(placeService.delete(any())).willReturn(true);

		//when
		mvc.perform(delete("/place/delete?placeId=1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(true))
			.andDo(print())
		;
		//then
	}

	@DisplayName("03_01. /place/delete fail not deleted ")
	@Test
	public void test_03_01() throws Exception {
		//given
		given(placeService.delete(any())).willThrow(new PlaceException(PlaceErrorCode.NOT_FOUND_PLACE));

		//when
		mvc.perform(delete("/place/delete?placeId=1"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.placeErrorCode").value(PlaceErrorCode.NOT_FOUND_PLACE.toString()))
			.andDo(print())
		;
	}
	@DisplayName("04_00. /place/delete-all success")
	@Test
	public void test_04_00() throws Exception {
		//given
		given(placeService.deleteAll(any())).willReturn(true);

		//when
		mvc.perform(delete("/place/delete-all?journeyId=1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(true))
			.andDo(print());

		//then
	}

	@DisplayName("04_01. /place/delete-all fail not found journey id")
	@Test
	public void test_04_01() throws Exception {
		//given
		given(placeService.deleteAll(any())).willThrow(new PlaceException(PlaceErrorCode.NOT_FOUND_JOURNEY));

		//when
		mvc.perform(delete("/place/delete-all?journeyId=1"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.placeErrorCode").value(ErrorCode.NOT_FOUND_JOURNEY.toString()))
			.andDo(print());

		//then
	}

	@DisplayName("04_02. /place/delete-all fail deleted nothing")
	@Test
	public void test_04_02() throws Exception {
		//given
		given(placeService.deleteAll(any())).willThrow(new PlaceException(PlaceErrorCode.DELETED_NOTING));

		//when
		mvc.perform(delete("/place/delete-all?journeyId=1"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.placeErrorCode").value(PlaceErrorCode.DELETED_NOTING.toString()))
			.andDo(print());

		//then
	}


	private static Place testPlace(Long id) {
		return Place.builder()
			.id(id)
			.title("장소 테스트 제목")
			.text("장소 테스트 본문")
			.addressName("장소 테스르 전체 주소")
			.placeHeartSum(0L)
			.journey(Journey.builder().build())
			.placeCategory(PlaceCategoryType.ECT)
			.build();
	}
}