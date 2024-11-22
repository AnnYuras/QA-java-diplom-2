package apiTests.methods;

import org.junit.Before;
import org.junit.After;
import io.qameta.allure.Step;
import io.restassured.RestAssured;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

// Общие методы для создания тестовых классов
public class MethodsUserCreation {

    protected String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            removeUserByToken(accessToken);
        }
    }


    @Step("Удаление пользователя по токену")
    public void removeUserByToken(String token) {
        // Убираем префикс "Bearer " из токена
        String tokenWithoutBearer = token.replace("Bearer ", "");

        // Отправляем запрос для удаления пользователя
        Response response = given()
                .header("Authorization", "Bearer " + tokenWithoutBearer)
                .when()
                .delete("/api/auth/user");

        // Выводим статус и тело ответа на консоль
        System.out.println("Ответ на удаление пользователя: Код - " + response.getStatusCode());
        System.out.println("Ответ на удаление:\n" + response.getBody().prettyPrint());

        // Проверка успешного удаления
        assertThat(response.getStatusCode(), is(202));
        assertThat(response.jsonPath().getBoolean("success"), is(true));

        // Ожидаемое сообщение об удалении
        String expectedSuccessMessage = "User successfully removed";

        // Проверяем, что сообщение совпадает с ожидаемым
        assertThat(response.jsonPath().getString("message"), is(expectedSuccessMessage));

        // Информация об успешном удалении
        System.out.println("Пользователь был удален успешно.");
    }


    @Step("Регистрация нового пользователя")
    public Response registerNewUser(String email, String password, String name) {
        // Формируем тело запроса для создания пользователя
        String requestBody = String.format(
                "{\n" +
                        "  \"email\": \"%s\",\n" +
                        "  \"password\": \"%s\",\n" +
                        "  \"name\": \"%s\"\n" +
                        "}", email, password, name);

        // Отправляем запрос для регистрации пользователя
        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/auth/register");

        // Выводим информацию о создании пользователя
        System.out.println("Пользователь с email " + email + " успешно зарегистрирован.");

        // Возвращаем ответ для дальнейшей проверки
        return response;
    }


    @Step("Проверка успешной регистрации пользователя")
    public String validateUserCreationResponse(Response response, String email, String name) {
        // Выводим код и тело ответа для отладки
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body:\n" + response.getBody().prettyPrint());

        // Проверка, что код ответа равен 200
        assertThat(response.getStatusCode(), is(200));

        // Проверка, что поле "success" имеет значение true
        assertThat(response.jsonPath().getBoolean("success"), is(true));

        // Проверка совпадения email и имени пользователя
        assertThat(response.jsonPath().getString("user.email"), is(email));
        assertThat(response.jsonPath().getString("user.name"), is(name));

        // Проверка наличия и корректности токенов
        String accessToken = "Bearer " + response.jsonPath().getString("accessToken");
        assertThat(accessToken, not(isEmptyOrNullString()));
        assertThat(response.jsonPath().getString("refreshToken"), not(isEmptyOrNullString()));

        // Возвращаем accessToken для использования в дальнейшем
        return accessToken;
    }



    @Step("Проверка ошибки при создании существующего пользователя")
    public void validateUserAlreadyExistsError(Response response) {
        // Проверяем, что статус ответа равен 403
        assertThat("Статус код должен быть 403", response.getStatusCode(), is(403));
        // Проверяем, что сообщение об ошибке корректное
        assertThat("Сообщение об ошибке должно быть 'User already exists'",
                response.jsonPath().getString("message"), is("User already exists"));
    }



    @Step("Проверка успешной регистрации пользователя")
    public void validateUserCreationSuccess(Response response) {
        // Выводим код и тело ответа для анализа
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body:\n" + response.getBody().prettyPrint());

        // Проверяем, что код ответа 200
        assertThat(response.getStatusCode(), is(200));

        // Проверка, что поле "success" имеет значение true
        assertThat(response.jsonPath().getBoolean("success"), is(true));
    }


    @Step("Проверка ошибки при попытке регистрации с уже существующим пользователем")
    public void validateDuplicateUserError(Response response) {
        // Выводим код и тело ответа для отладки
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body:\n" + response.getBody().prettyPrint());

        // Проверяем, что код ответа 403
        assertThat(response.getStatusCode(), is(403));

        // Проверяем, что сообщение об ошибке совпадает с ожидаемым
        assertThat(response.jsonPath().getString("message"), equalTo("User already exists"));
    }


    // Создание пользователя без пароля
    @Step("Создать уникального пользователя без указания пароля")
    public Response createUserWithoutPassword(String email, String name) {
        String body = String.format(
                "{\n" +
                        "  \"email\": \"%s\",\n" +
                        "  \"name\": \"%s\"\n" +
                        "}", email, name);
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/register");
    }

    @Step("Проверка ответа на неудачную попытку создания пользователя без пароля")
    public void verifyUserCreationFailureDueToMissingPassword(Response response) {
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), is(403));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is("Email, password and name are required fields"));
    }

    // Создание пользователя без email
    @Step("Создать уникального пользователя без указания email")
    public Response createUserWithoutEmail(String password, String name) {
        String body = String.format(
                "{\n" +
                        "  \"password\": \"%s\",\n" +
                        "  \"name\": \"%s\"\n" +
                        "}", password, name);
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/register");
    }

    @Step("Проверка ответа на неудачную попытку создания пользователя без email")
    public void verifyUserCreationFailureDueToMissingEmail(Response response, String expectedMessage) {
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), is(403));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    // Создание пользователя без имени
    @Step("Создать уникального пользователя без указания имени")
    public Response createUserWithoutName(String password, String email) {
        String body = String.format(
                "{\n" +
                        "  \"password\": \"%s\",\n" +
                        "  \"email\": \"%s\"\n" +
                        "}", password, email);
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/register");
    }
    public String generateUniqueEmail() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 6);
        return "user" + uniqueId + "@yandex.ru";
    }

    public String generateUniquePassword() {
        return "pass" + UUID.randomUUID().toString().substring(0, 6);
    }

    public String generateUniqueName() {
        return "User" + UUID.randomUUID().toString().substring(0, 6);
    }

    // Методы для тестового класса UserLoginTest

    @Step("Логин под существующим пользователем")
    public Response loginWithUser(String email, String password, String name) {
        System.out.println("Логин с данными - email: " + email + ", password: " + password + ", name: " + name);
        String body = String.format(
                "{\n" +
                        "  \"email\": \"%s\",\n" +
                        "  \"password\": \"%s\",\n" +
                        "  \"name\": \"%s\"\n" +
                        "}", email, password, name);
        // Выполнение запроса на логин пользователя
        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/api/auth/login");
        System.out.println("Пользователь успешно вошел в систему");
        return response;
    }

    @Step("Проверка ответа успешного логина пользователя")
    public String verifyLoginSuccess(Response response) {
        // Выводим код и тело ответа на консоль для отладки
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response Body:\n" + response.getBody().prettyPrint());
        // Проверка кода ответа и успешного статуса
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
        // Извлечение accessToken из ответа и возврат его
        return response.jsonPath().getString("accessToken");
    }

    @Step("Проверка ответа при попытке логина с неверными email и паролем")
    public static void verifyLoginWithInvalidCredentials(Response loginResponse) {
        // Проверка кода ответа и сообщения об ошибке для неверных данных
        loginResponse
                .then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }


    // Методы для тестового класса UserDataUpdateTest

    // Обновление email пользователя с авторизацией
    @Step("Проверка обновления email пользователя с авторизацией")
    public static Response updateUserEmail(String accessToken, String newEmail, String password, String name) {
        String requestBody = String.format(
                "{\"email\":\"%s\", \"name\":\"%s\", \"password\":\"%s\"}", newEmail, name, password);
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken) // Устанавливаем токен авторизации
                .body(requestBody) // Тело запроса
                .when()
                .patch("/api/auth/user"); // Выполняем PATCH-запрос на обновление данных пользователя
    }

    // Логирование данных запроса обновления email пользователя с авторизацией
    @Step("Логирование данных запроса обновления email пользователя с авторизацией")
    public static void logRequest(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление email:");
        System.out.println("Токен авторизации: " + accessToken); // Логирование токена авторизации
        System.out.println("Тело запроса: " + requestBody); // Логирование тела запроса
    }

    // Логирование данных ответа обновления email пользователя с авторизацией
    @Step("Логирование данных ответа обновления email пользователя с авторизацией")
    public static void logResponse(Response response) {
        System.out.println("Ответ после обновления email:");
        System.out.println(response.prettyPrint()); // Логирование красивого вывода ответа
    }

    // Проверка успешного обновления email пользователя с авторизацией
    @Step("Проверка, что статус код 200 и данные email пользователя с авторизацией обновлены")
    public static void validateUpdateResponse(Response response, String newEmail, String name) {
        // Проверка, что статус код 200 и успешность обновления
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true)) // Проверка, что обновление прошло успешно
                .body("user.email", equalTo(newEmail)) // Проверка обновленного email
                .body("user.name", equalTo(name)); // Проверка, что имя не изменилось
    }


    // Методы для обновления имени пользователя с авторизацией

    // Обновление имени пользователя с авторизацией
    @Step("Проверка обновления name пользователя с авторизацией")
    public static Response updateUserName(String accessToken, String email, String password, String newName) {
        // Формирование тела запроса с использованием String.format
        String requestBody = String.format("{\"email\":\"%s\", \"name\":\"%s\", \"password\":\"%s\"}", email, newName, password);
        return given()
                .contentType(ContentType.JSON) // Устанавливаем тип контента
                .header("Authorization", accessToken) // Устанавливаем токен авторизации
                .body(requestBody) // Устанавливаем тело запроса
                .when()
                .patch("/api/auth/user"); // Выполняем PATCH-запрос на обновление имени
    }

    // Логирование данных запроса обновления имени пользователя с авторизацией
    @Step("Логирование данных запроса обновления name пользователя с авторизацией")
    public static void logRequestName(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление name:");
        System.out.println("Токен авторизации: " + accessToken); // Логирование токена авторизации
        System.out.println("Тело запроса: " + requestBody); // Логирование тела запроса
    }

    // Логирование данных ответа обновления имени пользователя с авторизацией
    @Step("Логирование данных ответа обновления name пользователя с авторизацией")
    public static void logResponseName(Response response) {
        System.out.println("Ответ после обновления name:");
        System.out.println(response.prettyPrint()); // Логирование красивого вывода ответа
    }

    // Проверка успешного обновления имени пользователя с авторизацией
    @Step("Проверка, что статус код 200 и name пользователя с авторизацией обновлено")
    public static void validateUpdateNameResponse(Response response, String newName, String email) {
        // Проверка, что статус код 200 и успешность обновления
        response
                .then()
                .statusCode(200)
                .body("success", equalTo(true)) // Проверка успешности операции
                .body("user.name", equalTo(newName)) // Проверка обновленного имени
                .body("user.email", equalTo(email)); // Проверка, что email остался прежним
    }


