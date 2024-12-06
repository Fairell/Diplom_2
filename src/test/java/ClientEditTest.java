import client.ClientProcess;
import client.ClientVerifier;
import client.Credentials;
import client.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class ClientEditTest {
    User client = null;
    String token = null;

    ClientProcess clientProcess = new ClientProcess();
    ClientVerifier clientVerifier = new ClientVerifier();

    public ClientEditTest() {
        client = User.getRandomUser();
    }

    @Before
    public void setUp() throws Exception {
        Response resp = registerNewClient(client);
        token = verifyRegistrationSuccess(resp);
    }

    @Test
    @DisplayName("Авторизованный пользователь может редактировать профиль")
    public void changeAuthorizedClientData() throws JsonProcessingException {
        // Авторизация клиента
        Response loginResponse = authorizeClient(client);
        String refreshToken = extractRefreshToken(loginResponse);

        // Обновление данных
        HashMap<String, String> updatedData = prepareUpdatedClientData(loginResponse);
        Response changeResponse = changeClientData(token, updatedData);

        // Проверка изменений
        verifyClientDataChanged(changeResponse);

        // Логаут
        logoutClient(refreshToken);
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может редактировать профиль")
    public void changeUnauthorizedClientData() {
        // Подготовка новых данных профиля
        HashMap<String, String> newProfileData = new HashMap<>();
        newProfileData.put("email", "unknown@mail.ru");
        newProfileData.put("name", "unknownuser");

        // Попытка изменить данные без авторизации
        Response response = changeUnauthorizedClientData(newProfileData);

        // Проверка, что запрос заблокирован
        verifyBlockingUnauthorizedClient(response);
    }

    // ==== Шаги для Allure ====

    @Step("Регистрация нового клиента")
    private Response registerNewClient(User client) throws JsonProcessingException {
        return clientProcess.registrationNewClient(client);
    }

    @Step("Проверка успешной регистрации клиента")
    private String verifyRegistrationSuccess(Response response) {
        return clientVerifier.checkRegistrationSuccess(response);
    }

    @Step("Авторизация клиента")
    private Response authorizeClient(User client) throws JsonProcessingException {
        Credentials credentials = Credentials.fromClient(client);
        return clientProcess.authorizationClient(credentials);
    }

    @Step("Извлечение refreshToken после авторизации")
    private String extractRefreshToken(Response response) {
        return response.jsonPath().getString("refreshToken");
    }

    @Step("Подготовка обновлённых данных клиента")
    private HashMap<String, String> prepareUpdatedClientData(Response loginResponse) {
        HashMap<String, String> userData = new HashMap<>(clientProcess.extractClientNameAndEmail(loginResponse));
        userData.put("name", userData.get("name") + RandomStringUtils.randomAlphanumeric(3));
        userData.put("email", "ars" + RandomStringUtils.randomAlphanumeric(4, 8) + "@mail.ru");
        return userData;
    }

    @Step("Изменение данных клиента")
    private Response changeClientData(String token, HashMap<String, String> updatedData) {
        return clientProcess.changeClientData(token, updatedData);
    }

    @Step("Проверка успешного изменения данных клиента")
    private void verifyClientDataChanged(Response response) {
        clientVerifier.checkClientDataChanged(response);
    }

    @Step("Выход клиента из системы (logout)")
    private void logoutClient(String refreshToken) {
        Response response = clientProcess.logoutClient(refreshToken);
        String logoutMessage = response.jsonPath().getString("message");
    }

    @Step("Попытка изменить данные клиента без авторизации")
    private Response changeUnauthorizedClientData(HashMap<String, String> newProfileData) {
        return clientProcess.changeUnauthorizedClientData(newProfileData);
    }

    @Step("Проверка блокировки запроса для неавторизованного пользователя")
    private void verifyBlockingUnauthorizedClient(Response response) {
        clientVerifier.checkBlockingUnauthorizedClient(response);
    }
}
