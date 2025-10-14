class LogOutController {
    private boolean isLoggedIn;

    public void handleLogOut() {

        isLoggedIn = false; // Simulate successful logout

        System.out.println("User logged out successfully.");
    }

    public void confirmLogOut() {
        // Logic to confirm log out action
        System.out.println("Are you sure you want to log out? (yes/no)");
    }

}