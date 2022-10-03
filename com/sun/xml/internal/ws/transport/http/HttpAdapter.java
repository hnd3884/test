package com.sun.xml.internal.ws.transport.http;

import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.AbstractServerAsyncTransport;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Module;
import java.io.ByteArrayOutputStream;
import java.util.List;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import com.sun.xml.internal.ws.api.server.DocumentAddressResolver;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.server.UnsupportedMediaException;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.ha.HaInfo;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.SOAPVersion;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.EndpointAddress;
import java.io.OutputStream;
import com.sun.xml.internal.ws.api.addressing.NonAnonymousResponseProcessor;
import java.io.InputStream;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.util.logging.Level;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Codec;
import javax.xml.ws.Binding;
import javax.xml.ws.http.HTTPBinding;
import com.sun.xml.internal.ws.api.Component;
import java.io.IOException;
import com.sun.xml.internal.ws.util.Pool;
import com.sun.istack.internal.NotNull;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Collections;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.api.server.SDDocument;
import java.util.Map;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.server.Adapter;

public class HttpAdapter extends Adapter<HttpToolkit>
{
    private static final Logger LOGGER;
    protected Map<String, SDDocument> wsdls;
    private Map<SDDocument, String> revWsdls;
    private ServiceDefinition serviceDefinition;
    public final HttpAdapterList<? extends HttpAdapter> owner;
    public final String urlPattern;
    protected boolean stickyCookie;
    protected boolean disableJreplicaCookie;
    public static final CompletionCallback NO_OP_COMPLETION_CALLBACK;
    public static volatile boolean dump;
    public static volatile int dump_threshold;
    public static volatile boolean publishStatusPage;
    
    public static HttpAdapter createAlone(final WSEndpoint endpoint) {
        return new DummyList().createAdapter("", "", endpoint);
    }
    
    @Deprecated
    protected HttpAdapter(final WSEndpoint endpoint, final HttpAdapterList<? extends HttpAdapter> owner) {
        this(endpoint, owner, null);
    }
    
    protected HttpAdapter(final WSEndpoint endpoint, final HttpAdapterList<? extends HttpAdapter> owner, final String urlPattern) {
        super(endpoint);
        this.serviceDefinition = null;
        this.disableJreplicaCookie = false;
        this.owner = owner;
        this.urlPattern = urlPattern;
        this.initWSDLMap(endpoint.getServiceDefinition());
    }
    
    public ServiceDefinition getServiceDefinition() {
        return this.serviceDefinition;
    }
    
    public final void initWSDLMap(final ServiceDefinition sdef) {
        this.serviceDefinition = sdef;
        if (sdef == null) {
            this.wsdls = Collections.emptyMap();
            this.revWsdls = Collections.emptyMap();
        }
        else {
            this.wsdls = new HashMap<String, SDDocument>();
            final Map<String, SDDocument> systemIds = new TreeMap<String, SDDocument>();
            for (final SDDocument sdd : sdef) {
                if (sdd == sdef.getPrimary()) {
                    this.wsdls.put("wsdl", sdd);
                    this.wsdls.put("WSDL", sdd);
                }
                else {
                    systemIds.put(sdd.getURL().toString(), sdd);
                }
            }
            int wsdlnum = 1;
            int xsdnum = 1;
            for (final Map.Entry<String, SDDocument> e : systemIds.entrySet()) {
                final SDDocument sdd2 = e.getValue();
                if (sdd2.isWSDL()) {
                    this.wsdls.put("wsdl=" + wsdlnum++, sdd2);
                }
                if (sdd2.isSchema()) {
                    this.wsdls.put("xsd=" + xsdnum++, sdd2);
                }
            }
            this.revWsdls = new HashMap<SDDocument, String>();
            for (final Map.Entry<String, SDDocument> e : this.wsdls.entrySet()) {
                if (!e.getKey().equals("WSDL")) {
                    this.revWsdls.put(e.getValue(), e.getKey());
                }
            }
        }
    }
    
    public String getValidPath() {
        if (this.urlPattern.endsWith("/*")) {
            return this.urlPattern.substring(0, this.urlPattern.length() - 2);
        }
        return this.urlPattern;
    }
    
    @Override
    protected HttpToolkit createToolkit() {
        return new HttpToolkit();
    }
    
    public void handle(@NotNull final WSHTTPConnection connection) throws IOException {
        if (this.handleGet(connection)) {
            return;
        }
        final Pool<HttpToolkit> currentPool = this.getPool();
        final HttpToolkit tk = currentPool.take();
        try {
            tk.handle(connection);
        }
        finally {
            currentPool.recycle(tk);
        }
    }
    
