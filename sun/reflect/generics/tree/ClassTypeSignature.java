package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;
import java.util.List;

public class ClassTypeSignature implements FieldTypeSignature
{
    private final List<SimpleClassTypeSignature> path;
    
    private ClassTypeSignature(final List<SimpleClassTypeSignature> path) {
        this.path = path;
    }
    
    public static ClassTypeSignature make(final List<SimpleClassTypeSignature> list) {
        return new ClassTypeSignature(list);
    }
    
    public List<SimpleClassTypeSignature> getPath() {
        return this.path;
    }
    
    @Override
    public void accept(final TypeTreeVisitor<?> typeTreeVisitor) {
        typeTreeVisitor.visitClassTypeSignature(this);
    }
}
