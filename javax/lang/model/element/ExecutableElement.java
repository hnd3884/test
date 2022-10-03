package javax.lang.model.element;

import javax.lang.model.type.TypeMirror;
import java.util.List;

public interface ExecutableElement extends Element, Parameterizable
{
    List<? extends TypeParameterElement> getTypeParameters();
    
    TypeMirror getReturnType();
    
    List<? extends VariableElement> getParameters();
    
    TypeMirror getReceiverType();
    
    boolean isVarArgs();
    
    boolean isDefault();
    
    List<? extends TypeMirror> getThrownTypes();
    
    AnnotationValue getDefaultValue();
    
    Name getSimpleName();
}
