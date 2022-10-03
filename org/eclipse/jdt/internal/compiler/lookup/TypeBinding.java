package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import java.util.Set;
import java.util.List;

public abstract class TypeBinding extends Binding
{
    public int id;
    public long tagBits;
    protected AnnotationBinding[] typeAnnotations;
    public static final ReferenceBinding TYPE_USE_BINDING;
    public static final BaseTypeBinding INT;
    public static final BaseTypeBinding BYTE;
    public static final BaseTypeBinding SHORT;
    public static final BaseTypeBinding CHAR;
    public static final BaseTypeBinding LONG;
    public static final BaseTypeBinding FLOAT;
    public static final BaseTypeBinding DOUBLE;
    public static final BaseTypeBinding BOOLEAN;
    public static final NullTypeBinding NULL;
    public static final VoidTypeBinding VOID;
    
    static {
        TYPE_USE_BINDING = new ReferenceBinding() {
            {
                this.id = 0;
            }
            
            @Override
            public int kind() {
                return 16388;
            }
            
            @Override
            public boolean hasTypeBit(final int bit) {
                return false;
            }
        };
        INT = new BaseTypeBinding(10, TypeConstants.INT, new char[] { 'I' });
        BYTE = new BaseTypeBinding(3, TypeConstants.BYTE, new char[] { 'B' });
        SHORT = new BaseTypeBinding(4, TypeConstants.SHORT, new char[] { 'S' });
        CHAR = new BaseTypeBinding(2, TypeConstants.CHAR, new char[] { 'C' });
        LONG = new BaseTypeBinding(7, TypeConstants.LONG, new char[] { 'J' });
        FLOAT = new BaseTypeBinding(9, TypeConstants.FLOAT, new char[] { 'F' });
        DOUBLE = new BaseTypeBinding(8, TypeConstants.DOUBLE, new char[] { 'D' });
        BOOLEAN = new BaseTypeBinding(5, TypeConstants.BOOLEAN, new char[] { 'Z' });
        NULL = new NullTypeBinding();
        VOID = new VoidTypeBinding();
    }
    
    public TypeBinding() {
        this.id = Integer.MAX_VALUE;
        this.tagBits = 0L;
        this.typeAnnotations = Binding.NO_ANNOTATIONS;
    }
    
    public TypeBinding(final TypeBinding prototype) {
        this.id = Integer.MAX_VALUE;
        this.tagBits = 0L;
        this.typeAnnotations = Binding.NO_ANNOTATIONS;
        this.id = prototype.id;
        this.tagBits = (prototype.tagBits & 0xFE7FFFFFFFFFFFFFL);
    }
    
    public static final TypeBinding wellKnownType(final Scope scope, final int id) {
        switch (id) {
            case 5: {
                return TypeBinding.BOOLEAN;
            }
            case 3: {
                return TypeBinding.BYTE;
            }
            case 2: {
                return TypeBinding.CHAR;
            }
            case 4: {
                return TypeBinding.SHORT;
            }
            case 8: {
                return TypeBinding.DOUBLE;
            }
            case 9: {
                return TypeBinding.FLOAT;
            }
            case 10: {
                return TypeBinding.INT;
            }
            case 7: {
                return TypeBinding.LONG;
            }
            case 1: {
                return scope.getJavaLangObject();
            }
            case 11: {
                return scope.getJavaLangString();
            }
            default: {
                return null;
            }
        }
    }
    
    public ReferenceBinding actualType() {
        return null;
    }
    
    TypeBinding[] additionalBounds() {
        return null;
    }
    
    public String annotatedDebugName() {
        final TypeBinding enclosingType = this.enclosingType();
        final StringBuffer buffer = new StringBuffer(16);
        if (enclosingType != null) {
            buffer.append(enclosingType.annotatedDebugName());
            buffer.append('.');
        }
        final AnnotationBinding[] annotations = this.getTypeAnnotations();
        for (int i = 0, length = (annotations == null) ? 0 : annotations.length; i < length; ++i) {
            buffer.append(annotations[i]);
            buffer.append(' ');
        }
        buffer.append(this.sourceName());
        return buffer.toString();
    }
    
    TypeBinding bound() {
        return null;
    }
    
    int boundKind() {
        return -1;
    }
    
    int rank() {
        return -1;
    }
    
    public ReferenceBinding containerAnnotationType() {
        return null;
    }
    
    public boolean canBeInstantiated() {
        return !this.isBaseType();
    }
    
    public TypeBinding capture(final Scope scope, final int start, final int end) {
        return this;
    }
    
    public TypeBinding uncapture(final Scope scope) {
        return this;
    }
    
    public TypeBinding closestMatch() {
        return this;
    }
    
    public List<TypeBinding> collectMissingTypes(final List<TypeBinding> missingTypes) {
        return missingTypes;
    }
    
    public void collectSubstitutes(final Scope scope, final TypeBinding actualType, final InferenceContext inferenceContext, final int constraint) {
    }
    
    public TypeBinding clone(final TypeBinding enclosingType) {
        throw new IllegalStateException("TypeBinding#clone() should have been overridden");
    }
    
    public abstract char[] constantPoolName();
    
    public String debugName() {
        return this.hasTypeAnnotations() ? this.annotatedDebugName() : new String(this.readableName());
    }
    
    public int dimensions() {
        return 0;
    }
    
    public int depth() {
        return 0;
    }
    
    public MethodBinding enclosingMethod() {
        return null;
    }
    
    public ReferenceBinding enclosingType() {
        return null;
    }
    
    public TypeBinding erasure() {
        return this;
    }
    
