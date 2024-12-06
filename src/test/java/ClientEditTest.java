import client.ClientProcess;
import client.ClientVerifier;
import client.Credentials;
import client.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class ClientEditTest {
    private final User client;
    private String token;
    private final ClientProcess clientProcess = new ClientProcess();
    private final ClientVerifier clientVerifier = new ClientVerifier();

    public ClientEditTest() {
        client = User.getRandomUser();
    }

    @Before
    public void setUp() throws Exception {
        Response resp = clientProcess.registrationNewClient(client);
        token = clientVerifier.checkRegistrationSuccess(resp);
    }

    @Test
    @DisplayName("Авторизованный пользователь может редактировать профиль")
    public void changeAuthorizedClientData() throws JsonProcessingException {
        // Авторизация клиента
        Response loginResponse = clientProcess.authorizationClient(Credentials.fromClient(client));
        String refreshToken = loginResponse.jsonPath().getString("refreshToken");

        // Подготовка данных для обновления
        HashMap<String, String> updatedData = new HashMap<>();
        updatedData.put("name", client.getName() + RandomStringUtils.randomAlphanumeric(3));
        updatedData.put("email", "ars" + RandomStringUtils.randomAlphanumeric(4, 8) + "@mail.ru");

        // Обновление данных
        Response changeResponse = clientProcess.changeClientData(token, updatedData);

        // Проверка изменений
        clientVerifier.checkClientDataChanged(changeResponse);

        // Логаут
        clientProcess.logoutClient(refreshToken);
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может редактировать профиль")
    public void changeUnauthorizedClientData() {
        // Подготовка данных для обновления
        HashMap<String, String> newProfileData = new HashMap<>();
        newProfileData.put("email", "unknown@mail.ru");
        newProfileData.put("name", "unknownuser");

        // Попытка изменить данные без авторизации
        Response response = clientProcess.changeUnauthorizedClientData(newProfileData);

        // Проверка блокировки
        clientVerifier.checkBlockingUnauthorizedClient(response);
    }
}
