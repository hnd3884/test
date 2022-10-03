package org.glassfish.jersey.server.model.internal;

import java.lang.annotation.Annotation;
import javax.ws.rs.Path;

public final class ModelHelper
{
    public static Class<?> getAnnotatedResourceClass(final Class<?> resourceClass) {
        Class<?> foundInterface = null;
        Class<?> cls = resourceClass;
        while (!cls.isAnnotationPresent((Class<? extends Annotation>)Path.class)) {
            if (foundInterface == null) {
                for (final Class<?> i : cls.getInterfaces()) {
                    if (i.isAnnotationPresent((Class<? extends Annotation>)Path.class)) {
                        foundInterface = i;
                        break;
                    }
                }
            }
            if ((cls = cls.getSuperclass()) == null) {
                if (foundInterface != null) {
                    return foundInterface;
                }
                return resourceClass;
            }
        }
        return cls;
    }
    
    private ModelHelper() {
        throw new AssertionError((Object)"Instantiation not allowed.");
    }
}
