package com.azul.crs.com.fasterxml.jackson.databind.ext;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.deser.Deserializers;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.ser.Serializers;
import com.azul.crs.com.fasterxml.jackson.databind.JsonSerializer;
import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.SerializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.ser.std.DateSerializer;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class OptionalHandlerFactory implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String PACKAGE_PREFIX_JAVAX_XML = "javax.xml.";
    private static final String SERIALIZERS_FOR_JAVAX_XML = "com.azul.crs.com.fasterxml.jackson.databind.ext.CoreXMLSerializers";
    private static final String DESERIALIZERS_FOR_JAVAX_XML = "com.azul.crs.com.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
    private static final String SERIALIZER_FOR_DOM_NODE = "com.azul.crs.com.fasterxml.jackson.databind.ext.DOMSerializer";
    private static final String DESERIALIZER_FOR_DOM_DOCUMENT = "com.azul.crs.com.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer";
    private static final String DESERIALIZER_FOR_DOM_NODE = "com.azul.crs.com.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer";
    private static final Class<?> CLASS_DOM_NODE;
    private static final Class<?> CLASS_DOM_DOCUMENT;
    private static final Java7Handlers _jdk7Helper;
    public static final OptionalHandlerFactory instance;
    private final Map<String, String> _sqlDeserializers;
    private final Map<String, Object> _sqlSerializers;
    private static final String CLS_NAME_JAVA_SQL_TIMESTAMP = "java.sql.Timestamp";
    private static final String CLS_NAME_JAVA_SQL_DATE = "java.sql.Date";
    private static final String CLS_NAME_JAVA_SQL_TIME = "java.sql.Time";
    private static final String CLS_NAME_JAVA_SQL_BLOB = "java.sql.Blob";
    private static final String CLS_NAME_JAVA_SQL_SERIALBLOB = "javax.sql.rowset.serial.SerialBlob";
    
    protected OptionalHandlerFactory() {
        (this._sqlDeserializers = new HashMap<String, String>()).put("java.sql.Date", "com.azul.crs.com.fasterxml.jackson.databind.deser.std.DateDeserializers$SqlDateDeserializer");
        this._sqlDeserializers.put("java.sql.Timestamp", "com.azul.crs.com.fasterxml.jackson.databind.deser.std.DateDeserializers$TimestampDeserializer");
        (this._sqlSerializers = new HashMap<String, Object>()).put("java.sql.Timestamp", DateSerializer.instance);
        this._sqlSerializers.put("java.sql.Date", "com.azul.crs.com.fasterxml.jackson.databind.ser.std.SqlDateSerializer");
        this._sqlSerializers.put("java.sql.Time", "com.azul.crs.com.fasterxml.jackson.databind.ser.std.SqlTimeSerializer");
        this._sqlSerializers.put("java.sql.Blob", "com.azul.crs.com.fasterxml.jackson.databind.ext.SqlBlobSerializer");
        this._sqlSerializers.put("javax.sql.rowset.serial.SerialBlob", "com.azul.crs.com.fasterxml.jackson.databind.ext.SqlBlobSerializer");
    }
    
    public JsonSerializer<?> findSerializer(final SerializationConfig config, final JavaType type, final BeanDescription beanDesc) {
        final Class<?> rawType = type.getRawClass();
        if (OptionalHandlerFactory.CLASS_DOM_NODE != null && OptionalHandlerFactory.CLASS_DOM_NODE.isAssignableFrom(rawType)) {
            return (JsonSerializer)this.instantiate("com.azul.crs.com.fasterxml.jackson.databind.ext.DOMSerializer", type);
        }
        if (OptionalHandlerFactory._jdk7Helper != null) {
            final JsonSerializer<?> ser = OptionalHandlerFactory._jdk7Helper.getSerializerForJavaNioFilePath(rawType);
            if (ser != null) {
                return ser;
            }
        }
        final String className = rawType.getName();
        final Object sqlHandler = this._sqlSerializers.get(className);
        if (sqlHandler != null) {
            if (sqlHandler instanceof JsonSerializer) {
                return (JsonSerializer)sqlHandler;
            }
            return (JsonSerializer)this.instantiate((String)sqlHandler, type);
        }
        else {
            if (!className.startsWith("javax.xml.") && !this.hasSuperClassStartingWith(rawType, "javax.xml.")) {
                return null;
            }
            final String factoryName = "com.azul.crs.com.fasterxml.jackson.databind.ext.CoreXMLSerializers";
            final Object ob = this.instantiate(factoryName, type);
            if (ob == null) {
                return null;
            }
            return ((Serializers)ob).findSerializer(config, type, beanDesc);
        }
    }
    
    public JsonDeserializer<?> findDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) throws JsonMappingException {
        final Class<?> rawType = type.getRawClass();
        if (OptionalHandlerFactory._jdk7Helper != null) {
            final JsonDeserializer<?> deser = OptionalHandlerFactory._jdk7Helper.getDeserializerForJavaNioFilePath(rawType);
            if (deser != null) {
                return deser;
            }
        }
        if (OptionalHandlerFactory.CLASS_DOM_NODE != null && OptionalHandlerFactory.CLASS_DOM_NODE.isAssignableFrom(rawType)) {
            return (JsonDeserializer)this.instantiate("com.azul.crs.com.fasterxml.jackson.databind.ext.DOMDeserializer$NodeDeserializer", type);
        }
        if (OptionalHandlerFactory.CLASS_DOM_DOCUMENT != null && OptionalHandlerFactory.CLASS_DOM_DOCUMENT.isAssignableFrom(rawType)) {
            return (JsonDeserializer)this.instantiate("com.azul.crs.com.fasterxml.jackson.databind.ext.DOMDeserializer$DocumentDeserializer", type);
        }
        final String className = rawType.getName();
        final String deserName = this._sqlDeserializers.get(className);
        if (deserName != null) {
            return (JsonDeserializer)this.instantiate(deserName, type);
        }
        if (!className.startsWith("javax.xml.") && !this.hasSuperClassStartingWith(rawType, "javax.xml.")) {
            return null;
        }
        final String factoryName = "com.azul.crs.com.fasterxml.jackson.databind.ext.CoreXMLDeserializers";
        final Object ob = this.instantiate(factoryName, type);
        if (ob == null) {
            return null;
        }
        return ((Deserializers)ob).findBeanDeserializer(type, config, beanDesc);
    }
    
    public boolean hasDeserializerFor(final Class<?> valueType) {
        if (OptionalHandlerFactory.CLASS_DOM_NODE != null && OptionalHandlerFactory.CLASS_DOM_NODE.isAssignableFrom(valueType)) {
            return true;
        }
        if (OptionalHandlerFactory.CLASS_DOM_DOCUMENT != null && OptionalHandlerFactory.CLASS_DOM_DOCUMENT.isAssignableFrom(valueType)) {
            return true;
        }
        final String className = valueType.getName();
        return className.startsWith("javax.xml.") || this.hasSuperClassStartingWith(valueType, "javax.xml.") || this._sqlDeserializers.containsKey(className);
    }
    
    private Object instantiate(final String className, final JavaType valueType) {
        try {
            return this.instantiate(Class.forName(className), valueType);
        }
        catch (final Throwable e) {
            throw new IllegalStateException("Failed to find class `" + className + "` for handling values of type " + ClassUtil.getTypeDescription(valueType) + ", problem: (" + e.getClass().getName() + ") " + e.getMessage());
        }
    }
    
    private Object instantiate(final Class<?> handlerClass, final JavaType valueType) {
        try {
            return ClassUtil.createInstance(handlerClass, false);
        }
        catch (final Throwable e) {
            throw new IllegalStateException("Failed to create instance of `" + handlerClass.getName() + "` for handling values of type " + ClassUtil.getTypeDescription(valueType) + ", problem: (" + e.getClass().getName() + ") " + e.getMessage());
        }
    }
    
    private boolean hasSuperClassStartingWith(final Class<?> rawType, final String prefix) {
        for (Class<?> supertype = rawType.getSuperclass(); supertype != null; supertype = supertype.getSuperclass()) {
            if (supertype == Object.class) {
                return false;
            }
            if (supertype.getName().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        Class<?> doc = null;
        Class<?> node = null;
        try {
            node = Node.class;
            doc = Document.class;
        }
        catch (final Throwable t) {}
        CLASS_DOM_NODE = node;
        CLASS_DOM_DOCUMENT = doc;
        Java7Handlers x = null;
        try {
            x = Java7Handlers.instance();
        }
        catch (final Throwable t2) {}
        _jdk7Helper = x;
        instance = new OptionalHandlerFactory();
    }
}