    public ReferenceBinding findSuperTypeOriginatingFrom(final int wellKnownOriginalID, final boolean originalIsClass) {
        if (!(this instanceof ReferenceBinding)) {
            return null;
        }
        final ReferenceBinding reference = (ReferenceBinding)this;
        if (reference.id == wellKnownOriginalID || this.original().id == wellKnownOriginalID) {
            return reference;
        }
        ReferenceBinding currentType = reference;
        if (originalIsClass) {
            while ((currentType = currentType.superclass()) != null) {
                if (currentType.id == wellKnownOriginalID) {
                    return currentType;
                }
                if (currentType.original().id == wellKnownOriginalID) {
                    return currentType;
                }
            }
            return null;
        }
        ReferenceBinding[] interfacesToVisit = null;
        int nextPosition = 0;
        do {
            final ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
            if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                if (interfacesToVisit == null) {
                    interfacesToVisit = itsInterfaces;
                    nextPosition = interfacesToVisit.length;
                }
                else {
                    final int itsLength = itsInterfaces.length;
                    if (nextPosition + itsLength >= interfacesToVisit.length) {
                        System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                    }
                    int a = 0;
                Label_0224:
                    while (a < itsLength) {
                        final ReferenceBinding next = itsInterfaces[a];
                        while (true) {
                            for (int b = 0; b < nextPosition; ++b) {
                                if (equalsEquals(next, interfacesToVisit[b])) {
                                    ++a;
                                    continue Label_0224;
                                }
                            }
                            interfacesToVisit[nextPosition++] = next;
                            continue;
                        }
                    }
                }
            }
        } while ((currentType = currentType.superclass()) != null);
        for (int i = 0; i < nextPosition; ++i) {
            currentType = interfacesToVisit[i];
            if (currentType.id == wellKnownOriginalID) {
                return currentType;
            }
            if (currentType.original().id == wellKnownOriginalID) {
                return currentType;
            }
            final ReferenceBinding[] itsInterfaces2 = currentType.superInterfaces();
            if (itsInterfaces2 != null && itsInterfaces2 != Binding.NO_SUPERINTERFACES) {
                final int itsLength2 = itsInterfaces2.length;
                if (nextPosition + itsLength2 >= interfacesToVisit.length) {
                    System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength2 + 5], 0, nextPosition);
                }
                int a2 = 0;
            Label_0398:
                while (a2 < itsLength2) {
                    final ReferenceBinding next2 = itsInterfaces2[a2];
                    while (true) {
                        for (int b2 = 0; b2 < nextPosition; ++b2) {
                            if (equalsEquals(next2, interfacesToVisit[b2])) {
                                ++a2;
                                continue Label_0398;
                            }
                        }
                        interfacesToVisit[nextPosition++] = next2;
                        continue;
                    }
                }
            }
        }
        return null;
    }
    
    public TypeBinding findSuperTypeOriginatingFrom(TypeBinding otherType) {
        if (equalsEquals(this, otherType)) {
            return this;
        }
        if (otherType == null) {
            return null;
        }
        final CaptureBinding capture;
        final TypeBinding captureBound;
        final TypeBinding match;
        switch (this.kind()) {
            case 68: {
                final ArrayBinding arrayType = (ArrayBinding)this;
                final int otherDim = otherType.dimensions();
                if (arrayType.dimensions != otherDim) {
                    switch (otherType.id) {
                        case 1:
                        case 36:
                        case 37: {
                            return otherType;
                        }
                        default: {
                            if (otherDim < arrayType.dimensions && otherType.leafComponentType().id == 1) {
                                return otherType;
                            }
                            return null;
                        }
                    }
                }
                else {
                    if (!(arrayType.leafComponentType instanceof ReferenceBinding)) {
                        return null;
                    }
                    final TypeBinding leafSuperType = arrayType.leafComponentType.findSuperTypeOriginatingFrom(otherType.leafComponentType());
                    if (leafSuperType == null) {
                        return null;
                    }
                    return arrayType.environment().createArrayType(leafSuperType, arrayType.dimensions);
                }
                break;
            }
            case 4100:
                Label_0275: {
                    if (!this.isCapture()) {
                        break Label_0275;
                    }
                    capture = (CaptureBinding)this;
                    captureBound = capture.firstBound;
                    if (!(captureBound instanceof ArrayBinding)) {
                        break Label_0275;
                    }
                    match = captureBound.findSuperTypeOriginatingFrom(otherType);
                    if (match != null) {
                        return match;
                    }
                    break Label_0275;
                }
            case 4:
            case 260:
            case 516:
            case 1028:
            case 2052:
            case 8196: {
                otherType = otherType.original();
                if (equalsEquals(this, otherType)) {
                    return this;
                }
                if (equalsEquals(this.original(), otherType)) {
                    return this;
                }
                ReferenceBinding currentType = (ReferenceBinding)this;
                if (!otherType.isInterface()) {
                    while ((currentType = currentType.superclass()) != null) {
                        if (equalsEquals(currentType, otherType)) {
                            return currentType;
                        }
                        if (equalsEquals(currentType.original(), otherType)) {
                            return currentType;
                        }
                    }
                    return null;
                }
                ReferenceBinding[] interfacesToVisit = null;
                int nextPosition = 0;
                do {
                    final ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
                    if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
                        if (interfacesToVisit == null) {
                            interfacesToVisit = itsInterfaces;
                            nextPosition = interfacesToVisit.length;
                        }
                        else {
                            final int itsLength = itsInterfaces.length;
                            if (nextPosition + itsLength >= interfacesToVisit.length) {
                                System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
                            }
                            int a = 0;
                        Label_0498:
                            while (a < itsLength) {
                                final ReferenceBinding next = itsInterfaces[a];
                                while (true) {
                                    for (int b = 0; b < nextPosition; ++b) {
                                        if (equalsEquals(next, interfacesToVisit[b])) {
                                            ++a;
                                            continue Label_0498;
                                        }
                                    }
                                    interfacesToVisit[nextPosition++] = next;
                                    continue;
                                }
                            }
                        }
                    }
                } while ((currentType = currentType.superclass()) != null);
                for (int i = 0; i < nextPosition; ++i) {
                    currentType = interfacesToVisit[i];
                    if (equalsEquals(currentType, otherType)) {
                        return currentType;
                    }
                    if (equalsEquals(currentType.original(), otherType)) {
                        return currentType;
                    }
                    final ReferenceBinding[] itsInterfaces2 = currentType.superInterfaces();
                    if (itsInterfaces2 != null && itsInterfaces2 != Binding.NO_SUPERINTERFACES) {
                        final int itsLength2 = itsInterfaces2.length;
                        if (nextPosition + itsLength2 >= interfacesToVisit.length) {
                            System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength2 + 5], 0, nextPosition);
                        }
                        int a2 = 0;
                    Label_0672:
                        while (a2 < itsLength2) {
                            final ReferenceBinding next2 = itsInterfaces2[a2];
                            while (true) {
                                for (int b2 = 0; b2 < nextPosition; ++b2) {
                                    if (equalsEquals(next2, interfacesToVisit[b2])) {
                                        ++a2;
                                        continue Label_0672;
                                    }
                                }
                                interfacesToVisit[nextPosition++] = next2;
                                continue;
                            }
                        }
                    }
                }
                break;
            }
            case 32772: {
                final IntersectionTypeBinding18 itb18 = (IntersectionTypeBinding18)this;
                final ReferenceBinding[] intersectingTypes = itb18.getIntersectingTypes();
                for (int j = 0, length = intersectingTypes.length; j < length; ++j) {
                    final TypeBinding superType = intersectingTypes[j].findSuperTypeOriginatingFrom(otherType);
                    if (superType != null) {
                        return superType;
                    }
                }
                break;
            }
        }
        return null;
    }
    
    public TypeBinding genericCast(final TypeBinding targetType) {
        if (equalsEquals(this, targetType)) {
            return null;
        }
        final TypeBinding targetErasure = targetType.erasure();
        if (this.erasure().findSuperTypeOriginatingFrom(targetErasure) != null) {
            return null;
        }
        return targetErasure;
    }
    
    public char[] genericTypeSignature() {
        return this.signature();
    }
    
    public TypeBinding getErasureCompatibleType(final TypeBinding declaringClass) {
        switch (this.kind()) {
            case 4100: {
                final TypeVariableBinding variable = (TypeVariableBinding)this;
                if (variable.erasure().findSuperTypeOriginatingFrom(declaringClass) != null) {
                    return this;
                }
                if (variable.superclass != null && variable.superclass.findSuperTypeOriginatingFrom(declaringClass) != null) {
                    return variable.superclass.getErasureCompatibleType(declaringClass);
                }
                for (int i = 0, otherLength = variable.superInterfaces.length; i < otherLength; ++i) {
                    final ReferenceBinding superInterface = variable.superInterfaces[i];
                    if (superInterface.findSuperTypeOriginatingFrom(declaringClass) != null) {
                        return superInterface.getErasureCompatibleType(declaringClass);
                    }
                }
                return this;
            }
            case 8196: {
                final WildcardBinding intersection = (WildcardBinding)this;
                if (intersection.erasure().findSuperTypeOriginatingFrom(declaringClass) != null) {
                    return this;
                }
                if (intersection.superclass != null && intersection.superclass.findSuperTypeOriginatingFrom(declaringClass) != null) {
                    return intersection.superclass.getErasureCompatibleType(declaringClass);
                }
                for (int j = 0, otherLength2 = intersection.superInterfaces.length; j < otherLength2; ++j) {
                    final ReferenceBinding superInterface2 = intersection.superInterfaces[j];
                    if (superInterface2.findSuperTypeOriginatingFrom(declaringClass) != null) {
                        return superInterface2.getErasureCompatibleType(declaringClass);
                    }
                }
                return this;
            }
            default: {
                return this;
            }
        }
    }
    
    public abstract PackageBinding getPackage();
    
    void initializeForStaticImports() {
    }
    
    public final boolean isAnonymousType() {
        return (this.tagBits & 0x20L) != 0x0L;
    }
    
    public final boolean isArrayType() {
        return (this.tagBits & 0x1L) != 0x0L;
    }
    
    public final boolean isBaseType() {
        return (this.tagBits & 0x2L) != 0x0L;
    }
    
    public final boolean isPrimitiveType() {
        return (this.tagBits & 0x2L) != 0x0L && this.id != 6 && this.id != 12;
    }
    
    public final boolean isPrimitiveOrBoxedPrimitiveType() {
        if (this.isPrimitiveType()) {
            return true;
        }
        switch (this.id) {
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isBoxedPrimitiveType() {
        switch (this.id) {
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isBoundParameterizedType() {
        return false;
    }
    
    public boolean isCapture() {
        return false;
    }
    
    public boolean isClass() {
        return false;
    }
    
    public boolean isCompatibleWith(final TypeBinding right) {
        return this.isCompatibleWith(right, null);
    }
    
    public abstract boolean isCompatibleWith(final TypeBinding p0, final Scope p1);
    
    public boolean isPotentiallyCompatibleWith(final TypeBinding right, final Scope scope) {
        return this.isCompatibleWith(right, scope);
    }
    
    public boolean isBoxingCompatibleWith(final TypeBinding right, final Scope scope) {
        if (right == null) {
            return false;
        }
        if (equalsEquals(this, right)) {
            return true;
        }
        if (this.isCompatibleWith(right, scope)) {
            return true;
        }
        if (this.isBaseType() != right.isBaseType()) {
            final TypeBinding convertedType = scope.environment().computeBoxingType(this);
            if (equalsEquals(convertedType, right) || convertedType.isCompatibleWith(right, scope)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isEnum() {
        return false;
    }
    
    public boolean isEquivalentTo(final TypeBinding otherType) {
        if (equalsEquals(this, otherType)) {
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
            default: {
                return false;
            }
        }
    }
    
    public boolean isGenericType() {
        return false;
    }
    
    public final boolean isHierarchyInconsistent() {
        return (this.tagBits & 0x20000L) != 0x0L;
    }
    
    public boolean isInterface() {
        return false;
    }
    
    public boolean isFunctionalInterface(final Scope scope) {
        return false;
    }
    
    public boolean isIntersectionType() {
        return false;
    }
    
    public final boolean isLocalType() {
        return (this.tagBits & 0x10L) != 0x0L;
    }
    
    public final boolean isMemberType() {
        return (this.tagBits & 0x8L) != 0x0L;
    }
    
    public final boolean isNestedType() {
        return (this.tagBits & 0x4L) != 0x0L;
    }
    
    public final boolean isNumericType() {
        switch (this.id) {
            case 2:
            case 3:
            case 4:
            case 7:
            case 8:
            case 9:
            case 10: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public boolean isParameterizedType() {
        return false;
    }
    
    public boolean hasNullTypeAnnotations() {
        return (this.tagBits & 0x100000L) != 0x0L;
    }
    
    public boolean acceptsNonNullDefault() {
        return false;
    }
    
    public boolean isIntersectionType18() {
        return false;
    }
    
    public final boolean isParameterizedTypeWithActualArguments() {
        return this.kind() == 260 && ((ParameterizedTypeBinding)this).arguments != null;
    }
    
    public boolean isParameterizedWithOwnVariables() {
        if (this.kind() != 260) {
            return false;
        }
        final ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)this;
        if (paramType.arguments == null) {
            return false;
        }
        final TypeVariableBinding[] variables = this.erasure().typeVariables();
        for (int i = 0, length = variables.length; i < length; ++i) {
            if (notEquals(variables[i], paramType.arguments[i])) {
                return false;
            }
        }
        final ReferenceBinding enclosing = paramType.enclosingType();
        return enclosing == null || !enclosing.erasure().isGenericType() || enclosing.isParameterizedWithOwnVariables();
    }
    
    public boolean isProperType(final boolean admitCapture18) {
        return true;
    }
    
    public boolean isPolyType() {
        return false;
    }
    
    TypeBinding substituteInferenceVariable(final InferenceVariable var, final TypeBinding substituteType) {
        return this;
    }
    
    private boolean isProvableDistinctSubType(final TypeBinding otherType) {
        if (otherType.isInterface()) {
            return !this.isInterface() && (this.isArrayType() || (this instanceof ReferenceBinding && ((ReferenceBinding)this).isFinal()) || (this.isTypeVariable() && ((TypeVariableBinding)this).superclass().isFinal())) && !this.isCompatibleWith(otherType);
        }
        if (this.isInterface()) {
            if (otherType.isArrayType() || (otherType instanceof ReferenceBinding && ((ReferenceBinding)otherType).isFinal()) || (otherType.isTypeVariable() && ((TypeVariableBinding)otherType).superclass().isFinal())) {
                return !this.isCompatibleWith(otherType);
            }
        }
        else if (!this.isTypeVariable() && !otherType.isTypeVariable()) {
            return !this.isCompatibleWith(otherType);
        }
        return false;
    }
    
    public boolean isProvablyDistinct(final TypeBinding otherType) {
        if (equalsEquals(this, otherType)) {
            return false;
        }
        if (otherType == null) {
            return true;
        }
        Label_0584: {
            switch (this.kind()) {
                case 260: {
                    final ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)this;
                    switch (otherType.kind()) {
                        case 260: {
                            final ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding)otherType;
                            if (notEquals(paramType.genericType(), otherParamType.genericType())) {
                                return true;
                            }
                            if (!paramType.isStatic()) {
                                final ReferenceBinding enclosing = this.enclosingType();
                                if (enclosing != null) {
                                    final ReferenceBinding otherEnclosing = otherParamType.enclosingType();
                                    if (otherEnclosing == null) {
                                        return true;
                                    }
                                    if ((otherEnclosing.tagBits & 0x40000000L) == 0x0L) {
                                        if (enclosing.isProvablyDistinct(otherEnclosing)) {
                                            return true;
                                        }
                                    }
                                    else if (!enclosing.isEquivalentTo(otherParamType.enclosingType())) {
                                        return true;
                                    }
                                }
                            }
                            final int length = (paramType.arguments == null) ? 0 : paramType.arguments.length;
                            final TypeBinding[] otherArguments = otherParamType.arguments;
                            final int otherLength = (otherArguments == null) ? 0 : otherArguments.length;
                            if (otherLength != length) {
                                return true;
                            }
                            for (int i = 0; i < length; ++i) {
                                if (paramType.arguments[i].isProvablyDistinctTypeArgument(otherArguments[i], paramType, i)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                        case 2052: {
                            if (notEquals(paramType.genericType(), otherType)) {
                                return true;
                            }
                            if (!paramType.isStatic()) {
                                final ReferenceBinding enclosing2 = this.enclosingType();
                                if (enclosing2 != null) {
                                    final ReferenceBinding otherEnclosing2 = otherType.enclosingType();
                                    if (otherEnclosing2 == null) {
                                        return true;
                                    }
                                    if ((otherEnclosing2.tagBits & 0x40000000L) == 0x0L) {
                                        if (notEquals(enclosing2, otherEnclosing2)) {
                                            return true;
                                        }
                                    }
                                    else if (!enclosing2.isEquivalentTo(otherType.enclosingType())) {
                                        return true;
                                    }
                                }
                            }
                            final int length = (paramType.arguments == null) ? 0 : paramType.arguments.length;
                            final TypeBinding[] otherArguments = otherType.typeVariables();
                            final int otherLength = (otherArguments == null) ? 0 : otherArguments.length;
                            if (otherLength != length) {
                                return true;
                            }
                            for (int i = 0; i < length; ++i) {
                                if (paramType.arguments[i].isProvablyDistinctTypeArgument(otherArguments[i], paramType, i)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                        case 1028: {
                            return notEquals(this.erasure(), otherType.erasure());
                        }
                        case 4: {
                            return notEquals(this.erasure(), otherType);
                        }
                        default: {
                            return true;
                        }
                    }
                    break;
                }
                case 1028: {
                    switch (otherType.kind()) {
                        case 4:
                        case 260:
                        case 1028:
                        case 2052: {
                            return notEquals(this.erasure(), otherType.erasure());
                        }
                        default: {
                            return true;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (otherType.kind()) {
                        case 260:
                        case 1028: {
                            return notEquals(this, otherType.erasure());
                        }
                        default: {
                            break Label_0584;
                        }
                    }
                    break;
                }
            }
        }
        return true;
    }
    
    private boolean isProvablyDistinctTypeArgument(final TypeBinding otherArgument, final ParameterizedTypeBinding paramType, final int rank) {
        if (equalsEquals(this, otherArgument)) {
            return false;
        }
        TypeBinding upperBound1 = null;
        TypeBinding lowerBound1 = null;
        final ReferenceBinding genericType = paramType.genericType();
        Label_0358: {
            switch (this.kind()) {
                case 516: {
                    final WildcardBinding wildcard = (WildcardBinding)this;
                    switch (wildcard.boundKind) {
                        case 1: {
                            upperBound1 = wildcard.bound;
                            break Label_0358;
                        }
                        case 2: {
                            lowerBound1 = wildcard.bound;
                            break Label_0358;
                        }
                        case 0: {
                            return false;
                        }
                        default: {
                            break Label_0358;
                        }
                    }
                    break;
                }
                case 4100: {
                    final TypeVariableBinding variable = (TypeVariableBinding)this;
                    if (variable.isCapture()) {
                        if (variable instanceof CaptureBinding18) {
                            final CaptureBinding18 cb18 = (CaptureBinding18)variable;
                            upperBound1 = cb18.firstBound;
                            lowerBound1 = cb18.lowerBound;
                            break;
                        }
                        final CaptureBinding capture = (CaptureBinding)variable;
                        switch (capture.wildcard.boundKind) {
                            case 1: {
                                upperBound1 = capture.wildcard.bound;
                                break Label_0358;
                            }
                            case 2: {
                                lowerBound1 = capture.wildcard.bound;
                                break Label_0358;
                            }
                            case 0: {
                                return false;
                            }
                            default: {
                                break Label_0358;
                            }
                        }
                    }
                    else {
                        if (variable.firstBound == null) {
                            return false;
                        }
                        final TypeBinding eliminatedType = Scope.convertEliminatingTypeVariables(variable, genericType, rank, null);
                        switch (eliminatedType.kind()) {
                            case 516:
                            case 8196: {
                                final WildcardBinding wildcard = (WildcardBinding)eliminatedType;
                                switch (wildcard.boundKind) {
                                    case 1: {
                                        upperBound1 = wildcard.bound;
                                        break Label_0358;
                                    }
                                    case 2: {
                                        lowerBound1 = wildcard.bound;
                                        break Label_0358;
                                    }
                                    case 0: {
                                        return false;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        TypeBinding upperBound2 = null;
        TypeBinding lowerBound2 = null;
        Label_0702: {
            switch (otherArgument.kind()) {
                case 516: {
                    final WildcardBinding otherWildcard = (WildcardBinding)otherArgument;
                    switch (otherWildcard.boundKind) {
                        case 1: {
                            upperBound2 = otherWildcard.bound;
                            break Label_0702;
                        }
                        case 2: {
                            lowerBound2 = otherWildcard.bound;
                            break Label_0702;
                        }
                        case 0: {
                            return false;
                        }
                        default: {
                            break Label_0702;
                        }
                    }
                    break;
                }
                case 4100: {
                    final TypeVariableBinding otherVariable = (TypeVariableBinding)otherArgument;
                    if (otherVariable.isCapture()) {
                        if (otherVariable instanceof CaptureBinding18) {
                            final CaptureBinding18 cb19 = (CaptureBinding18)otherVariable;
                            upperBound2 = cb19.firstBound;
                            lowerBound2 = cb19.lowerBound;
                            break;
                        }
                        final CaptureBinding otherCapture = (CaptureBinding)otherVariable;
                        switch (otherCapture.wildcard.boundKind) {
                            case 1: {
                                upperBound2 = otherCapture.wildcard.bound;
                                break Label_0702;
                            }
                            case 2: {
                                lowerBound2 = otherCapture.wildcard.bound;
                                break Label_0702;
                            }
                            case 0: {
                                return false;
                            }
                            default: {
                                break Label_0702;
                            }
                        }
                    }
                    else {
                        if (otherVariable.firstBound == null) {
                            return false;
                        }
                        final TypeBinding otherEliminatedType = Scope.convertEliminatingTypeVariables(otherVariable, genericType, rank, null);
                        switch (otherEliminatedType.kind()) {
                            case 516:
                            case 8196: {
                                final WildcardBinding otherWildcard = (WildcardBinding)otherEliminatedType;
                                switch (otherWildcard.boundKind) {
                                    case 1: {
                                        upperBound2 = otherWildcard.bound;
                                        break Label_0702;
                                    }
                                    case 2: {
                                        lowerBound2 = otherWildcard.bound;
                                        break Label_0702;
                                    }
                                    case 0: {
                                        return false;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        if (lowerBound1 != null) {
            if (lowerBound2 != null) {
                return false;
            }
            if (upperBound2 != null) {
                return !lowerBound1.isTypeVariable() && !upperBound2.isTypeVariable() && !lowerBound1.isCompatibleWith(upperBound2);
            }
            return !lowerBound1.isTypeVariable() && !otherArgument.isTypeVariable() && !lowerBound1.isCompatibleWith(otherArgument);
        }
        else if (upperBound1 != null) {
            if (lowerBound2 != null) {
                return !lowerBound2.isCompatibleWith(upperBound1);
            }
            if (upperBound2 != null) {
                return upperBound1.isProvableDistinctSubType(upperBound2) && upperBound2.isProvableDistinctSubType(upperBound1);
            }
            return otherArgument.isProvableDistinctSubType(upperBound1);
        }
        else {
            if (lowerBound2 != null) {
                return !lowerBound2.isTypeVariable() && !this.isTypeVariable() && !lowerBound2.isCompatibleWith(this);
            }
            return upperBound2 == null || this.isProvableDistinctSubType(upperBound2);
        }
    }
    
    public boolean isRepeatableAnnotationType() {
        return false;
    }
    
    public final boolean isRawType() {
        return this.kind() == 1028;
    }
    
    public boolean isReifiable() {
        final TypeBinding leafType = this.leafComponentType();
        if (!(leafType instanceof ReferenceBinding)) {
            return true;
        }
        ReferenceBinding current = (ReferenceBinding)leafType;
        do {
            switch (current.kind()) {
                case 516:
                case 2052:
                case 4100:
                case 8196: {
                    return false;
                }
                case 260: {
                    if (current.isBoundParameterizedType()) {
                        return false;
                    }
                    break;
                }
                case 1028: {
                    return true;
                }
            }
            if (current.isStatic()) {
                return true;
            }
            if (!current.isLocalType()) {
                continue;
            }
            final LocalTypeBinding localTypeBinding = (LocalTypeBinding)current.erasure();
            final MethodBinding enclosingMethod = localTypeBinding.enclosingMethod;
            if (enclosingMethod != null && enclosingMethod.isStatic()) {
                return true;
            }
        } while ((current = current.enclosingType()) != null);
        return true;
    }
    
    public boolean isStatic() {
        return false;
    }
    
    public boolean isThrowable() {
        return false;
    }
    
    public boolean isTypeArgumentContainedBy(TypeBinding otherType) {
        if (equalsEquals(this, otherType)) {
            return true;
        }
        switch (otherType.kind()) {
            case 4100: {
                if (!this.isParameterizedType() || !otherType.isCapture()) {
                    return false;
                }
                final CaptureBinding capture = (CaptureBinding)otherType;
                if (capture instanceof CaptureBinding18) {
                    final CaptureBinding18 cb18 = (CaptureBinding18)capture;
                    if (cb18.firstBound != null) {
                        if (cb18.lowerBound != null) {
                            return false;
                        }
                        TypeBinding[] otherBounds = null;
                        final int len = cb18.upperBounds.length;
                        if (len > 1) {
                            System.arraycopy(cb18.upperBounds, 1, otherBounds = new TypeBinding[len - 1], 0, len - 1);
                        }
                        otherType = capture.environment.createWildcard(null, 0, cb18.firstBound, otherBounds, 1);
                    }
                    else {
                        if (cb18.lowerBound == null) {
                            return false;
                        }
                        otherType = capture.environment.createWildcard(null, 0, cb18.lowerBound, null, 2);
                    }
                }
                else {
                    TypeBinding upperBound = null;
                    TypeBinding[] otherBounds = null;
                    final WildcardBinding wildcard = capture.wildcard;
                    switch (wildcard.boundKind) {
                        case 2: {
                            return false;
                        }
                        case 0: {
                            final TypeVariableBinding variable = wildcard.genericType.typeVariables()[wildcard.rank];
                            upperBound = variable.upperBound();
                            otherBounds = (TypeBinding[])((variable.boundsCount() > 1) ? variable.otherUpperBounds() : null);
                            break;
                        }
                        case 1: {
                            upperBound = wildcard.bound;
                            otherBounds = wildcard.otherBounds;
                            break;
                        }
                    }
                    if (upperBound.id == 1 && otherBounds == null) {
                        return false;
                    }
                    otherType = capture.environment.createWildcard(null, 0, upperBound, otherBounds, 1);
                }
                return this.isTypeArgumentContainedBy(otherType);
            }
            case 516:
            case 8196: {
                TypeBinding lowerBound = this;
                TypeBinding upperBound = this;
                switch (this.kind()) {
                    case 516:
                    case 8196: {
                        final WildcardBinding wildcard2 = (WildcardBinding)this;
                        switch (wildcard2.boundKind) {
                            case 1: {
                                if (wildcard2.otherBounds != null) {
                                    break;
                                }
                                upperBound = wildcard2.bound;
                                lowerBound = null;
                                break;
                            }
                            case 2: {
                                upperBound = wildcard2;
                                lowerBound = wildcard2.bound;
                                break;
                            }
                            case 0: {
                                upperBound = wildcard2;
                                lowerBound = null;
                                break;
                            }
                        }
                        break;
                    }
                    case 4100: {
                        if (!this.isCapture()) {
                            break;
                        }
                        final CaptureBinding capture2 = (CaptureBinding)this;
                        if (capture2.lowerBound != null) {
                            lowerBound = capture2.lowerBound;
                            break;
                        }
                        break;
                    }
                }
                final WildcardBinding otherWildcard = (WildcardBinding)otherType;
                if (otherWildcard.otherBounds != null) {
                    return false;
                }
                final TypeBinding otherBound = otherWildcard.bound;
                switch (otherWildcard.boundKind) {
                    case 1: {
                        if (otherBound instanceof IntersectionTypeBinding18) {
                            final TypeBinding[] intersectingTypes = ((IntersectionTypeBinding18)otherBound).intersectingTypes;
                            for (int i = 0, length = intersectingTypes.length; i < length; ++i) {
                                if (equalsEquals(intersectingTypes[i], this)) {
                                    return true;
                                }
                            }
                        }
                        if (equalsEquals(otherBound, this)) {
                            return true;
                        }
                        if (upperBound == null) {
                            return false;
                        }
                        TypeBinding match = upperBound.findSuperTypeOriginatingFrom(otherBound);
                        if (match != null && (match = match.leafComponentType()).isRawType()) {
                            return equalsEquals(match, otherBound.leafComponentType());
                        }
                        return upperBound.isCompatibleWith(otherBound);
                    }
                    case 2: {
                        if (otherBound instanceof IntersectionTypeBinding18) {
                            final TypeBinding[] intersectingTypes2 = ((IntersectionTypeBinding18)otherBound).intersectingTypes;
                            for (int j = 0, length2 = intersectingTypes2.length; j < length2; ++j) {
                                if (equalsEquals(intersectingTypes2[j], this)) {
                                    return true;
                                }
                            }
                        }
                        if (equalsEquals(otherBound, this)) {
                            return true;
                        }
                        if (lowerBound == null) {
                            return false;
                        }
                        TypeBinding match = otherBound.findSuperTypeOriginatingFrom(lowerBound);
                        if (match != null && (match = match.leafComponentType()).isRawType()) {
                            return equalsEquals(match, lowerBound.leafComponentType());
                        }
                        return otherBound.isCompatibleWith(lowerBound);
                    }
                    default: {
                        return true;
                    }
                }
                break;
            }
            case 260: {
                if (!this.isParameterizedType()) {
                    return false;
                }
                final ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)this;
                final ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding)otherType;
                if (notEquals(paramType.actualType(), otherParamType.actualType())) {
                    return false;
                }
                if (!paramType.isStatic()) {
                    final ReferenceBinding enclosing = this.enclosingType();
                    if (enclosing != null) {
                        final ReferenceBinding otherEnclosing = otherParamType.enclosingType();
                        if (otherEnclosing == null) {
                            return false;
                        }
                        if ((otherEnclosing.tagBits & 0x40000000L) == 0x0L) {
                            if (notEquals(enclosing, otherEnclosing)) {
                                return false;
                            }
                        }
                        else if (!enclosing.isEquivalentTo(otherParamType.enclosingType())) {
                            return false;
                        }
                    }
                }
                final int length = (paramType.arguments == null) ? 0 : paramType.arguments.length;
                final TypeBinding[] otherArguments = otherParamType.arguments;
                final int otherLength = (otherArguments == null) ? 0 : otherArguments.length;
                if (otherLength != length) {
                    return false;
                }
                for (int k = 0; k < length; ++k) {
                    final TypeBinding argument = paramType.arguments[k];
                    final TypeBinding otherArgument = otherArguments[k];
                    if (!equalsEquals(argument, otherArgument)) {
                        final int kind = argument.kind();
                        if (otherArgument.kind() != kind) {
                            return false;
                        }
                        Label_1140: {
                            switch (kind) {
                                case 260: {
                                    if (argument.isTypeArgumentContainedBy(otherArgument)) {
                                        continue;
                                    }
                                    break;
                                }
                                case 516:
                                case 8196: {
                                    final WildcardBinding wildcard3 = (WildcardBinding)argument;
                                    final WildcardBinding otherWildcard = (WildcardBinding)otherArgument;
                                    switch (wildcard3.boundKind) {
                                        case 1: {
                                            if (otherWildcard.boundKind == 0 && equalsEquals(wildcard3.bound, wildcard3.typeVariable().upperBound())) {
                                                continue;
                                            }
                                            break Label_1140;
                                        }
                                        case 2: {
                                            break Label_1140;
                                        }
                                        case 0: {
                                            if (otherWildcard.boundKind == 1 && equalsEquals(otherWildcard.bound, otherWildcard.typeVariable().upperBound())) {
                                                continue;
                                            }
                                            break Label_1140;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        return false;
                    }
                }
                return true;
            }
            default: {
                if (otherType.id == 1) {
                    switch (this.kind()) {
                        case 516: {
                            final WildcardBinding wildcard4 = (WildcardBinding)this;
                            if (wildcard4.boundKind == 2 && wildcard4.bound.id == 1) {
                                return true;
                            }
                            break;
                        }
                    }
                }
                return false;
            }
        }
    }
    
    public boolean isTypeVariable() {
        return false;
    }
    
    public boolean isUnboundWildcard() {
        return false;
    }
    
    public boolean isUncheckedException(final boolean includeSupertype) {
        return false;
    }
    
    public boolean isWildcard() {
        return false;
    }
    
    @Override
    public int kind() {
        return 4;
    }
    
    public TypeBinding leafComponentType() {
        return this;
    }
    
    public boolean needsUncheckedConversion(TypeBinding targetType) {
        if (equalsEquals(this, targetType)) {
            return false;
        }
        targetType = targetType.leafComponentType();
        if (!(targetType instanceof ReferenceBinding)) {
            return false;
        }
        final TypeBinding currentType = this.leafComponentType();
        final TypeBinding match = currentType.findSuperTypeOriginatingFrom(targetType);
        if (!(match instanceof ReferenceBinding)) {
            return false;
        }
        ReferenceBinding compatible = (ReferenceBinding)match;
        while (compatible.isRawType()) {
            if (targetType.isBoundParameterizedType()) {
                return true;
            }
            if (compatible.isStatic()) {
                break;
            }
            if ((compatible = compatible.enclosingType()) == null) {
                break;
            }
            if ((targetType = targetType.enclosingType()) == null) {
                break;
            }
        }
        return false;
    }
    
    public char[] nullAnnotatedReadableName(final CompilerOptions options, final boolean shortNames) {
        if (shortNames) {
            return this.shortReadableName();
        }
        return this.readableName();
    }
    
    public TypeBinding original() {
        switch (this.kind()) {
            case 68:
            case 260:
            case 1028: {
                return this.erasure().unannotated();
            }
            default: {
                return this.unannotated();
            }
        }
    }
    
    public TypeBinding unannotated() {
        return this;
    }
    
    public TypeBinding withoutToplevelNullAnnotation() {
        return this;
    }
    
    public final boolean hasTypeAnnotations() {
        return (this.tagBits & 0x200000L) != 0x0L;
    }
    
    public char[] qualifiedPackageName() {
        final PackageBinding packageBinding = this.getPackage();
        return (packageBinding == null || packageBinding.compoundName == CharOperation.NO_CHAR_CHAR) ? CharOperation.NO_CHAR : packageBinding.readableName();
    }
    
    public abstract char[] qualifiedSourceName();
    
    public final AnnotationBinding[] getTypeAnnotations() {
        return this.typeAnnotations;
    }
    
    public void setTypeAnnotations(final AnnotationBinding[] annotations, final boolean evalNullAnnotations) {
        this.tagBits |= 0x200000L;
        if (annotations == null || annotations.length == 0) {
            return;
        }
        this.typeAnnotations = annotations;
        if (evalNullAnnotations) {
            for (int i = 0, length = annotations.length; i < length; ++i) {
                final AnnotationBinding annotation = annotations[i];
                if (annotation != null) {
                    if (annotation.type.hasNullBit(64)) {
                        this.tagBits |= 0x80000000100000L;
                    }
                    else if (annotation.type.hasNullBit(32)) {
                        this.tagBits |= 0x100000000100000L;
                    }
                }
            }
        }
    }
    
    public char[] signableName() {
        return this.readableName();
    }
    
    public char[] signature() {
        return this.constantPoolName();
    }
    
    public abstract char[] sourceName();
    
    public void swapUnresolved(final UnresolvedReferenceBinding unresolvedType, final ReferenceBinding resolvedType, final LookupEnvironment environment) {
    }
    
    TypeBinding[] typeArguments() {
        return null;
    }
    
    public TypeVariableBinding[] typeVariables() {
        return Binding.NO_TYPE_VARIABLES;
    }
    
    public MethodBinding getSingleAbstractMethod(final Scope scope, final boolean replaceWildcards) {
        return null;
    }
    
    public ReferenceBinding[] getIntersectingTypes() {
        return null;
    }
    
    public static boolean equalsEquals(final TypeBinding that, final TypeBinding other) {
        return that == other || (that != null && other != null && (that.id != Integer.MAX_VALUE && that.id == other.id));
    }
    
    public static boolean notEquals(final TypeBinding that, final TypeBinding other) {
        return that != other && (that == null || other == null || that.id == Integer.MAX_VALUE || that.id != other.id);
    }
    
    public TypeBinding prototype() {
        return null;
    }
    
    public boolean isUnresolvedType() {
        return false;
    }
    
    public boolean mentionsAny(final TypeBinding[] parameters, final int idx) {
        for (int i = 0; i < parameters.length; ++i) {
            if (i != idx && equalsEquals(parameters[i], this)) {
                return true;
            }
        }
        return false;
    }
    
    void collectInferenceVariables(final Set<InferenceVariable> variables) {
    }
    
    public boolean hasTypeBit(final int bit) {
        return false;
    }
    
    public boolean sIsMoreSpecific(final TypeBinding s, final TypeBinding t, final Scope scope) {
        return s.isCompatibleWith(t, scope) && !s.needsUncheckedConversion(t);
    }
    
    public boolean isSubtypeOf(final TypeBinding right) {
        return this.isCompatibleWith(right);
    }
    
    public MethodBinding[] getMethods(final char[] selector) {
        return Binding.NO_METHODS;
    }
    
    public boolean canBeSeenBy(final Scope scope) {
        return true;
    }
    
    public ReferenceBinding superclass() {
        return null;
    }
    
    public ReferenceBinding[] superInterfaces() {
        return Binding.NO_SUPERINTERFACES;
    }
    
    public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
        return null;
    }
    
    public boolean enterRecursiveFunction() {
        return true;
    }
    
    public void exitRecursiveFunction() {
    }
    
    public boolean isFunctionalType() {
        return false;
    }
    
    public long updateTagBits() {
        return this.tagBits & 0x100000L;
    }
    
    public boolean isFreeTypeVariable() {
        return false;
    }
}
