package onde.there.place.repository;

import onde.there.domain.PlaceHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceHeartRepository extends JpaRepository<PlaceHeart, Long> {

}
