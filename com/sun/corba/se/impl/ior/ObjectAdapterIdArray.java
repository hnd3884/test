package com.sun.corba.se.impl.ior;

import org.omg.CORBA_2_3.portable.OutputStream;
import java.util.Arrays;
import java.util.Iterator;

public class ObjectAdapterIdArray extends ObjectAdapterIdBase
{
    private final String[] objectAdapterId;
    
    public ObjectAdapterIdArray(final String[] objectAdapterId) {
        this.objectAdapterId = objectAdapterId;
    }
    
    public ObjectAdapterIdArray(final String s, final String s2) {
        (this.objectAdapterId = new String[2])[0] = s;
        this.objectAdapterId[1] = s2;
    }
    
    @Override
    public int getNumLevels() {
        return this.objectAdapterId.length;
    }
    
    @Override
    public Iterator iterator() {
        return Arrays.asList(this.objectAdapterId).iterator();
    }
    
    @Override
    public String[] getAdapterName() {
        return this.objectAdapterId.clone();
    }
}
