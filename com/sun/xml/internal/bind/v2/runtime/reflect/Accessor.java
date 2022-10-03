package com.sun.xml.internal.bind.v2.runtime.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.OptimizedAccessorFactory;
import java.util.logging.Level;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Arrays;
import java.net.URL;
import java.net.URI;
import java.io.File;
import java.util.Date;
import javax.xml.transform.Source;
import javax.activation.DataHandler;
import java.awt.Image;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.Duration;
import java.util.Calendar;
import java.lang.reflect.Type;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.xml.sax.SAXException;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import java.util.List;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;

public abstract class Accessor<BeanT, ValueT> implements Receiver
{
    public final Class<ValueT> valueType;
    private static List<Class> nonAbstractableClasses;
    private static boolean accessWarned;
    private static final Accessor ERROR;
    public static final Accessor<JAXBElement, Object> JAXB_ELEMENT_VALUE;
    private static final Map<Class, Object> uninitializedValues;
    
    public Class<ValueT> getValueType() {
        return this.valueType;
    }
    
    protected Accessor(final Class<ValueT> valueType) {
        this.valueType = valueType;
    }
    
    public Accessor<BeanT, ValueT> optimize(@Nullable final JAXBContextImpl context) {
        return this;
    }
    
    public abstract ValueT get(final BeanT p0) throws AccessorException;
    
    public abstract void set(final BeanT p0, final ValueT p1) throws AccessorException;
    
    public Object getUnadapted(final BeanT bean) throws AccessorException {
        return this.get(bean);
    }
    
    public boolean isAdapted() {
        return false;
    }
    
    public void setUnadapted(final BeanT bean, final Object value) throws AccessorException {
        this.set(bean, value);
    }
    
    @Override
    public void receive(final UnmarshallingContext.State state, final Object o) throws SAXException {
        try {
            this.set(state.getTarget(), o);
        }
        catch (final AccessorException e) {
            Loader.handleGenericException(e, true);
        }
        catch (final IllegalAccessError iae) {
            Loader.handleGenericError(iae);
        }
    }
    
    public boolean isValueTypeAbstractable() {
        return !Accessor.nonAbstractableClasses.contains(this.getValueType());
    }
    
    public boolean isAbstractable(final Class clazz) {
        return !Accessor.nonAbstractableClasses.contains(clazz);
    }
    
    public final <T> Accessor<BeanT, T> adapt(final Class<T> targetType, final Class<? extends XmlAdapter<T, ValueT>> adapter) {
        return (Accessor<BeanT, T>)new AdaptedAccessor((Class<Object>)targetType, (Accessor<Object, Object>)this, (Class<? extends XmlAdapter<Object, Object>>)adapter);
    }
    
    public final <T> Accessor<BeanT, T> adapt(final Adapter<Type, Class> adapter) {
        return (Accessor<BeanT, T>)new AdaptedAccessor(Utils.REFLECTION_NAVIGATOR.erasure(adapter.defaultType), (Accessor<Object, Object>)this, adapter.adapterType);
    }
    
    public static <A, B> Accessor<A, B> getErrorInstance() {
        return Accessor.ERROR;
    }
    
    static {
        Accessor.nonAbstractableClasses = (List<Class>)Arrays.asList(Object.class, Calendar.class, Duration.class, XMLGregorianCalendar.class, Image.class, DataHandler.class, Source.class, Date.class, File.class, URI.class, URL.class, Class.class, String.class, Source.class);
        Accessor.accessWarned = false;
        ERROR = new Accessor<Object, Object>(Object.class) {
            @Override
            public Object get(final Object o) {
                return null;
            }
            
            @Override
            public void set(final Object o, final Object o1) {
            }
        };
        JAXB_ELEMENT_VALUE = new Accessor<JAXBElement, Object>(Object.class) {
            @Override
            public Object get(final JAXBElement jaxbElement) {
                return jaxbElement.getValue();
            }
            
            @Override
            public void set(final JAXBElement jaxbElement, final Object o) {
                jaxbElement.setValue(o);
            }
        };
        (uninitializedValues = new HashMap<Class, Object>()).put(Byte.TYPE, 0);
        Accessor.uninitializedValues.put(Boolean.TYPE, false);
        Accessor.uninitializedValues.put(Character.TYPE, '\0');
        Accessor.uninitializedValues.put(Float.TYPE, 0.0f);
        Accessor.uninitializedValues.put(Double.TYPE, 0.0);
        Accessor.uninitializedValues.put(Integer.TYPE, 0);
        Accessor.uninitializedValues.put(Long.TYPE, 0L);
        Accessor.uninitializedValues.put(Short.TYPE, 0);
    }
    
    public static class FieldReflection<BeanT, ValueT> extends Accessor<BeanT, ValueT>
    {
        public final Field f;
        private static final Logger logger;
        
        public FieldReflection(final Field f) {
            this(f, false);
        }
        
        public FieldReflection(final Field f, final boolean supressAccessorWarnings) {
            super(f.getType());
            this.f = f;
            final int mod = f.getModifiers();
            if (Modifier.isPublic(mod) && !Modifier.isFinal(mod)) {
                if (Modifier.isPublic(f.getDeclaringClass().getModifiers())) {
                    return;
                }
            }
            try {
                f.setAccessible(true);
            }
            catch (final SecurityException e) {
                if (!Accessor.accessWarned && !supressAccessorWarnings) {
                    FieldReflection.logger.log(Level.WARNING, Messages.UNABLE_TO_ACCESS_NON_PUBLIC_FIELD.format(f.getDeclaringClass().getName(), f.getName()), e);
                }
                Accessor.accessWarned = true;
            }
        }
        
