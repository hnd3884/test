package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import java.awt.color.ICC_Profile;

public class PdfICCBased extends PdfStream
{
    public PdfICCBased(final ICC_Profile profile) {
        this(profile, -1);
    }
    
    public PdfICCBased(final ICC_Profile profile, final int compressionLevel) {
        try {
            final int numberOfComponents = profile.getNumComponents();
            switch (numberOfComponents) {
                case 1: {
                    this.put(PdfName.ALTERNATE, PdfName.DEVICEGRAY);
                    break;
                }
                case 3: {
                    this.put(PdfName.ALTERNATE, PdfName.DEVICERGB);
                    break;
                }
                case 4: {
                    this.put(PdfName.ALTERNATE, PdfName.DEVICECMYK);
                    break;
                }
                default: {
                    throw new PdfException(MessageLocalization.getComposedMessage("1.component.s.is.not.supported", numberOfComponents));
                }
            }
            this.put(PdfName.N, new PdfNumber(numberOfComponents));
            this.bytes = profile.getData();
            this.flateCompress(compressionLevel);
        }
        catch (final Exception e) {
            throw new ExceptionConverter(e);
        }
    }
}
