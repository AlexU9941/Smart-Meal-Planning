import React from "react";
import { BrowserRouter as Router, Routes, Route, NavLink } from "react-router-dom";

import CreateAccountForm from "./CreateAccountForm";
import SignInForm from "./SignInForm";
import RecipeCard from "./RecipeCard";
import RecoverPassword from "./RecoverPassword";
import FindRecipes from "./FindRecipes";
import HealthInfoForm from "./HealthInfoForm";
import IngredientInput from "./IngredientInput";
import Budget from "./Budget";
import ProviderSelector from "./ProviderSelector"; 
import GrocerySearch from "./GrocerySearch";
import GroceryPage from "./GroceryPage";
import "./css/App.css";

const Sidebar = () => {
  return (
    <div className="sidebar">
      <h2>Smart Meal Planner</h2>
      <NavLink to="/create-account" className={({ isActive }) => (isActive ? "active" : "")}>Create Account</NavLink>
      <NavLink to="/sign-in" className={({ isActive }) => (isActive ? "active" : "")}>Sign In</NavLink>
      <NavLink to="/recover-password" className={({ isActive }) => (isActive ? "active" : "")}>Recover Password</NavLink>
      <NavLink to="/health-info" className={({ isActive }) => (isActive ? "active" : "")}>Health Info</NavLink>
      <NavLink to="/ingredient-input" className={({ isActive }) => (isActive ? "active" : "")}>Ingredient Input</NavLink>
      <NavLink to="/budget" className={({ isActive }) => (isActive ? "active" : "")}>Budget</NavLink>
      <NavLink to="/find-recipes" className={({ isActive }) => (isActive ? "active" : "")}>Find Recipes</NavLink>
      <NavLink to="/recipe-card" className={({ isActive }) => (isActive ? "active" : "")}>Recipe Card</NavLink>
      <NavLink to="/grocery" className={({ isActive }) => (isActive ? "active" : "")}>Grocery Integration</NavLink>

    </div>
  );
};

const Layout = ({ children }) => {
  return (
    <div className="app-container">
      <Sidebar />
      <div className="main-content">{children}</div>
    </div>
  );
};

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/create-account" element={<CreateAccountForm />} />
          <Route path="/sign-in" element={<SignInForm />} />
          <Route path="/recover-password" element={<RecoverPassword />} />
          <Route path="/health-info" element={<HealthInfoForm />} />
          <Route path="/ingredient-input" element={<IngredientInput />} />
          <Route path="/budget" element={<Budget />} />
          <Route path="/find-recipes" element={<FindRecipes />} />
          <Route path="/recipe-card" element={<RecipeCard />} />
          <Route path="*" element={<SignInForm />} /> {/* Default route */}
          <Route path="/grocery" element={<GroceryPage />} />
        </Routes>
      </Layout>
    </Router>
  );
}

export default App;
