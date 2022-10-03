package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.Image;
import java.awt.Color;
import com.lowagie.text.Rectangle;

public class BarcodeEANSUPP extends Barcode
{
    protected Barcode ean;
    protected Barcode supp;
    
    public BarcodeEANSUPP(final Barcode ean, final Barcode supp) {
        this.n = 8.0f;
        this.ean = ean;
        this.supp = supp;
    }
    
    @Override
    public Rectangle getBarcodeSize() {
        final Rectangle rect = this.ean.getBarcodeSize();
        rect.setRight(rect.getWidth() + this.supp.getBarcodeSize().getWidth() + this.n);
        return rect;
    }
    
    @Override
    public Rectangle placeBarcode(final PdfContentByte cb, final Color barColor, final Color textColor) {
        if (this.supp.getFont() != null) {
            this.supp.setBarHeight(this.ean.getBarHeight() + this.supp.getBaseline() - this.supp.getFont().getFontDescriptor(2, this.supp.getSize()));
        }
        else {
            this.supp.setBarHeight(this.ean.getBarHeight());
        }
        final Rectangle eanR = this.ean.getBarcodeSize();
        cb.saveState();
        this.ean.placeBarcode(cb, barColor, textColor);
        cb.restoreState();
        cb.saveState();
        cb.concatCTM(1.0f, 0.0f, 0.0f, 1.0f, eanR.getWidth() + this.n, eanR.getHeight() - this.ean.getBarHeight());
        this.supp.placeBarcode(cb, barColor, textColor);
        cb.restoreState();
        return this.getBarcodeSize();
    }
    
    @Override
    public Image createAwtImage(final Color foreground, final Color background) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("the.two.barcodes.must.be.composed.externally"));
    }
}
