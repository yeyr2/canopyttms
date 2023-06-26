package com.ttms.controller;

import com.ttms.component.MailComponent;
import com.ttms.component.ResponseEntityComponent;
import com.ttms.pojo.Mail;
import com.ttms.pojo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private MailComponent mailComponent;

    @PostMapping("/fail_quick_mail")
    public ResponseEntity<Response> Mail(@RequestBody Mail mail) throws MessagingException, UnsupportedEncodingException {
        if(mail == null){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        return mailComponent.Mail(mail);
    }
}