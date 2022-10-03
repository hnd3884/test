package com.adventnet.sym.server.mdm.queue.CollectionAssociationQueue;

import java.util.Hashtable;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.Enumeration;
import java.util.logging.Level;
import java.io.Serializable;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import org.bouncycastle.util.encoders.Base64;
import java.util.logging.Logger;

public class AssociationQueueDataSerializer
{
    private Logger tempLogger;
    protected static AssociationQueueDataSerializer dataSerializer;
    private final String nonSerializableType = "NonSerializableType";
    private final String nonSerializableData = "NonSerializableData";
    private String jsonType;
    
    public AssociationQueueDataSerializer() {
        this.tempLogger = Logger.getLogger(AssociationQueueDataSerializer.class.getName());
        this.jsonType = "JSONObject";
    }
    
    public static AssociationQueueDataSerializer getInstance() {
        if (AssociationQueueDataSerializer.dataSerializer == null) {
            AssociationQueueDataSerializer.dataSerializer = new AssociationQueueDataSerializer();
        }
        return AssociationQueueDataSerializer.dataSerializer;
    }
    
    public String convertObjectToString(final CommandQueueObject object) throws Exception {
        return Base64.toBase64String(this.convertObjectToStream(object).toByteArray());
    }
    
    public String convertPropertiesToString(final Properties properties) throws Exception {
        return Base64.toBase64String(this.convertPropertyToStream(properties).toByteArray());
    }
    
    public byte[] convertObjectToBytes(final CommandQueueObject object) throws Exception {
        return this.convertObjectToStream(object).toByteArray();
    }
    
    private ByteArrayOutputStream convertObjectToStream(final CommandQueueObject object) throws Exception {
        ObjectOutputStream objectStream = null;
        try {
            final ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            objectStream = new ObjectOutputStream(byteArray);
            object.setPropsFile(this.serializePropsfile(object.getPropsFile()));
            objectStream.writeObject(object);
            objectStream.flush();
            return byteArray;
        }
        finally {
            objectStream.close();
        }
    }
    
    private ByteArrayOutputStream convertPropertyToStream(final Properties properties) throws Exception {
        ObjectOutputStream objectStream = null;
        try {
            final ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            objectStream = new ObjectOutputStream(byteArray);
            objectStream.writeObject(this.serializePropsfile(properties));
            objectStream.flush();
            return byteArray;
        }
        finally {
            objectStream.close();
        }
    }
    
    public CommandQueueObject convertStringToObject(final String qData) throws Exception {
        return this.convertByteStreamToObject(Base64.decode(qData));
    }
    
    public Properties convertStringToProperties(final String qData) throws Exception {
        return this.convertByteStreamToProperty(Base64.decode(qData));
    }
    
    private CommandQueueObject convertByteStreamToObject(final byte[] qData) throws Exception {
        ObjectInputStream inputStream = null;
        try {
            final ByteArrayInputStream byteStream = new ByteArrayInputStream(qData);
            inputStream = new ObjectInputStream(byteStream);
            final Object commandObject = inputStream.readObject();
            return this.deSerializeObject((CommandQueueObject)commandObject);
        }
        finally {
            inputStream.close();
        }
    }
    
    private Properties convertByteStreamToProperty(final byte[] qData) throws Exception {
        ObjectInputStream inputStream = null;
        try {
            final ByteArrayInputStream byteStream = new ByteArrayInputStream(qData);
            inputStream = new ObjectInputStream(byteStream);
            final Object property = inputStream.readObject();
            return this.deSerializePropfile((Properties)property);
        }
        finally {
            inputStream.close();
        }
    }
    
