package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.OMException;
import javax.activation.DataHandler;
import java.io.Closeable;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import org.apache.axiom.om.util.StAXUtils;
import java.io.FileInputStream;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.apache.axiom.util.stax.xop.XOPDecodingStreamReader;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.attachments.Attachments;

public class XOPAwareStAXOMBuilder extends StAXOMBuilder implements XOPBuilder
{
    Attachments attachments;
    
    @Deprecated
    public XOPAwareStAXOMBuilder(final OMFactory ombuilderFactory, final XMLStreamReader parser, final Attachments attachments) {
        super(ombuilderFactory, new XOPDecodingStreamReader(parser, new AttachmentsMimePartProvider(attachments)));
        this.attachments = attachments;
    }
    
    @Deprecated
    public XOPAwareStAXOMBuilder(final OMFactory factory, final XMLStreamReader parser, final OMElement element, final Attachments attachments) {
        super(factory, new XOPDecodingStreamReader(parser, new AttachmentsMimePartProvider(attachments)), element);
        this.attachments = attachments;
    }
    
    @Deprecated
    public XOPAwareStAXOMBuilder(final String filePath, final Attachments attachments) throws XMLStreamException, FileNotFoundException {
        super(new XOPDecodingStreamReader(StAXUtils.createXMLStreamReader(new FileInputStream(filePath)), new AttachmentsMimePartProvider(attachments)));
        this.attachments = attachments;
    }
    
    @Deprecated
    public XOPAwareStAXOMBuilder(final InputStream inStream, final Attachments attachments) throws XMLStreamException {
        super(new XOPDecodingStreamReader(StAXUtils.createXMLStreamReader(inStream), new AttachmentsMimePartProvider(attachments)));
        this.attachments = attachments;
    }
    
    @Deprecated
    public XOPAwareStAXOMBuilder(final XMLStreamReader parser, final Attachments attachments) {
        super(new XOPDecodingStreamReader(parser, new AttachmentsMimePartProvider(attachments)));
        this.attachments = attachments;
    }
    
    public XOPAwareStAXOMBuilder(final OMFactory omFactory, final XMLStreamReader reader, final MimePartProvider mimePartProvider, final Detachable detachable, final Closeable closeable) {
        super(omFactory, new XOPDecodingStreamReader(reader, mimePartProvider), detachable, closeable);
        this.attachments = null;
    }
    
    public DataHandler getDataHandler(final String blobContentID) throws OMException {
        return this.attachments.getDataHandler(blobContentID);
    }
    
    public Attachments getAttachments() {
        return this.attachments;
    }
}
