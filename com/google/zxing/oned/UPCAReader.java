package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.common.BitArray;

public final class UPCAReader extends UPCEANReader
{
    private final UPCEANReader ean13Reader;
    
    public UPCAReader() {
        this.ean13Reader = new EAN13Reader();
    }
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final int[] startGuardRange, final Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException, ChecksumException {
        return maybeReturnResult(this.ean13Reader.decodeRow(rowNumber, row, startGuardRange, hints));
    }
    
    @Override
    public Result decodeRow(final int rowNumber, final BitArray row, final Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException, ChecksumException {
        return maybeReturnResult(this.ean13Reader.decodeRow(rowNumber, row, hints));
    }
    
    @Override
    public Result decode(final BinaryBitmap image) throws NotFoundException, FormatException {
        return maybeReturnResult(this.ean13Reader.decode(image));
    }
    
    @Override
    public Result decode(final BinaryBitmap image, final Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
        return maybeReturnResult(this.ean13Reader.decode(image, hints));
    }
    
    @Override
    BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.UPC_A;
    }
    
    @Override
    protected int decodeMiddle(final BitArray row, final int[] startRange, final StringBuilder resultString) throws NotFoundException {
        return this.ean13Reader.decodeMiddle(row, startRange, resultString);
    }
    
    private static Result maybeReturnResult(final Result result) throws FormatException {
        final String text = result.getText();
        if (text.charAt(0) == '0') {
            return new Result(text.substring(1), null, result.getResultPoints(), BarcodeFormat.UPC_A);
        }
        throw FormatException.getFormatInstance();
    }
}
