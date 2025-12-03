import React, { Component } from "react";
import axios from "axios";

class Budget extends Component {
  constructor(props) {
    super(props);
    this.state = {
      amount: "",
      message: ""
    };
  }

  handleChange = (e) => {
    this.setState({ [e.target.name]: e.target.value });
  };

  handleSubmit = async (e) => {
    e.preventDefault();
    const { amount } = this.state;

    if (!amount) {
      this.setState({ message: "Please enter a budget amount." });
      return;
    }

    try {
      const res = await axios.post("http://localhost:8080/api/budget", {
        amount,
        period: "weekly" // backend expects period
      });
      this.setState({ message: "Budget set successfully!", amount: "" });
      localStorage.setItem("budget", amount);
      console.log(res.data);
    } catch (err) {
      console.error(err);
      this.setState({ message: "Failed to set budget." });
    }
  };

  render() {
    return (
      <div>
        <h2>Set Your Weekly Budget</h2>
        <form onSubmit={this.handleSubmit}>
          <input
            type="number"
            name="amount"
            placeholder="Enter budget amount"
            value={this.state.amount}
            onChange={this.handleChange}
          />
          <button type="submit">Set Budget</button>
        </form>
        <p>{this.state.message}</p>
      </div>
    );
  }
}

export default Budget;
