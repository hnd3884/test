package org.msgpack.template.builder;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.packer.Packer;
import java.lang.reflect.Modifier;
import org.msgpack.annotation.Index;
import org.msgpack.annotation.NotNullable;
import org.msgpack.annotation.Optional;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import org.msgpack.annotation.Ignore;
import java.lang.reflect.Method;
import org.msgpack.template.builder.beans.BeanInfo;
import org.msgpack.template.builder.beans.PropertyDescriptor;
import java.util.ArrayList;
import org.msgpack.template.builder.beans.IntrospectionException;
import org.msgpack.template.builder.beans.Introspector;
import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import java.util.logging.Level;
import java.lang.reflect.Type;
import org.msgpack.template.TemplateRegistry;
import java.util.logging.Logger;

public class ReflectionBeansTemplateBuilder extends ReflectionTemplateBuilder
{
    private static Logger LOG;
    
    public ReflectionBeansTemplateBuilder(final TemplateRegistry registry) {
        super(registry);
    }
    
    @Override
    public boolean matchType(final Type targetType, final boolean hasAnnotation) {
        final Class<?> targetClass = (Class<?>)targetType;
        final boolean matched = AbstractTemplateBuilder.matchAtBeansClassTemplateBuilder(targetClass, hasAnnotation);
        if (matched && ReflectionBeansTemplateBuilder.LOG.isLoggable(Level.FINE)) {
            ReflectionBeansTemplateBuilder.LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }
    
    @Override
    protected ReflectionFieldTemplate[] toTemplates(final FieldEntry[] entries) {
        final ReflectionFieldTemplate[] tmpls = new ReflectionFieldTemplate[entries.length];
        for (int i = 0; i < entries.length; ++i) {
            final FieldEntry e = entries[i];
            final Class<?> type = e.getType();
            if (type.isPrimitive()) {
                tmpls[i] = new ReflectionBeansFieldTemplate(e);
            }
            else {
                final Template tmpl = this.registry.lookup(e.getGenericType());
                tmpls[i] = new FieldTemplateImpl(e, tmpl);
            }
        }
        return tmpls;
    }
    
    public FieldEntry[] toFieldEntries(final Class<?> targetClass, final FieldOption implicitOption) {
        BeanInfo desc;
        try {
            desc = Introspector.getBeanInfo(targetClass);
        }
        catch (final IntrospectionException e1) {
            throw new TemplateBuildException("Class must be java beans class:" + targetClass.getName());
        }
        PropertyDescriptor[] props = desc.getPropertyDescriptors();
        final ArrayList<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>();
        for (int i = 0; i < props.length; ++i) {
            final PropertyDescriptor pd = props[i];
            if (!this.isIgnoreProperty(pd)) {
                list.add(pd);
            }
        }
        props = new PropertyDescriptor[list.size()];
        list.toArray(props);
        final BeansFieldEntry[] entries = new BeansFieldEntry[props.length];
        for (int j = 0; j < props.length; ++j) {
            final PropertyDescriptor p = props[j];
            final int index = this.getPropertyIndex(p);
            if (index >= 0) {
                if (entries[index] != null) {
                    throw new TemplateBuildException("duplicated index: " + index);
                }
                if (index >= entries.length) {
                    throw new TemplateBuildException("invalid index: " + index);
                }
                entries[index] = new BeansFieldEntry(p);
                props[index] = null;
            }
        }
        int insertIndex = 0;
        for (int k = 0; k < props.length; ++k) {
            final PropertyDescriptor p2 = props[k];
            if (p2 != null) {
                while (entries[insertIndex] != null) {
                    ++insertIndex;
                }
                entries[insertIndex] = new BeansFieldEntry(p2);
            }
        }
        for (int k = 0; k < entries.length; ++k) {
            final BeansFieldEntry e2 = entries[k];
            final FieldOption op = this.getPropertyOption(e2, implicitOption);
            e2.setOption(op);
        }
        return entries;
    }
    
    private FieldOption getPropertyOption(final BeansFieldEntry e, final FieldOption implicitOption) {
        final FieldOption forGetter = this.getMethodOption(e.getPropertyDescriptor().getReadMethod());
        if (forGetter != FieldOption.DEFAULT) {
            return forGetter;
        }
        final FieldOption forSetter = this.getMethodOption(e.getPropertyDescriptor().getWriteMethod());
        if (forSetter != FieldOption.DEFAULT) {
            return forSetter;
        }
        return implicitOption;
    }
    
    private FieldOption getMethodOption(final Method method) {
        if (AbstractTemplateBuilder.isAnnotated(method, Ignore.class)) {
            return FieldOption.IGNORE;
        }
        if (AbstractTemplateBuilder.isAnnotated(method, Optional.class)) {
            return FieldOption.OPTIONAL;
        }
        if (AbstractTemplateBuilder.isAnnotated(method, NotNullable.class)) {
            return FieldOption.NOTNULLABLE;
        }
        return FieldOption.DEFAULT;
    }
    
    private int getPropertyIndex(final PropertyDescriptor desc) {
        final int getterIndex = this.getMethodIndex(desc.getReadMethod());
        if (getterIndex >= 0) {
            return getterIndex;
        }
        final int setterIndex = this.getMethodIndex(desc.getWriteMethod());
        return setterIndex;
    }
    
    private int getMethodIndex(final Method method) {
        final Index a = method.getAnnotation(Index.class);
        if (a == null) {
            return -1;
        }
        return a.value();
    }
    
    private boolean isIgnoreProperty(final PropertyDescriptor desc) {
        if (desc == null) {
            return true;
        }
        final Method getter = desc.getReadMethod();
        final Method setter = desc.getWriteMethod();
        return getter == null || setter == null || !Modifier.isPublic(getter.getModifiers()) || !Modifier.isPublic(setter.getModifiers()) || AbstractTemplateBuilder.isAnnotated(getter, Ignore.class) || AbstractTemplateBuilder.isAnnotated(setter, Ignore.class);
    }
    
    static {
        ReflectionBeansTemplateBuilder.LOG = Logger.getLogger(ReflectionBeansTemplateBuilder.class.getName());
    }
    
    static class ReflectionBeansFieldTemplate extends ReflectionFieldTemplate
    {
        ReflectionBeansFieldTemplate(final FieldEntry entry) {
            super(entry);
        }
        
        @Override
        public void write(final Packer packer, final Object v, final boolean required) throws IOException {
            packer.write(v);
        }
        
        @Override
        public Object read(final Unpacker unpacker, final Object to, final boolean required) throws IOException {
            final Object o = unpacker.read((Class<Object>)this.entry.getType());
            this.entry.set(to, o);
            return o;
        }
    }
}
