package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

public abstract class FlowInfo
{
    public int tagBits;
    public static final int REACHABLE = 0;
    public static final int UNREACHABLE_OR_DEAD = 1;
    public static final int UNREACHABLE_BY_NULLANALYSIS = 2;
    public static final int UNREACHABLE = 3;
    public static final int NULL_FLAG_MASK = 4;
    public static final int UNKNOWN = 1;
    public static final int NULL = 2;
    public static final int NON_NULL = 4;
    public static final int POTENTIALLY_UNKNOWN = 8;
    public static final int POTENTIALLY_NULL = 16;
    public static final int POTENTIALLY_NON_NULL = 32;
    public static final int UNROOTED = 64;
    public static final int FREE_TYPEVARIABLE = 48;
    public static final UnconditionalFlowInfo DEAD_END;
    
    static {
        DEAD_END = new UnconditionalFlowInfo();
        FlowInfo.DEAD_END.tagBits = 3;
    }
    
    public abstract FlowInfo addInitializationsFrom(final FlowInfo p0);
    
    public abstract FlowInfo addNullInfoFrom(final FlowInfo p0);
    
    public abstract FlowInfo addPotentialInitializationsFrom(final FlowInfo p0);
    
    public FlowInfo asNegatedCondition() {
        return this;
    }
    
    public static FlowInfo conditional(final FlowInfo initsWhenTrue, final FlowInfo initsWhenFalse) {
        if (initsWhenTrue == initsWhenFalse) {
            return initsWhenTrue;
        }
        return new ConditionalFlowInfo(initsWhenTrue, initsWhenFalse);
    }
    
    public boolean cannotBeDefinitelyNullOrNonNull(final LocalVariableBinding local) {
        return this.isPotentiallyUnknown(local) || (this.isPotentiallyNonNull(local) && this.isPotentiallyNull(local));
    }
    
    public boolean cannotBeNull(final LocalVariableBinding local) {
        return this.isDefinitelyNonNull(local) || this.isProtectedNonNull(local);
    }
    
    public boolean canOnlyBeNull(final LocalVariableBinding local) {
        return this.isDefinitelyNull(local) || this.isProtectedNull(local);
    }
    
    public abstract FlowInfo copy();
    
    public static UnconditionalFlowInfo initial(final int maxFieldCount) {
        final UnconditionalFlowInfo info = new UnconditionalFlowInfo();
        info.maxFieldCount = maxFieldCount;
        return info;
    }
    
    public abstract FlowInfo initsWhenFalse();
    
    public abstract FlowInfo initsWhenTrue();
    
    public abstract boolean isDefinitelyAssigned(final FieldBinding p0);
    
    public abstract boolean isDefinitelyAssigned(final LocalVariableBinding p0);
    
    public abstract boolean isDefinitelyNonNull(final LocalVariableBinding p0);
    
    public abstract boolean isDefinitelyNull(final LocalVariableBinding p0);
    
    public abstract boolean isDefinitelyUnknown(final LocalVariableBinding p0);
    
    public abstract boolean hasNullInfoFor(final LocalVariableBinding p0);
    
    public abstract boolean isPotentiallyAssigned(final FieldBinding p0);
    
    public abstract boolean isPotentiallyAssigned(final LocalVariableBinding p0);
    
    public abstract boolean isPotentiallyNonNull(final LocalVariableBinding p0);
    
    public abstract boolean isPotentiallyNull(final LocalVariableBinding p0);
    
    public abstract boolean isPotentiallyUnknown(final LocalVariableBinding p0);
    
    public abstract boolean isProtectedNonNull(final LocalVariableBinding p0);
    
    public abstract boolean isProtectedNull(final LocalVariableBinding p0);
    
    public abstract void markAsComparedEqualToNonNull(final LocalVariableBinding p0);
    
    public abstract void markAsComparedEqualToNull(final LocalVariableBinding p0);
    
    public abstract void markAsDefinitelyAssigned(final FieldBinding p0);
    
    public abstract void markAsDefinitelyNonNull(final LocalVariableBinding p0);
    
    public abstract void markAsDefinitelyNull(final LocalVariableBinding p0);
    
    public abstract void resetNullInfo(final LocalVariableBinding p0);
    
    public abstract void markPotentiallyUnknownBit(final LocalVariableBinding p0);
    
    public abstract void markPotentiallyNullBit(final LocalVariableBinding p0);
    
    public abstract void markPotentiallyNonNullBit(final LocalVariableBinding p0);
    
    public abstract void markAsDefinitelyAssigned(final LocalVariableBinding p0);
    
    public abstract void markAsDefinitelyUnknown(final LocalVariableBinding p0);
    
