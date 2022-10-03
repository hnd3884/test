package javax.lang.model.util;

import javax.lang.model.type.IntersectionType;

public abstract class AbstractTypeVisitor8<R, P> extends AbstractTypeVisitor7<R, P>
{
    protected AbstractTypeVisitor8() {
    }
    
    @Override
    public abstract R visitIntersection(final IntersectionType p0, final P p1);
}