// Методы для обновления пароля пользователя с авторизацией

    // Обновление пароля пользователя с авторизацией
    @Step("Проверка обновления password пользователя с авторизацией")
    public static Response updateUserPassword(String accessToken, String newPassword) {
        // Формируем тело запроса с использованием String.format для лучшей читаемости
        String requestBody = String.format("{\"password\":\"%s\"}", newPassword);
        return given()
                .contentType(ContentType.JSON) // Устанавливаем тип контента
                .header("Authorization", accessToken) // Устанавливаем токен авторизации
                .body(requestBody) // Устанавливаем тело запроса
                .when()
                .patch("/api/auth/user"); // Выполняем PATCH-запрос на обновление пароля
    }

    // Логирование данных запроса обновления пароля пользователя с авторизацией
    @Step("Логирование данных запроса обновления password пользователя с авторизацией")
    public static void logRequestPassword(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление password:");
        System.out.println("Токен авторизации: " + accessToken); // Логируем токен авторизации
        System.out.println("Тело запроса: " + requestBody); // Логируем тело запроса
    }

    // Логирование данных ответа обновления пароля пользователя с авторизацией
    @Step("Логирование данных ответа обновления password пользователя с авторизацией")
    public static void logResponsePassword(Response response) {
        System.out.println("Ответ после обновления password:");
        System.out.println(response.prettyPrint()); // Логируем красивый вывод ответа
    }

    // Проверка успешного обновления пароля пользователя с авторизацией
    @Step("Проверка кода и ответа обновления password пользователя с авторизацией")
    public static void validateUpdatePasswordResponse(Response response, String newPassword) {
        // Проверка, что статус код 200 и успешность обновления
        response
                .then()
                .statusCode(200) // Проверка, что статус код равен 200
                .body("success", equalTo(true)); // Проверка, что обновление пароля успешно
    }


