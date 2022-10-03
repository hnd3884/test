package org.eclipse.jdt.internal.compiler.env;

public interface IBinaryTypeAnnotation
{
    public static final int[] NO_TYPE_PATH = new int[0];
    
    IBinaryAnnotation getAnnotation();
    
    int getTargetType();
    
    int[] getTypePath();
    
    int getSupertypeIndex();
    
    int getTypeParameterIndex();
    
    int getBoundIndex();
    
    int getMethodFormalParameterIndex();
    
    int getThrowsTypeIndex();
}
