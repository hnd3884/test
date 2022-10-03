package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;

public class RawTypeBinding extends ParameterizedTypeBinding
{
    public RawTypeBinding(final ReferenceBinding type, final ReferenceBinding enclosingType, final LookupEnvironment environment) {
        super(type, null, enclosingType, environment);
        this.tagBits &= 0xFFFFFFFFFFFFFF7FL;
        if ((type.tagBits & 0x80L) != 0x0L) {
            if (type instanceof MissingTypeBinding) {
                this.tagBits |= 0x80L;
            }
            else if (type instanceof ParameterizedTypeBinding) {
                final ParameterizedTypeBinding parameterizedTypeBinding = (ParameterizedTypeBinding)type;
                if (parameterizedTypeBinding.genericType() instanceof MissingTypeBinding) {
                    this.tagBits |= 0x80L;
                }
            }
        }
        if (enclosingType != null && (enclosingType.tagBits & 0x80L) != 0x0L) {
            if (enclosingType instanceof MissingTypeBinding) {
                this.tagBits |= 0x80L;
            }
            else if (enclosingType instanceof ParameterizedTypeBinding) {
                final ParameterizedTypeBinding parameterizedTypeBinding = (ParameterizedTypeBinding)enclosingType;
                if (parameterizedTypeBinding.genericType() instanceof MissingTypeBinding) {
                    this.tagBits |= 0x80L;
                }
            }
        }
        if (enclosingType == null || (enclosingType.modifiers & 0x40000000) == 0x0) {
            this.modifiers &= 0xBFFFFFFF;
        }
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final StringBuffer sig = new StringBuffer(10);
        if (this.isMemberType() && this.enclosingType().isParameterizedType()) {
            final char[] typeSig = this.enclosingType().computeUniqueKey(false);
            sig.append(typeSig, 0, typeSig.length - 1);
            sig.append('.').append(this.sourceName()).append('<').append('>').append(';');
        }
        else {
            sig.append(this.genericType().computeUniqueKey(false));
            sig.insert(sig.length() - 1, "<>");
        }
        final int sigLength = sig.length();
        final char[] uniqueKey = new char[sigLength];
        sig.getChars(0, sigLength, uniqueKey, 0);
        return uniqueKey;
    }
    