// Методы для обновления всех полей пользователя с авторизацией

    // Обновление всех полей пользователя с авторизацией
    @Step("Проверка обновления всех полей пользователя с авторизацией")
    public static Response updateUserAllFields(String accessToken, String newEmail, String newPassword, String newName) {
        // Формируем тело запроса с использованием String.format для лучшей читаемости
        String requestBody = String.format("{\"email\":\"%s\", \"name\":\"%s\", \"password\":\"%s\"}", newEmail, newName, newPassword);
        return given()
                .contentType(ContentType.JSON) // Устанавливаем тип контента
                .header("Authorization", accessToken) // Устанавливаем токен авторизации
                .body(requestBody) // Устанавливаем тело запроса
                .when()
                .patch("/api/auth/user"); // Выполняем PATCH-запрос на обновление всех полей
    }

    // Логирование данных запроса на обновление всех полей пользователя с авторизацией
    @Step("Логирование данных запроса на обновление всех полей пользователя с авторизацией")
    public static void logRequestAllFields(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление всех полей:");
        System.out.println("Токен авторизации: " + accessToken); // Логируем токен авторизации
        System.out.println("Тело запроса: " + requestBody); // Логируем тело запроса
    }

    // Логирование данных ответа обновления всех полей пользователя с авторизацией
    @Step("Логирование данных ответа обновления всех полей пользователя с авторизацией")
    public static void logResponseAll(Response response) {
        System.out.println("Ответ после обновления всех полей:");
        System.out.println(response.prettyPrint()); // Логируем красивый вывод ответа
    }

    // Проверка успешного обновления всех полей пользователя с авторизацией
    @Step("Проверка обновления всех данных пользователя с авторизацией")
    public static void validateUpdateAllFieldsResponse(Response response, String newEmail, String newName) {
        // Проверка, что статус код 200 и успешность обновления всех полей
        response
                .then()
                .statusCode(200) // Проверка, что статус код равен 200
                .body("success", equalTo(true)) // Проверка, что обновление прошло успешно
                .body("user.email", equalTo(newEmail)) // Проверка, что email обновился
                .body("user.name", equalTo(newName)); // Проверка, что name обновилось
    }


