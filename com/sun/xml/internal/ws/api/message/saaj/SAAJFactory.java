package com.sun.xml.internal.ws.api.message.saaj;

import javax.xml.soap.AttachmentPart;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.Attachment;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import org.w3c.dom.Node;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.message.saaj.SAAJMessage;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPException;
import java.util.Iterator;
import com.sun.xml.internal.ws.util.ServiceFinder;
import javax.xml.soap.MessageFactory;

public class SAAJFactory
{
    private static final SAAJFactory instance;
    
    public static MessageFactory getMessageFactory(final String protocol) throws SOAPException {
        for (final SAAJFactory s : ServiceFinder.find(SAAJFactory.class)) {
            final MessageFactory mf = s.createMessageFactory(protocol);
            if (mf != null) {
                return mf;
            }
        }
        return SAAJFactory.instance.createMessageFactory(protocol);
    }
    
    public static SOAPFactory getSOAPFactory(final String protocol) throws SOAPException {
        for (final SAAJFactory s : ServiceFinder.find(SAAJFactory.class)) {
            final SOAPFactory sf = s.createSOAPFactory(protocol);
            if (sf != null) {
                return sf;
            }
        }
        return SAAJFactory.instance.createSOAPFactory(protocol);
    }
    
    public static Message create(final SOAPMessage saaj) {
        for (final SAAJFactory s : ServiceFinder.find(SAAJFactory.class)) {
            final Message m = s.createMessage(saaj);
            if (m != null) {
                return m;
            }
        }
        return SAAJFactory.instance.createMessage(saaj);
    }
    
    public static SOAPMessage read(final SOAPVersion soapVersion, final Message message) throws SOAPException {
        for (final SAAJFactory s : ServiceFinder.find(SAAJFactory.class)) {
            final SOAPMessage msg = s.readAsSOAPMessage(soapVersion, message);
            if (msg != null) {
                return msg;
            }
        }
        return SAAJFactory.instance.readAsSOAPMessage(soapVersion, message);
    }
    
    public static SOAPMessage read(final SOAPVersion soapVersion, final Message message, final Packet packet) throws SOAPException {
        for (final SAAJFactory s : ServiceFinder.find(SAAJFactory.class)) {
            final SOAPMessage msg = s.readAsSOAPMessage(soapVersion, message, packet);
            if (msg != null) {
                return msg;
            }
        }
        return SAAJFactory.instance.readAsSOAPMessage(soapVersion, message, packet);
    }
    
    public static SAAJMessage read(final Packet packet) throws SOAPException {
        final ServiceFinder<SAAJFactory> factories = (packet.component != null) ? ServiceFinder.find(SAAJFactory.class, packet.component) : ServiceFinder.find(SAAJFactory.class);
        for (final SAAJFactory s : factories) {
            final SAAJMessage msg = s.readAsSAAJ(packet);
            if (msg != null) {
                return msg;
            }
        }
        return SAAJFactory.instance.readAsSAAJ(packet);
    }
    
    public SAAJMessage readAsSAAJ(final Packet packet) throws SOAPException {
        final SOAPVersion v = packet.getMessage().getSOAPVersion();
        final SOAPMessage msg = this.readAsSOAPMessage(v, packet.getMessage());
        return new SAAJMessage(msg);
    }
    
    public MessageFactory createMessageFactory(final String protocol) throws SOAPException {
        return MessageFactory.newInstance(protocol);
    }
    
    public SOAPFactory createSOAPFactory(final String protocol) throws SOAPException {
        return SOAPFactory.newInstance(protocol);
    }
    
    public Message createMessage(final SOAPMessage saaj) {
        return new SAAJMessage(saaj);
    }
    
    public SOAPMessage readAsSOAPMessage(final SOAPVersion soapVersion, final Message message) throws SOAPException {
        SOAPMessage msg = soapVersion.getMessageFactory().createMessage();
        final SaajStaxWriter writer = new SaajStaxWriter(msg);
        try {
            message.writeTo(writer);
        }
        catch (final XMLStreamException e) {
            throw (e.getCause() instanceof SOAPException) ? e.getCause() : new SOAPException(e);
        }
        msg = writer.getSOAPMessage();
        addAttachmentsToSOAPMessage(msg, message);
        if (msg.saveRequired()) {
            msg.saveChanges();
        }
        return msg;
    }
    
    public SOAPMessage readAsSOAPMessageSax2Dom(final SOAPVersion soapVersion, final Message message) throws SOAPException {
        final SOAPMessage msg = soapVersion.getMessageFactory().createMessage();
        final SAX2DOMEx s2d = new SAX2DOMEx(msg.getSOAPPart());
        try {
            message.writeTo(s2d, XmlUtil.DRACONIAN_ERROR_HANDLER);
        }
        catch (final SAXException e) {
            throw new SOAPException(e);
        }
        addAttachmentsToSOAPMessage(msg, message);
        if (msg.saveRequired()) {
            msg.saveChanges();
        }
        return msg;
    }
    
    protected static void addAttachmentsToSOAPMessage(final SOAPMessage msg, final Message message) {
        for (final Attachment att : message.getAttachments()) {
            final AttachmentPart part = msg.createAttachmentPart();
            part.setDataHandler(att.asDataHandler());
            final String cid = att.getContentId();
            if (cid != null) {
                if (cid.startsWith("<") && cid.endsWith(">")) {
                    part.setContentId(cid);
                }
                else {
                    part.setContentId('<' + cid + '>');
                }
            }
            if (att instanceof AttachmentEx) {
                final AttachmentEx ax = (AttachmentEx)att;
                final Iterator<AttachmentEx.MimeHeader> imh = ax.getMimeHeaders();
                while (imh.hasNext()) {
                    final AttachmentEx.MimeHeader ame = imh.next();
                    if (!"Content-ID".equals(ame.getName()) && !"Content-Type".equals(ame.getName())) {
                        part.addMimeHeader(ame.getName(), ame.getValue());
                    }
                }
            }
            msg.addAttachmentPart(part);
        }
    }
    
    public SOAPMessage readAsSOAPMessage(final SOAPVersion soapVersion, final Message message, final Packet packet) throws SOAPException {
        return this.readAsSOAPMessage(soapVersion, message);
    }
    
    static {
        instance = new SAAJFactory();
    }
}
