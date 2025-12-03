package smart_meal_planner.dto;

import java.util.List;

public class MealPlanResponseDTO {

    private Long planId;
    private List<DayDTO> days;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public List<DayDTO> getDays() {
        return days;
    }

    public void setDays(List<DayDTO> days) {
        this.days = days;
    }

    public static class DayDTO {
        private String day;
        private SimpleRecipeDTO breakfast;
        private SimpleRecipeDTO lunch;
        private SimpleRecipeDTO dinner;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public SimpleRecipeDTO getBreakfast() {
            return breakfast;
        }

        public void setBreakfast(SimpleRecipeDTO breakfast) {
            this.breakfast = breakfast;
        }

        public SimpleRecipeDTO getLunch() {
            return lunch;
        }

        public void setLunch(SimpleRecipeDTO lunch) {
            this.lunch = lunch;
        }

        public SimpleRecipeDTO getDinner() {
            return dinner;
        }

        public void setDinner(SimpleRecipeDTO dinner) {
            this.dinner = dinner;
        }
    }

    public static class SimpleRecipeDTO {
        private Long id;
        private String title;
        private String image;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
