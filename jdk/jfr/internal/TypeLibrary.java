package jdk.jfr.internal;

import java.util.LinkedHashMap;
import java.util.HashSet;
import jdk.jfr.SettingDescriptor;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Modifier;
import jdk.jfr.Description;
import jdk.jfr.Label;
import java.lang.reflect.Field;
import java.util.HashMap;
import jdk.jfr.Event;
import jdk.jfr.Name;
import jdk.jfr.MetadataDefinition;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.io.IOException;
import java.util.Collections;
import jdk.jfr.Timespan;
import java.util.ArrayList;
import java.lang.annotation.Annotation;
import jdk.jfr.AnnotationElement;
import jdk.jfr.Timestamp;
import java.util.Collection;
import java.util.List;
import jdk.jfr.ValueDescriptor;
import java.util.Map;

public final class TypeLibrary
{
    private static TypeLibrary instance;
    private static final Map<Long, Type> types;
    static final ValueDescriptor DURATION_FIELD;
    static final ValueDescriptor THREAD_FIELD;
    static final ValueDescriptor STACK_TRACE_FIELD;
    static final ValueDescriptor START_TIME_FIELD;
    
    private TypeLibrary(final List<Type> list) {
        visitReachable(list, type -> !TypeLibrary.types.containsKey(type.getId()), type2 -> {
            final Type type3 = TypeLibrary.types.put(type2.getId(), type2);
            return;
        });
        if (Logger.shouldLog(LogTag.JFR_SYSTEM_METADATA, LogLevel.INFO)) {
            TypeLibrary.types.values().stream().sorted((type4, type5) -> Long.compare(type4.getId(), type5.getId())).forEach(type6 -> type6.log("Added", LogTag.JFR_SYSTEM_METADATA, LogLevel.INFO));
        }
    }
    
    private static ValueDescriptor createStartTimeField() {
        final List<AnnotationElement> standardAnnotations = createStandardAnnotations("Start Time", null);
        standardAnnotations.add(new AnnotationElement((Class<? extends Annotation>)Timestamp.class, "TICKS"));
        return PrivateAccess.getInstance().newValueDescriptor("startTime", Type.LONG, standardAnnotations, 0, false, "startTime");
    }
    
    private static ValueDescriptor createStackTraceField() {
        final ArrayList list = new ArrayList();
        return PrivateAccess.getInstance().newValueDescriptor("stackTrace", Type.STACK_TRACE, createStandardAnnotations("Stack Trace", "Stack Trace starting from the method the event was committed in"), 0, true, "stackTrace");
    }
    
    private static ValueDescriptor createThreadField() {
        final ArrayList list = new ArrayList();
        return PrivateAccess.getInstance().newValueDescriptor("eventThread", Type.THREAD, createStandardAnnotations("Event Thread", "Thread in which event was committed in"), 0, true, "eventThread");
    }
    
    private static ValueDescriptor createDurationField() {
        final ArrayList list = new ArrayList();
        final List<AnnotationElement> standardAnnotations = createStandardAnnotations("Duration", null);
        standardAnnotations.add(new AnnotationElement(Timespan.class, "TICKS"));
        return PrivateAccess.getInstance().newValueDescriptor("duration", Type.LONG, standardAnnotations, 0, false, "duration");
    }
    
    public static TypeLibrary getInstance() {
        synchronized (TypeLibrary.class) {
            if (TypeLibrary.instance == null) {
                List<Type> types;
                try {
                    types = MetadataHandler.createTypes();
                    Collections.sort((List<Object>)types, (type, type2) -> Long.compare(type.getId(), type2.getId()));
                }
                catch (final IOException ex) {
                    throw new Error("JFR: Could not read metadata");
                }
                TypeLibrary.instance = new TypeLibrary(types);
            }
            return TypeLibrary.instance;
        }
    }
    
    public List<Type> getTypes() {
        return new ArrayList<Type>(TypeLibrary.types.values());
    }
    
