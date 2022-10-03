package com.sun.corba.se.impl.oa.poa;

import java.util.HashSet;
import java.util.Set;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import java.util.HashMap;
import java.util.Map;

class MultipleObjectMap extends ActiveObjectMap
{
    private Map entryToKeys;
    
    public MultipleObjectMap(final POAImpl poaImpl) {
        super(poaImpl);
        this.entryToKeys = new HashMap();
    }
    
    @Override
    public Key getKey(final AOMEntry aomEntry) throws WrongPolicy {
        throw new WrongPolicy();
    }
    
    @Override
    protected void putEntry(final Key key, final AOMEntry aomEntry) {
        super.putEntry(key, aomEntry);
        Set set = this.entryToKeys.get(aomEntry);
        if (set == null) {
            set = new HashSet();
            this.entryToKeys.put(aomEntry, set);
        }
        set.add(key);
    }
    
    @Override
    public boolean hasMultipleIDs(final AOMEntry aomEntry) {
        final Set set = this.entryToKeys.get(aomEntry);
        return set != null && set.size() > 1;
    }
    
    @Override
    protected void removeEntry(final AOMEntry aomEntry, final Key key) {
        final Set set = this.entryToKeys.get(aomEntry);
        if (set != null) {
            set.remove(key);
            if (set.isEmpty()) {
                this.entryToKeys.remove(aomEntry);
            }
        }
    }
    
    public void clear() {
        super.clear();
        this.entryToKeys.clear();
    }
}
