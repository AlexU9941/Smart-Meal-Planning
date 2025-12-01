import React, { useState } from "react";
import SignInForm from "./SignInForm";
import CreateAccountForm from "./CreateAccountForm";
import FindRecipes from "./FindRecipes";
import HealthInfoForm from "./HealthInfoForm";
import CustomizeProfile from "./CustomizeProfile";
import GenerateMealPlan from "./GenerateMealPlan";
import NutritionalTracker from "./NutritionalTracker";
import TrackHealth from "./TrackHealth";
import PersonalRecipes from "./PersonalRecipes";
import GroceryPage from "./GroceryPage";

import "./css/App.css";

function App() {
  const [page, setPage] = useState("signin"); 
  const [userSignedIn, setUserSignedIn] = useState(false);

  const handleSignIn = () => {
    setUserSignedIn(true);
    setPage("home"); // Redirect to home after sign in
  };

  const handleSignOut = () => {
    setUserSignedIn(false);
    setPage("signin");
  };

  const renderPage = () => {
    if (!userSignedIn) {
      switch (page) {
        case "signin":
          return <SignInForm onSignIn={handleSignIn} switchToCreate={() => setPage("create")} />;
        case "create":
          return <CreateAccountForm onSignUp={handleSignIn} switchToSignIn={() => setPage("signin")} />;
        default:
          return <SignInForm onSignIn={handleSignIn} switchToCreate={() => setPage("create")} />;
      }
    }

    // Main app pages
    switch (page) {
      case "home":
        return <div className="page-card"><h2>Home</h2><p>Welcome to Smart Meal Planner — pick a page from the sidebar.</p></div>;
      case "find":
        return <FindRecipes />;
      case "health":
        return <HealthInfoForm />;
      case "customize":
        return <CustomizeProfile />;
      case "generate":
        return <GenerateMealPlan />;
      case "nutrition":
        return <NutritionalTracker />;
      case "track":
        return <TrackHealth />;
      case "personal":
        return <PersonalRecipes />;
      case "grocery":
        return <GroceryPage />;
      default:
        return <div className="page-card"><h2>Home</h2><p>Welcome to Smart Meal Planner — pick a page from the sidebar.</p></div>;
    }
  };

  // Visual layout: when not signed in, center auth card, otherwise show sidebar + content
  if (!userSignedIn) {
    return (
      <div className="auth-container">
        <div className="auth-card">
          <h2>Welcome to Smart Meal Planner</h2>
          {renderPage()}
        </div>
      </div>
    );
  }

  return (
    <div className="app-container">
      <aside className="sidebar">
        <h2>Smart Meal Planner</h2>
        <button className={`nav-button ${page === 'home' ? 'active' : ''}`} onClick={() => setPage('home')}>Home</button>
        <button className={`nav-button ${page === 'find' ? 'active' : ''}`} onClick={() => setPage('find')}>Find Recipes</button>
        <button className={`nav-button ${page === 'health' ? 'active' : ''}`} onClick={() => setPage('health')}>Health Info</button>
        <button className={`nav-button ${page === 'customize' ? 'active' : ''}`} onClick={() => setPage('customize')}>Customize Profile</button>
        <button className={`nav-button ${page === 'generate' ? 'active' : ''}`} onClick={() => setPage('generate')}>Generate Meal Plan</button>
        <button className={`nav-button ${page === 'grocery' ? 'active' : ''}`} onClick={() => setPage('grocery')}>Kroger Grocery</button>
        <button className={`nav-button ${page === 'nutrition' ? 'active' : ''}`} onClick={() => setPage('nutrition')}>Nutrition Tracker</button>
        <button className={`nav-button ${page === 'track' ? 'active' : ''}`} onClick={() => setPage('track')}>Track Health</button>
        <button className={`nav-button ${page === 'personal' ? 'active' : ''}`} onClick={() => setPage('personal')}>Personal Recipes</button>
        <div style={{ marginTop: 'auto' }}>
          <button className="nav-button" onClick={handleSignOut}>Sign Out</button>
        </div>
      </aside>

      <main className="main-content">
        <div className="header-bar">
          <h1>Smart Meal Planner</h1>
        </div>
        {renderPage()}
      </main>
    </div>
  );
}

export default App;
