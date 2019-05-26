package com.ssplugins.shadow3.entity;

import com.ssplugins.shadow3.api.ShadowContext;
import com.ssplugins.shadow3.execute.Scope;
import com.ssplugins.shadow3.execute.Stepper;
import com.ssplugins.shadow3.parsing.TokenLine;
import com.ssplugins.shadow3.section.ShadowSection;

import java.util.List;

public abstract class ShadowEntity {
    
    private ShadowContext topContext;
    
    private Flow flow;
    private ShadowEntity previous;
    private ShadowEntity next;
    
    private TokenLine line;
    private ShadowEntity parent;
    
    private boolean inline;
    
    public ShadowEntity(TokenLine line, ShadowEntity parent) {
        this.line = line;
        this.parent = parent;
        flow = new Flow(this);
    }
    
    public abstract String getName();
    
    public abstract Object execute(Stepper stepper, Scope scope, List<Object> args);
    
    public abstract void addArgument(ShadowSection section);
    
    public abstract ShadowContext getInnerContext();
    
    ShadowEntity getPrevious() {
        return previous;
    }
    
    void setPrevious(ShadowEntity previous) {
        this.previous = previous;
    }
    
    ShadowEntity getNext() {
        return next;
    }
    
    void setNext(ShadowEntity next) {
        this.next = next;
    }
    
    protected void setTopContext(ShadowContext context) {
        this.topContext = context;
    }
    
    public ShadowContext getTopContext() {
        return topContext;
    }
    
    public Flow flow() {
        return flow;
    }
    
    public ShadowEntity getParent() {
        return parent;
    }
    
    public TokenLine getLine() {
        return line;
    }
    
    public boolean isInline() {
        return inline;
    }
    
    public void setInline(boolean inline) {
        this.inline = inline;
    }
    
}
