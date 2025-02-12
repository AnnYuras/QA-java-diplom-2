package praktikum.tests.order;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;

import io.restassured.response.Response;

import org.junit.Before;
import org.junit.Test;
import praktikum.operators.CheckResponse;
import praktikum.operators.OrderAPIOperators;
import praktikum.operators.UserAPIOperators;
import praktikum.objects.Ingredients;
import praktikum.objects.Order;
import praktikum.objects.IngredientsResponse;
import static org.apache.http.HttpStatus.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OrderCreationTest {

    private String email;
    private String password;
    private String name;
    private String token;
    private List<Ingredients> ingredients = new ArrayList<>();
    private final OrderAPIOperators orderAPI = new OrderAPIOperators();
    private final UserAPIOperators userAPI = new UserAPIOperators();
    private final CheckResponse checkResponse = new CheckResponse();
    private final Random random = new Random(); // Использование Random для случайных данных

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        // Генерация случайных данных
        this.email = "user" + random.nextInt(10000) + "@example.com"; // Генерация случайного email
        this.password = Integer.toHexString(random.nextInt()); // Генерация случайного пароля
        this.name = "User" + random.nextInt(10000); // Генерация случайного имени

        Response response = userAPI.createNewUser(email, password, name);
        checkResponse.verifyResponseStatus(response, SC_OK);

        if (response.getStatusCode() == SC_OK) {
            token = userAPI.extractAuthToken(response);
        }

        response = orderAPI.getIngredientList();
        checkResponse.verifyResponseStatus(response, SC_OK);
        ingredients = response.body().as(IngredientsResponse.class).getData();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и случайными ингредиентами")
    @Description("Тест API на создание заказа с авторизацией, используя случайные ингредиенты из списка. " +
            "Ожидаемый результат - заказ успешно создан.")
    public void createOrderWithAuthAndRandomIngredients() {
        int numberOfIngredients = random.nextInt(5) + 2; // Случайное количество ингредиентов от 2 до 6
        List<String> selectedIngredients = new ArrayList<>();
        for (int i = 0; i < numberOfIngredients; i++) {
            Ingredients randomIngredient = ingredients.get(random.nextInt(ingredients.size())); // Случайный ингредиент
            selectedIngredients.add(randomIngredient.get_id());
        }
        Response response = orderAPI.createOrder(selectedIngredients, token);

        checkResponse.verifyResponseStatus(response, SC_OK);
        checkResponse.verifySuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Создание заказа без авторизации и случайными ингредиентами")
    @Description("Тест API на создание заказа с авторизацией, используя случайные ингредиенты из списка. " +
            "Ожидаемый результат - заказ успешно создан.")
    public void createOrderWithoutAuthAndRandomIngredients() {
        int numberOfIngredients = random.nextInt(5) + 2; // Случайное количество ингредиентов от 2 до 6
        List<String> selectedIngredients = new ArrayList<>();
        for (int i = 0; i < numberOfIngredients; i++) {
            Ingredients randomIngredient = ingredients.get(random.nextInt(ingredients.size())); // Случайный ингредиент
            selectedIngredients.add(randomIngredient.get_id());
        }
        Response response = orderAPI.createOrder(new Order(selectedIngredients));

        checkResponse.verifyResponseStatus(response, SC_OK);
        checkResponse.verifySuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и без ингредиентов")
    @Description("Тест API на создание заказа с авторизацией, без добавления ингредиентов. " +
            "Ожидаемый результат - заказ не создан, получено сообщение об ошибке.")
    public void createOrderWithAuthAndWithoutIngredients() {
        List<String> emptyIngredients = new ArrayList<>();
        Response response = orderAPI.createOrder(emptyIngredients, token);

        checkResponse.verifyResponseStatus(response, SC_BAD_REQUEST);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    @Description("Тест API на создание заказа без авторизации, без добавления ингредиентов. " +
            "Ожидаемый результат - заказ не создан, получено сообщение об ошибке.")
    public void createOrderWithoutAuthAndWithoutIngredients() {
        List<String> emptyIngredients = new ArrayList<>();
        Response response = orderAPI.createOrder(emptyIngredients);

        checkResponse.verifyResponseStatus(response, SC_BAD_REQUEST);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с неверным хешем ингредиентов")
    @Description("Тест API на создание заказа с авторизацией, с неверным хешем ингредиентов. " +
            "Ожидаемый результат - заказ не создан, получено сообщение об ошибке.")
    public void createOrderWithoutAuthAndWithWrongHash() {
        List<String> testIngredients = Arrays.asList(
                Integer.toHexString(random.nextInt()),
                Integer.toHexString(random.nextInt()));
        Response response = orderAPI.createOrder(testIngredients, token);

        checkResponse.verifyResponseStatus(response, SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без авторизации и с неверным хешем ингредиентов")
    @Description("Тест API на создание заказа без авторизации, с неверным хешем ингредиентов. " +
            "Ожидаемый результат - заказ не создан, получено сообщение об ошибке.")
    public void createOrderWithAuthAndWithWrongHash() {
        List<String> testIngredients = Arrays.asList(
                Integer.toHexString(random.nextInt()),
                Integer.toHexString(random.nextInt()));
        Response response = orderAPI.createOrder(testIngredients);

        checkResponse.verifyResponseStatus(response, SC_INTERNAL_SERVER_ERROR);
    }
}
