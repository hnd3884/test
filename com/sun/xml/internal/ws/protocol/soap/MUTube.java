package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.message.Header;
import javax.xml.soap.SOAPElement;
import java.util.Iterator;
import com.sun.xml.internal.ws.message.DOMHeader;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPException;
import javax.xml.ws.soap.SOAPFaultException;
import java.util.Set;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.api.SOAPVersion;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;

abstract class MUTube extends AbstractFilterTubeImpl
{
    private static final String MU_FAULT_DETAIL_LOCALPART = "NotUnderstood";
    private static final QName MU_HEADER_DETAIL;
    protected static final Logger logger;
    private static final String MUST_UNDERSTAND_FAULT_MESSAGE_STRING = "One or more mandatory SOAP header blocks not understood";
    protected final SOAPVersion soapVersion;
    protected SOAPBindingImpl binding;
    
    protected MUTube(final WSBinding binding, final Tube next) {
        super(next);
        if (!(binding instanceof SOAPBinding)) {
            throw new WebServiceException("MUPipe should n't be used for bindings other than SOAP.");
        }
        this.binding = (SOAPBindingImpl)binding;
        this.soapVersion = binding.getSOAPVersion();
    }
    
    protected MUTube(final MUTube that, final TubeCloner cloner) {
        super(that, cloner);
        this.binding = that.binding;
        this.soapVersion = that.soapVersion;
    }
    
    public final Set<QName> getMisUnderstoodHeaders(final MessageHeaders headers, final Set<String> roles, final Set<QName> handlerKnownHeaders) {
        return headers.getNotUnderstoodHeaders(roles, handlerKnownHeaders, this.binding);
    }
    
    final SOAPFaultException createMUSOAPFaultException(final Set<QName> notUnderstoodHeaders) {
        try {
            final SOAPFault fault = this.soapVersion.getSOAPFactory().createFault("One or more mandatory SOAP header blocks not understood", this.soapVersion.faultCodeMustUnderstand);
            fault.setFaultString("MustUnderstand headers:" + notUnderstoodHeaders + " are not understood");
            return new SOAPFaultException(fault);
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
    }
    
    final Message createMUSOAPFaultMessage(final Set<QName> notUnderstoodHeaders) {
        try {
            String faultString = "One or more mandatory SOAP header blocks not understood";
            if (this.soapVersion == SOAPVersion.SOAP_11) {
                faultString = "MustUnderstand headers:" + notUnderstoodHeaders + " are not understood";
            }
            final Message muFaultMessage = SOAPFaultBuilder.createSOAPFaultMessage(this.soapVersion, faultString, this.soapVersion.faultCodeMustUnderstand);
            if (this.soapVersion == SOAPVersion.SOAP_12) {
                addHeader(muFaultMessage, notUnderstoodHeaders);
            }
            return muFaultMessage;
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
    }
    
    private static void addHeader(final Message m, final Set<QName> notUnderstoodHeaders) throws SOAPException {
        for (final QName qname : notUnderstoodHeaders) {
            final SOAPElement soapEl = SOAPVersion.SOAP_12.getSOAPFactory().createElement(MUTube.MU_HEADER_DETAIL);
            soapEl.addNamespaceDeclaration("abc", qname.getNamespaceURI());
            soapEl.setAttribute("qname", "abc:" + qname.getLocalPart());
            final Header header = new DOMHeader<Object>(soapEl);
            m.getHeaders().add(header);
        }
    }
    
    static {
        MU_HEADER_DETAIL = new QName(SOAPVersion.SOAP_12.nsUri, "NotUnderstood");
        logger = Logger.getLogger("com.sun.xml.internal.ws.soap.decoder");
    }
}
