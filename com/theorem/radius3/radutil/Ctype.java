package com.theorem.radius3.radutil;

public class Ctype
{
    private static int[] a;
    
    public static void main(final String[] array) {
        System.out.println("a is alpha: " + isalpha((byte)65));
        System.out.println("z is alpha: " + isalpha((byte)66));
        System.out.println("1 is alpha: " + isalpha((byte)49));
        final byte b = 32;
        System.out.println("' ' isspace: " + isspace(b));
        System.out.println("' ' isspace: " + isspace(b));
        System.out.println("'\\t' isspace: " + isspace((byte)9));
        System.out.println("'\\n' isspace: " + isspace((byte)10));
    }
    
    public static boolean isalpha(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x3) > 0;
    }
    
    public static boolean isupper(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x1) > 0;
    }
    
    public static boolean islower(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x2) > 0;
    }
    
    public static boolean isdigit(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x4) > 0;
    }
    
    public static boolean isxdigit(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x40) > 0;
    }
    
    public static boolean isspace(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x8) > 0;
    }
    
    public static boolean ispunct(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x10) > 0;
    }
    
    public static boolean isalnum(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x7) > 0;
    }
    
    public static boolean isprint(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x97) > 0;
    }
    
    public static boolean isgraph(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x17) > 0;
    }
    
    public static boolean iscntrl(final byte b) {
        return (Ctype.a[b & 0xFF] & 0x20) > 0;
    }
    
    public static boolean isascii(final byte b) {
        return (b & 0xFF) <= 31;
    }
    
    public static byte toascii(final byte b) {
        return (byte)(b & 0x1F);
    }
    
    static {
        Ctype.a = new int[] { 32, 32, 32, 32, 32, 32, 32, 32, 32, 40, 40, 40, 40, 40, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 136, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 68, 68, 68, 68, 68, 68, 68, 68, 68, 68, 16, 16, 16, 16, 16, 16, 16, 65, 65, 65, 65, 65, 65, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 16, 16, 16, 16, 16, 16, 66, 66, 66, 66, 66, 66, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 16, 16, 16, 16, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
}
