package Order;

import Config.Globals;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static Config.Globals.*;
import static io.qameta.allure.model.Parameter.Mode.HIDDEN;

public class OrderProcess {

    public OrderProcess() {
        // чтобы не устанавливать базовый URI перед каждым запросом
        RestAssured.baseURI = Globals.BASE_URI;
    }


    @Step("Send POST request to /api/orders  to create order with accessToken and ingredient")
    @DisplayName("Create order with token and ingredient")
    public Response createOrder(@Param(mode = HIDDEN)String token, OrderBase orderBase) throws JsonProcessingException {
        Object requestBody = orderBase.getIngredients();
        return RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(new ObjectMapper().writeValueAsString(requestBody))
                .when()
                .post(CREATE_ORDER);
    }


    @Step("Send POST request to /api/orders to create order without accessToken")
    @DisplayName("Create order without authorization with ingredient")
    public Response createOrderWithoutToken(OrderBase orderBase) {
        Object requestBody = orderBase.getIngredients();
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(CREATE_ORDER);
    }


    @Step("Send POST request to /api/orders to create order with accessToken, but without ingredients")
    @DisplayName("Create order with authorization and without ingredients")
    public Response createOrderWithoutIngredients(@Param(mode = HIDDEN)String token) throws JsonProcessingException {
        OrderBase orderBase = new OrderBase(null);
        Object requestBody = orderBase.getIngredients();
        return RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(new ObjectMapper().writeValueAsString(requestBody))
                .when()
                .post(CREATE_ORDER);
    }


    @Step("Send POST request to /api/orders to create order without accessToken and without ingredients")
    @DisplayName("Create order without authorization and without ingredients")
    public Response createOrderWithoutTokenAndIngredients() throws JsonProcessingException {
        OrderBase orderBase = new OrderBase(null);
        Object requestBody = orderBase.getIngredients();
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(new ObjectMapper().writeValueAsString(requestBody))
                .when()
                .post(CREATE_ORDER);
    }


    @Step("Send POST request to /api/orders to create order without accessToken and wrong hash of ingredients")
    @DisplayName("Create order without authorization and wrong hash ingredients")
    public Response createOrderWithWrongHashIngredients(OrderBase orderBase) {
        Object requestBody = orderBase.getIngredients();
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(CREATE_ORDER);
    }

    // ================================================

    @Step("Send GET request to /api/orders to fetch order list without accessToken")
    @DisplayName("Fetching orders without authorization")
    public Response fetchOrdersWithoutToken() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get(GET_ORDERS);
    }


    @Step("Send GET request to /api/orders to fetch all orders with accessToken")
    @DisplayName("Fetching all orders with authorization")
    public Response fetchAllOrdersWithToken(@Param(mode = HIDDEN)String token) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .when()
                .get(GET_ALL_ORDERS);
    }


    @Step("Send GET request to /api/orders to fetch client orders with accessToken")
    @DisplayName("Fetching client orders with authorization")
    public Response fetchClientOrdersWithToken(@Param(mode = HIDDEN)String token) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .when()
                .get(GET_ORDERS);
    }


}
