package javax.lang.model.util;

import java.io.Writer;
import java.util.List;
import javax.lang.model.element.Name;
import javax.lang.model.element.Element;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.PackageElement;

public interface Elements
{
    PackageElement getPackageElement(final CharSequence p0);
    
    TypeElement getTypeElement(final CharSequence p0);
    
    Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(final AnnotationMirror p0);
    
    String getDocComment(final Element p0);
    
    boolean isDeprecated(final Element p0);
    
    Name getBinaryName(final TypeElement p0);
    
    PackageElement getPackageOf(final Element p0);
    
    List<? extends Element> getAllMembers(final TypeElement p0);
    
    List<? extends AnnotationMirror> getAllAnnotationMirrors(final Element p0);
    
    boolean hides(final Element p0, final Element p1);
    
    boolean overrides(final ExecutableElement p0, final ExecutableElement p1, final TypeElement p2);
    
    String getConstantExpression(final Object p0);
    
    void printElements(final Writer p0, final Element... p1);
    
    Name getName(final CharSequence p0);
    
    boolean isFunctionalInterface(final TypeElement p0);
}