    public static Type createAnnotationType(final Class<? extends Annotation> clazz) {
        if (shouldPersist(clazz)) {
            final Type defineType = defineType(clazz, Type.SUPER_TYPE_ANNOTATION, false);
            if (defineType != null) {
                SecuritySupport.makeVisibleToJFR(clazz);
                for (final Method method : clazz.getDeclaredMethods()) {
                    defineType.add(PrivateAccess.getInstance().newValueDescriptor(method.getReturnType(), method.getName()));
                }
                final ArrayList<AnnotationElement> annotations = new ArrayList<AnnotationElement>();
                final Iterator<Annotation> iterator = resolveRepeatedAnnotations(clazz.getAnnotations()).iterator();
                while (iterator.hasNext()) {
                    final AnnotationElement annotation = createAnnotation(iterator.next());
                    if (annotation != null) {
                        annotations.add(annotation);
                    }
                }
                annotations.trimToSize();
                defineType.setAnnotations(annotations);
            }
            return getType(clazz);
        }
        return null;
    }
    
    static AnnotationElement createAnnotation(final Annotation annotation) {
        final Type annotationType = createAnnotationType(annotation.annotationType());
        if (annotationType != null) {
            final ArrayList list = new ArrayList();
            final Iterator<ValueDescriptor> iterator = annotationType.getFields().iterator();
            while (iterator.hasNext()) {
                list.add(invokeAnnotation(annotation, iterator.next().getName()));
            }
            return PrivateAccess.getInstance().newAnnotation(annotationType, list, annotation.annotationType().getClassLoader() == null);
        }
        return null;
    }
    
