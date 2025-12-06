package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import smart_meal_planner.controller.UserNutritionalGoalsController;
import smart_meal_planner.model.User;
import smart_meal_planner.model.UserNutritionalGoals;
import smart_meal_planner.repository.UserRepository;
import smart_meal_planner.service.UserNutritionalGoalsService;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserNutritionalGoalsControllerTest {

    @InjectMocks
    private UserNutritionalGoalsController controller;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserNutritionalGoalsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addNutritionalGoals_ShouldSave_WhenUserExists() {
        UserNutritionalGoals goals = new UserNutritionalGoals();
        goals.setEmail("test@example.com");

        User user = new User();
        user.setUID(1L);

        when(userRepository.findByEmail(goals.getEmail())).thenReturn(user);

        ResponseEntity<?> response = controller.addNutritionalGoals(goals);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, goals.getUser());
        verify(service, times(1)).saveUserNutritionalGoals(goals);
    }

    @Test
    void addNutritionalGoals_ShouldReturnNotFound_WhenUserDoesNotExist() {
        UserNutritionalGoals goals = new UserNutritionalGoals();
        goals.setEmail("unknown@example.com");

        when(userRepository.findByEmail(goals.getEmail())).thenReturn(null);

        ResponseEntity<?> response = controller.addNutritionalGoals(goals);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getNutrtionalGoals_ShouldReturnOptional() {
        Long id = 1L;
        UserNutritionalGoals goals = new UserNutritionalGoals();
        goals.setId(id);

        when(service.getUserNutritionalGoals(id)).thenReturn(Optional.of(goals));

        Optional<UserNutritionalGoals> result = controller.getNutrtionalGoals(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void updateNutrtionalGoals_ShouldCallServiceAndReturnUpdated() {
        Long id = 1L;
        UserNutritionalGoals goals = new UserNutritionalGoals();
        goals.setId(id);

        when(service.updateUserNutritionalGoals(id, goals)).thenReturn(goals);

        UserNutritionalGoals result = controller.updateNutrtionalGoals(id, goals);

        assertEquals(goals, result);
        verify(service, times(1)).updateUserNutritionalGoals(id, goals);
    }
}
