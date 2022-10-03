package org.apache.axiom.util.stax.dialect;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;

class XLXP1StreamReaderWrapper extends XLXPStreamReaderWrapper
{
    public XLXP1StreamReaderWrapper(final XMLStreamReader parent) {
        super(parent);
    }
    
    @Override
    public String getEncoding() {
        final String encoding = super.getEncoding();
        return (encoding == null || encoding.length() == 0) ? null : encoding;
    }
    
    @Override
    public String getNamespaceURI(final String prefix) {
        final String uri = super.getNamespaceURI(prefix);
        return (uri == null || uri.length() == 0) ? null : uri;
    }
    
    @Override
    public NamespaceContext getNamespaceContext() {
        return new NamespaceURICorrectingNamespaceContextWrapper(super.getNamespaceContext());
    }
}
