package org.eclipse.jdt.internal.compiler.ast;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.flow.FinallyFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import java.util.HashMap;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

public class FakedTrackingVariable extends LocalDeclaration
{
    private static final char[] UNASSIGNED_CLOSEABLE_NAME;
    private static final char[] UNASSIGNED_CLOSEABLE_NAME_TEMPLATE;
    private static final char[] TEMPLATE_ARGUMENT;
    private static final int CLOSE_SEEN = 1;
    private static final int SHARED_WITH_OUTSIDE = 2;
    private static final int OWNED_BY_OUTSIDE = 4;
    private static final int CLOSED_IN_NESTED_METHOD = 8;
    private static final int REPORTED_EXPLICIT_CLOSE = 16;
    private static final int REPORTED_POTENTIAL_LEAK = 32;
    private static final int REPORTED_DEFINITIVE_LEAK = 64;
    public static boolean TEST_372319;
    private int globalClosingState;
    public LocalVariableBinding originalBinding;
    public FakedTrackingVariable innerTracker;
    public FakedTrackingVariable outerTracker;
    MethodScope methodScope;
    private HashMap recordedLocations;
    private ASTNode currentAssignment;
    private FlowContext tryContext;
    
    static {
        UNASSIGNED_CLOSEABLE_NAME = "<unassigned Closeable value>".toCharArray();
        UNASSIGNED_CLOSEABLE_NAME_TEMPLATE = "<unassigned Closeable value from line {0}>".toCharArray();
        TEMPLATE_ARGUMENT = "{0}".toCharArray();
        FakedTrackingVariable.TEST_372319 = false;
    }
    
    public FakedTrackingVariable(final LocalVariableBinding original, final ASTNode location, final FlowInfo flowInfo, FlowContext flowContext, final int nullStatus) {
        super(original.name, location.sourceStart, location.sourceEnd);
        this.globalClosingState = 0;
        this.type = new SingleTypeReference(TypeConstants.OBJECT, ((long)this.sourceStart << 32) + this.sourceEnd);
        this.methodScope = original.declaringScope.methodScope();
        this.originalBinding = original;
        while (flowContext != null) {
            if (flowContext instanceof FinallyFlowContext) {
                this.tryContext = ((FinallyFlowContext)flowContext).tryContext;
                break;
            }
            flowContext = flowContext.parent;
        }
        this.resolve(original.declaringScope);
        if (nullStatus != 0) {
            flowInfo.markNullStatus(this.binding, nullStatus);
        }
    }
    
    private FakedTrackingVariable(final BlockScope scope, final ASTNode location, final FlowInfo flowInfo, final int nullStatus) {
        super(FakedTrackingVariable.UNASSIGNED_CLOSEABLE_NAME, location.sourceStart, location.sourceEnd);
        this.globalClosingState = 0;
        this.type = new SingleTypeReference(TypeConstants.OBJECT, ((long)this.sourceStart << 32) + this.sourceEnd);
        this.methodScope = scope.methodScope();
        this.originalBinding = null;
        this.resolve(scope);
        if (nullStatus != 0) {
            flowInfo.markNullStatus(this.binding, nullStatus);
        }
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        this.binding = new LocalVariableBinding(this.name, scope.getJavaLangObject(), 0, false);
        this.binding.closeTracker = this;
        this.binding.declaringScope = scope;
        this.binding.setConstant(Constant.NotAConstant);
        this.binding.useFlag = 1;
        this.binding.id = scope.registerTrackingVariable(this);
    }
    