    public boolean handleGet(@NotNull final WSHTTPConnection connection) throws IOException {
        if (connection.getRequestMethod().equals("GET")) {
            for (final Component c : this.endpoint.getComponents()) {
                final HttpMetadataPublisher spi = c.getSPI(HttpMetadataPublisher.class);
                if (spi != null && spi.handleMetadataRequest(this, connection)) {
                    return true;
                }
            }
            if (this.isMetadataQuery(connection.getQueryString())) {
                this.publishWSDL(connection);
                return true;
            }
            final Binding binding = this.getEndpoint().getBinding();
            if (!(binding instanceof HTTPBinding)) {
                this.writeWebServicesHtmlPage(connection);
                return true;
            }
        }
        else if (connection.getRequestMethod().equals("HEAD")) {
            connection.getInput().close();
            final Binding binding = this.getEndpoint().getBinding();
            if (this.isMetadataQuery(connection.getQueryString())) {
                final SDDocument doc = this.wsdls.get(connection.getQueryString());
                connection.setStatus((doc != null) ? 200 : 404);
                connection.getOutput().close();
                connection.close();
                return true;
            }
            if (!(binding instanceof HTTPBinding)) {
                connection.setStatus(404);
                connection.getOutput().close();
                connection.close();
                return true;
            }
        }
        return false;
    }
    
    private Packet decodePacket(@NotNull final WSHTTPConnection con, @NotNull final Codec codec) throws IOException {
        final String ct = con.getRequestHeader("Content-Type");
        InputStream in = con.getInput();
        final Packet packet = new Packet();
        packet.soapAction = fixQuotesAroundSoapAction(con.getRequestHeader("SOAPAction"));
        packet.wasTransportSecure = con.isSecure();
        packet.acceptableMimeTypes = con.getRequestHeader("Accept");
        packet.addSatellite(con);
        this.addSatellites(packet);
        packet.isAdapterDeliversNonAnonymousResponse = true;
        packet.component = this;
        packet.transportBackChannel = new Oneway(con);
        packet.webServiceContextDelegate = con.getWebServiceContextDelegate();
        packet.setState(Packet.State.ServerRequest);
        if (HttpAdapter.dump || HttpAdapter.LOGGER.isLoggable(Level.FINER)) {
            final ByteArrayBuffer buf = new ByteArrayBuffer();
            buf.write(in);
            in.close();
            dump(buf, "HTTP request", con.getRequestHeaders());
            in = buf.newInputStream();
        }
        codec.decode(in, ct, packet);
        return packet;
    }
    
    protected void addSatellites(final Packet packet) {
    }
    
    public static String fixQuotesAroundSoapAction(final String soapAction) {
        if (soapAction != null && (!soapAction.startsWith("\"") || !soapAction.endsWith("\""))) {
            if (HttpAdapter.LOGGER.isLoggable(Level.INFO)) {
                HttpAdapter.LOGGER.log(Level.INFO, "Received WS-I BP non-conformant Unquoted SoapAction HTTP header: {0}", soapAction);
            }
            String fixedSoapAction = soapAction;
            if (!soapAction.startsWith("\"")) {
                fixedSoapAction = "\"" + fixedSoapAction;
            }
            if (!soapAction.endsWith("\"")) {
                fixedSoapAction += "\"";
            }
            return fixedSoapAction;
        }
        return soapAction;
    }
    
    protected NonAnonymousResponseProcessor getNonAnonymousResponseProcessor() {
        return NonAnonymousResponseProcessor.getDefault();
    }
    
    protected void writeClientError(final int connStatus, @NotNull final OutputStream os, @NotNull final Packet packet) throws IOException {
    }
    
    private boolean isClientErrorStatus(final int connStatus) {
        return connStatus == 403;
    }
    
    private boolean isNonAnonymousUri(final EndpointAddress addr) {
        return addr != null && !addr.toString().equals(AddressingVersion.W3C.anonymousUri) && !addr.toString().equals(AddressingVersion.MEMBER.anonymousUri);
    }
    
