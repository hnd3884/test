package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Arrays;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class ParameterizedTypeBinding extends ReferenceBinding implements Substitution
{
    protected ReferenceBinding type;
    public TypeBinding[] arguments;
    public LookupEnvironment environment;
    public char[] genericTypeSignature;
    public ReferenceBinding superclass;
    public ReferenceBinding[] superInterfaces;
    public FieldBinding[] fields;
    public ReferenceBinding[] memberTypes;
    public MethodBinding[] methods;
    protected ReferenceBinding enclosingType;
    
    public ParameterizedTypeBinding(final ReferenceBinding type, final TypeBinding[] arguments, final ReferenceBinding enclosingType, final LookupEnvironment environment) {
        this.environment = environment;
        this.enclosingType = enclosingType;
        this.initialize(type, arguments);
        if (type instanceof UnresolvedReferenceBinding) {
            ((UnresolvedReferenceBinding)type).addWrapper(this, environment);
        }
        if (arguments != null) {
            for (int i = 0, l = arguments.length; i < l; ++i) {
                if (arguments[i] instanceof UnresolvedReferenceBinding) {
                    ((UnresolvedReferenceBinding)arguments[i]).addWrapper(this, environment);
                }
                if (arguments[i].hasNullTypeAnnotations()) {
                    this.tagBits |= 0x100000L;
                }
            }
        }
        if (enclosingType != null && enclosingType.hasNullTypeAnnotations()) {
            this.tagBits |= 0x100000L;
        }
        this.tagBits |= 0x1000000L;
        this.typeBits = type.typeBits;
    }
    
    @Override
    public ReferenceBinding actualType() {
        return this.type;
    }
    
    @Override
    public boolean isParameterizedType() {
        return true;
    }
    
    public void boundCheck(final Scope scope, final TypeReference[] argumentReferences) {
        if ((this.tagBits & 0x400000L) == 0x0L) {
            boolean hasErrors = false;
            final TypeVariableBinding[] typeVariables = this.type.typeVariables();
            if (this.arguments != null && typeVariables != null) {
                for (int i = 0, length = typeVariables.length; i < length; ++i) {
                    final TypeConstants.BoundCheckStatus checkStatus = typeVariables[i].boundCheck(this, this.arguments[i], scope, argumentReferences[i]);
                    hasErrors |= (checkStatus != TypeConstants.BoundCheckStatus.OK);
                    if (!checkStatus.isOKbyJLS() && (this.arguments[i].tagBits & 0x80L) == 0x0L) {
                        scope.problemReporter().typeMismatchError(this.arguments[i], typeVariables[i], this.type, argumentReferences[i]);
                    }
                }
            }
            if (!hasErrors) {
                this.tagBits |= 0x400000L;
            }
        }
    }
    
    @Override
    public boolean canBeInstantiated() {
        return (this.tagBits & 0x40000000L) == 0x0L && super.canBeInstantiated();
    }
    
    @Override
    public ParameterizedTypeBinding capture(final Scope scope, final int start, final int end) {
        if ((this.tagBits & 0x40000000L) == 0x0L) {
            return this;
        }
        final TypeBinding[] originalArguments = this.arguments;
        final int length = originalArguments.length;
        final TypeBinding[] capturedArguments = new TypeBinding[length];
        ReferenceBinding contextType = scope.enclosingSourceType();
        if (contextType != null) {
            contextType = contextType.outermostEnclosingType();
        }
        final CompilationUnitScope compilationUnitScope = scope.compilationUnitScope();
        final ASTNode cud = compilationUnitScope.referenceContext;
        final long sourceLevel = this.environment.globalOptions.sourceLevel;
        final boolean needUniqueCapture = sourceLevel >= 3407872L;
        for (int i = 0; i < length; ++i) {
            final TypeBinding argument = originalArguments[i];
            if (argument.kind() == 516) {
                final WildcardBinding wildcard = (WildcardBinding)argument;
                if (wildcard.boundKind == 2 && wildcard.bound.id == 1) {
                    capturedArguments[i] = wildcard.bound;
                }
                else if (needUniqueCapture) {
                    capturedArguments[i] = this.environment.createCapturedWildcard(wildcard, contextType, start, end, cud, compilationUnitScope.nextCaptureID());
                }
                else {
                    capturedArguments[i] = new CaptureBinding(wildcard, contextType, start, end, cud, compilationUnitScope.nextCaptureID());
                }
            }
            else {
                capturedArguments[i] = argument;
            }
        }
        final ParameterizedTypeBinding capturedParameterizedType = this.environment.createParameterizedType(this.type, capturedArguments, this.enclosingType(), this.typeAnnotations);
        for (final TypeBinding argument2 : capturedArguments) {
            if (argument2.isCapture()) {
                ((CaptureBinding)argument2).initializeBounds(scope, capturedParameterizedType);
            }
        }
        return capturedParameterizedType;
    }
    
    @Override
    public TypeBinding uncapture(final Scope scope) {
        if ((this.tagBits & 0x2000000000000000L) == 0x0L) {
            return this;
        }
        final int length = (this.arguments == null) ? 0 : this.arguments.length;
        final TypeBinding[] freeTypes = new TypeBinding[length];
        for (int i = 0; i < length; ++i) {
            freeTypes[i] = this.arguments[i].uncapture(scope);
        }
        return scope.environment().createParameterizedType(this.type, freeTypes, (ReferenceBinding)((this.enclosingType != null) ? this.enclosingType.uncapture(scope) : null), this.typeAnnotations);
    }
    
    @Override
    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if ((this.tagBits & 0x80L) != 0x0L) {
            if (this.enclosingType != null) {
                missingTypes = this.enclosingType.collectMissingTypes(missingTypes);
            }
            missingTypes = this.genericType().collectMissingTypes(missingTypes);
            if (this.arguments != null) {
                for (int i = 0, max = this.arguments.length; i < max; ++i) {
                    missingTypes = this.arguments[i].collectMissingTypes(missingTypes);
                }
            }
        }
        return missingTypes;
    }
    
    @Override
    public void collectSubstitutes(final Scope scope, final TypeBinding actualType, final InferenceContext inferenceContext, final int constraint) {
        if ((this.tagBits & 0x20000000L) == 0x0L) {
            final TypeBinding actualEquivalent = actualType.findSuperTypeOriginatingFrom(this.type);
            if (actualEquivalent != null && actualEquivalent.isRawType()) {
                inferenceContext.isUnchecked = true;
            }
            return;
        }
        if (actualType == TypeBinding.NULL || actualType.kind() == 65540) {
            return;
        }
        if (!(actualType instanceof ReferenceBinding)) {
            return;
        }
        TypeBinding formalEquivalent = null;
        TypeBinding actualEquivalent2 = null;
        switch (constraint) {
            case 0:
            case 1: {
                formalEquivalent = this;
                actualEquivalent2 = actualType.findSuperTypeOriginatingFrom(this.type);
                if (actualEquivalent2 == null) {
                    return;
                }
                break;
            }
            default: {
                formalEquivalent = this.findSuperTypeOriginatingFrom(actualType);
                if (formalEquivalent == null) {
                    return;
                }
                actualEquivalent2 = actualType;
                break;
            }
        }
        final ReferenceBinding formalEnclosingType = formalEquivalent.enclosingType();
        if (formalEnclosingType != null) {
            formalEnclosingType.collectSubstitutes(scope, actualEquivalent2.enclosingType(), inferenceContext, constraint);
        }
        if (this.arguments == null) {
            return;
        }
        TypeBinding[] formalArguments = null;
        switch (formalEquivalent.kind()) {
            case 2052: {
                formalArguments = formalEquivalent.typeVariables();
                break;
            }
            case 260: {
                formalArguments = ((ParameterizedTypeBinding)formalEquivalent).arguments;
                break;
            }
            case 1028: {
                if (inferenceContext.depth > 0) {
                    inferenceContext.status = 1;
                }
                return;
            }
            default: {
                return;
            }
        }
        TypeBinding[] actualArguments = null;
        switch (actualEquivalent2.kind()) {
            case 2052: {
                actualArguments = actualEquivalent2.typeVariables();
                break;
            }
            case 260: {
                actualArguments = ((ParameterizedTypeBinding)actualEquivalent2).arguments;
                break;
            }
            case 1028: {
                if (inferenceContext.depth > 0) {
                    inferenceContext.status = 1;
                }
                else {
                    inferenceContext.isUnchecked = true;
                }
                return;
            }
            default: {
                return;
            }
        }
        if (formalArguments == null || actualArguments == null) {
            return;
        }
        ++inferenceContext.depth;
        for (int i = 0, length = formalArguments.length; i < length; ++i) {
            final TypeBinding formalArgument = formalArguments[i];
            final TypeBinding actualArgument = actualArguments[i];
            if (formalArgument.isWildcard()) {
                formalArgument.collectSubstitutes(scope, actualArgument, inferenceContext, constraint);
            }
            else {
                if (actualArgument.isWildcard()) {
                    final WildcardBinding actualWildcardArgument = (WildcardBinding)actualArgument;
                    if (actualWildcardArgument.otherBounds == null) {
                        if (constraint != 2) {
                            continue;
                        }
                        switch (actualWildcardArgument.boundKind) {
                            case 1: {
                                formalArgument.collectSubstitutes(scope, actualWildcardArgument.bound, inferenceContext, 2);
                                continue;
                            }
                            case 2: {
                                formalArgument.collectSubstitutes(scope, actualWildcardArgument.bound, inferenceContext, 1);
                            }
                            default: {
                                continue;
                            }
                        }
                    }
                }
                formalArgument.collectSubstitutes(scope, actualArgument, inferenceContext, 0);
            }
        }
        --inferenceContext.depth;
    }
    
    @Override
    public void computeId() {
        this.id = Integer.MAX_VALUE;
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final StringBuffer sig = new StringBuffer(10);
        ReferenceBinding enclosing;
        if (this.isMemberType() && ((enclosing = this.enclosingType()).isParameterizedType() || enclosing.isRawType())) {
            final char[] typeSig = enclosing.computeUniqueKey(false);
            sig.append(typeSig, 0, typeSig.length - 1);
            sig.append('.').append(this.sourceName());
        }
        else if (this.type.isLocalType()) {
            final LocalTypeBinding localTypeBinding = (LocalTypeBinding)this.type;
            ReferenceBinding temp;
            for (enclosing = localTypeBinding.enclosingType(); (temp = enclosing.enclosingType()) != null; enclosing = temp) {}
            final char[] typeSig2 = enclosing.computeUniqueKey(false);
            sig.append(typeSig2, 0, typeSig2.length - 1);
            sig.append('$');
            sig.append(localTypeBinding.sourceStart);
        }
        else {
            final char[] typeSig = this.type.computeUniqueKey(false);
            sig.append(typeSig, 0, typeSig.length - 1);
        }
        ReferenceBinding captureSourceType = null;
        if (this.arguments != null) {
            sig.append('<');
            for (int i = 0, length = this.arguments.length; i < length; ++i) {
                final TypeBinding typeBinding = this.arguments[i];
                sig.append(typeBinding.computeUniqueKey(false));
                if (typeBinding instanceof CaptureBinding) {
                    captureSourceType = ((CaptureBinding)typeBinding).sourceType;
                }
            }
            sig.append('>');
        }
        sig.append(';');
        if (captureSourceType != null && TypeBinding.notEquals(captureSourceType, this.type)) {
            sig.insert(0, "&");
            sig.insert(0, captureSourceType.computeUniqueKey(false));
        }
        final int sigLength = sig.length();
        final char[] uniqueKey = new char[sigLength];
        sig.getChars(0, sigLength, uniqueKey, 0);
        return uniqueKey;
    }
    
    @Override
    public char[] constantPoolName() {
        return this.type.constantPoolName();
    }
    
    @Override
    public TypeBinding clone(final TypeBinding outerType) {
        return new ParameterizedTypeBinding(this.type, this.arguments, (ReferenceBinding)outerType, this.environment);
    }
    
    public ParameterizedMethodBinding createParameterizedMethod(final MethodBinding originalMethod) {
        return new ParameterizedMethodBinding(this, originalMethod);
    }
    
    @Override
    public String debugName() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        final StringBuffer nameBuffer = new StringBuffer(10);
        if (this.type instanceof UnresolvedReferenceBinding) {
            nameBuffer.append(this.type);
        }
        else {
            nameBuffer.append(this.type.sourceName());
        }
        if (this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = this.arguments.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].debugName());
            }
            nameBuffer.append('>');
        }
        return nameBuffer.toString();
    }
    
    @Override
    public String annotatedDebugName() {
        final StringBuffer nameBuffer = new StringBuffer(super.annotatedDebugName());
        if (this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = this.arguments.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].annotatedDebugName());
            }
            nameBuffer.append('>');
        }
        return nameBuffer.toString();
    }
    
    @Override
    public ReferenceBinding enclosingType() {
        return this.enclosingType;
    }
    
    @Override
    public LookupEnvironment environment() {
        return this.environment;
    }
    
    @Override
    public TypeBinding erasure() {
        return this.type.erasure();
    }
    
    @Override
    public int fieldCount() {
        return this.type.fieldCount();
    }
    
    @Override
    public FieldBinding[] fields() {
        if ((this.tagBits & 0x2000L) != 0x0L) {
            return this.fields;
        }
        try {
            final FieldBinding[] originalFields = this.type.fields();
            final int length = originalFields.length;
            final FieldBinding[] parameterizedFields = new FieldBinding[length];
            for (int i = 0; i < length; ++i) {
                parameterizedFields[i] = new ParameterizedFieldBinding(this, originalFields[i]);
            }
            this.fields = parameterizedFields;
        }
        finally {
            if (this.fields == null) {
                this.fields = Binding.NO_FIELDS;
            }
            this.tagBits |= 0x2000L;
        }
        if (this.fields == null) {
            this.fields = Binding.NO_FIELDS;
        }
        this.tagBits |= 0x2000L;
        return this.fields;
    }
    
    public ReferenceBinding genericType() {
        if (this.type instanceof UnresolvedReferenceBinding) {
            ((UnresolvedReferenceBinding)this.type).resolve(this.environment, false);
        }
        return this.type;
    }
    
    @Override
    public char[] genericTypeSignature() {
        if (this.genericTypeSignature == null) {
            if ((this.modifiers & 0x40000000) == 0x0) {
                this.genericTypeSignature = this.type.signature();
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
                    final char[] typeSig2 = this.type.signature();
                    sig.append(typeSig2, 0, typeSig2.length - 1);
                }
                if (this.arguments != null) {
                    sig.append('<');
                    for (int i = 0, length = this.arguments.length; i < length; ++i) {
                        sig.append(this.arguments[i].genericTypeSignature());
                    }
                    sig.append('>');
                }
                sig.append(';');
                final int sigLength = sig.length();
                sig.getChars(0, sigLength, this.genericTypeSignature = new char[sigLength], 0);
            }
        }
        return this.genericTypeSignature;
    }
    
    @Override
    public long getAnnotationTagBits() {
        return this.type.getAnnotationTagBits();
    }
    
    @Override
    public int getEnclosingInstancesSlotSize() {
        return this.genericType().getEnclosingInstancesSlotSize();
    }
    
    @Override
    public MethodBinding getExactConstructor(final TypeBinding[] argumentTypes) {
        final int argCount = argumentTypes.length;
        MethodBinding match = null;
        if ((this.tagBits & 0x8000L) != 0x0L) {
            final long range;
            if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0L) {
            Label_0120:
                for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; ++imethod) {
                    final MethodBinding method = this.methods[imethod];
                    if (method.parameters.length == argCount) {
                        final TypeBinding[] toMatch = method.parameters;
                        for (int iarg = 0; iarg < argCount; ++iarg) {
                            if (TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                continue Label_0120;
                            }
                        }
                        if (match != null) {
                            return null;
                        }
                        match = method;
                    }
                }
            }
        }
        else {
            final MethodBinding[] matchingMethods = this.getMethods(TypeConstants.INIT);
            int m = matchingMethods.length;
        Label_0213:
            while (--m >= 0) {
                final MethodBinding method2 = matchingMethods[m];
                final TypeBinding[] toMatch2 = method2.parameters;
                if (toMatch2.length == argCount) {
                    for (int p = 0; p < argCount; ++p) {
                        if (TypeBinding.notEquals(toMatch2[p], argumentTypes[p])) {
                            continue Label_0213;
                        }
                    }
                    if (match != null) {
                        return null;
                    }
                    match = method2;
                }
            }
        }
        return match;
    }
    
    @Override
    public MethodBinding getExactMethod(final char[] selector, final TypeBinding[] argumentTypes, final CompilationUnitScope refScope) {
        final int argCount = argumentTypes.length;
        boolean foundNothing = true;
        MethodBinding match = null;
        if ((this.tagBits & 0x8000L) != 0x0L) {
            final long range;
            if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
            Label_0130:
                for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; ++imethod) {
                    final MethodBinding method = this.methods[imethod];
                    foundNothing = false;
                    if (method.parameters.length == argCount) {
                        final TypeBinding[] toMatch = method.parameters;
                        for (int iarg = 0; iarg < argCount; ++iarg) {
                            if (TypeBinding.notEquals(toMatch[iarg], argumentTypes[iarg])) {
                                continue Label_0130;
                            }
                        }
                        if (match != null) {
                            return null;
                        }
                        match = method;
                    }
                }
            }
        }
        else {
            final MethodBinding[] matchingMethods = this.getMethods(selector);
            foundNothing = (matchingMethods == Binding.NO_METHODS);
            int m = matchingMethods.length;
        Label_0240:
            while (--m >= 0) {
                final MethodBinding method2 = matchingMethods[m];
                final TypeBinding[] toMatch2 = method2.parameters;
                if (toMatch2.length == argCount) {
                    for (int p = 0; p < argCount; ++p) {
                        if (TypeBinding.notEquals(toMatch2[p], argumentTypes[p])) {
                            continue Label_0240;
                        }
                    }
                    if (match != null) {
                        return null;
                    }
                    match = method2;
                }
            }
        }
        if (match == null) {
            if (foundNothing && (this.arguments == null || this.arguments.length <= 1)) {
                if (this.isInterface()) {
                    if (this.superInterfaces().length == 1) {
                        if (refScope != null) {
                            refScope.recordTypeReference(this.superInterfaces[0]);
                        }
                        return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
                    }
                }
                else if (this.superclass() != null) {
                    if (refScope != null) {
                        refScope.recordTypeReference(this.superclass);
                    }
                    return this.superclass.getExactMethod(selector, argumentTypes, refScope);
                }
            }
            return null;
        }
        if (match.hasSubstitutedParameters()) {
            return null;
        }
        return match;
    }
    
    @Override
    public FieldBinding getField(final char[] fieldName, final boolean needResolve) {
        this.fields();
        return ReferenceBinding.binarySearch(fieldName, this.fields);
    }
    
    @Override
    public ReferenceBinding getMemberType(final char[] typeName) {
        this.memberTypes();
        final int typeLength = typeName.length;
        int i = this.memberTypes.length;
        while (--i >= 0) {
            final ReferenceBinding memberType = this.memberTypes[i];
            if (memberType.sourceName.length == typeLength && CharOperation.equals(memberType.sourceName, typeName)) {
                return memberType;
            }
        }
        return null;
    }
    
    @Override
    public MethodBinding[] getMethods(final char[] selector) {
        final long range;
        if (this.methods != null && (range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
            final int start = (int)range;
            final int length = (int)(range >> 32) - start + 1;
            final MethodBinding[] result;
            System.arraycopy(this.methods, start, result = new MethodBinding[length], 0, length);
            return result;
        }
        if ((this.tagBits & 0x8000L) != 0x0L) {
            return Binding.NO_METHODS;
        }
        MethodBinding[] parameterizedMethods = null;
        try {
            final MethodBinding[] originalMethods = this.type.getMethods(selector);
            final int length2 = originalMethods.length;
            if (length2 == 0) {
                return Binding.NO_METHODS;
            }
            parameterizedMethods = new MethodBinding[length2];
            final boolean useNullTypeAnnotations = this.environment.usesNullTypeAnnotations();
            for (int i = 0; i < length2; ++i) {
                parameterizedMethods[i] = this.createParameterizedMethod(originalMethods[i]);
                if (useNullTypeAnnotations) {
                    parameterizedMethods[i] = NullAnnotationMatching.checkForContradictions(parameterizedMethods[i], null, null);
                }
            }
            if (this.methods == null) {
                final MethodBinding[] temp = new MethodBinding[length2];
                System.arraycopy(parameterizedMethods, 0, temp, 0, length2);
                this.methods = temp;
            }
            else {
                final int total = length2 + this.methods.length;
                final MethodBinding[] temp2 = new MethodBinding[total];
                System.arraycopy(parameterizedMethods, 0, temp2, 0, length2);
                System.arraycopy(this.methods, 0, temp2, length2, this.methods.length);
                if (total > 1) {
                    ReferenceBinding.sortMethods(temp2, 0, total);
                }
                this.methods = temp2;
            }
            return parameterizedMethods;
        }
        finally {
            if (parameterizedMethods == null) {
                parameterizedMethods = (this.methods = Binding.NO_METHODS);
            }
        }
    }
    
    @Override
    public int getOuterLocalVariablesSlotSize() {
        return this.genericType().getOuterLocalVariablesSlotSize();
    }
    
    @Override
    public boolean hasMemberTypes() {
        return this.type.hasMemberTypes();
    }
    
    @Override
    public boolean hasTypeBit(final int bit) {
        final TypeBinding erasure = this.erasure();
        return erasure instanceof ReferenceBinding && erasure.hasTypeBit(bit);
    }
    
    public boolean implementsMethod(final MethodBinding method) {
        return this.type.implementsMethod(method);
    }
    
    void initialize(final ReferenceBinding someType, final TypeBinding[] someArguments) {
        this.type = someType;
        this.sourceName = someType.sourceName;
        this.compoundName = someType.compoundName;
        this.fPackage = someType.fPackage;
        this.fileName = someType.fileName;
        this.modifiers = (someType.modifiers & 0xBFFFFFFF);
        if (someArguments != null) {
            this.modifiers |= 0x40000000;
        }
        else if (this.enclosingType != null) {
            this.modifiers |= (this.enclosingType.modifiers & 0x40000000);
            this.tagBits |= (this.enclosingType.tagBits & 0x2000000020000080L);
        }
        if (someArguments != null) {
            this.arguments = someArguments;
            for (int i = 0, length = someArguments.length; i < length; ++i) {
                final TypeBinding someArgument = someArguments[i];
                switch (someArgument.kind()) {
                    case 516: {
                        this.tagBits |= 0x40000000L;
                        if (((WildcardBinding)someArgument).boundKind != 0) {
                            this.tagBits |= 0x800000L;
                            break;
                        }
                        break;
                    }
                    case 8196: {
                        this.tagBits |= 0x40800000L;
                        break;
                    }
                    default: {
                        this.tagBits |= 0x800000L;
                        break;
                    }
                }
                this.tagBits |= (someArgument.tagBits & 0x2000000020000880L);
            }
        }
        this.tagBits |= (someType.tagBits & 0x278000000000089CL);
        this.tagBits &= 0xFFFFFFFFFFFF5FFFL;
    }
    
    protected void initializeArguments() {
    }
    
    @Override
    void initializeForStaticImports() {
        this.type.initializeForStaticImports();
    }
    
    @Override
    public boolean isBoundParameterizedType() {
        return (this.tagBits & 0x800000L) != 0x0L;
    }
    
    @Override
    public boolean isEquivalentTo(final TypeBinding otherType) {
        if (TypeBinding.equalsEquals(this, otherType)) {
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
            case 260: {
                final ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding)otherType;
                if (TypeBinding.notEquals(this.type, otherParamType.type)) {
                    return false;
                }
                if (!this.isStatic()) {
                    final ReferenceBinding enclosing = this.enclosingType();
                    if (enclosing != null) {
                        final ReferenceBinding otherEnclosing = otherParamType.enclosingType();
                        if (otherEnclosing == null) {
                            return false;
                        }
                        if ((otherEnclosing.tagBits & 0x40000000L) == 0x0L) {
                            if (TypeBinding.notEquals(enclosing, otherEnclosing)) {
                                return false;
                            }
                        }
                        else if (!enclosing.isEquivalentTo(otherParamType.enclosingType())) {
                            return false;
                        }
                    }
                }
                if (this.arguments != ParameterizedSingleTypeReference.DIAMOND_TYPE_ARGUMENTS) {
                    if (this.arguments == null) {
                        return otherParamType.arguments == null;
                    }
                    final int length = this.arguments.length;
                    final TypeBinding[] otherArguments = otherParamType.arguments;
                    if (otherArguments == null || otherArguments.length != length) {
                        return false;
                    }
                    for (int i = 0; i < length; ++i) {
                        if (!this.arguments[i].isTypeArgumentContainedBy(otherArguments[i])) {
                            return false;
                        }
                    }
                }
                return true;
            }
            case 1028: {
                return TypeBinding.equalsEquals(this.erasure(), otherType.erasure());
            }
            default: {
                return TypeBinding.equalsEquals(this.erasure(), otherType);
            }
        }
    }
    
    @Override
    public boolean isHierarchyConnected() {
        return this.superclass != null && this.superInterfaces != null;
    }
    
    @Override
    public boolean isProperType(final boolean admitCapture18) {
        if (this.arguments != null) {
            for (int i = 0; i < this.arguments.length; ++i) {
                if (!this.arguments[i].isProperType(admitCapture18)) {
                    return false;
                }
            }
        }
        return super.isProperType(admitCapture18);
    }
    
    @Override
    TypeBinding substituteInferenceVariable(final InferenceVariable var, final TypeBinding substituteType) {
        if (this.arguments != null) {
            TypeBinding[] newArgs = null;
            for (int length = this.arguments.length, i = 0; i < length; ++i) {
                final TypeBinding oldArg = this.arguments[i];
                final TypeBinding newArg = oldArg.substituteInferenceVariable(var, substituteType);
                if (TypeBinding.notEquals(newArg, oldArg)) {
                    if (newArgs == null) {
                        System.arraycopy(this.arguments, 0, newArgs = new TypeBinding[length], 0, length);
                    }
                    newArgs[i] = newArg;
                }
            }
            if (newArgs != null) {
                return this.environment.createParameterizedType(this.type, newArgs, this.enclosingType);
            }
        }
        return this;
    }
    
    @Override
    public boolean isRawSubstitution() {
        return this.isRawType();
    }
    
    @Override
    public TypeBinding unannotated() {
        return this.hasTypeAnnotations() ? this.environment.getUnannotatedType(this) : this;
    }
    
    @Override
    public TypeBinding withoutToplevelNullAnnotation() {
        if (!this.hasNullTypeAnnotations()) {
            return this;
        }
        final ReferenceBinding unannotatedGenericType = (ReferenceBinding)this.environment.getUnannotatedType(this.type);
        final AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.typeAnnotations);
        return this.environment.createParameterizedType(unannotatedGenericType, this.arguments, this.enclosingType, newAnnotations);
    }
    
    @Override
    public int kind() {
        return 260;
    }
    
    @Override
    public ReferenceBinding[] memberTypes() {
        if (this.memberTypes == null) {
            try {
                final ReferenceBinding[] originalMemberTypes = this.type.memberTypes();
                final int length = originalMemberTypes.length;
                final ReferenceBinding[] parameterizedMemberTypes = new ReferenceBinding[length];
                for (int i = 0; i < length; ++i) {
                    parameterizedMemberTypes[i] = this.environment.createParameterizedType(originalMemberTypes[i], null, this);
                }
                this.memberTypes = parameterizedMemberTypes;
            }
            finally {
                if (this.memberTypes == null) {
                    this.memberTypes = Binding.NO_MEMBER_TYPES;
                }
            }
            if (this.memberTypes == null) {
                this.memberTypes = Binding.NO_MEMBER_TYPES;
            }
        }
        return this.memberTypes;
    }
    
    @Override
    public boolean mentionsAny(final TypeBinding[] parameters, final int idx) {
        if (super.mentionsAny(parameters, idx)) {
            return true;
        }
        if (this.arguments != null) {
            for (int len = this.arguments.length, i = 0; i < len; ++i) {
                if (TypeBinding.notEquals(this.arguments[i], this) && this.arguments[i].mentionsAny(parameters, idx)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    void collectInferenceVariables(final Set<InferenceVariable> variables) {
        if (this.arguments != null) {
            for (int len = this.arguments.length, i = 0; i < len; ++i) {
                if (TypeBinding.notEquals(this.arguments[i], this)) {
                    this.arguments[i].collectInferenceVariables(variables);
                }
            }
        }
    }
    
    @Override
    public MethodBinding[] methods() {
        if ((this.tagBits & 0x8000L) != 0x0L) {
            return this.methods;
        }
        try {
            final MethodBinding[] originalMethods = this.type.methods();
            final int length = originalMethods.length;
            final MethodBinding[] parameterizedMethods = new MethodBinding[length];
            final boolean useNullTypeAnnotations = this.environment.usesNullTypeAnnotations();
            for (int i = 0; i < length; ++i) {
                parameterizedMethods[i] = this.createParameterizedMethod(originalMethods[i]);
                if (useNullTypeAnnotations) {
                    parameterizedMethods[i] = NullAnnotationMatching.checkForContradictions(parameterizedMethods[i], null, null);
                }
            }
            this.methods = parameterizedMethods;
        }
        finally {
            if (this.methods == null) {
                this.methods = Binding.NO_METHODS;
            }
            this.tagBits |= 0x8000L;
        }
        if (this.methods == null) {
            this.methods = Binding.NO_METHODS;
        }
        this.tagBits |= 0x8000L;
        return this.methods;
    }
    
    @Override
    public int problemId() {
        return this.type.problemId();
    }
    
    @Override
    public char[] qualifiedPackageName() {
        return this.type.qualifiedPackageName();
    }
    
    @Override
    public char[] qualifiedSourceName() {
        return this.type.qualifiedSourceName();
    }
    
    @Override
    public char[] readableName() {
        final StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(CharOperation.concat(this.enclosingType().readableName(), this.sourceName, '.'));
        }
        else {
            nameBuffer.append(CharOperation.concatWith(this.type.compoundName, '.'));
        }
        if (this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = this.arguments.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].readableName());
            }
            nameBuffer.append('>');
        }
        final int nameLength = nameBuffer.length();
        final char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }
    
    ReferenceBinding resolve() {
        if ((this.tagBits & 0x1000000L) == 0x0L) {
            return this;
        }
        this.tagBits &= 0xFFFFFFFFFEFFFFFFL;
        final ReferenceBinding resolvedType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.type, this.environment, false);
        this.tagBits |= (resolvedType.tagBits & 0x800L);
        if (this.arguments != null) {
            for (int argLength = this.arguments.length, i = 0; i < argLength; ++i) {
                final TypeBinding resolveType = BinaryTypeBinding.resolveType(this.arguments[i], this.environment, true);
                this.arguments[i] = resolveType;
                this.tagBits |= (resolvedType.tagBits & 0x800L);
            }
        }
        return this;
    }
    
    @Override
    public char[] shortReadableName() {
        final StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(CharOperation.concat(this.enclosingType().shortReadableName(), this.sourceName, '.'));
        }
        else {
            nameBuffer.append(this.type.sourceName);
        }
        if (this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = this.arguments.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].shortReadableName());
            }
            nameBuffer.append('>');
        }
        final int nameLength = nameBuffer.length();
        final char[] shortReadableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        return shortReadableName;
    }
    
    @Override
    public char[] nullAnnotatedReadableName(final CompilerOptions options, final boolean shortNames) {
        if (shortNames) {
            return this.nullAnnotatedShortReadableName(options);
        }
        return this.nullAnnotatedReadableName(options);
    }
    
    @Override
    char[] nullAnnotatedReadableName(final CompilerOptions options) {
        final StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(this.enclosingType().nullAnnotatedReadableName(options, false));
            nameBuffer.append('.');
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.sourceName);
        }
        else if (this.type.compoundName != null) {
            int l;
            int i;
            for (l = this.type.compoundName.length, i = 0; i < l - 1; ++i) {
                nameBuffer.append(this.type.compoundName[i]);
                nameBuffer.append('.');
            }
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.type.compoundName[i]);
        }
        else {
            this.appendNullAnnotation(nameBuffer, options);
            if (this.type.sourceName != null) {
                nameBuffer.append(this.type.sourceName);
            }
            else {
                nameBuffer.append(this.type.readableName());
            }
        }
        if (this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = this.arguments.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].nullAnnotatedReadableName(options, false));
            }
            nameBuffer.append('>');
        }
        final int nameLength = nameBuffer.length();
        final char[] readableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, readableName, 0);
        return readableName;
    }
    
    @Override
    char[] nullAnnotatedShortReadableName(final CompilerOptions options) {
        final StringBuffer nameBuffer = new StringBuffer(10);
        if (this.isMemberType()) {
            nameBuffer.append(this.enclosingType().nullAnnotatedReadableName(options, true));
            nameBuffer.append('.');
            this.appendNullAnnotation(nameBuffer, options);
            nameBuffer.append(this.sourceName);
        }
        else {
            this.appendNullAnnotation(nameBuffer, options);
            if (this.type.sourceName != null) {
                nameBuffer.append(this.type.sourceName);
            }
            else {
                nameBuffer.append(this.type.shortReadableName());
            }
        }
        if (this.arguments != null && this.arguments.length > 0) {
            nameBuffer.append('<');
            for (int i = 0, length = this.arguments.length; i < length; ++i) {
                if (i > 0) {
                    nameBuffer.append(',');
                }
                nameBuffer.append(this.arguments[i].nullAnnotatedReadableName(options, true));
            }
            nameBuffer.append('>');
        }
        final int nameLength = nameBuffer.length();
        final char[] shortReadableName = new char[nameLength];
        nameBuffer.getChars(0, nameLength, shortReadableName, 0);
        return shortReadableName;
    }
    
    @Override
    public char[] signature() {
        if (this.signature == null) {
            this.signature = this.type.signature();
        }
        return this.signature;
    }
    
    @Override
    public char[] sourceName() {
        return this.type.sourceName();
    }
    
    @Override
    public TypeBinding substitute(final TypeVariableBinding originalVariable) {
        ParameterizedTypeBinding currentType = this;
        while (true) {
            final TypeVariableBinding[] typeVariables = currentType.type.typeVariables();
            final int length = typeVariables.length;
            if (originalVariable.rank < length && TypeBinding.equalsEquals(typeVariables[originalVariable.rank], originalVariable)) {
                if (currentType.arguments == null) {
                    currentType.initializeArguments();
                }
                if (currentType.arguments != null) {
                    if (currentType.arguments.length == 0) {
                        return originalVariable;
                    }
                    final TypeBinding substitute = currentType.arguments[originalVariable.rank];
                    return originalVariable.combineTypeAnnotations(substitute);
                }
            }
            if (currentType.isStatic()) {
                break;
            }
            final ReferenceBinding enclosing = currentType.enclosingType();
            if (!(enclosing instanceof ParameterizedTypeBinding)) {
                break;
            }
            currentType = (ParameterizedTypeBinding)enclosing;
        }
        return originalVariable;
    }
    
    @Override
    public ReferenceBinding superclass() {
        if (this.superclass == null) {
            final ReferenceBinding genericSuperclass = this.type.superclass();
            if (genericSuperclass == null) {
                return null;
            }
            this.superclass = (ReferenceBinding)Scope.substitute(this, genericSuperclass);
            this.typeBits |= (this.superclass.typeBits & 0x13);
            if ((this.typeBits & 0x3) != 0x0) {
                this.typeBits |= this.applyCloseableClassWhitelists();
            }
        }
        return this.superclass;
    }
    
    @Override
    public ReferenceBinding[] superInterfaces() {
        if (this.superInterfaces == null) {
            if (this.type.isHierarchyBeingConnected()) {
                return Binding.NO_SUPERINTERFACES;
            }
            this.superInterfaces = Scope.substitute(this, this.type.superInterfaces());
            if (this.superInterfaces != null) {
                int i = this.superInterfaces.length;
                while (--i >= 0) {
                    this.typeBits |= (this.superInterfaces[i].typeBits & 0x13);
                    if ((this.typeBits & 0x3) != 0x0) {
                        this.typeBits |= this.applyCloseableInterfaceWhitelists();
                    }
                }
            }
        }
        return this.superInterfaces;
    }
    
    @Override
    public void swapUnresolved(final UnresolvedReferenceBinding unresolvedType, final ReferenceBinding resolvedType, final LookupEnvironment env) {
        boolean update = false;
        if (this.type == unresolvedType) {
            this.type = resolvedType;
            update = true;
            final ReferenceBinding enclosing = resolvedType.enclosingType();
            if (enclosing != null) {
                this.enclosingType = (ReferenceBinding)env.convertUnresolvedBinaryToRawType(enclosing);
            }
        }
        if (this.arguments != null) {
            for (int i = 0, l = this.arguments.length; i < l; ++i) {
                if (this.arguments[i] == unresolvedType) {
                    this.arguments[i] = env.convertUnresolvedBinaryToRawType(resolvedType);
                    update = true;
                }
            }
        }
        if (update) {
            this.initialize(this.type, this.arguments);
        }
    }
    
    @Override
    public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
        return this.genericType().syntheticEnclosingInstanceTypes();
    }
    
    @Override
    public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
        return this.genericType().syntheticOuterLocalVariables();
    }
    
    @Override
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        final StringBuffer buffer = new StringBuffer(30);
        if (this.type instanceof UnresolvedReferenceBinding) {
            buffer.append(this.debugName());
        }
        else {
            if (this.isDeprecated()) {
                buffer.append("deprecated ");
            }
            if (this.isPublic()) {
                buffer.append("public ");
            }
            if (this.isProtected()) {
                buffer.append("protected ");
            }
            if (this.isPrivate()) {
                buffer.append("private ");
            }
            if (this.isAbstract() && this.isClass()) {
                buffer.append("abstract ");
            }
            if (this.isStatic() && this.isNestedType()) {
                buffer.append("static ");
            }
            if (this.isFinal()) {
                buffer.append("final ");
            }
            if (this.isEnum()) {
                buffer.append("enum ");
            }
            else if (this.isAnnotationType()) {
                buffer.append("@interface ");
            }
            else if (this.isClass()) {
                buffer.append("class ");
            }
            else {
                buffer.append("interface ");
            }
            buffer.append(this.debugName());
            buffer.append("\n\textends ");
            buffer.append((this.superclass != null) ? this.superclass.debugName() : "NULL TYPE");
            if (this.superInterfaces != null) {
                if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
                    buffer.append("\n\timplements : ");
                    for (int i = 0, length = this.superInterfaces.length; i < length; ++i) {
                        if (i > 0) {
                            buffer.append(", ");
                        }
                        buffer.append((this.superInterfaces[i] != null) ? this.superInterfaces[i].debugName() : "NULL TYPE");
                    }
                }
            }
            else {
                buffer.append("NULL SUPERINTERFACES");
            }
            if (this.enclosingType() != null) {
                buffer.append("\n\tenclosing type : ");
                buffer.append(this.enclosingType().debugName());
            }
            if (this.fields != null) {
                if (this.fields != Binding.NO_FIELDS) {
                    buffer.append("\n/*   fields   */");
                    for (int i = 0, length = this.fields.length; i < length; ++i) {
                        buffer.append('\n').append((this.fields[i] != null) ? this.fields[i].toString() : "NULL FIELD");
                    }
                }
            }
            else {
                buffer.append("NULL FIELDS");
            }
            if (this.methods != null) {
                if (this.methods != Binding.NO_METHODS) {
                    buffer.append("\n/*   methods   */");
                    for (int i = 0, length = this.methods.length; i < length; ++i) {
                        buffer.append('\n').append((this.methods[i] != null) ? this.methods[i].toString() : "NULL METHOD");
                    }
                }
            }
            else {
                buffer.append("NULL METHODS");
            }
            buffer.append("\n\n");
        }
        return buffer.toString();
    }
    
    @Override
    public TypeVariableBinding[] typeVariables() {
        if (this.arguments == null) {
            return this.type.typeVariables();
        }
        return Binding.NO_TYPE_VARIABLES;
    }
    
    public TypeBinding[] typeArguments() {
        return this.arguments;
    }
    
    @Override
    public FieldBinding[] unResolvedFields() {
        return this.fields;
    }
    
    @Override
    protected MethodBinding[] getInterfaceAbstractContracts(final Scope scope, final boolean replaceWildcards) throws InvalidInputException {
        if (replaceWildcards) {
            final TypeBinding[] types = this.getNonWildcardParameterization(scope);
            if (types == null) {
                return new MethodBinding[] { new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 18) };
            }
            for (int i = 0; i < types.length; ++i) {
                if (TypeBinding.notEquals(types[i], this.arguments[i])) {
                    final ParameterizedTypeBinding declaringType = scope.environment().createParameterizedType(this.type, types, this.type.enclosingType());
                    final TypeVariableBinding[] typeParameters = this.type.typeVariables();
                    for (int j = 0, length = typeParameters.length; j < length; ++j) {
                        if (!typeParameters[j].boundCheck(declaringType, types[j], scope, null).isOKbyJLS()) {
                            return new MethodBinding[] { new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 18) };
                        }
                    }
                    return declaringType.getInterfaceAbstractContracts(scope, replaceWildcards);
                }
            }
        }
        return super.getInterfaceAbstractContracts(scope, replaceWildcards);
    }
    
    @Override
    public MethodBinding getSingleAbstractMethod(final Scope scope, final boolean replaceWildcards) {
        return this.getSingleAbstractMethod(scope, replaceWildcards, -1, -1);
    }
    
    public MethodBinding getSingleAbstractMethod(final Scope scope, final boolean replaceWildcards, final int start, final int end) {
        final int index = replaceWildcards ? ((end < 0) ? 0 : 1) : 2;
        if (this.singleAbstractMethod != null) {
            if (this.singleAbstractMethod[index] != null) {
                return this.singleAbstractMethod[index];
            }
        }
        else {
            this.singleAbstractMethod = new MethodBinding[3];
        }
        if (!this.isValidBinding()) {
            return null;
        }
        final ReferenceBinding genericType = this.genericType();
        final MethodBinding theAbstractMethod = genericType.getSingleAbstractMethod(scope, replaceWildcards);
        if (theAbstractMethod == null || !theAbstractMethod.isValidBinding()) {
            return this.singleAbstractMethod[index] = theAbstractMethod;
        }
        ParameterizedTypeBinding declaringType = null;
        TypeBinding[] types = this.arguments;
        if (replaceWildcards) {
            types = this.getNonWildcardParameterization(scope);
            if (types == null) {
                return this.singleAbstractMethod[index] = new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 18);
            }
        }
        else if (types == null) {
            types = ParameterizedTypeBinding.NO_TYPES;
        }
        if (end >= 0) {
            for (int i = 0, length = types.length; i < length; ++i) {
                types[i] = types[i].capture(scope, start, end);
            }
        }
        declaringType = scope.environment().createParameterizedType(genericType, types, genericType.enclosingType());
        final TypeVariableBinding[] typeParameters = genericType.typeVariables();
        for (int j = 0, length2 = typeParameters.length; j < length2; ++j) {
            if (!typeParameters[j].boundCheck(declaringType, types[j], scope, null).isOKbyJLS()) {
                return this.singleAbstractMethod[index] = new ProblemMethodBinding(TypeConstants.ANONYMOUS_METHOD, null, 18);
            }
        }
        final ReferenceBinding substitutedDeclaringType = (ReferenceBinding)declaringType.findSuperTypeOriginatingFrom(theAbstractMethod.declaringClass);
        final MethodBinding[] choices = substitutedDeclaringType.getMethods(theAbstractMethod.selector);
        for (int k = 0, length3 = choices.length; k < length3; ++k) {
            final MethodBinding method = choices[k];
            if (method.isAbstract() && !method.redeclaresPublicObjectMethod(scope)) {
                this.singleAbstractMethod[index] = method;
                break;
            }
        }
        return this.singleAbstractMethod[index];
    }
    
    public TypeBinding[] getNonWildcardParameterization(final Scope scope) {
        final TypeBinding[] typeArguments = this.arguments;
        if (typeArguments == null) {
            return ParameterizedTypeBinding.NO_TYPES;
        }
        final TypeVariableBinding[] typeParameters = this.genericType().typeVariables();
        final TypeBinding[] types = new TypeBinding[typeArguments.length];
        for (int i = 0, length = typeArguments.length; i < length; ++i) {
            final TypeBinding typeArgument = typeArguments[i];
            if (typeArgument.kind() == 516) {
                if (typeParameters[i].mentionsAny(typeParameters, i)) {
                    return null;
                }
                final WildcardBinding wildcard = (WildcardBinding)typeArgument;
                switch (wildcard.boundKind) {
                    case 1: {
                        final TypeBinding[] otherUBounds = wildcard.otherBounds;
                        final TypeBinding[] otherBBounds = typeParameters[i].otherUpperBounds();
                        int len = 1 + ((otherUBounds != null) ? otherUBounds.length : 0) + otherBBounds.length;
                        if (typeParameters[i].firstBound != null) {
                            ++len;
                        }
                        final TypeBinding[] allBounds = new TypeBinding[len];
                        int idx = 0;
                        allBounds[idx++] = wildcard.bound;
                        if (otherUBounds != null) {
                            for (int j = 0; j < otherUBounds.length; ++j) {
                                allBounds[idx++] = otherUBounds[j];
                            }
                        }
                        if (typeParameters[i].firstBound != null) {
                            allBounds[idx++] = typeParameters[i].firstBound;
                        }
                        for (int j = 0; j < otherBBounds.length; ++j) {
                            allBounds[idx++] = otherBBounds[j];
                        }
                        final TypeBinding[] glb = Scope.greaterLowerBound(allBounds, null, this.environment);
                        if (glb == null || glb.length == 0) {
                            return null;
                        }
                        if (glb.length == 1) {
                            types[i] = glb[0];
                            break;
                        }
                        try {
                            final ReferenceBinding[] refs = new ReferenceBinding[glb.length];
                            System.arraycopy(glb, 0, refs, 0, glb.length);
                            types[i] = this.environment.createIntersectionType18(refs);
                            break;
                        }
                        catch (final ArrayStoreException ex) {
                            scope.problemReporter().genericInferenceError("Cannot compute glb of " + Arrays.toString(glb), null);
                            return null;
                        }
                    }
                    case 2: {
                        types[i] = wildcard.bound;
                        break;
                    }
                    case 0: {
                        types[i] = typeParameters[i].firstBound;
                        if (types[i] == null) {
                            types[i] = typeParameters[i].superclass;
                            break;
                        }
                        break;
                    }
                }
            }
            else {
                types[i] = typeArgument;
            }
        }
        return types;
    }
    
    @Override
    public long updateTagBits() {
        if (this.arguments != null) {
            TypeBinding[] arguments;
            for (int length = (arguments = this.arguments).length, i = 0; i < length; ++i) {
                final TypeBinding argument = arguments[i];
                this.tagBits |= argument.updateTagBits();
            }
        }
        return super.updateTagBits();
    }
}
