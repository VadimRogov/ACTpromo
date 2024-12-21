package backend.repository;

import backend.model.EventType;
import backend.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    List<UserActivity> findByUserIp(String userIp);

    // JPQL запрос для группировки и суммирования
    @Query("SELECT u.eventType, SUM(u.countEvent) " +
            "FROM UserActivity u " +
            "GROUP BY u.eventType")
    List<Object[]> findInteractiveElementStats();

    // Найти все события с типом MAIN_SHOP
    List<UserActivity> findByEventType(EventType eventType);
}
