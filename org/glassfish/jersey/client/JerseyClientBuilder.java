package org.glassfish.jersey.client;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.client.Client;
import java.util.Map;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.internal.util.collection.UnsafeValue;
import org.glassfish.jersey.client.spi.DefaultSslContextProvider;
import java.util.concurrent.TimeUnit;
import java.security.KeyStore;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import javax.ws.rs.core.Configuration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import javax.net.ssl.SSLContext;
import org.glassfish.jersey.SslConfigurator;
import javax.net.ssl.HostnameVerifier;
import javax.ws.rs.client.ClientBuilder;

public class JerseyClientBuilder extends ClientBuilder
{
    private final ClientConfig config;
    private HostnameVerifier hostnameVerifier;
    private SslConfigurator sslConfigurator;
    private SSLContext sslContext;
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;
    
    public static JerseyClient createClient() {
        return new JerseyClientBuilder().build();
    }
    
    public static JerseyClient createClient(final Configuration configuration) {
        return new JerseyClientBuilder().withConfig(configuration).build();
    }
    
    public JerseyClientBuilder() {
        this.config = new ClientConfig();
    }
    
    public JerseyClientBuilder sslContext(final SSLContext sslContext) {
        if (sslContext == null) {
            throw new NullPointerException(LocalizationMessages.NULL_SSL_CONTEXT());
        }
        this.sslContext = sslContext;
        this.sslConfigurator = null;
        return this;
    }
    
    public JerseyClientBuilder keyStore(final KeyStore keyStore, final char[] password) {
        if (keyStore == null) {
            throw new NullPointerException(LocalizationMessages.NULL_KEYSTORE());
        }
        if (password == null) {
            throw new NullPointerException(LocalizationMessages.NULL_KEYSTORE_PASWORD());
        }
        if (this.sslConfigurator == null) {
            this.sslConfigurator = SslConfigurator.newInstance();
        }
        this.sslConfigurator.keyStore(keyStore);
        this.sslConfigurator.keyPassword(password);
        this.sslContext = null;
        return this;
    }
    
    public JerseyClientBuilder trustStore(final KeyStore trustStore) {
        if (trustStore == null) {
            throw new NullPointerException(LocalizationMessages.NULL_TRUSTSTORE());
        }
        if (this.sslConfigurator == null) {
            this.sslConfigurator = SslConfigurator.newInstance();
        }
        this.sslConfigurator.trustStore(trustStore);
        this.sslContext = null;
        return this;
    }
    
    public JerseyClientBuilder hostnameVerifier(final HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }
    
    public ClientBuilder executorService(final ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }
    
    public ClientBuilder scheduledExecutorService(final ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        return this;
    }
    
    public ClientBuilder connectTimeout(final long timeout, final TimeUnit unit) {
        if (timeout < 0L) {
            throw new IllegalArgumentException("Negative timeout.");
        }
        this.property("jersey.config.client.connectTimeout", Math.toIntExact(unit.toMillis(timeout)));
        return this;
    }
    
    public ClientBuilder readTimeout(final long timeout, final TimeUnit unit) {
        if (timeout < 0L) {
            throw new IllegalArgumentException("Negative timeout.");
        }
        this.property("jersey.config.client.readTimeout", Math.toIntExact(unit.toMillis(timeout)));
        return this;
    }
    
    public JerseyClient build() {
        if (this.sslContext != null) {
            return new JerseyClient((Configuration)this.config, this.sslContext, this.hostnameVerifier, null, this.executorService, this.scheduledExecutorService);
        }
        if (this.sslConfigurator != null) {
            final SslConfigurator sslConfiguratorCopy = this.sslConfigurator.copy();
            return new JerseyClient((Configuration)this.config, (UnsafeValue<SSLContext, IllegalStateException>)Values.lazy((UnsafeValue)new UnsafeValue<SSLContext, IllegalStateException>() {
                public SSLContext get() {
                    return sslConfiguratorCopy.createSSLContext();
                }
            }), this.hostnameVerifier, this.executorService, this.scheduledExecutorService);
        }
        return new JerseyClient((Configuration)this.config, null, this.hostnameVerifier, this.executorService, this.scheduledExecutorService);
    }
    
    public ClientConfig getConfiguration() {
        return this.config;
    }
    
    public JerseyClientBuilder property(final String name, final Object value) {
        this.config.property(name, value);
        return this;
    }
    
    public JerseyClientBuilder register(final Class<?> componentClass) {
        this.config.register(componentClass);
        return this;
    }
    
    public JerseyClientBuilder register(final Class<?> componentClass, final int priority) {
        this.config.register(componentClass, priority);
        return this;
    }
    
    public JerseyClientBuilder register(final Class<?> componentClass, final Class<?>... contracts) {
        this.config.register(componentClass, contracts);
        return this;
    }
    
    public JerseyClientBuilder register(final Class<?> componentClass, final Map<Class<?>, Integer> contracts) {
        this.config.register(componentClass, contracts);
        return this;
    }
    
    public JerseyClientBuilder register(final Object component) {
        this.config.register(component);
        return this;
    }
    
    public JerseyClientBuilder register(final Object component, final int priority) {
        this.config.register(component, priority);
        return this;
    }
    
    public JerseyClientBuilder register(final Object component, final Class<?>... contracts) {
        this.config.register(component, contracts);
        return this;
    }
    
    public JerseyClientBuilder register(final Object component, final Map<Class<?>, Integer> contracts) {
        this.config.register(component, contracts);
        return this;
    }
    
    public JerseyClientBuilder withConfig(final Configuration config) {
        this.config.loadFrom(config);
        return this;
    }
}
