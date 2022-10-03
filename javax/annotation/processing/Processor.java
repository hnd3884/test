package javax.annotation.processing;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.SourceVersion;
import java.util.Set;

public interface Processor
{
    Set<String> getSupportedOptions();
    
    Set<String> getSupportedAnnotationTypes();
    
    SourceVersion getSupportedSourceVersion();
    
    void init(final ProcessingEnvironment p0);
    
    boolean process(final Set<? extends TypeElement> p0, final RoundEnvironment p1);
    
    Iterable<? extends Completion> getCompletions(final Element p0, final AnnotationMirror p1, final ExecutableElement p2, final String p3);
}
