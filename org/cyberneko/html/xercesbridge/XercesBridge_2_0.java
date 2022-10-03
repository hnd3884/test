package org.cyberneko.html.xercesbridge;

import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.impl.Version;

public class XercesBridge_2_0 extends XercesBridge
{
    protected XercesBridge_2_0() {
    }
    
    public String getVersion() {
        return Version.fVersion;
    }
    
    public void XMLDocumentHandler_startPrefixMapping(final XMLDocumentHandler documentHandler, final String prefix, final String uri, final Augmentations augs) {
        documentHandler.startPrefixMapping(prefix, uri, augs);
    }
    
    public void XMLDocumentHandler_endPrefixMapping(final XMLDocumentHandler documentHandler, final String prefix, final Augmentations augs) {
        documentHandler.endPrefixMapping(prefix, augs);
    }
    
    public void XMLDocumentHandler_startDocument(final XMLDocumentHandler documentHandler, final XMLLocator locator, final String encoding, final NamespaceContext nscontext, final Augmentations augs) {
        documentHandler.startDocument(locator, encoding, augs);
    }
}
