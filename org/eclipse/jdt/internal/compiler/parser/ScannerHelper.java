package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;

public class ScannerHelper
{
    public static final long[] Bits;
    private static final int START_INDEX = 0;
    private static final int PART_INDEX = 1;
    private static long[][][] Tables;
    private static long[][][] Tables7;
    private static long[][][] Tables8;
    public static final int MAX_OBVIOUS = 128;
    public static final int[] OBVIOUS_IDENT_CHAR_NATURES;
    public static final int C_JLS_SPACE = 256;
    public static final int C_SPECIAL = 128;
    public static final int C_IDENT_START = 64;
    public static final int C_UPPER_LETTER = 32;
    public static final int C_LOWER_LETTER = 16;
    public static final int C_IDENT_PART = 8;
    public static final int C_DIGIT = 4;
    public static final int C_SEPARATOR = 2;
    public static final int C_SPACE = 1;
    
    static {
        Bits = new long[] { 1L, 2L, 4L, 8L, 16L, 32L, 64L, 128L, 256L, 512L, 1024L, 2048L, 4096L, 8192L, 16384L, 32768L, 65536L, 131072L, 262144L, 524288L, 1048576L, 2097152L, 4194304L, 8388608L, 16777216L, 33554432L, 67108864L, 134217728L, 268435456L, 536870912L, 1073741824L, 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L, 549755813888L, 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 4503599627370496L, 9007199254740992L, 18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, Long.MIN_VALUE };
        (OBVIOUS_IDENT_CHAR_NATURES = new int[128])[0] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[1] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[2] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[3] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[4] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[5] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[6] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[7] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[8] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[14] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[15] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[16] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[17] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[18] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[19] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[20] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[21] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[22] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[23] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[24] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[25] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[26] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[27] = 8;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[127] = 8;
        for (int i = 48; i <= 57; ++i) {
            ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[i] = 12;
        }
        for (int i = 97; i <= 122; ++i) {
            ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[i] = 88;
        }
        for (int i = 65; i <= 90; ++i) {
            ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[i] = 104;
        }
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[95] = 200;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[36] = 200;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[9] = 257;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[10] = 257;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[11] = 1;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[12] = 257;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[13] = 257;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[28] = 1;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[29] = 1;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[30] = 1;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[31] = 1;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[32] = 257;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[46] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[58] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[59] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[44] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[91] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[93] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[40] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[41] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[123] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[125] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[43] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[45] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[42] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[47] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[61] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[38] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[124] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[63] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[60] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[62] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[33] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[37] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[94] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[126] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[34] = 2;
        ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[39] = 2;
    }
    
    static void initializeTable() {
        ScannerHelper.Tables = initializeTables("unicode");
    }
    
    static void initializeTable17() {
        ScannerHelper.Tables7 = initializeTables("unicode6");
    }
    
    static void initializeTable18() {
        ScannerHelper.Tables8 = initializeTables("unicode6_2");
    }
    
