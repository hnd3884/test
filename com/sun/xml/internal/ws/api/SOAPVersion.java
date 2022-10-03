package com.sun.xml.internal.ws.api;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.internal.bind.util.Which;
import javax.xml.soap.SOAPException;
import java.util.Collections;
import java.util.Set;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.namespace.QName;

public enum SOAPVersion
{
    SOAP_11("http://schemas.xmlsoap.org/wsdl/soap/http", "http://schemas.xmlsoap.org/soap/envelope/", "text/xml", "http://schemas.xmlsoap.org/soap/actor/next", "actor", "SOAP 1.1 Protocol", new QName("http://schemas.xmlsoap.org/soap/envelope/", "MustUnderstand"), "Client", "Server", Collections.singleton("http://schemas.xmlsoap.org/soap/actor/next")), 
    SOAP_12("http://www.w3.org/2003/05/soap/bindings/HTTP/", "http://www.w3.org/2003/05/soap-envelope", "application/soap+xml", "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver", "role", "SOAP 1.2 Protocol", new QName("http://www.w3.org/2003/05/soap-envelope", "MustUnderstand"), "Sender", "Receiver", (Set<String>)new HashSet<String>(Arrays.asList("http://www.w3.org/2003/05/soap-envelope/role/next", "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver")));
    
    public final String httpBindingId;
    public final String nsUri;
    public final String contentType;
    public final QName faultCodeMustUnderstand;
    @Deprecated
    public final MessageFactory saajMessageFactory;
    @Deprecated
    public final SOAPFactory saajSoapFactory;
    private final String saajFactoryString;
    public final String implicitRole;
    public final Set<String> implicitRoleSet;
    public final Set<String> requiredRoles;
    public final String roleAttributeName;
    public final QName faultCodeClient;
    public final QName faultCodeServer;
    
    private SOAPVersion(final String httpBindingId, final String nsUri, final String contentType, final String implicitRole, final String roleAttributeName, final String saajFactoryString, final QName faultCodeMustUnderstand, final String faultCodeClientLocalName, final String faultCodeServerLocalName, final Set<String> requiredRoles) {
        this.httpBindingId = httpBindingId;
        this.nsUri = nsUri;
        this.contentType = contentType;
        this.implicitRole = implicitRole;
        this.implicitRoleSet = Collections.singleton(implicitRole);
        this.roleAttributeName = roleAttributeName;
        this.saajFactoryString = saajFactoryString;
        try {
            this.saajMessageFactory = MessageFactory.newInstance(saajFactoryString);
            this.saajSoapFactory = SOAPFactory.newInstance(saajFactoryString);
        }
        catch (final SOAPException e) {
            throw new Error(e);
        }
        catch (final NoSuchMethodError e2) {
            final LinkageError x = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
            x.initCause(e2);
            throw x;
        }
        this.faultCodeMustUnderstand = faultCodeMustUnderstand;
        this.requiredRoles = requiredRoles;
        this.faultCodeClient = new QName(nsUri, faultCodeClientLocalName);
        this.faultCodeServer = new QName(nsUri, faultCodeServerLocalName);
    }
    
    public SOAPFactory getSOAPFactory() {
        try {
            return SAAJFactory.getSOAPFactory(this.saajFactoryString);
        }
        catch (final SOAPException e) {
            throw new Error(e);
        }
        catch (final NoSuchMethodError e2) {
            final LinkageError x = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
            x.initCause(e2);
            throw x;
        }
    }
    
    public MessageFactory getMessageFactory() {
        try {
            return SAAJFactory.getMessageFactory(this.saajFactoryString);
        }
        catch (final SOAPException e) {
            throw new Error(e);
        }
        catch (final NoSuchMethodError e2) {
            final LinkageError x = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
            x.initCause(e2);
            throw x;
        }
    }
    
    @Override
    public String toString() {
        return this.httpBindingId;
    }
    
    public static SOAPVersion fromHttpBinding(final String binding) {
        if (binding == null) {
            return SOAPVersion.SOAP_11;
        }
        if (binding.equals(SOAPVersion.SOAP_12.httpBindingId)) {
            return SOAPVersion.SOAP_12;
        }
        return SOAPVersion.SOAP_11;
    }
    
    public static SOAPVersion fromNsUri(final String nsUri) {
        if (nsUri.equals(SOAPVersion.SOAP_12.nsUri)) {
            return SOAPVersion.SOAP_12;
        }
        return SOAPVersion.SOAP_11;
    }
    
    public static SOAPVersion from(final EnvelopeStyleFeature f) {
        final EnvelopeStyle.Style[] style = f.getStyles();
        if (style.length != 1) {
            throw new IllegalArgumentException("The EnvelopingFeature must has exactly one Enveloping.Style");
        }
        return from(style[0]);
    }
    
    public static SOAPVersion from(final EnvelopeStyle.Style style) {
        switch (style) {
            case SOAP11: {
                return SOAPVersion.SOAP_11;
            }
            case SOAP12: {
                return SOAPVersion.SOAP_12;
            }
            default: {
                return SOAPVersion.SOAP_11;
            }
        }
    }
    
    public EnvelopeStyleFeature toFeature() {
        return SOAPVersion.SOAP_11.equals(this) ? new EnvelopeStyleFeature(new EnvelopeStyle.Style[] { EnvelopeStyle.Style.SOAP11 }) : new EnvelopeStyleFeature(new EnvelopeStyle.Style[] { EnvelopeStyle.Style.SOAP12 });
    }
}
