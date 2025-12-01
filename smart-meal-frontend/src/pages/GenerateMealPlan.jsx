import React, { useState } from "react";
import "../css/generateMealPlan.css";

const DAYS = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
const emptyDay = (i) => ({ day: DAYS[i], lunch: null, dinner: null });

export default function GenerateMealPlan({ onIngredientsGenerated }) {
  const [plan, setPlan] = useState(Array.from({ length: 7 }, (_, i) => emptyDay(i)));
  const [message, setMessage] = useState("");

  const recipes = [
    { title: "Chicken Salad", ingredients: ["Chicken", "Lettuce", "Tomatoes"] },
    { title: "Pasta Alfredo", ingredients: ["Pasta", "Cream", "Parmesan"] },
    { title: "Veggie Stir Fry", ingredients: ["Broccoli", "Carrots", "Soy Sauce"] },
  ];

  const generateRandomMealPlan = () => {
    const newPlan = plan.map((day) => ({
      ...day,
      lunch: recipes[Math.floor(Math.random() * recipes.length)],
      dinner: recipes[Math.floor(Math.random() * recipes.length)],
    }));

    setPlan(newPlan);
    setMessage("Meal plan generated!");

    const ingredients = newPlan.flatMap((d) => [
      d.lunch?.title,
      d.dinner?.title,
    ].filter(Boolean));

    if (onIngredientsGenerated) onIngredientsGenerated(ingredients);
  };

  return (
    <div>
      <h2>Generate Meal Plan</h2>
      <button onClick={generateRandomMealPlan}>Generate</button>

      {message && <p>{message}</p>}

      <ul>
        {plan.map((d, idx) => (
          <li key={idx}>
            <strong>{d.day}</strong> – Lunch: {d.lunch?.title ?? "None"}, Dinner: {d.dinner?.title ?? "None"}
          </li>
        ))}
      </ul>
    </div>
  );
}
