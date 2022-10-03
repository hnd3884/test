package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import javax.xml.bind.JAXBElement;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;

public interface RuntimeElementInfo extends ElementInfo<Type, Class>, RuntimeElement
{
    RuntimeClassInfo getScope();
    
    RuntimeElementPropertyInfo getProperty();
    
    Class<? extends JAXBElement> getType();
    
    RuntimeNonElement getContentType();
}