    private void encodePacket(@NotNull Packet packet, @NotNull final WSHTTPConnection con, @NotNull final Codec codec) throws IOException {
        if (this.isNonAnonymousUri(packet.endpointAddress) && packet.getMessage() != null) {
            try {
                packet = this.getNonAnonymousResponseProcessor().process(packet);
            }
            catch (final RuntimeException re) {
                final SOAPVersion soapVersion = packet.getBinding().getSOAPVersion();
                final Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, re);
                packet = packet.createServerResponse(faultMsg, packet.endpoint.getPort(), null, packet.endpoint.getBinding());
            }
        }
        if (con.isClosed()) {
            return;
        }
        final Message responseMessage = packet.getMessage();
        this.addStickyCookie(con);
        this.addReplicaCookie(con, packet);
        if (responseMessage == null) {
            if (!con.isClosed()) {
                if (con.getStatus() == 0) {
                    con.setStatus(202);
                }
                final OutputStream os = con.getProtocol().contains("1.1") ? con.getOutput() : new Http10OutputStream(con);
                if (HttpAdapter.dump || HttpAdapter.LOGGER.isLoggable(Level.FINER)) {
                    final ByteArrayBuffer buf = new ByteArrayBuffer();
                    codec.encode(packet, buf);
                    dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                    buf.writeTo(os);
                }
                else {
                    codec.encode(packet, os);
                }
                try {
                    os.close();
                }
                catch (final IOException e) {
                    throw new WebServiceException(e);
                }
            }
        }
        else {
            if (con.getStatus() == 0) {
                con.setStatus(responseMessage.isFault() ? 500 : 200);
            }
            if (this.isClientErrorStatus(con.getStatus())) {
                final OutputStream os = con.getOutput();
                if (HttpAdapter.dump || HttpAdapter.LOGGER.isLoggable(Level.FINER)) {
                    final ByteArrayBuffer buf = new ByteArrayBuffer();
                    this.writeClientError(con.getStatus(), buf, packet);
                    dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                    buf.writeTo(os);
                }
                else {
                    this.writeClientError(con.getStatus(), os, packet);
                }
                os.close();
                return;
            }
            ContentType contentType = codec.getStaticContentType(packet);
            if (contentType != null) {
                con.setContentTypeResponseHeader(contentType.getContentType());
                final OutputStream os2 = con.getProtocol().contains("1.1") ? con.getOutput() : new Http10OutputStream(con);
                if (HttpAdapter.dump || HttpAdapter.LOGGER.isLoggable(Level.FINER)) {
                    final ByteArrayBuffer buf2 = new ByteArrayBuffer();
                    codec.encode(packet, buf2);
                    dump(buf2, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                    buf2.writeTo(os2);
                }
                else {
                    codec.encode(packet, os2);
                }
                os2.close();
            }
            else {
                final ByteArrayBuffer buf = new ByteArrayBuffer();
                contentType = codec.encode(packet, buf);
                con.setContentTypeResponseHeader(contentType.getContentType());
                if (HttpAdapter.dump || HttpAdapter.LOGGER.isLoggable(Level.FINER)) {
                    dump(buf, "HTTP response " + con.getStatus(), con.getResponseHeaders());
                }
                final OutputStream os3 = con.getOutput();
                buf.writeTo(os3);
                os3.close();
            }
        }
    }
    
    private void addStickyCookie(final WSHTTPConnection con) {
        if (this.stickyCookie) {
            final String proxyJroute = con.getRequestHeader("proxy-jroute");
            if (proxyJroute == null) {
                return;
            }
            final String jrouteId = con.getCookie("JROUTE");
            if (jrouteId == null || !jrouteId.equals(proxyJroute)) {
                con.setCookie("JROUTE", proxyJroute);
            }
        }
    }
    
    private void addReplicaCookie(final WSHTTPConnection con, final Packet packet) {
        if (this.stickyCookie) {
            HaInfo haInfo = null;
            if (packet.supports("com.sun.xml.internal.ws.api.message.packet.hainfo")) {
                haInfo = (HaInfo)packet.get("com.sun.xml.internal.ws.api.message.packet.hainfo");
            }
            if (haInfo != null) {
                con.setCookie("METRO_KEY", haInfo.getKey());
                if (!this.disableJreplicaCookie) {
                    con.setCookie("JREPLICA", haInfo.getReplicaInstance());
                }
            }
        }
    }
    
    public void invokeAsync(final WSHTTPConnection con) throws IOException {
        this.invokeAsync(con, HttpAdapter.NO_OP_COMPLETION_CALLBACK);
    }
    
