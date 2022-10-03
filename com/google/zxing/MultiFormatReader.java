package com.google.zxing;

import com.google.zxing.maxicode.MaxiCodeReader;
import com.google.zxing.pdf417.PDF417Reader;
import com.google.zxing.aztec.AztecReader;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.oned.MultiFormatOneDReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public final class MultiFormatReader implements Reader
{
    private Map<DecodeHintType, ?> hints;
    private Reader[] readers;
    
    @Override
    public Result decode(final BinaryBitmap image) throws NotFoundException {
        this.setHints(null);
        return this.decodeInternal(image);
    }
    
    @Override
    public Result decode(final BinaryBitmap image, final Map<DecodeHintType, ?> hints) throws NotFoundException {
        this.setHints(hints);
        return this.decodeInternal(image);
    }
    
    public Result decodeWithState(final BinaryBitmap image) throws NotFoundException {
        if (this.readers == null) {
            this.setHints(null);
        }
        return this.decodeInternal(image);
    }
    
    public void setHints(final Map<DecodeHintType, ?> hints) {
        this.hints = hints;
        final boolean tryHarder = hints != null && hints.containsKey(DecodeHintType.TRY_HARDER);
        final Collection<BarcodeFormat> formats = (hints == null) ? null : ((Collection)hints.get(DecodeHintType.POSSIBLE_FORMATS));
        final Collection<Reader> readers = new ArrayList<Reader>();
        if (formats != null) {
            final boolean addOneDReader = formats.contains(BarcodeFormat.UPC_A) || formats.contains(BarcodeFormat.UPC_E) || formats.contains(BarcodeFormat.EAN_13) || formats.contains(BarcodeFormat.EAN_8) || formats.contains(BarcodeFormat.CODE_39) || formats.contains(BarcodeFormat.CODE_93) || formats.contains(BarcodeFormat.CODE_128) || formats.contains(BarcodeFormat.ITF) || formats.contains(BarcodeFormat.RSS_14) || formats.contains(BarcodeFormat.RSS_EXPANDED);
            if (addOneDReader && !tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }
            if (formats.contains(BarcodeFormat.QR_CODE)) {
                readers.add(new QRCodeReader());
            }
            if (formats.contains(BarcodeFormat.DATA_MATRIX)) {
                readers.add(new DataMatrixReader());
            }
            if (formats.contains(BarcodeFormat.AZTEC)) {
                readers.add(new AztecReader());
            }
            if (formats.contains(BarcodeFormat.PDF_417)) {
                readers.add(new PDF417Reader());
            }
            if (formats.contains(BarcodeFormat.MAXICODE)) {
                readers.add(new MaxiCodeReader());
            }
            if (addOneDReader && tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }
        }
        if (readers.isEmpty()) {
            if (!tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }
            readers.add(new QRCodeReader());
            readers.add(new DataMatrixReader());
            readers.add(new AztecReader());
            readers.add(new PDF417Reader());
            readers.add(new MaxiCodeReader());
            if (tryHarder) {
                readers.add(new MultiFormatOneDReader(hints));
            }
        }
        this.readers = readers.toArray(new Reader[readers.size()]);
    }
    
    @Override
    public void reset() {
        if (this.readers != null) {
            for (final Reader reader : this.readers) {
                reader.reset();
            }
        }
    }
    
    private Result decodeInternal(final BinaryBitmap image) throws NotFoundException {
        if (this.readers != null) {
            final Reader[] arr$ = this.readers;
            final int len$ = arr$.length;
            int i$ = 0;
            while (i$ < len$) {
                final Reader reader = arr$[i$];
                try {
                    return reader.decode(image, this.hints);
                }
                catch (final ReaderException re) {
                    ++i$;
                    continue;
                }
                break;
            }
        }
        throw NotFoundException.getNotFoundInstance();
    }
}
