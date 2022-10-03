package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

public class CaptureBinding extends TypeVariableBinding
{
    public TypeBinding lowerBound;
    public WildcardBinding wildcard;
    public int captureID;
    public ReferenceBinding sourceType;
    public int start;
    public int end;
    public ASTNode cud;
    TypeBinding pendingSubstitute;
    
    public CaptureBinding(final WildcardBinding wildcard, final ReferenceBinding sourceType, final int start, final int end, final ASTNode cud, final int captureID) {
        super(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX, wildcard.environment);
        this.wildcard = wildcard;
        this.modifiers = 1073741825;
        this.fPackage = wildcard.fPackage;
        this.sourceType = sourceType;
        this.start = start;
        this.end = end;
        this.captureID = captureID;
        this.tagBits |= 0x2000000000000000L;
        this.cud = cud;
        if (wildcard.hasTypeAnnotations()) {
            final CaptureBinding unannotated = (CaptureBinding)this.clone(null);
            unannotated.wildcard = (WildcardBinding)this.wildcard.unannotated();
            this.environment.getUnannotatedType(unannotated);
            this.id = unannotated.id;
            this.environment.typeSystem.cacheDerivedType(this, unannotated, this);
            super.setTypeAnnotations(wildcard.getTypeAnnotations(), wildcard.environment.globalOptions.isAnnotationBasedNullAnalysisEnabled);
            if (wildcard.hasNullTypeAnnotations()) {
                this.tagBits |= 0x100000L;
            }
        }
        else {
            this.computeId(this.environment);
        }
    }
    
    protected CaptureBinding(final ReferenceBinding sourceType, final char[] sourceName, final int start, final int end, final int captureID, final LookupEnvironment environment) {
        super(sourceName, null, 0, environment);
        this.modifiers = 1073741825;
        this.sourceType = sourceType;
        this.start = start;
        this.end = end;
        this.captureID = captureID;
    }
    
    public CaptureBinding(final CaptureBinding prototype) {
        super(prototype);
        this.wildcard = prototype.wildcard;
        this.sourceType = prototype.sourceType;
        this.start = prototype.start;
        this.end = prototype.end;
        this.captureID = prototype.captureID;
        this.lowerBound = prototype.lowerBound;
        this.tagBits |= (prototype.tagBits & 0x2000000000000000L);
        this.cud = prototype.cud;
    }
    
    @Override
    public TypeBinding clone(final TypeBinding enclosingType) {
        return new CaptureBinding(this);
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final StringBuffer buffer = new StringBuffer();
        if (isLeaf) {
            buffer.append(this.sourceType.computeUniqueKey(false));
            buffer.append('&');
        }
        buffer.append(TypeConstants.WILDCARD_CAPTURE);
        buffer.append(this.wildcard.computeUniqueKey(false));
        buffer.append(this.end);
        buffer.append(';');
        final int length = buffer.length();
        final char[] uniqueKey = new char[length];
        buffer.getChars(0, length, uniqueKey, 0);
        return uniqueKey;
    }
    
