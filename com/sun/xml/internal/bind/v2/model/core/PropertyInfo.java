package com.sun.xml.internal.bind.v2.model.core;

import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;
import javax.activation.MimeType;
import java.util.Collection;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;

public interface PropertyInfo<T, C> extends AnnotationSource
{
    TypeInfo<T, C> parent();
    
    String getName();
    
    String displayName();
    
    boolean isCollection();
    
    Collection<? extends TypeInfo<T, C>> ref();
    
    PropertyKind kind();
    
    Adapter<T, C> getAdapter();
    
    ID id();
    
    MimeType getExpectedMimeType();
    
    boolean inlineBinaryData();
    
    @Nullable
    QName getSchemaType();
}
