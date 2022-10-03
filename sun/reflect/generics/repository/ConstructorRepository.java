package sun.reflect.generics.repository;

import sun.reflect.generics.tree.Tree;
import sun.reflect.generics.tree.FieldTypeSignature;
import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.visitor.TypeTreeVisitor;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.factory.GenericsFactory;
import java.lang.reflect.Type;
import sun.reflect.generics.tree.MethodTypeSignature;

public class ConstructorRepository extends GenericDeclRepository<MethodTypeSignature>
{
    private Type[] paramTypes;
    private Type[] exceptionTypes;
    
    protected ConstructorRepository(final String s, final GenericsFactory genericsFactory) {
        super(s, genericsFactory);
    }
    
    @Override
    protected MethodTypeSignature parse(final String s) {
        return SignatureParser.make().parseMethodSig(s);
    }
    
    public static ConstructorRepository make(final String s, final GenericsFactory genericsFactory) {
        return new ConstructorRepository(s, genericsFactory);
    }
    
    public Type[] getParameterTypes() {
        if (this.paramTypes == null) {
            final TypeSignature[] parameterTypes = this.getTree().getParameterTypes();
            final Type[] paramTypes = new Type[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; ++i) {
                final Reifier reifier = this.getReifier();
                parameterTypes[i].accept(reifier);
                paramTypes[i] = reifier.getResult();
            }
            this.paramTypes = paramTypes;
        }
        return this.paramTypes.clone();
    }
    
    public Type[] getExceptionTypes() {
        if (this.exceptionTypes == null) {
            final FieldTypeSignature[] exceptionTypes = this.getTree().getExceptionTypes();
            final Type[] exceptionTypes2 = new Type[exceptionTypes.length];
            for (int i = 0; i < exceptionTypes.length; ++i) {
                final Reifier reifier = this.getReifier();
                exceptionTypes[i].accept(reifier);
                exceptionTypes2[i] = reifier.getResult();
            }
            this.exceptionTypes = exceptionTypes2;
        }
        return this.exceptionTypes.clone();
    }
}