    public static FakedTrackingVariable getCloseTrackingVariable(Expression expression, final FlowInfo flowInfo, final FlowContext flowContext) {
        while (true) {
            if (expression instanceof CastExpression) {
                expression = ((CastExpression)expression).expression;
            }
            else {
                if (!(expression instanceof Assignment)) {
                    break;
                }
                expression = ((Assignment)expression).expression;
            }
        }
        if (!(expression instanceof ConditionalExpression)) {
            if (expression instanceof SingleNameReference) {
                final SingleNameReference name = (SingleNameReference)expression;
                if (name.binding instanceof LocalVariableBinding) {
                    final LocalVariableBinding local = (LocalVariableBinding)name.binding;
                    if (local.closeTracker != null) {
                        return local.closeTracker;
                    }
                    if (!isAnyCloseable(expression.resolvedType)) {
                        return null;
                    }
                    if ((local.tagBits & 0x2000L) != 0x0L) {
                        return null;
                    }
                    final Statement location = local.declaration;
                    local.closeTracker = new FakedTrackingVariable(local, location, flowInfo, flowContext, 1);
                    if (local.isParameter()) {
                        final FakedTrackingVariable closeTracker = local.closeTracker;
                        closeTracker.globalClosingState |= 0x4;
                    }
                    return local.closeTracker;
                }
            }
            else if (expression instanceof AllocationExpression) {
                return ((AllocationExpression)expression).closeTracker;
            }
            return null;
        }
        final FakedTrackingVariable falseTrackingVariable = getCloseTrackingVariable(((ConditionalExpression)expression).valueIfFalse, flowInfo, flowContext);
        if (falseTrackingVariable != null) {
            return falseTrackingVariable;
        }
        return getCloseTrackingVariable(((ConditionalExpression)expression).valueIfTrue, flowInfo, flowContext);
    }
    
    public static void preConnectTrackerAcrossAssignment(final ASTNode location, final LocalVariableBinding local, final Expression rhs, final FlowInfo flowInfo) {
        FakedTrackingVariable closeTracker = null;
        if (containsAllocation(rhs)) {
            closeTracker = local.closeTracker;
            if (closeTracker == null && rhs.resolvedType != TypeBinding.NULL) {
                closeTracker = new FakedTrackingVariable(local, location, flowInfo, null, 1);
                if (local.isParameter()) {
                    final FakedTrackingVariable fakedTrackingVariable = closeTracker;
                    fakedTrackingVariable.globalClosingState |= 0x4;
                }
            }
            if (closeTracker != null) {
                preConnectTrackerAcrossAssignment(closeTracker.currentAssignment = location, local, flowInfo, closeTracker, rhs);
            }
        }
    }
    
    private static boolean containsAllocation(final ASTNode location) {
        if (location instanceof AllocationExpression) {
            return true;
        }
        if (location instanceof ConditionalExpression) {
            final ConditionalExpression conditional = (ConditionalExpression)location;
            return containsAllocation(conditional.valueIfTrue) || containsAllocation(conditional.valueIfFalse);
        }
        return location instanceof CastExpression && containsAllocation(((CastExpression)location).expression);
    }
    
    private static void preConnectTrackerAcrossAssignment(final ASTNode location, final LocalVariableBinding local, final FlowInfo flowInfo, final FakedTrackingVariable closeTracker, final Expression expression) {
        if (expression instanceof AllocationExpression) {
            preConnectTrackerAcrossAssignment(location, local, flowInfo, (AllocationExpression)expression, closeTracker);
        }
        else if (expression instanceof ConditionalExpression) {
            preConnectTrackerAcrossAssignment(location, local, flowInfo, (ConditionalExpression)expression, closeTracker);
        }
        else if (expression instanceof CastExpression) {
            preConnectTrackerAcrossAssignment(location, local, ((CastExpression)expression).expression, flowInfo);
        }
    }
    
    private static void preConnectTrackerAcrossAssignment(final ASTNode location, final LocalVariableBinding local, final FlowInfo flowInfo, final ConditionalExpression conditional, final FakedTrackingVariable closeTracker) {
        preConnectTrackerAcrossAssignment(location, local, flowInfo, closeTracker, conditional.valueIfFalse);
        preConnectTrackerAcrossAssignment(location, local, flowInfo, closeTracker, conditional.valueIfTrue);
    }
    
    private static void preConnectTrackerAcrossAssignment(final ASTNode location, final LocalVariableBinding local, final FlowInfo flowInfo, final AllocationExpression allocationExpression, final FakedTrackingVariable closeTracker) {
        allocationExpression.closeTracker = closeTracker;
        if (allocationExpression.arguments != null && allocationExpression.arguments.length > 0) {
            preConnectTrackerAcrossAssignment(location, local, allocationExpression.arguments[0], flowInfo);
        }
    }
    
