package javax.lang.model.util;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ElementKindVisitor7<R, P> extends ElementKindVisitor6<R, P>
{
    protected ElementKindVisitor7() {
        super(null);
    }
    
    protected ElementKindVisitor7(final R r) {
        super(r);
    }
    
    @Override
    public R visitVariableAsResourceVariable(final VariableElement variableElement, final P p2) {
        return this.defaultAction(variableElement, p2);
    }
}