        @Override
        public ValueT get(final BeanT bean) {
            try {
                return (ValueT)this.f.get(bean);
            }
            catch (final IllegalAccessException e) {
                throw new IllegalAccessError(e.getMessage());
            }
        }
        
        @Override
        public void set(final BeanT bean, ValueT value) {
            try {
                if (value == null) {
                    value = Accessor.uninitializedValues.get(this.valueType);
                }
                this.f.set(bean, value);
            }
            catch (final IllegalAccessException e) {
                throw new IllegalAccessError(e.getMessage());
            }
        }
        
        @Override
        public Accessor<BeanT, ValueT> optimize(final JAXBContextImpl context) {
            if (context != null && context.fastBoot) {
                return this;
            }
            final Accessor<BeanT, ValueT> acc = OptimizedAccessorFactory.get(this.f);
            if (acc != null) {
                return acc;
            }
            return this;
        }
        
        static {
            logger = Util.getClassLogger();
        }
    }
    
    public static final class ReadOnlyFieldReflection<BeanT, ValueT> extends FieldReflection<BeanT, ValueT>
    {
        public ReadOnlyFieldReflection(final Field f, final boolean supressAccessorWarnings) {
            super(f, supressAccessorWarnings);
        }
        
        public ReadOnlyFieldReflection(final Field f) {
            super(f);
        }
        
        @Override
        public void set(final BeanT bean, final ValueT value) {
        }
        
        @Override
        public Accessor<BeanT, ValueT> optimize(final JAXBContextImpl context) {
            return this;
        }
    }
    
    public static class GetterSetterReflection<BeanT, ValueT> extends Accessor<BeanT, ValueT>
    {
        public final Method getter;
        public final Method setter;
        private static final Logger logger;
        
        public GetterSetterReflection(final Method getter, final Method setter) {
            super((Class<Object>)((getter != null) ? getter.getReturnType() : setter.getParameterTypes()[0]));
            this.getter = getter;
            this.setter = setter;
            if (getter != null) {
                this.makeAccessible(getter);
            }
            if (setter != null) {
                this.makeAccessible(setter);
            }
        }
        
        private void makeAccessible(final Method m) {
            if (Modifier.isPublic(m.getModifiers())) {
                if (Modifier.isPublic(m.getDeclaringClass().getModifiers())) {
                    return;
                }
            }
            try {
                m.setAccessible(true);
            }
            catch (final SecurityException e) {
                if (!Accessor.accessWarned) {
                    GetterSetterReflection.logger.log(Level.WARNING, Messages.UNABLE_TO_ACCESS_NON_PUBLIC_FIELD.format(m.getDeclaringClass().getName(), m.getName()), e);
                }
                Accessor.accessWarned = true;
            }
        }
        
        @Override
        public ValueT get(final BeanT bean) throws AccessorException {
            try {
                return (ValueT)this.getter.invoke(bean, new Object[0]);
            }
            catch (final IllegalAccessException e) {
                throw new IllegalAccessError(e.getMessage());
            }
            catch (final InvocationTargetException e2) {
                throw this.handleInvocationTargetException(e2);
            }
        }
        
        @Override
        public void set(final BeanT bean, ValueT value) throws AccessorException {
            try {
                if (value == null) {
                    value = Accessor.uninitializedValues.get(this.valueType);
                }
                this.setter.invoke(bean, value);
            }
            catch (final IllegalAccessException e) {
                throw new IllegalAccessError(e.getMessage());
            }
            catch (final InvocationTargetException e2) {
                throw this.handleInvocationTargetException(e2);
            }
        }
        
        private AccessorException handleInvocationTargetException(final InvocationTargetException e) {
            final Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            return new AccessorException(t);
        }
        
        @Override
        public Accessor<BeanT, ValueT> optimize(final JAXBContextImpl context) {
            if (this.getter == null || this.setter == null) {
                return this;
            }
            if (context != null && context.fastBoot) {
                return this;
            }
            final Accessor<BeanT, ValueT> acc = OptimizedAccessorFactory.get(this.getter, this.setter);
            if (acc != null) {
                return acc;
            }
            return this;
        }
        
        static {
            logger = Util.getClassLogger();
        }
    }
    
    public static class GetterOnlyReflection<BeanT, ValueT> extends GetterSetterReflection<BeanT, ValueT>
    {
        public GetterOnlyReflection(final Method getter) {
            super(getter, null);
        }
        
        @Override
        public void set(final BeanT bean, final ValueT value) throws AccessorException {
            throw new AccessorException(Messages.NO_SETTER.format(this.getter.toString()));
        }
    }
    
    public static class SetterOnlyReflection<BeanT, ValueT> extends GetterSetterReflection<BeanT, ValueT>
    {
        public SetterOnlyReflection(final Method setter) {
            super(null, setter);
        }
        
        @Override
        public ValueT get(final BeanT bean) throws AccessorException {
            throw new AccessorException(Messages.NO_GETTER.format(this.setter.toString()));
        }
    }
}
