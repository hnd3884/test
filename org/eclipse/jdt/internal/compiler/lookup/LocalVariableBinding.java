package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;

public class LocalVariableBinding extends VariableBinding
{
    public int resolvedPosition;
    public static final int UNUSED = 0;
    public static final int USED = 1;
    public static final int FAKE_USED = 2;
    public int useFlag;
    public BlockScope declaringScope;
    public LocalDeclaration declaration;
    public int[] initializationPCs;
    public int initializationCount;
    public FakedTrackingVariable closeTracker;
    
    public LocalVariableBinding(final char[] name, final TypeBinding type, final int modifiers, final boolean isArgument) {
        super(name, type, modifiers, isArgument ? Constant.NotAConstant : null);
        this.initializationCount = 0;
        if (isArgument) {
            this.tagBits |= 0x400L;
        }
        this.tagBits |= 0x800L;
    }
    
    public LocalVariableBinding(final LocalDeclaration declaration, final TypeBinding type, final int modifiers, final boolean isArgument) {
        this(declaration.name, type, modifiers, isArgument);
        this.declaration = declaration;
    }
    
    public LocalVariableBinding(final LocalDeclaration declaration, final TypeBinding type, final int modifiers, final MethodScope declaringScope) {
        this(declaration, type, modifiers, true);
        this.declaringScope = declaringScope;
    }
    
    @Override
    public final int kind() {
        return 2;
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final StringBuffer buffer = new StringBuffer();
        final BlockScope scope = this.declaringScope;
        int occurenceCount = 0;
        if (scope != null) {
            final MethodScope methodScope = (MethodScope)((scope instanceof MethodScope) ? scope : scope.enclosingMethodScope());
            final ReferenceContext referenceContext = methodScope.referenceContext;
            if (referenceContext instanceof AbstractMethodDeclaration) {
                final MethodBinding methodBinding = ((AbstractMethodDeclaration)referenceContext).binding;
                if (methodBinding != null) {
                    buffer.append(methodBinding.computeUniqueKey(false));
                }
            }
            else if (referenceContext instanceof TypeDeclaration) {
                final TypeBinding typeBinding = ((TypeDeclaration)referenceContext).binding;
                if (typeBinding != null) {
                    buffer.append(typeBinding.computeUniqueKey(false));
                }
            }
            else if (referenceContext instanceof LambdaExpression) {
                final MethodBinding methodBinding = ((LambdaExpression)referenceContext).binding;
                if (methodBinding != null) {
                    buffer.append(methodBinding.computeUniqueKey(false));
                }
            }
            this.getScopeKey(scope, buffer);
            final LocalVariableBinding[] locals = scope.locals;
            for (int i = 0; i < scope.localIndex; ++i) {
                final LocalVariableBinding local = locals[i];
                if (CharOperation.equals(this.name, local.name)) {
                    if (this == local) {
                        break;
                    }
                    ++occurenceCount;
                }
            }
        }
        buffer.append('#');
        buffer.append(this.name);
        final boolean addParameterRank = this.isParameter() && this.declaringScope != null;
        if (occurenceCount > 0 || addParameterRank) {
            buffer.append('#');
            buffer.append(occurenceCount);
            if (addParameterRank) {
                int pos = -1;
                final LocalVariableBinding[] params = this.declaringScope.locals;
                for (int i = 0; i < params.length; ++i) {
                    if (params[i] == this) {
                        pos = i;
                        break;
                    }
                }
                if (pos > -1) {
                    buffer.append('#');
                    buffer.append(pos);
                }
            }
        }
        final int length = buffer.length();
        final char[] uniqueKey = new char[length];
        buffer.getChars(0, length, uniqueKey, 0);
        return uniqueKey;
    }
    
