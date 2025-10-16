abstract class User { 
    int userId;
    String email;
    String username;
    String password;
    String fname;
    String lname;
    Data dob;
    Boolean accountEnabled;
    String sessionID;
    Boolean isActive;

    public String getEmail() {
        return this.email;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String newUsername) {
        this.username = newUsername;
    }

    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    public void createAccount(String em, String uname, String pword) {
        User newAcc;
        newAcc.setUsername(uname);
        newAcc.setEmail(em);
        newAcc.password = pword;
    }

    public void endSession() {
        if !(this.isActive) {
            //endSession
        }
    }
}