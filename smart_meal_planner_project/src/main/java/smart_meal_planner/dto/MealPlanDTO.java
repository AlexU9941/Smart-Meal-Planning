package smart_meal_planner.dto;

import java.util.List;

public class MealPlanDTO {

    private Long userId;
    private List<DayDTO> days;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<DayDTO> getDays() { return days; }
    public void setDays(List<DayDTO> days) { this.days = days; }

    public static class DayDTO {
        private String day;

        public String getDay() { return day; }
        public void setDay(String day) { this.day = day; }
    }
}
