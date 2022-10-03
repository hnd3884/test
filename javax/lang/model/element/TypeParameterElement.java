package javax.lang.model.element;

import javax.lang.model.type.TypeMirror;
import java.util.List;

public interface TypeParameterElement extends Element
{
    Element getGenericElement();
    
    List<? extends TypeMirror> getBounds();
    
    Element getEnclosingElement();
}
