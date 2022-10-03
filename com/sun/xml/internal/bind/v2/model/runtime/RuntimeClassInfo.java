package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import org.xml.sax.Locator;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Method;
import java.util.List;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;

public interface RuntimeClassInfo extends ClassInfo<Type, Class>, RuntimeNonElement
{
    RuntimeClassInfo getBaseClass();
    
    List<? extends RuntimePropertyInfo> getProperties();
    
    RuntimePropertyInfo getProperty(final String p0);
    
    Method getFactoryMethod();
    
     <BeanT> Accessor<BeanT, Map<QName, String>> getAttributeWildcard();
    
     <BeanT> Accessor<BeanT, Locator> getLocatorField();
}
