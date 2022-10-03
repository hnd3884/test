package org.apache.coyote.ajp;

import org.apache.tomcat.util.net.ApplicationBufferHandler;
import java.util.Collections;
import java.util.HashSet;
import org.apache.juli.logging.LogFactory;
import org.apache.coyote.Response;
import java.nio.ByteBuffer;
import java.io.EOFException;
import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.tomcat.util.http.HttpMessages;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.coyote.RequestInfo;
import org.apache.coyote.ActionCode;
import java.io.InterruptedIOException;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.coyote.ErrorState;
import org.apache.tomcat.util.net.SocketWrapperBase;
import java.io.IOException;
import org.apache.coyote.OutputBuffer;
import org.apache.coyote.InputBuffer;
import org.apache.tomcat.util.net.AbstractEndpoint;
import java.util.regex.Pattern;
import org.apache.tomcat.util.buf.MessageBytes;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.coyote.AbstractProcessor;

public class AjpProcessor extends AbstractProcessor
{
    private static final Log log;
    private static final StringManager sm;
    private static final byte[] endMessageArray;
    private static final byte[] endAndCloseMessageArray;
    private static final byte[] flushMessageArray;
    private static final byte[] pongMessageArray;
    private static final Set<String> javaxAttributes;
    private static final Set<String> iisTlsAttributes;
    private final byte[] getBodyMessageArray;
    private final int outputMaxChunkSize;
    private final AjpMessage requestHeaderMessage;
    private final AjpMessage responseMessage;
    private int responseMsgPos;
    private final AjpMessage bodyMessage;
    private final MessageBytes bodyBytes;
    private final MessageBytes tmpMB;
    private final MessageBytes certificates;
    private boolean endOfStream;
    private boolean empty;
    private boolean first;
    private boolean waitingForBodyMessage;
    private boolean replay;
    private boolean swallowResponse;
    private boolean responseFinished;
    private long bytesWritten;
    protected boolean ajpFlush;
    private int keepAliveTimeout;
    private boolean tomcatAuthentication;
    private boolean tomcatAuthorization;
    private String secret;
    private String clientCertProvider;
    @Deprecated
    private boolean sendReasonPhrase;
    private Pattern allowedRequestAttributesPattern;
    
    public AjpProcessor(final int packetSize, final AbstractEndpoint<?> endpoint) {
        super(endpoint);
        this.responseMsgPos = -1;
        this.bodyBytes = MessageBytes.newInstance();
        this.tmpMB = MessageBytes.newInstance();
        this.certificates = MessageBytes.newInstance();
        this.endOfStream = false;
        this.empty = true;
        this.first = true;
        this.waitingForBodyMessage = false;
        this.replay = false;
        this.swallowResponse = false;
        this.responseFinished = false;
        this.bytesWritten = 0L;
        this.ajpFlush = true;
        this.keepAliveTimeout = -1;
        this.tomcatAuthentication = true;
        this.tomcatAuthorization = false;
        this.secret = null;
        this.clientCertProvider = null;
        this.sendReasonPhrase = false;
        this.outputMaxChunkSize = 8184 + packetSize - 8192;
        this.request.setInputBuffer(new SocketInputBuffer());
        this.requestHeaderMessage = new AjpMessage(packetSize);
        this.responseMessage = new AjpMessage(packetSize);
        this.bodyMessage = new AjpMessage(packetSize);
        final AjpMessage getBodyMessage = new AjpMessage(16);
        getBodyMessage.reset();
        getBodyMessage.appendByte(6);
        getBodyMessage.appendInt(8186 + packetSize - 8192);
        getBodyMessage.end();
        this.getBodyMessageArray = new byte[getBodyMessage.getLen()];
        System.arraycopy(getBodyMessage.getBuffer(), 0, this.getBodyMessageArray, 0, getBodyMessage.getLen());
        this.response.setOutputBuffer(new SocketOutputBuffer());
    }
    
    public boolean getAjpFlush() {
        return this.ajpFlush;
    }
    
    public void setAjpFlush(final boolean ajpFlush) {
        this.ajpFlush = ajpFlush;
    }
    
    public int getKeepAliveTimeout() {
        return this.keepAliveTimeout;
    }
    
    public void setKeepAliveTimeout(final int timeout) {
        this.keepAliveTimeout = timeout;
    }
    
    public boolean getTomcatAuthentication() {
        return this.tomcatAuthentication;
    }
    