    public void markNullStatus(final LocalVariableBinding local, final int nullStatus) {
        switch (nullStatus) {
            case 1: {
                this.markAsDefinitelyUnknown(local);
                break;
            }
            case 2: {
                this.markAsDefinitelyNull(local);
                break;
            }
            case 4: {
                this.markAsDefinitelyNonNull(local);
                break;
            }
            default: {
                this.resetNullInfo(local);
                if ((nullStatus & 0x8) != 0x0) {
                    this.markPotentiallyUnknownBit(local);
                }
                if ((nullStatus & 0x10) != 0x0) {
                    this.markPotentiallyNullBit(local);
                }
                if ((nullStatus & 0x20) != 0x0) {
                    this.markPotentiallyNonNullBit(local);
                }
                if ((nullStatus & 0x38) == 0x0) {
                    this.markAsDefinitelyUnknown(local);
                    break;
                }
                break;
            }
        }
    }
    
    public int nullStatus(final LocalVariableBinding local) {
        if (this.isDefinitelyUnknown(local)) {
            return 1;
        }
        if (this.isDefinitelyNull(local)) {
            return 2;
        }
        if (this.isDefinitelyNonNull(local)) {
            return 4;
        }
        int status = 0;
        if (this.isPotentiallyUnknown(local)) {
            status |= 0x8;
        }
        if (this.isPotentiallyNull(local)) {
            status |= 0x10;
        }
        if (this.isPotentiallyNonNull(local)) {
            status |= 0x20;
        }
        if (status > 0) {
            return status;
        }
        return 1;
    }
    
    public static int mergeNullStatus(final int nullStatus1, final int nullStatus2) {
        boolean canBeNull = false;
        boolean canBeNonNull = false;
        switch (nullStatus1) {
            case 16: {
                canBeNonNull = true;
            }
            case 2: {
                canBeNull = true;
                break;
            }
            case 32: {
                canBeNull = true;
            }
            case 4: {
                canBeNonNull = true;
                break;
            }
        }
        switch (nullStatus2) {
            case 16: {
                canBeNonNull = true;
            }
            case 2: {
                canBeNull = true;
                break;
            }
            case 32: {
                canBeNull = true;
            }
            case 4: {
                canBeNonNull = true;
                break;
            }
        }
        if (canBeNull) {
            if (canBeNonNull) {
                return 16;
            }
            return 2;
        }
        else {
            if (canBeNonNull) {
                return 4;
            }
            return 1;
        }
    }
    
    public static UnconditionalFlowInfo mergedOptimizedBranches(final FlowInfo initsWhenTrue, final boolean isOptimizedTrue, final FlowInfo initsWhenFalse, final boolean isOptimizedFalse, final boolean allowFakeDeadBranch) {
        UnconditionalFlowInfo mergedInfo;
        if (isOptimizedTrue) {
            if (initsWhenTrue == FlowInfo.DEAD_END && allowFakeDeadBranch) {
                mergedInfo = initsWhenFalse.setReachMode(1).unconditionalInits();
            }
            else {
                mergedInfo = initsWhenTrue.addPotentialInitializationsFrom(initsWhenFalse.nullInfoLessUnconditionalCopy()).unconditionalInits();
            }
        }
        else if (isOptimizedFalse) {
            if (initsWhenFalse == FlowInfo.DEAD_END && allowFakeDeadBranch) {
                mergedInfo = initsWhenTrue.setReachMode(1).unconditionalInits();
            }
            else {
                mergedInfo = initsWhenFalse.addPotentialInitializationsFrom(initsWhenTrue.nullInfoLessUnconditionalCopy()).unconditionalInits();
            }
        }
        else {
            mergedInfo = initsWhenTrue.mergedWith(initsWhenFalse.unconditionalInits());
        }
        return mergedInfo;
    }
    
