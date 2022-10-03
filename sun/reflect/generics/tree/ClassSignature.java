package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.Visitor;

public class ClassSignature implements Signature
{
    private final FormalTypeParameter[] formalTypeParams;
    private final ClassTypeSignature superclass;
    private final ClassTypeSignature[] superInterfaces;
    
    private ClassSignature(final FormalTypeParameter[] formalTypeParams, final ClassTypeSignature superclass, final ClassTypeSignature[] superInterfaces) {
        this.formalTypeParams = formalTypeParams;
        this.superclass = superclass;
        this.superInterfaces = superInterfaces;
    }
    
    public static ClassSignature make(final FormalTypeParameter[] array, final ClassTypeSignature classTypeSignature, final ClassTypeSignature[] array2) {
        return new ClassSignature(array, classTypeSignature, array2);
    }
    
    @Override
    public FormalTypeParameter[] getFormalTypeParameters() {
        return this.formalTypeParams;
    }
    
    public ClassTypeSignature getSuperclass() {
        return this.superclass;
    }
    
    public ClassTypeSignature[] getSuperInterfaces() {
        return this.superInterfaces;
    }
    
    public void accept(final Visitor<?> visitor) {
        visitor.visitClassSignature(this);
    }
}
