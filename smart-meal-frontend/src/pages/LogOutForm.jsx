import { useState } from 'react';
import axios from 'axios';

export default function LogOutForm() {
const [message, setMessage] = useState('');

    const handleLogOut = async e => {
        e.preventDefault();

    try {
        const res = await axios.post('http://localhost:8080/api/log-out');
        setMessage('Logged out successfully!');
        // TODO: Redirect to sign-in page
    }
    catch (err) {
        setMessage('Log out failed.');
    }
};

    return(
    <div style={{ padding: '1rem' }}>
        <button onClick={handleLogout}>Log Out</button>
        <p>{message}</p>
    </div>
    );
}
