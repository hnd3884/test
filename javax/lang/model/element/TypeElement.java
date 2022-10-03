package javax.lang.model.element;

import javax.lang.model.type.TypeMirror;
import java.util.List;

public interface TypeElement extends Element, Parameterizable, QualifiedNameable
{
    List<? extends Element> getEnclosedElements();
    
    NestingKind getNestingKind();
    
    Name getQualifiedName();
    
    Name getSimpleName();
    
    TypeMirror getSuperclass();
    
    List<? extends TypeMirror> getInterfaces();
    
    List<? extends TypeParameterElement> getTypeParameters();
    
    Element getEnclosingElement();
}
