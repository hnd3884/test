package sun.reflect.generics.repository;

import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.visitor.TypeTreeVisitor;
import sun.reflect.generics.factory.GenericsFactory;
import java.lang.reflect.TypeVariable;
import sun.reflect.generics.tree.Signature;

public abstract class GenericDeclRepository<S extends Signature> extends AbstractRepository<S>
{
    private volatile TypeVariable<?>[] typeParams;
    
    protected GenericDeclRepository(final String s, final GenericsFactory genericsFactory) {
        super(s, genericsFactory);
    }
    
    public TypeVariable<?>[] getTypeParameters() {
        TypeVariable<?>[] typeParams = this.typeParams;
        if (typeParams == null) {
            final FormalTypeParameter[] formalTypeParameters = this.getTree().getFormalTypeParameters();
            typeParams = new TypeVariable[formalTypeParameters.length];
            for (int i = 0; i < formalTypeParameters.length; ++i) {
                final Reifier reifier = this.getReifier();
                formalTypeParameters[i].accept(reifier);
                typeParams[i] = (TypeVariable)reifier.getResult();
            }
            this.typeParams = typeParams;
        }
        return typeParams.clone();
    }
}
