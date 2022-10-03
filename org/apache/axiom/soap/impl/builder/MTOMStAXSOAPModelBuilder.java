package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.om.OMException;
import javax.activation.DataHandler;
import java.io.Closeable;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.apache.axiom.util.stax.xop.XOPDecodingStreamReader;
import org.apache.axiom.om.impl.builder.AttachmentsMimePartProvider;
import org.apache.axiom.soap.SOAPFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.impl.builder.XOPBuilder;

public class MTOMStAXSOAPModelBuilder extends StAXSOAPModelBuilder implements XOPBuilder
{
    private final Attachments attachments;
    
    @Deprecated
    public MTOMStAXSOAPModelBuilder(final XMLStreamReader parser, final SOAPFactory factory, final Attachments attachments, final String soapVersion) {
        super(new XOPDecodingStreamReader(parser, new AttachmentsMimePartProvider(attachments)), factory, soapVersion);
        this.attachments = attachments;
    }
    
    @Deprecated
    public MTOMStAXSOAPModelBuilder(final XMLStreamReader reader, final Attachments attachments, final String soapVersion) {
        super(new XOPDecodingStreamReader(reader, new AttachmentsMimePartProvider(attachments)), soapVersion);
        this.attachments = attachments;
    }
    
    @Deprecated
    public MTOMStAXSOAPModelBuilder(final XMLStreamReader reader, final Attachments attachments) {
        super(new XOPDecodingStreamReader(reader, new AttachmentsMimePartProvider(attachments)));
        this.attachments = attachments;
    }
    
    public MTOMStAXSOAPModelBuilder(final SOAPFactory soapFactory, final XMLStreamReader reader, final MimePartProvider mimePartProvider, final Detachable detachable, final Closeable closeable) {
        super(new XOPDecodingStreamReader(reader, mimePartProvider), soapFactory, soapFactory.getSoapVersionURI(), detachable, closeable);
        this.attachments = null;
    }
    
    public DataHandler getDataHandler(final String blobContentID) throws OMException {
        final DataHandler dataHandler = this.attachments.getDataHandler(blobContentID);
        return dataHandler;
    }
    
    public Attachments getAttachments() {
        return this.attachments;
    }
}
