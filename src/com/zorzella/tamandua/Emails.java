package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Emails {

  public static void sendEmail(
      CharSequence body, 
      String from, 
      String to,
      List<String> ccs,
      String subject) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
  
    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(from));
      msg.addRecipient(Message.RecipientType.TO,
          new InternetAddress(to));
      for (String cc : ccs) {
        msg.addRecipient(Message.RecipientType.CC,
            new InternetAddress(cc));
      }
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
  static final List<String> CC = 
    Lists.newArrayList("keylazorzella@gmail.com");

}
