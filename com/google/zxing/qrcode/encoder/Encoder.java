package com.google.zxing.qrcode.encoder;

import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import com.google.zxing.common.reedsolomon.GenericGF;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.google.zxing.qrcode.decoder.Version;
import java.io.UnsupportedEncodingException;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.qrcode.decoder.Mode;
import com.google.zxing.common.BitArray;
import com.google.zxing.WriterException;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public final class Encoder
{
    private static final int[] ALPHANUMERIC_TABLE;
    static final String DEFAULT_BYTE_MODE_ENCODING = "ISO-8859-1";
    
    private Encoder() {
    }
    
    private static int calculateMaskPenalty(final ByteMatrix matrix) {
        int penalty = 0;
        penalty += MaskUtil.applyMaskPenaltyRule1(matrix);
        penalty += MaskUtil.applyMaskPenaltyRule2(matrix);
        penalty += MaskUtil.applyMaskPenaltyRule3(matrix);
        penalty += MaskUtil.applyMaskPenaltyRule4(matrix);
        return penalty;
    }
    
    public static void encode(final String content, final ErrorCorrectionLevel ecLevel, final QRCode qrCode) throws WriterException {
        encode(content, ecLevel, null, qrCode);
    }
    
    public static void encode(final String content, final ErrorCorrectionLevel ecLevel, final Map<EncodeHintType, ?> hints, final QRCode qrCode) throws WriterException {
        String encoding = (hints == null) ? null : ((String)hints.get(EncodeHintType.CHARACTER_SET));
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }
        final Mode mode = chooseMode(content, encoding);
        final BitArray dataBits = new BitArray();
        appendBytes(content, mode, dataBits, encoding);
        final int numInputBits = dataBits.getSize();
        initQRCode(numInputBits, ecLevel, mode, qrCode);
        final BitArray headerAndDataBits = new BitArray();
        if (mode == Mode.BYTE && !"ISO-8859-1".equals(encoding)) {
            final CharacterSetECI eci = CharacterSetECI.getCharacterSetECIByName(encoding);
            if (eci != null) {
                appendECI(eci, headerAndDataBits);
            }
        }
        appendModeInfo(mode, headerAndDataBits);
        final int numLetters = (mode == Mode.BYTE) ? dataBits.getSizeInBytes() : content.length();
        appendLengthInfo(numLetters, qrCode.getVersion(), mode, headerAndDataBits);
        headerAndDataBits.appendBitArray(dataBits);
        terminateBits(qrCode.getNumDataBytes(), headerAndDataBits);
        final BitArray finalBits = new BitArray();
        interleaveWithECBytes(headerAndDataBits, qrCode.getNumTotalBytes(), qrCode.getNumDataBytes(), qrCode.getNumRSBlocks(), finalBits);
        final ByteMatrix matrix = new ByteMatrix(qrCode.getMatrixWidth(), qrCode.getMatrixWidth());
        qrCode.setMaskPattern(chooseMaskPattern(finalBits, ecLevel, qrCode.getVersion(), matrix));
        MatrixUtil.buildMatrix(finalBits, ecLevel, qrCode.getVersion(), qrCode.getMaskPattern(), matrix);
        qrCode.setMatrix(matrix);
        if (!qrCode.isValid()) {
            throw new WriterException("Invalid QR code: " + qrCode.toString());
        }
    }
    
    static int getAlphanumericCode(final int code) {
        if (code < Encoder.ALPHANUMERIC_TABLE.length) {
            return Encoder.ALPHANUMERIC_TABLE[code];
        }
        return -1;
    }
    
    public static Mode chooseMode(final String content) {
        return chooseMode(content, null);
    }
    
    private static Mode chooseMode(final String content, final String encoding) {
        if ("Shift_JIS".equals(encoding)) {
            return isOnlyDoubleByteKanji(content) ? Mode.KANJI : Mode.BYTE;
        }
        boolean hasNumeric = false;
        boolean hasAlphanumeric = false;
        for (int i = 0; i < content.length(); ++i) {
            final char c = content.charAt(i);
            if (c >= '0' && c <= '9') {
                hasNumeric = true;
            }
            else {
                if (getAlphanumericCode(c) == -1) {
                    return Mode.BYTE;
                }
                hasAlphanumeric = true;
            }
        }
        if (hasAlphanumeric) {
            return Mode.ALPHANUMERIC;
        }
        if (hasNumeric) {
            return Mode.NUMERIC;
        }
        return Mode.BYTE;
    }
    
    private static boolean isOnlyDoubleByteKanji(final String content) {
        byte[] bytes;
        try {
            bytes = content.getBytes("Shift_JIS");
        }
        catch (final UnsupportedEncodingException uee) {
            return false;
        }
        final int length = bytes.length;
        if (length % 2 != 0) {
            return false;
        }
        for (int i = 0; i < length; i += 2) {
            final int byte1 = bytes[i] & 0xFF;
            if ((byte1 < 129 || byte1 > 159) && (byte1 < 224 || byte1 > 235)) {
                return false;
            }
        }
        return true;
    }
    
    private static int chooseMaskPattern(final BitArray bits, final ErrorCorrectionLevel ecLevel, final int version, final ByteMatrix matrix) throws WriterException {
        int minPenalty = Integer.MAX_VALUE;
        int bestMaskPattern = -1;
        for (int maskPattern = 0; maskPattern < 8; ++maskPattern) {
            MatrixUtil.buildMatrix(bits, ecLevel, version, maskPattern, matrix);
            final int penalty = calculateMaskPenalty(matrix);
            if (penalty < minPenalty) {
                minPenalty = penalty;
                bestMaskPattern = maskPattern;
            }
        }
        return bestMaskPattern;
    }
    
    private static void initQRCode(final int numInputBits, final ErrorCorrectionLevel ecLevel, final Mode mode, final QRCode qrCode) throws WriterException {
        qrCode.setECLevel(ecLevel);
        qrCode.setMode(mode);
        for (int versionNum = 1; versionNum <= 40; ++versionNum) {
            final Version version = Version.getVersionForNumber(versionNum);
            final int numBytes = version.getTotalCodewords();
            final Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
            final int numEcBytes = ecBlocks.getTotalECCodewords();
            final int numRSBlocks = ecBlocks.getNumBlocks();
            final int numDataBytes = numBytes - numEcBytes;
            if (numDataBytes >= getTotalInputBytes(numInputBits, version, mode)) {
                qrCode.setVersion(versionNum);
                qrCode.setNumTotalBytes(numBytes);
                qrCode.setNumDataBytes(numDataBytes);
                qrCode.setNumRSBlocks(numRSBlocks);
                qrCode.setNumECBytes(numEcBytes);
                qrCode.setMatrixWidth(version.getDimensionForVersion());
                return;
            }
        }
        throw new WriterException("Cannot find proper rs block info (input data too big?)");
    }
    
    private static int getTotalInputBytes(final int numInputBits, final Version version, final Mode mode) {
        final int modeInfoBits = 4;
        final int charCountBits = mode.getCharacterCountBits(version);
        final int headerBits = modeInfoBits + charCountBits;
        final int totalBits = numInputBits + headerBits;
        return (totalBits + 7) / 8;
    }
    
    static void terminateBits(final int numDataBytes, final BitArray bits) throws WriterException {
        final int capacity = numDataBytes << 3;
        if (bits.getSize() > capacity) {
            throw new WriterException("data bits cannot fit in the QR Code" + bits.getSize() + " > " + capacity);
        }
        for (int i = 0; i < 4 && bits.getSize() < capacity; ++i) {
            bits.appendBit(false);
        }
        final int numBitsInLastByte = bits.getSize() & 0x7;
        if (numBitsInLastByte > 0) {
            for (int j = numBitsInLastByte; j < 8; ++j) {
                bits.appendBit(false);
            }
        }
        for (int numPaddingBytes = numDataBytes - bits.getSizeInBytes(), k = 0; k < numPaddingBytes; ++k) {
            bits.appendBits(((k & 0x1) == 0x0) ? 236 : 17, 8);
        }
        if (bits.getSize() != capacity) {
            throw new WriterException("Bits size does not equal capacity");
        }
    }
    
    static void getNumDataBytesAndNumECBytesForBlockID(final int numTotalBytes, final int numDataBytes, final int numRSBlocks, final int blockID, final int[] numDataBytesInBlock, final int[] numECBytesInBlock) throws WriterException {
        if (blockID >= numRSBlocks) {
            throw new WriterException("Block ID too large");
        }
        final int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
        final int numRsBlocksInGroup3 = numRSBlocks - numRsBlocksInGroup2;
        final int numTotalBytesInGroup1 = numTotalBytes / numRSBlocks;
        final int numTotalBytesInGroup2 = numTotalBytesInGroup1 + 1;
        final int numDataBytesInGroup1 = numDataBytes / numRSBlocks;
        final int numDataBytesInGroup2 = numDataBytesInGroup1 + 1;
        final int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
        final int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
        if (numEcBytesInGroup1 != numEcBytesInGroup2) {
            throw new WriterException("EC bytes mismatch");
        }
        if (numRSBlocks != numRsBlocksInGroup3 + numRsBlocksInGroup2) {
            throw new WriterException("RS blocks mismatch");
        }
        if (numTotalBytes != (numDataBytesInGroup1 + numEcBytesInGroup1) * numRsBlocksInGroup3 + (numDataBytesInGroup2 + numEcBytesInGroup2) * numRsBlocksInGroup2) {
            throw new WriterException("Total bytes mismatch");
        }
        if (blockID < numRsBlocksInGroup3) {
            numDataBytesInBlock[0] = numDataBytesInGroup1;
            numECBytesInBlock[0] = numEcBytesInGroup1;
        }
        else {
            numDataBytesInBlock[0] = numDataBytesInGroup2;
            numECBytesInBlock[0] = numEcBytesInGroup2;
        }
    }
    
    static void interleaveWithECBytes(final BitArray bits, final int numTotalBytes, final int numDataBytes, final int numRSBlocks, final BitArray result) throws WriterException {
        if (bits.getSizeInBytes() != numDataBytes) {
            throw new WriterException("Number of bits and data bytes does not match");
        }
        int dataBytesOffset = 0;
        int maxNumDataBytes = 0;
        int maxNumEcBytes = 0;
        final Collection<BlockPair> blocks = new ArrayList<BlockPair>(numRSBlocks);
        for (int i = 0; i < numRSBlocks; ++i) {
            final int[] numDataBytesInBlock = { 0 };
            final int[] numEcBytesInBlock = { 0 };
            getNumDataBytesAndNumECBytesForBlockID(numTotalBytes, numDataBytes, numRSBlocks, i, numDataBytesInBlock, numEcBytesInBlock);
            final int size = numDataBytesInBlock[0];
            final byte[] dataBytes = new byte[size];
            bits.toBytes(8 * dataBytesOffset, dataBytes, 0, size);
            final byte[] ecBytes = generateECBytes(dataBytes, numEcBytesInBlock[0]);
            blocks.add(new BlockPair(dataBytes, ecBytes));
            maxNumDataBytes = Math.max(maxNumDataBytes, size);
            maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes.length);
            dataBytesOffset += numDataBytesInBlock[0];
        }
        if (numDataBytes != dataBytesOffset) {
            throw new WriterException("Data bytes does not match offset");
        }
        for (int i = 0; i < maxNumDataBytes; ++i) {
            for (final BlockPair block : blocks) {
                final byte[] dataBytes2 = block.getDataBytes();
                if (i < dataBytes2.length) {
                    result.appendBits(dataBytes2[i], 8);
                }
            }
        }
        for (int i = 0; i < maxNumEcBytes; ++i) {
            for (final BlockPair block : blocks) {
                final byte[] ecBytes2 = block.getErrorCorrectionBytes();
                if (i < ecBytes2.length) {
                    result.appendBits(ecBytes2[i], 8);
                }
            }
        }
        if (numTotalBytes != result.getSizeInBytes()) {
            throw new WriterException("Interleaving error: " + numTotalBytes + " and " + result.getSizeInBytes() + " differ.");
        }
    }
    
    static byte[] generateECBytes(final byte[] dataBytes, final int numEcBytesInBlock) {
        final int numDataBytes = dataBytes.length;
        final int[] toEncode = new int[numDataBytes + numEcBytesInBlock];
        for (int i = 0; i < numDataBytes; ++i) {
            toEncode[i] = (dataBytes[i] & 0xFF);
        }
        new ReedSolomonEncoder(GenericGF.QR_CODE_FIELD_256).encode(toEncode, numEcBytesInBlock);
        final byte[] ecBytes = new byte[numEcBytesInBlock];
        for (int j = 0; j < numEcBytesInBlock; ++j) {
            ecBytes[j] = (byte)toEncode[numDataBytes + j];
        }
        return ecBytes;
    }
    
    static void appendModeInfo(final Mode mode, final BitArray bits) {
        bits.appendBits(mode.getBits(), 4);
    }
    
    static void appendLengthInfo(final int numLetters, final int version, final Mode mode, final BitArray bits) throws WriterException {
        final int numBits = mode.getCharacterCountBits(Version.getVersionForNumber(version));
        if (numLetters > (1 << numBits) - 1) {
            throw new WriterException(numLetters + "is bigger than" + ((1 << numBits) - 1));
        }
        bits.appendBits(numLetters, numBits);
    }
    
    static void appendBytes(final String content, final Mode mode, final BitArray bits, final String encoding) throws WriterException {
        switch (mode) {
            case NUMERIC: {
                appendNumericBytes(content, bits);
                break;
            }
            case ALPHANUMERIC: {
                appendAlphanumericBytes(content, bits);
                break;
            }
            case BYTE: {
                append8BitBytes(content, bits, encoding);
                break;
            }
            case KANJI: {
                appendKanjiBytes(content, bits);
                break;
            }
            default: {
                throw new WriterException("Invalid mode: " + mode);
            }
        }
    }
    
    static void appendNumericBytes(final CharSequence content, final BitArray bits) {
        final int length = content.length();
        int i = 0;
        while (i < length) {
            final int num1 = content.charAt(i) - '0';
            if (i + 2 < length) {
                final int num2 = content.charAt(i + 1) - '0';
                final int num3 = content.charAt(i + 2) - '0';
                bits.appendBits(num1 * 100 + num2 * 10 + num3, 10);
                i += 3;
            }
            else if (i + 1 < length) {
                final int num2 = content.charAt(i + 1) - '0';
                bits.appendBits(num1 * 10 + num2, 7);
                i += 2;
            }
            else {
                bits.appendBits(num1, 4);
                ++i;
            }
        }
    }
    
    static void appendAlphanumericBytes(final CharSequence content, final BitArray bits) throws WriterException {
        final int length = content.length();
        int i = 0;
        while (i < length) {
            final int code1 = getAlphanumericCode(content.charAt(i));
            if (code1 == -1) {
                throw new WriterException();
            }
            if (i + 1 < length) {
                final int code2 = getAlphanumericCode(content.charAt(i + 1));
                if (code2 == -1) {
                    throw new WriterException();
                }
                bits.appendBits(code1 * 45 + code2, 11);
                i += 2;
            }
            else {
                bits.appendBits(code1, 6);
                ++i;
            }
        }
    }
    
    static void append8BitBytes(final String content, final BitArray bits, final String encoding) throws WriterException {
        byte[] bytes;
        try {
            bytes = content.getBytes(encoding);
        }
        catch (final UnsupportedEncodingException uee) {
            throw new WriterException(uee.toString());
        }
        for (final byte b : bytes) {
            bits.appendBits(b, 8);
        }
    }
    
    static void appendKanjiBytes(final String content, final BitArray bits) throws WriterException {
        byte[] bytes;
        try {
            bytes = content.getBytes("Shift_JIS");
        }
        catch (final UnsupportedEncodingException uee) {
            throw new WriterException(uee.toString());
        }
        for (int length = bytes.length, i = 0; i < length; i += 2) {
            final int byte1 = bytes[i] & 0xFF;
            final int byte2 = bytes[i + 1] & 0xFF;
            final int code = byte1 << 8 | byte2;
            int subtracted = -1;
            if (code >= 33088 && code <= 40956) {
                subtracted = code - 33088;
            }
            else if (code >= 57408 && code <= 60351) {
                subtracted = code - 49472;
            }
            if (subtracted == -1) {
                throw new WriterException("Invalid byte sequence");
            }
            final int encoded = (subtracted >> 8) * 192 + (subtracted & 0xFF);
            bits.appendBits(encoded, 13);
        }
    }
    
    private static void appendECI(final CharacterSetECI eci, final BitArray bits) {
        bits.appendBits(Mode.ECI.getBits(), 4);
        bits.appendBits(eci.getValue(), 8);
    }
    
    static {
        ALPHANUMERIC_TABLE = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 44, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1 };
    }
}
