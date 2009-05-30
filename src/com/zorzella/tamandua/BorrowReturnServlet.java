package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BorrowReturnServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(BorrowReturnServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    String admin = AdminOrDie.adminOrLogin(req, resp);
    if (admin == null) {
      return;
    }

    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      go(req, resp, pm, admin);
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    } finally {
      pm.close();
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


    @SuppressWarnings("unchecked")
    Map<String,String[]> parameters = req.getParameterMap();


    String[] temp = parameters.get("member");
    if (temp.length != 1) {
      throw new IllegalArgumentException();
    }
    
    Long memberId = Long.parseLong(temp[0]);
    Member member = Queries.getById(Member.class, pm, "id", memberId + "");
    ps.println("Member: " + member);
    
    List<Item> returnedItems = Lists.newArrayList();
    List<Item> borrowedItems = Lists.newArrayList();
    
    for (String key : parameters.keySet()) {
      if (key.equals("member")) {
        continue;
      } else {
        Item item = Queries.getById(Item.class, pm, "id", key.substring(2));

        if (key.startsWith("r-")) {
          if (!item.getParadeiro().equals(memberId)) {
            ps.println(String.format(
                "<br> Ignoring '%s' which is not on loan to '%s'", 
                item.getTitulo(), 
                memberId));
          } else {

            Loan loan = Queries.getFirstByQuery(Loan.class, pm, 
                "memberId == " + memberId + 
                " && itemId == " + item.getId() + "" +
                //  && returnDate == null" +
                "", memberId);
            //                "memberCode == ? && itemId == ? && returnDate == NULL", memberCode, item.getId());
            loan.setReturnDate(new Date());
            pm.makePersistent(loan);

            item.setParadeiro(null);
            pm.makePersistent(item);
            ps.println("<br> Returned: " + item.getTitulo());
            returnedItems.add(item);
          }
        } else if (key.startsWith("b-")) {
          Loan loan = new Loan(admin, memberId, item.getId());
          pm.makePersistent(loan);

          item.setParadeiro(memberId);
          pm.makePersistent(item);
          ps.println("<br> Borrowed: " + item.getTitulo());
          borrowedItems.add(item);
        } else {
          throw new IllegalArgumentException();
        }
      }
    }
    
    sendEmail(member, borrowedItems, returnedItems);

    ps.println("<br>");

    ps.println("<a href='member'>back</a>");

    ps.println("</html></body>");

    ps.flush();
    resp.getOutputStream().close();
  }

  private void sendEmail(Member member, List<Item> borrowedItems, List<Item> returnedItems) {
    if ((borrowedItems.size() == 0) && (returnedItems.size() == 0)) {
      return;
    }
    
    String subject = "Itens ";
    
    if (returnedItems.size() == 0) {
      subject += "emprestados";
    } else {
    	if (borrowedItems.size() == 0) {
    		subject += "devolvidos";
    	} else {
    		subject += "emprestados e devolvidos";
    	}
    }
    // TODO
//    subject += " no encontro de ";

    StringBuilder body = new StringBuilder();
    body.append(EmailServlet.nome(member) + ":\n\n");

    if (returnedItems.size() > 0) {
      body.append("Os seguintes \u00EDtens foram emprestados:\n\n");
      for (Item borrowed : borrowedItems) {
        body.append(String.format("* %s\n", borrowed.getTitulo()));
      }
      body.append("\n\n");
    }

    if (returnedItems.size() > 0) {
      body.append("Os seguintes \u00EDtens foram devolvidos:\n\n");
      for (Item returned : returnedItems) {
        body.append(String.format("* %s\n", returned.getTitulo()));
      }
      body.append("\n\n");
    }
    body.append("\nO acervo e constitui\u00E7\u00E3o podem ser encontrados em: \n" +
    		"\n" +
        "http://mensageirosdacultura.com/MDC_Biblioteca.html\n" +
        "\n" +
        "Z (o Tamandu\u00E1)\n");
    Emails.sendEmail(body, Emails.FROM, Emails.FROM, subject);
  }
}