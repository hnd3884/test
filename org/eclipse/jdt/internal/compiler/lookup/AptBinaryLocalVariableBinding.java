package org.eclipse.jdt.internal.compiler.lookup;

public class AptBinaryLocalVariableBinding extends LocalVariableBinding
{
    AnnotationBinding[] annotationBindings;
    public MethodBinding methodBinding;
    
    public AptBinaryLocalVariableBinding(final char[] name, final TypeBinding type, final int modifiers, final AnnotationBinding[] annotationBindings, final MethodBinding methodBinding) {
        super(name, type, modifiers, true);
        this.annotationBindings = ((annotationBindings == null) ? Binding.NO_ANNOTATIONS : annotationBindings);
        this.methodBinding = methodBinding;
    }
    
    @Override
    public AnnotationBinding[] getAnnotations() {
        return this.annotationBindings;
    }
}
