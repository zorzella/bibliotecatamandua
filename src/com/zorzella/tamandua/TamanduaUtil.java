package com.zorzella.tamandua;

public class TamanduaUtil {

  public static String nome(Member member) {
    String result = "";
  
    if ((TamanduaUtil.empty(member.getPai())) && (TamanduaUtil.empty(member.getMae()))) {
      result += member.getNome();
    }
  
    if ((!TamanduaUtil.empty(member.getPai())) && (!TamanduaUtil.empty(member.getMae()))) {
      result += member.getPai() + "/" + member.getMae();
    } else if (!TamanduaUtil.empty(member.getPai())) {
      result += member.getPai();
    } else if (!TamanduaUtil.empty(member.getMae())) {
      result += member.getMae();
    }
    return result += " (" + member.getNome() + " " + member.getSobrenome() + ")";
  }

  static boolean empty(String string) {
    return string == null || string.trim().length() == 0;
  }

}
