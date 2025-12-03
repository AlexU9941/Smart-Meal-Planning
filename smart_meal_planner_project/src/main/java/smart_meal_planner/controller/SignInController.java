package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import smart_meal_planner.model.User;
import smart_meal_planner.service.DatabaseCommunicator;
import smart_meal_planner.service.PasswordUtils;
import smart_meal_planner.service.MailService;



@RestController
@RequestMapping("/api")
public class SignInController
{
    private boolean signInSuccess; //may not need

    @Autowired
    private DatabaseCommunicator dbCommunicator;

    @Autowired
    private MailService mailService;

    @PostMapping("/sign-in")
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    public ResponseEntity<?> signIn(HttpServletRequest request, @RequestBody Map<String, String> credentials) {
        try{
            String username = credentials.get("username");
            String password = credentials.get("password");

            User user = dbCommunicator.authenticateUser(username,password);
            signInSuccess = true;  
            request.getSession(true).setAttribute("user", user); //store login status

            // Return a subset of fields (avoid sending password!)
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail()); 

            //return ResponseEntity.ok("Login successful for user: " + user.getUsername());
            return ResponseEntity.ok(response);
           
        }
        catch (RuntimeException e) {
               // e.printStackTrace(); // <-- log full exception
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
                //e.printStackTrace(); // <-- log full exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    //may need to rework later
    @PostMapping("/log-out")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok("Logged out");
    }


    @PostMapping("/recover-password")
    public ResponseEntity<?> recoverPassword(@RequestBody Map<String, String> body)
    {

        System.out.println("Email password: " + System.getenv("EMAIL_PASSWORD"));

        String email = body.get("email");
        User user = dbCommunicator.getUserByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
            }

            try{
               
               SecureRandom random = new SecureRandom();
               String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
               StringBuilder tempPassword = new StringBuilder();

               //random password 
               for (int i = 0; i < 10; i++) {
                tempPassword.append(chars.charAt(random.nextInt(chars.length())));               
                }

               //new salt 
               String newSalt = PasswordUtils.generateSalt();
               String hashedTempPassword = PasswordUtils.hashPassword(tempPassword.toString(), newSalt);
               user.setPassword(hashedTempPassword);
               user.setSalt(newSalt);
               dbCommunicator.updateUser(user);

               //recovery email  TEMP COMMENTED 
               mailService.sendEmail(
                   user.getEmail(), 
                   "Password Recovery - Smart Meal Planner", 
                   "I tYour temporary password is: " + tempPassword + "\nPlease log in and change your password immediately."
               ); 

                return ResponseEntity.ok("Temp password sent.");

            }

            catch (Exception e)
            {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error sending password recovery email.");
            }
    }
    

    

}