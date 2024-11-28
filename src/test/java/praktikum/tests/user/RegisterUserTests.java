package praktikum.tests.user;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.operators.CheckResponse;
import praktikum.operators.UserAPIOperators;
import static org.apache.http.HttpStatus.*;

import java.util.ArrayList;

import java.util.Random;

public class RegisterUserTests {
    private String email;
    private String password;
    private String name;
    private ArrayList<String> tokens = new ArrayList<>();
    private final UserAPIOperators userAPI = new UserAPIOperators();
    private final CheckResponse checkResponse = new CheckResponse();
    private final Random random = new Random();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        // Генерация случайных данных с использованием Random
        this.email = "user" + random.nextInt(10000) + "@example.com"; // Генерация случайного email
        this.password = generateRandomPassword(8); // Генерация случайного пароля длиной 8 символов
        this.name = "User" + random.nextInt(10000); // Генерация случайного имени
    }

    @After
    @Step ("Удаление данных после теста")
    public void clearAfterTests() {
        if(tokens.isEmpty()) return;
        for (String token : tokens) {
            checkResponse.verifyResponseStatus(userAPI.deleteUser(token), SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Регистрация нового пользователя")
    @Description("Тест API создание нового пользователя. Ожидаемый результат - пользователь создан")
    public void registerUserIsSuccess() {
        Response response = userAPI.createNewUser(email, password, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.extractAuthToken(response));
        }
        checkResponse.verifyResponseStatus(response, SC_OK);
        checkResponse.verifySuccessStatus(response, "true");
    }

    @Test
    @DisplayName("Регистрация двух пользователей с одинаковыми данными")
    @Description("Тест API создание двух пользователей с одинаковыми данными. Ожидаемый результат - одинаковых пользователей создать нельзя.")
    public void registerSameUserIsFailed() {
        Response responseX = userAPI.createNewUser(email, password, name);
        Response responseY = userAPI.createNewUser(email, password, name);
        if (responseX.getStatusCode() == SC_OK) {
            tokens.add(userAPI.extractAuthToken(responseX));
        }
        if (responseY.getStatusCode() == SC_OK) {
            tokens.add(userAPI.extractAuthToken(responseY));
        }

        checkResponse.verifyResponseStatus(responseY, SC_FORBIDDEN);
        checkResponse.verifySuccessStatus(responseY, "false");
        checkResponse.verifyMessageText(responseY,"User already exists");
    }

    @Test
    @DisplayName("Регистрация пользователя без email")
    @Description("Тест API регистрация пользователя без email. Ожидаемый результат - пользователя без email создать нельзя")
    public void registerUserWithoutEmailIsFailed() {
        Response response = userAPI.createNewUser(null, password, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.extractAuthToken(response));
        }
        checkResponse.verifyResponseStatus(response, SC_FORBIDDEN);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без пароля")
    @Description("Тест API регистрация пользователя без пароля. Ожидаемый результат - пользователя без пароля создать нельзя")
    public void registerUserWithoutPasswordIsFailed() {
        Response response = userAPI.createNewUser(email, null, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.extractAuthToken(response));
        }
        checkResponse.verifyResponseStatus(response, SC_FORBIDDEN);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без имени")
    @Description("Тест API регистрация пользователя без имени. Ожидаемый результат - пользователя без имени создать нельзя")
    public void registerUserWithoutNameIsFailed() {
        Response response = userAPI.createNewUser(email, null, name);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.extractAuthToken(response));
        }
        checkResponse.verifyResponseStatus(response, SC_FORBIDDEN);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Регистрация пользователя без данных")
    @Description("Тест API регистрация пользователя без данных. Ожидаемый результат - пользователя без данных создать нельзя")
    public void registerUserWithoutDataIsFailed() {
        Response response = userAPI.createNewUser(null, null, null);
        if (response.getStatusCode() == SC_OK) {
            tokens.add(userAPI.extractAuthToken(response));
        }
        checkResponse.verifyResponseStatus(response, SC_FORBIDDEN);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "Email, password and name are required fields");
    }

    // Метод для генерации случайного пароля
    private String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=<>?";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }
}
