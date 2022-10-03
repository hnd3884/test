package javax.lang.model.type;

public interface TypeVisitor<R, P>
{
    R visit(final TypeMirror p0, final P p1);
    
    R visit(final TypeMirror p0);
    
    R visitPrimitive(final PrimitiveType p0, final P p1);
    
    R visitNull(final NullType p0, final P p1);
    
    R visitArray(final ArrayType p0, final P p1);
    
    R visitDeclared(final DeclaredType p0, final P p1);
    
    R visitError(final ErrorType p0, final P p1);
    
    R visitTypeVariable(final TypeVariable p0, final P p1);
    
    R visitWildcard(final WildcardType p0, final P p1);
    
    R visitExecutable(final ExecutableType p0, final P p1);
    
    R visitNoType(final NoType p0, final P p1);
    
    R visitUnknown(final TypeMirror p0, final P p1);
    
    R visitUnion(final UnionType p0, final P p1);
    
    R visitIntersection(final IntersectionType p0, final P p1);
}
