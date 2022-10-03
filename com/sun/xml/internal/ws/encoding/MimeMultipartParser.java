package com.sun.xml.internal.ws.encoding;

import com.sun.xml.internal.org.jvnet.mimepull.Header;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import javax.xml.transform.Source;
import javax.activation.DataSource;
import com.sun.xml.internal.ws.util.ByteArrayDataSource;
import javax.activation.DataHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.sun.xml.internal.ws.developer.StreamingDataHandler;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import java.io.IOException;
import com.sun.istack.internal.NotNull;
import java.util.Iterator;
import java.util.List;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEPart;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceException;
import java.util.HashMap;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import java.io.InputStream;
import java.util.Map;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;

public final class MimeMultipartParser
{
    private final String start;
    private final MIMEMessage message;
    private Attachment root;
    private ContentTypeImpl contentType;
    private final Map<String, Attachment> attachments;
    private boolean gotAll;
    
    public MimeMultipartParser(final InputStream in, final String cType, final StreamingAttachmentFeature feature) {
        this.attachments = new HashMap<String, Attachment>();
        this.contentType = new ContentTypeImpl(cType);
        final String boundary = this.contentType.getBoundary();
        if (boundary == null || boundary.equals("")) {
            throw new WebServiceException("MIME boundary parameter not found" + this.contentType);
        }
        this.message = ((feature != null) ? new MIMEMessage(in, boundary, feature.getConfig()) : new MIMEMessage(in, boundary));
        String st = this.contentType.getRootId();
        if (st != null && st.length() > 2 && st.charAt(0) == '<' && st.charAt(st.length() - 1) == '>') {
            st = st.substring(1, st.length() - 1);
        }
        this.start = st;
    }
    
    @Nullable
    public Attachment getRootPart() {
        if (this.root == null) {
            this.root = new PartAttachment((this.start != null) ? this.message.getPart(this.start) : this.message.getPart(0));
        }
        return this.root;
    }
    
    @NotNull
    public Map<String, Attachment> getAttachmentParts() {
        if (!this.gotAll) {
            final MIMEPart rootPart = (this.start != null) ? this.message.getPart(this.start) : this.message.getPart(0);
            final List<MIMEPart> parts = this.message.getAttachments();
            for (final MIMEPart part : parts) {
                if (part != rootPart) {
                    final String cid = part.getContentId();
                    if (this.attachments.containsKey(cid)) {
                        continue;
                    }
                    final PartAttachment attach = new PartAttachment(part);
                    this.attachments.put(attach.getContentId(), attach);
                }
            }
            this.gotAll = true;
        }
        return this.attachments;
    }
    
    @Nullable
    public Attachment getAttachmentPart(final String contentId) throws IOException {
        Attachment attach = this.attachments.get(contentId);
        if (attach == null) {
            final MIMEPart part = this.message.getPart(contentId);
            attach = new PartAttachment(part);
            this.attachments.put(contentId, attach);
        }
        return attach;
    }
    
    public ContentTypeImpl getContentType() {
        return this.contentType;
    }
    
    static class PartAttachment implements AttachmentEx
    {
        final MIMEPart part;
        byte[] buf;
        private StreamingDataHandler streamingDataHandler;
        
        PartAttachment(final MIMEPart part) {
            this.part = part;
        }
        
        @NotNull
        @Override
        public String getContentId() {
            return this.part.getContentId();
        }
        
        @NotNull
        @Override
        public String getContentType() {
            return this.part.getContentType();
        }
        
        @Override
        public byte[] asByteArray() {
            if (this.buf == null) {
                final ByteArrayBuffer baf = new ByteArrayBuffer();
                try {
                    baf.write(this.part.readOnce());
                }
                catch (final IOException ioe) {
                    throw new WebServiceException(ioe);
                }
                finally {
                    if (baf != null) {
                        try {
                            baf.close();
                        }
                        catch (final IOException ex) {
                            Logger.getLogger(MimeMultipartParser.class.getName()).log(Level.FINE, null, ex);
                        }
                    }
                }
                this.buf = baf.toByteArray();
            }
            return this.buf;
        }
        
        @Override
        public DataHandler asDataHandler() {
            if (this.streamingDataHandler == null) {
                this.streamingDataHandler = ((this.buf != null) ? new DataSourceStreamingDataHandler(new ByteArrayDataSource(this.buf, this.getContentType())) : new MIMEPartStreamingDataHandler(this.part));
            }
            return this.streamingDataHandler;
        }
        
        @Override
        public Source asSource() {
            return (this.buf != null) ? new StreamSource(new ByteArrayInputStream(this.buf)) : new StreamSource(this.part.read());
        }
        
        @Override
        public InputStream asInputStream() {
            return (this.buf != null) ? new ByteArrayInputStream(this.buf) : this.part.read();
        }
        
        @Override
        public void writeTo(final OutputStream os) throws IOException {
            if (this.buf != null) {
                os.write(this.buf);
            }
            else {
                final InputStream in = this.part.read();
                final byte[] temp = new byte[8192];
                int len;
                while ((len = in.read(temp)) != -1) {
                    os.write(temp, 0, len);
                }
                in.close();
            }
        }
        
        @Override
        public void writeTo(final SOAPMessage saaj) throws SOAPException {
            saaj.createAttachmentPart().setDataHandler(this.asDataHandler());
        }
        
        @Override
        public Iterator<MimeHeader> getMimeHeaders() {
            final Iterator<? extends Header> ih = this.part.getAllHeaders().iterator();
            return new Iterator<MimeHeader>() {
                @Override
                public boolean hasNext() {
                    return ih.hasNext();
                }
                
                @Override
                public MimeHeader next() {
                    final Header hdr = ih.next();
                    return new MimeHeader() {
                        @Override
                        public String getValue() {
                            return hdr.getValue();
                        }
                        
                        @Override
                        public String getName() {
                            return hdr.getName();
                        }
                    };
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}