    public static void analyseCloseableAllocation(final BlockScope scope, final FlowInfo flowInfo, final AllocationExpression allocation) {
        if (allocation.resolvedType.hasTypeBit(8)) {
            if (allocation.closeTracker != null) {
                allocation.closeTracker.withdraw();
                allocation.closeTracker = null;
            }
        }
        else if (allocation.resolvedType.hasTypeBit(4)) {
            boolean isWrapper = true;
            if (allocation.arguments != null && allocation.arguments.length > 0) {
                FakedTrackingVariable innerTracker = findCloseTracker(scope, flowInfo, allocation.arguments[0]);
                if (innerTracker != null) {
                    FakedTrackingVariable currentInner = innerTracker;
                    while (currentInner != allocation.closeTracker) {
                        currentInner = currentInner.innerTracker;
                        if (currentInner == null) {
                            int newStatus = 2;
                            if (allocation.closeTracker == null) {
                                allocation.closeTracker = new FakedTrackingVariable(scope, allocation, flowInfo, 1);
                            }
                            else if (scope.finallyInfo != null) {
                                final int finallyStatus = scope.finallyInfo.nullStatus(allocation.closeTracker.binding);
                                if (finallyStatus != 1) {
                                    newStatus = finallyStatus;
                                }
                            }
                            if (allocation.closeTracker.innerTracker != null) {
                                innerTracker = pickMoreUnsafe(allocation.closeTracker.innerTracker, innerTracker, scope, flowInfo);
                            }
                            allocation.closeTracker.innerTracker = innerTracker;
                            innerTracker.outerTracker = allocation.closeTracker;
                            flowInfo.markNullStatus(allocation.closeTracker.binding, newStatus);
                            if (newStatus != 2) {
                                for (FakedTrackingVariable currentTracker = innerTracker; currentTracker != null; currentTracker = currentTracker.innerTracker) {
                                    flowInfo.markNullStatus(currentTracker.binding, newStatus);
                                    final FakedTrackingVariable fakedTrackingVariable = currentTracker;
                                    fakedTrackingVariable.globalClosingState |= allocation.closeTracker.globalClosingState;
                                }
                            }
                        }
                    }
                    return;
                }
                if (!isAnyCloseable(allocation.arguments[0].resolvedType)) {
                    isWrapper = false;
                }
            }
            else {
                isWrapper = false;
            }
            if (isWrapper) {
                if (allocation.closeTracker != null) {
                    allocation.closeTracker.withdraw();
                    allocation.closeTracker = null;
                }
            }
            else {
                handleRegularResource(scope, flowInfo, allocation);
            }
        }
        else {
            handleRegularResource(scope, flowInfo, allocation);
        }
    }
    
    private static FakedTrackingVariable pickMoreUnsafe(final FakedTrackingVariable tracker1, final FakedTrackingVariable tracker2, final BlockScope scope, final FlowInfo info) {
        final int status1 = info.nullStatus(tracker1.binding);
        final int status2 = info.nullStatus(tracker2.binding);
        if (status1 == 2 || status2 == 4) {
            return pick(tracker1, tracker2, scope);
        }
        if (status1 == 4 || status2 == 2) {
            return pick(tracker2, tracker1, scope);
        }
        if ((status1 & 0x10) != 0x0) {
            return pick(tracker1, tracker2, scope);
        }
        if ((status2 & 0x10) != 0x0) {
            return pick(tracker2, tracker1, scope);
        }
        return pick(tracker1, tracker2, scope);
    }
    
    private static FakedTrackingVariable pick(final FakedTrackingVariable tracker1, final FakedTrackingVariable tracker2, final BlockScope scope) {
        tracker2.withdraw();
        return tracker1;
    }
    
    private static void handleRegularResource(final BlockScope scope, final FlowInfo flowInfo, final AllocationExpression allocation) {
        final FakedTrackingVariable presetTracker = allocation.closeTracker;
        if (presetTracker != null && presetTracker.originalBinding != null) {
            final int closeStatus = flowInfo.nullStatus(presetTracker.binding);
            if (closeStatus != 4 && closeStatus != 1 && !flowInfo.isDefinitelyNull(presetTracker.originalBinding) && !(presetTracker.currentAssignment instanceof LocalDeclaration)) {
                allocation.closeTracker.recordErrorLocation(presetTracker.currentAssignment, closeStatus);
            }
        }
        else {
            allocation.closeTracker = new FakedTrackingVariable(scope, allocation, flowInfo, 1);
        }
        flowInfo.markAsDefinitelyNull(allocation.closeTracker.binding);
    }
    
