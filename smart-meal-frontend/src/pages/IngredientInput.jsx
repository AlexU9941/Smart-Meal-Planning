import React, { useState } from "react";
import axios from "axios";

const IngredientInput = ({ onIngredientsSaved }) => {
  const [ingredients, setIngredients] = useState([{ name: "", quantity: "", unit: "" }]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (index, event) => {
    const { name, value } = event.target;
    const updated = [...ingredients];
    updated[index][name] = value;
    setIngredients(updated);
  };

  const addIngredient = () => {
    setIngredients([...ingredients, { name: "", quantity: "", unit: "" }]);
  };

  const removeIngredient = (index) => {
    setIngredients(ingredients.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const response = await axios.post("/api/ingredients", ingredients );
      setLoading(false);
      if (onIngredientsSaved) onIngredientsSaved(response.data);
      alert("Ingredients saved successfully!");
    } catch (err) {
      setLoading(false);
      setError("Failed to save ingredients. Please try again.");
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="max-w-xl mx-auto p-4 bg-white rounded-2xl shadow-md space-y-4"
    >
      <h2 className="text-xl font-semibold text-center">Add Ingredients</h2>

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
            required
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
            onClick={() => removeIngredient(index)}
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
          {loading ? "Saving..." : "Save"}
        </button>
      </div>

      {error && <p className="text-red-600 text-center">{error}</p>}
    </form>
  );
};

export default IngredientInput;
