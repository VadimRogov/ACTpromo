package backend.service;

import backend.model.UserActivity;
import backend.repository.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;

    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public UserActivity logActivity(UserActivity userActivity) {
        return userActivityRepository.save(userActivity);
    }

    public List<UserActivity> getAllUserActivities() {
        return userActivityRepository.findAll();
    }

    public List<UserActivity> getUserActivityByIp(String ip) {
        return userActivityRepository.findByUserIp(ip);
    }
}
