package com.zorzella.tamandua;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmailServlet extends HttpServlet {

  @SuppressWarnings("unused")
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
//      foo("... Message ...", "zorzella@gmail.com", "zorzella@gmail.com", "Your Example.com account has been activated");
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    } finally {
      pm.close();
    }
  }

  private static final String subject = "Biblioteca Tamandua -- \u00EDtens sob sua cust\u00F3dia";

  private void go(HttpServletRequest req, HttpServletResponse resp, PersistenceManager pm, String admin)
  throws UnsupportedEncodingException, IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding(Constants.encoding);
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

    Html.htmlHeadBody(ps);
   
//    ps.println("<html>");
//    ps.println("<head><link type='text/css' rel='stylesheet' href='/stylesheets/main.css'/></head>");
//    ps.println("<body>");

    ps.println("<form action='/email' method='POST'>");

    Collection<Member> members = Queries.getAll(Member.class, pm);

    for (Member member : members) {
      if (member.getCodigo().equals("?")) {
        continue;
      }
      Long id = member.getId();
      Collection<Loan> loans = Queries.getByQuery(Loan.class, pm, 
          "memberId == " + id + " && returnDate == null");
      if (loans.size() == 0) {
        continue;
      }
      String itemsOnLoan = "";
      for (Loan loan : loans) {
        Long itemId = loan.getItemId();
        Item item = Queries.getById(Item.class, pm, "id", itemId + "");
        itemsOnLoan += "* "+ item.getTitulo() +
//        " [emprestado em " + Dates.dateToString(loan.getLoanDate()) + "]" +
        "\n";
      }
      String message = 
        "Lembrete -- os seguintes \u00EDtens est\u00E3o sob sua cust\u00F3dia:\n" +
        "\n" +
        itemsOnLoan +
        "\n" +
        "Nossa constitui\u00E7\u00E3o e acervo podem ser encontrados em:\n" +
        "\n" +
        "http://mensageirosdacultura.com/MDC_Biblioteca.html\n" +
        "\n" +
        "Z (o Tamandu\u00E1)";

      ps.printf("<input type='checkbox' name='sendto-%s'>\n", id);
      ps.printf("To: [%s] %s &lt;%s&gt; (\u00FAltimo email data '%s')\n", 
          tudo(TamanduaUtil.nome(member), member.getEmail(), subject, message), TamanduaUtil.nome(member), member.getEmail(), Dates.dateToString(member.getLastContacted()));
      ps.printf("<br>Subject: %s\n", subject);
      ps.printf("<br>Body: <textarea name='message-%s' rows='10' cols='100'>%s</textarea>\n", id, message);
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
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {

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
        String id = key.substring("sendto".length() + 1);
        String message = map.get("message-" + id)[0];
        Member member = Queries.getById(Member.class, pm, "id", id);
        
        Emails.sendEmail(
            message, 
            Emails.FROM, 
            member.getEmail(),
            Emails.CC, 
            subject);
      }
    }
  }
}