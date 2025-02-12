package praktikum.operators;


import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.http.OrderHTTP;
import praktikum.objects.Order;

import java.util.List;

public class OrderAPIOperators extends OrderHTTP {
    @Step("Создание заказа (с авторизацией)")
    public Response createOrder(List<String> ingredients, String token) {
        return super.createOrder(new Order(ingredients), token);
    }

    @Step("Создание заказа (без авторизации)")
    public Response createOrder(List<String> ingredients) {
        return super.createOrder(new Order(ingredients));
    }

    @Step("Получить список ингредиентов")
    public Response getIngredientList() {
        return super.getIngredientList();
    }

    @Step("Получить список заказов")
    public Response getOrderList(String token) {
        return super.getOrderList(token);
    }

    @Step("Получить список всех заказов")
    public Response getAllOrderList() {
        return super.getAllOrderList();
    }
}

