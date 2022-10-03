package org.glassfish.hk2.api.messaging;

import java.util.Set;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface Topic<T>
{
    void publish(final T p0);
    
    Topic<T> named(final String p0);
    
     <U> Topic<U> ofType(final Type p0);
    
    Topic<T> qualifiedWith(final Annotation... p0);
    
    Type getTopicType();
    
    Set<Annotation> getTopicQualifiers();
}
