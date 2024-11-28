package praktikum.http;

import io.restassured.response.Response;
import praktikum.ServerAPIURLs;
import praktikum.objects.User;

public class UserHTTP extends BaseHTTP{


    // Метод для удаления пользователя по токену
    public Response deleteUser(String token) {
        return sendDeleteRequest(
                ServerAPIURLs.BASE_URL + ServerAPIURLs.USER,
                token
        );
    }
    // Метод для регистрации пользователя
    public Response userRegistration(User user) {
        return sendPostRequest(
                ServerAPIURLs.BASE_URL + ServerAPIURLs.USER_REGISTRATION,
                user,
                "application/json"
        );
    }



    // Метод для логина пользователя
    public Response loginUser(User user) {
        return sendPostRequest(
                ServerAPIURLs.BASE_URL + ServerAPIURLs.USER_LOGIN,
                user,
                "application/json"
        );
    }

    // Метод для обновления данных пользователя с авторизацией через токен
    public Response updateUser(User user, String token) {
        return sendPatchRequest(
                ServerAPIURLs.BASE_URL + ServerAPIURLs.USER,
                user,
                "application/json",
                token
        );
    }

    // Метод для обновления данных пользователя без использования токена
    public Response updateUser(User user) {
        return sendPatchRequest(
                ServerAPIURLs.BASE_URL + ServerAPIURLs.USER,
                user,
                "application/json"
        );
    }
}

