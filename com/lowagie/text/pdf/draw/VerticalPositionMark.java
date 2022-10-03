package com.lowagie.text.pdf.draw;

import com.lowagie.text.Chunk;
import java.util.ArrayList;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ElementListener;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.Element;

public class VerticalPositionMark implements DrawInterface, Element
{
    protected DrawInterface drawInterface;
    protected float offset;
    
    public VerticalPositionMark() {
        this.drawInterface = null;
        this.offset = 0.0f;
    }
    
    public VerticalPositionMark(final DrawInterface drawInterface, final float offset) {
        this.drawInterface = null;
        this.offset = 0.0f;
        this.drawInterface = drawInterface;
        this.offset = offset;
    }
    
    @Override
    public void draw(final PdfContentByte canvas, final float llx, final float lly, final float urx, final float ury, final float y) {
        if (this.drawInterface != null) {
            this.drawInterface.draw(canvas, llx, lly, urx, ury, y + this.offset);
        }
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (final DocumentException e) {
            return false;
        }
    }
    
    @Override
    public int type() {
        return 55;
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return false;
    }
    
    @Override
    public ArrayList getChunks() {
        final ArrayList list = new ArrayList();
        list.add(new Chunk(this, true));
        return list;
    }
    
    public DrawInterface getDrawInterface() {
        return this.drawInterface;
    }
    
    public void setDrawInterface(final DrawInterface drawInterface) {
        this.drawInterface = drawInterface;
    }
    
    public float getOffset() {
        return this.offset;
    }
    
    public void setOffset(final float offset) {
        this.offset = offset;
    }
}
