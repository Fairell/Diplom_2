package client;

import config.Globals;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import static config.Globals.AUTHORIZATION_CLIENT;
import static io.qameta.allure.model.Parameter.Mode.HIDDEN;


public class ClientProcess {
    private static final String ACTIONS_CLIENT = "/api/auth/user";
    private final RequestSpecification requestSpec;

    public ClientProcess() {
        RestAssured.baseURI = Globals.BASE_URI; // Задаём базовый URI для всех запросов
        requestSpec = RestAssured.given()
                .baseUri(Globals.BASE_URI) // Альтернативный способ для baseURI
                .contentType("application/json"); // Устанавливаем Content-Type
    }

    @Step("Send POST request to api/auth/register to create new client")
    @DisplayName("Register a client")
    public Response registrationNewClient(User user) throws JsonProcessingException {
        RestAssured.baseURI = Globals.BASE_URI;
        return RestAssured.given()
                .when()
                .contentType(ContentType.JSON)
                .body(new ObjectMapper().writeValueAsString(user))
                .post(Globals.CREATE_REGISTRATION_CLIENT);
    }

    @Step("Send POST request to api/auth/login to login client")
    @DisplayName("Authorize a client")
    public Response authorizationClient(Credentials credentials) throws JsonProcessingException {
        RestAssured.baseURI = Globals.BASE_URI;
        return RestAssured.given()
                .when()
                .contentType(ContentType.JSON)
                .body(new ObjectMapper().writeValueAsString(credentials))
                .post(AUTHORIZATION_CLIENT);
    }

    @Step("Send POST request to api/auth/user to get name and email from client profile")
    @DisplayName("Get name and email from client profile")
    public HashMap<String, String> extractClientNameAndEmail(Response response) {
        String email = response.then()
                .extract().body().path("user.email");
        String name = response.then()
                .extract().body().path("user.name");
        HashMap<String, String> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        return result;
    }

    @Step("Send PATCH request to change name and email in client profile")
    @DisplayName("Change name and email in the profile of the registered client")
    public Response changeClientData(@Param(mode = HIDDEN)String token, HashMap<String, String> newProfileData) {
        RestAssured.baseURI = Globals.BASE_URI;
        return RestAssured.given()
                .when()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .and()
                .body(newProfileData)
                .patch(ACTIONS_CLIENT);
    }


    @Step("Send PATCH request to change client profile without accessToken")
    @DisplayName("Change client profile of an unauthorized client")
    public Response changeUnauthorizedClientData(HashMap<String, String> newProfileData) {
        return RestAssured.given()
                .body(newProfileData)
                .when()
                .patch(ACTIONS_CLIENT);
    }

    // ================================

    @Step("Send POST request for logout")
    @DisplayName("Logout")
    public Response logoutClient(String refreshToken) {
        var strBody = String.format("{ \"token\": \"%s\" }", refreshToken);
        RestAssured.baseURI = Globals.BASE_URI;
        return RestAssured.given()
                .when()
                .contentType(ContentType.JSON)
                .body(strBody)
                .post("/api/auth/logout");
    }


    @Step("Send DELETE request to api/auth/user to delete client")
    @DisplayName("Delete a client")
    public Response deleteClient(String token) {
        return requestSpec
                .auth().oauth2(token) // Добавляем авторизацию для конкретного запроса
                .when()
                .delete(ACTIONS_CLIENT);
    }
}
