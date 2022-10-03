package org.apache.axiom.util.namespace;

import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class MapBasedNamespaceContext extends AbstractNamespaceContext
{
    private final Map namespaces;
    
    public MapBasedNamespaceContext(final Map map) {
        this.namespaces = map;
    }
    
    @Override
    protected String doGetNamespaceURI(final String prefix) {
        final String namespaceURI = this.namespaces.get(prefix);
        return (namespaceURI == null) ? "" : namespaceURI;
    }
    
    @Override
    protected String doGetPrefix(final String nsURI) {
        for (final Map.Entry entry : this.namespaces.entrySet()) {
            final String uri = entry.getValue();
            if (uri.equals(nsURI)) {
                return entry.getKey();
            }
        }
        if (nsURI.length() == 0) {
            return "";
        }
        return null;
    }
    
    @Override
    protected Iterator doGetPrefixes(final String nsURI) {
        Set prefixes = null;
        for (final Map.Entry entry : this.namespaces.entrySet()) {
            final String uri = entry.getValue();
            if (uri.equals(nsURI)) {
                if (prefixes == null) {
                    prefixes = new HashSet();
                }
                prefixes.add(entry.getKey());
            }
        }
        if (prefixes != null) {
            return Collections.unmodifiableSet((Set<?>)prefixes).iterator();
        }
        if (nsURI.length() == 0) {
            return Collections.singleton("").iterator();
        }
        return Collections.EMPTY_LIST.iterator();
    }
}
