package com.sun.xml.internal.messaging.saaj.util;

import java.util.Collection;
import java.util.ArrayList;

public final class FinalArrayList extends ArrayList
{
    public FinalArrayList(final int initialCapacity) {
        super(initialCapacity);
    }
    
    public FinalArrayList() {
    }
    
    public FinalArrayList(final Collection collection) {
        super(collection);
    }
}
