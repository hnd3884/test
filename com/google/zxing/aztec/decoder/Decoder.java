package com.google.zxing.aztec.decoder;

import com.google.zxing.common.reedsolomon.ReedSolomonException;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;
import java.util.List;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.aztec.AztecDetectorResult;

public final class Decoder
{
    private static final int[] NB_BITS_COMPACT;
    private static final int[] NB_BITS;
    private static final int[] NB_DATABLOCK_COMPACT;
    private static final int[] NB_DATABLOCK;
    private static final String[] UPPER_TABLE;
    private static final String[] LOWER_TABLE;
    private static final String[] MIXED_TABLE;
    private static final String[] PUNCT_TABLE;
    private static final String[] DIGIT_TABLE;
    private int numCodewords;
    private int codewordSize;
    private AztecDetectorResult ddata;
    private int invertedBitCount;
    
    public DecoderResult decode(final AztecDetectorResult detectorResult) throws FormatException {
        this.ddata = detectorResult;
        BitMatrix matrix = detectorResult.getBits();
        if (!this.ddata.isCompact()) {
            matrix = removeDashedLines(this.ddata.getBits());
        }
        final boolean[] rawbits = this.extractBits(matrix);
        final boolean[] correctedBits = this.correctBits(rawbits);
        final String result = this.getEncodedData(correctedBits);
        return new DecoderResult(null, result, null, null);
    }
    
    private String getEncodedData(final boolean[] correctedBits) throws FormatException {
        final int endIndex = this.codewordSize * this.ddata.getNbDatablocks() - this.invertedBitCount;
        if (endIndex > correctedBits.length) {
            throw FormatException.getFormatInstance();
        }
        Table lastTable = Table.UPPER;
        Table table = Table.UPPER;
        int startIndex = 0;
        final StringBuilder result = new StringBuilder(20);
        boolean end = false;
        boolean shift = false;
        boolean switchShift = false;
        while (!end) {
            if (shift) {
                switchShift = true;
            }
            else {
                lastTable = table;
            }
            switch (table) {
                case BINARY: {
                    if (endIndex - startIndex < 8) {
                        end = true;
                        break;
                    }
                    final int code = readCode(correctedBits, startIndex, 8);
                    startIndex += 8;
                    result.append((char)code);
                    break;
                }
                default: {
                    int size = 5;
                    if (table == Table.DIGIT) {
                        size = 4;
                    }
                    if (endIndex - startIndex < size) {
                        end = true;
                        break;
                    }
                    final int code = readCode(correctedBits, startIndex, size);
                    startIndex += size;
                    final String str = getCharacter(table, code);
                    if (!str.startsWith("CTRL_")) {
                        result.append(str);
                        break;
                    }
                    table = getTable(str.charAt(5));
                    if (str.charAt(6) == 'S') {
                        shift = true;
                        break;
                    }
                    break;
                }
            }
            if (switchShift) {
                table = lastTable;
                shift = false;
                switchShift = false;
            }
        }
        return result.toString();
    }
    
    private static Table getTable(final char t) {
        switch (t) {
            case 'L': {
                return Table.LOWER;
            }
            case 'P': {
                return Table.PUNCT;
            }
            case 'M': {
                return Table.MIXED;
            }
            case 'D': {
                return Table.DIGIT;
            }
            case 'B': {
                return Table.BINARY;
            }
            default: {
                return Table.UPPER;
            }
        }
    }
    
    private static String getCharacter(final Table table, final int code) {
        switch (table) {
            case UPPER: {
                return Decoder.UPPER_TABLE[code];
            }
            case LOWER: {
                return Decoder.LOWER_TABLE[code];
            }
            case MIXED: {
                return Decoder.MIXED_TABLE[code];
            }
            case PUNCT: {
                return Decoder.PUNCT_TABLE[code];
            }
            case DIGIT: {
                return Decoder.DIGIT_TABLE[code];
            }
            default: {
                return "";
            }
        }
    }
    
