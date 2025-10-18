package smart_meal_planner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import smart_meal_planner.model.User;
import smart_meal_planner.service.DatabaseCommunicator;

@RestController
@RequestMapping("/healthinfo")
class HealthInfoController{
    
    private final HealthInfoUploadManager uploadManager = new HealthInfoUploadManager();
    
    @PostMapping("/upload")
    public ResponseEntity<String> uploadHealthInfo(@RequestBody UserHealthInfo info) {
        try {
            uploadManager.HealthInfoUploadManager(info);
            return ResponseEntity.ok("Health info uploaded successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{uid}")
    public ResponseEntity<String> updateHealthInfo(@PathVariable int userId, @RequestBody UserHealthInfo info) {
        uploadManager.updateHealthInfo(userId, info);
        if (!uploadManager.isValid(info)) {
            return ResponseEntity.badRequest().body("Invalid health info data.");
        }
        return ResponseEntity.ok("Health info updated successfully.");
    }

    @GetMapping("/{uid}")
    public ResponseEntity<UserHealthInfo> getHealthInfo(@PathVariable int userId) {
        UserHealthInfo info = HealthInfoUploadManager.getHealthInfo(userId);
        return ResponseEntity.ok(info);
    }
   
   @DeleteMapping("/{uid}") 
   public ResponseEntity<String> deleteHealthInfo(@PathVariable int userId) {
       uploadManager.deleteHealthInfo(userId);
       return ResponseEntity.ok("Health info deleted successfully.");
   }
} 