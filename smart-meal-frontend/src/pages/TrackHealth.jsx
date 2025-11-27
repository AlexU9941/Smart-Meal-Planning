import React, { useEffect, useState } from 'react';
import './css/generateMealPlan.css';

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

    // try to read stored email if any
    const storedEmail = localStorage.getItem('userEmail');
    if (storedEmail) setEmail(storedEmail);
  }, []);

  useEffect(() => {
    async function fetchSummary() {
      if (!plan || !Array.isArray(plan) || plan.length === 0) return;
      try {
        const resp = await fetch('http://localhost:8080/api/nutrition/summary', {
          method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ days: plan })
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
        // fetch estimated calories
        try {
          const est = await fetch(`http://localhost:8080/api/health-info/estimate?email=${encodeURIComponent(email)}`);
          if (est.ok) {
            const ejson = await est.json();
            // attach to healthInfo for use
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
    // 3500 kcal ~= 1 lb of fat
    const weeklyDiff = weeklyCalories - goalCalories; // positive => gain
    const weeklyLbChange = weeklyDiff / 3500;
    const result = [];
    let weight = (healthInfo && healthInfo.weight) ? Number(healthInfo.weight) : 180; // default
    for (let i=0;i<weeks;i++) {
      weight += weeklyLbChange;
      result.push({ week: i+1, weight: Math.round(weight * 100)/100 });
    }
    return result;
  };

  const renderLineChart = (points) => {
    if (!points || points.length === 0) return <div>No projection data</div>;
    const width = 600, height = 240, margin = 30;
    const max = Math.max(...points.map(p => p.weight));
    const min = Math.min(...points.map(p => p.weight));
    const range = Math.max(1, max - min);
    return (
      <svg width={width} height={height} style={{ border: '1px solid #ddd' }}>
        <polyline
          fill="none"
          stroke="#3f51b5"
          strokeWidth="2"
          points={points.map((p,i)=>{
            const x = margin + (i/(points.length-1))*(width-2*margin);
            const y = margin + ((max - p.weight)/range)*(height-2*margin);
            return `${x},${y}`;
          }).join(' ')}
        />
        {points.map((p,i)=>{
          const x = margin + (i/(points.length-1))*(width-2*margin);
          const y = margin + ((max - p.weight)/range)*(height-2*margin);
          return <circle key={i} cx={x} cy={y} r={3} fill="#3f51b5" />;
        })}
      </svg>
    );
  };

  const renderBarComparison = (weeklyCalories, goalCalories) => {
    const width = 500, height = 120;
    const max = Math.max(weeklyCalories, goalCalories, 1000);
    const w1 = Math.round((weeklyCalories / max) * (width-40));
    const w2 = Math.round((goalCalories / max) * (width-40));
    return (
      <div>
        <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
          <div>
            <div>Weekly Calories</div>
            <div style={{ width: width, height: 24, background: '#eee', position: 'relative' }}>
              <div style={{ width: w1, height: 24, background: '#f44336' }} />
              <div style={{ position: 'absolute', left: 6, top: 2, color: '#fff' }}>{weeklyCalories}</div>
            </div>
          </div>
          <div>
            <div>Goal Calories</div>
            <div style={{ width: width, height: 24, background: '#eee', position: 'relative' }}>
              <div style={{ width: w2, height: 24, background: '#4caf50' }} />
              <div style={{ position: 'absolute', left: 6, top: 2, color: '#fff' }}>{goalCalories}</div>
            </div>
          </div>
        </div>
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

  // compute weekly calories from summary if available, else sum local
  let weeklyCalories = 0;
  if (summary && summary.weeklyTotals) {
    weeklyCalories = Math.round(summary.weeklyTotals.calories);
  } else {
    // sum mocked
    plan.forEach(d => {
      ['lunch','dinner'].forEach(k => {
        if (d[k] && d[k].nutrition && d[k].nutrition.calories) weeklyCalories += Number(d[k].nutrition.calories);
        else if (d[k]) weeklyCalories += (d[k].title ? d[k].title.length * 20 : 300);
      });
    });
  }

  const goalCalories = healthInfo && healthInfo.estimatedCalories && healthInfo.estimatedCalories.weeklyCalorieGoal ? Number(healthInfo.estimatedCalories.weeklyCalorieGoal) : (healthInfo && healthInfo.weeklyCalorieGoal ? Number(healthInfo.weeklyCalorieGoal) : (healthInfo && healthInfo.calorieGoal ? Number(healthInfo.calorieGoal) : 2000*7));

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
          {renderBarComparison(weeklyCalories, goalCalories)}
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <h4>Projected weight change over 12 weeks</h4>
          {renderLineChart(projection)}
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
