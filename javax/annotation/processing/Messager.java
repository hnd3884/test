package javax.annotation.processing;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public interface Messager
{
    void printMessage(final Diagnostic.Kind p0, final CharSequence p1);
    
    void printMessage(final Diagnostic.Kind p0, final CharSequence p1, final Element p2);
    
    void printMessage(final Diagnostic.Kind p0, final CharSequence p1, final Element p2, final AnnotationMirror p3);
    
    void printMessage(final Diagnostic.Kind p0, final CharSequence p1, final Element p2, final AnnotationMirror p3, final AnnotationValue p4);
}
