package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;

final class FastLz
{
    private static final int MAX_DISTANCE = 8191;
    private static final int MAX_FARDISTANCE = 73725;
    private static final int HASH_LOG = 13;
    private static final int HASH_SIZE = 8192;
    private static final int HASH_MASK = 8191;
    private static final int MAX_COPY = 32;
    private static final int MAX_LEN = 264;
    private static final int MIN_RECOMENDED_LENGTH_FOR_LEVEL_2 = 65536;
    static final int MAGIC_NUMBER = 4607066;
    static final byte BLOCK_TYPE_NON_COMPRESSED = 0;
    static final byte BLOCK_TYPE_COMPRESSED = 1;
    static final byte BLOCK_WITHOUT_CHECKSUM = 0;
    static final byte BLOCK_WITH_CHECKSUM = 16;
    static final int OPTIONS_OFFSET = 3;
    static final int CHECKSUM_OFFSET = 4;
    static final int MAX_CHUNK_LENGTH = 65535;
    static final int MIN_LENGTH_TO_COMPRESSION = 32;
    static final int LEVEL_AUTO = 0;
    static final int LEVEL_1 = 1;
    static final int LEVEL_2 = 2;
    
    static int calculateOutputBufferLength(final int inputLength) {
        final int outputLength = (int)(inputLength * 1.06);
        return Math.max(outputLength, 66);
    }
    
