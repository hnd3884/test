package com.sun.xml.internal.ws.api.message;

import com.sun.xml.internal.ws.api.DistributedPropertySet;
import java.nio.channels.WritableByteChannel;
import java.io.IOException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.util.AbstractSet;
import java.util.AbstractMap;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import java.io.ByteArrayOutputStream;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.message.RelatesToHeader;
import com.sun.xml.internal.ws.message.StringHeader;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.addressing.WsaPropertyBag;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import java.util.HashSet;
import java.util.Collections;
import com.sun.istack.internal.NotNull;
import org.w3c.dom.Document;
import java.util.Iterator;
import org.xml.sax.SAXException;
import javax.xml.ws.WebServiceException;
import org.xml.sax.ContentHandler;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import org.w3c.dom.Node;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import java.util.ArrayList;
import org.w3c.dom.Element;
import java.util.List;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.api.WSBinding;
import java.util.HashMap;
import javax.xml.ws.soap.MTOMFeature;
import com.oracle.webservices.internal.api.message.ContentType;
import com.sun.xml.internal.ws.api.pipe.Codec;
import java.util.logging.Logger;
import com.oracle.webservices.internal.api.message.BasePropertySet;
import java.util.Map;
import java.util.Set;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.Component;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.api.EndpointAddress;
import javax.xml.ws.BindingProvider;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;

public final class Packet extends BaseDistributedPropertySet implements MessageContext, MessageMetadata
{
    private Message message;
    private WSDLOperationMapping wsdlOperationMapping;
    private QName wsdlOperation;
    public boolean wasTransportSecure;
    public static final String INBOUND_TRANSPORT_HEADERS = "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers";
    public static final String OUTBOUND_TRANSPORT_HEADERS = "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers";
    public static final String HA_INFO = "com.sun.xml.internal.ws.api.message.packet.hainfo";
    @PropertySet.Property({ "com.sun.xml.internal.ws.handler.config" })
    public HandlerConfiguration handlerConfig;
    @PropertySet.Property({ "com.sun.xml.internal.ws.client.handle" })
    public BindingProvider proxy;
    public boolean isAdapterDeliversNonAnonymousResponse;
    public boolean packetTakesPriorityOverRequestContext;
    public EndpointAddress endpointAddress;
    public ContentNegotiation contentNegotiation;
    public String acceptableMimeTypes;
    public WebServiceContextDelegate webServiceContextDelegate;
    @Nullable
    public TransportBackChannel transportBackChannel;
    public Component component;
    @PropertySet.Property({ "com.sun.xml.internal.ws.api.server.WSEndpoint" })
    public WSEndpoint endpoint;
    @PropertySet.Property({ "javax.xml.ws.soap.http.soapaction.uri" })
    public String soapAction;
    @PropertySet.Property({ "com.sun.xml.internal.ws.server.OneWayOperation" })
    public Boolean expectReply;
    @Deprecated
    public Boolean isOneWay;
    public Boolean isSynchronousMEP;
    public Boolean nonNullAsyncHandlerGiven;
    private Boolean isRequestReplyMEP;
    private Set<String> handlerScopePropertyNames;
    public final Map<String, Object> invocationProperties;
    private static final PropertyMap model;
    private static final Logger LOGGER;
    public Codec codec;
    private ContentType contentType;
    private Boolean mtomRequest;
    private Boolean mtomAcceptable;
    private MTOMFeature mtomFeature;
    Boolean checkMtomAcceptable;
    private Boolean fastInfosetAcceptable;
    private State state;
    private boolean isFastInfosetDisabled;
    
    public Packet(final Message request) {
        this();
        this.message = request;
        if (this.message != null) {
            this.message.setMessageMedadata(this);
        }
    }
    
    public Packet() {
        this.wsdlOperationMapping = null;
        this.packetTakesPriorityOverRequestContext = false;
        this.codec = null;
        this.state = State.ServerRequest;
        this.invocationProperties = new HashMap<String, Object>();
    }
    
