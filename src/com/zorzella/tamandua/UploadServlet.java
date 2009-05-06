package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.collect.Lists;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(UploadServlet.class.getName());

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    AdminOrDie.adminOrDie(req, resp);

    resp.setCharacterEncoding(Constants.encoding);
    resp.setContentType("text/plain");
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

    PersistenceManager pm = PMF.get().getPersistenceManager();

    @SuppressWarnings("unchecked")
    Collection<Book> books = (Collection<Book>)pm.newQuery(Book.class).execute();
    pm.deletePersistentAll(books);

    try {
      ps.println("***** Books *****");
      persistBooks(req, ps, pm);
      ps.println("***** Members *****");
      persistMembers(req, ps, pm);
    } catch (RuntimeException e) {
      e.printStackTrace();
    } finally {
      ps.flush();
      resp.getOutputStream().close();
      pm.close();
    }
  }

  private void persistBooks(
      HttpServletRequest req, 
      PrintWriter ps, 
      PersistenceManager pm) {
    for (String line : req.getParameter("books").split("\n|\r")) {
      if (line.length() == 0) {
        continue;
      }
      List<String> parsed = parseLine(line, 9);
      
      boolean pagGrossa = getBoolean(parsed.get(5));
      boolean rima = getBoolean(parsed.get(7));
      boolean especial = getBoolean(parsed.get(8));
      Book book = new Book(
          parsed.get(0), 
          parsed.get(1), 
          parsed.get(3), 
          parsed.get(2),
          parsed.get(4),
          especial,
          parsed.get(6));
      
      if (pagGrossa) {
        book.addTag("pagina-grossa");
      }

      if (rima) {
        book.addTag("rima");
      }
      
      pm.makePersistent(book);
      ps.println(book);  
    }
  }

  private void persistMembers(
      HttpServletRequest req, 
      PrintWriter ps, 
      PersistenceManager pm) {
    for (String line : req.getParameter("members").split("\n|\r")) {
      if (line.length() == 0) {
        continue;
      }
      List<String> parsed = parseLine(line, 12);
      Member member = new Member(
          parsed.get(0), 
          parsed.get(1), 
          parsed.get(2), 
          getDate(parsed.get(3)), 
          parsed.get(4), 
          parsed.get(5), 
          parsed.get(6), 
          parsed.get(7),
          getInt(parsed.get(8)),
          getInt(parsed.get(9)),
          getBoolean(parsed.get(10)),
          getDate(parsed.get(11)));
      pm.makePersistent(member);
      ps.println(member);  
    }
  }

  private int getInt(String string) {
    // TODO
    return 0;
  }

  private Date getDate(String string) {
    return null;
    //    return new DateFormat().parse(string.replace("/", "/01/"));
  }

  private static boolean getBoolean(String string) {
    return string.trim().length() > 0;
  }

  static List<String> parseLine(String line, int numOfFields) {
    List<String> fields = Lists.newArrayList();

    for (int i=0; i<numOfFields; i++) {
      int nextComma = line.indexOf(',');

      if (nextComma == -1) {
        nextComma = line.length();
      }

      if (line.startsWith("\"")) {
        int nextQuote = line.indexOf('"', 1);
        fields.add(line.substring(1, nextQuote));
        nextQuote++;
        if (nextQuote != line.length()) {
          nextQuote++;
        }
        line = line.substring(nextQuote);
      } else {
        fields.add(line.substring(0, nextComma));
        if (nextComma != line.length()) {
          nextComma++;
        }
        line = line.substring(nextComma);
      }
    }
    return fields;
  }
}