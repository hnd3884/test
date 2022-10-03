package com.sun.xml.internal.bind.v2.model.runtime;

import java.util.List;
import java.util.Collection;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;

public interface RuntimeElementPropertyInfo extends ElementPropertyInfo<Type, Class>, RuntimePropertyInfo
{
    Collection<? extends RuntimeTypeInfo> ref();
    
    List<? extends RuntimeTypeRef> getTypes();
}