    static long[][][] initializeTables(final String unicode_path) {
        final long[][][] tempTable = { new long[3][], new long[4][] };
        try {
            final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/start0.rsc")));
            final long[] readValues = new long[1024];
            for (int i = 0; i < 1024; ++i) {
                readValues[i] = inputStream.readLong();
            }
            inputStream.close();
            tempTable[0][0] = readValues;
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        try {
            final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/start1.rsc")));
            final long[] readValues = new long[1024];
            for (int i = 0; i < 1024; ++i) {
                readValues[i] = inputStream.readLong();
            }
            inputStream.close();
            tempTable[0][1] = readValues;
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        try {
            final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/start2.rsc")));
            final long[] readValues = new long[1024];
            for (int i = 0; i < 1024; ++i) {
                readValues[i] = inputStream.readLong();
            }
            inputStream.close();
            tempTable[0][2] = readValues;
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        try {
            final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part0.rsc")));
            final long[] readValues = new long[1024];
            for (int i = 0; i < 1024; ++i) {
                readValues[i] = inputStream.readLong();
            }
            inputStream.close();
            tempTable[1][0] = readValues;
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        try {
            final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part1.rsc")));
            final long[] readValues = new long[1024];
            for (int i = 0; i < 1024; ++i) {
                readValues[i] = inputStream.readLong();
            }
            inputStream.close();
            tempTable[1][1] = readValues;
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        try {
            final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part2.rsc")));
            final long[] readValues = new long[1024];
            for (int i = 0; i < 1024; ++i) {
                readValues[i] = inputStream.readLong();
            }
            inputStream.close();
            tempTable[1][2] = readValues;
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        try {
            final DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream(String.valueOf(unicode_path) + "/part14.rsc")));
            final long[] readValues = new long[1024];
            for (int i = 0; i < 1024; ++i) {
                readValues[i] = inputStream.readLong();
            }
            inputStream.close();
            tempTable[1][3] = readValues;
        }
        catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
        return tempTable;
    }
    
    private static final boolean isBitSet(final long[] values, final int i) {
        try {
            return (values[i / 64] & ScannerHelper.Bits[i % 64]) != 0x0L;
        }
        catch (final NullPointerException ex) {
            return false;
        }
    }
    
    public static boolean isJavaIdentifierPart(final char c) {
        if (c < '\u0080') {
            return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x8) != 0x0;
        }
        return Character.isJavaIdentifierPart(c);
    }
    
    public static boolean isJavaIdentifierPart(final long complianceLevel, final char c) {
        if (c < '\u0080') {
            return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x8) != 0x0;
        }
        return isJavaIdentifierPart(complianceLevel, (int)c);
    }
    
    public static boolean isJavaIdentifierPart(final long complianceLevel, final int codePoint) {
        if (complianceLevel <= 3276800L) {
            if (ScannerHelper.Tables == null) {
                initializeTable();
            }
            switch ((codePoint & 0x1F0000) >> 16) {
                case 0: {
                    return isBitSet(ScannerHelper.Tables[1][0], codePoint & 0xFFFF);
                }
                case 1: {
                    return isBitSet(ScannerHelper.Tables[1][1], codePoint & 0xFFFF);
                }
                case 2: {
                    return isBitSet(ScannerHelper.Tables[1][2], codePoint & 0xFFFF);
                }
                case 14: {
                    return isBitSet(ScannerHelper.Tables[1][3], codePoint & 0xFFFF);
                }
            }
        }
        else if (complianceLevel <= 3342336L) {
            if (ScannerHelper.Tables7 == null) {
                initializeTable17();
            }
            switch ((codePoint & 0x1F0000) >> 16) {
                case 0: {
                    return isBitSet(ScannerHelper.Tables7[1][0], codePoint & 0xFFFF);
                }
                case 1: {
                    return isBitSet(ScannerHelper.Tables7[1][1], codePoint & 0xFFFF);
                }
                case 2: {
                    return isBitSet(ScannerHelper.Tables7[1][2], codePoint & 0xFFFF);
                }
                case 14: {
                    return isBitSet(ScannerHelper.Tables7[1][3], codePoint & 0xFFFF);
                }
            }
        }
        else {
            if (ScannerHelper.Tables8 == null) {
                initializeTable18();
            }
            switch ((codePoint & 0x1F0000) >> 16) {
                case 0: {
                    return isBitSet(ScannerHelper.Tables8[1][0], codePoint & 0xFFFF);
                }
                case 1: {
                    return isBitSet(ScannerHelper.Tables8[1][1], codePoint & 0xFFFF);
                }
                case 2: {
                    return isBitSet(ScannerHelper.Tables8[1][2], codePoint & 0xFFFF);
                }
                case 14: {
                    return isBitSet(ScannerHelper.Tables8[1][3], codePoint & 0xFFFF);
                }
            }
        }
        return false;
    }
    
    public static boolean isJavaIdentifierPart(final long complianceLevel, final char high, final char low) {
        return isJavaIdentifierPart(complianceLevel, toCodePoint(high, low));
    }
    
    public static boolean isJavaIdentifierStart(final char c) {
        if (c < '\u0080') {
            return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0x0;
        }
        return Character.isJavaIdentifierStart(c);
    }
    
    public static boolean isJavaIdentifierStart(final long complianceLevel, final char c) {
        if (c < '\u0080') {
            return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0x0;
        }
        return isJavaIdentifierStart(complianceLevel, (int)c);
    }
    
    public static boolean isJavaIdentifierStart(final long complianceLevel, final char high, final char low) {
        return isJavaIdentifierStart(complianceLevel, toCodePoint(high, low));
    }
    
    public static boolean isJavaIdentifierStart(final long complianceLevel, final int codePoint) {
        if (complianceLevel <= 3276800L) {
            if (ScannerHelper.Tables == null) {
                initializeTable();
            }
            switch ((codePoint & 0x1F0000) >> 16) {
                case 0: {
                    return isBitSet(ScannerHelper.Tables[0][0], codePoint & 0xFFFF);
                }
                case 1: {
                    return isBitSet(ScannerHelper.Tables[0][1], codePoint & 0xFFFF);
                }
                case 2: {
                    return isBitSet(ScannerHelper.Tables[0][2], codePoint & 0xFFFF);
                }
            }
        }
        else if (complianceLevel <= 3342336L) {
            if (ScannerHelper.Tables7 == null) {
                initializeTable17();
            }
            switch ((codePoint & 0x1F0000) >> 16) {
                case 0: {
                    return isBitSet(ScannerHelper.Tables7[0][0], codePoint & 0xFFFF);
                }
                case 1: {
                    return isBitSet(ScannerHelper.Tables7[0][1], codePoint & 0xFFFF);
                }
                case 2: {
                    return isBitSet(ScannerHelper.Tables7[0][2], codePoint & 0xFFFF);
                }
            }
        }
        else {
            if (ScannerHelper.Tables8 == null) {
                initializeTable18();
            }
            switch ((codePoint & 0x1F0000) >> 16) {
                case 0: {
                    return isBitSet(ScannerHelper.Tables8[0][0], codePoint & 0xFFFF);
                }
                case 1: {
                    return isBitSet(ScannerHelper.Tables8[0][1], codePoint & 0xFFFF);
                }
                case 2: {
                    return isBitSet(ScannerHelper.Tables8[0][2], codePoint & 0xFFFF);
                }
            }
        }
        return false;
    }
    
    private static int toCodePoint(final char high, final char low) {
        return (high - '\ud800') * 1024 + (low - '\udc00') + 65536;
    }
    
    public static boolean isDigit(final char c) throws InvalidInputException {
        if (c < '\u0080') {
            return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x4) != 0x0;
        }
        if (Character.isDigit(c)) {
            throw new InvalidInputException("Invalid_Digit");
        }
        return false;
    }
    
    public static int digit(final char c, final int radix) {
        if (c < '\u0080') {
            switch (radix) {
                case 8: {
                    if (c >= '0' && c <= '7') {
                        return c - '0';
                    }
                    return -1;
                }
                case 10: {
                    if (c >= '0' && c <= '9') {
                        return c - '0';
                    }
                    return -1;
                }
                case 16: {
                    if (c >= '0' && c <= '9') {
                        return c - '0';
                    }
                    if (c >= 'A' && c <= 'F') {
                        return c - 'A' + 10;
                    }
                    if (c >= 'a' && c <= 'f') {
                        return c - 'a' + 10;
                    }
                    return -1;
                }
            }
        }
        return Character.digit(c, radix);
    }
    
    public static int getNumericValue(final char c) {
        if (c < '\u0080') {
            switch (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c]) {
                case 4: {
                    return c - '0';
                }
                case 16: {
                    return '\n' + c - 97;
                }
                case 32: {
                    return '\n' + c - 65;
                }
            }
        }
        return Character.getNumericValue(c);
    }
    
