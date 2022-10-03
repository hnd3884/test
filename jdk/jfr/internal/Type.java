package jdk.jfr.internal;

import java.util.HashMap;
import jdk.jfr.Event;
import jdk.jfr.SettingControl;
import java.lang.annotation.Annotation;
import jdk.jfr.AnnotationElement;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;
import jdk.jfr.ValueDescriptor;
import java.util.List;
import java.util.Map;

public class Type implements Comparable<Type>
{
    public static final String SUPER_TYPE_ANNOTATION;
    public static final String SUPER_TYPE_SETTING;
    public static final String SUPER_TYPE_EVENT;
    public static final String EVENT_NAME_PREFIX = "jdk.";
    public static final String TYPES_PREFIX = "jdk.types.";
    public static final String SETTINGS_PREFIX = "jdk.settings.";
    private static final Map<Type, Class<?>> knownTypes;
    static final Type BOOLEAN;
    static final Type CHAR;
    static final Type FLOAT;
    static final Type DOUBLE;
    static final Type BYTE;
    static final Type SHORT;
    static final Type INT;
    static final Type LONG;
    static final Type CLASS;
    static final Type STRING;
    static final Type THREAD;
    static final Type STACK_TRACE;
    private final AnnotationConstruct annos;
    private final String name;
    private final String superType;
    private final boolean constantPool;
    private final long id;
    private List<ValueDescriptor> fields;
    private Boolean simpleType;
    private boolean remove;
    
    public Type(final String s, final String s2, final long n) {
        this(s, s2, n, false);
    }
    
    Type(final String s, final String s2, final long n, final boolean b) {
        this(s, s2, n, b, null);
    }
    
    Type(final String name, final String superType, final long id, final boolean constantPool, final Boolean simpleType) {
        this.annos = new AnnotationConstruct();
        this.fields = new ArrayList<ValueDescriptor>();
        this.remove = true;
        Objects.requireNonNull(name);
        if (!isValidJavaIdentifier(name)) {
            throw new IllegalArgumentException(name + " is not a valid Java identifier");
        }
        this.constantPool = constantPool;
        this.superType = superType;
        this.name = name;
        this.id = id;
        this.simpleType = simpleType;
    }
    
    static boolean isDefinedByJVM(final long n) {
        return n < 400L;
    }
    
    public static long getTypeId(final Class<?> clazz) {
        final Type knownType = getKnownType(clazz);
        return (knownType == null) ? JVM.getJVM().getTypeId(clazz) : knownType.getId();
    }
    
    static Collection<Type> getKnownTypes() {
        return Type.knownTypes.keySet();
    }
    
