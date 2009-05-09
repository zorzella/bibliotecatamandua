package com.zorzella.tamandua;


import org.joda.time.DateTime;
import org.joda.time.DateTimeField;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyLoansServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(ModifyLoansServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

	  AdminOrDie.adminOrDie(req, resp);
    resp.setContentType("text/html");
    resp.setCharacterEncoding(Constants.encoding);
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {

      Html.htmlHeadBody(ps);

      @SuppressWarnings("unchecked")
      Map<String,String[]> map = req.getParameterMap();

      boolean adminCode = map.containsKey("adminCode");
      boolean memberCode = map.containsKey("memberCode");
      boolean bookId = map.containsKey("bookId");
      boolean loanDate = map.containsKey("loanDate");
      boolean returnDate = map.containsKey("returnDate");
      
      if (!adminCode && !memberCode && !bookId && !loanDate && !returnDate 
          ) {
        memberCode = true;
        bookId = true;
        returnDate = true;
      }
      
      ps.println("<form action='modifyloans' method='post'>");
      
      ps.println("<input type='submit' value='Change'>");
      ps.println("<table>");
      
      ps.println("<th>Admin</th>");
      ps.println("<th>Member</th>");
      ps.println("<th>Book</th>");
      ps.println("<th>Loaned</th>");
      ps.println("<th>Returned</th>");

      Collection<Loan> loans = Queries.getAll(Loan.class, pm);
      
      boolean even = false;
      for (Loan loan : loans) {
        even = !even;
        if (even) {
          ps.printf("<tr class='a'>");
        } else {
          ps.printf("<tr class='b'>");
        }
        choose(ps, false, adminCode, loan, true, loan.getAdminCode(), "adminCode");
        choose(ps, false, memberCode, loan, true, loan.getMemberCode(), "memberCode");
        choose(ps, false, bookId, loan, true, loan.getBookId() + "", "bookId");
        choose(ps, false, loanDate, loan, true, Dates.dateToString(loan.getLoanDate()), "loanDate");
        choose(ps, false, returnDate, loan, true, Dates.dateToString(loan.getReturnDate()), "returnDate");
        ps.print("\n"); 
      }

      ps.println("</table>");

      ps.println("</form>");
      ps.println("<form action='/modifyloans'>");

      printCheckboxesAndDropdown(ps,
          adminCode, memberCode, bookId, loanDate, 
          returnDate
   );

      ps.println("</body></html>");
      ps.flush();
      resp.getOutputStream().close();

    } catch (RuntimeException e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
  }

  private void choose(
      PrintWriter ps, 
      boolean shortInput, 
      boolean nome, 
      Loan loan, 
      boolean alignRight, 
      String content, 
      String key) {
    if (nome) {
      if (shortInput) {
        input(ps, loan, key, content);
      } else {
        shortInput(ps, loan, key, content);
      }
    } else {
      if (alignRight) {
        Html.tdRight(ps, content);
      } else {
        Html.td(ps, content);
      }
    }
  }

  private PrintWriter shortInput(PrintWriter ps, Loan member, String key, String value) {
    return ps.printf("<td><input type='text' name='%s' value='%s' class='short'></td>", 
        key + "-" + member.getId(), value);
  }

  private PrintWriter input(PrintWriter ps, Loan member, String key, String value) {
    return ps.printf("<td><input type='text' name='%s' value='%s' class='medium'></td>", 
        key + "-" + member.getId(), value);
  }

  private void printCheckboxesAndDropdown(PrintWriter ps, boolean adminCode, boolean memberCode, boolean bookId, boolean loanDate, boolean returnDate
      ) {
    ps.println("<hr>");
    ps.println("Mostre: " +
        checkbox(adminCode, "adminCode", "Admin") +
        checkbox(memberCode, "memberCode", "Member") +
        checkbox(bookId, "bookId", "Book") + 
        checkbox(loanDate, "loanDate", "Loaned") + 
        checkbox(returnDate, "returnDate", "Returned") + 
          "");
    ps.println("<input type='submit' value='Membros'>");

  }

  private String checkbox(boolean selected, String key, String label) {
    return "<input type='checkbox' name='" + key + "'" + (selected ? " checked" : "" ) + ">" + label + "</input>\n";
  }
  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

	  AdminOrDie.adminOrDie(req, resp);

	@SuppressWarnings("unchecked")
    Map<String,String[]> map = req.getParameterMap();
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Collection<Loan> items = Queries.getAll(Loan.class, pm);
    for (Loan item : items) {
      String codigo = item.getId() + "";
      String key = "adminCode-" + codigo;
      if (map.containsKey(key)) {
        item.setAdminCode(toString(map, key));
      }
      key = "memberCode-" + codigo;
      if (map.containsKey(key)) {
        item.setMemberCode(toString(map, key));
      }
      key = "bookId-" + codigo;
      if (map.containsKey(key)) {
        item.setBookId(toLong(map, key));
      } 
      key = "loanDate-" + codigo;
      if (map.containsKey(key)) {
        item.setLoanDate(toDate(map, key));
      } 
      key = "returnDate-" + codigo;
      if (map.containsKey(key)) {
        item.setReturnDate(toDate(map, key));
      } 
      pm.makePersistent(item);
    }
    pm.close();
    resp.sendRedirect("/modifyloans");
  }

  private boolean toBoolean(Map<String, String[]> map, String key) {
    return Boolean.parseBoolean(map.get(key)[0]);
  }

  private String toString(Map<String, String[]> map, String key) {
    return map.get(key)[0];
  }
  
  private Date toDate(Map<String, String[]> map, String key) {
    String string = map.get(key)[0];
    if (string.trim().length() == 0) {
      return null;
    }
    String[] split = string.split("-");
    return new DateTime(
        Integer.parseInt(split[0]),
        Integer.parseInt(split[1]),
        Integer.parseInt(split[2]),
        0, 0, 0, 0).toDate();
  }

  private int toInt(Map<String, String[]> map, String key) {
  return Integer.parseInt(map.get(key)[0]);
}

  private long toLong(Map<String, String[]> map, String key) {
  return Long.parseLong(map.get(key)[0]);
}
}