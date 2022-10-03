package com.sun.beans.decoder;

public abstract class ElementHandler
{
    private DocumentHandler owner;
    private ElementHandler parent;
    private String id;
    
    public final DocumentHandler getOwner() {
        return this.owner;
    }
    
    final void setOwner(final DocumentHandler owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Every element should have owner");
        }
        this.owner = owner;
    }
    
    public final ElementHandler getParent() {
        return this.parent;
    }
    
    final void setParent(final ElementHandler parent) {
        this.parent = parent;
    }
    
    protected final Object getVariable(final String s) {
        if (!s.equals(this.id)) {
            return (this.parent != null) ? this.parent.getVariable(s) : this.owner.getVariable(s);
        }
        final ValueObject valueObject = this.getValueObject();
        if (valueObject.isVoid()) {
            throw new IllegalStateException("The element does not return value");
        }
        return valueObject.getValue();
    }
    
    protected Object getContextBean() {
        if (this.parent != null) {
            final ValueObject valueObject = this.parent.getValueObject();
            if (!valueObject.isVoid()) {
                return valueObject.getValue();
            }
            throw new IllegalStateException("The outer element does not return value");
        }
        else {
            final Object owner = this.owner.getOwner();
            if (owner != null) {
                return owner;
            }
            throw new IllegalStateException("The topmost element does not have context");
        }
    }
    
    public void addAttribute(final String s, final String id) {
        if (s.equals("id")) {
            this.id = id;
            return;
        }
        throw new IllegalArgumentException("Unsupported attribute: " + s);
    }
    
    public void startElement() {
    }
    
    public void endElement() {
        final ValueObject valueObject = this.getValueObject();
        if (!valueObject.isVoid()) {
            if (this.id != null) {
                this.owner.setVariable(this.id, valueObject.getValue());
            }
            if (this.isArgument()) {
                if (this.parent != null) {
                    this.parent.addArgument(valueObject.getValue());
                }
                else {
                    this.owner.addObject(valueObject.getValue());
                }
            }
        }
    }
    
    public void addCharacter(final char c) {
        if (c != ' ' && c != '\n' && c != '\t' && c != '\r') {
            throw new IllegalStateException("Illegal character with code " + (int)c);
        }
    }
    
    protected void addArgument(final Object o) {
        throw new IllegalStateException("Could not add argument to simple element");
    }
    
    protected boolean isArgument() {
        return this.id == null;
    }
    
    protected abstract ValueObject getValueObject();
}
