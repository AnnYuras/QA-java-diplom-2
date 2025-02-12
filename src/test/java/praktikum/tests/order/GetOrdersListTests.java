package praktikum.tests.order;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.operators.CheckResponse;
import praktikum.operators.OrderAPIOperators;
import praktikum.operators.UserAPIOperators;
import praktikum.objects.Ingredients;
import praktikum.objects.IngredientsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.apache.http.HttpStatus.*;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Feature("Получение списка заказов в сервисе Stellar Burgers")
@DisplayName("Тест # 5 - Получение списка заказов")
public class GetOrdersListTests {

    private String email;
    private String password;
    private String name;
    private String token;
    private final OrderAPIOperators orderAPI = new OrderAPIOperators();
    private final UserAPIOperators userAPI = new UserAPIOperators();
    private final CheckResponse checkResponse = new CheckResponse();
    private final Random random = new Random();


    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        this.email = generateEmail();
        this.password = generatePassword();
        this.name = generateName();

        Allure.addAttachment("Сгенерированный email: ", email);
        Allure.addAttachment("Сгенерированный пароль: ", password);
        Allure.addAttachment("Сгенерированное имя: ", name);


        Response response = userAPI.createNewUser(email, password, name);
        checkResponse.verifyResponseStatus(response, SC_OK);

        if (response.getStatusCode() == SC_OK) {
            token = userAPI.extractAuthToken(response);
        }

        response = orderAPI.getIngredientList();
        checkResponse.verifyResponseStatus(response, SC_OK);
        List<Ingredients> ingredients = response.body().as(IngredientsResponse.class).getData();

        int numberOfIngredients = random.nextInt(5) + 2; // случайное число от 2 до 6
        List<String> selectedIngredients = new ArrayList<>();
        for (int i = 0; i < numberOfIngredients; i++) {
            Ingredients randomIngredient = ingredients.get(random.nextInt(ingredients.size()));
            selectedIngredients.add(randomIngredient.get_id());
        }

        Allure.addAttachment("Выбранные ингредиенты: ", selectedIngredients.toString());

        response = orderAPI.createOrder(selectedIngredients, token);
        checkResponse.verifyResponseStatus(response, SC_OK);
    }

    @After
    @Step("Удаление данных после теста")
    public void clearAfterTests() {
        if (token != null) {
            checkResponse.verifyResponseStatus(userAPI.deleteUser(token), SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Получение списка заказов неавторизованного пользователя")
    @Description("Тест API на получение списка заказов неавторизованного пользователя. " +
            "Ожидаемый результат - список заказов не получен, получено сообщение об ошибке.")
    public void getNotAuthUsersOrdersIsSuccess() {
        Response response = orderAPI.getOrderList("");
        checkResponse.verifyResponseStatus(response, SC_UNAUTHORIZED);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response,"You should be authorised");
    }

    @Test
    @DisplayName("Получение всех заказов")
    @Description("Тест API на получение списка заказов. " +
            "Ожидаемый результат - список заказов получен.")
    public void getAllOrdersIsSuccess() {
        Response response = orderAPI.getAllOrderList();

        checkResponse.verifyResponseStatus(response, SC_OK);
        checkResponse.verifySuccessStatus(response, "true");
    }

    @Test
    @DisplayName("API тест на получение списка заказов для авторизованного пользователя")
    @Description("Проверка API для получения заказов авторизованным пользователем. Ожидаемый результат — список заказов успешно получен.")
    public void getAuthUsersOrdersIsSuccess() {
        Response response = orderAPI.getOrderList(token);
        checkResponse.verifyResponseStatus(response, SC_OK);
        checkResponse.verifySuccessStatus(response, "true");
    }

    // Методы для генерации случайных данных
    private String generateEmail() {
        return UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateName() {
        return "User" + UUID.randomUUID().toString().substring(0, 5);
    }
}
