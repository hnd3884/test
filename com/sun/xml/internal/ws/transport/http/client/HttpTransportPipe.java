package com.sun.xml.internal.ws.transport.http.client;

import com.sun.xml.internal.ws.util.RuntimeVersion;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import javax.xml.bind.DatatypeConverter;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import com.sun.xml.internal.ws.api.SOAPVersion;
import java.io.IOException;
import java.io.InputStream;
import com.sun.xml.internal.ws.client.ClientTransportException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.util.StreamUtils;
import com.sun.xml.internal.ws.api.pipe.ContentType;
import javax.xml.ws.WebServiceException;
import java.util.logging.Level;
import javax.xml.ws.soap.SOAPBinding;
import java.util.Collections;
import java.io.OutputStream;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.xml.internal.ws.transport.Headers;
import java.util.Map;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import com.sun.xml.internal.ws.developer.HttpConfigFeature;
import java.net.CookieHandler;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.pipe.Codec;
import java.util.logging.Logger;
import java.util.List;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;

public class HttpTransportPipe extends AbstractTubeImpl
{
    private static final List<String> USER_AGENT;
    private static final Logger LOGGER;
    public static boolean dump;
    private final Codec codec;
    private final WSBinding binding;
    private final CookieHandler cookieJar;
    private final boolean sticky;
    
    public HttpTransportPipe(final Codec codec, final WSBinding binding) {
        this.codec = codec;
        this.binding = binding;
        this.sticky = isSticky(binding);
        HttpConfigFeature configFeature = binding.getFeature(HttpConfigFeature.class);
        if (configFeature == null) {
            configFeature = new HttpConfigFeature();
        }
        this.cookieJar = configFeature.getCookieHandler();
    }
    
    private static boolean isSticky(final WSBinding binding) {
        boolean tSticky = false;
        final WebServiceFeature[] array;
        final WebServiceFeature[] features = array = binding.getFeatures().toArray();
        for (final WebServiceFeature f : array) {
            if (f instanceof StickyFeature) {
                tSticky = true;
                break;
            }
        }
        return tSticky;
    }
    
    private HttpTransportPipe(final HttpTransportPipe that, final TubeCloner cloner) {
        this(that.codec.copy(), that.binding);
        cloner.add(that, this);
    }
    
    @Override
    public NextAction processException(@NotNull final Throwable t) {
        return this.doThrow(t);
    }
    
    @Override
    public NextAction processRequest(@NotNull final Packet request) {
        return this.doReturnWith(this.process(request));
    }
    
    @Override
    public NextAction processResponse(@NotNull final Packet response) {
        return this.doReturnWith(response);
    }
    
    protected HttpClientTransport getTransport(final Packet request, final Map<String, List<String>> reqHeaders) {
        return new HttpClientTransport(request, reqHeaders);
    }
    
