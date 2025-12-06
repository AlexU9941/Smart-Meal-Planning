package smart_meal_planner.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import smart_meal_planner.controller.ResetController;
import smart_meal_planner.service.ResetService;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ResetControllerTest {

    @InjectMocks
    private ResetController resetController;

    @Mock
    private ResetService resetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void resetUserPreferences_HappyPath() {
        Long userId = 1L;

        // Call the controller method
        resetController.resetUserPreferences(userId);

        // Verify the service was called
        verify(resetService, times(1)).resetUserPreferences(userId);
    }

    @Test
    void resetUserPreferences_ServiceThrowsException() {
        Long userId = 1L;

        doThrow(new RuntimeException("Reset failed")).when(resetService).resetUserPreferences(userId);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            resetController.resetUserPreferences(userId);
        });

        assertEquals("Reset failed", ex.getMessage());

        verify(resetService, times(1)).resetUserPreferences(userId);
    }
}
