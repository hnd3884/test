package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

public final class Const
{
    public static byte default_value_byte;
    public static boolean default_value_boolean;
    public static char default_value_char;
    public static float default_value_float;
    public static double default_value_double;
    public static int default_value_int;
    public static long default_value_long;
    public static short default_value_short;
    
    static {
        Const.default_value_byte = 0;
        Const.default_value_boolean = false;
        Const.default_value_char = '\0';
        Const.default_value_float = 0.0f;
        Const.default_value_double = 0.0;
        Const.default_value_int = 0;
        Const.default_value_long = 0L;
        Const.default_value_short = 0;
    }
}
