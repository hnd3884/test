package org.glassfish.jersey;

import java.util.HashMap;
import javax.ws.rs.RuntimeType;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import java.util.Map;
import org.glassfish.jersey.internal.util.PropertiesClass;

@PropertiesClass
public final class CommonProperties
{
    private static final Map<String, String> LEGACY_FALLBACK_MAP;
    public static final String FEATURE_AUTO_DISCOVERY_DISABLE = "jersey.config.disableAutoDiscovery";
    public static final String FEATURE_AUTO_DISCOVERY_DISABLE_CLIENT = "jersey.config.client.disableAutoDiscovery";
    public static final String FEATURE_AUTO_DISCOVERY_DISABLE_SERVER = "jersey.config.server.disableAutoDiscovery";
    public static final String JSON_PROCESSING_FEATURE_DISABLE = "jersey.config.disableJsonProcessing";
    public static final String JSON_PROCESSING_FEATURE_DISABLE_CLIENT = "jersey.config.client.disableJsonProcessing";
    public static final String JSON_PROCESSING_FEATURE_DISABLE_SERVER = "jersey.config.server.disableJsonProcessing";
    public static final String METAINF_SERVICES_LOOKUP_DISABLE = "jersey.config.disableMetainfServicesLookup";
    public static final String METAINF_SERVICES_LOOKUP_DISABLE_CLIENT = "jersey.config.client.disableMetainfServicesLookup";
    public static final String METAINF_SERVICES_LOOKUP_DISABLE_SERVER = "jersey.config.server.disableMetainfServicesLookup";
    public static final String MOXY_JSON_FEATURE_DISABLE = "jersey.config.disableMoxyJson";
    public static final String MOXY_JSON_FEATURE_DISABLE_CLIENT = "jersey.config.client.disableMoxyJson";
    public static final String MOXY_JSON_FEATURE_DISABLE_SERVER = "jersey.config.server.disableMoxyJson";
    public static final String OUTBOUND_CONTENT_LENGTH_BUFFER = "jersey.config.contentLength.buffer";
    public static final String OUTBOUND_CONTENT_LENGTH_BUFFER_CLIENT = "jersey.config.client.contentLength.buffer";
    public static final String OUTBOUND_CONTENT_LENGTH_BUFFER_SERVER = "jersey.config.server.contentLength.buffer";
    
    private CommonProperties() {
    }
    
    public static Object getValue(final Map<String, ?> properties, final String propertyName, final Class<?> type) {
        return PropertiesHelper.getValue(properties, propertyName, (Class<Object>)type, CommonProperties.LEGACY_FALLBACK_MAP);
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final String propertyName, final T defaultValue) {
        return PropertiesHelper.getValue(properties, propertyName, defaultValue, CommonProperties.LEGACY_FALLBACK_MAP);
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final RuntimeType runtime, final String propertyName, final T defaultValue) {
        return PropertiesHelper.getValue(properties, runtime, propertyName, defaultValue, CommonProperties.LEGACY_FALLBACK_MAP);
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final RuntimeType runtime, final String propertyName, final T defaultValue, final Class<T> type) {
        return PropertiesHelper.getValue(properties, runtime, propertyName, defaultValue, type, CommonProperties.LEGACY_FALLBACK_MAP);
    }
    
    public static <T> T getValue(final Map<String, ?> properties, final RuntimeType runtime, final String propertyName, final Class<T> type) {
        return PropertiesHelper.getValue(properties, runtime, propertyName, type, CommonProperties.LEGACY_FALLBACK_MAP);
    }
    
    static {
        (LEGACY_FALLBACK_MAP = new HashMap<String, String>()).put("jersey.config.client.contentLength.buffer", "jersey.config.contentLength.buffer.client");
        CommonProperties.LEGACY_FALLBACK_MAP.put("jersey.config.server.contentLength.buffer", "jersey.config.contentLength.buffer.server");
        CommonProperties.LEGACY_FALLBACK_MAP.put("jersey.config.client.disableAutoDiscovery", "jersey.config.disableAutoDiscovery.client");
        CommonProperties.LEGACY_FALLBACK_MAP.put("jersey.config.server.disableAutoDiscovery", "jersey.config.disableAutoDiscovery.server");
        CommonProperties.LEGACY_FALLBACK_MAP.put("jersey.config.client.disableJsonProcessing", "jersey.config.disableJsonProcessing.client");
        CommonProperties.LEGACY_FALLBACK_MAP.put("jersey.config.server.disableJsonProcessing", "jersey.config.disableJsonProcessing.server");
        CommonProperties.LEGACY_FALLBACK_MAP.put("jersey.config.client.disableMetainfServicesLookup", "jersey.config.disableMetainfServicesLookup.client");
        CommonProperties.LEGACY_FALLBACK_MAP.put("jersey.config.server.disableMetainfServicesLookup", "jersey.config.disableMetainfServicesLookup.server");
        CommonProperties.LEGACY_FALLBACK_MAP.put("jersey.config.client.disableMoxyJson", "jersey.config.disableMoxyJson.client");
        CommonProperties.LEGACY_FALLBACK_MAP.put("jersey.config.server.disableMoxyJson", "jersey.config.disableMoxyJson.server");
    }
}
