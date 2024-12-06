import client.ClientProcess;
import client.ClientVerifier;
import client.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientRegTest {

    private final ClientProcess clientProcess = new ClientProcess();
    private final ClientVerifier clientVerifier = new ClientVerifier();
    private String tokenToClean;

    @Before
    public void setUp() {
        tokenToClean = null; // Обнуляем перед каждым тестом
    }

    @Test
    public void registrationNewClient() throws JsonProcessingException {
        var client = User.getRandomUser();
        var resp = clientProcess.registrationNewClient(client);
        tokenToClean = clientVerifier.checkRegistrationSuccess(resp);
    }

    @Test
    public void registrationExistingClient() throws JsonProcessingException {
        var client = User.getRandomUser();
        var resp = clientProcess.registrationNewClient(client);
        tokenToClean = clientVerifier.checkRegistrationSuccess(resp);

        resp = clientProcess.registrationNewClient(client);
        clientVerifier.checkBanRegisteringExistingClient(resp);
    }

    @Test
    public void registrationWrongClient() throws JsonProcessingException {
        var client = User.getWithoutPassword();
        Response resp = clientProcess.registrationNewClient(client);
        clientVerifier.checkBanRegisteringWrongClient(resp);
    }

    @After
    public void tearDown() {
        // Удаляем созданного пользователя, если токен существует
        if (tokenToClean != null) {
                clientProcess.deleteClient(tokenToClean);
                tokenToClean = null; // Очищаем токен после удаления
        }
    }
}
