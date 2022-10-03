package org.apache.commons.collections;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

public class HashBag extends DefaultMapBag implements Bag
{
    public HashBag() {
        super(new HashMap());
    }
    
    public HashBag(final Collection coll) {
        this();
        this.addAll(coll);
    }
}
