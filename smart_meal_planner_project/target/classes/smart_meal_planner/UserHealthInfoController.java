import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateHealthInfo(@PathVariable int userId, @RequestBody UserHealthInfo info) {
        uploadManager.updateHealthInfo(userId, info);
        if (!uploadManager.isValid(info)) {
            return ResponseEntity.badRequest().body("Invalid health info data.");
        }
        return ResponseEntity.ok("Health info updated successfully.");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserHealthInfo> getHealthInfo(@PathVariable int userId) {
        UserHealthInfo info = HealthInfoUploadManager.getHealthInfo(userId);
        return ResponseEntity.ok(info);
    }
   
   @DeleteMapping("/{userId}") 
   public ResponseEntity<String> deleteHealthInfo(@PathVariable int userId) {
       uploadManager.deleteHealthInfo(userId);
       return ResponseEntity.ok("Health info deleted successfully.");
   }
} 