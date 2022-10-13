package onde.there.place.controller;

import lombok.RequiredArgsConstructor;
import onde.there.place.service.PlaceCategoryService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlaceCategoryController {

	private final PlaceCategoryService placeCategoryService;
}
