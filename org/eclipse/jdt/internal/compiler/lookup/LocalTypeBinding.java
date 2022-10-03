package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;

public final class LocalTypeBinding extends NestedTypeBinding
{
    static final char[] LocalTypePrefix;
    private InnerEmulationDependency[] dependents;
    public CaseStatement enclosingCase;
    public int sourceStart;
    public MethodBinding enclosingMethod;
    
    static {
        LocalTypePrefix = new char[] { '$', 'L', 'o', 'c', 'a', 'l', '$' };
    }
    
    public LocalTypeBinding(final ClassScope scope, final SourceTypeBinding enclosingType, final CaseStatement switchCase) {
        super(new char[][] { CharOperation.concat(LocalTypeBinding.LocalTypePrefix, scope.referenceContext.name) }, scope, enclosingType);
        final TypeDeclaration typeDeclaration = scope.referenceContext;
        if ((typeDeclaration.bits & 0x200) != 0x0) {
            this.tagBits |= 0x834L;
        }
        else {
            this.tagBits |= 0x814L;
        }
        this.enclosingCase = switchCase;
        this.sourceStart = typeDeclaration.sourceStart;
        final MethodScope methodScope = scope.enclosingMethodScope();
        final MethodBinding methodBinding = methodScope.referenceMethodBinding();
        if (methodBinding != null) {
            this.enclosingMethod = methodBinding;
        }
    }
    
    public LocalTypeBinding(final LocalTypeBinding prototype) {
        super(prototype);
        this.dependents = prototype.dependents;
        this.enclosingCase = prototype.enclosingCase;
        this.sourceStart = prototype.sourceStart;
        this.enclosingMethod = prototype.enclosingMethod;
    }
    
