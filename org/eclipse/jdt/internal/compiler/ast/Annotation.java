package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.impl.IrritantSet;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.env.EnumConstantSignature;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import java.util.Stack;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;

public abstract class Annotation extends Expression
{
    Annotation persistibleAnnotation;
    static final MemberValuePair[] NoValuePairs;
    static final int[] TYPE_PATH_ELEMENT_ARRAY;
    static final int[] TYPE_PATH_INNER_TYPE;
    static final int[] TYPE_PATH_ANNOTATION_ON_WILDCARD_BOUND;
    public int declarationSourceEnd;
    public Binding recipient;
    public TypeReference type;
    protected AnnotationBinding compilerAnnotation;
    
    static {
        NoValuePairs = new MemberValuePair[0];
        TYPE_PATH_ELEMENT_ARRAY = new int[2];
        TYPE_PATH_INNER_TYPE = new int[] { 1, 0 };
        TYPE_PATH_ANNOTATION_ON_WILDCARD_BOUND = new int[] { 2, 0 };
    }
    
    public Annotation() {
        this.persistibleAnnotation = this;
        this.compilerAnnotation = null;
    }
    
    public static int[] getLocations(final Expression reference, final Annotation annotation) {
        if (reference == null) {
            return null;
        }
        class LocationCollector extends ASTVisitor
        {
            Stack typePathEntries;
            Annotation searchedAnnotation = annotation;
            boolean continueSearch;
            
            public LocationCollector(final Annotation currentAnnotation) {
                this.continueSearch = true;
                this.typePathEntries = new Stack();
            }
            
            private int[] computeNestingDepth(final TypeReference typeReference) {
                TypeBinding type = (typeReference.resolvedType == null) ? null : typeReference.resolvedType.leafComponentType();
                final int[] nestingDepths = new int[typeReference.getAnnotatableLevels()];
                if (type != null && type.isNestedType()) {
                    int depth = 0;
                    for (TypeBinding currentType = type; currentType != null; currentType = currentType.enclosingType()) {
                        depth += (currentType.isStatic() ? 0 : 1);
                    }
                    for (int counter = nestingDepths.length - 1; type != null && counter >= 0; nestingDepths[counter--] = depth, depth -= (type.isStatic() ? 0 : 1), type = type.enclosingType()) {}
                }
                return nestingDepths;
            }
            
            private void inspectAnnotations(final Annotation[] annotations) {
                for (int i = 0, length = (annotations == null) ? 0 : annotations.length; this.continueSearch && i < length; ++i) {
                    if (annotations[i] == this.searchedAnnotation) {
                        this.continueSearch = false;
                    }
                }
            }
            
            private void inspectArrayDimensions(final Annotation[][] annotationsOnDimensions, final int dimensions) {
                for (int i = 0; this.continueSearch && i < dimensions; ++i) {
                    final Annotation[] annotations = (Annotation[])((annotationsOnDimensions == null) ? null : annotationsOnDimensions[i]);
                    this.inspectAnnotations(annotations);
                    if (!this.continueSearch) {
                        return;
                    }
                    this.typePathEntries.push(Annotation.TYPE_PATH_ELEMENT_ARRAY);
                }
            }
            
            private void inspectTypeArguments(final TypeReference[] typeReferences) {
                for (int i = 0, length = (typeReferences == null) ? 0 : typeReferences.length; this.continueSearch && i < length; ++i) {
                    final int size = this.typePathEntries.size();
                    this.typePathEntries.add(new int[] { 3, i });
                    typeReferences[i].traverse(this, (BlockScope)null);
                    if (!this.continueSearch) {
                        return;
                    }
                    this.typePathEntries.setSize(size);
                }
            }
            
            public boolean visit(final TypeReference typeReference, final BlockScope scope) {
                if (this.continueSearch) {
                    this.inspectArrayDimensions(typeReference.getAnnotationsOnDimensions(), typeReference.dimensions());
                    if (this.continueSearch) {
                        final int[] nestingDepths = this.computeNestingDepth(typeReference);
                        final Annotation[][] annotations = typeReference.annotations;
                        final TypeReference[][] typeArguments = typeReference.getTypeArguments();
                        final int levels = typeReference.getAnnotatableLevels();
                        final int size = this.typePathEntries.size();
                        for (int i = levels - 1; this.continueSearch && i >= 0; --i) {
                            this.typePathEntries.setSize(size);
                            for (int j = 0, depth = nestingDepths[i]; j < depth; ++j) {
                                this.typePathEntries.add(Annotation.TYPE_PATH_INNER_TYPE);
                            }
                            if (annotations != null) {
                                this.inspectAnnotations(annotations[i]);
                            }
                            if (this.continueSearch && typeArguments != null) {
                                this.inspectTypeArguments(typeArguments[i]);
                            }
                        }
                    }
                }
                return false;
            }
            
            @Override
            public boolean visit(final SingleTypeReference typeReference, final BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }
            
            @Override
            public boolean visit(final ArrayTypeReference typeReference, final BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }
            
            @Override
            public boolean visit(final ParameterizedSingleTypeReference typeReference, final BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }
            
            @Override
            public boolean visit(final QualifiedTypeReference typeReference, final BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }
            
            @Override
            public boolean visit(final ArrayQualifiedTypeReference typeReference, final BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }
            
            @Override
            public boolean visit(final ParameterizedQualifiedTypeReference typeReference, final BlockScope scope) {
                return this.visit((TypeReference)typeReference, scope);
            }
            
            @Override
            public boolean visit(final Wildcard typeReference, final BlockScope scope) {
                this.visit((TypeReference)typeReference, scope);
                if (this.continueSearch) {
                    final TypeReference bound = typeReference.bound;
                    if (bound != null) {
                        final int size = this.typePathEntries.size();
                        this.typePathEntries.push(Annotation.TYPE_PATH_ANNOTATION_ON_WILDCARD_BOUND);
                        bound.traverse(this, scope);
                        if (this.continueSearch) {
                            this.typePathEntries.setSize(size);
                        }
                    }
                }
                return false;
            }
            
            @Override
            public boolean visit(final ArrayAllocationExpression allocationExpression, final BlockScope scope) {
                if (this.continueSearch) {
                    this.inspectArrayDimensions(allocationExpression.getAnnotationsOnDimensions(), allocationExpression.dimensions.length);
                    if (this.continueSearch) {
                        allocationExpression.type.traverse(this, scope);
                    }
                    if (this.continueSearch) {
                        throw new IllegalStateException();
                    }
                }
                return false;
            }
            
            @Override
            public String toString() {
                final StringBuffer buffer = new StringBuffer();
                buffer.append("search location for ").append(this.searchedAnnotation).append("\ncurrent type_path entries : ");
                for (int i = 0, maxi = this.typePathEntries.size(); i < maxi; ++i) {
                    final int[] typePathEntry = (int[])this.typePathEntries.get(i);
                    buffer.append('(').append(typePathEntry[0]).append(',').append(typePathEntry[1]).append(')');
                }
                return String.valueOf(buffer);
            }
        }
        final LocationCollector collector = new LocationCollector();
        reference.traverse(collector, (BlockScope)null);
        if (collector.typePathEntries.isEmpty()) {
            return null;
        }
        final int size = collector.typePathEntries.size();
        final int[] result = new int[size * 2];
        int offset = 0;
        for (int i = 0; i < size; ++i) {
            final int[] pathElement = (int[])collector.typePathEntries.get(i);
            result[offset++] = pathElement[0];
            result[offset++] = pathElement[1];
        }
        return result;
    }
    
