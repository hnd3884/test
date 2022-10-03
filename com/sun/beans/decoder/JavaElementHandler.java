package com.sun.beans.decoder;

import java.beans.XMLDecoder;

final class JavaElementHandler extends ElementHandler
{
    private Class<?> type;
    private ValueObject value;
    
    @Override
    public void addAttribute(final String s, final String s2) {
        if (!s.equals("version")) {
            if (s.equals("class")) {
                this.type = this.getOwner().findClass(s2);
            }
            else {
                super.addAttribute(s, s2);
            }
        }
    }
    
    @Override
    protected void addArgument(final Object o) {
        this.getOwner().addObject(o);
    }
    
    @Override
    protected boolean isArgument() {
        return false;
    }
    
    @Override
    protected ValueObject getValueObject() {
        if (this.value == null) {
            this.value = ValueObjectImpl.create(this.getValue());
        }
        return this.value;
    }
    
    private Object getValue() {
        Object o = this.getOwner().getOwner();
        if (this.type == null || this.isValid(o)) {
            return o;
        }
        if (o instanceof XMLDecoder) {
            o = ((XMLDecoder)o).getOwner();
            if (this.isValid(o)) {
                return o;
            }
        }
        throw new IllegalStateException("Unexpected owner class: " + ((XMLDecoder)o).getClass().getName());
    }
    
    private boolean isValid(final Object o) {
        return o == null || this.type.isInstance(o);
    }
}
