package com.sun.beans.decoder;

import com.sun.beans.finder.FieldFinder;
import java.lang.reflect.Field;

final class FieldElementHandler extends AccessorElementHandler
{
    private Class<?> type;
    
    @Override
    public void addAttribute(final String s, final String s2) {
        if (s.equals("class")) {
            this.type = this.getOwner().findClass(s2);
        }
        else {
            super.addAttribute(s, s2);
        }
    }
    
    @Override
    protected boolean isArgument() {
        return super.isArgument() && this.type != null;
    }
    
    @Override
    protected Object getContextBean() {
        return (this.type != null) ? this.type : super.getContextBean();
    }
    
    @Override
    protected Object getValue(final String s) {
        try {
            return getFieldValue(this.getContextBean(), s);
        }
        catch (final Exception ex) {
            this.getOwner().handleException(ex);
            return null;
        }
    }
    
    @Override
    protected void setValue(final String s, final Object o) {
        try {
            setFieldValue(this.getContextBean(), s, o);
        }
        catch (final Exception ex) {
            this.getOwner().handleException(ex);
        }
    }
    
    static Object getFieldValue(final Object o, final String s) throws IllegalAccessException, NoSuchFieldException {
        return findField(o, s).get(o);
    }
    
    private static void setFieldValue(final Object o, final String s, final Object o2) throws IllegalAccessException, NoSuchFieldException {
        findField(o, s).set(o, o2);
    }
    
    private static Field findField(final Object o, final String s) throws NoSuchFieldException {
        return (o instanceof Class) ? FieldFinder.findStaticField((Class<?>)o, s) : FieldFinder.findField(o.getClass(), s);
    }
}
