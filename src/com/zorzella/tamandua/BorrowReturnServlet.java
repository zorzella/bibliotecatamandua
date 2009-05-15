package com.zorzella.tamandua;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
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

    Long memberId = null;

    for (String key : parameters.keySet()) {
      if (key.equals("member")) {
        if (memberId != null) {
          throw new IllegalArgumentException();
        }
        String[] temp = parameters.get(key);
        if (temp.length != 1) {
          throw new IllegalArgumentException();
        }
        memberId = Long.parseLong(temp[0]);
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
                "memberId == \"" + memberId + "\"" +
                " && itemId == " + item.getId() + "" +
                //  && returnDate == null" +
                "", memberId);
            //                "memberCode == ? && itemId == ? && returnDate == NULL", memberCode, item.getId());
            loan.setReturnDate(new Date());
            pm.makePersistent(loan);

            item.setParadeiro(null);
            pm.makePersistent(item);
            ps.println("<br> Returned: " + item.getTitulo());
          }
        } else if (key.startsWith("b-")) {
          Loan loan = new Loan(admin, memberId, item.getId());
          pm.makePersistent(loan);

          item.setParadeiro(memberId);
          pm.makePersistent(item);
          ps.println("<br> Borrowed: " + item.getTitulo());
        } else {
          throw new IllegalArgumentException();
        }
      }
    }

    ps.println("<br>");

    ps.println("<a href='member'>back</a>");

    ps.println("</html></body>");

    ps.flush();
    resp.getOutputStream().close();
  }
}