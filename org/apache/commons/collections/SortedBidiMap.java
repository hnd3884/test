package org.apache.commons.collections;

import java.util.SortedMap;

public interface SortedBidiMap extends OrderedBidiMap, SortedMap
{
    BidiMap inverseBidiMap();
    
    SortedBidiMap inverseSortedBidiMap();
}
