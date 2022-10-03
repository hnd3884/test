package com.sun.xml.internal.ws.encoding;

import java.io.OutputStream;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import java.nio.channels.WritableByteChannel;
import java.io.IOException;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.message.Attachment;
import java.util.Map;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.message.MimeAttachmentSet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.SOAPVersion;

public final class SwACodec extends MimeCodec
{
    public SwACodec(final SOAPVersion version, final WSFeatureList f, final Codec rootCodec) {
        super(version, f);
        this.mimeRootCodec = rootCodec;
    }
    
    private SwACodec(final SwACodec that) {
        super(that);
        this.mimeRootCodec = that.mimeRootCodec.copy();
    }
    
    @Override
    protected void decode(final MimeMultipartParser mpp, final Packet packet) throws IOException {
        final Attachment root = mpp.getRootPart();
        final Codec rootCodec = this.getMimeRootCodec(packet);
        if (rootCodec instanceof RootOnlyCodec) {
            ((RootOnlyCodec)rootCodec).decode(root.asInputStream(), root.getContentType(), packet, new MimeAttachmentSet(mpp));
        }
        else {
            rootCodec.decode(root.asInputStream(), root.getContentType(), packet);
            final Map<String, Attachment> atts = mpp.getAttachmentParts();
            for (final Map.Entry<String, Attachment> att : atts.entrySet()) {
                packet.getMessage().getAttachments().add(att.getValue());
            }
        }
    }
    
    @Override
    public ContentType encode(final Packet packet, final WritableByteChannel buffer) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SwACodec copy() {
        return new SwACodec(this);
    }
}
