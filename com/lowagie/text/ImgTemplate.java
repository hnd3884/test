package com.lowagie.text;

import com.lowagie.text.error_messages.MessageLocalization;
import java.net.URL;
import com.lowagie.text.pdf.PdfTemplate;

public class ImgTemplate extends Image
{
    ImgTemplate(final Image image) {
        super(image);
    }
    
    public ImgTemplate(final PdfTemplate template) throws BadElementException {
        super((URL)null);
        if (template == null) {
            throw new BadElementException(MessageLocalization.getComposedMessage("the.template.can.not.be.null"));
        }
        if (template.getType() == 3) {
            throw new BadElementException(MessageLocalization.getComposedMessage("a.pattern.can.not.be.used.as.a.template.to.create.an.image"));
        }
        this.type = 35;
        this.setTop(this.scaledHeight = template.getHeight());
        this.setRight(this.scaledWidth = template.getWidth());
        this.setTemplateData(template);
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
    }
}
