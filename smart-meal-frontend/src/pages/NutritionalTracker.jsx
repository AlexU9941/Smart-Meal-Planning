import React, { useEffect, useState } from "react";
import "../css/generateMealPlan.css";

const DAYS = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];

// Simple nutrition structure for a meal
const emptyNutrition = () => ({
  calories: 0,
  protein: 0,
  carbs: 0,
  fat: 0,
  addedSugars: 0,
  cholesterol: 0,
});

const mockNutritionForTitle = (title) => {
  // deterministic-ish mock values based on title length
  const seed = title ? title.length : 5;
  return {
    calories: 200 + (seed * 10) % 400,
    protein: 10 + (seed * 3) % 40,
    carbs: 20 + (seed * 5) % 100,
    fat: 5 + (seed * 2) % 50,
    addedSugars: (seed * 2) % 15,
    cholesterol: (seed * 4) % 100,
  };
};

const NutritionalTracker = () => {
  const [plan, setPlan] = useState(Array.from({ length: 7 }, (_, i) => ({ day: DAYS[i], lunch: null, dinner: null })));
  const [nutritionMap, setNutritionMap] = useState({});
  const [error, setError] = useState("");
  const [summary, setSummary] = useState(null);

  useEffect(() => {
    try {
      const raw = localStorage.getItem("weeklyMealPlan");
      if (!raw) return; // nothing saved yet

      const parsed = JSON.parse(raw);
      // Expecting an array of 7 day objects - validate lightly
      if (!Array.isArray(parsed) || parsed.length !== 7) {
        setError("Saved meal plan is in an unexpected format.");
        return;
      }

      setPlan(parsed.map((p, i) => ({ day: p.day || DAYS[i], lunch: p.lunch || null, dinner: p.dinner || null })));
    } catch (e) {
      console.error(e);
      setError("Failed to load saved weekly meal plan. The saved data may be corrupt.");
    }
  }, []);

  // Fetch summary from backend when plan changes
  useEffect(() => {
    async function fetchSummary() {
      try {
        const resp = await fetch("http://localhost:8080/api/nutrition/summary", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ days: plan })
        });
        if (!resp.ok) {
          console.warn("Nutrition summary fetch failed", resp.status);
          setSummary(null);
          return;
        }
        const data = await resp.json();
        setSummary(data);
      } catch (e) {
        console.error(e);
        setSummary(null);
      }
    }

    // Only call backend if there's a non-empty plan
    if (plan && plan.some(p => p.lunch || p.dinner)) {
      fetchSummary();
    } else {
      setSummary(null);
    }
  }, [plan]);

  useEffect(() => {
    // compute nutrition map from plan whenever plan changes
    const map = {};
    try {
      plan.forEach((p) => {
        ["lunch", "dinner"].forEach((m) => {
          const meal = p[m];
          if (!meal) return;

          // If meal already has nutrition provided by backend, try to use it
          if (meal.nutrition && typeof meal.nutrition === "object") {
            map[meal.title] = {
              calories: Number(meal.nutrition.calories) || 0,
              protein: Number(meal.nutrition.protein) || 0,
              carbs: Number(meal.nutrition.carbs) || 0,
              fat: Number(meal.nutrition.fat) || 0,
              addedSugars: Number(meal.nutrition.addedSugars) || 0,
              cholesterol: Number(meal.nutrition.cholesterol) || 0,
            };
          } else {
            // placeholder: create mock nutrition for display until backend provides values
            map[meal.title] = mockNutritionForTitle(meal.title);
          }
        });
      });

      setNutritionMap(map);
      setError("");
    } catch (e) {
      console.error(e);
      setError("Failed to compute nutrition for the current plan.");
    }
  }, [plan]);

  // Simple SVG bar chart for calories per meal
  const CaloriesBarChart = ({ perMeal }) => {
    if (!perMeal) return <div>No data for chart</div>;
    const items = Object.entries(perMeal);
    const max = Math.max(...items.map(([,v]) => v.calories), 100);
    const width = 600; const height = 200; const barW = Math.max(20, Math.floor(width / items.length) - 4);
    return (
      <svg width={width} height={height} style={{ border: '1px solid #ddd' }}>
        {items.map(([title, v], i) => {
          const h = (v.calories / max) * (height - 40);
          return (
            <g key={i} transform={`translate(${i * (barW + 4) + 30}, ${height - h - 20})`}>
              <rect width={barW} height={h} fill="#4CAF50" />
              <text x={barW / 2} y={h + 14} fontSize={10} textAnchor="middle">{Math.round(v.calories)}</text>
              <text x={barW / 2} y={h + 28} fontSize={9} textAnchor="middle">{title.length > 12 ? title.slice(0,12)+"..." : title}</text>
            </g>
          );
        })}
      </svg>
    );
  };

  // Stacked macros per day (protein/carbs/fat) simple bars
  const MacrosStacked = ({ days }) => {
    if (!days) return <div>No day macro data</div>;
    const width = 700; const height = 220; const colW = Math.floor(width / days.length) - 8;
    const totals = days.map(d => d.totals);
    const max = Math.max(...totals.map(t => t.protein + t.carbs + t.fat), 10);
    return (
      <svg width={width} height={height} style={{ border: '1px solid #eee', background: '#fff' }}>
        {totals.map((t, i) => {
          const total = t.protein + t.carbs + t.fat;
          const scale = (height - 60) / max;
          const pH = t.protein * scale; const cH = t.carbs * scale; const fH = t.fat * scale;
          const x = i * (colW + 8) + 40;
          return (
            <g key={i} transform={`translate(${x}, ${height - 40 - (pH + cH + fH)})`}>
              <rect y={0} width={colW} height={pH} fill="#2196F3" />
              <rect y={pH} width={colW} height={cH} fill="#FFC107" />
              <rect y={pH + cH} width={colW} height={fH} fill="#9C27B0" />
              <text x={colW/2} y={pH + cH + fH + 14} fontSize={10} textAnchor="middle">{days[i].day}</text>
            </g>
          );
        })}
      </svg>
    );
  };

  const simulatePopulateNutrition = () => {
    // Adds nutrition objects to each meal in local storage so the tracker can pick them up later
    try {
      const newPlan = plan.map((p) => ({ ...p }));
      newPlan.forEach((p) => {
        ["lunch", "dinner"].forEach((m) => {
          const meal = p[m];
          if (!meal) return;
          meal.nutrition = mockNutritionForTitle(meal.title);
        });
      });
      localStorage.setItem("weeklyMealPlan", JSON.stringify(newPlan));
      setPlan(newPlan);
    } catch (e) {
      console.error(e);
      setError("Failed to simulate nutrition population.");
    }
  };

  const totalForDay = (dayPlan) => {
    const totals = emptyNutrition();
    ["lunch", "dinner"].forEach((m) => {
      const meal = dayPlan[m];
      if (!meal) return;
      const n = meal.nutrition || nutritionMap[meal.title] || emptyNutrition();
      totals.calories += Number(n.calories) || 0;
      totals.protein += Number(n.protein) || 0;
      totals.carbs += Number(n.carbs) || 0;
      totals.fat += Number(n.fat) || 0;
      totals.addedSugars += Number(n.addedSugars) || 0;
      totals.cholesterol += Number(n.cholesterol) || 0;
    });
    return totals;
  };

  const totalForWeek = () => {
    const totals = emptyNutrition();
    plan.forEach((p) => {
      const d = totalForDay(p);
      Object.keys(totals).forEach((k) => totals[k] += d[k]);
    });
    return totals;
  };

  return (
    <div className="generate-meal-plan">
      <h2>Nutrition Tracker</h2>
      <div className="actions">
        <button onClick={simulatePopulateNutrition}>Simulate Populate Nutrition (for testing)</button>
      </div>
      {error && <div className="message" style={{ color: "red" }}>{error}</div>}

      <div className="days-row" style={{ flexDirection: "column", gap: "1rem" }}>
        {plan.map((p, idx) => (
          <div key={idx} className="day-card" style={{ marginBottom: "0.5rem" }}>
            <div className="day-header">{p.day}</div>
            <div className="meal"><strong>Lunch:</strong> {p.lunch ? p.lunch.title : <em>No lunch</em>}</div>
            {p.lunch && (
              <div className="nutrition-row">{Object.entries(nutritionMap[p.lunch.title] || (p.lunch.nutrition || {})).map(([k,v]) => (
                <div key={k} style={{ marginRight: "0.5rem" }}>{k}: {v}</div>
              ))}</div>
            )}
            <div className="meal"><strong>Dinner:</strong> {p.dinner ? p.dinner.title : <em>No dinner</em>}</div>
            {p.dinner && (
              <div className="nutrition-row">{Object.entries(nutritionMap[p.dinner.title] || (p.dinner.nutrition || {})).map(([k,v]) => (
                <div key={k} style={{ marginRight: "0.5rem" }}>{k}: {v}</div>
              ))}</div>
            )}

            <div style={{ marginTop: "0.5rem" }}><strong>Day totals:</strong>
              <div style={{ display: "flex", gap: "0.5rem", marginTop: "0.25rem" }}>
                {Object.entries(totalForDay(p)).map(([k,v]) => (
                  <div key={k}>{k}: {Math.round(v*100)/100}</div>
                ))}
              </div>
            </div>
          </div>
        ))}

        <div style={{ marginTop: "1rem", padding: "0.5rem", borderTop: "1px solid #ddd" }}>
          <h3>Weekly totals</h3>
          <div style={{ display: "flex", gap: "0.5rem" }}>
            {Object.entries(totalForWeek()).map(([k,v]) => (
              <div key={k}>{k}: {Math.round(v*100)/100}</div>
            ))}
          </div>
        </div>
      </div>

      <div style={{ marginTop: "1rem", padding: "0.5rem", borderTop: "1px solid #ddd" }}>
        <h3>Charts</h3>
        {summary && (
          <>
            <div style={{ marginBottom: "1rem" }}>
              <strong>Calories per meal</strong>
              <CaloriesBarChart perMeal={summary.caloriesPerMeal} />
            </div>
            <div>
              <strong>Macros per day (Protein/Carbs/Fat)</strong>
              <MacrosStacked days={summary.days} />
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default NutritionalTracker;
