package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;

public class CaptureBinding18 extends CaptureBinding
{
    TypeBinding[] upperBounds;
    private char[] originalName;
    private CaptureBinding18 prototype;
    int recursionLevel;
    
    public CaptureBinding18(final ReferenceBinding contextType, final char[] sourceName, final char[] originalName, final int start, final int end, final int captureID, final LookupEnvironment environment) {
        super(contextType, sourceName, start, end, captureID, environment);
        this.recursionLevel = 0;
        this.originalName = originalName;
        this.prototype = this;
    }
    
    private CaptureBinding18(final CaptureBinding18 prototype) {
        super(prototype);
        this.recursionLevel = 0;
        this.sourceName = CharOperation.append(prototype.sourceName, '\'');
        this.originalName = prototype.originalName;
        this.upperBounds = prototype.upperBounds;
        this.prototype = prototype.prototype;
    }
    
    public boolean setUpperBounds(final TypeBinding[] upperBounds, final ReferenceBinding javaLangObject) {
        this.upperBounds = upperBounds;
        if (upperBounds.length > 0) {
            this.firstBound = upperBounds[0];
        }
        int numReferenceInterfaces = 0;
        if (!ReferenceBinding.isConsistentIntersection(upperBounds)) {
            return false;
        }
        for (int i = 0; i < upperBounds.length; ++i) {
            final TypeBinding aBound = upperBounds[i];
            if (aBound instanceof ReferenceBinding) {
                if (this.superclass == null && aBound.isClass()) {
                    this.superclass = (ReferenceBinding)aBound;
                }
                else if (aBound.isInterface()) {
                    ++numReferenceInterfaces;
                }
            }
            else if (TypeBinding.equalsEquals(aBound.leafComponentType(), this)) {
                return false;
            }
        }
        this.superInterfaces = new ReferenceBinding[numReferenceInterfaces];
        int idx = 0;
        for (int j = 0; j < upperBounds.length; ++j) {
            final TypeBinding aBound2 = upperBounds[j];
            if (aBound2.isInterface()) {
                this.superInterfaces[idx++] = (ReferenceBinding)aBound2;
            }
        }
        if (this.superclass == null) {
            this.superclass = javaLangObject;
        }
        return true;
    }
    
    @Override
    public void initializeBounds(final Scope scope, final ParameterizedTypeBinding capturedParameterizedType) {
    }
    
    @Override
    public TypeBinding clone(final TypeBinding enclosingType) {
        return new CaptureBinding18(this);
    }
    
    @Override
    public MethodBinding[] getMethods(final char[] selector) {
        if (this.upperBounds.length == 1 && this.upperBounds[0] instanceof ReferenceBinding) {
            return ((ReferenceBinding)this.upperBounds[0]).getMethods(selector);
        }
        return super.getMethods(selector);
    }
    
    @Override
    public TypeBinding erasure() {
        if (this.upperBounds == null || this.upperBounds.length <= 1) {
            return super.erasure();
        }
        final ReferenceBinding[] erasures = new ReferenceBinding[this.upperBounds.length];
        boolean multipleErasures = false;
        for (int i = 0; i < this.upperBounds.length; ++i) {
            erasures[i] = (ReferenceBinding)this.upperBounds[i].erasure();
            if (i > 0 && TypeBinding.notEquals(erasures[0], erasures[i])) {
                multipleErasures = true;
            }
        }
        if (!multipleErasures) {
            return erasures[0];
        }
        return this.environment.createIntersectionType18(erasures);
    }
    
