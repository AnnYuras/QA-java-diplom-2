package praktikum.http;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
public class BaseHTTP {


    // Метод для создания базовой спецификации запроса с фильтром для Allure
    private RequestSpecification createBaseRequest() {
        return new RequestSpecBuilder()
                .addFilter(new AllureRestAssured())// Добавляем фильтр Allure для отчётности
                .setRelaxedHTTPSValidation()// Игнорируем проблемы с SSL-сертификатами
                .build();
    }

    private RequestSpecification createBaseRequest(String contentType) {
        return new RequestSpecBuilder()
                .addHeader("Content-type", contentType)// Устанавливаем заголовок Content-Type
                .addFilter(new AllureRestAssured())// Добавляем фильтр для Allure
                .setRelaxedHTTPSValidation()
                .build();
    }

    // Метод для отправки POST-запроса с телом и типом контента
    public Response sendPostRequest(String url, Object requestBody, String contentType) {
        return given(this.createBaseRequest(contentType))// Создаём базовый запрос
                .body(requestBody)// Добавляем тело запроса
                .when()
                .post(url);// Выполняем POST-запрос
    }

    // метод для отправки POST-запроса с авторизацией
    public Response sendPostRequest(String url, Object requestBody, String contentType, String token) {
        return given(this.createBaseRequest(contentType))
                .auth().oauth2(token)
                .body(requestBody)
                .when()
                .post(url);
    }



    // Метод для отправки GET-запроса
    public Response sendGetRequest(String url) {
        return given(this.createBaseRequest())// Создаём базовый запрос
                .get(url);// Выполняем GET-запрос
    }

    public Response sendGetRequest(String url, String token) {
        return given(this.createBaseRequest())
                .auth().oauth2(token)
                .when()
                .get(url);
    }


    // Метод для отправки DELETE-запроса с авторизацией
    public Response sendDeleteRequest(String url, String token) {
        return given(this.createBaseRequest())
                .auth().oauth2(token)
                .delete(url);
    }


    // Метод для отправки PATCH-запроса с авторизацией и телом запроса
    public Response sendPatchRequest(String url, Object requestBody, String contentType, String token) {
        return given(this.createBaseRequest(contentType))
                .auth().oauth2(token)
                .body(requestBody)
                .when()
                .patch(url);
    }


    //метод для отправки PATCH-запроса без авторизации
    public Response sendPatchRequest(String url, Object requestBody, String contentType) {
        return given(this.createBaseRequest(contentType))
                .body(requestBody)
                .when()
                .patch(url);
    }
}

