package javax.lang.model.type;

import javax.lang.model.AnnotatedConstruct;

public interface TypeMirror extends AnnotatedConstruct
{
    TypeKind getKind();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
    
     <R, P> R accept(final TypeVisitor<R, P> p0, final P p1);
}
