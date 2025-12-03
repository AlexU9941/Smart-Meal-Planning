import React, { useState, useEffect } from "react";
import axios from "axios";

export default function Budget() {
  const [budget, setBudget] = useState(null); // stores existing budget
  const [amount, setAmount] = useState(""); // input field
  const [editing, setEditing] = useState(false); // toggle change mode
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");

  // Load the user's current budget from backend
  const loadBudget = async () => {
    setLoading(true);
    setMessage("");

    try {
      const res = await axios.get("http://localhost:8080/api/budget", {
        withCredentials: true,
      });

      if (res.data) {
        setBudget(res.data.amount);
      } else {
        setBudget(null);
      }
    } catch (err) {
      console.error("Error loading budget:", err);
      setMessage("Unable to load budget.");
    } finally {
      setLoading(false);
    }
  };

  // Load on mount
  useEffect(() => {
    loadBudget();
  }, []);

  // Save or change budget
  const handleSave = async (e) => {
    e.preventDefault();
    setMessage("");

    if (!amount || Number(amount) <= 0) {
      setMessage("Please enter a valid budget amount.");
      return;
    }

    try {
      const res = await axios.post(
        "http://localhost:8080/api/budget",
        { amount: parseFloat(amount) },
        { withCredentials: true }
      );

      setBudget(res.data.amount);
      setAmount("");
      setEditing(false);
      setMessage("Budget saved successfully!");
    } catch (err) {
      console.error("Error saving budget:", err);
      setMessage("Failed to save budget.");
    }
  };

  return (
    <div className="max-w-xl mx-auto p-5 bg-white rounded-2xl shadow-md space-y-4">
      <h2 className="text-2xl font-semibold text-center">Manage Budget</h2>

      {loading ? (
        <p className="text-center">Loading...</p>
      ) : (
        <>
          {/* Display current budget */}
          {budget !== null ? (
            <div className="text-center">
              <p className="text-lg font-medium">
                Current Budget:{" "}
                <span className="font-bold text-green-700">
                  ${budget.toFixed(2)}
                </span>
              </p>

              {!editing ? (
                <button
                  className="mt-3 bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
                  onClick={() => setEditing(true)}
                >
                  Change Budget
                </button>
              ) : (
                <form onSubmit={handleSave} className="mt-3 space-y-3">
                  <input
                    type="number"
                    step="0.01"
                    placeholder="Enter new budget"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    className="border p-2 rounded w-full"
                  />

                  <div className="flex justify-center space-x-3">
                    <button
                      type="submit"
                      className="bg-green-600 text-white px-4 py-2 rounded-md hover:bg-green-700"
                    >
                      Save
                    </button>

                    <button
                      type="button"
                      className="bg-gray-400 text-white px-4 py-2 rounded-md hover:bg-gray-500"
                      onClick={() => {
                        setEditing(false);
                        setAmount("");
                      }}
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              )}
            </div>
          ) : (
            // No budget exists → set a new one
            <form onSubmit={handleSave} className="space-y-3">
              <p className="text-center text-lg font-medium">Set Your Budget</p>

              <input
                type="number"
                step="0.01"
                placeholder="Enter budget amount"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="border p-2 rounded w-full"
              />

              <button
                type="submit"
                className="w-full bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
              >
                Set Budget
              </button>
            </form>
          )}

          {message && (
            <p className="text-center text-green-700 font-medium">{message}</p>
          )}
        </>
      )}
    </div>
  );
}