    public Properties serializePropsfile(final Properties properties) throws Exception {
        final Enumeration<?> enums = properties.propertyNames();
        final StringBuilder builder = new StringBuilder();
        while (enums.hasMoreElements()) {
            final String propNames = (String)enums.nextElement();
            if (!(properties.get(propNames) instanceof Serializable)) {
                final Object serializedObject = this.serializeObject(((Hashtable<K, Object>)properties).get(propNames));
                this.tempLogger.log(Level.INFO, "{0} isSerializable {1}", new Object[] { propNames, serializedObject instanceof Serializable });
                properties.remove(propNames);
                ((Hashtable<String, Object>)properties).put(propNames, serializedObject);
            }
            builder.append("\nKey " + propNames + " Value " + ((Hashtable<K, Object>)properties).get(propNames) + " " + (properties.get(propNames) instanceof Serializable) + " " + ((Hashtable<K, Object>)properties).get(propNames).getClass().getName());
        }
        this.tempLogger.log(Level.INFO, "Printing the data of the props file - After serializing the file");
        this.printPropsFile(properties);
        return properties;
    }
    
    private Object serializeObject(final Object obj) throws Exception {
        if (obj instanceof JSONObject) {
            final HashMap<String, String> jsonMap = new HashMap<String, String>();
            jsonMap.put("NonSerializableType", this.jsonType);
            jsonMap.put("NonSerializableData", ((JSONObject)obj).toString());
            return jsonMap;
        }
        throw new Exception(obj.getClass().getName() + " is not handled, introduce appropriate handling");
    }
    
    private CommandQueueObject deSerializeObject(final CommandQueueObject queueObject) throws Exception {
        queueObject.setPropsFile(this.deSerializePropfile(queueObject.getPropsFile()));
        return queueObject;
    }
    
    public Properties deSerializePropfile(final Properties properties) throws Exception {
        final Enumeration<?> enums = properties.propertyNames();
        final StringBuilder builder = new StringBuilder();
        while (enums.hasMoreElements()) {
            final String propNames = (String)enums.nextElement();
            if (properties.get(propNames) instanceof HashMap) {
                final HashMap<?, ?> mapFile = ((Hashtable<K, HashMap<?, ?>>)properties).get(propNames);
                if (mapFile.containsKey("NonSerializableType")) {
                    this.tempLogger.log(Level.INFO, "The property has been serialized {0}", propNames);
                    final Object deSerializeObject = this.deSerializeObject(((Hashtable<K, Object>)properties).get(propNames));
                    this.tempLogger.log(Level.INFO, "{0} isSerializable {1}", new Object[] { propNames, deSerializeObject instanceof Serializable });
                    properties.remove(propNames);
                    ((Hashtable<String, Object>)properties).put(propNames, deSerializeObject);
                }
            }
            builder.append("\nKey " + propNames + " Value " + ((Hashtable<K, Object>)properties).get(propNames) + " " + (properties.get(propNames) instanceof Serializable) + " " + ((Hashtable<K, Object>)properties).get(propNames).getClass().getName());
        }
        this.tempLogger.log(Level.INFO, "Printing the data of the props file - After de-serializing the file");
        this.printPropsFile(properties);
        return properties;
    }
    
    private Object deSerializeObject(final Object obj) throws Exception {
        final HashMap<String, String> hashMap = (HashMap<String, String>)obj;
        if (hashMap.get("NonSerializableType").equalsIgnoreCase(this.jsonType)) {
            return new JSONObject((String)hashMap.get("NonSerializableData"));
        }
        return obj;
    }
    
    private void printPropsFile(final Properties props) {
        final StringBuilder builder = new StringBuilder();
        final Enumeration<?> enums = props.propertyNames();
        while (enums.hasMoreElements()) {
            final String propNames = (String)enums.nextElement();
            builder.append("\nKey " + propNames + " Value " + ((Hashtable<K, Object>)props).get(propNames) + " " + (props.get(propNames) instanceof Serializable) + " " + ((Hashtable<K, Object>)props).get(propNames).getClass().getName());
        }
        this.tempLogger.log(Level.INFO, builder.toString());
    }
    
    static {
        AssociationQueueDataSerializer.dataSerializer = null;
    }
}
