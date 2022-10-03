package com.lowagie.text.pdf;

import com.lowagie.text.ListItem;
import java.util.Iterator;
import com.lowagie.text.Chunk;
import java.util.ArrayList;

public class PdfLine
{
    protected ArrayList line;
    protected float left;
    protected float width;
    protected int alignment;
    protected float height;
    protected Chunk listSymbol;
    protected float symbolIndent;
    protected boolean newlineSplit;
    protected float originalWidth;
    protected boolean isRTL;
    
    PdfLine(final float left, final float right, final int alignment, final float height) {
        this.listSymbol = null;
        this.newlineSplit = false;
        this.isRTL = false;
        this.left = left;
        this.width = right - left;
        this.originalWidth = this.width;
        this.alignment = alignment;
        this.height = height;
        this.line = new ArrayList();
    }
    
    PdfLine(final float left, final float originalWidth, final float remainingWidth, final int alignment, final boolean newlineSplit, final ArrayList line, final boolean isRTL) {
        this.listSymbol = null;
        this.newlineSplit = false;
        this.isRTL = false;
        this.left = left;
        this.originalWidth = originalWidth;
        this.width = remainingWidth;
        this.alignment = alignment;
        this.line = line;
        this.newlineSplit = newlineSplit;
        this.isRTL = isRTL;
    }
    
    PdfChunk add(PdfChunk chunk) {
        if (chunk == null || chunk.toString().equals("")) {
            return null;
        }
        PdfChunk overflow = chunk.split(this.width);
        this.newlineSplit = (chunk.isNewlineSplit() || overflow == null);
        if (chunk.isTab()) {
            final Object[] tab = (Object[])chunk.getAttribute("TAB");
            final float tabPosition = (float)tab[1];
            final boolean newline = (boolean)tab[2];
            if (newline && tabPosition < this.originalWidth - this.width) {
                return chunk;
            }
            this.width = this.originalWidth - tabPosition;
            chunk.adjustLeft(this.left);
            this.addToLine(chunk);
        }
        else if (chunk.length() > 0 || chunk.isImage()) {
            if (overflow != null) {
                chunk.trimLastSpace();
            }
            this.width -= chunk.width();
            this.addToLine(chunk);
        }
        else if (this.line.size() < 1) {
            chunk = overflow;
            overflow = chunk.truncate(this.width);
            this.width -= chunk.width();
            if (chunk.length() > 0) {
                this.addToLine(chunk);
                return overflow;
            }
            if (overflow != null) {
                this.addToLine(overflow);
            }
            return null;
        }
        else {
            this.width += this.line.get(this.line.size() - 1).trimLastSpace();
        }
        return overflow;
    }
    
    private void addToLine(final PdfChunk chunk) {
        if (chunk.changeLeading && chunk.isImage()) {
            final float f = chunk.getImage().getScaledHeight() + chunk.getImageOffsetY() + chunk.getImage().getBorderWidthTop();
            if (f > this.height) {
                this.height = f;
            }
        }
        this.line.add(chunk);
    }
    
    public int size() {
        return this.line.size();
    }
    
    public Iterator iterator() {
        return this.line.iterator();
    }
    
    float height() {
        return this.height;
    }
    
    float indentLeft() {
        if (!this.isRTL) {
            if (this.getSeparatorCount() == 0) {
                switch (this.alignment) {
                    case 2: {
                        return this.left + this.width;
                    }
                    case 1: {
                        return this.left + this.width / 2.0f;
                    }
                }
            }
            return this.left;
        }
        switch (this.alignment) {
            case 0: {
                return this.left + this.width;
            }
            case 1: {
                return this.left + this.width / 2.0f;
            }
            default: {
                return this.left;
            }
        }
    }
    
    public boolean hasToBeJustified() {
        return (this.alignment == 3 || this.alignment == 8) && this.width != 0.0f;
    }
    
    public void resetAlignment() {
        if (this.alignment == 3) {
            this.alignment = 0;
        }
    }
    
