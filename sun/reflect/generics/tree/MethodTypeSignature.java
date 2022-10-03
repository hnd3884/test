package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.Visitor;

public class MethodTypeSignature implements Signature
{
    private final FormalTypeParameter[] formalTypeParams;
    private final TypeSignature[] parameterTypes;
    private final ReturnType returnType;
    private final FieldTypeSignature[] exceptionTypes;
    
    private MethodTypeSignature(final FormalTypeParameter[] formalTypeParams, final TypeSignature[] parameterTypes, final ReturnType returnType, final FieldTypeSignature[] exceptionTypes) {
        this.formalTypeParams = formalTypeParams;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.exceptionTypes = exceptionTypes;
    }
    
    public static MethodTypeSignature make(final FormalTypeParameter[] array, final TypeSignature[] array2, final ReturnType returnType, final FieldTypeSignature[] array3) {
        return new MethodTypeSignature(array, array2, returnType, array3);
    }
    
    @Override
    public FormalTypeParameter[] getFormalTypeParameters() {
        return this.formalTypeParams;
    }
    
    public TypeSignature[] getParameterTypes() {
        return this.parameterTypes;
    }
    
    public ReturnType getReturnType() {
        return this.returnType;
    }
    
    public FieldTypeSignature[] getExceptionTypes() {
        return this.exceptionTypes;
    }
    
    public void accept(final Visitor<?> visitor) {
        visitor.visitMethodTypeSignature(this);
    }
}
