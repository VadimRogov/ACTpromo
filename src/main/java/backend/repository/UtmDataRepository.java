package backend.repository;

import backend.model.UtmData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtmDataRepository extends JpaRepository<UtmData, Long> {

}