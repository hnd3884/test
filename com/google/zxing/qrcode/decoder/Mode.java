package com.google.zxing.qrcode.decoder;

public enum Mode
{
    TERMINATOR(new int[] { 0, 0, 0 }, 0), 
    NUMERIC(new int[] { 10, 12, 14 }, 1), 
    ALPHANUMERIC(new int[] { 9, 11, 13 }, 2), 
    STRUCTURED_APPEND(new int[] { 0, 0, 0 }, 3), 
    BYTE(new int[] { 8, 16, 16 }, 4), 
    ECI(new int[] { 0, 0, 0 }, 7), 
    KANJI(new int[] { 8, 10, 12 }, 8), 
    FNC1_FIRST_POSITION(new int[] { 0, 0, 0 }, 5), 
    FNC1_SECOND_POSITION(new int[] { 0, 0, 0 }, 9), 
    HANZI(new int[] { 8, 10, 12 }, 13);
    
    private final int[] characterCountBitsForVersions;
    private final int bits;
    
    private Mode(final int[] characterCountBitsForVersions, final int bits) {
        this.characterCountBitsForVersions = characterCountBitsForVersions;
        this.bits = bits;
    }
    
    public static Mode forBits(final int bits) {
        switch (bits) {
            case 0: {
                return Mode.TERMINATOR;
            }
            case 1: {
                return Mode.NUMERIC;
            }
            case 2: {
                return Mode.ALPHANUMERIC;
            }
            case 3: {
                return Mode.STRUCTURED_APPEND;
            }
            case 4: {
                return Mode.BYTE;
            }
            case 5: {
                return Mode.FNC1_FIRST_POSITION;
            }
            case 7: {
                return Mode.ECI;
            }
            case 8: {
                return Mode.KANJI;
            }
            case 9: {
                return Mode.FNC1_SECOND_POSITION;
            }
            case 13: {
                return Mode.HANZI;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    public int getCharacterCountBits(final Version version) {
        final int number = version.getVersionNumber();
        int offset;
        if (number <= 9) {
            offset = 0;
        }
        else if (number <= 26) {
            offset = 1;
        }
        else {
            offset = 2;
        }
        return this.characterCountBitsForVersions[offset];
    }
    
    public int getBits() {
        return this.bits;
    }
}
