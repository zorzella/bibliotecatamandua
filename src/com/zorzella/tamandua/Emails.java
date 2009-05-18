package com.zorzella.tamandua;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emails {

  public static void sendEmail(CharSequence body, String from, String to, String subject) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
  
    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(from));
      msg.addRecipient(Message.RecipientType.TO,
          new InternetAddress(to));
      msg.setSubject(subject);
      msg.setText(body.toString());
      Transport.send(msg);
  
    } catch (AddressException e) {
      throw new RuntimeException(e);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  static final String FROM = "zorzella@gmail.com";

}
