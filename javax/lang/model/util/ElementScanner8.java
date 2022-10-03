package javax.lang.model.util;

import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ElementScanner8<R, P> extends ElementScanner7<R, P>
{
    protected ElementScanner8() {
        super(null);
    }
    
    protected ElementScanner8(final R r) {
        super(r);
    }
}
