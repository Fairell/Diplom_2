package Order;

import Config.Globals;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class OrderIngredients {
    private final Random random = new Random();

    public List<HashMap<String, Object>> getIngredients() {
        RestAssured.baseURI = Globals.BASE_URI;
        Response response = RestAssured.given()
                .when()
                .contentType(ContentType.JSON)
                .get("/api/ingredients");
        return response.then().extract().body().path("data");
    }

    @Step("Fetching random ingredient by type: {type}")
    public String getRandomIngredientsByType(List<HashMap<String, Object>> ingredients, String type) {
        List<HashMap<String, Object>> ingredientsByType =
                ingredients.stream().filter(ingredient -> ingredient.get("type").equals(type)).collect(Collectors.toList());
        return (String) ingredientsByType.get(random.nextInt(ingredientsByType.size())).get("_id");
    }

    @Step("Fetching random burger ingredients")
    public OrderBase getRandomBurger() {
        List<HashMap<String, Object>> ingredients = getIngredients();
        List<String> randomIngredientsId = new ArrayList<>();
        String bunId = getRandomIngredientsByType(ingredients, "bun");
        String mainId = getRandomIngredientsByType(ingredients, "main");
        String sauceId = getRandomIngredientsByType(ingredients, "sauce");

        randomIngredientsId.add(bunId);
        randomIngredientsId.add(mainId);
        randomIngredientsId.add(sauceId);
        return new OrderBase(randomIngredientsId);
    }

}
