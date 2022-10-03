package javax.lang.model.element;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.AnnotatedConstruct;

public interface Element extends AnnotatedConstruct
{
    TypeMirror asType();
    
    ElementKind getKind();
    
    Set<Modifier> getModifiers();
    
    Name getSimpleName();
    
    Element getEnclosingElement();
    
    List<? extends Element> getEnclosedElements();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    List<? extends AnnotationMirror> getAnnotationMirrors();
    
     <A extends Annotation> A getAnnotation(final Class<A> p0);
    
     <R, P> R accept(final ElementVisitor<R, P> p0, final P p1);
}
