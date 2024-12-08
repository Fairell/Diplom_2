package client;

import io.restassured.response.Response;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.equalTo;
import io.qameta.allure.Step;


public class ClientVerifier {

    @Step("Verify that client is registered and status code 200")
    public String checkRegistrationSuccess(Response resp) {
        String token = resp.then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and().body("success", equalTo(true))
                .and().extract().body().path("accessToken");
        return token.substring(7);
    }

    @Step("Verify that client can't register twice")
    public void checkBanRegisteringExistingClient(Response resp) {
        resp.then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and().body("message", equalTo("User already exists"));
    }

    @Step("Verify that the client cannot be registered if not all fields are filled in")
    public void checkBanRegisteringWrongClient(Response resp) {
        resp.then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and().body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Verify that client authorized and status code 200")
    public void checkAuthorizationClient(Response response) {
        response.then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and().body("success", equalTo(true));
    }

    @Step("Verify that client can't authorize with wrong password and email, status code 401")
    public void checkUnauthorizedClient(Response resp) {
        resp.then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and().body("message", equalTo("email or password are incorrect"));
    }

    @Step("Verify that client deleted and status code 202")
    public void checkClientDeleted(Response resp) {
        resp.then()
                .assertThat()
                .statusCode(HTTP_ACCEPTED)
                .and().body("message", equalTo("User successfully removed"));
    }

    @Step("Verify that client could change data in profile with token and status code 200")
    public void checkClientDataChanged(Response resp) {
        resp.then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and().body("success", equalTo(true));
    }

    @Step("Verify that an unauthorized user cannot change their profile")
    public void checkBlockingUnauthorizedClient(Response response) {
        response.then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and().body("message", equalTo("You should be authorised"));
    }

}
