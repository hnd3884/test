package com.google.api.client.googleapis.util;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Beta;

@Beta
public final class Utils
{
    public static JsonFactory getDefaultJsonFactory() {
        return JsonFactoryInstanceHolder.INSTANCE;
    }
    
    public static HttpTransport getDefaultTransport() {
        return TransportInstanceHolder.INSTANCE;
    }
    
    private Utils() {
    }
    
    private static class JsonFactoryInstanceHolder
    {
        static final JsonFactory INSTANCE;
        
        static {
            INSTANCE = (JsonFactory)new GsonFactory();
        }
    }
    
    private static class TransportInstanceHolder
    {
        static final HttpTransport INSTANCE;
        
        static {
            INSTANCE = (HttpTransport)new NetHttpTransport();
        }
    }
}
