import React from "react";
import CreateAccountForm from "./CreateAccountForm";
import SignInForm from "./SignInForm";

function App() {
  return (
    <div style={{ padding: "2rem", fontFamily: "Arial, sans-serif" }}>
      <h1>Smart Meal Planner</h1>
      {/* <CreateAccountForm /> */}
      <SignInForm />
    </div>
  );
}

export default App;
