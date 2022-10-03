package com.google.zxing.qrcode.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;

final class BitMatrixParser
{
    private final BitMatrix bitMatrix;
    private Version parsedVersion;
    private FormatInformation parsedFormatInfo;
    
    BitMatrixParser(final BitMatrix bitMatrix) throws FormatException {
        final int dimension = bitMatrix.getHeight();
        if (dimension < 21 || (dimension & 0x3) != 0x1) {
            throw FormatException.getFormatInstance();
        }
        this.bitMatrix = bitMatrix;
    }
    
    FormatInformation readFormatInformation() throws FormatException {
        if (this.parsedFormatInfo != null) {
            return this.parsedFormatInfo;
        }
        int formatInfoBits1 = 0;
        for (int i = 0; i < 6; ++i) {
            formatInfoBits1 = this.copyBit(i, 8, formatInfoBits1);
        }
        formatInfoBits1 = this.copyBit(7, 8, formatInfoBits1);
        formatInfoBits1 = this.copyBit(8, 8, formatInfoBits1);
        formatInfoBits1 = this.copyBit(8, 7, formatInfoBits1);
        for (int j = 5; j >= 0; --j) {
            formatInfoBits1 = this.copyBit(8, j, formatInfoBits1);
        }
        final int dimension = this.bitMatrix.getHeight();
        int formatInfoBits2 = 0;
        for (int jMin = dimension - 7, k = dimension - 1; k >= jMin; --k) {
            formatInfoBits2 = this.copyBit(8, k, formatInfoBits2);
        }
        for (int l = dimension - 8; l < dimension; ++l) {
            formatInfoBits2 = this.copyBit(l, 8, formatInfoBits2);
        }
        this.parsedFormatInfo = FormatInformation.decodeFormatInformation(formatInfoBits1, formatInfoBits2);
        if (this.parsedFormatInfo != null) {
            return this.parsedFormatInfo;
        }
        throw FormatException.getFormatInstance();
    }
    
    Version readVersion() throws FormatException {
        if (this.parsedVersion != null) {
            return this.parsedVersion;
        }
        final int dimension = this.bitMatrix.getHeight();
        final int provisionalVersion = dimension - 17 >> 2;
        if (provisionalVersion <= 6) {
            return Version.getVersionForNumber(provisionalVersion);
        }
        int versionBits = 0;
        final int ijMin = dimension - 11;
        for (int j = 5; j >= 0; --j) {
            for (int i = dimension - 9; i >= ijMin; --i) {
                versionBits = this.copyBit(i, j, versionBits);
            }
        }
        Version theParsedVersion = Version.decodeVersionInformation(versionBits);
        if (theParsedVersion != null && theParsedVersion.getDimensionForVersion() == dimension) {
            return this.parsedVersion = theParsedVersion;
        }
        versionBits = 0;
        for (int i = 5; i >= 0; --i) {
            for (int k = dimension - 9; k >= ijMin; --k) {
                versionBits = this.copyBit(i, k, versionBits);
            }
        }
        theParsedVersion = Version.decodeVersionInformation(versionBits);
        if (theParsedVersion != null && theParsedVersion.getDimensionForVersion() == dimension) {
            return this.parsedVersion = theParsedVersion;
        }
        throw FormatException.getFormatInstance();
    }
    
    private int copyBit(final int i, final int j, final int versionBits) {
        return this.bitMatrix.get(i, j) ? (versionBits << 1 | 0x1) : (versionBits << 1);
    }
    
    byte[] readCodewords() throws FormatException {
        final FormatInformation formatInfo = this.readFormatInformation();
        final Version version = this.readVersion();
        final DataMask dataMask = DataMask.forReference(formatInfo.getDataMask());
        final int dimension = this.bitMatrix.getHeight();
        dataMask.unmaskBitMatrix(this.bitMatrix, dimension);
        final BitMatrix functionPattern = version.buildFunctionPattern();
        boolean readingUp = true;
        final byte[] result = new byte[version.getTotalCodewords()];
        int resultOffset = 0;
        int currentByte = 0;
        int bitsRead = 0;
        for (int j = dimension - 1; j > 0; j -= 2) {
            if (j == 6) {
                --j;
            }
            for (int count = 0; count < dimension; ++count) {
                final int i = readingUp ? (dimension - 1 - count) : count;
                for (int col = 0; col < 2; ++col) {
                    if (!functionPattern.get(j - col, i)) {
                        ++bitsRead;
                        currentByte <<= 1;
                        if (this.bitMatrix.get(j - col, i)) {
                            currentByte |= 0x1;
                        }
                        if (bitsRead == 8) {
                            result[resultOffset++] = (byte)currentByte;
                            bitsRead = 0;
                            currentByte = 0;
                        }
                    }
                }
            }
            readingUp ^= true;
        }
        if (resultOffset != version.getTotalCodewords()) {
            throw FormatException.getFormatInstance();
        }
        return result;
    }
}
