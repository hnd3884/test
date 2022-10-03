package org.eclipse.jdt.internal.compiler.lookup;

public class AptSourceLocalVariableBinding extends LocalVariableBinding
{
    public MethodBinding methodBinding;
    private LocalVariableBinding local;
    
    public AptSourceLocalVariableBinding(final LocalVariableBinding localVariableBinding, final MethodBinding methodBinding) {
        super(localVariableBinding.name, localVariableBinding.type, localVariableBinding.modifiers, true);
        this.constant = localVariableBinding.constant;
        this.declaration = localVariableBinding.declaration;
        this.declaringScope = localVariableBinding.declaringScope;
        this.id = localVariableBinding.id;
        this.resolvedPosition = localVariableBinding.resolvedPosition;
        this.tagBits = localVariableBinding.tagBits;
        this.useFlag = localVariableBinding.useFlag;
        this.initializationCount = localVariableBinding.initializationCount;
        this.initializationPCs = localVariableBinding.initializationPCs;
        this.methodBinding = methodBinding;
        this.local = localVariableBinding;
    }
    
    @Override
    public AnnotationBinding[] getAnnotations() {
        return this.local.getAnnotations();
    }
}
