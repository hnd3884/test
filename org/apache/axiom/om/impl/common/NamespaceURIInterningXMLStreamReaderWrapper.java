package org.apache.axiom.om.impl.common;

import javax.xml.namespace.NamespaceContext;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class NamespaceURIInterningXMLStreamReaderWrapper extends XMLStreamReaderWrapper implements OMXMLStreamReader
{
    private NamespaceURIInterningNamespaceContextWrapper namespaceContextWrapper;
    
    public NamespaceURIInterningXMLStreamReaderWrapper(final OMXMLStreamReader parent) {
        super((XMLStreamReader)parent);
    }
    
    private static String intern(final String s) {
        return (s == null) ? null : s.intern();
    }
    
    public String getAttributeNamespace(final int index) {
        return intern(super.getAttributeNamespace(index));
    }
    
    public String getNamespaceURI() {
        return intern(super.getNamespaceURI());
    }
    
    public String getNamespaceURI(final int index) {
        return intern(super.getNamespaceURI(index));
    }
    
    public String getNamespaceURI(final String prefix) {
        return intern(super.getNamespaceURI(prefix));
    }
    
    public DataHandler getDataHandler(final String blobcid) {
        return ((OMXMLStreamReader)this.getParent()).getDataHandler(blobcid);
    }
    
    public NamespaceContext getNamespaceContext() {
        final NamespaceContext namespaceContext = super.getNamespaceContext();
        if (this.namespaceContextWrapper == null || this.namespaceContextWrapper.getParent() != namespaceContext) {
            this.namespaceContextWrapper = new NamespaceURIInterningNamespaceContextWrapper(namespaceContext);
        }
        return this.namespaceContextWrapper;
    }
    
    public boolean isInlineMTOM() {
        return ((OMXMLStreamReader)this.getParent()).isInlineMTOM();
    }
    
    public void setInlineMTOM(final boolean value) {
        ((OMXMLStreamReader)this.getParent()).setInlineMTOM(value);
    }
}
