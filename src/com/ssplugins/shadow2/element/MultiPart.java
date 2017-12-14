package com.ssplugins.shadow2.element;

import com.ssplugins.shadow2.ShadowTools;

import java.util.List;

public final class MultiPart extends ShadowSection {
	
	private List<ShadowSection> parts;
	
	public MultiPart(List<ShadowSection> parts) {
		this.parts = parts;
	}
	
	@Override
	public String toString() {
		return ShadowTools.asString(parts);
	}
	
}
