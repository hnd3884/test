package org.glassfish.jersey.client;

import org.glassfish.jersey.SslConfigurator;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation;
import java.util.Map;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import org.glassfish.jersey.internal.guava.Preconditions;
import java.util.concurrent.Executors;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import java.util.logging.Level;
import java.lang.ref.Reference;
import java.util.Iterator;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.internal.util.collection.Values;
import javax.ws.rs.core.Configuration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.LinkedBlockingDeque;
import javax.net.ssl.SSLContext;
import org.glassfish.jersey.internal.util.collection.UnsafeValue;
import javax.net.ssl.HostnameVerifier;
import java.util.concurrent.atomic.AtomicBoolean;
import org.glassfish.jersey.client.spi.DefaultSslContextProvider;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;

public class JerseyClient implements Client, Initializable<JerseyClient>
{
    private static final Logger LOG;
    private static final DefaultSslContextProvider DEFAULT_SSL_CONTEXT_PROVIDER;
    private final AtomicBoolean closedFlag;
    private final boolean isDefaultSslContext;
    private final ClientConfig config;
    private final HostnameVerifier hostnameVerifier;
    private final UnsafeValue<SSLContext, IllegalStateException> sslContext;
    private final LinkedBlockingDeque<WeakReference<ShutdownHook>> shutdownHooks;
    private final ReferenceQueue<ShutdownHook> shReferenceQueue;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    
    protected JerseyClient() {
        this(null, (UnsafeValue<SSLContext, IllegalStateException>)null, null, null);
    }
    
    protected JerseyClient(final Configuration config, final SSLContext sslContext, final HostnameVerifier verifier) {
        this(config, sslContext, verifier, null);
    }
    
    protected JerseyClient(final Configuration config, final SSLContext sslContext, final HostnameVerifier verifier, final DefaultSslContextProvider defaultSslContextProvider) {
        this(config, (UnsafeValue<SSLContext, IllegalStateException>)((sslContext == null) ? null : Values.unsafe((Object)sslContext)), verifier, defaultSslContextProvider);
    }
    
    protected JerseyClient(final Configuration config, final UnsafeValue<SSLContext, IllegalStateException> sslContextProvider, final HostnameVerifier verifier) {
        this(config, sslContextProvider, verifier, null);
    }
    
    protected JerseyClient(final Configuration config, final SSLContext sslContext, final HostnameVerifier verifier, final DefaultSslContextProvider defaultSslContextProvider, final ExecutorService executorService, final ScheduledExecutorService scheduledExecutorService) {
        this(config, (UnsafeValue<SSLContext, IllegalStateException>)((sslContext == null) ? null : Values.unsafe((Object)sslContext)), verifier, defaultSslContextProvider, executorService, scheduledExecutorService);
    }
    
    protected JerseyClient(final Configuration config, final UnsafeValue<SSLContext, IllegalStateException> sslContextProvider, final HostnameVerifier verifier, final ExecutorService executorService, final ScheduledExecutorService scheduledExecutorService) {
        this(config, sslContextProvider, verifier, null, executorService, scheduledExecutorService);
    }
    
    protected JerseyClient(final Configuration config, final UnsafeValue<SSLContext, IllegalStateException> sslContextProvider, final HostnameVerifier verifier, final DefaultSslContextProvider defaultSslContextProvider) {
        this(config, sslContextProvider, verifier, defaultSslContextProvider, null, null);
    }
    
    protected JerseyClient(final Configuration config, final UnsafeValue<SSLContext, IllegalStateException> sslContextProvider, final HostnameVerifier verifier, final DefaultSslContextProvider defaultSslContextProvider, final ExecutorService executorService, final ScheduledExecutorService scheduledExecutorService) {
        this.closedFlag = new AtomicBoolean(false);
        this.shutdownHooks = new LinkedBlockingDeque<WeakReference<ShutdownHook>>();
        this.shReferenceQueue = new ReferenceQueue<ShutdownHook>();
        this.config = ((config == null) ? new ClientConfig(this) : new ClientConfig(this, config));
        if (sslContextProvider == null) {
            this.isDefaultSslContext = true;
            if (defaultSslContextProvider != null) {
                this.sslContext = this.createLazySslContext(defaultSslContextProvider);
            }
            else {
                final Iterator<DefaultSslContextProvider> iterator = ServiceFinder.find((Class)DefaultSslContextProvider.class).iterator();
                DefaultSslContextProvider lookedUpSslContextProvider;
                if (iterator.hasNext()) {
                    lookedUpSslContextProvider = iterator.next();
                }
                else {
                    lookedUpSslContextProvider = JerseyClient.DEFAULT_SSL_CONTEXT_PROVIDER;
                }
                this.sslContext = this.createLazySslContext(lookedUpSslContextProvider);
            }
        }
        else {
            this.isDefaultSslContext = false;
            this.sslContext = (UnsafeValue<SSLContext, IllegalStateException>)Values.lazy((UnsafeValue)sslContextProvider);
        }
        this.hostnameVerifier = verifier;
        this.executorService = executorService;
        this.scheduledExecutorService = scheduledExecutorService;
    }
    
    public void close() {
        if (this.closedFlag.compareAndSet(false, true)) {
            this.release();
        }
    }
    
    private void release() {
        Reference<ShutdownHook> listenerRef;
        while ((listenerRef = this.shutdownHooks.pollFirst()) != null) {
            final ShutdownHook listener = listenerRef.get();
            if (listener != null) {
                try {
                    listener.onShutdown();
                }
                catch (final Throwable t) {
                    JerseyClient.LOG.log(Level.WARNING, LocalizationMessages.ERROR_SHUTDOWNHOOK_CLOSE(listenerRef.getClass().getName()), t);
                }
            }
        }
    }
    
