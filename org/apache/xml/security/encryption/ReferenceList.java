package org.apache.xml.security.encryption;

import java.util.Iterator;

public interface ReferenceList
{
    public static final int DATA_REFERENCE = 1;
    public static final int KEY_REFERENCE = 2;
    
    void add(final Reference p0);
    
    void remove(final Reference p0);
    
    int size();
    
    boolean isEmpty();
    
    Iterator getReferences();
    
    Reference newDataReference(final String p0);
    
    Reference newKeyReference(final String p0);
}