    public void addInnerEmulationDependent(final BlockScope dependentScope, final boolean wasEnclosingInstanceSupplied) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        int index;
        if (this.dependents == null) {
            index = 0;
            this.dependents = new InnerEmulationDependency[1];
        }
        else {
            index = this.dependents.length;
            for (int i = 0; i < index; ++i) {
                if (this.dependents[i].scope == dependentScope) {
                    return;
                }
            }
            System.arraycopy(this.dependents, 0, this.dependents = new InnerEmulationDependency[index + 1], 0, index);
        }
        this.dependents[index] = new InnerEmulationDependency(dependentScope, wasEnclosingInstanceSupplied);
    }
    
    @Override
    public MethodBinding enclosingMethod() {
        return this.enclosingMethod;
    }
    
    public ReferenceBinding anonymousOriginalSuperType() {
        if (!this.isPrototype()) {
            return ((LocalTypeBinding)this.prototype).anonymousOriginalSuperType();
        }
        if (this.superclass == null && this.scope != null) {
            return this.scope.getJavaLangObject();
        }
        if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
            return this.superInterfaces[0];
        }
        if ((this.tagBits & 0x20000L) == 0x0L) {
            return this.superclass;
        }
        if (this.scope != null) {
            final TypeReference typeReference = this.scope.referenceContext.allocation.type;
            if (typeReference != null) {
                return (ReferenceBinding)typeReference.resolvedType;
            }
        }
        return this.superclass;
    }
    
    @Override
    protected void checkRedundantNullnessDefaultRecurse(final ASTNode location, final Annotation[] annotations, final long nullBits, final boolean useNullTypeAnnotations) {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        long outerDefault = 0L;
        if (this.enclosingMethod != null) {
            outerDefault = (useNullTypeAnnotations ? this.enclosingMethod.defaultNullness : (this.enclosingMethod.tagBits & 0x600000000000000L));
        }
        if (outerDefault != 0L) {
            if (outerDefault == nullBits) {
                this.scope.problemReporter().nullDefaultAnnotationIsRedundant(location, annotations, this.enclosingMethod);
            }
            return;
        }
        super.checkRedundantNullnessDefaultRecurse(location, annotations, nullBits, useNullTypeAnnotations);
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        if (!this.isPrototype()) {
            return this.prototype.computeUniqueKey(isLeaf);
        }
        final char[] outerKey = this.outermostEnclosingType().computeUniqueKey(isLeaf);
        final int semicolon = CharOperation.lastIndexOf(';', outerKey);
        final StringBuffer sig = new StringBuffer();
        sig.append(outerKey, 0, semicolon);
        sig.append('$');
        sig.append(String.valueOf(this.sourceStart));
        if (!this.isAnonymousType()) {
            sig.append('$');
            sig.append(this.sourceName);
        }
        sig.append(outerKey, semicolon, outerKey.length - semicolon);
        final int sigLength = sig.length();
        final char[] uniqueKey = new char[sigLength];
        sig.getChars(0, sigLength, uniqueKey, 0);
        return uniqueKey;
    }
    
    @Override
    public char[] constantPoolName() {
        if (this.constantPoolName != null) {
            return this.constantPoolName;
        }
        if (!this.isPrototype()) {
            return this.constantPoolName = this.prototype.constantPoolName();
        }
        if (this.constantPoolName == null && this.scope != null) {
            this.constantPoolName = this.scope.compilationUnitScope().computeConstantPoolName(this);
        }
        return this.constantPoolName;
    }
    
    @Override
    public TypeBinding clone(final TypeBinding outerType) {
        final LocalTypeBinding copy = new LocalTypeBinding(this);
        copy.enclosingType = (SourceTypeBinding)outerType;
        return copy;
    }
    
    @Override
    public int hashCode() {
        return this.enclosingType.hashCode();
    }
    
    @Override
    public char[] genericTypeSignature() {
        if (!this.isPrototype()) {
            return this.prototype.genericTypeSignature();
        }
        if (this.genericReferenceTypeSignature == null && this.constantPoolName == null) {
            if (this.isAnonymousType()) {
                this.setConstantPoolName(this.superclass().sourceName());
            }
            else {
                this.setConstantPoolName(this.sourceName());
            }
        }
        return super.genericTypeSignature();
    }
    
    @Override
    public char[] readableName() {
        char[] readableName;
        if (this.isAnonymousType()) {
            readableName = CharOperation.concat(TypeConstants.ANONYM_PREFIX, this.anonymousOriginalSuperType().readableName(), TypeConstants.ANONYM_SUFFIX);
        }
        else if (this.isMemberType()) {
            readableName = CharOperation.concat(this.enclosingType().readableName(), this.sourceName, '.');
        }
        else {
            readableName = this.sourceName;
        }
        final TypeVariableBinding[] typeVars;
        if ((typeVars = this.typeVariables()) != Binding.NO_TYPE_VARIABLES) {
            final StringBuffer nameBuffer = new StringBuffer(10);
            nameBuffer.append(readableName).append('<');
            for (int i = 0, length = typeVars.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(typeVars[i].readableName());
            }
            nameBuffer.append('>');
            final int nameLength = nameBuffer.length();
            readableName = new char[nameLength];
            nameBuffer.getChars(0, nameLength, readableName, 0);
        }
        return readableName;
    }
    
    @Override
    public char[] shortReadableName() {
        char[] shortReadableName;
        if (this.isAnonymousType()) {
            shortReadableName = CharOperation.concat(TypeConstants.ANONYM_PREFIX, this.anonymousOriginalSuperType().shortReadableName(), TypeConstants.ANONYM_SUFFIX);
        }
        else if (this.isMemberType()) {
            shortReadableName = CharOperation.concat(this.enclosingType().shortReadableName(), this.sourceName, '.');
        }
        else {
            shortReadableName = this.sourceName;
        }
        final TypeVariableBinding[] typeVars;
        if ((typeVars = this.typeVariables()) != Binding.NO_TYPE_VARIABLES) {
            final StringBuffer nameBuffer = new StringBuffer(10);
            nameBuffer.append(shortReadableName).append('<');
            for (int i = 0, length = typeVars.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(typeVars[i].shortReadableName());
            }
            nameBuffer.append('>');
            final int nameLength = nameBuffer.length();
            shortReadableName = new char[nameLength];
            nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        }
        return shortReadableName;
    }
    
    public void setAsMemberType() {
        if (!this.isPrototype()) {
            this.tagBits |= 0x80CL;
            ((LocalTypeBinding)this.prototype).setAsMemberType();
            return;
        }
        this.tagBits |= 0x80CL;
    }
    
    public void setConstantPoolName(final char[] computedConstantPoolName) {
        if (!this.isPrototype()) {
            this.constantPoolName = computedConstantPoolName;
            ((LocalTypeBinding)this.prototype).setConstantPoolName(computedConstantPoolName);
            return;
        }
        this.constantPoolName = computedConstantPoolName;
    }
    
    @Override
    public char[] signature() {
        if (!this.isPrototype()) {
            return this.prototype.signature();
        }
        if (this.signature == null && this.constantPoolName == null) {
            if (this.isAnonymousType()) {
                this.setConstantPoolName(this.superclass().sourceName());
            }
            else {
                this.setConstantPoolName(this.sourceName());
            }
        }
        return super.signature();
    }
    
    @Override
    public char[] sourceName() {
        if (this.isAnonymousType()) {
            return CharOperation.concat(TypeConstants.ANONYM_PREFIX, this.anonymousOriginalSuperType().sourceName(), TypeConstants.ANONYM_SUFFIX);
        }
        return this.sourceName;
    }
    
    @Override
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return String.valueOf(this.annotatedDebugName()) + " (local)";
        }
        if (this.isAnonymousType()) {
            return "Anonymous type : " + super.toString();
        }
        if (this.isMemberType()) {
            return "Local member type : " + new String(this.sourceName()) + " " + super.toString();
        }
        return "Local type : " + new String(this.sourceName()) + " " + super.toString();
    }
    
    @Override
    public void updateInnerEmulationDependents() {
        if (!this.isPrototype()) {
            throw new IllegalStateException();
        }
        if (this.dependents != null) {
            for (int i = 0; i < this.dependents.length; ++i) {
                final InnerEmulationDependency dependency = this.dependents[i];
                dependency.scope.propagateInnerEmulation(this, dependency.wasEnclosingInstanceSupplied);
            }
        }
    }
}
