package javax.lang.model.util;

import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SimpleElementVisitor8<R, P> extends SimpleElementVisitor7<R, P>
{
    protected SimpleElementVisitor8() {
        super(null);
    }
    
    protected SimpleElementVisitor8(final R r) {
        super(r);
    }
}
