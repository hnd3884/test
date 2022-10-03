package javax.lang.model.util;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.Element;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleElementVisitor6<R, P> extends AbstractElementVisitor6<R, P>
{
    protected final R DEFAULT_VALUE;
    
    protected SimpleElementVisitor6() {
        this.DEFAULT_VALUE = null;
    }
    
    protected SimpleElementVisitor6(final R default_VALUE) {
        this.DEFAULT_VALUE = default_VALUE;
    }
    
    protected R defaultAction(final Element element, final P p2) {
        return this.DEFAULT_VALUE;
    }
    
    @Override
    public R visitPackage(final PackageElement packageElement, final P p2) {
        return this.defaultAction(packageElement, p2);
    }
    
    @Override
    public R visitType(final TypeElement typeElement, final P p2) {
        return this.defaultAction(typeElement, p2);
    }
    
    @Override
    public R visitVariable(final VariableElement variableElement, final P p2) {
        if (variableElement.getKind() != ElementKind.RESOURCE_VARIABLE) {
            return this.defaultAction(variableElement, p2);
        }
        return this.visitUnknown(variableElement, p2);
    }
    
    @Override
    public R visitExecutable(final ExecutableElement executableElement, final P p2) {
        return this.defaultAction(executableElement, p2);
    }
    
    @Override
    public R visitTypeParameter(final TypeParameterElement typeParameterElement, final P p2) {
        return this.defaultAction(typeParameterElement, p2);
    }
}
