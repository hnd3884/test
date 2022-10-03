package com.sun.corba.se.impl.orbutil;

import java.util.ArrayList;

public class DenseIntMapImpl
{
    private ArrayList list;
    
    public DenseIntMapImpl() {
        this.list = new ArrayList();
    }
    
    private void checkKey(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Key must be >= 0.");
        }
    }
    
    public Object get(final int n) {
        this.checkKey(n);
        Object value = null;
        if (n < this.list.size()) {
            value = this.list.get(n);
        }
        return value;
    }
    
    public void set(final int n, final Object o) {
        this.checkKey(n);
        this.extend(n);
        this.list.set(n, o);
    }
    
    private void extend(final int n) {
        if (n >= this.list.size()) {
            this.list.ensureCapacity(n + 1);
            int size = this.list.size();
            while (size++ <= n) {
                this.list.add(null);
            }
        }
    }
}
