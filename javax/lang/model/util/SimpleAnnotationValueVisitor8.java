package javax.lang.model.util;

import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SimpleAnnotationValueVisitor8<R, P> extends SimpleAnnotationValueVisitor7<R, P>
{
    protected SimpleAnnotationValueVisitor8() {
        super(null);
    }
    
    protected SimpleAnnotationValueVisitor8(final R r) {
        super(r);
    }
}
