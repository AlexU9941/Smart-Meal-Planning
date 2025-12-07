import React, { useState, useEffect } from "react";
import "../css/generateMealPlan.css";

const DAYS = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

const emptyDay = (index) => ({
  day: DAYS[index],
  breakfast: null,
  lunch: null,
  dinner: null,
  dayId: null
});

export default function GenerateMealPlan() {
  const storedUser = JSON.parse(localStorage.getItem("user"));
  const userId = storedUser?.uid || null;

  const [plan, setPlan] = useState(Array.from({ length: 7 }, (_, i) => emptyDay(i)));
  const [message, setMessage] = useState("");
  const [clickedMeal, setClickedMeal] = useState(null);

  const safeJson = async (response) => {
    try {
      return await response.json();
    } catch (err) {
      console.error("JSON parse failed:", err);
      return null;
    }
  };

  const generate = async () => {
    try {
      const response = await fetch("http://localhost:8080/meal-plans/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ingredients: [],
          budget: 250,
          userId: userId
        })
      });

      const data = await safeJson(response);

      if (!data || !data.days) {
        setMessage("No meal plan available.");
        return;
      }

      const formatted = data.days.map((d, i) => ({
        day: d.day || DAYS[i],
        dayId: d.dayId,
        breakfast: d.breakfast || null,
        lunch: d.lunch || null,
        dinner: d.dinner || null
      }));

      setPlan(formatted);
      setMessage("Meal plan loaded.");
    } catch (err) {
      console.error("Error generating:", err);
      setMessage("Failed to generate.");
    }
  };

  const requestAlternative = async (dayId, mealType) => {
    try {
      const res = await fetch(
        `http://localhost:8080/meal-plans/alternative?dayId=${dayId}&mealType=${mealType}`,
        { method: "POST" }
      );

      const updated = await safeJson(res);
      if (!updated) return;

      setPlan((prev) =>
        prev.map((d) =>
          d.dayId === dayId ? { ...d, [mealType]: updated } : d
        )
      );

      setClickedMeal(updated);
    } catch (err) {
      console.error("Alternative request failed:", err);
    }
  };

  const getMealType = (day, meal) => {
    if (day.breakfast?.id === meal.id) return "breakfast";
    if (day.lunch?.id === meal.id) return "lunch";
    if (day.dinner?.id === meal.id) return "dinner";
    return null;
  };

  const handleShare = async (url) => {
    if (!url) return;
    try {
      await navigator.clipboard.writeText(url);
      alert("Recipe link copied!");
    } catch (err) {
      console.error("Failed to copy:", err);
      alert("Could not copy the link.");
    }
  };

  return (
    <div className="generate-meal-plan">
      <h2>Generate Weekly Meal Plan</h2>

      <div className="actions">
        <button onClick={generate}>Generate Weekly Meal Plan</button>
      </div>

      {message && <div className="message">{message}</div>}

      <div className="days-row">
        {plan.map((p, idx) => (
          <div key={idx} className="day-card">
            <div className="day-header">{p.day}</div>

            {/* FIXED: backend keys remain lowercase */}
            {[
              { key: "breakfast", label: "Breakfast" },
              { key: "lunch", label: "Lunch" },
              { key: "dinner", label: "Dinner" }
            ].map(({ key, label }) => (
              <div className="meal-row" key={key}>
                <span className="meal-label">{label}:</span>
                <span
                  className="meal-text"
                  onClick={() => p[key] && setClickedMeal(p[key])}
                >
                  {p[key] ? p[key].title : <em>No {label}</em>}
                </span>
              </div>
            ))}
          </div>
        ))}
      </div>

      {clickedMeal && (
        <div className="modal-overlay" onClick={() => setClickedMeal(null)}>
          <div className="modal-content meal-modal" onClick={(e) => e.stopPropagation()}>
            <h3>{clickedMeal.title}</h3>

            {clickedMeal.image && (
              <img src={clickedMeal.image} alt="" className="meal-image" />
            )}

            {clickedMeal.sourceUrl && (
              <p>
                <a href={clickedMeal.sourceUrl} target="_blank" rel="noreferrer">
                  Open Recipe
                </a>
              </p>
            )}

            {/* SHARE BUTTON WITH ICON */}
            {clickedMeal.sourceUrl && (
              <button
                className="share-btn"
                onClick={() => handleShare(clickedMeal.sourceUrl)}
              >
                📤 Share Recipe
              </button>
            )}

            {/* ALTERNATIVE MEAL BUTTON */}
            <button
              className="alternative-btn"
              onClick={() => {
                const parentDay = plan.find((d) =>
                  ["breakfast", "lunch", "dinner"].some(
                    (m) => d[m]?.id === clickedMeal.id
                  )
                );
                const mealType = getMealType(parentDay, clickedMeal);
                if (parentDay && mealType) {
                  requestAlternative(parentDay.dayId, mealType);
                }
              }}
            >
              🔁 Alternative Meal
            </button>

            <button className="close-btn" onClick={() => setClickedMeal(null)}>
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