    public void invokeAsync(final WSHTTPConnection con, final CompletionCallback callback) throws IOException {
        if (this.handleGet(con)) {
            callback.onCompletion();
            return;
        }
        final Pool<HttpToolkit> currentPool = this.getPool();
        final HttpToolkit tk = currentPool.take();
        Packet request;
        try {
            request = this.decodePacket(con, tk.codec);
        }
        catch (final ExceptionHasMessage e) {
            HttpAdapter.LOGGER.log(Level.SEVERE, e.getMessage(), e);
            final Packet response = new Packet();
            response.setMessage(e.getFaultMessage());
            this.encodePacket(response, con, tk.codec);
            currentPool.recycle(tk);
            con.close();
            callback.onCompletion();
            return;
        }
        catch (final UnsupportedMediaException e2) {
            HttpAdapter.LOGGER.log(Level.SEVERE, e2.getMessage(), e2);
            final Packet response = new Packet();
            con.setStatus(415);
            this.encodePacket(response, con, tk.codec);
            currentPool.recycle(tk);
            con.close();
            callback.onCompletion();
            return;
        }
        this.endpoint.process(request, new WSEndpoint.CompletionCallback() {
            @Override
            public void onCompletion(@NotNull final Packet response) {
                try {
                    try {
                        HttpAdapter.this.encodePacket(response, con, tk.codec);
                    }
                    catch (final IOException ioe) {
                        HttpAdapter.LOGGER.log(Level.SEVERE, ioe.getMessage(), ioe);
                    }
                    currentPool.recycle(tk);
                }
                finally {
                    con.close();
                    callback.onCompletion();
                }
            }
        }, null);
    }
    
    private boolean isMetadataQuery(final String query) {
        return query != null && (query.equals("WSDL") || query.startsWith("wsdl") || query.startsWith("xsd="));
    }
    
    public void publishWSDL(@NotNull final WSHTTPConnection con) throws IOException {
        con.getInput().close();
        final SDDocument doc = this.wsdls.get(con.getQueryString());
        if (doc == null) {
            this.writeNotFoundErrorPage(con, "Invalid Request");
            return;
        }
        con.setStatus(200);
        con.setContentTypeResponseHeader("text/xml;charset=utf-8");
        final OutputStream os = con.getProtocol().contains("1.1") ? con.getOutput() : new Http10OutputStream(con);
        final PortAddressResolver portAddressResolver = this.getPortAddressResolver(con.getBaseAddress());
        final DocumentAddressResolver resolver = this.getDocumentAddressResolver(portAddressResolver);
        doc.writeTo(portAddressResolver, resolver, os);
        os.close();
    }
    
    public PortAddressResolver getPortAddressResolver(final String baseAddress) {
        return this.owner.createPortAddressResolver(baseAddress, this.endpoint.getImplementationClass());
    }
    
    public DocumentAddressResolver getDocumentAddressResolver(final PortAddressResolver portAddressResolver) {
        final String address = portAddressResolver.getAddressFor(this.endpoint.getServiceName(), this.endpoint.getPortName().getLocalPart());
        assert address != null;
        return new DocumentAddressResolver() {
            @Override
            public String getRelativeAddressFor(@NotNull final SDDocument current, @NotNull final SDDocument referenced) {
                assert HttpAdapter.this.revWsdls.containsKey(referenced);
                return address + '?' + HttpAdapter.this.revWsdls.get(referenced);
            }
        };
    }
    
