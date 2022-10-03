package javax.lang.model.element;

public interface ElementVisitor<R, P>
{
    R visit(final Element p0, final P p1);
    
    R visit(final Element p0);
    
    R visitPackage(final PackageElement p0, final P p1);
    
    R visitType(final TypeElement p0, final P p1);
    
    R visitVariable(final VariableElement p0, final P p1);
    
    R visitExecutable(final ExecutableElement p0, final P p1);
    
    R visitTypeParameter(final TypeParameterElement p0, final P p1);
    
    R visitUnknown(final Element p0, final P p1);
}
