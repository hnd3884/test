package sun.reflect.generics.repository;

import sun.reflect.generics.tree.Tree;
import sun.reflect.generics.visitor.Reifier;
import sun.reflect.generics.visitor.TypeTreeVisitor;
import sun.reflect.generics.parser.SignatureParser;
import sun.reflect.generics.factory.GenericsFactory;
import java.lang.reflect.Type;
import sun.reflect.generics.tree.TypeSignature;

public class FieldRepository extends AbstractRepository<TypeSignature>
{
    private Type genericType;
    
    protected FieldRepository(final String s, final GenericsFactory genericsFactory) {
        super(s, genericsFactory);
    }
    
    @Override
    protected TypeSignature parse(final String s) {
        return SignatureParser.make().parseTypeSig(s);
    }
    
    public static FieldRepository make(final String s, final GenericsFactory genericsFactory) {
        return new FieldRepository(s, genericsFactory);
    }
    
    public Type getGenericType() {
        if (this.genericType == null) {
            final Reifier reifier = this.getReifier();
            this.getTree().accept(reifier);
            this.genericType = reifier.getResult();
        }
        return this.genericType;
    }
}
