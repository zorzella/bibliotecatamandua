package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.base.Join;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyItemsServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(ModifyItemsServlet.class.getName());

  public enum Sort {
    PARADEIRO,
    TOCA,
    TITULO,
    AUTOR,
  }

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

      Sort sortKey = Sort.TITULO;
      String[] temp = map.get("sort");
      if ((temp != null) && (temp.length == 1)) {
        sortKey = Sort.valueOf(temp[0]);
      }

      boolean paradeiro = map.containsKey("paradeiro");
      boolean toca = map.containsKey("toca");
      boolean titulo = map.containsKey("titulo");
      boolean autor = map.containsKey("autor");
      boolean isbn = map.containsKey("isbn");
      boolean tamanho = map.containsKey("tamanho");
      boolean tags = map.containsKey("tags");

      if (!map.containsKey("custom")) {
        titulo = true;
        tamanho = true;
        autor = true;
        tags = true;
      }

      if (map.containsKey("added")) {
        toca = true;
      }

      ps.println("<form action='modifyitems' method='post'>");

      ps.println("<input type='text' value='0' name='add'>");
      ps.println("<input type='submit' value='Change'>");
      ps.println("<table>");

      ps.println("<th>Parad</th>");
      ps.println("<th>Toca</th>");
      ps.println("<th>Titulo</th>");
      ps.println("<th>Autor</th>");
      ps.println("<th>ISBN</th>");
      ps.println("<th>Tamanho</th>");
      ps.println("<th>Tags</th>");

      Collection<Item> sortedItems;

      switch (sortKey) {
        case TITULO: 
          sortedItems = Queries.getSortedItems(pm);
          break;
        case AUTOR:
          sortedItems = Queries.getAutorSortedItems(pm);
          break;
        case PARADEIRO:
          sortedItems = Queries.getParadeiroSortedItems(pm);
          break;
        case TOCA:
          sortedItems = Queries.getTocaSortedItems(pm);
          break;
        default:
          throw new UnsupportedOperationException();  
      }

      boolean even = false;
      Collection<Member> sortedMembers = Queries.getSortedMembers(pm);
      for (Item item : sortedItems) {
        even = !even;
        if (even) {
          ps.printf("<tr class='a'>");
        } else {
          ps.printf("<tr class='b'>");
        }
        if (paradeiro) {
          paradeiroDropdown(ps, item, item.getParadeiro(), sortedMembers);         
        } else {
          String paradeiroString = "";
          if (item.getParadeiro() != null) {
            paradeiroString = Queries.getById(Member.class, pm, "id", item.getParadeiro() + "").getCodigo();
          }
          Html.tdRight(ps, paradeiroString);
        }
        if (toca) {
          shortInput(ps, item, "toca", item.getToca());         
        } else {
          Html.tdRight(ps, item.getToca());
        }
        if (titulo) {
          input(ps, item, "titulo", item.getTitulo());          
        } else {
          Html.td(ps, item.getTitulo());
        }

        if (autor) {
          input(ps, item, "autor", item.getAutor());
        } else {
          Html.td(ps, item.getAutor());
        }

        if (isbn) {
          input(ps, item, "isbn", item.getIsbn());
        } else {
          Html.td(ps, item.getIsbn());
        }

        if (tamanho) {
          shortInput(ps, item, "tamanho", item.getTamanho());
        } else {
          Html.td(ps, item.getTamanho());
        }

        String tagsString = Join.join(" ", item.getTags());
        if (tags) {
          input(ps, item, "tags", tagsString);
        } else {
          Html.td(ps, tagsString);
        }
        ps.print("\n"); 
      }

      ps.println("</table>");

      ps.println("</form>");
      ps.println("<form action='modifyitems'>");

      printCheckboxesAndDropdown(ps, sortKey, paradeiro, toca, titulo, autor, isbn, tamanho, tags);

      ps.println("</body></html>");
      ps.flush();
      resp.getOutputStream().close();

    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    } finally {
      pm.close();
    }
  }

  private void paradeiroDropdown(PrintWriter ps, Item item, Long value, Collection<Member> members) {
    ps.printf("<td><select name='paradeiro-%s'><option value=''></option>",
        item.getId());

    for (Member member : members) {
      String selected = "";
      if (member.getId().equals(value)) {
        selected = "selected='true'";
      }
      ps.printf("<option value='%s' %s>%s</option>", member.getId(), selected, member.getCodigo());
    }
    
    ps.println("</select></td>");
  }

  private PrintWriter shortInput(PrintWriter ps, Item item, String key, String value) {
    return ps.printf("<td><input type='text' name='%s' value='%s' class='x-short'></td>", 
        key + "-" + item.getId(), value);
  }

  private PrintWriter input(PrintWriter ps, Item item, String key, String value) {
    return ps.printf("<td><input type='text' name='%s' value='%s' class='long'></td>", 
        key + "-" + item.getId(), value);
  }

  private void printCheckboxesAndDropdown(PrintWriter ps, Sort sortKey, 
      boolean paradeiro, boolean toca,
      boolean titulo, boolean autor, boolean isbn, boolean tamanho, boolean tags) {
    ps.println("<hr>");
    ps.println("<br>Ordem: " +
        "<select name='sort'>" +
        dropdown(sortKey, "PARADEIRO", "Paradeiro") +
        dropdown(sortKey, "TOCA", "Toca") +
        dropdown(sortKey, "TITULO", "Titulo") +
        dropdown(sortKey, "AUTOR", "Autor") +
        "</select>" +
    "");
    ps.println("Modificaveis: " +
        checkbox(paradeiro, "paradeiro", "Paradeiro") +
        checkbox(toca, "toca", "Toca") +
        checkbox(titulo, "titulo", "Titulo") +
        checkbox(autor, "autor", "Autor") +
        checkbox(isbn, "isbn", "ISBN") +
        checkbox(tamanho, "tamanho", "Tamanho") +
        checkbox(tags, "tags", "Tags") +
    "");
    ps.println("<input type='hidden' name='custom' value='true'>");
    ps.println("<input type='submit' value='Itens'>");

  }

  private String dropdown(Sort sortKey, String key, String label) {
    return "<option value='" + key + "'" + (sortKey.toString().equals(key) ? " selected" : "") + ">" + label + "</option>\n";
  }

  private String checkbox(boolean selected, String key, String label) {
    return "<input type='checkbox' name='" + key + "'" + (selected ? " checked" : "" ) + ">" + label + "</input>\n";
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    if (AdminOrDie.adminOrLogin(req, resp) == null) {
      return;
    }

    @SuppressWarnings("unchecked")
    Map<String,String[]> map = req.getParameterMap();
    PersistenceManager pm = PMF.get().getPersistenceManager();

    int toAdd = Integer.parseInt(req.getParameter("add"));
    for (int i=0; i<toAdd; i++) {
      pm.makePersistent(new Item());
    }

    Collection<Item> items = Queries.getUnSortedItems(pm);
    for (Item item : items) {
      Long id = item.getId();
      String key = "paradeiro-" + id;
      if (map.containsKey(key)) {
        item.setParadeiro(toLong(map, key));
      }
      key = "toca-" + id;
      if (map.containsKey(key)) {
        item.setToca(toString(map, key));
      }
      key = "titulo-" + id;
      if (map.containsKey(key)) {
        item.setTitulo(toString(map, key));
      }
      key = "autor-" + id;
      if (map.containsKey(key)) {
        item.setAutor(toString(map, key));
      }
      key = "tamanho-" + id;
      if (map.containsKey(key)) {
        item.setTamanho(toString(map, key));
      }
      key = "tags-" + id;
      if (map.containsKey(key)) {
        String[] tags = map.get(key)[0].split(" ");
        List<String> tagList = item.getTags();
        tagList.clear();
        for (String tag : tags) {
          item.addTag(tag);
        }
      }
      pm.makePersistent(item);
    }
    pm.close();
    resp.sendRedirect("/modifyitems" + (toAdd > 0 ? "?added=true" : ""));
  }

  private Long toLong(Map<String, String[]> map, String key) {
    String value = map.get(key)[0];
    if (value.length() == 0) {
      return null;
    }
    return Long.parseLong(value);
  }

  private String toString(Map<String, String[]> map, String key) {
    return map.get(key)[0];
  }
}