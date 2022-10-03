package org.eclipse.jdt.internal.compiler.impl;

import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;

public abstract class Constant implements TypeIds, OperatorIds
{
    public static final Constant NotAConstant;
    
    static {
        NotAConstant = DoubleConstant.fromValue(Double.NaN);
    }
    
    public boolean booleanValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { this.typeName(), "boolean" }));
    }
    
    public byte byteValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { this.typeName(), "byte" }));
    }
    
    public final Constant castTo(final int conversionToTargetType) {
        if (this == Constant.NotAConstant) {
            return Constant.NotAConstant;
        }
        switch (conversionToTargetType) {
            case 0: {
                return this;
            }
            case 51: {
                return this;
            }
            case 55: {
                return ByteConstant.fromValue((byte)this.longValue());
            }
            case 52: {
                return ByteConstant.fromValue((byte)this.shortValue());
            }
            case 56: {
                return ByteConstant.fromValue((byte)this.doubleValue());
            }
            case 57: {
                return ByteConstant.fromValue((byte)this.floatValue());
            }
            case 50: {
                return ByteConstant.fromValue((byte)this.charValue());
            }
            case 58: {
                return ByteConstant.fromValue((byte)this.intValue());
            }
            case 115: {
                return LongConstant.fromValue(this.byteValue());
            }
            case 119: {
                return this;
            }
            case 116: {
                return LongConstant.fromValue(this.shortValue());
            }
            case 120: {
                return LongConstant.fromValue((long)this.doubleValue());
            }
            case 121: {
                return LongConstant.fromValue((long)this.floatValue());
            }
            case 114: {
                return LongConstant.fromValue(this.charValue());
            }
            case 122: {
                return LongConstant.fromValue(this.intValue());
            }
            case 67: {
                return ShortConstant.fromValue(this.byteValue());
            }
            case 71: {
                return ShortConstant.fromValue((short)this.longValue());
            }
            case 68: {
                return this;
            }
            case 72: {
                return ShortConstant.fromValue((short)this.doubleValue());
            }
            case 73: {
                return ShortConstant.fromValue((short)this.floatValue());
            }
            case 66: {
                return ShortConstant.fromValue((short)this.charValue());
            }
            case 74: {
                return ShortConstant.fromValue((short)this.intValue());
            }
            case 187: {
                return this;
            }
            case 131: {
                return DoubleConstant.fromValue(this.byteValue());
            }
            case 135: {
                return DoubleConstant.fromValue((double)this.longValue());
            }
            case 132: {
                return DoubleConstant.fromValue(this.shortValue());
            }
            case 136: {
                return this;
            }
            case 137: {
                return DoubleConstant.fromValue(this.floatValue());
            }
            case 130: {
                return DoubleConstant.fromValue(this.charValue());
            }
            case 138: {
                return DoubleConstant.fromValue(this.intValue());
            }
            case 147: {
                return FloatConstant.fromValue(this.byteValue());
            }
            case 151: {
                return FloatConstant.fromValue((float)this.longValue());
            }
            case 148: {
                return FloatConstant.fromValue(this.shortValue());
            }
            case 152: {
                return FloatConstant.fromValue((float)this.doubleValue());
            }
            case 153: {
                return this;
            }
            case 146: {
                return FloatConstant.fromValue(this.charValue());
            }
            case 154: {
                return FloatConstant.fromValue((float)this.intValue());
            }
            case 85: {
                return this;
            }
            case 35: {
                return CharConstant.fromValue((char)this.byteValue());
            }
            case 39: {
                return CharConstant.fromValue((char)this.longValue());
            }
            case 36: {
                return CharConstant.fromValue((char)this.shortValue());
            }
            case 40: {
                return CharConstant.fromValue((char)this.doubleValue());
            }
            case 41: {
                return CharConstant.fromValue((char)this.floatValue());
            }
            case 34: {
                return this;
            }
            case 42: {
                return CharConstant.fromValue((char)this.intValue());
            }
            case 163: {
                return IntConstant.fromValue(this.byteValue());
            }
            case 167: {
                return IntConstant.fromValue((int)this.longValue());
            }
            case 164: {
                return IntConstant.fromValue(this.shortValue());
            }
            case 168: {
                return IntConstant.fromValue((int)this.doubleValue());
            }
            case 169: {
                return IntConstant.fromValue((int)this.floatValue());
            }
            case 162: {
                return IntConstant.fromValue(this.charValue());
            }
            case 170: {
                return this;
            }
            default: {
                return Constant.NotAConstant;
            }
        }
    }
    
    public char charValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { this.typeName(), "char" }));
    }
    
    public static final Constant computeConstantOperation(final Constant cst, final int id, final int operator) {
        switch (operator) {
            case 11: {
                return BooleanConstant.fromValue(!cst.booleanValue());
            }
            case 14: {
                return computeConstantOperationPLUS(IntConstant.fromValue(0), 10, cst, id);
            }
            case 13: {
                switch (id) {
                    case 9: {
                        final float f;
                        if ((f = cst.floatValue()) != 0.0f) {
                            break;
                        }
                        if (Float.floatToIntBits(f) == 0) {
                            return FloatConstant.fromValue(-0.0f);
                        }
                        return FloatConstant.fromValue(0.0f);
                    }
                    case 8: {
                        final double d;
                        if ((d = cst.doubleValue()) != 0.0) {
                            break;
                        }
                        if (Double.doubleToLongBits(d) == 0L) {
                            return DoubleConstant.fromValue(-0.0);
                        }
                        return DoubleConstant.fromValue(0.0);
                    }
                }
                return computeConstantOperationMINUS(IntConstant.fromValue(0), 10, cst, id);
            }
            case 12: {
                switch (id) {
                    case 2: {
                        return IntConstant.fromValue(~cst.charValue());
                    }
                    case 3: {
                        return IntConstant.fromValue(~cst.byteValue());
                    }
                    case 4: {
                        return IntConstant.fromValue(~cst.shortValue());
                    }
                    case 10: {
                        return IntConstant.fromValue(~cst.intValue());
                    }
                    case 7: {
                        return LongConstant.fromValue(~cst.longValue());
                    }
                    default: {
                        return Constant.NotAConstant;
                    }
                }
                break;
            }
            default: {
                return Constant.NotAConstant;
            }
        }
    }
    
    public static final Constant computeConstantOperation(final Constant left, final int leftId, final int operator, final Constant right, final int rightId) {
        switch (operator) {
            case 2: {
                return computeConstantOperationAND(left, leftId, right, rightId);
            }
            case 0: {
                return computeConstantOperationAND_AND(left, leftId, right, rightId);
            }
            case 9: {
                return computeConstantOperationDIVIDE(left, leftId, right, rightId);
            }
            case 6: {
                return computeConstantOperationGREATER(left, leftId, right, rightId);
            }
            case 7: {
                return computeConstantOperationGREATER_EQUAL(left, leftId, right, rightId);
            }
            case 10: {
                return computeConstantOperationLEFT_SHIFT(left, leftId, right, rightId);
            }
            case 4: {
                return computeConstantOperationLESS(left, leftId, right, rightId);
            }
            case 5: {
                return computeConstantOperationLESS_EQUAL(left, leftId, right, rightId);
            }
            case 13: {
                return computeConstantOperationMINUS(left, leftId, right, rightId);
            }
            case 15: {
                return computeConstantOperationMULTIPLY(left, leftId, right, rightId);
            }
            case 3: {
                return computeConstantOperationOR(left, leftId, right, rightId);
            }
            case 1: {
                return computeConstantOperationOR_OR(left, leftId, right, rightId);
            }
            case 14: {
                return computeConstantOperationPLUS(left, leftId, right, rightId);
            }
            case 16: {
                return computeConstantOperationREMAINDER(left, leftId, right, rightId);
            }
            case 17: {
                return computeConstantOperationRIGHT_SHIFT(left, leftId, right, rightId);
            }
            case 19: {
                return computeConstantOperationUNSIGNED_RIGHT_SHIFT(left, leftId, right, rightId);
            }
            case 8: {
                return computeConstantOperationXOR(left, leftId, right, rightId);
            }
            default: {
                return Constant.NotAConstant;
            }
        }
    }
    
    public static final Constant computeConstantOperationAND(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_0665: {
            switch (leftId) {
                case 5: {
                    return BooleanConstant.fromValue(left.booleanValue() & right.booleanValue());
                }
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() & right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() & right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() & right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() & right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.charValue() & right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() & right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() & right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() & right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() & right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.byteValue() & right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() & right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() & right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() & right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() & right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.shortValue() & right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() & right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() & right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() & right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() & right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.intValue() & right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() & (long)right.charValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() & (long)right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() & (long)right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() & (long)right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() & right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationAND_AND(final Constant left, final int leftId, final Constant right, final int rightId) {
        return BooleanConstant.fromValue(left.booleanValue() && right.booleanValue());
    }
    
    public static final Constant computeConstantOperationDIVIDE(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_1097: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() / right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.charValue() / right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.charValue() / right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() / right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() / right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() / right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.charValue() / right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (rightId) {
                        case 2: {
                            return FloatConstant.fromValue(left.floatValue() / right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.floatValue() / right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.floatValue() / right.doubleValue());
                        }
                        case 3: {
                            return FloatConstant.fromValue(left.floatValue() / right.byteValue());
                        }
                        case 4: {
                            return FloatConstant.fromValue(left.floatValue() / right.shortValue());
                        }
                        case 10: {
                            return FloatConstant.fromValue(left.floatValue() / right.intValue());
                        }
                        case 7: {
                            return FloatConstant.fromValue(left.floatValue() / right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (rightId) {
                        case 2: {
                            return DoubleConstant.fromValue(left.doubleValue() / right.charValue());
                        }
                        case 9: {
                            return DoubleConstant.fromValue(left.doubleValue() / right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.doubleValue() / right.doubleValue());
                        }
                        case 3: {
                            return DoubleConstant.fromValue(left.doubleValue() / right.byteValue());
                        }
                        case 4: {
                            return DoubleConstant.fromValue(left.doubleValue() / right.shortValue());
                        }
                        case 10: {
                            return DoubleConstant.fromValue(left.doubleValue() / right.intValue());
                        }
                        case 7: {
                            return DoubleConstant.fromValue(left.doubleValue() / right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() / right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.byteValue() / right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.byteValue() / right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() / right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() / right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() / right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.byteValue() / right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() / right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.shortValue() / right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.shortValue() / right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() / right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() / right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() / right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.shortValue() / right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() / right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.intValue() / right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.intValue() / right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() / right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() / right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() / right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.intValue() / right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() / right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.longValue() / right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.longValue() / right.doubleValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() / right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() / right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() / right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() / right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationEQUAL_EQUAL(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_1548: {
            switch (leftId) {
                case 5: {
                    if (rightId == 5) {
                        return BooleanConstant.fromValue(left.booleanValue() == right.booleanValue());
                    }
                    break;
                }
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.charValue() == right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.charValue() == right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.charValue() == right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.charValue() == right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.charValue() == right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.charValue() == right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.charValue() == right.longValue());
                        }
                        default: {
                            break Label_1548;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.floatValue() == right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.floatValue() == right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.floatValue() == right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.floatValue() == right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.floatValue() == right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.floatValue() == right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.floatValue() == right.longValue());
                        }
                        default: {
                            break Label_1548;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.doubleValue() == right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.doubleValue() == right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.doubleValue() == right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.doubleValue() == right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.doubleValue() == right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.doubleValue() == right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.doubleValue() == right.longValue());
                        }
                        default: {
                            break Label_1548;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.byteValue() == right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.byteValue() == right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.byteValue() == right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.byteValue() == right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.byteValue() == right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.byteValue() == right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.byteValue() == right.longValue());
                        }
                        default: {
                            break Label_1548;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.shortValue() == right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.shortValue() == right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.shortValue() == right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.shortValue() == right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.shortValue() == right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.shortValue() == right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.shortValue() == right.longValue());
                        }
                        default: {
                            break Label_1548;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.intValue() == right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.intValue() == right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.intValue() == right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.intValue() == right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.intValue() == right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.intValue() == right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.intValue() == right.longValue());
                        }
                        default: {
                            break Label_1548;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.longValue() == right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.longValue() == right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.longValue() == right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.longValue() == right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.longValue() == right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.longValue() == right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.longValue() == right.longValue());
                        }
                        default: {
                            break Label_1548;
                        }
                    }
                    break;
                }
                case 11: {
                    if (rightId == 11) {
                        return BooleanConstant.fromValue(left.hasSameValue(right));
                    }
                    break;
                }
                case 12: {
                    if (rightId == 11) {
                        return BooleanConstant.fromValue(false);
                    }
                    if (rightId == 12) {
                        return BooleanConstant.fromValue(true);
                    }
                    break;
                }
            }
        }
        return BooleanConstant.fromValue(false);
    }
    
    public static final Constant computeConstantOperationGREATER(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_1473: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.charValue() > right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.charValue() > right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.charValue() > right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.charValue() > right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.charValue() > right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.charValue() > right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.charValue() > right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.floatValue() > right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.floatValue() > right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.floatValue() > right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.floatValue() > right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.floatValue() > right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.floatValue() > right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.floatValue() > right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.doubleValue() > right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.doubleValue() > right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.doubleValue() > right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.doubleValue() > right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.doubleValue() > right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.doubleValue() > right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.doubleValue() > right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.byteValue() > right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.byteValue() > right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.byteValue() > right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.byteValue() > right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.byteValue() > right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.byteValue() > right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.byteValue() > right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.shortValue() > right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.shortValue() > right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.shortValue() > right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.shortValue() > right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.shortValue() > right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.shortValue() > right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.shortValue() > right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.intValue() > right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.intValue() > right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.intValue() > right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.intValue() > right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.intValue() > right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.intValue() > right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.intValue() > right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.longValue() > right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.longValue() > right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.longValue() > right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.longValue() > right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.longValue() > right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.longValue() > right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.longValue() > right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationGREATER_EQUAL(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_1473: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.charValue() >= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.charValue() >= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.charValue() >= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.charValue() >= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.charValue() >= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.charValue() >= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.charValue() >= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.floatValue() >= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.floatValue() >= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.floatValue() >= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.floatValue() >= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.floatValue() >= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.floatValue() >= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.floatValue() >= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.doubleValue() >= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.doubleValue() >= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.doubleValue() >= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.doubleValue() >= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.doubleValue() >= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.doubleValue() >= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.doubleValue() >= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.byteValue() >= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.byteValue() >= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.byteValue() >= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.byteValue() >= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.byteValue() >= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.byteValue() >= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.byteValue() >= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.shortValue() >= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.shortValue() >= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.shortValue() >= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.shortValue() >= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.shortValue() >= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.shortValue() >= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.shortValue() >= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.intValue() >= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.intValue() >= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.intValue() >= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.intValue() >= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.intValue() >= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.intValue() >= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.intValue() >= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.longValue() >= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.longValue() >= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.longValue() >= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.longValue() >= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.longValue() >= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.longValue() >= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.longValue() >= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationLEFT_SHIFT(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_0650: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() << right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() << right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() << right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() << right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.charValue() << (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() << right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() << right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() << right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() << right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.byteValue() << (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() << right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() << right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() << right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() << right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.shortValue() << (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() << right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() << right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() << right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() << right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.intValue() << (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() << right.charValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() << right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() << right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() << right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() << (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationLESS(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_1473: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.charValue() < right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.charValue() < right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.charValue() < right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.charValue() < right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.charValue() < right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.charValue() < right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.charValue() < right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.floatValue() < right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.floatValue() < right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.floatValue() < right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.floatValue() < right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.floatValue() < right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.floatValue() < right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.floatValue() < right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.doubleValue() < right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.doubleValue() < right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.doubleValue() < right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.doubleValue() < right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.doubleValue() < right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.doubleValue() < right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.doubleValue() < right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.byteValue() < right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.byteValue() < right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.byteValue() < right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.byteValue() < right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.byteValue() < right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.byteValue() < right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.byteValue() < right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.shortValue() < right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.shortValue() < right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.shortValue() < right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.shortValue() < right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.shortValue() < right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.shortValue() < right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.shortValue() < right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.intValue() < right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.intValue() < right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.intValue() < right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.intValue() < right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.intValue() < right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.intValue() < right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.intValue() < right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.longValue() < right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.longValue() < right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.longValue() < right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.longValue() < right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.longValue() < right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.longValue() < right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.longValue() < right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationLESS_EQUAL(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_1473: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.charValue() <= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.charValue() <= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.charValue() <= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.charValue() <= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.charValue() <= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.charValue() <= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.charValue() <= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.floatValue() <= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.floatValue() <= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.floatValue() <= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.floatValue() <= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.floatValue() <= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.floatValue() <= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.floatValue() <= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.doubleValue() <= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.doubleValue() <= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.doubleValue() <= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.doubleValue() <= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.doubleValue() <= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.doubleValue() <= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.doubleValue() <= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.byteValue() <= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.byteValue() <= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.byteValue() <= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.byteValue() <= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.byteValue() <= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.byteValue() <= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.byteValue() <= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.shortValue() <= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.shortValue() <= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.shortValue() <= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.shortValue() <= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.shortValue() <= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.shortValue() <= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.shortValue() <= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.intValue() <= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.intValue() <= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.intValue() <= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.intValue() <= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.intValue() <= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.intValue() <= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.intValue() <= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return BooleanConstant.fromValue(left.longValue() <= right.charValue());
                        }
                        case 9: {
                            return BooleanConstant.fromValue(left.longValue() <= right.floatValue());
                        }
                        case 8: {
                            return BooleanConstant.fromValue(left.longValue() <= right.doubleValue());
                        }
                        case 3: {
                            return BooleanConstant.fromValue(left.longValue() <= right.byteValue());
                        }
                        case 4: {
                            return BooleanConstant.fromValue(left.longValue() <= right.shortValue());
                        }
                        case 10: {
                            return BooleanConstant.fromValue(left.longValue() <= right.intValue());
                        }
                        case 7: {
                            return BooleanConstant.fromValue(left.longValue() <= right.longValue());
                        }
                        default: {
                            break Label_1473;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationMINUS(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_1097: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() - right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.charValue() - right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.charValue() - right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() - right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() - right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() - right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.charValue() - right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (rightId) {
                        case 2: {
                            return FloatConstant.fromValue(left.floatValue() - right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.floatValue() - right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.floatValue() - right.doubleValue());
                        }
                        case 3: {
                            return FloatConstant.fromValue(left.floatValue() - right.byteValue());
                        }
                        case 4: {
                            return FloatConstant.fromValue(left.floatValue() - right.shortValue());
                        }
                        case 10: {
                            return FloatConstant.fromValue(left.floatValue() - right.intValue());
                        }
                        case 7: {
                            return FloatConstant.fromValue(left.floatValue() - right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (rightId) {
                        case 2: {
                            return DoubleConstant.fromValue(left.doubleValue() - right.charValue());
                        }
                        case 9: {
                            return DoubleConstant.fromValue(left.doubleValue() - right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.doubleValue() - right.doubleValue());
                        }
                        case 3: {
                            return DoubleConstant.fromValue(left.doubleValue() - right.byteValue());
                        }
                        case 4: {
                            return DoubleConstant.fromValue(left.doubleValue() - right.shortValue());
                        }
                        case 10: {
                            return DoubleConstant.fromValue(left.doubleValue() - right.intValue());
                        }
                        case 7: {
                            return DoubleConstant.fromValue(left.doubleValue() - right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() - right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.byteValue() - right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.byteValue() - right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() - right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() - right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() - right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.byteValue() - right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() - right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.shortValue() - right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.shortValue() - right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() - right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() - right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() - right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.shortValue() - right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() - right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.intValue() - right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.intValue() - right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() - right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() - right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() - right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.intValue() - right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() - right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.longValue() - right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.longValue() - right.doubleValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() - right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() - right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() - right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() - right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationMULTIPLY(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_1097: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() * right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.charValue() * right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.charValue() * right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() * right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() * right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() * right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.charValue() * right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (rightId) {
                        case 2: {
                            return FloatConstant.fromValue(left.floatValue() * right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.floatValue() * right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.floatValue() * right.doubleValue());
                        }
                        case 3: {
                            return FloatConstant.fromValue(left.floatValue() * right.byteValue());
                        }
                        case 4: {
                            return FloatConstant.fromValue(left.floatValue() * right.shortValue());
                        }
                        case 10: {
                            return FloatConstant.fromValue(left.floatValue() * right.intValue());
                        }
                        case 7: {
                            return FloatConstant.fromValue(left.floatValue() * right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (rightId) {
                        case 2: {
                            return DoubleConstant.fromValue(left.doubleValue() * right.charValue());
                        }
                        case 9: {
                            return DoubleConstant.fromValue(left.doubleValue() * right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.doubleValue() * right.doubleValue());
                        }
                        case 3: {
                            return DoubleConstant.fromValue(left.doubleValue() * right.byteValue());
                        }
                        case 4: {
                            return DoubleConstant.fromValue(left.doubleValue() * right.shortValue());
                        }
                        case 10: {
                            return DoubleConstant.fromValue(left.doubleValue() * right.intValue());
                        }
                        case 7: {
                            return DoubleConstant.fromValue(left.doubleValue() * right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() * right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.byteValue() * right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.byteValue() * right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() * right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() * right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() * right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.byteValue() * right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() * right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.shortValue() * right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.shortValue() * right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() * right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() * right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() * right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.shortValue() * right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() * right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.intValue() * right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.intValue() * right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() * right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() * right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() * right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.intValue() * right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() * right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.longValue() * right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.longValue() * right.doubleValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() * right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() * right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() * right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() * right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationOR(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_0665: {
            switch (leftId) {
                case 5: {
                    return BooleanConstant.fromValue(left.booleanValue() | right.booleanValue());
                }
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() | right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() | right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() | right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() | right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.charValue() | right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() | right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() | right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() | right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() | right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.byteValue() | right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() | right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() | right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() | right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() | right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.shortValue() | right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() | right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() | right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() | right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() | right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.intValue() | right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() | (long)right.charValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() | (long)right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() | (long)right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() | (long)right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() | right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationOR_OR(final Constant left, final int leftId, final Constant right, final int rightId) {
        return BooleanConstant.fromValue(left.booleanValue() || right.booleanValue());
    }
    
    public static final Constant computeConstantOperationPLUS(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_1729: {
            switch (leftId) {
                case 1: {
                    if (rightId == 11) {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                    }
                    break;
                }
                case 5: {
                    if (rightId == 11) {
                        return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                    }
                    break;
                }
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() + right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.charValue() + right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.charValue() + right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() + right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() + right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() + right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.charValue() + right.longValue());
                        }
                        case 11: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                        }
                        default: {
                            break Label_1729;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (rightId) {
                        case 2: {
                            return FloatConstant.fromValue(left.floatValue() + right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.floatValue() + right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.floatValue() + right.doubleValue());
                        }
                        case 3: {
                            return FloatConstant.fromValue(left.floatValue() + right.byteValue());
                        }
                        case 4: {
                            return FloatConstant.fromValue(left.floatValue() + right.shortValue());
                        }
                        case 10: {
                            return FloatConstant.fromValue(left.floatValue() + right.intValue());
                        }
                        case 7: {
                            return FloatConstant.fromValue(left.floatValue() + right.longValue());
                        }
                        case 11: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                        }
                        default: {
                            break Label_1729;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (rightId) {
                        case 2: {
                            return DoubleConstant.fromValue(left.doubleValue() + right.charValue());
                        }
                        case 9: {
                            return DoubleConstant.fromValue(left.doubleValue() + right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.doubleValue() + right.doubleValue());
                        }
                        case 3: {
                            return DoubleConstant.fromValue(left.doubleValue() + right.byteValue());
                        }
                        case 4: {
                            return DoubleConstant.fromValue(left.doubleValue() + right.shortValue());
                        }
                        case 10: {
                            return DoubleConstant.fromValue(left.doubleValue() + right.intValue());
                        }
                        case 7: {
                            return DoubleConstant.fromValue(left.doubleValue() + right.longValue());
                        }
                        case 11: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                        }
                        default: {
                            break Label_1729;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() + right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.byteValue() + right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.byteValue() + right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() + right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() + right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() + right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.byteValue() + right.longValue());
                        }
                        case 11: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                        }
                        default: {
                            break Label_1729;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() + right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.shortValue() + right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.shortValue() + right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() + right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() + right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() + right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.shortValue() + right.longValue());
                        }
                        case 11: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                        }
                        default: {
                            break Label_1729;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() + right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.intValue() + right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.intValue() + right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() + right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() + right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() + right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.intValue() + right.longValue());
                        }
                        case 11: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                        }
                        default: {
                            break Label_1729;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() + right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.longValue() + right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.longValue() + right.doubleValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() + right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() + right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() + right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() + right.longValue());
                        }
                        case 11: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                        }
                        default: {
                            break Label_1729;
                        }
                    }
                    break;
                }
                case 11: {
                    switch (rightId) {
                        case 2: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.charValue()));
                        }
                        case 9: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.floatValue()));
                        }
                        case 8: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.doubleValue()));
                        }
                        case 3: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.byteValue()));
                        }
                        case 4: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.shortValue()));
                        }
                        case 10: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.intValue()));
                        }
                        case 7: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + String.valueOf(right.longValue()));
                        }
                        case 11: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.stringValue());
                        }
                        case 5: {
                            return StringConstant.fromValue(String.valueOf(left.stringValue()) + right.booleanValue());
                        }
                        default: {
                            break Label_1729;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationREMAINDER(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_1097: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() % right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.charValue() % right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.charValue() % right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() % right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() % right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() % right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.charValue() % right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 9: {
                    switch (rightId) {
                        case 2: {
                            return FloatConstant.fromValue(left.floatValue() % right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.floatValue() % right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.floatValue() % right.doubleValue());
                        }
                        case 3: {
                            return FloatConstant.fromValue(left.floatValue() % right.byteValue());
                        }
                        case 4: {
                            return FloatConstant.fromValue(left.floatValue() % right.shortValue());
                        }
                        case 10: {
                            return FloatConstant.fromValue(left.floatValue() % right.intValue());
                        }
                        case 7: {
                            return FloatConstant.fromValue(left.floatValue() % right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 8: {
                    switch (rightId) {
                        case 2: {
                            return DoubleConstant.fromValue(left.doubleValue() % right.charValue());
                        }
                        case 9: {
                            return DoubleConstant.fromValue(left.doubleValue() % right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.doubleValue() % right.doubleValue());
                        }
                        case 3: {
                            return DoubleConstant.fromValue(left.doubleValue() % right.byteValue());
                        }
                        case 4: {
                            return DoubleConstant.fromValue(left.doubleValue() % right.shortValue());
                        }
                        case 10: {
                            return DoubleConstant.fromValue(left.doubleValue() % right.intValue());
                        }
                        case 7: {
                            return DoubleConstant.fromValue(left.doubleValue() % right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() % right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.byteValue() % right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.byteValue() % right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() % right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() % right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() % right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.byteValue() % right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() % right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.shortValue() % right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.shortValue() % right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() % right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() % right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() % right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.shortValue() % right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() % right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.intValue() % right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.intValue() % right.doubleValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() % right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() % right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() % right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.intValue() % right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() % right.charValue());
                        }
                        case 9: {
                            return FloatConstant.fromValue(left.longValue() % right.floatValue());
                        }
                        case 8: {
                            return DoubleConstant.fromValue(left.longValue() % right.doubleValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() % right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() % right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() % right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() % right.longValue());
                        }
                        default: {
                            break Label_1097;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationRIGHT_SHIFT(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_0650: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() >> right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() >> right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() >> right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() >> right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.charValue() >> (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() >> right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() >> right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() >> right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() >> right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.byteValue() >> (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() >> right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() >> right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() >> right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() >> right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.shortValue() >> (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() >> right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() >> right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() >> right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() >> right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.intValue() >> (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() >> right.charValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() >> right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() >> right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() >> right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() >> (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationUNSIGNED_RIGHT_SHIFT(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_0650: {
            switch (leftId) {
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() >>> right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() >>> right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() >>> right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() >>> right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.charValue() >>> (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() >>> right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() >>> right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() >>> right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() >>> right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.byteValue() >>> (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() >>> right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() >>> right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() >>> right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() >>> right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.shortValue() >>> (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() >>> right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() >>> right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() >>> right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() >>> right.intValue());
                        }
                        case 7: {
                            return IntConstant.fromValue(left.intValue() >>> (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() >>> right.charValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() >>> right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() >>> right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() >>> right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() >>> (int)right.longValue());
                        }
                        default: {
                            break Label_0650;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public static final Constant computeConstantOperationXOR(final Constant left, final int leftId, final Constant right, final int rightId) {
        Label_0665: {
            switch (leftId) {
                case 5: {
                    return BooleanConstant.fromValue(left.booleanValue() ^ right.booleanValue());
                }
                case 2: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.charValue() ^ right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.charValue() ^ right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.charValue() ^ right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.charValue() ^ right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.charValue() ^ right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.byteValue() ^ right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.byteValue() ^ right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.byteValue() ^ right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.byteValue() ^ right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.byteValue() ^ right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.shortValue() ^ right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.shortValue() ^ right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.shortValue() ^ right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.shortValue() ^ right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.shortValue() ^ right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 10: {
                    switch (rightId) {
                        case 2: {
                            return IntConstant.fromValue(left.intValue() ^ right.charValue());
                        }
                        case 3: {
                            return IntConstant.fromValue(left.intValue() ^ right.byteValue());
                        }
                        case 4: {
                            return IntConstant.fromValue(left.intValue() ^ right.shortValue());
                        }
                        case 10: {
                            return IntConstant.fromValue(left.intValue() ^ right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue((long)left.intValue() ^ right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
                case 7: {
                    switch (rightId) {
                        case 2: {
                            return LongConstant.fromValue(left.longValue() ^ (long)right.charValue());
                        }
                        case 3: {
                            return LongConstant.fromValue(left.longValue() ^ (long)right.byteValue());
                        }
                        case 4: {
                            return LongConstant.fromValue(left.longValue() ^ (long)right.shortValue());
                        }
                        case 10: {
                            return LongConstant.fromValue(left.longValue() ^ (long)right.intValue());
                        }
                        case 7: {
                            return LongConstant.fromValue(left.longValue() ^ right.longValue());
                        }
                        default: {
                            break Label_0665;
                        }
                    }
                    break;
                }
            }
        }
        return Constant.NotAConstant;
    }
    
    public double doubleValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { this.typeName(), "double" }));
    }
    
    public float floatValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { this.typeName(), "float" }));
    }
    
    public boolean hasSameValue(final Constant otherConstant) {
        if (this == otherConstant) {
            return true;
        }
        final int typeID;
        if ((typeID = this.typeID()) != otherConstant.typeID()) {
            return false;
        }
        switch (typeID) {
            case 5: {
                return this.booleanValue() == otherConstant.booleanValue();
            }
            case 3: {
                return this.byteValue() == otherConstant.byteValue();
            }
            case 2: {
                return this.charValue() == otherConstant.charValue();
            }
            case 8: {
                return this.doubleValue() == otherConstant.doubleValue();
            }
            case 9: {
                return this.floatValue() == otherConstant.floatValue();
            }
            case 10: {
                return this.intValue() == otherConstant.intValue();
            }
            case 4: {
                return this.shortValue() == otherConstant.shortValue();
            }
            case 7: {
                return this.longValue() == otherConstant.longValue();
            }
            case 11: {
                final String value = this.stringValue();
                return (value == null) ? (otherConstant.stringValue() == null) : value.equals(otherConstant.stringValue());
            }
            default: {
                return false;
            }
        }
    }
    
    public int intValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { this.typeName(), "int" }));
    }
    
    public long longValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotCastedInto, new String[] { this.typeName(), "long" }));
    }
    
    public short shortValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotConvertedTo, new String[] { this.typeName(), "short" }));
    }
    
    public String stringValue() {
        throw new ShouldNotImplement(Messages.bind(Messages.constant_cannotConvertedTo, new String[] { this.typeName(), "String" }));
    }
    
    @Override
    public String toString() {
        if (this == Constant.NotAConstant) {
            return "(Constant) NotAConstant";
        }
        return super.toString();
    }
    
    public abstract int typeID();
    
    public String typeName() {
        switch (this.typeID()) {
            case 10: {
                return "int";
            }
            case 3: {
                return "byte";
            }
            case 4: {
                return "short";
            }
            case 2: {
                return "char";
            }
            case 9: {
                return "float";
            }
            case 8: {
                return "double";
            }
            case 5: {
                return "boolean";
            }
            case 7: {
                return "long";
            }
            case 11: {
                return "java.lang.String";
            }
            default: {
                return "unknown";
            }
        }
    }
}
