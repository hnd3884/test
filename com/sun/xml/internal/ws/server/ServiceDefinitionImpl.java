package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.server.SDDocument;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.istack.internal.NotNull;
import java.util.Map;
import java.util.List;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;

public final class ServiceDefinitionImpl implements ServiceDefinition, SDDocumentResolver
{
    private final List<SDDocumentImpl> docs;
    private final Map<String, SDDocumentImpl> bySystemId;
    @NotNull
    private final SDDocumentImpl primaryWsdl;
    WSEndpointImpl<?> owner;
    final List<SDDocumentFilter> filters;
    
    public ServiceDefinitionImpl(final List<SDDocumentImpl> docs, @NotNull final SDDocumentImpl primaryWsdl) {
        this.filters = new ArrayList<SDDocumentFilter>();
        assert docs.contains(primaryWsdl);
        this.docs = docs;
        this.primaryWsdl = primaryWsdl;
        this.bySystemId = new HashMap<String, SDDocumentImpl>(docs.size());
        for (final SDDocumentImpl doc : docs) {
            this.bySystemId.put(doc.getURL().toExternalForm(), doc);
            doc.setFilters(this.filters);
            doc.setResolver(this);
        }
    }
    
    void setOwner(final WSEndpointImpl<?> owner) {
        assert owner != null && this.owner == null;
        this.owner = owner;
    }
    
    @NotNull
    @Override
    public SDDocument getPrimary() {
        return this.primaryWsdl;
    }
    
    @Override
    public void addFilter(final SDDocumentFilter filter) {
        this.filters.add(filter);
    }
    
    @Override
    public Iterator<SDDocument> iterator() {
        return (Iterator<SDDocument>)this.docs.iterator();
    }
    
    @Override
    public SDDocument resolve(final String systemId) {
        return this.bySystemId.get(systemId);
    }
}
