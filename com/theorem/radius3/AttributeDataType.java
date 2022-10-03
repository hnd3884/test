package com.theorem.radius3;

import com.theorem.radius3.dictionary.RADIUSDictionary;

public final class AttributeDataType
{
    public static final int UNKNOWN = 0;
    public static final int INTEGER = 1;
    public static final int OCTETS = 2;
    public static final int STRING = 4;
    public static final int DATE = 8;
    public static final int IPADDRESS = 16;
    public static final int TUNNEL = 32;
    public static final int ENCRYPT1 = 64;
    public static final int ENCRYPT2 = 128;
    public static final int ENCRYPT3 = 256;
    private static int[] a;
    private static int[][] b;
    
    public static void addDictionary(final RADIUSDictionary radiusDictionary) {
        Dict.addDictionary(radiusDictionary);
    }
    
    public static int getDataType(final int n) {
        return getDataType(0, n);
    }
    
    public static int getDataType(final int n, final int n2) {
        final RADIUSDictionary[] dictionaries = Dict.getDictionaries();
        for (int i = 0; i < dictionaries.length; ++i) {
            final int dataType = dictionaries[i].getDataType(n, n2);
            if (dataType != -1) {
                return dataType;
            }
        }
        if (n == 0 && n2 <= 255 && n2 >= 0) {
            return AttributeDataType.a[n2];
        }
        return 0;
    }
    
    public static String getDataTypeName(int n) {
        final RADIUSDictionary[] dictionaries = Dict.getDictionaries();
        if (dictionaries.length > 0) {
            return dictionaries[0].getDataTypeName(n);
        }
        String s = "";
        if ((n & 0x20) == 0x20) {
            s = "TUNNEL / ";
            n &= 0xFFFFFFDF;
        }
        if ((n & 0x40) == 0x40) {
            s = "ENCRYPT1 / ";
            n &= 0xFFFFFFBF;
        }
        if ((n & 0x80) == 0x80) {
            s = "ENCRYPT2 / ";
            n &= 0xFFFFFF7F;
        }
        if ((n & 0x100) == 0x100) {
            s = "ENCRYPT3 / ";
            n &= 0xFFFFFEFF;
        }
        switch (n) {
            default: {
                s = s + "UNKNOWN (" + n + ")";
                break;
            }
            case 2: {
                s += "OCTETS";
                break;
            }
            case 8: {
                s += "DATE";
                break;
            }
            case 4: {
                s += "STRING";
                break;
            }
            case 16: {
                s += "IPADDRESS";
                break;
            }
            case 0: {
                break;
            }
            case 1: {
                s += "INTEGER";
                break;
            }
        }
        return s;
    }
    
    public static void setDataType(final int n, final int n2) {
        if (n > 255 || n < 0) {
            return;
        }
        AttributeDataType.a[n] = n2;
    }
    
    private static void a() {
        a(AttributeDataType.b);
        AttributeDataType.a = new int[256];
        for (int length = AttributeDataType.b.length, i = 0; i < length; ++i) {
            AttributeDataType.a[AttributeDataType.b[i][0]] = AttributeDataType.b[i][1];
        }
        AttributeDataType.b = null;
    }
    
    private static void a(final int[][] array) {
        for (int length = array.length, i = 1; i < length; ++i) {
            int n = i;
            final int n2 = array[i][0];
            final int n3 = array[i][1];
            while (n > 0 && array[n - 1][0] > n2) {
                array[n][0] = array[n - 1][0];
                array[n][1] = array[n - 1][1];
                --n;
            }
            array[n][0] = n2;
            array[n][1] = n3;
        }
    }
    
    public static void main(final String[] array) {
        final int n = 251;
        System.out.println("Searching for " + AttributeName.lookup(n) + "[" + n + "] Found " + getDataType(n));
        final int n2 = 101;
        System.out.println("Changing attribute s = " + AttributeName.lookup(n2));
        System.out.println("Was " + getDataType(n2));
        System.out.println("Should return type  0");
        setDataType(n2, 4);
        System.out.println("Searching for " + AttributeName.lookup(n2) + "[" + n2 + "] Now " + getDataType(n2));
        System.out.println();
        final int n3 = 1;
        System.out.println("Changing attribute s = " + AttributeName.lookup(n3));
        System.out.println("Setting data type to INTEGER");
        System.out.println("Searching for " + AttributeName.lookup(n3) + "[" + n3 + "] Found " + getDataType(n3));
        setDataType(n3, 1);
        System.out.println("Searching for " + AttributeName.lookup(n3) + "[" + n3 + "] Found " + getDataType(n3));
    }
    
