package javax.lang.model.util;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.UnionType;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SimpleTypeVisitor7<R, P> extends SimpleTypeVisitor6<R, P>
{
    protected SimpleTypeVisitor7() {
        super(null);
    }
    
    protected SimpleTypeVisitor7(final R r) {
        super(r);
    }
    
    @Override
    public R visitUnion(final UnionType unionType, final P p2) {
        return this.defaultAction(unionType, p2);
    }
}
