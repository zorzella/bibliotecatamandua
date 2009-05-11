package com.zorzella.tamandua;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
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
      foo();
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    } finally {
      pm.close();
    }
  }

  private void foo() {
	  Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);

      String msgBody = "... Message ...";

      try {
          Message msg = new MimeMessage(session);
          msg.setFrom(new InternetAddress("zorzella@gmail.com"));
          msg.addRecipient(Message.RecipientType.TO,
                           new InternetAddress("zorzella@gmail.com"));
          msg.setSubject("Your Example.com account has been activated");
          msg.setText(msgBody);
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
			itemsOnLoan += "* "+ item.getTitulo() + " emprestado em " + Dates.dateToString(loan.getLoanDate()) + "\n";
    	}
		String message = 
    		"Somente um lembrete, os seguintes ítens estão sob sua custódia:\n" +
    		"\n" +
    		itemsOnLoan +
    		"\n" +
    		"Nossa constituição e acervo podem ser encontrados em:\n" +
    		"\n" +
    		"http://mensageirosdacultura.com/MDC_Biblioteca.html\n" +
    		"\n" +
    		"Z (o Tamanduá)";
    	
    	ps.printf("<input type='checkbox' name='m-%s'>\n", codigo);
    	ps.printf("To: %s &lt;%s&gt; (último email data '%s')\n", 
    			nome(member), member.getEmail(), Dates.dateToString(member.getLastContacted()));
    	ps.println("<br>Subject: Biblioteca Tamanduá -- ítens sob sua custódia");
    	ps.printf("<br>Body: <textarea name='message-%s' rows='10' cols='100'>%s</textarea>\n", codigo, message);
    	ps.println("<hr>");
    }

    ps.println("</form>");
    ps.println("</html></body>");

    ps.flush();
    resp.getOutputStream().close();
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