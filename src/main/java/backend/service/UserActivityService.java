package backend.service;

import backend.model.UserActivity;
import backend.repository.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

@Service
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;

    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public List<UserActivity> logActivity(List<UserActivity> userActivity) {
        List<UserActivity> userActivities = new ArrayList<>();
        for (UserActivity activy: userActivity) {
            if (activy != null) {
                userActivityRepository.save(activy);
                userActivities.add(activy);
            }
        }
        return userActivities;
    }

    public List<UserActivity> getAllUserActivities() {
        return userActivityRepository.findAll();
    }

    public List<UserActivity> getUserActivityByIp(String ip) {
        return userActivityRepository.findByUserIp(ip);
    }
}
