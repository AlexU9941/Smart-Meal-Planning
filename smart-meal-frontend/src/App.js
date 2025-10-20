import React from "react";
import CreateAccountForm from "./pages/CreateAccountForm";
import HealthInfoForm from "./pages/HealthInfoForm";
import SignInForm from "./pages/SignInForm";
import UpdateHealthInfo from "./pages/UpdateHealthInfo";

function App() {
  return (
    <div style={{ padding: "2rem", fontFamily: "Arial, sans-serif" }}>
      <h1>Smart Meal Planner</h1>
      <CreateAccountForm />
      <HealthInfoForm />
      <SignInForm />
      <UpdateHealthInfo />
    </div>
  );
}

export default App;
