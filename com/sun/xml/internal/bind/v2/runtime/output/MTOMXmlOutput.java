package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.bind.v2.runtime.Name;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;

public final class MTOMXmlOutput extends XmlOutputAbstractImpl
{
    private final XmlOutput next;
    private String nsUri;
    private String localName;
    
    public MTOMXmlOutput(final XmlOutput next) {
        this.next = next;
    }
    
    @Override
    public void startDocument(final XMLSerializer serializer, final boolean fragment, final int[] nsUriIndex2prefixIndex, final NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        super.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
        this.next.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
    }
    
    @Override
    public void endDocument(final boolean fragment) throws IOException, SAXException, XMLStreamException {
        this.next.endDocument(fragment);
        super.endDocument(fragment);
    }
    
    @Override
    public void beginStartTag(final Name name) throws IOException, XMLStreamException {
        this.next.beginStartTag(name);
        this.nsUri = name.nsUri;
        this.localName = name.localName;
    }
    
    @Override
    public void beginStartTag(final int prefix, final String localName) throws IOException, XMLStreamException {
        this.next.beginStartTag(prefix, localName);
        this.nsUri = this.nsContext.getNamespaceURI(prefix);
        this.localName = localName;
    }
    
    @Override
    public void attribute(final Name name, final String value) throws IOException, XMLStreamException {
        this.next.attribute(name, value);
    }
    
    @Override
    public void attribute(final int prefix, final String localName, final String value) throws IOException, XMLStreamException {
        this.next.attribute(prefix, localName, value);
    }
    
    @Override
    public void endStartTag() throws IOException, SAXException {
        this.next.endStartTag();
    }
    
    @Override
    public void endTag(final Name name) throws IOException, SAXException, XMLStreamException {
        this.next.endTag(name);
    }
    
    @Override
    public void endTag(final int prefix, final String localName) throws IOException, SAXException, XMLStreamException {
        this.next.endTag(prefix, localName);
    }
    
    @Override
    public void text(final String value, final boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        this.next.text(value, needsSeparatingWhitespace);
    }
    
    @Override
    public void text(final Pcdata value, final boolean needsSeparatingWhitespace) throws IOException, SAXException, XMLStreamException {
        if (value instanceof Base64Data && !this.serializer.getInlineBinaryFlag()) {
            final Base64Data b64d = (Base64Data)value;
            String cid;
            if (b64d.hasData()) {
                cid = this.serializer.attachmentMarshaller.addMtomAttachment(b64d.get(), 0, b64d.getDataLen(), b64d.getMimeType(), this.nsUri, this.localName);
            }
            else {
                cid = this.serializer.attachmentMarshaller.addMtomAttachment(b64d.getDataHandler(), this.nsUri, this.localName);
            }
            if (cid != null) {
                this.nsContext.getCurrent().push();
                final int prefix = this.nsContext.declareNsUri("http://www.w3.org/2004/08/xop/include", "xop", false);
                this.beginStartTag(prefix, "Include");
                this.attribute(-1, "href", cid);
                this.endStartTag();
                this.endTag(prefix, "Include");
                this.nsContext.getCurrent().pop();
                return;
            }
        }
        this.next.text(value, needsSeparatingWhitespace);
    }
}
