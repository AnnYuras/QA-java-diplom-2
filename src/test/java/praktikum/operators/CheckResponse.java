package praktikum.operators;


import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import praktikum.objects.UserResponse;

import static org.hamcrest.CoreMatchers.equalTo;

public class CheckResponse {

    /**
     * Метод для проверки статуса ответа и его кода.
     */

    @Step("Проверка кода и статуса ответа")
    public void verifyResponseStatus(Response response, int code) {

        // Добавление вложения в Allure с кодом и статусом ответа
        Allure.addAttachment("Код и статус: ", response.getStatusLine());
        // Проверка, что код статуса соответствует ожидаемому
        response.then().statusCode(code);
    }


    //Метод для проверки успешности запроса по полю success.
    @Step("Проверка успешности запроса")
    public void verifySuccessStatus(Response response, String expectedValue) {

        // Сравнение значения поля "success" с ожидаемым значением
        MatcherAssert.assertThat(
                "Неверное значение в поле success",
                expectedValue,
                equalTo(response.body().as(UserResponse.class).getSuccess())
        );
    }

    //Метод для проверки текста сообщения в ответе.
    @Step("Проверка тела (сообщения) ответа")
    public void verifyMessageText(Response response, String expectedMessage) {

        // Сравнение значения поля "message" с ожидаемым сообщением
        MatcherAssert.assertThat(
                "Неверный текст в поле message",
                expectedMessage,
                equalTo(response.body().as(UserResponse.class).getMessage())
        );
    }
}