    private boolean[] correctBits(final boolean[] rawbits) throws FormatException {
        GenericGF gf;
        if (this.ddata.getNbLayers() <= 2) {
            this.codewordSize = 6;
            gf = GenericGF.AZTEC_DATA_6;
        }
        else if (this.ddata.getNbLayers() <= 8) {
            this.codewordSize = 8;
            gf = GenericGF.AZTEC_DATA_8;
        }
        else if (this.ddata.getNbLayers() <= 22) {
            this.codewordSize = 10;
            gf = GenericGF.AZTEC_DATA_10;
        }
        else {
            this.codewordSize = 12;
            gf = GenericGF.AZTEC_DATA_12;
        }
        final int numDataCodewords = this.ddata.getNbDatablocks();
        int offset;
        int numECCodewords;
        if (this.ddata.isCompact()) {
            offset = Decoder.NB_BITS_COMPACT[this.ddata.getNbLayers()] - this.numCodewords * this.codewordSize;
            numECCodewords = Decoder.NB_DATABLOCK_COMPACT[this.ddata.getNbLayers()] - numDataCodewords;
        }
        else {
            offset = Decoder.NB_BITS[this.ddata.getNbLayers()] - this.numCodewords * this.codewordSize;
            numECCodewords = Decoder.NB_DATABLOCK[this.ddata.getNbLayers()] - numDataCodewords;
        }
        final int[] dataWords = new int[this.numCodewords];
        for (int i = 0; i < this.numCodewords; ++i) {
            int flag = 1;
            for (int j = 1; j <= this.codewordSize; ++j) {
                if (rawbits[this.codewordSize * i + this.codewordSize - j + offset]) {
                    final int[] array = dataWords;
                    final int n = i;
                    array[n] += flag;
                }
                flag <<= 1;
            }
        }
        try {
            final ReedSolomonDecoder rsDecoder = new ReedSolomonDecoder(gf);
            rsDecoder.decode(dataWords, numECCodewords);
        }
        catch (final ReedSolomonException rse) {
            throw FormatException.getFormatInstance();
        }
        offset = 0;
        this.invertedBitCount = 0;
        final boolean[] correctedBits = new boolean[numDataCodewords * this.codewordSize];
        for (int k = 0; k < numDataCodewords; ++k) {
            boolean seriesColor = false;
            int seriesCount = 0;
            int flag2 = 1 << this.codewordSize - 1;
            for (int l = 0; l < this.codewordSize; ++l) {
                final boolean color = (dataWords[k] & flag2) == flag2;
                if (seriesCount == this.codewordSize - 1) {
                    if (color == seriesColor) {
                        throw FormatException.getFormatInstance();
                    }
                    seriesColor = false;
                    seriesCount = 0;
                    ++offset;
                    ++this.invertedBitCount;
                }
                else {
                    if (seriesColor == color) {
                        ++seriesCount;
                    }
                    else {
                        seriesCount = 1;
                        seriesColor = color;
                    }
                    correctedBits[k * this.codewordSize + l - offset] = color;
                }
                flag2 >>>= 1;
            }
        }
        return correctedBits;
    }
    
    private boolean[] extractBits(final BitMatrix matrix) throws FormatException {
        boolean[] rawbits;
        if (this.ddata.isCompact()) {
            if (this.ddata.getNbLayers() > Decoder.NB_BITS_COMPACT.length) {
                throw FormatException.getFormatInstance();
            }
            rawbits = new boolean[Decoder.NB_BITS_COMPACT[this.ddata.getNbLayers()]];
            this.numCodewords = Decoder.NB_DATABLOCK_COMPACT[this.ddata.getNbLayers()];
        }
        else {
            if (this.ddata.getNbLayers() > Decoder.NB_BITS.length) {
                throw FormatException.getFormatInstance();
            }
            rawbits = new boolean[Decoder.NB_BITS[this.ddata.getNbLayers()]];
            this.numCodewords = Decoder.NB_DATABLOCK[this.ddata.getNbLayers()];
        }
        int layer = this.ddata.getNbLayers();
        int size = matrix.getHeight();
        int rawbitsOffset = 0;
        int matrixOffset = 0;
        while (layer != 0) {
            int flip = 0;
            for (int i = 0; i < 2 * size - 4; ++i) {
                rawbits[rawbitsOffset + i] = matrix.get(matrixOffset + flip, matrixOffset + i / 2);
                rawbits[rawbitsOffset + 2 * size - 4 + i] = matrix.get(matrixOffset + i / 2, matrixOffset + size - 1 - flip);
                flip = (flip + 1) % 2;
            }
            flip = 0;
            for (int i = 2 * size + 1; i > 5; --i) {
                rawbits[rawbitsOffset + 4 * size - 8 + (2 * size - i) + 1] = matrix.get(matrixOffset + size - 1 - flip, matrixOffset + i / 2 - 1);
                rawbits[rawbitsOffset + 6 * size - 12 + (2 * size - i) + 1] = matrix.get(matrixOffset + i / 2 - 1, matrixOffset + flip);
                flip = (flip + 1) % 2;
            }
            matrixOffset += 2;
            rawbitsOffset += 8 * size - 16;
            --layer;
            size -= 4;
        }
        return rawbits;
    }
    
