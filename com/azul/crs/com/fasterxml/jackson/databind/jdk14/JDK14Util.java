package com.azul.crs.com.fasterxml.jackson.databind.jdk14;

import com.azul.crs.com.fasterxml.jackson.annotation.JsonCreator;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.Annotated;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.util.Iterator;
import java.util.Collections;
import com.azul.crs.com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Method;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;

public class JDK14Util
{
    public static String[] getRecordFieldNames(final Class<?> recordType) {
        return RecordAccessor.instance().getRecordFieldNames(recordType);
    }
    
    public static AnnotatedConstructor findRecordConstructor(final DeserializationContext ctxt, final BeanDescription beanDesc, final List<String> names) {
        return new CreatorLocator(ctxt, beanDesc).locate(names);
    }
    
    static class RecordAccessor
    {
        private final Method RECORD_GET_RECORD_COMPONENTS;
        private final Method RECORD_COMPONENT_GET_NAME;
        private final Method RECORD_COMPONENT_GET_TYPE;
        private static final RecordAccessor INSTANCE;
        private static final RuntimeException PROBLEM;
        
        private RecordAccessor() throws RuntimeException {
            try {
                this.RECORD_GET_RECORD_COMPONENTS = Class.class.getMethod("getRecordComponents", (Class<?>[])new Class[0]);
                final Class<?> c = Class.forName("java.lang.reflect.RecordComponent");
                this.RECORD_COMPONENT_GET_NAME = c.getMethod("getName", (Class<?>[])new Class[0]);
                this.RECORD_COMPONENT_GET_TYPE = c.getMethod("getType", (Class<?>[])new Class[0]);
            }
            catch (final Exception e) {
                throw new RuntimeException(String.format("Failed to access Methods needed to support `java.lang.Record`: (%s) %s", e.getClass().getName(), e.getMessage()), e);
            }
        }
        
        public static RecordAccessor instance() {
            if (RecordAccessor.PROBLEM != null) {
                throw RecordAccessor.PROBLEM;
            }
            return RecordAccessor.INSTANCE;
        }
        
        public String[] getRecordFieldNames(final Class<?> recordType) throws IllegalArgumentException {
            final Object[] components = this.recordComponents(recordType);
            final String[] names = new String[components.length];
            for (int i = 0; i < components.length; ++i) {
                try {
                    names[i] = (String)this.RECORD_COMPONENT_GET_NAME.invoke(components[i], new Object[0]);
                }
                catch (final Exception e) {
                    throw new IllegalArgumentException(String.format("Failed to access name of field #%d (of %d) of Record type %s", i, components.length, ClassUtil.nameOf(recordType)), e);
                }
            }
            return names;
        }
        
        public RawTypeName[] getRecordFields(final Class<?> recordType) throws IllegalArgumentException {
            final Object[] components = this.recordComponents(recordType);
            final RawTypeName[] results = new RawTypeName[components.length];
            for (int i = 0; i < components.length; ++i) {
                String name;
                try {
                    name = (String)this.RECORD_COMPONENT_GET_NAME.invoke(components[i], new Object[0]);
                }
                catch (final Exception e) {
                    throw new IllegalArgumentException(String.format("Failed to access name of field #%d (of %d) of Record type %s", i, components.length, ClassUtil.nameOf(recordType)), e);
                }
                Class<?> type;
                try {
                    type = (Class)this.RECORD_COMPONENT_GET_TYPE.invoke(components[i], new Object[0]);
                }
                catch (final Exception e2) {
                    throw new IllegalArgumentException(String.format("Failed to access type of field #%d (of %d) of Record type %s", i, components.length, ClassUtil.nameOf(recordType)), e2);
                }
                results[i] = new RawTypeName(type, name);
            }
            return results;
        }
        
        protected Object[] recordComponents(final Class<?> recordType) throws IllegalArgumentException {
            try {
                return (Object[])this.RECORD_GET_RECORD_COMPONENTS.invoke(recordType, new Object[0]);
            }
            catch (final Exception e) {
                throw new IllegalArgumentException("Failed to access RecordComponents of type " + ClassUtil.nameOf(recordType));
            }
        }
        
        static {
            RuntimeException prob = null;
            RecordAccessor inst = null;
            try {
                inst = new RecordAccessor();
            }
            catch (final RuntimeException e) {
                prob = e;
            }
            INSTANCE = inst;
            PROBLEM = prob;
        }
    }
    
    static class RawTypeName
    {
        public final Class<?> rawType;
        public final String name;
        
        public RawTypeName(final Class<?> rt, final String n) {
            this.rawType = rt;
            this.name = n;
        }
    }
    
    static class CreatorLocator
    {
        protected final BeanDescription _beanDesc;
        protected final DeserializationConfig _config;
        protected final AnnotationIntrospector _intr;
        protected final List<AnnotatedConstructor> _constructors;
        protected final AnnotatedConstructor _primaryConstructor;
        protected final RawTypeName[] _recordFields;
        
        CreatorLocator(final DeserializationContext ctxt, final BeanDescription beanDesc) {
            this._beanDesc = beanDesc;
            this._intr = ctxt.getAnnotationIntrospector();
            this._config = ctxt.getConfig();
            this._recordFields = RecordAccessor.instance().getRecordFields(beanDesc.getBeanClass());
            final int argCount = this._recordFields.length;
            AnnotatedConstructor primary = null;
            if (argCount == 0) {
                primary = beanDesc.findDefaultConstructor();
                this._constructors = Collections.singletonList(primary);
            }
            else {
                this._constructors = beanDesc.getConstructors();
            Label_0089:
                for (final AnnotatedConstructor ctor : this._constructors) {
                    if (ctor.getParameterCount() != argCount) {
                        continue;
                    }
                    for (int i = 0; i < argCount; ++i) {
                        if (!ctor.getRawParameterType(i).equals(this._recordFields[i].rawType)) {
                            continue Label_0089;
                        }
                    }
                    primary = ctor;
                    break;
                }
            }
            if (primary == null) {
                throw new IllegalArgumentException("Failed to find the canonical Record constructor of type " + ClassUtil.getTypeDescription(this._beanDesc.getType()));
            }
            this._primaryConstructor = primary;
        }
        
        public AnnotatedConstructor locate(final List<String> names) {
            for (final AnnotatedConstructor ctor : this._constructors) {
                final JsonCreator.Mode creatorMode = this._intr.findCreatorAnnotation(this._config, ctor);
                if (null != creatorMode) {
                    if (JsonCreator.Mode.DISABLED == creatorMode) {
                        continue;
                    }
                    if (JsonCreator.Mode.DELEGATING == creatorMode) {
                        return null;
                    }
                    if (ctor != this._primaryConstructor) {
                        return null;
                    }
                    continue;
                }
            }
            for (final RawTypeName field : this._recordFields) {
                names.add(field.name);
            }
            return this._primaryConstructor;
        }
    }
}
