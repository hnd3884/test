package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSFeatureList;

public abstract class Codecs
{
    @NotNull
    public static SOAPBindingCodec createSOAPBindingCodec(final WSFeatureList feature) {
        return new com.sun.xml.internal.ws.encoding.SOAPBindingCodec(feature);
    }
    
    @NotNull
    public static Codec createXMLCodec(final WSFeatureList feature) {
        return new XMLHTTPBindingCodec(feature);
    }
    
    @NotNull
    public static SOAPBindingCodec createSOAPBindingCodec(final WSBinding binding, final StreamSOAPCodec xmlEnvelopeCodec) {
        return new com.sun.xml.internal.ws.encoding.SOAPBindingCodec(binding.getFeatures(), xmlEnvelopeCodec);
    }
    
    @NotNull
    public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull final SOAPVersion version) {
        return com.sun.xml.internal.ws.encoding.StreamSOAPCodec.create(version);
    }
    
    @NotNull
    @Deprecated
    public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull final WSBinding binding) {
        return com.sun.xml.internal.ws.encoding.StreamSOAPCodec.create(binding);
    }
    
    @NotNull
    public static StreamSOAPCodec createSOAPEnvelopeXmlCodec(@NotNull final WSFeatureList features) {
        return com.sun.xml.internal.ws.encoding.StreamSOAPCodec.create(features);
    }
}
