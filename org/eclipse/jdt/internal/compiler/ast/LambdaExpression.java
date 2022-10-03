package org.eclipse.jdt.internal.compiler.ast;

import java.util.LinkedHashSet;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.AbortType;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.ExceptionInferenceFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.PolyTypeBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.IntersectionTypeBinding18;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import java.util.HashMap;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;

public class LambdaExpression extends FunctionalExpression implements IPolyExpression, ReferenceContext, ProblemSeverities
{
    public Argument[] arguments;
    private TypeBinding[] argumentTypes;
    public int arrowPosition;
    public Statement body;
    public boolean hasParentheses;
    public MethodScope scope;
    boolean voidCompatible;
    boolean valueCompatible;
    boolean returnsValue;
    private boolean requiresGenericSignature;
    boolean returnsVoid;
    public LambdaExpression original;
    public SyntheticArgumentBinding[] outerLocalVariables;
    private int outerLocalVariablesSlotSize;
    private boolean assistNode;
    private boolean hasIgnoredMandatoryErrors;
    private ReferenceBinding classType;
    public int ordinal;
    private Set thrownExceptions;
    public char[] text;
    private static final SyntheticArgumentBinding[] NO_SYNTHETIC_ARGUMENTS;
    private static final Block NO_BODY;
    private HashMap<TypeBinding, LambdaExpression> copiesPerTargetType;
    protected Expression[] resultExpressions;
    public InferenceContext18 inferenceContext;
    
    static {
        NO_SYNTHETIC_ARGUMENTS = new SyntheticArgumentBinding[0];
        NO_BODY = new Block(0);
    }
    
    public LambdaExpression(final CompilationResult compilationResult, final boolean assistNode, final boolean requiresGenericSignature) {
        super(compilationResult);
        this.voidCompatible = true;
        this.valueCompatible = false;
        this.original = this;
        this.outerLocalVariables = LambdaExpression.NO_SYNTHETIC_ARGUMENTS;
        this.outerLocalVariablesSlotSize = 0;
        this.assistNode = false;
        this.hasIgnoredMandatoryErrors = false;
        this.resultExpressions = LambdaExpression.NO_EXPRESSIONS;
        this.assistNode = assistNode;
        this.requiresGenericSignature = requiresGenericSignature;
        this.setArguments(LambdaExpression.NO_ARGUMENTS);
        this.setBody(LambdaExpression.NO_BODY);
    }
    
    public LambdaExpression(final CompilationResult compilationResult, final boolean assistNode) {
        this(compilationResult, assistNode, false);
    }
    
    public void setArguments(final Argument[] arguments) {
        this.arguments = ((arguments != null) ? arguments : ASTNode.NO_ARGUMENTS);
        this.argumentTypes = new TypeBinding[(arguments != null) ? arguments.length : 0];
    }
    
    public Argument[] arguments() {
        return this.arguments;
    }
    
    public TypeBinding[] argumentTypes() {
        return this.argumentTypes;
    }
    
    public void setBody(final Statement body) {
        this.body = ((body == null) ? LambdaExpression.NO_BODY : body);
    }
    
    public Statement body() {
        return this.body;
    }
    
    public Expression[] resultExpressions() {
        return this.resultExpressions;
    }
    
    public void setArrowPosition(final int arrowPosition) {
        this.arrowPosition = arrowPosition;
    }
    
    public int arrowPosition() {
        return this.arrowPosition;
    }
    
