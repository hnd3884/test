package com.google.api.client.googleapis.auth.oauth2;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.net.SocketTimeoutException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import java.util.Iterator;
import java.util.Collection;
import com.google.api.client.http.HttpHeaders;
import java.util.logging.Logger;
import java.nio.charset.Charset;
import com.google.api.client.util.Beta;

@Beta
public class OAuth2Utils
{
    static final Charset UTF_8;
    private static final Logger LOGGER;
    private static final String DEFAULT_METADATA_SERVER_URL = "http://169.254.169.254";
    private static final int MAX_COMPUTE_PING_TRIES = 3;
    private static final int COMPUTE_PING_CONNECTION_TIMEOUT_MS = 500;
    
    static <T extends Throwable> T exceptionWithCause(final T exception, final Throwable cause) {
        exception.initCause(cause);
        return exception;
    }
    
    static boolean headersContainValue(final HttpHeaders headers, final String headerName, final String value) {
        final Object values = headers.get((Object)headerName);
        if (values instanceof Collection) {
            final Collection<Object> valuesList = (Collection<Object>)values;
            for (final Object headerValue : valuesList) {
                if (headerValue instanceof String && ((String)headerValue).equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    static boolean runningOnComputeEngine(final HttpTransport transport, final SystemEnvironmentProvider environment) {
        if (Boolean.parseBoolean(environment.getEnv("NO_GCE_CHECK"))) {
            return false;
        }
        final GenericUrl tokenUrl = new GenericUrl(getMetadataServerUrl(environment));
        for (int i = 1; i <= 3; ++i) {
            try {
                final HttpRequest request = transport.createRequestFactory().buildGetRequest(tokenUrl);
                request.setConnectTimeout(500);
                request.getHeaders().set("Metadata-Flavor", (Object)"Google");
                final HttpResponse response = request.execute();
                try {
                    final HttpHeaders headers = response.getHeaders();
                    return headersContainValue(headers, "Metadata-Flavor", "Google");
                }
                finally {
                    response.disconnect();
                }
            }
            catch (final SocketTimeoutException ex) {}
            catch (final IOException e) {
                OAuth2Utils.LOGGER.log(Level.WARNING, "Failed to detect whether we are running on Google Compute Engine.", e);
            }
        }
        return false;
    }
    
    public static String getMetadataServerUrl() {
        return getMetadataServerUrl(SystemEnvironmentProvider.INSTANCE);
    }
    
    static String getMetadataServerUrl(final SystemEnvironmentProvider environment) {
        final String metadataServerAddress = environment.getEnv("GCE_METADATA_HOST");
        if (metadataServerAddress != null) {
            return "http://" + metadataServerAddress;
        }
        return "http://169.254.169.254";
    }
    
    static {
        UTF_8 = Charset.forName("UTF-8");
        LOGGER = Logger.getLogger(OAuth2Utils.class.getName());
    }
}
