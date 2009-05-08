package com.zorzella.tamandua;


import org.joda.time.DateTime;
import org.joda.time.DateTimeField;

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

  private static final Logger log = Logger.getLogger(ModifyMembersServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

	  AdminOrDie.adminOrDie(req, resp);
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
      boolean desde = map.containsKey("desde");
      boolean confirmado = map.containsKey("confirmado");
      boolean endereco = map.containsKey("endereco");
      boolean cidade = map.containsKey("cidade");
      boolean estado = map.containsKey("estado");
      boolean zip = map.containsKey("zip");
      
      if (!codigo && !nome && !sobrenome && !nascimento && !email && !pai && !mae && 
          !dolares && !livros && !fone && !fone2 && !desde && !confirmado && 
          !endereco && !cidade && !estado && !zip
          ) {
        nome = true;
        sobrenome = true;
        email = true;
        pai = true;
        mae = true;
      }
      
      ps.println("<form action='modifymembers' method='post'>");
      
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
      ps.println("<th>Fone</th>");
      ps.println("<th>Fone2</th>");
      ps.println("<th>Desde</th>");
      ps.println("<th>Conf</th>");
      ps.println("<th>Endereco</th>");
      ps.println("<th>Cidade</th>");
      ps.println("<th>Estado</th>");
      ps.println("<th>Zip</th>");

      Collection<Member> sortedMembers = Queries.getSortedMembers(pm);
      
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
        choose(ps, true, fone, member, true, member.getFone(), "fone");
        choose(ps, false, fone2, member, true, member.getFone2(), "fone2");
        choose(ps, true, desde, member, true, Dates.dateToString(member.getDesde()), "desde");
        choose(ps, false, confirmado, member, true, member.isConfirmado() + "", "confirmado");
        choose(ps, true, endereco, member, true, member.getEndereco() + "", "endereco");
        choose(ps, true, cidade, member, true, member.getCidade() + "", "cidade");
        choose(ps, false, estado, member, true, member.getEstado() + "", "estado");
        choose(ps, false, zip, member, true, member.getZip() + "", "zip");
        ps.print("\n"); 
      }

      ps.println("</table>");

      ps.println("</form>");
      ps.println("<form action='modifymembers'>");

      printCheckboxesAndDropdown(ps, nome, sobrenome, nascimento, 
          email, pai, mae, dolares, livros, fone, fone2, desde, confirmado, 
          endereco, cidade, estado, zip);

      ps.println("</body></html>");
      ps.flush();
      resp.getOutputStream().close();

    } catch (RuntimeException e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
  }

  private void choose(
      PrintWriter ps, 
      boolean shortInput, 
      boolean nome, 
      Member member, 
      boolean alignRight, 
      String content, 
      String key) {
    if (nome) {
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
        key + "-" + member.getCodigo(), value);
  }

  private PrintWriter input(PrintWriter ps, Member member, String key, String value) {
    return ps.printf("<td><input type='text' name='%s' value='%s' class='medium'></td>", 
        key + "-" + member.getCodigo(), value);
  }

  private void printCheckboxesAndDropdown(PrintWriter ps, 
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
      boolean desde, 
      boolean confirmado, 
      boolean endereco, 
      boolean cidade, 
      boolean estado, 
      boolean zip
      ) {
    ps.println("<hr>");
    ps.println("Mostre: " +
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
        checkbox(desde, "desde", "desde") + 
        checkbox(confirmado, "confirmado", "confirmado") + 
        checkbox(endereco, "endereco", "endereco") + 
        checkbox(cidade, "cidade", "cidade") + 
        checkbox(estado, "estado", "estado") + 
        checkbox(zip, "zip", "zip") +
          "");
    ps.println("<input type='submit' value='Membros'>");

  }

  private String checkbox(boolean selected, String key, String label) {
    return "<input type='checkbox' name='" + key + "'" + (selected ? " checked" : "" ) + ">" + label + "</input>\n";
  }
  
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

	  AdminOrDie.adminOrDie(req, resp);

	@SuppressWarnings("unchecked")
    Map<String,String[]> map = req.getParameterMap();
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Collection<Member> items = Queries.getSortedMembers(pm);
    for (Member item : items) {
      String codigo = item.getCodigo();
      String key = "nome-" + codigo;
      if (map.containsKey(key)) {
        item.setNome(toString(map, key));
      }
      key = "sobrenome-" + codigo;
      if (map.containsKey(key)) {
        item.setSobrenome(toString(map, key));
      }
      key = "nascimento-" + codigo;
      if (map.containsKey(key)) {
        item.setNascimento(toDate(map, key));
      } 
      key = "email-" + codigo;
      if (map.containsKey(key)) {
        item.setEmail(toString(map, key));
      } 
      key = "pai-" + codigo;
      if (map.containsKey(key)) {
        item.setPai(toString(map, key));
      } 
      key = "mae-" + codigo;
      if (map.containsKey(key)) {
        item.setMae(toString(map, key));
      } 
      key = "dolares-" + codigo;
      if (map.containsKey(key)) {
        item.setDolares(toInt(map, key));
      } 
      key = "livros-" + codigo;
      if (map.containsKey(key)) {
        item.setLivrosDoados(toInt(map, key));
      } 
      key = "fone-" + codigo;
      if (map.containsKey(key)) {
        item.setFone(toString(map, key));
      } 
      key = "fone2-" + codigo;
      if (map.containsKey(key)) {
        item.setFone2(toString(map, key));
      } 
      key = "desde-" + codigo;
      if (map.containsKey(key)) {
        item.setDesde(toDate(map, key));
      } 
      key = "confirmado-" + codigo;
      if (map.containsKey(key)) {
        item.setConfirmado(toBoolean(map, key));
      } 
      key = "endereco-" + codigo;
      if (map.containsKey(key)) {
        item.setEndereco(toString(map, key));
      } 
      key = "cidade-" + codigo;
      if (map.containsKey(key)) {
        item.setCidade(toString(map, key));
      } 
      key = "estado-" + codigo;
      if (map.containsKey(key)) {
        item.setEstado(toString(map, key));
      } 
      key = "zip-" + codigo;
      if (map.containsKey(key)) {
        item.setZip(toString(map, key));
      }
      pm.makePersistent(item);
    }
    pm.close();
    resp.sendRedirect("/modifymembers");
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