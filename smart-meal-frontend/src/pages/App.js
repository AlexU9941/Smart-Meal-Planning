import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, NavLink } from "react-router-dom";

import CreateAccountForm from "./CreateAccountForm";
import SignInForm from "./SignInForm";
import RecipeCard from "./RecipeCard";
import RecoverPassword from "./RecoverPassword";
import FindRecipes from "./FindRecipes";
import HealthInfoForm from "./HealthInfoForm";
import CustomizeProfile from "./CustomizeProfile";
import GenerateMealPlan from "./GenerateMealPlan";
import IngredientInput from "./IngredientInput";
import Budget from "./Budget";
import ProviderSelector from "./ProviderSelector"; 
import GrocerySearch from "./GrocerySearch";
import GroceryPage from "./GroceryPage";
import "./css/App.css";
import NutritionalTracker from "./NutritionalTracker";
import TrackHealth from "./TrackHealth";
import PersonalRecipes from "./PersonalRecipes";


function App() {
  const [page, setPage] = useState("home");

  const renderPage = () => {
    switch (page) {
      case "track":
        return <TrackHealth />;
      case "personal":
        return <PersonalRecipes />;
      case "create":
        return <CreateAccountForm />;
      case "signin":
        return <SignInForm />;
      case "recover":
        return <RecoverPassword />;
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
      case "home":
        return <div>Welcome to Smart Meal Planner!</div>;
      default:
        return <SignInForm />;
    }
  };

  return (
    <div style={{ padding: "2rem", fontFamily: "Arial, sans-serif" }}>
      <h1>Smart Meal Planner</h1>

      <nav style={{ marginBottom: "1rem", display: "flex", gap: "0.5rem" }}>
        <button onClick={() => setPage("home")}>Home</button>
        <button onClick={() => setPage("find")}>Find Recipes</button>
        <button onClick={() => setPage("health")}>Health Info</button>
        <button onClick={() => setPage("customize")}>Customize Profile</button>
        <button onClick={() => setPage("generate")}>Generate Meal Plan</button>
        <button onClick={() => setPage("nutrition")}>Nutrition Tracker</button>
        <button onClick={() => setPage("track")}>Track Health</button>
        <button onClick={() => setPage("personal")}>Personal Recipes</button>
      </nav>

      {renderPage()}
    </div>
  );
}

export default App;





// TEMP COMMENTING to see what Srinath's looks like 
//const Sidebar = () => {
//   return (
//     <div className="sidebar">
//       <h2>Smart Meal Planner</h2>
//       <NavLink to="/create-account" className={({ isActive }) => (isActive ? "active" : "")}>Create Account</NavLink>
//       <NavLink to="/sign-in" className={({ isActive }) => (isActive ? "active" : "")}>Sign In</NavLink>
//       <NavLink to="/recover-password" className={({ isActive }) => (isActive ? "active" : "")}>Recover Password</NavLink>
//       <NavLink to="/health-info" className={({ isActive }) => (isActive ? "active" : "")}>Health Info</NavLink>
//       <NavLink to="/ingredient-input" className={({ isActive }) => (isActive ? "active" : "")}>Ingredient Input</NavLink>
//       <NavLink to="/budget" className={({ isActive }) => (isActive ? "active" : "")}>Budget</NavLink>
//       <NavLink to="/find-recipes" className={({ isActive }) => (isActive ? "active" : "")}>Find Recipes</NavLink>
//       <NavLink to="/recipe-card" className={({ isActive }) => (isActive ? "active" : "")}>Recipe Card</NavLink>
//       <NavLink to="/grocery" className={({ isActive }) => (isActive ? "active" : "")}>Grocery Integration</NavLink>

//     </div>
//   );
// };

// const Layout = ({ children }) => {
//   return (
//     <div className="app-container">
//       <Sidebar />
//       <div className="main-content">{children}</div>
//     </div>
//   );
// };

// function App() {
//   return (
//     <Router>
//       <Layout>
//         <Routes>
//           <Route path="/create-account" element={<CreateAccountForm />} />
//           <Route path="/sign-in" element={<SignInForm />} />
//           <Route path="/recover-password" element={<RecoverPassword />} />
//           <Route path="/health-info" element={<HealthInfoForm />} />
//           <Route path="/ingredient-input" element={<IngredientInput />} />
//           <Route path="/budget" element={<Budget />} />
//           <Route path="/find-recipes" element={<FindRecipes />} />
//           <Route path="/recipe-card" element={<RecipeCard />} />
//           <Route path="*" element={<SignInForm />} /> {/* Default route */}
//           <Route path="/grocery" element={<GroceryPage />} />
//         </Routes>
//       </Layout>
//     </Router>
//   );
// }

// export default App;
