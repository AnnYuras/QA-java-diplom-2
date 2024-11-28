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
@Tag("change user data")
@Epic("Диплом. Тестирование API.")
@Feature("Редактирование данных пользователя в сервисе Stellar Burgers")
@DisplayName("Тест # 3 - Редактирование данных пользователя")
public class ChangeUserDataTests {
    private String email;
    private String password;
    private String name;
    private String token;
    private final ArrayList<String> tokens = new ArrayList<>();
    private final UserAPIOperators userAPI = new UserAPIOperators();
    private final CheckResponse checkResponse = new CheckResponse();
    private final Random random = new Random();

    @Before
    @Step("Подготовка тестовых данных")
    public void prepareTestData() {
        this.email = generateRandomEmail();
        this.password = generateRandomPassword();
        this.name = generateRandomName();

        Response response = userAPI.createNewUser(email, password, name);
        checkResponse.verifyResponseStatus(response, SC_OK);

        if (response.getStatusCode() == SC_OK) {
            token = userAPI.extractAuthToken(response);
        }
    }

    @After
    @Step ("Удаление данных после теста")
    public void clearAfterTests() {
        if(tokens.isEmpty())
            return;
        for (String token: tokens) {
            checkResponse.verifyResponseStatus(userAPI.deleteUser(token), SC_ACCEPTED);
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

    //Баг. Ожидаемый результат не совпадает с фактическим
    @Test
    @DisplayName("Изменение email пользователя: с авторизацией")
    @Description("Тест API редактирование email авторизованного пользователя. " +
            "Ожидаемый результат - email изменен")
    public void changeUserEmailWithAuthIsSuccess() {
        String newEmail = "m_" + generateRandomEmail();

        Response response = userAPI.updateUser(newEmail, password, name, token);

        checkResponse.verifyResponseStatus(response, SC_OK);
        checkResponse.verifySuccessStatus(response, "true");
        userAPI.checkUserData(response, newEmail, password, name);
    }
//Баг. Ожидаемый результат не совпадает с фактическим
    @Test
    @DisplayName("Изменение пароля пользователя: с авторизацией")
    @Description("Тест API редактирование пароля авторизованного пользователя. " +
            "Ожидаемый результат - пароль изменен")
    public void changeUserPasswordWithAuthIsSuccess() {
        String newPassword = "p_" + generateRandomPassword();

        Response response = userAPI.updateUser(email, newPassword, name, token);

        checkResponse.verifyResponseStatus(response, SC_OK);
        checkResponse.verifySuccessStatus(response, "true");
        userAPI.checkUserData(response, email, newPassword, name);
    }


    //Баг. Ожидаемый результат не совпадает с фактическим
    @Test
    @DisplayName("Изменение имени пользователя: с авторизацией")
    @Description("Тест API редактирование имени авторизованного пользователя. " +
            "Ожидаемый результат - имя изменено")
    public void changeUserNameWithAuthIsSuccess() {
        String newName = "n_" + generateRandomName();

        Response response = userAPI.updateUser(email, password, newName, token);

        checkResponse.verifyResponseStatus(response, SC_OK);
        checkResponse.verifySuccessStatus(response, "true");
        userAPI.checkUserData(response, email, password, newName);
    }

    @Test
    @DisplayName("Изменение email пользователя: без авторизации")
    @Description("Тест API редактирование email неавторизованного пользователя. " +
            "Ожидаемый результат - email не изменен, получено сообщение об ошибке")
    public void changeUserEmailWithoutAuthIsSuccess() {
        String newEmail = "m_" + generateRandomEmail();

        Response response = userAPI.updateUser(newEmail, password, name);

        checkResponse.verifyResponseStatus(response, SC_UNAUTHORIZED);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "You should be authorised");
    }

    @Test
    @DisplayName("Изменение пароля пользователя: без авторизации")
    @Description("Тест API редактирование пароля неавторизованного пользователя. " +
            "Ожидаемый результат - пароль не изменен, получено сообщение об ошибке")
    public void changeUserPasswordWithoutAuthIsSuccess() {
        String newPassword = "p_" + generateRandomPassword();

        Response response = userAPI.updateUser(email, newPassword, name);

        checkResponse.verifyResponseStatus(response, SC_UNAUTHORIZED);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "You should be authorised");
    }

    @Test
    @DisplayName("Изменение имени пользователя: без авторизации")
    @Description("Тест API редактирование имени неавторизованного пользователя. " +
            "Ожидаемый результат - имя не изменено, получено сообщение об ошибке")
    public void changeUserNameWithoutAuthIsSuccess() {
        String newName = "n_" + generateRandomName();

        Response response = userAPI.updateUser(email, password, newName);

        checkResponse.verifyResponseStatus(response, SC_UNAUTHORIZED);
        checkResponse.verifySuccessStatus(response, "false");
        checkResponse.verifyMessageText(response, "You should be authorised");
    }
}
