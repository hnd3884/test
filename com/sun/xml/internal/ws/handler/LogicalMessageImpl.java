package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import org.w3c.dom.Node;
import com.sun.xml.internal.ws.message.DOMMessage;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Source;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.LogicalMessage;

class LogicalMessageImpl implements LogicalMessage
{
    private Packet packet;
    protected BindingContext defaultJaxbContext;
    private ImmutableLM lm;
    
    public LogicalMessageImpl(final BindingContext defaultJaxbContext, final Packet packet) {
        this.lm = null;
        this.packet = packet;
        this.defaultJaxbContext = defaultJaxbContext;
    }
    
    @Override
    public Source getPayload() {
        if (this.lm == null) {
            final Source payload = this.packet.getMessage().copy().readPayloadAsSource();
            if (payload instanceof DOMSource) {
                this.lm = this.createLogicalMessageImpl(payload);
            }
            return payload;
        }
        return this.lm.getPayload();
    }
    
    @Override
    public void setPayload(final Source payload) {
        this.lm = this.createLogicalMessageImpl(payload);
    }
    
    private ImmutableLM createLogicalMessageImpl(final Source payload) {
        if (payload == null) {
            this.lm = new EmptyLogicalMessageImpl();
        }
        else if (payload instanceof DOMSource) {
            this.lm = new DOMLogicalMessageImpl((DOMSource)payload);
        }
        else {
            this.lm = new SourceLogicalMessageImpl(payload);
        }
        return this.lm;
    }
    
    public Object getPayload(BindingContext context) {
        if (context == null) {
            context = this.defaultJaxbContext;
        }
        if (context == null) {
            throw new WebServiceException("JAXBContext parameter cannot be null");
        }
        if (this.lm == null) {
            try {
                final Object o = this.packet.getMessage().copy().readPayloadAsJAXB(context.createUnmarshaller());
                return o;
            }
            catch (final JAXBException e) {
                throw new WebServiceException(e);
            }
        }
        final Object o = this.lm.getPayload(context);
        this.lm = new JAXBLogicalMessageImpl(context.getJAXBContext(), o);
        return o;
    }
    
    @Override
    public Object getPayload(final JAXBContext context) {
        if (context == null) {
            return this.getPayload(this.defaultJaxbContext);
        }
        if (context == null) {
            throw new WebServiceException("JAXBContext parameter cannot be null");
        }
        if (this.lm == null) {
            try {
                final Object o = this.packet.getMessage().copy().readPayloadAsJAXB(context.createUnmarshaller());
                return o;
            }
            catch (final JAXBException e) {
                throw new WebServiceException(e);
            }
        }
        final Object o = this.lm.getPayload(context);
        this.lm = new JAXBLogicalMessageImpl(context, o);
        return o;
    }
    
    public void setPayload(final Object payload, BindingContext context) {
        if (context == null) {
            context = this.defaultJaxbContext;
        }
        if (payload == null) {
            this.lm = new EmptyLogicalMessageImpl();
        }
        else {
            this.lm = new JAXBLogicalMessageImpl(context.getJAXBContext(), payload);
        }
    }
    
    @Override
    public void setPayload(final Object payload, final JAXBContext context) {
        if (context == null) {
            this.setPayload(payload, this.defaultJaxbContext);
        }
        if (payload == null) {
            this.lm = new EmptyLogicalMessageImpl();
        }
        else {
            this.lm = new JAXBLogicalMessageImpl(context, payload);
        }
    }
    
    public boolean isPayloadModifed() {
        return this.lm != null;
    }
    
    public Message getMessage(final MessageHeaders headers, final AttachmentSet attachments, final WSBinding binding) {
        assert this.isPayloadModifed();
        if (this.isPayloadModifed()) {
            return this.lm.getMessage(headers, attachments, binding);
        }
        return this.packet.getMessage();
    }
    
    private abstract class ImmutableLM
    {
        public abstract Source getPayload();
        
        public abstract Object getPayload(final BindingContext p0);
        
        public abstract Object getPayload(final JAXBContext p0);
        
        public abstract Message getMessage(final MessageHeaders p0, final AttachmentSet p1, final WSBinding p2);
    }
    
    private class DOMLogicalMessageImpl extends SourceLogicalMessageImpl
    {
        private DOMSource dom;
        
        public DOMLogicalMessageImpl(final DOMSource dom) {
            super(dom);
            this.dom = dom;
        }
        
