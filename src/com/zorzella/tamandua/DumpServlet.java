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

    AdminOrDie.adminOrDie(req, resp);
    
    resp.setContentType("text/plain");
    resp.setCharacterEncoding(Constants.encoding);
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {

      Collection<Book> items = Queries.getUnSortedItems(pm);

      for (Book item : items) {
        ps.println(item.toDebugString());  
      }

      Collection<Loan> loans = Queries.getAll(Loan.class, pm);

      for (Loan loan : loans) {
        ps.println(loan.toString());  
      }

      ps.flush();
      resp.getOutputStream().close();

    } catch (RuntimeException e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
  }
}