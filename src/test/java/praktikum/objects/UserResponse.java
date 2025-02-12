package praktikum.objects;

import lombok.Getter;
import lombok.Setter;


public class UserResponse {
    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private String refreshToken;

    @Getter
    @Setter
    private String accessToken;

    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    private String success;

    public UserResponse() { }

    public UserResponse(String success, User user, String accessToken, String refreshToken) {
        this.success = success;
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public UserResponse(String success, String message) {
        this.success = success;
        this.message = message;
    }
}
