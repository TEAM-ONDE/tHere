package onde.there.place.service;

import lombok.RequiredArgsConstructor;
import onde.there.place.repository.PlaceHeartRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceHeartService {
	private final PlaceHeartRepository placeHeartRepository;
}