    public void setTomcatAuthentication(final boolean tomcatAuthentication) {
        this.tomcatAuthentication = tomcatAuthentication;
    }
    
    public boolean getTomcatAuthorization() {
        return this.tomcatAuthorization;
    }
    
    public void setTomcatAuthorization(final boolean tomcatAuthorization) {
        this.tomcatAuthorization = tomcatAuthorization;
    }
    
    @Deprecated
    public void setRequiredSecret(final String requiredSecret) {
        this.setSecret(requiredSecret);
    }
    
    public void setSecret(final String secret) {
        this.secret = secret;
    }
    
    public String getClientCertProvider() {
        return this.clientCertProvider;
    }
    
    public void setClientCertProvider(final String clientCertProvider) {
        this.clientCertProvider = clientCertProvider;
    }
    
    @Deprecated
    void setSendReasonPhrase(final boolean sendReasonPhrase) {
        this.sendReasonPhrase = sendReasonPhrase;
    }
    
    public void setAllowedRequestAttributesPattern(final Pattern allowedRequestAttributesPattern) {
        this.allowedRequestAttributesPattern = allowedRequestAttributesPattern;
    }
    
    @Override
    protected boolean flushBufferedWrite() throws IOException {
        if (this.hasDataToWrite()) {
            this.socketWrapper.flush(false);
            if (this.hasDataToWrite()) {
                this.response.checkRegisterForWrite();
                return true;
            }
        }
        return false;
    }
    
    @Override
    protected void dispatchNonBlockingRead() {
        if (this.available(true) > 0) {
            super.dispatchNonBlockingRead();
        }
    }
    
    @Override
    protected AbstractEndpoint.Handler.SocketState dispatchEndRequest() {
        if (this.keepAliveTimeout > 0) {
            this.socketWrapper.setReadTimeout(this.keepAliveTimeout);
        }
        this.recycle();
        if (this.endpoint.isPaused()) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        return AbstractEndpoint.Handler.SocketState.OPEN;
    }
    
