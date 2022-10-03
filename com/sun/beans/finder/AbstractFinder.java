package com.sun.beans.finder;

import java.util.HashMap;
import java.lang.reflect.Modifier;
import java.lang.reflect.Executable;

abstract class AbstractFinder<T extends Executable>
{
    private final Class<?>[] args;
    
    protected AbstractFinder(final Class<?>[] args) {
        this.args = args;
    }
    
    protected boolean isValid(final T t) {
        return Modifier.isPublic(t.getModifiers());
    }
    
    final T find(final T[] array) throws NoSuchMethodException {
        final HashMap hashMap = new HashMap();
        Executable executable = null;
        Class<?>[] array2 = null;
        boolean b = false;
        for (final Executable executable2 : array) {
            if (this.isValid((T)executable2)) {
                final Class<?>[] parameterTypes = executable2.getParameterTypes();
                if (parameterTypes.length == this.args.length) {
                    PrimitiveWrapperMap.replacePrimitivesWithWrappers(parameterTypes);
                    if (this.isAssignable(parameterTypes, this.args)) {
                        if (executable == null) {
                            executable = executable2;
                            array2 = parameterTypes;
                        }
                        else {
                            boolean assignable = this.isAssignable(array2, parameterTypes);
                            boolean assignable2 = this.isAssignable(parameterTypes, array2);
                            if (assignable2 && assignable) {
                                assignable = !executable2.isSynthetic();
                                assignable2 = !executable.isSynthetic();
                            }
                            if (assignable2 == assignable) {
                                b = true;
                            }
                            else if (assignable) {
                                executable = executable2;
                                array2 = parameterTypes;
                                b = false;
                            }
                        }
                    }
                }
                if (executable2.isVarArgs()) {
                    final int n = parameterTypes.length - 1;
                    if (n <= this.args.length) {
                        final Class[] array3 = new Class[this.args.length];
                        System.arraycopy(parameterTypes, 0, array3, 0, n);
                        if (n < this.args.length) {
                            Class<?> clazz = parameterTypes[n].getComponentType();
                            if (clazz.isPrimitive()) {
                                clazz = PrimitiveWrapperMap.getType(clazz.getName());
                            }
                            for (int j = n; j < this.args.length; ++j) {
                                array3[j] = clazz;
                            }
                        }
                        hashMap.put(executable2, array3);
                    }
                }
            }
        }
        for (final Executable executable3 : array) {
            final Class[] array4 = (Class[])hashMap.get(executable3);
            if (array4 != null && this.isAssignable(array4, this.args)) {
                if (executable == null) {
                    executable = executable3;
                    array2 = array4;
                }
                else {
                    boolean assignable3 = this.isAssignable(array2, array4);
                    boolean assignable4 = this.isAssignable(array4, array2);
                    if (assignable4 && assignable3) {
                        assignable3 = !executable3.isSynthetic();
                        assignable4 = !executable.isSynthetic();
                    }
                    if (assignable4 == assignable3) {
                        if (array2 == hashMap.get(executable)) {
                            b = true;
                        }
                    }
                    else if (assignable3) {
                        executable = executable3;
                        array2 = array4;
                        b = false;
                    }
                }
            }
        }
        if (b) {
            throw new NoSuchMethodException("Ambiguous methods are found");
        }
        if (executable == null) {
            throw new NoSuchMethodException("Method is not found");
        }
        return (T)executable;
    }
    
    private boolean isAssignable(final Class<?>[] array, final Class<?>[] array2) {
        for (int i = 0; i < this.args.length; ++i) {
            if (null != this.args[i] && !array[i].isAssignableFrom(array2[i])) {
                return false;
            }
        }
        return true;
    }
}
