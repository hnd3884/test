package javax.lang.model.util;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.UnionType;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TypeKindVisitor7<R, P> extends TypeKindVisitor6<R, P>
{
    protected TypeKindVisitor7() {
        super(null);
    }
    
    protected TypeKindVisitor7(final R r) {
        super(r);
    }
    
    @Override
    public R visitUnion(final UnionType unionType, final P p2) {
        return this.defaultAction(unionType, p2);
    }
}
