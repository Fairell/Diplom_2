package order;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class OrderVerifier {

    // === Проверка создания заказа ===

    @Step("Verify that order could be created with authorization and status code 200")
    public void verifyOrderCreation(Response response) {
        response.then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true))
                .body("order", hasKey("owner"));
    }


    @Step("Verify that order could be created with authorization and status code 200")
    public void verifyOrderCreationWithoutToken(Response response) {
        response.then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
    }


    @Step("Verify that order can't be created without ingredients and status code is 400")
    public void verifyOrderCreationWithoutIngredients(Response response) {
        response.then()
                .assertThat()
                .statusCode(HTTP_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }


    @Step("Verify that order can't be created with wrong hash ingredient and without authorization and status code is 500")
    public void verifyOrderCreationWithoutAuthAndWrongHashIngredients(Response response) {
        response.then()
                .assertThat()
                .statusCode(HTTP_INTERNAL_ERROR);
    }

    // === Проверка получение заказов ===

    @Step("Verify that the orders (list) can't be fetched without authorization")
    public void verifyFetchingOrdersWithoutAuth(Response response) {
        response.then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and().body("message", equalTo("You should be authorised"));
    }


    @Step("Verify OK for the orders fetched with accessToken")
    public void verifyOrdersFetchedWithToken(Response response) {
        response.then()
                .assertThat().statusCode(HTTP_OK)
                .and()
                .body("$", hasKey("orders"));
    }

}
