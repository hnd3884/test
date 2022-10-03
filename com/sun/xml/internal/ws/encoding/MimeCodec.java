package com.sun.xml.internal.ws.encoding;

import java.nio.channels.ReadableByteChannel;
import com.sun.xml.internal.ws.developer.StreamingAttachmentFeature;
import java.io.InputStream;
import java.util.UUID;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import java.io.IOException;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.Codec;

abstract class MimeCodec implements Codec
{
    public static final String MULTIPART_RELATED_MIME_TYPE = "multipart/related";
    protected Codec mimeRootCodec;
    protected final SOAPVersion version;
    protected final WSFeatureList features;
    
    protected MimeCodec(final SOAPVersion version, final WSFeatureList f) {
        this.version = version;
        this.features = f;
    }
    
    @Override
    public String getMimeType() {
        return "multipart/related";
    }
    
    protected Codec getMimeRootCodec(final Packet packet) {
        return this.mimeRootCodec;
    }
    
    @Override
    public ContentType encode(final Packet packet, final OutputStream out) throws IOException {
        final Message msg = packet.getMessage();
        if (msg == null) {
            return null;
        }
        final ContentTypeImpl ctImpl = (ContentTypeImpl)this.getStaticContentType(packet);
        final String boundary = ctImpl.getBoundary();
        final boolean hasAttachments = boundary != null;
        final Codec rootCodec = this.getMimeRootCodec(packet);
        if (hasAttachments) {
            writeln("--" + boundary, out);
            final ContentType ct = rootCodec.getStaticContentType(packet);
            final String ctStr = (ct != null) ? ct.getContentType() : rootCodec.getMimeType();
            writeln("Content-Type: " + ctStr, out);
            writeln(out);
        }
        final ContentType primaryCt = rootCodec.encode(packet, out);
        if (hasAttachments) {
            writeln(out);
            for (final Attachment att : msg.getAttachments()) {
                writeln("--" + boundary, out);
                String cid = att.getContentId();
                if (cid != null && cid.length() > 0 && cid.charAt(0) != '<') {
                    cid = '<' + cid + '>';
                }
                writeln("Content-Id:" + cid, out);
                writeln("Content-Type: " + att.getContentType(), out);
                this.writeCustomMimeHeaders(att, out);
                writeln("Content-Transfer-Encoding: binary", out);
                writeln(out);
                att.writeTo(out);
                writeln(out);
            }
            writeAsAscii("--" + boundary, out);
            writeAsAscii("--", out);
        }
        return hasAttachments ? ctImpl : primaryCt;
    }
    
    private void writeCustomMimeHeaders(final Attachment att, final OutputStream out) throws IOException {
        if (att instanceof AttachmentEx) {
            final Iterator<AttachmentEx.MimeHeader> allMimeHeaders = ((AttachmentEx)att).getMimeHeaders();
            while (allMimeHeaders.hasNext()) {
                final AttachmentEx.MimeHeader mh = allMimeHeaders.next();
                final String name = mh.getName();
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Id".equalsIgnoreCase(name)) {
                    writeln(name + ": " + mh.getValue(), out);
                }
            }
        }
    }
    
    @Override
    public ContentType getStaticContentType(final Packet packet) {
        ContentType ct = (ContentType)packet.getInternalContentType();
        if (ct != null) {
            return ct;
        }
        final Message msg = packet.getMessage();
        final boolean hasAttachments = !msg.getAttachments().isEmpty();
        final Codec rootCodec = this.getMimeRootCodec(packet);
        if (hasAttachments) {
            final String boundary = "uuid:" + UUID.randomUUID().toString();
            final String boundaryParameter = "boundary=\"" + boundary + "\"";
            final String messageContentType = "multipart/related; type=\"" + rootCodec.getMimeType() + "\"; " + boundaryParameter;
            final ContentTypeImpl impl = new ContentTypeImpl(messageContentType, packet.soapAction, null);
            impl.setBoundary(boundary);
            impl.setBoundaryParameter(boundaryParameter);
            packet.setContentType(impl);
            return impl;
        }
        ct = rootCodec.getStaticContentType(packet);
        packet.setContentType(ct);
        return ct;
    }
    
    protected MimeCodec(final MimeCodec that) {
        this.version = that.version;
        this.features = that.features;
    }
    
    @Override
    public void decode(final InputStream in, final String contentType, final Packet packet) throws IOException {
        final MimeMultipartParser parser = new MimeMultipartParser(in, contentType, this.features.get(StreamingAttachmentFeature.class));
        this.decode(parser, packet);
    }
    
    @Override
    public void decode(final ReadableByteChannel in, final String contentType, final Packet packet) {
        throw new UnsupportedOperationException();
    }
    
    protected abstract void decode(final MimeMultipartParser p0, final Packet p1) throws IOException;
    
    @Override
    public abstract MimeCodec copy();
    
    public static void writeln(final String s, final OutputStream out) throws IOException {
        writeAsAscii(s, out);
        writeln(out);
    }
    
    public static void writeAsAscii(final String s, final OutputStream out) throws IOException {
        for (int len = s.length(), i = 0; i < len; ++i) {
            out.write((byte)s.charAt(i));
        }
    }
    
    public static void writeln(final OutputStream out) throws IOException {
        out.write(13);
        out.write(10);
    }
}
