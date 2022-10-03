package org.glassfish.jersey.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.glassfish.jersey.client.internal.HttpUrlConnector;
import java.util.Map;
import org.glassfish.jersey.client.spi.Connector;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.client.Client;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import java.util.logging.Logger;
import org.glassfish.jersey.client.spi.ConnectorProvider;

public class HttpUrlConnectorProvider implements ConnectorProvider
{
    public static final String USE_FIXED_LENGTH_STREAMING = "jersey.config.client.httpUrlConnector.useFixedLengthStreaming";
    public static final String SET_METHOD_WORKAROUND = "jersey.config.client.httpUrlConnection.setMethodWorkaround";
    private static final ConnectionFactory DEFAULT_CONNECTION_FACTORY;
    private static final Logger LOGGER;
    private ConnectionFactory connectionFactory;
    private int chunkSize;
    private boolean useFixedLengthStreaming;
    private boolean useSetMethodWorkaround;
    
    public HttpUrlConnectorProvider() {
        this.connectionFactory = HttpUrlConnectorProvider.DEFAULT_CONNECTION_FACTORY;
        this.chunkSize = 4096;
        this.useFixedLengthStreaming = false;
        this.useSetMethodWorkaround = false;
    }
    
    public HttpUrlConnectorProvider connectionFactory(final ConnectionFactory connectionFactory) {
        if (connectionFactory == null) {
            throw new NullPointerException(LocalizationMessages.NULL_INPUT_PARAMETER("connectionFactory"));
        }
        this.connectionFactory = connectionFactory;
        return this;
    }
    
    public HttpUrlConnectorProvider chunkSize(final int chunkSize) {
        if (chunkSize < 0) {
            throw new IllegalArgumentException(LocalizationMessages.NEGATIVE_INPUT_PARAMETER("chunkSize"));
        }
        this.chunkSize = chunkSize;
        return this;
    }
    
    public HttpUrlConnectorProvider useFixedLengthStreaming() {
        this.useFixedLengthStreaming = true;
        return this;
    }
    
    public HttpUrlConnectorProvider useSetMethodWorkaround() {
        this.useSetMethodWorkaround = true;
        return this;
    }
    
    @Override
    public Connector getConnector(final Client client, final Configuration config) {
        final Map<String, Object> properties = config.getProperties();
        int computedChunkSize = ClientProperties.getValue(properties, "jersey.config.client.chunkedEncodingSize", this.chunkSize, Integer.class);
        if (computedChunkSize < 0) {
            HttpUrlConnectorProvider.LOGGER.warning(LocalizationMessages.NEGATIVE_CHUNK_SIZE(computedChunkSize, this.chunkSize));
            computedChunkSize = this.chunkSize;
        }
        final boolean computedUseFixedLengthStreaming = ClientProperties.getValue(properties, "jersey.config.client.httpUrlConnector.useFixedLengthStreaming", this.useFixedLengthStreaming, Boolean.class);
        final boolean computedUseSetMethodWorkaround = ClientProperties.getValue(properties, "jersey.config.client.httpUrlConnection.setMethodWorkaround", this.useSetMethodWorkaround, Boolean.class);
        return this.createHttpUrlConnector(client, this.connectionFactory, computedChunkSize, computedUseFixedLengthStreaming, computedUseSetMethodWorkaround);
    }
    
    protected Connector createHttpUrlConnector(final Client client, final ConnectionFactory connectionFactory, final int chunkSize, final boolean fixLengthStreaming, final boolean setMethodWorkaround) {
        return new HttpUrlConnector(client, connectionFactory, chunkSize, fixLengthStreaming, setMethodWorkaround);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final HttpUrlConnectorProvider that = (HttpUrlConnectorProvider)o;
        return this.chunkSize == that.chunkSize && this.useFixedLengthStreaming == that.useFixedLengthStreaming && this.connectionFactory.equals(that.connectionFactory);
    }
    
    @Override
    public int hashCode() {
        int result = this.connectionFactory.hashCode();
        result = 31 * result + this.chunkSize;
        result = 31 * result + (this.useFixedLengthStreaming ? 1 : 0);
        return result;
    }
    
    static {
        DEFAULT_CONNECTION_FACTORY = new DefaultConnectionFactory();
        LOGGER = Logger.getLogger(HttpUrlConnectorProvider.class.getName());
    }
    
    private static class DefaultConnectionFactory implements ConnectionFactory
    {
        @Override
        public HttpURLConnection getConnection(final URL url) throws IOException {
            return (HttpURLConnection)url.openConnection();
        }
    }
    
    public interface ConnectionFactory
    {
        HttpURLConnection getConnection(final URL p0) throws IOException;
    }
}