    public static boolean isValidJavaIdentifier(final String s) {
        if (s.isEmpty()) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 != '.' && !Character.isJavaIdentifierPart(char1)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isValidJavaFieldType(final String s) {
        final Iterator<Map.Entry<Type, Class<?>>> iterator = Type.knownTypes.entrySet().iterator();
        while (iterator.hasNext()) {
            final Class clazz = ((Map.Entry<K, Class>)iterator.next()).getValue();
            if (clazz != null && s.equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }
    
    public static Type getKnownType(final String s) {
        for (final Type type : Type.knownTypes.keySet()) {
            if (type.getName().equals(s)) {
                return type;
            }
        }
        return null;
    }
    
    static boolean isKnownType(final Class<?> clazz) {
        return clazz.isPrimitive() || (clazz.equals(Class.class) || clazz.equals(Thread.class) || clazz.equals(String.class));
    }
    
    public static Type getKnownType(final Class<?> clazz) {
        for (final Map.Entry entry : Type.knownTypes.entrySet()) {
            if (clazz != null && clazz.equals(entry.getValue())) {
                return (Type)entry.getKey();
            }
        }
        return null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getLogName() {
        return this.getName() + "(" + this.getId() + ")";
    }
    
    public List<ValueDescriptor> getFields() {
        if (this.fields instanceof ArrayList) {
            ((ArrayList)this.fields).trimToSize();
            this.fields = Collections.unmodifiableList((List<? extends ValueDescriptor>)this.fields);
        }
        return this.fields;
    }
    
    public boolean isSimpleType() {
        if (this.simpleType == null) {
            this.simpleType = this.calculateSimpleType();
        }
        return this.simpleType;
    }
    
    private boolean calculateSimpleType() {
        return this.fields.size() == 1 && this.superType == null;
    }
    
    public boolean isDefinedByJVM() {
        return this.id < 400L;
    }
    
    private static Type register(final Class<?> clazz, final Type type) {
        Type.knownTypes.put(type, clazz);
        return type;
    }
    
    public void add(final ValueDescriptor valueDescriptor) {
        Objects.requireNonNull(valueDescriptor);
        this.fields.add(valueDescriptor);
    }
    
    void trimFields() {
        this.getFields();
    }
    
    void setAnnotations(final List<AnnotationElement> annotationElements) {
        this.annos.setAnnotationElements(annotationElements);
    }
    
    public String getSuperType() {
        return this.superType;
    }
    
    public long getId() {
        return this.id;
    }
    
    public boolean isConstantPool() {
        return this.constantPool;
    }
    
    public String getLabel() {
        return this.annos.getLabel();
    }
    
    public List<AnnotationElement> getAnnotationElements() {
        return this.annos.getUnmodifiableAnnotationElements();
    }
    
    public <T> T getAnnotation(final Class<? extends Annotation> clazz) {
        return this.annos.getAnnotation(clazz);
    }
    
    public String getDescription() {
        return this.annos.getDescription();
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Type && ((Type)o).id == this.id;
    }
    
    @Override
    public int compareTo(final Type type) {
        return Long.compare(this.id, type.id);
    }
    
    void log(final String s, final LogTag logTag, final LogLevel logLevel) {
        if (Logger.shouldLog(logTag, logLevel) && !this.isSimpleType()) {
            Logger.log(logTag, LogLevel.TRACE, s + " " + this.typeText() + " " + this.getLogName() + " {");
            for (final ValueDescriptor valueDescriptor : this.getFields()) {
                Logger.log(logTag, LogLevel.TRACE, "  " + valueDescriptor.getTypeName() + (valueDescriptor.isArray() ? "[]" : "") + " " + valueDescriptor.getName() + ";");
            }
            Logger.log(logTag, LogLevel.TRACE, "}");
        }
        else if (Logger.shouldLog(logTag, LogLevel.INFO) && !this.isSimpleType()) {
            Logger.log(logTag, LogLevel.INFO, s + " " + this.typeText() + " " + this.getLogName());
        }
    }
    
    private String typeText() {
        if (this instanceof PlatformEventType) {
            return "event type";
        }
        if (Type.SUPER_TYPE_SETTING.equals(this.superType)) {
            return "setting type";
        }
        if (Type.SUPER_TYPE_ANNOTATION.equals(this.superType)) {
            return "annotation type";
        }
        return "type";
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getLogName());
        if (!this.getFields().isEmpty()) {
            sb.append(" {\n");
            for (final ValueDescriptor valueDescriptor : this.getFields()) {
                sb.append("  type=" + valueDescriptor.getTypeName() + "(" + valueDescriptor.getTypeId() + ") name=" + valueDescriptor.getName() + "\n");
            }
            sb.append("}\n");
        }
        return sb.toString();
    }
    
    public void setRemove(final boolean remove) {
        this.remove = remove;
    }
    
    public boolean getRemove() {
        return this.remove;
    }
    
    static {
        SUPER_TYPE_ANNOTATION = Annotation.class.getName();
        SUPER_TYPE_SETTING = SettingControl.class.getName();
        SUPER_TYPE_EVENT = Event.class.getName();
        knownTypes = new HashMap<Type, Class<?>>();
        BOOLEAN = register(Boolean.TYPE, new Type("boolean", null, 4L));
        CHAR = register(Character.TYPE, new Type("char", null, 5L));
        FLOAT = register(Float.TYPE, new Type("float", null, 6L));
        DOUBLE = register(Double.TYPE, new Type("double", null, 7L));
        BYTE = register(Byte.TYPE, new Type("byte", null, 8L));
        SHORT = register(Short.TYPE, new Type("short", null, 9L));
        INT = register(Integer.TYPE, new Type("int", null, 10L));
        LONG = register(Long.TYPE, new Type("long", null, 11L));
        CLASS = register(Class.class, new Type("java.lang.Class", null, 20L));
        STRING = register(String.class, new Type("java.lang.String", null, 21L));
        THREAD = register(Thread.class, new Type("java.lang.Thread", null, 22L));
        STACK_TRACE = register(null, new Type("jdk.types.StackTrace", null, 23L));
    }
}
