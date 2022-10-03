package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;

public class DefinitionKind implements IDLEntity
{
    public static final int _dk_none = 0;
    public static final int _dk_all = 1;
    public static final int _dk_Attribute = 2;
    public static final int _dk_Constant = 3;
    public static final int _dk_Exception = 4;
    public static final int _dk_Interface = 5;
    public static final int _dk_Module = 6;
    public static final int _dk_Operation = 7;
    public static final int _dk_Typedef = 8;
    public static final int _dk_Alias = 9;
    public static final int _dk_Struct = 10;
    public static final int _dk_Union = 11;
    public static final int _dk_Enum = 12;
    public static final int _dk_Primitive = 13;
    public static final int _dk_String = 14;
    public static final int _dk_Sequence = 15;
    public static final int _dk_Array = 16;
    public static final int _dk_Repository = 17;
    public static final int _dk_Wstring = 18;
    public static final int _dk_Fixed = 19;
    public static final int _dk_Value = 20;
    public static final int _dk_ValueBox = 21;
    public static final int _dk_ValueMember = 22;
    public static final int _dk_Native = 23;
    public static final int _dk_AbstractInterface = 24;
    public static final DefinitionKind dk_none;
    public static final DefinitionKind dk_all;
    public static final DefinitionKind dk_Attribute;
    public static final DefinitionKind dk_Constant;
    public static final DefinitionKind dk_Exception;
    public static final DefinitionKind dk_Interface;
    public static final DefinitionKind dk_Module;
    public static final DefinitionKind dk_Operation;
    public static final DefinitionKind dk_Typedef;
    public static final DefinitionKind dk_Alias;
    public static final DefinitionKind dk_Struct;
    public static final DefinitionKind dk_Union;
    public static final DefinitionKind dk_Enum;
    public static final DefinitionKind dk_Primitive;
    public static final DefinitionKind dk_String;
    public static final DefinitionKind dk_Sequence;
    public static final DefinitionKind dk_Array;
    public static final DefinitionKind dk_Repository;
    public static final DefinitionKind dk_Wstring;
    public static final DefinitionKind dk_Fixed;
    public static final DefinitionKind dk_Value;
    public static final DefinitionKind dk_ValueBox;
    public static final DefinitionKind dk_ValueMember;
    public static final DefinitionKind dk_Native;
    public static final DefinitionKind dk_AbstractInterface;
    private int _value;
    
    public int value() {
        return this._value;
    }
    
    public static DefinitionKind from_int(final int n) {
        switch (n) {
            case 0: {
                return DefinitionKind.dk_none;
            }
            case 1: {
                return DefinitionKind.dk_all;
            }
            case 2: {
                return DefinitionKind.dk_Attribute;
            }
            case 3: {
                return DefinitionKind.dk_Constant;
            }
            case 4: {
                return DefinitionKind.dk_Exception;
            }
            case 5: {
                return DefinitionKind.dk_Interface;
            }
            case 6: {
                return DefinitionKind.dk_Module;
            }
            case 7: {
                return DefinitionKind.dk_Operation;
            }
            case 8: {
                return DefinitionKind.dk_Typedef;
            }
            case 9: {
                return DefinitionKind.dk_Alias;
            }
            case 10: {
                return DefinitionKind.dk_Struct;
            }
            case 11: {
                return DefinitionKind.dk_Union;
            }
            case 12: {
                return DefinitionKind.dk_Enum;
            }
            case 13: {
                return DefinitionKind.dk_Primitive;
            }
            case 14: {
                return DefinitionKind.dk_String;
            }
            case 15: {
                return DefinitionKind.dk_Sequence;
            }
            case 16: {
                return DefinitionKind.dk_Array;
            }
            case 17: {
                return DefinitionKind.dk_Repository;
            }
            case 18: {
                return DefinitionKind.dk_Wstring;
            }
            case 19: {
                return DefinitionKind.dk_Fixed;
            }
            case 20: {
                return DefinitionKind.dk_Value;
            }
            case 21: {
                return DefinitionKind.dk_ValueBox;
            }
            case 22: {
                return DefinitionKind.dk_ValueMember;
            }
            case 23: {
                return DefinitionKind.dk_Native;
            }
            default: {
                throw new BAD_PARAM();
            }
        }
    }
    
    protected DefinitionKind(final int value) {
        this._value = value;
    }
    
    static {
        dk_none = new DefinitionKind(0);
        dk_all = new DefinitionKind(1);
        dk_Attribute = new DefinitionKind(2);
        dk_Constant = new DefinitionKind(3);
        dk_Exception = new DefinitionKind(4);
        dk_Interface = new DefinitionKind(5);
        dk_Module = new DefinitionKind(6);
        dk_Operation = new DefinitionKind(7);
        dk_Typedef = new DefinitionKind(8);
        dk_Alias = new DefinitionKind(9);
        dk_Struct = new DefinitionKind(10);
        dk_Union = new DefinitionKind(11);
        dk_Enum = new DefinitionKind(12);
        dk_Primitive = new DefinitionKind(13);
        dk_String = new DefinitionKind(14);
        dk_Sequence = new DefinitionKind(15);
        dk_Array = new DefinitionKind(16);
        dk_Repository = new DefinitionKind(17);
        dk_Wstring = new DefinitionKind(18);
        dk_Fixed = new DefinitionKind(19);
        dk_Value = new DefinitionKind(20);
        dk_ValueBox = new DefinitionKind(21);
        dk_ValueMember = new DefinitionKind(22);
        dk_Native = new DefinitionKind(23);
        dk_AbstractInterface = new DefinitionKind(24);
    }
}
