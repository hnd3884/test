package com.sun.xml.internal.ws.api.message;

import javax.xml.ws.soap.MTOMFeature;
import java.util.Iterator;
import java.util.ArrayList;
import javax.xml.soap.MimeHeader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import javax.xml.soap.MimeHeaders;
import java.io.IOException;
import java.io.InputStream;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.transform.Source;
import javax.xml.soap.SOAPMessage;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import javax.xml.ws.WebServiceFeature;
import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.WSFeatureList;

public class MessageContextFactory extends com.oracle.webservices.internal.api.message.MessageContextFactory
{
    private WSFeatureList features;
    private Codec soapCodec;
    private Codec xmlCodec;
    private EnvelopeStyleFeature envelopeStyle;
    private EnvelopeStyle.Style singleSoapStyle;
    
    public MessageContextFactory(final WebServiceFeature[] wsf) {
        this(new WebServiceFeatureList(wsf));
    }
    
    public MessageContextFactory(final WSFeatureList wsf) {
        this.features = wsf;
        this.envelopeStyle = this.features.get(EnvelopeStyleFeature.class);
        if (this.envelopeStyle == null) {
            this.envelopeStyle = new EnvelopeStyleFeature(new EnvelopeStyle.Style[] { EnvelopeStyle.Style.SOAP11 });
            this.features.mergeFeatures(new WebServiceFeature[] { this.envelopeStyle }, false);
        }
        for (final EnvelopeStyle.Style s : this.envelopeStyle.getStyles()) {
            if (s.isXML()) {
                if (this.xmlCodec == null) {
                    this.xmlCodec = Codecs.createXMLCodec(this.features);
                }
            }
            else {
                if (this.soapCodec == null) {
                    this.soapCodec = Codecs.createSOAPBindingCodec(this.features);
                }
                this.singleSoapStyle = s;
            }
        }
    }
    
    @Override
    protected com.oracle.webservices.internal.api.message.MessageContextFactory newFactory(final WebServiceFeature... f) {
        return new MessageContextFactory(f);
    }
    
    @Override
    public MessageContext createContext() {
        return this.packet(null);
    }
    
    @Override
    public MessageContext createContext(final SOAPMessage soap) {
        this.throwIfIllegalMessageArgument(soap);
        return this.packet(Messages.create(soap));
    }
    
    @Override
    public MessageContext createContext(final Source m, final EnvelopeStyle.Style envelopeStyle) {
        this.throwIfIllegalMessageArgument(m);
        return this.packet(Messages.create(m, SOAPVersion.from(envelopeStyle)));
    }
    
    @Override
    public MessageContext createContext(final Source m) {
        this.throwIfIllegalMessageArgument(m);
        return this.packet(Messages.create(m, SOAPVersion.from(this.singleSoapStyle)));
    }
    
    @Override
    public MessageContext createContext(final InputStream in, final String contentType) throws IOException {
        this.throwIfIllegalMessageArgument(in);
        final Packet p = this.packet(null);
        this.soapCodec.decode(in, contentType, p);
        return p;
    }
    
    @Deprecated
    @Override
    public MessageContext createContext(final InputStream in, final MimeHeaders headers) throws IOException {
        final String contentType = getHeader(headers, "Content-Type");
        final Packet packet = (Packet)this.createContext(in, contentType);
        packet.acceptableMimeTypes = getHeader(headers, "Accept");
        packet.soapAction = HttpAdapter.fixQuotesAroundSoapAction(getHeader(headers, "SOAPAction"));
        return packet;
    }
    
    static String getHeader(final MimeHeaders headers, final String name) {
        final String[] values = headers.getHeader(name);
        return (values != null && values.length > 0) ? values[0] : null;
    }
    
    static Map<String, List<String>> toMap(final MimeHeaders headers) {
        final HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        final Iterator<MimeHeader> i = headers.getAllHeaders();
        while (i.hasNext()) {
            final MimeHeader mh = i.next();
            List<String> values = map.get(mh.getName());
            if (values == null) {
                values = new ArrayList<String>();
                map.put(mh.getName(), values);
            }
            values.add(mh.getValue());
        }
        return map;
    }
    
    public MessageContext createContext(final Message m) {
        this.throwIfIllegalMessageArgument(m);
        return this.packet(m);
    }
    
    private Packet packet(final Message m) {
        final Packet p = new Packet();
        p.codec = this.soapCodec;
        if (m != null) {
            p.setMessage(m);
        }
        final MTOMFeature mf = this.features.get(MTOMFeature.class);
        if (mf != null) {
            p.setMtomFeature(mf);
        }
        return p;
    }
    
    private void throwIfIllegalMessageArgument(final Object message) throws IllegalArgumentException {
        if (message == null) {
            throw new IllegalArgumentException("null messages are not allowed.  Consider using MessageContextFactory.createContext()");
        }
    }
    
    @Deprecated
    @Override
    public MessageContext doCreate() {
        return this.packet(null);
    }
    
    @Deprecated
    @Override
    public MessageContext doCreate(final SOAPMessage m) {
        return this.createContext(m);
    }
    
    @Deprecated
    @Override
    public MessageContext doCreate(final Source x, final SOAPVersion soapVersion) {
        return this.packet(Messages.create(x, soapVersion));
    }
}
