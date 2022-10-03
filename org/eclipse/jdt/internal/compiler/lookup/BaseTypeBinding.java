package org.eclipse.jdt.internal.compiler.lookup;

public class BaseTypeBinding extends TypeBinding
{
    public static final int[] CONVERSIONS;
    public static final int IDENTITY = 1;
    public static final int WIDENING = 2;
    public static final int NARROWING = 4;
    public static final int MAX_CONVERSIONS = 256;
    public char[] simpleName;
    private char[] constantPoolName;
    
    static {
        CONVERSIONS = initializeConversions();
    }
    
    public static final int[] initializeConversions() {
        final int[] table = new int[256];
        table[51] = (table[85] = 1);
        table[67] = 2;
        table[35] = 4;
        table[115] = (table[163] = 2);
        table[131] = (table[147] = 2);
        table[52] = 4;
        table[68] = 1;
        table[36] = 4;
        table[116] = (table[164] = 2);
        table[132] = (table[148] = 2);
        table[66] = (table[50] = 4);
        table[34] = 1;
        table[114] = (table[162] = 2);
        table[130] = (table[146] = 2);
        table[58] = 4;
        table[42] = (table[74] = 4);
        table[170] = 1;
        table[122] = 2;
        table[138] = (table[154] = 2);
        table[71] = (table[55] = 4);
        table[167] = (table[39] = 4);
        table[119] = 1;
        table[135] = (table[151] = 2);
        table[57] = 4;
        table[41] = (table[73] = 4);
        table[121] = (table[169] = 4);
        table[153] = 1;
        table[137] = 2;
        table[72] = (table[56] = 4);
        table[168] = (table[40] = 4);
        table[152] = (table[120] = 4);
        table[136] = 1;
        return table;
    }
    
    public static final boolean isNarrowing(final int left, final int right) {
        final int right2left = right + (left << 4);
        return right2left >= 0 && right2left < 256 && (BaseTypeBinding.CONVERSIONS[right2left] & 0x5) != 0x0;
    }
    
    public static final boolean isWidening(final int left, final int right) {
        final int right2left = right + (left << 4);
        return right2left >= 0 && right2left < 256 && (BaseTypeBinding.CONVERSIONS[right2left] & 0x3) != 0x0;
    }
    
    BaseTypeBinding(final int id, final char[] name, final char[] constantPoolName) {
        this.tagBits |= 0x2L;
        this.id = id;
        this.simpleName = name;
        this.constantPoolName = constantPoolName;
    }
    
    @Override
    public char[] computeUniqueKey(final boolean isLeaf) {
        return this.constantPoolName();
    }
    
    @Override
    public char[] constantPoolName() {
        return this.constantPoolName;
    }
    
    @Override
    public TypeBinding clone(final TypeBinding enclosingType) {
        return new BaseTypeBinding(this.id, this.simpleName, this.constantPoolName);
    }
    
    @Override
    public PackageBinding getPackage() {
        return null;
    }
    
    @Override
    public final boolean isCompatibleWith(final TypeBinding right, final Scope captureScope) {
        if (TypeBinding.equalsEquals(this, right)) {
            return true;
        }
        final int right2left = this.id + (right.id << 4);
        return (right2left >= 0 && right2left < 256 && (BaseTypeBinding.CONVERSIONS[right2left] & 0x3) != 0x0) || (this == TypeBinding.NULL && !right.isBaseType());
    }
    
    @Override
    public void setTypeAnnotations(final AnnotationBinding[] annotations, final boolean evalNullAnnotations) {
        super.setTypeAnnotations(annotations, false);
    }
    
    @Override
    public TypeBinding unannotated() {
        if (!this.hasTypeAnnotations()) {
            return this;
        }
        switch (this.id) {
            case 5: {
                return TypeBinding.BOOLEAN;
            }
            case 3: {
                return TypeBinding.BYTE;
            }
            case 2: {
                return TypeBinding.CHAR;
            }
            case 8: {
                return TypeBinding.DOUBLE;
            }
            case 9: {
                return TypeBinding.FLOAT;
            }
            case 10: {
                return TypeBinding.INT;
            }
            case 7: {
                return TypeBinding.LONG;
            }
            case 4: {
                return TypeBinding.SHORT;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public boolean isUncheckedException(final boolean includeSupertype) {
        return this == TypeBinding.NULL;
    }
    
    @Override
    public int kind() {
        return 132;
    }
    
    @Override
    public char[] qualifiedSourceName() {
        return this.simpleName;
    }
    
    @Override
    public char[] readableName() {
        return this.simpleName;
    }
    
    @Override
    public char[] shortReadableName() {
        return this.simpleName;
    }
    
    @Override
    public char[] sourceName() {
        return this.simpleName;
    }
    
    @Override
    public String toString() {
        return this.hasTypeAnnotations() ? this.annotatedDebugName() : new String(this.readableName());
    }
}
