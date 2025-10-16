import logo from './logo.svg';
import CreateAccountForm from './CreateAccountForm';
import SignInForm from './SignInForm'
import HealthInfoForm from './HealthInfoForm'
import UpdatePersonalHealthInfo from './UpdatePersonalHealthInfo'
import './App.css';

function App() {
    return (
        <div>
            <h1>Smart Meal Planning</h1>
            <CreateAccountForm />
            <SignInForm />
            <HealthInfoForm />
            <UpdatePersonalHealthInfo />
        </div>
    );
}

export default App;