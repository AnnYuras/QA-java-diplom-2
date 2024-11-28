package praktikum.http;

import io.restassured.response.Response;
import praktikum.ServerAPIURLs;
import praktikum.objects.Order;

public class OrderHTTP extends BaseHTTP {


    // Метод для создания заказа без авторизации
    public Response createOrder(Order order) {

        // Отправка POST-запроса на сервер без авторизации
        return sendPostRequest(
                ServerAPIURLs.BASE_URL + ServerAPIURLs.ORDERS,
                order,
                "application/json"
        );
    }

    // Метод для создания заказа с авторизацией через токен
    public Response createOrder(Order order, String token) {

        // Отправка POST-запроса на сервер с авторизацией
        return sendPostRequest(
                ServerAPIURLs.BASE_URL + ServerAPIURLs.ORDERS,
                order, // Объект заказа
                "application/json",
                token // Токен для авторизации
        );
    }




    // Метод для получения всех заказов (без авторизации)
    public Response getAllOrderList() {
        return sendGetRequest(
                ServerAPIURLs.BASE_URL + ServerAPIURLs.GET_ALL_ORDERS
        );
    }

    // Метод для получения списка ингредиентов
    public Response getIngredientList() {
        return sendGetRequest(
                ServerAPIURLs.BASE_URL + ServerAPIURLs.GET_INGREDIENTS_LIST
        );
    }

    // Метод для получения списка заказов с авторизацией через токен
    public Response getOrderList(String token) {
        return sendGetRequest(
                ServerAPIURLs.BASE_URL + ServerAPIURLs.ORDERS,
                token
        );
    }


}