    private static BitMatrix removeDashedLines(final BitMatrix matrix) {
        final int nbDashed = 1 + 2 * ((matrix.getWidth() - 1) / 2 / 16);
        final BitMatrix newMatrix = new BitMatrix(matrix.getWidth() - nbDashed, matrix.getHeight() - nbDashed);
        int nx = 0;
        for (int x = 0; x < matrix.getWidth(); ++x) {
            if ((matrix.getWidth() / 2 - x) % 16 != 0) {
                int ny = 0;
                for (int y = 0; y < matrix.getHeight(); ++y) {
                    if ((matrix.getWidth() / 2 - y) % 16 != 0) {
                        if (matrix.get(x, y)) {
                            newMatrix.set(nx, ny);
                        }
                        ++ny;
                    }
                }
                ++nx;
            }
        }
        return newMatrix;
    }
    
    private static int readCode(final boolean[] rawbits, final int startIndex, final int length) {
        int res = 0;
        for (int i = startIndex; i < startIndex + length; ++i) {
            res <<= 1;
            if (rawbits[i]) {
                ++res;
            }
        }
        return res;
    }
    
    static {
        NB_BITS_COMPACT = new int[] { 0, 104, 240, 408, 608 };
        NB_BITS = new int[] { 0, 128, 288, 480, 704, 960, 1248, 1568, 1920, 2304, 2720, 3168, 3648, 4160, 4704, 5280, 5888, 6528, 7200, 7904, 8640, 9408, 10208, 11040, 11904, 12800, 13728, 14688, 15680, 16704, 17760, 18848, 19968 };
        NB_DATABLOCK_COMPACT = new int[] { 0, 17, 40, 51, 76 };
        NB_DATABLOCK = new int[] { 0, 21, 48, 60, 88, 120, 156, 196, 240, 230, 272, 316, 364, 416, 470, 528, 588, 652, 720, 790, 864, 940, 1020, 920, 992, 1066, 1144, 1224, 1306, 1392, 1480, 1570, 1664 };
        UPPER_TABLE = new String[] { "CTRL_PS", " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "CTRL_LL", "CTRL_ML", "CTRL_DL", "CTRL_BS" };
        LOWER_TABLE = new String[] { "CTRL_PS", " ", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "CTRL_US", "CTRL_ML", "CTRL_DL", "CTRL_BS" };
        MIXED_TABLE = new String[] { "CTRL_PS", " ", "\u0001", "\u0002", "\u0003", "\u0004", "\u0005", "\u0006", "\u0007", "\b", "\t", "\n", "\u000b", "\f", "\r", "\u001b", "\u001c", "\u001d", "\u001e", "\u001f", "@", "\\", "^", "_", "`", "|", "~", "\u007f", "CTRL_LL", "CTRL_UL", "CTRL_PL", "CTRL_BS" };
        PUNCT_TABLE = new String[] { "", "\r", "\r\n", ". ", ", ", ": ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", ":", ";", "<", "=", ">", "?", "[", "]", "{", "}", "CTRL_UL" };
        DIGIT_TABLE = new String[] { "CTRL_PS", " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ",", ".", "CTRL_UL", "CTRL_US" };
    }
    
    private enum Table
    {
        UPPER, 
        LOWER, 
        MIXED, 
        DIGIT, 
        PUNCT, 
        BINARY;
    }
}
