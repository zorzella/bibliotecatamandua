package com.zorzella.tamandua;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModifyMembersServlet extends HttpServlet {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ModifyMembersServlet.class.getName());

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

      boolean codigo = map.containsKey("codigo");
      boolean nome = map.containsKey("nome");
      boolean sobrenome = map.containsKey("sobrenome");
      boolean nascimento = map.containsKey("nascimento");
      boolean email = map.containsKey("email");
      boolean pai = map.containsKey("pai");
      boolean mae = map.containsKey("mae");
      boolean dolares = map.containsKey("dolares");
      boolean livros = map.containsKey("livros");
      boolean fone = map.containsKey("fone");
      boolean fone2 = map.containsKey("fone2");
      boolean lastContacted = map.containsKey("lastContacted");
      boolean confirmado = map.containsKey("confirmado");
      boolean desde = map.containsKey("desde");
      boolean endereco = map.containsKey("endereco");
      boolean cidade = map.containsKey("cidade");
      boolean estado = map.containsKey("estado");
      boolean zip = map.containsKey("zip");

      if (!map.containsKey("custom")) {
        codigo = true;
        nome = true;
        sobrenome = true;
        nascimento = true;
        email = true;
        pai = true;
        mae = true;
      }

      if (map.containsKey("added")) {
        codigo = true;
        nascimento = true;
      }

      ps.println("<form action='modifymembers' method='post'>");

      ps.println("<input type='text' value='0' name='add'>");
      ps.println("<input type='submit' value='Change'>");
      ps.println("<table>");

      ps.println("<th>Codigo</th>");
      ps.println("<th>Nome</th>");
      ps.println("<th>Sobrenome</th>");
      ps.println("<th>Nascimento</th>");
      ps.println("<th>Email</th>");
      ps.println("<th>Pai</th>");
      ps.println("<th>Mae</th>");
      ps.println("<th>Dolares</th>");
      ps.println("<th>Livros</th>");
      if (fone) {
        ps.println("<th>Fone</th>");
      }
      if (fone2) {
        ps.println("<th>Fone2</th>");
      }
      if (lastContacted) { 
        ps.println("<th>Last Contacted</th>");
      }
      ps.println("<th>Conf</th>");
      ps.println("<th>Desde</th>");
      if (endereco) {
        ps.println("<th>Endereco</th>");
      }
      if (cidade) {
        ps.println("<th>Cidade</th>");
      }
      if (estado) {
        ps.println("<th>Estado</th>");
      }
      if (zip) {
        ps.println("<th>Zip</th>");
      }

      Collection<Member> sortedMembers = Queries.getSortedMembersWithBlanks(pm);

      boolean even = false;
      for (Member member : sortedMembers) {
        even = !even;
        if (even) {
          ps.printf("<tr class='a'>");
        } else {
          ps.printf("<tr class='b'>");
        }
        choose(ps, false, codigo, member, true, member.getCodigo(), "codigo");
        choose(ps, false, nome, member, true, member.getNome(), "nome");
        choose(ps, false, sobrenome, member, true, member.getSobrenome(), "sobrenome");
        choose(ps, false, nascimento, member, true, Dates.dateToString(member.getNascimento()), "nascimento");
        choose(ps, true, email, member, true, member.getEmail(), "email");
        choose(ps, true, pai, member, true, member.getPai(), "pai");
        choose(ps, true, mae, member, true, member.getMae(), "mae");
        choose(ps, false, dolares, member, true, member.getDolares() + "", "dolares");
        choose(ps, false, livros, member, true, member.getLivrosDoados() + "", "livros");
        if (fone) {
          choose(ps, true, fone, member, true, member.getFone(), "fone");
        }
        if (fone2) {
          choose(ps, false, fone2, member, true, member.getFone2(), "fone2");
        }
        if (lastContacted) {
          choose(ps, true, lastContacted, member, true, Dates.dateToString(member.getLastContacted()), "lastContacted");
        }
        confirmado(ps, false, confirmado, member, true, member.isConfirmado(), "confirmado");
        choose(ps, true, desde, member, true, Dates.dateToString(member.getDesde()), "desde");
        if (endereco) {
          choose(ps, true, endereco, member, true, member.getEndereco() + "", "endereco");
        }
        if (cidade) {
          choose(ps, true, cidade, member, true, member.getCidade() + "", "cidade");
        }
        if (estado) {
          choose(ps, false, estado, member, true, member.getEstado() + "", "estado");
        }
        if (zip) {
          choose(ps, false, zip, member, true, member.getZip() + "", "zip");
        }
        ps.print("\n"); 
      }

      ps.println("</table>");

      ps.println("</form>");
      ps.println("<form action='modifymembers'>");

      printCheckboxesAndDropdown(ps, codigo, nome, sobrenome, nascimento, 
          email, pai, mae, dolares, livros, fone, fone2, lastContacted, confirmado, desde, 
          endereco, cidade, estado, zip);

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

  private String confirmadoHtml(Member member) {
    return member.isConfirmado()  
        ? ""
        : "<img src='bang.png'>";
  }

  private void confirmado(
      PrintWriter ps, 
      boolean shortInput, 
      boolean editable, 
      Member member, 
      boolean alignRight, 
      boolean content, 
      String key) {
    if (editable) {
      if (shortInput) {
        input(ps, member, key, content + "");
      } else {
        shortInput(ps, member, key, content + "");
      }
    } else {
      if (alignRight) {
        Html.tdRight(ps, confirmadoHtml(member));
      } else {
        Html.td(ps, confirmadoHtml(member));
      }
    }
  }

  private void choose(
      PrintWriter ps, 
      boolean shortInput, 
      boolean editable, 
      Member member, 
      boolean alignRight, 
      String content, 
      String key) {
    if (editable) {
      if (shortInput) {
        input(ps, member, key, content);
      } else {
        shortInput(ps, member, key, content);
      }
    } else {
      if (alignRight) {
        Html.tdRight(ps, content);
      } else {
        Html.td(ps, content);
      }
    }
  }

  private PrintWriter shortInput(PrintWriter ps, Member member, String key, String value) {
    return ps.printf("<td><input type='text' name='%s' value='%s' class='short'></td>", 
        key + "-" + member.getId() , value);
  }

  private PrintWriter input(PrintWriter ps, Member member, String key, String value) {
    return ps.printf("<td><input type='text' name='%s' value='%s' class='medium'></td>", 
        key + "-" + member.getId(), value);
  }

  private void printCheckboxesAndDropdown(PrintWriter ps, 
      boolean codigo,
      boolean nome, 
      boolean sobrenome, 
      boolean nascimento, 
      boolean email, 
      boolean pai, 
      boolean mae, 
      boolean dolares, 
      boolean livros, 
      boolean fone, 
      boolean fone2, 
      boolean lastContacted, 
      boolean confirmado, 
      boolean desde, 
      boolean endereco, 
      boolean cidade, 
      boolean estado, 
      boolean zip
  ) {
    ps.println("<hr>");
    ps.println("Modifique: " +
        checkbox(codigo, "codigo", "Codigo") +
        checkbox(nome, "nome", "Nome") +
        checkbox(sobrenome, "sobrenome", "Sobrenome") +
        checkbox(nascimento, "nascimento", "nascimento") + 
        checkbox(email, "email", "email") + 
        checkbox(pai, "pai", "pai") + 
        checkbox(mae, "mae", "mae") + 
        checkbox(dolares, "dolares", "dolares") + 
        checkbox(livros, "livros", "livros") + 
        checkbox(fone, "fone", "fone") + 
        checkbox(fone2, "fone2", "fone2") + 
        checkbox(desde, "lastContacted", "lastContacted") + 
        checkbox(confirmado, "confirmado", "confirmado") + 
        checkbox(desde, "desde", "desde") + 
        checkbox(endereco, "endereco", "endereco") + 
        checkbox(cidade, "cidade", "cidade") + 
        checkbox(estado, "estado", "estado") + 
        checkbox(zip, "zip", "zip") +
    "");
    ps.println("<input type='hidden' name='custom' value='true'>");
    ps.println("<input type='submit' value='Membros'>");

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
      pm.makePersistent(new Member(""));
    }

    Collection<Member> members = Queries.getSortedMembersWithBlanks(pm);
    for (Member member : members) {
      String id = member.getId() + "";
      String 
      key = "codigo-" + id;
      if (map.containsKey(key)) {
        member.setCodigo(toString(map, key));
      }
      key = "nome-" + id;
      if (map.containsKey(key)) {
        member.setNome(toString(map, key));
      }
      key = "sobrenome-" + id;
      if (map.containsKey(key)) {
        member.setSobrenome(toString(map, key));
      }
      key = "nascimento-" + id;
      if (map.containsKey(key)) {
        member.setNascimento(toDate(map, key));
      } 
      key = "email-" + id;
      if (map.containsKey(key)) {
        member.setEmail(toString(map, key));
      } 
      key = "pai-" + id;
      if (map.containsKey(key)) {
        member.setPai(toString(map, key));
      } 
      key = "mae-" + id;
      if (map.containsKey(key)) {
        member.setMae(toString(map, key));
      } 
      key = "dolares-" + id;
      if (map.containsKey(key)) {
        member.setDolares(toInt(map, key));
      } 
      key = "livros-" + id;
      if (map.containsKey(key)) {
        member.setLivrosDoados(toInt(map, key));
      } 
      key = "fone-" + id;
      if (map.containsKey(key)) {
        member.setFone(toString(map, key));
      } 
      key = "fone2-" + id;
      if (map.containsKey(key)) {
        member.setFone2(toString(map, key));
      } 
      key = "desde-" + id;
      if (map.containsKey(key)) {
        member.setDesde(toDate(map, key));
      } 
      key = "confirmado-" + id;
      if (map.containsKey(key)) {
        member.setConfirmado(toBoolean(map, key));
      } 
      key = "endereco-" + id;
      if (map.containsKey(key)) {
        member.setEndereco(toString(map, key));
      } 
      key = "cidade-" + id;
      if (map.containsKey(key)) {
        member.setCidade(toString(map, key));
      } 
      key = "estado-" + id;
      if (map.containsKey(key)) {
        member.setEstado(toString(map, key));
      } 
      key = "zip-" + id;
      if (map.containsKey(key)) {
        member.setZip(toString(map, key));
      }
      pm.makePersistent(member);
    }
    pm.close();
    resp.sendRedirect("/modifymembers" + (toAdd > 0 ? "?added=true" : ""));
  }

  private boolean toBoolean(Map<String, String[]> map, String key) {
    return Boolean.parseBoolean(map.get(key)[0]);
  }

  private String toString(Map<String, String[]> map, String key) {
    return map.get(key)[0];
  }

  private Date toDate(Map<String, String[]> map, String key) {
    String string = map.get(key)[0];
    if (string.trim().length() == 0) {
      return null;
    }
    String[] split = string.split("-");
    return new DateTime(
        Integer.parseInt(split[0]),
        Integer.parseInt(split[1]),
        Integer.parseInt(split[2]),
        0, 0, 0, 0).toDate();
  }

  private int toInt(Map<String, String[]> map, String key) {
    return Integer.parseInt(map.get(key)[0]);
  }
}