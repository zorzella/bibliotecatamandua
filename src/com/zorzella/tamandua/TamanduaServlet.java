package com.zorzella.tamandua;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TamanduaServlet extends HttpServlet {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(TamanduaServlet.class.getName());

  private static final String HTML = 
    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n" +
    "<html>\n" +
    "  <head>\n" +
    "    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n" +
    "    <title>Tamandua</title>\n" +
    "    <script type=\"text/javascript\" language=\"javascript\" " +
    "      src=\"com.zorzella.tamandua.gwt.tamandua/com.zorzella.tamandua.gwt.tamandua.nocache.js\"></script>\n" +
    "    <LINK REL=StyleSheet HREF=\"red.css\" TYPE=\"text/css\"></LINK>\n" +
    "  </head>\n" +
    "  <body>\n" +
    "    <iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" tabIndex=\"-1\" " +
    "      style=\"position:absolute;width:0;height:0;border:0\"></iframe>\n" +
    "   <div id=\"list\"></div>\n" +
    "  </body>\n" +
    "</html>";
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    if (AdminOrDie.adminOrLogin(req, resp) == null){
      return;
    }

    resp.setContentType("text/html");
    resp.setCharacterEncoding(Constants.encoding);
    PrintWriter ps = new PrintWriter(
        new OutputStreamWriter(resp.getOutputStream(), Constants.encoding));

    Html.htmlHeadBody(ps);
    ps.println(HTML);
    ps.println("</html></body>");

    ps.flush();
    resp.getOutputStream().close();
  }
}