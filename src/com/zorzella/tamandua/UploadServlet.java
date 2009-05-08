package com.zorzella.tamandua;

import com.google.appengine.repackaged.com.google.common.base.Join;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

import com.ibm.icu.impl.duration.DateFormatter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

//    @SuppressWarnings("unchecked")
    Collection<Book> books = Queries.getUnSortedItems(pm);//(Collection<Book>)pm.newQuery(Book.class).execute();
    pm.deletePersistentAll(books);
    Collection<Member> members = Queries.getSortedMembers(pm);
    pm.deletePersistentAll(members);

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
      String titulo = dismangle(parsed.get(2));
      Book book = new Book(
          parsed.get(0), 
          parsed.get(1), 
          parsed.get(3), 
          titulo,
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

  private String dismangle(String string) {
    if (string.toLowerCase().endsWith(", a")) {
      return "A " + string.substring(0, string.length() - 3);
    }
    if (string.toLowerCase().endsWith(", o")) {
      return "O " + string.substring(0, string.length() - 3);
    }
    if (string.toLowerCase().endsWith(", um")) {
      return "Um " + string.substring(0, string.length() - 4);
    }
    if (string.toLowerCase().endsWith(", uma")) {
      return "Uma " + string.substring(0, string.length() - 5);
    }
    if (string.toLowerCase().endsWith(", as")) {
      return "As " + string.substring(0, string.length() - 4);
    }
    if (string.toLowerCase().endsWith(", os")) {
      return "Os " + string.substring(0, string.length() - 4);
    }
    if (string.toLowerCase().endsWith(", uns")) {
      return "Uns " + string.substring(0, string.length() - 5);
    }
    if (string.toLowerCase().endsWith(", umas")) {
      return "Umas " + string.substring(0, string.length() - 6);
    }
    return string;
  }

  private void persistMembers(
      HttpServletRequest req, 
      PrintWriter ps, 
      PersistenceManager pm) {
    for (String line : req.getParameter("members").split("\n|\r")) {
      if (line.length() == 0) {
        continue;
      }
      List<String> parsed = parseLine(line, 17);
      String codigo = parsed.get(0);
      String nome = parsed.get(1);
      String sobrenome = parsed.get(2);
      String email = parsed.get(4);
      String pai = parsed.get(5);
      String mae = parsed.get(6);
      String endereco = parsed.get(7);
      String cidade = parsed.get(8);
      String estado = parsed.get(9);
      String zip = parsed.get(10);
      String fone = parsed.get(11);
      String fone2 = parsed.get(12);
      Member member = new Member(
          codigo, 
          nome, 
          sobrenome, 
          getDate(parsed.get(3)), 
          email, 
          pai, 
          mae, 
          endereco,
          cidade, 
          estado, 
          zip, 
          fone, 
          fone2, 
          getInt(parsed.get(13)),
          getInt(parsed.get(14)),
          getBoolean(parsed.get(15)),
          getDate(parsed.get(16)));
      pm.makePersistent(member);
      ps.println(member);  
    }
  }

  private int getInt(String string) {
    if (string.startsWith("$")) {
      string = string.substring(1);
    }
    string = string.trim();
    if (string.length() == 0) {
      return 0;
    }
    return Integer.parseInt(string);
  }

  private Date getDate(String string) {
    
    if (string.trim().length() == 0) {
      return null;
    }
    
    String[] split = string.split("/");
    if (split.length == 2) {
      string = split[0] + "/01/" + split[1];
    }
    DateTimeFormatter fmt = org.joda.time.format.DateTimeFormat.shortDate();
    Locale locale = Locale.US;
    DateTimeZone timeZone = DateTimeZone.forID("America/Los_Angeles");

    DateTime result = fmt.withZone(timeZone).parseDateTime(string);

    return result.toDate();
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