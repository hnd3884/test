package org.eclipse.jdt.internal.compiler.impl;

public class IrritantSet
{
    public static final int GROUP_MASK = -536870912;
    public static final int GROUP_SHIFT = 29;
    public static final int GROUP_MAX = 3;
    public static final int GROUP0 = 0;
    public static final int GROUP1 = 536870912;
    public static final int GROUP2 = 1073741824;
    public static final IrritantSet ALL;
    public static final IrritantSet BOXING;
    public static final IrritantSet CAST;
    public static final IrritantSet DEPRECATION;
    public static final IrritantSet DEP_ANN;
    public static final IrritantSet FALLTHROUGH;
    public static final IrritantSet FINALLY;
    public static final IrritantSet HIDING;
    public static final IrritantSet INCOMPLETE_SWITCH;
    public static final IrritantSet NLS;
    public static final IrritantSet NULL;
    public static final IrritantSet RAW;
    public static final IrritantSet RESTRICTION;
    public static final IrritantSet SERIAL;
    public static final IrritantSet STATIC_ACCESS;
    public static final IrritantSet STATIC_METHOD;
    public static final IrritantSet SYNTHETIC_ACCESS;
    public static final IrritantSet SYNCHRONIZED;
    public static final IrritantSet SUPER;
    public static final IrritantSet UNUSED;
    public static final IrritantSet UNCHECKED;
    public static final IrritantSet UNQUALIFIED_FIELD_ACCESS;
    public static final IrritantSet RESOURCE;
    public static final IrritantSet JAVADOC;
    public static final IrritantSet COMPILER_DEFAULT_ERRORS;
    public static final IrritantSet COMPILER_DEFAULT_WARNINGS;
    public static final IrritantSet COMPILER_DEFAULT_INFOS;
    private int[] bits;
    
    static {
        ALL = new IrritantSet(536870911);
        BOXING = new IrritantSet(536871168);
        CAST = new IrritantSet(67108864);
        DEPRECATION = new IrritantSet(4);
        DEP_ANN = new IrritantSet(536879104);
        FALLTHROUGH = new IrritantSet(537395200);
        FINALLY = new IrritantSet(16777216);
        HIDING = new IrritantSet(8);
        INCOMPLETE_SWITCH = new IrritantSet(536875008);
        NLS = new IrritantSet(256);
        NULL = new IrritantSet(536871040);
        RAW = new IrritantSet(536936448);
        RESTRICTION = new IrritantSet(536870944);
        SERIAL = new IrritantSet(536870920);
        STATIC_ACCESS = new IrritantSet(268435456);
        STATIC_METHOD = new IrritantSet(1073741840);
        SYNTHETIC_ACCESS = new IrritantSet(128);
        SYNCHRONIZED = new IrritantSet(805306368);
        SUPER = new IrritantSet(537919488);
        UNUSED = new IrritantSet(16);
        UNCHECKED = new IrritantSet(536870914);
        UNQUALIFIED_FIELD_ACCESS = new IrritantSet(4194304);
        RESOURCE = new IrritantSet(1073741952);
        JAVADOC = new IrritantSet(33554432);
        COMPILER_DEFAULT_ERRORS = new IrritantSet(0);
        COMPILER_DEFAULT_WARNINGS = new IrritantSet(0);
        COMPILER_DEFAULT_INFOS = new IrritantSet(0);
        IrritantSet.COMPILER_DEFAULT_WARNINGS.set(16838239).set(721671934).set(1075458182);
        IrritantSet.COMPILER_DEFAULT_ERRORS.set(1073744896);
        IrritantSet.ALL.setAll();
        IrritantSet.HIDING.set(131072).set(65536).set(536871936);
        IrritantSet.NULL.set(538968064).set(541065216).set(1073742848).set(1073743872).set(1073745920).set(1073750016).set(1073872896).set(1073758208).set(1074266112).set(1074790400);
        IrritantSet.RESTRICTION.set(536887296);
        IrritantSet.STATIC_ACCESS.set(2048);
        IrritantSet.UNUSED.set(32).set(1074003968).set(32768).set(8388608).set(537001984).set(1024).set(553648128).set(603979776).set(1073741826).set(1073741832).set(1073807360).set(1073741888);
        IrritantSet.STATIC_METHOD.set(1073741856);
        IrritantSet.RESOURCE.set(1073742080).set(1073742336);
        IrritantSet.INCOMPLETE_SWITCH.set(1073774592);
        final String suppressRawWhenUnchecked = System.getProperty("suppressRawWhenUnchecked");
        if (suppressRawWhenUnchecked != null && "true".equalsIgnoreCase(suppressRawWhenUnchecked)) {
            IrritantSet.UNCHECKED.set(536936448);
        }
        IrritantSet.JAVADOC.set(1048576).set(2097152);
    }
    
