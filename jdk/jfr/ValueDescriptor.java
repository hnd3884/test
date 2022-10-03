package jdk.jfr;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import jdk.jfr.internal.Utils;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.List;
import jdk.jfr.internal.Type;
import jdk.jfr.internal.AnnotationConstruct;
import jdk.Exported;

@Exported
public final class ValueDescriptor
{
    private final AnnotationConstruct annotationConstruct;
    private final Type type;
    private final String name;
    private final boolean isArray;
    private final boolean constantPool;
    private final String javaFieldName;
    
    ValueDescriptor(final Type type, final String s, final List<AnnotationElement> list, final int n, final boolean constantPool, final String javaFieldName) {
        Objects.requireNonNull(list);
        if (n < 0) {
            throw new IllegalArgumentException("Dimension must be positive");
        }
        this.name = Objects.requireNonNull(s, "Name of value descriptor can't be null");
        this.type = Objects.requireNonNull(type);
        this.isArray = (n > 0);
        this.constantPool = constantPool;
        this.annotationConstruct = new AnnotationConstruct(list);
        this.javaFieldName = javaFieldName;
    }
    
    public ValueDescriptor(final Class<?> clazz, final String s) {
        this(clazz, s, Collections.emptyList());
    }
    
    public ValueDescriptor(final Class<?> clazz, final String s, final List<AnnotationElement> list) {
        this(clazz, s, new ArrayList<AnnotationElement>(list), false);
    }
    
    ValueDescriptor(final Class<?> clazz, final String javaFieldName, final List<AnnotationElement> list, final boolean b) {
        Objects.requireNonNull(list);
        Utils.checkRegisterPermission();
        if (!b && clazz.isArray()) {
            throw new IllegalArgumentException("Array types are not allowed");
        }
        this.name = Objects.requireNonNull(javaFieldName, "Name of value descriptor can't be null");
        this.type = Objects.requireNonNull(Utils.getValidType(Objects.requireNonNull(clazz), Objects.requireNonNull(javaFieldName)));
        this.annotationConstruct = new AnnotationConstruct(list);
        this.javaFieldName = javaFieldName;
        this.isArray = clazz.isArray();
        this.constantPool = (clazz == Class.class || clazz == Thread.class);
    }
    
    public String getLabel() {
        return this.annotationConstruct.getLabel();
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.annotationConstruct.getDescription();
    }
    
    public String getContentType() {
        for (final AnnotationElement annotationElement : this.getAnnotationElements()) {
            final Iterator<AnnotationElement> iterator2 = annotationElement.getAnnotationElements().iterator();
            while (iterator2.hasNext()) {
                if (iterator2.next().getTypeName().equals(ContentType.class.getName())) {
                    return annotationElement.getTypeName();
                }
            }
        }
        return null;
    }
    
    public String getTypeName() {
        if (this.type.isSimpleType()) {
            return this.type.getFields().get(0).getTypeName();
        }
        return this.type.getName();
    }
    
    public long getTypeId() {
        return this.type.getId();
    }
    
    public boolean isArray() {
        return this.isArray;
    }
    
    public <A extends Annotation> A getAnnotation(final Class<A> clazz) {
        Objects.requireNonNull(clazz);
        return this.annotationConstruct.getAnnotation(clazz);
    }
    
    public List<AnnotationElement> getAnnotationElements() {
        return this.annotationConstruct.getUnmodifiableAnnotationElements();
    }
    
    public List<ValueDescriptor> getFields() {
        if (this.type.isSimpleType()) {
            return Collections.emptyList();
        }
        return this.type.getFields();
    }
    
    Type getType() {
        return this.type;
    }
    
    void setAnnotations(final List<AnnotationElement> annotationElements) {
        this.annotationConstruct.setAnnotationElements(annotationElements);
    }
    
    boolean isConstantPool() {
        return this.constantPool;
    }
    
    String getJavaFieldName() {
        return this.javaFieldName;
    }
    
    boolean isUnsigned() {
        return this.annotationConstruct.hasUnsigned();
    }
}
