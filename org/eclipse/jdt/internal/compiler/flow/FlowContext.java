package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.LambdaExpression;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.CatchParameterBinding;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

public class FlowContext implements TypeConstants
{
    public static final FlowContext NotContinuableContext;
    public ASTNode associatedNode;
    public FlowContext parent;
    public FlowInfo initsOnFinally;
    public int conditionalLevel;
    public int tagBits;
    public TypeBinding[][] providedExpectedTypes;
    private Reference[] nullCheckedFieldReferences;
    private int[] timesToLiveForNullCheckInfo;
    public static final int DEFER_NULL_DIAGNOSTIC = 1;
    public static final int PREEMPT_NULL_DIAGNOSTIC = 2;
    public static final int INSIDE_NEGATION = 4;
    public static final int HIDE_NULL_COMPARISON_WARNING = 4096;
    public static final int HIDE_NULL_COMPARISON_WARNING_MASK = 61440;
    public static final int CAN_ONLY_NULL_NON_NULL = 0;
    public static final int CAN_ONLY_NULL = 1;
    public static final int CAN_ONLY_NON_NULL = 2;
    public static final int MAY_NULL = 3;
    public static final int ASSIGN_TO_NONNULL = 128;
    public static final int IN_UNBOXING = 16;
    public static final int EXIT_RESOURCE = 2048;
    public static final int CHECK_MASK = 255;
    public static final int IN_COMPARISON_NULL = 256;
    public static final int IN_COMPARISON_NON_NULL = 512;
    public static final int IN_ASSIGNMENT = 768;
    public static final int IN_INSTANCEOF = 1024;
    public static final int CONTEXT_MASK = -61696;
    
    static {
        NotContinuableContext = new FlowContext(null, null);
    }
    
    public FlowContext(final FlowContext parent, final ASTNode associatedNode) {
        this.conditionalLevel = -1;
        this.providedExpectedTypes = null;
        this.nullCheckedFieldReferences = null;
        this.timesToLiveForNullCheckInfo = null;
        this.parent = parent;
        this.associatedNode = associatedNode;
        if (parent != null) {
            if ((parent.tagBits & 0x3) != 0x0) {
                this.tagBits |= 0x1;
            }
            this.initsOnFinally = parent.initsOnFinally;
            this.conditionalLevel = parent.conditionalLevel;
            this.nullCheckedFieldReferences = parent.nullCheckedFieldReferences;
            this.timesToLiveForNullCheckInfo = parent.timesToLiveForNullCheckInfo;
        }
    }
    
    public void recordNullCheckedFieldReference(final Reference reference, final int timeToLive) {
        if (this.nullCheckedFieldReferences == null) {
            this.nullCheckedFieldReferences = new Reference[] { reference, null };
            this.timesToLiveForNullCheckInfo = new int[] { timeToLive, -1 };
        }
        else {
            final int len = this.nullCheckedFieldReferences.length;
            for (int i = 0; i < len; ++i) {
                if (this.nullCheckedFieldReferences[i] == null) {
                    this.nullCheckedFieldReferences[i] = reference;
                    this.timesToLiveForNullCheckInfo[i] = timeToLive;
                    return;
                }
            }
            System.arraycopy(this.nullCheckedFieldReferences, 0, this.nullCheckedFieldReferences = new Reference[len + 2], 0, len);
            System.arraycopy(this.timesToLiveForNullCheckInfo, 0, this.timesToLiveForNullCheckInfo = new int[len + 2], 0, len);
            this.nullCheckedFieldReferences[len] = reference;
            this.timesToLiveForNullCheckInfo[len] = timeToLive;
        }
    }
    
    public void extendTimeToLiveForNullCheckedField(final int t) {
        if (this.timesToLiveForNullCheckInfo != null) {
            for (int i = 0; i < this.timesToLiveForNullCheckInfo.length; ++i) {
                if (this.timesToLiveForNullCheckInfo[i] > 0) {
                    final int[] timesToLiveForNullCheckInfo = this.timesToLiveForNullCheckInfo;
                    final int n = i;
                    timesToLiveForNullCheckInfo[n] += t;
                }
            }
        }
    }
    
