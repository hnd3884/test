package org.jscep.transport;

import org.slf4j.LoggerFactory;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import java.io.BufferedOutputStream;
import java.io.UnsupportedEncodingException;
import org.bouncycastle.util.encoders.Base64;
import org.apache.commons.io.Charsets;
import java.io.IOException;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import org.jscep.transport.request.PkiOperationRequest;
import org.jscep.transport.response.ScepResponseHandler;
import org.jscep.transport.request.Request;
import java.net.URL;
import javax.net.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
final class UrlConnectionPostTransport extends AbstractTransport
{
    private static final Logger LOGGER;
    private SSLSocketFactory sslSocketFactory;
    
    public UrlConnectionPostTransport(final URL url) {
        super(url);
    }
    
    public UrlConnectionPostTransport(final URL url, final SSLSocketFactory sslSocketFactory) {
        super(url);
        this.sslSocketFactory = sslSocketFactory;
    }
    
    @Override
    public <T> T sendRequest(final Request msg, final ScepResponseHandler<T> handler) throws TransportException {
        if (!PkiOperationRequest.class.isAssignableFrom(msg.getClass())) {
            throw new IllegalArgumentException("POST transport may not be used for " + msg.getOperation() + " messages.");
        }
        final URL url = this.getUrl(msg.getOperation());
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            if (conn instanceof HttpsURLConnection && this.sslSocketFactory != null) {
                ((HttpsURLConnection)conn).setSSLSocketFactory(this.sslSocketFactory);
            }
        }
        catch (final IOException e) {
            throw new TransportException(e);
        }
        conn.setDoOutput(true);
        byte[] message;
        try {
            message = Base64.decode(msg.getMessage().getBytes(Charsets.US_ASCII.name()));
        }
        catch (final UnsupportedEncodingException e2) {
            throw new RuntimeException(e2);
        }
        OutputStream stream = null;
        try {
            stream = new BufferedOutputStream(conn.getOutputStream());
            stream.write(message);
        }
        catch (final IOException e3) {
            throw new TransportException(e3);
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (final IOException e4) {
                    UrlConnectionPostTransport.LOGGER.error("Failed to close output stream", (Throwable)e4);
                }
            }
        }
        try {
            final int responseCode = conn.getResponseCode();
            final String responseMessage = conn.getResponseMessage();
            UrlConnectionPostTransport.LOGGER.debug("Received '{} {}' when sending {} to {}", this.varargs(responseCode, responseMessage, msg, url));
            if (responseCode != 200) {
                throw new TransportException(responseCode + " " + responseMessage);
            }
        }
        catch (final IOException e3) {
            throw new TransportException("Error connecting to server.", e3);
        }
        byte[] response;
        try {
            response = IOUtils.toByteArray(conn.getInputStream());
        }
        catch (final IOException e5) {
            throw new TransportException("Error reading response stream", e5);
        }
        return handler.getResponse(response, conn.getContentType());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)UrlConnectionPostTransport.class);
    }
}
