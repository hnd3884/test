package com.sun.xml.internal.ws.client.dispatch;

import com.sun.xml.internal.ws.client.ResponseContext;
import java.util.concurrent.Callable;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import javax.xml.transform.Source;
import java.util.Iterator;
import java.util.List;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.api.message.Attachment;
import java.util.ArrayList;
import javax.activation.DataHandler;
import java.util.HashMap;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.URI;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.ws.WebServiceException;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import java.util.Map;
import javax.xml.ws.soap.SOAPFaultException;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.client.RequestContext;
import java.util.concurrent.Future;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.client.AsyncInvoker;
import com.sun.xml.internal.ws.api.server.Container;
import javax.xml.ws.AsyncHandler;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import javax.xml.ws.Response;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.ws.Service;
import java.util.logging.Logger;
import javax.xml.ws.Dispatch;
import com.sun.xml.internal.ws.client.Stub;

public abstract class DispatchImpl<T> extends Stub implements Dispatch<T>
{
    private static final Logger LOGGER;
    final Service.Mode mode;
    final SOAPVersion soapVersion;
    final boolean allowFaultResponseMsg;
    static final long AWAIT_TERMINATION_TIME = 800L;
    static final String HTTP_REQUEST_METHOD_GET = "GET";
    static final String HTTP_REQUEST_METHOD_POST = "POST";
    static final String HTTP_REQUEST_METHOD_PUT = "PUT";
    
    @Deprecated
    protected DispatchImpl(final QName port, final Service.Mode mode, final WSServiceDelegate owner, final Tube pipe, final BindingImpl binding, @Nullable final WSEndpointReference epr) {
        super(port, owner, pipe, binding, (owner.getWsdlService() != null) ? owner.getWsdlService().get(port) : null, owner.getEndpointAddress(port), epr);
        this.mode = mode;
        this.soapVersion = binding.getSOAPVersion();
        this.allowFaultResponseMsg = false;
    }
    
    protected DispatchImpl(final WSPortInfo portInfo, final Service.Mode mode, final BindingImpl binding, @Nullable final WSEndpointReference epr) {
        this(portInfo, mode, binding, epr, false);
    }
    
    protected DispatchImpl(final WSPortInfo portInfo, final Service.Mode mode, final BindingImpl binding, @Nullable final WSEndpointReference epr, final boolean allowFaultResponseMsg) {
        this(portInfo, mode, binding, null, epr, allowFaultResponseMsg);
    }
    
    protected DispatchImpl(final WSPortInfo portInfo, final Service.Mode mode, final BindingImpl binding, final Tube pipe, @Nullable final WSEndpointReference epr, final boolean allowFaultResponseMsg) {
        super(portInfo, binding, pipe, portInfo.getEndpointAddress(), epr);
        this.mode = mode;
        this.soapVersion = binding.getSOAPVersion();
        this.allowFaultResponseMsg = allowFaultResponseMsg;
    }
    
    protected DispatchImpl(final WSPortInfo portInfo, final Service.Mode mode, final Tube pipe, final BindingImpl binding, @Nullable final WSEndpointReference epr, final boolean allowFaultResponseMsg) {
        super(portInfo, binding, pipe, portInfo.getEndpointAddress(), epr);
        this.mode = mode;
        this.soapVersion = binding.getSOAPVersion();
        this.allowFaultResponseMsg = allowFaultResponseMsg;
    }
    
    abstract Packet createPacket(final T p0);
    
    abstract T toReturnValue(final Packet p0);
    
