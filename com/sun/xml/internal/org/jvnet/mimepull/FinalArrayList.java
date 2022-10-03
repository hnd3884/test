package com.sun.xml.internal.org.jvnet.mimepull;

import java.util.Collection;
import java.util.ArrayList;

final class FinalArrayList<T> extends ArrayList<T>
{
    public FinalArrayList(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public FinalArrayList() {
    }
    
    public FinalArrayList(final Collection<? extends T> ts) {
        super(ts);
    }
}
