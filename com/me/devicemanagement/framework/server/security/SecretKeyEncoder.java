package com.me.devicemanagement.framework.server.security;

public class SecretKeyEncoder
{
    private static final String BASE32CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final byte[] BASE32LOOKUP;
    private static final String ERRORCANONICALLENGTH = "non canonical Base32 string length";
    private static final String ERRORCANONICALEND = "non canonical bits at end of Base32 string";
    private static final String ERRORINVALIDCHAR = "invalid character in Base32 string";
    
    public static byte[] decode(final String base32) throws IllegalArgumentException {
        switch (base32.length() % 8) {
            case 1:
            case 3:
            case 6: {
                throw new IllegalArgumentException("non canonical Base32 string length");
            }
            default: {
                final int length = base32.length() * 5 / 8;
                if (length > 1000) {
                    return null;
                }
                final byte[] bytes = new byte[length];
                int offset = 0;
                int i = 0;
                while (i < base32.length()) {
                    int lookup = base32.charAt(i++) - '2';
                    if (lookup < 0 || lookup >= SecretKeyEncoder.BASE32LOOKUP.length) {
                        throw new IllegalArgumentException("invalid character in Base32 string");
                    }
                    byte digit = SecretKeyEncoder.BASE32LOOKUP[lookup];
                    if (digit == -1) {
                        throw new IllegalArgumentException("invalid character in Base32 string");
                    }
                    byte nextByte = (byte)(digit << 3);
                    lookup = base32.charAt(i++) - '2';
                    if (lookup < 0 || lookup >= SecretKeyEncoder.BASE32LOOKUP.length) {
                        throw new IllegalArgumentException("invalid character in Base32 string");
                    }
                    digit = SecretKeyEncoder.BASE32LOOKUP[lookup];
                    if (digit == -1) {
                        throw new IllegalArgumentException("invalid character in Base32 string");
                    }
                    bytes[offset++] = (byte)(nextByte | digit >> 2);
                    nextByte = (byte)((digit & 0x3) << 6);
                    if (i >= base32.length()) {
                        if (nextByte != 0) {
                            throw new IllegalArgumentException("non canonical bits at end of Base32 string");
                        }
                        break;
                    }
                    else {
                        lookup = base32.charAt(i++) - '2';
                        if (lookup < 0 || lookup >= SecretKeyEncoder.BASE32LOOKUP.length) {
                            throw new IllegalArgumentException("invalid character in Base32 string");
                        }
                        digit = SecretKeyEncoder.BASE32LOOKUP[lookup];
                        if (digit == -1) {
                            throw new IllegalArgumentException("invalid character in Base32 string");
                        }
                        nextByte |= (byte)(digit << 1);
                        lookup = base32.charAt(i++) - '2';
                        if (lookup < 0 || lookup >= SecretKeyEncoder.BASE32LOOKUP.length) {
                            throw new IllegalArgumentException("invalid character in Base32 string");
                        }
                        digit = SecretKeyEncoder.BASE32LOOKUP[lookup];
                        if (digit == -1) {
                            throw new IllegalArgumentException("invalid character in Base32 string");
                        }
                        bytes[offset++] = (byte)(nextByte | digit >> 4);
                        nextByte = (byte)((digit & 0xF) << 4);
                        if (i >= base32.length()) {
                            if (nextByte != 0) {
                                throw new IllegalArgumentException("non canonical bits at end of Base32 string");
                            }
                            break;
                        }
                        else {
                            lookup = base32.charAt(i++) - '2';
                            if (lookup < 0 || lookup >= SecretKeyEncoder.BASE32LOOKUP.length) {
                                throw new IllegalArgumentException("invalid character in Base32 string");
                            }
                            digit = SecretKeyEncoder.BASE32LOOKUP[lookup];
                            if (digit == -1) {
                                throw new IllegalArgumentException("invalid character in Base32 string");
                            }
                            bytes[offset++] = (byte)(nextByte | digit >> 1);
                            nextByte = (byte)((digit & 0x1) << 7);
                            if (i >= base32.length()) {
                                if (nextByte != 0) {
                                    throw new IllegalArgumentException("non canonical bits at end of Base32 string");
                                }
                                break;
                            }
                            else {
                                lookup = base32.charAt(i++) - '2';
                                if (lookup < 0 || lookup >= SecretKeyEncoder.BASE32LOOKUP.length) {
                                    throw new IllegalArgumentException("invalid character in Base32 string");
                                }
                                digit = SecretKeyEncoder.BASE32LOOKUP[lookup];
                                if (digit == -1) {
                                    throw new IllegalArgumentException("invalid character in Base32 string");
                                }
                                nextByte |= (byte)(digit << 2);
                                lookup = base32.charAt(i++) - '2';
                                if (lookup < 0 || lookup >= SecretKeyEncoder.BASE32LOOKUP.length) {
                                    throw new IllegalArgumentException("invalid character in Base32 string");
                                }
                                digit = SecretKeyEncoder.BASE32LOOKUP[lookup];
                                if (digit == -1) {
                                    throw new IllegalArgumentException("invalid character in Base32 string");
                                }
                                bytes[offset++] = (byte)(nextByte | digit >> 3);
                                nextByte = (byte)((digit & 0x7) << 5);
                                if (i >= base32.length()) {
                                    if (nextByte != 0) {
                                        throw new IllegalArgumentException("non canonical bits at end of Base32 string");
                                    }
                                    break;
                                }
                                else {
                                    lookup = base32.charAt(i++) - '2';
                                    if (lookup < 0 || lookup >= SecretKeyEncoder.BASE32LOOKUP.length) {
                                        throw new IllegalArgumentException("invalid character in Base32 string");
                                    }
                                    digit = SecretKeyEncoder.BASE32LOOKUP[lookup];
                                    if (digit == -1) {
                                        throw new IllegalArgumentException("invalid character in Base32 string");
                                    }
                                    bytes[offset++] = (byte)(nextByte | digit);
                                }
                            }
                        }
                    }
                }
                return bytes;
            }
        }
    }
    
    public static String encode(final byte[] bytes) {
        final StringBuffer base32 = new StringBuffer((bytes.length * 8 + 4) / 5);
        int i = 0;
        while (i < bytes.length) {
            int currByte = bytes[i++] & 0xFF;
            base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(currByte >> 3));
            int digit = (currByte & 0x7) << 2;
            if (i >= bytes.length) {
                base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(digit));
                break;
            }
            currByte = (bytes[i++] & 0xFF);
            base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(digit | currByte >> 6));
            base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(currByte >> 1 & 0x1F));
            digit = (currByte & 0x1) << 4;
            if (i >= bytes.length) {
                base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(digit));
                break;
            }
            currByte = (bytes[i++] & 0xFF);
            base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(digit | currByte >> 4));
            digit = (currByte & 0xF) << 1;
            if (i >= bytes.length) {
                base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(digit));
                break;
            }
            currByte = (bytes[i++] & 0xFF);
            base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(digit | currByte >> 7));
            base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(currByte >> 2 & 0x1F));
            digit = (currByte & 0x3) << 3;
            if (i >= bytes.length) {
                base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(digit));
                break;
            }
            currByte = (bytes[i++] & 0xFF);
            base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(digit | currByte >> 5));
            base32.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".charAt(currByte & 0x1F));
        }
        return base32.toString();
    }
    
    static {
        BASE32LOOKUP = new byte[] { 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
    }
}
