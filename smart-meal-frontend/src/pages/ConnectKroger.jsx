import React from "react";

const ConnectKroger = ({ onConnected }) => {
  const handleConnect = () => {
    const width = 500;
    const height = 600;
    const left = window.screenX + (window.innerWidth - width) / 2;
    const top = window.screenY + (window.innerHeight - height) / 2;

    // Open popup to your backend /connect endpoint
    const popup = window.open(
      "http://localhost:8080/api/kroger/connect",
      "Kroger OAuth",
      `width=${width},height=${height},top=${top},left=${left}`
    );

  const timer = setInterval(async () => {
    if (!popup || popup.closed) {
      clearInterval(timer);
      try {
        // Fix 1: Use full URL or rely on proxy
        const res = await fetch("http://localhost:8080/api/kroger/status");
        // OR: fetch("/api/kroger/status") if proxy is configured

        if (!res.ok) throw new Error("Status check failed");

        const data = await res.json();

        if (data.connected) {
          onConnected();
        } else {
          alert("Kroger login cancelled or failed.");
        }
      } catch (err) {
        console.error("Error checking Kroger status:", err);
        alert("Connection failed. Is the backend running?");
      }
    }
  }, 800); // slightly longer interval is safer
  };

  return (
    <button onClick={handleConnect} className="button-primary">
      Connect Kroger
    </button>
  );
};

export default ConnectKroger;
