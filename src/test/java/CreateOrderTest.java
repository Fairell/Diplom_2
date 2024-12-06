import client.ClientProcess;
import client.ClientVerifier;
import order.OrderProcess;
import order.OrderVerifier;
import order.OrderIngredients;
import client.User;
import order.OrderBase;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

public class CreateOrderTest {
    private final User _client;
    private String _token;

    private final OrderProcess _orderProcess;
    private final OrderVerifier _orderVerifier;

    public CreateOrderTest() {
        _orderProcess = new OrderProcess();
        _orderVerifier = new OrderVerifier();
        _client = User.getRandomUser();
    }

    @Before
    public void setUp() throws Exception {
        ClientProcess clientProcess = new ClientProcess();
        ClientVerifier clientVerifier = new ClientVerifier();

        var resp = clientProcess.registrationNewClient(_client);
        _token = clientVerifier.checkRegistrationSuccess(resp);
    }

    @After
    public void tearDown() {
        if (_token != null) {
            ClientProcess clientProcess = new ClientProcess();
            clientProcess.deleteClient(_token); // метод для удаления клиента
        }
    }

    @Test
    @DisplayName("Проверка создания заказа с токеном")
    public void testOrderCreationWithToken() throws JsonProcessingException {
        OrderIngredients ingredients = new OrderIngredients();
        OrderBase orderBase = ingredients.getRandomBurger();
        Response resp = _orderProcess.createOrder(_token, orderBase);

        _orderVerifier.verifyOrderCreation(resp);
    }

    @Test
    @DisplayName("Проверка создания заказа без токена")
    public void testOrderCreationWithoutToken() {
        OrderIngredients ingredients = new OrderIngredients();
        OrderBase orderBase = ingredients.getRandomBurger();
        Response resp = _orderProcess.createOrderWithoutToken(orderBase);

        _orderVerifier.verifyOrderCreationWithoutToken(resp);
    }

    @Test
    @DisplayName("Проверка создания заказа без ингридиентов")
    public void testOrderCreationWithoutIngredients() throws JsonProcessingException {
        Response resp = _orderProcess.createOrderWithoutIngredients(_token);

        _orderVerifier.verifyOrderCreationWithoutIngredients(resp);
    }

    @Test
    @DisplayName("Проверка создания заказа с некорректным хэшем ингридиентов")
    public void testOrderCreationWithWrongIngredients() {
        List<String> ingredients = List.of("invalid_hash1", "invalid_hash2");
        OrderBase orderBase = new OrderBase(ingredients);

        Response resp = _orderProcess.createOrderWithWrongHashIngredients(orderBase);
        _orderVerifier.verifyOrderCreationWithoutAuthAndWrongHashIngredients(resp);
    }
}
