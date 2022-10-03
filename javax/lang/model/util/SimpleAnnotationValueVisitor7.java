package javax.lang.model.util;

import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SimpleAnnotationValueVisitor7<R, P> extends SimpleAnnotationValueVisitor6<R, P>
{
    protected SimpleAnnotationValueVisitor7() {
        super(null);
    }
    
    protected SimpleAnnotationValueVisitor7(final R r) {
        super(r);
    }
}