    @Override
    public TypeBinding clone(final TypeBinding outerType) {
        return new RawTypeBinding(this.actualType(), (ReferenceBinding)outerType, this.environment);
    }
    
    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        final ReferenceBinding unannotatedGenericType = (ReferenceBinding)this.environment.getUnannotatedType(this.genericType());
        final AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        return this.environment.createRawType(unannotatedGenericType, this.enclosingType(), newAnnotations);
    }
    
    @Override
    public ParameterizedMethodBinding createParameterizedMethod(final MethodBinding originalMethod) {
        if (originalMethod.typeVariables == Binding.NO_TYPE_VARIABLES || originalMethod.isStatic()) {
            return super.createParameterizedMethod(originalMethod);
        }
        return this.environment.createParameterizedGenericMethod(originalMethod, this);
    }
    
    @Override
    public boolean isParameterizedType() {
        return false;
    }
    
    @Override
    public int kind() {
        return 1028;
    }
    
    @Override
    public String debugName() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        final StringBuffer nameBuffer = new StringBuffer(10);
        nameBuffer.append(this.actualType().sourceName()).append("#RAW");
        return nameBuffer.toString();
    }
    
    @Override
    public String annotatedDebugName() {
        final StringBuffer buffer = new StringBuffer(super.annotatedDebugName());
        buffer.append("#RAW");
        return buffer.toString();
    }
    
    @Override
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature == null) {
            if ((this.modifiers & 0x40000000) == 0x0) {
                this.genericTypeSignature = this.genericType().signature();
            }
            else {
                final StringBuffer sig = new StringBuffer(10);
                if (this.isMemberType()) {
                    final ReferenceBinding enclosing = this.enclosingType();
                    final char[] typeSig = enclosing.genericTypeSignature();
                    sig.append(typeSig, 0, typeSig.length - 1);
                    if ((enclosing.modifiers & 0x40000000) != 0x0) {
                        sig.append('.');
                    }
                    else {
                        sig.append('$');
                    }
                    sig.append(this.sourceName());
                }
                else {
                    final char[] typeSig2 = this.genericType().signature();
                    sig.append(typeSig2, 0, typeSig2.length - 1);
                }
                sig.append(';');
                final int sigLength = sig.length();
                sig.getChars(0, sigLength, this.genericTypeSignature = new char[sigLength], 0);
            }
        }
        return this.genericTypeSignature;
    }
    
    @Override
    public boolean isEquivalentTo(final TypeBinding otherType) {
        if (TypeBinding.equalsEquals(this, otherType) || TypeBinding.equalsEquals(this.erasure(), otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        switch (otherType.kind()) {
            case 516:
            case 8196: {
                return ((WildcardBinding)otherType).boundCheck(this);
            }
            case 260:
            case 1028:
            case 2052: {
                return TypeBinding.equalsEquals(this.erasure(), otherType.erasure());
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean isProvablyDistinct(final TypeBinding otherType) {
        if (TypeBinding.equalsEquals(this, otherType) || TypeBinding.equalsEquals(this.erasure(), otherType)) {
            return false;
        }
        if (otherType == null) {
            return true;
        }
        switch (otherType.kind()) {
            case 260:
            case 1028:
            case 2052: {
                return TypeBinding.notEquals(this.erasure(), otherType.erasure());
            }
            default: {
                return true;
            }
        }
    }
    
    @Override
    public boolean isProperType(final boolean admitCapture18) {
        final TypeBinding actualType = this.actualType();
        return actualType != null && actualType.isProperType(admitCapture18);
    }
    
    @Override
    protected void initializeArguments() {
        final TypeVariableBinding[] typeVariables = this.genericType().typeVariables();
        final int length = typeVariables.length;
        final TypeBinding[] typeArguments = new TypeBinding[length];
        for (int i = 0; i < length; ++i) {
            typeArguments[i] = this.environment.convertToRawType(typeVariables[i].erasure(), false);
        }
        this.arguments = typeArguments;
    }
    
    @Override
    public ParameterizedTypeBinding capture(final Scope scope, final int start, final int end) {
        return this;
    }
    
    @Override
    public TypeBinding uncapture(final Scope scope) {
        return this;
    }
    
    @Override
    TypeBinding substituteInferenceVariable(final InferenceVariable var, final TypeBinding substituteType) {
        return this;
    }
    
    @Override
    public MethodBinding getSingleAbstractMethod(final Scope scope, final boolean replaceWildcards) {
        final int index = replaceWildcards ? 0 : 1;
        if (this.singleAbstractMethod != null) {
            if (this.singleAbstractMethod[index] != null) {
                return this.singleAbstractMethod[index];
            }
        }
        else {
            this.singleAbstractMethod = new MethodBinding[2];
        }
        final ReferenceBinding genericType = this.genericType();
        final MethodBinding theAbstractMethod = genericType.getSingleAbstractMethod(scope, replaceWildcards);
        if (theAbstractMethod == null || !theAbstractMethod.isValidBinding()) {
            return this.singleAbstractMethod[index] = theAbstractMethod;
        }
        ReferenceBinding declaringType = (ReferenceBinding)scope.environment().convertToRawType(genericType, true);
        declaringType = (ReferenceBinding)declaringType.findSuperTypeOriginatingFrom(theAbstractMethod.declaringClass);
        final MethodBinding[] choices = declaringType.getMethods(theAbstractMethod.selector);
        for (int i = 0, length = choices.length; i < length; ++i) {
            final MethodBinding method = choices[i];
            if (method.isAbstract() && !method.redeclaresPublicObjectMethod(scope)) {
                this.singleAbstractMethod[index] = method;
                break;
            }
        }
        return this.singleAbstractMethod[index];
    }
    
    @Override
    public boolean mentionsAny(final TypeBinding[] parameters, final int idx) {
        return false;
    }
    
    @Override
    public char[] readableName() {
        char[] readableName;
        if (this.isMemberType()) {
            readableName = CharOperation.concat(this.enclosingType().readableName(), this.sourceName, '.');
        }
        else {
            readableName = CharOperation.concatWith(this.actualType().compoundName, '.');
        }
        return readableName;
    }
    
    @Override
    public char[] shortReadableName() {
        char[] shortReadableName;
        if (this.isMemberType()) {
            shortReadableName = CharOperation.concat(this.enclosingType().shortReadableName(), this.sourceName, '.');
        }
        else {
            shortReadableName = this.actualType().sourceName;
        }
        return shortReadableName;
    }
}
