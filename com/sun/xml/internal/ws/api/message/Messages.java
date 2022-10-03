package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.Nullable;
import javax.xml.ws.ProtocolException;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;
import com.sun.xml.internal.ws.message.ProblemActionHeader;
import javax.xml.soap.SOAPConstants;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.WSBinding;
import javax.xml.soap.SOAPFault;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderException;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.pipe.Codecs;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.message.source.ProtocolSourceMessage;
import org.w3c.dom.Node;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.message.DOMMessage;
import org.w3c.dom.Element;
import com.sun.xml.internal.ws.message.stream.PayloadStreamReaderMessage;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Source;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import javax.xml.soap.SOAPMessage;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import javax.xml.bind.Marshaller;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.bind.JAXBContext;

public abstract class Messages
{
    private Messages() {
    }
    
    @Deprecated
    public static Message create(final JAXBContext context, final Object jaxbObject, final SOAPVersion soapVersion) {
        return JAXBMessage.create(context, jaxbObject, soapVersion);
    }
    
    @Deprecated
    public static Message createRaw(final JAXBContext context, final Object jaxbObject, final SOAPVersion soapVersion) {
        return JAXBMessage.createRaw(context, jaxbObject, soapVersion);
    }
    
    @Deprecated
    public static Message create(final Marshaller marshaller, final Object jaxbObject, final SOAPVersion soapVersion) {
        return create(BindingContextFactory.getBindingContext(marshaller).getJAXBContext(), jaxbObject, soapVersion);
    }
    
    public static Message create(final SOAPMessage saaj) {
        return SAAJFactory.create(saaj);
    }
    
    public static Message createUsingPayload(final Source payload, final SOAPVersion ver) {
        if (payload instanceof DOMSource) {
            if (((DOMSource)payload).getNode() == null) {
                return new EmptyMessageImpl(ver);
            }
        }
        else if (payload instanceof StreamSource) {
            final StreamSource ss = (StreamSource)payload;
            if (ss.getInputStream() == null && ss.getReader() == null && ss.getSystemId() == null) {
                return new EmptyMessageImpl(ver);
            }
        }
        else if (payload instanceof SAXSource) {
            final SAXSource ss2 = (SAXSource)payload;
            if (ss2.getInputSource() == null && ss2.getXMLReader() == null) {
                return new EmptyMessageImpl(ver);
            }
        }
        return new PayloadSourceMessage(payload, ver);
    }
    
    public static Message createUsingPayload(final XMLStreamReader payload, final SOAPVersion ver) {
        return new PayloadStreamReaderMessage(payload, ver);
    }
    
    public static Message createUsingPayload(final Element payload, final SOAPVersion ver) {
        return new DOMMessage(ver, payload);
    }
    
    public static Message create(final Element soapEnvelope) {
        final SOAPVersion ver = SOAPVersion.fromNsUri(soapEnvelope.getNamespaceURI());
        final Element header = DOMUtil.getFirstChild(soapEnvelope, ver.nsUri, "Header");
        HeaderList headers = null;
        if (header != null) {
            for (Node n = header.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeType() == 1) {
                    if (headers == null) {
                        headers = new HeaderList(ver);
                    }
                    headers.add(Headers.create((Element)n));
                }
            }
        }
        final Element body = DOMUtil.getFirstChild(soapEnvelope, ver.nsUri, "Body");
        if (body == null) {
            throw new WebServiceException("Message doesn't have <S:Body> " + soapEnvelope);
        }
        final Element payload = DOMUtil.getFirstChild(soapEnvelope, ver.nsUri, "Body");
        if (payload == null) {
            return new EmptyMessageImpl(headers, new AttachmentSetImpl(), ver);
        }
        return new DOMMessage(ver, headers, payload);
    }
    
    public static Message create(final Source envelope, final SOAPVersion soapVersion) {
        return new ProtocolSourceMessage(envelope, soapVersion);
    }
    
    public static Message createEmpty(final SOAPVersion soapVersion) {
        return new EmptyMessageImpl(soapVersion);
    }
    
    @NotNull
    public static Message create(@NotNull final XMLStreamReader reader) {
        if (reader.getEventType() != 1) {
            XMLStreamReaderUtil.nextElementContent(reader);
        }
        assert reader.getEventType() == 1 : reader.getEventType();
        final SOAPVersion ver = SOAPVersion.fromNsUri(reader.getNamespaceURI());
        return Codecs.createSOAPEnvelopeXmlCodec(ver).decode(reader);
    }
    
    @NotNull
    public static Message create(@NotNull final XMLStreamBuffer xsb) {
        try {
            return create(xsb.readAsXMLStreamReader());
        }
        catch (final XMLStreamException e) {
            throw new XMLStreamReaderException(e);
        }
    }
    
    public static Message create(final Throwable t, final SOAPVersion soapVersion) {
        return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, t);
    }
    
    public static Message create(final SOAPFault fault) {
        final SOAPVersion ver = SOAPVersion.fromNsUri(fault.getNamespaceURI());
        return new DOMMessage(ver, fault);
    }
    
    @Deprecated
    public static Message createAddressingFaultMessage(final WSBinding binding, final QName missingHeader) {
        return createAddressingFaultMessage(binding, null, missingHeader);
    }
    
    public static Message createAddressingFaultMessage(final WSBinding binding, final Packet p, final QName missingHeader) {
        final AddressingVersion av = binding.getAddressingVersion();
        if (av == null) {
            throw new WebServiceException(AddressingMessages.ADDRESSING_SHOULD_BE_ENABLED());
        }
        final WsaTubeHelper helper = av.getWsaHelper(null, null, binding);
        return create(helper.newMapRequiredFault(new MissingAddressingHeaderException(missingHeader, p)));
    }
    
    public static Message create(@NotNull final String unsupportedAction, @NotNull final AddressingVersion av, @NotNull final SOAPVersion sv) {
        final QName subcode = av.actionNotSupportedTag;
        final String faultstring = String.format(av.actionNotSupportedText, unsupportedAction);
        Message faultMessage;
        try {
            SOAPFault fault;
            if (sv == SOAPVersion.SOAP_12) {
                fault = SOAPVersion.SOAP_12.getSOAPFactory().createFault();
                fault.setFaultCode(SOAPConstants.SOAP_SENDER_FAULT);
                fault.appendFaultSubcode(subcode);
                final Detail detail = fault.addDetail();
                SOAPElement se = detail.addChildElement(av.problemActionTag);
                se = se.addChildElement(av.actionTag);
                se.addTextNode(unsupportedAction);
            }
            else {
                fault = SOAPVersion.SOAP_11.getSOAPFactory().createFault();
                fault.setFaultCode(subcode);
            }
            fault.setFaultString(faultstring);
            faultMessage = SOAPFaultBuilder.createSOAPFaultMessage(sv, fault);
            if (sv == SOAPVersion.SOAP_11) {
                faultMessage.getHeaders().add(new ProblemActionHeader(unsupportedAction, av));
            }
        }
        catch (final SOAPException e) {
            throw new WebServiceException(e);
        }
        return faultMessage;
    }
    
    @NotNull
    public static Message create(@NotNull final SOAPVersion soapVersion, @NotNull final ProtocolException pex, @Nullable final QName faultcode) {
        return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, pex, faultcode);
    }
}
