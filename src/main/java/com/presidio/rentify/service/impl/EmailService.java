package com.presidio.rentify.service.impl;

import com.presidio.rentify.dto.MailBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  @Autowired
  private JavaMailSender mailSender;

  public void sendEmail(MailBody mailBody) {
    SimpleMailMessage message = new SimpleMailMessage();

    message.setTo(mailBody.getTo());
    message.setSubject(mailBody.getSubject());
    message.setText(mailBody.getText());

    mailSender.send(message);
  }
}