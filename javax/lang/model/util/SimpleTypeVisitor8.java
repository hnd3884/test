package javax.lang.model.util;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SimpleTypeVisitor8<R, P> extends SimpleTypeVisitor7<R, P>
{
    protected SimpleTypeVisitor8() {
        super(null);
    }
    
    protected SimpleTypeVisitor8(final R r) {
        super(r);
    }
    
    @Override
    public R visitIntersection(final IntersectionType intersectionType, final P p2) {
        return this.defaultAction(intersectionType, p2);
    }
}