    private void writeNotFoundErrorPage(final WSHTTPConnection con, final String message) throws IOException {
        con.setStatus(404);
        con.setContentTypeResponseHeader("text/html; charset=utf-8");
        final PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutput(), "UTF-8"));
        out.println("<html>");
        out.println("<head><title>");
        out.println(WsservletMessages.SERVLET_HTML_TITLE());
        out.println("</title></head>");
        out.println("<body>");
        out.println(WsservletMessages.SERVLET_HTML_NOT_FOUND(message));
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
    
    private void writeInternalServerError(final WSHTTPConnection con) throws IOException {
        con.setStatus(500);
        con.getOutput().close();
    }
    
    private static void dump(final ByteArrayBuffer buf, final String caption, final Map<String, List<String>> headers) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter pw = new PrintWriter(baos, true);
        pw.println("---[" + caption + "]---");
        if (headers != null) {
            for (final Map.Entry<String, List<String>> header : headers.entrySet()) {
                if (header.getValue().isEmpty()) {
                    pw.println(header.getValue());
                }
                else {
                    for (final String value : header.getValue()) {
                        pw.println(header.getKey() + ": " + value);
                    }
                }
            }
        }
        if (buf.size() > HttpAdapter.dump_threshold) {
            final byte[] b = buf.getRawData();
            baos.write(b, 0, HttpAdapter.dump_threshold);
            pw.println();
            pw.println(WsservletMessages.MESSAGE_TOO_LONG(HttpAdapter.class.getName() + ".dumpTreshold"));
        }
        else {
            buf.writeTo(baos);
        }
        pw.println("--------------------");
        final String msg = baos.toString();
        if (HttpAdapter.dump) {
            System.out.println(msg);
        }
        if (HttpAdapter.LOGGER.isLoggable(Level.FINER)) {
            HttpAdapter.LOGGER.log(Level.FINER, msg);
        }
    }
    
    private void writeWebServicesHtmlPage(final WSHTTPConnection con) throws IOException {
        if (!HttpAdapter.publishStatusPage) {
            return;
        }
        con.getInput().close();
        con.setStatus(200);
        con.setContentTypeResponseHeader("text/html; charset=utf-8");
        final PrintWriter out = new PrintWriter(new OutputStreamWriter(con.getOutput(), "UTF-8"));
        out.println("<html>");
        out.println("<head><title>");
        out.println(WsservletMessages.SERVLET_HTML_TITLE());
        out.println("</title></head>");
        out.println("<body>");
        out.println(WsservletMessages.SERVLET_HTML_TITLE_2());
        final Module module = this.getEndpoint().getContainer().getSPI(Module.class);
        List<BoundEndpoint> endpoints = Collections.emptyList();
        if (module != null) {
            endpoints = module.getBoundEndpoints();
        }
        if (endpoints.isEmpty()) {
            out.println(WsservletMessages.SERVLET_HTML_NO_INFO_AVAILABLE());
        }
        else {
            out.println("<table width='100%' border='1'>");
            out.println("<tr>");
            out.println("<td>");
            out.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_PORT_NAME());
            out.println("</td>");
            out.println("<td>");
            out.println(WsservletMessages.SERVLET_HTML_COLUMN_HEADER_INFORMATION());
            out.println("</td>");
            out.println("</tr>");
            for (final BoundEndpoint a : endpoints) {
                final String endpointAddress = a.getAddress(con.getBaseAddress()).toString();
                out.println("<tr>");
                out.println("<td>");
                out.println(WsservletMessages.SERVLET_HTML_ENDPOINT_TABLE(a.getEndpoint().getServiceName(), a.getEndpoint().getPortName()));
                out.println("</td>");
                out.println("<td>");
                out.println(WsservletMessages.SERVLET_HTML_INFORMATION_TABLE(endpointAddress, a.getEndpoint().getImplementationClass().getName()));
                out.println("</td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
    
    public static synchronized void setPublishStatus(final boolean publish) {
        HttpAdapter.publishStatusPage = publish;
    }
    
    public static void setDump(final boolean dumpMessages) {
        HttpAdapter.dump = dumpMessages;
    }
    
    static {
        LOGGER = Logger.getLogger(HttpAdapter.class.getName());
        NO_OP_COMPLETION_CALLBACK = new CompletionCallback() {
            @Override
            public void onCompletion() {
            }
        };
        HttpAdapter.dump = false;
        HttpAdapter.dump_threshold = 4096;
        HttpAdapter.publishStatusPage = true;
        try {
            HttpAdapter.dump = Boolean.getBoolean(HttpAdapter.class.getName() + ".dump");
        }
        catch (final SecurityException se) {
            if (HttpAdapter.LOGGER.isLoggable(Level.CONFIG)) {
                HttpAdapter.LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[] { HttpAdapter.class.getName() + ".dump" });
            }
        }
        try {
            HttpAdapter.dump_threshold = Integer.getInteger(HttpAdapter.class.getName() + ".dumpTreshold", 4096);
        }
        catch (final SecurityException se) {
            if (HttpAdapter.LOGGER.isLoggable(Level.CONFIG)) {
                HttpAdapter.LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[] { HttpAdapter.class.getName() + ".dumpTreshold" });
            }
        }
        try {
            setPublishStatus(Boolean.getBoolean(HttpAdapter.class.getName() + ".publishStatusPage"));
        }
        catch (final SecurityException se) {
            if (HttpAdapter.LOGGER.isLoggable(Level.CONFIG)) {
                HttpAdapter.LOGGER.log(Level.CONFIG, "Cannot read ''{0}'' property, using defaults.", new Object[] { HttpAdapter.class.getName() + ".publishStatusPage" });
            }
        }
    }
    
    final class AsyncTransport extends AbstractServerAsyncTransport<WSHTTPConnection>
    {
        public AsyncTransport() {
            super(HttpAdapter.this.endpoint);
        }
        
        public void handleAsync(final WSHTTPConnection con) throws IOException {
            super.handle(con);
        }
        
        @Override
        protected void encodePacket(final WSHTTPConnection con, @NotNull final Packet packet, @NotNull final Codec codec) throws IOException {
            HttpAdapter.this.encodePacket(packet, con, codec);
        }
        
        @Nullable
        @Override
        protected String getAcceptableMimeTypes(final WSHTTPConnection con) {
            return null;
        }
        
        @Nullable
        @Override
        protected TransportBackChannel getTransportBackChannel(final WSHTTPConnection con) {
            return new Oneway(con);
        }
        
        @NotNull
        @Override
        protected PropertySet getPropertySet(final WSHTTPConnection con) {
            return con;
        }
        
        @NotNull
        @Override
        protected WebServiceContextDelegate getWebServiceContextDelegate(final WSHTTPConnection con) {
            return con.getWebServiceContextDelegate();
        }
    }
    
    static final class Oneway implements TransportBackChannel
    {
        WSHTTPConnection con;
        boolean closed;
        
        Oneway(final WSHTTPConnection con) {
            this.con = con;
        }
        
        @Override
        public void close() {
            if (!this.closed) {
                this.closed = true;
                if (this.con.getStatus() == 0) {
                    this.con.setStatus(202);
                }
                OutputStream output = null;
                try {
                    output = this.con.getOutput();
                }
                catch (final IOException ex) {}
                Label_0125: {
                    if (!HttpAdapter.dump) {
                        if (!HttpAdapter.LOGGER.isLoggable(Level.FINER)) {
                            break Label_0125;
                        }
                    }
                    try {
                        final ByteArrayBuffer buf = new ByteArrayBuffer();
                        dump(buf, "HTTP response " + this.con.getStatus(), this.con.getResponseHeaders());
                    }
                    catch (final Exception e) {
                        throw new WebServiceException(e.toString(), e);
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    }
                    catch (final IOException e2) {
                        throw new WebServiceException(e2);
                    }
                }
                this.con.close();
            }
        }
    }
    
    final class HttpToolkit extends Toolkit
    {
        public void handle(final WSHTTPConnection con) throws IOException {
            try {
                boolean invoke = false;
                Packet packet;
                try {
                    packet = HttpAdapter.this.decodePacket(con, this.codec);
                    invoke = true;
                }
                catch (final Exception e) {
                    packet = new Packet();
                    if (e instanceof ExceptionHasMessage) {
                        HttpAdapter.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        packet.setMessage(((ExceptionHasMessage)e).getFaultMessage());
                    }
                    else if (e instanceof UnsupportedMediaException) {
                        HttpAdapter.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        con.setStatus(415);
                    }
                    else {
                        HttpAdapter.LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        con.setStatus(500);
                    }
                }
                if (invoke) {
                    try {
                        packet = this.head.process(packet, con.getWebServiceContextDelegate(), packet.transportBackChannel);
                    }
                    catch (final Throwable e2) {
                        HttpAdapter.LOGGER.log(Level.SEVERE, e2.getMessage(), e2);
                        if (!con.isClosed()) {
                            HttpAdapter.this.writeInternalServerError(con);
                        }
                        return;
                    }
                }
                HttpAdapter.this.encodePacket(packet, con, this.codec);
            }
            finally {
                if (!con.isClosed()) {
                    if (HttpAdapter.LOGGER.isLoggable(Level.FINE)) {
                        HttpAdapter.LOGGER.log(Level.FINE, "Closing HTTP Connection with status: {0}", con.getStatus());
                    }
                    con.close();
                }
            }
        }
    }
    
    private static final class Http10OutputStream extends ByteArrayBuffer
    {
        private final WSHTTPConnection con;
        
        Http10OutputStream(final WSHTTPConnection con) {
            this.con = con;
        }
        
        @Override
        public void close() throws IOException {
            super.close();
            this.con.setContentLengthResponseHeader(this.size());
            final OutputStream os = this.con.getOutput();
            this.writeTo(os);
            os.close();
        }
    }
    
    private static final class DummyList extends HttpAdapterList<HttpAdapter>
    {
        @Override
        protected HttpAdapter createHttpAdapter(final String name, final String urlPattern, final WSEndpoint<?> endpoint) {
            return new HttpAdapter(endpoint, this, urlPattern);
        }
    }
    
    public interface CompletionCallback
    {
        void onCompletion();
    }
}
