import React, { useState, useEffect } from 'react';
import './ShoppingList.css';

function ShoppingList({ mealPlan }) {
  const [shoppingList, setShoppingList] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!mealPlan || mealPlan.length === 0) {
      setError('No meal plan found. Please generate a meal plan first.');
      return;
    }

    try {
      const list = mealPlan.flatMap(meal => meal.ingredients || []);
      if (list.length === 0) {
        setError('Some recipes are missing ingredient details.');
      } else {
        setShoppingList(list);
      }
    } catch (err) {
      setError('Failed to generate shopping list.');
    }
  }, [mealPlan]);

  return (
    <div className="shopping-list-container">
      <h2>Your Shopping List</h2>
      {error ? (
        <p className="error">{error}</p>
      ) : (
        <ul>
          {shoppingList.map((item, index) => (
            <li key={index}>{item}</li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default ShoppingList;