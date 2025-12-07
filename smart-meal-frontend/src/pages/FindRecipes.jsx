import React, { useEffect, useState } from 'react';
import axios from 'axios';

const FindRecipes = () => {
  const [name, setName] = useState('');
  const [budget, setBudget] = useState('');
  const [results, setResults] = useState([]);
  const [message, setMessage] = useState('');

  // filter UI state
  const [showFilter, setShowFilter] = useState(false);
  const [available, setAvailable] = useState({ dishTypes: [], ingredients: [] });
  const [filters, setFilters] = useState({ dietary: '', ingredients: [], maxPrepTime: '', difficulty: '', timeOfDay: '', maxCalories: '' });

  useEffect(() => {
    // fetch available values for filters from backend
    async function fetchAvailable() {
      try {
        const resp = await axios.get('http://localhost:8080/api/recipes/all');
        const list = resp.data;
        const dishSet = new Set();
        const ingSet = new Set();
        list.forEach(r => {
          if (r.dishTypes) r.dishTypes.forEach(dt => dishSet.add(dt));
          if (r.ingredients) r.ingredients.forEach(i => ingSet.add(i.toLowerCase()));
        });
        setAvailable({ dishTypes: Array.from(dishSet), ingredients: Array.from(ingSet) });
      } catch (e) {
        console.warn('Failed to fetch available recipe list for filters', e);
      }
    }
    fetchAvailable();
  }, []);

  const handleSearch = async () => {
    if (isNaN(budget) && budget !== '') {
      setMessage('Invalid budget input. Please enter a number.');
      return;
    }

    try {
      const response = await axios.get('http://localhost:8080/api/recipes/search', {
        params: { name, budget }
      });

      if (response.data.length === 0) {
        setMessage('No recipes match your search criteria.');
        setResults([]);
      } else {
        setResults(response.data);
        setMessage('');
      }
    } catch (error) {
      setMessage('Search failed. Please try again.');
    }
  };

  const toggleFilter = () => setShowFilter(s => !s);

  const handleFilterChange = (k, v) => {
    setFilters(prev => ({ ...prev, [k]: v }));
  };

  const toggleIngredient = (ing) => {
    setFilters(prev => {
      const list = new Set(prev.ingredients || []);
      if (list.has(ing)) list.delete(ing); else list.add(ing);
      return { ...prev, ingredients: Array.from(list) };
    });
  };

  const applyFilters = async () => {
    setMessage('');
    try {
      const params = {};
      if (filters.dietary) params.dietary = filters.dietary;
      if (filters.ingredients && filters.ingredients.length) params.ingredients = filters.ingredients.join(',');
      if (filters.maxPrepTime) params.maxPrepTime = filters.maxPrepTime;
      if (filters.difficulty) params.difficulty = filters.difficulty;
      if (filters.timeOfDay) params.timeOfDay = filters.timeOfDay;
      if (filters.maxCalories) params.maxCalories = filters.maxCalories;

      const resp = await axios.get('http://localhost:8080/api/recipes/filter', { params });
      if (!resp.data || resp.data.length === 0) {
        setResults([]);
        setMessage('No recipes match the selected filters.');
      } else {
        setResults(resp.data);
        setMessage('');
      }
      setShowFilter(false);
    } catch (e) {
      console.error('Filter request failed', e);
      setMessage('Failed to filter recipes. Please try again.');
    }
  };

  const clearFilters = () => {
    setFilters({ dietary: '', ingredients: [], maxPrepTime: '', difficulty: '', timeOfDay: '', maxCalories: '' });
    setMessage('Filters cleared.');
  };

  return (
    <div className="page-card find-recipes">
      <h2>Find Recipes</h2>
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.5rem' }}>
        <input type="text" placeholder="Recipe Name" value={name} onChange={e => setName(e.target.value)} />
        <input type="text" placeholder="Estimated Budget" value={budget} onChange={e => setBudget(e.target.value)} />
        <button onClick={handleSearch}>Search</button>
        <div style={{ position: 'relative' }}>
          <button onClick={toggleFilter} className="secondary">{showFilter ? 'Close Filters' : 'Filters ▾'}</button>
          {showFilter && (
            <div style={{ position: 'absolute', top: '36px', right: 0, width: 420, background: '#fff', border: '1px solid #ddd', padding: 12, boxShadow: '0 4px 12px rgba(0,0,0,0.08)', zIndex: 100 }}>
              <div style={{ display: 'flex', gap: 12 }}>
                <div style={{ minWidth: 180 }}>
                  <div><strong>Dietary restrictions</strong></div>
                  <select value={filters.dietary} onChange={e => handleFilterChange('dietary', e.target.value)}>
                    <option value="">-- any --</option>
                    {available.dishTypes.map(dt => <option key={dt} value={dt}>{dt}</option>)}
                  </select>

                  <div style={{ marginTop: 8 }}><strong>Time of day</strong></div>
                  <select value={filters.timeOfDay} onChange={e => handleFilterChange('timeOfDay', e.target.value)}>
                    <option value="">-- any --</option>
                    {available.dishTypes.map(dt => <option key={'t-'+dt} value={dt}>{dt}</option>)}
                  </select>

                  <div style={{ marginTop: 8 }}><strong>Difficulty</strong></div>
                  <select value={filters.difficulty} onChange={e => handleFilterChange('difficulty', e.target.value)}>
                    <option value="">-- any --</option>
                    <option value="Easy">Easy</option>
                    <option value="Medium">Medium</option>
                    <option value="Hard">Hard</option>
                  </select>
                </div>

                <div style={{ minWidth: 220 }}>
                  <div><strong>Ingredients (click to toggle)</strong></div>
                  <div style={{ maxHeight: 160, overflowY: 'auto', border: '1px solid #f0f0f0', padding: 6 }}>
                    {available.ingredients.map(i => (
                      <div key={i} style={{ display: 'inline-block', margin: 4 }}>
                        <button style={{ padding: '4px 6px', borderRadius: 6, background: filters.ingredients.includes(i) ? '#1976d2' : '#f3f3f3', color: filters.ingredients.includes(i) ? '#fff' : '#222', border: 'none', cursor: 'pointer' }} onClick={() => toggleIngredient(i)}>{i}</button>
                      </div>
                    ))}
                  </div>

                  <div style={{ marginTop: 8 }}><strong>Max prep time (min)</strong></div>
                  <input type="number" value={filters.maxPrepTime} onChange={e => handleFilterChange('maxPrepTime', e.target.value)} />

                  <div style={{ marginTop: 8 }}><strong>Max calories</strong></div>
                  <input type="number" value={filters.maxCalories} onChange={e => handleFilterChange('maxCalories', e.target.value)} />
                </div>
              </div>

              <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', marginTop: 8 }}>
                <button className="secondary" onClick={clearFilters}>Clear</button>
                <button onClick={applyFilters}>Apply</button>
              </div>
            </div>
          )}
        </div>
      </div>

      {message && <p>{message}</p>}
      <div className="results">
        {results.length === 0 && !message && <p>Start a search or apply filters to find recipes.</p>}
        {results.map(recipe => (
          <div key={recipe.id} style={{ border: '1px solid #eee', padding: 8, borderRadius: 6, marginBottom: 8 }}>
            <h4>{recipe.title || recipe.name}</h4>
            {recipe.readyInMinutes && <div>Ready in: {recipe.readyInMinutes} min</div>}
            {recipe.calories !== undefined && <div>Calories: {Math.round(recipe.calories)}</div>}
            {recipe.pricePerServing && <div>Price per serving: ${recipe.pricePerServing}</div>}
          </div>
        ))}
      </div>
    </div>
  );
};

export default FindRecipes;
