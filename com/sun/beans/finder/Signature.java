package com.sun.beans.finder;

final class Signature
{
    private final Class<?> type;
    private final String name;
    private final Class<?>[] args;
    private volatile int code;
    
    Signature(final Class<?> clazz, final Class<?>[] array) {
        this(clazz, null, array);
    }
    
    Signature(final Class<?> type, final String name, final Class<?>[] args) {
        this.type = type;
        this.name = name;
        this.args = args;
    }
    
    Class<?> getType() {
        return this.type;
    }
    
    String getName() {
        return this.name;
    }
    
    Class<?>[] getArgs() {
        return this.args;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Signature) {
            final Signature signature = (Signature)o;
            return isEqual(signature.type, this.type) && isEqual(signature.name, this.name) && isEqual(signature.args, this.args);
        }
        return false;
    }
    
    private static boolean isEqual(final Object o, final Object o2) {
        return (o == null) ? (o2 == null) : o.equals(o2);
    }
    
    private static boolean isEqual(final Class<?>[] array, final Class<?>[] array2) {
        if (array == null || array2 == null) {
            return array == array2;
        }
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (!isEqual(array[i], array2[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        if (this.code == 0) {
            int code = addHashCode(addHashCode(17, this.type), this.name);
            if (this.args != null) {
                final Class<?>[] args = this.args;
                for (int length = args.length, i = 0; i < length; ++i) {
                    code = addHashCode(code, args[i]);
                }
            }
            this.code = code;
        }
        return this.code;
    }
    
    private static int addHashCode(int n, final Object o) {
        n *= 37;
        return (o != null) ? (n + o.hashCode()) : n;
    }
}