    private Packet(final Packet that) {
        this.wsdlOperationMapping = null;
        this.packetTakesPriorityOverRequestContext = false;
        this.codec = null;
        this.state = State.ServerRequest;
        this.relatePackets(that, true);
        this.invocationProperties = that.invocationProperties;
    }
    
    public Packet copy(final boolean copyMessage) {
        final Packet copy = new Packet(this);
        if (copyMessage && this.message != null) {
            copy.message = this.message.copy();
        }
        if (copy.message != null) {
            copy.message.setMessageMedadata(copy);
        }
        return copy;
    }
    
    public Message getMessage() {
        if (this.message != null && !(this.message instanceof MessageWrapper)) {
            this.message = new MessageWrapper(this, this.message);
        }
        return this.message;
    }
    
    public Message getInternalMessage() {
        return (this.message instanceof MessageWrapper) ? ((MessageWrapper)this.message).delegate : this.message;
    }
    
    public WSBinding getBinding() {
        if (this.endpoint != null) {
            return this.endpoint.getBinding();
        }
        if (this.proxy != null) {
            return (WSBinding)this.proxy.getBinding();
        }
        return null;
    }
    
    public void setMessage(final Message message) {
        this.message = message;
        if (message != null) {
            this.message.setMessageMedadata(this);
        }
    }
    
    @PropertySet.Property({ "javax.xml.ws.wsdl.operation" })
    @Nullable
    public final QName getWSDLOperation() {
        if (this.wsdlOperation != null) {
            return this.wsdlOperation;
        }
        if (this.wsdlOperationMapping == null) {
            this.wsdlOperationMapping = this.getWSDLOperationMapping();
        }
        if (this.wsdlOperationMapping != null) {
            this.wsdlOperation = this.wsdlOperationMapping.getOperationName();
        }
        return this.wsdlOperation;
    }
    
    @Override
    public WSDLOperationMapping getWSDLOperationMapping() {
        if (this.wsdlOperationMapping != null) {
            return this.wsdlOperationMapping;
        }
        OperationDispatcher opDispatcher = null;
        if (this.endpoint != null) {
            opDispatcher = this.endpoint.getOperationDispatcher();
        }
        else if (this.proxy != null) {
            opDispatcher = ((Stub)this.proxy).getOperationDispatcher();
        }
        if (opDispatcher != null) {
            try {
                this.wsdlOperationMapping = opDispatcher.getWSDLOperationMapping(this);
            }
            catch (final DispatchException ex) {}
        }
        return this.wsdlOperationMapping;
    }
    
    public void setWSDLOperation(final QName wsdlOp) {
        this.wsdlOperation = wsdlOp;
    }
    
    @PropertySet.Property({ "javax.xml.ws.service.endpoint.address" })
    @Deprecated
    public String getEndPointAddressString() {
        if (this.endpointAddress == null) {
            return null;
        }
        return this.endpointAddress.toString();
    }
    
