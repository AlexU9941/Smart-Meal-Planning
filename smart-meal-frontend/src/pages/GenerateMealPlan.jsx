import React, { useState } from "react";
import "../css/generateMealPlan.css";

const DAYS = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];

const emptyDay = (index) => ({
  day: DAYS[index],
  lunch: null,
  dinner: null,
});

const GenerateMealPlan = () => {
  const [plan, setPlan] = useState(Array.from({ length: 7 }, (_, i) => emptyDay(i)));
  const [message, setMessage] = useState("");

  const generate = () => {
    // For now populate with placeholder meals. Integration with backend will replace this.
    const newPlan = plan.map((p, i) => ({
      ...p,
      lunch: { title: `Lunch: Quick Meal ${i + 1}` },
      dinner: { title: `Dinner: Hearty Meal ${i + 1}` },
    }));

    setPlan(newPlan);
    setMessage("Weekly meal plan generated (placeholders). Replace with backend data later.");
    try {
      localStorage.setItem("weeklyMealPlan", JSON.stringify(newPlan));
    } catch (e) {
      console.error("Failed to save weeklyMealPlan to localStorage", e);
    }
  };

  const clearPlan = () => {
    setPlan(Array.from({ length: 7 }, (_, i) => emptyDay(i)));
    setMessage("");
    try {
      localStorage.removeItem("weeklyMealPlan");
    } catch (e) {
      console.error("Failed to clear weeklyMealPlan from localStorage", e);
    }
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
