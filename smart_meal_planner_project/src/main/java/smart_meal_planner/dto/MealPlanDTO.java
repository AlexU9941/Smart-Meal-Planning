package smart_meal_planner.dto;

import java.util.List;

public class MealPlanDTO {

    private List<MealDayDTO> days;

    public List<MealDayDTO> getDays() {
        return days;
    }

    public void setDays(List<MealDayDTO> days) {
        this.days = days;
    }

    public static class MealDayDTO {
        private Integer lunchId;  // just the recipe IDs
        private Integer dinnerId;

        public Integer getLunchId() {
            return lunchId;
        }

        public void setLunchId(Integer lunchId) {
            this.lunchId = lunchId;
        }

        public Integer getDinnerId() {
            return dinnerId;
        }

        public void setDinnerId(Integer dinnerId) {
            this.dinnerId = dinnerId;
        }
    }
}