    static int compress(final ByteBuf input, final int inOffset, final int inLength, final ByteBuf output, final int outOffset, final int proposedLevel) {
        int level;
        if (proposedLevel == 0) {
            level = ((inLength < 65536) ? 1 : 2);
        }
        else {
            level = proposedLevel;
        }
        int ip = 0;
        int ipBound = ip + inLength - 2;
        final int ipLimit = ip + inLength - 12;
        int op = 0;
        final int[] htab = new int[8192];
        if (inLength >= 4) {
            for (int hslot = 0; hslot < 8192; ++hslot) {
                htab[hslot] = ip;
            }
            int copy = 2;
            output.setByte(outOffset + op++, 31);
            output.setByte(outOffset + op++, input.getByte(inOffset + ip++));
            output.setByte(outOffset + op++, input.getByte(inOffset + ip++));
            while (ip < ipLimit) {
                int ref = 0;
                long distance = 0L;
                int len = 3;
                int anchor = ip;
                boolean matchLabel = false;
                if (level == 2 && input.getByte(inOffset + ip) == input.getByte(inOffset + ip - 1) && readU16(input, inOffset + ip - 1) == readU16(input, inOffset + ip + 1)) {
                    distance = 1L;
                    ip += 3;
                    ref = anchor + 2;
                    matchLabel = true;
                }
                Label_0638: {
                    if (!matchLabel) {
                        final int hslot;
                        final int hval = hslot = hashFunction(input, inOffset + ip);
                        ref = htab[hval];
                        distance = anchor - ref;
                        htab[hslot] = anchor;
                        Label_0452: {
                            if (distance != 0L) {
                                if (level == 1) {
                                    if (distance >= 8191L) {
                                        break Label_0452;
                                    }
                                }
                                else if (distance >= 73725L) {
                                    break Label_0452;
                                }
                                if (input.getByte(inOffset + ref++) == input.getByte(inOffset + ip++) && input.getByte(inOffset + ref++) == input.getByte(inOffset + ip++) && input.getByte(inOffset + ref++) == input.getByte(inOffset + ip++)) {
                                    if (level != 2 || distance < 8191L) {
                                        break Label_0638;
                                    }
                                    if (input.getByte(inOffset + ip++) == input.getByte(inOffset + ref++) && input.getByte(inOffset + ip++) == input.getByte(inOffset + ref++)) {
                                        len += 2;
                                        break Label_0638;
                                    }
                                    output.setByte(outOffset + op++, input.getByte(inOffset + anchor++));
                                    ip = anchor;
                                    if (++copy == 32) {
                                        copy = 0;
                                        output.setByte(outOffset + op++, 31);
                                        continue;
                                    }
                                    continue;
                                }
                            }
                        }
                        output.setByte(outOffset + op++, input.getByte(inOffset + anchor++));
                        ip = anchor;
                        if (++copy == 32) {
                            copy = 0;
                            output.setByte(outOffset + op++, 31);
                            continue;
                        }
                        continue;
                    }
                }
                ip = anchor + len;
                --distance;
                if (distance == 0L) {
                    for (byte x = input.getByte(inOffset + ip - 1); ip < ipBound && input.getByte(inOffset + ref++) == x; ++ip) {}
                }
                else {
                    boolean missMatch = false;
                    for (int i = 0; i < 8; ++i) {
                        if (input.getByte(inOffset + ref++) != input.getByte(inOffset + ip++)) {
                            missMatch = true;
                            break;
                        }
                    }
                    if (!missMatch) {
                        while (ip < ipBound && input.getByte(inOffset + ref++) == input.getByte(inOffset + ip++)) {}
                    }
                }
                if (copy != 0) {
                    output.setByte(outOffset + op - copy - 1, (byte)(copy - 1));
                }
                else {
                    --op;
                }
                copy = 0;
                ip -= 3;
                len = ip - anchor;
                if (level == 2) {
                    if (distance < 8191L) {
                        if (len < 7) {
                            output.setByte(outOffset + op++, (byte)((len << 5) + (distance >>> 8)));
                            output.setByte(outOffset + op++, (byte)(distance & 0xFFL));
                        }
                        else {
                            output.setByte(outOffset + op++, (byte)(224L + (distance >>> 8)));
                            for (len -= 7; len >= 255; len -= 255) {
                                output.setByte(outOffset + op++, -1);
                            }
                            output.setByte(outOffset + op++, (byte)len);
                            output.setByte(outOffset + op++, (byte)(distance & 0xFFL));
                        }
                    }
                    else if (len < 7) {
                        distance -= 8191L;
                        output.setByte(outOffset + op++, (byte)((len << 5) + 31));
                        output.setByte(outOffset + op++, -1);
                        output.setByte(outOffset + op++, (byte)(distance >>> 8));
                        output.setByte(outOffset + op++, (byte)(distance & 0xFFL));
                    }
                    else {
                        distance -= 8191L;
                        output.setByte(outOffset + op++, -1);
                        for (len -= 7; len >= 255; len -= 255) {
                            output.setByte(outOffset + op++, -1);
                        }
                        output.setByte(outOffset + op++, (byte)len);
                        output.setByte(outOffset + op++, -1);
                        output.setByte(outOffset + op++, (byte)(distance >>> 8));
                        output.setByte(outOffset + op++, (byte)(distance & 0xFFL));
                    }
                }
                else {
                    if (len > 262) {
                        while (len > 262) {
                            output.setByte(outOffset + op++, (byte)(224L + (distance >>> 8)));
                            output.setByte(outOffset + op++, -3);
                            output.setByte(outOffset + op++, (byte)(distance & 0xFFL));
                            len -= 262;
                        }
                    }
                    if (len < 7) {
                        output.setByte(outOffset + op++, (byte)((len << 5) + (distance >>> 8)));
                        output.setByte(outOffset + op++, (byte)(distance & 0xFFL));
                    }
                    else {
                        output.setByte(outOffset + op++, (byte)(224L + (distance >>> 8)));
                        output.setByte(outOffset + op++, (byte)(len - 7));
                        output.setByte(outOffset + op++, (byte)(distance & 0xFFL));
                    }
                }
                int hval = hashFunction(input, inOffset + ip);
                htab[hval] = ip++;
                hval = hashFunction(input, inOffset + ip);
                htab[hval] = ip++;
                output.setByte(outOffset + op++, 31);
            }
            ++ipBound;
            while (ip <= ipBound) {
                output.setByte(outOffset + op++, input.getByte(inOffset + ip++));
                if (++copy == 32) {
                    copy = 0;
                    output.setByte(outOffset + op++, 31);
                }
            }
            if (copy != 0) {
                output.setByte(outOffset + op - copy - 1, (byte)(copy - 1));
            }
            else {
                --op;
            }
            if (level == 2) {
                output.setByte(outOffset, output.getByte(outOffset) | 0x20);
            }
            return op;
        }
        if (inLength != 0) {
            output.setByte(outOffset + op++, (byte)(inLength - 1));
            ++ipBound;
            while (ip <= ipBound) {
                output.setByte(outOffset + op++, input.getByte(inOffset + ip++));
            }
            return inLength + 1;
        }
        return 0;
    }
    
