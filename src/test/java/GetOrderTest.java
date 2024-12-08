import client.ClientProcess;
import client.ClientVerifier;
import client.User;
import order.OrderProcess;
import order.OrderVerifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GetOrderTest {

    private User testUser;
    private String token;

    private final ClientProcess clientProcess = new ClientProcess();
    private final ClientVerifier clientVerifier = new ClientVerifier();
    private final OrderProcess orderProcess = new OrderProcess();
    private final OrderVerifier orderVerifier = new OrderVerifier();

    @Before
    @Step("Регистрируем нового пользователя и получаем токен авторизации")
    public void setUp() throws JsonProcessingException {
        testUser = User.getRandomUser();
        Response registrationResponse = clientProcess.registrationNewClient(testUser);
        token = clientVerifier.checkRegistrationSuccess(registrationResponse);
    }

    @After
    @Step("Удаляем тестовые данные")
    public void tearDown() {
        clientProcess.deleteClient(token);
    }

    @Test
    @DisplayName("Проверяем, что неавторизованный пользователь не может получить заказы")
    public void testFetchingOrdersWithoutToken() {
        Response response = orderProcess.fetchOrdersWithoutToken();
        orderVerifier.verifyFetchingOrdersWithoutAuth(response);
    }

    @Test
    @DisplayName("Проверяем, что с токеном авторизации можно получить заказы")
    public void testFetchingClientOrders() {
        Response response = orderProcess.fetchClientOrdersWithToken(token);
        orderVerifier.verifyOrdersFetchedWithToken(response);
    }
}
