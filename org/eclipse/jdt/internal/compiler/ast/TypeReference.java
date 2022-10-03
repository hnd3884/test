package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Substitution;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.codegen.AnnotationContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public abstract class TypeReference extends Expression
{
    public static final TypeReference[] NO_TYPE_ARGUMENTS;
    public Annotation[][] annotations;
    
    static {
        NO_TYPE_ARGUMENTS = new TypeReference[0];
    }
    
    public TypeReference() {
        this.annotations = null;
    }
    
    public static final TypeReference baseTypeReference(final int baseType, final int dim, final Annotation[][] dimAnnotations) {
        if (dim == 0) {
            switch (baseType) {
                case 6: {
                    return new SingleTypeReference(TypeBinding.VOID.simpleName, 0L);
                }
                case 5: {
                    return new SingleTypeReference(TypeBinding.BOOLEAN.simpleName, 0L);
                }
                case 2: {
                    return new SingleTypeReference(TypeBinding.CHAR.simpleName, 0L);
                }
                case 9: {
                    return new SingleTypeReference(TypeBinding.FLOAT.simpleName, 0L);
                }
                case 8: {
                    return new SingleTypeReference(TypeBinding.DOUBLE.simpleName, 0L);
                }
                case 3: {
                    return new SingleTypeReference(TypeBinding.BYTE.simpleName, 0L);
                }
                case 4: {
                    return new SingleTypeReference(TypeBinding.SHORT.simpleName, 0L);
                }
                case 10: {
                    return new SingleTypeReference(TypeBinding.INT.simpleName, 0L);
                }
                default: {
                    return new SingleTypeReference(TypeBinding.LONG.simpleName, 0L);
                }
            }
        }
        else {
            switch (baseType) {
                case 6: {
                    return new ArrayTypeReference(TypeBinding.VOID.simpleName, dim, dimAnnotations, 0L);
                }
                case 5: {
                    return new ArrayTypeReference(TypeBinding.BOOLEAN.simpleName, dim, dimAnnotations, 0L);
                }
                case 2: {
                    return new ArrayTypeReference(TypeBinding.CHAR.simpleName, dim, dimAnnotations, 0L);
                }
                case 9: {
                    return new ArrayTypeReference(TypeBinding.FLOAT.simpleName, dim, dimAnnotations, 0L);
                }
                case 8: {
                    return new ArrayTypeReference(TypeBinding.DOUBLE.simpleName, dim, dimAnnotations, 0L);
                }
                case 3: {
                    return new ArrayTypeReference(TypeBinding.BYTE.simpleName, dim, dimAnnotations, 0L);
                }
                case 4: {
                    return new ArrayTypeReference(TypeBinding.SHORT.simpleName, dim, dimAnnotations, 0L);
                }
                case 10: {
                    return new ArrayTypeReference(TypeBinding.INT.simpleName, dim, dimAnnotations, 0L);
                }
                default: {
                    return new ArrayTypeReference(TypeBinding.LONG.simpleName, dim, dimAnnotations, 0L);
                }
            }
        }
    }
    
    public static final TypeReference baseTypeReference(final int baseType, final int dim) {
        return baseTypeReference(baseType, dim, null);
    }
    
    public void aboutToResolve(final Scope scope) {
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return flowInfo;
    }
    
    public void checkBounds(final Scope scope) {
    }
    
    public abstract TypeReference augmentTypeWithAdditionalDimensions(final int p0, final Annotation[][] p1, final boolean p2);
    
    protected Annotation[][] getMergedAnnotationsOnDimensions(final int additionalDimensions, final Annotation[][] additionalAnnotations) {
        final Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions(true);
        final int dimensions = this.dimensions();
        if (annotationsOnDimensions == null && additionalAnnotations == null) {
            return null;
        }
        final int totalDimensions = dimensions + additionalDimensions;
        final Annotation[][] mergedAnnotations = new Annotation[totalDimensions][];
        if (annotationsOnDimensions != null) {
            for (int i = 0; i < dimensions; ++i) {
                mergedAnnotations[i] = annotationsOnDimensions[i];
            }
        }
        if (additionalAnnotations != null) {
            for (int i = dimensions, j = 0; i < totalDimensions; ++i, ++j) {
                mergedAnnotations[i] = additionalAnnotations[j];
            }
        }
        return mergedAnnotations;
    }
    
    public int dimensions() {
        return 0;
    }
    
    public int extraDimensions() {
        return 0;
    }
    
    public AnnotationContext[] getAllAnnotationContexts(final int targetType) {
        final List allAnnotationContexts = new ArrayList();
        final AnnotationCollector collector = new AnnotationCollector(this, targetType, allAnnotationContexts);
        this.traverse(collector, (BlockScope)null);
        return allAnnotationContexts.toArray(new AnnotationContext[allAnnotationContexts.size()]);
    }
    
    public void getAllAnnotationContexts(final int targetType, final int info, final List allAnnotationContexts) {
        final AnnotationCollector collector = new AnnotationCollector(this, targetType, info, allAnnotationContexts);
        this.traverse(collector, (BlockScope)null);
    }
    
    public void getAllAnnotationContexts(final int targetType, final int info, final List allAnnotationContexts, final Annotation[] se7Annotations) {
        final AnnotationCollector collector = new AnnotationCollector(this, targetType, info, allAnnotationContexts);
        for (int i = 0, length = (se7Annotations == null) ? 0 : se7Annotations.length; i < length; ++i) {
            final Annotation annotation = se7Annotations[i];
            annotation.traverse(collector, (BlockScope)null);
        }
        this.traverse(collector, (BlockScope)null);
    }
    
    public void getAllAnnotationContexts(final int targetType, final int info, final List allAnnotationContexts, final Annotation[][] annotationsOnDimensions, final int dimensions) {
        final AnnotationCollector collector = new AnnotationCollector(this, targetType, info, allAnnotationContexts, annotationsOnDimensions, dimensions);
        this.traverse(collector, (BlockScope)null);
        if (annotationsOnDimensions != null) {
            for (int i = 0, max = annotationsOnDimensions.length; i < max; ++i) {
                final Annotation[] annotationsOnDimension = annotationsOnDimensions[i];
                if (annotationsOnDimension != null) {
                    for (int j = 0, max2 = annotationsOnDimension.length; j < max2; ++j) {
                        annotationsOnDimension[j].traverse(collector, (BlockScope)null);
                    }
                }
            }
        }
    }
    
    public void getAllAnnotationContexts(final int targetType, final int info, final int typeIndex, final List allAnnotationContexts) {
        final AnnotationCollector collector = new AnnotationCollector(this, targetType, info, typeIndex, allAnnotationContexts);
        this.traverse(collector, (BlockScope)null);
    }
    
    public void getAllAnnotationContexts(final int targetType, final List allAnnotationContexts) {
        final AnnotationCollector collector = new AnnotationCollector(this, targetType, allAnnotationContexts);
        this.traverse(collector, (BlockScope)null);
    }
    
    public Annotation[][] getAnnotationsOnDimensions() {
        return this.getAnnotationsOnDimensions(false);
    }
    
    public TypeReference[][] getTypeArguments() {
        return null;
    }
    
    public Annotation[][] getAnnotationsOnDimensions(final boolean useSourceOrder) {
        return null;
    }
    
    public void setAnnotationsOnDimensions(final Annotation[][] annotationsOnDimensions) {
    }
    
    public abstract char[] getLastToken();
    
    public char[][] getParameterizedTypeName() {
        return this.getTypeName();
    }
    
    protected abstract TypeBinding getTypeBinding(final Scope p0);
    
    public abstract char[][] getTypeName();
    
    protected TypeBinding internalResolveType(final Scope scope, final int location) {
        this.constant = Constant.NotAConstant;
        if (this.resolvedType != null) {
            if (this.resolvedType.isValidBinding()) {
                return this.resolvedType;
            }
            switch (this.resolvedType.problemId()) {
                case 1:
                case 2:
                case 5: {
                    final TypeBinding type = this.resolvedType.closestMatch();
                    if (type == null) {
                        return null;
                    }
                    return scope.environment().convertToRawType(type, false);
                }
                default: {
                    return null;
                }
            }
        }
        else {
            final TypeBinding typeBinding = this.getTypeBinding(scope);
            this.resolvedType = typeBinding;
            TypeBinding type2 = typeBinding;
            if (type2 == null) {
                return null;
            }
            final boolean hasError;
            if (hasError = !type2.isValidBinding()) {
                this.reportInvalidType(scope);
                switch (type2.problemId()) {
                    case 1:
                    case 2:
                    case 5: {
                        type2 = type2.closestMatch();
                        if (type2 == null) {
                            return null;
                        }
                        break;
                    }
                    default: {
                        return null;
                    }
                }
            }
            if (type2.isArrayType() && ((ArrayBinding)type2).leafComponentType == TypeBinding.VOID) {
                scope.problemReporter().cannotAllocateVoidArray(this);
                return null;
            }
            if (!(this instanceof QualifiedTypeReference) && this.isTypeUseDeprecated(type2, scope)) {
                this.reportDeprecatedType(type2, scope);
            }
            type2 = scope.environment().convertToRawType(type2, false);
            if (type2.leafComponentType().isRawType() && (this.bits & 0x40000000) == 0x0 && scope.compilerOptions().getSeverity(536936448) != 256) {
                scope.problemReporter().rawTypeReference(this, type2);
            }
            if (hasError) {
                this.resolveAnnotations(scope, 0);
                return type2;
            }
            this.resolvedType = type2;
            this.resolveAnnotations(scope, location);
            return this.resolvedType;
        }
    }
    
    @Override
    public boolean isTypeReference() {
        return true;
    }
    
    public boolean isWildcard() {
        return false;
    }
    
    public boolean isUnionType() {
        return false;
    }
    
    public boolean isVarargs() {
        return (this.bits & 0x4000) != 0x0;
    }
    
    public boolean isParameterizedTypeReference() {
        return false;
    }
    
    protected void reportDeprecatedType(final TypeBinding type, final Scope scope, final int index) {
        scope.problemReporter().deprecatedType(type, this, index);
    }
    
    protected void reportDeprecatedType(final TypeBinding type, final Scope scope) {
        scope.problemReporter().deprecatedType(type, this, Integer.MAX_VALUE);
    }
    
    protected void reportInvalidType(final Scope scope) {
        scope.problemReporter().invalidType(this, this.resolvedType);
    }
    
    public TypeBinding resolveSuperType(final ClassScope scope) {
        final TypeBinding superType = this.resolveType(scope);
        if (superType == null) {
            return null;
        }
        if (superType.isTypeVariable()) {
            if (this.resolvedType.isValidBinding()) {
                this.resolvedType = new ProblemReferenceBinding(this.getTypeName(), (ReferenceBinding)this.resolvedType, 9);
                this.reportInvalidType(scope);
            }
            return null;
        }
        return superType;
    }
    
    @Override
    public final TypeBinding resolveType(final BlockScope blockScope) {
        return this.resolveType(blockScope, true);
    }
    
    public TypeBinding resolveType(final BlockScope scope, final boolean checkBounds) {
        return this.resolveType(scope, checkBounds, 0);
    }
    
    public TypeBinding resolveType(final BlockScope scope, final boolean checkBounds, final int location) {
        return this.internalResolveType(scope, location);
    }
    
    @Override
    public TypeBinding resolveType(final ClassScope scope) {
        return this.resolveType(scope, 0);
    }
    
    public TypeBinding resolveType(final ClassScope scope, final int location) {
        return this.internalResolveType(scope, location);
    }
    
    public TypeBinding resolveTypeArgument(final BlockScope blockScope, final ReferenceBinding genericType, final int rank) {
        return this.resolveType(blockScope, true, 64);
    }
    
    public TypeBinding resolveTypeArgument(final ClassScope classScope, final ReferenceBinding genericType, final int rank) {
        final ReferenceBinding ref = classScope.referenceContext.binding;
        boolean pauseHierarchyCheck = false;
        try {
            if (ref.isHierarchyBeingConnected()) {
                final ReferenceBinding referenceBinding = ref;
                referenceBinding.tagBits |= 0x80000L;
                pauseHierarchyCheck = true;
            }
            return this.resolveType(classScope, 64);
        }
        finally {
            if (pauseHierarchyCheck) {
                final ReferenceBinding referenceBinding2 = ref;
                referenceBinding2.tagBits &= 0xFFFFFFFFFFF7FFFFL;
            }
        }
    }
    
    @Override
    public abstract void traverse(final ASTVisitor p0, final BlockScope p1);
    
    @Override
    public abstract void traverse(final ASTVisitor p0, final ClassScope p1);
    
    protected void resolveAnnotations(final Scope scope, final int location) {
        final Annotation[][] annotationsOnDimensions = this.getAnnotationsOnDimensions();
        if (this.annotations != null || annotationsOnDimensions != null) {
            final BlockScope resolutionScope = Scope.typeAnnotationsResolutionScope(scope);
            if (resolutionScope != null) {
                final int dimensions = this.dimensions();
                if (this.annotations != null) {
                    TypeBinding leafComponentType = this.resolvedType.leafComponentType();
                    leafComponentType = ASTNode.resolveAnnotations(resolutionScope, this.annotations, leafComponentType);
                    this.resolvedType = ((dimensions > 0) ? scope.environment().createArrayType(leafComponentType, dimensions) : leafComponentType);
                }
                if (annotationsOnDimensions != null) {
                    this.resolvedType = ASTNode.resolveAnnotations(resolutionScope, annotationsOnDimensions, this.resolvedType);
                    if (this.resolvedType instanceof ArrayBinding) {
                        final long[] nullTagBitsPerDimension = ((ArrayBinding)this.resolvedType).nullTagBitsPerDimension;
                        if (nullTagBitsPerDimension != null) {
                            for (int i = 0; i < dimensions; ++i) {
                                if ((nullTagBitsPerDimension[i] & 0x180000000000000L) == 0x180000000000000L) {
                                    scope.problemReporter().contradictoryNullAnnotations(annotationsOnDimensions[i]);
                                    nullTagBitsPerDimension[i] = 0L;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (scope.compilerOptions().isAnnotationBasedNullAnalysisEnabled && this.resolvedType != null && (this.resolvedType.tagBits & 0x180000000000000L) == 0x0L && !this.resolvedType.isTypeVariable() && !this.resolvedType.isWildcard() && location != 0 && scope.hasDefaultNullnessFor(location)) {
            if (location == 256 && this.resolvedType.id == 1) {
                scope.problemReporter().implicitObjectBoundNoNullDefault(this);
            }
            else {
                final LookupEnvironment environment = scope.environment();
                final AnnotationBinding[] annots = { environment.getNonNullAnnotation() };
                this.resolvedType = environment.createAnnotatedType(this.resolvedType, annots);
            }
        }
    }
    
    public int getAnnotatableLevels() {
        return 1;
    }
    
    protected void checkIllegalNullAnnotations(final Scope scope, final TypeReference[] typeArguments) {
        if (scope.environment().usesNullTypeAnnotations() && typeArguments != null) {
            for (int i = 0; i < typeArguments.length; ++i) {
                final TypeReference arg = typeArguments[i];
                if (arg.resolvedType != null) {
                    arg.checkIllegalNullAnnotation(scope);
                }
            }
        }
    }
    
    protected void checkNullConstraints(final Scope scope, final Substitution substitution, final TypeBinding[] variables, final int rank) {
        if (variables != null && variables.length > rank) {
            final TypeBinding variable = variables[rank];
            if (variable.hasNullTypeAnnotations() && NullAnnotationMatching.analyse(variable, this.resolvedType, null, substitution, -1, null, NullAnnotationMatching.CheckMode.BOUND_CHECK).isAnyMismatch()) {
                scope.problemReporter().nullityMismatchTypeArgument(variable, this.resolvedType, this);
            }
        }
        this.checkIllegalNullAnnotation(scope);
    }
    
    protected void checkIllegalNullAnnotation(final Scope scope) {
        if (this.resolvedType.leafComponentType().isBaseType() && this.hasNullTypeAnnotation(AnnotationPosition.LEAF_TYPE)) {
            scope.problemReporter().illegalAnnotationForBaseType(this, this.annotations[0], this.resolvedType.tagBits & 0x180000000000000L);
        }
    }
    
    public Annotation findAnnotation(final long nullTagBits) {
        if (this.annotations != null) {
            final Annotation[] innerAnnotations = this.annotations[this.annotations.length - 1];
            if (innerAnnotations != null) {
                final int annBit = (nullTagBits == 72057594037927936L) ? 32 : 64;
                for (int i = 0; i < innerAnnotations.length; ++i) {
                    if (innerAnnotations[i] != null && innerAnnotations[i].hasNullBit(annBit)) {
                        return innerAnnotations[i];
                    }
                }
            }
        }
        return null;
    }
    
    public boolean hasNullTypeAnnotation(final AnnotationPosition position) {
        if (this.annotations != null) {
            if (position == AnnotationPosition.MAIN_TYPE) {
                final Annotation[] innerAnnotations = this.annotations[this.annotations.length - 1];
                return containsNullAnnotation(innerAnnotations);
            }
            Annotation[][] annotations;
            for (int length = (annotations = this.annotations).length, i = 0; i < length; ++i) {
                final Annotation[] someAnnotations = annotations[i];
                if (containsNullAnnotation(someAnnotations)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean containsNullAnnotation(final Annotation[] annotations) {
        if (annotations != null) {
            for (int i = 0; i < annotations.length; ++i) {
                if (annotations[i] != null && annotations[i].hasNullBit(96)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public TypeReference[] getTypeReferences() {
        return new TypeReference[] { this };
    }
    
    public boolean isBaseTypeReference() {
        return false;
    }
    
    public enum AnnotationPosition
    {
        MAIN_TYPE("MAIN_TYPE", 0), 
        LEAF_TYPE("LEAF_TYPE", 1), 
        ANY("ANY", 2);
        
        private AnnotationPosition(final String s, final int n) {
        }
    }
    
    static class AnnotationCollector extends ASTVisitor
    {
        List annotationContexts;
        Expression typeReference;
        int targetType;
        int info;
        int info2;
        LocalVariableBinding localVariable;
        Annotation[][] annotationsOnDimensions;
        int dimensions;
        Wildcard currentWildcard;
        
        public AnnotationCollector(final TypeParameter typeParameter, final int targetType, final int typeParameterIndex, final List annotationContexts) {
            this.info = 0;
            this.info2 = 0;
            this.annotationContexts = annotationContexts;
            this.typeReference = typeParameter.type;
            this.targetType = targetType;
            this.info = typeParameterIndex;
        }
        
        public AnnotationCollector(final LocalDeclaration localDeclaration, final int targetType, final LocalVariableBinding localVariable, final List annotationContexts) {
            this.info = 0;
            this.info2 = 0;
            this.annotationContexts = annotationContexts;
            this.typeReference = localDeclaration.type;
            this.targetType = targetType;
            this.localVariable = localVariable;
        }
        
        public AnnotationCollector(final LocalDeclaration localDeclaration, final int targetType, final int parameterIndex, final List annotationContexts) {
            this.info = 0;
            this.info2 = 0;
            this.annotationContexts = annotationContexts;
            this.typeReference = localDeclaration.type;
            this.targetType = targetType;
            this.info = parameterIndex;
        }
        
        public AnnotationCollector(final TypeReference typeReference, final int targetType, final List annotationContexts) {
            this.info = 0;
            this.info2 = 0;
            this.annotationContexts = annotationContexts;
            this.typeReference = typeReference;
            this.targetType = targetType;
        }
        
        public AnnotationCollector(final Expression typeReference, final int targetType, final int info, final List annotationContexts) {
            this.info = 0;
            this.info2 = 0;
            this.annotationContexts = annotationContexts;
            this.typeReference = typeReference;
            this.info = info;
            this.targetType = targetType;
        }
        
        public AnnotationCollector(final TypeReference typeReference, final int targetType, final int info, final int typeIndex, final List annotationContexts) {
            this.info = 0;
            this.info2 = 0;
            this.annotationContexts = annotationContexts;
            this.typeReference = typeReference;
            this.info = info;
            this.targetType = targetType;
            this.info2 = typeIndex;
        }
        
        public AnnotationCollector(final TypeReference typeReference, final int targetType, final int info, final List annotationContexts, final Annotation[][] annotationsOnDimensions, final int dimensions) {
            this.info = 0;
            this.info2 = 0;
            this.annotationContexts = annotationContexts;
            this.typeReference = typeReference;
            this.info = info;
            this.targetType = targetType;
            this.annotationsOnDimensions = annotationsOnDimensions;
            this.dimensions = dimensions;
        }
        
        private boolean internalVisit(final Annotation annotation) {
            AnnotationContext annotationContext = null;
            if (annotation.isRuntimeTypeInvisible()) {
                annotationContext = new AnnotationContext(annotation, this.typeReference, this.targetType, 2);
            }
            else if (annotation.isRuntimeTypeVisible()) {
                annotationContext = new AnnotationContext(annotation, this.typeReference, this.targetType, 1);
            }
            if (annotationContext != null) {
                annotationContext.wildcard = this.currentWildcard;
                switch (this.targetType) {
                    case 0:
                    case 1:
                    case 16:
                    case 22:
                    case 23:
                    case 66:
                    case 67:
                    case 68:
                    case 69:
                    case 70: {
                        annotationContext.info = this.info;
                        break;
                    }
                    case 64:
                    case 65: {
                        annotationContext.variableBinding = this.localVariable;
                        break;
                    }
                    case 17:
                    case 18:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75: {
                        annotationContext.info2 = this.info2;
                        annotationContext.info = this.info;
                        break;
                    }
                }
                this.annotationContexts.add(annotationContext);
            }
            return true;
        }
        
        @Override
        public boolean visit(final MarkerAnnotation annotation, final BlockScope scope) {
            return this.internalVisit(annotation);
        }
        
        @Override
        public boolean visit(final NormalAnnotation annotation, final BlockScope scope) {
            return this.internalVisit(annotation);
        }
        
        @Override
        public boolean visit(final SingleMemberAnnotation annotation, final BlockScope scope) {
            return this.internalVisit(annotation);
        }
        
        @Override
        public boolean visit(final Wildcard wildcard, final BlockScope scope) {
            this.currentWildcard = wildcard;
            return true;
        }
        
        @Override
        public boolean visit(final Argument argument, final BlockScope scope) {
            if ((argument.bits & 0x20000000) == 0x0) {
                return true;
            }
            for (int i = 0, max = this.localVariable.initializationCount; i < max; ++i) {
                final int startPC = this.localVariable.initializationPCs[i << 1];
                final int endPC = this.localVariable.initializationPCs[(i << 1) + 1];
                if (startPC != endPC) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean visit(final Argument argument, final ClassScope scope) {
            if ((argument.bits & 0x20000000) == 0x0) {
                return true;
            }
            for (int i = 0, max = this.localVariable.initializationCount; i < max; ++i) {
                final int startPC = this.localVariable.initializationPCs[i << 1];
                final int endPC = this.localVariable.initializationPCs[(i << 1) + 1];
                if (startPC != endPC) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean visit(final LocalDeclaration localDeclaration, final BlockScope scope) {
            for (int i = 0, max = this.localVariable.initializationCount; i < max; ++i) {
                final int startPC = this.localVariable.initializationPCs[i << 1];
                final int endPC = this.localVariable.initializationPCs[(i << 1) + 1];
                if (startPC != endPC) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public void endVisit(final Wildcard wildcard, final BlockScope scope) {
            this.currentWildcard = null;
        }
    }
}
