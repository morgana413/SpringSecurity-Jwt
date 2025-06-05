package org.example.myprojectbackend.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    @Resource
    private MailSender mailSender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMailMessage(Map<String,Object> map){
        String email = (String)map.get("email");
        Integer code = (Integer)map.get("code");
        String type = (String)map.get("type");
        SimpleMailMessage message = switch (type){
            case "register" -> createMessage("欢迎注册","您的验证码是："+code+"有效期为三分钟，请勿向别人泄露",email);
            case "reset" -> createMessage("您的重置密码邮件","您的验证码为"+code+"有效期为三分钟，请勿向别人泄露",email);
            default -> null;
        };
        if (message == null) return;
        mailSender.send(message);
    }

    private SimpleMailMessage createMessage(String title, String content,String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(title);
        message.setText(content);
        message.setFrom(username);
        return message;
    }
}
