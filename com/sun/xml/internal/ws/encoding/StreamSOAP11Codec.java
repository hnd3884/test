package com.sun.xml.internal.ws.encoding;

import java.util.Collections;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.SOAPVersion;
import java.util.List;

final class StreamSOAP11Codec extends StreamSOAPCodec
{
    public static final String SOAP11_MIME_TYPE = "text/xml";
    public static final String DEFAULT_SOAP11_CONTENT_TYPE = "text/xml; charset=utf-8";
    private static final List<String> EXPECTED_CONTENT_TYPES;
    
    StreamSOAP11Codec() {
        super(SOAPVersion.SOAP_11);
    }
    
    StreamSOAP11Codec(final WSBinding binding) {
        super(binding);
    }
    
    StreamSOAP11Codec(final WSFeatureList features) {
        super(features);
    }
    
    @Override
    public String getMimeType() {
        return "text/xml";
    }
    
    @Override
    protected ContentType getContentType(final Packet packet) {
        final ContentTypeImpl.Builder b = this.getContenTypeBuilder(packet);
        b.soapAction = packet.soapAction;
        return b.build();
    }
    
    @Override
    protected String getDefaultContentType() {
        return "text/xml; charset=utf-8";
    }
    
    @Override
    protected List<String> getExpectedContentTypes() {
        return StreamSOAP11Codec.EXPECTED_CONTENT_TYPES;
    }
    
    static {
        EXPECTED_CONTENT_TYPES = Collections.singletonList("text/xml");
    }
}