// Методы для обновления email пользователя без авторизации

    // Логирование данных запроса на обновление email без авторизации
    @Step("Логирование данных запроса на обновление email без авторизации")
    public static void logRequestWithoutAuth(String requestBody) {
        System.out.println("Запрос на обновление email без авторизации:");
        System.out.println("Тело запроса: " + requestBody); // Логируем тело запроса
    }

    // Обновление email пользователя без авторизации
    @Step("Обновление email пользователя без авторизации")
    public static Response updateUserEmailWithoutAuth(String newEmail, String password, String name) {
        // Формируем тело запроса с использованием String.format для лучшей читаемости
        String requestBody = String.format("{\"email\":\"%s\", \"name\":\"%s\", \"password\":\"%s\"}", newEmail, name, password);
        return given()
                .contentType(ContentType.JSON) // Устанавливаем тип контента
                .body(requestBody) // Устанавливаем тело запроса
                .when()
                .patch("/api/auth/user"); // Выполняем PATCH-запрос без авторизации
    }

    // Проверка ошибки доступа без авторизации
    @Step("Проверка ошибки доступа без авторизации")
    public static void validateUnauthorizedResponse(Response response) {
        response
                .then()
                .statusCode(401) // Проверка, что статус код 401 (неавторизован)
                .body("success", equalTo(false)) // Проверка, что операция не успешна
                .body("message", equalTo("You should be authorised")); // Проверка сообщения об ошибке
    }


    // Обновление name пользователя без авторизации
    @Step("Обновление name пользователя без авторизации")
    public static Response updateUserNameWithoutAuth(String email, String password, String newName) {
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + newName + "\", \"password\":\"" + password + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }

    // Логирование тела запроса на обновление name без авторизации
    @Step("Логирование данных запроса на обновление name без авторизации")
    public static void logRequestWithoutAuthName(String requestBody) {
        System.out.println("Запрос на обновление name без авторизации:");
        System.out.println("Тело запроса: " + requestBody); // Выводим тело запроса для отладки
    }


    // Обновление пароля пользователя без авторизации
    @Step("Обновление пароля пользователя без авторизации")
    public static Response updateUserPasswordWithoutAuth(String email, String newPassword, String name) {
        String requestBody = "{\"email\":\"" + email + "\", \"name\":\"" + name + "\", \"password\":\"" + newPassword + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }

    // Логирование запроса на обновление пароля пользователя без авторизации
    @Step("Логирование запроса на обновление пароля без авторизации")
    public static void logRequestWithoutAuthPassword(String requestBody) {
        System.out.println("Запрос на обновление пароля без авторизации:");
        System.out.println("Тело запроса: " + requestBody); // Выводим тело запроса для отладки
    }


    // Обновление всех данных пользователя без авторизации
    @Step("Обновление всех данных пользователя без авторизации")
    public static Response updateUserAllWithoutAuth(String newEmail, String newPassword, String newName) {
        String requestBody = "{\"email\":\"" + newEmail + "\", \"name\":\"" + newName + "\", \"password\":\"" + newPassword + "\"}";
        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/api/auth/user");
    }

    // Логирование запроса на обновление всех данных пользователя без авторизации
    @Step("Логирование запроса на обновление всех данных без авторизации")
    public static void logRequestWithoutAuthAll(String requestBody) {
        System.out.println("Запрос на обновление всех данных без авторизации:");
        System.out.println("Тело запроса: " + requestBody); // Выводим тело запроса для проверки
    }


