package javax.lang.model.util;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ElementScanner7<R, P> extends ElementScanner6<R, P>
{
    protected ElementScanner7() {
        super(null);
    }
    
    protected ElementScanner7(final R r) {
        super(r);
    }
    
    @Override
    public R visitVariable(final VariableElement variableElement, final P p2) {
        return this.scan(variableElement.getEnclosedElements(), p2);
    }
}
