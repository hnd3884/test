package org.apache.lucene.util;

import java.util.Collection;

public interface Accountable
{
    long ramBytesUsed();
    
    Collection<Accountable> getChildResources();
}
