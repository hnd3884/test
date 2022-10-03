package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.Constants;

public final class BasicType extends Type
{
    BasicType(final byte type) {
        super(type, Constants.SHORT_TYPE_NAMES[type]);
        if (type < 4 || type > 12) {
            throw new ClassGenException("Invalid type: " + type);
        }
    }
    
    public static final BasicType getType(final byte type) {
        switch (type) {
            case 12: {
                return BasicType.VOID;
            }
            case 4: {
                return BasicType.BOOLEAN;
            }
            case 8: {
                return BasicType.BYTE;
            }
            case 9: {
                return BasicType.SHORT;
            }
            case 5: {
                return BasicType.CHAR;
            }
            case 10: {
                return BasicType.INT;
            }
            case 11: {
                return BasicType.LONG;
            }
            case 7: {
                return BasicType.DOUBLE;
            }
            case 6: {
                return BasicType.FLOAT;
            }
            default: {
                throw new ClassGenException("Invalid type: " + type);
            }
        }
    }
    
    @Override
    public boolean equals(final Object type) {
        return type instanceof BasicType && ((BasicType)type).type == this.type;
    }
    
    @Override
    public int hashCode() {
        return this.type;
    }
}
