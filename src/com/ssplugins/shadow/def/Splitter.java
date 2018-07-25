package com.ssplugins.shadow.def;

import com.ssplugins.shadow.ParseContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public interface Splitter {
	
	String[] split(String content, ParseContext context);
	
	static Splitter replacerSplit() {
		return singleArg();
	}
	
	static Splitter evalSplit() {
		return (content, context) -> {
			List<String> list = new ArrayList<>();
			Matcher m = EvalSymbolDef.PATTERN.matcher(content);
			while (m.find()) {
				list.add(m.group());
			}
			return list.toArray(new String[list.size()]);
		};
	}
	
	static Splitter singleArg() {
		return (content, context) -> {
			if (content.isEmpty()) return new String[0];
			return new String[] {content};
		};
	}
	
}