package com.sun.jmx.mbeanserver;

import sun.reflect.misc.ReflectUtil;
import java.io.ObjectStreamClass;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

class ObjectInputStreamWithLoader extends ObjectInputStream
{
    private ClassLoader loader;
    
    public ObjectInputStreamWithLoader(final InputStream inputStream, final ClassLoader loader) throws IOException {
        super(inputStream);
        this.loader = loader;
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        if (this.loader == null) {
            return super.resolveClass(objectStreamClass);
        }
        final String name = objectStreamClass.getName();
        ReflectUtil.checkPackageAccess(name);
        return Class.forName(name, false, this.loader);
    }
}
