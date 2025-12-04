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

        private Integer breakfastId; // NEW
        private Integer lunchId;
        private Integer dinnerId;

        public Integer getBreakfastId() {
            return breakfastId;
        }

        public void setBreakfastId(Integer breakfastId) {
            this.breakfastId = breakfastId;
        }

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
