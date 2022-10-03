package com.sun.beans.decoder;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import com.sun.beans.finder.ConstructorFinder;
import java.util.ArrayList;
import java.util.List;

class NewElementHandler extends ElementHandler
{
    private List<Object> arguments;
    private ValueObject value;
    private Class<?> type;
    
    NewElementHandler() {
        this.arguments = new ArrayList<Object>();
        this.value = ValueObjectImpl.VOID;
    }
    
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
    protected final void addArgument(final Object o) {
        if (this.arguments == null) {
            throw new IllegalStateException("Could not add argument to evaluated element");
        }
        this.arguments.add(o);
    }
    
    @Override
    protected final Object getContextBean() {
        return (this.type != null) ? this.type : super.getContextBean();
    }
    
    @Override
    protected final ValueObject getValueObject() {
        if (this.arguments != null) {
            try {
                this.value = this.getValueObject(this.type, this.arguments.toArray());
            }
            catch (final Exception ex) {
                this.getOwner().handleException(ex);
            }
            finally {
                this.arguments = null;
            }
        }
        return this.value;
    }
    
    ValueObject getValueObject(final Class<?> clazz, Object[] arguments) throws Exception {
        if (clazz == null) {
            throw new IllegalArgumentException("Class name is not set");
        }
        final Constructor<?> constructor = ConstructorFinder.findConstructor(clazz, getArgumentTypes(arguments));
        if (constructor.isVarArgs()) {
            arguments = getArguments(arguments, constructor.getParameterTypes());
        }
        return ValueObjectImpl.create(constructor.newInstance(arguments));
    }
    
    static Class<?>[] getArgumentTypes(final Object[] array) {
        final Class[] array2 = new Class[array.length];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                array2[i] = array[i].getClass();
            }
        }
        return array2;
    }
    
    static Object[] getArguments(final Object[] array, final Class<?>[] array2) {
        final int n = array2.length - 1;
        if (array2.length == array.length) {
            final Object o = array[n];
            if (o == null) {
                return array;
            }
            if (array2[n].isAssignableFrom(o.getClass())) {
                return array;
            }
        }
        final int n2 = array.length - n;
        final Object instance = Array.newInstance(array2[n].getComponentType(), n2);
        System.arraycopy(array, n, instance, 0, n2);
        final Object[] array3 = new Object[array2.length];
        System.arraycopy(array, 0, array3, 0, n);
        array3[n] = instance;
        return array3;
    }
}
