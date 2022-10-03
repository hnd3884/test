package com.google.zxing.multi;

import com.google.zxing.FormatException;
import com.google.zxing.ChecksumException;
import com.google.zxing.NotFoundException;
import com.google.zxing.DecodeHintType;
import java.util.Map;
import com.google.zxing.Result;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.Reader;

public final class ByQuadrantReader implements Reader
{
    private final Reader delegate;
    
    public ByQuadrantReader(final Reader delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public Result decode(final BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
        return this.decode(image, null);
    }
    
    @Override
    public Result decode(final BinaryBitmap image, final Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int halfWidth = width / 2;
        final int halfHeight = height / 2;
        final BinaryBitmap topLeft = image.crop(0, 0, halfWidth, halfHeight);
        try {
            return this.delegate.decode(topLeft, hints);
        }
        catch (final NotFoundException re) {
            final BinaryBitmap topRight = image.crop(halfWidth, 0, halfWidth, halfHeight);
            try {
                return this.delegate.decode(topRight, hints);
            }
            catch (final NotFoundException re2) {
                final BinaryBitmap bottomLeft = image.crop(0, halfHeight, halfWidth, halfHeight);
                try {
                    return this.delegate.decode(bottomLeft, hints);
                }
                catch (final NotFoundException re3) {
                    final BinaryBitmap bottomRight = image.crop(halfWidth, halfHeight, halfWidth, halfHeight);
                    try {
                        return this.delegate.decode(bottomRight, hints);
                    }
                    catch (final NotFoundException re4) {
                        final int quarterWidth = halfWidth / 2;
                        final int quarterHeight = halfHeight / 2;
                        final BinaryBitmap center = image.crop(quarterWidth, quarterHeight, halfWidth, halfHeight);
                        return this.delegate.decode(center, hints);
                    }
                }
            }
        }
    }
    
    @Override
    public void reset() {
        this.delegate.reset();
    }
}
