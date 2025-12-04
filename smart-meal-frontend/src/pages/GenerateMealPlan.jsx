import React, { useState, useEffect } from "react";
import "../css/generateMealPlan.css";

const DAYS = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

const emptyDay = (index) => ({
  day: DAYS[index],
  lunch: null,
  dinner: null,
});

const GenerateMealPlan = () => {
  // Initialize state: try to load from localStorage, fallback to empty plan
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

  // Load ingredient names for the current user from the backend
  useEffect(() => {
    const loadIngredients = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/ingredients", {
          credentials: "include",
        });

        if (!res.ok) {
          console.error("Failed to load ingredients, status:", res.status);
          return;
        }

        const data = await res.json();
        const names = (data || [])
          .map((ing) => ing.name)
          .filter((name) => name && name.trim() !== "");

        setIngredientNames(names);
      } catch (err) {
        console.error("Error loading ingredients for meal plan:", err);
      }
    };

    loadIngredients();
  }, []);

  const budget = localStorage.getItem("budget") || 100; // default budget if none set

  const generate = async () => {
    try {
      const response = await fetch("http://localhost:8080/meal-plans/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ingredients: ingredientNames,
          budget: budget,
        }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log("Received meal plan data:", data);

      /*const newPlan = data.days.map((day, index) => ({
        day: DAYS[index],
        lunch: day.lunch ? { title: day.lunch.title } : null,
        dinner: day.dinner ? { title: day.dinner.title } : null,
      }));
      **/

      const newPlan = data.days.map((day, index) => ({
        day: DAYS[index],
        lunch: day.lunch
          ? {
              title: day.lunch.title,
              ingredients: day.lunch.ingredients || [],
            }
          : null,

        dinner: day.dinner
          ? {
              title: day.dinner.title,
              ingredients: day.dinner.ingredients || [],
            }
          : null,
      }));


      const extractAllIngredients = (plan) => {
        return plan.flatMap((day) => [
          ...(day.lunch?.ingredients || []),
          ...(day.dinner?.ingredients || []),
        ]);
      };

      setPlan(newPlan);
      
      const allIngredients = extractAllIngredients(newPlan);
      localStorage.setItem("mealPlanIngredients", JSON.stringify(allIngredients));

      const anyMissing = newPlan.some((day) => !day.lunch || !day.dinner);
      if (anyMissing) {
        setMessage("Unable to generate meal plan with current criteria.");
      } else {
        setMessage("Weekly meal plan generated!");
      }

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
        <button className="generate" onClick={generate}>
          Generate Weekly Meal Plan
        </button>
        <button className="clear" onClick={clearPlan}>
          Clear
        </button>
      </div>
      {message && <div className="message">{message}</div>}

      <div className="days-row">
        {plan.map((p, idx) => (
          <div key={idx} className="day-card">
            <div className="day-header">{p.day}</div>
            <div className="meal">
              {p.lunch ? p.lunch.title : <em>No lunch</em>}
            </div>
            <div className="meal">
              {p.dinner ? p.dinner.title : <em>No dinner</em>}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default GenerateMealPlan;