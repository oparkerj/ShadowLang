package com.ssplugins.shadow2.element;

public abstract class ShadowSection {
	
	public boolean isPlain() {
		return this instanceof Plain;
	}
	
	public boolean isReplacer() {
		return this instanceof Replacer;
	}
	
	public boolean isMultiPart() {
		return this instanceof MultiPart;
	}
	
	public boolean isEvalGroup() {
		return this instanceof EvalGroup;
	}
	
	public boolean isLazyReplacer() {
		return this instanceof LazyReplacers;
	}
	
	public Plain asPlain() {
		if (isPlain()) {
			return (Plain) this;
		}
		throw new IllegalStateException("Object is not Plain.");
	}
	
	public Replacer asReplacer() {
		if (isReplacer()) {
			return (Replacer) this;
		}
		throw new IllegalStateException("Object is not Replacer.");
	}
	
	public MultiPart asMultiPart() {
		if (isMultiPart()) {
			return (MultiPart) this;
		}
		throw new IllegalStateException("Object is not MultiPart.");
	}
	
	public EvalGroup asEvalGroup() {
		if (isEvalGroup()) {
			return (EvalGroup) this;
		}
		throw new IllegalStateException("Object is not EvalGroup.");
	}
	
	public LazyReplacers asLazyReplacer() {
		if (isLazyReplacer()) {
			return (LazyReplacers) this;
		}
		throw new IllegalStateException("Object is not LazyReplacers");
	}

}
