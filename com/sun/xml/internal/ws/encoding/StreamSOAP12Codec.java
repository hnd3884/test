package com.sun.xml.internal.ws.encoding;

import java.util.Collections;
import java.io.IOException;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import java.io.InputStream;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.SOAPVersion;
import java.util.List;

final class StreamSOAP12Codec extends StreamSOAPCodec
{
    public static final String SOAP12_MIME_TYPE = "application/soap+xml";
    public static final String DEFAULT_SOAP12_CONTENT_TYPE = "application/soap+xml; charset=utf-8";
    private static final List<String> EXPECTED_CONTENT_TYPES;
    
    StreamSOAP12Codec() {
        super(SOAPVersion.SOAP_12);
    }
    
    StreamSOAP12Codec(final WSBinding binding) {
        super(binding);
    }
    
    StreamSOAP12Codec(final WSFeatureList features) {
        super(features);
    }
    
    @Override
    public String getMimeType() {
        return "application/soap+xml";
    }
    
    @Override
    protected ContentType getContentType(final Packet packet) {
        final ContentTypeImpl.Builder b = this.getContenTypeBuilder(packet);
        if (packet.soapAction == null) {
            return b.build();
        }
        b.contentType = b.contentType + ";action=" + this.fixQuotesAroundSoapAction(packet.soapAction);
        return b.build();
    }
    
    @Override
    public void decode(final InputStream in, final String contentType, final Packet packet, final AttachmentSet att) throws IOException {
        final com.sun.xml.internal.ws.encoding.ContentType ct = new com.sun.xml.internal.ws.encoding.ContentType(contentType);
        packet.soapAction = this.fixQuotesAroundSoapAction(ct.getParameter("action"));
        super.decode(in, contentType, packet, att);
    }
    
    private String fixQuotesAroundSoapAction(final String soapAction) {
        if (soapAction != null && (!soapAction.startsWith("\"") || !soapAction.endsWith("\""))) {
            String fixedSoapAction = soapAction;
            if (!soapAction.startsWith("\"")) {
                fixedSoapAction = "\"" + fixedSoapAction;
            }
            if (!soapAction.endsWith("\"")) {
                fixedSoapAction += "\"";
            }
            return fixedSoapAction;
        }
        return soapAction;
    }
    
    @Override
    protected List<String> getExpectedContentTypes() {
        return StreamSOAP12Codec.EXPECTED_CONTENT_TYPES;
    }
    
    @Override
    protected String getDefaultContentType() {
        return "application/soap+xml; charset=utf-8";
    }
    
    static {
        EXPECTED_CONTENT_TYPES = Collections.singletonList("application/soap+xml");
    }
}
