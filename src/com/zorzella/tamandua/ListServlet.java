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

public class ListServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(ListServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    resp.setContentType("text/html");
    resp.setCharacterEncoding(Constants.encoding);
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {

      Html.htmlHeadBody(ps);
      
      ps.println("<table>" +
            "<th>Onde</th>" +
      		"<th>Titulo</th>" +
            "<th>Autor</th>" +
            "<th>Tamanho</th>" +
            "<th>Tags</th>" +
      		"");
      Collection<Book> sortedBooks = Queries.getSortedBooks(pm);

      for (Book book : sortedBooks) {
        ps.printf("<tr>" +
        		"<td align='right'>%s</td>" +
        		"<td>%s</td>" +
        		"<td>%s</td>" +
        		"<td>%s</td>" +
        		"<td>%s</td>" +
        		"\n", 
            book.getParadeiro() + "&nbsp;" + book.getToca(), book.getTitulo(), book.getAutor(), 
            book.getTamanho(), book.getTags());  
      }

      ps.println("</body></html>");
      ps.flush();
      resp.getOutputStream().close();

    } catch (RuntimeException e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
  }
}