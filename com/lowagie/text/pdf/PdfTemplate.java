package com.lowagie.text.pdf;

import java.io.IOException;
import com.lowagie.text.Rectangle;

public class PdfTemplate extends PdfContentByte
{
    public static final int TYPE_TEMPLATE = 1;
    public static final int TYPE_IMPORTED = 2;
    public static final int TYPE_PATTERN = 3;
    protected int type;
    protected PdfIndirectReference thisReference;
    protected PageResources pageResources;
    protected Rectangle bBox;
    protected PdfArray matrix;
    protected PdfTransparencyGroup group;
    protected PdfOCG layer;
    
    protected PdfTemplate() {
        super(null);
        this.bBox = new Rectangle(0.0f, 0.0f);
        this.type = 1;
    }
    
    PdfTemplate(final PdfWriter wr) {
        super(wr);
        this.bBox = new Rectangle(0.0f, 0.0f);
        this.type = 1;
        (this.pageResources = new PageResources()).addDefaultColor(wr.getDefaultColorspace());
        this.thisReference = this.writer.getPdfIndirectReference();
    }
    
    public static PdfTemplate createTemplate(final PdfWriter writer, final float width, final float height) {
        return createTemplate(writer, width, height, null);
    }
    
    static PdfTemplate createTemplate(final PdfWriter writer, final float width, final float height, final PdfName forcedName) {
        final PdfTemplate template = new PdfTemplate(writer);
        template.setWidth(width);
        template.setHeight(height);
        writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }
    
    public void setWidth(final float width) {
        this.bBox.setLeft(0.0f);
        this.bBox.setRight(width);
    }
    
    public void setHeight(final float height) {
        this.bBox.setBottom(0.0f);
        this.bBox.setTop(height);
    }
    
    public float getWidth() {
        return this.bBox.getWidth();
    }
    
    public float getHeight() {
        return this.bBox.getHeight();
    }
    
    public Rectangle getBoundingBox() {
        return this.bBox;
    }
    
    public void setBoundingBox(final Rectangle bBox) {
        this.bBox = bBox;
    }
    
    public void setLayer(final PdfOCG layer) {
        this.layer = layer;
    }
    
    public PdfOCG getLayer() {
        return this.layer;
    }
    
    public void setMatrix(final float a, final float b, final float c, final float d, final float e, final float f) {
        (this.matrix = new PdfArray()).add(new PdfNumber(a));
        this.matrix.add(new PdfNumber(b));
        this.matrix.add(new PdfNumber(c));
        this.matrix.add(new PdfNumber(d));
        this.matrix.add(new PdfNumber(e));
        this.matrix.add(new PdfNumber(f));
    }
    
    PdfArray getMatrix() {
        return this.matrix;
    }
    
    public PdfIndirectReference getIndirectReference() {
        if (this.thisReference == null) {
            this.thisReference = this.writer.getPdfIndirectReference();
        }
        return this.thisReference;
    }
    
    public void beginVariableText() {
        this.content.append("/Tx BMC ");
    }
    
    public void endVariableText() {
        this.content.append("EMC ");
    }
    
    PdfObject getResources() {
        return this.getPageResources().getResources();
    }
    
    PdfStream getFormXObject(final int compressionLevel) throws IOException {
        return new PdfFormXObject(this, compressionLevel);
    }
    
    @Override
    public PdfContentByte getDuplicate() {
        final PdfTemplate tpl = new PdfTemplate();
        tpl.writer = this.writer;
        tpl.pdf = this.pdf;
        tpl.thisReference = this.thisReference;
        tpl.pageResources = this.pageResources;
        tpl.bBox = new Rectangle(this.bBox);
        tpl.group = this.group;
        tpl.layer = this.layer;
        if (this.matrix != null) {
            tpl.matrix = new PdfArray(this.matrix);
        }
        tpl.separator = this.separator;
        return tpl;
    }
    
    public int getType() {
        return this.type;
    }
    
    @Override
    PageResources getPageResources() {
        return this.pageResources;
    }
    
    public PdfTransparencyGroup getGroup() {
        return this.group;
    }
    
    public void setGroup(final PdfTransparencyGroup group) {
        this.group = group;
    }
}
