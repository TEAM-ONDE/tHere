package onde.there.place.service;

import lombok.RequiredArgsConstructor;
import onde.there.place.repository.PlaceCategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceCategoryService {

	private final PlaceCategoryRepository placeCategoryRepository;
}
