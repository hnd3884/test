package com.lowagie.text.pdf;

import java.awt.Color;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.Iterator;
import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import java.util.ArrayList;

public class VerticalText
{
    public static final int NO_MORE_TEXT = 1;
    public static final int NO_MORE_COLUMN = 2;
    protected ArrayList chunks;
    protected PdfContentByte text;
    protected int alignment;
    protected int currentChunkMarker;
    protected PdfChunk currentStandbyChunk;
    protected String splittedChunkText;
    protected float leading;
    protected float startX;
    protected float startY;
    protected int maxLines;
    protected float height;
    
    public VerticalText(final PdfContentByte text) {
        this.chunks = new ArrayList();
        this.alignment = 0;
        this.currentChunkMarker = -1;
        this.text = text;
    }
    
    public void addText(final Phrase phrase) {
        final Iterator j = phrase.getChunks().iterator();
        while (j.hasNext()) {
            this.chunks.add(new PdfChunk(j.next(), null));
        }
    }
    
    public void addText(final Chunk chunk) {
        this.chunks.add(new PdfChunk(chunk, null));
    }
    
    public void setVerticalLayout(final float startX, final float startY, final float height, final int maxLines, final float leading) {
        this.startX = startX;
        this.startY = startY;
        this.height = height;
        this.maxLines = maxLines;
        this.setLeading(leading);
    }
    
    public void setLeading(final float leading) {
        this.leading = leading;
    }
    
    public float getLeading() {
        return this.leading;
    }
    
    protected PdfLine createLine(final float width) {
        if (this.chunks.isEmpty()) {
            return null;
        }
        this.splittedChunkText = null;
        this.currentStandbyChunk = null;
        final PdfLine line = new PdfLine(0.0f, width, this.alignment, 0.0f);
        this.currentChunkMarker = 0;
        while (this.currentChunkMarker < this.chunks.size()) {
            final PdfChunk original = this.chunks.get(this.currentChunkMarker);
            final String total = original.toString();
            this.currentStandbyChunk = line.add(original);
            if (this.currentStandbyChunk != null) {
                this.splittedChunkText = original.toString();
                original.setValue(total);
                return line;
            }
            ++this.currentChunkMarker;
        }
        return line;
    }
    
    protected void shortenChunkArray() {
        if (this.currentChunkMarker < 0) {
            return;
        }
        if (this.currentChunkMarker >= this.chunks.size()) {
            this.chunks.clear();
            return;
        }
        final PdfChunk split = this.chunks.get(this.currentChunkMarker);
        split.setValue(this.splittedChunkText);
        this.chunks.set(this.currentChunkMarker, this.currentStandbyChunk);
        for (int j = this.currentChunkMarker - 1; j >= 0; --j) {
            this.chunks.remove(j);
        }
    }
    
    public int go() {
        return this.go(false);
    }
    
    public int go(final boolean simulate) {
        boolean dirty = false;
        PdfContentByte graphics = null;
        if (this.text != null) {
            graphics = this.text.getDuplicate();
        }
        else if (!simulate) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("verticaltext.go.with.simulate.eq.eq.false.and.text.eq.eq.null"));
        }
        int status = 0;
        while (true) {
            while (this.maxLines > 0) {
                if (this.chunks.isEmpty()) {
                    status = 1;
                    if (dirty) {
                        this.text.endText();
                        this.text.add(graphics);
                    }
                    return status;
                }
                final PdfLine line = this.createLine(this.height);
                if (!simulate && !dirty) {
                    this.text.beginText();
                    dirty = true;
                }
                this.shortenChunkArray();
                if (!simulate) {
                    this.text.setTextMatrix(this.startX, this.startY - line.indentLeft());
                    this.writeLine(line, this.text, graphics);
                }
                --this.maxLines;
                this.startX -= this.leading;
            }
            status = 2;
            if (this.chunks.isEmpty()) {
                status |= 0x1;
            }
            continue;
        }
    }
    
    void writeLine(final PdfLine line, final PdfContentByte text, final PdfContentByte graphics) {
        PdfFont currentFont = null;
        for (final PdfChunk chunk : line) {
            if (chunk.font().compareTo(currentFont) != 0) {
                currentFont = chunk.font();
                text.setFontAndSize(currentFont.getFont(), currentFont.size());
            }
            final Color color = chunk.color();
            if (color != null) {
                text.setColorFill(color);
            }
            text.showText(chunk.toString());
            if (color != null) {
                text.resetRGBColorFill();
            }
        }
    }
    
    public void setOrigin(final float startX, final float startY) {
        this.startX = startX;
        this.startY = startY;
    }
    
    public float getOriginX() {
        return this.startX;
    }
    
    public float getOriginY() {
        return this.startY;
    }
    
    public int getMaxLines() {
        return this.maxLines;
    }
    
    public void setMaxLines(final int maxLines) {
        this.maxLines = maxLines;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public void setHeight(final float height) {
        this.height = height;
    }
    
    public void setAlignment(final int alignment) {
        this.alignment = alignment;
    }
    
    public int getAlignment() {
        return this.alignment;
    }
}
