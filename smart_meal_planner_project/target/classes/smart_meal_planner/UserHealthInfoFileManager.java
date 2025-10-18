class HealthInfoUploadManager {
    // UserHealthInfo -> void
    // Upload the health info to the database. Check for valid data before uploading.
    public void uploadHealthInfo(UserHealthInfo info) {
        if (!isValid(info)) {
            throw new IllegalArgumentException("Invalid health info data.");
        }
        saveToDatabase(info);
    }

    // UserHealthInfo-> Boolean
    // Validate the data format for each field of UserHealthInfo
    public boolean isValid(UserHealthInfo info) {
        if (info.getHeightFt() <= 0 || info.getHeightIn() < 0 || info.getHeightIn() > 12) return false;
        if (info.getWeight() <= 0) return false;
        if (!List.of("Male", "Female", "Other").contains(info.getSex())) return false;
        if (!List.of("Not Active", "Lightly Active", "Moderately Active", "Very Active", "Extra Active")
                .contains(info.getWeeklyActivityLevel())) return false;
        return true;
    }

    public void updateHealthInfo(int userId, UserHealthInfo info) {
        if (!isValid(info)) {
            throw new IllegalArgumentException("Invalid health info data.");
        }  
        // Logic to update health info in the database
    }

    public UserHealthInfo getHealthInfo(int userId) {
        // Logic to retrieve health info from the database
    }

    public void deleteHealthInfo(int userId) {
        // Logic to delete health info from the database
    }

    // UserHealthInfo -> void
    // Save the valid UserHealthInfo to the database
    public void saveToDatabase(UserHealthInfo info) {
        // Database connection logic here
    }
}

s