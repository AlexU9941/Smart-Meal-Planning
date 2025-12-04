import React, { useState, useEffect } from "react";
import "../css/generateMealPlan.css";

const DAYS = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

const emptyDay = (index) => ({
  day: DAYS[index],
  breakfast: null,
  lunch: null,
  dinner: null,
});

const GenerateMealPlan = () => {
  const [plan, setPlan] = useState(() => {
    try {
      if (typeof window === "undefined") {
        return Array.from({ length: 7 }, (_, i) => emptyDay(i));
      }

      const saved = localStorage.getItem("weeklyMealPlan");
      if (saved) {
        const parsed = JSON.parse(saved);
        if (Array.isArray(parsed) && parsed.length === 7) {
          return parsed;
        }
      }
    } catch (e) {
      console.error("Failed to load weeklyMealPlan from localStorage", e);
    }

    return Array.from({ length: 7 }, (_, i) => emptyDay(i));
  });

  const [message, setMessage] = useState("");
  const [ingredientNames, setIngredientNames] = useState([]);

  const userId = Number(localStorage.getItem("userId"));
  const budget = localStorage.getItem("budget") || 100;

  // Load user ingredients
  useEffect(() => {
    const loadIngredients = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/ingredients", {
          credentials: "include",
        });

        if (!res.ok) return console.error("Failed to load ingredients");

        const data = await res.json();
        const names = (data || [])
          .map((ing) => ing.name)
          .filter(Boolean);

        setIngredientNames(names);
      } catch (err) {
        console.error("Error loading ingredients:", err);
      }
    };

    loadIngredients();
  }, []);

  const generate = async () => {
    try {
      const response = await fetch("http://localhost:8080/meal-plans/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ingredients: ingredientNames,
          budget: budget,
          userId: userId,
        }),
      });

      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

      const data = await response.json();
      console.log("Received meal plan data:", data);

      const newPlan = data.days.map((day, index) => ({
        day: DAYS[index],
        breakfast: day.breakfast ? { title: day.breakfast.title } : null,
        lunch: day.lunch ? { title: day.lunch.title } : null,
        dinner: day.dinner ? { title: day.dinner.title } : null,
      }));

      setPlan(newPlan);

      const anyMissing = newPlan.some(
        (d) => !d.breakfast || !d.lunch || !d.dinner
      );

      setMessage(
        anyMissing
          ? "Unable to generate a full meal plan with current criteria."
          : "Weekly meal plan generated!"
      );

      localStorage.setItem("weeklyMealPlan", JSON.stringify(newPlan));
    } catch (error) {
      console.error("Error generating meal plan:", error);
      setMessage("Failed to generate meal plan. Please try again later.");
    }
  };

  const clearPlan = () => {
    const emptyPlan = Array.from({ length: 7 }, (_, i) => emptyDay(i));
    setPlan(emptyPlan);
    setMessage("Meal plan cleared.");
    localStorage.removeItem("weeklyMealPlan");
  };

  return (
    <div className="generate-meal-plan">
      <h2>Generate Weekly Meal Plan</h2>

      <div className="actions">
        <button className="generate" onClick={generate}>Generate Weekly Meal Plan</button>
        <button className="clear" onClick={clearPlan}>Clear</button>
      </div>

      {message && <div className="message">{message}</div>}

      <div className="days-row">
        {plan.map((p, idx) => (
          <div key={idx} className="day-card">
            <div className="day-header">{p.day}</div>

            {/* 📅 Breakfast */}
            <div className="meal-row">
              <span className="meal-label">Breakfast:</span>
              <span className="meal-text">
                {p.breakfast ? p.breakfast.title : <em>No breakfast generated</em>}
              </span>
            </div>

            {/* 🍽 Lunch */}
            <div className="meal-row">
              <span className="meal-label">Lunch:</span>
              <span className="meal-text">
                {p.lunch ? p.lunch.title : <em>No lunch</em>}
              </span>
            </div>

            {/* 🍛 Dinner */}
            <div className="meal-row">
              <span className="meal-label">Dinner:</span>
              <span className="meal-text">
                {p.dinner ? p.dinner.title : <em>No dinner</em>}
              </span>
            </div>

          </div>
        ))}
      </div>
    </div>
  );
};

export default GenerateMealPlan;
