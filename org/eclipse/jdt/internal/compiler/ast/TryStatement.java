package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.codegen.MultiCatchExceptionLabel;
import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FinallyFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class TryStatement extends SubRoutineStatement
{
    static final char[] SECRET_RETURN_ADDRESS_NAME;
    static final char[] SECRET_ANY_HANDLER_NAME;
    static final char[] SECRET_PRIMARY_EXCEPTION_VARIABLE_NAME;
    static final char[] SECRET_CAUGHT_THROWABLE_VARIABLE_NAME;
    static final char[] SECRET_RETURN_VALUE_NAME;
    private static LocalDeclaration[] NO_RESOURCES;
    public LocalDeclaration[] resources;
    public Block tryBlock;
    public Block[] catchBlocks;
    public Argument[] catchArguments;
    public Block finallyBlock;
    BlockScope scope;
    public UnconditionalFlowInfo subRoutineInits;
    ReferenceBinding[] caughtExceptionTypes;
    boolean[] catchExits;
    BranchLabel subRoutineStartLabel;
    public LocalVariableBinding anyExceptionVariable;
    public LocalVariableBinding returnAddressVariable;
    public LocalVariableBinding secretReturnValue;
    ExceptionLabel[] declaredExceptionLabels;
    private Object[] reusableJSRTargets;
    private BranchLabel[] reusableJSRSequenceStartLabels;
    private int[] reusableJSRStateIndexes;
    private int reusableJSRTargetsCount;
    private static final int NO_FINALLY = 0;
    private static final int FINALLY_SUBROUTINE = 1;
    private static final int FINALLY_DOES_NOT_COMPLETE = 2;
    private static final int FINALLY_INLINE = 3;
    int mergedInitStateIndex;
    int preTryInitStateIndex;
    int postTryInitStateIndex;
    int[] postResourcesInitStateIndexes;
    int naturalExitMergeInitStateIndex;
    int[] catchExitInitStateIndexes;
    private LocalVariableBinding primaryExceptionVariable;
    private LocalVariableBinding caughtThrowableVariable;
    private ExceptionLabel[] resourceExceptionLabels;
    private int[] caughtExceptionsCatchBlocks;
    
    static {
        SECRET_RETURN_ADDRESS_NAME = " returnAddress".toCharArray();
        SECRET_ANY_HANDLER_NAME = " anyExceptionHandler".toCharArray();
        SECRET_PRIMARY_EXCEPTION_VARIABLE_NAME = " primaryException".toCharArray();
        SECRET_CAUGHT_THROWABLE_VARIABLE_NAME = " caughtThrowable".toCharArray();
        SECRET_RETURN_VALUE_NAME = " returnValue".toCharArray();
        TryStatement.NO_RESOURCES = new LocalDeclaration[0];
    }
    
    public TryStatement() {
        this.resources = TryStatement.NO_RESOURCES;
        this.reusableJSRTargetsCount = 0;
        this.mergedInitStateIndex = -1;
        this.preTryInitStateIndex = -1;
        this.postTryInitStateIndex = -1;
        this.naturalExitMergeInitStateIndex = -1;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        this.preTryInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        if (this.anyExceptionVariable != null) {
            this.anyExceptionVariable.useFlag = 1;
        }
        if (this.primaryExceptionVariable != null) {
            this.primaryExceptionVariable.useFlag = 1;
        }
        if (this.caughtThrowableVariable != null) {
            this.caughtThrowableVariable.useFlag = 1;
        }
        if (this.returnAddressVariable != null) {
            this.returnAddressVariable.useFlag = 1;
        }
        final int resourcesLength = this.resources.length;
        if (resourcesLength > 0) {
            this.postResourcesInitStateIndexes = new int[resourcesLength];
        }
        if (this.subRoutineStartLabel == null) {
            if (flowContext instanceof FinallyFlowContext) {
                final FinallyFlowContext finallyContext = (FinallyFlowContext)flowContext;
                finallyContext.outerTryContext = finallyContext.tryContext;
            }
            final ExceptionHandlingFlowContext handlingContext = new ExceptionHandlingFlowContext(flowContext, this, this.caughtExceptionTypes, this.caughtExceptionsCatchBlocks, null, this.scope, flowInfo);
            handlingContext.conditionalLevel = 0;
            FlowInfo tryInfo = flowInfo.copy();
            for (int i = 0; i < resourcesLength; ++i) {
                final LocalDeclaration resource = this.resources[i];
                tryInfo = resource.analyseCode(currentScope, handlingContext, tryInfo);
                this.postResourcesInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(tryInfo);
                final LocalVariableBinding resourceBinding = resource.binding;
                resourceBinding.useFlag = 1;
                if (resourceBinding.closeTracker != null) {
                    resourceBinding.closeTracker.withdraw();
                    resourceBinding.closeTracker = null;
                }
                final MethodBinding closeMethod = this.findCloseMethod(resource, resourceBinding);
                if (closeMethod != null && closeMethod.isValidBinding() && closeMethod.returnType.id == 6) {
                    final ReferenceBinding[] thrownExceptions = closeMethod.thrownExceptions;
                    for (int j = 0, length = thrownExceptions.length; j < length; ++j) {
                        handlingContext.checkExceptionHandlers(thrownExceptions[j], this.resources[i], tryInfo, currentScope, true);
                    }
                }
            }
            if (!this.tryBlock.isEmptyBlock()) {
                tryInfo = this.tryBlock.analyseCode(currentScope, handlingContext, tryInfo);
                if ((tryInfo.tagBits & 0x1) != 0x0) {
                    this.bits |= 0x20000000;
                }
            }
            if (resourcesLength > 0) {
                this.postTryInitStateIndex = currentScope.methodScope().recordInitializationStates(tryInfo);
                for (int i = 0; i < resourcesLength; ++i) {
                    tryInfo.resetAssignmentInfo(this.resources[i].binding);
                }
            }
            handlingContext.complainIfUnusedExceptionHandlers(this.scope, this);
            if (this.catchArguments != null) {
                final int catchCount;
                this.catchExits = new boolean[catchCount = this.catchBlocks.length];
                this.catchExitInitStateIndexes = new int[catchCount];
                for (int k = 0; k < catchCount; ++k) {
                    FlowInfo catchInfo = this.prepareCatchInfo(flowInfo, handlingContext, tryInfo, k);
                    ++flowContext.conditionalLevel;
                    catchInfo = this.catchBlocks[k].analyseCode(currentScope, flowContext, catchInfo);
                    --flowContext.conditionalLevel;
                    this.catchExitInitStateIndexes[k] = currentScope.methodScope().recordInitializationStates(catchInfo);
                    this.catchExits[k] = ((catchInfo.tagBits & 0x1) != 0x0);
                    tryInfo = tryInfo.mergedWith(catchInfo.unconditionalInits());
                }
            }
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(tryInfo);
            flowContext.mergeFinallyNullInfo(handlingContext.initsOnFinally);
            return tryInfo;
        }
        final InsideSubRoutineFlowContext insideSubContext = new InsideSubRoutineFlowContext(flowContext, this);
        if (flowContext instanceof FinallyFlowContext) {
            insideSubContext.outerTryContext = ((FinallyFlowContext)flowContext).tryContext;
        }
        final ExceptionHandlingFlowContext handlingContext2 = new ExceptionHandlingFlowContext(insideSubContext, this, this.caughtExceptionTypes, this.caughtExceptionsCatchBlocks, null, this.scope, flowInfo);
        insideSubContext.initsOnFinally = handlingContext2.initsOnFinally;
        final FinallyFlowContext finallyContext2;
        final UnconditionalFlowInfo subInfo = this.finallyBlock.analyseCode(currentScope, finallyContext2 = new FinallyFlowContext(flowContext, this.finallyBlock, handlingContext2), flowInfo.nullInfoLessUnconditionalCopy()).unconditionalInits();
        handlingContext2.conditionalLevel = 0;
        if (subInfo == FlowInfo.DEAD_END) {
            this.bits |= 0x4000;
            this.scope.problemReporter().finallyMustCompleteNormally(this.finallyBlock);
        }
        else {
            final FlowInfo finallyInfo = subInfo.copy();
            this.tryBlock.scope.finallyInfo = finallyInfo;
            if (this.catchBlocks != null) {
                for (int l = 0; l < this.catchBlocks.length; ++l) {
                    this.catchBlocks[l].scope.finallyInfo = finallyInfo;
                }
            }
        }
        this.subRoutineInits = subInfo;
        FlowInfo tryInfo2 = flowInfo.copy();
        for (int l = 0; l < resourcesLength; ++l) {
            final LocalDeclaration resource2 = this.resources[l];
            tryInfo2 = resource2.analyseCode(currentScope, handlingContext2, tryInfo2);
            this.postResourcesInitStateIndexes[l] = currentScope.methodScope().recordInitializationStates(tryInfo2);
            final LocalVariableBinding resourceBinding2 = resource2.binding;
            resourceBinding2.useFlag = 1;
            if (resourceBinding2.closeTracker != null) {
                resourceBinding2.closeTracker.withdraw();
            }
            final MethodBinding closeMethod2 = this.findCloseMethod(resource2, resourceBinding2);
            if (closeMethod2 != null && closeMethod2.isValidBinding() && closeMethod2.returnType.id == 6) {
                final ReferenceBinding[] thrownExceptions2 = closeMethod2.thrownExceptions;
                for (int m = 0, length2 = thrownExceptions2.length; m < length2; ++m) {
                    handlingContext2.checkExceptionHandlers(thrownExceptions2[m], this.resources[l], tryInfo2, currentScope, true);
                }
            }
        }
        if (!this.tryBlock.isEmptyBlock()) {
            tryInfo2 = this.tryBlock.analyseCode(currentScope, handlingContext2, tryInfo2);
            if ((tryInfo2.tagBits & 0x1) != 0x0) {
                this.bits |= 0x20000000;
            }
        }
        if (resourcesLength > 0) {
            this.postTryInitStateIndex = currentScope.methodScope().recordInitializationStates(tryInfo2);
            for (int l = 0; l < resourcesLength; ++l) {
                tryInfo2.resetAssignmentInfo(this.resources[l].binding);
            }
        }
        handlingContext2.complainIfUnusedExceptionHandlers(this.scope, this);
        if (this.catchArguments != null) {
            final int catchCount2;
            this.catchExits = new boolean[catchCount2 = this.catchBlocks.length];
            this.catchExitInitStateIndexes = new int[catchCount2];
            for (int i2 = 0; i2 < catchCount2; ++i2) {
                FlowInfo catchInfo2 = this.prepareCatchInfo(flowInfo, handlingContext2, tryInfo2, i2);
                insideSubContext.conditionalLevel = 1;
                catchInfo2 = this.catchBlocks[i2].analyseCode(currentScope, insideSubContext, catchInfo2);
                this.catchExitInitStateIndexes[i2] = currentScope.methodScope().recordInitializationStates(catchInfo2);
                this.catchExits[i2] = ((catchInfo2.tagBits & 0x1) != 0x0);
                tryInfo2 = tryInfo2.mergedWith(catchInfo2.unconditionalInits());
            }
        }
        finallyContext2.complainOnDeferredChecks((((tryInfo2.tagBits & 0x3) == 0x0) ? flowInfo.unconditionalCopy().addPotentialInitializationsFrom(tryInfo2).addPotentialInitializationsFrom(insideSubContext.initsOnReturn) : insideSubContext.initsOnReturn).addNullInfoFrom(handlingContext2.initsOnFinally), currentScope);
        flowContext.mergeFinallyNullInfo(handlingContext2.initsOnFinally);
        this.naturalExitMergeInitStateIndex = currentScope.methodScope().recordInitializationStates(tryInfo2);
        if (subInfo == FlowInfo.DEAD_END) {
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(subInfo);
            return subInfo;
        }
        final FlowInfo mergedInfo = tryInfo2.addInitializationsFrom(subInfo);
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        return mergedInfo;
    }
    
    private MethodBinding findCloseMethod(final LocalDeclaration resource, final LocalVariableBinding resourceBinding) {
        MethodBinding closeMethod = null;
        final TypeBinding type = resourceBinding.type;
        if (type != null && type.isValidBinding()) {
            final ReferenceBinding binding = (ReferenceBinding)type;
            closeMethod = binding.getExactMethod(ConstantPool.Close, new TypeBinding[0], this.scope.compilationUnitScope());
            if (closeMethod == null) {
                final InvocationSite site = new InvocationSite.EmptyWithAstNode(resource);
                closeMethod = this.scope.compilationUnitScope().findMethod(binding, ConstantPool.Close, new TypeBinding[0], site, false);
            }
        }
        return closeMethod;
    }
    
    private FlowInfo prepareCatchInfo(final FlowInfo flowInfo, final ExceptionHandlingFlowContext handlingContext, final FlowInfo tryInfo, final int i) {
        FlowInfo catchInfo;
        if (this.isUncheckedCatchBlock(i)) {
            catchInfo = flowInfo.unconditionalCopy().addPotentialInitializationsFrom(handlingContext.initsOnException(i)).addPotentialInitializationsFrom(tryInfo).addPotentialInitializationsFrom(handlingContext.initsOnReturn).addNullInfoFrom(handlingContext.initsOnFinally);
        }
        else {
            final FlowInfo initsOnException = handlingContext.initsOnException(i);
            catchInfo = flowInfo.nullInfoLessUnconditionalCopy().addPotentialInitializationsFrom(initsOnException).addNullInfoFrom(initsOnException).addPotentialInitializationsFrom(tryInfo.nullInfoLessUnconditionalCopy()).addPotentialInitializationsFrom(handlingContext.initsOnReturn.nullInfoLessUnconditionalCopy());
        }
        final LocalVariableBinding catchArg = this.catchArguments[i].binding;
        catchInfo.markAsDefinitelyAssigned(catchArg);
        catchInfo.markAsDefinitelyNonNull(catchArg);
        if (this.tryBlock.statements == null && this.resources == TryStatement.NO_RESOURCES) {
            catchInfo.setReachMode(1);
        }
        return catchInfo;
    }
    
    private boolean isUncheckedCatchBlock(final int catchBlock) {
        if (this.caughtExceptionsCatchBlocks == null) {
            return this.caughtExceptionTypes[catchBlock].isUncheckedException(true);
        }
        for (int i = 0, length = this.caughtExceptionsCatchBlocks.length; i < length; ++i) {
            if (this.caughtExceptionsCatchBlocks[i] == catchBlock && this.caughtExceptionTypes[i].isUncheckedException(true)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public ExceptionLabel enterAnyExceptionHandler(final CodeStream codeStream) {
        if (this.subRoutineStartLabel == null) {
            return null;
        }
        return super.enterAnyExceptionHandler(codeStream);
    }
    
    @Override
    public void enterDeclaredExceptionHandlers(final CodeStream codeStream) {
        for (int i = 0, length = (this.declaredExceptionLabels == null) ? 0 : this.declaredExceptionLabels.length; i < length; ++i) {
            this.declaredExceptionLabels[i].placeStart();
        }
    }
    
    @Override
    public void exitAnyExceptionHandler() {
        if (this.subRoutineStartLabel == null) {
            return;
        }
        super.exitAnyExceptionHandler();
    }
    
    @Override
    public void exitDeclaredExceptionHandlers(final CodeStream codeStream) {
        for (int i = 0, length = (this.declaredExceptionLabels == null) ? 0 : this.declaredExceptionLabels.length; i < length; ++i) {
            this.declaredExceptionLabels[i].placeEnd();
        }
    }
    
    private int finallyMode() {
        if (this.subRoutineStartLabel == null) {
            return 0;
        }
        if (this.isSubRoutineEscaping()) {
            return 2;
        }
        if (this.scope.compilerOptions().inlineJsrBytecode) {
            return 3;
        }
        return 1;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final boolean isStackMapFrameCodeStream = codeStream instanceof StackMapFrameCodeStream;
        this.anyExceptionLabel = null;
        this.reusableJSRTargets = null;
        this.reusableJSRSequenceStartLabels = null;
        this.reusableJSRTargetsCount = 0;
        final int pc = codeStream.position;
        final int finallyMode = this.finallyMode();
        boolean requiresNaturalExit = false;
        final int maxCatches = (this.catchArguments == null) ? 0 : this.catchArguments.length;
        ExceptionLabel[] exceptionLabels;
        if (maxCatches > 0) {
            exceptionLabels = new ExceptionLabel[maxCatches];
            for (int i = 0; i < maxCatches; ++i) {
                final Argument argument = this.catchArguments[i];
                ExceptionLabel exceptionLabel = null;
                if ((argument.binding.tagBits & 0x1000L) != 0x0L) {
                    final MultiCatchExceptionLabel multiCatchExceptionLabel = new MultiCatchExceptionLabel(codeStream, argument.binding.type);
                    multiCatchExceptionLabel.initialize((UnionTypeReference)argument.type, argument.annotations);
                    exceptionLabel = multiCatchExceptionLabel;
                }
                else {
                    exceptionLabel = new ExceptionLabel(codeStream, argument.binding.type, argument.type, argument.annotations);
                }
                exceptionLabel.placeStart();
                exceptionLabels[i] = exceptionLabel;
            }
        }
        else {
            exceptionLabels = null;
        }
        if (this.subRoutineStartLabel != null) {
            this.subRoutineStartLabel.initialize(codeStream);
            this.enterAnyExceptionHandler(codeStream);
        }
        try {
            this.declaredExceptionLabels = exceptionLabels;
            final int resourceCount = this.resources.length;
            if (resourceCount > 0) {
                this.resourceExceptionLabels = new ExceptionLabel[resourceCount + 1];
                codeStream.aconst_null();
                codeStream.store(this.primaryExceptionVariable, false);
                codeStream.addVariable(this.primaryExceptionVariable);
                codeStream.aconst_null();
                codeStream.store(this.caughtThrowableVariable, false);
                codeStream.addVariable(this.caughtThrowableVariable);
                for (int j = 0; j <= resourceCount; ++j) {
                    (this.resourceExceptionLabels[j] = new ExceptionLabel(codeStream, null)).placeStart();
                    if (j < resourceCount) {
                        this.resources[j].generateCode(this.scope, codeStream);
                    }
                }
            }
            this.tryBlock.generateCode(this.scope, codeStream);
            if (resourceCount > 0) {
                for (int j = resourceCount; j >= 0; --j) {
                    final BranchLabel exitLabel = new BranchLabel(codeStream);
                    this.resourceExceptionLabels[j].placeEnd();
                    final LocalVariableBinding localVariable = (j > 0) ? this.resources[j - 1].binding : null;
                    if ((this.bits & 0x20000000) == 0x0) {
                        if (j > 0) {
                            final int invokeCloseStartPc = codeStream.position;
                            if (this.postTryInitStateIndex != -1) {
                                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.postTryInitStateIndex);
                                codeStream.addDefinitelyAssignedVariables(currentScope, this.postTryInitStateIndex);
                            }
                            codeStream.load(localVariable);
                            codeStream.ifnull(exitLabel);
                            codeStream.load(localVariable);
                            codeStream.invokeAutoCloseableClose(localVariable.type);
                            codeStream.recordPositionsFrom(invokeCloseStartPc, this.tryBlock.sourceEnd);
                        }
                        codeStream.goto_(exitLabel);
                    }
                    if (j > 0) {
                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.postResourcesInitStateIndexes[j - 1]);
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.postResourcesInitStateIndexes[j - 1]);
                    }
                    else {
                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                    }
                    codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
                    this.resourceExceptionLabels[j].place();
                    if (j == resourceCount) {
                        codeStream.store(this.primaryExceptionVariable, false);
                    }
                    else {
                        final BranchLabel elseLabel = new BranchLabel(codeStream);
                        final BranchLabel postElseLabel = new BranchLabel(codeStream);
                        codeStream.store(this.caughtThrowableVariable, false);
                        codeStream.load(this.primaryExceptionVariable);
                        codeStream.ifnonnull(elseLabel);
                        codeStream.load(this.caughtThrowableVariable);
                        codeStream.store(this.primaryExceptionVariable, false);
                        codeStream.goto_(postElseLabel);
                        elseLabel.place();
                        codeStream.load(this.primaryExceptionVariable);
                        codeStream.load(this.caughtThrowableVariable);
                        codeStream.if_acmpeq(postElseLabel);
                        codeStream.load(this.primaryExceptionVariable);
                        codeStream.load(this.caughtThrowableVariable);
                        codeStream.invokeThrowableAddSuppressed();
                        postElseLabel.place();
                    }
                    if (j > 0) {
                        final BranchLabel postCloseLabel = new BranchLabel(codeStream);
                        final int invokeCloseStartPc2 = codeStream.position;
                        codeStream.load(localVariable);
                        codeStream.ifnull(postCloseLabel);
                        codeStream.load(localVariable);
                        codeStream.invokeAutoCloseableClose(localVariable.type);
                        codeStream.recordPositionsFrom(invokeCloseStartPc2, this.tryBlock.sourceEnd);
                        codeStream.removeVariable(localVariable);
                        postCloseLabel.place();
                    }
                    codeStream.load(this.primaryExceptionVariable);
                    codeStream.athrow();
                    exitLabel.place();
                }
                codeStream.removeVariable(this.primaryExceptionVariable);
                codeStream.removeVariable(this.caughtThrowableVariable);
            }
        }
        finally {
            this.declaredExceptionLabels = null;
            this.resourceExceptionLabels = null;
        }
        this.declaredExceptionLabels = null;
        this.resourceExceptionLabels = null;
        final boolean tryBlockHasSomeCode = codeStream.position != pc;
        if (tryBlockHasSomeCode) {
            final BranchLabel naturalExitLabel = new BranchLabel(codeStream);
            BranchLabel postCatchesFinallyLabel = null;
            for (int k = 0; k < maxCatches; ++k) {
                exceptionLabels[k].placeEnd();
            }
            if ((this.bits & 0x20000000) == 0x0) {
                final int position = codeStream.position;
                switch (finallyMode) {
                    case 1:
                    case 3: {
                        requiresNaturalExit = true;
                        if (this.naturalExitMergeInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                            codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                        }
                        codeStream.goto_(naturalExitLabel);
                        break;
                    }
                    case 0: {
                        if (this.naturalExitMergeInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                            codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                        }
                        codeStream.goto_(naturalExitLabel);
                        break;
                    }
                    case 2: {
                        codeStream.goto_(this.subRoutineStartLabel);
                        break;
                    }
                }
                codeStream.recordPositionsFrom(position, this.tryBlock.sourceEnd);
            }
            this.exitAnyExceptionHandler();
            if (this.catchArguments != null) {
                postCatchesFinallyLabel = new BranchLabel(codeStream);
                for (int k = 0; k < maxCatches; ++k) {
                    if (exceptionLabels[k].getCount() != 0) {
                        this.enterAnyExceptionHandler(codeStream);
                        if (this.preTryInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                            codeStream.addDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                        }
                        codeStream.pushExceptionOnStack(exceptionLabels[k].exceptionType);
                        exceptionLabels[k].place();
                        final int varPC = codeStream.position;
                        final LocalVariableBinding catchVar;
                        if ((catchVar = this.catchArguments[k].binding).resolvedPosition != -1) {
                            codeStream.store(catchVar, false);
                            catchVar.recordInitializationStartPC(codeStream.position);
                            codeStream.addVisibleLocalVariable(catchVar);
                        }
                        else {
                            codeStream.pop();
                        }
                        codeStream.recordPositionsFrom(varPC, this.catchArguments[k].sourceStart);
                        this.catchBlocks[k].generateCode(this.scope, codeStream);
                        this.exitAnyExceptionHandler();
                        if (!this.catchExits[k]) {
                            switch (finallyMode) {
                                case 3: {
                                    if (isStackMapFrameCodeStream) {
                                        ((StackMapFrameCodeStream)codeStream).pushStateIndex(this.naturalExitMergeInitStateIndex);
                                    }
                                    if (this.catchExitInitStateIndexes[k] != -1) {
                                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.catchExitInitStateIndexes[k]);
                                        codeStream.addDefinitelyAssignedVariables(currentScope, this.catchExitInitStateIndexes[k]);
                                    }
                                    this.finallyBlock.generateCode(this.scope, codeStream);
                                    codeStream.goto_(postCatchesFinallyLabel);
                                    if (isStackMapFrameCodeStream) {
                                        ((StackMapFrameCodeStream)codeStream).popStateIndex();
                                        break;
                                    }
                                    break;
                                }
                                case 1: {
                                    requiresNaturalExit = true;
                                }
                                case 0: {
                                    if (this.naturalExitMergeInitStateIndex != -1) {
                                        codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                                        codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                                    }
                                    codeStream.goto_(naturalExitLabel);
                                    break;
                                }
                                case 2: {
                                    codeStream.goto_(this.subRoutineStartLabel);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            final ExceptionLabel naturalExitExceptionHandler = (requiresNaturalExit && finallyMode == 1) ? new ExceptionLabel(codeStream, null) : null;
            final int finallySequenceStartPC = codeStream.position;
            if (this.subRoutineStartLabel != null && this.anyExceptionLabel.getCount() != 0) {
                codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
                if (this.preTryInitStateIndex != -1) {
                    codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                    codeStream.addDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                }
                this.placeAllAnyExceptionHandler();
                if (naturalExitExceptionHandler != null) {
                    naturalExitExceptionHandler.place();
                }
                switch (finallyMode) {
                    case 1: {
                        codeStream.store(this.anyExceptionVariable, false);
                        codeStream.jsr(this.subRoutineStartLabel);
                        codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
                        int position2 = codeStream.position;
                        codeStream.throwAnyException(this.anyExceptionVariable);
                        codeStream.recordPositionsFrom(position2, this.finallyBlock.sourceEnd);
                        this.subRoutineStartLabel.place();
                        codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
                        position2 = codeStream.position;
                        codeStream.store(this.returnAddressVariable, false);
                        codeStream.recordPositionsFrom(position2, this.finallyBlock.sourceStart);
                        this.finallyBlock.generateCode(this.scope, codeStream);
                        position2 = codeStream.position;
                        codeStream.ret(this.returnAddressVariable.resolvedPosition);
                        codeStream.recordPositionsFrom(position2, this.finallyBlock.sourceEnd);
                        break;
                    }
                    case 3: {
                        codeStream.store(this.anyExceptionVariable, false);
                        codeStream.addVariable(this.anyExceptionVariable);
                        codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
                        this.finallyBlock.generateCode(currentScope, codeStream);
                        final int position2 = codeStream.position;
                        codeStream.throwAnyException(this.anyExceptionVariable);
                        codeStream.removeVariable(this.anyExceptionVariable);
                        if (this.preTryInitStateIndex != -1) {
                            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
                        }
                        this.subRoutineStartLabel.place();
                        codeStream.recordPositionsFrom(position2, this.finallyBlock.sourceEnd);
                        break;
                    }
                    case 2: {
                        codeStream.pop();
                        this.subRoutineStartLabel.place();
                        codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
                        this.finallyBlock.generateCode(this.scope, codeStream);
                        break;
                    }
                }
                if (requiresNaturalExit) {
                    switch (finallyMode) {
                        case 1: {
                            naturalExitLabel.place();
                            final int position2 = codeStream.position;
                            naturalExitExceptionHandler.placeStart();
                            codeStream.jsr(this.subRoutineStartLabel);
                            naturalExitExceptionHandler.placeEnd();
                            codeStream.recordPositionsFrom(position2, this.finallyBlock.sourceEnd);
                            break;
                        }
                        case 3: {
                            if (isStackMapFrameCodeStream) {
                                ((StackMapFrameCodeStream)codeStream).pushStateIndex(this.naturalExitMergeInitStateIndex);
                            }
                            if (this.naturalExitMergeInitStateIndex != -1) {
                                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                                codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
                            }
                            naturalExitLabel.place();
                            this.finallyBlock.generateCode(this.scope, codeStream);
                            if (postCatchesFinallyLabel != null) {
                                final int position2 = codeStream.position;
                                codeStream.goto_(postCatchesFinallyLabel);
                                codeStream.recordPositionsFrom(position2, this.finallyBlock.sourceEnd);
                            }
                            if (isStackMapFrameCodeStream) {
                                ((StackMapFrameCodeStream)codeStream).popStateIndex();
                                break;
                            }
                            break;
                        }
                        case 2: {
                            break;
                        }
                        default: {
                            naturalExitLabel.place();
                            break;
                        }
                    }
                }
                if (postCatchesFinallyLabel != null) {
                    postCatchesFinallyLabel.place();
                }
            }
            else {
                naturalExitLabel.place();
            }
        }
        else if (this.subRoutineStartLabel != null) {
            this.finallyBlock.generateCode(this.scope, codeStream);
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public boolean generateSubRoutineInvocation(final BlockScope currentScope, final CodeStream codeStream, final Object targetLocation, final int stateIndex, final LocalVariableBinding secretLocal) {
        final int resourceCount = this.resources.length;
        if (resourceCount > 0 && this.resourceExceptionLabels != null) {
            for (int i = resourceCount; i > 0; --i) {
                this.resourceExceptionLabels[i].placeEnd();
                final LocalVariableBinding localVariable = this.resources[i - 1].binding;
                final BranchLabel exitLabel = new BranchLabel(codeStream);
                final int invokeCloseStartPc = codeStream.position;
                codeStream.load(localVariable);
                codeStream.ifnull(exitLabel);
                codeStream.load(localVariable);
                codeStream.invokeAutoCloseableClose(localVariable.type);
                codeStream.recordPositionsFrom(invokeCloseStartPc, this.tryBlock.sourceEnd);
                exitLabel.place();
            }
            for (int i = resourceCount; i > 0; --i) {
                this.resourceExceptionLabels[i].placeStart();
            }
        }
        final boolean isStackMapFrameCodeStream = codeStream instanceof StackMapFrameCodeStream;
        final int finallyMode = this.finallyMode();
        switch (finallyMode) {
            case 2: {
                codeStream.goto_(this.subRoutineStartLabel);
                return true;
            }
            case 0: {
                this.exitDeclaredExceptionHandlers(codeStream);
                return false;
            }
            default: {
                final CompilerOptions options = this.scope.compilerOptions();
                if (options.shareCommonFinallyBlocks && targetLocation != null) {
                    boolean reuseTargetLocation = true;
                    if (this.reusableJSRTargetsCount > 0) {
                        int j = 0;
                        final int count = this.reusableJSRTargetsCount;
                        while (j < count) {
                            final Object reusableJSRTarget = this.reusableJSRTargets[j];
                            if (targetLocation != reusableJSRTarget) {
                                if (!(targetLocation instanceof Constant) || !(reusableJSRTarget instanceof Constant) || !((Constant)targetLocation).hasSameValue((Constant)reusableJSRTarget)) {
                                    ++j;
                                    continue;
                                }
                            }
                            if (this.reusableJSRStateIndexes[j] != stateIndex && finallyMode == 3) {
                                reuseTargetLocation = false;
                                break;
                            }
                            codeStream.goto_(this.reusableJSRSequenceStartLabels[j]);
                            return true;
                        }
                    }
                    else {
                        this.reusableJSRTargets = new Object[3];
                        this.reusableJSRSequenceStartLabels = new BranchLabel[3];
                        this.reusableJSRStateIndexes = new int[3];
                    }
                    if (reuseTargetLocation) {
                        if (this.reusableJSRTargetsCount == this.reusableJSRTargets.length) {
                            System.arraycopy(this.reusableJSRTargets, 0, this.reusableJSRTargets = new Object[2 * this.reusableJSRTargetsCount], 0, this.reusableJSRTargetsCount);
                            System.arraycopy(this.reusableJSRSequenceStartLabels, 0, this.reusableJSRSequenceStartLabels = new BranchLabel[2 * this.reusableJSRTargetsCount], 0, this.reusableJSRTargetsCount);
                            System.arraycopy(this.reusableJSRStateIndexes, 0, this.reusableJSRStateIndexes = new int[2 * this.reusableJSRTargetsCount], 0, this.reusableJSRTargetsCount);
                        }
                        this.reusableJSRTargets[this.reusableJSRTargetsCount] = targetLocation;
                        final BranchLabel reusableJSRSequenceStartLabel = new BranchLabel(codeStream);
                        reusableJSRSequenceStartLabel.place();
                        this.reusableJSRStateIndexes[this.reusableJSRTargetsCount] = stateIndex;
                        this.reusableJSRSequenceStartLabels[this.reusableJSRTargetsCount++] = reusableJSRSequenceStartLabel;
                    }
                }
                if (finallyMode == 3) {
                    if (isStackMapFrameCodeStream) {
                        ((StackMapFrameCodeStream)codeStream).pushStateIndex(stateIndex);
                    }
                    this.exitAnyExceptionHandler();
                    this.exitDeclaredExceptionHandlers(codeStream);
                    this.finallyBlock.generateCode(currentScope, codeStream);
                    if (isStackMapFrameCodeStream) {
                        ((StackMapFrameCodeStream)codeStream).popStateIndex();
                    }
                }
                else {
                    codeStream.jsr(this.subRoutineStartLabel);
                    this.exitAnyExceptionHandler();
                    this.exitDeclaredExceptionHandlers(codeStream);
                }
                return false;
            }
        }
    }
    
    @Override
    public boolean isSubRoutineEscaping() {
        return (this.bits & 0x4000) != 0x0;
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        final int length = this.resources.length;
        ASTNode.printIndent(indent, output).append("try" + ((length == 0) ? "\n" : " ("));
        for (int i = 0; i < length; ++i) {
            this.resources[i].printAsExpression(0, output);
            if (i != length - 1) {
                output.append(";\n");
                ASTNode.printIndent(indent + 2, output);
            }
        }
        if (length > 0) {
            output.append(")\n");
        }
        this.tryBlock.printStatement(indent + 1, output);
        if (this.catchBlocks != null) {
            for (int i = 0; i < this.catchBlocks.length; ++i) {
                output.append('\n');
                ASTNode.printIndent(indent, output).append("catch (");
                this.catchArguments[i].print(0, output).append(")\n");
                this.catchBlocks[i].printStatement(indent + 1, output);
            }
        }
        if (this.finallyBlock != null) {
            output.append('\n');
            ASTNode.printIndent(indent, output).append("finally\n");
            this.finallyBlock.printStatement(indent + 1, output);
        }
        return output;
    }
    
    @Override
    public void resolve(final BlockScope upperScope) {
        this.scope = new BlockScope(upperScope);
        BlockScope finallyScope = null;
        BlockScope resourceManagementScope = null;
        final int resourceCount = this.resources.length;
        if (resourceCount > 0) {
            resourceManagementScope = new BlockScope(this.scope);
            resourceManagementScope.addLocalVariable(this.primaryExceptionVariable = new LocalVariableBinding(TryStatement.SECRET_PRIMARY_EXCEPTION_VARIABLE_NAME, this.scope.getJavaLangThrowable(), 0, false));
            this.primaryExceptionVariable.setConstant(Constant.NotAConstant);
            resourceManagementScope.addLocalVariable(this.caughtThrowableVariable = new LocalVariableBinding(TryStatement.SECRET_CAUGHT_THROWABLE_VARIABLE_NAME, this.scope.getJavaLangThrowable(), 0, false));
            this.caughtThrowableVariable.setConstant(Constant.NotAConstant);
        }
        for (int i = 0; i < resourceCount; ++i) {
            this.resources[i].resolve(resourceManagementScope);
            final LocalVariableBinding localVariableBinding = this.resources[i].binding;
            if (localVariableBinding != null && localVariableBinding.isValidBinding()) {
                final LocalVariableBinding localVariableBinding2 = localVariableBinding;
                localVariableBinding2.modifiers |= 0x10;
                final LocalVariableBinding localVariableBinding3 = localVariableBinding;
                localVariableBinding3.tagBits |= 0x2000L;
                final TypeBinding resourceType = localVariableBinding.type;
                if (resourceType instanceof ReferenceBinding) {
                    if (resourceType.findSuperTypeOriginatingFrom(62, false) == null && resourceType.isValidBinding()) {
                        upperScope.problemReporter().resourceHasToImplementAutoCloseable(resourceType, this.resources[i].type);
                        localVariableBinding.type = new ProblemReferenceBinding(CharOperation.splitOn('.', resourceType.shortReadableName()), null, 15);
                    }
                }
                else if (resourceType != null) {
                    upperScope.problemReporter().resourceHasToImplementAutoCloseable(resourceType, this.resources[i].type);
                    localVariableBinding.type = new ProblemReferenceBinding(CharOperation.splitOn('.', resourceType.shortReadableName()), null, 15);
                }
            }
        }
        final BlockScope tryScope = new BlockScope((resourceManagementScope != null) ? resourceManagementScope : this.scope);
        if (this.finallyBlock != null) {
            if (this.finallyBlock.isEmptyBlock()) {
                if ((this.finallyBlock.bits & 0x8) != 0x0) {
                    this.scope.problemReporter().undocumentedEmptyBlock(this.finallyBlock.sourceStart, this.finallyBlock.sourceEnd);
                }
            }
            else {
                finallyScope = new BlockScope(this.scope, false);
                final MethodScope methodScope = this.scope.methodScope();
                if (!upperScope.compilerOptions().inlineJsrBytecode) {
                    finallyScope.addLocalVariable(this.returnAddressVariable = new LocalVariableBinding(TryStatement.SECRET_RETURN_ADDRESS_NAME, upperScope.getJavaLangObject(), 0, false));
                    this.returnAddressVariable.setConstant(Constant.NotAConstant);
                }
                this.subRoutineStartLabel = new BranchLabel();
                finallyScope.addLocalVariable(this.anyExceptionVariable = new LocalVariableBinding(TryStatement.SECRET_ANY_HANDLER_NAME, this.scope.getJavaLangThrowable(), 0, false));
                this.anyExceptionVariable.setConstant(Constant.NotAConstant);
                if (!methodScope.isInsideInitializer()) {
                    final MethodBinding methodBinding = (methodScope.referenceContext instanceof AbstractMethodDeclaration) ? ((AbstractMethodDeclaration)methodScope.referenceContext).binding : ((methodScope.referenceContext instanceof LambdaExpression) ? ((LambdaExpression)methodScope.referenceContext).binding : null);
                    if (methodBinding != null) {
                        final TypeBinding methodReturnType = methodBinding.returnType;
                        if (methodReturnType.id != 6) {
                            finallyScope.addLocalVariable(this.secretReturnValue = new LocalVariableBinding(TryStatement.SECRET_RETURN_VALUE_NAME, methodReturnType, 0, false));
                            this.secretReturnValue.setConstant(Constant.NotAConstant);
                        }
                    }
                }
                this.finallyBlock.resolveUsing(finallyScope);
                final int shiftScopesLength = (this.catchArguments == null) ? 1 : (this.catchArguments.length + 1);
                (finallyScope.shiftScopes = new BlockScope[shiftScopesLength])[0] = tryScope;
            }
        }
        this.tryBlock.resolveUsing(tryScope);
        if (this.catchBlocks != null) {
            final int length = this.catchArguments.length;
            final TypeBinding[] argumentTypes = new TypeBinding[length];
            boolean containsUnionTypes = false;
            boolean catchHasError = false;
            for (int j = 0; j < length; ++j) {
                final BlockScope catchScope = new BlockScope(this.scope);
                if (finallyScope != null) {
                    finallyScope.shiftScopes[j + 1] = catchScope;
                }
                final Argument catchArgument = this.catchArguments[j];
                containsUnionTypes |= ((catchArgument.type.bits & 0x20000000) != 0x0);
                if ((argumentTypes[j] = catchArgument.resolveForCatch(catchScope)) == null) {
                    catchHasError = true;
                }
                this.catchBlocks[j].resolveUsing(catchScope);
            }
            if (catchHasError) {
                return;
            }
            this.verifyDuplicationAndOrder(length, argumentTypes, containsUnionTypes);
        }
        else {
            this.caughtExceptionTypes = new ReferenceBinding[0];
        }
        if (finallyScope != null) {
            this.scope.addSubscope(finallyScope);
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            final LocalDeclaration[] localDeclarations = this.resources;
            for (int i = 0, max = localDeclarations.length; i < max; ++i) {
                localDeclarations[i].traverse(visitor, this.scope);
            }
            this.tryBlock.traverse(visitor, this.scope);
            if (this.catchArguments != null) {
                for (int i = 0, max = this.catchBlocks.length; i < max; ++i) {
                    this.catchArguments[i].traverse(visitor, this.scope);
                    this.catchBlocks[i].traverse(visitor, this.scope);
                }
            }
            if (this.finallyBlock != null) {
                this.finallyBlock.traverse(visitor, this.scope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    protected void verifyDuplicationAndOrder(final int length, final TypeBinding[] argumentTypes, final boolean containsUnionTypes) {
        if (containsUnionTypes) {
            int totalCount = 0;
            final ReferenceBinding[][] allExceptionTypes = new ReferenceBinding[length][];
            for (int i = 0; i < length; ++i) {
                if (!(argumentTypes[i] instanceof ArrayBinding)) {
                    final ReferenceBinding currentExceptionType = (ReferenceBinding)argumentTypes[i];
                    final TypeReference catchArgumentType = this.catchArguments[i].type;
                    if ((catchArgumentType.bits & 0x20000000) != 0x0) {
                        final TypeReference[] typeReferences = ((UnionTypeReference)catchArgumentType).typeReferences;
                        final int typeReferencesLength = typeReferences.length;
                        final ReferenceBinding[] unionExceptionTypes = new ReferenceBinding[typeReferencesLength];
                        for (int j = 0; j < typeReferencesLength; ++j) {
                            unionExceptionTypes[j] = (ReferenceBinding)typeReferences[j].resolvedType;
                        }
                        totalCount += typeReferencesLength;
                        allExceptionTypes[i] = unionExceptionTypes;
                    }
                    else {
                        allExceptionTypes[i] = new ReferenceBinding[] { currentExceptionType };
                        ++totalCount;
                    }
                }
            }
            this.caughtExceptionTypes = new ReferenceBinding[totalCount];
            this.caughtExceptionsCatchBlocks = new int[totalCount];
            int i = 0;
            int l = 0;
            while (i < length) {
                final ReferenceBinding[] currentExceptions = allExceptionTypes[i];
                Label_0375: {
                    if (currentExceptions != null) {
                        for (int k = 0, max = currentExceptions.length; k < max; ++k) {
                            final ReferenceBinding exception = currentExceptions[k];
                            this.caughtExceptionTypes[l] = exception;
                            this.caughtExceptionsCatchBlocks[l++] = i;
                            for (final ReferenceBinding[] exceptions : allExceptionTypes) {
                                if (exceptions != null) {
                                    for (int n = 0, max2 = exceptions.length; n < max2; ++n) {
                                        final ReferenceBinding currentException = exceptions[n];
                                        if (exception.isCompatibleWith(currentException)) {
                                            TypeReference catchArgumentType2 = this.catchArguments[i].type;
                                            if ((catchArgumentType2.bits & 0x20000000) != 0x0) {
                                                catchArgumentType2 = ((UnionTypeReference)catchArgumentType2).typeReferences[k];
                                            }
                                            this.scope.problemReporter().wrongSequenceOfExceptionTypesError(catchArgumentType2, exception, currentException);
                                            break Label_0375;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                ++i;
            }
        }
        else {
            this.caughtExceptionTypes = new ReferenceBinding[length];
            for (int i2 = 0; i2 < length; ++i2) {
                if (!(argumentTypes[i2] instanceof ArrayBinding)) {
                    this.caughtExceptionTypes[i2] = (ReferenceBinding)argumentTypes[i2];
                    for (int j2 = 0; j2 < i2; ++j2) {
                        if (this.caughtExceptionTypes[i2].isCompatibleWith(argumentTypes[j2])) {
                            this.scope.problemReporter().wrongSequenceOfExceptionTypesError(this.catchArguments[i2].type, this.caughtExceptionTypes[i2], argumentTypes[j2]);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        if (!this.tryBlock.doesNotCompleteNormally()) {
            return this.finallyBlock != null && this.finallyBlock.doesNotCompleteNormally();
        }
        if (this.catchBlocks != null) {
            for (int i = 0; i < this.catchBlocks.length; ++i) {
                if (!this.catchBlocks[i].doesNotCompleteNormally()) {
                    return this.finallyBlock != null && this.finallyBlock.doesNotCompleteNormally();
                }
            }
        }
        return true;
    }
    
    @Override
    public boolean completesByContinue() {
        if (this.tryBlock.completesByContinue()) {
            return this.finallyBlock == null || !this.finallyBlock.doesNotCompleteNormally() || this.finallyBlock.completesByContinue();
        }
        if (this.catchBlocks != null) {
            for (int i = 0; i < this.catchBlocks.length; ++i) {
                if (this.catchBlocks[i].completesByContinue()) {
                    return this.finallyBlock == null || !this.finallyBlock.doesNotCompleteNormally() || this.finallyBlock.completesByContinue();
                }
            }
        }
        return this.finallyBlock != null && this.finallyBlock.completesByContinue();
    }
}
