package smart_meal_planner.dto;

import java.util.List;

public class MealPlanResponseDTO {

    private Long planId;
    private List<DayDTO> days;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public List<DayDTO> getDays() { return days; }
    public void setDays(List<DayDTO> days) { this.days = days; }

    // ---------------- SIMPLE RECIPE DTO ----------------
    public static class SimpleRecipeDTO {
        private Long id;
        private String title;
        private String image;
        private String sourceUrl;

        public SimpleRecipeDTO() {}

        public SimpleRecipeDTO(Long id, String title, String image, String sourceUrl) {
            this.id = id;
            this.title = title;
            this.image = image;
            this.sourceUrl = sourceUrl;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getImage() { return image; }
        public String getSourceUrl() { return sourceUrl; }

        public void setId(Long id) { this.id = id; }
        public void setTitle(String title) { this.title = title; }
        public void setImage(String image) { this.image = image; }
        public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    }

    // ---------------- DAY DTO ----------------
    public static class DayDTO {
        private Long dayId;
        private String day;
        private SimpleRecipeDTO breakfast;
        private SimpleRecipeDTO lunch;
        private SimpleRecipeDTO dinner;

        public Long getDayId() { return dayId; }
        public void setDayId(Long dayId) { this.dayId = dayId; }

        public String getDay() { return day; }
        public void setDay(String day) { this.day = day; }

        public SimpleRecipeDTO getBreakfast() { return breakfast; }
        public void setBreakfast(SimpleRecipeDTO breakfast) { this.breakfast = breakfast; }

        public SimpleRecipeDTO getLunch() { return lunch; }
        public void setLunch(SimpleRecipeDTO lunch) { this.lunch = lunch; }

        public SimpleRecipeDTO getDinner() { return dinner; }
        public void setDinner(SimpleRecipeDTO dinner) { this.dinner = dinner; }
    }
}
