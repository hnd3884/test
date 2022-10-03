package com.lowagie.text;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.codec.wmf.MetaDo;
import com.lowagie.text.pdf.PdfTemplate;
import java.io.InputStream;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.codec.wmf.InputMeta;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

public class ImgWMF extends Image
{
    ImgWMF(final Image image) {
        super(image);
    }
    
    public ImgWMF(final URL url) throws BadElementException, IOException {
        super(url);
        this.processParameters();
    }
    
    public ImgWMF(final String filename) throws BadElementException, IOException {
        this(Utilities.toURL(filename));
    }
    
    public ImgWMF(final byte[] img) throws BadElementException, IOException {
        super((URL)null);
        this.rawData = img;
        this.originalData = img;
        this.processParameters();
    }
    
    private void processParameters() throws BadElementException, IOException {
        this.type = 35;
        this.originalType = 6;
        InputStream is = null;
        try {
            String errorID;
            if (this.rawData == null) {
                is = this.url.openStream();
                errorID = this.url.toString();
            }
            else {
                is = new ByteArrayInputStream(this.rawData);
                errorID = "Byte array";
            }
            final InputMeta in = new InputMeta(is);
            if (in.readInt() != -1698247209) {
                throw new BadElementException(MessageLocalization.getComposedMessage("1.is.not.a.valid.placeable.windows.metafile", errorID));
            }
            in.readWord();
            final int left = in.readShort();
            final int top = in.readShort();
            final int right = in.readShort();
            final int bottom = in.readShort();
            final int inch = in.readWord();
            this.dpiX = 72;
            this.dpiY = 72;
            this.setTop(this.scaledHeight = (bottom - top) / (float)inch * 72.0f);
            this.setRight(this.scaledWidth = (right - left) / (float)inch * 72.0f);
        }
        finally {
            if (is != null) {
                is.close();
            }
            this.plainWidth = this.getWidth();
            this.plainHeight = this.getHeight();
        }
    }
    
    public void readWMF(final PdfTemplate template) throws IOException, DocumentException {
        this.setTemplateData(template);
        template.setWidth(this.getWidth());
        template.setHeight(this.getHeight());
        InputStream is = null;
        try {
            if (this.rawData == null) {
                is = this.url.openStream();
            }
            else {
                is = new ByteArrayInputStream(this.rawData);
            }
            final MetaDo meta = new MetaDo(is, template);
            meta.readAll();
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