// Методы для тестового класса OrderCreationTest
// С авторизацией и с ингредиентами

    // Метод для запроса на создание заказа с ингредиентами и авторизацией
    @Step("Метод для запроса создания заказа с ингредиентами и авторизацией")
    public static Response createOrderWithIngredients(String accessToken) {
        System.out.println("Проверка успешного создания заказа...");
        // Формирование тела запроса для создания заказа с ингредиентами
        String orderRequestBody = "{\n" +
                "  \"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\", \"61c0c5a71d1f82001bdaaa77\"]\n" +
                "}";
        // Отправка запроса на создание заказа с токеном авторизации
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(orderRequestBody)
                .when()
                .post("/api/orders");
    }

    // Метод для проверки успешного создания заказа с ингредиентами и авторизацией
    @Step("Метод для проверки кода и тела ответа успешности создания заказа с ингредиентами и авторизацией")
    public static void verifyOrderCreation(Response orderResponse) {
        System.out.println("Проверка ответа успешного создания заказа...");
        // Проверяем, что статус код 200 и успешность создания заказа
        orderResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));
        System.out.println("Создание заказа проверено успешно");
    }


    // С авторизацией, но без ингредиентов

    // Метод для запроса создания заказа с авторизацией, но без ингредиентов
    @Step("Метод для запроса создания заказа с авторизацией, но без ингредиентов")
    public static Response createOrderWitNoIngredients(String accessToken) {
        // Формирование тела запроса для создания заказа без ингредиентов
        String orderRequestBody = "{\n" +
                "  \"ingredients\": []\n" +
                "}";
        // Отправка запроса на создание заказа с токеном авторизации
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(orderRequestBody)
                .when()
                .post("/api/orders");
    }

    // Метод для проверки кода и тела ответа при ошибке создания заказа без ингредиентов, но с авторизацией
    @Step("Метод для проверки кода и тела ответа ошибки создания заказа без ингредиентов, но с авторизацией")
    public static void verifyOrderCreationNoIngredients(Response orderResponse) {
        // Проверяем статус ошибки при отсутствии ингредиентов
        orderResponse.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }


    // Без авторизации, но с ингредиентами

    // Метод для проверки ошибки запроса создания заказа с ингредиентами, но без авторизации
    @Step("Метод для проверки ошибки запроса создания заказа с ингредиентами, но без авторизации")
    public static Response createOrderWithoutAuthorization() {
        System.out.println("Выполняется метод для проверки ошибки запроса создания заказа без авторизации, с ингредиентами...");
        // Формирование тела запроса для создания заказа с ингредиентами
        String orderRequestBody = "{\n" +
                "  \"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\", \"61c0c5a71d1f82001bdaaa77\"]\n" +
                "}";
        // Отправка запроса на создание заказа без авторизации
        return given()
                .contentType(ContentType.JSON)
                .body(orderRequestBody)
                .when()
                .post("/api/orders");
    }

    // Метод для проверки кода и тела ответа при отсутствии авторизации для создания заказа с ингредиентами
    @Step("Метод для проверки кода и тела ответа при отсутствии авторизации для создания заказа с ингредиентами")
    public static void verifyOrderCreationUnauthorized(Response orderResponse) {
        orderResponse.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
        System.out.println("Метод для проверки ошибки кода и тела ответа при отсутствии авторизации создания заказа с ингредиентами отработал успешно");
    }


