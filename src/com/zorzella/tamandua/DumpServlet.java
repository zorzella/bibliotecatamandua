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

public class DumpServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(DumpServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    if (AdminOrDie.adminOrLogin(req, resp) == null) {
      return;
    }

    resp.setContentType("text/plain");
    resp.setCharacterEncoding(Constants.encoding);
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {

      Collection<Item> items = Queries.getUnSortedItems(pm);

      for (Item item : items) {
        ps.println(toDebugString(item));
      }

      Collection<Loan> loans = Queries.getAll(Loan.class, pm);

      for (Loan loan : loans) {
        ps.println(loan.toString());  
      }

      ps.flush();
      resp.getOutputStream().close();

    } catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    } finally {
      pm.close();
    }
  }
  
  private static String toDebugString(Item item) {
    StringBuilder result = new StringBuilder("id:" + item.getId() + " ");

    if (item.isEspecial()) {
      result.append("<especial> ");
    }
    maybeAdd(result, "paradeiro", item.getParadeiro() + "");
    maybeAdd(result, "toca", item.getToca());
    maybeAdd(result, "isbn", item.getIsbn());
    maybeAdd(result, "titulo", item.getTitulo());
    maybeAdd(result, "autor", item.getAutor());
    maybeAdd(result, "tamanho", item.getTamanho());
    maybeAdd(result, "desde", Dates.dateToString(item.getDesde()));
    result.append("tags:" + item.getTags());
    return result.toString();
  }
  
  private static void maybeAdd(StringBuilder result, String key, String value) {
    if (value.trim().length() > 0) {
      result.append(key + ":[" + value + "] ");
    }
  }
}