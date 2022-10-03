package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.ast.NullAnnotationMatching;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.ast.Reference;

public class FinallyFlowContext extends TryFlowContext
{
    Reference[] finalAssignments;
    VariableBinding[] finalVariables;
    int assignCount;
    LocalVariableBinding[] nullLocals;
    ASTNode[] nullReferences;
    int[] nullCheckTypes;
    int nullCount;
    public FlowContext tryContext;
    
    public FinallyFlowContext(final FlowContext parent, final ASTNode associatedNode, final ExceptionHandlingFlowContext tryContext) {
        super(parent, associatedNode);
        this.tryContext = tryContext;
    }
    
    public void complainOnDeferredChecks(final FlowInfo flowInfo, final BlockScope scope) {
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
                    for (FlowContext currentContext = this.getLocalParent(); currentContext != null; currentContext = currentContext.getLocalParent()) {
                        currentContext.removeFinalAssignmentIfAny(this.finalAssignments[i]);
                    }
                }
            }
        }
        if ((this.tagBits & 0x1) != 0x0) {
            for (int i = 0; i < this.nullCount; ++i) {
                final ASTNode location = this.nullReferences[i];
                switch (this.nullCheckTypes[i] & 0xFFFF0FFF) {
                    case 128: {
                        final int nullStatus = flowInfo.nullStatus(this.nullLocals[i]);
                        if (nullStatus != 4) {
                            this.parent.recordNullityMismatch(scope, (Expression)location, this.providedExpectedTypes[i][0], this.providedExpectedTypes[i][1], flowInfo, nullStatus, null);
                            break;
                        }
                        break;
                    }
                    case 16: {
                        this.checkUnboxing(scope, (Expression)location, flowInfo);
                        break;
                    }
                    default: {
                        this.parent.recordUsingNullReference(scope, this.nullLocals[i], this.nullReferences[i], this.nullCheckTypes[i], flowInfo);
                        break;
                    }
                }
            }
        }
        else {
            for (int i = 0; i < this.nullCount; ++i) {
                final ASTNode location = this.nullReferences[i];
                final LocalVariableBinding local = this.nullLocals[i];
                switch (this.nullCheckTypes[i] & 0xFFFF0FFF) {
                    case 256:
                    case 512: {
                        if (!flowInfo.isDefinitelyNonNull(local))
                        if ((this.nullCheckTypes[i] & 0xFFFF0FFF) != 0x200) {
                            scope.problemReporter().localVariableNonNullComparedToNull(local, location);
                            break;
                        }
                        if ((this.nullCheckTypes[i] & 0x1000) == 0x0) {
                            scope.problemReporter().localVariableRedundantCheckOnNonNull(local, location);
                            break;
                        }
                        break;
                    }
                    case 257:
                    case 513:
                    case 769:
                    case 1025: {
                        final Expression expression = (Expression)location;
                        if (flowInfo.isDefinitelyNull(local)) {
                            switch (this.nullCheckTypes[i] & 0xFFFF0F00) {
                                case 256: {
                                    if ((this.nullCheckTypes[i] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                        scope.problemReporter().localVariableNullReference(local, expression);
                                        continue;
                                    }
                                    if ((this.nullCheckTypes[i] & 0x1000) == 0x0) {
                                        scope.problemReporter().localVariableRedundantCheckOnNull(local, expression);
                                        continue;
                                    }
                                    continue;
                                }
                                case 512: {
                                    if ((this.nullCheckTypes[i] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
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
                                    continue;
                                }
                            }
                        }
                        else {
                            if (!flowInfo.isPotentiallyNull(local)) {
                                break;
                            }
                            switch (this.nullCheckTypes[i] & 0xFFFF0F00) {
                                case 256: {
                                    this.nullReferences[i] = null;
                                    if ((this.nullCheckTypes[i] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                        scope.problemReporter().localVariablePotentialNullReference(local, expression);
                                        continue;
                                    }
                                    continue;
                                }
                                case 512: {
                                    this.nullReferences[i] = null;
                                    if ((this.nullCheckTypes[i] & 0xFF & 0xFFFF0FFF) == 0x1 && (expression.implicitConversion & 0x400) != 0x0) {
                                        scope.problemReporter().localVariablePotentialNullReference(local, expression);
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
                        if (flowInfo.isDefinitelyNull(local)) {
                            scope.problemReporter().localVariableNullReference(local, location);
                            break;
                        }
                        if (flowInfo.isPotentiallyNull(local)) {
                            scope.problemReporter().localVariablePotentialNullReference(local, location);
                            break;
                        }
                        break;
                    }
                    case 128: {
                        final int nullStatus2 = flowInfo.nullStatus(local);
                        if (nullStatus2 != 4) {
                            final char[][] annotationName = scope.environment().getNonNullAnnotationName();
                            scope.problemReporter().nullityMismatch((Expression)location, this.providedExpectedTypes[i][0], this.providedExpectedTypes[i][1], nullStatus2, annotationName);
                            break;
                        }
                        break;
                    }
                    case 16: {
                        this.checkUnboxing(scope, (Expression)location, flowInfo);
                        break;
                    }
                }
            }
        }
    }
    
    @Override
    public String individualToString() {
        final StringBuffer buffer = new StringBuffer("Finally flow context");
        buffer.append("[finalAssignments count - ").append(this.assignCount).append(']');
        buffer.append("[nullReferences count - ").append(this.nullCount).append(']');
        return buffer.toString();
    }
    
    @Override
    public boolean isSubRoutine() {
        return true;
    }
    
    @Override
    protected boolean recordFinalAssignment(final VariableBinding binding, final Reference finalAssignment) {
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
    public void recordUsingNullReference(final Scope scope, final LocalVariableBinding local, final ASTNode location, int checkType, final FlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x3) == 0x0 && !flowInfo.isDefinitelyUnknown(local)) {
            checkType |= (this.tagBits & 0x1000);
            final int checkTypeWithoutHideNullWarning = checkType & 0xFFFF0FFF;
            Label_1041: {
                if ((this.tagBits & 0x1) != 0x0) {
                    switch (checkTypeWithoutHideNullWarning) {
                        case 256:
                        case 257:
                        case 512:
                        case 513:
                        case 769:
                        case 1025: {
                            final Expression reference = (Expression)location;
                            if (flowInfo.cannotBeNull(local)) {
                                if (checkTypeWithoutHideNullWarning == 512) {
                                    if ((checkType & 0x1000) == 0x0) {
                                        scope.problemReporter().localVariableRedundantCheckOnNonNull(local, reference);
                                    }
                                    flowInfo.initsWhenFalse().setReachMode(2);
                                }
                                else if (checkTypeWithoutHideNullWarning == 256) {
                                    scope.problemReporter().localVariableNonNullComparedToNull(local, reference);
                                    flowInfo.initsWhenTrue().setReachMode(2);
                                }
                                return;
                            }
                            if (flowInfo.canOnlyBeNull(local)) {
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
                                        break Label_1041;
                                    }
                                }
                            }
                            else {
                                if (!flowInfo.isPotentiallyNull(local)) {
                                    break;
                                }
                                switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                                    case 256: {
                                        if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                            scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                            return;
                                        }
                                        break Label_1041;
                                    }
                                    case 512: {
                                        if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                            scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                            return;
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
                            if (flowInfo.cannotBeNull(local)) {
                                return;
                            }
                            if (flowInfo.canOnlyBeNull(local)) {
                                scope.problemReporter().localVariableNullReference(local, location);
                                return;
                            }
                            break;
                        }
                    }
                }
                else {
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
                                        break Label_1041;
                                    }
                                }
                            }
                            else {
                                if (!flowInfo.isPotentiallyNull(local)) {
                                    break;
                                }
                                switch (checkTypeWithoutHideNullWarning & 0xFFFF0F00) {
                                    case 256: {
                                        if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                            scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                            return;
                                        }
                                        break Label_1041;
                                    }
                                    case 512: {
                                        if ((checkTypeWithoutHideNullWarning & 0xFF) == 0x1 && (reference.implicitConversion & 0x400) != 0x0) {
                                            scope.problemReporter().localVariablePotentialNullReference(local, reference);
                                            return;
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
                                scope.problemReporter().localVariableNullReference(local, location);
                                return;
                            }
                            if (flowInfo.isPotentiallyNull(local)) {
                                scope.problemReporter().localVariablePotentialNullReference(local, location);
                                return;
                            }
                            if (flowInfo.isDefinitelyNonNull(local)) {
                                return;
                            }
                            break;
                        }
                    }
                }
            }
            this.recordNullReference(local, location, checkType, flowInfo);
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
    
    @Override
    protected void recordNullReference(final LocalVariableBinding local, final ASTNode expression, final int checkType, final FlowInfo nullInfo) {
        if (this.nullCount == 0) {
            this.nullLocals = new LocalVariableBinding[5];
            this.nullReferences = new ASTNode[5];
            this.nullCheckTypes = new int[5];
        }
        else if (this.nullCount == this.nullLocals.length) {
            final int newLength = this.nullCount * 2;
            System.arraycopy(this.nullLocals, 0, this.nullLocals = new LocalVariableBinding[newLength], 0, this.nullCount);
            System.arraycopy(this.nullReferences, 0, this.nullReferences = new ASTNode[newLength], 0, this.nullCount);
            System.arraycopy(this.nullCheckTypes, 0, this.nullCheckTypes = new int[newLength], 0, this.nullCount);
        }
        this.nullLocals[this.nullCount] = local;
        this.nullReferences[this.nullCount] = expression;
        this.nullCheckTypes[this.nullCount++] = checkType;
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
    protected boolean internalRecordNullityMismatch(final Expression expression, final TypeBinding providedType, final FlowInfo flowInfo, final int nullStatus, final TypeBinding expectedType, final int checkType) {
        if (nullStatus == 1 || ((this.tagBits & 0x1) != 0x0 && nullStatus != 2)) {
            this.recordProvidedExpectedTypes(providedType, expectedType, this.nullCount);
            this.recordNullReference(expression.localVariableBinding(), expression, checkType, flowInfo);
            return true;
        }
        return false;
    }
}
