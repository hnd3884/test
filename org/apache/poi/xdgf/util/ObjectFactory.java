package org.apache.poi.xdgf.util;

import java.lang.reflect.InvocationTargetException;
import org.apache.poi.ooxml.POIXMLException;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import java.util.Map;
import org.apache.xmlbeans.XmlObject;

public class ObjectFactory<T, X extends XmlObject>
{
    Map<String, Constructor<? extends T>> _types;
    
    public ObjectFactory() {
        this._types = new HashMap<String, Constructor<? extends T>>();
    }
    
    public void put(final String typeName, final Class<? extends T> cls, final Class<?>... varargs) throws NoSuchMethodException, SecurityException {
        this._types.put(typeName, cls.getDeclaredConstructor(varargs));
    }
    
    public T load(final String name, final Object... varargs) {
        final Constructor<? extends T> constructor = this._types.get(name);
        if (constructor == null) {
            final X xmlObject = (X)varargs[0];
            final String typeName = xmlObject.schemaType().getName().getLocalPart();
            throw new POIXMLException("Invalid '" + typeName + "' name '" + name + "'");
        }
        try {
            return (T)constructor.newInstance(varargs);
        }
        catch (final InvocationTargetException e) {
            throw new POIXMLException(e.getCause());
        }
        catch (final Exception e2) {
            throw new POIXMLException(e2);
        }
    }
}