    @Override
    public String debugName() {
        if (this.wildcard != null) {
            final StringBuffer buffer = new StringBuffer(10);
            final AnnotationBinding[] annotations = this.getTypeAnnotations();
            for (int i = 0, length = (annotations == null) ? 0 : annotations.length; i < length; ++i) {
                buffer.append(annotations[i]);
                buffer.append(' ');
            }
            buffer.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX).append(this.captureID).append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX).append(this.wildcard.debugName());
            return buffer.toString();
        }
        return super.debugName();
    }
    
    @Override
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature == null) {
            this.genericTypeSignature = CharOperation.concat(TypeConstants.WILDCARD_CAPTURE, this.wildcard.genericTypeSignature());
        }
        return this.genericTypeSignature;
    }
    
    public void initializeBounds(final Scope scope, final ParameterizedTypeBinding capturedParameterizedType) {
        final TypeVariableBinding wildcardVariable = this.wildcard.typeVariable();
        if (wildcardVariable == null) {
            final TypeBinding originalWildcardBound = this.wildcard.bound;
            switch (this.wildcard.boundKind) {
                case 1: {
                    final TypeBinding capturedWildcardBound = originalWildcardBound.capture(scope, this.start, this.end);
                    if (originalWildcardBound.isInterface()) {
                        this.setSuperClass(scope.getJavaLangObject());
                        this.setSuperInterfaces(new ReferenceBinding[] { (ReferenceBinding)capturedWildcardBound });
                    }
                    else {
                        if (capturedWildcardBound.isArrayType() || TypeBinding.equalsEquals(capturedWildcardBound, this)) {
                            this.setSuperClass(scope.getJavaLangObject());
                        }
                        else {
                            this.setSuperClass((ReferenceBinding)capturedWildcardBound);
                        }
                        this.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
                    }
                    this.setFirstBound(capturedWildcardBound);
                    if ((capturedWildcardBound.tagBits & 0x20000000L) == 0x0L) {
                        this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                        break;
                    }
                    break;
                }
                case 0: {
                    this.setSuperClass(scope.getJavaLangObject());
                    this.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
                    this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                    break;
                }
                case 2: {
                    this.setSuperClass(scope.getJavaLangObject());
                    this.setSuperInterfaces(Binding.NO_SUPERINTERFACES);
                    this.lowerBound = this.wildcard.bound;
                    if ((originalWildcardBound.tagBits & 0x20000000L) == 0x0L) {
                        this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                        break;
                    }
                    break;
                }
            }
            return;
        }
        final ReferenceBinding originalVariableSuperclass = wildcardVariable.superclass;
        ReferenceBinding substitutedVariableSuperclass = (ReferenceBinding)Scope.substitute(capturedParameterizedType, originalVariableSuperclass);
        if (TypeBinding.equalsEquals(substitutedVariableSuperclass, this)) {
            substitutedVariableSuperclass = originalVariableSuperclass;
        }
        final ReferenceBinding[] originalVariableInterfaces = wildcardVariable.superInterfaces();
        ReferenceBinding[] substitutedVariableInterfaces = Scope.substitute(capturedParameterizedType, originalVariableInterfaces);
        if (substitutedVariableInterfaces != originalVariableInterfaces) {
            for (int i = 0, length = substitutedVariableInterfaces.length; i < length; ++i) {
                if (TypeBinding.equalsEquals(substitutedVariableInterfaces[i], this)) {
                    substitutedVariableInterfaces[i] = originalVariableInterfaces[i];
                }
            }
        }
        final TypeBinding originalWildcardBound2 = this.wildcard.bound;
        switch (this.wildcard.boundKind) {
            case 1: {
                final TypeBinding capturedWildcardBound2 = originalWildcardBound2.capture(scope, this.start, this.end);
                if (originalWildcardBound2.isInterface()) {
                    this.setSuperClass(substitutedVariableSuperclass);
                    if (substitutedVariableInterfaces == Binding.NO_SUPERINTERFACES) {
                        this.setSuperInterfaces(new ReferenceBinding[] { (ReferenceBinding)capturedWildcardBound2 });
                    }
                    else {
                        final int length2 = substitutedVariableInterfaces.length;
                        System.arraycopy(substitutedVariableInterfaces, 0, substitutedVariableInterfaces = new ReferenceBinding[length2 + 1], 1, length2);
                        substitutedVariableInterfaces[0] = (ReferenceBinding)capturedWildcardBound2;
                        this.setSuperInterfaces(Scope.greaterLowerBound(substitutedVariableInterfaces));
                    }
                }
                else {
                    if (capturedWildcardBound2.isArrayType() || TypeBinding.equalsEquals(capturedWildcardBound2, this)) {
                        this.setSuperClass(substitutedVariableSuperclass);
                    }
                    else {
                        this.setSuperClass((ReferenceBinding)capturedWildcardBound2);
                        if (this.superclass.isSuperclassOf(substitutedVariableSuperclass)) {
                            this.setSuperClass(substitutedVariableSuperclass);
                        }
                    }
                    this.setSuperInterfaces(substitutedVariableInterfaces);
                }
                this.setFirstBound(capturedWildcardBound2);
                if ((capturedWildcardBound2.tagBits & 0x20000000L) == 0x0L) {
                    this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                    break;
                }
                break;
            }
            case 0: {
                this.setSuperClass(substitutedVariableSuperclass);
                this.setSuperInterfaces(substitutedVariableInterfaces);
                this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                break;
            }
            case 2: {
                this.setSuperClass(substitutedVariableSuperclass);
                if (TypeBinding.equalsEquals(wildcardVariable.firstBound, substitutedVariableSuperclass) || TypeBinding.equalsEquals(originalWildcardBound2, substitutedVariableSuperclass)) {
                    this.setFirstBound(substitutedVariableSuperclass);
                }
                this.setSuperInterfaces(substitutedVariableInterfaces);
                this.lowerBound = originalWildcardBound2;
                if ((originalWildcardBound2.tagBits & 0x20000000L) == 0x0L) {
                    this.tagBits &= 0xFFFFFFFFDFFFFFFFL;
                    break;
                }
                break;
            }
        }
        if (scope.environment().usesNullTypeAnnotations()) {
            this.evaluateNullAnnotations(scope, null);
        }
    }
    
    @Override
    public boolean isCapture() {
        return true;
    }
    
    @Override
    public boolean isEquivalentTo(final TypeBinding otherType) {
        if (TypeBinding.equalsEquals(this, otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        if (this.firstBound != null && this.firstBound.isArrayType() && this.firstBound.isCompatibleWith(otherType)) {
            return true;
        }
        switch (otherType.kind()) {
            case 516:
            case 8196: {
                return ((WildcardBinding)otherType).boundCheck(this);
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public boolean isProperType(final boolean admitCapture18) {
        return (this.lowerBound == null || this.lowerBound.isProperType(admitCapture18)) && (this.wildcard == null || this.wildcard.isProperType(admitCapture18)) && super.isProperType(admitCapture18);
    }
    
    @Override
    public char[] readableName() {
        if (this.wildcard != null) {
            final StringBuffer buffer = new StringBuffer(10);
            buffer.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX).append(this.captureID).append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX).append(this.wildcard.readableName());
            final int length = buffer.length();
            final char[] name = new char[length];
            buffer.getChars(0, length, name, 0);
            return name;
        }
        return super.readableName();
    }
    
    @Override
    public char[] signableName() {
        if (this.wildcard != null) {
            final StringBuffer buffer = new StringBuffer(10);
            buffer.append(TypeConstants.WILDCARD_CAPTURE_SIGNABLE_NAME_SUFFIX).append(this.wildcard.readableName());
            final int length = buffer.length();
            final char[] name = new char[length];
            buffer.getChars(0, length, name, 0);
            return name;
        }
        return super.readableName();
    }
    
    @Override
    public char[] shortReadableName() {
        if (this.wildcard != null) {
            final StringBuffer buffer = new StringBuffer(10);
            buffer.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX).append(this.captureID).append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX).append(this.wildcard.shortReadableName());
            final int length = buffer.length();
            final char[] name = new char[length];
            buffer.getChars(0, length, name, 0);
            return name;
        }
        return super.shortReadableName();
    }
    
    @Override
    public char[] nullAnnotatedReadableName(final CompilerOptions options, final boolean shortNames) {
        final StringBuffer nameBuffer = new StringBuffer(10);
        this.appendNullAnnotation(nameBuffer, options);
        nameBuffer.append(this.sourceName());
        if (!this.inRecursiveFunction) {
            this.inRecursiveFunction = true;
            try {
                if (this.wildcard != null) {
                    nameBuffer.append("of ");
                    nameBuffer.append(this.wildcard.withoutToplevelNullAnnotation().nullAnnotatedReadableName(options, shortNames));
                }
                else if (this.lowerBound != null) {
                    nameBuffer.append(" super ");
                    nameBuffer.append(this.lowerBound.nullAnnotatedReadableName(options, shortNames));
                }
                else if (this.firstBound != null) {
                    nameBuffer.append(" extends ");
                    nameBuffer.append(this.firstBound.nullAnnotatedReadableName(options, shortNames));
                    final TypeBinding[] otherUpperBounds = this.otherUpperBounds();
                    if (otherUpperBounds != CaptureBinding.NO_TYPES) {
                        nameBuffer.append(" & ...");
                    }
                }
            }
            finally {
                this.inRecursiveFunction = false;
            }
            this.inRecursiveFunction = false;
        }
        final int nameLength = nameBuffer.length();
        final char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }
    
    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        if (this.wildcard != null && this.wildcard.hasNullTypeAnnotations()) {
            final WildcardBinding newWildcard = (WildcardBinding)this.wildcard.withoutToplevelNullAnnotation();
            if (newWildcard != this.wildcard) {
                final CaptureBinding newCapture = (CaptureBinding)this.environment.getUnannotatedType(this).clone(null);
                if (newWildcard.hasTypeAnnotations()) {
                    final CaptureBinding captureBinding = newCapture;
                    captureBinding.tagBits |= 0x200000L;
                }
                newCapture.wildcard = newWildcard;
                newCapture.superclass = this.superclass;
                newCapture.superInterfaces = this.superInterfaces;
                final AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
                return this.environment.createAnnotatedType(newCapture, newAnnotations);
            }
        }
        return super.withoutToplevelNullAnnotation();
    }
    
    @Override
    TypeBinding substituteInferenceVariable(final InferenceVariable var, final TypeBinding substituteType) {
        if (this.pendingSubstitute != null) {
            return this.pendingSubstitute;
        }
        try {
            final TypeBinding substitutedWildcard = this.wildcard.substituteInferenceVariable(var, substituteType);
            if (substitutedWildcard != this.wildcard) {
                final CaptureBinding substitute = (CaptureBinding)this.clone(this.enclosingType());
                substitute.wildcard = (WildcardBinding)substitutedWildcard;
                this.pendingSubstitute = substitute;
                if (this.lowerBound != null) {
                    substitute.lowerBound = this.lowerBound.substituteInferenceVariable(var, substituteType);
                }
                if (this.firstBound != null) {
                    substitute.firstBound = this.firstBound.substituteInferenceVariable(var, substituteType);
                }
                if (this.superclass != null) {
                    substitute.superclass = (ReferenceBinding)this.superclass.substituteInferenceVariable(var, substituteType);
                }
                if (this.superInterfaces != null) {
                    final int length = this.superInterfaces.length;
                    substitute.superInterfaces = new ReferenceBinding[length];
                    for (int i = 0; i < length; ++i) {
                        substitute.superInterfaces[i] = (ReferenceBinding)this.superInterfaces[i].substituteInferenceVariable(var, substituteType);
                    }
                }
                return substitute;
            }
            return this;
        }
        finally {
            this.pendingSubstitute = null;
        }
    }
    
    @Override
    public void setTypeAnnotations(final AnnotationBinding[] annotations, final boolean evalNullAnnotations) {
        super.setTypeAnnotations(annotations, evalNullAnnotations);
        if (annotations != Binding.NO_ANNOTATIONS && this.wildcard != null) {
            this.wildcard = (WildcardBinding)this.wildcard.environment.createAnnotatedType(this.wildcard, annotations);
        }
    }
    
    @Override
    public TypeBinding uncapture(final Scope scope) {
        return this.wildcard;
    }
    
    @Override
    protected TypeBinding[] getDerivedTypesForDeferredInitialization() {
        TypeBinding[] derived = this.environment.typeSystem.getDerivedTypes(this);
        if (derived.length > 0) {
            int count = 0;
            for (int i = 0; i < derived.length; ++i) {
                if (derived[i] != null && derived[i].id == this.id) {
                    derived[count++] = derived[i];
                }
            }
            if (count < derived.length) {
                System.arraycopy(derived, 0, derived = new TypeBinding[count], 0, count);
            }
        }
        return derived;
    }
    
    @Override
    public String toString() {
        if (this.wildcard != null) {
            final StringBuffer buffer = new StringBuffer(10);
            final AnnotationBinding[] annotations = this.getTypeAnnotations();
            for (int i = 0, length = (annotations == null) ? 0 : annotations.length; i < length; ++i) {
                buffer.append(annotations[i]);
                buffer.append(' ');
            }
            buffer.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX).append(this.captureID).append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX).append(this.wildcard);
            return buffer.toString();
        }
        return super.toString();
    }
}