    public IrritantSet(final int singleGroupIrritants) {
        this.bits = new int[3];
        this.initialize(singleGroupIrritants);
    }
    
    public IrritantSet(final IrritantSet other) {
        this.bits = new int[3];
        this.initialize(other);
    }
    
    public boolean areAllSet() {
        for (int i = 0; i < 3; ++i) {
            if (this.bits[i] != 536870911) {
                return false;
            }
        }
        return true;
    }
    
    public IrritantSet clear(final int singleGroupIrritants) {
        final int group = (singleGroupIrritants & 0xE0000000) >> 29;
        final int[] bits = this.bits;
        final int n = group;
        bits[n] &= ~singleGroupIrritants;
        return this;
    }
    
    public IrritantSet clearAll() {
        for (int i = 0; i < 3; ++i) {
            this.bits[i] = 0;
        }
        return this;
    }
    
    public void initialize(final int singleGroupIrritants) {
        if (singleGroupIrritants == 0) {
            return;
        }
        final int group = (singleGroupIrritants & 0xE0000000) >> 29;
        this.bits[group] = (singleGroupIrritants & 0x1FFFFFFF);
    }
    
    public void initialize(final IrritantSet other) {
        if (other == null) {
            return;
        }
        System.arraycopy(other.bits, 0, this.bits = new int[3], 0, 3);
    }
    
    public boolean isAnySet(final IrritantSet other) {
        if (other == null) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            if ((this.bits[i] & other.bits[i]) != 0x0) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasSameIrritants(final IrritantSet irritantSet) {
        if (irritantSet == null) {
            return false;
        }
        for (int i = 0; i < 3; ++i) {
            if (this.bits[i] != irritantSet.bits[i]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isSet(final int singleGroupIrritants) {
        final int group = (singleGroupIrritants & 0xE0000000) >> 29;
        return (this.bits[group] & singleGroupIrritants) != 0x0;
    }
    
    public IrritantSet set(final int singleGroupIrritants) {
        final int group = (singleGroupIrritants & 0xE0000000) >> 29;
        final int[] bits = this.bits;
        final int n = group;
        bits[n] |= (singleGroupIrritants & 0x1FFFFFFF);
        return this;
    }
    
    public IrritantSet set(final IrritantSet other) {
        if (other == null) {
            return this;
        }
        boolean wasNoOp = true;
        for (int i = 0; i < 3; ++i) {
            final int otherIrritant = other.bits[i] & 0x1FFFFFFF;
            if ((this.bits[i] & otherIrritant) != otherIrritant) {
                wasNoOp = false;
                final int[] bits = this.bits;
                final int n = i;
                bits[n] |= otherIrritant;
            }
        }
        return wasNoOp ? null : this;
    }
    
    public IrritantSet setAll() {
        for (int i = 0; i < 3; ++i) {
            final int[] bits = this.bits;
            final int n = i;
            bits[n] |= 0x1FFFFFFF;
        }
        return this;
    }
}
