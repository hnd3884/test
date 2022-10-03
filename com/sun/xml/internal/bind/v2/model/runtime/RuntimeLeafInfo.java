package com.sun.xml.internal.bind.v2.model.runtime;

import javax.xml.namespace.QName;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.LeafInfo;

public interface RuntimeLeafInfo extends LeafInfo<Type, Class>, RuntimeNonElement
{
     <V> Transducer<V> getTransducer();
    
    Class getClazz();
    
    QName[] getTypeNames();
}
