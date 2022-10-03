package com.google.api.client.googleapis.auth.oauth2;

class SystemEnvironmentProvider
{
    static final SystemEnvironmentProvider INSTANCE;
    
    String getEnv(final String name) {
        return System.getenv(name);
    }
    
    boolean getEnvEquals(final String name, final String value) {
        return System.getenv().containsKey(name) && System.getenv(name).equals(value);
    }
    
    static {
        INSTANCE = new SystemEnvironmentProvider();
    }
}
