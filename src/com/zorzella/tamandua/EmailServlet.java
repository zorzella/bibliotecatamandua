package com.zorzella.tamandua;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmailServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(EmailServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    String admin = AdminOrDie.adminOrLogin(req, resp);
    if (admin == null) {
      return;
    }
    
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      go(req, resp, pm, admin);
      foo("... Message ...", "zorzella@gmail.com", "zorzella@gmail.com", "Your Example.com account has been activated");
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    } finally {
      pm.close();
    }
  }

  private void foo(String body, String from, String to, String subject) {
	  Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);

      try {
          Message msg = new MimeMessage(session);
          msg.setFrom(new InternetAddress(from));
          msg.addRecipient(Message.RecipientType.TO,
                           new InternetAddress(to));
          msg.setSubject(subject);
          msg.setText(body);
          Transport.send(msg);

      } catch (AddressException e) {
          throw new RuntimeException(e);
      } catch (MessagingException e) {
    	  throw new RuntimeException(e);
      }
}
  
  private void go(HttpServletRequest req, HttpServletResponse resp, PersistenceManager pm, String admin)
      throws UnsupportedEncodingException, IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding(Constants.encoding);
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));
    
    ps.println("<html>");
    ps.println("<head><link type='text/css' rel='stylesheet' href='/stylesheets/main.css'/></head>");
    ps.println("<body>");

    ps.println("<form action='/email' method='POST'>");

    Collection<Member> members = Queries.getAll(Member.class, pm);
    
    for (Member member : members) {
    	String codigo = member.getCodigo();
		Collection<Loan> loans = Queries.getByQuery(Loan.class, pm, 
    			"memberCode == \"" + codigo + "\" && returnDate == null");
    	if (loans.size() == 0) {
    		continue;
    	}
    	String itemsOnLoan = "";
    	for (Loan loan : loans) {
    		Long itemId = loan.getItemId();
    		Item item = Queries.getById(Item.class, pm, "id", itemId + "");
			itemsOnLoan += "* "+ item.getTitulo() +
			 " [emprestado em " + Dates.dateToString(loan.getLoanDate()) + "]" +
			"\n";
    	}
		String message = 
    		"Lembrete -- os seguintes &iacute;tens est&atilde;o sob sua cust&oacute;dia:\n" +
    		"\n" +
    		itemsOnLoan +
    		"\n" +
    		"Nossa constitui&ccedil;&atilde;o e acervo podem ser encontrados em:\n" +
    		"\n" +
    		"http://mensageirosdacultura.com/MDC_Biblioteca.html\n" +
    		"\n" +
    		"Z (o Tamandu&aacute;)";
    	
		String subject = "Biblioteca Tamandu&aacute; -- &iacute;tens sob sua cust&oacute;dia";
    	ps.printf("<input type='checkbox' name='sendto-%s'>\n", codigo);
    	ps.printf("To: [%s] %s &lt;%s&gt; (&uacute;ltimo email data '%s')\n", 
    			tudo(nome(member), member.getEmail(), subject, message), nome(member), member.getEmail(), Dates.dateToString(member.getLastContacted()));
      ps.printf("<br>Subject: %s\n", subject);
    	ps.printf("<br>Body: <textarea name='message-%s' rows='10' cols='100'>%s</textarea>\n", codigo, message);
    	ps.println("<hr>");
    }

    ps.println("<input type='submit' value='Manda'>");
    ps.println("</form>");
    ps.println("</html></body>");

    ps.flush();
    resp.getOutputStream().close();
  }
  
  private String tudo(String nome, String email, String subject, String message) {
  return String.format("<a href='mailto:%s<%s>?subject=%s&body=%s'>send</a>", nome, email, subject, 
//      URLEncoder.encode(message)
  message    
  );
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
  
  
  String admin = AdminOrDie.adminOrLogin(req, resp);
  if (admin == null) {
    return;
  }
  
  PersistenceManager pm = PMF.get().getPersistenceManager();
  try {
     email(req, resp, pm, admin);
//    foo();
  } catch (RuntimeException e) {
    e.printStackTrace();
    throw e;
  } finally {
    pm.close();
  }

  
  }

private void email(HttpServletRequest req, HttpServletResponse resp,
      PersistenceManager pm, String admin) {

@SuppressWarnings("unchecked")
Map<String,String[]> map = req.getParameterMap();
for (String key : map.keySet()) {
  if (key.startsWith("sendto-")) {
    String codigo = key.substring("sendto".length());
    String message = map.get("message=" + codigo)[0];
    
  }
}


  }

private String nome(Member member) {
	String result = "";
	
	if ((empty(member.getPai())) && (empty(member.getMae()))) {
		result += member.getNome();
	}

	if ((!empty(member.getPai())) && (!empty(member.getMae()))) {
		result += member.getPai() + "/" + member.getMae();
	}
	if (!empty(member.getPai())) {
		result += member.getPai();
	} else if (!empty(member.getMae())) {
		result += member.getMae();
	}
	return result += " [" + member.getNome() + " " + member.getSobrenome() + "]";
}

private boolean empty(String string) {
	return string == null || string.trim().length() == 0;
}
}