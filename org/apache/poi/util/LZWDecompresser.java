package org.apache.poi.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public abstract class LZWDecompresser
{
    public static final int DICT_SIZE = 4096;
    public static final int DICT_MASK = 4095;
    private static final int MAX_RECORD_LENGTH = 1000000;
    private final boolean maskMeansCompressed;
    private final int codeLengthIncrease;
    private final boolean positionIsBigEndian;
    
    protected LZWDecompresser(final boolean maskMeansCompressed, final int codeLengthIncrease, final boolean positionIsBigEndian) {
        this.maskMeansCompressed = maskMeansCompressed;
        this.codeLengthIncrease = codeLengthIncrease;
        this.positionIsBigEndian = positionIsBigEndian;
    }
    
    protected abstract int populateDictionary(final byte[] p0);
    
    protected abstract int adjustDictionaryOffset(final int p0);
    
    public byte[] decompress(final InputStream src) throws IOException {
        final ByteArrayOutputStream res = new ByteArrayOutputStream();
        this.decompress(src, res);
        return res.toByteArray();
    }
    
    public void decompress(final InputStream src, final OutputStream res) throws IOException {
        final byte[] buffer = new byte[4096];
        int pos = this.populateDictionary(buffer);
        final byte[] dataB = IOUtils.safelyAllocate(16 + this.codeLengthIncrease, 1000000);
        int flag;
        while ((flag = src.read()) != -1) {
            for (int mask = 1; mask < 256; mask <<= 1) {
                final boolean isMaskSet = (flag & mask) > 0;
                if (isMaskSet ^ this.maskMeansCompressed) {
                    final int dataI;
                    if ((dataI = src.read()) != -1) {
                        buffer[pos++ & 0xFFF] = (byte)dataI;
                        res.write(dataI);
                    }
                }
                else {
                    final int dataIPt1 = src.read();
                    final int dataIPt2 = src.read();
                    if (dataIPt1 == -1) {
                        break;
                    }
                    if (dataIPt2 == -1) {
                        break;
                    }
                    final int len = (dataIPt2 & 0xF) + this.codeLengthIncrease;
                    int pntr;
                    if (this.positionIsBigEndian) {
                        pntr = (dataIPt1 << 4) + (dataIPt2 >>> 4);
                    }
                    else {
                        pntr = dataIPt1 + ((dataIPt2 & 0xF0) << 4);
                    }
                    pntr = this.adjustDictionaryOffset(pntr);
                    for (int i = 0; i < len; ++i) {
                        buffer[pos + i & 0xFFF] = (dataB[i] = buffer[pntr + i & 0xFFF]);
                    }
                    res.write(dataB, 0, len);
                    pos += len;
                }
            }
        }
    }
}
