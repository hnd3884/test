package jdk.jfr;

import java.util.Collections;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Objects;
import java.util.List;
import jdk.jfr.internal.Type;
import jdk.jfr.internal.AnnotationConstruct;
import jdk.Exported;

@Exported
public final class SettingDescriptor
{
    private final AnnotationConstruct annotationConstruct;
    private final Type type;
    private final String name;
    private final String defaultValue;
    
    SettingDescriptor(final Type type, final String s, final String s2, final List<AnnotationElement> list) {
        Objects.requireNonNull(list);
        this.name = Objects.requireNonNull(s, "Name of value descriptor can't be null");
        this.type = Objects.requireNonNull(type);
        this.annotationConstruct = new AnnotationConstruct(list);
        this.defaultValue = Objects.requireNonNull(s2);
    }
    
    void setAnnotations(final List<AnnotationElement> annotationElements) {
        this.annotationConstruct.setAnnotationElements(annotationElements);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getLabel() {
        String s = this.annotationConstruct.getLabel();
        if (s == null) {
            s = this.type.getLabel();
        }
        return s;
    }
    
    public String getDescription() {
        String s = this.annotationConstruct.getDescription();
        if (s == null) {
            s = this.type.getDescription();
        }
        return s;
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
        for (final AnnotationElement annotationElement2 : this.type.getAnnotationElements()) {
            final Iterator<AnnotationElement> iterator4 = annotationElement2.getAnnotationElements().iterator();
            while (iterator4.hasNext()) {
                if (iterator4.next().getTypeName().equals(ContentType.class.getName())) {
                    return annotationElement2.getTypeName();
                }
            }
        }
        return null;
    }
    
    public String getTypeName() {
        return this.type.getName();
    }
    
    public long getTypeId() {
        return this.type.getId();
    }
    
    public <A extends Annotation> A getAnnotation(final Class<A> clazz) {
        Objects.requireNonNull(clazz);
        return this.annotationConstruct.getAnnotation(clazz);
    }
    
    public List<AnnotationElement> getAnnotationElements() {
        return Collections.unmodifiableList((List<? extends AnnotationElement>)this.annotationConstruct.getUnmodifiableAnnotationElements());
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    Type getType() {
        return this.type;
    }
}
