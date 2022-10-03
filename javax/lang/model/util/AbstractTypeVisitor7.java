package javax.lang.model.util;

import javax.lang.model.type.UnionType;

public abstract class AbstractTypeVisitor7<R, P> extends AbstractTypeVisitor6<R, P>
{
    protected AbstractTypeVisitor7() {
    }
    
    @Override
    public abstract R visitUnion(final UnionType p0, final P p1);
}
