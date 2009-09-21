package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Maps;

import com.zorzella.tamandua.Queries.Books;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BorrowReturnServlet extends HttpServlet {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(BorrowReturnServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    if (AdminOrDie.adminOrLogin(req, resp) == null){
      return;
    }

    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      goGet(resp, pm);
    } catch (RuntimeException e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
  }

  private void goGet(HttpServletResponse resp, PersistenceManager pm) throws IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding(Constants.encoding);
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

    Html.htmlHeadBody(ps);
    printForm(pm, ps);
    ps.println("</html></body>");

    ps.flush();
    resp.getOutputStream().close();
  }

  static void printForm(PersistenceManager pm, PrintWriter ps) {
    ps.println("<br><br><br><br>");
    ps.println("<form action='borrowreturn' method='POST'>");

    Collection<Member> members = Queries.getSortedMembers(pm);

    ps.println("<select name='member'>");
    for (Member member : members) {
      if (member.getNome().trim().equals("")) {
        continue;
      }
      ps.printf("<option value='%s'>%s - %s</option>\n", 
          member.getId(), member.getCodigo(), EmailServlet.nome(member));
    }
    ps.println("</select><br><br><br><br>");
//    ps.println("<input type='submit' value='Empresta e Devolve'><br>");

    Map<Long, String> map = getMap(members);
    Books books = new Queries(map).getFancySortedBooks(pm);

    ps.println("<select name='r' multiple size=6>");

    printBorrowedSelectOptions(pm, ps, books);

    ps.println("</select><br><br><br><br>\n" +
    		"<select name='b' multiple size=10>");
    
    printAvailableSelectOptions(ps, books);

    ps.println("</select><br><br><br><br>");

    ps.println("<input type='submit' value='Empresta e Devolve'>");
    ps.println("</form>");
  }

  private static void printBorrowedSelectOptions(PersistenceManager pm, PrintWriter ps,
      Books books) {
    for (Item book : books.getBorrowed()) {
      Long paradeiro = book.getParadeiro();
      String memberCodigo = "";
      if (paradeiro != null) {
        memberCodigo = "[" + 
          Queries.getById(Member.class, pm, "id", paradeiro + "").getCodigo()
          + "] ";
      }
      String htmlValue = memberCodigo + book.getStrippedTitle();
      ps.println(String.format(
          "<option value='%s'>%s", book.getId(), htmlValue));
    }
  }

  private static void printAvailableSelectOptions(PrintWriter ps, Books books) {
    String lastTitle = "";
    for (Item book : books.getAvailable()) {
      Long paradeiro = book.getParadeiro();
      String strippedTitle = book.getStrippedTitle();
      if (lastTitle.equals(strippedTitle)) {
        // Show only a single copy of each available title
        continue;
      }
      if (isABreak(lastTitle, book)) {
        ps.println("</select><br>\n" +
          "<select name='b' multiple size=10>");
      }
      ps.println(String.format(
          "<option value='%s'> %s", book.getId(), strippedTitle));

      lastTitle = strippedTitle;
    }
  }

  private static boolean isABreak(String lastTitle, Item book) {
    String bookTitle = book.getStrippedTitle().toLowerCase();
    String lastBookTitle = lastTitle.toLowerCase();
    boolean dToH = lastBookTitle.matches("^[a-c].*") && 
        bookTitle.matches("^[d-h].*");
    boolean iToO = lastBookTitle.matches("^[d-h].*") && 
        bookTitle.matches("^[i-o].*");
    boolean pToZ = lastBookTitle.matches("^[i-o].*") && 
        bookTitle.matches("^[p-z].*");
    return dToH || iToO || pToZ;
  }

  private static Map<Long, String> getMap(Collection<Member> members) {
    Map<Long, String> result = Maps.newHashMap();
    for (Member member : members) {
        result.put(member.getId(), member.getCodigo());
    }
    return result;
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    String admin = AdminOrDie.adminOrLogin(req, resp);
    if (admin == null) {
      return;
    }

    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      goPost(req, resp, pm, admin);
    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    } finally {
      pm.close();
      resp.sendRedirect("/member");

    }
  }

  private void goPost(HttpServletRequest req, HttpServletResponse resp, PersistenceManager pm, String admin) {
//    resp.setContentType("text/html");
//    resp.setCharacterEncoding(Constants.encoding);
//    PrintWriter ps = new PrintWriter(
//        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

//    ps.println("<html>");
//    ps.println("<head><link type='text/css' rel='stylesheet' href='/stylesheets/main.css'/></head>");
//    ps.println("<body>");

    @SuppressWarnings("unchecked")
    Map<String,String[]> parameters = req.getParameterMap();

    String[] temp = parameters.get("member");
    if (temp.length != 1) {
      throw new IllegalArgumentException();
    }
    
    Long memberId = Long.parseLong(temp[0]);
    Member member = Queries.getById(Member.class, pm, "id", memberId + "");
    
    List<Item> returnedItems = Lists.newArrayList();
    List<Item> borrowedItems = Lists.newArrayList();
    
    String[] returned = parameters.get("r");
    if (returned != null) {
      for (String key : returned) {
        Item item = Queries.getById(Item.class, pm, "id", key);
  
        if (!item.getParadeiro().equals(memberId)) {
          log.warning(String.format(
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
          returnedItems.add(item);
        }
      } 
    }
    String[] borrowed = parameters.get("b");
    if (borrowed != null) {
      for (String key : borrowed) {
        Item item = Queries.getById(Item.class, pm, "id", key);
  
        Loan loan = new Loan(admin, memberId, item.getId());
        pm.makePersistent(loan);
  
        item.setParadeiro(memberId);
        pm.makePersistent(item);
        borrowedItems.add(item);
      }
    }    
    sendEmail(member, borrowedItems, returnedItems);
  }

  private void sendEmail(
      Member member, 
      List<Item> borrowedItems, 
      List<Item> returnedItems) {
    if ((borrowedItems.size() == 0) && (returnedItems.size() == 0)) {
      return;
    }
    
    String to = Emails.FROM;
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
    body.append(EmailServlet.nome(member) + " - " + member.getEmail() + ":\n\n");

    if (borrowedItems.size() > 0) {
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
    Emails.sendEmail(
        body, 
        Emails.FROM, 
        to, 
        Emails.CC,
        subject);
  }
}