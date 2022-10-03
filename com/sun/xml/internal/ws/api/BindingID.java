package com.sun.xml.internal.ws.api;

import javax.xml.ws.soap.MTOMFeature;
import com.sun.xml.internal.ws.encoding.SOAPBindingCodec;
import java.util.HashMap;
import java.util.Map;
import com.sun.xml.internal.ws.encoding.XMLHTTPBindingCodec;
import javax.xml.ws.BindingType;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Iterator;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.api.pipe.Codec;
import javax.xml.ws.WebServiceFeature;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.binding.BindingImpl;

public abstract class BindingID
{
    public static final SOAPHTTPImpl X_SOAP12_HTTP;
    public static final SOAPHTTPImpl SOAP12_HTTP;
    public static final SOAPHTTPImpl SOAP11_HTTP;
    public static final SOAPHTTPImpl SOAP12_HTTP_MTOM;
    public static final SOAPHTTPImpl SOAP11_HTTP_MTOM;
    public static final BindingID XML_HTTP;
    private static final BindingID REST_HTTP;
    
    @NotNull
    public final WSBinding createBinding() {
        return BindingImpl.create(this);
    }
    
    @NotNull
    public String getTransport() {
        return "http://schemas.xmlsoap.org/soap/http";
    }
    
    @NotNull
    public final WSBinding createBinding(final WebServiceFeature... features) {
        return BindingImpl.create(this, features);
    }
    
    @NotNull
    public final WSBinding createBinding(final WSFeatureList features) {
        return this.createBinding(features.toArray());
    }
    
    public abstract SOAPVersion getSOAPVersion();
    
    @NotNull
    public abstract Codec createEncoder(@NotNull final WSBinding p0);
    
    @Override
    public abstract String toString();
    
    public WebServiceFeatureList createBuiltinFeatureList() {
        return new WebServiceFeatureList();
    }
    
    public boolean canGenerateWSDL() {
        return false;
    }
    
    public String getParameter(final String parameterName, final String defaultValue) {
        return defaultValue;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof BindingID && this.toString().equals(obj.toString());
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
    
    @NotNull
    public static BindingID parse(final String lexical) {
        if (lexical.equals(BindingID.XML_HTTP.toString())) {
            return BindingID.XML_HTTP;
        }
        if (lexical.equals(BindingID.REST_HTTP.toString())) {
            return BindingID.REST_HTTP;
        }
        if (belongsTo(lexical, BindingID.SOAP11_HTTP.toString())) {
            return customize(lexical, BindingID.SOAP11_HTTP);
        }
        if (belongsTo(lexical, BindingID.SOAP12_HTTP.toString())) {
            return customize(lexical, BindingID.SOAP12_HTTP);
        }
        if (belongsTo(lexical, "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")) {
            return customize(lexical, BindingID.X_SOAP12_HTTP);
        }
        for (final BindingIDFactory f : ServiceFinder.find(BindingIDFactory.class)) {
            final BindingID r = f.parse(lexical);
            if (r != null) {
                return r;
            }
        }
        throw new WebServiceException("Wrong binding ID: " + lexical);
    }
    
    private static boolean belongsTo(final String lexical, final String id) {
        return lexical.equals(id) || lexical.startsWith(id + '?');
    }
    
    private static SOAPHTTPImpl customize(final String lexical, final SOAPHTTPImpl base) {
        if (lexical.equals(base.toString())) {
            return base;
        }
        final SOAPHTTPImpl r = new SOAPHTTPImpl(base.getSOAPVersion(), lexical, base.canGenerateWSDL());
        try {
            if (lexical.indexOf(63) == -1) {
                return r;
            }
            final String query = URLDecoder.decode(lexical.substring(lexical.indexOf(63) + 1), "UTF-8");
            for (final String token : query.split("&")) {
                final int idx = token.indexOf(61);
                if (idx < 0) {
                    throw new WebServiceException("Malformed binding ID (no '=' in " + token + ")");
                }
                r.parameters.put(token.substring(0, idx), token.substring(idx + 1));
            }
        }
        catch (final UnsupportedEncodingException e) {
            throw new AssertionError((Object)e);
        }
        return r;
    }
    
    @NotNull
    public static BindingID parse(final Class<?> implClass) {
        final BindingType bindingType = implClass.getAnnotation(BindingType.class);
        if (bindingType != null) {
            final String bindingId = bindingType.value();
            if (bindingId.length() > 0) {
                return parse(bindingId);
            }
        }
        return BindingID.SOAP11_HTTP;
    }
    
    static {
        X_SOAP12_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/", true);
        SOAP12_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://www.w3.org/2003/05/soap/bindings/HTTP/", true);
        SOAP11_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_11, "http://schemas.xmlsoap.org/wsdl/soap/http", true);
        SOAP12_HTTP_MTOM = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true", true, true);
        SOAP11_HTTP_MTOM = new SOAPHTTPImpl(SOAPVersion.SOAP_11, "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true", true, true);
        XML_HTTP = new Impl(SOAPVersion.SOAP_11, "http://www.w3.org/2004/08/wsdl/http", false) {
            @Override
            public Codec createEncoder(final WSBinding binding) {
                return new XMLHTTPBindingCodec(binding.getFeatures());
            }
        };
        REST_HTTP = new Impl(SOAPVersion.SOAP_11, "http://jax-ws.dev.java.net/rest", true) {
            @Override
            public Codec createEncoder(final WSBinding binding) {
                return new XMLHTTPBindingCodec(binding.getFeatures());
            }
        };
    }
    