    public static long getRetentionPolicy(final char[] policyName) {
        if (policyName == null || policyName.length == 0) {
            return 0L;
        }
        switch (policyName[0]) {
            case 'C': {
                if (CharOperation.equals(policyName, TypeConstants.UPPER_CLASS)) {
                    return 35184372088832L;
                }
                break;
            }
            case 'S': {
                if (CharOperation.equals(policyName, TypeConstants.UPPER_SOURCE)) {
                    return 17592186044416L;
                }
                break;
            }
            case 'R': {
                if (CharOperation.equals(policyName, TypeConstants.UPPER_RUNTIME)) {
                    return 52776558133248L;
                }
                break;
            }
        }
        return 0L;
    }
    
    public static long getTargetElementType(final char[] elementName) {
        if (elementName == null || elementName.length == 0) {
            return 0L;
        }
        switch (elementName[0]) {
            case 'A': {
                if (CharOperation.equals(elementName, TypeConstants.UPPER_ANNOTATION_TYPE)) {
                    return 4398046511104L;
                }
                break;
            }
            case 'C': {
                if (CharOperation.equals(elementName, TypeConstants.UPPER_CONSTRUCTOR)) {
                    return 1099511627776L;
                }
                break;
            }
            case 'F': {
                if (CharOperation.equals(elementName, TypeConstants.UPPER_FIELD)) {
                    return 137438953472L;
                }
                break;
            }
            case 'L': {
                if (CharOperation.equals(elementName, TypeConstants.UPPER_LOCAL_VARIABLE)) {
                    return 2199023255552L;
                }
                break;
            }
            case 'M': {
                if (CharOperation.equals(elementName, TypeConstants.UPPER_METHOD)) {
                    return 274877906944L;
                }
                break;
            }
            case 'P': {
                if (CharOperation.equals(elementName, TypeConstants.UPPER_PARAMETER)) {
                    return 549755813888L;
                }
                if (CharOperation.equals(elementName, TypeConstants.UPPER_PACKAGE)) {
                    return 8796093022208L;
                }
                break;
            }
            case 'T': {
                if (CharOperation.equals(elementName, TypeConstants.TYPE)) {
                    return 68719476736L;
                }
                if (CharOperation.equals(elementName, TypeConstants.TYPE_USE_TARGET)) {
                    return 9007199254740992L;
                }
                if (CharOperation.equals(elementName, TypeConstants.TYPE_PARAMETER_TARGET)) {
                    return 18014398509481984L;
                }
                break;
            }
        }
        return 0L;
    }
    
    public ElementValuePair[] computeElementValuePairs() {
        return Binding.NO_ELEMENT_VALUE_PAIRS;
    }
    
