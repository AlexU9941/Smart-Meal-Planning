import React from "react";

const ConnectKroger = ({ onConnected }) => {
  const handleConnect = () => {
    // Open a small popup window for OAuth
    const width = 600;
    const height = 700;
    const left = window.screen.width / 2 - width / 2;
    const top = window.screen.height / 2 - height / 2;

    const oauthWindow = window.open(
      "http://localhost:8080/api/kroger/connect",
      "KrogerConnect",
      `width=${width},height=${height},top=${top},left=${left}`
    );

    // Poll the popup to detect when it closes
    const interval = setInterval(() => {
      if (oauthWindow.closed) {
        clearInterval(interval);

        // Optionally, check backend for token status
        fetch("http://localhost:8080/api/kroger/status")
          .then((res) => res.json())
          .then((connected) => {
            if (connected) onConnected();
          })
          .catch((err) => console.error(err));
      }
    }, 500);
  };

  return (
    <div>
      <h2>Connect to Kroger</h2>
      <button onClick={handleConnect}>Connect</button>
    </div>
  );
};

export default ConnectKroger;
