package org.glassfish.jersey.apache.connector;

import org.glassfish.jersey.internal.util.PropertiesHelper;
import java.util.Map;
import org.glassfish.jersey.internal.util.PropertiesClass;

@PropertiesClass
public final class ApacheClientProperties
{
    public static final String CREDENTIALS_PROVIDER = "jersey.config.apache.client.credentialsProvider";
    public static final String DISABLE_COOKIES = "jersey.config.apache.client.handleCookies";
    public static final String PREEMPTIVE_BASIC_AUTHENTICATION = "jersey.config.apache.client.preemptiveBasicAuthentication";
    public static final String CONNECTION_MANAGER = "jersey.config.apache.client.connectionManager";
    public static final String CONNECTION_MANAGER_SHARED = "jersey.config.apache.client.connectionManagerShared";
    public static final String REQUEST_CONFIG = "jersey.config.apache.client.requestConfig";
    public static final String RETRY_HANDLER = "jersey.config.apache.client.retryHandler";
    
    public static <T> T getValue(final Map<String, ?> properties, final String key, final Class<T> type) {
        return (T)PropertiesHelper.getValue((Map)properties, key, (Class)type, (Map)null);
    }
    
    private ApacheClientProperties() {
        throw new AssertionError((Object)"No instances allowed.");
    }
}
