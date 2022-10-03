package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.NonElementRef;

public interface RuntimeNonElementRef extends NonElementRef<Type, Class>
{
    RuntimeNonElement getTarget();
    
    RuntimePropertyInfo getSource();
    
    Transducer getTransducer();
}
