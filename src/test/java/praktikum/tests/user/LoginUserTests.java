package praktikum.tests.user;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.operators.CheckResponse;
import praktikum.operators.UserAPIOperators;

import static org.apache.http.HttpStatus.*;

import java.util.ArrayList;
import java.util.Random;

@Link(url = "https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf")
@Tag("login user")
@Epic("Диплом. Тестирование API.")
@Feature("Логин пользователя в сервисе Stellar Burgers")
@DisplayName("Тест # 2 - Логин пользователя")
public class LoginUserTests {
    private String email;
    private String password;
    private String name;
    private ArrayList<String> tokens = new ArrayList<>();
    private final UserAPIOperators userAPI = new UserAPIOperators();
    private final CheckResponse checkResponse = new CheckResponse();
    private final Random random = new Random();

    // Генерация случайных данных для каждого теста
    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        this.email = generateRandomEmail(); // Генерация случайного email
        this.password = generateRandomPassword(); // Генерация случайного пароля
        this.name = generateRandomName(); // Генерация случайного имени

        // Регистрация пользователя перед каждым тестом
        Response response = userAPI.createNewUser(email, password, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.extractAuthToken(response));
        }
    }

    @After
    @Step ("Удаление данных после теста")
    public void clearAfterTests() {
        if (!tokens.isEmpty()) {
            for (String token : tokens) {
                checkResponse.verifyResponseStatus(userAPI.deleteUser(token), SC_ACCEPTED);
            }
        }
    }

    // Генерация случайного email
    private String generateRandomEmail() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder email = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            email.append(characters.charAt(random.nextInt(characters.length())));
        }
        email.append("@example.com");
        return email.toString();
    }

    // Генерация случайного пароля
    private String generateRandomPassword() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    // Генерация случайного имени
    private String generateRandomName() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < 7; i++) { // Длина имени - 7 символов
            name.append(characters.charAt(random.nextInt(characters.length())));
        }
        return name.toString();
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("Тест API логин пользователя. Ожидаемый результат - пользователь залогинен")
    public void loginUserIsSuccess() {
        Response response = userAPI.loginUser(email, password);

        checkResponse.verifyResponseStatus(response, SC_OK);
        checkResponse.verifySuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Логин пользователя без email")
    @Description("Тест API логин пользователя без email. Ожидаемый результат - пользователь не залогинен")
    public void loginUserWithoutEmailIsFailed() {
        Response response = userAPI.loginUser(null, password);

        checkResponse.verifyResponseStatus(response, SC_UNAUTHORIZED);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Логин пользователя без пароля")
    @Description("Тест API логин пользователя без пароля. Ожидаемый результат - пользователь не залогинен")
    public void loginUserWithoutPasswordIsFailed() {
        Response response = userAPI.loginUser(email, null);

        checkResponse.verifyResponseStatus(response, SC_UNAUTHORIZED);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Логин пользователя c некорректным email")
    @Description("Тест API логин пользователя с некорректным email. Ожидаемый результат - пользователь не залогинен")
    public void loginUserWithIncorrectEmailIsFailed() {
        Response response = userAPI.loginUser(email + "qwe", password);

        checkResponse.verifyResponseStatus(response, SC_UNAUTHORIZED);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "email or password are incorrect");
    }

    @Test
    @DisplayName("Логин пользователя c некорректным паролем")
    @Description("Тест API логин пользователя с некорректным паролем. Ожидаемый результат - пользователь не залогинен")
    public void loginUserWithIncorrectPasswordIsFailed() {
        Response response = userAPI.loginUser(email, password + "qwe");

        checkResponse.verifyResponseStatus(response, SC_UNAUTHORIZED);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "email or password are incorrect");
    }
}

