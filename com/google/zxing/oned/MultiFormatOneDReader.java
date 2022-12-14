package com.google.zxing.oned;

import com.google.zxing.Reader;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;
import com.google.zxing.oned.rss.expanded.RSSExpandedReader;
import com.google.zxing.oned.rss.RSS14Reader;
import com.google.zxing.BarcodeFormat;
import java.util.ArrayList;
import java.util.Collection;
import com.google.zxing.DecodeHintType;
import java.util.Map;

public final class MultiFormatOneDReader extends OneDReader
{
    private final OneDReader[] readers;
    
    public MultiFormatOneDReader(final Map<DecodeHintType, ?> hints) {
        final Collection<BarcodeFormat> possibleFormats = (hints == null) ? null : ((Collection)hints.get(DecodeHintType.POSSIBLE_FORMATS));
        final boolean useCode39CheckDigit = hints != null && hints.get(DecodeHintType.ASSUME_CODE_39_CHECK_DIGIT) != null;
        final Collection<OneDReader> readers = new ArrayList<OneDReader>();
        if (possibleFormats != null) {
            if (possibleFormats.contains(BarcodeFormat.EAN_13) || possibleFormats.contains(BarcodeFormat.UPC_A) || possibleFormats.contains(BarcodeFormat.EAN_8) || possibleFormats.contains(BarcodeFormat.UPC_E)) {
                readers.add(new MultiFormatUPCEANReader(hints));
            }
            if (possibleFormats.contains(BarcodeFormat.CODE_39)) {
                readers.add(new Code39Reader(useCode39CheckDigit));
            }
            if (possibleFormats.contains(BarcodeFormat.CODE_93)) {
                readers.add(new Code93Reader());
            }
            if (possibleFormats.contains(BarcodeFormat.CODE_128)) {
                readers.add(new Code128Reader());
            }
            if (possibleFormats.contains(BarcodeFormat.ITF)) {
                readers.add(new ITFReader());
            }
            if (possibleFormats.contains(BarcodeFormat.CODABAR)) {
                readers.add(new CodaBarReader());
            }
            if (possibleFormats.contains(BarcodeFormat.RSS_14)) {
                readers.add(new RSS14Reader());
            }
            if (possibleFormats.contains(BarcodeFormat.RSS_EXPANDED)) {
                readers.add(new RSSExpandedReader());
            }
        }
        if (readers.isEmpty()) {
            readers.add(new MultiFormatUPCEANReader(hints));
            readers.add(new Code39Reader());
            readers.add(new Code93Reader());
            readers.add(new Code128Reader());
            readers.add(new ITFReader());
            readers.add(new RSS14Reader());
            readers.add(new RSSExpandedReader());
        }
        this.readers = readers.toArray(new OneDReader[readers.size()]);
    }
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws NotFoundException {
        final OneDReader[] arr$ = this.readers;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final OneDReader reader = arr$[i$];
            try {
                return reader.decodeRow(rowNumber, row, hints);
            }
            catch (final ReaderException re) {
                ++i$;
                continue;
            }
            break;
        }
        throw NotFoundException.getNotFoundInstance();
    }
    
    @Override
    public void reset() {
        for (final Reader reader : this.readers) {
            reader.reset();
        }
    }
}
