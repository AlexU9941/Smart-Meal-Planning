package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import smart_meal_planner.controller.UserHealthInfoController;
import smart_meal_planner.model.User;
import smart_meal_planner.model.UserHealthInfo;
import smart_meal_planner.repository.UserRepository;
import smart_meal_planner.service.UserHealthInfoService;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserHealthInfoControllerTest {

    @InjectMocks
    private UserHealthInfoController controller;

    @Mock
    private UserHealthInfoService service;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addHealthInfo_ShouldSaveInfo_WhenUserExists() {
        UserHealthInfo info = new UserHealthInfo();
        info.setEmail("test@example.com");

        User user = new User();
        user.setUID(1L);

        when(userRepository.findByEmail(info.getEmail())).thenReturn(user);

        ResponseEntity<?> response = controller.addHealthInfo(info);

        assertEquals(200, response.getStatusCodeValue());
        verify(service, times(1)).saveUserHealthInfo(info);
        assertEquals(user, info.getUser());
    }

    @Test
    void addHealthInfo_ShouldReturnNotFound_WhenUserDoesNotExist() {
        UserHealthInfo info = new UserHealthInfo();
        info.setEmail("unknown@example.com");

        when(userRepository.findByEmail(info.getEmail())).thenReturn(null);

        ResponseEntity<?> response = controller.addHealthInfo(info);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getHealthInfo_ShouldReturnOptional() {
        Long id = 1L;
        UserHealthInfo info = new UserHealthInfo();
        info.setUID(id);

        when(service.getUserHealthInfoById(id)).thenReturn(Optional.of(info));

        Optional<UserHealthInfo> result = controller.getHealthInfo(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getUID());
    }

    @Test
    void updateHealthInfo_ShouldCallService() {
        Long id = 1L;
        UserHealthInfo updated = new UserHealthInfo();
        updated.setUID(id);

        when(service.updateUserHealthInfo(id, updated)).thenReturn(updated);

        UserHealthInfo result = controller.updateHealthInfo(id, updated);

        assertEquals(updated, result);
        verify(service, times(1)).updateUserHealthInfo(id, updated);
    }

    @Test
    void getHealthInfoByEmail_ShouldReturnInfoByEmail() {
        String email = "test@example.com";
        UserHealthInfo info = new UserHealthInfo();
        info.setEmail(email);

        when(service.findByEmail(email)).thenReturn(info);

        ResponseEntity<?> response = controller.getHealthInfoByEmail(email, null);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(info, response.getBody());
    }

    @Test
    void getHealthInfoByEmail_ShouldReturnInfoByUsername() {
        String username = "testuser";
        User user = new User();
        user.setUID(1L);
        UserHealthInfo info = new UserHealthInfo();

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(service.findByUserUID(user.getUID())).thenReturn(info);

        ResponseEntity<?> response = controller.getHealthInfoByEmail(null, username);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(info, response.getBody());
    }

    @Test
    void getHealthInfoByEmail_ShouldReturnNotFound() {
        when(service.findByEmail("unknown@example.com")).thenReturn(null);

        ResponseEntity<?> response = controller.getHealthInfoByEmail("unknown@example.com", null);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void estimateCalories_ShouldReturnCalculatedValues() {
        UserHealthInfo info = new UserHealthInfo();
        info.setEmail("test@example.com");
        info.setSex("male");
        info.setHeightFt(5);
        info.setHeightIn(10);
        info.setWeight(160);
        info.setDateOfBirth(LocalDate.now().minusYears(30));
        info.setWeeklyActivityLevel("moderate");

        when(service.findByEmail("test@example.com")).thenReturn(info);

        ResponseEntity<?> response = controller.estimateCalories("test@example.com", null);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((java.util.Map<?, ?>) response.getBody()).containsKey("dailyCalorieGoal"));
        assertTrue(((java.util.Map<?, ?>) response.getBody()).containsKey("weeklyCalorieGoal"));
    }

    @Test
    void estimateCalories_ShouldReturnNotFound_WhenNoInfo() {
        when(service.findByEmail("unknown@example.com")).thenReturn(null);

        ResponseEntity<?> response = controller.estimateCalories("unknown@example.com", null);

        assertEquals(404, response.getStatusCodeValue());
    }
}
