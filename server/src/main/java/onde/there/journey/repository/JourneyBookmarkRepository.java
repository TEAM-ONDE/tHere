package onde.there.journey.repository;

import java.util.List;
import java.util.Optional;
import onde.there.domain.JourneyBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JourneyBookmarkRepository extends JpaRepository<JourneyBookmark, Long> {
	Optional<List<JourneyBookmark>> findAllByMemberId(String id);
}