    void setExtraIndent(final float extra) {
        this.left += extra;
        this.width -= extra;
    }
    
    float widthLeft() {
        return this.width;
    }
    
    int numberOfSpaces() {
        final String string = this.toString();
        final int length = string.length();
        int numberOfSpaces = 0;
        for (int i = 0; i < length; ++i) {
            if (string.charAt(i) == ' ') {
                ++numberOfSpaces;
            }
        }
        return numberOfSpaces;
    }
    
    public void setListItem(final ListItem listItem) {
        this.listSymbol = listItem.getListSymbol();
        this.symbolIndent = listItem.getIndentationLeft();
    }
    
    public Chunk listSymbol() {
        return this.listSymbol;
    }
    
    public float listIndent() {
        return this.symbolIndent;
    }
    
    @Override
    public String toString() {
        final StringBuffer tmp = new StringBuffer();
        final Iterator i = this.line.iterator();
        while (i.hasNext()) {
            tmp.append(i.next().toString());
        }
        return tmp.toString();
    }
    
    public int GetLineLengthUtf32() {
        int total = 0;
        final Iterator i = this.line.iterator();
        while (i.hasNext()) {
            total += i.next().lengthUtf32();
        }
        return total;
    }
    
    public boolean isNewlineSplit() {
        return this.newlineSplit && this.alignment != 8;
    }
    
    public int getLastStrokeChunk() {
        int lastIdx;
        for (lastIdx = this.line.size() - 1; lastIdx >= 0; --lastIdx) {
            final PdfChunk chunk = this.line.get(lastIdx);
            if (chunk.isStroked()) {
                break;
            }
        }
        return lastIdx;
    }
    
    public PdfChunk getChunk(final int idx) {
        if (idx < 0 || idx >= this.line.size()) {
            return null;
        }
        return this.line.get(idx);
    }
    
    public float getOriginalWidth() {
        return this.originalWidth;
    }
    
    float[] getMaxSize() {
        float normal_leading = 0.0f;
        float image_leading = -10000.0f;
        for (int k = 0; k < this.line.size(); ++k) {
            final PdfChunk chunk = this.line.get(k);
            if (!chunk.isImage()) {
                normal_leading = Math.max(chunk.font().size(), normal_leading);
            }
            else {
                image_leading = Math.max(chunk.getImage().getScaledHeight() + chunk.getImageOffsetY(), image_leading);
            }
        }
        return new float[] { normal_leading, image_leading };
    }
    
    boolean isRTL() {
        return this.isRTL;
    }
    
    int getSeparatorCount() {
        int s = 0;
        for (final PdfChunk ck : this.line) {
            if (ck.isTab()) {
                return 0;
            }
            if (!ck.isHorizontalSeparator()) {
                continue;
            }
            ++s;
        }
        return s;
    }
    
    public float getWidthCorrected(final float charSpacing, final float wordSpacing) {
        float total = 0.0f;
        for (int k = 0; k < this.line.size(); ++k) {
            final PdfChunk ck = this.line.get(k);
            total += ck.getWidthCorrected(charSpacing, wordSpacing);
        }
        return total;
    }
    
    public float getAscender() {
        float ascender = 0.0f;
        for (int k = 0; k < this.line.size(); ++k) {
            final PdfChunk ck = this.line.get(k);
            if (ck.isImage()) {
                ascender = Math.max(ascender, ck.getImage().getScaledHeight() + ck.getImageOffsetY());
            }
            else {
                final PdfFont font = ck.font();
                ascender = Math.max(ascender, font.getFont().getFontDescriptor(1, font.size()));
            }
        }
        return ascender;
    }
    
    public float getDescender() {
        float descender = 0.0f;
        for (int k = 0; k < this.line.size(); ++k) {
            final PdfChunk ck = this.line.get(k);
            if (ck.isImage()) {
                descender = Math.min(descender, ck.getImageOffsetY());
            }
            else {
                final PdfFont font = ck.font();
                descender = Math.min(descender, font.getFont().getFontDescriptor(3, font.size()));
            }
        }
        return descender;
    }
}
