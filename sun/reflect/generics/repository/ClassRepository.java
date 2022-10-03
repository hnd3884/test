package sun.reflect.generics.repository;

import sun.reflect.generics.tree.Tree;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.visitor.TypeTreeVisitor;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.factory.GenericsFactory;
import java.lang.reflect.Type;
import sun.reflect.generics.tree.ClassSignature;

public class ClassRepository extends GenericDeclRepository<ClassSignature>
{
    public static final ClassRepository NONE;
    private volatile Type superclass;
    private volatile Type[] superInterfaces;
    
    private ClassRepository(final String s, final GenericsFactory genericsFactory) {
        super(s, genericsFactory);
    }
    
    @Override
    protected ClassSignature parse(final String s) {
        return SignatureParser.make().parseClassSig(s);
    }
    
    public static ClassRepository make(final String s, final GenericsFactory genericsFactory) {
        return new ClassRepository(s, genericsFactory);
    }
    
    public Type getSuperclass() {
        Type superclass = this.superclass;
        if (superclass == null) {
            final Reifier reifier = this.getReifier();
            this.getTree().getSuperclass().accept(reifier);
            superclass = reifier.getResult();
            this.superclass = superclass;
        }
        return superclass;
    }
    
    public Type[] getSuperInterfaces() {
        Type[] superInterfaces = this.superInterfaces;
        if (superInterfaces == null) {
            final ClassTypeSignature[] superInterfaces2 = this.getTree().getSuperInterfaces();
            superInterfaces = new Type[superInterfaces2.length];
            for (int i = 0; i < superInterfaces2.length; ++i) {
                final Reifier reifier = this.getReifier();
                superInterfaces2[i].accept(reifier);
                superInterfaces[i] = reifier.getResult();
            }
            this.superInterfaces = superInterfaces;
        }
        return superInterfaces.clone();
    }
    
    static {
        NONE = make("Ljava/lang/Object;", null);
    }
}
