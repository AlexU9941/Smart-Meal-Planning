package smart_meal_planner.model;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "meal_plan")
public class MealPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL)
    private List<MealDayEntity> days;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // getters/setters

    public Long getId() { return id; }
    public List<MealDayEntity> getDays() { return days; }
    public void setDays(List<MealDayEntity> days) { this.days = days; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
