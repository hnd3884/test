package com.google.zxing.oned;

import com.google.zxing.Reader;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.BitArray;
import com.google.zxing.BarcodeFormat;
import java.util.ArrayList;
import java.util.Collection;
import com.google.zxing.DecodeHintType;
import java.util.Map;

public final class MultiFormatUPCEANReader extends OneDReader
{
    private final UPCEANReader[] readers;
    
    public MultiFormatUPCEANReader(final Map<DecodeHintType, ?> hints) {
        final Collection<BarcodeFormat> possibleFormats = (hints == null) ? null : ((Collection)hints.get(DecodeHintType.POSSIBLE_FORMATS));
        final Collection<UPCEANReader> readers = new ArrayList<UPCEANReader>();
        if (possibleFormats != null) {
            if (possibleFormats.contains(BarcodeFormat.EAN_13)) {
                readers.add(new EAN13Reader());
            }
            else if (possibleFormats.contains(BarcodeFormat.UPC_A)) {
                readers.add(new UPCAReader());
            }
            if (possibleFormats.contains(BarcodeFormat.EAN_8)) {
                readers.add(new EAN8Reader());
            }
            if (possibleFormats.contains(BarcodeFormat.UPC_E)) {
                readers.add(new UPCEReader());
            }
        }
        if (readers.isEmpty()) {
            readers.add(new EAN13Reader());
            readers.add(new EAN8Reader());
            readers.add(new UPCEReader());
        }
        this.readers = readers.toArray(new UPCEANReader[readers.size()]);
    }
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws NotFoundException {
        final int[] startGuardPattern = UPCEANReader.findStartGuardPattern(row);
        final UPCEANReader[] arr$ = this.readers;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final UPCEANReader reader = arr$[i$];
            Result result = null;
            Label_0054: {
                Label_0172: {
                    try {
                        result = reader.decodeRow(rowNumber, row, startGuardPattern, hints);
                    }
                    catch (final ReaderException re) {
                        break Label_0172;
                    }
                    break Label_0054;
                }
                ++i$;
                continue;
            }
            final boolean ean13MayBeUPCA = result.getBarcodeFormat() == BarcodeFormat.EAN_13 && result.getText().charAt(0) == '0';
            final Collection<BarcodeFormat> possibleFormats = (hints == null) ? null : ((Collection)hints.get(DecodeHintType.POSSIBLE_FORMATS));
            final boolean canReturnUPCA = possibleFormats == null || possibleFormats.contains(BarcodeFormat.UPC_A);
            if (ean13MayBeUPCA && canReturnUPCA) {
                return new Result(result.getText().substring(1), null, result.getResultPoints(), BarcodeFormat.UPC_A);
            }
            return result;
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