    public static UnconditionalFlowInfo mergedOptimizedBranchesIfElse(final FlowInfo initsWhenTrue, final boolean isOptimizedTrue, final FlowInfo initsWhenFalse, final boolean isOptimizedFalse, final boolean allowFakeDeadBranch, final FlowInfo flowInfo, final IfStatement ifStatement, final boolean reportDeadCodeInKnownPattern) {
        UnconditionalFlowInfo mergedInfo;
        if (isOptimizedTrue) {
            if (initsWhenTrue == FlowInfo.DEAD_END && allowFakeDeadBranch) {
                if (!reportDeadCodeInKnownPattern) {
                    if (ifStatement.elseStatement == null) {
                        mergedInfo = flowInfo.unconditionalInits();
                    }
                    else {
                        mergedInfo = initsWhenFalse.unconditionalInits();
                        if (initsWhenFalse != FlowInfo.DEAD_END) {
                            mergedInfo.setReachMode(flowInfo.reachMode());
                        }
                    }
                }
                else {
                    mergedInfo = initsWhenFalse.setReachMode(1).unconditionalInits();
                }
            }
            else {
                mergedInfo = initsWhenTrue.addPotentialInitializationsFrom(initsWhenFalse.nullInfoLessUnconditionalCopy()).unconditionalInits();
            }
        }
        else if (isOptimizedFalse) {
            if (initsWhenFalse == FlowInfo.DEAD_END && allowFakeDeadBranch) {
                if (!reportDeadCodeInKnownPattern) {
                    if (ifStatement.thenStatement == null) {
                        mergedInfo = flowInfo.unconditionalInits();
                    }
                    else {
                        mergedInfo = initsWhenTrue.unconditionalInits();
                        if (initsWhenTrue != FlowInfo.DEAD_END) {
                            mergedInfo.setReachMode(flowInfo.reachMode());
                        }
                    }
                }
                else {
                    mergedInfo = initsWhenTrue.setReachMode(1).unconditionalInits();
                }
            }
            else {
                mergedInfo = initsWhenFalse.addPotentialInitializationsFrom(initsWhenTrue.nullInfoLessUnconditionalCopy()).unconditionalInits();
            }
        }
        else if ((flowInfo.tagBits & 0x3) == 0x0 && (ifStatement.bits & 0x80) != 0x0 && initsWhenTrue != FlowInfo.DEAD_END && initsWhenFalse != FlowInfo.DEAD_END) {
            mergedInfo = initsWhenTrue.addPotentialInitializationsFrom(initsWhenFalse.nullInfoLessUnconditionalCopy()).unconditionalInits();
            mergedInfo.mergeDefiniteInitsWith(initsWhenFalse.unconditionalCopy());
            if ((mergedInfo.tagBits & 0x1) != 0x0 && (initsWhenFalse.tagBits & 0x3) == 0x2) {
                final UnconditionalFlowInfo unconditionalFlowInfo = mergedInfo;
                unconditionalFlowInfo.tagBits &= 0xFFFFFFFE;
                final UnconditionalFlowInfo unconditionalFlowInfo2 = mergedInfo;
                unconditionalFlowInfo2.tagBits |= 0x2;
            }
        }
        else if ((flowInfo.tagBits & 0x3) == 0x0 && (ifStatement.bits & 0x100) != 0x0 && initsWhenTrue != FlowInfo.DEAD_END && initsWhenFalse != FlowInfo.DEAD_END) {
            mergedInfo = initsWhenFalse.addPotentialInitializationsFrom(initsWhenTrue.nullInfoLessUnconditionalCopy()).unconditionalInits();
            mergedInfo.mergeDefiniteInitsWith(initsWhenTrue.unconditionalCopy());
            if ((mergedInfo.tagBits & 0x1) != 0x0 && (initsWhenTrue.tagBits & 0x3) == 0x2) {
                final UnconditionalFlowInfo unconditionalFlowInfo3 = mergedInfo;
                unconditionalFlowInfo3.tagBits &= 0xFFFFFFFE;
                final UnconditionalFlowInfo unconditionalFlowInfo4 = mergedInfo;
                unconditionalFlowInfo4.tagBits |= 0x2;
            }
        }
        else {
            mergedInfo = initsWhenTrue.mergedWith(initsWhenFalse.unconditionalInits());
        }
        return mergedInfo;
    }
    
    public int reachMode() {
        return this.tagBits & 0x3;
    }
    
    public abstract FlowInfo safeInitsWhenTrue();
    
    public abstract FlowInfo setReachMode(final int p0);
    
    public abstract UnconditionalFlowInfo mergedWith(final UnconditionalFlowInfo p0);
    
    public abstract UnconditionalFlowInfo mergeDefiniteInitsWith(final UnconditionalFlowInfo p0);
    
    public abstract UnconditionalFlowInfo nullInfoLessUnconditionalCopy();
    
    @Override
    public String toString() {
        if (this == FlowInfo.DEAD_END) {
            return "FlowInfo.DEAD_END";
        }
        return super.toString();
    }
    
    public abstract UnconditionalFlowInfo unconditionalCopy();
    
    public abstract UnconditionalFlowInfo unconditionalFieldLessCopy();
    
    public abstract UnconditionalFlowInfo unconditionalInits();
    
    public abstract UnconditionalFlowInfo unconditionalInitsWithoutSideEffect();
    
    public abstract void resetAssignmentInfo(final LocalVariableBinding p0);
    
    public static int tagBitsToNullStatus(final long tagBits) {
        if ((tagBits & 0x100000000000000L) != 0x0L) {
            return 4;
        }
        if ((tagBits & 0x80000000000000L) != 0x0L) {
            return 48;
        }
        return 1;
    }
}