    @Override
    public final Response<T> invokeAsync(final T param) {
        final Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                this.dumpParam(param, "invokeAsync(T)");
            }
            final AsyncInvoker invoker = new DispatchAsyncInvoker(param);
            final AsyncResponseImpl<T> ft = new AsyncResponseImpl<T>(invoker, (AsyncHandler<T>)null);
            invoker.setReceiver(ft);
            ft.run();
            return ft;
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }
    
    private void dumpParam(final T param, final String method) {
        if (param instanceof Packet) {
            final Packet message = (Packet)param;
            if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                final AddressingVersion av = this.getBinding().getAddressingVersion();
                final SOAPVersion sv = this.getBinding().getSOAPVersion();
                final String action = (av != null && message.getMessage() != null) ? AddressingUtils.getAction(message.getMessage().getHeaders(), av, sv) : null;
                final String msgId = (av != null && message.getMessage() != null) ? AddressingUtils.getMessageID(message.getMessage().getHeaders(), av, sv) : null;
                DispatchImpl.LOGGER.fine("In DispatchImpl." + method + " for message with action: " + action + " and msg ID: " + msgId + " msg: " + message.getMessage());
                if (message.getMessage() == null) {
                    DispatchImpl.LOGGER.fine("Dispatching null message for action: " + action + " and msg ID: " + msgId);
                }
            }
        }
    }
    
    @Override
    public final Future<?> invokeAsync(final T param, final AsyncHandler<T> asyncHandler) {
        final Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                this.dumpParam(param, "invokeAsync(T, AsyncHandler<T>)");
            }
            final AsyncInvoker invoker = new DispatchAsyncInvoker(param);
            final AsyncResponseImpl<T> ft = new AsyncResponseImpl<T>(invoker, asyncHandler);
            invoker.setReceiver(ft);
            invoker.setNonNullAsyncHandlerGiven(asyncHandler != null);
            ft.run();
            return ft;
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }
    
    public final T doInvoke(final T in, final RequestContext rc, final ResponseContextReceiver receiver) {
        Packet response = null;
        try {
            try {
                checkNullAllowed(in, rc, this.binding, this.mode);
                final Packet message = this.createPacket(in);
                message.setState(Packet.State.ClientRequest);
                this.resolveEndpointAddress(message, rc);
                this.setProperties(message, true);
                response = this.process(message, rc, receiver);
                final Message msg = response.getMessage();
                if (msg != null && msg.isFault() && !this.allowFaultResponseMsg) {
                    final SOAPFaultBuilder faultBuilder = SOAPFaultBuilder.create(msg);
                    throw (SOAPFaultException)faultBuilder.createException(null);
                }
            }
            catch (final JAXBException e) {
                throw new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { e });
            }
            catch (final WebServiceException e2) {
                throw e2;
            }
            catch (final Throwable e3) {
                throw new WebServiceException(e3);
            }
            return this.toReturnValue(response);
        }
        finally {
            if (response != null && response.transportBackChannel != null) {
                response.transportBackChannel.close();
            }
        }
    }
    
    @Override
    public final T invoke(final T in) {
        final Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                this.dumpParam(in, "invoke(T)");
            }
            return (T)this.doInvoke(in, this.requestContext, this);
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }
    
    @Override
    public final void invokeOneWay(final T in) {
        final Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                this.dumpParam(in, "invokeOneWay(T)");
            }
            try {
                checkNullAllowed(in, this.requestContext, this.binding, this.mode);
                final Packet request = this.createPacket(in);
                request.setState(Packet.State.ClientRequest);
                this.setProperties(request, false);
                this.process(request, this.requestContext, this);
            }
            catch (final WebServiceException e) {
                throw e;
            }
            catch (final Throwable e2) {
                throw new WebServiceException(e2);
            }
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }
    
    void setProperties(final Packet packet, final boolean expectReply) {
        packet.expectReply = expectReply;
    }
    
    static boolean isXMLHttp(@NotNull final WSBinding binding) {
        return binding.getBindingId().equals(BindingID.XML_HTTP);
    }
    
    static boolean isPAYLOADMode(@NotNull final Service.Mode mode) {
        return mode == Service.Mode.PAYLOAD;
    }
    
    static void checkNullAllowed(@Nullable final Object in, final RequestContext rc, final WSBinding binding, final Service.Mode mode) {
        if (in != null) {
            return;
        }
        if (isXMLHttp(binding)) {
            if (methodNotOk(rc)) {
                throw new WebServiceException(DispatchMessages.INVALID_NULLARG_XMLHTTP_REQUEST_METHOD("POST", "GET"));
            }
        }
        else if (mode == Service.Mode.MESSAGE) {
            throw new WebServiceException(DispatchMessages.INVALID_NULLARG_SOAP_MSGMODE(mode.name(), Service.Mode.PAYLOAD.toString()));
        }
    }
    
    static boolean methodNotOk(@NotNull final RequestContext rc) {
        final String requestMethod = (String)rc.get("javax.xml.ws.http.request.method");
        final String request = (requestMethod == null) ? "POST" : requestMethod;
        return "POST".equalsIgnoreCase(request) || "PUT".equalsIgnoreCase(request);
    }
    
    public static void checkValidSOAPMessageDispatch(final WSBinding binding, final Service.Mode mode) {
        if (isXMLHttp(binding)) {
            throw new WebServiceException(DispatchMessages.INVALID_SOAPMESSAGE_DISPATCH_BINDING("http://www.w3.org/2004/08/wsdl/http", "http://schemas.xmlsoap.org/wsdl/soap/http or http://www.w3.org/2003/05/soap/bindings/HTTP/"));
        }
        if (isPAYLOADMode(mode)) {
            throw new WebServiceException(DispatchMessages.INVALID_SOAPMESSAGE_DISPATCH_MSGMODE(mode.name(), Service.Mode.MESSAGE.toString()));
        }
    }
    
    public static void checkValidDataSourceDispatch(final WSBinding binding, final Service.Mode mode) {
        if (!isXMLHttp(binding)) {
            throw new WebServiceException(DispatchMessages.INVALID_DATASOURCE_DISPATCH_BINDING("SOAP/HTTP", "http://www.w3.org/2004/08/wsdl/http"));
        }
        if (isPAYLOADMode(mode)) {
            throw new WebServiceException(DispatchMessages.INVALID_DATASOURCE_DISPATCH_MSGMODE(mode.name(), Service.Mode.MESSAGE.toString()));
        }
    }
    
    @NotNull
    public final QName getPortName() {
        return this.portname;
    }
    
    void resolveEndpointAddress(@NotNull final Packet message, @NotNull final RequestContext requestContext) {
        final boolean p = message.packetTakesPriorityOverRequestContext;
        String endpoint;
        if (p && message.endpointAddress != null) {
            endpoint = message.endpointAddress.toString();
        }
        else {
            endpoint = (String)requestContext.get("javax.xml.ws.service.endpoint.address");
        }
        if (endpoint == null) {
            if (message.endpointAddress == null) {
                throw new WebServiceException(DispatchMessages.INVALID_NULLARG_URI());
            }
            endpoint = message.endpointAddress.toString();
        }
        String pathInfo = null;
        String queryString = null;
        if (p && message.invocationProperties.get("javax.xml.ws.http.request.pathinfo") != null) {
            pathInfo = message.invocationProperties.get("javax.xml.ws.http.request.pathinfo");
        }
        else if (requestContext.get("javax.xml.ws.http.request.pathinfo") != null) {
            pathInfo = (String)requestContext.get("javax.xml.ws.http.request.pathinfo");
        }
        if (p && message.invocationProperties.get("javax.xml.ws.http.request.querystring") != null) {
            queryString = message.invocationProperties.get("javax.xml.ws.http.request.querystring");
        }
        else if (requestContext.get("javax.xml.ws.http.request.querystring") != null) {
            queryString = (String)requestContext.get("javax.xml.ws.http.request.querystring");
        }
        if (pathInfo != null || queryString != null) {
            pathInfo = checkPath(pathInfo);
            queryString = checkQuery(queryString);
            if (endpoint != null) {
                try {
                    final URI endpointURI = new URI(endpoint);
                    endpoint = this.resolveURI(endpointURI, pathInfo, queryString);
                }
                catch (final URISyntaxException e) {
                    throw new WebServiceException(DispatchMessages.INVALID_URI(endpoint));
                }
            }
        }
        requestContext.put("javax.xml.ws.service.endpoint.address", endpoint);
    }
    
    @NotNull
    protected String resolveURI(@NotNull final URI endpointURI, @Nullable final String pathInfo, @Nullable final String queryString) {
        String query = null;
        String fragment = null;
        if (queryString != null) {
            URI result;
            try {
                final URI tp = new URI(null, null, endpointURI.getPath(), queryString, null);
                result = endpointURI.resolve(tp);
            }
            catch (final URISyntaxException e) {
                throw new WebServiceException(DispatchMessages.INVALID_QUERY_STRING(queryString));
            }
            query = result.getQuery();
            fragment = result.getFragment();
        }
        final String path = (pathInfo != null) ? pathInfo : endpointURI.getPath();
        try {
            final StringBuilder spec = new StringBuilder();
            if (path != null) {
                spec.append(path);
            }
            if (query != null) {
                spec.append("?");
                spec.append(query);
            }
            if (fragment != null) {
                spec.append("#");
                spec.append(fragment);
            }
            return new URL(endpointURI.toURL(), spec.toString()).toExternalForm();
        }
        catch (final MalformedURLException e2) {
            throw new WebServiceException(DispatchMessages.INVALID_URI_RESOLUTION(path));
        }
    }
    
    private static String checkPath(@Nullable final String path) {
        return (path == null || path.startsWith("/")) ? path : ("/" + path);
    }
    
    private static String checkQuery(@Nullable final String query) {
        if (query == null) {
            return null;
        }
        if (query.indexOf(63) == 0) {
            throw new WebServiceException(DispatchMessages.INVALID_QUERY_LEADING_CHAR(query));
        }
        return query;
    }
    
    protected AttachmentSet setOutboundAttachments() {
        final HashMap<String, DataHandler> attachments = this.getRequestContext().get("javax.xml.ws.binding.attachments.outbound");
        if (attachments != null) {
            final List<Attachment> alist = new ArrayList<Attachment>();
            for (final Map.Entry<String, DataHandler> att : attachments.entrySet()) {
                final DataHandlerAttachment dha = new DataHandlerAttachment(att.getKey(), att.getValue());
                alist.add(dha);
            }
            return new AttachmentSetImpl(alist);
        }
        return new AttachmentSetImpl();
    }
    
    @Override
    public void setOutboundHeaders(final Object... headers) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    public static Dispatch<Source> createSourceDispatch(final QName port, final Service.Mode mode, final WSServiceDelegate owner, final Tube pipe, final BindingImpl binding, final WSEndpointReference epr) {
        if (isXMLHttp(binding)) {
            return new RESTSourceDispatch(port, mode, owner, pipe, binding, epr);
        }
        return new SOAPSourceDispatch(port, mode, owner, pipe, binding, epr);
    }
    
    public static Dispatch<Source> createSourceDispatch(final WSPortInfo portInfo, final Service.Mode mode, final BindingImpl binding, final WSEndpointReference epr) {
        if (isXMLHttp(binding)) {
            return new RESTSourceDispatch(portInfo, mode, binding, epr);
        }
        return new SOAPSourceDispatch(portInfo, mode, binding, epr);
    }
    
    static {
        LOGGER = Logger.getLogger(DispatchImpl.class.getName());
    }
    
    private class Invoker implements Callable
    {
        private final T param;
        private final RequestContext rc;
        private ResponseContextReceiver receiver;
        
        Invoker(final T param) {
            this.rc = DispatchImpl.this.requestContext.copy();
            this.param = param;
        }
        
        @Override
        public T call() throws Exception {
            if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                DispatchImpl.this.dumpParam(this.param, "call()");
            }
            return DispatchImpl.this.doInvoke(this.param, this.rc, this.receiver);
        }
        
        void setReceiver(final ResponseContextReceiver receiver) {
            this.receiver = receiver;
        }
    }
    
    private class DispatchAsyncInvoker extends AsyncInvoker
    {
        private final T param;
        private final RequestContext rc;
        
        DispatchAsyncInvoker(final T param) {
            this.rc = DispatchImpl.this.requestContext.copy();
            this.param = param;
        }
        
        @Override
        public void do_run() {
            DispatchImpl.checkNullAllowed(this.param, this.rc, DispatchImpl.this.binding, DispatchImpl.this.mode);
            final Packet message = DispatchImpl.this.createPacket(this.param);
            message.setState(Packet.State.ClientRequest);
            message.nonNullAsyncHandlerGiven = this.nonNullAsyncHandlerGiven;
            DispatchImpl.this.resolveEndpointAddress(message, this.rc);
            DispatchImpl.this.setProperties(message, true);
            String action = null;
            String msgId = null;
            if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                final AddressingVersion av = DispatchImpl.this.getBinding().getAddressingVersion();
                final SOAPVersion sv = DispatchImpl.this.getBinding().getSOAPVersion();
                action = ((av != null && message.getMessage() != null) ? AddressingUtils.getAction(message.getMessage().getHeaders(), av, sv) : null);
                msgId = ((av != null && message.getMessage() != null) ? AddressingUtils.getMessageID(message.getMessage().getHeaders(), av, sv) : null);
                DispatchImpl.LOGGER.fine("In DispatchAsyncInvoker.do_run for async message with action: " + action + " and msg ID: " + msgId);
            }
            final String actionUse = action;
            final String msgIdUse = msgId;
            final Fiber.CompletionCallback callback = new Fiber.CompletionCallback() {
                @Override
                public void onCompletion(@NotNull final Packet response) {
                    if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                        DispatchImpl.LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + actionUse + " and msg ID: " + msgIdUse);
                    }
                    final Message msg = response.getMessage();
                    if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                        DispatchImpl.LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + actionUse + " and msg ID: " + msgIdUse + " msg: " + msg);
                    }
                    try {
                        if (msg != null && msg.isFault() && !DispatchImpl.this.allowFaultResponseMsg) {
                            final SOAPFaultBuilder faultBuilder = SOAPFaultBuilder.create(msg);
                            throw (SOAPFaultException)faultBuilder.createException(null);
                        }
                        DispatchAsyncInvoker.this.responseImpl.setResponseContext(new ResponseContext(response));
                        DispatchAsyncInvoker.this.responseImpl.set(DispatchImpl.this.toReturnValue(response), null);
                    }
                    catch (final JAXBException e) {
                        DispatchAsyncInvoker.this.responseImpl.set(null, new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { e }));
                    }
                    catch (final WebServiceException e2) {
                        DispatchAsyncInvoker.this.responseImpl.set(null, e2);
                    }
                    catch (final Throwable e3) {
                        DispatchAsyncInvoker.this.responseImpl.set(null, new WebServiceException(e3));
                    }
                }
                
                @Override
                public void onCompletion(@NotNull final Throwable error) {
                    if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
                        DispatchImpl.LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + actionUse + " and msg ID: " + msgIdUse + " Throwable: " + error.toString());
                    }
                    if (error instanceof WebServiceException) {
                        DispatchAsyncInvoker.this.responseImpl.set(null, error);
                    }
                    else {
                        DispatchAsyncInvoker.this.responseImpl.set(null, new WebServiceException(error));
                    }
                }
            };
            Stub.this.processAsync(this.responseImpl, message, this.rc, callback);
        }
    }
}
