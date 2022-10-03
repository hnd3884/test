package javax.annotation.processing;

import java.lang.annotation.Annotation;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import java.util.Set;

public interface RoundEnvironment
{
    boolean processingOver();
    
    boolean errorRaised();
    
    Set<? extends Element> getRootElements();
    
    Set<? extends Element> getElementsAnnotatedWith(final TypeElement p0);
    
    Set<? extends Element> getElementsAnnotatedWith(final Class<? extends Annotation> p0);
}