    static int decompress(final ByteBuf input, final int inOffset, final int inLength, final ByteBuf output, final int outOffset, final int outLength) {
        final int level = (input.getByte(inOffset) >> 5) + 1;
        if (level != 1 && level != 2) {
            throw new DecompressionException(String.format("invalid level: %d (expected: %d or %d)", level, 1, 2));
        }
        int ip = 0;
        int op = 0;
        long ctrl = input.getByte(inOffset + ip++) & 0x1F;
        int loop = 1;
        do {
            int ref = op;
            long len = ctrl >> 5;
            long ofs = (ctrl & 0x1FL) << 8;
            if (ctrl >= 32L) {
                --len;
                ref -= (int)ofs;
                if (len == 6L) {
                    if (level == 1) {
                        len += input.getUnsignedByte(inOffset + ip++);
                    }
                    else {
                        int code;
                        do {
                            code = input.getUnsignedByte(inOffset + ip++);
                            len += code;
                        } while (code == 255);
                    }
                }
                if (level == 1) {
                    ref -= input.getUnsignedByte(inOffset + ip++);
                }
                else {
                    final int code = input.getUnsignedByte(inOffset + ip++);
                    ref -= code;
                    if (code == 255 && ofs == 7936L) {
                        ofs = input.getUnsignedByte(inOffset + ip++) << 8;
                        ofs += input.getUnsignedByte(inOffset + ip++);
                        ref = (int)(op - ofs - 8191L);
                    }
                }
                if (op + len + 3L > outLength) {
                    return 0;
                }
                if (ref - 1 < 0) {
                    return 0;
                }
                if (ip < inLength) {
                    ctrl = input.getUnsignedByte(inOffset + ip++);
                }
                else {
                    loop = 0;
                }
                if (ref == op) {
                    final byte b = output.getByte(outOffset + ref - 1);
                    output.setByte(outOffset + op++, b);
                    output.setByte(outOffset + op++, b);
                    output.setByte(outOffset + op++, b);
                    while (len != 0L) {
                        output.setByte(outOffset + op++, b);
                        --len;
                    }
                }
                else {
                    --ref;
                    output.setByte(outOffset + op++, output.getByte(outOffset + ref++));
                    output.setByte(outOffset + op++, output.getByte(outOffset + ref++));
                    output.setByte(outOffset + op++, output.getByte(outOffset + ref++));
                    while (len != 0L) {
                        output.setByte(outOffset + op++, output.getByte(outOffset + ref++));
                        --len;
                    }
                }
            }
            else {
                ++ctrl;
                if (op + ctrl > outLength) {
                    return 0;
                }
                if (ip + ctrl > inLength) {
                    return 0;
                }
                output.setByte(outOffset + op++, input.getByte(inOffset + ip++));
                for (--ctrl; ctrl != 0L; --ctrl) {
                    output.setByte(outOffset + op++, input.getByte(inOffset + ip++));
                }
                loop = ((ip < inLength) ? 1 : 0);
                if (loop == 0) {
                    continue;
                }
                ctrl = input.getUnsignedByte(inOffset + ip++);
            }
        } while (loop != 0);
        return op;
    }
    
    private static int hashFunction(final ByteBuf p, final int offset) {
        int v = readU16(p, offset);
        v ^= (readU16(p, offset + 1) ^ v >> 3);
        v &= 0x1FFF;
        return v;
    }
    
    private static int readU16(final ByteBuf data, final int offset) {
        if (offset + 1 >= data.readableBytes()) {
            return data.getUnsignedByte(offset);
        }
        return data.getUnsignedByte(offset + 1) << 8 | data.getUnsignedByte(offset);
    }
    
    private FastLz() {
    }
}
