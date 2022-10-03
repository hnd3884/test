package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import java.util.ArrayList;
import java.io.Serializable;

public class ObjectPool implements Serializable
{
    static final long serialVersionUID = -8519013691660936643L;
    private final Class objectType;
    private final ArrayList freeStack;
    
    public ObjectPool(final Class type) {
        this.objectType = type;
        this.freeStack = new ArrayList();
    }
    
    public ObjectPool(final String className) {
        try {
            this.objectType = ObjectFactory.findProviderClass(className, true);
        }
        catch (final ClassNotFoundException cnfe) {
            throw new WrappedRuntimeException(cnfe);
        }
        this.freeStack = new ArrayList();
    }
    
    public ObjectPool(final Class type, final int size) {
        this.objectType = type;
        this.freeStack = new ArrayList(size);
    }
    
    public ObjectPool() {
        this.objectType = null;
        this.freeStack = new ArrayList();
    }
    
    public synchronized Object getInstanceIfFree() {
        if (!this.freeStack.isEmpty()) {
            final Object result = this.freeStack.remove(this.freeStack.size() - 1);
            return result;
        }
        return null;
    }
    
    public synchronized Object getInstance() {
        if (this.freeStack.isEmpty()) {
            try {
                return this.objectType.newInstance();
            }
            catch (final InstantiationException ex) {}
            catch (final IllegalAccessException ex2) {}
            throw new RuntimeException(XMLMessages.createXMLMessage("ER_EXCEPTION_CREATING_POOL", null));
        }
        final Object result = this.freeStack.remove(this.freeStack.size() - 1);
        return result;
    }
    
    public synchronized void freeInstance(final Object obj) {
        this.freeStack.add(obj);
    }
}
