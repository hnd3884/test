package org.eclipse.jdt.internal.compiler.env;

public interface IBinaryMethod extends IGenericMethod
{
    IBinaryAnnotation[] getAnnotations();
    
    Object getDefaultValue();
    
    char[][] getExceptionTypeNames();
    
    char[] getGenericSignature();
    
    char[] getMethodDescriptor();
    
    IBinaryAnnotation[] getParameterAnnotations(final int p0, final char[] p1);
    
    int getAnnotatedParametersCount();
    
    char[] getSelector();
    
    long getTagBits();
    
    boolean isClinit();
    
    IBinaryTypeAnnotation[] getTypeAnnotations();
}
