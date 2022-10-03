package org.bouncycastle.cert.dane;

import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.StoreException;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.util.Selector;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.util.Store;

public class DANEEntryStore implements Store
{
    private final Map entries;
    
    DANEEntryStore(final List list) {
        final HashMap hashMap = new HashMap();
        for (final DANEEntry daneEntry : list) {
            hashMap.put(daneEntry.getDomainName(), daneEntry);
        }
        this.entries = Collections.unmodifiableMap((Map<?, ?>)hashMap);
    }
    
    public Collection getMatches(final Selector selector) throws StoreException {
        if (selector == null) {
            return this.entries.values();
        }
        final ArrayList list = new ArrayList();
        for (final Object next : this.entries.values()) {
            if (selector.match(next)) {
                list.add(next);
            }
        }
        return Collections.unmodifiableList((List<?>)list);
    }
    
    public Store toCertificateStore() {
        final Collection matches = this.getMatches(null);
        final ArrayList list = new ArrayList(matches.size());
        final Iterator iterator = matches.iterator();
        while (iterator.hasNext()) {
            list.add((Object)((DANEEntry)iterator.next()).getCertificate());
        }
        return (Store)new CollectionStore((Collection)list);
    }
}
