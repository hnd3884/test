package com.sun.corba.se.impl.oa.poa;

import java.util.Set;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import java.util.HashMap;
import java.util.Map;

public abstract class ActiveObjectMap
{
    protected POAImpl poa;
    private Map keyToEntry;
    private Map entryToServant;
    private Map servantToEntry;
    
    protected ActiveObjectMap(final POAImpl poa) {
        this.keyToEntry = new HashMap();
        this.entryToServant = new HashMap();
        this.servantToEntry = new HashMap();
        this.poa = poa;
    }
    
    public static ActiveObjectMap create(final POAImpl poaImpl, final boolean b) {
        if (b) {
            return new MultipleObjectMap(poaImpl);
        }
        return new SingleObjectMap(poaImpl);
    }
    
    public final boolean contains(final Servant servant) {
        return this.servantToEntry.containsKey(servant);
    }
    
    public final boolean containsKey(final Key key) {
        return this.keyToEntry.containsKey(key);
    }
    
    public final AOMEntry get(final Key key) {
        AOMEntry aomEntry = this.keyToEntry.get(key);
        if (aomEntry == null) {
            aomEntry = new AOMEntry(this.poa);
            this.putEntry(key, aomEntry);
        }
        return aomEntry;
    }
    
    public final Servant getServant(final AOMEntry aomEntry) {
        return this.entryToServant.get(aomEntry);
    }
    
    public abstract Key getKey(final AOMEntry p0) throws WrongPolicy;
    
    public Key getKey(final Servant servant) throws WrongPolicy {
        return this.getKey(this.servantToEntry.get(servant));
    }
    
    protected void putEntry(final Key key, final AOMEntry aomEntry) {
        this.keyToEntry.put(key, aomEntry);
    }
    
    public final void putServant(final Servant servant, final AOMEntry aomEntry) {
        this.entryToServant.put(aomEntry, servant);
        this.servantToEntry.put(servant, aomEntry);
    }
    
    protected abstract void removeEntry(final AOMEntry p0, final Key p1);
    
    public final void remove(final Key key) {
        final AOMEntry aomEntry = this.keyToEntry.remove(key);
        final Servant servant = this.entryToServant.remove(aomEntry);
        if (servant != null) {
            this.servantToEntry.remove(servant);
        }
        this.removeEntry(aomEntry, key);
    }
    
    public abstract boolean hasMultipleIDs(final AOMEntry p0);
    
    protected void clear() {
        this.keyToEntry.clear();
    }
    
    public final Set keySet() {
        return this.keyToEntry.keySet();
    }
    
    public static class Key
    {
        public byte[] id;
        
        Key(final byte[] id) {
            this.id = id;
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < this.id.length; ++i) {
                sb.append(Integer.toString(this.id[i], 16));
                if (i != this.id.length - 1) {
                    sb.append(":");
                }
            }
            return sb.toString();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Key)) {
                return false;
            }
            final Key key = (Key)o;
            if (key.id.length != this.id.length) {
                return false;
            }
            for (int i = 0; i < this.id.length; ++i) {
                if (this.id[i] != key.id[i]) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int n = 0;
            for (int i = 0; i < this.id.length; ++i) {
                n = 31 * n + this.id[i];
            }
            return n;
        }
    }
}
