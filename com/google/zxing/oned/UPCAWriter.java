package com.google.zxing.oned;

import com.google.zxing.WriterException;
import com.google.zxing.EncodeHintType;
import java.util.Map;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;

public class UPCAWriter implements Writer
{
    private final EAN13Writer subWriter;
    
    public UPCAWriter() {
        this.subWriter = new EAN13Writer();
    }
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height) throws WriterException {
        return this.encode(contents, format, width, height, null);
    }
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height, final Map<EncodeHintType, ?> hints) throws WriterException {
        if (format != BarcodeFormat.UPC_A) {
            throw new IllegalArgumentException("Can only encode UPC-A, but got " + format);
        }
        return this.subWriter.encode(preencode(contents), BarcodeFormat.EAN_13, width, height, hints);
    }
    
    private static String preencode(String contents) {
        final int length = contents.length();
        if (length == 11) {
            int sum = 0;
            for (int i = 0; i < 11; ++i) {
                sum += (contents.charAt(i) - '0') * ((i % 2 == 0) ? 3 : 1);
            }
            contents += (1000 - sum) % 10;
        }
        else if (length != 12) {
            throw new IllegalArgumentException("Requested contents should be 11 or 12 digits long, but got " + contents.length());
        }
        return '0' + contents;
    }
}