// Без авторизации и без ингредиентов

    // Метод для запроса создания заказа без авторизации и без ингредиентов
    @Step("Метод для запроса создания заказа без авторизации и без ингредиентов")
    public static Response createOrderWithoutAuthorizationAndIngredients() {
        // Формирование тела запроса для создания заказа без ингредиентов
        String orderRequestBody = "{\n" +
                "  \"ingredients\": []\n" +
                "}";
        // Отправка запроса на создание заказа без авторизации
        return given()
                .contentType(ContentType.JSON)
                .body(orderRequestBody)
                .when()
                .post("/api/orders");
    }

    // Метод для проверки кода и тела ответа при отсутствии авторизации для создания заказа без ингредиентов
    @Step("Метод для проверки кода и тела ответа при отсутствии авторизации для создания заказа без ингредиентов")
    public static void verifyOrderCreationNoIngredientsUnauthorized(Response orderResponse) {
        orderResponse.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    // Метод для проверки кода и тела ответа при отсутствии ингредиентов для создания заказа без авторизации
    @Step("Метод для проверки кода и тела ответа при отсутствии ингредиентов для создания заказа без авторизации")
    public static void verifyOrderCreationNoAuthorizedAndNoIngredients(Response orderResponse) {
        orderResponse.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }


// С авторизацией, но с неверным хешем ингредиентов

    // Метод для создания заказа с неверным хешем ингредиентов
    @Step("Метод для запроса создания заказа с неверным хешем ингредиентов")
    public static Response createOrderWithInvalidIngredientsHash(String accessToken) {
        // Формирование тела запроса с неверными хешами ингредиентов
        String orderRequestBody = "{\n" +
                "  \"ingredients\": [\"invalidHash1\", \"invalidHash2\"]\n" +
                "}";
        // Отправка запроса на создание заказа с токеном авторизации
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken) // Добавлен префикс "Bearer"
                .body(orderRequestBody)
                .when()
                .post("/api/orders");
    }

    // Метод для проверки кода и тела ответа при неверном хеше ингредиентов
    @Step("Метод для проверки кода и тела ответа при неверном хеше ингредиентов")
    public static void verifyOrderCreationInvalidIngredientsHash(Response orderResponse) {
        // Проверка статуса и тела ответа при ошибке из-за неверных хешей ингредиентов
        orderResponse.then()
                .statusCode(400)  // Ожидаем ошибку 400, так как ингредиенты некорректны
                .body("success", equalTo(false))
                .body("message", equalTo("Invalid ingredient hash"));  // Проверка конкретного сообщения об ошибке
    }