    private static Object invokeAnnotation(final Annotation annotation, final String s) {
        Method method;
        try {
            method = annotation.getClass().getMethod(s, (Class<?>[])new Class[0]);
        }
        catch (final NoSuchMethodException ex) {
            throw new InternalError("Could not loacate method " + s + " in annotation " + annotation.getClass().getName());
        }
        SecuritySupport.setAccessible(method);
        try {
            return method.invoke(annotation, new Object[0]);
        }
        catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException ex2) {
            throw new InternalError("Could not get value for method " + s + " in annotation " + annotation.getClass().getName());
        }
    }
    
    private static boolean shouldPersist(final Class<? extends Annotation> clazz) {
        return clazz != MetadataDefinition.class && clazz.getAnnotation(MetadataDefinition.class) != null;
    }
    
    private static boolean isDefined(final Class<?> clazz) {
        return TypeLibrary.types.containsKey(Type.getTypeId(clazz));
    }
    
    private static Type getType(final Class<?> clazz) {
        return TypeLibrary.types.get(Type.getTypeId(clazz));
    }
    
    private static Type defineType(final Class<?> clazz, final String s, final boolean b) {
        if (!isDefined(clazz)) {
            final Name name = clazz.getAnnotation(Name.class);
            final String s2 = (name != null) ? name.value() : clazz.getName();
            final long typeId = Type.getTypeId(clazz);
            Type type;
            if (b) {
                type = new PlatformEventType(s2, typeId, clazz.getClassLoader() == null, true);
            }
            else {
                type = new Type(s2, s, typeId);
            }
            TypeLibrary.types.put(type.getId(), type);
            return type;
        }
        return null;
    }
    
    public static Type createType(final Class<?> clazz) {
        return createType(clazz, Collections.emptyList(), Collections.emptyList());
    }
    
    public static Type createType(final Class<?> clazz, final List<AnnotationElement> list, final List<ValueDescriptor> list2) {
        if (Thread.class == clazz) {
            return Type.THREAD;
        }
        if (Class.class.isAssignableFrom(clazz)) {
            return Type.CLASS;
        }
        if (String.class.equals(clazz)) {
            return Type.STRING;
        }
        if (isDefined(clazz)) {
            return getType(clazz);
        }
        if (clazz.isPrimitive()) {
            return defineType(clazz, null, false);
        }
        if (clazz.isArray()) {
            throw new InternalError("Arrays not supported");
        }
        String s = null;
        boolean b = false;
        if (Event.class.isAssignableFrom(clazz)) {
            s = Type.SUPER_TYPE_EVENT;
            b = true;
        }
        if (Control.class.isAssignableFrom(clazz)) {
            s = Type.SUPER_TYPE_SETTING;
        }
        defineType(clazz, s, b);
        final Type type = getType(clazz);
        if (b) {
            addImplicitFields(type, true, true, true, true, false);
            addUserFields(clazz, type, list2);
            type.trimFields();
        }
        addAnnotations(clazz, type, list);
        if (clazz.getClassLoader() == null) {
            type.log("Added", LogTag.JFR_SYSTEM_METADATA, LogLevel.INFO);
        }
        else {
            type.log("Added", LogTag.JFR_METADATA, LogLevel.INFO);
        }
        return type;
    }
    
    private static void addAnnotations(final Class<?> clazz, final Type type, final List<AnnotationElement> list) {
        final ArrayList annotations = new ArrayList();
        if (list.isEmpty()) {
            final Iterator<Annotation> iterator = Utils.getAnnotations(clazz).iterator();
            while (iterator.hasNext()) {
                final AnnotationElement annotation = createAnnotation(iterator.next());
                if (annotation != null) {
                    annotations.add(annotation);
                }
            }
        }
        else {
            final ArrayList list2 = new ArrayList();
            annotations.addAll(list);
            final Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                list2.add(PrivateAccess.getInstance().getType(iterator2.next()));
            }
            addTypes(list2);
        }
        type.setAnnotations(annotations);
        annotations.trimToSize();
    }
    
    private static void addUserFields(final Class<?> clazz, final Type type, final List<ValueDescriptor> list) {
        final HashMap hashMap = new HashMap();
        for (final ValueDescriptor valueDescriptor : list) {
            hashMap.put(valueDescriptor.getName(), valueDescriptor);
        }
        final ArrayList list2 = new ArrayList();
        for (final Field field : Utils.getVisibleEventFields(clazz)) {
            ValueDescriptor field2 = (ValueDescriptor)hashMap.get(field.getName());
            if (field2 != null) {
                if (!field2.getTypeName().equals(field.getType().getName())) {
                    throw new InternalError("Type expected to match for field " + field2.getName() + " expected " + field.getName() + " but got " + field2.getName());
                }
                final Iterator<AnnotationElement> iterator3 = field2.getAnnotationElements().iterator();
                while (iterator3.hasNext()) {
                    list2.add(PrivateAccess.getInstance().getType(iterator3.next()));
                }
                list2.add(PrivateAccess.getInstance().getType(field2));
            }
            else {
                field2 = createField(field);
            }
            if (field2 != null) {
                type.add(field2);
            }
        }
        addTypes(list2);
    }
    
    static void addImplicitFields(final Type type, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5) {
        createAnnotationType(Timespan.class);
        createAnnotationType((Class<? extends Annotation>)Timestamp.class);
        createAnnotationType((Class<? extends Annotation>)Label.class);
        defineType(Long.TYPE, null, false);
        addFields(type, b, b2, b3, b4, b5);
    }
    
    private static void addFields(final Type type, final boolean b, final boolean b2, final boolean b3, final boolean b4, final boolean b5) {
        type.add(TypeLibrary.START_TIME_FIELD);
        if (b2 || b5) {
            type.add(TypeLibrary.DURATION_FIELD);
        }
        if (b3) {
            type.add(TypeLibrary.THREAD_FIELD);
        }
        if (b4) {
            type.add(TypeLibrary.STACK_TRACE_FIELD);
        }
    }
    
    private static List<AnnotationElement> createStandardAnnotations(final String s, final String s2) {
        final ArrayList list = new ArrayList(2);
        list.add(new AnnotationElement((Class<? extends Annotation>)Label.class, s));
        if (s2 != null) {
            list.add(new AnnotationElement((Class<? extends Annotation>)Description.class, s2));
        }
        return list;
    }
    
    private static ValueDescriptor createField(final Field field) {
        final int modifiers = field.getModifiers();
        if (Modifier.isTransient(modifiers)) {
            return null;
        }
        if (Modifier.isStatic(modifiers)) {
            return null;
        }
        final Class<?> type = field.getType();
        if (!Type.isKnownType(type)) {
            return null;
        }
        final boolean b = Thread.class == type || type == Class.class;
        final Type type2 = createType(type);
        final String name = field.getName();
        final Name name2 = field.getAnnotation(Name.class);
        String value = name;
        if (name2 != null) {
            value = name2.value();
        }
        final ArrayList list = new ArrayList();
        final Iterator<Annotation> iterator = resolveRepeatedAnnotations(field.getAnnotations()).iterator();
        while (iterator.hasNext()) {
            final AnnotationElement annotation = createAnnotation(iterator.next());
            if (annotation != null) {
                list.add(annotation);
            }
        }
        return PrivateAccess.getInstance().newValueDescriptor(value, type2, list, 0, b, name);
    }
    
    private static List<Annotation> resolveRepeatedAnnotations(final Annotation[] array) {
        final ArrayList list = new ArrayList(array.length);
        for (final Annotation annotation : array) {
            boolean b = false;
            try {
                final Method method = annotation.annotationType().getMethod("value", (Class<?>[])new Class[0]);
                final Class<?> returnType = method.getReturnType();
                if (returnType.isArray()) {
                    final Class componentType = returnType.getComponentType();
                    if (Annotation.class.isAssignableFrom(componentType) && componentType.getAnnotation(Repeatable.class) != null) {
                        final Object invoke = method.invoke(annotation, new Object[0]);
                        if (invoke != null && Annotation[].class.isAssignableFrom(invoke.getClass())) {
                            final Annotation[] array2 = (Annotation[])method.invoke(annotation, new Object[0]);
                            for (int length2 = array2.length, j = 0; j < length2; ++j) {
                                list.add(array2[j]);
                            }
                            b = true;
                        }
                    }
                }
            }
            catch (final NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {}
            if (!b) {
                list.add(annotation);
            }
        }
        return list;
    }
    
    public boolean clearUnregistered() {
        Logger.log(LogTag.JFR_METADATA, LogLevel.TRACE, "Cleaning out obsolete metadata");
        final ArrayList list = new ArrayList();
        for (final Type type : TypeLibrary.types.values()) {
            if (type instanceof PlatformEventType && ((PlatformEventType)type).isRegistered()) {
                list.add(type);
            }
        }
        visitReachable(list, type3 -> type3.getRemove(), type4 -> type4.setRemove((boolean)(0 != 0)));
        final ArrayList list2 = new ArrayList();
        for (final Type type2 : TypeLibrary.types.values()) {
            if (type2.getRemove() && !Type.isDefinedByJVM(type2.getId())) {
                list2.add(type2.getId());
                if (Logger.shouldLog(LogTag.JFR_METADATA, LogLevel.TRACE)) {
                    Logger.log(LogTag.JFR_METADATA, LogLevel.TRACE, "Removed obsolete metadata " + type2.getName());
                }
            }
            type2.setRemove(true);
        }
        final Iterator iterator3 = list2.iterator();
        while (iterator3.hasNext()) {
            TypeLibrary.types.remove(iterator3.next());
        }
        return !list2.isEmpty();
    }
    
    public void addType(final Type type) {
        addTypes(Collections.singletonList(type));
    }
    
    public static void addTypes(final List<Type> list) {
        if (!list.isEmpty()) {
            visitReachable(list, type -> !TypeLibrary.types.containsKey(type.getId()), type2 -> {
                final Type type3 = TypeLibrary.types.put(type2.getId(), type2);
            });
        }
    }
    
    private static void visitReachable(final Collection<Type> collection, final Predicate<Type> predicate, final Consumer<Type> consumer) {
        final ArrayDeque arrayDeque = new ArrayDeque((Collection<? extends E>)collection);
        while (!arrayDeque.isEmpty()) {
            final Type type = (Type)arrayDeque.poll();
            if (predicate.test(type)) {
                consumer.accept(type);
                visitAnnotations(arrayDeque, type.getAnnotationElements());
                for (final ValueDescriptor valueDescriptor : type.getFields()) {
                    arrayDeque.add(PrivateAccess.getInstance().getType(valueDescriptor));
                    visitAnnotations(arrayDeque, valueDescriptor.getAnnotationElements());
                }
                if (!(type instanceof PlatformEventType)) {
                    continue;
                }
                for (final SettingDescriptor settingDescriptor : ((PlatformEventType)type).getAllSettings()) {
                    arrayDeque.add(PrivateAccess.getInstance().getType(settingDescriptor));
                    visitAnnotations(arrayDeque, settingDescriptor.getAnnotationElements());
                }
            }
        }
    }
    
    private static void visitAnnotations(final Queue<Type> queue, final List<AnnotationElement> list) {
        final ArrayDeque arrayDeque = new ArrayDeque((Collection<? extends E>)list);
        final HashSet set = new HashSet();
        while (!arrayDeque.isEmpty()) {
            final AnnotationElement annotationElement = (AnnotationElement)arrayDeque.poll();
            if (!set.contains(annotationElement)) {
                queue.add(PrivateAccess.getInstance().getType(annotationElement));
                set.add(annotationElement);
            }
            arrayDeque.addAll(annotationElement.getAnnotationElements());
        }
    }
    
    static {
        types = new LinkedHashMap<Long, Type>(100);
        DURATION_FIELD = createDurationField();
        THREAD_FIELD = createThreadField();
        STACK_TRACE_FIELD = createStackTraceField();
        START_TIME_FIELD = createStartTimeField();
    }
}
