package com.zorzella.tamandua;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

public class Members {

	public static Map<Long, String> getMap(Collection<Member> members) {
	    Map<Long, String> result = Maps.newHashMap();
	    for (Member member : members) {
	        result.put(member.getId(), member.getCodigo());
	    }
	    return result;
	}
}
