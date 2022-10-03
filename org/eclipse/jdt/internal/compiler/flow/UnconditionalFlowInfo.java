package org.eclipse.jdt.internal.compiler.flow;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

public class UnconditionalFlowInfo extends FlowInfo
{
    public static final boolean COVERAGE_TEST_FLAG = false;
    public static int CoverageTestId;
    public long definiteInits;
    public long potentialInits;
    public long nullBit1;
    public long nullBit2;
    public long nullBit3;
    public long nullBit4;
    public long iNBit;
    public long iNNBit;
    public static final int extraLength = 8;
    public long[][] extra;
    public int maxFieldCount;
    public static final int BitCacheSize = 64;
    public static final int IN = 6;
    public static final int INN = 7;
    
    public static UnconditionalFlowInfo fakeInitializedFlowInfo(final int localsCount, final int maxFieldCount) {
        final UnconditionalFlowInfo flowInfo = new UnconditionalFlowInfo();
        flowInfo.maxFieldCount = maxFieldCount;
        for (int i = 0; i < localsCount; ++i) {
            flowInfo.markAsDefinitelyAssigned(i + maxFieldCount);
        }
        return flowInfo;
    }
    
    @Override
    public FlowInfo addInitializationsFrom(final FlowInfo inits) {
        return this.addInfoFrom(inits, true);
    }
    
    @Override
    public FlowInfo addNullInfoFrom(final FlowInfo inits) {
        return this.addInfoFrom(inits, false);
    }
    
