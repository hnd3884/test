package org.omg.CORBA;

public class TCKind
{
    public static final int _tk_null = 0;
    public static final int _tk_void = 1;
    public static final int _tk_short = 2;
    public static final int _tk_long = 3;
    public static final int _tk_ushort = 4;
    public static final int _tk_ulong = 5;
    public static final int _tk_float = 6;
    public static final int _tk_double = 7;
    public static final int _tk_boolean = 8;
    public static final int _tk_char = 9;
    public static final int _tk_octet = 10;
    public static final int _tk_any = 11;
    public static final int _tk_TypeCode = 12;
    public static final int _tk_Principal = 13;
    public static final int _tk_objref = 14;
    public static final int _tk_struct = 15;
    public static final int _tk_union = 16;
    public static final int _tk_enum = 17;
    public static final int _tk_string = 18;
    public static final int _tk_sequence = 19;
    public static final int _tk_array = 20;
    public static final int _tk_alias = 21;
    public static final int _tk_except = 22;
    public static final int _tk_longlong = 23;
    public static final int _tk_ulonglong = 24;
    public static final int _tk_longdouble = 25;
    public static final int _tk_wchar = 26;
    public static final int _tk_wstring = 27;
    public static final int _tk_fixed = 28;
    public static final int _tk_value = 29;
    public static final int _tk_value_box = 30;
    public static final int _tk_native = 31;
    public static final int _tk_abstract_interface = 32;
    public static final TCKind tk_null;
    public static final TCKind tk_void;
    public static final TCKind tk_short;
    public static final TCKind tk_long;
    public static final TCKind tk_ushort;
    public static final TCKind tk_ulong;
    public static final TCKind tk_float;
    public static final TCKind tk_double;
    public static final TCKind tk_boolean;
    public static final TCKind tk_char;
    public static final TCKind tk_octet;
    public static final TCKind tk_any;
    public static final TCKind tk_TypeCode;
    public static final TCKind tk_Principal;
    public static final TCKind tk_objref;
    public static final TCKind tk_struct;
    public static final TCKind tk_union;
    public static final TCKind tk_enum;
    public static final TCKind tk_string;
    public static final TCKind tk_sequence;
    public static final TCKind tk_array;
    public static final TCKind tk_alias;
    public static final TCKind tk_except;
    public static final TCKind tk_longlong;
    public static final TCKind tk_ulonglong;
    public static final TCKind tk_longdouble;
    public static final TCKind tk_wchar;
    public static final TCKind tk_wstring;
    public static final TCKind tk_fixed;
    public static final TCKind tk_value;
    public static final TCKind tk_value_box;
    public static final TCKind tk_native;
    public static final TCKind tk_abstract_interface;
    private int _value;
    
    public int value() {
        return this._value;
    }
    
    public static TCKind from_int(final int n) {
        switch (n) {
            case 0: {
                return TCKind.tk_null;
            }
            case 1: {
                return TCKind.tk_void;
            }
            case 2: {
                return TCKind.tk_short;
            }
            case 3: {
                return TCKind.tk_long;
            }
            case 4: {
                return TCKind.tk_ushort;
            }
            case 5: {
                return TCKind.tk_ulong;
            }
            case 6: {
                return TCKind.tk_float;
            }
            case 7: {
                return TCKind.tk_double;
            }
            case 8: {
                return TCKind.tk_boolean;
            }
            case 9: {
                return TCKind.tk_char;
            }
            case 10: {
                return TCKind.tk_octet;
            }
            case 11: {
                return TCKind.tk_any;
            }
            case 12: {
                return TCKind.tk_TypeCode;
            }
            case 13: {
                return TCKind.tk_Principal;
            }
            case 14: {
                return TCKind.tk_objref;
            }
            case 15: {
                return TCKind.tk_struct;
            }
            case 16: {
                return TCKind.tk_union;
            }
            case 17: {
                return TCKind.tk_enum;
            }
            case 18: {
                return TCKind.tk_string;
            }
            case 19: {
                return TCKind.tk_sequence;
            }
            case 20: {
                return TCKind.tk_array;
            }
            case 21: {
                return TCKind.tk_alias;
            }
            case 22: {
                return TCKind.tk_except;
            }
            case 23: {
                return TCKind.tk_longlong;
            }
            case 24: {
                return TCKind.tk_ulonglong;
            }
            case 25: {
                return TCKind.tk_longdouble;
            }
            case 26: {
                return TCKind.tk_wchar;
            }
            case 27: {
                return TCKind.tk_wstring;
            }
            case 28: {
                return TCKind.tk_fixed;
            }
            case 29: {
                return TCKind.tk_value;
            }
            case 30: {
                return TCKind.tk_value_box;
            }
            case 31: {
                return TCKind.tk_native;
            }
            case 32: {
                return TCKind.tk_abstract_interface;
            }
            default: {
                throw new BAD_PARAM();
            }
        }
    }
    
    @Deprecated
    protected TCKind(final int value) {
        this._value = value;
    }
    
    static {
        tk_null = new TCKind(0);
        tk_void = new TCKind(1);
        tk_short = new TCKind(2);
        tk_long = new TCKind(3);
        tk_ushort = new TCKind(4);
        tk_ulong = new TCKind(5);
        tk_float = new TCKind(6);
        tk_double = new TCKind(7);
        tk_boolean = new TCKind(8);
        tk_char = new TCKind(9);
        tk_octet = new TCKind(10);
        tk_any = new TCKind(11);
        tk_TypeCode = new TCKind(12);
        tk_Principal = new TCKind(13);
        tk_objref = new TCKind(14);
        tk_struct = new TCKind(15);
        tk_union = new TCKind(16);
        tk_enum = new TCKind(17);
        tk_string = new TCKind(18);
        tk_sequence = new TCKind(19);
        tk_array = new TCKind(20);
        tk_alias = new TCKind(21);
        tk_except = new TCKind(22);
        tk_longlong = new TCKind(23);
        tk_ulonglong = new TCKind(24);
        tk_longdouble = new TCKind(25);
        tk_wchar = new TCKind(26);
        tk_wstring = new TCKind(27);
        tk_fixed = new TCKind(28);
        tk_value = new TCKind(29);
        tk_value_box = new TCKind(30);
        tk_native = new TCKind(31);
        tk_abstract_interface = new TCKind(32);
    }
}
