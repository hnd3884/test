package com.sun.beans.decoder;

public class StringElementHandler extends ElementHandler
{
    private StringBuilder sb;
    private ValueObject value;
    
    public StringElementHandler() {
        this.sb = new StringBuilder();
        this.value = ValueObjectImpl.NULL;
    }
    
    @Override
    public final void addCharacter(final char c) {
        if (this.sb == null) {
            throw new IllegalStateException("Could not add chararcter to evaluated string element");
        }
        this.sb.append(c);
    }
    
    @Override
    protected final void addArgument(final Object o) {
        if (this.sb == null) {
            throw new IllegalStateException("Could not add argument to evaluated string element");
        }
        this.sb.append(o);
    }
    
    @Override
    protected final ValueObject getValueObject() {
        if (this.sb != null) {
            try {
                this.value = ValueObjectImpl.create(this.getValue(this.sb.toString()));
            }
            catch (final RuntimeException ex) {
                this.getOwner().handleException(ex);
            }
            finally {
                this.sb = null;
            }
        }
        return this.value;
    }
    
    protected Object getValue(final String s) {
        return s;
    }
}
