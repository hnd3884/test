package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElementRef;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import java.util.Collections;
import java.util.List;
import java.util.Collection;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import javax.xml.bind.JAXBElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

final class RuntimeElementInfoImpl extends ElementInfoImpl<Type, Class, Field, Method> implements RuntimeElementInfo
{
    private final Class<? extends XmlAdapter> adapterType;
    
    public RuntimeElementInfoImpl(final RuntimeModelBuilder modelBuilder, final RegistryInfoImpl registry, final Method method) throws IllegalAnnotationException {
        super((ModelBuilder<Object, Object, Object, Method>)modelBuilder, registry, method);
        final Adapter<Type, Class> a = (Adapter<Type, Class>)this.getProperty().getAdapter();
        if (a != null) {
            this.adapterType = a.adapterType;
        }
        else {
            this.adapterType = null;
        }
    }
    
    @Override
    protected PropertyImpl createPropertyImpl() {
        return new RuntimePropertyImpl();
    }
    
    @Override
    public RuntimeElementPropertyInfo getProperty() {
        return (RuntimeElementPropertyInfo)super.getProperty();
    }
    
    @Override
    public Class<? extends JAXBElement> getType() {
        return Utils.REFLECTION_NAVIGATOR.erasure(super.getType());
    }
    
    @Override
    public RuntimeClassInfo getScope() {
        return (RuntimeClassInfo)super.getScope();
    }
    
    @Override
    public RuntimeNonElement getContentType() {
        return (RuntimeNonElement)super.getContentType();
    }
    
    class RuntimePropertyImpl extends PropertyImpl implements RuntimeElementPropertyInfo, RuntimeTypeRef
    {
        @Override
        public Accessor getAccessor() {
            if (RuntimeElementInfoImpl.this.adapterType == null) {
                return Accessor.JAXB_ELEMENT_VALUE;
            }
            return Accessor.JAXB_ELEMENT_VALUE.adapt((Class<Object>)this.getAdapter().defaultType, RuntimeElementInfoImpl.this.adapterType);
        }
        
        @Override
        public Type getRawType() {
            return Collection.class;
        }
        
        @Override
        public Type getIndividualType() {
            return ((TypeInfo<Type, C>)RuntimeElementInfoImpl.this.getContentType()).getType();
        }
        
        @Override
        public boolean elementOnlyContent() {
            return false;
        }
        
        @Override
        public List<? extends RuntimeTypeRef> getTypes() {
            return Collections.singletonList(this);
        }
        
        @Override
        public List<? extends RuntimeNonElement> ref() {
            return (List<? extends RuntimeNonElement>)super.ref();
        }
        
        @Override
        public RuntimeNonElement getTarget() {
            return (RuntimeNonElement)super.getTarget();
        }
        
        @Override
        public RuntimePropertyInfo getSource() {
            return this;
        }
        
        @Override
        public Transducer getTransducer() {
            return RuntimeModelBuilder.createTransducer(this);
        }
    }
}
