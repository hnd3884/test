package javax.lang.model;

import java.lang.annotation.Annotation;
import javax.lang.model.element.AnnotationMirror;
import java.util.List;

public interface AnnotatedConstruct
{
    List<? extends AnnotationMirror> getAnnotationMirrors();
    
     <A extends Annotation> A getAnnotation(final Class<A> p0);
    
     <A extends Annotation> A[] getAnnotationsByType(final Class<A> p0);
}