    @Override
    public AnnotationBinding[] getAnnotations() {
        if (this.declaringScope == null) {
            if ((this.tagBits & 0x200000000L) != 0x0L) {
                if (this.declaration == null) {
                    return Binding.NO_ANNOTATIONS;
                }
                final Annotation[] annotations = this.declaration.annotations;
                if (annotations != null) {
                    final int length = annotations.length;
                    final AnnotationBinding[] annotationBindings = new AnnotationBinding[length];
                    for (int i = 0; i < length; ++i) {
                        final AnnotationBinding compilerAnnotation = annotations[i].getCompilerAnnotation();
                        if (compilerAnnotation == null) {
                            return Binding.NO_ANNOTATIONS;
                        }
                        annotationBindings[i] = compilerAnnotation;
                    }
                    return annotationBindings;
                }
            }
            return Binding.NO_ANNOTATIONS;
        }
        final SourceTypeBinding sourceType = this.declaringScope.enclosingSourceType();
        if (sourceType == null) {
            return Binding.NO_ANNOTATIONS;
        }
        if ((this.tagBits & 0x200000000L) == 0x0L && (this.tagBits & 0x400L) != 0x0L && this.declaration != null) {
            final Annotation[] annotationNodes = this.declaration.annotations;
            if (annotationNodes != null) {
                ASTNode.resolveAnnotations(this.declaringScope, annotationNodes, this, true);
            }
        }
        return sourceType.retrieveAnnotations(this);
    }
    
    private void getScopeKey(final BlockScope scope, final StringBuffer buffer) {
        final int scopeIndex = scope.scopeIndex();
        if (scopeIndex != -1) {
            this.getScopeKey((BlockScope)scope.parent, buffer);
            buffer.append('#');
            buffer.append(scopeIndex);
        }
    }
    
    public boolean isSecret() {
        return this.declaration == null && (this.tagBits & 0x400L) == 0x0L;
    }
    
    public void recordInitializationEndPC(final int pc) {
        if (this.initializationPCs[(this.initializationCount - 1 << 1) + 1] == -1) {
            this.initializationPCs[(this.initializationCount - 1 << 1) + 1] = pc;
        }
    }
    
    public void recordInitializationStartPC(final int pc) {
        if (this.initializationPCs == null) {
            return;
        }
        if (this.initializationCount > 0) {
            final int previousEndPC = this.initializationPCs[(this.initializationCount - 1 << 1) + 1];
            if (previousEndPC == -1) {
                return;
            }
            if (previousEndPC == pc) {
                this.initializationPCs[(this.initializationCount - 1 << 1) + 1] = -1;
                return;
            }
        }
        final int index = this.initializationCount << 1;
        if (index == this.initializationPCs.length) {
            System.arraycopy(this.initializationPCs, 0, this.initializationPCs = new int[this.initializationCount << 2], 0, index);
        }
        this.initializationPCs[index] = pc;
        this.initializationPCs[index + 1] = -1;
        ++this.initializationCount;
    }
    
    @Override
    public void setAnnotations(final AnnotationBinding[] annotations, final Scope scope) {
        if (scope == null) {
            return;
        }
        final SourceTypeBinding sourceType = scope.enclosingSourceType();
        if (sourceType != null) {
            sourceType.storeAnnotations(this, annotations);
        }
    }
    
    public void resetInitializations() {
        this.initializationCount = 0;
        this.initializationPCs = null;
    }
    
    @Override
    public String toString() {
        String s = super.toString();
        switch (this.useFlag) {
            case 1: {
                s = String.valueOf(s) + "[pos: " + String.valueOf(this.resolvedPosition) + "]";
                break;
            }
            case 0: {
                s = String.valueOf(s) + "[pos: unused]";
                break;
            }
            case 2: {
                s = String.valueOf(s) + "[pos: fake_used]";
                break;
            }
        }
        s = String.valueOf(s) + "[id:" + String.valueOf(this.id) + "]";
        if (this.initializationCount > 0) {
            s = String.valueOf(s) + "[pc: ";
            for (int i = 0; i < this.initializationCount; ++i) {
                if (i > 0) {
                    s = String.valueOf(s) + ", ";
                }
                s = String.valueOf(s) + String.valueOf(this.initializationPCs[i << 1]) + "-" + ((this.initializationPCs[(i << 1) + 1] == -1) ? "?" : String.valueOf(this.initializationPCs[(i << 1) + 1]));
            }
            s = String.valueOf(s) + "]";
        }
        return s;
    }
    
    @Override
    public boolean isParameter() {
        return (this.tagBits & 0x400L) != 0x0L;
    }
    
    public boolean isCatchParameter() {
        return false;
    }
    
    public MethodBinding getEnclosingMethod() {
        final BlockScope blockScope = this.declaringScope;
        if (blockScope != null) {
            final ReferenceContext referenceContext = blockScope.referenceContext();
            if (referenceContext instanceof Initializer) {
                return null;
            }
            if (referenceContext instanceof AbstractMethodDeclaration) {
                return ((AbstractMethodDeclaration)referenceContext).binding;
            }
        }
        return null;
    }
}
