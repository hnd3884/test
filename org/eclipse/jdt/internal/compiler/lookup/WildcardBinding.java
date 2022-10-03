package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Set;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;

public class WildcardBinding extends ReferenceBinding
{
    public ReferenceBinding genericType;
    public int rank;
    public TypeBinding bound;
    public TypeBinding[] otherBounds;
    char[] genericSignature;
    public int boundKind;
    ReferenceBinding superclass;
    ReferenceBinding[] superInterfaces;
    TypeVariableBinding typeVariable;
    LookupEnvironment environment;
    boolean inRecursiveFunction;
    
    public WildcardBinding(final ReferenceBinding genericType, final int rank, final TypeBinding bound, final TypeBinding[] otherBounds, final int boundKind, final LookupEnvironment environment) {
        this.inRecursiveFunction = false;
        this.rank = rank;
        this.boundKind = boundKind;
        this.modifiers = 1073741825;
        this.environment = environment;
        this.initialize(genericType, bound, otherBounds);
        if (genericType instanceof UnresolvedReferenceBinding) {
            ((UnresolvedReferenceBinding)genericType).addWrapper(this, environment);
        }
        if (bound instanceof UnresolvedReferenceBinding) {
            ((UnresolvedReferenceBinding)bound).addWrapper(this, environment);
        }
        this.tagBits |= 0x1000000L;
        this.typeBits = 134217728;
    }
    
    @Override
    TypeBinding bound() {
        return this.bound;
    }
    
    @Override
    int boundKind() {
        return this.boundKind;
    }
    
    public TypeBinding allBounds() {
        if (this.otherBounds == null || this.otherBounds.length == 0) {
            return this.bound;
        }
        final ReferenceBinding[] allBounds = new ReferenceBinding[this.otherBounds.length + 1];
        try {
            allBounds[0] = (ReferenceBinding)this.bound;
            System.arraycopy(this.otherBounds, 0, allBounds, 1, this.otherBounds.length);
        }
        catch (final ClassCastException ex) {
            return this.bound;
        }
        catch (final ArrayStoreException ex2) {
            return this.bound;
        }
        return this.environment.createIntersectionType18(allBounds);
    }
    
    @Override
    public void setTypeAnnotations(final AnnotationBinding[] annotations, final boolean evalNullAnnotations) {
        this.tagBits |= 0x200000L;
        if (annotations != null && annotations.length != 0) {
            this.typeAnnotations = annotations;
        }
        if (evalNullAnnotations) {
            this.evaluateNullAnnotations(null, null);
        }
    }
    
    public void evaluateNullAnnotations(final Scope scope, final Wildcard wildcard) {
        long nullTagBits = this.determineNullBitsFromDeclaration(scope, wildcard);
        if (nullTagBits == 0L) {
            final TypeVariableBinding typeVariable2 = this.typeVariable();
            if (typeVariable2 != null) {
                final long typeVariableNullTagBits = typeVariable2.tagBits & 0x180000000000000L;
                if (typeVariableNullTagBits != 0L) {
                    nullTagBits = typeVariableNullTagBits;
                }
            }
        }
        if (nullTagBits != 0L) {
            this.tagBits = ((this.tagBits & 0xFE7FFFFFFFFFFFFFL) | nullTagBits | 0x100000L);
        }
    }
    
