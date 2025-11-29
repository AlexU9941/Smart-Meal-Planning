import React, { useState } from "react";
import FindRecipes from "./FindRecipes";
import HealthInfoForm from "./HealthInfoForm";
import CustomizeProfile from "./CustomizeProfile";
import GenerateMealPlan from "./GenerateMealPlan";
import NutritionalTracker from "./NutritionalTracker";
import TrackHealth from "./TrackHealth";
import PersonalRecipes from "./PersonalRecipes";
import GroceryPage from "./GroceryPage";
import "./css/App.css";

// ---------------------- Sign In Form ----------------------
function SignInForm({ onSignIn, switchToCreate }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: Replace with real authentication logic
    const success = true;

    if (success) {
      onSignIn(); // Mark user as signed in
    } else {
      alert("Sign in failed");
    }
  };

  return (
    <div>
      <h2>Sign In</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Sign In</button>
      </form>
      <p>
        Don't have an account?{" "}
        <button onClick={switchToCreate}>Create Account</button>
      </p>
    </div>
  );
}

// ---------------------- Create Account Form ----------------------
function CreateAccountForm({ onSignUp, switchToSignIn }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    // TODO: Replace with real account creation logic
    const success = true;

    if (success) {
      onSignUp(); // Auto sign in after account creation
    } else {
      alert("Account creation failed");
    }
  };

  return (
    <div>
      <h2>Create Account</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Create Account</button>
      </form>
      <p>
        Already have an account?{" "}
        <button onClick={switchToSignIn}>Sign In</button>
      </p>
    </div>
  );
}

// ---------------------- Main App ----------------------
function App() {
  const [page, setPage] = useState("signin"); // default page
  const [userSignedIn, setUserSignedIn] = useState(false);

  const handleSignIn = () => {
    setUserSignedIn(true);
    setPage("home"); // redirect to main page after sign in
  };

  const handleSignOut = () => {
    setUserSignedIn(false);
    setPage("signin");
  };

  const renderPage = () => {
    if (!userSignedIn) {
      switch (page) {
        case "signin":
          return (
            <SignInForm
              onSignIn={handleSignIn}
              switchToCreate={() => setPage("create")}
            />
          );
        case "create":
          return (
            <CreateAccountForm
              onSignUp={handleSignIn}
              switchToSignIn={() => setPage("signin")}
            />
          );
        default:
          return (
            <SignInForm
              onSignIn={handleSignIn}
              switchToCreate={() => setPage("create")}
            />
          );
      }
    }

    // Main app pages
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
        return <div>Welcome to Smart Meal Planner!</div>;
    }
  };

  return (
    <div style={{ padding: "2rem", fontFamily: "Arial, sans-serif" }}>
      <h1>Smart Meal Planner</h1>

      {userSignedIn && (
        <nav
          style={{ marginBottom: "1rem", display: "flex", gap: "0.5rem" }}
        >
          <button onClick={() => setPage("home")}>Home</button>
          <button onClick={() => setPage("find")}>Find Recipes</button>
          <button onClick={() => setPage("health")}>Health Info</button>
          <button onClick={() => setPage("customize")}>Customize Profile</button>
          <button onClick={() => setPage("generate")}>Generate Meal Plan</button>
          <button onClick={() => setPage("nutrition")}>Nutrition Tracker</button>
          <button onClick={() => setPage("track")}>Track Health</button>
          <button onClick={() => setPage("personal")}>Personal Recipes</button>
          <button onClick={() => setPage("grocery")}>Grocery Page</button>
          <button onClick={handleSignOut}>Sign Out</button>
        </nav>
      )}

      {renderPage()}
    </div>
  );
}

export default App;

/*import React, { useState } from "react";
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
import GroceryPage from "./GroceryPage";
import NutritionalTracker from "./NutritionalTracker";
import TrackHealth from "./TrackHealth";
import PersonalRecipes from "./PersonalRecipes";

import "./css/App.css";

function App() {
  const [page, setPage] = useState("signin"); // Default to sign in
  const [userSignedIn, setUserSignedIn] = useState(false);

  const handleSignIn = () => {
    setUserSignedIn(true);
    setPage("home"); // redirect to main page after sign in
  };

  const renderPage = () => {
    // If user is not signed in, restrict to signin/create account/recover
    if (!userSignedIn) {
      switch (page) {
        case "signin":
          return <SignInForm onSignIn={handleSignIn} />;
        case "create":
          return <CreateAccountForm onSignUp={handleSignIn} />; // auto sign in after create
        case "recover":
          return <RecoverPassword />;
        default:
          return <SignInForm onSignIn={handleSignIn} />;
      }
    }

    // Main app pages
    switch (page) {
      case "track":
        return <TrackHealth />;
      case "personal":
        return <PersonalRecipes />;
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
      case "grocery":
        return <GroceryPage />;
      case "home":
        return <div>Welcome to Smart Meal Planner!</div>;
      default:
        return <SignInForm onSignIn={handleSignIn} />;
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
          <button onClick={() => setPage("grocery")}>Grocery Page</button>
          <button onClick={() => { setUserSignedIn(false); setPage("signin"); }}>Sign Out</button>
        </nav>
      )}

      {renderPage()}
    </div>
  );
}

export default App;
*/



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