    @Override
    public Packet process(final Packet request) {
        try {
            final Map<String, List<String>> reqHeaders = new Headers();
            final Map<String, List<String>> userHeaders = request.invocationProperties.get("javax.xml.ws.http.request.headers");
            boolean addUserAgent = true;
            if (userHeaders != null) {
                reqHeaders.putAll(userHeaders);
                if (userHeaders.get("User-Agent") != null) {
                    addUserAgent = false;
                }
            }
            if (addUserAgent) {
                reqHeaders.put("User-Agent", HttpTransportPipe.USER_AGENT);
            }
            this.addBasicAuth(request, reqHeaders);
            this.addCookies(request, reqHeaders);
            final HttpClientTransport con = this.getTransport(request, reqHeaders);
            request.addSatellite(new HttpResponseProperties(con));
            ContentType ct = this.codec.getStaticContentType(request);
            if (ct == null) {
                final ByteArrayBuffer buf = new ByteArrayBuffer();
                ct = this.codec.encode(request, buf);
                reqHeaders.put("Content-Length", Collections.singletonList(Integer.toString(buf.size())));
                reqHeaders.put("Content-Type", Collections.singletonList(ct.getContentType()));
                if (ct.getAcceptHeader() != null) {
                    reqHeaders.put("Accept", Collections.singletonList(ct.getAcceptHeader()));
                }
                if (this.binding instanceof SOAPBinding) {
                    this.writeSOAPAction(reqHeaders, ct.getSOAPActionHeader());
                }
                if (HttpTransportPipe.dump || HttpTransportPipe.LOGGER.isLoggable(Level.FINER)) {
                    this.dump(buf, "HTTP request", reqHeaders);
                }
                buf.writeTo(con.getOutput());
            }
            else {
                reqHeaders.put("Content-Type", Collections.singletonList(ct.getContentType()));
                if (ct.getAcceptHeader() != null) {
                    reqHeaders.put("Accept", Collections.singletonList(ct.getAcceptHeader()));
                }
                if (this.binding instanceof SOAPBinding) {
                    this.writeSOAPAction(reqHeaders, ct.getSOAPActionHeader());
                }
                if (HttpTransportPipe.dump || HttpTransportPipe.LOGGER.isLoggable(Level.FINER)) {
                    final ByteArrayBuffer buf = new ByteArrayBuffer();
                    this.codec.encode(request, buf);
                    this.dump(buf, "HTTP request - " + request.endpointAddress, reqHeaders);
                    final OutputStream out = con.getOutput();
                    if (out != null) {
                        buf.writeTo(out);
                    }
                }
                else {
                    final OutputStream os = con.getOutput();
                    if (os != null) {
                        this.codec.encode(request, os);
                    }
                }
            }
            con.closeOutput();
            return this.createResponsePacket(request, con);
        }
        catch (final WebServiceException wex) {
            throw wex;
        }
        catch (final Exception ex) {
            throw new WebServiceException(ex);
        }
    }
    
