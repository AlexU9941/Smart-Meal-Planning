package smart_meal_planner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smart_meal_planner.model.User;
import smart_meal_planner.repository.UserRepository;

@Service
public class DatabaseCommunicator{
    
    @Autowired 
    private UserRepository userRepository; 

    
    public User getUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found with username: " + username);
        }

        return user;
    }

    public User getUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        return user;
    }

    public User saveUser(User user) {
        // Basic validation
        if (user.getUsername() == null || user.getUsername().isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");
        if (user.getEmail() == null || user.getEmail().isEmpty())
            throw new IllegalArgumentException("Email cannot be empty");
        if (user.getPassword() == null || user.getPassword().isEmpty())
            throw new IllegalArgumentException("Password cannot be empty");
        
        //duplicate checks
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }

        //Generate hashed passowrd
        String salt = PasswordUtils.generateSalt(); 
        String hashedPassword = PasswordUtils.hashPassword(user.getPassword(), salt);
        
        user.setSalt(salt); //store salt value
        user.setPassword(hashedPassword); //overwrite password with hashed value

        //save to DB
        return userRepository.save(user);
    }


    public User authenticateUser(String username, String password)
    {
       // System.out.println("Authenticating user: " + username); //TEMP LOG
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
        throw new IllegalArgumentException("Username and password cannot be empty.");
        }

        User user = userRepository.findByUsername(username); 
       // System.out.println("Found user: " + user); //TEMP LOG
        if (user == null)
        {
            throw new RuntimeException("User not found with username: " + username);
        }

        String hashedInput = PasswordUtils.hashPassword(password, user.getSalt());

        //Compare hashes
        if (!hashedInput.equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user; 

    }

    public boolean updateUser(User user)
    {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

