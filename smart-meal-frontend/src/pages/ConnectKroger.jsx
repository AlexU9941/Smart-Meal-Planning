import React, { useEffect, useState } from "react";

const ConnectKroger = ({ onConnected }) => {
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    // Function defined inside useEffect to avoid missing dependency warning
    const checkStatus = async () => {
      try {
        const res = await fetch("http://localhost:8080/api/kroger/status", {
          credentials: "include",
        });
        const data = await res.json();
        if (data.connected) {
          setConnected(true);
          onConnected && onConnected();
        }
      } catch (err) {
        console.error("Failed to check Kroger connection:", err);
      }
    };

    // Initial check
    checkStatus();

    // Poll every 2 seconds to detect connection after OAuth redirect
    const interval = setInterval(checkStatus, 2000);

    // Cleanup interval on unmount
    return () => clearInterval(interval);
  }, [onConnected]); // only external dependency

  const handleConnect = () => {
    // Open OAuth in a separate tab
    window.open(
      "http://localhost:8080/api/kroger/connect",
      "_blank",
      "width=500,height=600"
    );
  };

  return (
    <div>
      <h2>Connect to Kroger</h2>
      {connected ? (
        <div style={{ color: "green" }}>Connected!</div>
      ) : (
        <button onClick={handleConnect}>Connect</button>
      )}
    </div>
  );
};

export default ConnectKroger;