    private static FakedTrackingVariable findCloseTracker(final BlockScope scope, final FlowInfo flowInfo, Expression arg) {
        while (arg instanceof Assignment) {
            final Assignment assign = (Assignment)arg;
            final LocalVariableBinding innerLocal = assign.localVariableBinding();
            if (innerLocal != null) {
                return innerLocal.closeTracker;
            }
            arg = assign.expression;
        }
        if (arg instanceof SingleNameReference) {
            final LocalVariableBinding local = arg.localVariableBinding();
            if (local != null) {
                return local.closeTracker;
            }
        }
        else if (arg instanceof AllocationExpression) {
            return ((AllocationExpression)arg).closeTracker;
        }
        return null;
    }
    
    public static void handleResourceAssignment(final BlockScope scope, final FlowInfo upstreamInfo, final FlowInfo flowInfo, final FlowContext flowContext, final ASTNode location, final Expression rhs, final LocalVariableBinding local) {
        FakedTrackingVariable previousTracker = null;
        FakedTrackingVariable disconnectedTracker = null;
        if (local.closeTracker != null) {
            previousTracker = local.closeTracker;
            final int nullStatus = upstreamInfo.nullStatus(local);
            if (nullStatus != 2 && nullStatus != 1) {
                disconnectedTracker = previousTracker;
            }
        }
        Label_0337: {
            if (rhs.resolvedType != TypeBinding.NULL) {
                FakedTrackingVariable rhsTrackVar = getCloseTrackingVariable(rhs, flowInfo, flowContext);
                if (rhsTrackVar != null) {
                    if (local.closeTracker == null) {
                        if (rhsTrackVar.originalBinding != null) {
                            local.closeTracker = rhsTrackVar;
                        }
                        if (rhsTrackVar.currentAssignment == location) {
                            final FakedTrackingVariable fakedTrackingVariable = rhsTrackVar;
                            fakedTrackingVariable.globalClosingState &= 0xFFFFFFF9;
                        }
                    }
                    else {
                        if (rhs instanceof AllocationExpression || rhs instanceof ConditionalExpression) {
                            if (rhsTrackVar == disconnectedTracker) {
                                return;
                            }
                            if (local.closeTracker == rhsTrackVar && (rhsTrackVar.globalClosingState & 0x4) != 0x0) {
                                local.closeTracker = new FakedTrackingVariable(local, location, flowInfo, flowContext, 2);
                                break Label_0337;
                            }
                        }
                        local.closeTracker = rhsTrackVar;
                    }
                }
                else if (previousTracker != null) {
                    FlowContext currentFlowContext = flowContext;
                    if (previousTracker.tryContext != null) {
                        while (currentFlowContext != null) {
                            if (previousTracker.tryContext == currentFlowContext) {
                                break Label_0337;
                            }
                            currentFlowContext = currentFlowContext.parent;
                        }
                    }
                    if ((previousTracker.globalClosingState & 0x6) == 0x0 && flowInfo.hasNullInfoFor(previousTracker.binding)) {
                        flowInfo.markAsDefinitelyNull(previousTracker.binding);
                    }
                    local.closeTracker = analyseCloseableExpression(flowInfo, flowContext, local, location, rhs, previousTracker);
                }
                else {
                    rhsTrackVar = analyseCloseableExpression(flowInfo, flowContext, local, location, rhs, null);
                    if (rhsTrackVar != null) {
                        local.closeTracker = rhsTrackVar;
                        if ((rhsTrackVar.globalClosingState & 0x6) == 0x0) {
                            flowInfo.markAsDefinitelyNull(rhsTrackVar.binding);
                        }
                    }
                }
            }
        }
        if (disconnectedTracker != null) {
            if (disconnectedTracker.innerTracker != null && disconnectedTracker.innerTracker.binding.declaringScope == scope) {
                disconnectedTracker.innerTracker.outerTracker = null;
                scope.pruneWrapperTrackingVar(disconnectedTracker);
            }
            else {
                final int upstreamStatus = upstreamInfo.nullStatus(disconnectedTracker.binding);
                if (upstreamStatus != 4) {
                    disconnectedTracker.recordErrorLocation(location, upstreamStatus);
                }
            }
        }
    }
    
