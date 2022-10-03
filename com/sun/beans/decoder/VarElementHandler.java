package com.sun.beans.decoder;

final class VarElementHandler extends ElementHandler
{
    private ValueObject value;
    
    @Override
    public void addAttribute(final String s, final String s2) {
        if (s.equals("idref")) {
            this.value = ValueObjectImpl.create(this.getVariable(s2));
        }
        else {
            super.addAttribute(s, s2);
        }
    }
    
    @Override
    protected ValueObject getValueObject() {
        if (this.value == null) {
            throw new IllegalArgumentException("Variable name is not set");
        }
        return this.value;
    }
}