    private UnsafeValue<SSLContext, IllegalStateException> createLazySslContext(final DefaultSslContextProvider provider) {
        return (UnsafeValue<SSLContext, IllegalStateException>)Values.lazy((UnsafeValue)new UnsafeValue<SSLContext, IllegalStateException>() {
            public SSLContext get() {
                return provider.getDefaultSslContext();
            }
        });
    }
    
    void registerShutdownHook(final ShutdownHook shutdownHook) {
        this.checkNotClosed();
        this.shutdownHooks.push(new WeakReference<ShutdownHook>(shutdownHook, this.shReferenceQueue));
        this.cleanUpShutdownHooks();
    }
    
    private void cleanUpShutdownHooks() {
        Reference<? extends ShutdownHook> reference;
        while ((reference = this.shReferenceQueue.poll()) != null) {
            this.shutdownHooks.remove(reference);
            final ShutdownHook shutdownHook = (ShutdownHook)reference.get();
            if (shutdownHook != null) {
                shutdownHook.onShutdown();
            }
        }
    }
    
    private ScheduledExecutorService getDefaultScheduledExecutorService() {
        return Executors.newScheduledThreadPool(8);
    }
    
    public boolean isClosed() {
        return this.closedFlag.get();
    }
    
    void checkNotClosed() {
        Preconditions.checkState(!this.closedFlag.get(), (Object)LocalizationMessages.CLIENT_INSTANCE_CLOSED());
    }
    
    public boolean isDefaultSslContext() {
        return this.isDefaultSslContext;
    }
    
    public JerseyWebTarget target(final String uri) {
        this.checkNotClosed();
        Preconditions.checkNotNull((Object)uri, (Object)LocalizationMessages.CLIENT_URI_TEMPLATE_NULL());
        return new JerseyWebTarget(uri, this);
    }
    
    public JerseyWebTarget target(final URI uri) {
        this.checkNotClosed();
        Preconditions.checkNotNull((Object)uri, (Object)LocalizationMessages.CLIENT_URI_NULL());
        return new JerseyWebTarget(uri, this);
    }
    
    public JerseyWebTarget target(final UriBuilder uriBuilder) {
        this.checkNotClosed();
        Preconditions.checkNotNull((Object)uriBuilder, (Object)LocalizationMessages.CLIENT_URI_BUILDER_NULL());
        return new JerseyWebTarget(uriBuilder, this);
    }
    
    public JerseyWebTarget target(final Link link) {
        this.checkNotClosed();
        Preconditions.checkNotNull((Object)link, (Object)LocalizationMessages.CLIENT_TARGET_LINK_NULL());
        return new JerseyWebTarget(link, this);
    }
    
    public JerseyInvocation.Builder invocation(final Link link) {
        this.checkNotClosed();
        Preconditions.checkNotNull((Object)link, (Object)LocalizationMessages.CLIENT_INVOCATION_LINK_NULL());
        final JerseyWebTarget t = new JerseyWebTarget(link, this);
        final String acceptType = link.getType();
        return (acceptType != null) ? t.request(acceptType) : t.request();
    }
    
    public JerseyClient register(final Class<?> providerClass) {
        this.checkNotClosed();
        this.config.register(providerClass);
        return this;
    }
    
    public JerseyClient register(final Object provider) {
        this.checkNotClosed();
        this.config.register(provider);
        return this;
    }
    
    public JerseyClient register(final Class<?> providerClass, final int bindingPriority) {
        this.checkNotClosed();
        this.config.register(providerClass, bindingPriority);
        return this;
    }
    
    public JerseyClient register(final Class<?> providerClass, final Class<?>... contracts) {
        this.checkNotClosed();
        this.config.register(providerClass, contracts);
        return this;
    }
    
    public JerseyClient register(final Class<?> providerClass, final Map<Class<?>, Integer> contracts) {
        this.checkNotClosed();
        this.config.register(providerClass, contracts);
        return this;
    }
    
    public JerseyClient register(final Object provider, final int bindingPriority) {
        this.checkNotClosed();
        this.config.register(provider, bindingPriority);
        return this;
    }
    
    public JerseyClient register(final Object provider, final Class<?>... contracts) {
        this.checkNotClosed();
        this.config.register(provider, contracts);
        return this;
    }
    
    public JerseyClient register(final Object provider, final Map<Class<?>, Integer> contracts) {
        this.checkNotClosed();
        this.config.register(provider, contracts);
        return this;
    }
    
    public JerseyClient property(final String name, final Object value) {
        this.checkNotClosed();
        this.config.property(name, value);
        return this;
    }
    
    public ClientConfig getConfiguration() {
        this.checkNotClosed();
        return this.config.getConfiguration();
    }
    
    public SSLContext getSslContext() {
        return (SSLContext)this.sslContext.get();
    }
    
    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }
    
    public ExecutorService getExecutorService() {
        return this.executorService;
    }
    
    public ScheduledExecutorService getScheduledExecutorService() {
        return this.scheduledExecutorService;
    }
    
    public JerseyClient preInitialize() {
        this.config.preInitialize();
        return this;
    }
    
    static {
        LOG = Logger.getLogger(JerseyClient.class.getName());
        DEFAULT_SSL_CONTEXT_PROVIDER = new DefaultSslContextProvider() {
            @Override
            public SSLContext getDefaultSslContext() {
                return SslConfigurator.getDefaultContext();
            }
        };
    }
    
    interface ShutdownHook
    {
        void onShutdown();
    }
}