    private static FakedTrackingVariable analyseCloseableExpression(final FlowInfo flowInfo, final FlowContext flowContext, final LocalVariableBinding local, final ASTNode location, Expression expression, final FakedTrackingVariable previousTracker) {
        while (true) {
            if (expression instanceof Assignment) {
                expression = ((Assignment)expression).expression;
            }
            else {
                if (!(expression instanceof CastExpression)) {
                    break;
                }
                expression = ((CastExpression)expression).expression;
            }
        }
        boolean isResourceProducer = false;
        if (expression.resolvedType instanceof ReferenceBinding) {
            final ReferenceBinding resourceType = (ReferenceBinding)expression.resolvedType;
            if (resourceType.hasTypeBit(8)) {
                if (!isBlacklistedMethod(expression)) {
                    return null;
                }
                isResourceProducer = true;
            }
        }
        if (expression instanceof AllocationExpression) {
            final FakedTrackingVariable tracker = ((AllocationExpression)expression).closeTracker;
            if (tracker != null && tracker.originalBinding == null) {
                return null;
            }
            return tracker;
        }
        else {
            if (expression instanceof MessageSend || expression instanceof ArrayReference) {
                final FakedTrackingVariable tracker = new FakedTrackingVariable(local, location, flowInfo, flowContext, 16);
                if (!isResourceProducer) {
                    final FakedTrackingVariable fakedTrackingVariable = tracker;
                    fakedTrackingVariable.globalClosingState |= 0x2;
                }
                return tracker;
            }
            if ((expression.bits & 0x7) == 0x1 || (expression instanceof QualifiedNameReference && ((QualifiedNameReference)expression).isFieldAccess())) {
                final FakedTrackingVariable fakedTrackingVariable2;
                final FakedTrackingVariable tracker = fakedTrackingVariable2 = new FakedTrackingVariable(local, location, flowInfo, flowContext, 1);
                fakedTrackingVariable2.globalClosingState |= 0x4;
                return tracker;
            }
            if (local.closeTracker != null) {
                return local.closeTracker;
            }
            final FakedTrackingVariable newTracker = new FakedTrackingVariable(local, location, flowInfo, flowContext, 1);
            final LocalVariableBinding rhsLocal = expression.localVariableBinding();
            if (rhsLocal != null && rhsLocal.isParameter()) {
                final FakedTrackingVariable fakedTrackingVariable3 = newTracker;
                fakedTrackingVariable3.globalClosingState |= 0x4;
            }
            return newTracker;
        }
    }
    
    private static boolean isBlacklistedMethod(final Expression expression) {
        if (expression instanceof MessageSend) {
            final MethodBinding method = ((MessageSend)expression).binding;
            if (method != null && method.isValidBinding()) {
                return CharOperation.equals(method.declaringClass.compoundName, TypeConstants.JAVA_NIO_FILE_FILES);
            }
        }
        return false;
    }
    
    public static void cleanUpAfterAssignment(final BlockScope currentScope, final int lhsBits, Expression expression) {
        while (true) {
            if (expression instanceof Assignment) {
                expression = ((Assignment)expression).expression;
            }
            else {
                if (!(expression instanceof CastExpression)) {
                    break;
                }
                expression = ((CastExpression)expression).expression;
            }
        }
        if (expression instanceof AllocationExpression) {
            final FakedTrackingVariable tracker = ((AllocationExpression)expression).closeTracker;
            if (tracker != null && tracker.originalBinding == null) {
                tracker.withdraw();
                ((AllocationExpression)expression).closeTracker = null;
            }
        }
        else {
            final LocalVariableBinding local = expression.localVariableBinding();
            if (local != null && local.closeTracker != null && (lhsBits & 0x1) != 0x0) {
                local.closeTracker.withdraw();
            }
        }
    }
    
    public static boolean isAnyCloseable(final TypeBinding typeBinding) {
        return typeBinding instanceof ReferenceBinding && typeBinding.hasTypeBit(3);
    }
    
