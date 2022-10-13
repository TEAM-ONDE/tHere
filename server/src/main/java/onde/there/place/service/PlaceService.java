package onde.there.place.service;

import lombok.RequiredArgsConstructor;
import onde.there.place.repository.PlaceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final PlaceRepository placeRepository;


}
