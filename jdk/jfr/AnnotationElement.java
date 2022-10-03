package jdk.jfr;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import jdk.jfr.internal.TypeLibrary;
import java.util.HashMap;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import jdk.jfr.internal.Utils;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.Objects;
import java.util.List;
import jdk.jfr.internal.Type;
import jdk.Exported;

@Exported
public final class AnnotationElement
{
    private final Type type;
    private final List<Object> annotationValues;
    private final List<String> annotationNames;
    private final boolean inBootClassLoader;
    
    AnnotationElement(final Type type, final List<Object> list, final boolean inBootClassLoader) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(list);
        this.type = type;
        if (list.size() != type.getFields().size()) {
            final StringJoiner stringJoiner = new StringJoiner(",", "[", "]");
            final Iterator<ValueDescriptor> iterator = type.getFields().iterator();
            while (iterator.hasNext()) {
                stringJoiner.add(iterator.next().getName());
            }
            final StringJoiner stringJoiner2 = new StringJoiner(",", "[", "]");
            final Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                stringJoiner.add(String.valueOf(iterator2.next()));
            }
            throw new IllegalArgumentException("Annotation " + stringJoiner + " for " + type.getName() + " doesn't match number of values " + stringJoiner2);
        }
        final ArrayList list2 = new ArrayList();
        final ArrayList list3 = new ArrayList();
        int n = 0;
        for (final ValueDescriptor valueDescriptor : type.getFields()) {
            final Object value = list.get(n);
            if (value == null) {
                throw new IllegalArgumentException("Annotation value can't be null");
            }
            Class<?> clazz = value.getClass();
            if (valueDescriptor.isArray()) {
                clazz = clazz.getComponentType();
            }
            checkType(Utils.unboxType(clazz));
            list2.add(valueDescriptor.getName());
            list3.add(value);
            ++n;
        }
        this.annotationValues = Utils.smallUnmodifiable((List<Object>)list3);
        this.annotationNames = Utils.smallUnmodifiable((List<String>)list2);
        this.inBootClassLoader = inBootClassLoader;
    }
    
    public AnnotationElement(final Class<? extends Annotation> clazz, final Map<String, Object> map) {
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(map);
        Utils.checkRegisterPermission();
        final HashMap hashMap = new HashMap((Map<? extends K, ? extends V>)map);
        for (final Map.Entry entry : hashMap.entrySet()) {
            if (entry.getKey() == null) {
                throw new NullPointerException("Name of annotation method can't be null");
            }
            if (entry.getValue() == null) {
                throw new NullPointerException("Return value for annotation method can't be null");
            }
        }
        if (AnnotationElement.class.isAssignableFrom(clazz) && clazz.isInterface()) {
            throw new IllegalArgumentException("Must be interface extending " + Annotation.class.getName());
        }
        if (!isKnownJFRAnnotation(clazz) && clazz.getAnnotation(MetadataDefinition.class) == null) {
            throw new IllegalArgumentException("Annotation class must be annotated with jdk.jfr.MetadataDefinition to be valid");
        }
        if (isKnownJFRAnnotation(clazz)) {
            this.type = new Type(clazz.getCanonicalName(), Type.SUPER_TYPE_ANNOTATION, Type.getTypeId(clazz));
        }
        else {
            this.type = TypeLibrary.createAnnotationType(clazz);
        }
        final Method[] declaredMethods = clazz.getDeclaredMethods();
        if (declaredMethods.length != hashMap.size()) {
            throw new IllegalArgumentException("Number of declared methods must match size of value map");
        }
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        final HashSet set = new HashSet();
        final Method[] array = declaredMethods;
        for (int length = array.length, i = 0; i < length; ++i) {
            final String name = array[i].getName();
            final Object value = hashMap.get(name);
            if (value == null) {
                throw new IllegalArgumentException("No method in annotation interface " + clazz.getName() + " matching name " + name);
            }
            Class<?> clazz2 = ((String[])value).getClass();
            if (clazz2 == Class.class) {
                throw new IllegalArgumentException("Annotation value for " + name + " can't be class");
            }
            if (value instanceof Enum) {
                throw new IllegalArgumentException("Annotation value for " + name + " can't be enum");
            }
            if (!clazz2.equals(((String[])value).getClass())) {
                throw new IllegalArgumentException("Return type of annotation " + clazz2.getName() + " must match type of object" + ((String[])value).getClass());
            }
            if (clazz2.isArray()) {
                final Class<?> componentType = clazz2.getComponentType();
                checkType(componentType);
                if (componentType.equals(String.class)) {
                    final String[] array2 = (String[])value;
                    for (int j = 0; j < array2.length; ++j) {
                        if (array2[j] == null) {
                            throw new IllegalArgumentException("Annotation value for " + name + " contains null");
                        }
                    }
                }
            }
            else {
                clazz2 = Utils.unboxType(((String[])value).getClass());
                checkType(clazz2);
            }
            if (set.contains(name)) {
                throw new IllegalArgumentException("Value with name '" + name + "' already exists");
            }
            if (isKnownJFRAnnotation(clazz)) {
                this.type.add(new ValueDescriptor(clazz2, name, Collections.emptyList(), true));
            }
            list.add(name);
            list2.add(value);
        }
        this.annotationValues = Utils.smallUnmodifiable((List<Object>)list2);
        this.annotationNames = Utils.smallUnmodifiable((List<String>)list);
        this.inBootClassLoader = (clazz.getClassLoader() == null);
    }
    
    public AnnotationElement(final Class<? extends Annotation> clazz, final Object o) {
        this(clazz, Collections.singletonMap("value", (Object)Objects.requireNonNull(o)));
    }
    
    public AnnotationElement(final Class<? extends Annotation> clazz) {
        this(clazz, Collections.emptyMap());
    }
    
    public List<Object> getValues() {
        return this.annotationValues;
    }
    
    public List<ValueDescriptor> getValueDescriptors() {
        return Collections.unmodifiableList((List<? extends ValueDescriptor>)this.type.getFields());
    }
    
    public List<AnnotationElement> getAnnotationElements() {
        return this.type.getAnnotationElements();
    }
    
    public String getTypeName() {
        return this.type.getName();
    }
    
    public Object getValue(final String s) {
        Objects.requireNonNull(s);
        int n = 0;
        final Iterator<String> iterator = this.annotationNames.iterator();
        while (iterator.hasNext()) {
            if (s.equals(iterator.next())) {
                return this.annotationValues.get(n);
            }
            ++n;
        }
        final StringJoiner stringJoiner = new StringJoiner(",", "[", "]");
        final Iterator<ValueDescriptor> iterator2 = this.type.getFields().iterator();
        while (iterator2.hasNext()) {
            stringJoiner.add(iterator2.next().getName());
        }
        throw new IllegalArgumentException("No value with name '" + s + "'. Valid names are " + stringJoiner);
    }
    
    public boolean hasValue(final String s) {
        Objects.requireNonNull(s);
        final Iterator<String> iterator = this.annotationNames.iterator();
        while (iterator.hasNext()) {
            if (s.equals(iterator.next())) {
                return true;
            }
        }
        return false;
    }
    
    public final <A> A getAnnotation(final Class<? extends Annotation> clazz) {
        Objects.requireNonNull(clazz);
        return this.type.getAnnotation(clazz);
    }
    
    public long getTypeId() {
        return this.type.getId();
    }
    
    Type getType() {
        return this.type;
    }
    
    private static void checkType(final Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return;
        }
        if (clazz == String.class) {
            return;
        }
        throw new IllegalArgumentException("Only primitives types or java.lang.String are allowed");
    }
    
    private static boolean isKnownJFRAnnotation(final Class<? extends Annotation> clazz) {
        return clazz == Registered.class || clazz == Threshold.class || clazz == StackTrace.class || clazz == Period.class || clazz == Enabled.class;
    }
    
    boolean isInBoot() {
        return this.inBootClassLoader;
    }
}
