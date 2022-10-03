package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;

public interface RuntimeArrayInfo extends ArrayInfo<Type, Class>, RuntimeNonElement
{
    Class getType();
    
    RuntimeNonElement getItemType();
}
