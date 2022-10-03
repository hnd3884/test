package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public class SetOverrideType implements IDLEntity
{
    public static final int _SET_OVERRIDE = 0;
    public static final int _ADD_OVERRIDE = 1;
    public static final SetOverrideType SET_OVERRIDE;
    public static final SetOverrideType ADD_OVERRIDE;
    private int _value;
    
    public int value() {
        return this._value;
    }
    
    public static SetOverrideType from_int(final int n) {
        switch (n) {
            case 0: {
                return SetOverrideType.SET_OVERRIDE;
            }
            case 1: {
                return SetOverrideType.ADD_OVERRIDE;
            }
            default: {
                throw new BAD_PARAM();
            }
        }
    }
    
    protected SetOverrideType(final int value) {
        this._value = value;
    }
    
    static {
        SET_OVERRIDE = new SetOverrideType(0);
        ADD_OVERRIDE = new SetOverrideType(1);
    }
}
