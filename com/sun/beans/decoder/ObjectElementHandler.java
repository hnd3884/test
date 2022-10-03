package com.sun.beans.decoder;

import java.beans.Expression;
import java.util.Locale;

class ObjectElementHandler extends NewElementHandler
{
    private String idref;
    private String field;
    private Integer index;
    private String property;
    private String method;
    
    @Override
    public final void addAttribute(final String s, final String s2) {
        if (s.equals("idref")) {
            this.idref = s2;
        }
        else if (s.equals("field")) {
            this.field = s2;
        }
        else if (s.equals("index")) {
            this.addArgument(this.index = Integer.valueOf(s2));
        }
        else if (s.equals("property")) {
            this.property = s2;
        }
        else if (s.equals("method")) {
            this.method = s2;
        }
        else {
            super.addAttribute(s, s2);
        }
    }
    
    @Override
    public final void startElement() {
        if (this.field != null || this.idref != null) {
            this.getValueObject();
        }
    }
    
    @Override
    protected boolean isArgument() {
        return true;
    }
    
    protected final ValueObject getValueObject(final Class<?> clazz, final Object[] array) throws Exception {
        if (this.field != null) {
            return ValueObjectImpl.create(FieldElementHandler.getFieldValue(this.getContextBean(), this.field));
        }
        if (this.idref != null) {
            return ValueObjectImpl.create(this.getVariable(this.idref));
        }
        final Object contextBean = this.getContextBean();
        String string;
        if (this.index != null) {
            string = ((array.length == 2) ? "set" : "get");
        }
        else if (this.property != null) {
            string = ((array.length == 1) ? "set" : "get");
            if (0 < this.property.length()) {
                string = string + this.property.substring(0, 1).toUpperCase(Locale.ENGLISH) + this.property.substring(1);
            }
        }
        else {
            string = ((this.method != null && 0 < this.method.length()) ? this.method : "new");
        }
        return ValueObjectImpl.create(new Expression(contextBean, string, array).getValue());
    }
}
