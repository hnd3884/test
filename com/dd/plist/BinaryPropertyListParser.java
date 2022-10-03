package com.dd.plist;

import java.math.BigInteger;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public final class BinaryPropertyListParser
{
    private int majorVersion;
    private int minorVersion;
    private byte[] bytes;
    private int objectRefSize;
    private int[] offsetTable;
    
    private BinaryPropertyListParser() {
    }
    
    public static NSObject parse(final byte[] data) throws PropertyListFormatException, UnsupportedEncodingException {
        final BinaryPropertyListParser parser = new BinaryPropertyListParser();
        return parser.doParse(data);
    }
    
    private NSObject doParse(final byte[] data) throws PropertyListFormatException, UnsupportedEncodingException {
        this.bytes = data;
        final String magic = new String(copyOfRange(this.bytes, 0, 8));
        if (!magic.startsWith("bplist")) {
            throw new IllegalArgumentException("The given data is no binary property list. Wrong magic bytes: " + magic);
        }
        this.majorVersion = magic.charAt(6) - '0';
        this.minorVersion = magic.charAt(7) - '0';
        if (this.majorVersion > 0) {
            throw new IllegalArgumentException("Unsupported binary property list format: v" + this.majorVersion + "." + this.minorVersion + ". " + "Version 1.0 and later are not yet supported.");
        }
        final byte[] trailer = copyOfRange(this.bytes, this.bytes.length - 32, this.bytes.length);
        final int offsetSize = (int)parseUnsignedInt(trailer, 6, 7);
        this.objectRefSize = (int)parseUnsignedInt(trailer, 7, 8);
        final int numObjects = (int)parseUnsignedInt(trailer, 8, 16);
        final int topObject = (int)parseUnsignedInt(trailer, 16, 24);
        final int offsetTableOffset = (int)parseUnsignedInt(trailer, 24, 32);
        this.offsetTable = new int[numObjects];
        for (int i = 0; i < numObjects; ++i) {
            this.offsetTable[i] = (int)parseUnsignedInt(this.bytes, offsetTableOffset + i * offsetSize, offsetTableOffset + (i + 1) * offsetSize);
        }
        return this.parseObject(topObject);
    }
    
    public static NSObject parse(final InputStream is) throws IOException, PropertyListFormatException {
        final byte[] buf = PropertyListParser.readAll(is);
        return parse(buf);
    }
    
    public static NSObject parse(final File f) throws IOException, PropertyListFormatException {
        return parse(new FileInputStream(f));
    }
    
    private NSObject parseObject(final int obj) throws PropertyListFormatException, UnsupportedEncodingException {
        final int offset = this.offsetTable[obj];
        final byte type = this.bytes[offset];
        final int objType = (type & 0xF0) >> 4;
        final int objInfo = type & 0xF;
        switch (objType) {
            case 0: {
                switch (objInfo) {
                    case 0: {
                        return null;
                    }
                    case 8: {
                        return new NSNumber(false);
                    }
                    case 9: {
                        return new NSNumber(true);
                    }
                    case 12: {
                        throw new UnsupportedOperationException("The given binary property list contains a URL object. Parsing of this object type is not yet implemented.");
                    }
                    case 13: {
                        throw new UnsupportedOperationException("The given binary property list contains a URL object. Parsing of this object type is not yet implemented.");
                    }
                    case 14: {
                        throw new UnsupportedOperationException("The given binary property list contains a UUID object. Parsing of this object type is not yet implemented.");
                    }
                    default: {
                        throw new PropertyListFormatException("The given binary property list contains an object of unknown type (" + objType + ")");
                    }
                }
                break;
            }
            case 1: {
                final int length = (int)Math.pow(2.0, objInfo);
                return new NSNumber(this.bytes, offset + 1, offset + 1 + length, 0);
            }
            case 2: {
                final int length = (int)Math.pow(2.0, objInfo);
                return new NSNumber(this.bytes, offset + 1, offset + 1 + length, 1);
            }
            case 3: {
                if (objInfo != 3) {
                    throw new PropertyListFormatException("The given binary property list contains a date object of an unknown type (" + objInfo + ")");
                }
                return new NSDate(this.bytes, offset + 1, offset + 9);
            }
            case 4: {
                final int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
                final int length2 = lengthAndOffset[0];
                final int dataOffset = lengthAndOffset[1];
                return new NSData(copyOfRange(this.bytes, offset + dataOffset, offset + dataOffset + length2));
            }
            case 5: {
                final int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
                final int length2 = lengthAndOffset[0];
                final int strOffset = lengthAndOffset[1];
                return new NSString(this.bytes, offset + strOffset, offset + strOffset + length2, "ASCII");
            }
            case 6: {
                final int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
                final int characters = lengthAndOffset[0];
                final int strOffset = lengthAndOffset[1];
                final int length3 = characters * 2;
                return new NSString(this.bytes, offset + strOffset, offset + strOffset + length3, "UTF-16BE");
            }
            case 7: {
                final int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
                final int strOffset2 = lengthAndOffset[1];
                final int characters2 = lengthAndOffset[0];
                final int length3 = this.calculateUtf8StringLength(this.bytes, offset + strOffset2, characters2);
                return new NSString(this.bytes, offset + strOffset2, offset + strOffset2 + length3, "UTF-8");
            }
            case 8: {
                final int length = objInfo + 1;
                return new UID(String.valueOf(obj), copyOfRange(this.bytes, offset + 1, offset + 1 + length));
            }
            case 10: {
                final int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
                final int length2 = lengthAndOffset[0];
                final int arrayOffset = lengthAndOffset[1];
                final NSArray array = new NSArray(length2);
                for (int i = 0; i < length2; ++i) {
                    final int objRef = (int)parseUnsignedInt(this.bytes, offset + arrayOffset + i * this.objectRefSize, offset + arrayOffset + (i + 1) * this.objectRefSize);
                    array.setValue(i, this.parseObject(objRef));
                }
                return array;
            }
            case 11: {
                final int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
                final int length2 = lengthAndOffset[0];
                final int contentOffset = lengthAndOffset[1];
                final NSSet set = new NSSet(true);
                for (int i = 0; i < length2; ++i) {
                    final int objRef = (int)parseUnsignedInt(this.bytes, offset + contentOffset + i * this.objectRefSize, offset + contentOffset + (i + 1) * this.objectRefSize);
                    set.addObject(this.parseObject(objRef));
                }
                return set;
            }
            case 12: {
                final int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
                final int length2 = lengthAndOffset[0];
                final int contentOffset = lengthAndOffset[1];
                final NSSet set = new NSSet();
                for (int i = 0; i < length2; ++i) {
                    final int objRef = (int)parseUnsignedInt(this.bytes, offset + contentOffset + i * this.objectRefSize, offset + contentOffset + (i + 1) * this.objectRefSize);
                    set.addObject(this.parseObject(objRef));
                }
                return set;
            }
            case 13: {
                final int[] lengthAndOffset = this.readLengthAndOffset(objInfo, offset);
                final int length2 = lengthAndOffset[0];
                final int contentOffset = lengthAndOffset[1];
                final NSDictionary dict = new NSDictionary();
                for (int i = 0; i < length2; ++i) {
                    final int keyRef = (int)parseUnsignedInt(this.bytes, offset + contentOffset + i * this.objectRefSize, offset + contentOffset + (i + 1) * this.objectRefSize);
                    final int valRef = (int)parseUnsignedInt(this.bytes, offset + contentOffset + length2 * this.objectRefSize + i * this.objectRefSize, offset + contentOffset + length2 * this.objectRefSize + (i + 1) * this.objectRefSize);
                    final NSObject key = this.parseObject(keyRef);
                    final NSObject val = this.parseObject(valRef);
                    assert key != null;
                    dict.put(key.toString(), val);
                }
                return dict;
            }
            default: {
                throw new PropertyListFormatException("The given binary property list contains an object of unknown type (" + objType + ")");
            }
        }
    }
    
    private int[] readLengthAndOffset(final int objInfo, final int offset) {
        int lengthValue = objInfo;
        int offsetValue = 1;
        if (objInfo == 15) {
            final int int_type = this.bytes[offset + 1];
            final int intType = (int_type & 0xF0) >> 4;
            if (intType != 1) {
                System.err.println("BinaryPropertyListParser: Length integer has an unexpected type" + intType + ". Attempting to parse anyway...");
            }
            final int intInfo = int_type & 0xF;
            final int intLength = (int)Math.pow(2.0, intInfo);
            offsetValue = 2 + intLength;
            if (intLength < 3) {
                lengthValue = (int)parseUnsignedInt(this.bytes, offset + 2, offset + 2 + intLength);
            }
            else {
                lengthValue = new BigInteger(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength)).intValue();
            }
        }
        return new int[] { lengthValue, offsetValue };
    }
    
    private int calculateUtf8StringLength(final byte[] bytes, final int offset, final int numCharacters) {
        int length = 0;
        for (int i = 0; i < numCharacters; ++i) {
            final int tempOffset = offset + length;
            if (bytes.length <= tempOffset) {
                return numCharacters;
            }
            if (bytes[tempOffset] < 128) {
                ++length;
            }
            if (bytes[tempOffset] < 194) {
                return numCharacters;
            }
            if (bytes[tempOffset] < 224) {
                if ((bytes[tempOffset + 1] & 0xC0) != 0x80) {
                    return numCharacters;
                }
                length += 2;
            }
            else if (bytes[tempOffset] < 240) {
                if ((bytes[tempOffset + 1] & 0xC0) != 0x80 || (bytes[tempOffset + 2] & 0xC0) != 0x80) {
                    return numCharacters;
                }
                length += 3;
            }
            else if (bytes[tempOffset] < 245) {
                if ((bytes[tempOffset + 1] & 0xC0) != 0x80 || (bytes[tempOffset + 2] & 0xC0) != 0x80 || (bytes[tempOffset + 3] & 0xC0) != 0x80) {
                    return numCharacters;
                }
                length += 4;
            }
        }
        return length;
    }
    
    public static long parseUnsignedInt(final byte[] bytes) {
        return parseUnsignedInt(bytes, 0, bytes.length);
    }
    
    public static long parseUnsignedInt(final byte[] bytes, final int startIndex, final int endIndex) {
        long l = 0L;
        for (int i = startIndex; i < endIndex; ++i) {
            l <<= 8;
            l |= (bytes[i] & 0xFF);
        }
        l &= 0xFFFFFFFFL;
        return l;
    }
    
    public static long parseLong(final byte[] bytes) {
        return parseLong(bytes, 0, bytes.length);
    }
    
    public static long parseLong(final byte[] bytes, final int startIndex, final int endIndex) {
        long l = 0L;
        for (int i = startIndex; i < endIndex; ++i) {
            l <<= 8;
            l |= (bytes[i] & 0xFF);
        }
        return l;
    }
    
    public static double parseDouble(final byte[] bytes) {
        return parseDouble(bytes, 0, bytes.length);
    }
    
    public static double parseDouble(final byte[] bytes, final int startIndex, final int endIndex) {
        if (endIndex - startIndex == 8) {
            return Double.longBitsToDouble(parseLong(bytes, startIndex, endIndex));
        }
        if (endIndex - startIndex == 4) {
            return Float.intBitsToFloat((int)parseLong(bytes, startIndex, endIndex));
        }
        throw new IllegalArgumentException("endIndex (" + endIndex + ") - startIndex (" + startIndex + ") != 4 or 8");
    }
    
    public static byte[] copyOfRange(final byte[] src, final int startIndex, final int endIndex) {
        final int length = endIndex - startIndex;
        if (length < 0) {
            throw new IllegalArgumentException("startIndex (" + startIndex + ")" + " > endIndex (" + endIndex + ")");
        }
        final byte[] dest = new byte[length];
        System.arraycopy(src, startIndex, dest, 0, length);
        return dest;
    }
}
