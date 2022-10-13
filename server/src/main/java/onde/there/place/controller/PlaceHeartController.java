package onde.there.place.controller;

import lombok.RequiredArgsConstructor;
import onde.there.place.service.PlaceHeartService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlaceHeartController {

	private final PlaceHeartService placeHeartService;
}