    public long determineNullBitsFromDeclaration(final Scope scope, final Wildcard wildcard) {
        long nullTagBits = 0L;
        final AnnotationBinding[] annotations = this.typeAnnotations;
        if (annotations != null) {
            for (int i = 0, length = annotations.length; i < length; ++i) {
                final AnnotationBinding annotation = annotations[i];
                if (annotation != null) {
                    if (annotation.type.hasNullBit(64)) {
                        if ((nullTagBits & 0x100000000000000L) == 0x0L) {
                            nullTagBits |= 0x80000000000000L;
                        }
                        else if (wildcard != null) {
                            final Annotation annotation2 = wildcard.findAnnotation(36028797018963968L);
                            if (annotation2 != null) {
                                scope.problemReporter().contradictoryNullAnnotations(annotation2);
                            }
                        }
                    }
                    else if (annotation.type.hasNullBit(32)) {
                        if ((nullTagBits & 0x80000000000000L) == 0x0L) {
                            nullTagBits |= 0x100000000000000L;
                        }
                        else if (wildcard != null) {
                            final Annotation annotation2 = wildcard.findAnnotation(72057594037927936L);
                            if (annotation2 != null) {
                                scope.problemReporter().contradictoryNullAnnotations(annotation2);
                            }
                        }
                    }
                }
            }
        }
        if (this.bound != null && this.bound.isValidBinding()) {
            final long boundNullTagBits = this.bound.tagBits & 0x180000000000000L;
            if (boundNullTagBits != 0L) {
                if (this.boundKind == 2) {
                    if ((boundNullTagBits & 0x80000000000000L) != 0x0L) {
                        if (nullTagBits == 0L) {
                            nullTagBits = 36028797018963968L;
                        }
                        else if (wildcard != null && (nullTagBits & 0x100000000000000L) != 0x0L) {
                            final Annotation annotation3 = wildcard.bound.findAnnotation(boundNullTagBits);
                            if (annotation3 == null) {
                                final TypeBinding newBound = this.bound.withoutToplevelNullAnnotation();
                                this.bound = newBound;
                                wildcard.bound.resolvedType = newBound;
                            }
                            else {
                                scope.problemReporter().contradictoryNullAnnotationsOnBounds(annotation3, nullTagBits);
                            }
                        }
                    }
                }
                else {
                    if ((boundNullTagBits & 0x100000000000000L) != 0x0L) {
                        if (nullTagBits == 0L) {
                            nullTagBits = 72057594037927936L;
                        }
                        else if (wildcard != null && (nullTagBits & 0x80000000000000L) != 0x0L) {
                            final Annotation annotation3 = wildcard.bound.findAnnotation(boundNullTagBits);
                            if (annotation3 == null) {
                                final TypeBinding newBound = this.bound.withoutToplevelNullAnnotation();
                                this.bound = newBound;
                                wildcard.bound.resolvedType = newBound;
                            }
                            else {
                                scope.problemReporter().contradictoryNullAnnotationsOnBounds(annotation3, nullTagBits);
                            }
                        }
                    }
                    if (nullTagBits == 0L && this.otherBounds != null) {
                        for (int j = 0, length2 = this.otherBounds.length; j < length2; ++j) {
                            if ((this.otherBounds[j].tagBits & 0x100000000000000L) != 0x0L) {
                                nullTagBits = 72057594037927936L;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return nullTagBits;
    }
    
    @Override
    public ReferenceBinding actualType() {
        return this.genericType;
    }
    
    @Override
    TypeBinding[] additionalBounds() {
        return this.otherBounds;
    }
    
    @Override
    public int kind() {
        return (this.otherBounds == null) ? 516 : 8196;
    }
    
    public boolean boundCheck(final TypeBinding argumentType) {
        switch (this.boundKind) {
            case 0: {
                return true;
            }
            case 1: {
                if (!argumentType.isCompatibleWith(this.bound)) {
                    return false;
                }
                for (int i = 0, length = (this.otherBounds == null) ? 0 : this.otherBounds.length; i < length; ++i) {
                    if (!argumentType.isCompatibleWith(this.otherBounds[i])) {
                        return false;
                    }
                }
                return true;
            }
            default: {
                return argumentType.isCompatibleWith(this.bound);
            }
        }
    }
    
    @Override
    public boolean canBeInstantiated() {
        return false;
    }
    
    @Override
    public List<TypeBinding> collectMissingTypes(List<TypeBinding> missingTypes) {
        if ((this.tagBits & 0x80L) != 0x0L) {
            missingTypes = this.bound.collectMissingTypes(missingTypes);
        }
        return missingTypes;
    }
    
    @Override
    public void collectSubstitutes(final Scope scope, TypeBinding actualType, final InferenceContext inferenceContext, final int constraint) {
        if ((this.tagBits & 0x20000000L) == 0x0L) {
            return;
        }
        if (actualType == TypeBinding.NULL || actualType.kind() == 65540) {
            return;
        }
        if (actualType.isCapture()) {
            final CaptureBinding capture = (CaptureBinding)actualType;
            actualType = capture.wildcard;
        }
        Label_1257: {
            switch (constraint) {
                case 1: {
                    Label_0444: {
                        switch (this.boundKind) {
                            case 1: {
                                switch (actualType.kind()) {
                                    case 516: {
                                        final WildcardBinding actualWildcard = (WildcardBinding)actualType;
                                        switch (actualWildcard.boundKind) {
                                            case 0: {
                                                break Label_1257;
                                            }
                                            case 1: {
                                                this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 1);
                                                break Label_1257;
                                            }
                                            default: {
                                                break Label_1257;
                                            }
                                        }
                                        break;
                                    }
                                    case 8196: {
                                        final WildcardBinding actualIntersection = (WildcardBinding)actualType;
                                        this.bound.collectSubstitutes(scope, actualIntersection.bound, inferenceContext, 1);
                                        for (int i = 0, length = actualIntersection.otherBounds.length; i < length; ++i) {
                                            this.bound.collectSubstitutes(scope, actualIntersection.otherBounds[i], inferenceContext, 1);
                                        }
                                        break Label_1257;
                                    }
                                    default: {
                                        this.bound.collectSubstitutes(scope, actualType, inferenceContext, 1);
                                        break Label_1257;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                switch (actualType.kind()) {
                                    case 516: {
                                        final WildcardBinding actualWildcard = (WildcardBinding)actualType;
                                        switch (actualWildcard.boundKind) {
                                            case 2: {
                                                this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 2);
                                                for (int j = 0, length2 = (actualWildcard.otherBounds == null) ? 0 : actualWildcard.otherBounds.length; j < length2; ++j) {
                                                    this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[j], inferenceContext, 2);
                                                }
                                                break;
                                            }
                                        }
                                        break Label_1257;
                                    }
                                    case 8196: {
                                        break Label_1257;
                                    }
                                    default: {
                                        this.bound.collectSubstitutes(scope, actualType, inferenceContext, 2);
                                        break Label_0444;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 0: {
                    Label_0849: {
                        switch (this.boundKind) {
                            case 1: {
                                switch (actualType.kind()) {
                                    case 516: {
                                        final WildcardBinding actualWildcard = (WildcardBinding)actualType;
                                        switch (actualWildcard.boundKind) {
                                            case 0: {
                                                break Label_1257;
                                            }
                                            case 1: {
                                                this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 0);
                                                for (int j = 0, length2 = (actualWildcard.otherBounds == null) ? 0 : actualWildcard.otherBounds.length; j < length2; ++j) {
                                                    this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[j], inferenceContext, 0);
                                                }
                                                break Label_1257;
                                            }
                                            default: {
                                                break Label_1257;
                                            }
                                        }
                                        break;
                                    }
                                    case 8196: {
                                        final WildcardBinding actuaIntersection = (WildcardBinding)actualType;
                                        this.bound.collectSubstitutes(scope, actuaIntersection.bound, inferenceContext, 0);
                                        for (int i = 0, length = (actuaIntersection.otherBounds == null) ? 0 : actuaIntersection.otherBounds.length; i < length; ++i) {
                                            this.bound.collectSubstitutes(scope, actuaIntersection.otherBounds[i], inferenceContext, 0);
                                        }
                                        break Label_1257;
                                    }
                                    default: {
                                        break Label_1257;
                                    }
                                }
                                break;
                            }
                            case 2: {
                                switch (actualType.kind()) {
                                    case 516: {
                                        final WildcardBinding actualWildcard = (WildcardBinding)actualType;
                                        switch (actualWildcard.boundKind) {
                                            case 2: {
                                                this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 0);
                                                for (int j = 0, length2 = (actualWildcard.otherBounds == null) ? 0 : actualWildcard.otherBounds.length; j < length2; ++j) {
                                                    this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[j], inferenceContext, 0);
                                                }
                                                break;
                                            }
                                        }
                                        break Label_1257;
                                    }
                                    case 8196: {
                                        break Label_1257;
                                    }
                                    default: {
                                        break Label_0849;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    switch (this.boundKind) {
                        case 0: {
                            break Label_1257;
                        }
                        case 1: {
                            switch (actualType.kind()) {
                                case 516: {
                                    final WildcardBinding actualWildcard = (WildcardBinding)actualType;
                                    switch (actualWildcard.boundKind) {
                                        case 0: {
                                            break Label_1257;
                                        }
                                        case 1: {
                                            this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 2);
                                            for (int j = 0, length2 = (actualWildcard.otherBounds == null) ? 0 : actualWildcard.otherBounds.length; j < length2; ++j) {
                                                this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[j], inferenceContext, 2);
                                            }
                                            break Label_1257;
                                        }
                                        default: {
                                            break Label_1257;
                                        }
                                    }
                                    break;
                                }
                                case 8196: {
                                    final WildcardBinding actualIntersection = (WildcardBinding)actualType;
                                    this.bound.collectSubstitutes(scope, actualIntersection.bound, inferenceContext, 2);
                                    for (int i = 0, length = (actualIntersection.otherBounds == null) ? 0 : actualIntersection.otherBounds.length; i < length; ++i) {
                                        this.bound.collectSubstitutes(scope, actualIntersection.otherBounds[i], inferenceContext, 2);
                                    }
                                    break Label_1257;
                                }
                                default: {
                                    break Label_1257;
                                }
                            }
                            break;
                        }
                        case 2: {
                            switch (actualType.kind()) {
                                case 516: {
                                    final WildcardBinding actualWildcard = (WildcardBinding)actualType;
                                    switch (actualWildcard.boundKind) {
                                        case 2: {
                                            this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, 2);
                                            for (int j = 0, length2 = (actualWildcard.otherBounds == null) ? 0 : actualWildcard.otherBounds.length; j < length2; ++j) {
                                                this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[j], inferenceContext, 2);
                                            }
                                            break;
                                        }
                                    }
                                }
                                case 8196: {
                                    break Label_1257;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        final char[] genericTypeKey = this.genericType.computeUniqueKey(false);
        final char[] rankComponent = (String.valueOf('{') + String.valueOf(this.rank) + '}').toCharArray();
        char[] wildCardKey = null;
        switch (this.boundKind) {
            case 0: {
                wildCardKey = TypeConstants.WILDCARD_STAR;
                break;
            }
            case 1: {
                wildCardKey = CharOperation.concat(TypeConstants.WILDCARD_PLUS, this.bound.computeUniqueKey(false));
                break;
            }
            default: {
                wildCardKey = CharOperation.concat(TypeConstants.WILDCARD_MINUS, this.bound.computeUniqueKey(false));
                break;
            }
        }
        return CharOperation.concat(genericTypeKey, rankComponent, wildCardKey);
    }
    
    @Override
    public char[] constantPoolName() {
        return this.erasure().constantPoolName();
    }
    
    @Override
    public TypeBinding clone(final TypeBinding immaterial) {
        return new WildcardBinding(this.genericType, this.rank, this.bound, this.otherBounds, this.boundKind, this.environment);
    }
    
    @Override
    public String annotatedDebugName() {
        final StringBuffer buffer = new StringBuffer(16);
        final AnnotationBinding[] annotations = this.getTypeAnnotations();
        for (int i = 0, length = (annotations == null) ? 0 : annotations.length; i < length; ++i) {
            buffer.append(annotations[i]);
            buffer.append(' ');
        }
        switch (this.boundKind) {
            case 0: {
                return buffer.append(TypeConstants.WILDCARD_NAME).toString();
            }
            case 1: {
                if (this.otherBounds == null) {
                    return buffer.append(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.annotatedDebugName().toCharArray())).toString();
                }
                buffer.append(this.bound.annotatedDebugName());
                for (int i = 0, length = this.otherBounds.length; i < length; ++i) {
                    buffer.append(" & ").append(this.otherBounds[i].annotatedDebugName());
                }
                return buffer.toString();
            }
            default: {
                return buffer.append(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.annotatedDebugName().toCharArray())).toString();
            }
        }
    }
    
    @Override
    public String debugName() {
        return this.toString();
    }
    
    @Override
    public TypeBinding erasure() {
        if (this.otherBounds != null) {
            return (this.bound.id == 1) ? this.otherBounds[0].erasure() : this.bound.erasure();
        }
        if (this.boundKind == 1) {
            return this.bound.erasure();
        }
        final TypeVariableBinding var = this.typeVariable();
        if (var != null) {
            return var.erasure();
        }
        return this.genericType;
    }
    
    @Override
    public char[] genericTypeSignature() {
        if (this.genericSignature == null) {
            switch (this.boundKind) {
                case 0: {
                    this.genericSignature = TypeConstants.WILDCARD_STAR;
                    break;
                }
                case 1: {
                    this.genericSignature = CharOperation.concat(TypeConstants.WILDCARD_PLUS, this.bound.genericTypeSignature());
                    break;
                }
                default: {
                    this.genericSignature = CharOperation.concat(TypeConstants.WILDCARD_MINUS, this.bound.genericTypeSignature());
                    break;
                }
            }
        }
        return this.genericSignature;
    }
    
    @Override
    public int hashCode() {
        return this.genericType.hashCode();
    }
    
    @Override
    public boolean hasTypeBit(final int bit) {
        if (this.typeBits == 134217728) {
            this.typeBits = 0;
            if (this.superclass != null && this.superclass.hasTypeBit(-134217729)) {
                this.typeBits |= (this.superclass.typeBits & 0x13);
            }
            if (this.superInterfaces != null) {
                for (int i = 0, l = this.superInterfaces.length; i < l; ++i) {
                    if (this.superInterfaces[i].hasTypeBit(-134217729)) {
                        this.typeBits |= (this.superInterfaces[i].typeBits & 0x13);
                    }
                }
            }
        }
        return (this.typeBits & bit) != 0x0;
    }
    
    void initialize(final ReferenceBinding someGenericType, final TypeBinding someBound, final TypeBinding[] someOtherBounds) {
        this.genericType = someGenericType;
        this.bound = someBound;
        this.otherBounds = someOtherBounds;
        if (someGenericType != null) {
            this.fPackage = someGenericType.getPackage();
        }
        if (someBound != null) {
            this.tagBits |= (someBound.tagBits & 0x2000000020100880L);
        }
        if (someOtherBounds != null) {
            for (int i = 0, max = someOtherBounds.length; i < max; ++i) {
                final TypeBinding someOtherBound = someOtherBounds[i];
                this.tagBits |= (someOtherBound.tagBits & 0x2000000000100800L);
            }
        }
    }
    
    @Override
    public boolean isSuperclassOf(final ReferenceBinding otherType) {
        if (this.boundKind != 2) {
            return false;
        }
        if (this.bound instanceof ReferenceBinding) {
            return ((ReferenceBinding)this.bound).isSuperclassOf(otherType);
        }
        return otherType.id == 1;
    }
    
    @Override
    public boolean isIntersectionType() {
        return this.otherBounds != null;
    }
    
    @Override
    public ReferenceBinding[] getIntersectingTypes() {
        if (this.isIntersectionType()) {
            final ReferenceBinding[] allBounds = new ReferenceBinding[this.otherBounds.length + 1];
            try {
                allBounds[0] = (ReferenceBinding)this.bound;
                System.arraycopy(this.otherBounds, 0, allBounds, 1, this.otherBounds.length);
            }
            catch (final ClassCastException ex) {
                return null;
            }
            catch (final ArrayStoreException ex2) {
                return null;
            }
            return allBounds;
        }
        return null;
    }
    
    @Override
    public boolean isHierarchyConnected() {
        return this.superclass != null && this.superInterfaces != null;
    }
    
    @Override
    public boolean enterRecursiveFunction() {
        return !this.inRecursiveFunction && (this.inRecursiveFunction = true);
    }
    
    @Override
    public void exitRecursiveFunction() {
        this.inRecursiveFunction = false;
    }
    
    @Override
    public boolean isProperType(final boolean admitCapture18) {
        if (this.inRecursiveFunction) {
            return true;
        }
        this.inRecursiveFunction = true;
        try {
            if (this.bound != null && !this.bound.isProperType(admitCapture18)) {
                return false;
            }
            if (this.superclass != null && !this.superclass.isProperType(admitCapture18)) {
                return false;
            }
            if (this.superInterfaces != null) {
                for (int i = 0, l = this.superInterfaces.length; i < l; ++i) {
                    if (!this.superInterfaces[i].isProperType(admitCapture18)) {
                        return false;
                    }
                }
            }
            return true;
        }
        finally {
            this.inRecursiveFunction = false;
        }
    }
    
    @Override
    TypeBinding substituteInferenceVariable(final InferenceVariable var, final TypeBinding substituteType) {
        boolean haveSubstitution = false;
        TypeBinding currentBound = this.bound;
        if (currentBound != null) {
            currentBound = currentBound.substituteInferenceVariable(var, substituteType);
            haveSubstitution |= TypeBinding.notEquals(currentBound, this.bound);
        }
        TypeBinding[] currentOtherBounds = null;
        if (this.otherBounds != null) {
            final int length = this.otherBounds.length;
            if (haveSubstitution) {
                System.arraycopy(this.otherBounds, 0, currentOtherBounds = new ReferenceBinding[length], 0, length);
            }
            for (int i = 0; i < length; ++i) {
                TypeBinding currentOtherBound = this.otherBounds[i];
                if (currentOtherBound != null) {
                    currentOtherBound = currentOtherBound.substituteInferenceVariable(var, substituteType);
                    if (TypeBinding.notEquals(currentOtherBound, this.otherBounds[i])) {
                        if (currentOtherBounds == null) {
                            System.arraycopy(this.otherBounds, 0, currentOtherBounds = new ReferenceBinding[length], 0, length);
                        }
                        currentOtherBounds[i] = currentOtherBound;
                    }
                }
            }
        }
        haveSubstitution |= (currentOtherBounds != null);
        if (haveSubstitution) {
            return this.environment.createWildcard(this.genericType, this.rank, currentBound, currentOtherBounds, this.boundKind);
        }
        return this;
    }
    
    @Override
    public boolean isUnboundWildcard() {
        return this.boundKind == 0;
    }
    
    @Override
    public boolean isWildcard() {
        return true;
    }
    
    @Override
    int rank() {
        return this.rank;
    }
    
    @Override
    public char[] readableName() {
        switch (this.boundKind) {
            case 0: {
                return TypeConstants.WILDCARD_NAME;
            }
            case 1: {
                if (this.otherBounds == null) {
                    return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.readableName());
                }
                final StringBuffer buffer = new StringBuffer(10);
                buffer.append(this.bound.readableName());
                for (int i = 0, length = this.otherBounds.length; i < length; ++i) {
                    buffer.append('&').append(this.otherBounds[i].readableName());
                }
                final int length2;
                final char[] result = new char[length2 = buffer.length()];
                buffer.getChars(0, length2, result, 0);
                return result;
            }
            default: {
                return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.readableName());
            }
        }
    }
    
    @Override
    public char[] nullAnnotatedReadableName(final CompilerOptions options, final boolean shortNames) {
        final StringBuffer buffer = new StringBuffer(10);
        this.appendNullAnnotation(buffer, options);
        switch (this.boundKind) {
            case 0: {
                buffer.append(TypeConstants.WILDCARD_NAME);
                break;
            }
            case 1: {
                if (this.otherBounds == null) {
                    buffer.append(TypeConstants.WILDCARD_NAME).append(TypeConstants.WILDCARD_EXTENDS);
                    buffer.append(this.bound.nullAnnotatedReadableName(options, shortNames));
                    break;
                }
                buffer.append(this.bound.nullAnnotatedReadableName(options, shortNames));
                for (int i = 0, length = this.otherBounds.length; i < length; ++i) {
                    buffer.append('&').append(this.otherBounds[i].nullAnnotatedReadableName(options, shortNames));
                }
                break;
            }
            default: {
                buffer.append(TypeConstants.WILDCARD_NAME).append(TypeConstants.WILDCARD_SUPER).append(this.bound.nullAnnotatedReadableName(options, shortNames));
                break;
            }
        }
        final int length2;
        final char[] result = new char[length2 = buffer.length()];
        buffer.getChars(0, length2, result, 0);
        return result;
    }
    
    ReferenceBinding resolve() {
        if ((this.tagBits & 0x1000000L) == 0x0L) {
            return this;
        }
        this.tagBits &= 0xFFFFFFFFFEFFFFFFL;
        BinaryTypeBinding.resolveType(this.genericType, this.environment, false);
        switch (this.boundKind) {
            case 1: {
                TypeBinding resolveType = BinaryTypeBinding.resolveType(this.bound, this.environment, true);
                this.bound = resolveType;
                this.tagBits |= ((resolveType.tagBits & 0x800L) | 0x2000000000000000L);
                for (int i = 0, length = (this.otherBounds == null) ? 0 : this.otherBounds.length; i < length; ++i) {
                    resolveType = BinaryTypeBinding.resolveType(this.otherBounds[i], this.environment, true);
                    this.otherBounds[i] = resolveType;
                    this.tagBits |= ((resolveType.tagBits & 0x800L) | 0x2000000000000000L);
                }
                break;
            }
            case 2: {
                final TypeBinding resolveType = BinaryTypeBinding.resolveType(this.bound, this.environment, true);
                this.bound = resolveType;
                this.tagBits |= ((resolveType.tagBits & 0x800L) | 0x2000000000000000L);
                break;
            }
        }
        if (this.environment.usesNullTypeAnnotations()) {
            this.evaluateNullAnnotations(null, null);
        }
        return this;
    }
    
    @Override
    public char[] shortReadableName() {
        switch (this.boundKind) {
            case 0: {
                return TypeConstants.WILDCARD_NAME;
            }
            case 1: {
                if (this.otherBounds == null) {
                    return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.shortReadableName());
                }
                final StringBuffer buffer = new StringBuffer(10);
                buffer.append(this.bound.shortReadableName());
                for (int i = 0, length = this.otherBounds.length; i < length; ++i) {
                    buffer.append('&').append(this.otherBounds[i].shortReadableName());
                }
                final int length2;
                final char[] result = new char[length2 = buffer.length()];
                buffer.getChars(0, length2, result, 0);
                return result;
            }
            default: {
                return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.shortReadableName());
            }
        }
    }
    
    @Override
    public char[] signature() {
        if (this.signature != null) {
            return this.signature;
        }
        switch (this.boundKind) {
            case 1: {
                return this.bound.signature();
            }
            default: {
                return this.typeVariable().signature();
            }
        }
    }
    
    @Override
    public char[] sourceName() {
        switch (this.boundKind) {
            case 0: {
                return TypeConstants.WILDCARD_NAME;
            }
            case 1: {
                return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.sourceName());
            }
            default: {
                return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.sourceName());
            }
        }
    }
    
    @Override
    public ReferenceBinding superclass() {
        if (this.superclass == null) {
            TypeBinding superType = null;
            if (this.boundKind == 1 && !this.bound.isInterface()) {
                superType = this.bound;
            }
            else {
                final TypeVariableBinding variable = this.typeVariable();
                if (variable != null) {
                    superType = variable.firstBound;
                }
            }
            this.superclass = (ReferenceBinding)((superType instanceof ReferenceBinding && !superType.isInterface()) ? superType : this.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null));
        }
        return this.superclass;
    }
    
    @Override
    public ReferenceBinding[] superInterfaces() {
        if (this.superInterfaces == null) {
            if (this.typeVariable() != null) {
                this.superInterfaces = this.typeVariable.superInterfaces();
            }
            else {
                this.superInterfaces = Binding.NO_SUPERINTERFACES;
            }
            if (this.boundKind == 1) {
                if (this.bound.isInterface()) {
                    final int length = this.superInterfaces.length;
                    System.arraycopy(this.superInterfaces, 0, this.superInterfaces = new ReferenceBinding[length + 1], 1, length);
                    this.superInterfaces[0] = (ReferenceBinding)this.bound;
                }
                if (this.otherBounds != null) {
                    final int length = this.superInterfaces.length;
                    final int otherLength = this.otherBounds.length;
                    System.arraycopy(this.superInterfaces, 0, this.superInterfaces = new ReferenceBinding[length + otherLength], 0, length);
                    for (int i = 0; i < otherLength; ++i) {
                        this.superInterfaces[length + i] = (ReferenceBinding)this.otherBounds[i];
                    }
                }
            }
        }
        return this.superInterfaces;
    }
    
    @Override
    public void swapUnresolved(final UnresolvedReferenceBinding unresolvedType, final ReferenceBinding resolvedType, final LookupEnvironment env) {
        boolean affected = false;
        if (this.genericType == unresolvedType) {
            this.genericType = resolvedType;
            affected = true;
        }
        if (this.bound == unresolvedType) {
            this.bound = env.convertUnresolvedBinaryToRawType(resolvedType);
            affected = true;
        }
        if (this.otherBounds != null) {
            for (int i = 0, length = this.otherBounds.length; i < length; ++i) {
                if (this.otherBounds[i] == unresolvedType) {
                    this.otherBounds[i] = env.convertUnresolvedBinaryToRawType(resolvedType);
                    affected = true;
                }
            }
        }
        if (affected) {
            this.initialize(this.genericType, this.bound, this.otherBounds);
        }
    }
    
    @Override
    public String toString() {
        if (this.hasTypeAnnotations()) {
            return this.annotatedDebugName();
        }
        switch (this.boundKind) {
            case 0: {
                return new String(TypeConstants.WILDCARD_NAME);
            }
            case 1: {
                if (this.otherBounds == null) {
                    return new String(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.debugName().toCharArray()));
                }
                final StringBuffer buffer = new StringBuffer(this.bound.debugName());
                for (int i = 0, length = this.otherBounds.length; i < length; ++i) {
                    buffer.append('&').append(this.otherBounds[i].debugName());
                }
                return buffer.toString();
            }
            default: {
                return new String(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.debugName().toCharArray()));
            }
        }
    }
    
    public TypeVariableBinding typeVariable() {
        if (this.typeVariable == null) {
            final TypeVariableBinding[] typeVariables = this.genericType.typeVariables();
            if (this.rank < typeVariables.length) {
                this.typeVariable = typeVariables[this.rank];
            }
        }
        return this.typeVariable;
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
        final AnnotationBinding[] newAnnotations = this.environment.filterNullTypeAnnotations(this.getTypeAnnotations());
        return this.environment.createWildcard(this.genericType, this.rank, this.bound, this.otherBounds, this.boundKind, newAnnotations);
    }
    
    @Override
    public TypeBinding uncapture(final Scope scope) {
        if ((this.tagBits & 0x2000000000000000L) == 0x0L) {
            return this;
        }
        final TypeBinding freeBound = (this.bound != null) ? this.bound.uncapture(scope) : null;
        int length = 0;
        final TypeBinding[] freeOtherBounds = (TypeBinding[])((this.otherBounds == null) ? null : new TypeBinding[length = this.otherBounds.length]);
        for (int i = 0; i < length; ++i) {
            freeOtherBounds[i] = ((this.otherBounds[i] == null) ? null : this.otherBounds[i].uncapture(scope));
        }
        return scope.environment().createWildcard(this.genericType, this.rank, freeBound, freeOtherBounds, this.boundKind, this.getTypeAnnotations());
    }
    
    @Override
    void collectInferenceVariables(final Set<InferenceVariable> variables) {
        if (this.bound != null) {
            this.bound.collectInferenceVariables(variables);
        }
        if (this.otherBounds != null) {
            for (int i = 0, length = this.otherBounds.length; i < length; ++i) {
                this.otherBounds[i].collectInferenceVariables(variables);
            }
        }
    }
    
    @Override
    public boolean mentionsAny(final TypeBinding[] parameters, final int idx) {
        if (this.inRecursiveFunction) {
            return false;
        }
        this.inRecursiveFunction = true;
        try {
            if (super.mentionsAny(parameters, idx)) {
                return true;
            }
            if (this.bound != null && this.bound.mentionsAny(parameters, -1)) {
                return true;
            }
            if (this.otherBounds != null) {
                for (int i = 0, length = this.otherBounds.length; i < length; ++i) {
                    if (this.otherBounds[i].mentionsAny(parameters, -1)) {
                        return true;
                    }
                }
            }
        }
        finally {
            this.inRecursiveFunction = false;
        }
        return this.inRecursiveFunction = false;
    }
    
    @Override
    public boolean acceptsNonNullDefault() {
        return false;
    }
    
    @Override
    public long updateTagBits() {
        if (!this.inRecursiveFunction) {
            this.inRecursiveFunction = true;
            try {
                if (this.bound != null) {
                    this.tagBits |= this.bound.updateTagBits();
                }
                if (this.otherBounds != null) {
                    for (int i = 0, length = this.otherBounds.length; i < length; ++i) {
                        this.tagBits |= this.otherBounds[i].updateTagBits();
                    }
                }
            }
            finally {
                this.inRecursiveFunction = false;
            }
            this.inRecursiveFunction = false;
        }
        return super.updateTagBits();
    }
}
