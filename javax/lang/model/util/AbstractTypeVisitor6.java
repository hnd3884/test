package javax.lang.model.util;

import javax.lang.model.type.UnknownTypeException;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

public abstract class AbstractTypeVisitor6<R, P> implements TypeVisitor<R, P>
{
    protected AbstractTypeVisitor6() {
    }
    
    @Override
    public final R visit(final TypeMirror typeMirror, final P p2) {
        return typeMirror.accept((TypeVisitor<R, P>)this, p2);
    }
    
    @Override
    public final R visit(final TypeMirror typeMirror) {
        return typeMirror.accept((TypeVisitor<R, Object>)this, null);
    }
    
    @Override
    public R visitUnion(final UnionType unionType, final P p2) {
        return this.visitUnknown(unionType, p2);
    }
    
    @Override
    public R visitIntersection(final IntersectionType intersectionType, final P p2) {
        return this.visitUnknown(intersectionType, p2);
    }
    
    @Override
    public R visitUnknown(final TypeMirror typeMirror, final P p2) {
        throw new UnknownTypeException(typeMirror, p2);
    }
}
