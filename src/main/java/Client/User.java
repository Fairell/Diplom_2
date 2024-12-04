package Client;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.RandomStringUtils;

public class User {
    private String email;
    private String password;
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User() {
    }

    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    // Уникальный пользователь
    public static User getRandomUser() {
        return new User("ars" + RandomStringUtils.randomAlphanumeric(4, 8) + "@mail.ru", "ars!!" + RandomStringUtils.randomAlphanumeric(5, 10), "arseny" + RandomStringUtils.randomAlphanumeric(5, 10));
    }

    // Уникальный пользователь без пароля
    public static User getWithoutPassword() {
        return new User("ars" + RandomStringUtils.randomAlphanumeric(4, 8) + "@mail.ru", null, "arseny" + RandomStringUtils.randomAlphanumeric(5, 10));
    }
}