    public void expireNullCheckedFieldInfo() {
        if (this.nullCheckedFieldReferences != null) {
            for (int i = 0; i < this.nullCheckedFieldReferences.length; ++i) {
                final int[] timesToLiveForNullCheckInfo = this.timesToLiveForNullCheckInfo;
                final int n = i;
                if (--timesToLiveForNullCheckInfo[n] == 0) {
                    this.nullCheckedFieldReferences[i] = null;
                }
            }
        }
    }
    
    public boolean isNullcheckedFieldAccess(final Reference reference) {
        if (this.nullCheckedFieldReferences == null) {
            return false;
        }
        for (int len = this.nullCheckedFieldReferences.length, i = 0; i < len; ++i) {
            final Reference checked = this.nullCheckedFieldReferences[i];
            if (checked != null) {
                if (checked.isEquivalent(reference)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public BranchLabel breakLabel() {
        return null;
    }
    
    public void checkExceptionHandlers(final TypeBinding raisedException, final ASTNode location, final FlowInfo flowInfo, final BlockScope scope) {
        this.checkExceptionHandlers(raisedException, location, flowInfo, scope, false);
    }
    
    public void checkExceptionHandlers(final TypeBinding raisedException, final ASTNode location, final FlowInfo flowInfo, final BlockScope scope, final boolean isExceptionOnAutoClose) {
        FlowContext traversedContext = this;
        ArrayList abruptlyExitedLoops = null;
        if (scope.compilerOptions().sourceLevel >= 3342336L && location instanceof ThrowStatement) {
            final Expression throwExpression = ((ThrowStatement)location).exception;
            final LocalVariableBinding throwArgBinding = throwExpression.localVariableBinding();
            if (throwExpression instanceof SingleNameReference && throwArgBinding instanceof CatchParameterBinding && throwArgBinding.isEffectivelyFinal()) {
                final CatchParameterBinding parameter = (CatchParameterBinding)throwArgBinding;
                this.checkExceptionHandlers(parameter.getPreciseTypes(), location, flowInfo, scope);
                return;
            }
        }
        while (traversedContext != null) {
            final SubRoutineStatement sub;
            if ((sub = traversedContext.subroutine()) != null && sub.isSubRoutineEscaping()) {
                return;
            }
            if (traversedContext instanceof ExceptionHandlingFlowContext) {
                final ExceptionHandlingFlowContext exceptionContext = (ExceptionHandlingFlowContext)traversedContext;
                final ReferenceBinding[] caughtExceptions;
                if ((caughtExceptions = exceptionContext.handledExceptions) != Binding.NO_EXCEPTIONS) {
                    boolean definitelyCaught = false;
                    for (int caughtIndex = 0, caughtCount = caughtExceptions.length; caughtIndex < caughtCount; ++caughtIndex) {
                        final ReferenceBinding caughtException = caughtExceptions[caughtIndex];
                        FlowInfo exceptionFlow = flowInfo;
                        final int state = (caughtException == null) ? -1 : Scope.compareTypes(raisedException, caughtException);
                        if (abruptlyExitedLoops != null && caughtException != null && state != 0) {
                            for (int i = 0, abruptlyExitedLoopsCount = abruptlyExitedLoops.size(); i < abruptlyExitedLoopsCount; ++i) {
                                final LoopingFlowContext loop = abruptlyExitedLoops.get(i);
                                loop.recordCatchContextOfEscapingException(exceptionContext, caughtException, flowInfo);
                            }
                            exceptionFlow = FlowInfo.DEAD_END;
                        }
                        switch (state) {
                            case -1: {
                                exceptionContext.recordHandlingException(caughtException, exceptionFlow.unconditionalInits(), raisedException, raisedException, location, definitelyCaught);
                                definitelyCaught = true;
                                break;
                            }
                            case 1: {
                                exceptionContext.recordHandlingException(caughtException, exceptionFlow.unconditionalInits(), raisedException, caughtException, location, false);
                                break;
                            }
                        }
                    }
                    if (definitelyCaught) {
                        return;
                    }
                }
                if (exceptionContext.isMethodContext) {
                    if (raisedException.isUncheckedException(false)) {
                        return;
                    }
                    boolean shouldMergeUnhandledExceptions = exceptionContext instanceof ExceptionInferenceFlowContext;
                    if (exceptionContext.associatedNode instanceof AbstractMethodDeclaration) {
                        final AbstractMethodDeclaration method = (AbstractMethodDeclaration)exceptionContext.associatedNode;
                        if (method.isConstructor() && method.binding.declaringClass.isAnonymousType()) {
                            shouldMergeUnhandledExceptions = true;
                        }
                    }
                    if (shouldMergeUnhandledExceptions) {
                        exceptionContext.mergeUnhandledException(raisedException);
                        return;
                    }
                    break;
                }
            }
            else if (traversedContext instanceof LoopingFlowContext) {
                if (abruptlyExitedLoops == null) {
                    abruptlyExitedLoops = new ArrayList(5);
                }
                abruptlyExitedLoops.add(traversedContext);
            }
            traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
            if (!isExceptionOnAutoClose && traversedContext instanceof InsideSubRoutineFlowContext) {
                final ASTNode node = traversedContext.associatedNode;
                if (node instanceof TryStatement) {
                    final TryStatement tryStatement = (TryStatement)node;
                    flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
                }
            }
            traversedContext = traversedContext.getLocalParent();
        }
        if (isExceptionOnAutoClose) {
            scope.problemReporter().unhandledExceptionFromAutoClose(raisedException, location);
        }
        else {
            scope.problemReporter().unhandledException(raisedException, location);
        }
    }
    
    public void checkExceptionHandlers(TypeBinding[] raisedExceptions, final ASTNode location, final FlowInfo flowInfo, final BlockScope scope) {
        final int raisedCount;
        if (raisedExceptions == null || (raisedCount = raisedExceptions.length) == 0) {
            return;
        }
        int remainingCount = raisedCount;
        System.arraycopy(raisedExceptions, 0, raisedExceptions = new TypeBinding[raisedCount], 0, raisedCount);
        FlowContext traversedContext = this;
        ArrayList abruptlyExitedLoops = null;
        while (traversedContext != null) {
            final SubRoutineStatement sub;
            if ((sub = traversedContext.subroutine()) != null && sub.isSubRoutineEscaping()) {
                return;
            }
            if (traversedContext instanceof ExceptionHandlingFlowContext) {
                final ExceptionHandlingFlowContext exceptionContext = (ExceptionHandlingFlowContext)traversedContext;
                final ReferenceBinding[] caughtExceptions;
                if ((caughtExceptions = exceptionContext.handledExceptions) != Binding.NO_EXCEPTIONS) {
                    final int caughtCount = caughtExceptions.length;
                    final boolean[] locallyCaught = new boolean[raisedCount];
                    for (final ReferenceBinding caughtException : caughtExceptions) {
                        for (int raisedIndex = 0; raisedIndex < raisedCount; ++raisedIndex) {
                            final TypeBinding raisedException;
                            if ((raisedException = raisedExceptions[raisedIndex]) != null) {
                                FlowInfo exceptionFlow = flowInfo;
                                final int state = (caughtException == null) ? -1 : Scope.compareTypes(raisedException, caughtException);
                                if (abruptlyExitedLoops != null && caughtException != null && state != 0) {
                                    for (int i = 0, abruptlyExitedLoopsCount = abruptlyExitedLoops.size(); i < abruptlyExitedLoopsCount; ++i) {
                                        final LoopingFlowContext loop = abruptlyExitedLoops.get(i);
                                        loop.recordCatchContextOfEscapingException(exceptionContext, caughtException, flowInfo);
                                    }
                                    exceptionFlow = FlowInfo.DEAD_END;
                                }
                                switch (state) {
                                    case -1: {
                                        exceptionContext.recordHandlingException(caughtException, exceptionFlow.unconditionalInits(), raisedException, raisedException, location, locallyCaught[raisedIndex]);
                                        if (!locallyCaught[raisedIndex]) {
                                            locallyCaught[raisedIndex] = true;
                                            --remainingCount;
                                            break;
                                        }
                                        break;
                                    }
                                    case 1: {
                                        exceptionContext.recordHandlingException(caughtException, exceptionFlow.unconditionalInits(), raisedException, caughtException, location, false);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    for (int j = 0; j < raisedCount; ++j) {
                        if (locallyCaught[j]) {
                            raisedExceptions[j] = null;
                        }
                    }
                }
                if (exceptionContext.isMethodContext) {
                    for (int k = 0; k < raisedCount; ++k) {
                        final TypeBinding raisedException2;
                        if ((raisedException2 = raisedExceptions[k]) != null && raisedException2.isUncheckedException(false)) {
                            --remainingCount;
                            raisedExceptions[k] = null;
                        }
                    }
                    boolean shouldMergeUnhandledException = exceptionContext instanceof ExceptionInferenceFlowContext;
                    if (exceptionContext.associatedNode instanceof AbstractMethodDeclaration) {
                        final AbstractMethodDeclaration method = (AbstractMethodDeclaration)exceptionContext.associatedNode;
                        if (method.isConstructor() && method.binding.declaringClass.isAnonymousType()) {
                            shouldMergeUnhandledException = true;
                        }
                    }
                    if (shouldMergeUnhandledException) {
                        for (int l = 0; l < raisedCount; ++l) {
                            final TypeBinding raisedException3;
                            if ((raisedException3 = raisedExceptions[l]) != null) {
                                exceptionContext.mergeUnhandledException(raisedException3);
                            }
                        }
                        return;
                    }
                    break;
                }
            }
            else if (traversedContext instanceof LoopingFlowContext) {
                if (abruptlyExitedLoops == null) {
                    abruptlyExitedLoops = new ArrayList(5);
                }
                abruptlyExitedLoops.add(traversedContext);
            }
            if (remainingCount == 0) {
                return;
            }
            traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
            if (traversedContext instanceof InsideSubRoutineFlowContext) {
                final ASTNode node = traversedContext.associatedNode;
                if (node instanceof TryStatement) {
                    final TryStatement tryStatement = (TryStatement)node;
                    flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
                }
            }
            traversedContext = traversedContext.getLocalParent();
        }
    Label_0652:
        for (int m = 0; m < raisedCount; ++m) {
            final TypeBinding exception;
            if ((exception = raisedExceptions[m]) != null) {
                for (int j2 = 0; j2 < m; ++j2) {
                    if (TypeBinding.equalsEquals(raisedExceptions[j2], exception)) {
                        continue Label_0652;
                    }
                }
                scope.problemReporter().unhandledException(exception, location);
            }
        }
    }
    
    public BranchLabel continueLabel() {
        return null;
    }
    
    public FlowInfo getInitsForFinalBlankInitializationCheck(final TypeBinding declaringType, final FlowInfo flowInfo) {
        FlowContext current = this;
        FlowInfo inits = flowInfo;
        do {
            if (current instanceof InitializationFlowContext) {
                final InitializationFlowContext initializationContext = (InitializationFlowContext)current;
                if (TypeBinding.equalsEquals(((TypeDeclaration)initializationContext.associatedNode).binding, declaringType)) {
                    return inits;
                }
                inits = initializationContext.initsBeforeContext;
                current = initializationContext.initializationParent;
            }
            else if (current instanceof ExceptionHandlingFlowContext) {
                if (current instanceof FieldInitsFakingFlowContext) {
                    return FlowInfo.DEAD_END;
                }
                final ExceptionHandlingFlowContext exceptionContext = (ExceptionHandlingFlowContext)current;
                current = ((exceptionContext.initializationParent == null) ? exceptionContext.parent : exceptionContext.initializationParent);
            }
            else {
                current = current.getLocalParent();
            }
        } while (current != null);
        throw new IllegalStateException(declaringType.debugName());
    }
    
    public FlowContext getTargetContextForBreakLabel(final char[] labelName) {
        FlowContext current = this;
        FlowContext lastNonReturningSubRoutine = null;
        while (current != null) {
            if (current.isNonReturningContext()) {
                lastNonReturningSubRoutine = current;
            }
            final char[] currentLabelName;
            if ((currentLabelName = current.labelName()) != null && CharOperation.equals(currentLabelName, labelName)) {
                final LabeledStatement labeledStatement = (LabeledStatement)current.associatedNode;
                labeledStatement.bits |= 0x40;
                if (lastNonReturningSubRoutine == null) {
                    return current;
                }
                return lastNonReturningSubRoutine;
            }
            else {
                current = current.getLocalParent();
            }
        }
        return null;
    }
    
    public FlowContext getTargetContextForContinueLabel(final char[] labelName) {
        FlowContext current = this;
        FlowContext lastContinuable = null;
        FlowContext lastNonReturningSubRoutine = null;
        while (current != null) {
            if (current.isNonReturningContext()) {
                lastNonReturningSubRoutine = current;
            }
            else if (current.isContinuable()) {
                lastContinuable = current;
            }
            final char[] currentLabelName;
            if ((currentLabelName = current.labelName()) != null && CharOperation.equals(currentLabelName, labelName)) {
                final LabeledStatement labeledStatement = (LabeledStatement)current.associatedNode;
                labeledStatement.bits |= 0x40;
                if (lastContinuable == null || current.associatedNode.concreteStatement() != lastContinuable.associatedNode) {
                    return FlowContext.NotContinuableContext;
                }
                if (lastNonReturningSubRoutine == null) {
                    return lastContinuable;
                }
                return lastNonReturningSubRoutine;
            }
            else {
                current = current.getLocalParent();
            }
        }
        return null;
    }
    
    public FlowContext getTargetContextForDefaultBreak() {
        FlowContext current = this;
        FlowContext lastNonReturningSubRoutine = null;
        while (current != null) {
            if (current.isNonReturningContext()) {
                lastNonReturningSubRoutine = current;
            }
            if (current.isBreakable() && current.labelName() == null) {
                if (lastNonReturningSubRoutine == null) {
                    return current;
                }
                return lastNonReturningSubRoutine;
            }
            else {
                current = current.getLocalParent();
            }
        }
        return null;
    }
    
    public FlowContext getTargetContextForDefaultContinue() {
        FlowContext current = this;
        FlowContext lastNonReturningSubRoutine = null;
        while (current != null) {
            if (current.isNonReturningContext()) {
                lastNonReturningSubRoutine = current;
            }
            if (current.isContinuable()) {
                if (lastNonReturningSubRoutine == null) {
                    return current;
                }
                return lastNonReturningSubRoutine;
            }
            else {
                current = current.getLocalParent();
            }
        }
        return null;
    }
    
    public FlowContext getInitializationContext() {
        return null;
    }
    
    public FlowContext getLocalParent() {
        if (this.associatedNode instanceof AbstractMethodDeclaration || this.associatedNode instanceof TypeDeclaration || this.associatedNode instanceof LambdaExpression) {
            return null;
        }
        return this.parent;
    }
    
    public String individualToString() {
        return "Flow context";
    }
    
    public FlowInfo initsOnBreak() {
        return FlowInfo.DEAD_END;
    }
    
    public UnconditionalFlowInfo initsOnReturn() {
        return FlowInfo.DEAD_END;
    }
    
    public boolean isBreakable() {
        return false;
    }
    
    public boolean isContinuable() {
        return false;
    }
    
    public boolean isNonReturningContext() {
        return false;
    }
    
    public boolean isSubRoutine() {
        return false;
    }
    
    public char[] labelName() {
        return null;
    }
    
    public void markFinallyNullStatus(final LocalVariableBinding local, final int nullStatus) {
        if (this.initsOnFinally == null) {
            return;
        }
        if (this.conditionalLevel == -1) {
            return;
        }
        if (this.conditionalLevel == 0) {
            this.initsOnFinally.markNullStatus(local, nullStatus);
            return;
        }
        final UnconditionalFlowInfo newInfo = this.initsOnFinally.unconditionalCopy();
        newInfo.markNullStatus(local, nullStatus);
        this.initsOnFinally = this.initsOnFinally.mergedWith(newInfo);
    }
    
    public void mergeFinallyNullInfo(final FlowInfo flowInfo) {
        if (this.initsOnFinally == null) {
            return;
        }
        if (this.conditionalLevel == -1) {
            return;
        }
        if (this.conditionalLevel == 0) {
            this.initsOnFinally.addNullInfoFrom(flowInfo);
            return;
        }
        this.initsOnFinally = this.initsOnFinally.mergedWith(flowInfo.unconditionalCopy());
    }
    
    public void recordAbruptExit() {
        if (this.conditionalLevel > -1) {
            ++this.conditionalLevel;
            if (!(this instanceof ExceptionHandlingFlowContext) && this.parent != null) {
                this.parent.recordAbruptExit();
            }
        }
    }
    
    public void recordBreakFrom(final FlowInfo flowInfo) {
    }
    
    public void recordBreakTo(final FlowContext targetContext) {
    }
    
    public void recordContinueFrom(final FlowContext innerFlowContext, final FlowInfo flowInfo) {
    }
    
    public boolean recordExitAgainstResource(final BlockScope scope, final FlowInfo flowInfo, final FakedTrackingVariable trackingVar, final ASTNode reference) {
        return false;
    }
    
    protected void recordProvidedExpectedTypes(final TypeBinding providedType, final TypeBinding expectedType, final int nullCount) {
        if (nullCount == 0) {
            this.providedExpectedTypes = new TypeBinding[5][];
        }
        else if (this.providedExpectedTypes == null) {
            int size;
            for (size = 5; size <= nullCount; size *= 2) {}
            this.providedExpectedTypes = new TypeBinding[size][];
        }
        else if (nullCount >= this.providedExpectedTypes.length) {
            final int oldLen = this.providedExpectedTypes.length;
            System.arraycopy(this.providedExpectedTypes, 0, this.providedExpectedTypes = new TypeBinding[nullCount * 2][], 0, oldLen);
        }
        this.providedExpectedTypes[nullCount] = new TypeBinding[] { providedType, expectedType };
    }
    
    protected boolean recordFinalAssignment(final VariableBinding variable, final Reference finalReference) {
        return true;
    }
    
    protected void recordNullReference(final LocalVariableBinding local, final ASTNode location, final int checkType, final FlowInfo nullInfo) {
    }
    
    public void recordUnboxing(final Scope scope, final Expression expression, final int nullStatus, final FlowInfo flowInfo) {
        this.checkUnboxing(scope, expression, flowInfo);
    }
    
    protected void checkUnboxing(final Scope scope, final Expression expression, final FlowInfo flowInfo) {
        final int status = expression.nullStatus(flowInfo, this);
        if ((status & 0x2) != 0x0) {
            scope.problemReporter().nullUnboxing(expression, expression.resolvedType);
            return;
        }
        if ((status & 0x10) != 0x0) {
            scope.problemReporter().potentialNullUnboxing(expression, expression.resolvedType);
            return;
        }
        if ((status & 0x4) != 0x0) {
            return;
        }
        if (this.parent != null) {
            this.parent.recordUnboxing(scope, expression, 1, flowInfo);
        }
    }
    
    public void recordReturnFrom(final UnconditionalFlowInfo flowInfo) {
    }
    
    public void recordSettingFinal(final VariableBinding variable, final Reference finalReference, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) == 0x0) {
            for (FlowContext context = this; context != null; context = context.getLocalParent()) {
                if (!context.recordFinalAssignment(variable, finalReference)) {
                    break;
                }
            }
        }
    }
    
    public void recordUsingNullReference(final Scope scope, final LocalVariableBinding local, final ASTNode location, int checkType, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x3) != 0x0 || flowInfo.isDefinitelyUnknown(local)) {
            return;
        }
        checkType |= (this.tagBits & 0x1000);
        final int checkTypeWithoutHideNullWarning = checkType & 0xFFFF0FFF;
        Label_0569: {
            switch (checkTypeWithoutHideNullWarning) {
                case 256:
                case 512: {
                    if (flowInfo.isDefinitelyNonNull(local)) {
                        if (checkTypeWithoutHideNullWarning == 512) {
                            if ((checkType & 0x1000) == 0x0) {
                                scope.problemReporter().localVariableRedundantCheckOnNonNull(local, location);
                            }
                            flowInfo.initsWhenFalse().setReachMode(2);
                        }
                        else {
                            scope.problemReporter().localVariableNonNullComparedToNull(local, location);
                            flowInfo.initsWhenTrue().setReachMode(2);
                        }
                        return;
                    }
                    if (flowInfo.cannotBeDefinitelyNullOrNonNull(local)) {
                        return;
                    }
                }
                case 257:
                case 513:
                case 769:
                case 1025: {
                    final Expression reference = (Expression)location;
                    if (flowInfo.isDefinitelyNull(local)) {
                        switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                            case 256: {
                                if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                    scope.problemReporter().localVariableNullReference(local, reference);
                                    return;
                                }
                                if ((checkType & 0x1000) == 0x0) {
                                    scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
                                }
                                flowInfo.initsWhenFalse().setReachMode(2);
                                return;
                            }
                            case 512: {
                                if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                    scope.problemReporter().localVariableNullReference(local, reference);
                                    return;
                                }
                                scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
                                flowInfo.initsWhenTrue().setReachMode(2);
                                return;
                            }
                            case 768: {
                                scope.problemReporter().localVariableRedundantNullAssignment(local, reference);
                                return;
                            }
                            case 1024: {
                                scope.problemReporter().localVariableNullInstanceof(local, reference);
                                return;
                            }
                            default: {
                                break Label_0569;
                            }
                        }
                    }
                    else if (flowInfo.isPotentiallyNull(local)) {
                        switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                            case 256: {
                                if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                    scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                    return;
                                }
                                break Label_0569;
                            }
                            case 512: {
                                if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                    scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                    return;
                                }
                                break Label_0569;
                            }
                            default: {
                                break Label_0569;
                            }
                        }
                    }
                    else {
                        if (flowInfo.cannotBeDefinitelyNullOrNonNull(local)) {
                            return;
                        }
                        break;
                    }
                    break;
                }
                case 3: {
                    if (flowInfo.isDefinitelyNull(local)) {
                        scope.problemReporter().localVariableNullReference(local, location);
                        return;
                    }
                    if (!flowInfo.isPotentiallyNull(local)) {
                        break;
                    }
                    if (local.type.isFreeTypeVariable()) {
                        scope.problemReporter().localVariableFreeTypeVariableReference(local, location);
                        return;
                    }
                    scope.problemReporter().localVariablePotentialNullReference(local, location);
                    return;
                }
            }
        }
        if (this.parent != null) {
            this.parent.recordUsingNullReference(scope, local, location, checkType, flowInfo);
        }
    }
    
    void removeFinalAssignmentIfAny(final Reference reference) {
    }
    
    public SubRoutineStatement subroutine() {
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        FlowContext current = this;
        int parentsCount = 0;
        while ((current = current.parent) != null) {
            ++parentsCount;
        }
        final FlowContext[] parents = new FlowContext[parentsCount + 1];
        current = this;
        for (int index = parentsCount; index >= 0; parents[index--] = current, current = current.parent) {}
        for (int i = 0; i < parentsCount; ++i) {
            for (int j = 0; j < i; ++j) {
                buffer.append('\t');
            }
            buffer.append(parents[i].individualToString()).append('\n');
        }
        buffer.append('*');
        for (int k = 0; k < parentsCount + 1; ++k) {
            buffer.append('\t');
        }
        buffer.append(this.individualToString()).append('\n');
        return buffer.toString();
    }
    
    public void recordNullityMismatch(final BlockScope currentScope, final Expression expression, final TypeBinding providedType, final TypeBinding expectedType, final FlowInfo flowInfo, final int nullStatus, final NullAnnotationMatching annotationStatus) {
        if (providedType == null) {
            return;
        }
        if (expression.localVariableBinding() != null) {
            for (FlowContext currentContext = this; currentContext != null; currentContext = currentContext.parent) {
                int isInsideAssert = 0;
                if ((this.tagBits & 0x1000) != 0x0) {
                    isInsideAssert = 4096;
                }
                if (currentContext.internalRecordNullityMismatch(expression, providedType, flowInfo, nullStatus, expectedType, 0x80 | isInsideAssert)) {
                    return;
                }
            }
        }
        if (annotationStatus != null) {
            currentScope.problemReporter().nullityMismatchingTypeAnnotation(expression, providedType, expectedType, annotationStatus);
        }
        else {
            currentScope.problemReporter().nullityMismatch(expression, providedType, expectedType, nullStatus, currentScope.environment().getNonNullAnnotationName());
        }
    }
    
    protected boolean internalRecordNullityMismatch(final Expression expression, final TypeBinding providedType, final FlowInfo flowInfo, final int nullStatus, final TypeBinding expectedType, final int checkType) {
        return false;
    }
}
