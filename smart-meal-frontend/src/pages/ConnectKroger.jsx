import React from "react";

const ConnectKroger = ({ onConnected }) => {
  const handleConnect = () => {
    window.open("http://localhost:8080/api/kroger/connect", "_blank");

  };

  return (
    <div>
      <h2>Connect to Kroger</h2>
      <button onClick={handleConnect}>Connect</button>
    </div>
  );
};

export default ConnectKroger;
