package jdk.jfr.internal;

import java.lang.reflect.Method;
import jdk.jfr.Unsigned;
import java.util.Iterator;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import jdk.jfr.Description;
import java.lang.annotation.Annotation;
import jdk.jfr.Label;
import java.util.Collections;
import jdk.jfr.AnnotationElement;
import java.util.List;

public final class AnnotationConstruct
{
    private List<AnnotationElement> annotationElements;
    private byte unsignedFlag;
    
    public AnnotationConstruct(final List<AnnotationElement> annotationElements) {
        this.annotationElements = Collections.emptyList();
        this.unsignedFlag = -1;
        this.annotationElements = annotationElements;
    }
    
    public AnnotationConstruct() {
        this.annotationElements = Collections.emptyList();
        this.unsignedFlag = -1;
    }
    
    public void setAnnotationElements(final List<AnnotationElement> list) {
        this.annotationElements = Utils.smallUnmodifiable(list);
    }
    
    public String getLabel() {
        final Label label = this.getAnnotation((Class<? extends Annotation>)Label.class);
        if (label == null) {
            return null;
        }
        return label.value();
    }
    
    public String getDescription() {
        final Description description = this.getAnnotation((Class<? extends Annotation>)Description.class);
        if (description == null) {
            return null;
        }
        return description.value();
    }
    
    public final <T> T getAnnotation(final Class<? extends Annotation> clazz) {
        final AnnotationElement annotationElement = this.getAnnotationElement(clazz);
        if (annotationElement != null) {
            return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new AnnotationInvokationHandler(annotationElement));
        }
        return null;
    }
    
    public List<AnnotationElement> getUnmodifiableAnnotationElements() {
        return this.annotationElements;
    }
    
    boolean remove(final AnnotationElement annotationElement) {
        return this.annotationElements.remove(annotationElement);
    }
    
    private AnnotationElement getAnnotationElement(final Class<? extends Annotation> clazz) {
        final long typeId = Type.getTypeId(clazz);
        final String name = clazz.getName();
        for (final AnnotationElement annotationElement : this.getUnmodifiableAnnotationElements()) {
            if (annotationElement.getTypeId() == typeId && annotationElement.getTypeName().equals(name)) {
                return annotationElement;
            }
        }
        for (final AnnotationElement annotationElement2 : this.getUnmodifiableAnnotationElements()) {
            if (annotationElement2.getTypeName().equals(name)) {
                return annotationElement2;
            }
        }
        return null;
    }
    
    public boolean hasUnsigned() {
        if (this.unsignedFlag < 0) {
            this.unsignedFlag = (byte)((this.getAnnotation(Unsigned.class) != null) ? 1 : 0);
        }
        return this.unsignedFlag == 1;
    }
    
    private static final class AnnotationInvokationHandler implements InvocationHandler
    {
        private final AnnotationElement annotationElement;
        
        AnnotationInvokationHandler(final AnnotationElement annotationElement) {
            this.annotationElement = annotationElement;
        }
        
        @Override
        public Object invoke(final Object o, final Method method, final Object[] array) throws Throwable {
            final String name = method.getName();
            if (method.getTypeParameters().length == 0 && this.annotationElement.hasValue(name)) {
                return this.annotationElement.getValue(name);
            }
            throw new UnsupportedOperationException("Flight Recorder proxy only supports members declared in annotation interfaces, i.e. not toString, equals etc.");
        }
    }
}
