package order;

import java.util.HashMap;
import java.util.List;

public class OrderBase {
    private final HashMap<String, List<String>> ingredients;

    public OrderBase(List<String> ingredients) {
        this.ingredients = new HashMap<>();
        this.ingredients.put("ingredients", ingredients != null ? ingredients : List.of());
    }

    public HashMap<String, List<String>> getIngredients() {
        return ingredients;
    }

}
