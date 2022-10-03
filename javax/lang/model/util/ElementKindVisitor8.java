package javax.lang.model.util;

import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ElementKindVisitor8<R, P> extends ElementKindVisitor7<R, P>
{
    protected ElementKindVisitor8() {
        super(null);
    }
    
    protected ElementKindVisitor8(final R r) {
        super(r);
    }
}
