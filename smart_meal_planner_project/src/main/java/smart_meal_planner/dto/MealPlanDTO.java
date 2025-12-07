package smart_meal_planner.dto;

import java.util.List;

public class MealPlanDTO {

    private Long planId;               // optional, can be null when creating
    private List<MealDayDTO> days;     // 7 days typically

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public List<MealDayDTO> getDays() {
        return days;
    }

    public void setDays(List<MealDayDTO> days) {
        this.days = days;
    }

    // ================================================================
    //                Inner DTO for a single day
    // ================================================================
    public static class MealDayDTO {

        private Long breakfastId; // NEW
        private Long lunchId;
        private Long dinnerId;

        public Long getBreakfastId() {
            return breakfastId;
        }

        public void setBreakfastId(Long breakfastId) {
            this.breakfastId = breakfastId;
        }

        public Long getLunchId() {
            return lunchId;
        }

        public void setLunchId(Long lunchId) {
            this.lunchId = lunchId;
        }

        public Long getDinnerId() {
            return dinnerId;
        }

        public void setDinnerId(Long dinnerId) {
            this.dinnerId = dinnerId;
        }
    }
}
