package org.msgpack.template.builder;

import org.msgpack.annotation.MessagePackOrdinalEnum;
import org.msgpack.annotation.OrdinalEnum;
import java.lang.reflect.GenericArrayType;
import org.msgpack.annotation.MessagePackBeans;
import org.msgpack.annotation.Beans;
import org.msgpack.annotation.Index;
import org.msgpack.annotation.NotNullable;
import org.msgpack.annotation.Optional;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import org.msgpack.annotation.Ignore;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.msgpack.annotation.MessagePackMessage;
import org.msgpack.annotation.Message;
import javassist.Modifier;
import org.msgpack.template.FieldList;
import org.msgpack.template.FieldOption;
import org.msgpack.template.Template;
import java.lang.reflect.Type;
import org.msgpack.template.TemplateRegistry;

public abstract class AbstractTemplateBuilder implements TemplateBuilder
{
    protected TemplateRegistry registry;
    
    protected AbstractTemplateBuilder(final TemplateRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public <T> Template<T> buildTemplate(final Type targetType) throws TemplateBuildException {
        final Class<T> targetClass = (Class<T>)targetType;
        this.checkClassValidation(targetClass);
        final FieldOption fieldOption = this.getFieldOption(targetClass);
        final FieldEntry[] entries = this.toFieldEntries(targetClass, fieldOption);
        return this.buildTemplate(targetClass, entries);
    }
    
    @Override
    public <T> Template<T> buildTemplate(final Class<T> targetClass, final FieldList fieldList) throws TemplateBuildException {
        this.checkClassValidation(targetClass);
        final FieldEntry[] entries = this.toFieldEntries(targetClass, fieldList);
        return this.buildTemplate(targetClass, entries);
    }
    
    protected abstract <T> Template<T> buildTemplate(final Class<T> p0, final FieldEntry[] p1);
    
    protected void checkClassValidation(final Class<?> targetClass) {
        if (Modifier.isAbstract(targetClass.getModifiers())) {
            throw new TemplateBuildException("Cannot build template for abstract class: " + targetClass.getName());
        }
        if (targetClass.isInterface()) {
            throw new TemplateBuildException("Cannot build template for interface: " + targetClass.getName());
        }
        if (targetClass.isArray()) {
            throw new TemplateBuildException("Cannot build template for array class: " + targetClass.getName());
        }
        if (targetClass.isPrimitive()) {
            throw new TemplateBuildException("Cannot build template of primitive type: " + targetClass.getName());
        }
    }
    
    protected FieldOption getFieldOption(final Class<?> targetClass) {
        final Message m = targetClass.getAnnotation(Message.class);
        if (m == null) {
            return FieldOption.DEFAULT;
        }
        final MessagePackMessage mpm = targetClass.getAnnotation(MessagePackMessage.class);
        if (mpm == null) {
            return FieldOption.DEFAULT;
        }
        return m.value();
    }
    
    private FieldEntry[] toFieldEntries(final Class<?> targetClass, final FieldList flist) {
        final List<FieldList.Entry> src = flist.getList();
        final FieldEntry[] entries = new FieldEntry[src.size()];
        for (int i = 0; i < src.size(); ++i) {
            final FieldList.Entry s = src.get(i);
            if (s.isAvailable()) {
                try {
                    entries[i] = new DefaultFieldEntry(targetClass.getDeclaredField(s.getName()), s.getOption());
                    continue;
                }
                catch (final SecurityException e) {
                    throw new TemplateBuildException(e);
                }
                catch (final NoSuchFieldException e2) {
                    throw new TemplateBuildException(e2);
                }
            }
            entries[i] = new DefaultFieldEntry();
        }
        return entries;
    }
    
    protected FieldEntry[] toFieldEntries(final Class<?> targetClass, final FieldOption from) {
        final Field[] fields = this.getFields(targetClass);
        final List<FieldEntry> indexed = new ArrayList<FieldEntry>();
        int maxIndex = -1;
        for (final Field f : fields) {
            final FieldOption opt = this.getFieldOption(f, from);
            if (opt != FieldOption.IGNORE) {
                final int index = this.getFieldIndex(f, maxIndex);
                if (indexed.size() > index && indexed.get(index) != null) {
                    throw new TemplateBuildException("duplicated index: " + index);
                }
                if (index < 0) {
                    throw new TemplateBuildException("invalid index: " + index);
                }
                while (indexed.size() <= index) {
                    indexed.add(null);
                }
                indexed.set(index, new DefaultFieldEntry(f, opt));
                if (maxIndex < index) {
                    maxIndex = index;
                }
            }
        }
        final FieldEntry[] entries = new FieldEntry[maxIndex + 1];
        for (int i = 0; i < indexed.size(); ++i) {
            final FieldEntry e = indexed.get(i);
            if (e == null) {
                entries[i] = new DefaultFieldEntry();
            }
            else {
                entries[i] = e;
            }
        }
        return entries;
    }
    
    private Field[] getFields(final Class<?> targetClass) {
        final List<Field[]> succ = new ArrayList<Field[]>();
        int total = 0;
        for (Class<?> c = targetClass; c != Object.class; c = c.getSuperclass()) {
            final Field[] fields = c.getDeclaredFields();
            total += fields.length;
            succ.add(fields);
        }
        final Field[] result = new Field[total];
        int off = 0;
        for (int i = succ.size() - 1; i >= 0; --i) {
            final Field[] fields2 = succ.get(i);
            System.arraycopy(fields2, 0, result, off, fields2.length);
            off += fields2.length;
        }
        return result;
    }
    
    private FieldOption getFieldOption(final Field field, final FieldOption from) {
        final int mod = field.getModifiers();
        if (java.lang.reflect.Modifier.isStatic(mod) || java.lang.reflect.Modifier.isFinal(mod) || java.lang.reflect.Modifier.isTransient(mod)) {
            return FieldOption.IGNORE;
        }
        if (isAnnotated(field, Ignore.class)) {
            return FieldOption.IGNORE;
        }
        if (isAnnotated(field, Optional.class)) {
            return FieldOption.OPTIONAL;
        }
        if (isAnnotated(field, NotNullable.class)) {
            return FieldOption.NOTNULLABLE;
        }
        if (from != FieldOption.DEFAULT) {
            return from;
        }
        if (field.getType().isPrimitive()) {
            return FieldOption.NOTNULLABLE;
        }
        return FieldOption.OPTIONAL;
    }
    
    private int getFieldIndex(final Field field, final int maxIndex) {
        final Index a = field.getAnnotation(Index.class);
        if (a == null) {
            return maxIndex + 1;
        }
        return a.value();
    }
    
    @Override
    public void writeTemplate(final Type targetType, final String directoryName) {
        throw new UnsupportedOperationException(targetType.toString());
    }
    
    @Override
    public <T> Template<T> loadTemplate(final Type targetType) {
        return null;
    }
    
    public static boolean isAnnotated(final Class<?> targetClass, final Class<? extends Annotation> with) {
        return targetClass.getAnnotation(with) != null;
    }
    
    public static boolean isAnnotated(final AccessibleObject accessibleObject, final Class<? extends Annotation> with) {
        return accessibleObject.getAnnotation(with) != null;
    }
    
    public static boolean matchAtClassTemplateBuilder(final Class<?> targetClass, final boolean hasAnnotation) {
        if (hasAnnotation) {
            return isAnnotated(targetClass, Message.class) || isAnnotated(targetClass, MessagePackMessage.class);
        }
        return !targetClass.isEnum() && !targetClass.isInterface();
    }
    
    public static boolean matchAtBeansClassTemplateBuilder(final Type targetType, final boolean hasAnnotation) {
        final Class<?> targetClass = (Class<?>)targetType;
        if (hasAnnotation) {
            return isAnnotated((Class<?>)targetType, Beans.class) || isAnnotated((Class<?>)targetType, MessagePackBeans.class);
        }
        return !targetClass.isEnum() || !targetClass.isInterface();
    }
    
    public static boolean matchAtArrayTemplateBuilder(final Class<?> targetClass, final boolean hasAnnotation) {
        return targetClass instanceof GenericArrayType || targetClass.isArray();
    }
    
    public static boolean matchAtOrdinalEnumTemplateBuilder(final Class<?> targetClass, final boolean hasAnnotation) {
        if (hasAnnotation) {
            return isAnnotated(targetClass, OrdinalEnum.class) || isAnnotated(targetClass, MessagePackOrdinalEnum.class);
        }
        return targetClass.isEnum();
    }
}
