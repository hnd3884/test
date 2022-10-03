package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.core.compiler.CharOperation;

public interface IBinaryType extends IGenericType
{
    public static final char[][] NoInterface = CharOperation.NO_CHAR_CHAR;
    public static final IBinaryNestedType[] NoNestedType = new IBinaryNestedType[0];
    public static final IBinaryField[] NoField = new IBinaryField[0];
    public static final IBinaryMethod[] NoMethod = new IBinaryMethod[0];
    
    IBinaryAnnotation[] getAnnotations();
    
    IBinaryTypeAnnotation[] getTypeAnnotations();
    
    char[] getEnclosingMethod();
    
    char[] getEnclosingTypeName();
    
    IBinaryField[] getFields();
    
    char[] getGenericSignature();
    
    char[][] getInterfaceNames();
    
    IBinaryNestedType[] getMemberTypes();
    
    IBinaryMethod[] getMethods();
    
    char[][][] getMissingTypeNames();
    
    char[] getName();
    
    char[] getSourceName();
    
    char[] getSuperclassName();
    
    long getTagBits();
    
    boolean isAnonymous();
    
    boolean isLocal();
    
    boolean isMember();
    
    char[] sourceFileName();
    
    ITypeAnnotationWalker enrichWithExternalAnnotationsFor(final ITypeAnnotationWalker p0, final Object p1, final LookupEnvironment p2);
    
    BinaryTypeBinding.ExternalAnnotationStatus getExternalAnnotationStatus();
}
