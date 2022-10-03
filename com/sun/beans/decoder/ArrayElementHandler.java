package com.sun.beans.decoder;

import java.lang.reflect.Array;

final class ArrayElementHandler extends NewElementHandler
{
    private Integer length;
    
    @Override
    public void addAttribute(final String s, final String s2) {
        if (s.equals("length")) {
            this.length = Integer.valueOf(s2);
        }
        else {
            super.addAttribute(s, s2);
        }
    }
    
    @Override
    public void startElement() {
        if (this.length != null) {
            this.getValueObject();
        }
    }
    
    @Override
    protected boolean isArgument() {
        return true;
    }
    
    protected ValueObject getValueObject(Class<?> clazz, final Object[] array) {
        if (clazz == null) {
            clazz = Object.class;
        }
        if (this.length != null) {
            return ValueObjectImpl.create(Array.newInstance(clazz, (int)this.length));
        }
        final Object instance = Array.newInstance(clazz, array.length);
        for (int i = 0; i < array.length; ++i) {
            Array.set(instance, i, array[i]);
        }
        return ValueObjectImpl.create(instance);
    }
}
