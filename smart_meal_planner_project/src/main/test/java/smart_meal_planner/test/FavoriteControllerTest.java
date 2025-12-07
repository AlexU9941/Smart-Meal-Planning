package smart_meal_planner.test;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import smart_meal_planner.controller.FavoriteController;
import smart_meal_planner.controller.FavoriteController.FavoriteDto;
import smart_meal_planner.model.Favorite;
import smart_meal_planner.service.FavoriteService;

import java.util.Arrays;
import java.util.List;


class FavoriteControllerTest {

    private FavoriteController controller;
    private FavoriteService favoriteService;

    @BeforeEach
    void setUp() {
        favoriteService = mock(FavoriteService.class);
        controller = new FavoriteController(favoriteService);
    }

    // ------------------------------------------------------
    //  listFavorites() -- HAPPY PATH
    // ------------------------------------------------------

    @Test
    void testListFavorites_ReturnsFavoriteDtos() {
        String username = "abby";

        Favorite f1 = new Favorite(1, "Pasta", "img1.png", "url1", username);
        Favorite f2 = new Favorite(2, "Salad", "img2.png", "url2", username);

        when(favoriteService.listFavorites(username))
                .thenReturn(Arrays.asList(f1, f2));

        List<FavoriteDto> result = controller.listFavorites(username);

        assertEquals(2, result.size());
        assertEquals("Pasta", result.get(0).getTitle());
        assertEquals("Salad", result.get(1).getTitle());

        verify(favoriteService).listFavorites(username);
    }

    // ------------------------------------------------------
    //  addFavorite() -- HAPPY PATH
    // ------------------------------------------------------

    @Test
    void testAddFavorite_HappyPath_ReturnsSavedFavorite() {
        String username = "abby";

        FavoriteDto dto = new FavoriteDto();
        dto.setRecipeId(100);
        dto.setTitle("Pizza");
        dto.setImage("pizza.png");
        dto.setSourceUrl("link");

        Favorite savedFavorite = new Favorite(100, "Pizza", "pizza.png", "link", username);

        when(favoriteService.addFavorite(eq(username), any(Favorite.class)))
                .thenReturn(savedFavorite);

        ResponseEntity<FavoriteDto> response = controller.addFavorite(username, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Pizza", response.getBody().getTitle());
        verify(favoriteService).addFavorite(eq(username), any(Favorite.class));
    }

    // ------------------------------------------------------
    //  addFavorite() -- FAILURE PATH (SERVICE RETURNS NULL)
    // ------------------------------------------------------

    @Test
    void testAddFavorite_Failure_ReturnsBadRequest() {
        String username = "abby";

        FavoriteDto dto = new FavoriteDto();
        dto.setRecipeId(500);

        when(favoriteService.addFavorite(eq(username), any(Favorite.class)))
                .thenReturn(null);  // service indicates failure

        ResponseEntity<FavoriteDto> response = controller.addFavorite(username, dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ------------------------------------------------------
    //  removeFavorite() -- HAPPY PATH (FOUND AND REMOVED)
    // ------------------------------------------------------

    @Test
    void testRemoveFavorite_HappyPath_ReturnsNoContent() {
        String username = "abby";
        int recipeId = 10;

        when(favoriteService.removeFavorite(username, recipeId))
                .thenReturn(true);

        ResponseEntity<Void> response = controller.removeFavorite(username, recipeId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(favoriteService).removeFavorite(username, recipeId);
    }

    // ------------------------------------------------------
    //  removeFavorite() -- FAILURE PATH (NOT FOUND)
    // ------------------------------------------------------

    @Test
    void testRemoveFavorite_NotFound_Returns404() {
        String username = "abby";
        int recipeId = 999;

        when(favoriteService.removeFavorite(username, recipeId))
                .thenReturn(false);

        ResponseEntity<Void> response = controller.removeFavorite(username, recipeId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