    protected FunctionalExpression original() {
        return this.original;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        if (this.shouldCaptureInstance) {
            final MethodBinding binding = this.binding;
            binding.modifiers &= 0xFFFFFFF7;
        }
        else {
            final MethodBinding binding2 = this.binding;
            binding2.modifiers |= 0x8;
        }
        final SourceTypeBinding sourceType = currentScope.enclosingSourceType();
        final boolean firstSpill = !(this.binding instanceof SyntheticMethodBinding);
        this.binding = sourceType.addSyntheticMethod(this);
        final int pc = codeStream.position;
        final StringBuffer signature = new StringBuffer();
        signature.append('(');
        if (this.shouldCaptureInstance) {
            codeStream.aload_0();
            signature.append(sourceType.signature());
        }
        for (int i = 0, length = (this.outerLocalVariables == null) ? 0 : this.outerLocalVariables.length; i < length; ++i) {
            final SyntheticArgumentBinding syntheticArgument = this.outerLocalVariables[i];
            if (this.shouldCaptureInstance && firstSpill) {
                final SyntheticArgumentBinding syntheticArgumentBinding = syntheticArgument;
                ++syntheticArgumentBinding.resolvedPosition;
            }
            signature.append(syntheticArgument.type.signature());
            final LocalVariableBinding capturedOuterLocal = syntheticArgument.actualOuterLocalVariable;
            final VariableBinding[] path = currentScope.getEmulationPath(capturedOuterLocal);
            codeStream.generateOuterAccess(path, this, capturedOuterLocal, currentScope);
        }
        signature.append(')');
        if (this.expectedType instanceof IntersectionTypeBinding18) {
            signature.append(((IntersectionTypeBinding18)this.expectedType).getSAMType(currentScope).signature());
        }
        else {
            signature.append(this.expectedType.signature());
        }
        final int invokeDynamicNumber = codeStream.classFile.recordBootstrapMethod(this);
        codeStream.invokeDynamic(invokeDynamicNumber, (this.shouldCaptureInstance ? 1 : 0) + this.outerLocalVariablesSlotSize, 1, this.descriptor.selector, signature.toString().toCharArray());
        if (!valueRequired) {
            codeStream.pop();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public boolean kosherDescriptor(final Scope currentScope, final MethodBinding sam, final boolean shouldChatter) {
        if (sam.typeVariables != Binding.NO_TYPE_VARIABLES) {
            if (shouldChatter) {
                currentScope.problemReporter().lambdaExpressionCannotImplementGenericMethod(this, sam);
            }
            return false;
        }
        return super.kosherDescriptor(currentScope, sam, shouldChatter);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope blockScope, final boolean skipKosherCheck) {
        final boolean argumentsTypeElided = this.argumentsTypeElided();
        final int argumentsLength = (this.arguments == null) ? 0 : this.arguments.length;
        if (this.constant != Constant.NotAConstant) {
            this.constant = Constant.NotAConstant;
            this.enclosingScope = blockScope;
            if (this.original == this) {
                this.ordinal = this.recordFunctionalType(blockScope);
            }
            if (!argumentsTypeElided) {
                for (int i = 0; i < argumentsLength; ++i) {
                    this.argumentTypes[i] = this.arguments[i].type.resolveType(blockScope, true);
                }
            }
            if (this.expectedType == null && this.expressionContext == ExpressionContext.INVOCATION_CONTEXT) {
                return new PolyTypeBinding(this);
            }
        }
        final MethodScope methodScope = blockScope.methodScope();
        this.scope = new MethodScope(blockScope, this, methodScope.isStatic, methodScope.lastVisibleFieldID);
        this.scope.isConstructorCall = methodScope.isConstructorCall;
        super.resolveType(blockScope, skipKosherCheck);
        final boolean haveDescriptor = this.descriptor != null;
        if (!skipKosherCheck && (!haveDescriptor || this.descriptor.typeVariables != Binding.NO_TYPE_VARIABLES)) {
            return this.resolvedType = null;
        }
        this.binding = new MethodBinding(33558530, CharOperation.concat(TypeConstants.ANONYMOUS_METHOD, Integer.toString(this.ordinal).toCharArray()), haveDescriptor ? this.descriptor.returnType : TypeBinding.VOID, Binding.NO_PARAMETERS, haveDescriptor ? this.descriptor.thrownExceptions : Binding.NO_EXCEPTIONS, blockScope.enclosingSourceType());
        this.binding.typeVariables = Binding.NO_TYPE_VARIABLES;
        boolean argumentsHaveErrors = false;
        if (haveDescriptor) {
            final int parametersLength = this.descriptor.parameters.length;
            if (parametersLength != argumentsLength) {
                this.scope.problemReporter().lambdaSignatureMismatched(this);
                if (argumentsTypeElided || this.original != this) {
                    return this.resolvedType = null;
                }
                this.resolvedType = null;
                argumentsHaveErrors = true;
            }
        }
        final TypeBinding[] newParameters = new TypeBinding[argumentsLength];
        AnnotationBinding[][] parameterAnnotations = null;
        for (int j = 0; j < argumentsLength; ++j) {
            final Argument argument = this.arguments[j];
            if (argument.isVarArgs()) {
                if (j == argumentsLength - 1) {
                    final MethodBinding binding = this.binding;
                    binding.modifiers |= 0x80;
                }
                else {
                    this.scope.problemReporter().illegalVarargInLambda(argument);
                    argumentsHaveErrors = true;
                }
            }
            final TypeBinding expectedParameterType = (haveDescriptor && j < this.descriptor.parameters.length) ? this.descriptor.parameters[j] : null;
            final TypeBinding argumentType = argumentsTypeElided ? expectedParameterType : this.argumentTypes[j];
            if (argumentType == null) {
                argumentsHaveErrors = true;
            }
            else if (argumentType == TypeBinding.VOID) {
                this.scope.problemReporter().argumentTypeCannotBeVoid(this, argument);
                argumentsHaveErrors = true;
            }
            else {
                if (!argumentType.isValidBinding()) {
                    final MethodBinding binding2 = this.binding;
                    binding2.tagBits |= 0x200L;
                }
                if ((argumentType.tagBits & 0x80L) != 0x0L) {
                    final MethodBinding binding3 = this.binding;
                    binding3.tagBits |= 0x80L;
                }
            }
        }
        if (!argumentsTypeElided && !argumentsHaveErrors) {
            ReferenceBinding groundType = null;
            ReferenceBinding expectedSAMType = null;
            if (this.expectedType instanceof IntersectionTypeBinding18) {
                expectedSAMType = (ReferenceBinding)((IntersectionTypeBinding18)this.expectedType).getSAMType(blockScope);
            }
            else if (this.expectedType instanceof ReferenceBinding) {
                expectedSAMType = (ReferenceBinding)this.expectedType;
            }
            if (expectedSAMType != null) {
                groundType = this.findGroundTargetType(blockScope, expectedSAMType, argumentsTypeElided);
            }
            if (groundType != null) {
                this.descriptor = groundType.getSingleAbstractMethod(blockScope, true);
                if (!this.descriptor.isValidBinding()) {
                    this.reportSamProblem(blockScope, this.descriptor);
                }
                else {
                    if (groundType != expectedSAMType && !groundType.isCompatibleWith(expectedSAMType, this.scope)) {
                        blockScope.problemReporter().typeMismatchError(groundType, this.expectedType, this, null);
                        return this.resolvedType = null;
                    }
                    this.resolvedType = groundType;
                }
            }
        }
        final boolean genericSignatureNeeded = this.requiresGenericSignature || blockScope.compilerOptions().generateGenericSignatureForLambdaExpressions;
        for (int k = 0; k < argumentsLength; ++k) {
            final Argument argument2 = this.arguments[k];
            final TypeBinding expectedParameterType2 = (haveDescriptor && k < this.descriptor.parameters.length) ? this.descriptor.parameters[k] : null;
            final TypeBinding argumentType2 = argumentsTypeElided ? expectedParameterType2 : this.argumentTypes[k];
            if (argumentType2 != null && argumentType2 != TypeBinding.VOID) {
                if (haveDescriptor && expectedParameterType2 != null && argumentType2.isValidBinding() && TypeBinding.notEquals(argumentType2, expectedParameterType2) && expectedParameterType2.isProperType(true)) {
                    this.scope.problemReporter().lambdaParameterTypeMismatched(argument2, argument2.type, expectedParameterType2);
                    this.resolvedType = null;
                }
                if (genericSignatureNeeded) {
                    final TypeBinding leafType = argumentType2.leafComponentType();
                    if (leafType instanceof ReferenceBinding && (((ReferenceBinding)leafType).modifiers & 0x40000000) != 0x0) {
                        final MethodBinding binding4 = this.binding;
                        binding4.modifiers |= 0x40000000;
                    }
                }
                newParameters[k] = argument2.bind(this.scope, argumentType2, false);
                if (argument2.annotations != null) {
                    final MethodBinding binding5 = this.binding;
                    binding5.tagBits |= 0x400L;
                    if (parameterAnnotations == null) {
                        parameterAnnotations = new AnnotationBinding[argumentsLength][];
                        for (int l = 0; l < k; ++l) {
                            parameterAnnotations[l] = Binding.NO_ANNOTATIONS;
                        }
                    }
                    parameterAnnotations[k] = argument2.binding.getAnnotations();
                }
                else if (parameterAnnotations != null) {
                    parameterAnnotations[k] = Binding.NO_ANNOTATIONS;
                }
            }
        }
        if (!argumentsHaveErrors) {
            this.binding.parameters = newParameters;
            if (parameterAnnotations != null) {
                this.binding.setParameterAnnotations(parameterAnnotations);
            }
        }
        if (!argumentsTypeElided && !argumentsHaveErrors && this.binding.isVarargs() && !this.binding.parameters[this.binding.parameters.length - 1].isReifiable()) {
            this.scope.problemReporter().possibleHeapPollutionFromVararg(this.arguments[this.arguments.length - 1]);
        }
        for (final ReferenceBinding exception : this.binding.thrownExceptions) {
            if ((exception.tagBits & 0x80L) != 0x0L) {
                final MethodBinding binding6 = this.binding;
                binding6.tagBits |= 0x80L;
            }
            if (genericSignatureNeeded) {
                final MethodBinding binding7 = this.binding;
                binding7.modifiers |= (exception.modifiers & 0x40000000);
            }
        }
        final TypeBinding returnType = this.binding.returnType;
        if (returnType != null) {
            if ((returnType.tagBits & 0x80L) != 0x0L) {
                final MethodBinding binding8 = this.binding;
                binding8.tagBits |= 0x80L;
            }
            if (genericSignatureNeeded) {
                final TypeBinding leafType2 = returnType.leafComponentType();
                if (leafType2 instanceof ReferenceBinding && (((ReferenceBinding)leafType2).modifiers & 0x40000000) != 0x0) {
                    final MethodBinding binding9 = this.binding;
                    binding9.modifiers |= 0x40000000;
                }
            }
        }
        if (haveDescriptor && !argumentsHaveErrors && blockScope.compilerOptions().isAnnotationBasedNullAnalysisEnabled) {
            if (!argumentsTypeElided) {
                AbstractMethodDeclaration.createArgumentBindings(this.arguments, this.binding, this.scope);
                this.mergeParameterNullAnnotations(blockScope);
            }
            final MethodBinding binding10 = this.binding;
            binding10.tagBits |= (this.descriptor.tagBits & 0x180000000000000L);
        }
        final MethodBinding binding11 = this.binding;
        binding11.modifiers &= 0xFDFFFFFF;
        if (this.body instanceof Expression) {
            final Expression expression = (Expression)this.body;
            new ReturnStatement(expression, expression.sourceStart, expression.sourceEnd, true).resolve(this.scope);
            if (expression.resolvedType == TypeBinding.VOID && !expression.statementExpression()) {
                this.scope.problemReporter().invalidExpressionAsStatement(expression);
            }
        }
        else {
            this.body.resolve(this.scope);
            if (!this.returnsVoid && !this.returnsValue) {
                this.valueCompatible = this.body.doesNotCompleteNormally();
            }
        }
        if ((this.binding.tagBits & 0x80L) != 0x0L) {
            this.scope.problemReporter().missingTypeInLambda(this, this.binding);
        }
        if (this.shouldCaptureInstance && this.scope.isConstructorCall) {
            this.scope.problemReporter().fieldsOrThisBeforeConstructorInvocation(this);
        }
        return argumentsHaveErrors ? (this.resolvedType = null) : this.resolvedType;
    }
    
    private ReferenceBinding findGroundTargetType(final BlockScope blockScope, TypeBinding targetType, final boolean argumentTypesElided) {
        if (targetType instanceof IntersectionTypeBinding18) {
            targetType = ((IntersectionTypeBinding18)targetType).getSAMType(blockScope);
        }
        if (!(targetType instanceof ReferenceBinding) || !targetType.isValidBinding()) {
            return null;
        }
        final ParameterizedTypeBinding withWildCards = InferenceContext18.parameterizedWithWildcard(targetType);
        if (withWildCards != null) {
            if (!argumentTypesElided) {
                final InferenceContext18 freshInferenceContext = new InferenceContext18(blockScope);
                try {
                    return freshInferenceContext.inferFunctionalInterfaceParameterization(this, blockScope, withWildCards);
                }
                finally {
                    freshInferenceContext.cleanUp();
                }
            }
            return this.findGroundTargetTypeForElidedLambda(blockScope, withWildCards);
        }
        return (ReferenceBinding)targetType;
    }
    
    public ReferenceBinding findGroundTargetTypeForElidedLambda(final BlockScope blockScope, final ParameterizedTypeBinding withWildCards) {
        final TypeBinding[] types = withWildCards.getNonWildcardParameterization(blockScope);
        if (types == null) {
            return null;
        }
        final ReferenceBinding genericType = withWildCards.genericType();
        return blockScope.environment().createParameterizedType(genericType, types, withWildCards.enclosingType());
    }
    
    @Override
    public boolean argumentsTypeElided() {
        return this.arguments.length > 0 && this.arguments[0].hasElidedType();
    }
    
    private void analyzeExceptions() {
        try {
            final ExceptionHandlingFlowContext ehfc;
            this.body.analyseCode(this.scope, ehfc = new ExceptionInferenceFlowContext(null, this, Binding.NO_EXCEPTIONS, null, this.scope, FlowInfo.DEAD_END), UnconditionalFlowInfo.fakeInitializedFlowInfo(this.scope.outerMostMethodScope().analysisIndex, this.scope.referenceType().maxFieldCount));
            this.thrownExceptions = ((ehfc.extendedExceptions == null) ? Collections.emptySet() : new HashSet<Object>(ehfc.extendedExceptions));
        }
        catch (final Exception ex) {}
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        if (this.ignoreFurtherInvestigation) {
            return flowInfo;
        }
        FlowInfo lambdaInfo = flowInfo.copy();
        final ExceptionHandlingFlowContext methodContext = new ExceptionHandlingFlowContext(flowContext, this, this.binding.thrownExceptions, flowContext.getInitializationContext(), this.scope, FlowInfo.DEAD_END);
        final MethodBinding methodWithParameterDeclaration = this.argumentsTypeElided() ? this.descriptor : this.binding;
        AbstractMethodDeclaration.analyseArguments(currentScope.environment(), lambdaInfo, this.arguments, methodWithParameterDeclaration);
        if (this.arguments != null) {
            for (int i = 0, count = this.arguments.length; i < count; ++i) {
                this.bits |= (this.arguments[i].bits & 0x100000);
            }
        }
        lambdaInfo = this.body.analyseCode(this.scope, methodContext, lambdaInfo);
        if (this.body instanceof Block) {
            final TypeBinding returnTypeBinding = this.expectedResultType();
            if (returnTypeBinding == TypeBinding.VOID) {
                if ((lambdaInfo.tagBits & 0x1) == 0x0 || ((Block)this.body).statements == null) {
                    this.bits |= 0x40;
                }
            }
            else if (lambdaInfo != FlowInfo.DEAD_END) {
                this.scope.problemReporter().shouldReturn(returnTypeBinding, this);
            }
        }
        else if (currentScope.compilerOptions().isAnnotationBasedNullAnalysisEnabled && lambdaInfo.reachMode() == 0) {
            final Expression expression = (Expression)this.body;
            this.checkAgainstNullAnnotation(flowContext, expression, flowInfo, expression.nullStatus(lambdaInfo, flowContext));
        }
        return flowInfo;
    }
    
    void validateNullAnnotations() {
        if (this.binding != null) {
            for (int length = this.binding.parameters.length, i = 0; i < length; ++i) {
                if (!this.scope.validateNullAnnotation(this.binding.returnType.tagBits, this.arguments[i].type, this.arguments[i].annotations)) {
                    this.binding.returnType = this.binding.returnType.withoutToplevelNullAnnotation();
                }
            }
        }
    }
    
    private void mergeParameterNullAnnotations(final BlockScope currentScope) {
        final LookupEnvironment env = currentScope.environment();
        final TypeBinding[] ourParameters = this.binding.parameters;
        final TypeBinding[] descParameters = this.descriptor.parameters;
        for (int len = Math.min(ourParameters.length, descParameters.length), i = 0; i < len; ++i) {
            final long ourTagBits = ourParameters[i].tagBits & 0x180000000000000L;
            final long descTagBits = descParameters[i].tagBits & 0x180000000000000L;
            if (ourTagBits == 0L) {
                if (descTagBits != 0L && !ourParameters[i].isBaseType()) {
                    final AnnotationBinding[] annotations = descParameters[i].getTypeAnnotations();
                    for (int j = 0, length = annotations.length; j < length; ++j) {
                        final AnnotationBinding annotation = annotations[j];
                        if (annotation != null && annotation.getAnnotationType().hasNullBit(96)) {
                            ourParameters[i] = env.createAnnotatedType(ourParameters[i], new AnnotationBinding[] { annotation });
                        }
                    }
                }
            }
            else if (ourTagBits != descTagBits && ourTagBits == 72057594037927936L) {
                char[][] inheritedAnnotationName = null;
                if (descTagBits == 36028797018963968L) {
                    inheritedAnnotationName = env.getNullableAnnotationName();
                }
                currentScope.problemReporter().illegalRedefinitionToNonNullParameter(this.arguments[i], this.descriptor.declaringClass, inheritedAnnotationName);
            }
        }
    }
    
    void checkAgainstNullAnnotation(final FlowContext flowContext, final Expression expression, final FlowInfo flowInfo, final int nullStatus) {
        if (nullStatus != 4 && (this.descriptor.returnType.tagBits & 0x100000000000000L) != 0x0L) {
            flowContext.recordNullityMismatch(this.scope, expression, expression.resolvedType, this.descriptor.returnType, flowInfo, nullStatus, null);
        }
    }
    
    @Override
    public boolean isPertinentToApplicability(final TypeBinding targetType, final MethodBinding method) {
        if (targetType == null) {
            return true;
        }
        if (this.argumentsTypeElided()) {
            return false;
        }
        if (!super.isPertinentToApplicability(targetType, method)) {
            return false;
        }
        if (this.body instanceof Expression) {
            if (!((Expression)this.body).isPertinentToApplicability(targetType, method)) {
                return false;
            }
        }
        else {
            final Expression[] returnExpressions = this.resultExpressions;
            if (returnExpressions != LambdaExpression.NO_EXPRESSIONS) {
                for (int i = 0, length = returnExpressions.length; i < length; ++i) {
                    if (!returnExpressions[i].isPertinentToApplicability(targetType, method)) {
                        return false;
                    }
                }
            }
            else {
                class NotPertientToApplicability extends RuntimeException
                {
                    private static final long serialVersionUID = 1L;
                    final /* synthetic */ LambdaExpression this$0 = LambdaExpression.this;
                }
                try {
                    class ResultsAnalyser extends ASTVisitor
                    {
                        @Override
                        public boolean visit(final TypeDeclaration type, final BlockScope skope) {
                            return false;
                        }
                        
                        @Override
                        public boolean visit(final TypeDeclaration type, final ClassScope skope) {
                            return false;
                        }
                        
                        @Override
                        public boolean visit(final LambdaExpression type, final BlockScope skope) {
                            return false;
                        }
                        
                        @Override
                        public boolean visit(final ReturnStatement returnStatement, final BlockScope skope) {
                            if (returnStatement.expression != null && !returnStatement.expression.isPertinentToApplicability(targetType, method)) {
                                throw new NotPertientToApplicability();
                            }
                            return false;
                        }
                    }
                    this.body.traverse(new ResultsAnalyser(), this.scope);
                }
                catch (final NotPertientToApplicability notPertientToApplicability) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isVoidCompatible() {
        return this.voidCompatible;
    }
    
    public boolean isValueCompatible() {
        return this.valueCompatible;
    }
    
    @Override
    public StringBuffer printExpression(final int tab, final StringBuffer output) {
        return this.printExpression(tab, output, false);
    }
    
    public StringBuffer printExpression(final int tab, final StringBuffer output, final boolean makeShort) {
        final int parenthesesCount = (this.bits & 0x1FE00000) >> 21;
        String suffix = "";
        for (int i = 0; i < parenthesesCount; ++i) {
            output.append('(');
            suffix = String.valueOf(suffix) + ')';
        }
        output.append('(');
        if (this.arguments != null) {
            for (int i = 0; i < this.arguments.length; ++i) {
                if (i > 0) {
                    output.append(", ");
                }
                this.arguments[i].print(0, output);
            }
        }
        output.append(") -> ");
        if (makeShort) {
            output.append("{}");
        }
        else if (this.body != null) {
            this.body.print((this.body instanceof Block) ? tab : 0, output);
        }
        else {
            output.append("<@incubator>");
        }
        return output.append(suffix);
    }
    
    public TypeBinding expectedResultType() {
        return (this.descriptor != null && this.descriptor.isValidBinding()) ? this.descriptor.returnType : null;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            if (this.arguments != null) {
                for (int argumentsLength = this.arguments.length, i = 0; i < argumentsLength; ++i) {
                    this.arguments[i].traverse(visitor, this.scope);
                }
            }
            if (this.body != null) {
                this.body.traverse(visitor, this.scope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    public MethodScope getScope() {
        return this.scope;
    }
    
    private boolean enclosingScopesHaveErrors() {
        for (Scope skope = this.enclosingScope; skope != null; skope = skope.parent) {
            final ReferenceContext context = skope.referenceContext();
            if (context != null && context.hasErrors()) {
                return true;
            }
        }
        return false;
    }
    
    private void analyzeShape() {
        if (this.body instanceof Expression) {
            this.voidCompatible = (this.assistNode || ((Expression)this.body).statementExpression());
            this.valueCompatible = true;
        }
        else {
            if (this.assistNode) {
                this.voidCompatible = true;
                this.valueCompatible = true;
            }
            class ShapeComputer extends ASTVisitor
            {
                @Override
                public boolean visit(final TypeDeclaration type, final BlockScope skope) {
                    return false;
                }
                
                @Override
                public boolean visit(final TypeDeclaration type, final ClassScope skope) {
                    return false;
                }
                
                @Override
                public boolean visit(final LambdaExpression type, final BlockScope skope) {
                    return false;
                }
                
                @Override
                public boolean visit(final ReturnStatement returnStatement, final BlockScope skope) {
                    if (returnStatement.expression != null) {
                        LambdaExpression.this.valueCompatible = true;
                        LambdaExpression.this.voidCompatible = false;
                        LambdaExpression.this.returnsValue = true;
                    }
                    else {
                        LambdaExpression.this.voidCompatible = true;
                        LambdaExpression.this.valueCompatible = false;
                        LambdaExpression.this.returnsVoid = true;
                    }
                    return false;
                }
            }
            this.body.traverse(new ShapeComputer(), null);
            if (!this.returnsValue && !this.returnsVoid) {
                this.valueCompatible = this.body.doesNotCompleteNormally();
            }
        }
    }
    
    @Override
    public boolean isPotentiallyCompatibleWith(final TypeBinding targetType, final Scope skope) {
        if (!super.isPertinentToApplicability(targetType, null)) {
            return true;
        }
        final MethodBinding sam = targetType.getSingleAbstractMethod(skope, true);
        if (sam == null || !sam.isValidBinding()) {
            return false;
        }
        if (sam.parameters.length != this.arguments.length) {
            return false;
        }
        this.analyzeShape();
        if (sam.returnType.id == 6) {
            if (!this.voidCompatible) {
                return false;
            }
        }
        else if (!this.valueCompatible) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean isCompatibleWith(TypeBinding targetType, final Scope skope) {
        if (!super.isPertinentToApplicability(targetType, null)) {
            return true;
        }
        LambdaExpression copy = null;
        try {
            copy = this.cachedResolvedCopy(targetType, this.argumentsTypeElided(), false, null);
        }
        catch (final CopyFailureException ex) {
            return this.assistNode || !this.isPertinentToApplicability(targetType, null);
        }
        if (copy == null) {
            return false;
        }
        targetType = this.findGroundTargetType(this.enclosingScope, targetType, this.argumentsTypeElided());
        final MethodBinding sam = targetType.getSingleAbstractMethod(this.enclosingScope, true);
        if (sam == null || sam.problemId() == 17) {
            return false;
        }
        if (sam.returnType.id == 6) {
            if (!copy.voidCompatible) {
                return false;
            }
        }
        else if (!copy.valueCompatible) {
            return false;
        }
        if (!this.isPertinentToApplicability(targetType, null)) {
            return true;
        }
        if (!this.kosherDescriptor(this.enclosingScope, sam, false)) {
            return false;
        }
        final Expression[] returnExpressions = copy.resultExpressions;
        for (int i = 0, length = returnExpressions.length; i < length; ++i) {
            if (this.enclosingScope.parameterCompatibilityLevel(returnExpressions[i].resolvedType, sam.returnType) == -1 && !returnExpressions[i].isConstantValueOfTypeAssignableToType(returnExpressions[i].resolvedType, sam.returnType) && (sam.returnType.id != 6 || this.body instanceof Block)) {
                return false;
            }
        }
        return true;
    }
    
    private LambdaExpression cachedResolvedCopy(TypeBinding targetType, final boolean anyTargetOk, final boolean requireExceptionAnalysis, final InferenceContext18 context) {
        targetType = this.findGroundTargetType(this.enclosingScope, targetType, this.argumentsTypeElided());
        if (targetType == null) {
            return null;
        }
        final MethodBinding sam = targetType.getSingleAbstractMethod(this.enclosingScope, true);
        if (sam == null || !sam.isValidBinding()) {
            return null;
        }
        if (sam.parameters.length != this.arguments.length) {
            return null;
        }
        LambdaExpression copy = null;
        if (this.copiesPerTargetType != null) {
            copy = this.copiesPerTargetType.get(targetType);
            if (copy == null && anyTargetOk && this.copiesPerTargetType.values().size() > 0) {
                copy = this.copiesPerTargetType.values().iterator().next();
            }
        }
        final IErrorHandlingPolicy oldPolicy = this.enclosingScope.problemReporter().switchErrorHandlingPolicy(LambdaExpression.silentErrorHandlingPolicy);
        try {
            if (copy == null) {
                copy = this.copy();
                if (copy == null) {
                    throw new CopyFailureException();
                }
                copy.setExpressionContext(this.expressionContext);
                copy.setExpectedType(targetType);
                copy.inferenceContext = context;
                final TypeBinding type = copy.resolveType(this.enclosingScope, true);
                if (type == null || !type.isValidBinding()) {
                    return null;
                }
                if (this.copiesPerTargetType == null) {
                    this.copiesPerTargetType = new HashMap<TypeBinding, LambdaExpression>();
                }
                this.copiesPerTargetType.put(targetType, copy);
            }
            if (!requireExceptionAnalysis) {
                return copy;
            }
            if (copy.thrownExceptions == null && !copy.hasIgnoredMandatoryErrors && !this.enclosingScopesHaveErrors()) {
                copy.analyzeExceptions();
            }
            return copy;
        }
        finally {
            this.enclosingScope.problemReporter().switchErrorHandlingPolicy(oldPolicy);
        }
    }
    
    @Override
    public LambdaExpression resolveExpressionExpecting(final TypeBinding targetType, final Scope skope, final InferenceContext18 context) {
        LambdaExpression copy = null;
        try {
            copy = this.cachedResolvedCopy(targetType, false, true, context);
        }
        catch (final CopyFailureException ex) {
            return null;
        }
        return copy;
    }
    
    @Override
    public boolean sIsMoreSpecific(TypeBinding s, final TypeBinding t, final Scope skope) {
        if (super.sIsMoreSpecific(s, t, skope)) {
            return true;
        }
        if (this.argumentsTypeElided() || t.findSuperTypeOriginatingFrom(s) != null) {
            return false;
        }
        s = s.capture(this.enclosingScope, this.sourceStart, this.sourceEnd);
        final MethodBinding sSam = s.getSingleAbstractMethod(this.enclosingScope, true);
        if (sSam == null || !sSam.isValidBinding()) {
            return false;
        }
        final TypeBinding r1 = sSam.returnType;
        final MethodBinding tSam = t.getSingleAbstractMethod(this.enclosingScope, true);
        if (tSam == null || !tSam.isValidBinding()) {
            return true;
        }
        final TypeBinding r2 = tSam.returnType;
        if (r2.id == 6) {
            return true;
        }
        if (r1.id == 6) {
            return false;
        }
        if (r1.isCompatibleWith(r2, skope)) {
            return true;
        }
        final LambdaExpression copy = this.cachedResolvedCopy(s, true, false, null);
        final Expression[] returnExpressions = copy.resultExpressions;
        final int returnExpressionsLength = (returnExpressions == null) ? 0 : returnExpressions.length;
        if (returnExpressionsLength > 0) {
            if (r1.isBaseType() && !r2.isBaseType()) {
                int i;
                for (i = 0; i < returnExpressionsLength && !returnExpressions[i].isPolyExpression() && returnExpressions[i].resolvedType.isBaseType(); ++i) {}
                if (i == returnExpressionsLength) {
                    return true;
                }
            }
            if (!r1.isBaseType() && r2.isBaseType()) {
                int i;
                for (i = 0; i < returnExpressionsLength && !returnExpressions[i].resolvedType.isBaseType(); ++i) {}
                if (i == returnExpressionsLength) {
                    return true;
                }
            }
            if (r1.isFunctionalInterface(this.enclosingScope) && r2.isFunctionalInterface(this.enclosingScope)) {
                int i;
                for (i = 0; i < returnExpressionsLength; ++i) {
                    final Expression resultExpression = returnExpressions[i];
                    if (!resultExpression.sIsMoreSpecific(r1, r2, skope)) {
                        break;
                    }
                }
                if (i == returnExpressionsLength) {
                    return true;
                }
            }
        }
        return false;
    }
    
    LambdaExpression copy() {
        final Parser parser = new Parser(this.enclosingScope.problemReporter(), false);
        final ICompilationUnit compilationUnit = this.compilationResult.getCompilationUnit();
        final char[] source = (compilationUnit != null) ? compilationUnit.getContents() : this.text;
        final LambdaExpression copy = (LambdaExpression)parser.parseLambdaExpression(source, (compilationUnit != null) ? this.sourceStart : 0, this.sourceEnd - this.sourceStart + 1, this.enclosingScope.referenceCompilationUnit(), false);
        if (copy != null) {
            copy.original = this;
            copy.assistNode = this.assistNode;
            copy.enclosingScope = this.enclosingScope;
        }
        return copy;
    }
    
    public void returnsExpression(final Expression expression, final TypeBinding resultType) {
        if (this.original == this) {
            return;
        }
        if (this.body instanceof Expression) {
            this.valueCompatible = (resultType == null || resultType.id != 6);
            this.voidCompatible = (this.assistNode || ((Expression)this.body).statementExpression());
            this.resultExpressions = new Expression[] { expression };
            return;
        }
        if (expression != null) {
            this.returnsValue = true;
            this.voidCompatible = false;
            this.valueCompatible = !this.returnsVoid;
            Expression[] returnExpressions = this.resultExpressions;
            final int resultsLength = returnExpressions.length;
            System.arraycopy(returnExpressions, 0, returnExpressions = new Expression[resultsLength + 1], 0, resultsLength);
            returnExpressions[resultsLength] = expression;
            this.resultExpressions = returnExpressions;
        }
        else {
            this.returnsVoid = true;
            this.valueCompatible = false;
            this.voidCompatible = !this.returnsValue;
        }
    }
    
    @Override
    public CompilationResult compilationResult() {
        return this.compilationResult;
    }
    
    @Override
    public void abort(final int abortLevel, final CategorizedProblem problem) {
        switch (abortLevel) {
            case 2: {
                throw new AbortCompilation(this.compilationResult, problem);
            }
            case 4: {
                throw new AbortCompilationUnit(this.compilationResult, problem);
            }
            case 8: {
                throw new AbortType(this.compilationResult, problem);
            }
            default: {
                throw new AbortMethod(this.compilationResult, problem);
            }
        }
    }
    
    @Override
    public CompilationUnitDeclaration getCompilationUnitDeclaration() {
        return (this.enclosingScope == null) ? null : this.enclosingScope.compilationUnitScope().referenceContext;
    }
    
    @Override
    public boolean hasErrors() {
        return this.ignoreFurtherInvestigation;
    }
    
    @Override
    public void tagAsHavingErrors() {
        this.ignoreFurtherInvestigation = true;
        for (Scope parent = this.enclosingScope.parent; parent != null; parent = parent.parent) {
            switch (parent.kind) {
                case 2:
                case 3: {
                    final ReferenceContext parentAST = parent.referenceContext();
                    if (parentAST != this) {
                        parentAST.tagAsHavingErrors();
                        return;
                    }
                    break;
                }
            }
        }
    }
    
    @Override
    public void tagAsHavingIgnoredMandatoryErrors(final int problemId) {
        switch (problemId) {
            case 16777362:
            case 16777384:
            case 16778098: {
                return;
            }
            case 99:
            case 16777235:
            case 67108969:
            case 67109635:
            case 553648781:
            case 553648783:
            case 553648784:
            case 553648785:
            case 553648786:
            case 553648787:
            case 603979884: {
                return;
            }
            default: {
                this.hasIgnoredMandatoryErrors = true;
                for (MethodScope enclosingLambdaScope = (this.scope == null) ? null : this.scope.enclosingLambdaScope(); enclosingLambdaScope != null; enclosingLambdaScope = enclosingLambdaScope.enclosingLambdaScope()) {
                    final LambdaExpression enclosingLambda = (LambdaExpression)enclosingLambdaScope.referenceContext;
                    enclosingLambda.hasIgnoredMandatoryErrors = true;
                }
            }
        }
    }
    
    public Set<TypeBinding> getThrownExceptions() {
        if (this.thrownExceptions == null) {
            return Collections.emptySet();
        }
        return this.thrownExceptions;
    }
    
    public void generateCode(final ClassScope classScope, final ClassFile classFile) {
        int problemResetPC = 0;
        classFile.codeStream.wideMode = false;
        boolean restart = false;
        do {
            try {
                problemResetPC = classFile.contentsOffset;
                this.generateCode(classFile);
                restart = false;
            }
            catch (final AbortMethod e) {
                if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE) {
                    classFile.contentsOffset = problemResetPC;
                    --classFile.methodCount;
                    classFile.codeStream.resetInWideMode();
                    restart = true;
                }
                else {
                    if (e.compilationResult != CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE) {
                        throw new AbortType(this.compilationResult, e.problem);
                    }
                    classFile.contentsOffset = problemResetPC;
                    --classFile.methodCount;
                    classFile.codeStream.resetForCodeGenUnusedLocals();
                    restart = true;
                }
            }
        } while (restart);
    }
    
    public void generateCode(final ClassFile classFile) {
        classFile.generateMethodInfoHeader(this.binding);
        final int methodAttributeOffset = classFile.contentsOffset;
        int attributeNumber = classFile.generateMethodInfoAttributes(this.binding);
        final int codeAttributeOffset = classFile.contentsOffset;
        classFile.generateCodeAttributeHeader();
        final CodeStream codeStream = classFile.codeStream;
        codeStream.reset(this, classFile);
        this.scope.computeLocalVariablePositions(this.outerLocalVariablesSlotSize + (this.binding.isStatic() ? 0 : 1), codeStream);
        if (this.outerLocalVariables != null) {
            for (int i = 0, max = this.outerLocalVariables.length; i < max; ++i) {
                final LocalVariableBinding argBinding;
                codeStream.addVisibleLocalVariable(argBinding = this.outerLocalVariables[i]);
                codeStream.record(argBinding);
                argBinding.recordInitializationStartPC(0);
            }
        }
        if (this.arguments != null) {
            for (int i = 0, max = this.arguments.length; i < max; ++i) {
                final LocalVariableBinding argBinding;
                codeStream.addVisibleLocalVariable(argBinding = this.arguments[i].binding);
                argBinding.recordInitializationStartPC(0);
            }
        }
        if (this.body instanceof Block) {
            this.body.generateCode(this.scope, codeStream);
            if ((this.bits & 0x40) != 0x0) {
                codeStream.return_();
            }
        }
        else {
            final Expression expression = (Expression)this.body;
            expression.generateCode(this.scope, codeStream, true);
            if (this.binding.returnType == TypeBinding.VOID) {
                codeStream.return_();
            }
            else {
                codeStream.generateReturnBytecode(expression);
            }
        }
        codeStream.exitUserScope(this.scope);
        codeStream.recordPositionsFrom(0, this.sourceEnd);
        try {
            classFile.completeCodeAttribute(codeAttributeOffset);
        }
        catch (final NegativeArraySizeException ex) {
            throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, (CategorizedProblem)null);
        }
        ++attributeNumber;
        classFile.completeMethodInfo(this.binding, methodAttributeOffset, attributeNumber);
    }
    
    public void addSyntheticArgument(final LocalVariableBinding actualOuterLocalVariable) {
        if (this.original != this || this.binding == null) {
            return;
        }
        SyntheticArgumentBinding syntheticLocal = null;
        final int newSlot = this.outerLocalVariables.length;
        for (int i = 0; i < newSlot; ++i) {
            if (this.outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable) {
                return;
            }
        }
        System.arraycopy(this.outerLocalVariables, 0, this.outerLocalVariables = new SyntheticArgumentBinding[newSlot + 1], 0, newSlot);
        syntheticLocal = (this.outerLocalVariables[newSlot] = new SyntheticArgumentBinding(actualOuterLocalVariable));
        syntheticLocal.resolvedPosition = this.outerLocalVariablesSlotSize;
        syntheticLocal.declaringScope = this.scope;
        final int parameterCount = this.binding.parameters.length;
        final TypeBinding[] newParameters = new TypeBinding[parameterCount + 1];
        newParameters[newSlot] = actualOuterLocalVariable.type;
        for (int j = 0, k = 0; j < parameterCount; ++j, ++k) {
            if (j == newSlot) {
                ++k;
            }
            newParameters[k] = this.binding.parameters[j];
        }
        this.binding.parameters = newParameters;
        switch (syntheticLocal.type.id) {
            case 7:
            case 8: {
                this.outerLocalVariablesSlotSize += 2;
                break;
            }
            default: {
                ++this.outerLocalVariablesSlotSize;
                break;
            }
        }
    }
    
    public SyntheticArgumentBinding getSyntheticArgument(final LocalVariableBinding actualOuterLocalVariable) {
        for (int i = 0, length = (this.outerLocalVariables == null) ? 0 : this.outerLocalVariables.length; i < length; ++i) {
            if (this.outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable) {
                return this.outerLocalVariables[i];
            }
        }
        return null;
    }
    
    @Override
    public MethodBinding getMethodBinding() {
        if (this.actualMethodBinding == null) {
            if (this.binding != null) {
                TypeBinding[] newParams = null;
                if (this.binding instanceof SyntheticMethodBinding && this.outerLocalVariables.length > 0) {
                    newParams = new TypeBinding[this.binding.parameters.length - this.outerLocalVariables.length];
                    System.arraycopy(this.binding.parameters, this.outerLocalVariables.length, newParams, 0, newParams.length);
                }
                else {
                    newParams = this.binding.parameters;
                }
                this.actualMethodBinding = new MethodBinding(this.binding.modifiers, this.binding.selector, this.binding.returnType, newParams, this.binding.thrownExceptions, this.binding.declaringClass);
                this.actualMethodBinding.tagBits = this.binding.tagBits;
            }
            else {
                this.actualMethodBinding = new ProblemMethodBinding(CharOperation.NO_CHAR, null, 17);
            }
        }
        return this.actualMethodBinding;
    }
    
    @Override
    public int diagnosticsSourceEnd() {
        return (this.body instanceof Block) ? this.arrowPosition : this.sourceEnd;
    }
    
    public TypeBinding[] getMarkerInterfaces() {
        if (this.expectedType instanceof IntersectionTypeBinding18) {
            final Set markerBindings = new LinkedHashSet();
            final IntersectionTypeBinding18 intersectionType = (IntersectionTypeBinding18)this.expectedType;
            final TypeBinding[] intersectionTypes = intersectionType.intersectingTypes;
            final TypeBinding samType = intersectionType.getSAMType(this.enclosingScope);
            for (int i = 0, max = intersectionTypes.length; i < max; ++i) {
                final TypeBinding typeBinding = intersectionTypes[i];
                if (typeBinding.isInterface() && !TypeBinding.equalsEquals(samType, typeBinding)) {
                    if (typeBinding.id != 37) {
                        markerBindings.add(typeBinding);
                    }
                }
            }
            if (markerBindings.size() > 0) {
                return markerBindings.toArray(new TypeBinding[markerBindings.size()]);
            }
        }
        return null;
    }
    
    public ReferenceBinding getTypeBinding() {
        if (this.classType != null || this.resolvedType == null) {
            return null;
        }
        class LambdaTypeBinding extends ReferenceBinding
        {
            @Override
            public MethodBinding[] methods() {
                return new MethodBinding[] { LambdaExpression.this.getMethodBinding() };
            }
            
            @Override
            public char[] sourceName() {
                return TypeConstants.LAMBDA_TYPE;
            }
            
            @Override
            public ReferenceBinding superclass() {
                return LambdaExpression.this.scope.getJavaLangObject();
            }
            
            @Override
            public ReferenceBinding[] superInterfaces() {
                return new ReferenceBinding[] { (ReferenceBinding)LambdaExpression.this.resolvedType };
            }
            
            @Override
            public char[] computeUniqueKey() {
                return LambdaExpression.this.descriptor.declaringClass.computeUniqueKey();
            }
            
            @Override
            public String toString() {
                final StringBuffer output = new StringBuffer("()->{} implements ");
                output.append(LambdaExpression.this.descriptor.declaringClass.sourceName());
                output.append('.');
                output.append(LambdaExpression.this.descriptor.toString());
                return output.toString();
            }
        }
        return this.classType = new LambdaTypeBinding();
    }
    
    class CopyFailureException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
    }
}
