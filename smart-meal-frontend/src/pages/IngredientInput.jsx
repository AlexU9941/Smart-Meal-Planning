import React, { useState, useEffect } from "react";
import axios from "axios";

export default function IngredientInput() {
  const [ingredients, setIngredients] = useState([
    { name: "", quantity: "", unit: "" },
  ]);

  const [savedIngredients, setSavedIngredients] = useState([]);
  const [loading, setLoading] = useState(false);
  const [loadingSaved, setLoadingSaved] = useState(false);
  const [error, setError] = useState(null);

  // Update a row in the ingredient form
  const handleChange = (index, event) => {
    const { name, value } = event.target;
    const updated = [...ingredients];
    updated[index][name] = value;
    setIngredients(updated);
  };

  // Add a blank ingredient row
  const addIngredient = () => {
    setIngredients([...ingredients, { name: "", quantity: "", unit: "" }]);
  };

  // Remove a row from the form only
  const removeIngredientRow = (index) => {
    setIngredients(ingredients.filter((_, i) => i !== index));
  };

  // Load saved ingredients for the current logged-in user
  const loadSavedIngredients = async () => {
    setLoadingSaved(true);
    setError(null);

    try {
      const res = await axios.get("http://localhost:8080/api/ingredients", {
        withCredentials: true,
      });

      setSavedIngredients(res.data || []);
    } catch (err) {
      console.error("Error loading saved ingredients:", err);
      setError("Failed to load your saved ingredients.");
    } finally {
      setLoadingSaved(false);
    }
  };

  // Load when component mounts
  useEffect(() => {
    loadSavedIngredients();
  }, []);

  // Submit all ingredient rows to the backend
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await axios.post("http://localhost:8080/api/ingredients", ingredients, {
        withCredentials: true,
      });

      setLoading(false);

      // Reset form to a single blank ingredient row
      setIngredients([{ name: "", quantity: "", unit: "" }]);

      // IMPORTANT FIX: Reload full saved ingredient list
      await loadSavedIngredients();

      alert("All ingredients saved successfully!");
    } catch (err) {
      setLoading(false);
      console.error("Error saving ingredients:", err);
      setError("Failed to save ingredients. Please try again.");
    }
  };

  // Delete a saved ingredient from DB
  const deleteIngredient = async (id) => {
    setError(null);

    try {
      await axios.delete(`http://localhost:8080/api/ingredients/${id}`, {
        withCredentials: true,
      });

      // Update list immediately
      setSavedIngredients((prev) => prev.filter((ing) => ing.id !== id));
    } catch (err) {
      console.error("Error deleting ingredient:", err);
      setError("Failed to delete ingredient. Please try again.");
    }
  };

  return (
    <div className="max-w-xl mx-auto p-4 bg-white rounded-2xl shadow-md space-y-4">
      <h2 className="text-xl font-semibold text-center">Input Ingredients</h2>

      <form onSubmit={handleSubmit} className="space-y-4">
        {ingredients.map((ingredient, index) => (
          <div key={index} className="flex space-x-2 items-center">
            <input
              type="text"
              name="name"
              placeholder="Name"
              value={ingredient.name}
              onChange={(e) => handleChange(index, e)}
              className="border p-2 rounded w-1/2"
              required
            />

            <input
              type="number"
              name="quantity"
              placeholder="Qty"
              value={ingredient.quantity}
              onChange={(e) => handleChange(index, e)}
              className="border p-2 rounded w-1/4"
            />

            <input
              type="text"
              name="unit"
              placeholder="Unit (g, ml, etc.)"
              value={ingredient.unit}
              onChange={(e) => handleChange(index, e)}
              className="border p-2 rounded w-1/4"
            />

            <button
              type="button"
              onClick={() => removeIngredientRow(index)}
              className="text-red-500 hover:underline"
            >
              ✕
            </button>
          </div>
        ))}

        <div className="flex justify-between">
          <button
            type="button"
            onClick={addIngredient}
            className="text-blue-600 hover:underline"
          >
            + Add another
          </button>

          <button
            type="submit"
            disabled={loading}
            className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
          >
            {loading ? "Saving..." : "Save All"}
          </button>
        </div>
      </form>

      {error && <p className="text-red-600 text-center">{error}</p>}

      <hr className="my-4" />

      <div>
        <h3 className="text-lg font-semibold mb-2">Your saved ingredients</h3>

        {loadingSaved ? (
          <p>Loading...</p>
        ) : savedIngredients.length === 0 ? (
          <p>No ingredients saved yet.</p>
        ) : (
          <ul className="space-y-2">
            {savedIngredients.map((ing) => (
              <li
                key={ing.id}
                className="flex justify-between items-center border p-2 rounded"
              >
                <span>
                  {ing.name}
                  {ing.quantity &&
                  ing.quantity !== "" &&
                  Number(ing.quantity) !== 0
                    ? ` - ${ing.quantity}`
                    : ""}
                  {ing.unit ? ` ${ing.unit}` : ""}
                </span>

                <button
                  type="button"
                  onClick={() => deleteIngredient(ing.id)}
                  className="text-red-500 hover:underline"
                >
                  Remove
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
