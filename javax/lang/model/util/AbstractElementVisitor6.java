package javax.lang.model.util;

import javax.lang.model.element.UnknownElementException;
import javax.lang.model.element.Element;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.ElementVisitor;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public abstract class AbstractElementVisitor6<R, P> implements ElementVisitor<R, P>
{
    protected AbstractElementVisitor6() {
    }
    
    @Override
    public final R visit(final Element element, final P p2) {
        return element.accept((ElementVisitor<R, P>)this, p2);
    }
    
    @Override
    public final R visit(final Element element) {
        return element.accept((ElementVisitor<R, Object>)this, null);
    }
    
    @Override
    public R visitUnknown(final Element element, final P p2) {
        throw new UnknownElementException(element, p2);
    }
}
