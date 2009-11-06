package com.zorzella.tamandua;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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

    if (AdminOrDie.adminOrLogin(req, resp) == null) {
      return;
    }
    resp.setContentType("text/html");
    resp.setCharacterEncoding(Constants.encoding);
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {

      Html.htmlHeadBody(ps);

      @SuppressWarnings("unchecked")
      Map<String,String[]> map = req.getParameterMap();

      boolean editableBorrowAdminCode = map.containsKey("borrowAdminCode");
      boolean editableMemberCode = map.containsKey("memberCode");
      boolean editableBookId = map.containsKey("bookId");
      boolean editableLoanDate = map.containsKey("loanDate");
      boolean editableReturnDate = map.containsKey("returnDate");
      boolean editableReturnAdminCode = map.containsKey("returnAdminCode");
      boolean editableComment = map.containsKey("comment");
      
      if (!map.containsKey("custom")) {
//        memberCode = true;
//        bookId = true;
//        editableReturnDate = true;
      }
      
      ps.println("<form action='/modifyloans' method='post'>");
      
      ps.println("<input type='submit' value='Change'>");
      ps.println("<table>");
      
      ps.println("<th>Borrow Admin</th>");
      ps.println("<th>Member</th>");
      ps.println("<th>Book</th>");
      ps.println("<th>Loaned</th>");
      ps.println("<th>Returned</th>");
      ps.println("<th>Return Admin</th>");
      ps.println("<th>Comment</th>");

      Collection<Loan> loans = //Queries.getByQuery(Loan.class, pm, "ORDER BY loanDate");
        (Collection<Loan>) pm.newQuery(
            "SELECT FROM com.zorzella.tamandua.Loan " +
//          "WHERE adminCode == :ac " +
//          "PARAMETERS String ac " +
            "ORDER BY returnDate, loanDate" +
          "").execute();//"zorzella");
      Collection<Member> members = Queries.getAll(Member.class, pm);
      Collection<Item> items = Queries.getAll(Item.class, pm);
      
      boolean even = false;
      for (Loan loan : loans) {
        even = !even;
        if (even) {
          ps.printf("<tr class='a'>");
        } else {
          ps.printf("<tr class='b'>");
        }
        choose(ps, false, editableBorrowAdminCode, 
            loan, true, loan.getBorrowAdminCode(), "borrowAdminCode");
        Long memberId = loan.getMemberId();
        if (editableMemberCode) {
//          shortInput(ps, loan, "memberId", content);
          memberDropdown(ps, loan.getId(), loan.getMemberId(), members);
        } else {
          Html.tdRight(ps, memberCodeFor(members, memberId));
        }
        Long itemId = loan.getItemId();
        if (editableBookId) {
//          shortInput(ps, loan, "bookId", itemId);
          itemDropdown(ps, loan.getId(), loan.getItemId(), items);
        } else {
          Html.td(ps, itemTitleFor(items, itemId));
        }
//        Item item = Queries.getById(Item.class, pm, "id", loan.getItemId() + "");
//        choose(ps, false, false, loan, false, item.getTitulo(), "titulo");
        choose(ps, false, editableLoanDate, loan, true, Dates.dateToString(loan.getLoanDate()), "loanDate");
        choose(ps, false, editableReturnDate, loan, true, Dates.dateToString(loan.getReturnDate()), "returnDate");
        choose(ps, false, editableReturnAdminCode, 
            loan, true, loan.getReturnAdminCode(), "returnAdminCode");
        choose(ps, false, editableComment, loan, true, loan.getComment(), "comment");
        ps.print("\n"); 
      }

      ps.println("</table>");

      ps.println("</form>");
      ps.println("<form action='/modifyloans'>");

      printCheckboxesAndDropdown(ps,
          editableBorrowAdminCode, 
          editableMemberCode, 
          editableBookId, 
          editableLoanDate, 
          editableReturnDate,
          editableComment
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
  
  private String memberCodeFor(Collection<Member> members, Long memberId) {
    for (Member member : members) {
      if (member.getId().equals(memberId)) {
        return member.getCodigo();
      }
    }
    throw new IllegalStateException();
  }

  private String itemTitleFor(Collection<Item> items, Long itemId) {
    for (Item member : items) {
      if (member.getId().equals(itemId)) {
        return member.getTitulo();
      }
    }
    throw new IllegalStateException();
  }

  private static void memberDropdown(PrintWriter ps, Long loanId, Long currentMemberId, Collection<Member> members) {
    ps.printf("<td><select name='member-%s'><option value=''></option>",
        loanId);
  
    for (Member member : members) {
      String selected = "";
      if (member.getId().equals(currentMemberId)) {
        selected = "selected='true'";
      }
      ps.printf("<option value='%s' %s>%s</option>", member.getId(), selected, member.getCodigo());
    }
    
    ps.println("</select></td>");
  }

  private static void itemDropdown(PrintWriter ps, Long loanId, Long currentItemId, Collection<Item> items) {
    ps.printf("<td><select name='item-%s'><option value=''></option>",
        loanId);
  
    for (Item item : items) {
      String selected = "";
      if (item.getId().equals(currentItemId)) {
        selected = "selected='true'";
      }
      ps.printf("<option value='%s' %s>%s</option>", item.getId(), selected, item.getTitulo());
    }
  
    ps.println("</select></td>");
  }

  private void choose(
      PrintWriter ps, 
      boolean shortInput, 
      boolean editable, 
      Loan loan, 
      boolean alignRight, 
      String content, 
      String key) {
    if (editable) {
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
    return ps.printf("<td><input type='text' name='%s' value='%s' class='medium'></td>", 
        key + "-" + member.getId(), value);
  }

  private PrintWriter input(PrintWriter ps, Loan member, String key, String value) {
    return ps.printf("<td><input type='text' name='%s' value='%s' class='medium'></td>", 
        key + "-" + member.getId(), value);
  }

  private void printCheckboxesAndDropdown(
      PrintWriter ps, 
      boolean editableAdminCode, 
      boolean editableMemberCode, 
      boolean editableBookId, 
      boolean editableLoanDate, 
      boolean editableReturnDate,
      boolean editableComment
      ) {
    ps.println("<hr>");
    ps.println("Mostre: " +
        checkbox(editableAdminCode, "adminCode", "Admin") +
        checkbox(editableMemberCode, "memberCode", "Member") +
        checkbox(editableBookId, "bookId", "Book") + 
        checkbox(editableLoanDate, "loanDate", "Loaned") + 
        checkbox(editableReturnDate, "returnDate", "Returned") + 
        checkbox(editableComment, "comment", "Comment") + 
          "");
    ps.println("<input type='hidden' name='custom' value='true'>");
    ps.println("<input type='submit' value='Loans'>");

  }

  private String checkbox(boolean selected, String key, String label) {
    return "<input type='checkbox' name='" + key + "'" + (selected ? " checked" : "" ) + ">" + label + "</input>\n";
  }
  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

	String adminCode = AdminOrDie.adminOrLogin(req, resp);
    if (adminCode == null) {
	  return;
	}

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
        item.setMemberId(toLong(map, key));
      }
      key = "bookId-" + codigo;
      if (map.containsKey(key)) {
        item.setItemId(toLong(map, key));
      } 
      key = "loanDate-" + codigo;
      if (map.containsKey(key)) {
        item.setLoanDate(toDate(map, key));
      } 
      key = "returnDate-" + codigo;
      if (map.containsKey(key)) {
        item.setReturnDate(adminCode, toDate(map, key));
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
    DateTime dateTime = new DateTime(
        Integer.parseInt(split[0]),
        Integer.parseInt(split[1]),
        Integer.parseInt(split[2]),
        0, 0, 0, 0);
    return dateTime.toDate();
  }

  private int toInt(Map<String, String[]> map, String key) {
    return Integer.parseInt(map.get(key)[0]);
  }

  private long toLong(Map<String, String[]> map, String key) {
    return Long.parseLong(map.get(key)[0]);
  }
}