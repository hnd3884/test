package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.ast.FakedTrackingVariable;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;

public class LoopingFlowContext extends SwitchFlowContext
{
    public BranchLabel continueLabel;
    public UnconditionalFlowInfo initsOnContinue;
    private UnconditionalFlowInfo upstreamNullFlowInfo;
    private LoopingFlowContext[] innerFlowContexts;
    private UnconditionalFlowInfo[] innerFlowInfos;
    private int innerFlowContextsCount;
    private LabelFlowContext[] breakTargetContexts;
    private int breakTargetsCount;
    Reference[] finalAssignments;
    VariableBinding[] finalVariables;
    int assignCount;
    LocalVariableBinding[] nullLocals;
    ASTNode[] nullReferences;
    int[] nullCheckTypes;
    UnconditionalFlowInfo[] nullInfos;
    int nullCount;
    private ArrayList escapingExceptionCatchSites;
    Scope associatedScope;
    
    public LoopingFlowContext(final FlowContext parent, final FlowInfo upstreamNullFlowInfo, final ASTNode associatedNode, final BranchLabel breakLabel, final BranchLabel continueLabel, final Scope associatedScope, final boolean isPreTest) {
        super(parent, associatedNode, breakLabel, isPreTest);
        this.initsOnContinue = FlowInfo.DEAD_END;
        this.innerFlowContexts = null;
        this.innerFlowInfos = null;
        this.innerFlowContextsCount = 0;
        this.breakTargetContexts = null;
        this.breakTargetsCount = 0;
        this.assignCount = 0;
        this.escapingExceptionCatchSites = null;
        this.tagBits |= 0x2;
        this.continueLabel = continueLabel;
        this.associatedScope = associatedScope;
        this.upstreamNullFlowInfo = upstreamNullFlowInfo.unconditionalCopy();
    }
    
    public void complainOnDeferredFinalChecks(final BlockScope scope, final FlowInfo flowInfo) {
        for (int i = 0; i < this.assignCount; ++i) {
            final VariableBinding variable = this.finalVariables[i];
            if (variable != null) {
                boolean complained = false;
                if (variable instanceof FieldBinding) {
                    if (flowInfo.isPotentiallyAssigned((FieldBinding)variable)) {
                        complained = true;
                        scope.problemReporter().duplicateInitializationOfBlankFinalField((FieldBinding)variable, this.finalAssignments[i]);
                    }
                }
                else if (flowInfo.isPotentiallyAssigned((LocalVariableBinding)variable)) {
                    final VariableBinding variableBinding = variable;
                    variableBinding.tagBits &= 0xFFFFFFFFFFFFF7FFL;
                    if (variable.isFinal()) {
                        complained = true;
                        scope.problemReporter().duplicateInitializationOfFinalLocal((LocalVariableBinding)variable, this.finalAssignments[i]);
                    }
                }
                if (complained) {
                    for (FlowContext context = this.getLocalParent(); context != null; context = context.getLocalParent()) {
                        context.removeFinalAssignmentIfAny(this.finalAssignments[i]);
                    }
                }
            }
        }
    }
    
    public void complainOnDeferredNullChecks(final BlockScope scope, final FlowInfo callerFlowInfo) {
        this.complainOnDeferredNullChecks(scope, callerFlowInfo, true);
    }
    
