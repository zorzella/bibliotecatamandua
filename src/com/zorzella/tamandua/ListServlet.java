package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.base.Join;

import com.zorzella.tamandua.Item.Type;

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

      Sort sortKey = Sort.TITULO;
      String[] temp = map.get("sort");
      if ((temp != null) && (temp.length == 1)) {
        sortKey = Sort.valueOf(temp[0]);
      }

      boolean onde = map.containsKey("onde");
      boolean showType = map.containsKey("type");
      boolean showTitulo = map.containsKey("titulo");
      boolean showAutor = map.containsKey("autor");
      boolean showTamanho = map.containsKey("tamanho");
      boolean showTags = map.containsKey("tags");
      boolean showBarcode = map.containsKey("barcode");
      boolean showIsbn = map.containsKey("isbn");

      if (!onde && !showType && !showTitulo && !showAutor && !showTamanho && !showTags) {
        onde = false;
        showType = true;
        showTitulo = true;
        showTamanho = true;
        showAutor = true;
        showTags = true;
      }

      ps.println("<form action='list'>");

      ps.println("<table>");

      if (onde) {
        ps.println("<th>Onde</th>");
      }

      if (showType) {
        ps.println("<th>Tipo</th>");
      }

      if (showTitulo) {
        ps.println("<th>Titulo</th>");
      }

      if (showAutor) {
        ps.println("<th>Autor</th>");
      }

      if (showTamanho) {
        ps.println("<th>Tamanho</th>");
      }

      if (showTags) {
        ps.println("<th>Tags</th>");
      }

      Collection<Item> sortedItems;

      switch (sortKey) {
        case TYPE: 
          sortedItems = Queries.getTypeSortedItems(pm);
          break;
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
      for (Item item : sortedItems) {
        if (item.getTitulo().trim().equals("")) {
          continue;
        }
        even = !even;
        if (even) {
          ps.printf("<tr class='a'>");
        } else {
          ps.printf("<tr class='b'>");
        }
        if (onde) {
          Long paradeiro = item.getParadeiro();
          String memberCodigo = "";
          if (paradeiro != null) {
            memberCodigo = Queries.getById(Member.class, pm, "id", paradeiro + "").getCodigo();
          }
          ps.printf("<td align='right'>%s</td>", memberCodigo + " " + item.getToca());
        }
        
        maybeShow(ps, showType, typeImage(item.getType()));
        maybeShow(ps, showTitulo, item.getTitulo());
        maybeShow(ps, showAutor, item.getAutor());
        maybeShow(ps, showTamanho, item.getTamanho());
        maybeShow(ps, showTags, Join.join(" ", item.getTags()));
        maybeShow(ps, showBarcode, item.getBarcode());
        maybeShow(ps, showIsbn, item.getIsbn());
        
        ps.print("\n"); 
      }

      ps.println("</table>");

      printCheckboxesAndDropdown(
          ps, 
          sortKey, 
          onde, 
          showType, 
          showTitulo, 
          showAutor, 
          showTamanho, 
          showTags,
          showBarcode,
          showIsbn);

      ps.println("</body></html>");
      ps.flush();
      resp.getOutputStream().close();

    } catch (RuntimeException e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
  }

  private void maybeShow(PrintWriter ps, boolean shouldShow, String content) {
    if (shouldShow) {
      ps.printf("<td>%s</td>", content);
    }
  }

  private String typeImage(Type type) {
    return String.format(
        "<img src='%s' alt='%s' title='%s'>", 
        typeImageName(type), 
        type.toString(),
        type.toString()); 
  }
  
  private String typeImageName(Type type) {
    switch (type) {
      case BOOK:
        return "book.png";
      case COMPUTER_GAME_CD:
        return "computer_game_cd.png";
      case MOVIE_DVD:
        return "movie_dvd.png";
      case MUSIC_CD:
        return "music_cd.png";
      default:
        throw new UnsupportedOperationException();
    }
  }

  private void printCheckboxesAndDropdown(PrintWriter ps, 
      Sort sortKey, 
      boolean onde,
      boolean type,
      boolean titulo,
      boolean autor, 
      boolean tamanho, 
      boolean tags,
      boolean showBarcode,
      boolean showIsbn
      ) {
    ps.println("<hr>");
    Html.printOrderDropDown(ps, sortKey);
    ps.println("Mostre: " +
        Html.checkbox(onde, "onde", "Onde (Paradeiro e Toca)") +
        Html.checkbox(type, "type", "Tipo") +
        Html.checkbox(titulo, "titulo", "Titulo") +
        Html.checkbox(autor, "autor", "Autor") +
        Html.checkbox(tamanho, "tamanho", "Tamanho") +
        Html.checkbox(tags, "tags", "Tags") +
        Html.checkbox(showBarcode, "barcode", "Barcode") +
        Html.checkbox(showIsbn, "isbn", "ISBN") +
    "");
    ps.println("<input type='submit' value='Livros'>");
  }
}