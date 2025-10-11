

public class SignInController
{
    private String attemptUsername; 
    private String attemptPassword; 
    private boolean signInSuccess; 


    public void enterAttemptUsername(String attemptUsername)
    {
        this.attemptUsername = attemptUsername; 
    }

    public void enterAttemptPassword(String attemptPassword)
    {
        this.attemptPassword = attemptPassword; 
    }


    //TO-DO
    private boolean validateSignInAttempt()
    {
        /*
         * Communicates with DatabaseCommunicator to verify if stored
         * username and password correctly identify an account. 
         * If so, signInSuccess is true and the account can be enabled, 
         * otherwise signInSuccess is false and sign in attempt is rejected. 
         */

        /*
         * In verification process, will need to obtain the salt value stored
         * in whatever User object matches the username. The attempt password 
         * will then need to be hashed to see if it matches stored database value. 
         * On sucess: will need to call enableAccount() in the corresponding User class
         * to indicate that the User has successfully signed on. 
         */

        return signInSuccess; 
    }
}