    private Packet createResponsePacket(final Packet request, final HttpClientTransport con) throws IOException {
        con.readResponseCodeAndMessage();
        this.recordCookies(request, con);
        InputStream responseStream = con.getInput();
        if (HttpTransportPipe.dump || HttpTransportPipe.LOGGER.isLoggable(Level.FINER)) {
            final ByteArrayBuffer buf = new ByteArrayBuffer();
            if (responseStream != null) {
                buf.write(responseStream);
                responseStream.close();
            }
            this.dump(buf, "HTTP response - " + request.endpointAddress + " - " + con.statusCode, con.getHeaders());
            responseStream = buf.newInputStream();
        }
        final int cl = con.contentLength;
        InputStream tempIn = null;
        if (cl == -1) {
            tempIn = StreamUtils.hasSomeData(responseStream);
            if (tempIn != null) {
                responseStream = tempIn;
            }
        }
        if ((cl == 0 || (cl == -1 && tempIn == null)) && responseStream != null) {
            responseStream.close();
            responseStream = null;
        }
        this.checkStatusCode(responseStream, con);
        final Packet reply = request.createClientResponse(null);
        reply.wasTransportSecure = con.isSecure();
        if (responseStream != null) {
            final String contentType = con.getContentType();
            if (contentType != null && contentType.contains("text/html") && this.binding instanceof SOAPBinding) {
                throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(con.statusCode, con.statusMessage));
            }
            this.codec.decode(responseStream, contentType, reply);
        }
        return reply;
    }
    
    private void checkStatusCode(final InputStream in, final HttpClientTransport con) throws IOException {
        final int statusCode = con.statusCode;
        final String statusMessage = con.statusMessage;
        if (this.binding instanceof SOAPBinding) {
            if (this.binding.getSOAPVersion() == SOAPVersion.SOAP_12) {
                if (statusCode == 200 || statusCode == 202 || this.isErrorCode(statusCode)) {
                    if (this.isErrorCode(statusCode) && in == null) {
                        throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(statusCode, statusMessage));
                    }
                    return;
                }
            }
            else if (statusCode == 200 || statusCode == 202 || statusCode == 500) {
                if (statusCode == 500 && in == null) {
                    throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(statusCode, statusMessage));
                }
                return;
            }
            if (in != null) {
                in.close();
            }
            throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(statusCode, statusMessage));
        }
    }
    
    private boolean isErrorCode(final int code) {
        return code == 500 || code == 400;
    }
    
    private void addCookies(final Packet context, final Map<String, List<String>> reqHeaders) throws IOException {
        final Boolean shouldMaintainSessionProperty = context.invocationProperties.get("javax.xml.ws.session.maintain");
        if (shouldMaintainSessionProperty != null && !shouldMaintainSessionProperty) {
            return;
        }
        if (this.sticky || (shouldMaintainSessionProperty != null && shouldMaintainSessionProperty)) {
            final Map<String, List<String>> rememberedCookies = this.cookieJar.get(context.endpointAddress.getURI(), reqHeaders);
            this.processCookieHeaders(reqHeaders, rememberedCookies, "Cookie");
            this.processCookieHeaders(reqHeaders, rememberedCookies, "Cookie2");
        }
    }
    
    private void processCookieHeaders(final Map<String, List<String>> requestHeaders, final Map<String, List<String>> rememberedCookies, final String cookieHeader) {
        final List<String> jarCookies = rememberedCookies.get(cookieHeader);
        if (jarCookies != null && !jarCookies.isEmpty()) {
            final List<String> resultCookies = this.mergeUserCookies(jarCookies, requestHeaders.get(cookieHeader));
            requestHeaders.put(cookieHeader, resultCookies);
        }
    }
    
    private List<String> mergeUserCookies(final List<String> rememberedCookies, final List<String> userCookies) {
        if (userCookies == null || userCookies.isEmpty()) {
            return rememberedCookies;
        }
        final Map<String, String> map = new HashMap<String, String>();
        this.cookieListToMap(rememberedCookies, map);
        this.cookieListToMap(userCookies, map);
        return new ArrayList<String>(map.values());
    }
    
    private void cookieListToMap(final List<String> cookieList, final Map<String, String> targetMap) {
        for (final String cookie : cookieList) {
            final int index = cookie.indexOf("=");
            final String cookieName = cookie.substring(0, index);
            targetMap.put(cookieName, cookie);
        }
    }
    
    private void recordCookies(final Packet context, final HttpClientTransport con) throws IOException {
        final Boolean shouldMaintainSessionProperty = context.invocationProperties.get("javax.xml.ws.session.maintain");
        if (shouldMaintainSessionProperty != null && !shouldMaintainSessionProperty) {
            return;
        }
        if (this.sticky || (shouldMaintainSessionProperty != null && shouldMaintainSessionProperty)) {
            this.cookieJar.put(context.endpointAddress.getURI(), con.getHeaders());
        }
    }
    
    private void addBasicAuth(final Packet context, final Map<String, List<String>> reqHeaders) {
        final String user = context.invocationProperties.get("javax.xml.ws.security.auth.username");
        if (user != null) {
            final String pw = context.invocationProperties.get("javax.xml.ws.security.auth.password");
            if (pw != null) {
                final StringBuilder buf = new StringBuilder(user);
                buf.append(":");
                buf.append(pw);
                final String creds = DatatypeConverter.printBase64Binary(buf.toString().getBytes());
                reqHeaders.put("Authorization", Collections.singletonList("Basic " + creds));
            }
        }
    }
    
    private void writeSOAPAction(final Map<String, List<String>> reqHeaders, final String soapAction) {
        if (SOAPVersion.SOAP_12.equals(this.binding.getSOAPVersion())) {
            return;
        }
        if (soapAction != null) {
            reqHeaders.put("SOAPAction", Collections.singletonList(soapAction));
        }
        else {
            reqHeaders.put("SOAPAction", Collections.singletonList("\"\""));
        }
    }
    
    @Override
    public void preDestroy() {
    }
    
    @Override
    public HttpTransportPipe copy(final TubeCloner cloner) {
        return new HttpTransportPipe(this, cloner);
    }
    
    private void dump(final ByteArrayBuffer buf, final String caption, final Map<String, List<String>> headers) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter pw = new PrintWriter(baos, true);
        pw.println("---[" + caption + "]---");
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
        if (HttpTransportPipe.dump) {
            System.out.println(msg);
        }
        if (HttpTransportPipe.LOGGER.isLoggable(Level.FINER)) {
            HttpTransportPipe.LOGGER.log(Level.FINER, msg);
        }
    }
    
    static {
        USER_AGENT = Collections.singletonList(RuntimeVersion.VERSION.toString());
        LOGGER = Logger.getLogger(HttpTransportPipe.class.getName());
        boolean b;
        try {
            b = Boolean.getBoolean(HttpTransportPipe.class.getName() + ".dump");
        }
        catch (final Throwable t) {
            b = false;
        }
        HttpTransportPipe.dump = b;
    }
}
