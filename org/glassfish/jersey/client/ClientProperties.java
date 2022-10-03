package org.glassfish.jersey.client;

import org.glassfish.jersey.internal.util.PropertiesHelper;
import java.util.Map;
import org.glassfish.jersey.internal.util.PropertyAlias;
import org.glassfish.jersey.internal.util.PropertiesClass;

@PropertiesClass
public final class ClientProperties
{
    public static final String FOLLOW_REDIRECTS = "jersey.config.client.followRedirects";
    public static final String READ_TIMEOUT = "jersey.config.client.readTimeout";
    public static final String CONNECT_TIMEOUT = "jersey.config.client.connectTimeout";
    public static final String CHUNKED_ENCODING_SIZE = "jersey.config.client.chunkedEncodingSize";
    public static final int DEFAULT_CHUNK_SIZE = 4096;
    public static final String ASYNC_THREADPOOL_SIZE = "jersey.config.client.async.threadPoolSize";
    public static final String BACKGROUND_SCHEDULER_THREADPOOL_SIZE = "jersey.config.client.backgroundScheduler.threadPoolSize";
    public static final String USE_ENCODING = "jersey.config.client.useEncoding";
    @PropertyAlias
    public static final String FEATURE_AUTO_DISCOVERY_DISABLE = "jersey.config.client.disableAutoDiscovery";
    @PropertyAlias
    public static final String OUTBOUND_CONTENT_LENGTH_BUFFER = "jersey.config.client.contentLength.buffer";
    @PropertyAlias
    public static final String JSON_PROCESSING_FEATURE_DISABLE = "jersey.config.client.disableJsonProcessing";
    @PropertyAlias
    public static final String METAINF_SERVICES_LOOKUP_DISABLE = "jersey.config.client.disableMetainfServicesLookup";
    @PropertyAlias
    public static final String MOXY_JSON_FEATURE_DISABLE = "jersey.config.client.disableMoxyJson";
    public static final String SUPPRESS_HTTP_COMPLIANCE_VALIDATION = "jersey.config.client.suppressHttpComplianceValidation";
    public static final String DIGESTAUTH_URI_CACHE_SIZELIMIT = "jersey.config.client.digestAuthUriCacheSizeLimit";
    public static final String PROXY_URI = "jersey.config.client.proxy.uri";
    public static final String PROXY_USERNAME = "jersey.config.client.proxy.username";
    public static final String PROXY_PASSWORD = "jersey.config.client.proxy.password";
    public static final String REQUEST_ENTITY_PROCESSING = "jersey.config.client.request.entity.processing";
    
    private ClientProperties() {
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final String key, final T defaultValue) {
        return (T)PropertiesHelper.getValue((Map)properties, key, (Object)defaultValue, (Map)null);
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final String key, final T defaultValue, final Class<T> type) {
        return (T)PropertiesHelper.getValue((Map)properties, key, (Object)defaultValue, (Class)type, (Map)null);
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final String key, final Class<T> type) {
        return (T)PropertiesHelper.getValue((Map)properties, key, (Class)type, (Map)null);
    }
}