    public int findMostSpecificStatus(final FlowInfo flowInfo, final BlockScope currentScope, final BlockScope locationScope) {
        int status = 1;
        for (FakedTrackingVariable currentTracker = this; currentTracker != null; currentTracker = currentTracker.innerTracker) {
            final LocalVariableBinding currentVar = currentTracker.binding;
            int currentStatus = this.getNullStatusAggressively(currentVar, flowInfo);
            if (locationScope != null) {
                currentStatus = this.mergeCloseStatus(locationScope, currentStatus, currentVar, currentScope);
            }
            if (currentStatus == 4) {
                status = currentStatus;
                break;
            }
            if (status == 2 || status == 1) {
                status = currentStatus;
            }
        }
        return status;
    }
    
    private int getNullStatusAggressively(final LocalVariableBinding local, final FlowInfo flowInfo) {
        if (flowInfo == FlowInfo.DEAD_END) {
            return 1;
        }
        final int reachMode = flowInfo.reachMode();
        int status = 0;
        try {
            if (reachMode != 0) {
                flowInfo.tagBits &= 0xFFFFFFFC;
            }
            status = flowInfo.nullStatus(local);
            if (FakedTrackingVariable.TEST_372319) {
                try {
                    Thread.sleep(5L);
                }
                catch (final InterruptedException ex) {}
            }
        }
        finally {
            flowInfo.tagBits |= reachMode;
        }
        flowInfo.tagBits |= reachMode;
        if ((status & 0x2) != 0x0) {
            if ((status & 0x24) != 0x0) {
                return 16;
            }
            return 2;
        }
        else if ((status & 0x4) != 0x0) {
            if ((status & 0x10) != 0x0) {
                return 16;
            }
            return 4;
        }
        else {
            if ((status & 0x10) != 0x0) {
                return 16;
            }
            return status;
        }
    }
    
    public int mergeCloseStatus(final BlockScope currentScope, int status, final LocalVariableBinding local, final BlockScope outerScope) {
        if (status != 4) {
            if (currentScope.finallyInfo != null) {
                final int finallyStatus = currentScope.finallyInfo.nullStatus(local);
                if (finallyStatus == 4) {
                    return finallyStatus;
                }
                if (finallyStatus != 2 && currentScope.finallyInfo.hasNullInfoFor(local)) {
                    status = 16;
                }
            }
            if (currentScope != outerScope && currentScope.parent instanceof BlockScope) {
                return this.mergeCloseStatus((BlockScope)currentScope.parent, status, local, outerScope);
            }
        }
        return status;
    }
    
    public void markClose(final FlowInfo flowInfo, final FlowContext flowContext) {
        FakedTrackingVariable current = this;
        do {
            flowInfo.markAsDefinitelyNonNull(current.binding);
            final FakedTrackingVariable fakedTrackingVariable = current;
            fakedTrackingVariable.globalClosingState |= 0x1;
            flowContext.markFinallyNullStatus(current.binding, 4);
            current = current.innerTracker;
        } while (current != null);
    }
    
    public void markClosedInNestedMethod() {
        this.globalClosingState |= 0x8;
    }
    
    public static FlowInfo markPassedToOutside(final BlockScope scope, final Expression expression, final FlowInfo flowInfo, final FlowContext flowContext, final boolean owned) {
        FakedTrackingVariable trackVar = getCloseTrackingVariable(expression, flowInfo, flowContext);
        if (trackVar == null) {
            return flowInfo;
        }
        final FlowInfo infoResourceIsClosed = owned ? flowInfo : flowInfo.copy();
        final int flag = owned ? 4 : 2;
        do {
            final FakedTrackingVariable fakedTrackingVariable = trackVar;
            fakedTrackingVariable.globalClosingState |= flag;
            if (scope.methodScope() != trackVar.methodScope) {
                final FakedTrackingVariable fakedTrackingVariable2 = trackVar;
                fakedTrackingVariable2.globalClosingState |= 0x8;
            }
            infoResourceIsClosed.markAsDefinitelyNonNull(trackVar.binding);
        } while ((trackVar = trackVar.innerTracker) != null);
        if (owned) {
            return infoResourceIsClosed;
        }
        return FlowInfo.conditional(flowInfo, infoResourceIsClosed);
    }
    
