package com.sun.beans.finder;

class InstanceFinder<T>
{
    private static final String[] EMPTY;
    private final Class<? extends T> type;
    private final boolean allow;
    private final String suffix;
    private volatile String[] packages;
    
    InstanceFinder(final Class<? extends T> type, final boolean allow, final String suffix, final String... array) {
        this.type = type;
        this.allow = allow;
        this.suffix = suffix;
        this.packages = array.clone();
    }
    
    public String[] getPackages() {
        return this.packages.clone();
    }
    
    public void setPackages(final String... array) {
        this.packages = ((array != null && array.length > 0) ? array.clone() : InstanceFinder.EMPTY);
    }
    
    public T find(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        String s = clazz.getName() + this.suffix;
        final T instantiate = this.instantiate(clazz, s);
        if (instantiate != null) {
            return instantiate;
        }
        if (this.allow) {
            final T instantiate2 = this.instantiate(clazz, null);
            if (instantiate2 != null) {
                return instantiate2;
            }
        }
        final int n = s.lastIndexOf(46) + 1;
        if (n > 0) {
            s = s.substring(n);
        }
        final String[] packages = this.packages;
        for (int length = packages.length, i = 0; i < length; ++i) {
            final T instantiate3 = this.instantiate(clazz, packages[i], s);
            if (instantiate3 != null) {
                return instantiate3;
            }
        }
        return null;
    }
    
    protected T instantiate(Class<?> class1, final String s) {
        if (class1 != null) {
            try {
                if (s != null) {
                    class1 = ClassFinder.findClass(s, class1.getClassLoader());
                }
                if (this.type.isAssignableFrom(class1)) {
                    return class1.newInstance();
                }
            }
            catch (final Exception ex) {}
        }
        return null;
    }
    
    protected T instantiate(final Class<?> clazz, final String s, final String s2) {
        return this.instantiate(clazz, s + '.' + s2);
    }
    
    static {
        EMPTY = new String[0];
    }
}
