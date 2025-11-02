import React from "react";
import CreateAccountForm from "./CreateAccountForm";
import SignInForm from "./SignInForm";
import RecipeCard from "./RecipeCard";
import RecoverPassword from "./RecoverPassword";
import FindRecipes from "./FindRecipes";
import HealthInfoForm from "./HealthInfoForm";

function App() {
  return (
    <div style={{ padding: "2rem", fontFamily: "Arial, sans-serif" }}>
      <h1>Smart Meal Planner</h1>
      {/* <CreateAccountForm /> */}
       {/* <SignInForm /> */}
       {/* <RecoverPassword /> */}
        {/* <FindRecipes /> */}
        <HealthInfoForm />
        {/* <RecipeCard /> */}
    </div>
  );
}

export default App;
