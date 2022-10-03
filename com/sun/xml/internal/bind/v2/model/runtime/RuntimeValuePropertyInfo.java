package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;

public interface RuntimeValuePropertyInfo extends ValuePropertyInfo<Type, Class>, RuntimePropertyInfo, RuntimeNonElementRef
{
    RuntimeNonElement getTarget();
}
