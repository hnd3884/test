package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ClassFile;
import java.util.List;

public class TypeAnnotationCodeStream extends StackMapFrameCodeStream
{
    public List allTypeAnnotationContexts;
    
    public TypeAnnotationCodeStream(final ClassFile givenClassFile) {
        super(givenClassFile);
        this.generateAttributes |= 0x20;
        this.allTypeAnnotationContexts = new ArrayList();
    }
    
    private void addAnnotationContext(final TypeReference typeReference, final int info, final int targetType, final ArrayAllocationExpression allocationExpression) {
        allocationExpression.getAllAnnotationContexts(targetType, info, this.allTypeAnnotationContexts);
    }
    
    private void addAnnotationContext(final TypeReference typeReference, final int info, final int targetType) {
        typeReference.getAllAnnotationContexts(targetType, info, this.allTypeAnnotationContexts);
    }
    
    private void addAnnotationContext(final TypeReference typeReference, final int info, final int typeIndex, final int targetType) {
        typeReference.getAllAnnotationContexts(targetType, info, typeIndex, this.allTypeAnnotationContexts);
    }
    
    @Override
    public void instance_of(final TypeReference typeReference, final TypeBinding typeBinding) {
        if (typeReference != null && (typeReference.bits & 0x100000) != 0x0) {
            this.addAnnotationContext(typeReference, this.position, 67);
        }
        super.instance_of(typeReference, typeBinding);
    }
    
    @Override
    public void multianewarray(final TypeReference typeReference, final TypeBinding typeBinding, final int dimensions, final ArrayAllocationExpression allocationExpression) {
        if (typeReference != null && (typeReference.bits & 0x100000) != 0x0) {
            this.addAnnotationContext(typeReference, this.position, 68, allocationExpression);
        }
        super.multianewarray(typeReference, typeBinding, dimensions, allocationExpression);
    }
    
    @Override
    public void new_(final TypeReference typeReference, final TypeBinding typeBinding) {
        if (typeReference != null && (typeReference.bits & 0x100000) != 0x0) {
            this.addAnnotationContext(typeReference, this.position, 68);
        }
        super.new_(typeReference, typeBinding);
    }
    
    @Override
    public void newArray(final TypeReference typeReference, final ArrayAllocationExpression allocationExpression, final ArrayBinding arrayBinding) {
        if (typeReference != null && (typeReference.bits & 0x100000) != 0x0) {
            this.addAnnotationContext(typeReference, this.position, 68, allocationExpression);
        }
        super.newArray(typeReference, allocationExpression, arrayBinding);
    }
    
    @Override
    public void checkcast(TypeReference typeReference, final TypeBinding typeBinding, final int currentPosition) {
        if (typeReference != null) {
            final TypeReference[] typeReferences = typeReference.getTypeReferences();
            for (int i = typeReferences.length - 1; i >= 0; --i) {
                typeReference = typeReferences[i];
                if (typeReference != null) {
                    if ((typeReference.bits & 0x100000) != 0x0) {
                        if (!typeReference.resolvedType.isBaseType()) {
                            this.addAnnotationContext(typeReference, this.position, i, 71);
                        }
                        else {
                            this.addAnnotationContext(typeReference, currentPosition, i, 71);
                        }
                    }
                    if (!typeReference.resolvedType.isBaseType()) {
                        super.checkcast(typeReference, typeReference.resolvedType, currentPosition);
                    }
                }
            }
        }
        else {
            super.checkcast(null, typeBinding, currentPosition);
        }
    }
    
    @Override
    public void invoke(final byte opcode, final MethodBinding methodBinding, final TypeBinding declaringClass, final TypeReference[] typeArguments) {
        if (typeArguments != null) {
            final int targetType = methodBinding.isConstructor() ? 72 : 73;
            for (int i = 0, max = typeArguments.length; i < max; ++i) {
                final TypeReference typeArgument = typeArguments[i];
                if ((typeArgument.bits & 0x100000) != 0x0) {
                    this.addAnnotationContext(typeArgument, this.position, i, targetType);
                }
            }
        }
        super.invoke(opcode, methodBinding, declaringClass, typeArguments);
    }
    
    @Override
    public void invokeDynamic(final int bootStrapIndex, final int argsSize, final int returnTypeSize, final char[] selector, final char[] signature, final boolean isConstructorReference, final TypeReference lhsTypeReference, final TypeReference[] typeArguments) {
        if (lhsTypeReference != null && (lhsTypeReference.bits & 0x100000) != 0x0) {
            if (isConstructorReference) {
                this.addAnnotationContext(lhsTypeReference, this.position, 0, 69);
            }
            else {
                this.addAnnotationContext(lhsTypeReference, this.position, 0, 70);
            }
        }
        if (typeArguments != null) {
            final int targetType = isConstructorReference ? 74 : 75;
            for (int i = 0, max = typeArguments.length; i < max; ++i) {
                final TypeReference typeArgument = typeArguments[i];
                if ((typeArgument.bits & 0x100000) != 0x0) {
                    this.addAnnotationContext(typeArgument, this.position, i, targetType);
                }
            }
        }
        super.invokeDynamic(bootStrapIndex, argsSize, returnTypeSize, selector, signature, isConstructorReference, lhsTypeReference, typeArguments);
    }
    
    @Override
    public void reset(final ClassFile givenClassFile) {
        super.reset(givenClassFile);
        this.allTypeAnnotationContexts = new ArrayList();
    }
    
    @Override
    public void init(final ClassFile targetClassFile) {
        super.init(targetClassFile);
        this.allTypeAnnotationContexts = new ArrayList();
    }
}
