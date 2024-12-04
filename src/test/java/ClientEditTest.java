import Client.ClientProcess;
import Client.ClientVerifier;
import Client.Credentials;
import Client.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class ClientEditTest {
    User _client = null;
    String _token = null;

    ClientProcess _clientProcess = new ClientProcess();
    ClientVerifier _clientVerifier = new ClientVerifier();

    public ClientEditTest() {
        _client = User.getRandomUser();
    }

    @Before
    public void setUp() throws Exception {
        Response resp = registerNewClient(_client);
        _token = verifyRegistrationSuccess(resp);
    }

    @Test
    @DisplayName("Авторизованный пользователь может редактировать профиль")
    public void changeAuthorizedClientData() throws JsonProcessingException {
        // Авторизация клиента
        Response loginResponse = authorizeClient(_client);
        String refreshToken = extractRefreshToken(loginResponse);

        // Обновление данных
        HashMap<String, String> updatedData = prepareUpdatedClientData(loginResponse);
        Response changeResponse = changeClientData(_token, updatedData);

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
        return _clientProcess.registrationNewClient(client);
    }

    @Step("Проверка успешной регистрации клиента")
    private String verifyRegistrationSuccess(Response response) {
        return _clientVerifier.checkRegistrationSuccess(response);
    }

    @Step("Авторизация клиента")
    private Response authorizeClient(User client) throws JsonProcessingException {
        Credentials credentials = Credentials.fromClient(client);
        return _clientProcess.authorizationClient(credentials);
    }

    @Step("Извлечение refreshToken после авторизации")
    private String extractRefreshToken(Response response) {
        return response.jsonPath().getString("refreshToken");
    }

    @Step("Подготовка обновлённых данных клиента")
    private HashMap<String, String> prepareUpdatedClientData(Response loginResponse) {
        HashMap<String, String> userData = new HashMap<>(_clientProcess.extractClientNameAndEmail(loginResponse));
        userData.put("name", userData.get("name") + RandomStringUtils.randomAlphanumeric(3));
        userData.put("email", "ars" + RandomStringUtils.randomAlphanumeric(4, 8) + "@mail.ru");
        return userData;
    }

    @Step("Изменение данных клиента")
    private Response changeClientData(String token, HashMap<String, String> updatedData) {
        return _clientProcess.changeClientData(token, updatedData);
    }

    @Step("Проверка успешного изменения данных клиента")
    private void verifyClientDataChanged(Response response) {
        _clientVerifier.checkClientDataChanged(response);
    }

    @Step("Выход клиента из системы (logout)")
    private void logoutClient(String refreshToken) {
        Response response = _clientProcess.logoutClient(refreshToken);
        String logoutMessage = response.jsonPath().getString("message");
    }

    @Step("Попытка изменить данные клиента без авторизации")
    private Response changeUnauthorizedClientData(HashMap<String, String> newProfileData) {
        return _clientProcess.changeUnauthorizedClientData(newProfileData);
    }

    @Step("Проверка блокировки запроса для неавторизованного пользователя")
    private void verifyBlockingUnauthorizedClient(Response response) {
        _clientVerifier.checkBlockingUnauthorizedClient(response);
    }
}