    private abstract static class Impl extends BindingID
    {
        final SOAPVersion version;
        private final String lexical;
        private final boolean canGenerateWSDL;
        
        public Impl(final SOAPVersion version, final String lexical, final boolean canGenerateWSDL) {
            this.version = version;
            this.lexical = lexical;
            this.canGenerateWSDL = canGenerateWSDL;
        }
        
        @Override
        public SOAPVersion getSOAPVersion() {
            return this.version;
        }
        
        @Override
        public String toString() {
            return this.lexical;
        }
        
        @Deprecated
        @Override
        public boolean canGenerateWSDL() {
            return this.canGenerateWSDL;
        }
    }
    
    private static final class SOAPHTTPImpl extends Impl implements Cloneable
    {
        Map<String, String> parameters;
        static final String MTOM_PARAM = "mtom";
        
        public SOAPHTTPImpl(final SOAPVersion version, final String lexical, final boolean canGenerateWSDL) {
            super(version, lexical, canGenerateWSDL);
            this.parameters = new HashMap<String, String>();
        }
        
        public SOAPHTTPImpl(final SOAPVersion version, final String lexical, final boolean canGenerateWSDL, final boolean mtomEnabled) {
            this(version, lexical, canGenerateWSDL);
            final String mtomStr = mtomEnabled ? "true" : "false";
            this.parameters.put("mtom", mtomStr);
        }
        
        @NotNull
        @Override
        public Codec createEncoder(final WSBinding binding) {
            return new SOAPBindingCodec(binding.getFeatures());
        }
        
        private Boolean isMTOMEnabled() {
            final String mtom = this.parameters.get("mtom");
            return (mtom == null) ? null : Boolean.valueOf(mtom);
        }
        
        @Override
        public WebServiceFeatureList createBuiltinFeatureList() {
            final WebServiceFeatureList r = super.createBuiltinFeatureList();
            final Boolean mtom = this.isMTOMEnabled();
            if (mtom != null) {
                r.add(new MTOMFeature(mtom));
            }
            return r;
        }
        
        @Override
        public String getParameter(final String parameterName, final String defaultValue) {
            if (this.parameters.get(parameterName) == null) {
                return super.getParameter(parameterName, defaultValue);
            }
            return this.parameters.get(parameterName);
        }
        
        public SOAPHTTPImpl clone() throws CloneNotSupportedException {
            return (SOAPHTTPImpl)super.clone();
        }
    }
}
