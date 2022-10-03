package org.apache.lucene.queryparser.flexible.core.config;

public final class ConfigurationKey<T>
{
    private ConfigurationKey() {
    }
    
    public static <T> ConfigurationKey<T> newInstance() {
        return new ConfigurationKey<T>();
    }
}
