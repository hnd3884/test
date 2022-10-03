package org.msgpack.template.builder;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.template.FieldList;
import java.lang.reflect.Array;
import org.msgpack.template.ObjectArrayTemplate;
import org.msgpack.template.DoubleArrayTemplate;
import org.msgpack.template.FloatArrayTemplate;
import org.msgpack.template.LongArrayTemplate;
import org.msgpack.template.IntegerArrayTemplate;
import org.msgpack.template.ShortArrayTemplate;
import org.msgpack.template.BooleanArrayTemplate;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.GenericArrayType;
import org.msgpack.template.Template;
import java.util.logging.Level;
import java.lang.reflect.Type;
import org.msgpack.template.TemplateRegistry;
import java.util.logging.Logger;

public class ArrayTemplateBuilder extends AbstractTemplateBuilder
{
    private static final Logger LOG;
    
    public ArrayTemplateBuilder(final TemplateRegistry registry) {
        super(registry);
    }
    
    @Override
    public boolean matchType(final Type targetType, final boolean forceBuild) {
        final Class<?> targetClass = (Class<?>)targetType;
        final boolean matched = AbstractTemplateBuilder.matchAtArrayTemplateBuilder(targetClass, false);
        if (matched && ArrayTemplateBuilder.LOG.isLoggable(Level.FINE)) {
            ArrayTemplateBuilder.LOG.fine("matched type: " + targetClass.getName());
        }
        return matched;
    }
    
    @Override
    public <T> Template<T> buildTemplate(final Type arrayType) {
        int dim = 1;
        Type baseType;
        Class<?> baseClass;
        if (arrayType instanceof GenericArrayType) {
            final GenericArrayType type = (GenericArrayType)arrayType;
            for (baseType = type.getGenericComponentType(); baseType instanceof GenericArrayType; baseType = ((GenericArrayType)baseType).getGenericComponentType(), ++dim) {}
            if (baseType instanceof ParameterizedType) {
                baseClass = (Class)((ParameterizedType)baseType).getRawType();
            }
            else {
                baseClass = (Class)baseType;
            }
        }
        else {
            final Class<?> type2 = (Class<?>)arrayType;
            for (baseClass = type2.getComponentType(); baseClass.isArray(); baseClass = baseClass.getComponentType(), ++dim) {}
            baseType = baseClass;
        }
        return this.toTemplate(arrayType, baseType, baseClass, dim);
    }
    
    private Template toTemplate(final Type arrayType, final Type genericBaseType, final Class baseClass, final int dim) {
        if (dim == 1) {
            if (baseClass == Boolean.TYPE) {
                return BooleanArrayTemplate.getInstance();
            }
            if (baseClass == Short.TYPE) {
                return ShortArrayTemplate.getInstance();
            }
            if (baseClass == Integer.TYPE) {
                return IntegerArrayTemplate.getInstance();
            }
            if (baseClass == Long.TYPE) {
                return LongArrayTemplate.getInstance();
            }
            if (baseClass == Float.TYPE) {
                return FloatArrayTemplate.getInstance();
            }
            if (baseClass == Double.TYPE) {
                return DoubleArrayTemplate.getInstance();
            }
            final Template baseTemplate = this.registry.lookup(genericBaseType);
            return new ObjectArrayTemplate(baseClass, baseTemplate);
        }
        else {
            if (dim == 2) {
                final Class componentClass = Array.newInstance(baseClass, 0).getClass();
                final Template componentTemplate = this.toTemplate(arrayType, genericBaseType, baseClass, dim - 1);
                return new ReflectionMultidimentionalArrayTemplate(componentClass, componentTemplate);
            }
            final ReflectionMultidimentionalArrayTemplate componentTemplate2 = (ReflectionMultidimentionalArrayTemplate)this.toTemplate(arrayType, genericBaseType, baseClass, dim - 1);
            final Class componentClass2 = Array.newInstance(componentTemplate2.getComponentClass(), 0).getClass();
            return new ReflectionMultidimentionalArrayTemplate(componentClass2, componentTemplate2);
        }
    }
    
    @Override
    public <T> Template<T> buildTemplate(final Class<T> targetClass, final FieldList flist) throws TemplateBuildException {
        throw new UnsupportedOperationException(targetClass.getName());
    }
    
    @Override
    protected <T> Template<T> buildTemplate(final Class<T> targetClass, final FieldEntry[] entries) {
        throw new UnsupportedOperationException(targetClass.getName());
    }
    
    @Override
    public void writeTemplate(final Type targetType, final String directoryName) {
        throw new UnsupportedOperationException(targetType.toString());
    }
    
    @Override
    public <T> Template<T> loadTemplate(final Type targetType) {
        return null;
    }
    
    static {
        LOG = Logger.getLogger(ArrayTemplateBuilder.class.getName());
    }
    
    static class ReflectionMultidimentionalArrayTemplate extends AbstractTemplate
    {
        private Class componentClass;
        private Template componentTemplate;
        
        public ReflectionMultidimentionalArrayTemplate(final Class componentClass, final Template componentTemplate) {
            this.componentClass = componentClass;
            this.componentTemplate = componentTemplate;
        }
        
        Class getComponentClass() {
            return this.componentClass;
        }
        
        @Override
        public void write(final Packer packer, final Object v, final boolean required) throws IOException {
            if (v == null) {
                if (required) {
                    throw new MessageTypeException("Attempted to write null");
                }
                packer.writeNil();
            }
            else {
                if (!(v instanceof Object[]) || !this.componentClass.isAssignableFrom(v.getClass().getComponentType())) {
                    throw new MessageTypeException();
                }
                final Object[] array = (Object[])v;
                final int length = array.length;
                packer.writeArrayBegin(length);
                for (int i = 0; i < length; ++i) {
                    this.componentTemplate.write(packer, array[i], required);
                }
                packer.writeArrayEnd();
            }
        }
        
        @Override
        public Object read(final Unpacker unpacker, final Object to, final boolean required) throws IOException {
            if (!required && unpacker.trySkipNil()) {
                return null;
            }
            final int length = unpacker.readArrayBegin();
            final Object[] array = (Object[])Array.newInstance(this.componentClass, length);
            for (int i = 0; i < length; ++i) {
                array[i] = this.componentTemplate.read(unpacker, null, required);
            }
            unpacker.readArrayEnd();
            return array;
        }
    }
}