    private FlowInfo addInfoFrom(final FlowInfo inits, final boolean handleInits) {
        if (this == UnconditionalFlowInfo.DEAD_END) {
            return this;
        }
        if (inits == UnconditionalFlowInfo.DEAD_END) {
            return this;
        }
        final UnconditionalFlowInfo otherInits = inits.unconditionalInits();
        if (handleInits) {
            this.definiteInits |= otherInits.definiteInits;
            this.potentialInits |= otherInits.potentialInits;
        }
        final boolean thisHadNulls = (this.tagBits & 0x4) != 0x0;
        final boolean otherHasNulls = (otherInits.tagBits & 0x4) != 0x0;
        if (otherHasNulls) {
            if (!thisHadNulls) {
                this.nullBit1 = otherInits.nullBit1;
                this.nullBit2 = otherInits.nullBit2;
                this.nullBit3 = otherInits.nullBit3;
                this.nullBit4 = otherInits.nullBit4;
                this.iNBit = otherInits.iNBit;
                this.iNNBit = otherInits.iNNBit;
            }
            else {
                long a1 = this.nullBit1;
                long a2 = this.nullBit2;
                long a3 = this.nullBit3;
                long a4 = this.nullBit4;
                final long protNN1111 = a1 & a2 & a3 & a4;
                final long acceptNonNull = otherInits.iNNBit;
                final long acceptNull = otherInits.iNBit | protNN1111;
                final long dontResetToStart = ~protNN1111 | acceptNonNull;
                a1 &= dontResetToStart;
                a2 &= (dontResetToStart & acceptNull);
                a3 &= (dontResetToStart & acceptNonNull);
                a4 &= dontResetToStart;
                a1 &= (a2 | a3 | a4);
                final long b1;
                final long b2;
                final long nb2;
                final long b3;
                final long nb3;
                final long na4;
                final long na5;
                final long na6;
                final long b4;
                final long nb4;
                this.nullBit1 = ((b1 = otherInits.nullBit1) | (a1 & ((a3 & a4 & (nb2 = ~(b2 = otherInits.nullBit2)) & (nb3 = ~(b3 = otherInits.nullBit4))) | (((na4 = ~a4) | (na5 = ~a3)) & (((na6 = ~a2) & nb2) | (a2 & (nb4 = ~(b4 = otherInits.nullBit3)) & nb3))))));
                final long nb5;
                final long na7;
                this.nullBit2 = ((b2 & (nb3 | nb4)) | (na5 & na4 & b2) | (a2 & ((nb4 & nb3) | ((nb5 = ~b1) & (na5 | (na7 = ~a1))) | (a1 & b2))));
                this.nullBit3 = ((b4 & ((nb5 & (b2 | a2 | na7)) | (b1 & (b3 | nb2 | (a1 & a3))) | (na7 & na6 & na4))) | (a3 & nb2 & nb3) | (nb5 & ((((na6 & a4) | na7) & a3) | (a1 & na6 & na4 & b2))));
                this.nullBit4 = ((nb5 & ((a4 & ((na5 & nb4) | ((a3 | na6) & nb2))) | (a1 & ((a3 & nb2 & b3) | (a2 & b2 & (b3 | (a3 & na4 & nb4))))))) | (b1 & ((a3 & a4 & b3) | (na6 & na4 & nb4 & b3) | (a2 & (((b4 | a4) & b3) | (na5 & a4 & b2 & b4))) | (na7 & (b3 | ((a4 | a2) & b2 & b4))))) | (((na7 & ((na5 & nb4) | (na6 & nb2))) | (a1 & ((nb2 & nb4) | (a2 & a3)))) & b3));
                this.iNBit &= otherInits.iNBit;
                this.iNNBit &= otherInits.iNNBit;
            }
            this.tagBits |= 0x4;
        }
        if (this.extra != null || otherInits.extra != null) {
            int mergeLimit = 0;
            int copyLimit = 0;
            if (this.extra != null) {
                if (otherInits.extra != null) {
                    final int length;
                    final int otherLength;
                    if ((length = this.extra[0].length) < (otherLength = otherInits.extra[0].length)) {
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[otherLength], 0, length);
                        }
                        mergeLimit = length;
                        copyLimit = otherLength;
                    }
                    else {
                        mergeLimit = otherLength;
                    }
                }
            }
            else if (otherInits.extra != null) {
                this.extra = new long[8][];
                final int otherLength2;
                System.arraycopy(otherInits.extra[0], 0, this.extra[0] = new long[otherLength2 = otherInits.extra[0].length], 0, otherLength2);
                System.arraycopy(otherInits.extra[1], 0, this.extra[1] = new long[otherLength2], 0, otherLength2);
                if (otherHasNulls) {
                    for (int i = 2; i < 8; ++i) {
                        System.arraycopy(otherInits.extra[i], 0, this.extra[i] = new long[otherLength2], 0, otherLength2);
                    }
                }
                else {
                    for (int i = 2; i < 8; ++i) {
                        this.extra[i] = new long[otherLength2];
                    }
                    System.arraycopy(otherInits.extra[6], 0, this.extra[6], 0, otherLength2);
                    System.arraycopy(otherInits.extra[7], 0, this.extra[7], 0, otherLength2);
                }
            }
            if (handleInits) {
                int k;
                for (k = 0; k < mergeLimit; ++k) {
                    final long[] array = this.extra[0];
                    final int n = k;
                    array[n] |= otherInits.extra[0][k];
                    final long[] array2 = this.extra[1];
                    final int n2 = k;
                    array2[n2] |= otherInits.extra[1][k];
                }
                while (k < copyLimit) {
                    this.extra[0][k] = otherInits.extra[0][k];
                    this.extra[1][k] = otherInits.extra[1][k];
                    ++k;
                }
            }
            if (!thisHadNulls) {
                if (copyLimit < mergeLimit) {
                    copyLimit = mergeLimit;
                }
                mergeLimit = 0;
            }
            if (!otherHasNulls) {
                copyLimit = 0;
                mergeLimit = 0;
            }
            int k;
            for (k = 0; k < mergeLimit; ++k) {
                long a1 = this.extra[2][k];
                long a2 = this.extra[3][k];
                long a3 = this.extra[4][k];
                long a4 = this.extra[5][k];
                final long protNN1112 = a1 & a2 & a3 & a4;
                final long acceptNonNull2 = otherInits.extra[7][k];
                final long acceptNull2 = otherInits.extra[6][k] | protNN1112;
                final long dontResetToStart2 = ~protNN1112 | acceptNonNull2;
                a1 &= dontResetToStart2;
                a2 &= (dontResetToStart2 & acceptNull2);
                a3 &= (dontResetToStart2 & acceptNonNull2);
                a4 &= dontResetToStart2;
                a1 &= (a2 | a3 | a4);
                final long b1;
                final long b2;
                final long nb2;
                final long b3;
                final long nb3;
                final long na4;
                final long na5;
                final long na6;
                final long b4;
                final long nb4;
                this.extra[2][k] = ((b1 = otherInits.extra[2][k]) | (a1 & ((a3 & a4 & (nb2 = ~(b2 = otherInits.extra[3][k])) & (nb3 = ~(b3 = otherInits.extra[5][k]))) | (((na4 = ~a4) | (na5 = ~a3)) & (((na6 = ~a2) & nb2) | (a2 & (nb4 = ~(b4 = otherInits.extra[4][k])) & nb3))))));
                final long nb5;
                final long na7;
                this.extra[3][k] = ((b2 & (nb3 | nb4)) | (na5 & na4 & b2) | (a2 & ((nb4 & nb3) | ((nb5 = ~b1) & (na5 | (na7 = ~a1))) | (a1 & b2))));
                this.extra[4][k] = ((b4 & ((nb5 & (b2 | a2 | na7)) | (b1 & (b3 | nb2 | (a1 & a3))) | (na7 & na6 & na4))) | (a3 & nb2 & nb3) | (nb5 & ((((na6 & a4) | na7) & a3) | (a1 & na6 & na4 & b2))));
                this.extra[5][k] = ((nb5 & ((a4 & ((na5 & nb4) | ((a3 | na6) & nb2))) | (a1 & ((a3 & nb2 & b3) | (a2 & b2 & (b3 | (a3 & na4 & nb4))))))) | (b1 & ((a3 & a4 & b3) | (na6 & na4 & nb4 & b3) | (a2 & (((b4 | a4) & b3) | (na5 & a4 & b2 & b4))) | (na7 & (b3 | ((a4 | a2) & b2 & b4))))) | (((na7 & ((na5 & nb4) | (na6 & nb2))) | (a1 & ((nb2 & nb4) | (a2 & a3)))) & b3));
                final long[] array3 = this.extra[6];
                final int n3 = k;
                array3[n3] &= otherInits.extra[6][k];
                final long[] array4 = this.extra[7];
                final int n4 = k;
                array4[n4] &= otherInits.extra[7][k];
            }
            while (k < copyLimit) {
                for (int i = 2; i < 8; ++i) {
                    this.extra[i][k] = otherInits.extra[i][k];
                }
                ++k;
            }
        }
        return this;
    }
    
    @Override
    public FlowInfo addPotentialInitializationsFrom(final FlowInfo inits) {
        if (this == UnconditionalFlowInfo.DEAD_END) {
            return this;
        }
        if (inits == UnconditionalFlowInfo.DEAD_END) {
            return this;
        }
        final UnconditionalFlowInfo otherInits = inits.unconditionalInits();
        this.potentialInits |= otherInits.potentialInits;
        if (this.extra != null) {
            if (otherInits.extra != null) {
                int i = 0;
                final int length;
                final int otherLength;
                if ((length = this.extra[0].length) < (otherLength = otherInits.extra[0].length)) {
                    for (int j = 0; j < 8; ++j) {
                        System.arraycopy(this.extra[j], 0, this.extra[j] = new long[otherLength], 0, length);
                    }
                    while (i < length) {
                        final long[] array = this.extra[1];
                        final int n = i;
                        array[n] |= otherInits.extra[1][i];
                        ++i;
                    }
                    while (i < otherLength) {
                        this.extra[1][i] = otherInits.extra[1][i];
                        ++i;
                    }
                }
                else {
                    while (i < otherLength) {
                        final long[] array2 = this.extra[1];
                        final int n2 = i;
                        array2[n2] |= otherInits.extra[1][i];
                        ++i;
                    }
                }
            }
        }
        else if (otherInits.extra != null) {
            final int otherLength2 = otherInits.extra[0].length;
            this.createExtraSpace(otherLength2);
            System.arraycopy(otherInits.extra[1], 0, this.extra[1], 0, otherLength2);
        }
        this.addPotentialNullInfoFrom(otherInits);
        return this;
    }
    
    public UnconditionalFlowInfo addPotentialNullInfoFrom(final UnconditionalFlowInfo otherInits) {
        if ((this.tagBits & 0x3) != 0x0 || (otherInits.tagBits & 0x3) != 0x0 || (otherInits.tagBits & 0x4) == 0x0) {
            return this;
        }
        final boolean thisHadNulls = (this.tagBits & 0x4) != 0x0;
        boolean thisHasNulls = false;
        if (thisHadNulls) {
            final long a1;
            final long a2;
            final long a3;
            final long b2;
            final long nb2;
            final long b3;
            final long nb3;
            final long b4;
            final long b5;
            final long a4;
            final long na2;
            final long na3;
            final long na4;
            final long nb4;
            this.nullBit1 = ((a1 = this.nullBit1) & (((a2 = this.nullBit3) & (a3 = this.nullBit4) & (((nb2 = ~(b2 = otherInits.nullBit2)) & (nb3 = ~(b3 = otherInits.nullBit4))) | ((b4 = otherInits.nullBit1) & (b5 = otherInits.nullBit3)))) | ((na2 = ~(a4 = this.nullBit2)) & ((b4 & b5) | (((na3 = ~a3) | (na4 = ~a2)) & nb2))) | (a4 & ((na3 | na4) & (((nb4 = ~b5) & nb3) | (b4 & b2))))));
            final long nb5;
            final long na5;
            this.nullBit2 = ((b2 & (nb4 | (nb5 = ~b4))) | (a4 & ((nb4 & nb3) | b2 | na4 | (na5 = ~a1))));
            this.nullBit3 = ((b5 & ((nb5 & b2) | (a4 & (nb2 | a2)) | (na5 & nb2) | (a1 & na2 & na3 & b4))) | (a2 & ((nb2 & nb3) | (na2 & a3) | na5)) | (a1 & na2 & na3 & b2));
            this.nullBit4 = ((na4 & ((nb5 & nb4 & b3) | (a3 & (nb4 | (b4 & b2))))) | (nb2 & ((na4 & b4 & nb4) | (na2 & ((nb5 & b3) | (b4 & nb4) | a3)))) | (a2 & ((a3 & (nb2 | (b4 & b5))) | (a1 & a4 & ((nb5 & b3) | (na3 & (b2 | b4) & nb4))))));
            if ((this.nullBit2 | this.nullBit3 | this.nullBit4) != 0x0L) {
                thisHasNulls = true;
            }
        }
        else {
            this.nullBit1 = 0L;
            final long b2;
            final long n = b2 = otherInits.nullBit2;
            final long b5;
            final long nb4 = ~(b5 = otherInits.nullBit3);
            final long b4;
            final long nb5;
            this.nullBit2 = (n & (nb4 | (nb5 = ~(b4 = otherInits.nullBit1))));
            final long nb2;
            this.nullBit3 = (b5 & (nb5 | (nb2 = ~b2)));
            final long b3;
            this.nullBit4 = ((~b4 & ~b5 & (b3 = otherInits.nullBit4)) | (~b2 & ((b4 & ~b5) | (~b4 & b3))));
            if ((this.nullBit2 | this.nullBit3 | this.nullBit4) != 0x0L) {
                thisHasNulls = true;
            }
        }
        if (otherInits.extra != null) {
            int mergeLimit = 0;
            final int copyLimit = otherInits.extra[0].length;
            if (this.extra == null) {
                this.createExtraSpace(copyLimit);
            }
            else {
                mergeLimit = copyLimit;
                if (mergeLimit > this.extra[0].length) {
                    mergeLimit = this.extra[0].length;
                    for (int j = 0; j < 8; ++j) {
                        System.arraycopy(this.extra[j], 0, this.extra[j] = new long[copyLimit], 0, mergeLimit);
                    }
                    if (!thisHadNulls) {
                        mergeLimit = 0;
                    }
                }
            }
            int i;
            for (i = 0; i < mergeLimit; ++i) {
                final long a1;
                final long a2;
                final long a3;
                final long b2;
                final long nb2;
                final long b3;
                final long nb3;
                final long b4;
                final long b5;
                final long a4;
                final long na2;
                final long na3;
                final long na4;
                final long nb4;
                this.extra[2][i] = ((a1 = this.extra[2][i]) & (((a2 = this.extra[4][i]) & (a3 = this.extra[5][i]) & (((nb2 = ~(b2 = otherInits.extra[3][i])) & (nb3 = ~(b3 = otherInits.extra[5][i]))) | ((b4 = otherInits.extra[2][i]) & (b5 = otherInits.extra[4][i])))) | ((na2 = ~(a4 = this.extra[3][i])) & ((b4 & b5) | (((na3 = ~a3) | (na4 = ~a2)) & nb2))) | (a4 & ((na3 | na4) & (((nb4 = ~b5) & nb3) | (b4 & b2))))));
                final long nb5;
                final long na5;
                this.extra[3][i] = ((b2 & (nb4 | (nb5 = ~b4))) | (a4 & ((nb4 & nb3) | b2 | na4 | (na5 = ~a1))));
                this.extra[4][i] = ((b5 & ((nb5 & b2) | (a4 & (nb2 | a2)) | (na5 & nb2) | (a1 & na2 & na3 & b4))) | (a2 & ((nb2 & nb3) | (na2 & a3) | na5)) | (a1 & na2 & na3 & b2));
                this.extra[5][i] = ((na4 & ((nb5 & nb4 & b3) | (a3 & (nb4 | (b4 & b2))))) | (nb2 & ((na4 & b4 & nb4) | (na2 & ((nb5 & b3) | (b4 & nb4) | a3)))) | (a2 & ((a3 & (nb2 | (b4 & b5))) | (a1 & a4 & ((nb5 & b3) | (na3 & (b2 | b4) & nb4))))));
                if ((this.extra[3][i] | this.extra[4][i] | this.extra[5][i]) != 0x0L) {
                    thisHasNulls = true;
                }
            }
            while (i < copyLimit) {
                this.extra[2][i] = 0L;
                final long[] array = this.extra[3];
                final int n2 = i;
                final long b2;
                final long n3 = b2 = otherInits.extra[3][i];
                final long b5;
                final long nb4 = ~(b5 = otherInits.extra[4][i]);
                final long b4;
                final long nb5;
                array[n2] = (n3 & (nb4 | (nb5 = ~(b4 = otherInits.extra[2][i]))));
                final long nb2;
                this.extra[4][i] = (b5 & (nb5 | (nb2 = ~b2)));
                final long b3;
                this.extra[5][i] = ((~b4 & ~b5 & (b3 = otherInits.extra[5][i])) | (~b2 & ((b4 & ~b5) | (~b4 & b3))));
                if ((this.extra[3][i] | this.extra[4][i] | this.extra[5][i]) != 0x0L) {
                    thisHasNulls = true;
                }
                ++i;
            }
        }
        if (thisHasNulls) {
            this.tagBits |= 0x4;
        }
        else {
            this.tagBits &= 0x4;
        }
        return this;
    }
    
    @Override
    public final boolean cannotBeDefinitelyNullOrNonNull(final LocalVariableBinding local) {
        if ((this.tagBits & 0x4) == 0x0 || (local.type.tagBits & 0x2L) != 0x0L) {
            return false;
        }
        final int position;
        if ((position = local.id + this.maxFieldCount) < 64) {
            return (((~this.nullBit1 & ((this.nullBit2 & this.nullBit3) | this.nullBit4)) | (~this.nullBit2 & ~this.nullBit3 & this.nullBit4)) & 1L << position) != 0x0L;
        }
        if (this.extra == null) {
            return false;
        }
        final int vectorIndex;
        if ((vectorIndex = position / 64 - 1) >= this.extra[0].length) {
            return false;
        }
        final long n = ~this.extra[2][vectorIndex];
        final long a2 = this.extra[3][vectorIndex];
        final long a3;
        final long a4;
        return (((n & ((a2 & (a3 = this.extra[4][vectorIndex])) | (a4 = this.extra[5][vectorIndex]))) | (~a2 & ~a3 & a4)) & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean cannotBeNull(final LocalVariableBinding local) {
        if ((this.tagBits & 0x4) == 0x0 || (local.type.tagBits & 0x2L) != 0x0L) {
            return false;
        }
        final int position;
        if ((position = local.id + this.maxFieldCount) < 64) {
            return (this.nullBit1 & this.nullBit3 & ((this.nullBit2 & this.nullBit4) | ~this.nullBit2) & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[0].length && (this.extra[2][vectorIndex] & this.extra[4][vectorIndex] & ((this.extra[3][vectorIndex] & this.extra[5][vectorIndex]) | ~this.extra[3][vectorIndex]) & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean canOnlyBeNull(final LocalVariableBinding local) {
        if ((this.tagBits & 0x4) == 0x0 || (local.type.tagBits & 0x2L) != 0x0L) {
            return false;
        }
        final int position;
        if ((position = local.id + this.maxFieldCount) < 64) {
            return (this.nullBit1 & this.nullBit2 & (~this.nullBit3 | ~this.nullBit4) & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[0].length && (this.extra[2][vectorIndex] & this.extra[3][vectorIndex] & (~this.extra[4][vectorIndex] | ~this.extra[5][vectorIndex]) & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public FlowInfo copy() {
        if (this == UnconditionalFlowInfo.DEAD_END) {
            return this;
        }
        final UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
        copy.definiteInits = this.definiteInits;
        copy.potentialInits = this.potentialInits;
        final boolean hasNullInfo = (this.tagBits & 0x4) != 0x0;
        if (hasNullInfo) {
            copy.nullBit1 = this.nullBit1;
            copy.nullBit2 = this.nullBit2;
            copy.nullBit3 = this.nullBit3;
            copy.nullBit4 = this.nullBit4;
        }
        copy.iNBit = this.iNBit;
        copy.iNNBit = this.iNNBit;
        copy.tagBits = this.tagBits;
        copy.maxFieldCount = this.maxFieldCount;
        if (this.extra != null) {
            copy.extra = new long[8][];
            final int length;
            System.arraycopy(this.extra[0], 0, copy.extra[0] = new long[length = this.extra[0].length], 0, length);
            System.arraycopy(this.extra[1], 0, copy.extra[1] = new long[length], 0, length);
            if (hasNullInfo) {
                for (int j = 2; j < 8; ++j) {
                    System.arraycopy(this.extra[j], 0, copy.extra[j] = new long[length], 0, length);
                }
            }
            else {
                for (int j = 2; j < 8; ++j) {
                    copy.extra[j] = new long[length];
                }
            }
        }
        return copy;
    }
    
    public UnconditionalFlowInfo discardInitializationInfo() {
        if (this == UnconditionalFlowInfo.DEAD_END) {
            return this;
        }
        final long n = 0L;
        this.potentialInits = n;
        this.definiteInits = n;
        if (this.extra != null) {
            for (int i = 0, length = this.extra[0].length; i < length; ++i) {
                this.extra[0][i] = (this.extra[1][i] = 0L);
            }
        }
        return this;
    }
    
    public UnconditionalFlowInfo discardNonFieldInitializations() {
        final int limit = this.maxFieldCount;
        if (limit < 64) {
            final long mask = (1L << limit) - 1L;
            this.definiteInits &= mask;
            this.potentialInits &= mask;
            this.nullBit1 &= mask;
            this.nullBit2 &= mask;
            this.nullBit3 &= mask;
            this.nullBit4 &= mask;
            this.iNBit &= mask;
            this.iNNBit &= mask;
        }
        if (this.extra == null) {
            return this;
        }
        final int length = this.extra[0].length;
        final int vectorIndex;
        if ((vectorIndex = limit / 64 - 1) >= length) {
            return this;
        }
        if (vectorIndex >= 0) {
            final long mask2 = (1L << limit % 64) - 1L;
            for (int j = 0; j < 8; ++j) {
                final long[] array = this.extra[j];
                final int n = vectorIndex;
                array[n] &= mask2;
            }
        }
        for (int i = vectorIndex + 1; i < length; ++i) {
            for (int k = 0; k < 8; ++k) {
                this.extra[k][i] = 0L;
            }
        }
        return this;
    }
    
    @Override
    public FlowInfo initsWhenFalse() {
        return this;
    }
    
    @Override
    public FlowInfo initsWhenTrue() {
        return this;
    }
    
    private final boolean isDefinitelyAssigned(final int position) {
        if (position < 64) {
            return (this.definiteInits & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[0].length && (this.extra[0][vectorIndex] & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean isDefinitelyAssigned(final FieldBinding field) {
        return (this.tagBits & 0x1) != 0x0 || this.isDefinitelyAssigned(field.id);
    }
    
    @Override
    public final boolean isDefinitelyAssigned(final LocalVariableBinding local) {
        return ((this.tagBits & 0x1) != 0x0 && (local.declaration.bits & 0x40000000) != 0x0) || this.isDefinitelyAssigned(local.id + this.maxFieldCount);
    }
    
    @Override
    public final boolean isDefinitelyNonNull(final LocalVariableBinding local) {
        if ((this.tagBits & 0x3) != 0x0 || (this.tagBits & 0x4) == 0x0) {
            return false;
        }
        if ((local.type.tagBits & 0x2L) != 0x0L || local.constant() != Constant.NotAConstant) {
            return true;
        }
        final int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit1 & this.nullBit3 & (~this.nullBit2 | this.nullBit4) & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[2].length && (this.extra[2][vectorIndex] & this.extra[4][vectorIndex] & (~this.extra[3][vectorIndex] | this.extra[5][vectorIndex]) & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean isDefinitelyNull(final LocalVariableBinding local) {
        if ((this.tagBits & 0x3) != 0x0 || (this.tagBits & 0x4) == 0x0 || (local.type.tagBits & 0x2L) != 0x0L) {
            return false;
        }
        final int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit1 & this.nullBit2 & (~this.nullBit3 | ~this.nullBit4) & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[2].length && (this.extra[2][vectorIndex] & this.extra[3][vectorIndex] & (~this.extra[4][vectorIndex] | ~this.extra[5][vectorIndex]) & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean isDefinitelyUnknown(final LocalVariableBinding local) {
        if ((this.tagBits & 0x3) != 0x0 || (this.tagBits & 0x4) == 0x0) {
            return false;
        }
        final int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit1 & this.nullBit4 & ~this.nullBit2 & ~this.nullBit3 & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[2].length && (this.extra[2][vectorIndex] & this.extra[5][vectorIndex] & ~this.extra[3][vectorIndex] & ~this.extra[4][vectorIndex] & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean hasNullInfoFor(final LocalVariableBinding local) {
        if ((this.tagBits & 0x3) != 0x0 || (this.tagBits & 0x4) == 0x0) {
            return false;
        }
        final int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return ((this.nullBit1 | this.nullBit2 | this.nullBit3 | this.nullBit4) & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[2].length && ((this.extra[2][vectorIndex] | this.extra[3][vectorIndex] | this.extra[4][vectorIndex] | this.extra[5][vectorIndex]) & 1L << position % 64) != 0x0L;
    }
    
    private final boolean isPotentiallyAssigned(final int position) {
        if (position < 64) {
            return (this.potentialInits & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[0].length && (this.extra[1][vectorIndex] & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean isPotentiallyAssigned(final FieldBinding field) {
        return this.isPotentiallyAssigned(field.id);
    }
    
    @Override
    public final boolean isPotentiallyAssigned(final LocalVariableBinding local) {
        return local.constant() != Constant.NotAConstant || this.isPotentiallyAssigned(local.id + this.maxFieldCount);
    }
    
    @Override
    public final boolean isPotentiallyNonNull(final LocalVariableBinding local) {
        if ((this.tagBits & 0x4) == 0x0 || (local.type.tagBits & 0x2L) != 0x0L) {
            return false;
        }
        final int position;
        if ((position = local.id + this.maxFieldCount) < 64) {
            return (this.nullBit3 & (~this.nullBit1 | ~this.nullBit2) & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[2].length && (this.extra[4][vectorIndex] & (~this.extra[2][vectorIndex] | ~this.extra[3][vectorIndex]) & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean isPotentiallyNull(final LocalVariableBinding local) {
        if ((this.tagBits & 0x4) == 0x0 || (local.type.tagBits & 0x2L) != 0x0L) {
            return false;
        }
        final int position;
        if ((position = local.id + this.maxFieldCount) < 64) {
            return (this.nullBit2 & (~this.nullBit1 | ~this.nullBit3) & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[2].length && (this.extra[3][vectorIndex] & (~this.extra[2][vectorIndex] | ~this.extra[4][vectorIndex]) & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean isPotentiallyUnknown(final LocalVariableBinding local) {
        if ((this.tagBits & 0x3) != 0x0 || (this.tagBits & 0x4) == 0x0) {
            return false;
        }
        final int position = local.id + this.maxFieldCount;
        if (position < 64) {
            return (this.nullBit4 & (~this.nullBit1 | (~this.nullBit2 & ~this.nullBit3)) & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[2].length && (this.extra[5][vectorIndex] & (~this.extra[2][vectorIndex] | (~this.extra[3][vectorIndex] & ~this.extra[4][vectorIndex])) & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean isProtectedNonNull(final LocalVariableBinding local) {
        if ((this.tagBits & 0x4) == 0x0 || (local.type.tagBits & 0x2L) != 0x0L) {
            return false;
        }
        final int position;
        if ((position = local.id + this.maxFieldCount) < 64) {
            return (this.nullBit1 & this.nullBit3 & this.nullBit4 & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[0].length && (this.extra[2][vectorIndex] & this.extra[4][vectorIndex] & this.extra[5][vectorIndex] & 1L << position % 64) != 0x0L;
    }
    
    @Override
    public final boolean isProtectedNull(final LocalVariableBinding local) {
        if ((this.tagBits & 0x4) == 0x0 || (local.type.tagBits & 0x2L) != 0x0L) {
            return false;
        }
        final int position;
        if ((position = local.id + this.maxFieldCount) < 64) {
            return (this.nullBit1 & this.nullBit2 & (this.nullBit3 ^ this.nullBit4) & 1L << position) != 0x0L;
        }
        final int vectorIndex;
        return this.extra != null && (vectorIndex = position / 64 - 1) < this.extra[0].length && (this.extra[2][vectorIndex] & this.extra[3][vectorIndex] & (this.extra[4][vectorIndex] ^ this.extra[5][vectorIndex]) & 1L << position % 64) != 0x0L;
    }
    
    protected static boolean isTrue(final boolean expression, final String message) {
        if (!expression) {
            throw new AssertionFailedException("assertion failed: " + message);
        }
        return expression;
    }
    
    @Override
    public void markAsComparedEqualToNonNull(final LocalVariableBinding local) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.tagBits |= 0x4;
            final int position;
            if ((position = local.id + this.maxFieldCount) < 64) {
                final long mask;
                final long a1;
                final long a2;
                final long na2;
                final long a3;
                final long a4;
                if (((mask = 1L << position) & (a1 = this.nullBit1) & (na2 = ~(a2 = this.nullBit2)) & ~(a3 = this.nullBit3) & (a4 = this.nullBit4)) != 0x0L) {
                    this.nullBit4 &= ~mask;
                }
                else if ((mask & a1 & na2 & a3) == 0x0L) {
                    this.nullBit4 |= mask;
                    if ((mask & a1) == 0x0L) {
                        if ((mask & a2 & (a3 ^ a4)) != 0x0L) {
                            this.nullBit2 &= ~mask;
                        }
                        else if ((mask & (a2 | a3 | a4)) == 0x0L) {
                            this.nullBit2 |= mask;
                        }
                    }
                }
                this.nullBit1 |= mask;
                this.nullBit3 |= mask;
                this.iNBit &= ~mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    final int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                }
                else {
                    final int oldLength;
                    if (vectorIndex >= (oldLength = this.extra[0].length)) {
                        final int newLength = vectorIndex + 1;
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[newLength], 0, oldLength);
                        }
                    }
                }
                final long mask;
                final long a1;
                final long a2;
                final long na2;
                final long a3;
                final long a4;
                if (((mask = 1L << position % 64) & (a1 = this.extra[2][vectorIndex]) & (na2 = ~(a2 = this.extra[3][vectorIndex])) & ~(a3 = this.extra[4][vectorIndex]) & (a4 = this.extra[5][vectorIndex])) != 0x0L) {
                    final long[] array = this.extra[5];
                    final int n = vectorIndex;
                    array[n] &= ~mask;
                }
                else if ((mask & a1 & na2 & a3) == 0x0L) {
                    final long[] array2 = this.extra[5];
                    final int n2 = vectorIndex;
                    array2[n2] |= mask;
                    if ((mask & a1) == 0x0L) {
                        if ((mask & a2 & (a3 ^ a4)) != 0x0L) {
                            final long[] array3 = this.extra[3];
                            final int n3 = vectorIndex;
                            array3[n3] &= ~mask;
                        }
                        else if ((mask & (a2 | a3 | a4)) == 0x0L) {
                            final long[] array4 = this.extra[3];
                            final int n4 = vectorIndex;
                            array4[n4] |= mask;
                        }
                    }
                }
                final long[] array5 = this.extra[2];
                final int n5 = vectorIndex;
                array5[n5] |= mask;
                final long[] array6 = this.extra[4];
                final int n6 = vectorIndex;
                array6[n6] |= mask;
                final long[] array7 = this.extra[6];
                final int n7 = vectorIndex;
                array7[n7] &= ~mask;
            }
        }
    }
    
    @Override
    public void markAsComparedEqualToNull(final LocalVariableBinding local) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.tagBits |= 0x4;
            final int position;
            if ((position = local.id + this.maxFieldCount) < 64) {
                final long mask;
                if (((mask = 1L << position) & this.nullBit1) != 0x0L) {
                    if ((mask & (~this.nullBit2 | this.nullBit3 | ~this.nullBit4)) != 0x0L) {
                        this.nullBit4 &= ~mask;
                    }
                }
                else if ((mask & this.nullBit4) != 0x0L) {
                    this.nullBit3 &= ~mask;
                }
                else if ((mask & this.nullBit2) != 0x0L) {
                    this.nullBit3 &= ~mask;
                    this.nullBit4 |= mask;
                }
                else {
                    this.nullBit3 |= mask;
                }
                this.nullBit1 |= mask;
                this.nullBit2 |= mask;
                this.iNNBit &= ~mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                final long mask = 1L << position % 64;
                if (this.extra == null) {
                    final int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                }
                else {
                    final int oldLength;
                    if (vectorIndex >= (oldLength = this.extra[0].length)) {
                        final int newLength = vectorIndex + 1;
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[newLength], 0, oldLength);
                        }
                    }
                }
                if ((mask & this.extra[2][vectorIndex]) != 0x0L) {
                    if ((mask & (~this.extra[3][vectorIndex] | this.extra[4][vectorIndex] | ~this.extra[5][vectorIndex])) != 0x0L) {
                        final long[] array = this.extra[5];
                        final int n = vectorIndex;
                        array[n] &= ~mask;
                    }
                }
                else if ((mask & this.extra[5][vectorIndex]) != 0x0L) {
                    final long[] array2 = this.extra[4];
                    final int n2 = vectorIndex;
                    array2[n2] &= ~mask;
                }
                else if ((mask & this.extra[3][vectorIndex]) != 0x0L) {
                    final long[] array3 = this.extra[4];
                    final int n3 = vectorIndex;
                    array3[n3] &= ~mask;
                    final long[] array4 = this.extra[5];
                    final int n4 = vectorIndex;
                    array4[n4] |= mask;
                }
                else {
                    final long[] array5 = this.extra[4];
                    final int n5 = vectorIndex;
                    array5[n5] |= mask;
                }
                final long[] array6 = this.extra[2];
                final int n6 = vectorIndex;
                array6[n6] |= mask;
                final long[] array7 = this.extra[3];
                final int n7 = vectorIndex;
                array7[n7] |= mask;
                final long[] array8 = this.extra[7];
                final int n8 = vectorIndex;
                array8[n8] &= ~mask;
            }
        }
    }
    
    private final void markAsDefinitelyAssigned(final int position) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            if (position < 64) {
                final long mask;
                this.definiteInits |= (mask = 1L << position);
                this.potentialInits |= mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    final int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                }
                else {
                    final int oldLength;
                    if (vectorIndex >= (oldLength = this.extra[0].length)) {
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[vectorIndex + 1], 0, oldLength);
                        }
                    }
                }
                final long[] array = this.extra[0];
                final int n = vectorIndex;
                final long mask2;
                array[n] |= (mask2 = 1L << position % 64);
                final long[] array2 = this.extra[1];
                final int n2 = vectorIndex;
                array2[n2] |= mask2;
            }
        }
    }
    
    @Override
    public void markAsDefinitelyAssigned(final FieldBinding field) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.markAsDefinitelyAssigned(field.id);
        }
    }
    
    @Override
    public void markAsDefinitelyAssigned(final LocalVariableBinding local) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.markAsDefinitelyAssigned(local.id + this.maxFieldCount);
        }
    }
    
    @Override
    public void markAsDefinitelyNonNull(final LocalVariableBinding local) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.tagBits |= 0x4;
            final int position;
            if ((position = local.id + this.maxFieldCount) < 64) {
                long mask;
                this.nullBit1 |= (mask = 1L << position);
                this.nullBit3 |= mask;
                this.nullBit2 &= (mask ^= -1L);
                this.nullBit4 &= mask;
                this.iNBit &= mask;
                this.iNNBit &= mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    final int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                }
                else {
                    final int oldLength;
                    if (vectorIndex >= (oldLength = this.extra[0].length)) {
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[vectorIndex + 1], 0, oldLength);
                        }
                    }
                }
                final long[] array = this.extra[2];
                final int n = vectorIndex;
                long mask;
                array[n] |= (mask = 1L << position % 64);
                final long[] array2 = this.extra[4];
                final int n2 = vectorIndex;
                array2[n2] |= mask;
                final long[] array3 = this.extra[3];
                final int n3 = vectorIndex;
                array3[n3] &= (mask ^= -1L);
                final long[] array4 = this.extra[5];
                final int n4 = vectorIndex;
                array4[n4] &= mask;
                final long[] array5 = this.extra[6];
                final int n5 = vectorIndex;
                array5[n5] &= mask;
                final long[] array6 = this.extra[7];
                final int n6 = vectorIndex;
                array6[n6] &= mask;
            }
        }
    }
    
    @Override
    public void markAsDefinitelyNull(final LocalVariableBinding local) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.tagBits |= 0x4;
            final int position;
            if ((position = local.id + this.maxFieldCount) < 64) {
                long mask;
                this.nullBit1 |= (mask = 1L << position);
                this.nullBit2 |= mask;
                this.nullBit3 &= (mask ^= -1L);
                this.nullBit4 &= mask;
                this.iNBit &= mask;
                this.iNNBit &= mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    final int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                }
                else {
                    final int oldLength;
                    if (vectorIndex >= (oldLength = this.extra[0].length)) {
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[vectorIndex + 1], 0, oldLength);
                        }
                    }
                }
                final long[] array = this.extra[2];
                final int n = vectorIndex;
                long mask;
                array[n] |= (mask = 1L << position % 64);
                final long[] array2 = this.extra[3];
                final int n2 = vectorIndex;
                array2[n2] |= mask;
                final long[] array3 = this.extra[4];
                final int n3 = vectorIndex;
                array3[n3] &= (mask ^= -1L);
                final long[] array4 = this.extra[5];
                final int n4 = vectorIndex;
                array4[n4] &= mask;
                final long[] array5 = this.extra[6];
                final int n5 = vectorIndex;
                array5[n5] &= mask;
                final long[] array6 = this.extra[7];
                final int n6 = vectorIndex;
                array6[n6] &= mask;
            }
        }
    }
    
    @Override
    public void markAsDefinitelyUnknown(final LocalVariableBinding local) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.tagBits |= 0x4;
            final int position;
            if ((position = local.id + this.maxFieldCount) < 64) {
                long mask;
                this.nullBit1 |= (mask = 1L << position);
                this.nullBit4 |= mask;
                this.nullBit2 &= (mask ^= -1L);
                this.nullBit3 &= mask;
                this.iNBit &= mask;
                this.iNNBit &= mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    final int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                }
                else {
                    final int oldLength;
                    if (vectorIndex >= (oldLength = this.extra[0].length)) {
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[vectorIndex + 1], 0, oldLength);
                        }
                    }
                }
                final long[] array = this.extra[2];
                final int n = vectorIndex;
                long mask;
                array[n] |= (mask = 1L << position % 64);
                final long[] array2 = this.extra[5];
                final int n2 = vectorIndex;
                array2[n2] |= mask;
                final long[] array3 = this.extra[3];
                final int n3 = vectorIndex;
                array3[n3] &= (mask ^= -1L);
                final long[] array4 = this.extra[4];
                final int n4 = vectorIndex;
                array4[n4] &= mask;
                final long[] array5 = this.extra[6];
                final int n5 = vectorIndex;
                array5[n5] &= mask;
                final long[] array6 = this.extra[7];
                final int n6 = vectorIndex;
                array6[n6] &= mask;
            }
        }
    }
    
    @Override
    public void resetNullInfo(final LocalVariableBinding local) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.tagBits |= 0x4;
            final int position;
            if ((position = local.id + this.maxFieldCount) < 64) {
                final long mask;
                this.nullBit1 &= (mask = ~(1L << position));
                this.nullBit2 &= mask;
                this.nullBit3 &= mask;
                this.nullBit4 &= mask;
                this.iNBit &= mask;
                this.iNNBit &= mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                if (this.extra == null || vectorIndex >= this.extra[2].length) {
                    return;
                }
                final long[] array = this.extra[2];
                final int n = vectorIndex;
                final long mask;
                array[n] &= (mask = ~(1L << position % 64));
                final long[] array2 = this.extra[3];
                final int n2 = vectorIndex;
                array2[n2] &= mask;
                final long[] array3 = this.extra[4];
                final int n3 = vectorIndex;
                array3[n3] &= mask;
                final long[] array4 = this.extra[5];
                final int n4 = vectorIndex;
                array4[n4] &= mask;
                final long[] array5 = this.extra[6];
                final int n5 = vectorIndex;
                array5[n5] &= mask;
                final long[] array6 = this.extra[7];
                final int n6 = vectorIndex;
                array6[n6] &= mask;
            }
        }
    }
    
    @Override
    public void markPotentiallyUnknownBit(final LocalVariableBinding local) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.tagBits |= 0x4;
            final int position;
            if ((position = local.id + this.maxFieldCount) < 64) {
                final long mask = 1L << position;
                isTrue((this.nullBit1 & mask) == 0x0L, "Adding 'unknown' mark in unexpected state");
                this.nullBit4 |= mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    final int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                }
                else {
                    final int oldLength;
                    if (vectorIndex >= (oldLength = this.extra[0].length)) {
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[vectorIndex + 1], 0, oldLength);
                        }
                    }
                }
                final long mask = 1L << position % 64;
                isTrue((this.extra[2][vectorIndex] & mask) == 0x0L, "Adding 'unknown' mark in unexpected state");
                final long[] array = this.extra[5];
                final int n = vectorIndex;
                array[n] |= mask;
            }
        }
    }
    
    @Override
    public void markPotentiallyNullBit(final LocalVariableBinding local) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.tagBits |= 0x4;
            final int position;
            if ((position = local.id + this.maxFieldCount) < 64) {
                final long mask = 1L << position;
                isTrue((this.nullBit1 & mask) == 0x0L, "Adding 'potentially null' mark in unexpected state");
                this.nullBit2 |= mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    final int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                }
                else {
                    final int oldLength;
                    if (vectorIndex >= (oldLength = this.extra[0].length)) {
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[vectorIndex + 1], 0, oldLength);
                        }
                    }
                }
                final long mask = 1L << position % 64;
                final long[] array = this.extra[3];
                final int n = vectorIndex;
                array[n] |= mask;
                isTrue((this.extra[2][vectorIndex] & mask) == 0x0L, "Adding 'potentially null' mark in unexpected state");
            }
        }
    }
    
    @Override
    public void markPotentiallyNonNullBit(final LocalVariableBinding local) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            this.tagBits |= 0x4;
            final int position;
            if ((position = local.id + this.maxFieldCount) < 64) {
                final long mask = 1L << position;
                isTrue((this.nullBit1 & mask) == 0x0L, "Adding 'potentially non-null' mark in unexpected state");
                this.nullBit3 |= mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                if (this.extra == null) {
                    final int length = vectorIndex + 1;
                    this.createExtraSpace(length);
                }
                else {
                    final int oldLength;
                    if (vectorIndex >= (oldLength = this.extra[0].length)) {
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[vectorIndex + 1], 0, oldLength);
                        }
                    }
                }
                final long mask = 1L << position % 64;
                isTrue((this.extra[2][vectorIndex] & mask) == 0x0L, "Adding 'potentially non-null' mark in unexpected state");
                final long[] array = this.extra[4];
                final int n = vectorIndex;
                array[n] |= mask;
            }
        }
    }
    
    @Override
    public UnconditionalFlowInfo mergedWith(final UnconditionalFlowInfo otherInits) {
        if ((otherInits.tagBits & 0x1) != 0x0 && this != UnconditionalFlowInfo.DEAD_END) {
            return this;
        }
        if ((this.tagBits & 0x1) != 0x0) {
            return (UnconditionalFlowInfo)otherInits.copy();
        }
        this.definiteInits &= otherInits.definiteInits;
        this.potentialInits |= otherInits.potentialInits;
        boolean thisHasNulls = (this.tagBits & 0x4) != 0x0;
        boolean otherHasNulls = (otherInits.tagBits & 0x4) != 0x0;
        boolean thisHadNulls = thisHasNulls;
        if ((otherInits.tagBits & 0x2) != 0x0) {
            otherHasNulls = false;
        }
        else if ((this.tagBits & 0x2) != 0x0) {
            this.nullBit1 = otherInits.nullBit1;
            this.nullBit2 = otherInits.nullBit2;
            this.nullBit3 = otherInits.nullBit3;
            this.nullBit4 = otherInits.nullBit4;
            this.iNBit = otherInits.iNBit;
            this.iNNBit = otherInits.iNNBit;
            thisHadNulls = false;
            thisHasNulls = otherHasNulls;
            this.tagBits = otherInits.tagBits;
        }
        else if (thisHadNulls) {
            if (otherHasNulls) {
                final long a1;
                final long b1;
                final long a2;
                final long b2;
                final long a3;
                final long a4;
                final long b3;
                final long b4;
                final long nb2;
                final long na2;
                final long na3;
                this.nullBit1 = ((a1 = this.nullBit1) & (b1 = otherInits.nullBit1) & (((a2 = this.nullBit2) & (((b2 = otherInits.nullBit2) & ~(((a3 = this.nullBit3) & (a4 = this.nullBit4)) ^ ((b3 = otherInits.nullBit3) & (b4 = otherInits.nullBit4)))) | (a3 & a4 & (nb2 = ~b2)))) | ((na2 = ~a2) & ((b2 & b3 & b4) | (nb2 & ((na3 = ~a3) ^ b3))))));
                final long nb3;
                final long nb4;
                final long na4;
                final long nb5;
                final long na5;
                this.nullBit2 = ((b2 & ((nb3 = ~b3) | (nb4 = ~b1) | (a3 & (a4 | (na4 = ~a1)) & (nb5 = ~b4)))) | (a2 & (b2 | ((na5 = ~a4) & b3 & (b4 | nb4)) | na3 | na4)));
                this.nullBit3 = ((a3 & (na4 | (a1 & na2) | (b3 & (na5 ^ b4)))) | (b3 & (nb4 | (b1 & nb2))));
                this.nullBit4 = ((na3 & ((nb4 & nb3 & b4) | (b1 & ((nb2 & nb3) | (a4 & b2 & nb5))) | (na4 & a4 & (nb3 | (b1 & b2))))) | (a3 & a4 & ((b3 & b4) | (b1 & nb2) | (na4 & a2))) | (na2 & ((nb4 & b4) | (b1 & nb3) | (na4 & a4)) & nb2) | (a1 & ((na3 & ((nb3 & b4) | (b1 & b2 & b3 & nb5) | (na2 & (nb3 | nb2)))) | (na2 & b3 & b4) | (a2 & ((nb4 & b4) | (a3 & na5 & b1)) & nb3))) | (nb4 & b2 & b3 & b4));
            }
            else {
                final long a1 = this.nullBit1;
                this.nullBit1 = 0L;
                final long a2 = this.nullBit2;
                final long a3;
                final long na3;
                final long na4;
                this.nullBit2 = (a2 & (na3 = (~(a3 = this.nullBit3) | (na4 = ~a1))));
                final long a4;
                final long na2;
                this.nullBit3 = ((a3 & (((na2 = ~a2) & (a4 = this.nullBit4)) | na4)) | (a1 & na2 & ~a4));
                this.nullBit4 = (((na3 | na2) & na4 & a4) | (a1 & na3 & na2));
            }
            this.iNBit |= otherInits.iNBit;
            this.iNNBit |= otherInits.iNNBit;
        }
        else if (otherHasNulls) {
            this.nullBit1 = 0L;
            final long b2 = otherInits.nullBit2;
            final long b1;
            final long b3;
            final long nb3;
            final long nb4;
            this.nullBit2 = (b2 & (nb3 = (~(b3 = otherInits.nullBit3) | (nb4 = ~(b1 = otherInits.nullBit1)))));
            final long b4;
            final long nb2;
            this.nullBit3 = ((b3 & (((nb2 = ~b2) & (b4 = otherInits.nullBit4)) | nb4)) | (b1 & nb2 & ~b4));
            this.nullBit4 = (((nb3 | nb2) & nb4 & b4) | (b1 & nb3 & nb2));
            this.iNBit |= otherInits.iNBit;
            this.iNNBit |= otherInits.iNNBit;
            thisHasNulls = (this.nullBit2 != 0L || this.nullBit3 != 0L || this.nullBit4 != 0L);
        }
        if (this.extra != null || otherInits.extra != null) {
            int mergeLimit = 0;
            int copyLimit = 0;
            int resetLimit = 0;
            if (this.extra != null) {
                if (otherInits.extra != null) {
                    final int length;
                    final int otherLength;
                    if ((length = this.extra[0].length) < (otherLength = otherInits.extra[0].length)) {
                        for (int j = 0; j < 8; ++j) {
                            System.arraycopy(this.extra[j], 0, this.extra[j] = new long[otherLength], 0, length);
                        }
                        mergeLimit = length;
                        copyLimit = otherLength;
                    }
                    else {
                        mergeLimit = otherLength;
                        resetLimit = length;
                    }
                }
                else {
                    resetLimit = this.extra[0].length;
                }
            }
            else if (otherInits.extra != null) {
                final int otherLength2 = otherInits.extra[0].length;
                this.extra = new long[8][];
                for (int i = 0; i < 8; ++i) {
                    this.extra[i] = new long[otherLength2];
                }
                System.arraycopy(otherInits.extra[1], 0, this.extra[1], 0, otherLength2);
                System.arraycopy(otherInits.extra[6], 0, this.extra[6], 0, otherLength2);
                System.arraycopy(otherInits.extra[7], 0, this.extra[7], 0, otherLength2);
                copyLimit = otherLength2;
            }
            int k;
            for (k = 0; k < mergeLimit; ++k) {
                final long[] array = this.extra[0];
                final int n = k;
                array[n] &= otherInits.extra[0][k];
                final long[] array2 = this.extra[1];
                final int n2 = k;
                array2[n2] |= otherInits.extra[1][k];
            }
            while (k < copyLimit) {
                this.extra[1][k] = otherInits.extra[1][k];
                ++k;
            }
            while (k < resetLimit) {
                this.extra[0][k] = 0L;
                ++k;
            }
            if (!otherHasNulls) {
                if (resetLimit < mergeLimit) {
                    resetLimit = mergeLimit;
                }
                copyLimit = 0;
                mergeLimit = 0;
            }
            if (!thisHadNulls) {
                resetLimit = 0;
            }
            for (k = 0; k < mergeLimit; ++k) {
                final long a1;
                final long b1;
                final long a2;
                final long b2;
                final long a3;
                final long a4;
                final long b3;
                final long b4;
                final long nb2;
                final long na2;
                final long na3;
                this.extra[2][k] = ((a1 = this.extra[2][k]) & (b1 = otherInits.extra[2][k]) & (((a2 = this.extra[3][k]) & (((b2 = otherInits.extra[3][k]) & ~(((a3 = this.extra[4][k]) & (a4 = this.extra[5][k])) ^ ((b3 = otherInits.extra[4][k]) & (b4 = otherInits.extra[5][k])))) | (a3 & a4 & (nb2 = ~b2)))) | ((na2 = ~a2) & ((b2 & b3 & b4) | (nb2 & ((na3 = ~a3) ^ b3))))));
                final long nb3;
                final long nb4;
                final long na4;
                final long nb5;
                final long na5;
                this.extra[3][k] = ((b2 & ((nb3 = ~b3) | (nb4 = ~b1) | (a3 & (a4 | (na4 = ~a1)) & (nb5 = ~b4)))) | (a2 & (b2 | ((na5 = ~a4) & b3 & (b4 | nb4)) | na3 | na4)));
                this.extra[4][k] = ((a3 & (na4 | (a1 & na2) | (b3 & (na5 ^ b4)))) | (b3 & (nb4 | (b1 & nb2))));
                this.extra[5][k] = ((na3 & ((nb4 & nb3 & b4) | (b1 & ((nb2 & nb3) | (a4 & b2 & nb5))) | (na4 & a4 & (nb3 | (b1 & b2))))) | (a3 & a4 & ((b3 & b4) | (b1 & nb2) | (na4 & a2))) | (na2 & ((nb4 & b4) | (b1 & nb3) | (na4 & a4)) & nb2) | (a1 & ((na3 & ((nb3 & b4) | (b1 & b2 & b3 & nb5) | (na2 & (nb3 | nb2)))) | (na2 & b3 & b4) | (a2 & ((nb4 & b4) | (a3 & na5 & b1)) & nb3))) | (nb4 & b2 & b3 & b4));
                final long[] array3 = this.extra[6];
                final int n3 = k;
                array3[n3] |= otherInits.extra[6][k];
                final long[] array4 = this.extra[7];
                final int n4 = k;
                array4[n4] |= otherInits.extra[7][k];
                thisHasNulls = (thisHasNulls || this.extra[3][k] != 0L || this.extra[4][k] != 0L || this.extra[5][k] != 0L);
            }
            while (k < copyLimit) {
                this.extra[2][k] = 0L;
                final long[] array5 = this.extra[3];
                final int n5 = k;
                final long b2 = otherInits.extra[3][k];
                final long b1;
                final long b3;
                final long nb3;
                final long nb4;
                array5[n5] = (b2 & (nb3 = (~(b3 = otherInits.extra[4][k]) | (nb4 = ~(b1 = otherInits.extra[2][k])))));
                final long b4;
                final long nb2;
                this.extra[4][k] = ((b3 & (((nb2 = ~b2) & (b4 = otherInits.extra[5][k])) | nb4)) | (b1 & nb2 & ~b4));
                this.extra[5][k] = (((nb3 | nb2) & nb4 & b4) | (b1 & nb3 & nb2));
                final long[] array6 = this.extra[6];
                final int n6 = k;
                array6[n6] |= otherInits.extra[6][k];
                final long[] array7 = this.extra[7];
                final int n7 = k;
                array7[n7] |= otherInits.extra[7][k];
                thisHasNulls = (thisHasNulls || this.extra[3][k] != 0L || this.extra[4][k] != 0L || this.extra[5][k] != 0L);
                ++k;
            }
            while (k < resetLimit) {
                final long a1 = this.extra[2][k];
                this.extra[2][k] = 0L;
                final long[] array8 = this.extra[3];
                final int n8 = k;
                final long a2 = this.extra[3][k];
                final long a3;
                final long na3;
                final long na4;
                array8[n8] = (a2 & (na3 = (~(a3 = this.extra[4][k]) | (na4 = ~a1))));
                final long a4;
                final long na2;
                this.extra[4][k] = ((a3 & (((na2 = ~a2) & (a4 = this.extra[5][k])) | na4)) | (a1 & na2 & ~a4));
                this.extra[5][k] = (((na3 | na2) & na4 & a4) | (a1 & na3 & na2));
                thisHasNulls = (thisHasNulls || this.extra[3][k] != 0L || this.extra[4][k] != 0L || this.extra[5][k] != 0L);
                ++k;
            }
        }
        if (thisHasNulls) {
            this.tagBits |= 0x4;
        }
        else {
            this.tagBits &= 0xFFFFFFFB;
        }
        return this;
    }
    
    static int numberOfEnclosingFields(ReferenceBinding type) {
        int count = 0;
        for (type = type.enclosingType(); type != null; type = type.enclosingType()) {
            count += type.fieldCount();
        }
        return count;
    }
    
    @Override
    public UnconditionalFlowInfo nullInfoLessUnconditionalCopy() {
        if (this == UnconditionalFlowInfo.DEAD_END) {
            return this;
        }
        final UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
        copy.definiteInits = this.definiteInits;
        copy.potentialInits = this.potentialInits;
        copy.iNBit = -1L;
        copy.iNNBit = -1L;
        copy.tagBits = (this.tagBits & 0xFFFFFFFB);
        final UnconditionalFlowInfo unconditionalFlowInfo = copy;
        unconditionalFlowInfo.tagBits |= 0x40;
        copy.maxFieldCount = this.maxFieldCount;
        if (this.extra != null) {
            copy.extra = new long[8][];
            final int length;
            System.arraycopy(this.extra[0], 0, copy.extra[0] = new long[length = this.extra[0].length], 0, length);
            System.arraycopy(this.extra[1], 0, copy.extra[1] = new long[length], 0, length);
            for (int j = 2; j < 8; ++j) {
                copy.extra[j] = new long[length];
            }
            Arrays.fill(copy.extra[6], -1L);
            Arrays.fill(copy.extra[7], -1L);
        }
        return copy;
    }
    
    @Override
    public FlowInfo safeInitsWhenTrue() {
        return this.copy();
    }
    
    @Override
    public FlowInfo setReachMode(final int reachMode) {
        if (this == UnconditionalFlowInfo.DEAD_END) {
            return this;
        }
        if (reachMode == 0) {
            this.tagBits &= 0xFFFFFFFC;
        }
        else if (reachMode == 2) {
            this.tagBits |= 0x2;
        }
        else {
            if ((this.tagBits & 0x3) == 0x0) {
                this.potentialInits = 0L;
                if (this.extra != null) {
                    for (int i = 0, length = this.extra[0].length; i < length; ++i) {
                        this.extra[1][i] = 0L;
                    }
                }
            }
            this.tagBits |= reachMode;
        }
        return this;
    }
    
    @Override
    public String toString() {
        if (this == UnconditionalFlowInfo.DEAD_END) {
            return "FlowInfo.DEAD_END";
        }
        if ((this.tagBits & 0x4) != 0x0) {
            if (this.extra == null) {
                return "FlowInfo<def: " + this.definiteInits + ", pot: " + this.potentialInits + ", reachable:" + ((this.tagBits & 0x3) == 0x0) + ", null: " + this.nullBit1 + this.nullBit2 + this.nullBit3 + this.nullBit4 + ", incoming: " + this.iNBit + this.iNNBit + ">";
            }
            String def = "FlowInfo<def:[" + this.definiteInits;
            String pot = "], pot:[" + this.potentialInits;
            String nullS = ", null:[" + this.nullBit1 + this.nullBit2 + this.nullBit3 + this.nullBit4;
            int i;
            int ceil;
            for (i = 0, ceil = ((this.extra[0].length > 3) ? 3 : this.extra[0].length); i < ceil; ++i) {
                def = String.valueOf(def) + "," + this.extra[0][i];
                pot = String.valueOf(pot) + "," + this.extra[1][i];
                nullS = String.valueOf(nullS) + "," + this.extra[2][i] + this.extra[3][i] + this.extra[4][i] + this.extra[5][i] + ", incoming: " + this.extra[6][i] + this.extra[7];
            }
            if (ceil < this.extra[0].length) {
                def = String.valueOf(def) + ",...";
                pot = String.valueOf(pot) + ",...";
                nullS = String.valueOf(nullS) + ",...";
            }
            return String.valueOf(def) + pot + "], reachable:" + ((this.tagBits & 0x3) == 0x0) + nullS + "]>";
        }
        else {
            if (this.extra == null) {
                return "FlowInfo<def: " + this.definiteInits + ", pot: " + this.potentialInits + ", reachable:" + ((this.tagBits & 0x3) == 0x0) + ", no null info>";
            }
            String def = "FlowInfo<def:[" + this.definiteInits;
            String pot = "], pot:[" + this.potentialInits;
            int j;
            int ceil2;
            for (j = 0, ceil2 = ((this.extra[0].length > 3) ? 3 : this.extra[0].length); j < ceil2; ++j) {
                def = String.valueOf(def) + "," + this.extra[0][j];
                pot = String.valueOf(pot) + "," + this.extra[1][j];
            }
            if (ceil2 < this.extra[0].length) {
                def = String.valueOf(def) + ",...";
                pot = String.valueOf(pot) + ",...";
            }
            return String.valueOf(def) + pot + "], reachable:" + ((this.tagBits & 0x3) == 0x0) + ", no null info>";
        }
    }
    
    @Override
    public UnconditionalFlowInfo unconditionalCopy() {
        return (UnconditionalFlowInfo)this.copy();
    }
    
    @Override
    public UnconditionalFlowInfo unconditionalFieldLessCopy() {
        final UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
        copy.tagBits = this.tagBits;
        copy.maxFieldCount = this.maxFieldCount;
        final int limit = this.maxFieldCount;
        if (limit < 64) {
            final long mask;
            copy.definiteInits = (this.definiteInits & (mask = ~((1L << limit) - 1L)));
            copy.potentialInits = (this.potentialInits & mask);
            copy.nullBit1 = (this.nullBit1 & mask);
            copy.nullBit2 = (this.nullBit2 & mask);
            copy.nullBit3 = (this.nullBit3 & mask);
            copy.nullBit4 = (this.nullBit4 & mask);
            copy.iNBit = (this.iNBit & mask);
            copy.iNNBit = (this.iNNBit & mask);
        }
        if (this.extra == null) {
            return copy;
        }
        final int vectorIndex;
        final int length;
        if ((vectorIndex = limit / 64 - 1) >= (length = this.extra[0].length)) {
            return copy;
        }
        copy.extra = new long[8][];
        final int copyStart;
        if ((copyStart = vectorIndex + 1) < length) {
            final int copyLength = length - copyStart;
            for (int j = 0; j < 8; ++j) {
                System.arraycopy(this.extra[j], copyStart, copy.extra[j] = new long[length], copyStart, copyLength);
            }
        }
        else if (vectorIndex >= 0) {
            copy.createExtraSpace(length);
        }
        if (vectorIndex >= 0) {
            final long mask2 = ~((1L << limit % 64) - 1L);
            for (int i = 0; i < 8; ++i) {
                copy.extra[i][vectorIndex] = (this.extra[i][vectorIndex] & mask2);
            }
        }
        return copy;
    }
    
    @Override
    public UnconditionalFlowInfo unconditionalInits() {
        return this;
    }
    
    @Override
    public UnconditionalFlowInfo unconditionalInitsWithoutSideEffect() {
        return this;
    }
    
    @Override
    public UnconditionalFlowInfo mergeDefiniteInitsWith(final UnconditionalFlowInfo otherInits) {
        if ((otherInits.tagBits & 0x1) != 0x0 && this != UnconditionalFlowInfo.DEAD_END) {
            return this;
        }
        if ((this.tagBits & 0x1) != 0x0) {
            return (UnconditionalFlowInfo)otherInits.copy();
        }
        this.definiteInits &= otherInits.definiteInits;
        if (this.extra != null) {
            if (otherInits.extra != null) {
                int i = 0;
                final int length;
                final int otherLength;
                if ((length = this.extra[0].length) < (otherLength = otherInits.extra[0].length)) {
                    for (int j = 0; j < 8; ++j) {
                        System.arraycopy(this.extra[j], 0, this.extra[j] = new long[otherLength], 0, length);
                    }
                    while (i < length) {
                        final long[] array = this.extra[0];
                        final int n = i;
                        array[n] &= otherInits.extra[0][i];
                        ++i;
                    }
                    while (i < otherLength) {
                        this.extra[0][i] = otherInits.extra[0][i];
                        ++i;
                    }
                }
                else {
                    while (i < otherLength) {
                        final long[] array2 = this.extra[0];
                        final int n2 = i;
                        array2[n2] &= otherInits.extra[0][i];
                        ++i;
                    }
                }
            }
            else {
                for (int i = 0; i < this.extra[0].length; ++i) {
                    this.extra[0][i] = 0L;
                }
            }
        }
        else if (otherInits.extra != null) {
            final int otherLength2 = otherInits.extra[0].length;
            this.createExtraSpace(otherLength2);
            System.arraycopy(otherInits.extra[0], 0, this.extra[0], 0, otherLength2);
        }
        return this;
    }
    
    @Override
    public void resetAssignmentInfo(final LocalVariableBinding local) {
        this.resetAssignmentInfo(local.id + this.maxFieldCount);
    }
    
    public void resetAssignmentInfo(final int position) {
        if (this != UnconditionalFlowInfo.DEAD_END) {
            if (position < 64) {
                final long mask;
                this.definiteInits &= (mask = ~(1L << position));
                this.potentialInits &= mask;
            }
            else {
                final int vectorIndex = position / 64 - 1;
                if (this.extra == null || vectorIndex >= this.extra[0].length) {
                    return;
                }
                final long[] array = this.extra[0];
                final int n = vectorIndex;
                final long mask2;
                array[n] &= (mask2 = ~(1L << position % 64));
                final long[] array2 = this.extra[1];
                final int n2 = vectorIndex;
                array2[n2] &= mask2;
            }
        }
    }
    
    private void createExtraSpace(final int length) {
        this.extra = new long[8][];
        for (int j = 0; j < 8; ++j) {
            this.extra[j] = new long[length];
        }
        if ((this.tagBits & 0x40) != 0x0) {
            Arrays.fill(this.extra[6], -1L);
            Arrays.fill(this.extra[7], -1L);
        }
    }
    
    public static class AssertionFailedException extends RuntimeException
    {
        private static final long serialVersionUID = 1827352841030089703L;
        
        public AssertionFailedException(final String message) {
            super(message);
        }
    }
}
