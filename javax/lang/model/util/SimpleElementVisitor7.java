package javax.lang.model.util;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SimpleElementVisitor7<R, P> extends SimpleElementVisitor6<R, P>
{
    protected SimpleElementVisitor7() {
        super(null);
    }
    
    protected SimpleElementVisitor7(final R r) {
        super(r);
    }
    
    @Override
    public R visitVariable(final VariableElement variableElement, final P p2) {
        return this.defaultAction(variableElement, p2);
    }
}
