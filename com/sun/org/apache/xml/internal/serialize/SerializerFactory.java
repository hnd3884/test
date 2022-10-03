package com.sun.org.apache.xml.internal.serialize;

import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import java.util.StringTokenizer;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.Collections;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

public abstract class SerializerFactory
{
    public static final String FactoriesProperty = "com.sun.org.apache.xml.internal.serialize.factories";
    private static final Map<String, SerializerFactory> _factories;
    
    public static void registerSerializerFactory(final SerializerFactory factory) {
        synchronized (SerializerFactory._factories) {
            final String method = factory.getSupportedMethod();
            SerializerFactory._factories.put(method, factory);
        }
    }
    
    public static SerializerFactory getSerializerFactory(final String method) {
        return SerializerFactory._factories.get(method);
    }
    
    protected abstract String getSupportedMethod();
    
    public abstract Serializer makeSerializer(final OutputFormat p0);
    
    public abstract Serializer makeSerializer(final Writer p0, final OutputFormat p1);
    
    public abstract Serializer makeSerializer(final OutputStream p0, final OutputFormat p1) throws UnsupportedEncodingException;
    
    static {
        _factories = Collections.synchronizedMap(new HashMap<String, SerializerFactory>());
        SerializerFactory factory = new SerializerFactoryImpl("xml");
        registerSerializerFactory(factory);
        factory = new SerializerFactoryImpl("html");
        registerSerializerFactory(factory);
        factory = new SerializerFactoryImpl("xhtml");
        registerSerializerFactory(factory);
        factory = new SerializerFactoryImpl("text");
        registerSerializerFactory(factory);
        final String list = SecuritySupport.getSystemProperty("com.sun.org.apache.xml.internal.serialize.factories");
        if (list != null) {
            final StringTokenizer token = new StringTokenizer(list, " ;,:");
            while (token.hasMoreTokens()) {
                final String className = token.nextToken();
                try {
                    factory = (SerializerFactory)ObjectFactory.newInstance(className, true);
                    if (!SerializerFactory._factories.containsKey(factory.getSupportedMethod())) {
                        continue;
                    }
                    SerializerFactory._factories.put(factory.getSupportedMethod(), factory);
                }
                catch (final Exception ex) {}
            }
        }
    }
}
