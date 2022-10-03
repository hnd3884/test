package org.apache.poi.xslf.util;

import java.util.Collections;
import org.apache.poi.sl.draw.EmbeddedExtractor;
import org.apache.poi.common.usermodel.GenericRecord;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Dimension2D;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.draw.BitmapImageRenderer;
import java.awt.Graphics2D;
import org.apache.poi.sl.draw.DrawPictureShape;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.util.Internal;

@Internal
class EMFHandler extends MFProxy
{
    private ImageRenderer imgr;
    private InputStream is;
    
    EMFHandler() {
        this.imgr = null;
    }
    
    public void parse(final File file) throws IOException {
        this.parse(this.is = file.toURI().toURL().openStream());
    }
    
    public void parse(final InputStream is) throws IOException {
        this.imgr = DrawPictureShape.getImageRenderer((Graphics2D)null, this.getContentType());
        if (this.imgr instanceof BitmapImageRenderer) {
            throw new PPTX2PNG.NoScratchpadException();
        }
        this.imgr.loadImage(is, this.getContentType());
        if (this.ignoreParse) {
            try {
                this.imgr.getDimension();
            }
            catch (final Exception ex) {}
        }
    }
    
    protected String getContentType() {
        return PictureData.PictureType.EMF.contentType;
    }
    
    public Dimension2D getSize() {
        return this.imgr.getDimension();
    }
    
    public String getTitle() {
        return "";
    }
    
    public void draw(final Graphics2D ctx) {
        final Dimension2D dim = this.getSize();
        this.imgr.drawImage(ctx, (Rectangle2D)new Rectangle2D.Double(0.0, 0.0, dim.getWidth(), dim.getHeight()));
    }
    
    @Override
    public void close() throws IOException {
        if (this.is != null) {
            try {
                this.is.close();
            }
            finally {
                this.is = null;
            }
        }
    }
    
    public GenericRecord getRoot() {
        return this.imgr.getGenericRecord();
    }
    
    public Iterable<EmbeddedExtractor.EmbeddedPart> getEmbeddings(final int slideNo) {
        return (Iterable<EmbeddedExtractor.EmbeddedPart>)((this.imgr instanceof EmbeddedExtractor) ? ((EmbeddedExtractor)this.imgr).getEmbeddings() : Collections.emptyList());
    }
}
