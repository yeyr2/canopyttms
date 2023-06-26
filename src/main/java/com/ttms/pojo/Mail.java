package com.ttms.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.regex.Pattern;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mail {
    private String password;
    private String email;

    public static boolean formatCheck(String mail) {
        if (mail == null || mail.isEmpty()) {
            return false;
        }

        return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", mail);
    }
}
