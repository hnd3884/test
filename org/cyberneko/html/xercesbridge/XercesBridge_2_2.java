package org.cyberneko.html.xercesbridge;

import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.impl.Version;

public class XercesBridge_2_2 extends XercesBridge
{
    protected XercesBridge_2_2() throws InstantiationException {
        try {
            this.getVersion();
        }
        catch (final Throwable e) {
            throw new InstantiationException(e.getMessage());
        }
    }
    
    public String getVersion() {
        return Version.getVersion();
    }
    
    public void XMLDocumentHandler_startPrefixMapping(final XMLDocumentHandler documentHandler, final String prefix, final String uri, final Augmentations augs) {
    }
    
    public void XMLDocumentHandler_startDocument(final XMLDocumentHandler documentHandler, final XMLLocator locator, final String encoding, final NamespaceContext nscontext, final Augmentations augs) {
        documentHandler.startDocument(locator, encoding, nscontext, augs);
    }
    
    public void XMLDocumentFilter_setDocumentSource(final XMLDocumentFilter filter, final XMLDocumentSource lastSource) {
        filter.setDocumentSource(lastSource);
    }
}
