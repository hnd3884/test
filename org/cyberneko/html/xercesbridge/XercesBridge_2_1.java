package org.cyberneko.html.xercesbridge;

import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.impl.Version;

public class XercesBridge_2_1 extends XercesBridge
{
    public XercesBridge_2_1() throws InstantiationException {
        try {
            this.getVersion();
        }
        catch (final Error e) {
            throw new InstantiationException(e.getMessage());
        }
    }
    
    public String getVersion() {
        return new Version().getVersion();
    }
    
    public void XMLDocumentHandler_startDocument(final XMLDocumentHandler documentHandler, final XMLLocator locator, final String encoding, final NamespaceContext nscontext, final Augmentations augs) {
        documentHandler.startDocument(locator, encoding, augs);
    }
    
    public void XMLDocumentFilter_setDocumentSource(final XMLDocumentFilter filter, final XMLDocumentSource lastSource) {
        filter.setDocumentSource(lastSource);
    }
}