        @Override
        public Source getPayload() {
            return this.dom;
        }
        
        @Override
        public Message getMessage(final MessageHeaders headers, final AttachmentSet attachments, final WSBinding binding) {
            Node n = this.dom.getNode();
            if (n.getNodeType() == 9) {
                n = ((Document)n).getDocumentElement();
            }
            return new DOMMessage(binding.getSOAPVersion(), headers, (Element)n, attachments);
        }
    }
    
    private class EmptyLogicalMessageImpl extends ImmutableLM
    {
        public EmptyLogicalMessageImpl() {
        }
        
        @Override
        public Source getPayload() {
            return null;
        }
        
        @Override
        public Object getPayload(final JAXBContext context) {
            return null;
        }
        
        @Override
        public Object getPayload(final BindingContext context) {
            return null;
        }
        
        @Override
        public Message getMessage(final MessageHeaders headers, final AttachmentSet attachments, final WSBinding binding) {
            return new EmptyMessageImpl(headers, attachments, binding.getSOAPVersion());
        }
    }
    
    private class JAXBLogicalMessageImpl extends ImmutableLM
    {
        private JAXBContext ctxt;
        private Object o;
        
        public JAXBLogicalMessageImpl(final JAXBContext ctxt, final Object o) {
            this.ctxt = ctxt;
            this.o = o;
        }
        
        @Override
        public Source getPayload() {
            JAXBContext context = this.ctxt;
            if (context == null) {
                context = LogicalMessageImpl.this.defaultJaxbContext.getJAXBContext();
            }
            try {
                return new JAXBSource(context, this.o);
            }
            catch (final JAXBException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public Object getPayload(final JAXBContext context) {
            try {
                final Source payloadSrc = this.getPayload();
                if (payloadSrc == null) {
                    return null;
                }
                final Unmarshaller unmarshaller = context.createUnmarshaller();
                return unmarshaller.unmarshal(payloadSrc);
            }
            catch (final JAXBException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public Object getPayload(final BindingContext context) {
            try {
                final Source payloadSrc = this.getPayload();
                if (payloadSrc == null) {
                    return null;
                }
                final Unmarshaller unmarshaller = context.createUnmarshaller();
                return unmarshaller.unmarshal(payloadSrc);
            }
            catch (final JAXBException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public Message getMessage(final MessageHeaders headers, final AttachmentSet attachments, final WSBinding binding) {
            return JAXBMessage.create(BindingContextFactory.create(this.ctxt), this.o, binding.getSOAPVersion(), headers, attachments);
        }
    }
    
    private class SourceLogicalMessageImpl extends ImmutableLM
    {
        private Source payloadSrc;
        
        public SourceLogicalMessageImpl(final Source source) {
            this.payloadSrc = source;
        }
        
        @Override
        public Source getPayload() {
            assert !(this.payloadSrc instanceof DOMSource);
            try {
                final Transformer transformer = XmlUtil.newTransformer();
                final DOMResult domResult = new DOMResult();
                transformer.transform(this.payloadSrc, domResult);
                final DOMSource dom = new DOMSource(domResult.getNode());
                LogicalMessageImpl.this.lm = new DOMLogicalMessageImpl(dom);
                this.payloadSrc = null;
                return dom;
            }
            catch (final TransformerException te) {
                throw new WebServiceException(te);
            }
        }
        
        @Override
        public Object getPayload(final JAXBContext context) {
            try {
                final Source payloadSrc = this.getPayload();
                if (payloadSrc == null) {
                    return null;
                }
                final Unmarshaller unmarshaller = context.createUnmarshaller();
                return unmarshaller.unmarshal(payloadSrc);
            }
            catch (final JAXBException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public Object getPayload(final BindingContext context) {
            try {
                final Source payloadSrc = this.getPayload();
                if (payloadSrc == null) {
                    return null;
                }
                final Unmarshaller unmarshaller = context.createUnmarshaller();
                return unmarshaller.unmarshal(payloadSrc);
            }
            catch (final JAXBException e) {
                throw new WebServiceException(e);
            }
        }
        
        @Override
        public Message getMessage(final MessageHeaders headers, final AttachmentSet attachments, final WSBinding binding) {
            assert this.payloadSrc != null;
            return new PayloadSourceMessage(headers, this.payloadSrc, attachments, binding.getSOAPVersion());
        }
    }
}
