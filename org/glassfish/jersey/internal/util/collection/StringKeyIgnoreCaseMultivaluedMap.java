package org.glassfish.jersey.internal.util.collection;

import java.util.Map;
import javax.ws.rs.core.AbstractMultivaluedMap;

public class StringKeyIgnoreCaseMultivaluedMap<V> extends AbstractMultivaluedMap<String, V>
{
    public StringKeyIgnoreCaseMultivaluedMap() {
        super((Map)new KeyComparatorLinkedHashMap(StringIgnoreCaseKeyComparator.SINGLETON));
    }
}
