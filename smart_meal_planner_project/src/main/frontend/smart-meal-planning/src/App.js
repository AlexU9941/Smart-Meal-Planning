import logo from './logo.svg';
import CreateAccountForm from './CreateAccountForm';
import SignInForm from './SignInForm'
import HealthInfoForm from './HealthInfoForm'
import UpdateHealthInfo from "./UpdateHealthInfo";
import './App.css';

function App() {
    return (
    <div>
      <h1>Smart Meal Planning</h1>
      <CreateAccountForm />
      <SignInForm />
      <HealthInfoForm />
      <UpdateHealthInfo />
    </div>
  );
}

export default App;
