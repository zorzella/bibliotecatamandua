package com.zorzella.tamandua;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
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

      @SuppressWarnings("unchecked")
      Map<String,String[]> map = req.getParameterMap();

      String sortKey = "titulo";
      String[] temp = map.get("sort");
      if ((temp != null) && (temp.length == 1)) {
        sortKey = temp[0];
      }
      
      boolean onde = map.containsKey("onde");
      boolean titulo = map.containsKey("titulo");
      boolean autor = map.containsKey("autor");
      boolean tamanho = map.containsKey("tamanho");
      boolean tags = map.containsKey("tags");
      
      if (!onde && !titulo && !autor && !tamanho && !tags) {
        onde = true;
        titulo = true;
        tamanho = true;
        autor = true;
        tags = true;
      }
      
      ps.println("<form action='list'>");
      ps.println("Mostre: " +
          checkbox(onde, "onde", "Onde (Paradeiro e Toca)") +
          checkbox(titulo, "titulo", "Titulo") +
          checkbox(autor, "autor", "Autor") +
          checkbox(tamanho, "tamanho", "Tamanho") +
          checkbox(tags, "tags", "Tags") +
            "");
      ps.println("<br>Ordene: " +
          "<select name='sort'>" +
          dropdown(sortKey, "paradeiro", "Paradeiro") +
          dropdown(sortKey, "toca", "Toca") +
          dropdown(sortKey, "titulo", "Titulo") +
          dropdown(sortKey, "autor", "Autor") +
          "</select>" +
            "");
      ps.println("<br><input type='submit'>");
      
      ps.println("<table>");
      
      if (onde) {
        ps.println("<th>Onde</th>");
      }
      
      if (titulo) {
        ps.println("<th>Titulo</th>");
      }
      
      if (autor) {
        ps.println("<th>Autor</th>");
      }
      
      if (tamanho) {
        ps.println("<th>Tamanho</th>");
      }
      
      if (tags) {
        ps.println("<th>Tags</th>");
      }

      Collection<Book> sortedBooks = Queries.getSortedBooks(pm);

      for (Book book : sortedBooks) {
        ps.printf("<tr>");
        if (onde) {
          ps.printf("<td align='right'>%s</td>", book.getParadeiro() + "&nbsp;" + book.getToca());
        }
        if (titulo) {
          ps.printf("<td>%s</td>", book.getTitulo());
        }
        
        if (autor) {
          ps.printf("<td>%s</td>", book.getAutor());
        }
        
        if (tamanho) {
          ps.printf("<td>%s</td>", book.getTamanho());
        }

        if (tags) {
          ps.printf("<td>%s</td>", book.getTags());
        }
        ps.print("\n"); 
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

  private String dropdown(String sortKey, String key, String label) {
    return "<option value='" + key + "'" + (sortKey.equals(key) ? " selected" : "") + ">" + label + "</option>\n";
  }

  private String checkbox(boolean selected, String key, String label) {
    return "<input type='checkbox' name='" + key + "'" + (selected ? " checked" : "" ) + ">" + label + "</input>\n";
  }
}