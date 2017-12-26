package com.ssplugins.shadow2;

import com.ssplugins.shadow2.common.ClassFinder;
import com.ssplugins.shadow2.common.Parser;
import com.ssplugins.shadow2.def.*;

import java.util.List;

public abstract class ShadowAPI {
	
	public void peekLines(List<String> lines) {}
	
	public List<Parser> registerLineParsers() {
		return null;
	}
	
	public List<ClassFinder> registerClassFinders() {
		return null;
	}
	
	public List<ExpressionDef> registerExpressions() {
		return null;
	}
	
	public List<KeywordDef> registerKeywords() {
		return null;
	}
	
	public List<BlockDef> registerBlocks() {
		return null;
	}
	
	public List<ReplacerDef> registerReplacers() {
		return null;
	}
	
	public List<EvalSymbolDef> registerEvalSymbols() {
		return null;
	}

}