    public static int getHexadecimalValue(final char c) {
        switch (c) {
            case '0': {
                return 0;
            }
            case '1': {
                return 1;
            }
            case '2': {
                return 2;
            }
            case '3': {
                return 3;
            }
            case '4': {
                return 4;
            }
            case '5': {
                return 5;
            }
            case '6': {
                return 6;
            }
            case '7': {
                return 7;
            }
            case '8': {
                return 8;
            }
            case '9': {
                return 9;
            }
            case 'A':
            case 'a': {
                return 10;
            }
            case 'B':
            case 'b': {
                return 11;
            }
            case 'C':
            case 'c': {
                return 12;
            }
            case 'D':
            case 'd': {
                return 13;
            }
            case 'E':
            case 'e': {
                return 14;
            }
            case 'F':
            case 'f': {
                return 15;
            }
            default: {
                return -1;
            }
        }
    }
    
    public static char toUpperCase(final char c) {
        if (c < '\u0080') {
            if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x20) != 0x0) {
                return c;
            }
            if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x10) != 0x0) {
                return (char)(c - ' ');
            }
        }
        return Character.toUpperCase(c);
    }
    
    public static char toLowerCase(final char c) {
        if (c < '\u0080') {
            if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x10) != 0x0) {
                return c;
            }
            if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x20) != 0x0) {
                return (char)(' ' + c);
            }
        }
        return Character.toLowerCase(c);
    }
    
    public static boolean isLowerCase(final char c) {
        if (c < '\u0080') {
            return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x10) != 0x0;
        }
        return Character.isLowerCase(c);
    }
    
    public static boolean isUpperCase(final char c) {
        if (c < '\u0080') {
            return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x20) != 0x0;
        }
        return Character.isUpperCase(c);
    }
    
    public static boolean isWhitespace(final char c) {
        if (c < '\u0080') {
            return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x1) != 0x0;
        }
        return Character.isWhitespace(c);
    }
    
    public static boolean isLetter(final char c) {
        if (c < '\u0080') {
            return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x30) != 0x0;
        }
        return Character.isLetter(c);
    }
    
    public static boolean isLetterOrDigit(final char c) {
        if (c < '\u0080') {
            return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x34) != 0x0;
        }
        return Character.isLetterOrDigit(c);
    }
}
