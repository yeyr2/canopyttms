package com.ttms.component;

import com.ttms.dao.UserDao;
import com.ttms.pojo.Mail;
import com.ttms.pojo.Response;
import com.ttms.pojo.User;
import com.ttms.tools.Sha256Util;
import com.ttms.tools.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;

@Component
@PropertySource("classpath:application.properties")
public class MailComponent {

    @Autowired
    private UserDao userDao;
    @Value("${mail.username}")
    public String username;
    @Value("${mail.password}")
    public String password;

    public ResponseEntity<Response> Mail(Mail mail) throws MessagingException, UnsupportedEncodingException {
        String password = mail.getPassword();
        String email = mail.getEmail();

        if(password == null || password.isEmpty() ){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }
        password = Sha256Util.getSHA256Str(StringUtil.removeSpaces(password));
        email = StringUtil.removeSpaces(email);

        if (!Mail.formatCheck(email)) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        // 根据邮箱查询修改密码
        if(!userDao.changePasswordByEmail(email,password)){
            return ResponseEntityComponent.Update_Failed("password");
        }

        sendMail(email,password,null,"password");

        Response response = new Response();
        response.setValue(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public void sendMail(String toUser, Object object, User user,String type) throws MessagingException, UnsupportedEncodingException {
        // 创建Properties 类用于记录邮箱的一些属性
        Properties props = new Properties();
        // 表示SMTP发送邮件，必须进行身份验证
        props.put("mail.smtp.auth", "true");
        // 此处填写SMTP服务器
        props.put("mail.smtp.host", "smtp.qq.com");
        // 端口号，QQ邮箱端口587
        props.put("mail.smtp.port", "587");
        // 此处填写，写信人的账号
        props.put("mail.username", username);
        // 此处填写16位STMP口令
        props.put("mail.password", password);

        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                // 用户名、密码
                String username = props.getProperty("mail.username");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(username, password);
            }
        };
        // 使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        // 创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);

        // 设置自定义发件人昵称
        String nick = javax.mail.internet.MimeUtility.encodeText("yeyr2-ttms");
        String from = props.getProperty("mail.username");
        // 设置发件人
        InternetAddress form = new InternetAddress(nick + "<" + from + ">");

        // 第二种方式
        // InternetAddress form = new InternetAddress(from,"我的昵称");

        message.setFrom(form);

        switch (type){
            case "password":
                // 设置收件人的邮箱
                InternetAddress to = new InternetAddress(toUser);
                message.setRecipient(MimeMessage.RecipientType.TO, to);
                // 设置邮件标题
                message.setSubject("ttms：修改密码");
                // 设置邮件的内容体
                String context = "尊敬的用户"+user.getUsername()+":\n你的账号密码已经被修改，若不是本人操作请尽快修改密码。\n修改后的密码为："+object;
                message.setContent(context, "text/html;charset=UTF-8");

                // 发送邮件
                Transport.send(message);
                break;
            case "code":
                // 设置收件人的邮箱
                to = new InternetAddress(toUser);
                message.setRecipient(MimeMessage.RecipientType.TO, to);
                message.setSubject("ttms：验证码");
                String yzm = getYzm();
                context = "尊敬的用户"+user.getUsername()+":\n你的账号密码正在被修改，若不是本人操作请忽略。\n验证码："+yzm;
                message.setContent(context, "text/html;charset=UTF-8");
                UserComponent.verificationCode__tmp.put(user.getId(),yzm+" "+System.currentTimeMillis());

                // 发送邮件
                Transport.send(message);
                break;
            case "email":
                // 设置收件人的邮箱
                to = new InternetAddress(user.getEmail());
                message.setRecipient(MimeMessage.RecipientType.TO, to);
                message.setSubject("ttms：修改邮箱");
                context = "尊敬的用户"+user.getUsername()+":\n你的邮箱已从"+user.getEmail()+"更改为"+toUser;
                message.setContent(context, "text/html;charset=UTF-8");
                // 发送邮件
                Transport.send(message);

                to = new InternetAddress(toUser);
                message.setRecipient(MimeMessage.RecipientType.TO, to);
                // 发送邮件
                Transport.send(message);
                break;
            default:
                break;
        }
    }


    public String getYzm() {
        //1，用随机生成数方法，生成验证码
        Random yzm = new Random();

        StringBuilder yzm2 = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int a = yzm.nextInt(3);
            switch(a){
                case 0:
                    char s=(char)(yzm.nextInt(26)+65);
                    yzm2.append(s);
                    break;
                case 1:
                    char s1=(char)(yzm.nextInt(26)+97);
                    yzm2.append(s1);
                    break;
                case 2:
                    int s2=yzm.nextInt(10);
                    yzm2.append(s2);
                    break;
            }
        }
        return yzm2.toString();
    }
}

