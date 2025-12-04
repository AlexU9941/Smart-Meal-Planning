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
      if (typeof window === "undefined") return Array.from({ length: 7 }, (_, i) => emptyDay(i));

      const saved = localStorage.getItem("weeklyMealPlan");
      if (saved) {
        const parsed = JSON.parse(saved);
        if (Array.isArray(parsed) && parsed.length === 7) return parsed;
      }
    } catch (e) {
      console.error("Failed to load weeklyMealPlan from localStorage", e);
    }
    return Array.from({ length: 7 }, (_, i) => emptyDay(i));
  });

  const [message, setMessage] = useState("");
  const [ingredientNames, setIngredientNames] = useState([]);
  const [selectedMeal, setSelectedMeal] = useState(null); // clicked meal
  const [clickedUrl, setClickedUrl] = useState(null);

  useEffect(() => {
    const loadIngredients = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/ingredients", { credentials: "include" });
        if (!res.ok) return console.error("Failed to load ingredients:", res.status);

        const data = await res.json();
        const names = (data || []).map((ing) => ing.name).filter((n) => n && n.trim() !== "");
        setIngredientNames(names);
      } catch (err) {
        console.error("Error loading ingredients:", err);
      }
    };
    loadIngredients();
  }, []);

  const budget = localStorage.getItem("budget") || 100;

  const generate = async () => {
    try {
      const response = await fetch("http://localhost:8080/meal-plans/generate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ingredients: ingredientNames, budget }),
      });

      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

      const data = await response.json();

      const newPlan = data.days.map((day, index) => ({
        day: DAYS[index],
        breakfast: day.breakfast ? { title: day.breakfast.title, ingredients: day.breakfast.ingredients || [],sourceUrl: day.breakfast.sourceUrl || null  } : null,
        lunch: day.lunch ? { title: day.lunch.title, ingredients: day.lunch.ingredients || [], sourceUrl: day.lunch.sourceUrl || null } : null,
        dinner: day.dinner ? { title: day.dinner.title, ingredients: day.dinner.ingredients || [], sourceUrl: day.dinner.sourceUrl || null } : null,
      }));

      const extractAllIngredients = (plan) =>
        plan.flatMap((day) => [
          ...(day.breakfast?.ingredients || []),
          ...(day.lunch?.ingredients || []),
          ...(day.dinner?.ingredients || []),
        ]);

      setPlan(newPlan);
      const allIngredients = extractAllIngredients(newPlan);
      localStorage.setItem("mealPlanIngredients", JSON.stringify(allIngredients));

      const anyMissing = newPlan.some((day) => !day.breakfast || !day.lunch || !day.dinner);
      setMessage(anyMissing ? "Some meals could not be generated." : "Weekly meal plan generated!");

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

  const handleMealClick = (meal) => {
  //setSelectedMeal(meal);
if (meal) {
    setClickedUrl(meal.sourceUrl || "https://example.com");
  }
  };

  const closeModal = () => setSelectedMeal(null);


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
            <div className="meal" onClick={() => p.breakfast && handleMealClick(p.breakfast)}>
              <strong>Breakfast:</strong> {p.breakfast ? p.breakfast.title : <em>No breakfast</em>}
            </div>
            <div className="meal" onClick={() => p.lunch && handleMealClick(p.lunch)}>
              <strong>Lunch:</strong> {p.lunch ? p.lunch.title : <em>No lunch</em>}
            </div>
            <div className="meal" onClick={() => p.dinner && handleMealClick(p.dinner)}>
              <strong>Dinner:</strong> {p.dinner ? p.dinner.title : <em>No dinner</em>}
            </div>
          </div>
        ))}
      </div>
    


    {clickedUrl && (
        <div 
          className="modal-overlay" 
          style={{
            position: "fixed",
            top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: "rgba(0,0,0,0.5)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 1000
          }}
          onClick={() => setClickedUrl(null)} // click outside closes
        >
          <div
            className="modal-content"
            style={{
              backgroundColor: "#fff",
              padding: "20px",
              borderRadius: "8px",
              minWidth: "200px",
            }}
            onClick={(e) => e.stopPropagation()} // prevent closing when clicking inside
          >
            <p>
              <a href={clickedUrl} target="_blank" rel="noopener noreferrer">
                Open Recipe
              </a>
            </p>
            <button onClick={() => setClickedUrl(null)} style={{ marginTop: "10px" }}>
              Close
            </button>
          </div>
        </div>
      )}
    </div>  // <-- Close main container div
  );
};
export default GenerateMealPlan;


/* //     <div className="modal-overlay" onClick={closeModal}>
//       <div className="modal-content" onClick={(e) => e.stopPropagation()}>
//         <button className="close-button" onClick={closeModal}>×</button>
//         <h3>{selectedMeal.title}</h3>
//         <p><strong>Ingredients:</strong></p>
//         <ul>
//           {selectedMeal.ingredients && selectedMeal.ingredients.length > 0 ? (
//             selectedMeal.ingredients.map((ing, i) => (
//               <li key={i}>{ing.name} {ing.quantity ? `- ${ing.quantity}${ing.unit || ''}` : ''}</li>
//             ))
//           ) : (
//             <li>No ingredients listed</li>
//           )}
//         </ul>
//         {selectedMeal.sourceUrl && (
//           <p>
//             <a href={selectedMeal.sourceUrl} target="_blank" rel="noopener noreferrer">
//               View full recipe
//             </a>
//           </p>
//         )}
//       </div>
//     </div>
//   )}
// </div> */





