package com.sun.xml.internal.ws.message.jaxb;

import java.net.MalformedURLException;
import java.io.UnsupportedEncodingException;
import javax.xml.ws.WebServiceException;
import java.net.URLEncoder;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.net.URI;
import java.util.UUID;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import javax.activation.DataHandler;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.istack.internal.logging.Logger;
import javax.xml.bind.attachment.AttachmentMarshaller;

final class AttachmentMarshallerImpl extends AttachmentMarshaller
{
    private static final Logger LOGGER;
    private AttachmentSet attachments;
    
    public AttachmentMarshallerImpl(final AttachmentSet attachemnts) {
        this.attachments = attachemnts;
    }
    
    void cleanup() {
        this.attachments = null;
    }
    
    @Override
    public String addMtomAttachment(final DataHandler data, final String elementNamespace, final String elementLocalName) {
        throw new IllegalStateException();
    }
    
    @Override
    public String addMtomAttachment(final byte[] data, final int offset, final int length, final String mimeType, final String elementNamespace, final String elementLocalName) {
        throw new IllegalStateException();
    }
    
    @Override
    public String addSwaRefAttachment(final DataHandler data) {
        String cid = this.encodeCid(null);
        final Attachment att = new DataHandlerAttachment(cid, data);
        this.attachments.add(att);
        cid = "cid:" + cid;
        return cid;
    }
    
    private String encodeCid(final String ns) {
        String cid = "example.jaxws.sun.com";
        final String name = UUID.randomUUID() + "@";
        if (ns != null && ns.length() > 0) {
            try {
                final URI uri = new URI(ns);
                cid = uri.toURL().getHost();
            }
            catch (final URISyntaxException e) {
                if (AttachmentMarshallerImpl.LOGGER.isLoggable(Level.INFO)) {
                    AttachmentMarshallerImpl.LOGGER.log(Level.INFO, null, e);
                }
                return null;
            }
            catch (final MalformedURLException e2) {
                try {
                    cid = URLEncoder.encode(ns, "UTF-8");
                }
                catch (final UnsupportedEncodingException e3) {
                    throw new WebServiceException(e2);
                }
            }
        }
        return name + cid;
    }
    
    static {
        LOGGER = Logger.getLogger(AttachmentMarshallerImpl.class);
    }
}
