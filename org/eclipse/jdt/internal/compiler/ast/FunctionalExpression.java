package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBindingVisitor;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ImplicitNullAnnotationVerifier;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public abstract class FunctionalExpression extends Expression
{
    protected TypeBinding expectedType;
    public MethodBinding descriptor;
    public MethodBinding binding;
    protected MethodBinding actualMethodBinding;
    boolean ignoreFurtherInvestigation;
    protected ExpressionContext expressionContext;
    public CompilationResult compilationResult;
    public BlockScope enclosingScope;
    public int bootstrapMethodNumber;
    public boolean shouldCaptureInstance;
    protected static IErrorHandlingPolicy silentErrorHandlingPolicy;
    private boolean hasReportedSamProblem;
    public boolean isSerializable;
    
    static {
        FunctionalExpression.silentErrorHandlingPolicy = DefaultErrorHandlingPolicies.ignoreAllProblems();
    }
    
    public FunctionalExpression(final CompilationResult compilationResult) {
        this.expressionContext = ExpressionContext.VANILLA_CONTEXT;
        this.bootstrapMethodNumber = -1;
        this.shouldCaptureInstance = false;
        this.hasReportedSamProblem = false;
        this.compilationResult = compilationResult;
    }
    
    public FunctionalExpression() {
        this.expressionContext = ExpressionContext.VANILLA_CONTEXT;
        this.bootstrapMethodNumber = -1;
        this.shouldCaptureInstance = false;
        this.hasReportedSamProblem = false;
    }
    
    @Override
    public boolean isBoxingCompatibleWith(final TypeBinding targetType, final Scope scope) {
        return false;
    }
    
    public void setCompilationResult(final CompilationResult compilationResult) {
        this.compilationResult = compilationResult;
    }
    
    public MethodBinding getMethodBinding() {
        return null;
    }
    
    @Override
    public void setExpectedType(final TypeBinding expectedType) {
        this.expectedType = expectedType;
    }
    
    @Override
    public void setExpressionContext(final ExpressionContext context) {
        this.expressionContext = context;
    }
    
    @Override
    public ExpressionContext getExpressionContext() {
        return this.expressionContext;
    }
    
    @Override
    public boolean isPolyExpression(final MethodBinding candidate) {
        return true;
    }
    
    @Override
    public boolean isPolyExpression() {
        return true;
    }
    
    @Override
    public boolean isFunctionalType() {
        return true;
    }
    
    @Override
    public boolean isPertinentToApplicability(final TypeBinding targetType, final MethodBinding method) {
        if (targetType instanceof TypeVariableBinding) {
            final TypeVariableBinding typeVariable = (TypeVariableBinding)targetType;
            if (method != null) {
                if (typeVariable.declaringElement == method) {
                    return false;
                }
                if (method.isConstructor() && typeVariable.declaringElement == method.declaringClass) {
                    return false;
                }
            }
            else if (typeVariable.declaringElement instanceof MethodBinding) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public TypeBinding invocationTargetType() {
        if (this.expectedType == null) {
            return null;
        }
        final MethodBinding sam = this.expectedType.getSingleAbstractMethod(this.enclosingScope, true);
        if (sam == null || sam.problemId() == 17) {
            return null;
        }
        if (sam.isConstructor()) {
            return sam.declaringClass;
        }
        return sam.returnType;
    }
    
    @Override
    public TypeBinding expectedType() {
        return this.expectedType;
    }
    
    public boolean argumentsTypeElided() {
        return true;
    }
    
    public int recordFunctionalType(Scope scope) {
        while (scope != null) {
            switch (scope.kind) {
                case 2: {
                    final ReferenceContext context = ((MethodScope)scope).referenceContext;
                    if (!(context instanceof LambdaExpression)) {
                        break;
                    }
                    final LambdaExpression expression = (LambdaExpression)context;
                    if (expression != expression.original) {
                        return 0;
                    }
                    break;
                }
                case 4: {
                    final CompilationUnitDeclaration unit = ((CompilationUnitScope)scope).referenceContext;
                    return unit.record(this);
                }
            }
            scope = scope.parent;
        }
        return 0;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope blockScope) {
        return this.resolveType(blockScope, false);
    }
    
    public TypeBinding resolveType(final BlockScope blockScope, final boolean skipKosherCheck) {
        this.constant = Constant.NotAConstant;
        this.enclosingScope = blockScope;
        final MethodBinding sam = (this.expectedType == null) ? null : this.expectedType.getSingleAbstractMethod(blockScope, this.argumentsTypeElided());
        if (sam == null) {
            blockScope.problemReporter().targetTypeIsNotAFunctionalInterface(this);
            return null;
        }
        if (!sam.isValidBinding()) {
            return this.reportSamProblem(blockScope, sam);
        }
        this.descriptor = sam;
        if (skipKosherCheck || this.kosherDescriptor(blockScope, sam, true)) {
            if (this.expectedType instanceof IntersectionTypeBinding18) {
                final ReferenceBinding[] intersectingTypes = ((IntersectionTypeBinding18)this.expectedType).intersectingTypes;
                for (int t = 0, max = intersectingTypes.length; t < max; ++t) {
                    if (intersectingTypes[t].findSuperTypeOriginatingFrom(37, false) != null) {
                        this.isSerializable = true;
                        break;
                    }
                }
            }
            else if (this.expectedType.findSuperTypeOriginatingFrom(37, false) != null) {
                this.isSerializable = true;
            }
            final LookupEnvironment environment = blockScope.environment();
            if (environment.globalOptions.isAnnotationBasedNullAnalysisEnabled) {
                if ((sam.tagBits & 0x1000L) == 0x0L) {
                    new ImplicitNullAnnotationVerifier(environment, environment.globalOptions.inheritNullAnnotations).checkImplicitNullAnnotations(sam, null, false, blockScope);
                }
                NullAnnotationMatching.checkForContradictions(sam, this, blockScope);
            }
            return this.resolvedType = this.expectedType;
        }
        return this.resolvedType = null;
    }
    
    protected TypeBinding reportSamProblem(final BlockScope blockScope, final MethodBinding sam) {
        if (this.hasReportedSamProblem) {
            return null;
        }
        switch (sam.problemId()) {
            case 17: {
                blockScope.problemReporter().targetTypeIsNotAFunctionalInterface(this);
                this.hasReportedSamProblem = true;
                break;
            }
            case 18: {
                blockScope.problemReporter().illFormedParameterizationOfFunctionalInterface(this);
                this.hasReportedSamProblem = true;
                break;
            }
        }
        return null;
    }
    
    public boolean kosherDescriptor(final Scope scope, final MethodBinding sam, final boolean shouldChatter) {
        final VisibilityInspector inspector = new VisibilityInspector(this, scope, shouldChatter);
        boolean status = true;
        if (!inspector.visible(sam.returnType)) {
            status = false;
        }
        if (!inspector.visible(sam.parameters)) {
            status = false;
        }
        if (!inspector.visible(sam.thrownExceptions)) {
            status = false;
        }
        if (!inspector.visible(this.expectedType)) {
            status = false;
        }
        return status;
    }
    
    public int nullStatus(final FlowInfo flowInfo) {
        return 4;
    }
    
    public int diagnosticsSourceEnd() {
        return this.sourceEnd;
    }
    
    public MethodBinding[] getRequiredBridges() {
        ReferenceBinding functionalType;
        if (this.expectedType instanceof IntersectionTypeBinding18) {
            functionalType = (ReferenceBinding)((IntersectionTypeBinding18)this.expectedType).getSAMType(this.enclosingScope);
        }
        else {
            functionalType = (ReferenceBinding)this.expectedType;
        }
        class BridgeCollector
        {
            MethodBinding[] bridges;
            MethodBinding method = FunctionalExpression.this.descriptor;
            char[] selector;
            LookupEnvironment environment;
            Scope scope;
            
            BridgeCollector(final MethodBinding method) {
                this.selector = method.selector;
                this.environment = FunctionalExpression.this.enclosingScope.environment();
                this.scope = FunctionalExpression.this.enclosingScope;
                this.collectBridges(new ReferenceBinding[] { functionalType });
            }
            
            void collectBridges(final ReferenceBinding[] interfaces) {
                for (int length = (interfaces == null) ? 0 : interfaces.length, i = 0; i < length; ++i) {
                    final ReferenceBinding superInterface = interfaces[i];
                    if (superInterface != null) {
                        final MethodBinding[] methods = superInterface.getMethods(this.selector);
                        for (int j = 0, count = (methods == null) ? 0 : methods.length; j < count; ++j) {
                            MethodBinding inheritedMethod = methods[j];
                            if (inheritedMethod != null) {
                                if (this.method != inheritedMethod) {
                                    if (!inheritedMethod.isStatic()) {
                                        if (!inheritedMethod.redeclaresPublicObjectMethod(this.scope)) {
                                            inheritedMethod = MethodVerifier.computeSubstituteMethod(inheritedMethod, this.method, this.environment);
                                            if (inheritedMethod != null && MethodVerifier.isSubstituteParameterSubsignature(this.method, inheritedMethod, this.environment)) {
                                                if (MethodVerifier.areReturnTypesCompatible(this.method, inheritedMethod, this.environment)) {
                                                    final MethodBinding originalInherited = inheritedMethod.original();
                                                    final MethodBinding originalOverride = this.method.original();
                                                    if (!originalOverride.areParameterErasuresEqual(originalInherited) || TypeBinding.notEquals(originalOverride.returnType.erasure(), originalInherited.returnType.erasure())) {
                                                        this.add(originalInherited);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        this.collectBridges(superInterface.superInterfaces());
                    }
                }
            }
            
            void add(final MethodBinding inheritedMethod) {
                if (this.bridges == null) {
                    this.bridges = new MethodBinding[] { inheritedMethod };
                    return;
                }
                final int length = this.bridges.length;
                for (int i = 0; i < length; ++i) {
                    if (this.bridges[i].areParameterErasuresEqual(inheritedMethod) && TypeBinding.equalsEquals(this.bridges[i].returnType.erasure(), inheritedMethod.returnType.erasure())) {
                        return;
                    }
                }
                System.arraycopy(this.bridges, 0, this.bridges = new MethodBinding[length + 1], 0, length);
                this.bridges[length] = inheritedMethod;
            }
            
            MethodBinding[] getBridges() {
                return this.bridges;
            }
        }
        return new BridgeCollector(functionalType).getBridges();
    }
    
    boolean requiresBridges() {
        return this.getRequiredBridges() != null;
    }
    
    class VisibilityInspector extends TypeBindingVisitor
    {
        private Scope scope;
        private boolean shouldChatter;
        private boolean visible;
        private FunctionalExpression expression;
        
        public VisibilityInspector(final FunctionalExpression expression, final Scope scope, final boolean shouldChatter) {
            this.visible = true;
            this.scope = scope;
            this.shouldChatter = shouldChatter;
            this.expression = expression;
        }
        
        private void checkVisibility(final ReferenceBinding referenceBinding) {
            if (!referenceBinding.canBeSeenBy(this.scope)) {
                this.visible = false;
                if (this.shouldChatter) {
                    this.scope.problemReporter().descriptorHasInvisibleType(this.expression, referenceBinding);
                }
            }
        }
        
        @Override
        public boolean visit(final ReferenceBinding referenceBinding) {
            this.checkVisibility(referenceBinding);
            return true;
        }
        
        @Override
        public boolean visit(final ParameterizedTypeBinding parameterizedTypeBinding) {
            this.checkVisibility(parameterizedTypeBinding);
            return true;
        }
        
        @Override
        public boolean visit(final RawTypeBinding rawTypeBinding) {
            this.checkVisibility(rawTypeBinding);
            return true;
        }
        
        public boolean visible(final TypeBinding type) {
            TypeBindingVisitor.visit(this, type);
            return this.visible;
        }
        
        public boolean visible(final TypeBinding[] types) {
            TypeBindingVisitor.visit(this, types);
            return this.visible;
        }
    }
}
