package praktikum.operators;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import praktikum.http.UserHTTP;
import praktikum.objects.User;
import praktikum.objects.UserResponse;

import static org.apache.http.HttpStatus.*;

import static org.hamcrest.Matchers.equalTo;


public class UserAPIOperators extends UserHTTP {


    //Метод для регистрации нового пользователя с заданными параметрами.
    @Step("Создание нового пользователя")
    public Response createNewUser(String email, String password, String name) {
        return super.userRegistration(new User(email, password, name));
    }

    //Метод для получения токена  авторизации из ответа.
    @Step("Получение токена авторизации")
    public String extractAuthToken (Response response) {
        String token = response.body().as(UserResponse.class).getAccessToken().split(" ")[1];
        Allure.addAttachment("Код и статус: ", response.getStatusLine());
        Allure.addAttachment("Токен: ", token);
        return token;
    }

    //Метод для выполнения логина пользователя с использованием email и пароля.
    @Step("Логин пользователя")
    public Response loginUser(String email, String password) {
        return super.loginUser(new User(email, password));
    }


    //Метод для удаления пользователя по токену авторизации.
    @Step("Удаление пользователя")
    public Response deleteUser(String token) {
        return super.deleteUser(token);
    }

    //Метод для обновления данных пользователя с использованием email, пароля, имени и токена.
    @Step("Обновление данных пользователя")
    public Response updateUser(String email, String password, String name, String token) {
        return super.updateUser(new User(email, password, name), token);
    }


    //Метод для обновления данных пользователя без токена.
    @Step("Обновление данных пользователя без токена")
    public Response updateUser(String email, String password, String name) {
        return super.updateUser(new User(email, password, name));
    }



    //Метод для проверки данных пользователя, таких как email, имя и пароль.
    @Step("Проверка данных пользователя")
    public void checkUserData(Response response, String expectedEmail, String expectedPassword, String expectedName) {
        User currentUser = response.body().as(UserResponse.class).getUser();
        Allure.addAttachment("Новый пользователь", currentUser.toString());

        MatcherAssert.assertThat("Email не совпадает", currentUser.getEmail(), equalTo(expectedEmail));
        MatcherAssert.assertThat("Имя не совпадает", currentUser.getName(), equalTo(expectedName));

        new CheckResponse().verifyResponseStatus(loginUser(expectedEmail, expectedPassword), SC_OK);
    }
}

