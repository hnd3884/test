package com.sun.corba.se.impl.oa.poa;

import org.omg.PortableServer.POAPackage.WrongPolicy;
import java.util.HashMap;
import java.util.Map;

class SingleObjectMap extends ActiveObjectMap
{
    private Map entryToKey;
    
    public SingleObjectMap(final POAImpl poaImpl) {
        super(poaImpl);
        this.entryToKey = new HashMap();
    }
    
    @Override
    public Key getKey(final AOMEntry aomEntry) throws WrongPolicy {
        return this.entryToKey.get(aomEntry);
    }
    
    @Override
    protected void putEntry(final Key key, final AOMEntry aomEntry) {
        super.putEntry(key, aomEntry);
        this.entryToKey.put(aomEntry, key);
    }
    
    @Override
    public boolean hasMultipleIDs(final AOMEntry aomEntry) {
        return false;
    }
    
    @Override
    protected void removeEntry(final AOMEntry aomEntry, final Key key) {
        this.entryToKey.remove(aomEntry);
    }
    
    public void clear() {
        super.clear();
        this.entryToKey.clear();
    }
}
