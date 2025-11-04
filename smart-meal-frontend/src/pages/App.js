import React from "react";
import CreateAccountForm from "./CreateAccountForm";
import SignInForm from "./SignInForm";
import RecipeCard from "./RecipeCard";
import RecoverPassword from "./RecoverPassword";
import FindRecipes from "./FindRecipes";
import HealthInfoForm from "./HealthInfoForm";
import CustomizeProfile from "./CustomizeProfile";

function App() {
  const [page, setPage] = useState("home");

  const renderPage = () => {
    switch (page) {
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
      default:
        return <HealthInfoForm />;
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
      </nav>

      {renderPage()}
    </div>
  );
}

export default App;
