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
import IngredientInput from "./IngredientInput";
import Budget from "./Budget";

import "./css/App.css";

function App() {
  const [page, setPage] = useState("signin");
  const [userSignedIn, setUserSignedIn] = useState(false);
  const [mealPlanIngredients, setMealPlanIngredients] = useState([]);

  const handleSignIn = () => {
    setUserSignedIn(true);
    setPage("home");
  };

  const handleSignOut = () => {
    setUserSignedIn(false);
    setPage("signin");
  };

  const renderPage = () => {
    if (!userSignedIn) {
      if (page === "signin") {
        return (
          <SignInForm
            onSignIn={handleSignIn}
            switchToCreate={() => setPage("create")}
          />
        );
      }
      if (page === "create") {
        return (
          <CreateAccountForm
            onSignUp={handleSignIn}
            switchToSignIn={() => setPage("signin")}
          />
        );
      }
      return null;
    }

    switch (page) {
      case "home":
        return <div>Welcome to Smart Meal Planner!</div>;
      case "find":
        return <FindRecipes />;
      case "health":
        return <HealthInfoForm />;
      case "customize":
        return <CustomizeProfile />;
      case "generate":
        return <GenerateMealPlan onIngredientsGenerated={setMealPlanIngredients}/>;
      case "nutrition":
        return <NutritionalTracker />;
      case "track":
        return <TrackHealth />;
      case "personal":
        return <PersonalRecipes />;
      case "grocery":
        return <GroceryPage mealPlanIngredients={mealPlanIngredients}/>;
      default:
        return <div>Welcome to Smart Meal Planner!</div>;
    }
  };

  return (
    <div style={{ padding: "2rem", fontFamily: "Arial, sans-serif" }}>
      <h1>Smart Meal Planner</h1>

      {userSignedIn && (
        <nav style={{ marginBottom: "1rem", display: "flex", gap: "0.5rem" }}>
          <button onClick={() => setPage("home")}>Home</button>
          <button onClick={() => setPage("find")}>Find Recipes</button>
          <button onClick={() => setPage("health")}>Health Info</button>
          <button onClick={() => setPage("customize")}>Customize Profile</button>
          <button onClick={() => setPage("generate")}>Generate Meal Plan</button>
          <button onClick={() => setPage("nutrition")}>Nutrition Tracker</button>
          <button onClick={() => setPage("track")}>Track Health</button>
          <button onClick={() => setPage("personal")}>Personal Recipes</button>
          <button onClick={() => setPage("grocery")}>Kroger Grocery List</button>
          <button onClick={() => setPage("ingredient")}>Ingredient Input</button>
          <button onClick={() => setPage("budget")}>Budget</button>
          <button onClick={handleSignOut}>Sign Out</button>
        </nav>
      )}

      {renderPage()}
    </div>
  );
}

export default App;
