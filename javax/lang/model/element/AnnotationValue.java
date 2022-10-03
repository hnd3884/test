package javax.lang.model.element;

public interface AnnotationValue
{
    Object getValue();
    
    String toString();
    
     <R, P> R accept(final AnnotationValueVisitor<R, P> p0, final P p1);
}
