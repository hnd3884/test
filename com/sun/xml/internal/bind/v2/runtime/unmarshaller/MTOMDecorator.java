package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.activation.DataHandler;
import javax.xml.bind.ValidationEvent;
import org.xml.sax.SAXException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.bind.attachment.AttachmentUnmarshaller;

final class MTOMDecorator implements XmlVisitor
{
    private final XmlVisitor next;
    private final AttachmentUnmarshaller au;
    private UnmarshallerImpl parent;
    private final Base64Data base64data;
    private boolean inXopInclude;
    private boolean followXop;
    
    public MTOMDecorator(final UnmarshallerImpl parent, final XmlVisitor next, final AttachmentUnmarshaller au) {
        this.base64data = new Base64Data();
        this.parent = parent;
        this.next = next;
        this.au = au;
    }
    
    @Override
    public void startDocument(final LocatorEx loc, final NamespaceContext nsContext) throws SAXException {
        this.next.startDocument(loc, nsContext);
    }
    
    @Override
    public void endDocument() throws SAXException {
        this.next.endDocument();
    }
    
    @Override
    public void startElement(final TagName tagName) throws SAXException {
        if (tagName.local.equals("Include") && tagName.uri.equals("http://www.w3.org/2004/08/xop/include")) {
            final String href = tagName.atts.getValue("href");
            final DataHandler attachment = this.au.getAttachmentAsDataHandler(href);
            if (attachment == null) {
                this.parent.getEventHandler().handleEvent(null);
            }
            this.base64data.set(attachment);
            this.next.text(this.base64data);
            this.inXopInclude = true;
            this.followXop = true;
        }
        else {
            this.next.startElement(tagName);
        }
    }
    
    @Override
    public void endElement(final TagName tagName) throws SAXException {
        if (this.inXopInclude) {
            this.inXopInclude = false;
            this.followXop = true;
            return;
        }
        this.next.endElement(tagName);
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String nsUri) throws SAXException {
        this.next.startPrefixMapping(prefix, nsUri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) throws SAXException {
        this.next.endPrefixMapping(prefix);
    }
    
    @Override
    public void text(final CharSequence pcdata) throws SAXException {
        if (!this.followXop) {
            this.next.text(pcdata);
        }
        else {
            this.followXop = false;
        }
    }
    
    @Override
    public UnmarshallingContext getContext() {
        return this.next.getContext();
    }
    
    @Override
    public TextPredictor getPredictor() {
        return this.next.getPredictor();
    }
}
