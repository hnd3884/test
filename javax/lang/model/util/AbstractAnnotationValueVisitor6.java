package javax.lang.model.util;

import javax.lang.model.element.UnknownAnnotationValueException;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationValueVisitor;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public abstract class AbstractAnnotationValueVisitor6<R, P> implements AnnotationValueVisitor<R, P>
{
    protected AbstractAnnotationValueVisitor6() {
    }
    
    @Override
    public final R visit(final AnnotationValue annotationValue, final P p2) {
        return annotationValue.accept((AnnotationValueVisitor<R, P>)this, p2);
    }
    
    @Override
    public final R visit(final AnnotationValue annotationValue) {
        return annotationValue.accept((AnnotationValueVisitor<R, Object>)this, null);
    }
    
    @Override
    public R visitUnknown(final AnnotationValue annotationValue, final P p2) {
        throw new UnknownAnnotationValueException(annotationValue, p2);
    }
}
