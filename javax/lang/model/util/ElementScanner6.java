package javax.lang.model.util;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.ElementVisitor;
import java.util.Iterator;
import javax.lang.model.element.Element;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class ElementScanner6<R, P> extends AbstractElementVisitor6<R, P>
{
    protected final R DEFAULT_VALUE;
    
    protected ElementScanner6() {
        this.DEFAULT_VALUE = null;
    }
    
    protected ElementScanner6(final R default_VALUE) {
        this.DEFAULT_VALUE = default_VALUE;
    }
    
    public final R scan(final Iterable<? extends Element> iterable, final P p2) {
        R r = this.DEFAULT_VALUE;
        final Iterator<? extends Element> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            r = this.scan((Element)iterator.next(), p2);
        }
        return r;
    }
    
    public R scan(final Element element, final P p2) {
        return element.accept((ElementVisitor<R, P>)this, p2);
    }
    
    public final R scan(final Element element) {
        return this.scan(element, null);
    }
    
    @Override
    public R visitPackage(final PackageElement packageElement, final P p2) {
        return this.scan(packageElement.getEnclosedElements(), p2);
    }
    
    @Override
    public R visitType(final TypeElement typeElement, final P p2) {
        return this.scan(typeElement.getEnclosedElements(), p2);
    }
    
    @Override
    public R visitVariable(final VariableElement variableElement, final P p2) {
        if (variableElement.getKind() != ElementKind.RESOURCE_VARIABLE) {
            return this.scan(variableElement.getEnclosedElements(), p2);
        }
        return this.visitUnknown(variableElement, p2);
    }
    
    @Override
    public R visitExecutable(final ExecutableElement executableElement, final P p2) {
        return this.scan(executableElement.getParameters(), p2);
    }
    
    @Override
    public R visitTypeParameter(final TypeParameterElement typeParameterElement, final P p2) {
        return this.scan(typeParameterElement.getEnclosedElements(), p2);
    }
}