    @Override
    public boolean isEquivalentTo(final TypeBinding otherType) {
        if (TypeBinding.equalsEquals(this, otherType)) {
            return true;
        }
        if (otherType == null) {
            return false;
        }
        if (this.upperBounds != null) {
            for (int i = 0; i < this.upperBounds.length; ++i) {
                final TypeBinding aBound = this.upperBounds[i];
                if (aBound != null && aBound.isArrayType()) {
                    if (!aBound.isCompatibleWith(otherType)) {
                        return false;
                    }
                }
                else {
                    switch (otherType.kind()) {
                        case 516:
                        case 8196: {
                            if (!((WildcardBinding)otherType).boundCheck(aBound)) {
                                return false;
                            }
                            break;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isCompatibleWith(final TypeBinding otherType, final Scope captureScope) {
        if (TypeBinding.equalsEquals(this, otherType)) {
            return true;
        }
        if (this.inRecursiveFunction) {
            return true;
        }
        this.inRecursiveFunction = true;
        try {
            if (this.upperBounds != null) {
                final int length = this.upperBounds.length;
                final int rightKind = otherType.kind();
                TypeBinding[] rightIntersectingTypes = null;
                if (rightKind == 8196 && otherType.boundKind() == 1) {
                    final TypeBinding allRightBounds = ((WildcardBinding)otherType).allBounds();
                    if (allRightBounds instanceof IntersectionTypeBinding18) {
                        rightIntersectingTypes = ((IntersectionTypeBinding18)allRightBounds).intersectingTypes;
                    }
                }
                else if (rightKind == 32772) {
                    rightIntersectingTypes = ((IntersectionTypeBinding18)otherType).intersectingTypes;
                }
                if (rightIntersectingTypes != null) {
                    int numRequired = rightIntersectingTypes.length;
                    final TypeBinding[] required = new TypeBinding[numRequired];
                    System.arraycopy(rightIntersectingTypes, 0, required, 0, numRequired);
                    for (int i = 0; i < length; ++i) {
                        final TypeBinding provided = this.upperBounds[i];
                        for (int j = 0; j < required.length; ++j) {
                            if (required[j] != null) {
                                if (provided.isCompatibleWith(required[j], captureScope)) {
                                    required[j] = null;
                                    if (--numRequired == 0) {
                                        return true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    return false;
                }
                for (int k = 0; k < length; ++k) {
                    if (this.upperBounds[k].isCompatibleWith(otherType, captureScope)) {
                        return true;
                    }
                }
            }
            return false;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }
    
    @Override
    public TypeBinding findSuperTypeOriginatingFrom(final TypeBinding otherType) {
        if (this.upperBounds != null && this.upperBounds.length > 1) {
            for (int i = 0; i < this.upperBounds.length; ++i) {
                final TypeBinding candidate = this.upperBounds[i].findSuperTypeOriginatingFrom(otherType);
                if (candidate != null) {
                    return candidate;
                }
            }
        }
        return super.findSuperTypeOriginatingFrom(otherType);
    }
    
    @Override
    TypeBinding substituteInferenceVariable(final InferenceVariable var, final TypeBinding substituteType) {
        if (this.inRecursiveFunction) {
            return this;
        }
        this.inRecursiveFunction = true;
        try {
            boolean haveSubstitution = false;
            ReferenceBinding currentSuperclass = this.superclass;
            if (currentSuperclass != null) {
                currentSuperclass = (ReferenceBinding)currentSuperclass.substituteInferenceVariable(var, substituteType);
                haveSubstitution |= TypeBinding.notEquals(currentSuperclass, this.superclass);
            }
            ReferenceBinding[] currentSuperInterfaces = null;
            if (this.superInterfaces != null) {
                final int length = this.superInterfaces.length;
                if (haveSubstitution) {
                    System.arraycopy(this.superInterfaces, 0, currentSuperInterfaces = new ReferenceBinding[length], 0, length);
                }
                for (int i = 0; i < length; ++i) {
                    ReferenceBinding currentSuperInterface = this.superInterfaces[i];
                    if (currentSuperInterface != null) {
                        currentSuperInterface = (ReferenceBinding)currentSuperInterface.substituteInferenceVariable(var, substituteType);
                        if (TypeBinding.notEquals(currentSuperInterface, this.superInterfaces[i])) {
                            if (currentSuperInterfaces == null) {
                                System.arraycopy(this.superInterfaces, 0, currentSuperInterfaces = new ReferenceBinding[length], 0, length);
                            }
                            currentSuperInterfaces[i] = currentSuperInterface;
                            haveSubstitution = true;
                        }
                    }
                }
            }
            TypeBinding[] currentUpperBounds = null;
            if (this.upperBounds != null) {
                final int length2 = this.upperBounds.length;
                if (haveSubstitution) {
                    System.arraycopy(this.upperBounds, 0, currentUpperBounds = new TypeBinding[length2], 0, length2);
                }
                for (int j = 0; j < length2; ++j) {
                    TypeBinding currentBound = this.upperBounds[j];
                    if (currentBound != null) {
                        currentBound = currentBound.substituteInferenceVariable(var, substituteType);
                        if (TypeBinding.notEquals(currentBound, this.upperBounds[j])) {
                            if (currentUpperBounds == null) {
                                System.arraycopy(this.upperBounds, 0, currentUpperBounds = new TypeBinding[length2], 0, length2);
                            }
                            currentUpperBounds[j] = currentBound;
                            haveSubstitution = true;
                        }
                    }
                }
            }
            TypeBinding currentFirstBound = null;
            if (this.firstBound != null) {
                currentFirstBound = this.firstBound.substituteInferenceVariable(var, substituteType);
                haveSubstitution |= TypeBinding.notEquals(this.firstBound, currentFirstBound);
            }
            if (haveSubstitution) {
                final CaptureBinding18 newCapture = (CaptureBinding18)this.clone(this.enclosingType());
                newCapture.tagBits = this.tagBits;
                final Substitution substitution = new Substitution() {
                    @Override
                    public TypeBinding substitute(final TypeVariableBinding typeVariable) {
                        return (typeVariable == CaptureBinding18.this) ? newCapture : typeVariable;
                    }
                    
                    @Override
                    public boolean isRawSubstitution() {
                        return false;
                    }
                    
                    @Override
                    public LookupEnvironment environment() {
                        return CaptureBinding18.this.environment;
                    }
                };
                if (currentFirstBound != null) {
                    newCapture.firstBound = Scope.substitute(substitution, currentFirstBound);
                }
                newCapture.superclass = (ReferenceBinding)Scope.substitute(substitution, currentSuperclass);
                newCapture.superInterfaces = Scope.substitute(substitution, currentSuperInterfaces);
                newCapture.upperBounds = Scope.substitute(substitution, currentUpperBounds);
                return newCapture;
            }
            return this;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }
    
    @Override
    public boolean isProperType(final boolean admitCapture18) {
        if (!admitCapture18) {
            return false;
        }
        if (this.inRecursiveFunction) {
            return true;
        }
        this.inRecursiveFunction = true;
        try {
            if (this.lowerBound != null && !this.lowerBound.isProperType(admitCapture18)) {
                return false;
            }
            if (this.upperBounds != null) {
                for (int i = 0; i < this.upperBounds.length; ++i) {
                    if (!this.upperBounds[i].isProperType(admitCapture18)) {
                        return false;
                    }
                }
            }
        }
        finally {
            this.inRecursiveFunction = false;
        }
        this.inRecursiveFunction = false;
        return true;
    }
    
    @Override
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature == null) {
            try {
                char[] boundSignature;
                if (this.prototype.recursionLevel++ > 0 || this.firstBound == null) {
                    boundSignature = TypeConstants.WILDCARD_STAR;
                }
                else if (this.upperBounds != null) {
                    boundSignature = CharOperation.concat(TypeConstants.WILDCARD_PLUS, this.firstBound.genericTypeSignature());
                }
                else if (this.lowerBound != null) {
                    boundSignature = CharOperation.concat(TypeConstants.WILDCARD_MINUS, this.lowerBound.genericTypeSignature());
                }
                else {
                    boundSignature = TypeConstants.WILDCARD_STAR;
                }
                this.genericTypeSignature = CharOperation.concat(TypeConstants.WILDCARD_CAPTURE, boundSignature);
            }
            finally {
                final CaptureBinding18 prototype = this.prototype;
                --prototype.recursionLevel;
            }
            final CaptureBinding18 prototype2 = this.prototype;
            --prototype2.recursionLevel;
        }
        return this.genericTypeSignature;
    }
    
    @Override
    public char[] readableName() {
        if (this.lowerBound == null && this.firstBound != null) {
            if (this.prototype.recursionLevel < 2) {
                try {
                    final CaptureBinding18 prototype = this.prototype;
                    ++prototype.recursionLevel;
                    if (this.upperBounds != null && this.upperBounds.length > 1) {
                        final StringBuffer sb = new StringBuffer();
                        sb.append(this.upperBounds[0].readableName());
                        for (int i = 1; i < this.upperBounds.length; ++i) {
                            sb.append('&').append(this.upperBounds[i].readableName());
                        }
                        final int len = sb.length();
                        final char[] name = new char[len];
                        sb.getChars(0, len, name, 0);
                        return name;
                    }
                    return this.firstBound.readableName();
                }
                finally {
                    final CaptureBinding18 prototype2 = this.prototype;
                    --prototype2.recursionLevel;
                }
            }
            return this.originalName;
        }
        return super.readableName();
    }
    
    @Override
    public char[] shortReadableName() {
        if (this.lowerBound == null && this.firstBound != null) {
            if (this.prototype.recursionLevel < 2) {
                try {
                    final CaptureBinding18 prototype = this.prototype;
                    ++prototype.recursionLevel;
                    if (this.upperBounds != null && this.upperBounds.length > 1) {
                        final StringBuffer sb = new StringBuffer();
                        sb.append(this.upperBounds[0].shortReadableName());
                        for (int i = 1; i < this.upperBounds.length; ++i) {
                            sb.append('&').append(this.upperBounds[i].shortReadableName());
                        }
                        final int len = sb.length();
                        final char[] name = new char[len];
                        sb.getChars(0, len, name, 0);
                        return name;
                    }
                    return this.firstBound.shortReadableName();
                }
                finally {
                    final CaptureBinding18 prototype2 = this.prototype;
                    --prototype2.recursionLevel;
                }
            }
            return this.originalName;
        }
        return super.shortReadableName();
    }
    
    @Override
    public TypeBinding uncapture(final Scope scope) {
        return this;
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(TypeConstants.CAPTURE18);
        buffer.append('{').append(this.end).append('#').append(this.captureID).append('}');
        buffer.append(';');
        final int length = buffer.length();
        final char[] uniqueKey = new char[length];
        buffer.getChars(0, length, uniqueKey, 0);
        return uniqueKey;
    }
}
