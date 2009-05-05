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

public class BorrowReturnServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(BorrowReturnServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    AdminOrDie.adminOrDie(req, resp);

    resp.setContentType("text/html");
    resp.setCharacterEncoding(Constants.encoding);
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

    ps.println("<html>");
    ps.println("<head><link type='text/css' rel='stylesheet' href='/stylesheets/main.css'/></head>");
    ps.println("<body>");
    ps.println("<form action='borrowreturn'>");
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      Collection<Member> members = Queries.getSortedMembers(pm);

      ps.println("<select name='member'>");
      for (Member member : members) {
        ps.printf("<option value='%s'>%s (%s %s)</option>\n", 
            member.getCodigo(), member.getCodigo(), member.getNome(), member.getSobrenome());
      }
      ps.println("</select><br>");
      
      Collection<Book> books = Queries.getFancySortedBooks(pm);

      for (Book book : books) {
        ps.printf("<input type='checkbox' name='%s'> [%s] %s <br>\n",
            book.getId(), book.getParadeiro(), book.getTitulo());
      }

      ps.println("<br><input type='submit' value='Empresta e Devolve'>");
      ps.println("</form>");
      ps.println("</html></body>");

      ps.flush();
      resp.getOutputStream().close();

    } catch (RuntimeException e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
  }
}