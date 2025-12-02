import React, { useState } from "react";
import "../css/generateMealPlan.css";

const DAYS = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

const emptyDay = (index) => ({
  day: DAYS[index],
  lunch: null,
  dinner: null,
});

const GenerateMealPlan = () => {
  const [plan, setPlan] = useState(() => {
    try {
      const saved = localStorage.getItem("weeklyMealPlan");
      const parsed = JSON.parse(saved);

      if (Array.isArray(parsed) && parsed.length === 7) {
        return parsed;
      }
    } catch (e) {
      console.error("LocalStorage load error:", e);
    }

    return Array.from({ length: 7 }, (_, i) => emptyDay(i));
  });

  const [message, setMessage] = useState("");

  const generate = async () => {
    try {
      const response = await fetch("http://localhost:8080/meal-plans/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ingredients: [],
          budget: 50
        })
      });

      if (!response.ok) {
        throw new Error("Meal plan generation failed. Status: " + response.status);
      }

      const data = await response.json();
      console.log("Backend meal plan:", data);

      if (!data.days || !Array.isArray(data.days)) {
        throw new Error("Invalid backend format: missing days[]");
      }

      const newPlan = data.days.slice(0, 7).map((day, index) => ({
        day: DAYS[index],
        lunch: day.lunch ? { title: day.lunch.title } : null,
        dinner: day.dinner ? { title: day.dinner.title } : null,
      }));

      setPlan(newPlan);
      setMessage("Weekly meal plan generated!");

      localStorage.setItem("weeklyMealPlan", JSON.stringify(newPlan));
    } catch (error) {
      console.error("Error generating meal plan:", error);
      setMessage("Failed to generate meal plan. Try again later.");
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
            <div className="meal">{p.lunch ? p.lunch.title : <em>No lunch</em>}</div>
            <div className="meal">{p.dinner ? p.dinner.title : <em>No dinner</em>}</div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default GenerateMealPlan;