    public boolean hasDefinitelyNoResource(final FlowInfo flowInfo) {
        return this.originalBinding != null && (flowInfo.isDefinitelyNull(this.originalBinding) || (!flowInfo.isDefinitelyAssigned(this.originalBinding) && !flowInfo.isPotentiallyAssigned(this.originalBinding)));
    }
    
    public boolean isClosedInFinallyOfEnclosing(final BlockScope scope) {
        for (BlockScope currentScope = scope; currentScope.finallyInfo == null || !currentScope.finallyInfo.isDefinitelyNonNull(this.binding); currentScope = (BlockScope)currentScope.parent) {
            if (!(currentScope.parent instanceof BlockScope)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isResourceBeingReturned(final FakedTrackingVariable returnedResource) {
        FakedTrackingVariable current = this;
        while (current != returnedResource) {
            current = current.innerTracker;
            if (current == null) {
                return false;
            }
        }
        this.globalClosingState |= 0x40;
        return true;
    }
    
    public void withdraw() {
        this.binding.declaringScope.removeTrackingVar(this);
    }
    
    public void recordErrorLocation(final ASTNode location, final int nullStatus) {
        if ((this.globalClosingState & 0x4) != 0x0) {
            return;
        }
        if (this.recordedLocations == null) {
            this.recordedLocations = new HashMap();
        }
        this.recordedLocations.put(location, nullStatus);
    }
    
    public boolean reportRecordedErrors(final Scope scope, int mergedStatus, final boolean atDeadEnd) {
        FakedTrackingVariable current = this;
        while (current.globalClosingState == 0) {
            current = current.innerTracker;
            if (current == null) {
                if (atDeadEnd && this.neverClosedAtLocations()) {
                    mergedStatus = 2;
                }
                if ((mergedStatus & 0x32) != 0x0) {
                    this.reportError(scope.problemReporter(), null, mergedStatus);
                    return true;
                }
                break;
            }
        }
        boolean hasReported = false;
        if (this.recordedLocations != null) {
            final Iterator locations = this.recordedLocations.entrySet().iterator();
            int reportFlags = 0;
            while (locations.hasNext()) {
                final Map.Entry entry = locations.next();
                reportFlags |= this.reportError(scope.problemReporter(), entry.getKey(), entry.getValue());
                hasReported = true;
            }
            if (reportFlags != 0) {
                current = this;
                do {
                    final FakedTrackingVariable fakedTrackingVariable = current;
                    fakedTrackingVariable.globalClosingState |= reportFlags;
                } while ((current = current.innerTracker) != null);
            }
        }
        return hasReported;
    }
    
    private boolean neverClosedAtLocations() {
        if (this.recordedLocations != null) {
            for (final Object value : this.recordedLocations.values()) {
                if (!value.equals(2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public int reportError(final ProblemReporter problemReporter, final ASTNode location, final int nullStatus) {
        if ((this.globalClosingState & 0x4) != 0x0) {
            return 0;
        }
        boolean isPotentialProblem = false;
        if (nullStatus == 2) {
            if ((this.globalClosingState & 0x8) != 0x0) {
                isPotentialProblem = true;
            }
        }
        else if ((nullStatus & 0x30) != 0x0) {
            isPotentialProblem = true;
        }
        if (isPotentialProblem) {
            if ((this.globalClosingState & 0x60) != 0x0) {
                return 0;
            }
            problemReporter.potentiallyUnclosedCloseable(this, location);
        }
        else {
            if ((this.globalClosingState & 0x40) != 0x0) {
                return 0;
            }
            problemReporter.unclosedCloseable(this, location);
        }
        final int reportFlag = isPotentialProblem ? 32 : 64;
        if (location == null) {
            FakedTrackingVariable current = this;
            do {
                final FakedTrackingVariable fakedTrackingVariable = current;
                fakedTrackingVariable.globalClosingState |= reportFlag;
            } while ((current = current.innerTracker) != null);
        }
        return reportFlag;
    }
    
    public void reportExplicitClosing(final ProblemReporter problemReporter) {
        if ((this.globalClosingState & 0x14) == 0x0) {
            this.globalClosingState |= 0x10;
            problemReporter.explicitlyClosedAutoCloseable(this);
        }
    }
    
    public String nameForReporting(final ASTNode location, final ReferenceContext referenceContext) {
        if (this.name == FakedTrackingVariable.UNASSIGNED_CLOSEABLE_NAME && location != null && referenceContext != null) {
            final CompilationResult compResult = referenceContext.compilationResult();
            if (compResult != null) {
                final int[] lineEnds = compResult.getLineSeparatorPositions();
                final int resourceLine = Util.getLineNumber(this.sourceStart, lineEnds, 0, lineEnds.length - 1);
                final int reportLine = Util.getLineNumber(location.sourceStart, lineEnds, 0, lineEnds.length - 1);
                if (resourceLine != reportLine) {
                    final char[] replacement = Integer.toString(resourceLine).toCharArray();
                    return String.valueOf(CharOperation.replace(FakedTrackingVariable.UNASSIGNED_CLOSEABLE_NAME_TEMPLATE, FakedTrackingVariable.TEMPLATE_ARGUMENT, replacement));
                }
            }
        }
        return String.valueOf(this.name);
    }
    
    public static class IteratorForReporting implements Iterator<FakedTrackingVariable>
    {
        private final Set<FakedTrackingVariable> varSet;
        private final Scope scope;
        private final boolean atExit;
        private Stage stage;
        private Iterator<FakedTrackingVariable> iterator;
        private FakedTrackingVariable next;
        
        public IteratorForReporting(final List<FakedTrackingVariable> variables, final Scope scope, final boolean atExit) {
            this.varSet = new HashSet<FakedTrackingVariable>(variables);
            this.scope = scope;
            this.atExit = atExit;
            this.setUpForStage(Stage.OuterLess);
        }
        
        @Override
        public boolean hasNext() {
            switch (this.stage) {
                case OuterLess: {
                    while (this.iterator.hasNext()) {
                        final FakedTrackingVariable trackingVar = this.iterator.next();
                        if (trackingVar.outerTracker == null) {
                            return this.found(trackingVar);
                        }
                    }
                    this.setUpForStage(Stage.InnerOfProcessed);
                }
                case InnerOfProcessed: {
                    while (this.iterator.hasNext()) {
                        final FakedTrackingVariable trackingVar = this.iterator.next();
                        final FakedTrackingVariable outer = trackingVar.outerTracker;
                        if (outer.binding.declaringScope == this.scope && !this.varSet.contains(outer)) {
                            return this.found(trackingVar);
                        }
                    }
                    this.setUpForStage(Stage.InnerOfNotEnclosing);
                }
                case InnerOfNotEnclosing: {
                Label_0253:
                    while (this.iterator.hasNext()) {
                        final FakedTrackingVariable trackingVar = this.iterator.next();
                        final FakedTrackingVariable outer = trackingVar.outerTracker;
                        if (!this.varSet.contains(outer)) {
                            final Scope outerTrackerScope = outer.binding.declaringScope;
                            Scope currentScope = this.scope;
                            while ((currentScope = currentScope.parent) instanceof BlockScope) {
                                if (outerTrackerScope == currentScope) {
                                    break Label_0253;
                                }
                            }
                            return this.found(trackingVar);
                        }
                    }
                    this.setUpForStage(Stage.AtExit);
                }
                case AtExit: {
                    return this.atExit && this.iterator.hasNext() && this.found(this.iterator.next());
                }
                default: {
                    throw new IllegalStateException("Unexpected Stage " + this.stage);
                }
            }
        }
        
        private boolean found(final FakedTrackingVariable trackingVar) {
            this.iterator.remove();
            this.next = trackingVar;
            return true;
        }
        
        private void setUpForStage(final Stage nextStage) {
            this.iterator = this.varSet.iterator();
            this.stage = nextStage;
        }
        
        @Override
        public FakedTrackingVariable next() {
            return this.next;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        enum Stage
        {
            OuterLess("OuterLess", 0), 
            InnerOfProcessed("InnerOfProcessed", 1), 
            InnerOfNotEnclosing("InnerOfNotEnclosing", 2), 
            AtExit("AtExit", 3);
            
            private Stage(final String s, final int n) {
            }
        }
    }
}
