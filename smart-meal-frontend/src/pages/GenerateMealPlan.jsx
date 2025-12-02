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
        // SSR / non-browser safety
        return Array.from({ length: 7 }, (_, i) => emptyDay(i));
      }

      const saved = localStorage.getItem("weeklyMealPlan");
      if (saved) {
        const parsed = JSON.parse(saved);
        // Ensure it has 7 days and correct structure
        if (Array.isArray(parsed) && parsed.length === 7) {
          return parsed;
        }
      }
    } catch (e) {
      console.error("Failed to load weeklyMealPlan from localStorage", e);
    }

    // Fallback: create empty plan
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
          budget: 100,
        })
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      console.log("Received meal plan data:", data);

      const newPlan = data.days.map((day, index) => ({
        day: DAYS[index],
        lunch: day.lunch ? { title: day.lunch.title } : null,
        dinner: day.dinner ? { title: day.dinner.title } : null,
      }));

      setPlan(newPlan);
      setMessage("Weekly meal plan generated!");

      // Save to localStorage
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


// import React, { useState } from "react";
// import "../css/generateMealPlan.css";

// const DAYS = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];

// const emptyDay = (index) => ({
//   day: DAYS[index],
//   lunch: null,
//   dinner: null,
// });

// const GenerateMealPlan = () => {
//   const [plan, setPlan] = useState(Array.from({ length: 7 }, (_, i) => emptyDay(i)));
//   const [message, setMessage] = useState("");

//   const generate = async() => {
//     // For now populate with placeholder meals. Integration with backend will replace this.
//     // const newPlan = plan.map((p, i) => ({
//     //   ...p,
//     //   lunch: { title: `Lunch: Quick Meal ${i + 1}` },
//     //   dinner: { title: `Dinner: Hearty Meal ${i + 1}` },
//     // }));
//     try {
//       const response = await fetch("http://localhost:8080/meal-plans/generate", {
//       method: "POST",
//       headers: {
//       "Content-Type": "application/json" },
//       body: JSON.stringify({ //need to update with real ingredients and budget later
//         ingredients: [],
//         budget: 50
//       })
//       });

//       if (!response.ok) {
//       throw new Error(`HTTP error! status: ${response.status}`);
//       }
//       const data = await response.json();
//       console.log("Received meal plan data:", data);
//       // Transform backend data to match frontend structure
//       const newPlan = data.days.map((day, index) => ({
//         day: DAYS[index],
//         lunch: day.lunch ? { title: day.lunch.title } : null,
//         dinner: day.dinner ? { title: day.dinner.title } : null,
//       }));

//     setPlan(newPlan);
//     setMessage("Weekly meal plan generated!");
//     try {
//       localStorage.setItem("weeklyMealPlan", JSON.stringify(newPlan));
//     } catch (e) {
//       console.error("Failed to save weeklyMealPlan to localStorage", e);
//     }
//   }
//   catch (error) {
//     console.error("Error generating meal plan:", error);
//     setMessage("Failed to generate meal plan. Please try again later.");
//   }


//   };

//   const clearPlan = () => {
//     setPlan(Array.from({ length: 7 }, (_, i) => emptyDay(i)));
//     setMessage("");
//     try {
//       localStorage.removeItem("weeklyMealPlan");
//     } catch (e) {
//       console.error("Failed to clear weeklyMealPlan from localStorage", e);
//     }
//   };

//   return (
//     <div className="generate-meal-plan">
//       <h2>Generate Weekly Meal Plan</h2>
//       <div className="actions">
//         <button className="generate" onClick={generate}>Generate Weekly Meal Plan</button>
//         <button className="clear" onClick={clearPlan}>Clear</button>
//       </div>
//       {message && <div className="message">{message}</div>}

//       <div className="days-row">
//         {plan.map((p, idx) => (
//           <div key={idx} className="day-card">
//             <div className="day-header">{p.day}</div>
//             <div className="meal">{p.lunch ? p.lunch.title : <em>No lunch</em>}</div>
//             <div className="meal">{p.dinner ? p.dinner.title : <em>No dinner</em>}</div>
//           </div>
//         ))}
//       </div>
//     </div>
//   );
// };

// export default GenerateMealPlan;