    static {
        AttributeDataType.b = new int[][] { { 1, 4 }, { 2, 4 }, { 3, 2 }, { 4, 16 }, { 5, 1 }, { 6, 1 }, { 7, 1 }, { 8, 16 }, { 9, 16 }, { 10, 1 }, { 11, 4 }, { 12, 1 }, { 13, 1 }, { 14, 16 }, { 15, 1 }, { 16, 1 }, { 17, 4 }, { 18, 4 }, { 19, 4 }, { 20, 4 }, { 21, 8 }, { 22, 4 }, { 23, 16 }, { 24, 2 }, { 25, 2 }, { 26, 2 }, { 27, 1 }, { 28, 1 }, { 29, 1 }, { 30, 4 }, { 31, 4 }, { 32, 4 }, { 33, 2 }, { 34, 4 }, { 35, 4 }, { 36, 2 }, { 37, 1 }, { 38, 1 }, { 39, 4 }, { 40, 1 }, { 41, 1 }, { 42, 1 }, { 43, 1 }, { 44, 4 }, { 45, 1 }, { 46, 1 }, { 47, 1 }, { 48, 1 }, { 49, 1 }, { 50, 4 }, { 51, 1 }, { 52, 1 }, { 53, 1 }, { 55, 8 }, { 56, 2 }, { 57, 2 }, { 58, 2 }, { 59, 2 }, { 60, 4 }, { 61, 1 }, { 62, 1 }, { 63, 1 }, { 64, 33 }, { 65, 33 }, { 66, 36 }, { 67, 36 }, { 68, 4 }, { 69, 162 }, { 70, 4 }, { 71, 4 }, { 72, 1 }, { 73, 1 }, { 74, 4 }, { 75, 1 }, { 76, 1 }, { 77, 4 }, { 78, 4 }, { 79, 4 }, { 80, 2 }, { 81, 36 }, { 82, 36 }, { 83, 33 }, { 84, 4 }, { 85, 1 }, { 86, 33 }, { 87, 4 }, { 88, 4 }, { 90, 36 }, { 91, 36 }, { 94, 1 }, { 95, 2 }, { 96, 2 }, { 97, 2 }, { 98, 2 }, { 99, 4 }, { 100, 4 }, { 255, 1 }, { 254, 1 }, { 253, 16 }, { 252, 4 }, { 251, 1 }, { 250, 1 }, { 249, 4 }, { 248, 1 }, { 247, 1 }, { 246, 1 }, { 245, 1 }, { 244, 1 }, { 243, 2 }, { 242, 4 }, { 241, 1 }, { 240, 1 }, { 239, 1 }, { 238, 1 }, { 237, 1 }, { 236, 1 }, { 235, 1 }, { 234, 1 }, { 233, 1 }, { 232, 4 }, { 231, 1 }, { 230, 1 }, { 229, 1 }, { 228, 1 }, { 227, 4 }, { 226, 1 }, { 225, 1 }, { 224, 1 }, { 223, 1 }, { 222, 1 }, { 221, 1 }, { 220, 4 }, { 219, 1 }, { 218, 1 }, { 217, 4 }, { 216, 1 }, { 215, 2 }, { 214, 2 }, { 213, 4 }, { 212, 1 }, { 211, 1 }, { 210, 1 }, { 209, 16 }, { 208, 1 }, { 207, 1 }, { 206, 4 }, { 205, 4 }, { 204, 1 }, { 203, 4 }, { 202, 4 }, { 201, 1 }, { 199, 1 }, { 198, 1 }, { 197, 1 }, { 196, 1 }, { 195, 1 }, { 194, 1 }, { 193, 1 }, { 192, 1 }, { 191, 1 }, { 190, 1 }, { 189, 16 }, { 188, 1 }, { 187, 1 }, { 186, 1 }, { 185, 4 }, { 184, 2 }, { 183, 16 }, { 182, 16 }, { 181, 2 }, { 180, 4 }, { 179, 1 }, { 178, 4 }, { 177, 1 }, { 176, 4 }, { 175, 1 }, { 174, 4 }, { 173, 1 }, { 172, 1 }, { 171, 1 }, { 170, 1 }, { 169, 1 }, { 168, 4 }, { 167, 1 }, { 166, 1 }, { 165, 1 }, { 164, 1 }, { 163, 1 }, { 162, 1 }, { 161, 1 }, { 160, 1 }, { 159, 1 }, { 158, 1 }, { 157, 1 }, { 156, 4 }, { 155, 1 }, { 154, 16 }, { 153, 16 }, { 152, 1 }, { 151, 1 }, { 150, 1 }, { 149, 1 }, { 148, 1 }, { 147, 1 }, { 146, 4 }, { 145, 16 }, { 144, 16 }, { 143, 1 }, { 142, 1 }, { 141, 4 }, { 140, 1 }, { 139, 1 }, { 138, 1 }, { 137, 1 }, { 136, 16 }, { 135, 16 }, { 134, 1 }, { 133, 4 }, { 131, 1 }, { 130, 4 }, { 129, 4 }, { 128, 4 }, { 127, 4 }, { 126, 4 }, { 125, 1 }, { 124, 1 }, { 123, 1 }, { 122, 1 }, { 121, 1 }, { 120, 1 }, { 119, 4 }, { 118, 4 }, { 117, 4 }, { 116, 4 }, { 115, 4 }, { 114, 4 }, { 113, 4 }, { 112, 4 }, { 111, 1 }, { 110, 4 }, { 109, 4 }, { 108, 4 } };
        a();
    }
}
