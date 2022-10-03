package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.Image;

public final class Type3Glyph extends PdfContentByte
{
    private PageResources pageResources;
    private boolean colorized;
    
    private Type3Glyph() {
        super(null);
    }
    
    Type3Glyph(final PdfWriter writer, final PageResources pageResources, final float wx, final float llx, final float lly, final float urx, final float ury, final boolean colorized) {
        super(writer);
        this.pageResources = pageResources;
        this.colorized = colorized;
        if (colorized) {
            this.content.append(wx).append(" 0 d0\n");
        }
        else {
            this.content.append(wx).append(" 0 ").append(llx).append(' ').append(lly).append(' ').append(urx).append(' ').append(ury).append(" d1\n");
        }
    }
    
    @Override
    PageResources getPageResources() {
        return this.pageResources;
    }
    
    @Override
    public void addImage(final Image image, final float a, final float b, final float c, final float d, final float e, final float f, final boolean inlineImage) throws DocumentException {
        if (!this.colorized && (!image.isMask() || (image.getBpc() != 1 && image.getBpc() <= 255))) {
            throw new DocumentException(MessageLocalization.getComposedMessage("not.colorized.typed3.fonts.only.accept.mask.images"));
        }
        super.addImage(image, a, b, c, d, e, f, inlineImage);
    }
    
    @Override
    public PdfContentByte getDuplicate() {
        final Type3Glyph dup = new Type3Glyph();
        dup.writer = this.writer;
        dup.pdf = this.pdf;
        dup.pageResources = this.pageResources;
        dup.colorized = this.colorized;
        return dup;
    }
}
