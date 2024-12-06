import client.ClientProcess;
import client.ClientVerifier;
import client.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ClientRegTest {

    private final ClientProcess clientProcess = new ClientProcess();
    private final ClientVerifier clientVerifier = new ClientVerifier();
    private final List<String> tokensToClean = new ArrayList<>();

    @Before
    public void setUp() {
        // Логирование или настройка окружения, если требуется
    }

    @Test
    public void registrationNewClient() throws JsonProcessingException {
        var client = User.getRandomUser();
        var resp = clientProcess.registrationNewClient(client);
        String token = clientVerifier.checkRegistrationSuccess(resp);
        tokensToClean.add(token);
    }

    @Test
    public void registrationExistingClient() throws JsonProcessingException {
        var client = User.getRandomUser();
        var resp = clientProcess.registrationNewClient(client);
        String token = clientVerifier.checkRegistrationSuccess(resp);
        tokensToClean.add(token);

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
        // Удаляем созданных пользователей
        for (String token : tokensToClean) {
            try {
                clientProcess.deleteClient(token);
            } catch (Exception e) {
                System.err.printf("Failed to delete client with token %s: %s%n", token, e.getMessage());
            }
        }
        tokensToClean.clear();
    }
}
