package backend.service;

import backend.model.UserActivity;
import backend.repository.UserActivityRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;
    private final EntityManager entityManager;

    public UserActivityService(UserActivityRepository userActivityRepository, EntityManager entityManager) {
        this.userActivityRepository = userActivityRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public List<UserActivity> logActivity(List<UserActivity> userActivities) {
        List<UserActivity> savedActivities = new ArrayList<>();
        int batchSize = 50; // Размер пакета

        for (int i = 0; i < userActivities.size(); i++) {
            UserActivity activity = userActivities.get(i);
            if (activity != null) {
                userActivityRepository.save(activity); // Сохранение активности
                savedActivities.add(activity); // Добавление в список сохраненных активностей

                // Пакетная обработка
                if (i > 0 && i % batchSize == 0) {
                    entityManager.flush(); // Синхронизация с базой данных
                    entityManager.clear(); // Очистка контекста Persistence
                }
            }
        }

        return savedActivities; // Возвращаем список сохраненных активностей
    }

    public List<UserActivity> getAllUserActivities() {
        return userActivityRepository.findAll();
    }

    public List<UserActivity> getUserActivityByIp(String ip) {
        return userActivityRepository.findByUserIp(ip);
    }
}
