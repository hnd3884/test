package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.WrappedRuntimeException;
import org.xml.sax.ContentHandler;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.util.Properties;

public final class SerializerFactory
{
    private SerializerFactory() {
    }
    
    public static Serializer getSerializer(final Properties format) {
        Serializer ser;
        try {
            final String method = format.getProperty("method");
            if (method == null) {
                final String msg = Utils.messages.createMessage("ER_FACTORY_PROPERTY_MISSING", new Object[] { "method" });
                throw new IllegalArgumentException(msg);
            }
            String className = format.getProperty("{http://xml.apache.org/xalan}content-handler");
            if (null == className) {
                final Properties methodDefaults = OutputPropertiesFactory.getDefaultMethodProperties(method);
                className = methodDefaults.getProperty("{http://xml.apache.org/xalan}content-handler");
                if (null == className) {
                    final String msg2 = Utils.messages.createMessage("ER_FACTORY_PROPERTY_MISSING", new Object[] { "{http://xml.apache.org/xalan}content-handler" });
                    throw new IllegalArgumentException(msg2);
                }
            }
            Class cls = ObjectFactory.findProviderClass(className, true);
            final Object obj = cls.newInstance();
            if (obj instanceof SerializationHandler) {
                ser = cls.newInstance();
                ser.setOutputFormat(format);
            }
            else {
                if (!(obj instanceof ContentHandler)) {
                    throw new Exception(Utils.messages.createMessage("ER_SERIALIZER_NOT_CONTENTHANDLER", new Object[] { className }));
                }
                className = "com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler";
                cls = ObjectFactory.findProviderClass(className, true);
                final SerializationHandler sh = cls.newInstance();
                sh.setContentHandler((ContentHandler)obj);
                sh.setOutputFormat(format);
                ser = sh;
            }
        }
        catch (final Exception e) {
            throw new WrappedRuntimeException(e);
        }
        return ser;
    }
}