// Методы для тестового класса UserOrdersTest

    // Метод для получения списка заказов авторизованного пользователя
    @Step("Метод для получения списка заказов авторизованного пользователя")
    public static Response getUserOrders(String accessToken) {
        System.out.println("Запрос на получение списка заказов авторизованного пользователя...");
        // Отправка GET запроса на получение заказов с токеном авторизации
        Response response = given()
                .header("Authorization", "Bearer " + accessToken) // Добавление "Bearer " для корректного токена
                .log().all() // Логирование запроса для отладки
                .when()
                .get("/api/orders")
                .then()
                .log().all() // Логирование ответа
                .extract().response();
        System.out.println("Ответ на запрос получения списка заказов обработан");
        return response;
    }

    // Метод для проверки успешности получения списка заказов
    @Step("Метод для проверки ответа успешного получения списка заказов")
    public static void verifyUserOrdersRetrieval(Response ordersResponse) {
        System.out.println("Проверка успешного получения списка заказов...");
        ordersResponse.then()
                .statusCode(200) // Проверка успешного статуса ответа
                .body("success", equalTo(true)); // Проверка успешности операции
        // Дополнительная проверка, что хотя бы один заказ содержится в ответе
        List<Map<String, Object>> orders = ordersResponse.jsonPath().getList("orders");
        assertThat("Список заказов должен содержать хотя бы один элемент", orders, not(empty())); // Убедитесь, что список не пуст
        System.out.println("Список заказов проверен успешно");
    }


    // Без авторизации
    @Step("Метод для получения списка заказов без авторизации")
    public static Response getUserOrdersWithoutAuthorization() {
        System.out.println("Выполняется метод для проверки ответа на запрос списка заказов без авторизации...");
        // Отправка запроса на получение списка заказов без токена авторизации
        Response response = given()
                .log().all() // Логирование запроса для анализа
                .when()
                .get("/api/orders")
                .then()
                .log().all() // Логирование ответа для проверки
                .extract().response();
        System.out.println("Метод для проверки ошибки получения списка заказов без авторизации завершен");
        return response;
    }

    @Step("Метод для проверки ошибки получения списка заказов без авторизации")
    public static void verifyUnauthorizedResponse(Response response) {
        System.out.println("Проверка ответа на ошибку при запросе списка заказов без авторизации...");
        response.then()
                .statusCode(401) // Ожидаем код ошибки 401
                .body("success", equalTo(false)) // Проверка успешности запроса
                .body("message", equalTo("You should be authorised")); // Проверка сообщения ошибки
        System.out.println("Проверка ответа на ошибку при запросе списка заказов без авторизации завершена");
    }
}
