# Smart-Meal-Planning
Class project for IT326. 

**Overview:**
The Smart Meal Planner is a web application designed to assist users in generating personalized meal plans, track nutritional content, and managing shopping lists. With this app, we aim to make healthy eating simple and convenient. 

**Features:**

-Logging In/Out – Users can log in and out of the application easily.

-Profile customization – users can customize their profile's appearance in the app, as well as even resetting preferences as needed.

-Recipe management:
    Find favorite recipes – Users can search for and bookmark, or even create their own, favorite recipes.
    Recipe sharing – Users can share recipes with one another, uploading them into the app, even their favorites as well. 
    Recipe Saving - The application saves favorited recipes by those users.

-Meal Planning
  Create weekly meal plans based on user preferences.
  Auto-Generate Shopping List – Automatically compile ingredients from your meal plan.
  Portion Scaling – Adjust recipes to fit your desired portion size.

-Nutrition Tracking
  Track Nutritional Content – View calories, macronutrients, sugars, and cholesterol for each meal.
  Health Features – Compare meal plans against user health goals.

-Recommendations:
  Find Similar Meals – Get recommendations based on your favorite recipes.

**Tech Stack:**
Frontend: React (JSX), CSS
Backend: Java, REST API
Database: MySQL
Version Control: GitHub.

**Installation steps:**
**1. Clone our repository with these following commands:**

git clone https://github.com/AlexU9941/Smart-Meal-Planning.git

cd Smart-Meal-Planning

**2. Set up your Backend:**

cd smart_meal_planner_project

mvn clean install

mvn spring-boot:run

**3. Set up your Frontend:**

cd smart-meal-frontend

npm install

npm start

System Architecture Diagram: 
<img width="3407" height="2833" alt="326SoftwareArchitectureDiagram drawio" src="https://github.com/user-attachments/assets/eca502c9-9f79-4a66-adcf-1c693141793a" />


The Software Architecture Diagram reveals that our design has three layers: presentation, service, and resposity. The presentation layer includes the BudgetController, CreateAccountController, SignInController, IngredientController, LogoutController, UserNutritionalGoalsController, UserHealthInfoController, MealPlanController, and RecipeController. These controller classes communicate with service classes that connect them to the repository level. For example, the BudgetController class gets data from the BudgetService class that is apart of the BudgetRepository class. Another example of the connection to the repository class is the SignInController that connects with the MailService and PasswordUtils service layer classes. The PasswordUtils service layer class then goes through the DatabaseCommunicator to reach the repository layer. Once the DatabaseCommunicator reaches the UserRepository, the UserRepository connects these classes to the database. All the repository classes communicate with out SQL database. 

