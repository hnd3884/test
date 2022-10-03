package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.NonElement;

public interface RuntimeNonElement extends NonElement<Type, Class>, RuntimeTypeInfo
{
     <V> Transducer<V> getTransducer();
}
