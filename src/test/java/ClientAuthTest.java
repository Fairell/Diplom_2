import Client.ClientProcess;
import Client.ClientVerifier;
import Client.Credentials;
import Client.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientAuthTest {
    private final User client;
    private String token;

    private final ClientProcess clientProcess = new ClientProcess();
    private final ClientVerifier clientVerifier = new ClientVerifier();

    private void inform(String message) {
        System.out.println(message);
    }

    public ClientAuthTest() {
        client = User.getRandomUser();
    }

    @Before
    public void setUp() throws JsonProcessingException {
        Response resp = clientProcess.registrationNewClient(client);
        token = clientVerifier.checkRegistrationSuccess(resp);
    }

    @Test
    @DisplayName("Успешная авторизация клиента")
    public void authorizationClient() throws JsonProcessingException {
        Credentials credentials = Credentials.fromClient(client);
        Response resp = clientProcess.authorizationClient(credentials);
        clientVerifier.checkAuthorizationClient(resp);
    }

    @Test
    @DisplayName("Авторизация с некорректными данными")
    public void authorizationIncorrectClient() throws JsonProcessingException {
        Credentials incorrectCredentials = new Credentials("unknown@mail.ru", "wrongpassword");
        Response resp = clientProcess.authorizationClient(incorrectCredentials);
        clientVerifier.checkUnauthorizedClient(resp);
    }

    @After
    public void tearDown() {
        if (token != null) {
            try {
                Response resp = clientProcess.deleteClient(token);
                clientVerifier.checkClientDeleted(resp);
            } catch (Exception e) {
                inform(String.format("Ошибка при попытке удаления клиента: %s", e.getMessage()));
            } finally {
                token = null;
            }
        }
    }
}