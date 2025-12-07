import React, { useEffect, useState } from 'react';
import '../css/generateMealPlan.css';

const DAYS = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];

const TrackHealth = () => {
  const [plan, setPlan] = useState(null);
  const [summary, setSummary] = useState(null);
  const [healthInfo, setHealthInfo] = useState(null);
  const [error, setError] = useState("");
  const [email, setEmail] = useState("");

  useEffect(() => {
    try {
      const raw = localStorage.getItem('weeklyMealPlan');
      if (!raw) {
        setPlan(null);
        return;
      }
      const parsed = JSON.parse(raw);
      setPlan(parsed);
    } catch (e) {
      console.error(e);
      setError('Failed to load saved plan from localStorage');
    }

    const storedEmail = localStorage.getItem('userEmail');
    if (storedEmail) setEmail(storedEmail);
  }, []);

  useEffect(() => {
    async function fetchSummary() {
      if (!plan || !Array.isArray(plan) || plan.length === 0) return;
      try {
        console.log("Sending plan to summary endpoint:", plan);
        const resp = await fetch('http://localhost:8080/api/nutrition/summary', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ days: plan })
        });

        if (!resp.ok) {
          setError('Failed to fetch nutrition summary from backend');
          return;
        }
        const data = await resp.json();
        setSummary(data);
      } catch (e) {
        console.error(e);
        setError('Failed to contact nutrition service');
      }
    }

    fetchSummary();
  }, [plan]);

  useEffect(() => {
    async function fetchHealth() {
      if (!email) return;
      try {
        const resp = await fetch(`http://localhost:8080/api/health-info?email=${encodeURIComponent(email)}`);
        if (!resp.ok) {
          setHealthInfo(null);
          return;
        }
        const d = await resp.json();
        setHealthInfo(d);

        try {
          const est = await fetch(`http://localhost:8080/api/health-info/estimate?email=${encodeURIComponent(email)}`);
          if (est.ok) {
            const ejson = await est.json();
            setHealthInfo(prev => ({ ...d, estimatedCalories: ejson }));
          }
        } catch (ee) { console.error('estimate fetch failed', ee); }

      } catch (e) {
        console.error(e);
      }
    }
    fetchHealth();
  }, [email]);

  const estimateWeeklyWeightChange = (weeklyCalories, goalCalories, weeks = 12) => {
    const weeklyDiff = weeklyCalories - goalCalories;
    const weeklyLbChange = weeklyDiff / 3500;
    const result = [];
    let weight = (healthInfo && healthInfo.weight) ? Number(healthInfo.weight) : 180;

    for (let i = 0; i < weeks; i++) {
      weight += weeklyLbChange;
      result.push({ week: i + 1, weight: Math.round(weight * 100) / 100 });
    }
    return result;
  };

  const computeDailyCalories = () => {
    const daily = Array(7).fill(0);
    if (summary && Array.isArray(summary.days) && summary.days.length >= 7) {
      for (let i = 0; i < 7; i++) {
        const d = summary.days[i];
        daily[i] = d?.totals?.calories ? Math.round(d.totals.calories) : 0;
      }
      return daily;
    }

    try {
      for (let i = 0; i < 7; i++) {
        const d = plan[i] || {};
        const mealKeys = ['breakfast','lunch','dinner','snack'];
        mealKeys.forEach(m => {
          const meal = d[m];
          if (!meal) return;
          if (meal.nutrition?.calories) daily[i] += Number(meal.nutrition.calories) || 0;
          else if (meal.calories) daily[i] += Number(meal.calories) || 0;
          else if (meal.title) daily[i] += Math.min(1000, Math.max(150, meal.title.length * 25));
          else daily[i] += 400;
        });
        Object.keys(d).forEach(k => {
          if (mealKeys.includes(k)) return;
          const meal = d[k];
          if (!meal || typeof meal !== 'object') return;
          if (meal.nutrition?.calories) daily[i] += Number(meal.nutrition.calories) || 0;
          else if (meal.calories) daily[i] += Number(meal.calories) || 0;
        });
      }
    } catch (e) {
      console.warn('Failed to compute daily calories from plan', e);
    }
    return daily;
  };

  const dailyCalories = computeDailyCalories();
  let weeklyCalories = dailyCalories.reduce((s,v)=>s+v, 0);

  if (summary?.weeklyTotals?.calories) {
    weeklyCalories = Math.round(summary.weeklyTotals.calories);
  }

  const extractGoalCalories = () => {
    if (!healthInfo) return 2000 * 7;
    const est = healthInfo.estimatedCalories || {};

    if (typeof est.weeklyCalorieGoal === 'number') return est.weeklyCalorieGoal;
    if (typeof est.dailyCalorieGoal === 'number') return Math.round(est.dailyCalorieGoal * 7);
    if (typeof healthInfo.weeklyCalorieGoal === 'number') return healthInfo.weeklyCalorieGoal;
    if (typeof healthInfo.calorieGoal === 'number') return Math.round(healthInfo.calorieGoal * 7);
    if (typeof healthInfo.dailyCalorieGoal === 'number') return Math.round(healthInfo.dailyCalorieGoal * 7);

    return 2000 * 7;
  };

  const goalCalories = extractGoalCalories();
  const goalWeight = healthInfo?.goalWeight || healthInfo?.targetWeight || healthInfo?.desiredWeight || null;

  const renderDailyCaloriesChart = (daily) => {
    const width = 700, height = 240, margin = 40;
    const max = Math.max(...daily, 500);
    const barW = Math.floor((width - margin*2) / daily.length) - 10;

    const ticks = 4;
    const yTicks = [];
    for (let t = 0; t <= ticks; t++) yTicks.push(Math.round((t/ticks) * max));

    return (
      <svg width={width} height={height} style={{ border: '1px solid #eee', background: '#fff' }}>
        {yTicks.map((val, i) => {
          const y = margin + ((max - val)/max)*(height - margin*2);
          return (
            <g key={i}>
              <line x1={margin} x2={width - margin} y1={y} y2={y} stroke="#f0f0f0" />
              <text x={6} y={y+4} fontSize={11} fill="#666">{val}</text>
            </g>
          );
        })}

        {daily.map((val, i) => {
          const x = margin + i*(barW + 10) + 10;
          const h = Math.round((val / max) * (height - margin*2));
          const y = margin + (height - margin*2) - h;
          return (
            <g key={i}>
              <rect x={x} y={y} width={barW} height={h} fill="#1976d2" />
              <text x={x + barW/2} y={height - 6} fontSize={11} textAnchor="middle">{DAYS[i]}</text>
              <text x={x + barW/2} y={y - 6} fontSize={11} textAnchor="middle" fill="#333">{val}</text>
            </g>
          );
        })}
      </svg>
    );
  };

  const renderProjectionChart = (points, goalW) => {
    if (!points || points.length === 0) return <div>No projection data</div>;
    const width = 700, height = 320, margin = 50;

    const weights = points.map(p=>p.weight);
    const maxW = Math.max(...weights, goalW || -Infinity) + 5;
    const minW = Math.min(...weights, goalW || Infinity) - 5;
    const range = Math.max(1, maxW - minW);

    const yTicks = 6;
    const yVals = [];
    for (let i=0;i<=yTicks;i++) yVals.push(minW + (i/yTicks)*range);

    return (
      <svg width={width} height={height} style={{ border: '1px solid #ddd', background: '#fff' }}>
        {yVals.map((val, i) => {
          const y = margin + ((maxW - val)/range)*(height - margin*2);
          return (
            <g key={i}>
              <line x1={margin} x2={width-margin} y1={y} y2={y} stroke="#f0f0f0" />
              <text x={margin-8} y={y+4} fontSize={12} textAnchor="end">{Math.round(val*100)/100}</text>
            </g>
          );
        })}

        {goalW != null && (() => {
          const yGoal = margin + ((maxW - goalW)/range)*(height - margin*2);
          return (
            <g>
              <line x1={margin} x2={width-margin} y1={yGoal} y2={yGoal} stroke="#e91e63" strokeDasharray="6 6" />
              <text x={width-margin-6} y={yGoal-6} fontSize={12} textAnchor="end" fill="#e91e63">Goal: {goalW}</text>
            </g>
          );
        })()}

        <polyline
          fill="none"
          stroke="#3f51b5"
          strokeWidth="2"
          points={points.map((p,i)=>{
            const x = margin + (i/(points.length-1))*(width-2*margin);
            const y = margin + ((maxW - p.weight)/range)*(height-2*margin);
            return `${x},${y}`;
          }).join(' ')}
        />

        {points.map((p,i)=>{
          const x = margin + (i/(points.length-1))*(width-2*margin);
          const y = margin + ((maxW - p.weight)/range)*(height-2*margin);
          return (
            <g key={i}>
              <circle cx={x} cy={y} r={4} fill="#3f51b5" />
              <text x={x} y={y-8} fontSize={11} textAnchor="middle">{p.weight}</text>
              <text x={x} y={height-12} fontSize={11} textAnchor="middle">W{i+1}</text>
            </g>
          );
        })}
      </svg>
    );
  };

  // ✅ Add the missing function here  
  const renderBarComparison = (weekly, goal) => {
    const width = 400;
    const height = 60;

    const max = Math.max(weekly, goal, 1);
    const weeklyW = (weekly / max) * width;
    const goalW = (goal / max) * width;

    return (
      <div style={{ marginTop: "1rem" }}>
        <svg width={width} height={height}>
          {/* Weekly bar */}
          <rect x="0" y="10" width={weeklyW} height="15" fill="#1976d2" />
          <text x={weeklyW + 5} y="22" fontSize="12">{weekly} kcal</text>

          {/* Goal bar */}
          <rect x="0" y="35" width={goalW} height="15" fill="#43a047" />
          <text x={goalW + 5} y="48" fontSize="12">Goal: {goal} kcal</text>
        </svg>
      </div>
    );
  };

  if (!plan || plan.length === 0) {
    return (
      <div>
        <h2>Track Health</h2>
        <div>No meal plan generated. Please generate a meal plan to view nutritional information.</div>
      </div>
    );
  }

  if (error) return <div style={{ color: 'red' }}>{error}</div>;

  const projection = estimateWeeklyWeightChange(weeklyCalories, goalCalories, 12);

  return (
    <div className="generate-meal-plan">
      <h2>Track Health</h2>
      <div>
        <div style={{ marginBottom: '1rem' }}>
          <label>Account email (optional): </label>
          <input value={email} onChange={e=>setEmail(e.target.value)} placeholder="you@example.com" />
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <strong>Weekly calories: </strong>{weeklyCalories}
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <strong>Daily calories (current week)</strong>
          <div style={{ marginTop: 8 }}>{renderDailyCaloriesChart(dailyCalories)}</div>
        </div>

        <div style={{ marginBottom: '1rem' }}>
          {renderBarComparison(weeklyCalories, goalCalories)}
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <h4>Projected weight change over 12 weeks</h4>
          {renderProjectionChart(projection, goalWeight)}
        </div>

        <div>
          <h4>Details</h4>
          {summary && summary.days ? (
            <div>
              {summary.days.map((d,i)=> (
                <div key={i} style={{ padding: '6px', borderBottom: '1px solid #eee' }}>
                  <strong>{d.day}</strong>
                  <div>Calories: {Math.round(d.totals.calories)}</div>
                  <div>Protein: {Math.round(d.totals.protein)}, Carbs: {Math.round(d.totals.carbs)}, Fat: {Math.round(d.totals.fat)}</div>
                </div>
              ))}
            </div>
          ) : <div>No detailed summary available</div>}
        </div>
      </div>
    </div>
  );
};

export default TrackHealth;
