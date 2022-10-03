package com.sun.beans.decoder;

abstract class AccessorElementHandler extends ElementHandler
{
    private String name;
    private ValueObject value;
    
    @Override
    public void addAttribute(final String s, final String name) {
        if (s.equals("name")) {
            this.name = name;
        }
        else {
            super.addAttribute(s, name);
        }
    }
    
    @Override
    protected final void addArgument(final Object o) {
        if (this.value != null) {
            throw new IllegalStateException("Could not add argument to evaluated element");
        }
        this.setValue(this.name, o);
        this.value = ValueObjectImpl.VOID;
    }
    
    @Override
    protected final ValueObject getValueObject() {
        if (this.value == null) {
            this.value = ValueObjectImpl.create(this.getValue(this.name));
        }
        return this.value;
    }
    
    protected abstract Object getValue(final String p0);
    
    protected abstract void setValue(final String p0, final Object p1);
}