    public void complainOnDeferredNullChecks(final BlockScope scope, final FlowInfo callerFlowInfo, final boolean updateInitsOnBreak) {
        for (int i = 0; i < this.innerFlowContextsCount; ++i) {
            this.upstreamNullFlowInfo.addPotentialNullInfoFrom(this.innerFlowContexts[i].upstreamNullFlowInfo).addPotentialNullInfoFrom(this.innerFlowInfos[i]);
        }
        this.innerFlowContextsCount = 0;
        final FlowInfo upstreamCopy = this.upstreamNullFlowInfo.copy();
        final UnconditionalFlowInfo incomingInfo = this.upstreamNullFlowInfo.addPotentialNullInfoFrom(callerFlowInfo.unconditionalInitsWithoutSideEffect());
        if ((this.tagBits & 0x1) != 0x0) {
            for (int j = 0; j < this.nullCount; ++j) {
                final LocalVariableBinding local = this.nullLocals[j];
                final ASTNode location = this.nullReferences[j];
                final FlowInfo flowInfo = (this.nullInfos[j] != null) ? incomingInfo.copy().addNullInfoFrom(this.nullInfos[j]) : incomingInfo;
                Label_1041: {
                    switch (this.nullCheckTypes[j] & 0xFFFF0FFF) {
                        case 258:
                        case 514: {
                            if (!flowInfo.isDefinitelyNonNull(local)) {
                                break;
                            }
                            this.nullReferences[j] = null;
                            if ((this.nullCheckTypes[j] & 0xFFFF0FFF) != 0x202) {
                                scope.problemReporter().localVariableNonNullComparedToNull(local, location);
                                continue;
                            }
                            if ((this.nullCheckTypes[j] & 0x1000) == 0x0) {
                                scope.problemReporter().localVariableRedundantCheckOnNonNull(local, location);
                            }
                            continue;
                        }
                        case 256:
                        case 512: {
                            if (flowInfo.isDefinitelyNonNull(local)) {
                                this.nullReferences[j] = null;
                                if ((this.nullCheckTypes[j] & 0xFFFF0FFF) != 0x200) {
                                    scope.problemReporter().localVariableNonNullComparedToNull(local, location);
                                    continue;
                                }
                                if ((this.nullCheckTypes[j] & 0x1000) == 0x0) {
                                    scope.problemReporter().localVariableRedundantCheckOnNonNull(local, location);
                                }
                                continue;
                            }
                            else {
                                if (!flowInfo.isDefinitelyNull(local)) {
                                    break;
                                }
                                this.nullReferences[j] = null;
                                if ((this.nullCheckTypes[j] & 0xFFFF0FFF) != 0x100) {
                                    scope.problemReporter().localVariableNullComparedToNonNull(local, location);
                                    continue;
                                }
                                if ((this.nullCheckTypes[j] & 0x1000) == 0x0) {
                                    scope.problemReporter().localVariableRedundantCheckOnNull(local, location);
                                }
                                continue;
                            }
                            break;
                        }
                        case 257:
                        case 513:
                        case 769:
                        case 1025: {
                            final Expression expression = (Expression)location;
                            if (flowInfo.isDefinitelyNull(local)) {
                                this.nullReferences[j] = null;
                                switch (this.nullCheckTypes[j] & 0xFFFF0F00) {
                                    case 256: {
                                        if ((this.nullCheckTypes[j] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                            scope.problemReporter().localVariableNullReference(local, expression);
                                            continue;
                                        }
                                        if ((this.nullCheckTypes[j] & 0x1000) == 0x0) {
                                            scope.problemReporter().localVariableRedundantCheckOnNull(local, expression);
                                        }
                                        continue;
                                    }
                                    case 512: {
                                        if ((this.nullCheckTypes[j] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                            scope.problemReporter().localVariableNullReference(local, expression);
                                            continue;
                                        }
                                        scope.problemReporter().localVariableNullComparedToNonNull(local, expression);
                                        continue;
                                    }
                                    case 768: {
                                        scope.problemReporter().localVariableRedundantNullAssignment(local, expression);
                                        continue;
                                    }
                                    case 1024: {
                                        scope.problemReporter().localVariableNullInstanceof(local, expression);
                                        continue;
                                    }
                                    default: {
                                        break Label_1041;
                                    }
                                }
                            }
                            else {
                                if (!flowInfo.isPotentiallyNull(local)) {
                                    break;
                                }
                                switch (this.nullCheckTypes[j] & 0xFFFF0F00) {
                                    case 256: {
                                        this.nullReferences[j] = null;
                                        if ((this.nullCheckTypes[j] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                            scope.problemReporter().localVariablePotentialNullReference(local, expression);
                                            continue;
                                        }
                                        break Label_1041;
                                    }
                                    case 512: {
                                        this.nullReferences[j] = null;
                                        if ((this.nullCheckTypes[j] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                            scope.problemReporter().localVariablePotentialNullReference(local, expression);
                                            continue;
                                        }
                                        break Label_1041;
                                    }
                                    default: {
                                        break Label_1041;
                                    }
                                }
                            }
                            break;
                        }
                        case 3: {
                            if (flowInfo.isDefinitelyNull(local)) {
                                this.nullReferences[j] = null;
                                scope.problemReporter().localVariableNullReference(local, location);
                                continue;
                            }
                            break;
                        }
                        case 128: {
                            final int nullStatus = flowInfo.nullStatus(local);
                            if (nullStatus != 4) {
                                this.parent.recordNullityMismatch(scope, (Expression)location, this.providedExpectedTypes[j][0], this.providedExpectedTypes[j][1], flowInfo, nullStatus, null);
                            }
                            continue;
                        }
                        case 2048: {
                            final FakedTrackingVariable trackingVar = local.closeTracker;
                            if (trackingVar == null) {
                                break;
                            }
                            if (trackingVar.hasDefinitelyNoResource(flowInfo)) {
                                continue;
                            }
                            if (trackingVar.isClosedInFinallyOfEnclosing(scope)) {
                                continue;
                            }
                            if (this.parent.recordExitAgainstResource(scope, flowInfo, trackingVar, location)) {
                                this.nullReferences[j] = null;
                                continue;
                            }
                            break;
                        }
                        case 16: {
                            this.checkUnboxing(scope, (Expression)location, flowInfo);
                            continue;
                        }
                    }
                }
                if (this.nullCheckTypes[j] != 3 || !upstreamCopy.isDefinitelyNonNull(local)) {
                    this.parent.recordUsingNullReference(scope, local, location, this.nullCheckTypes[j], flowInfo);
                }
            }
        }
        else {
            for (int j = 0; j < this.nullCount; ++j) {
                final ASTNode location2 = this.nullReferences[j];
                final LocalVariableBinding local2 = this.nullLocals[j];
                final FlowInfo flowInfo = (this.nullInfos[j] != null) ? incomingInfo.copy().addNullInfoFrom(this.nullInfos[j]) : incomingInfo;
                switch (this.nullCheckTypes[j] & 0xFFFF0FFF) {
                    case 256:
                    case 512: {
                        if (!flowInfo.isDefinitelyNonNull(local2))
                        this.nullReferences[j] = null;
                        if ((this.nullCheckTypes[j] & 0xFFFF0FFF) != 0x200) {
                            scope.problemReporter().localVariableNonNullComparedToNull(local2, location2);
                            break;
                        }
                        if ((this.nullCheckTypes[j] & 0x1000) == 0x0) {
                            scope.problemReporter().localVariableRedundantCheckOnNonNull(local2, location2);
                            break;
                        }
                        break;
                    }
                    case 257:
                    case 513:
                    case 769:
                    case 1025: {
                        final Expression expression = (Expression)location2;
                        if (flowInfo.isDefinitelyNull(local2)) {
                            this.nullReferences[j] = null;
                            switch (this.nullCheckTypes[j] & 0xFFFF0F00) {
                                case 256: {
                                    if ((this.nullCheckTypes[j] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                        scope.problemReporter().localVariableNullReference(local2, expression);
                                        continue;
                                    }
                                    if ((this.nullCheckTypes[j] & 0x1000) == 0x0) {
                                        scope.problemReporter().localVariableRedundantCheckOnNull(local2, expression);
                                        continue;
                                    }
                                    continue;
                                }
                                case 512: {
                                    if ((this.nullCheckTypes[j] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                        scope.problemReporter().localVariableNullReference(local2, expression);
                                        continue;
                                    }
                                    scope.problemReporter().localVariableNullComparedToNonNull(local2, expression);
                                    continue;
                                }
                                case 768: {
                                    scope.problemReporter().localVariableRedundantNullAssignment(local2, expression);
                                    continue;
                                }
                                case 1024: {
                                    scope.problemReporter().localVariableNullInstanceof(local2, expression);
                                    continue;
                                }
                                default: {
                                    continue;
                                }
                            }
                        }
                        else {
                            if (!flowInfo.isPotentiallyNull(local2)) {
                                break;
                            }
                            switch (this.nullCheckTypes[j] & 0xFFFF0F00) {
                                case 256: {
                                    this.nullReferences[j] = null;
                                    if ((this.nullCheckTypes[j] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                        scope.problemReporter().localVariablePotentialNullReference(local2, expression);
                                        continue;
                                    }
                                    continue;
                                }
                                case 512: {
                                    this.nullReferences[j] = null;
                                    if ((this.nullCheckTypes[j] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                        scope.problemReporter().localVariablePotentialNullReference(local2, expression);
                                        continue;
                                    }
                                    continue;
                                }
                                default: {
                                    continue;
                                }
                            }
                        }
                        break;
                    }
                    case 3: {
                        if (flowInfo.isDefinitelyNull(local2)) {
                            this.nullReferences[j] = null;
                            scope.problemReporter().localVariableNullReference(local2, location2);
                            break;
                        }
                        if (flowInfo.isPotentiallyNull(local2)) {
                            this.nullReferences[j] = null;
                            scope.problemReporter().localVariablePotentialNullReference(local2, location2);
                            break;
                        }
                        break;
                    }
                    case 128: {
                        final int nullStatus = flowInfo.nullStatus(local2);
                        if (nullStatus != 4) {
                            final char[][] annotationName = scope.environment().getNonNullAnnotationName();
                            scope.problemReporter().nullityMismatch((Expression)location2, this.providedExpectedTypes[j][0], this.providedExpectedTypes[j][1], nullStatus, annotationName);
                            break;
                        }
                        break;
                    }
                    case 2048: {
                        int nullStatus = flowInfo.nullStatus(local2);
                        if (nullStatus == 4) {
                            break;
                        }
                        final FakedTrackingVariable closeTracker = local2.closeTracker;
                        if (closeTracker == null) {
                            break;
                        }
                        if (closeTracker.hasDefinitelyNoResource(flowInfo)) {
                            break;
                        }
                        if (closeTracker.isClosedInFinallyOfEnclosing(scope)) {
                            break;
                        }
                        nullStatus = closeTracker.findMostSpecificStatus(flowInfo, scope, null);
                        closeTracker.recordErrorLocation(this.nullReferences[j], nullStatus);
                        closeTracker.reportRecordedErrors(scope, nullStatus, flowInfo.reachMode() != 0);
                        this.nullReferences[j] = null;
                        break;
                    }
                    case 16: {
                        this.checkUnboxing(scope, (Expression)location2, flowInfo);
                        break;
                    }
                }
            }
        }
        if (updateInitsOnBreak) {
            this.initsOnBreak.addPotentialNullInfoFrom(incomingInfo);
            for (int j = 0; j < this.breakTargetsCount; ++j) {
                this.breakTargetContexts[j].initsOnBreak.addPotentialNullInfoFrom(incomingInfo);
            }
        }
    }
    
    @Override
    public BranchLabel continueLabel() {
        return this.continueLabel;
    }
    
    @Override
    public String individualToString() {
        final StringBuffer buffer = new StringBuffer("Looping flow context");
        buffer.append("[initsOnBreak - ").append(this.initsOnBreak.toString()).append(']');
        buffer.append("[initsOnContinue - ").append(this.initsOnContinue.toString()).append(']');
        buffer.append("[finalAssignments count - ").append(this.assignCount).append(']');
        buffer.append("[nullReferences count - ").append(this.nullCount).append(']');
        return buffer.toString();
    }
    
    @Override
    public boolean isContinuable() {
        return true;
    }
    
    public boolean isContinuedTo() {
        return this.initsOnContinue != FlowInfo.DEAD_END;
    }
    
    @Override
    public void recordBreakTo(final FlowContext targetContext) {
        if (targetContext instanceof LabelFlowContext) {
            final int current;
            if ((current = this.breakTargetsCount++) == 0) {
                this.breakTargetContexts = new LabelFlowContext[2];
            }
            else if (current == this.breakTargetContexts.length) {
                System.arraycopy(this.breakTargetContexts, 0, this.breakTargetContexts = new LabelFlowContext[current + 2], 0, current);
            }
            this.breakTargetContexts[current] = (LabelFlowContext)targetContext;
        }
    }
    
    @Override
    public void recordContinueFrom(final FlowContext innerFlowContext, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) == 0x0) {
            if ((this.initsOnContinue.tagBits & 0x1) == 0x0) {
                this.initsOnContinue = this.initsOnContinue.mergedWith(flowInfo.unconditionalInitsWithoutSideEffect());
            }
            else {
                this.initsOnContinue = flowInfo.unconditionalCopy();
            }
            FlowContext inner;
            for (inner = innerFlowContext; inner != this && !(inner instanceof LoopingFlowContext); inner = inner.parent) {}
            if (inner == this) {
                this.upstreamNullFlowInfo.addPotentialNullInfoFrom(flowInfo.unconditionalInitsWithoutSideEffect());
            }
            else {
                int length = 0;
                if (this.innerFlowContexts == null) {
                    this.innerFlowContexts = new LoopingFlowContext[5];
                    this.innerFlowInfos = new UnconditionalFlowInfo[5];
                }
                else if (this.innerFlowContextsCount == (length = this.innerFlowContexts.length) - 1) {
                    System.arraycopy(this.innerFlowContexts, 0, this.innerFlowContexts = new LoopingFlowContext[length + 5], 0, length);
                    System.arraycopy(this.innerFlowInfos, 0, this.innerFlowInfos = new UnconditionalFlowInfo[length + 5], 0, length);
                }
                this.innerFlowContexts[this.innerFlowContextsCount] = (LoopingFlowContext)inner;
                this.innerFlowInfos[this.innerFlowContextsCount++] = flowInfo.unconditionalInitsWithoutSideEffect();
            }
        }
    }
    
    @Override
    protected boolean recordFinalAssignment(final VariableBinding binding, final Reference finalAssignment) {
        if (binding instanceof LocalVariableBinding) {
            Scope scope = ((LocalVariableBinding)binding).declaringScope;
            while ((scope = scope.parent) != null) {
                if (scope == this.associatedScope) {
                    return false;
                }
            }
        }
        if (this.assignCount == 0) {
            this.finalAssignments = new Reference[5];
            this.finalVariables = new VariableBinding[5];
        }
        else {
            if (this.assignCount == this.finalAssignments.length) {
                System.arraycopy(this.finalAssignments, 0, this.finalAssignments = new Reference[this.assignCount * 2], 0, this.assignCount);
            }
            System.arraycopy(this.finalVariables, 0, this.finalVariables = new VariableBinding[this.assignCount * 2], 0, this.assignCount);
        }
        this.finalAssignments[this.assignCount] = finalAssignment;
        this.finalVariables[this.assignCount++] = binding;
        return true;
    }
    
    @Override
    protected void recordNullReference(final LocalVariableBinding local, final ASTNode expression, final int checkType, final FlowInfo nullInfo) {
        if (this.nullCount == 0) {
            this.nullLocals = new LocalVariableBinding[5];
            this.nullReferences = new ASTNode[5];
            this.nullCheckTypes = new int[5];
            this.nullInfos = new UnconditionalFlowInfo[5];
        }
        else if (this.nullCount == this.nullLocals.length) {
            System.arraycopy(this.nullLocals, 0, this.nullLocals = new LocalVariableBinding[this.nullCount * 2], 0, this.nullCount);
            System.arraycopy(this.nullReferences, 0, this.nullReferences = new ASTNode[this.nullCount * 2], 0, this.nullCount);
            System.arraycopy(this.nullCheckTypes, 0, this.nullCheckTypes = new int[this.nullCount * 2], 0, this.nullCount);
            System.arraycopy(this.nullInfos, 0, this.nullInfos = new UnconditionalFlowInfo[this.nullCount * 2], 0, this.nullCount);
        }
        this.nullLocals[this.nullCount] = local;
        this.nullReferences[this.nullCount] = expression;
        this.nullCheckTypes[this.nullCount] = checkType;
        this.nullInfos[this.nullCount++] = ((nullInfo != null) ? nullInfo.unconditionalCopy() : null);
    }
    
    @Override
    public void recordUnboxing(final Scope scope, final Expression expression, final int nullStatus, final FlowInfo flowInfo) {
        if (nullStatus == 2) {
            super.recordUnboxing(scope, expression, nullStatus, flowInfo);
        }
        else {
            this.recordNullReference(null, expression, 16, flowInfo);
        }
    }
    
    @Override
    public boolean recordExitAgainstResource(final BlockScope scope, final FlowInfo flowInfo, final FakedTrackingVariable trackingVar, final ASTNode reference) {
        final LocalVariableBinding local = trackingVar.binding;
        if (flowInfo.isDefinitelyNonNull(local)) {
            return false;
        }
        if (flowInfo.isDefinitelyNull(local)) {
            scope.problemReporter().unclosedCloseable(trackingVar, reference);
            return true;
        }
        if (flowInfo.isPotentiallyNull(local)) {
            scope.problemReporter().potentiallyUnclosedCloseable(trackingVar, reference);
            return true;
        }
        this.recordNullReference(trackingVar.binding, reference, 2048, flowInfo);
        return true;
    }
    
    @Override
    public void recordUsingNullReference(final Scope scope, final LocalVariableBinding local, final ASTNode location, int checkType, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x3) != 0x0 || flowInfo.isDefinitelyUnknown(local)) {
            return;
        }
        checkType |= (this.tagBits & 0x1000);
        final int checkTypeWithoutHideNullWarning = checkType & 0xFFFF0FFF;
        switch (checkTypeWithoutHideNullWarning) {
            case 256:
            case 512: {
                final Expression reference = (Expression)location;
                if (flowInfo.isDefinitelyNonNull(local)) {
                    if (checkTypeWithoutHideNullWarning == 512) {
                        if ((this.tagBits & 0x1000) == 0x0) {
                            scope.problemReporter().localVariableRedundantCheckOnNonNull(local, reference);
                        }
                        flowInfo.initsWhenFalse().setReachMode(2);
                    }
                    else {
                        scope.problemReporter().localVariableNonNullComparedToNull(local, reference);
                        flowInfo.initsWhenTrue().setReachMode(2);
                    }
                }
                else if (flowInfo.isDefinitelyNull(local)) {
                    if (checkTypeWithoutHideNullWarning == 256) {
                        if ((this.tagBits & 0x1000) == 0x0) {
                            scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
                        }
                        flowInfo.initsWhenFalse().setReachMode(2);
                    }
                    else {
                        scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
                        flowInfo.initsWhenTrue().setReachMode(2);
                    }
                }
                else if (this.upstreamNullFlowInfo.isDefinitelyNonNull(local) && !flowInfo.isPotentiallyNull(local) && !flowInfo.isPotentiallyUnknown(local)) {
                    this.recordNullReference(local, reference, checkType, flowInfo);
                    flowInfo.markAsDefinitelyNonNull(local);
                }
                else {
                    if (flowInfo.cannotBeDefinitelyNullOrNonNull(local)) {
                        return;
                    }
                    if (flowInfo.isPotentiallyNonNull(local)) {
                        this.recordNullReference(local, reference, 0x2 | (checkType & 0xFFFFFF00), flowInfo);
                    }
                    else if (flowInfo.isPotentiallyNull(local)) {
                        this.recordNullReference(local, reference, 0x1 | (checkType & 0xFFFFFF00), flowInfo);
                    }
                    else {
                        this.recordNullReference(local, reference, checkType, flowInfo);
                    }
                }
                return;
            }
            case 257:
            case 513:
            case 769:
            case 1025: {
                final Expression reference = (Expression)location;
                if (flowInfo.isPotentiallyNonNull(local) || flowInfo.isPotentiallyUnknown(local) || flowInfo.isProtectedNonNull(local)) {
                    return;
                }
                if (flowInfo.isDefinitelyNull(local)) {
                    switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                        case 256: {
                            if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                scope.problemReporter().localVariableNullReference(local, reference);
                                return;
                            }
                            if ((this.tagBits & 0x1000) == 0x0) {
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
                    }
                }
                else if (flowInfo.isPotentiallyNull(local)) {
                    switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                        case 256: {
                            if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                return;
                            }
                            break;
                        }
                        case 512: {
                            if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                return;
                            }
                            break;
                        }
                    }
                }
                this.recordNullReference(local, reference, checkType, flowInfo);
                return;
            }
            case 3: {
                if (flowInfo.isDefinitelyNonNull(local)) {
                    return;
                }
                if (flowInfo.isDefinitelyNull(local)) {
                    scope.problemReporter().localVariableNullReference(local, location);
                    return;
                }
                if (flowInfo.isPotentiallyNull(local)) {
                    scope.problemReporter().localVariablePotentialNullReference(local, location);
                    return;
                }
                this.recordNullReference(local, location, checkType, flowInfo);
            }
            default: {}
        }
    }
    
    @Override
    void removeFinalAssignmentIfAny(final Reference reference) {
        for (int i = 0; i < this.assignCount; ++i) {
            if (this.finalAssignments[i] == reference) {
                this.finalAssignments[i] = null;
                this.finalVariables[i] = null;
                return;
            }
        }
    }
    
    public void simulateThrowAfterLoopBack(final FlowInfo flowInfo) {
        if (this.escapingExceptionCatchSites != null) {
            for (int i = 0, exceptionCount = this.escapingExceptionCatchSites.size(); i < exceptionCount; ++i) {
                this.escapingExceptionCatchSites.get(i).simulateThrowAfterLoopBack(flowInfo);
            }
            this.escapingExceptionCatchSites = null;
        }
    }
    
    public void recordCatchContextOfEscapingException(final ExceptionHandlingFlowContext catchingContext, final ReferenceBinding caughtException, final FlowInfo exceptionInfo) {
        if (this.escapingExceptionCatchSites == null) {
            this.escapingExceptionCatchSites = new ArrayList(5);
        }
        this.escapingExceptionCatchSites.add(new EscapingExceptionCatchSite(catchingContext, caughtException, exceptionInfo));
    }
    
    public boolean hasEscapingExceptions() {
        return this.escapingExceptionCatchSites != null;
    }
    
    @Override
    protected boolean internalRecordNullityMismatch(final Expression expression, final TypeBinding providedType, final FlowInfo flowInfo, final int nullStatus, final TypeBinding expectedType, final int checkType) {
        this.recordProvidedExpectedTypes(providedType, expectedType, this.nullCount);
        this.recordNullReference(expression.localVariableBinding(), expression, checkType, flowInfo);
        return true;
    }
    
    private static class EscapingExceptionCatchSite
    {
        final ReferenceBinding caughtException;
        final ExceptionHandlingFlowContext catchingContext;
        final FlowInfo exceptionInfo;
        
        public EscapingExceptionCatchSite(final ExceptionHandlingFlowContext catchingContext, final ReferenceBinding caughtException, final FlowInfo exceptionInfo) {
            this.catchingContext = catchingContext;
            this.caughtException = caughtException;
            this.exceptionInfo = exceptionInfo;
        }
        
        void simulateThrowAfterLoopBack(final FlowInfo flowInfo) {
            this.catchingContext.recordHandlingException(this.caughtException, flowInfo.unconditionalCopy().addNullInfoFrom(this.exceptionInfo).unconditionalInits(), null, null, null, true);
        }
    }
}
