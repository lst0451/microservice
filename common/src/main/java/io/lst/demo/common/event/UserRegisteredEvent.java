package io.lst.demo.common.event;

import lombok.Data;

@Data
public class UserRegisteredEvent {
    private String username;
    private String email;

    //必须要有无参构造方法，否则 jackson 无法反序列化
    public UserRegisteredEvent() {
    }

    public UserRegisteredEvent(String username, String email) {
        this.username = username;
        this.email = email;
    }


    public String toString() {
        return "UserRegisteredEvent{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
