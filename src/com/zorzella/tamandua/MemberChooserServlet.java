package com.zorzella.tamandua;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MemberChooserServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(MemberChooserServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    AdminOrDie.adminOrDie(req, resp);

    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      go(resp, pm);
    } catch (RuntimeException e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
  }

  private void go(HttpServletResponse resp, PersistenceManager pm)
      throws IOException {
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
    ps.println("<form action='borrowreturn' method='POST'>");

    Collection<Member> members = Queries.getSortedMembers(pm);

    ps.println("<select name='member'>");
    for (Member member : members) {
      ps.printf("<option value='%s'>%s (%s %s)</option>\n", 
          member.getCodigo(), member.getCodigo(), member.getNome(), member.getSobrenome());
    }
    ps.println("</select>");
    ps.println("<input type='submit' value='Empresta e Devolve'><br>");

    Collection<Book> books = Queries.getFancySortedBooks(pm);

    for (Book book : books) {
      ps.printf("<input type='checkbox' name='%s-%s'> [%s] %s <br>\n",
          book.getParadeiro().length() > 0 ? "r" : "b", 
              book.getId(), book.getParadeiro(), book.getTitulo());
    }

    ps.println("<br><input type='submit' value='Empresta e Devolve'>");
    ps.println("</form>");
  }
}