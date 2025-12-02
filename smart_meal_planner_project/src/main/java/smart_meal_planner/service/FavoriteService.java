package smart_meal_planner.service;

import java.util.List;

import org.springframework.stereotype.Service;

import smart_meal_planner.model.Favorite;
import smart_meal_planner.model.User;
import smart_meal_planner.repository.FavoriteRepository;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final DatabaseCommunicator databaseCommunicator;

    public FavoriteService(FavoriteRepository favoriteRepository, DatabaseCommunicator databaseCommunicator) {
        this.favoriteRepository = favoriteRepository;
        this.databaseCommunicator = databaseCommunicator;
    }

    public List<Favorite> listFavorites(String username) {
        try {
            User user = databaseCommunicator.getUserByUsername(username);
            return favoriteRepository.findByUser(user);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    public Favorite addFavorite(String username, Favorite favorite) {
        try {
            User user = databaseCommunicator.getUserByUsername(username);
            if (user == null) return null;

            // Prevent duplicates
            Favorite existing = favoriteRepository.findByUserAndRecipeId(user, favorite.getRecipeId());
            if (existing != null) return existing;

            favorite.setUser(user);
            return favoriteRepository.save(favorite);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean removeFavorite(String username, int recipeId) {
        try {
            User user = databaseCommunicator.getUserByUsername(username);
            if (user == null) return false;

            Favorite existing = favoriteRepository.findByUserAndRecipeId(user, recipeId);
            if (existing == null) return false;
            favoriteRepository.delete(existing);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