    public AbstractEndpoint.Handler.SocketState service(final SocketWrapperBase<?> socket) throws IOException {
        final RequestInfo rp = this.request.getRequestProcessor();
        rp.setStage(1);
        this.socketWrapper = socket;
        final int soTimeout = this.endpoint.getConnectionTimeout();
        boolean cping = false;
        boolean firstRead = true;
        while (!this.getErrorState().isError() && !this.endpoint.isPaused()) {
            try {
                if (!this.readMessage(this.requestHeaderMessage, firstRead)) {
                    break;
                }
                firstRead = false;
                if (this.keepAliveTimeout > 0) {
                    this.socketWrapper.setReadTimeout(soTimeout);
                }
                final int type = this.requestHeaderMessage.getByte();
                if (type == 10) {
                    if (this.endpoint.isPaused()) {
                        this.recycle();
                        break;
                    }
                    cping = true;
                    try {
                        this.socketWrapper.write(true, AjpProcessor.pongMessageArray, 0, AjpProcessor.pongMessageArray.length);
                        this.socketWrapper.flush(true);
                    }
                    catch (final IOException e) {
                        if (this.getLog().isDebugEnabled()) {
                            this.getLog().debug((Object)"Pong message failed", (Throwable)e);
                        }
                        this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                    }
                    this.recycle();
                    continue;
                }
                else {
                    if (type != 2) {
                        if (this.getLog().isDebugEnabled()) {
                            this.getLog().debug((Object)("Unexpected message: " + type));
                        }
                        this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, null);
                        break;
                    }
                    this.request.setStartTime(System.currentTimeMillis());
                }
            }
            catch (final IOException e2) {
                this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e2);
                break;
            }
            catch (final Throwable t) {
                ExceptionUtils.handleThrowable(t);
                this.getLog().debug((Object)AjpProcessor.sm.getString("ajpprocessor.header.error"), t);
                this.response.setStatus(400);
                this.setErrorState(ErrorState.CLOSE_CLEAN, t);
            }
            if (this.getErrorState().isIoAllowed()) {
                rp.setStage(2);
                try {
                    this.prepareRequest();
                }
                catch (final Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    this.getLog().debug((Object)AjpProcessor.sm.getString("ajpprocessor.request.prepare"), t);
                    this.response.setStatus(500);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, t);
                }
            }
            if (this.getErrorState().isIoAllowed() && !cping && this.endpoint.isPaused()) {
                this.response.setStatus(503);
                this.setErrorState(ErrorState.CLOSE_CLEAN, null);
            }
            cping = false;
            if (this.getErrorState().isIoAllowed()) {
                try {
                    rp.setStage(3);
                    this.getAdapter().service(this.request, this.response);
                }
                catch (final InterruptedIOException e3) {
                    this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e3);
                }
                catch (final Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    this.getLog().error((Object)AjpProcessor.sm.getString("ajpprocessor.request.process"), t);
                    this.response.setStatus(500);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, t);
                    this.getAdapter().log(this.request, this.response, 0L);
                }
            }
            if (this.isAsync() && !this.getErrorState().isError()) {
                break;
            }
            if (!this.responseFinished && this.getErrorState().isIoAllowed()) {
                try {
                    this.action(ActionCode.COMMIT, null);
                    this.finishResponse();
                }
                catch (final IOException ioe) {
                    this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
                }
                catch (final Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    this.setErrorState(ErrorState.CLOSE_NOW, t);
                }
            }
            if (this.getErrorState().isError()) {
                this.response.setStatus(500);
            }
            this.request.updateCounters();
            rp.setStage(6);
            if (this.keepAliveTimeout > 0) {
                this.socketWrapper.setReadTimeout(this.keepAliveTimeout);
            }
            this.recycle();
        }
        rp.setStage(7);
        if (this.getErrorState().isError() || this.endpoint.isPaused()) {
            return AbstractEndpoint.Handler.SocketState.CLOSED;
        }
        if (this.isAsync()) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        return AbstractEndpoint.Handler.SocketState.OPEN;
    }
    
    @Override
    public void recycle() {
        this.getAdapter().checkRecycled(this.request, this.response);
        super.recycle();
        this.request.recycle();
        this.response.recycle();
        this.first = true;
        this.endOfStream = false;
        this.waitingForBodyMessage = false;
        this.empty = true;
        this.replay = false;
        this.responseFinished = false;
        this.certificates.recycle();
        this.swallowResponse = false;
        this.bytesWritten = 0L;
    }
    
    @Override
    public void pause() {
    }
    
    private boolean receive(final boolean block) throws IOException {
        this.bodyMessage.reset();
        if (!this.readMessage(this.bodyMessage, block)) {
            return false;
        }
        this.waitingForBodyMessage = false;
        if (this.bodyMessage.getLen() == 0) {
            return false;
        }
        final int blen = this.bodyMessage.peekInt();
        if (blen == 0) {
            return false;
        }
        this.bodyMessage.getBodyBytes(this.bodyBytes);
        this.empty = false;
        return true;
    }
    
    private boolean readMessage(final AjpMessage message, final boolean block) throws IOException {
        final byte[] buf = message.getBuffer();
        if (!this.read(buf, 0, 4, block)) {
            return false;
        }
        final int messageLength = message.processHeader(true);
        if (messageLength < 0) {
            throw new IOException(AjpProcessor.sm.getString("ajpmessage.invalidLength", new Object[] { messageLength }));
        }
        if (messageLength == 0) {
            return true;
        }
        if (messageLength > message.getBuffer().length) {
            final String msg = AjpProcessor.sm.getString("ajpprocessor.header.tooLong", new Object[] { messageLength, buf.length });
            AjpProcessor.log.error((Object)msg);
            throw new IllegalArgumentException(msg);
        }
        this.read(buf, 4, messageLength, true);
        return true;
    }
    
    protected boolean refillReadBuffer(final boolean block) throws IOException {
        if (this.replay) {
            this.endOfStream = true;
        }
        if (this.endOfStream) {
            return false;
        }
        if (this.first) {
            this.first = false;
            final long contentLength = this.request.getContentLengthLong();
            if (contentLength > 0L) {
                this.waitingForBodyMessage = true;
            }
            else if (contentLength == 0L) {
                this.endOfStream = true;
                return false;
            }
        }
        if (!this.waitingForBodyMessage) {
            this.socketWrapper.write(true, this.getBodyMessageArray, 0, this.getBodyMessageArray.length);
            this.socketWrapper.flush(true);
            this.waitingForBodyMessage = true;
        }
        final boolean moreData = this.receive(block);
        if (!moreData && !this.waitingForBodyMessage) {
            this.endOfStream = true;
        }
        return moreData;
    }
    
    private void prepareRequest() {
        final byte methodCode = this.requestHeaderMessage.getByte();
        if (methodCode != -1) {
            final String methodName = Constants.getMethodForCode(methodCode - 1);
            this.request.method().setString(methodName);
        }
        this.requestHeaderMessage.getBytes(this.request.protocol());
        this.requestHeaderMessage.getBytes(this.request.requestURI());
        this.requestHeaderMessage.getBytes(this.request.remoteAddr());
        this.requestHeaderMessage.getBytes(this.request.remoteHost());
        this.requestHeaderMessage.getBytes(this.request.localName());
        this.request.setLocalPort(this.requestHeaderMessage.getInt());
        if (this.socketWrapper != null) {
            this.request.peerAddr().setString(this.socketWrapper.getRemoteAddr());
        }
        final boolean isSSL = this.requestHeaderMessage.getByte() != 0;
        if (isSSL) {
            this.request.scheme().setString("https");
        }
        final MimeHeaders headers = this.request.getMimeHeaders();
        headers.setLimit(this.endpoint.getMaxHeaderCount());
        boolean contentLengthSet = false;
        for (int hCount = this.requestHeaderMessage.getInt(), i = 0; i < hCount; ++i) {
            String hName = null;
            int isc = this.requestHeaderMessage.peekInt();
            int hId = isc & 0xFF;
            MessageBytes vMB = null;
            isc &= 0xFF00;
            if (40960 == isc) {
                this.requestHeaderMessage.getInt();
                hName = Constants.getHeaderForCode(hId - 1);
                vMB = headers.addValue(hName);
            }
            else {
                hId = -1;
                this.requestHeaderMessage.getBytes(this.tmpMB);
                final ByteChunk bc = this.tmpMB.getByteChunk();
                vMB = headers.addValue(bc.getBuffer(), bc.getStart(), bc.getLength());
            }
            this.requestHeaderMessage.getBytes(vMB);
            if (hId == 8 || (hId == -1 && this.tmpMB.equalsIgnoreCase("Content-Length"))) {
                final long cl = vMB.getLong();
                if (contentLengthSet) {
                    this.response.setStatus(400);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                }
                else {
                    contentLengthSet = true;
                    this.request.setContentLength(cl);
                }
            }
            else if (hId == 7 || (hId == -1 && this.tmpMB.equalsIgnoreCase("Content-Type"))) {
                final ByteChunk bchunk = vMB.getByteChunk();
                this.request.contentType().setBytes(bchunk.getBytes(), bchunk.getOffset(), bchunk.getLength());
            }
        }
        boolean secretPresentInRequest = false;
        byte attributeCode;
        while ((attributeCode = this.requestHeaderMessage.getByte()) != -1) {
            switch (attributeCode) {
                case 10: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    final String n = this.tmpMB.toString();
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    final String v = this.tmpMB.toString();
                    if (n.equals("AJP_LOCAL_ADDR")) {
                        this.request.localAddr().setString(v);
                        continue;
                    }
                    if (n.equals("AJP_REMOTE_PORT")) {
                        try {
                            this.request.setRemotePort(Integer.parseInt(v));
                        }
                        catch (final NumberFormatException ex) {}
                        continue;
                    }
                    if (n.equals("AJP_SSL_PROTOCOL")) {
                        this.request.setAttribute("org.apache.tomcat.util.net.secure_protocol_version", v);
                        continue;
                    }
                    if (n.equals("JK_LB_ACTIVATION")) {
                        this.request.setAttribute(n, v);
                        continue;
                    }
                    if (AjpProcessor.javaxAttributes.contains(n)) {
                        this.request.setAttribute(n, v);
                        continue;
                    }
                    if (AjpProcessor.iisTlsAttributes.contains(n)) {
                        this.request.setAttribute(n, v);
                        continue;
                    }
                    if (this.allowedRequestAttributesPattern != null && this.allowedRequestAttributesPattern.matcher(n).matches()) {
                        this.request.setAttribute(n, v);
                        continue;
                    }
                    AjpProcessor.log.warn((Object)AjpProcessor.sm.getString("ajpprocessor.unknownAttribute", new Object[] { n }));
                    this.response.setStatus(403);
                    this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                    continue;
                }
                case 1: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    continue;
                }
                case 2: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    continue;
                }
                case 3: {
                    if (this.tomcatAuthorization || !this.tomcatAuthentication) {
                        this.requestHeaderMessage.getBytes(this.request.getRemoteUser());
                        this.request.setRemoteUserNeedsAuthorization(this.tomcatAuthorization);
                        continue;
                    }
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    continue;
                }
                case 4: {
                    if (this.tomcatAuthentication) {
                        this.requestHeaderMessage.getBytes(this.tmpMB);
                        continue;
                    }
                    this.requestHeaderMessage.getBytes(this.request.getAuthType());
                    continue;
                }
                case 5: {
                    this.requestHeaderMessage.getBytes(this.request.queryString());
                    continue;
                }
                case 6: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    continue;
                }
                case 7: {
                    this.requestHeaderMessage.getBytes(this.certificates);
                    continue;
                }
                case 8: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    this.request.setAttribute("javax.servlet.request.cipher_suite", this.tmpMB.toString());
                    continue;
                }
                case 9: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    this.request.setAttribute("javax.servlet.request.ssl_session_id", this.tmpMB.toString());
                    continue;
                }
                case 11: {
                    this.request.setAttribute("javax.servlet.request.key_size", this.requestHeaderMessage.getInt());
                    continue;
                }
                case 13: {
                    this.requestHeaderMessage.getBytes(this.request.method());
                    continue;
                }
                case 12: {
                    this.requestHeaderMessage.getBytes(this.tmpMB);
                    if (this.secret == null || this.secret.length() <= 0) {
                        continue;
                    }
                    secretPresentInRequest = true;
                    if (!this.tmpMB.equals(this.secret)) {
                        this.response.setStatus(403);
                        this.setErrorState(ErrorState.CLOSE_CLEAN, null);
                        continue;
                    }
                    continue;
                }
                default: {
                    continue;
                }
            }
        }
        if (this.secret != null && this.secret.length() > 0 && !secretPresentInRequest) {
            this.response.setStatus(403);
            this.setErrorState(ErrorState.CLOSE_CLEAN, null);
        }
        final ByteChunk uriBC = this.request.requestURI().getByteChunk();
        if (uriBC.startsWithIgnoreCase("http", 0)) {
            final int pos = uriBC.indexOf("://", 0, 3, 4);
            final int uriBCStart = uriBC.getStart();
            int slashPos = -1;
            if (pos != -1) {
                final byte[] uriB = uriBC.getBytes();
                slashPos = uriBC.indexOf('/', pos + 3);
                if (slashPos == -1) {
                    slashPos = uriBC.getLength();
                    this.request.requestURI().setBytes(uriB, uriBCStart + pos + 1, 1);
                }
                else {
                    this.request.requestURI().setBytes(uriB, uriBCStart + slashPos, uriBC.getLength() - slashPos);
                }
                final MessageBytes hostMB = headers.setValue("host");
                hostMB.setBytes(uriB, uriBCStart + pos + 3, slashPos - pos - 3);
            }
        }
        final MessageBytes valueMB = this.request.getMimeHeaders().getValue("host");
        this.parseHost(valueMB);
        if (!this.getErrorState().isIoAllowed()) {
            this.getAdapter().log(this.request, this.response, 0L);
        }
    }
    
    @Override
    protected void populateHost() {
        try {
            this.request.serverName().duplicate(this.request.localName());
        }
        catch (final IOException e) {
            this.response.setStatus(400);
            this.setErrorState(ErrorState.CLOSE_CLEAN, e);
        }
    }
    
    @Override
    protected void populatePort() {
        this.request.setServerPort(this.request.getLocalPort());
    }
    
    @Override
    protected final void prepareResponse() throws IOException {
        this.response.setCommitted(true);
        this.tmpMB.recycle();
        this.responseMsgPos = -1;
        this.responseMessage.reset();
        this.responseMessage.appendByte(4);
        final int statusCode = this.response.getStatus();
        if (statusCode < 200 || statusCode == 204 || statusCode == 205 || statusCode == 304) {
            this.swallowResponse = true;
        }
        final MessageBytes methodMB = this.request.method();
        if (methodMB.equals("HEAD")) {
            this.swallowResponse = true;
        }
        this.responseMessage.appendInt(statusCode);
        if (this.sendReasonPhrase) {
            String message = null;
            if (org.apache.coyote.Constants.USE_CUSTOM_STATUS_MSG_IN_HEADER && HttpMessages.isSafeInHttpHeader(this.response.getMessage())) {
                message = this.response.getMessage();
            }
            if (message == null) {
                message = HttpMessages.getInstance(this.response.getLocale()).getMessage(this.response.getStatus());
            }
            if (message == null) {
                message = Integer.toString(this.response.getStatus());
            }
            this.tmpMB.setString(message);
        }
        else {
            this.tmpMB.setString(Integer.toString(this.response.getStatus()));
        }
        this.responseMessage.appendBytes(this.tmpMB);
        final MimeHeaders headers = this.response.getMimeHeaders();
        final String contentType = this.response.getContentType();
        if (contentType != null) {
            headers.setValue("Content-Type").setString(contentType);
        }
        final String contentLanguage = this.response.getContentLanguage();
        if (contentLanguage != null) {
            headers.setValue("Content-Language").setString(contentLanguage);
        }
        final long contentLength = this.response.getContentLengthLong();
        if (contentLength >= 0L) {
            headers.setValue("Content-Length").setLong(contentLength);
        }
        final int numHeaders = headers.size();
        this.responseMessage.appendInt(numHeaders);
        for (int i = 0; i < numHeaders; ++i) {
            final MessageBytes hN = headers.getName(i);
            final int hC = Constants.getResponseAjpIndex(hN.toString());
            if (hC > 0) {
                this.responseMessage.appendInt(hC);
            }
            else {
                this.responseMessage.appendBytes(hN);
            }
            final MessageBytes hV = headers.getValue(i);
            this.responseMessage.appendBytes(hV);
        }
        this.responseMessage.end();
        this.socketWrapper.write(true, this.responseMessage.getBuffer(), 0, this.responseMessage.getLen());
        this.socketWrapper.flush(true);
    }
    
    @Override
    protected final void flush() throws IOException {
        if (!this.responseFinished) {
            if (this.ajpFlush) {
                this.socketWrapper.write(true, AjpProcessor.flushMessageArray, 0, AjpProcessor.flushMessageArray.length);
            }
            this.socketWrapper.flush(true);
        }
    }
    
    @Override
    protected final void finishResponse() throws IOException {
        if (this.responseFinished) {
            return;
        }
        this.responseFinished = true;
        if (this.waitingForBodyMessage || (this.first && this.request.getContentLengthLong() > 0L)) {
            this.refillReadBuffer(true);
        }
        if (this.getErrorState().isError()) {
            this.socketWrapper.write(true, AjpProcessor.endAndCloseMessageArray, 0, AjpProcessor.endAndCloseMessageArray.length);
        }
        else {
            this.socketWrapper.write(true, AjpProcessor.endMessageArray, 0, AjpProcessor.endMessageArray.length);
        }
        this.socketWrapper.flush(true);
    }
    
    @Override
    protected final void ack(final ContinueResponseTiming continueResponseTiming) {
    }
    
    @Override
    protected final int available(final boolean doRead) {
        if (this.endOfStream) {
            return 0;
        }
        if (this.empty && doRead) {
            try {
                this.refillReadBuffer(false);
            }
            catch (final IOException timeout) {
                return 1;
            }
        }
        if (this.empty) {
            return 0;
        }
        return this.request.getInputBuffer().available();
    }
    
    @Override
    protected final void setRequestBody(final ByteChunk body) {
        final int length = body.getLength();
        this.bodyBytes.setBytes(body.getBytes(), body.getStart(), length);
        this.request.setContentLength(length);
        this.first = false;
        this.empty = false;
        this.replay = true;
        this.endOfStream = false;
    }
    
    @Override
    protected final void setSwallowResponse() {
        this.swallowResponse = true;
    }
    
    @Override
    protected final void disableSwallowRequest() {
    }
    
    @Override
    protected final boolean getPopulateRequestAttributesFromSocket() {
        return false;
    }
    
    @Override
    protected final void populateRequestAttributeRemoteHost() {
        if (this.request.remoteHost().isNull()) {
            try {
                this.request.remoteHost().setString(InetAddress.getByName(this.request.remoteAddr().toString()).getHostName());
            }
            catch (final IOException ex) {}
        }
    }
    
    @Override
    protected final void populateSslRequestAttributes() {
        if (!this.certificates.isNull()) {
            final ByteChunk certData = this.certificates.getByteChunk();
            X509Certificate[] jsseCerts = null;
            final ByteArrayInputStream bais = new ByteArrayInputStream(certData.getBytes(), certData.getStart(), certData.getLength());
            try {
                final String clientCertProvider = this.getClientCertProvider();
                CertificateFactory cf;
                if (clientCertProvider == null) {
                    cf = CertificateFactory.getInstance("X.509");
                }
                else {
                    cf = CertificateFactory.getInstance("X.509", clientCertProvider);
                }
                while (bais.available() > 0) {
                    final X509Certificate cert = (X509Certificate)cf.generateCertificate(bais);
                    if (jsseCerts == null) {
                        jsseCerts = new X509Certificate[] { cert };
                    }
                    else {
                        final X509Certificate[] temp = new X509Certificate[jsseCerts.length + 1];
                        System.arraycopy(jsseCerts, 0, temp, 0, jsseCerts.length);
                        temp[jsseCerts.length] = cert;
                        jsseCerts = temp;
                    }
                }
            }
            catch (final CertificateException | NoSuchProviderException e) {
                this.getLog().error((Object)AjpProcessor.sm.getString("ajpprocessor.certs.fail"), (Throwable)e);
                return;
            }
            this.request.setAttribute("javax.servlet.request.X509Certificate", jsseCerts);
        }
    }
    
    @Override
    protected final boolean isRequestBodyFullyRead() {
        return this.endOfStream;
    }
    
    @Override
    protected final void registerReadInterest() {
        this.socketWrapper.registerReadInterest();
    }
    
    @Override
    protected final boolean isReadyForWrite() {
        return this.responseMsgPos == -1 && this.socketWrapper.isReadyForWrite();
    }
    
    private boolean read(final byte[] buf, final int pos, final int n, final boolean block) throws IOException {
        int read = this.socketWrapper.read(block, buf, pos, n);
        if (read > 0 && read < n) {
            for (int left = n - read, start = pos + read; left > 0; left -= read, start += read) {
                read = this.socketWrapper.read(true, buf, start, left);
                if (read == -1) {
                    throw new EOFException();
                }
            }
        }
        else if (read == -1) {
            throw new EOFException();
        }
        return read > 0;
    }
    
    @Deprecated
    private void writeData(final ByteChunk chunk) throws IOException {
        final boolean blocking = this.response.getWriteListener() == null;
        int len;
        int off;
        int thisTime;
        for (len = chunk.getLength(), off = 0; len > 0; len -= thisTime, off += thisTime) {
            thisTime = Math.min(len, this.outputMaxChunkSize);
            this.responseMessage.reset();
            this.responseMessage.appendByte(3);
            this.responseMessage.appendBytes(chunk.getBytes(), chunk.getOffset() + off, thisTime);
            this.responseMessage.end();
            this.socketWrapper.write(blocking, this.responseMessage.getBuffer(), 0, this.responseMessage.getLen());
            this.socketWrapper.flush(blocking);
        }
        this.bytesWritten += off;
    }
    
    private void writeData(final ByteBuffer chunk) throws IOException {
        final boolean blocking = this.response.getWriteListener() == null;
        int len;
        int off;
        int thisTime;
        for (len = chunk.remaining(), off = 0; len > 0; len -= thisTime, off += thisTime) {
            thisTime = Math.min(len, this.outputMaxChunkSize);
            this.responseMessage.reset();
            this.responseMessage.appendByte(3);
            chunk.limit(chunk.position() + thisTime);
            this.responseMessage.appendBytes(chunk);
            this.responseMessage.end();
            this.socketWrapper.write(blocking, this.responseMessage.getBuffer(), 0, this.responseMessage.getLen());
            this.socketWrapper.flush(blocking);
        }
        this.bytesWritten += off;
    }
    
    private boolean hasDataToWrite() {
        return this.responseMsgPos != -1 || this.socketWrapper.hasDataToWrite();
    }
    
    @Override
    protected Log getLog() {
        return AjpProcessor.log;
    }
    
    static {
        log = LogFactory.getLog((Class)AjpProcessor.class);
        sm = StringManager.getManager((Class)AjpProcessor.class);
        final AjpMessage endMessage = new AjpMessage(16);
        endMessage.reset();
        endMessage.appendByte(5);
        endMessage.appendByte(1);
        endMessage.end();
        endMessageArray = new byte[endMessage.getLen()];
        System.arraycopy(endMessage.getBuffer(), 0, AjpProcessor.endMessageArray, 0, endMessage.getLen());
        final AjpMessage endAndCloseMessage = new AjpMessage(16);
        endAndCloseMessage.reset();
        endAndCloseMessage.appendByte(5);
        endAndCloseMessage.appendByte(0);
        endAndCloseMessage.end();
        endAndCloseMessageArray = new byte[endAndCloseMessage.getLen()];
        System.arraycopy(endAndCloseMessage.getBuffer(), 0, AjpProcessor.endAndCloseMessageArray, 0, endAndCloseMessage.getLen());
        final AjpMessage flushMessage = new AjpMessage(16);
        flushMessage.reset();
        flushMessage.appendByte(3);
        flushMessage.appendInt(0);
        flushMessage.appendByte(0);
        flushMessage.end();
        flushMessageArray = new byte[flushMessage.getLen()];
        System.arraycopy(flushMessage.getBuffer(), 0, AjpProcessor.flushMessageArray, 0, flushMessage.getLen());
        final AjpMessage pongMessage = new AjpMessage(16);
        pongMessage.reset();
        pongMessage.appendByte(9);
        pongMessage.end();
        pongMessageArray = new byte[pongMessage.getLen()];
        System.arraycopy(pongMessage.getBuffer(), 0, AjpProcessor.pongMessageArray, 0, pongMessage.getLen());
        final Set<String> s = new HashSet<String>();
        s.add("javax.servlet.request.cipher_suite");
        s.add("javax.servlet.request.key_size");
        s.add("javax.servlet.request.ssl_session");
        s.add("javax.servlet.request.X509Certificate");
        javaxAttributes = Collections.unmodifiableSet((Set<? extends String>)s);
        final Set<String> iis = new HashSet<String>();
        iis.add("CERT_ISSUER");
        iis.add("CERT_SUBJECT");
        iis.add("CERT_COOKIE");
        iis.add("HTTPS_SERVER_SUBJECT");
        iis.add("CERT_FLAGS");
        iis.add("HTTPS_SECRETKEYSIZE");
        iis.add("CERT_SERIALNUMBER");
        iis.add("HTTPS_SERVER_ISSUER");
        iis.add("HTTPS_KEYSIZE");
        iisTlsAttributes = Collections.unmodifiableSet((Set<? extends String>)iis);
    }
    
    protected class SocketInputBuffer implements InputBuffer
    {
        @Deprecated
        @Override
        public int doRead(final ByteChunk chunk) throws IOException {
            if (AjpProcessor.this.endOfStream) {
                return -1;
            }
            if (AjpProcessor.this.empty && !AjpProcessor.this.refillReadBuffer(true)) {
                return -1;
            }
            final ByteChunk bc = AjpProcessor.this.bodyBytes.getByteChunk();
            chunk.setBytes(bc.getBuffer(), bc.getStart(), bc.getLength());
            AjpProcessor.this.empty = true;
            return chunk.getLength();
        }
        
        @Override
        public int doRead(final ApplicationBufferHandler handler) throws IOException {
            if (AjpProcessor.this.endOfStream) {
                return -1;
            }
            if (AjpProcessor.this.empty && !AjpProcessor.this.refillReadBuffer(true)) {
                return -1;
            }
            final ByteChunk bc = AjpProcessor.this.bodyBytes.getByteChunk();
            handler.setByteBuffer(ByteBuffer.wrap(bc.getBuffer(), bc.getStart(), bc.getLength()));
            AjpProcessor.this.empty = true;
            return handler.getByteBuffer().remaining();
        }
        
        @Override
        public int available() {
            if (AjpProcessor.this.empty) {
                return 0;
            }
            return AjpProcessor.this.bodyBytes.getByteChunk().getLength();
        }
    }
    
    protected class SocketOutputBuffer implements OutputBuffer
    {
        @Deprecated
        @Override
        public int doWrite(final ByteChunk chunk) throws IOException {
            if (!AjpProcessor.this.response.isCommitted()) {
                try {
                    AjpProcessor.this.prepareResponse();
                }
                catch (final IOException e) {
                    AbstractProcessor.this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                }
            }
            if (!AjpProcessor.this.swallowResponse) {
                AjpProcessor.this.writeData(chunk);
            }
            return chunk.getLength();
        }
        
        @Override
        public int doWrite(final ByteBuffer chunk) throws IOException {
            if (!AjpProcessor.this.response.isCommitted()) {
                try {
                    AjpProcessor.this.prepareResponse();
                }
                catch (final IOException e) {
                    AbstractProcessor.this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, e);
                }
            }
            int len = 0;
            if (!AjpProcessor.this.swallowResponse) {
                try {
                    len = chunk.remaining();
                    AjpProcessor.this.writeData(chunk);
                    len -= chunk.remaining();
                }
                catch (final IOException ioe) {
                    AbstractProcessor.this.setErrorState(ErrorState.CLOSE_CONNECTION_NOW, ioe);
                    throw ioe;
                }
            }
            return len;
        }
        
        @Override
        public long getBytesWritten() {
            return AjpProcessor.this.bytesWritten;
        }
    }
}
