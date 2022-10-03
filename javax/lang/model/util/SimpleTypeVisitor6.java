package javax.lang.model.util;

import javax.lang.model.type.NoType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.SourceVersion;
import javax.annotation.processing.SupportedSourceVersion;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleTypeVisitor6<R, P> extends AbstractTypeVisitor6<R, P>
{
    protected final R DEFAULT_VALUE;
    
    protected SimpleTypeVisitor6() {
        this.DEFAULT_VALUE = null;
    }
    
    protected SimpleTypeVisitor6(final R default_VALUE) {
        this.DEFAULT_VALUE = default_VALUE;
    }
    
    protected R defaultAction(final TypeMirror typeMirror, final P p2) {
        return this.DEFAULT_VALUE;
    }
    
    @Override
    public R visitPrimitive(final PrimitiveType primitiveType, final P p2) {
        return this.defaultAction(primitiveType, p2);
    }
    
    @Override
    public R visitNull(final NullType nullType, final P p2) {
        return this.defaultAction(nullType, p2);
    }
    
    @Override
    public R visitArray(final ArrayType arrayType, final P p2) {
        return this.defaultAction(arrayType, p2);
    }
    
    @Override
    public R visitDeclared(final DeclaredType declaredType, final P p2) {
        return this.defaultAction(declaredType, p2);
    }
    
    @Override
    public R visitError(final ErrorType errorType, final P p2) {
        return this.defaultAction(errorType, p2);
    }
    
    @Override
    public R visitTypeVariable(final TypeVariable typeVariable, final P p2) {
        return this.defaultAction(typeVariable, p2);
    }
    
    @Override
    public R visitWildcard(final WildcardType wildcardType, final P p2) {
        return this.defaultAction(wildcardType, p2);
    }
    
    @Override
    public R visitExecutable(final ExecutableType executableType, final P p2) {
        return this.defaultAction(executableType, p2);
    }
    
    @Override
    public R visitNoType(final NoType noType, final P p2) {
        return this.defaultAction(noType, p2);
    }
}
