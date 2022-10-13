package onde.there.place.controller;

import lombok.RequiredArgsConstructor;
import onde.there.place.service.PlaceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

	private final PlaceService placeService;
}
