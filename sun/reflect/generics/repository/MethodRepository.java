package sun.reflect.generics.repository;

import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.visitor.TypeTreeVisitor;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.factory.GenericsFactory;
import java.lang.reflect.Type;

public class MethodRepository extends ConstructorRepository
{
    private Type returnType;
    
    private MethodRepository(final String s, final GenericsFactory genericsFactory) {
        super(s, genericsFactory);
    }
    
    public static MethodRepository make(final String s, final GenericsFactory genericsFactory) {
        return new MethodRepository(s, genericsFactory);
    }
    
    public Type getReturnType() {
        if (this.returnType == null) {
            final Reifier reifier = this.getReifier();
            this.getTree().getReturnType().accept(reifier);
            this.returnType = reifier.getResult();
        }
        return this.returnType;
    }
}
