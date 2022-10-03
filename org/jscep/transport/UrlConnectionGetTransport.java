package org.jscep.transport;

import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import org.jscep.transport.request.Operation;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import org.jscep.transport.response.ScepResponseHandler;
import org.jscep.transport.request.Request;
import java.net.URL;
import javax.net.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
final class UrlConnectionGetTransport extends AbstractTransport
{
    private static final Logger LOGGER;
    private SSLSocketFactory sslSocketFactory;
    
    public UrlConnectionGetTransport(final URL url) {
        super(url);
    }
    
    public UrlConnectionGetTransport(final URL url, final SSLSocketFactory sslSocketFactory) {
        super(url);
        this.sslSocketFactory = sslSocketFactory;
    }
    
    @Override
    public <T> T sendRequest(final Request msg, final ScepResponseHandler<T> handler) throws TransportException {
        final URL url = this.getUrl(msg.getOperation(), msg.getMessage());
        if (UrlConnectionGetTransport.LOGGER.isDebugEnabled()) {
            UrlConnectionGetTransport.LOGGER.debug("Sending {} to {}", (Object)msg, (Object)url);
        }
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection)url.openConnection();
            if (conn instanceof HttpsURLConnection && this.sslSocketFactory != null) {
                ((HttpsURLConnection)conn).setSSLSocketFactory(this.sslSocketFactory);
            }
        }
        catch (final IOException e) {
            throw new TransportException(e);
        }
        try {
            final int responseCode = conn.getResponseCode();
            final String responseMessage = conn.getResponseMessage();
            UrlConnectionGetTransport.LOGGER.debug("Received '{} {}' when sending {} to {}", this.varargs(responseCode, responseMessage, msg, url));
            if (responseCode != 200) {
                throw new TransportException(responseCode + " " + responseMessage);
            }
        }
        catch (final IOException e) {
            throw new TransportException("Error connecting to server", e);
        }
        byte[] response;
        try {
            response = IOUtils.toByteArray(conn.getInputStream());
        }
        catch (final IOException e2) {
            throw new TransportException("Error reading response stream", e2);
        }
        return handler.getResponse(response, conn.getContentType());
    }
    
    private URL getUrl(final Operation op, final String message) throws TransportException {
        try {
            return new URL(this.getUrl(op).toExternalForm() + "&message=" + URLEncoder.encode(message, "UTF-8"));
        }
        catch (final MalformedURLException e) {
            throw new TransportException(e);
        }
        catch (final UnsupportedEncodingException e2) {
            throw new TransportException(e2);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)UrlConnectionGetTransport.class);
    }
}
