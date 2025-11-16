import React, { useState } from 'react';

function PortionScaler({ recipe }) {
  const [portions, setPortions] = useState(recipe.defaultPortions);
  const [scaledIngredients, setScaledIngredients] = useState(recipe.ingredients);

  const handleScale = () => {
    if (isNaN(portions) || portions <= 0) {
      alert('Please enter a valid number for portions.');
      return;
    }
    const factor = portions / recipe.defaultPortions;
    const newIngredients = recipe.ingredients.map(ing => ({
      ...ing,
      quantity: (ing.quantity * factor).toFixed(2)
    }));
    setScaledIngredients(newIngredients);
  };

  return (
    <div>
      <h3>Adjust Portions</h3>
      <input
        type="number"
        value={portions}
        onChange={(e) => setPortions(e.target.value)}
      />
      <button onClick={handleScale}>Scale</button>
      <ul>
        {scaledIngredients.map((ing, index) => (
          <li key={index}>{ing.name}: {ing.quantity} {ing.unit}</li>
        ))}
      </ul>
    </div>
  );
}

export default PortionScaler;