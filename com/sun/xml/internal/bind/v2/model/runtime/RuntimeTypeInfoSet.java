package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import javax.xml.namespace.QName;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;

public interface RuntimeTypeInfoSet extends TypeInfoSet<Type, Class, Field, Method>
{
    Map<Class, ? extends RuntimeArrayInfo> arrays();
    
    Map<Class, ? extends RuntimeClassInfo> beans();
    
    Map<Type, ? extends RuntimeBuiltinLeafInfo> builtins();
    
    Map<Class, ? extends RuntimeEnumLeafInfo> enums();
    
    RuntimeNonElement getTypeInfo(final Type p0);
    
    RuntimeNonElement getAnyTypeInfo();
    
    RuntimeNonElement getClassInfo(final Class p0);
    
    RuntimeElementInfo getElementInfo(final Class p0, final QName p1);
    
    Map<QName, ? extends RuntimeElementInfo> getElementMappings(final Class p0);
    
    Iterable<? extends RuntimeElementInfo> getAllElements();
}
