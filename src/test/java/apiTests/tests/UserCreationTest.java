package apiTests.tests;

import apiTests.methods.MethodsUserCreation;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;

public class UserCreationTest extends MethodsUserCreation {

    private final MethodsUserCreation userActions = new MethodsUserCreation();

    @Test
    @Description("Создание уникального пользователя и удаление после проверки")
    public void creationUniqueUser() {
        // Генерация случайных данных для пользователя
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Создаем пользователя
        Response response = registerNewUser(email, password, name);
        // Проверяем ответ и сохраняем токен для последующего удаления
        String accessToken = userActions.validateUserCreationResponse(response, email, name);
        // Можно использовать явно вызываемое удаление пользователя внутри теста
        removeUserByToken(accessToken);
    }

    @Test
    @Description("Создание зарегистрированного пользователя и проверка на ошибку повторной регистрации")
    public void createExistingUser() {
        // Генерация случайных данных для пользователя
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();

        // Первый запрос на создание пользователя
        Response firstResponse = userActions.registerNewUser(email, password, name);
        // Проверка успешного создания пользователя
        userActions.validateUserCreationResponse(firstResponse, email, name);

        // Сохраняем accessToken для удаления пользователя
        accessToken = firstResponse.jsonPath().getString("accessToken");

        // Второй запрос на создание того же пользователя
        Response secondResponse = userActions.registerNewUser(email, password, name);
        // Проверка ошибки при повторной регистрации
        userActions.validateUserAlreadyExistsError(secondResponse);
    }


    @Test
    @Description("Создание пользователя без пароля и проверка на ошибку")
    public void createUserWithoutRequiredFieldPassword() {
        // Генерация случайных данных для пользователя без password
        String email = generateUniqueEmail();
        String name = generateUniqueName();
        // Создаем пользователя
        Response response = createUserWithoutPassword(email, name);
        // Проверяем, что создание пользователя без password завершилось ошибкой
        userActions.validateUserCreationResponse(response, email,name);
    }


    @Test
    @Description("Создание пользователя без email и проверка на ошибку")
    public void createUserWithoutRequiredFieldEmail() {
        // Генерация случайных данных для пользователя без email
        String password = generateUniquePassword();
        String name = generateUniqueName();
        // Создаем пользователя
        Response response = createUserWithoutEmail(password, name);
        // Проверяем, что создание пользователя без email завершилось ошибкой
        userActions.validateUserCreationResponse(response, null, name);
    }

    @Test
    @Description("Создание пользователя без имени и проверка на ошибку")
    public void createUserWithoutRequiredFieldName() {
        // Генерация случайных данных для пользователя без name
        String password = generateUniquePassword();
        String email = generateUniqueEmail();
        // Создаем пользователя
        Response response = createUserWithoutName(password, email);
        // Проверяем, что создание пользователя без name завершилось ошибкой
        userActions.validateUserCreationResponse(response, email,null);
    }
}
