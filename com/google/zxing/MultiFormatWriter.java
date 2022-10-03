package com.google.zxing;

import com.google.zxing.oned.CodaBarWriter;
import com.google.zxing.pdf417.encoder.PDF417Writer;
import com.google.zxing.oned.ITFWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.Code39Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.oned.UPCAWriter;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.EAN8Writer;
import java.util.Map;
import com.google.zxing.common.BitMatrix;

public final class MultiFormatWriter implements Writer
{
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height) throws WriterException {
        return this.encode(contents, format, width, height, null);
    }
    
    @Override
    public BitMatrix encode(final String contents, final BarcodeFormat format, final int width, final int height, final Map<EncodeHintType, ?> hints) throws WriterException {
        Writer writer = null;
        switch (format) {
            case EAN_8: {
                writer = new EAN8Writer();
                break;
            }
            case EAN_13: {
                writer = new EAN13Writer();
                break;
            }
            case UPC_A: {
                writer = new UPCAWriter();
                break;
            }
            case QR_CODE: {
                writer = new QRCodeWriter();
                break;
            }
            case CODE_39: {
                writer = new Code39Writer();
                break;
            }
            case CODE_128: {
                writer = new Code128Writer();
                break;
            }
            case ITF: {
                writer = new ITFWriter();
                break;
            }
            case PDF_417: {
                writer = new PDF417Writer();
                break;
            }
            case CODABAR: {
                writer = new CodaBarWriter();
                break;
            }
            default: {
                throw new IllegalArgumentException("No encoder available for format " + format);
            }
        }
        return writer.encode(contents, format, width, height, hints);
    }
}