    public void setEndPointAddressString(final String s) {
        if (s == null) {
            this.endpointAddress = null;
        }
        else {
            this.endpointAddress = EndpointAddress.create(s);
        }
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.client.ContentNegotiation" })
    public String getContentNegotiationString() {
        return (this.contentNegotiation != null) ? this.contentNegotiation.toString() : null;
    }
    
    public void setContentNegotiationString(final String s) {
        if (s == null) {
            this.contentNegotiation = null;
        }
        else {
            try {
                this.contentNegotiation = ContentNegotiation.valueOf(s);
            }
            catch (final IllegalArgumentException e) {
                this.contentNegotiation = ContentNegotiation.none;
            }
        }
    }
    
    @PropertySet.Property({ "javax.xml.ws.reference.parameters" })
    @NotNull
    public List<Element> getReferenceParameters() {
        final Message msg = this.getMessage();
        final List<Element> refParams = new ArrayList<Element>();
        if (msg == null) {
            return refParams;
        }
        final MessageHeaders hl = msg.getHeaders();
        for (final Header h : hl.asList()) {
            final String attr = h.getAttribute(AddressingVersion.W3C.nsUri, "IsReferenceParameter");
            if (attr != null && (attr.equals("true") || attr.equals("1"))) {
                final Document d = DOMUtil.createDom();
                final SAX2DOMEx s2d = new SAX2DOMEx(d);
                try {
                    h.writeTo(s2d, XmlUtil.DRACONIAN_ERROR_HANDLER);
                    refParams.add((Element)d.getLastChild());
                }
                catch (final SAXException e) {
                    throw new WebServiceException(e);
                }
            }
        }
        return refParams;
    }
    
    @PropertySet.Property({ "com.sun.xml.internal.ws.api.message.HeaderList" })
    MessageHeaders getHeaderList() {
        final Message msg = this.getMessage();
        if (msg == null) {
            return null;
        }
        return msg.getHeaders();
    }
    
    public TransportBackChannel keepTransportBackChannelOpen() {
        final TransportBackChannel r = this.transportBackChannel;
        this.transportBackChannel = null;
        return r;
    }
    
    public Boolean isRequestReplyMEP() {
        return this.isRequestReplyMEP;
    }
    
    public void setRequestReplyMEP(final Boolean x) {
        this.isRequestReplyMEP = x;
    }
    
    public final Set<String> getHandlerScopePropertyNames(final boolean readOnly) {
        Set<String> o = this.handlerScopePropertyNames;
        if (o == null) {
            if (readOnly) {
                return Collections.emptySet();
            }
            o = new HashSet<String>();
            this.handlerScopePropertyNames = o;
        }
        return o;
    }
    
    @Deprecated
    public final Set<String> getApplicationScopePropertyNames(final boolean readOnly) {
        assert false;
        return new HashSet<String>();
    }
    
    @Deprecated
    public Packet createResponse(final Message msg) {
        final Packet response = new Packet(this);
        response.setMessage(msg);
        return response;
    }
    
    public Packet createClientResponse(final Message msg) {
        final Packet response = new Packet(this);
        response.setMessage(msg);
        this.finishCreateRelateClientResponse(response);
        return response;
    }
    
    public Packet relateClientResponse(final Packet response) {
        response.relatePackets(this, true);
        this.finishCreateRelateClientResponse(response);
        return response;
    }
    
    private void finishCreateRelateClientResponse(final Packet response) {
        response.soapAction = null;
        response.setState(State.ClientResponse);
    }
    
    public Packet createServerResponse(@Nullable final Message responseMessage, @Nullable final WSDLPort wsdlPort, @Nullable final SEIModel seiModel, @NotNull final WSBinding binding) {
        final Packet r = this.createClientResponse(responseMessage);
        return this.relateServerResponse(r, wsdlPort, seiModel, binding);
    }
    
    public void copyPropertiesTo(@Nullable final Packet response) {
        this.relatePackets(response, false);
    }
    
    private void relatePackets(@Nullable final Packet packet, final boolean isCopy) {
        Packet request;
        Packet response;
        if (!isCopy) {
            request = this;
            response = packet;
            response.soapAction = null;
            response.invocationProperties.putAll(request.invocationProperties);
            if (this.getState().equals(State.ServerRequest)) {
                response.setState(State.ServerResponse);
            }
        }
        else {
            request = packet;
            response = this;
            response.soapAction = request.soapAction;
            response.setState(request.getState());
        }
        request.copySatelliteInto(response);
        response.isAdapterDeliversNonAnonymousResponse = request.isAdapterDeliversNonAnonymousResponse;
        response.handlerConfig = request.handlerConfig;
        response.handlerScopePropertyNames = request.handlerScopePropertyNames;
        response.contentNegotiation = request.contentNegotiation;
        response.wasTransportSecure = request.wasTransportSecure;
        response.transportBackChannel = request.transportBackChannel;
        response.endpointAddress = request.endpointAddress;
        response.wsdlOperation = request.wsdlOperation;
        response.wsdlOperationMapping = request.wsdlOperationMapping;
        response.acceptableMimeTypes = request.acceptableMimeTypes;
        response.endpoint = request.endpoint;
        response.proxy = request.proxy;
        response.webServiceContextDelegate = request.webServiceContextDelegate;
        response.expectReply = request.expectReply;
        response.component = request.component;
        response.mtomAcceptable = request.mtomAcceptable;
        response.mtomRequest = request.mtomRequest;
    }
    
    public Packet relateServerResponse(@Nullable final Packet r, @Nullable final WSDLPort wsdlPort, @Nullable final SEIModel seiModel, @NotNull final WSBinding binding) {
        this.relatePackets(r, false);
        r.setState(State.ServerResponse);
        final AddressingVersion av = binding.getAddressingVersion();
        if (av == null) {
            return r;
        }
        if (this.getMessage() == null) {
            return r;
        }
        final String inputAction = AddressingUtils.getAction(this.getMessage().getHeaders(), av, binding.getSOAPVersion());
        if (inputAction == null) {
            return r;
        }
        if (r.getMessage() == null || (wsdlPort != null && this.getMessage().isOneWay(wsdlPort))) {
            return r;
        }
        this.populateAddressingHeaders(binding, r, wsdlPort, seiModel);
        return r;
    }
    
    public Packet createServerResponse(@Nullable final Message responseMessage, @NotNull final AddressingVersion addressingVersion, @NotNull final SOAPVersion soapVersion, @NotNull final String action) {
        final Packet responsePacket = this.createClientResponse(responseMessage);
        responsePacket.setState(State.ServerResponse);
        if (addressingVersion == null) {
            return responsePacket;
        }
        final String inputAction = AddressingUtils.getAction(this.getMessage().getHeaders(), addressingVersion, soapVersion);
        if (inputAction == null) {
            return responsePacket;
        }
        this.populateAddressingHeaders(responsePacket, addressingVersion, soapVersion, action, false);
        return responsePacket;
    }
    
    public void setResponseMessage(@NotNull final Packet request, @Nullable final Message responseMessage, @NotNull final AddressingVersion addressingVersion, @NotNull final SOAPVersion soapVersion, @NotNull final String action) {
        final Packet temp = request.createServerResponse(responseMessage, addressingVersion, soapVersion, action);
        this.setMessage(temp.getMessage());
    }
    
    private void populateAddressingHeaders(final Packet responsePacket, final AddressingVersion av, final SOAPVersion sv, final String action, final boolean mustUnderstand) {
        if (av == null) {
            return;
        }
        if (responsePacket.getMessage() == null) {
            return;
        }
        final MessageHeaders hl = responsePacket.getMessage().getHeaders();
        final WsaPropertyBag wpb = this.getSatellite(WsaPropertyBag.class);
        final Message msg = this.getMessage();
        WSEndpointReference replyTo = null;
        final Header replyToFromRequestMsg = AddressingUtils.getFirstHeader(msg.getHeaders(), av.replyToTag, true, sv);
        final Header replyToFromResponseMsg = hl.get(av.toTag, false);
        boolean replaceToTag = true;
        try {
            if (replyToFromRequestMsg != null) {
                replyTo = replyToFromRequestMsg.readAsEPR(av);
            }
            if (replyToFromResponseMsg != null && replyTo == null) {
                replaceToTag = false;
            }
        }
        catch (final XMLStreamException e) {
            throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), e);
        }
        if (replyTo == null) {
            replyTo = AddressingUtils.getReplyTo(msg.getHeaders(), av, sv);
        }
        if (AddressingUtils.getAction(responsePacket.getMessage().getHeaders(), av, sv) == null) {
            hl.add(new StringHeader(av.actionTag, action, sv, mustUnderstand));
        }
        if (responsePacket.getMessage().getHeaders().get(av.messageIDTag, false) == null) {
            final String newID = Message.generateMessageID();
            hl.add(new StringHeader(av.messageIDTag, newID));
        }
        String mid = null;
        if (wpb != null) {
            mid = wpb.getMessageID();
        }
        if (mid == null) {
            mid = AddressingUtils.getMessageID(msg.getHeaders(), av, sv);
        }
        if (mid != null) {
            hl.addOrReplace(new RelatesToHeader(av.relatesToTag, mid));
        }
        WSEndpointReference refpEPR = null;
        if (responsePacket.getMessage().isFault()) {
            if (wpb != null) {
                refpEPR = wpb.getFaultToFromRequest();
            }
            if (refpEPR == null) {
                refpEPR = AddressingUtils.getFaultTo(msg.getHeaders(), av, sv);
            }
            if (refpEPR == null) {
                refpEPR = replyTo;
            }
        }
        else {
            refpEPR = replyTo;
        }
        if (replaceToTag && refpEPR != null) {
            hl.addOrReplace(new StringHeader(av.toTag, refpEPR.getAddress()));
            refpEPR.addReferenceParametersToList(hl);
        }
    }
    
    private void populateAddressingHeaders(final WSBinding binding, final Packet responsePacket, final WSDLPort wsdlPort, final SEIModel seiModel) {
        final AddressingVersion addressingVersion = binding.getAddressingVersion();
        if (addressingVersion == null) {
            return;
        }
        final WsaTubeHelper wsaHelper = addressingVersion.getWsaHelper(wsdlPort, seiModel, binding);
        final String action = responsePacket.getMessage().isFault() ? wsaHelper.getFaultAction(this, responsePacket) : wsaHelper.getOutputAction(this);
        if (action == null) {
            Packet.LOGGER.info("WSA headers are not added as value for wsa:Action cannot be resolved for this message");
            return;
        }
        this.populateAddressingHeaders(responsePacket, addressingVersion, binding.getSOAPVersion(), action, AddressingVersion.isRequired(binding));
    }
    
    public String toShortString() {
        return super.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(super.toString());
        String content;
        try {
            final Message msg = this.getMessage();
            if (msg != null) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final XMLStreamWriter xmlWriter = XMLStreamWriterFactory.create(baos, "UTF-8");
                msg.copy().writeTo(xmlWriter);
                xmlWriter.flush();
                xmlWriter.close();
                baos.flush();
                XMLStreamWriterFactory.recycle(xmlWriter);
                final byte[] bytes = baos.toByteArray();
                content = new String(bytes, "UTF-8");
            }
            else {
                content = "<none>";
            }
        }
        catch (final Throwable t) {
            throw new WebServiceException(t);
        }
        buf.append(" Content: ").append(content);
        return buf.toString();
    }
    
    @Override
    protected PropertyMap getPropertyMap() {
        return Packet.model;
    }
    
    public Map<String, Object> asMapIncludingInvocationProperties() {
        final Map<String, Object> asMap = this.asMap();
        return new AbstractMap<String, Object>() {
            @Override
            public Object get(final Object key) {
                final Object o = asMap.get(key);
                if (o != null) {
                    return o;
                }
                return Packet.this.invocationProperties.get(key);
            }
            
            @Override
            public int size() {
                return asMap.size() + Packet.this.invocationProperties.size();
            }
            
            @Override
            public boolean containsKey(final Object key) {
                return asMap.containsKey(key) || Packet.this.invocationProperties.containsKey(key);
            }
            
            @Override
            public Set<Map.Entry<String, Object>> entrySet() {
                final Set<Map.Entry<String, Object>> asMapEntries = asMap.entrySet();
                final Set<Map.Entry<String, Object>> ipEntries = Packet.this.invocationProperties.entrySet();
                return new AbstractSet<Map.Entry<String, Object>>() {
                    @Override
                    public Iterator<Map.Entry<String, Object>> iterator() {
                        final Iterator<Map.Entry<String, Object>> asMapIt = asMapEntries.iterator();
                        final Iterator<Map.Entry<String, Object>> ipIt = ipEntries.iterator();
                        return new Iterator<Map.Entry<String, Object>>() {
                            @Override
                            public boolean hasNext() {
                                return asMapIt.hasNext() || ipIt.hasNext();
                            }
                            
                            @Override
                            public Map.Entry<String, Object> next() {
                                if (asMapIt.hasNext()) {
                                    return asMapIt.next();
                                }
                                return ipIt.next();
                            }
                            
                            @Override
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }
                    
                    @Override
                    public int size() {
                        return asMap.size() + Packet.this.invocationProperties.size();
                    }
                };
            }
            
            @Override
            public Object put(final String key, final Object value) {
                if (Packet.this.supports(key)) {
                    return asMap.put(key, value);
                }
                return Packet.this.invocationProperties.put(key, value);
            }
            
            @Override
            public void clear() {
                asMap.clear();
                Packet.this.invocationProperties.clear();
            }
            
            @Override
            public Object remove(final Object key) {
                if (Packet.this.supports(key)) {
                    return asMap.remove(key);
                }
                return Packet.this.invocationProperties.remove(key);
            }
        };
    }
    
    @Override
    public SOAPMessage getSOAPMessage() throws SOAPException {
        return this.getAsSOAPMessage();
    }
    
    @Override
    public SOAPMessage getAsSOAPMessage() throws SOAPException {
        final Message msg = this.getMessage();
        if (msg == null) {
            return null;
        }
        if (msg instanceof MessageWritable) {
            ((MessageWritable)msg).setMTOMConfiguration(this.mtomFeature);
        }
        return msg.readAsSOAPMessage(this, this.getState().isInbound());
    }
    
    public Codec getCodec() {
        if (this.codec != null) {
            return this.codec;
        }
        if (this.endpoint != null) {
            this.codec = this.endpoint.createCodec();
        }
        final WSBinding wsb = this.getBinding();
        if (wsb != null) {
            this.codec = wsb.getBindingId().createEncoder(wsb);
        }
        return this.codec;
    }
    
    @Override
    public ContentType writeTo(final OutputStream out) throws IOException {
        final Message msg = this.getInternalMessage();
        if (msg instanceof MessageWritable) {
            ((MessageWritable)msg).setMTOMConfiguration(this.mtomFeature);
            return ((MessageWritable)msg).writeTo(out);
        }
        return this.getCodec().encode(this, out);
    }
    
    public ContentType writeTo(final WritableByteChannel buffer) {
        return this.getCodec().encode(this, buffer);
    }
    
    public Boolean getMtomRequest() {
        return this.mtomRequest;
    }
    
    public void setMtomRequest(final Boolean mtomRequest) {
        this.mtomRequest = mtomRequest;
    }
    
    public Boolean getMtomAcceptable() {
        return this.mtomAcceptable;
    }
    
    public void checkMtomAcceptable() {
        if (this.checkMtomAcceptable == null) {
            if (this.acceptableMimeTypes == null || this.isFastInfosetDisabled) {
                this.checkMtomAcceptable = false;
            }
            else {
                this.checkMtomAcceptable = (this.acceptableMimeTypes.indexOf("application/xop+xml") != -1);
            }
        }
        this.mtomAcceptable = this.checkMtomAcceptable;
    }
    
    public Boolean getFastInfosetAcceptable(final String fiMimeType) {
        if (this.fastInfosetAcceptable == null) {
            if (this.acceptableMimeTypes == null || this.isFastInfosetDisabled) {
                this.fastInfosetAcceptable = false;
            }
            else {
                this.fastInfosetAcceptable = (this.acceptableMimeTypes.indexOf(fiMimeType) != -1);
            }
        }
        return this.fastInfosetAcceptable;
    }
    
    public void setMtomFeature(final MTOMFeature mtomFeature) {
        this.mtomFeature = mtomFeature;
    }
    
    public MTOMFeature getMtomFeature() {
        final WSBinding binding = this.getBinding();
        if (binding != null) {
            return binding.getFeature(MTOMFeature.class);
        }
        return this.mtomFeature;
    }
    
    @Override
    public ContentType getContentType() {
        if (this.contentType == null) {
            this.contentType = this.getInternalContentType();
        }
        if (this.contentType == null) {
            this.contentType = this.getCodec().getStaticContentType(this);
        }
        if (this.contentType == null) {}
        return this.contentType;
    }
    
    public ContentType getInternalContentType() {
        final Message msg = this.getInternalMessage();
        if (msg instanceof MessageWritable) {
            return ((MessageWritable)msg).getContentType();
        }
        return this.contentType;
    }
    
    public void setContentType(final ContentType contentType) {
        this.contentType = contentType;
    }
    
    public State getState() {
        return this.state;
    }
    
    public void setState(final State state) {
        this.state = state;
    }
    
    public boolean shouldUseMtom() {
        if (this.getState().isInbound()) {
            return this.isMtomContentType();
        }
        return this.shouldUseMtomOutbound();
    }
    
    private boolean shouldUseMtomOutbound() {
        final MTOMFeature myMtomFeature = this.getMtomFeature();
        if (myMtomFeature != null && myMtomFeature.isEnabled()) {
            if (this.getMtomAcceptable() == null && this.getMtomRequest() == null) {
                return true;
            }
            if (this.getMtomAcceptable() != null && this.getMtomAcceptable() && this.getState().equals(State.ServerResponse)) {
                return true;
            }
            if (this.getMtomRequest() != null && this.getMtomRequest() && this.getState().equals(State.ServerResponse)) {
                return true;
            }
            if (this.getMtomRequest() != null && this.getMtomRequest() && this.getState().equals(State.ClientRequest)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isMtomContentType() {
        return this.getInternalContentType() != null && this.getInternalContentType().getContentType().contains("application/xop+xml");
    }
    
    @Deprecated
    public void addSatellite(@NotNull final com.sun.xml.internal.ws.api.PropertySet satellite) {
        super.addSatellite(satellite);
    }
    
    @Deprecated
    public void addSatellite(@NotNull final Class keyClass, @NotNull final com.sun.xml.internal.ws.api.PropertySet satellite) {
        super.addSatellite(keyClass, satellite);
    }
    
    @Deprecated
    public void copySatelliteInto(@NotNull final com.sun.xml.internal.ws.api.DistributedPropertySet r) {
        super.copySatelliteInto(r);
    }
    
    @Deprecated
    public void removeSatellite(final com.sun.xml.internal.ws.api.PropertySet satellite) {
        super.removeSatellite(satellite);
    }
    
    public void setFastInfosetDisabled(final boolean b) {
        this.isFastInfosetDisabled = b;
    }
    
    static {
        model = BasePropertySet.parse(Packet.class);
        LOGGER = Logger.getLogger(Packet.class.getName());
    }
    
    public enum Status
    {
        Request, 
        Response, 
        Unknown;
        
        public boolean isRequest() {
            return Status.Request.equals(this);
        }
        
        public boolean isResponse() {
            return Status.Response.equals(this);
        }
    }
    
    public enum State
    {
        ServerRequest(true), 
        ClientRequest(false), 
        ServerResponse(false), 
        ClientResponse(true);
        
        private boolean inbound;
        
        private State(final boolean inbound) {
            this.inbound = inbound;
        }
        
        public boolean isInbound() {
            return this.inbound;
        }
    }
}