    private long detectStandardAnnotation(final Scope scope, final ReferenceBinding annotationType, final MemberValuePair valueAttribute) {
        long tagBits = 0L;
        switch (annotationType.id) {
            case 48: {
                if (valueAttribute == null) {
                    break;
                }
                final Expression expr = valueAttribute.value;
                if ((expr.bits & 0x3) != 0x1 || !(expr instanceof Reference)) {
                    break;
                }
                final FieldBinding field = ((Reference)expr).fieldBinding();
                if (field != null && field.declaringClass.id == 51) {
                    tagBits |= getRetentionPolicy(field.name);
                    break;
                }
                break;
            }
            case 50: {
                tagBits |= 0x800000000L;
                if (valueAttribute == null) {
                    break;
                }
                final Expression expr = valueAttribute.value;
                if (expr instanceof ArrayInitializer) {
                    final ArrayInitializer initializer = (ArrayInitializer)expr;
                    final Expression[] expressions = initializer.expressions;
                    if (expressions != null) {
                        for (int i = 0, length = expressions.length; i < length; ++i) {
                            final Expression initExpr = expressions[i];
                            if ((initExpr.bits & 0x3) == 0x1) {
                                final FieldBinding field2 = ((Reference)initExpr).fieldBinding();
                                if (field2 != null && field2.declaringClass.id == 52) {
                                    final long element = getTargetElementType(field2.name);
                                    if ((tagBits & element) != 0x0L) {
                                        scope.problemReporter().duplicateTargetInTargetAnnotation(annotationType, (NameReference)initExpr);
                                    }
                                    else {
                                        tagBits |= element;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    break;
                }
                else {
                    if ((expr.bits & 0x3) != 0x1) {
                        break;
                    }
                    final FieldBinding field = ((Reference)expr).fieldBinding();
                    if (field != null && field.declaringClass.id == 52) {
                        tagBits |= getTargetElementType(field.name);
                        break;
                    }
                    break;
                }
                break;
            }
            case 44: {
                tagBits |= 0x400000000000L;
                break;
            }
            case 45: {
                tagBits |= 0x800000000000L;
                break;
            }
            case 46: {
                tagBits |= 0x1000000000000L;
                break;
            }
            case 47: {
                tagBits |= 0x2000000000000L;
                break;
            }
            case 77: {
                tagBits |= 0x800000000000000L;
                break;
            }
            case 90: {
                tagBits |= 0x1000000000000000L;
                break;
            }
            case 49: {
                tagBits |= 0x4000000000000L;
                break;
            }
            case 60: {
                tagBits |= 0x8000000000000L;
                break;
            }
            case 61: {
                tagBits |= 0x10000000000000L;
                break;
            }
        }
        if (annotationType.hasNullBit(64)) {
            tagBits |= 0x80000000000000L;
        }
        else if (annotationType.hasNullBit(32)) {
            tagBits |= 0x100000000000000L;
        }
        else if (annotationType.hasNullBit(128)) {
            Object value = null;
            if (valueAttribute != null) {
                if (valueAttribute.compilerElementPair != null) {
                    value = valueAttribute.compilerElementPair.value;
                }
            }
            else {
                final MethodBinding[] methods = annotationType.methods();
                if (methods != null && methods.length == 1) {
                    value = methods[0].getDefaultValue();
                }
                else {
                    tagBits |= 0x200000000000000L;
                }
            }
            if (value instanceof BooleanConstant) {
                tagBits |= (((BooleanConstant)value).booleanValue() ? 144115188075855872L : 288230376151711744L);
            }
            else if (value != null) {
                tagBits |= nullLocationBitsFromAnnotationValue(value);
            }
        }
        return tagBits;
    }
    
    public static int nullLocationBitsFromAnnotationValue(final Object value) {
        if (!(value instanceof Object[])) {
            return evaluateDefaultNullnessLocation(value);
        }
        if (((Object[])value).length == 0) {
            return 2;
        }
        int bits = 0;
        Object[] array;
        for (int length = (array = (Object[])value).length, i = 0; i < length; ++i) {
            final Object single = array[i];
            bits |= evaluateDefaultNullnessLocation(single);
        }
        return bits;
    }
    
    private static int evaluateDefaultNullnessLocation(final Object value) {
        char[] name = null;
        if (value instanceof FieldBinding) {
            name = ((FieldBinding)value).name;
        }
        else if (value instanceof EnumConstantSignature) {
            name = ((EnumConstantSignature)value).getEnumConstantName();
        }
        else if (value instanceof ElementValuePair.UnresolvedEnumConstant) {
            name = ((ElementValuePair.UnresolvedEnumConstant)value).getEnumConstantName();
        }
        else if (value instanceof BooleanConstant) {
            return ((BooleanConstant)value).booleanValue() ? 1 : 2;
        }
        if (name != null) {
            switch (name.length) {
                case 5: {
                    if (CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__FIELD)) {
                        return 32;
                    }
                    break;
                }
                case 9: {
                    if (CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__PARAMETER)) {
                        return 8;
                    }
                    break;
                }
                case 10: {
                    if (CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__TYPE_BOUND)) {
                        return 256;
                    }
                    break;
                }
                case 11: {
                    if (CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__RETURN_TYPE)) {
                        return 16;
                    }
                    break;
                }
                case 13: {
                    if (CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__TYPE_ARGUMENT)) {
                        return 64;
                    }
                    break;
                }
                case 14: {
                    if (CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__TYPE_PARAMETER)) {
                        return 128;
                    }
                    if (CharOperation.equals(name, TypeConstants.DEFAULT_LOCATION__ARRAY_CONTENTS)) {
                        return 512;
                    }
                    break;
                }
            }
        }
        return 0;
    }
    
    static String getRetentionName(final long tagBits) {
        if ((tagBits & 0x300000000000L) == 0x300000000000L) {
            return new String(Annotation.UPPER_RUNTIME);
        }
        if ((tagBits & 0x100000000000L) != 0x0L) {
            return new String(Annotation.UPPER_SOURCE);
        }
        return new String(TypeConstants.UPPER_CLASS);
    }
    
    private static long getAnnotationRetention(final ReferenceBinding binding) {
        final long retention = binding.getAnnotationTagBits() & 0x300000000000L;
        return (retention != 0L) ? retention : 35184372088832L;
    }
    
    public void checkRepeatableMetaAnnotation(final BlockScope scope) {
        final ReferenceBinding repeatableAnnotationType = (ReferenceBinding)this.recipient;
        final MemberValuePair[] valuePairs = this.memberValuePairs();
        if (valuePairs == null || valuePairs.length != 1) {
            return;
        }
        final Object value = valuePairs[0].compilerElementPair.value;
        if (!(value instanceof ReferenceBinding)) {
            return;
        }
        final ReferenceBinding containerAnnotationType = (ReferenceBinding)value;
        if (!containerAnnotationType.isAnnotationType()) {
            return;
        }
        repeatableAnnotationType.setContainerAnnotationType(containerAnnotationType);
        checkContainerAnnotationType(valuePairs[0], scope, containerAnnotationType, repeatableAnnotationType, false);
    }
    
    public static void checkContainerAnnotationType(final ASTNode culpritNode, final BlockScope scope, final ReferenceBinding containerAnnotationType, final ReferenceBinding repeatableAnnotationType, final boolean useSite) {
        final MethodBinding[] annotationMethods = containerAnnotationType.methods();
        boolean sawValue = false;
        for (int i = 0, length = annotationMethods.length; i < length; ++i) {
            final MethodBinding method = annotationMethods[i];
            if (CharOperation.equals(method.selector, TypeConstants.VALUE)) {
                sawValue = true;
                if (method.returnType.isArrayType() && method.returnType.dimensions() == 1) {
                    final ArrayBinding array = (ArrayBinding)method.returnType;
                    if (TypeBinding.equalsEquals(array.elementsType(), repeatableAnnotationType)) {
                        continue;
                    }
                }
                repeatableAnnotationType.tagAsHavingDefectiveContainerType();
                scope.problemReporter().containerAnnotationTypeHasWrongValueType(culpritNode, containerAnnotationType, repeatableAnnotationType, method.returnType);
            }
            else if ((method.modifiers & 0x20000) == 0x0) {
                repeatableAnnotationType.tagAsHavingDefectiveContainerType();
                scope.problemReporter().containerAnnotationTypeHasNonDefaultMembers(culpritNode, containerAnnotationType, method.selector);
            }
        }
        if (!sawValue) {
            repeatableAnnotationType.tagAsHavingDefectiveContainerType();
            scope.problemReporter().containerAnnotationTypeMustHaveValue(culpritNode, containerAnnotationType);
        }
        if (useSite) {
            checkContainingAnnotationTargetAtUse((Annotation)culpritNode, scope, containerAnnotationType, repeatableAnnotationType);
        }
        else {
            checkContainerAnnotationTypeTarget(culpritNode, scope, containerAnnotationType, repeatableAnnotationType);
        }
        final long annotationTypeBits = getAnnotationRetention(repeatableAnnotationType);
        final long containerTypeBits = getAnnotationRetention(containerAnnotationType);
        if (containerTypeBits < annotationTypeBits) {
            repeatableAnnotationType.tagAsHavingDefectiveContainerType();
            scope.problemReporter().containerAnnotationTypeHasShorterRetention(culpritNode, repeatableAnnotationType, getRetentionName(annotationTypeBits), containerAnnotationType, getRetentionName(containerTypeBits));
        }
        if ((repeatableAnnotationType.getAnnotationTagBits() & 0x800000000000L) != 0x0L && (containerAnnotationType.getAnnotationTagBits() & 0x800000000000L) == 0x0L) {
            repeatableAnnotationType.tagAsHavingDefectiveContainerType();
            scope.problemReporter().repeatableAnnotationTypeIsDocumented(culpritNode, repeatableAnnotationType, containerAnnotationType);
        }
        if ((repeatableAnnotationType.getAnnotationTagBits() & 0x1000000000000L) != 0x0L && (containerAnnotationType.getAnnotationTagBits() & 0x1000000000000L) == 0x0L) {
            repeatableAnnotationType.tagAsHavingDefectiveContainerType();
            scope.problemReporter().repeatableAnnotationTypeIsInherited(culpritNode, repeatableAnnotationType, containerAnnotationType);
        }
    }
    
    private static void checkContainerAnnotationTypeTarget(final ASTNode culpritNode, final Scope scope, final ReferenceBinding containerType, final ReferenceBinding repeatableAnnotationType) {
        long tagBits = repeatableAnnotationType.getAnnotationTagBits();
        if ((tagBits & 0x600FF800000000L) == 0x0L) {
            tagBits = 17523466567680L;
        }
        long containerAnnotationTypeTypeTagBits = containerType.getAnnotationTagBits();
        if ((containerAnnotationTypeTypeTagBits & 0x600FF800000000L) == 0x0L) {
            containerAnnotationTypeTypeTagBits = 17523466567680L;
        }
        final long targets = tagBits & 0x600FF800000000L;
        final long containerAnnotationTypeTargets = containerAnnotationTypeTypeTagBits & 0x600FF800000000L;
        if ((containerAnnotationTypeTargets & ~targets) != 0x0L) {
            class MissingTargetBuilder
            {
                StringBuffer targetBuffer;
                
                MissingTargetBuilder() {
                    this.targetBuffer = new StringBuffer();
                }
                
                void check(final long targetMask, final char[] targetName) {
                    if ((containerAnnotationTypeTargets & targetMask & ~targets) != 0x0L) {
                        if (targetMask == 68719476736L && (targets & 0x20000000000000L) != 0x0L) {
                            return;
                        }
                        this.add(targetName);
                    }
                }
                
                void checkAnnotationType(final char[] targetName) {
                    if ((containerAnnotationTypeTargets & 0x40000000000L) != 0x0L && (targets & 0x41000000000L) == 0x0L) {
                        this.add(targetName);
                    }
                }
                
                private void add(final char[] targetName) {
                    if (this.targetBuffer.length() != 0) {
                        this.targetBuffer.append(", ");
                    }
                    this.targetBuffer.append(targetName);
                }
                
                @Override
                public String toString() {
                    return this.targetBuffer.toString();
                }
                
                public boolean hasError() {
                    return this.targetBuffer.length() != 0;
                }
            }
            final MissingTargetBuilder builder = new MissingTargetBuilder();
            builder.check(68719476736L, TypeConstants.TYPE);
            builder.check(137438953472L, TypeConstants.UPPER_FIELD);
            builder.check(274877906944L, TypeConstants.UPPER_METHOD);
            builder.check(549755813888L, TypeConstants.UPPER_PARAMETER);
            builder.check(1099511627776L, TypeConstants.UPPER_CONSTRUCTOR);
            builder.check(2199023255552L, TypeConstants.UPPER_LOCAL_VARIABLE);
            builder.checkAnnotationType(TypeConstants.UPPER_ANNOTATION_TYPE);
            builder.check(8796093022208L, TypeConstants.UPPER_PACKAGE);
            builder.check(18014398509481984L, TypeConstants.TYPE_PARAMETER_TARGET);
            builder.check(9007199254740992L, TypeConstants.TYPE_USE_TARGET);
            if (builder.hasError()) {
                repeatableAnnotationType.tagAsHavingDefectiveContainerType();
                scope.problemReporter().repeatableAnnotationTypeTargetMismatch(culpritNode, repeatableAnnotationType, containerType, builder.toString());
            }
        }
    }
    
    public static void checkContainingAnnotationTargetAtUse(final Annotation repeatingAnnotation, final BlockScope scope, final TypeBinding containerAnnotationType, final TypeBinding repeatingAnnotationType) {
        if (!repeatingAnnotationType.isValidBinding()) {
            return;
        }
        if (!isAnnotationTargetAllowed(repeatingAnnotation, scope, containerAnnotationType, repeatingAnnotation.recipient.kind())) {
            scope.problemReporter().disallowedTargetForContainerAnnotation(repeatingAnnotation, containerAnnotationType);
        }
    }
    
    public AnnotationBinding getCompilerAnnotation() {
        return this.compilerAnnotation;
    }
    
    public boolean isRuntimeInvisible() {
        final TypeBinding annotationBinding = this.resolvedType;
        if (annotationBinding == null) {
            return false;
        }
        final long metaTagBits = annotationBinding.getAnnotationTagBits();
        return ((metaTagBits & 0x60000000000000L) == 0x0L || (metaTagBits & 0xFF000000000L) != 0x0L) && ((metaTagBits & 0x300000000000L) == 0x0L || (metaTagBits & 0x300000000000L) == 0x200000000000L);
    }
    
    public boolean isRuntimeTypeInvisible() {
        final TypeBinding annotationBinding = this.resolvedType;
        if (annotationBinding == null) {
            return false;
        }
        final long metaTagBits = annotationBinding.getAnnotationTagBits();
        return (metaTagBits & 0x600FF800000000L) != 0x0L && (metaTagBits & 0x60000000000000L) != 0x0L && ((metaTagBits & 0x300000000000L) == 0x0L || (metaTagBits & 0x300000000000L) == 0x200000000000L);
    }
    
    public boolean isRuntimeTypeVisible() {
        final TypeBinding annotationBinding = this.resolvedType;
        if (annotationBinding == null) {
            return false;
        }
        final long metaTagBits = annotationBinding.getAnnotationTagBits();
        return (metaTagBits & 0x600FF800000000L) != 0x0L && (metaTagBits & 0x60000000000000L) != 0x0L && (metaTagBits & 0x300000000000L) != 0x0L && (metaTagBits & 0x300000000000L) == 0x300000000000L;
    }
    
    public boolean isRuntimeVisible() {
        final TypeBinding annotationBinding = this.resolvedType;
        if (annotationBinding == null) {
            return false;
        }
        final long metaTagBits = annotationBinding.getAnnotationTagBits();
        return ((metaTagBits & 0x60000000000000L) == 0x0L || (metaTagBits & 0xFF000000000L) != 0x0L) && (metaTagBits & 0x300000000000L) != 0x0L && (metaTagBits & 0x300000000000L) == 0x300000000000L;
    }
    
    public abstract MemberValuePair[] memberValuePairs();
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        output.append('@');
        this.type.printExpression(0, output);
        return output;
    }
    
    public void recordSuppressWarnings(final Scope scope, final int startSuppresss, final int endSuppress, final boolean isSuppressingWarnings) {
        IrritantSet suppressWarningIrritants = null;
        final MemberValuePair[] pairs = this.memberValuePairs();
        int i = 0;
        final int length = pairs.length;
        while (i < length) {
            final MemberValuePair pair = pairs[i];
            if (CharOperation.equals(pair.name, TypeConstants.VALUE)) {
                final Expression value = pair.value;
                if (value instanceof ArrayInitializer) {
                    final ArrayInitializer initializer = (ArrayInitializer)value;
                    final Expression[] inits = initializer.expressions;
                    if (inits != null) {
                        for (int j = 0, initsLength = inits.length; j < initsLength; ++j) {
                            final Constant cst = inits[j].constant;
                            if (cst != Constant.NotAConstant && cst.typeID() == 11) {
                                final IrritantSet irritants = CompilerOptions.warningTokenToIrritants(cst.stringValue());
                                if (irritants != null) {
                                    if (suppressWarningIrritants == null) {
                                        suppressWarningIrritants = new IrritantSet(irritants);
                                    }
                                    else if (suppressWarningIrritants.set(irritants) == null) {
                                        scope.problemReporter().unusedWarningToken(inits[j]);
                                    }
                                }
                                else {
                                    scope.problemReporter().unhandledWarningToken(inits[j]);
                                }
                            }
                        }
                        break;
                    }
                    break;
                }
                else {
                    final Constant cst2 = value.constant;
                    if (cst2 == Constant.NotAConstant || cst2.typeID() != 11) {
                        break;
                    }
                    final IrritantSet irritants2 = CompilerOptions.warningTokenToIrritants(cst2.stringValue());
                    if (irritants2 != null) {
                        suppressWarningIrritants = new IrritantSet(irritants2);
                        break;
                    }
                    scope.problemReporter().unhandledWarningToken(value);
                    break;
                }
            }
            else {
                ++i;
            }
        }
        if (isSuppressingWarnings && suppressWarningIrritants != null) {
            scope.referenceCompilationUnit().recordSuppressWarnings(suppressWarningIrritants, this, startSuppresss, endSuppress, scope.referenceContext());
        }
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        if (this.compilerAnnotation != null) {
            return this.resolvedType;
        }
        this.constant = Constant.NotAConstant;
        final TypeBinding typeBinding = this.type.resolveType(scope);
        if (typeBinding == null) {
            return null;
        }
        this.resolvedType = typeBinding;
        if (!typeBinding.isAnnotationType() && typeBinding.isValidBinding()) {
            scope.problemReporter().notAnnotationType(typeBinding, this.type);
            return null;
        }
        final ReferenceBinding annotationType = (ReferenceBinding)this.resolvedType;
        final MethodBinding[] methods = annotationType.methods();
        final MemberValuePair[] originalValuePairs = this.memberValuePairs();
        MemberValuePair valueAttribute = null;
        final int pairsLength = originalValuePairs.length;
        MemberValuePair[] pairs;
        if (pairsLength > 0) {
            System.arraycopy(originalValuePairs, 0, pairs = new MemberValuePair[pairsLength], 0, pairsLength);
        }
        else {
            pairs = originalValuePairs;
        }
        int i = 0;
        final int requiredLength = methods.length;
    Label_0399:
        while (i < requiredLength) {
            final MethodBinding method = methods[i];
            final char[] selector = method.selector;
            boolean foundValue = false;
            while (true) {
                for (int j = 0; j < pairsLength; ++j) {
                    final MemberValuePair pair = pairs[j];
                    if (pair != null) {
                        final char[] name = pair.name;
                        if (CharOperation.equals(name, selector)) {
                            if (valueAttribute == null && CharOperation.equals(name, TypeConstants.VALUE)) {
                                valueAttribute = pair;
                            }
                            pair.binding = method;
                            pair.resolveTypeExpecting(scope, method.returnType);
                            pairs[j] = null;
                            foundValue = true;
                            boolean foundDuplicate = false;
                            for (int k = j + 1; k < pairsLength; ++k) {
                                final MemberValuePair otherPair = pairs[k];
                                if (otherPair != null) {
                                    if (CharOperation.equals(otherPair.name, selector)) {
                                        foundDuplicate = true;
                                        scope.problemReporter().duplicateAnnotationValue(annotationType, otherPair);
                                        otherPair.binding = method;
                                        otherPair.resolveTypeExpecting(scope, method.returnType);
                                        pairs[k] = null;
                                    }
                                }
                            }
                            if (foundDuplicate) {
                                scope.problemReporter().duplicateAnnotationValue(annotationType, pair);
                                ++i;
                                continue Label_0399;
                            }
                        }
                    }
                }
                if (!foundValue && (method.modifiers & 0x20000) == 0x0 && (this.bits & 0x20) == 0x0 && annotationType.isValidBinding()) {
                    scope.problemReporter().missingValueForAnnotationMember(this, selector);
                }
                continue;
            }
        }
        for (i = 0; i < pairsLength; ++i) {
            if (pairs[i] != null) {
                if (annotationType.isValidBinding()) {
                    scope.problemReporter().undefinedAnnotationValue(annotationType, pairs[i]);
                }
                pairs[i].resolveTypeExpecting(scope, null);
            }
        }
        this.compilerAnnotation = scope.environment().createAnnotation((ReferenceBinding)this.resolvedType, this.computeElementValuePairs());
        long tagBits = this.detectStandardAnnotation(scope, annotationType, valueAttribute);
        final int defaultNullness = (int)(tagBits & 0x3FAL);
        tagBits &= 0xFFFFFFFFFFFFFC05L;
        scope.referenceCompilationUnit().recordSuppressWarnings(IrritantSet.NLS, null, this.sourceStart, this.declarationSourceEnd, scope.referenceContext());
        if (this.recipient != null) {
            int kind = this.recipient.kind();
            if (tagBits != 0L || defaultNullness != 0) {
                switch (kind) {
                    case 16: {
                        final PackageBinding packageBinding = (PackageBinding)this.recipient;
                        packageBinding.tagBits |= tagBits;
                        break;
                    }
                    case 4:
                    case 2052: {
                        final SourceTypeBinding sourceType = (SourceTypeBinding)this.recipient;
                        if ((tagBits & 0x1000000000000000L) == 0x0L || sourceType.isAnnotationType()) {
                            final SourceTypeBinding sourceTypeBinding = sourceType;
                            sourceTypeBinding.tagBits |= tagBits;
                        }
                        if ((tagBits & 0x4000000000000L) != 0x0L) {
                            final TypeDeclaration typeDeclaration = sourceType.scope.referenceContext;
                            int start;
                            if (scope.referenceCompilationUnit().types[0] == typeDeclaration) {
                                start = 0;
                            }
                            else {
                                start = typeDeclaration.declarationSourceStart;
                            }
                            this.recordSuppressWarnings(scope, start, typeDeclaration.declarationSourceEnd, scope.compilerOptions().suppressWarnings);
                        }
                        final SourceTypeBinding sourceTypeBinding2 = sourceType;
                        sourceTypeBinding2.defaultNullness |= defaultNullness;
                        break;
                    }
                    case 8: {
                        final MethodBinding methodBinding;
                        final MethodBinding sourceMethod = methodBinding = (MethodBinding)this.recipient;
                        methodBinding.tagBits |= tagBits;
                        if ((tagBits & 0x4000000000000L) != 0x0L) {
                            final SourceTypeBinding sourceType = (SourceTypeBinding)sourceMethod.declaringClass;
                            final AbstractMethodDeclaration methodDeclaration = sourceType.scope.referenceContext.declarationOf(sourceMethod);
                            this.recordSuppressWarnings(scope, methodDeclaration.declarationSourceStart, methodDeclaration.declarationSourceEnd, scope.compilerOptions().suppressWarnings);
                        }
                        final long nullBits = sourceMethod.tagBits & 0x180000000000000L;
                        if (nullBits == 108086391056891904L) {
                            scope.problemReporter().contradictoryNullAnnotations(this);
                            final MethodBinding methodBinding2 = sourceMethod;
                            methodBinding2.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                        }
                        if (nullBits != 0L && sourceMethod.isConstructor()) {
                            if (scope.compilerOptions().sourceLevel >= 3407872L) {
                                scope.problemReporter().nullAnnotationUnsupportedLocation(this);
                            }
                            final MethodBinding methodBinding3 = sourceMethod;
                            methodBinding3.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                        }
                        final MethodBinding methodBinding4 = sourceMethod;
                        methodBinding4.defaultNullness |= defaultNullness;
                        break;
                    }
                    case 1: {
                        final FieldBinding fieldBinding;
                        final FieldBinding sourceField = fieldBinding = (FieldBinding)this.recipient;
                        fieldBinding.tagBits |= tagBits;
                        if ((tagBits & 0x4000000000000L) != 0x0L) {
                            final SourceTypeBinding sourceType = (SourceTypeBinding)sourceField.declaringClass;
                            final FieldDeclaration fieldDeclaration = sourceType.scope.referenceContext.declarationOf(sourceField);
                            this.recordSuppressWarnings(scope, fieldDeclaration.declarationSourceStart, fieldDeclaration.declarationSourceEnd, scope.compilerOptions().suppressWarnings);
                        }
                        if ((sourceField.tagBits & 0x180000000000000L) == 0x180000000000000L) {
                            scope.problemReporter().contradictoryNullAnnotations(this);
                            final FieldBinding fieldBinding2 = sourceField;
                            fieldBinding2.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        final LocalVariableBinding localVariableBinding;
                        final LocalVariableBinding variable = localVariableBinding = (LocalVariableBinding)this.recipient;
                        localVariableBinding.tagBits |= tagBits;
                        if ((variable.tagBits & 0x180000000000000L) == 0x180000000000000L) {
                            scope.problemReporter().contradictoryNullAnnotations(this);
                            final LocalVariableBinding localVariableBinding2 = variable;
                            localVariableBinding2.tagBits &= 0xFE7FFFFFFFFFFFFFL;
                        }
                        if ((tagBits & 0x4000000000000L) != 0x0L) {
                            final LocalDeclaration localDeclaration = variable.declaration;
                            this.recordSuppressWarnings(scope, localDeclaration.declarationSourceStart, localDeclaration.declarationSourceEnd, scope.compilerOptions().suppressWarnings);
                            break;
                        }
                        break;
                    }
                }
            }
            if (kind == 4) {
                final SourceTypeBinding sourceType = (SourceTypeBinding)this.recipient;
                if (CharOperation.equals(sourceType.sourceName, TypeConstants.PACKAGE_INFO_NAME)) {
                    kind = 16;
                }
            }
            checkAnnotationTarget(this, scope, annotationType, kind, this.recipient, tagBits & 0x180000000000000L);
        }
        return this.resolvedType;
    }
    
    private static boolean isAnnotationTargetAllowed(final Binding recipient, final BlockScope scope, final TypeBinding annotationType, final int kind, final long metaTagBits) {
        switch (kind) {
            case 16: {
                if ((metaTagBits & 0x80000000000L) != 0x0L) {
                    return true;
                }
                if (scope.compilerOptions().sourceLevel > 3276800L) {
                    break;
                }
                final SourceTypeBinding sourceType = (SourceTypeBinding)recipient;
                if (CharOperation.equals(sourceType.sourceName, TypeConstants.PACKAGE_INFO_NAME)) {
                    return true;
                }
                break;
            }
            case 16388: {
                if ((metaTagBits & 0x20000000000000L) != 0x0L) {
                    return true;
                }
                if (scope.compilerOptions().sourceLevel < 3407872L) {
                    return true;
                }
                break;
            }
            case 4:
            case 2052: {
                if (((ReferenceBinding)recipient).isAnnotationType()) {
                    if ((metaTagBits & 0x20041000000000L) != 0x0L) {
                        return true;
                    }
                    break;
                }
                else {
                    if ((metaTagBits & 0x20001000000000L) != 0x0L) {
                        return true;
                    }
                    if ((metaTagBits & 0x80000000000L) != 0x0L && CharOperation.equals(((ReferenceBinding)recipient).sourceName, TypeConstants.PACKAGE_INFO_NAME)) {
                        return true;
                    }
                    break;
                }
                break;
            }
            case 8: {
                final MethodBinding methodBinding = (MethodBinding)recipient;
                if (methodBinding.isConstructor()) {
                    if ((metaTagBits & 0x20010000000000L) != 0x0L) {
                        return true;
                    }
                    break;
                }
                else {
                    if ((metaTagBits & 0x4000000000L) != 0x0L) {
                        return true;
                    }
                    if ((metaTagBits & 0x20000000000000L) == 0x0L) {
                        break;
                    }
                    final SourceTypeBinding sourceType2 = (SourceTypeBinding)methodBinding.declaringClass;
                    final MethodDeclaration methodDecl = (MethodDeclaration)sourceType2.scope.referenceContext.declarationOf(methodBinding);
                    if (isTypeUseCompatible(methodDecl.returnType, scope)) {
                        return true;
                    }
                    break;
                }
                break;
            }
            case 1: {
                if ((metaTagBits & 0x2000000000L) != 0x0L) {
                    return true;
                }
                if ((metaTagBits & 0x20000000000000L) == 0x0L) {
                    break;
                }
                final FieldBinding sourceField = (FieldBinding)recipient;
                final SourceTypeBinding sourceType3 = (SourceTypeBinding)sourceField.declaringClass;
                final FieldDeclaration fieldDeclaration = sourceType3.scope.referenceContext.declarationOf(sourceField);
                if (isTypeUseCompatible(fieldDeclaration.type, scope)) {
                    return true;
                }
                break;
            }
            case 2: {
                final LocalVariableBinding localVariableBinding = (LocalVariableBinding)recipient;
                if ((localVariableBinding.tagBits & 0x400L) != 0x0L) {
                    if ((metaTagBits & 0x8000000000L) != 0x0L) {
                        return true;
                    }
                    if ((metaTagBits & 0x20000000000000L) != 0x0L && isTypeUseCompatible(localVariableBinding.declaration.type, scope)) {
                        return true;
                    }
                    break;
                }
                else {
                    if ((annotationType.tagBits & 0x20000000000L) != 0x0L) {
                        return true;
                    }
                    if ((metaTagBits & 0x20000000000000L) != 0x0L && isTypeUseCompatible(localVariableBinding.declaration.type, scope)) {
                        return true;
                    }
                    break;
                }
                break;
            }
            case 4100: {
                if ((metaTagBits & 0x60000000000000L) != 0x0L) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    public static boolean isAnnotationTargetAllowed(final BlockScope scope, final TypeBinding annotationType, final Binding recipient) {
        final long metaTagBits = annotationType.getAnnotationTagBits();
        return (metaTagBits & 0x600FF800000000L) == 0x0L || isAnnotationTargetAllowed(recipient, scope, annotationType, recipient.kind(), metaTagBits);
    }
    
    static boolean isAnnotationTargetAllowed(final Annotation annotation, final BlockScope scope, final TypeBinding annotationType, final int kind) {
        final long metaTagBits = annotationType.getAnnotationTagBits();
        if ((metaTagBits & 0x600FF800000000L) == 0x0L) {
            if (kind == 4100 || kind == 16388) {
                scope.problemReporter().explitAnnotationTargetRequired(annotation);
            }
            return true;
        }
        if ((metaTagBits & 0xFF000000000L) == 0x0L && (metaTagBits & 0x60000000000000L) != 0x0L && scope.compilerOptions().sourceLevel < 3407872L) {
            switch (kind) {
                case 1:
                case 2:
                case 4:
                case 8:
                case 16:
                case 2052: {
                    scope.problemReporter().invalidUsageOfTypeAnnotations(annotation);
                    break;
                }
            }
        }
        return isAnnotationTargetAllowed(annotation.recipient, scope, annotationType, kind, metaTagBits);
    }
    
    static void checkAnnotationTarget(final Annotation annotation, final BlockScope scope, final ReferenceBinding annotationType, final int kind, final Binding recipient, final long tagBitsToRevert) {
        if (!annotationType.isValidBinding()) {
            return;
        }
        if (!isAnnotationTargetAllowed(annotation, scope, annotationType, kind)) {
            scope.problemReporter().disallowedTargetForAnnotation(annotation);
            if (recipient instanceof TypeBinding) {
                final TypeBinding typeBinding = (TypeBinding)recipient;
                typeBinding.tagBits &= ~tagBitsToRevert;
            }
        }
    }
    
    public static void checkForInstancesOfRepeatableWithRepeatingContainerAnnotation(final BlockScope scope, final ReferenceBinding repeatedAnnotationType, final Annotation[] sourceAnnotations) {
        final MethodBinding[] valueMethods = repeatedAnnotationType.getMethods(TypeConstants.VALUE);
        if (valueMethods.length != 1) {
            return;
        }
        final TypeBinding methodReturnType = valueMethods[0].returnType;
        if (!methodReturnType.isArrayType() || methodReturnType.dimensions() != 1) {
            return;
        }
        final ArrayBinding array = (ArrayBinding)methodReturnType;
        final TypeBinding elementsType = array.elementsType();
        if (!elementsType.isRepeatableAnnotationType()) {
            return;
        }
        for (int i = 0; i < sourceAnnotations.length; ++i) {
            final Annotation annotation = sourceAnnotations[i];
            if (TypeBinding.equalsEquals(elementsType, annotation.resolvedType)) {
                scope.problemReporter().repeatableAnnotationWithRepeatingContainer(annotation, repeatedAnnotationType);
                return;
            }
        }
    }
    
    public static boolean isTypeUseCompatible(final TypeReference reference, final Scope scope) {
        if (reference != null && !(reference instanceof SingleTypeReference)) {
            final Binding binding = scope.getPackage(reference.getTypeName());
            if (binding instanceof PackageBinding) {
                return false;
            }
        }
        return true;
    }
    
    public static void isTypeUseCompatible(final TypeReference reference, final Scope scope, final Annotation[] annotations) {
        if (annotations == null || reference == null || reference.getAnnotatableLevels() == 1) {
            return;
        }
        if (scope.environment().globalOptions.sourceLevel < 3407872L) {
            return;
        }
        final TypeBinding resolvedType = (reference.resolvedType == null) ? null : reference.resolvedType.leafComponentType();
        if (resolvedType == null || !resolvedType.isNestedType()) {
            return;
        }
        for (int i = 0, annotationsLength = annotations.length; i < annotationsLength; ++i) {
            final Annotation annotation = annotations[i];
            final long metaTagBits = annotation.resolvedType.getAnnotationTagBits();
            if ((metaTagBits & 0x20000000000000L) != 0x0L && (metaTagBits & 0xFF000000000L) == 0x0L) {
                for (ReferenceBinding currentType = (ReferenceBinding)resolvedType; currentType.isNestedType(); currentType = currentType.enclosingType()) {
                    if (currentType.isStatic()) {
                        QualifiedTypeReference.rejectAnnotationsOnStaticMemberQualififer(scope, currentType, new Annotation[] { annotation });
                        break;
                    }
                    if (annotation.hasNullBit(96)) {
                        scope.problemReporter().nullAnnotationUnsupportedLocation(annotation);
                        break;
                    }
                }
            }
        }
    }
    
    public boolean hasNullBit(final int bit) {
        return this.resolvedType instanceof ReferenceBinding && ((ReferenceBinding)this.resolvedType).hasNullBit(bit);
    }
    
    @Override
    public abstract void traverse(final ASTVisitor p0, final BlockScope p1);
    
    @Override
    public abstract void traverse(final ASTVisitor p0, final ClassScope p1);
    
    public Annotation getPersistibleAnnotation() {
        return this.persistibleAnnotation;
    }
    
    public void setPersistibleAnnotation(final ContainerAnnotation container) {
        this.persistibleAnnotation = container;
    }
}
