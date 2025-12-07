import React, { useEffect, useState } from 'react';
import axios from 'axios';

/* -----------------------------------------------------------
   SAFE INPUT VALIDATION — placed at top so ESLint detects usage
------------------------------------------------------------ */
const isUnsupportedInput = (text) => {
  if (!text) return false;

  const low = text.toLowerCase();

  // Detect script tags
  if (low.includes("<script")) return true;

  // Detect dangerous script URLs (ESLint-safe regex)
  if (/^\s*javascript:/i.test(low)) return true;

  return false;
};

const STORAGE_USER_ID_KEY = 'userId';
const STORAGE_USER_EMAIL_KEY = 'userEmail';

const PersonalRecipes = () => {
  // const [userId, setUserId] = useState(localStorage.getItem(STORAGE_USER_ID_KEY) || '');
  // const [email, setEmail] = useState(localStorage.getItem(STORAGE_USER_EMAIL_KEY) || '');
  //const storedUser = JSON.parse(localStorage.getItem("user")); 
  const [userId, setUserId] = useState(null);
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // form state
  const [showForm, setShowForm] = useState(false);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [servings, setServings] = useState(1);
  const [prepMinutes, setPrepMinutes] = useState(10);
  const [message, setMessage] = useState('');

  //set user id 
  useEffect(() => {
  const storedUser = JSON.parse(localStorage.getItem("user"));
  if (!storedUser || !storedUser.uid) {
  setError("You must be logged in to view personal recipes.");
  setLoading(false);
  return;
  }
  setUserId(Number(storedUser.uid));
  }, []);

  //load saved recipes. 
  useEffect(() => {
    if (userId) {
      fetchRecipes(userId);
    }
  }, [userId]);

  // const fetchRecipes = async (uid) => {
  //   setError('');
  //   setLoading(true);
  //   try {
  //     const resp = await axios.get(`http://localhost:8080/user-recipes/${uid}`);
  //     setRecipes(resp.data || []);
  //   } catch (e) {
  //     console.error('Failed to load user recipes', e);
  //     setError('Failed to load personal recipes. If you are offline, recipes cannot be retrieved.');
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  const fetchRecipes = async (uid) => {
  setLoading(true);
  setError('');

  try {
    const resp = await axios.get(`http://localhost:8080/user-recipes/${uid}`);
    setRecipes(resp.data || []);
  } catch (e) {
    setError("Failed to load personal recipes.");
  } finally {
    setLoading(false);
  }

  };

  const handleAddClick = () => {
    setTitle('');
    setContent('');
    setServings(1);
    setPrepMinutes(10);
    setMessage('');
    setShowForm(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    // if (!userId) {
    //   setError('No user id found. Please enter your user id to save recipes.');
    //   return;
    // }

    if (!title.trim()) { setMessage('Please provide a title.'); return; }
    if (!content.trim()) { setMessage('Please provide the recipe content.'); return; }
    if (isUnsupportedInput(title) || isUnsupportedInput(content)) {
      setMessage('Unsupported input detected (e.g. script tags or javascript URLs). Please remove it.');
      return;
    }
    if (content.length > 10000) { setMessage('Recipe content is too long. Max 10000 characters.'); return; }

    const payload = {
      userId: userId ? Number(userId) : null,
      title: title.trim(),
      recipeContent: content.trim(),
      servings: servings ? Number(servings) : null,
      prepMinutes: prepMinutes ? Number(prepMinutes) : null
    };

    try {
      const resp = await axios.post('http://localhost:8080/user-recipes', payload);
      if (resp && resp.data) {
        setMessage('Recipe saved locally and to the server.');
        // refresh list
        fetchRecipes(userId);
        setShowForm(false);
      } else {
        setError('Unexpected server response when saving recipe.');
      }
    } catch (err) {
      console.error('Failed to save personal recipe', err);
      setError('Failed to save recipe to the server. Your recipe may not be saved.');
    }
  };

  // const handleUserIdSave = () => {
  //   localStorage.setItem(STORAGE_USER_ID_KEY, userId);
  //   //if (email) localStorage.setItem(STORAGE_USER_EMAIL_KEY, email);
  //   setMessage('User id saved. Fetching your recipes...');
  //   fetchRecipes(userId);
  // };

  return (
    <div className="personal-recipes" style={{ maxWidth: 900, margin: '1rem auto', padding: 12 }}>
      <h2>Personal Recipes</h2>
      <p>Upload and manage your own recipes!</p>

{/*
      {!userId && (
        <div style={{ marginBottom: 12 }}>
          <label>Enter your user id (required to save): </label>
          <input value={userId} onChange={e => setUserId(e.target.value)} placeholder="123" />
          <label style={{ marginLeft: 8 }}>Email (optional): </label>
          <input value={email} onChange={e => setEmail(e.target.value)} placeholder="you@example.com" />
          <div style={{ marginTop: 6 }}>
            <button onClick={handleUserIdSave}>Save user id</button>
          </div>
        </div>
      )}
    */}

      <div style={{ marginBottom: 12 }}>
        <button onClick={handleAddClick} style={{ padding: '8px 12px' }}>Add Personal Recipe</button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} style={{ marginBottom: 12, border: '1px solid #eee', padding: 12, borderRadius: 6 }}>
          <div>
            <label>Title</label><br />
            <input value={title} onChange={e => setTitle(e.target.value)} style={{ width: '100%' }} />
          </div>
          <div style={{ marginTop: 8 }}>
            <label>Recipe (ingredients & steps)</label><br />
            <textarea value={content} onChange={e => setContent(e.target.value)} rows={10} style={{ width: '100%' }} />
          </div>
          <div style={{ display: 'flex', gap: 8, marginTop: 8 }}>
            <div>
              <label>Servings</label><br />
              <input type="number" value={servings} onChange={e => setServings(e.target.value)} min={1} />
            </div>
            <div>
              <label>Prep minutes</label><br />
              <input type="number" value={prepMinutes} onChange={e => setPrepMinutes(e.target.value)} min={0} />
            </div>
          </div>
          <div style={{ marginTop: 8, display: 'flex', gap: 8 }}>
            <button type="submit" style={{ background: '#1976d2', color: '#fff', border: 'none', padding: '8px 12px' }}>Save Recipe</button>
            <button type="button" onClick={() => setShowForm(false)}>Cancel</button>
          </div>
          {message && <div style={{ color: '#2e7d32', marginTop: 8 }}>{message}</div>}
          {error && <div style={{ color: '#d32f2f', marginTop: 8 }}>{error}</div>}
        </form>
      )}

      <div>
        <h3>Your Recipes</h3>
        {loading && <div>Loading...</div>}
        {error && <div style={{ color: '#d32f2f' }}>{error}</div>}
        {!loading && recipes.length === 0 && <div>No personal recipes found.</div>}
        <div style={{ display: 'grid', gap: 12, marginTop: 8 }}>
          {recipes.map(r => (
            <div key={r.id} style={{ border: '1px solid #eee', padding: 8, borderRadius: 6 }}>
              <h4>{r.title}</h4>
              <div>Servings: {r.servings || '-' } | Prep: {r.prepMinutes || '-'} min</div>
              <div style={{ marginTop: 8, whiteSpace: 'pre-wrap' }}>{r.recipeContent}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default PersonalRecipes;
