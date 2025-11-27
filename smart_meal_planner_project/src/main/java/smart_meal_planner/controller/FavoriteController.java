package smart_meal_planner.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import smart_meal_planner.model.Favorite;
import smart_meal_planner.service.FavoriteService;

@RestController
@CrossOrigin(origins = "*")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // List all favorites for a username (in real app use auth)
    @GetMapping("/api/favorites")
    public List<FavoriteDto> listFavorites(@RequestParam("username") String username) {
        List<Favorite> favorites = favoriteService.listFavorites(username);
        return favorites.stream().map(FavoriteDto::from).collect(Collectors.toList());
    }

    // Add a favorite for a user. RequestBody includes recipeId, title, image, sourceUrl
    @PostMapping("/api/favorites")
    public ResponseEntity<FavoriteDto> addFavorite(@RequestParam("username") String username, @RequestBody FavoriteDto dto) {
        Favorite f = new Favorite(dto.getRecipeId(), dto.getTitle(), dto.getImage(), dto.getSourceUrl(), null);
        Favorite saved = favoriteService.addFavorite(username, f);
        if (saved == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.ok(FavoriteDto.from(saved));
    }

    // Remove favorite by recipeId
    @DeleteMapping("/api/favorites/{recipeId}")
    public ResponseEntity<Void> removeFavorite(@RequestParam("username") String username, @PathVariable int recipeId) {
        boolean removed = favoriteService.removeFavorite(username, recipeId);
        if (!removed) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    // Simple DTO for REST
    public static class FavoriteDto {
        private int recipeId;
        private String title;
        private String image;
        private String sourceUrl;

        public static FavoriteDto from(Favorite f) {
            FavoriteDto d = new FavoriteDto();
            d.recipeId = f.getRecipeId();
            d.title = f.getTitle();
            d.image = f.getImage();
            d.sourceUrl = f.getSourceUrl();
            return d;
        }

        public int getRecipeId() { return recipeId; }
        public String getTitle() { return title; }
        public String getImage() { return image; }
        public String getSourceUrl() { return sourceUrl; }

        public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
        public void setTitle(String title) { this.title = title; }
        public void setImage(String image) { this.image = image; }
        public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    }
}
