package smart_meal_planner.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import smart_meal_planner.model.UserHealthInfo;
import smart_meal_planner.repository.UserHealthInfoRepository;

@Service
public class UserHealthInfoService {

    @Autowired
    private UserHealthInfoRepository repository;

    public UserHealthInfo saveUserHealthInfo(UserHealthInfo info) {
        return repository.save(info);
    }

    public Optional<UserHealthInfo> getUserHealthInfoById(Long uid) {
        return repository.findById(uid);
    }

    public UserHealthInfo findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public UserHealthInfo findByUserUID(Long uid) {
        return repository.findByUser_UID(uid);
    }

    public UserHealthInfo updateUserHealthInfo(Long uid, UserHealthInfo newInfo) {
        return repository.findById(uid).map(existing -> {
            existing.setHeightFt(newInfo.getHeightFt());
            existing.setHeightIn(newInfo.getHeightIn());
            existing.setWeight(newInfo.getWeight());
            existing.setSex(newInfo.getSex());
            existing.setWeeklyActivityLevel(newInfo.getWeeklyActivityLevel());
            existing.setAllergies(newInfo.getAllergies());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("User not found with id: " + uid));
    }
}
