package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;

public interface RuntimeTypeRef extends TypeRef<Type, Class>, RuntimeNonElementRef
{
    RuntimeNonElement getTarget();
    
    RuntimePropertyInfo getSource();
}
