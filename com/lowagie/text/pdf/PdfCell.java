package com.lowagie.text.pdf;

import com.lowagie.text.Anchor;
import com.lowagie.text.ListItem;
import java.util.Iterator;
import com.lowagie.text.Chunk;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.List;
import com.lowagie.text.Image;
import com.lowagie.text.Element;
import com.lowagie.text.Cell;
import java.util.ArrayList;
import com.lowagie.text.Rectangle;

public class PdfCell extends Rectangle
{
    private ArrayList lines;
    private PdfLine line;
    private ArrayList images;
    private float leading;
    private int rownumber;
    private int rowspan;
    private float cellspacing;
    private float cellpadding;
    private boolean header;
    private float contentHeight;
    private boolean useAscender;
    private boolean useDescender;
    private boolean useBorderPadding;
    private int verticalAlignment;
    private PdfLine firstLine;
    private PdfLine lastLine;
    private int groupNumber;
    
    public PdfCell(final Cell cell, final int rownumber, float left, float right, final float top, final float cellspacing, final float cellpadding) {
        super(left, top, right, top);
        this.header = false;
        this.contentHeight = 0.0f;
        this.cloneNonPositionParameters(cell);
        this.cellpadding = cellpadding;
        this.cellspacing = cellspacing;
        this.verticalAlignment = cell.getVerticalAlignment();
        this.useAscender = cell.isUseAscender();
        this.useDescender = cell.isUseDescender();
        this.useBorderPadding = cell.isUseBorderPadding();
        this.lines = new ArrayList();
        this.images = new ArrayList();
        this.leading = cell.getLeading();
        final int alignment = cell.getHorizontalAlignment();
        left += cellspacing + cellpadding;
        right -= cellspacing + cellpadding;
        left += this.getBorderWidthInside(4);
        right -= this.getBorderWidthInside(8);
        this.contentHeight = 0.0f;
        this.rowspan = cell.getRowspan();
        final Iterator i = cell.getElements();
        while (i.hasNext()) {
            final Element element = i.next();
            switch (element.type()) {
                case 32:
                case 33:
                case 34:
                case 35:
                case 36: {
                    this.addImage((Image)element, left, right, 0.4f * this.leading, alignment);
                    continue;
                }
                case 14: {
                    if (this.line != null && this.line.size() > 0) {
                        this.line.resetAlignment();
                        this.addLine(this.line);
                    }
                    this.addList((List)element, left, right, alignment);
                    this.line = new PdfLine(left, right, alignment, this.leading);
                    continue;
                }
                default: {
                    final ArrayList allActions = new ArrayList();
                    this.processActions(element, null, allActions);
                    int aCounter = 0;
                    float currentLineLeading = this.leading;
                    float currentLeft = left;
                    float currentRight = right;
                    if (element instanceof Phrase) {
                        currentLineLeading = ((Phrase)element).getLeading();
                    }
                    if (element instanceof Paragraph) {
                        final Paragraph p = (Paragraph)element;
                        currentLeft += p.getIndentationLeft();
                        currentRight -= p.getIndentationRight();
                    }
                    if (this.line == null) {
                        this.line = new PdfLine(currentLeft, currentRight, alignment, currentLineLeading);
                    }
                    final ArrayList chunks = element.getChunks();
                    if (chunks.isEmpty()) {
                        this.addLine(this.line);
                        this.line = new PdfLine(currentLeft, currentRight, alignment, currentLineLeading);
                    }
                    else {
                        for (final Chunk c : chunks) {
                            PdfChunk overflow;
                            for (PdfChunk chunk = new PdfChunk(c, allActions.get(aCounter++)); (overflow = this.line.add(chunk)) != null; chunk = overflow) {
                                this.addLine(this.line);
                                this.line = new PdfLine(currentLeft, currentRight, alignment, currentLineLeading);
                            }
                        }
                    }
                    switch (element.type()) {
                        case 12:
                        case 13:
                        case 16: {
                            this.line.resetAlignment();
                            this.flushCurrentLine();
                            continue;
                        }
                    }
                    break;
                }
            }
        }
        this.flushCurrentLine();
        if (this.lines.size() > cell.getMaxLines()) {
            while (this.lines.size() > cell.getMaxLines()) {
                this.removeLine(this.lines.size() - 1);
            }
            if (cell.getMaxLines() > 0) {
                final String more = cell.getShowTruncation();
                if (more != null && more.length() > 0) {
                    this.lastLine = this.lines.get(this.lines.size() - 1);
                    if (this.lastLine.size() >= 0) {
                        final PdfChunk lastChunk = this.lastLine.getChunk(this.lastLine.size() - 1);
                        final float moreWidth = new PdfChunk(more, lastChunk).width();
                        while (lastChunk.toString().length() > 0 && lastChunk.width() + moreWidth > right - left) {
                            lastChunk.setValue(lastChunk.toString().substring(0, lastChunk.length() - 1));
                        }
                        lastChunk.setValue(lastChunk.toString() + more);
                    }
                    else {
                        this.lastLine.add(new PdfChunk(new Chunk(more), null));
                    }
                }
            }
        }
        if (this.useDescender && this.lastLine != null) {
            this.contentHeight -= this.lastLine.getDescender();
        }
        if (!this.lines.isEmpty()) {
            this.firstLine = this.lines.get(0);
            final float firstLineRealHeight = this.firstLineRealHeight();
            this.contentHeight -= this.firstLine.height();
            this.firstLine.height = firstLineRealHeight;
            this.contentHeight += firstLineRealHeight;
        }
        float newBottom = top - this.contentHeight - 2.0f * this.cellpadding() - 2.0f * this.cellspacing();
        newBottom -= this.getBorderWidthInside(1) + this.getBorderWidthInside(2);
        this.setBottom(newBottom);
        this.rownumber = rownumber;
    }
    
    private void addList(final List list, final float left, final float right, final int alignment) {
        final ArrayList allActions = new ArrayList();
        this.processActions(list, null, allActions);
        int aCounter = 0;
        for (final Element ele : list.getItems()) {
            switch (ele.type()) {
                case 15: {
                    final ListItem item = (ListItem)ele;
                    (this.line = new PdfLine(left + item.getIndentationLeft(), right, alignment, item.getLeading())).setListItem(item);
                    final Iterator j = item.getChunks().iterator();
                    while (j.hasNext()) {
                        PdfChunk overflow;
                        for (PdfChunk chunk = new PdfChunk(j.next(), allActions.get(aCounter++)); (overflow = this.line.add(chunk)) != null; chunk = overflow) {
                            this.addLine(this.line);
                            this.line = new PdfLine(left + item.getIndentationLeft(), right, alignment, item.getLeading());
                        }
                        this.line.resetAlignment();
                        this.addLine(this.line);
                        this.line = new PdfLine(left + item.getIndentationLeft(), right, alignment, this.leading);
                    }
                    continue;
                }
                case 14: {
                    final List sublist = (List)ele;
                    this.addList(sublist, left + sublist.getIndentationLeft(), right, alignment);
                    continue;
                }
            }
        }
    }
    
    @Override
    public void setBottom(final float value) {
        super.setBottom(value);
        final float firstLineRealHeight = this.firstLineRealHeight();
        final float totalHeight = this.ury - value;
        float nonContentHeight = this.cellpadding() * 2.0f + this.cellspacing() * 2.0f;
        nonContentHeight += this.getBorderWidthInside(1) + this.getBorderWidthInside(2);
        final float interiorHeight = totalHeight - nonContentHeight;
        float extraHeight = 0.0f;
        switch (this.verticalAlignment) {
            case 6: {
                extraHeight = interiorHeight - this.contentHeight;
                break;
            }
            case 5: {
                extraHeight = (interiorHeight - this.contentHeight) / 2.0f;
                break;
            }
            default: {
                extraHeight = 0.0f;
                break;
            }
        }
        extraHeight += this.cellpadding() + this.cellspacing();
        extraHeight += this.getBorderWidthInside(1);
        if (this.firstLine != null) {
            this.firstLine.height = firstLineRealHeight + extraHeight;
        }
    }
    
    @Override
    public float getLeft() {
        return super.getLeft(this.cellspacing);
    }
    
    @Override
    public float getRight() {
        return super.getRight(this.cellspacing);
    }
    
    @Override
    public float getTop() {
        return super.getTop(this.cellspacing);
    }
    
    @Override
    public float getBottom() {
        return super.getBottom(this.cellspacing);
    }
    
    private void addLine(final PdfLine line) {
        this.lines.add(line);
        this.contentHeight += line.height();
        this.lastLine = line;
        this.line = null;
    }
    
    private PdfLine removeLine(final int index) {
        final PdfLine oldLine = this.lines.remove(index);
        this.contentHeight -= oldLine.height();
        if (index == 0 && !this.lines.isEmpty()) {
            this.firstLine = this.lines.get(0);
            final float firstLineRealHeight = this.firstLineRealHeight();
            this.contentHeight -= this.firstLine.height();
            this.firstLine.height = firstLineRealHeight;
            this.contentHeight += firstLineRealHeight;
        }
        return oldLine;
    }
    
    private void flushCurrentLine() {
        if (this.line != null && this.line.size() > 0) {
            this.addLine(this.line);
        }
    }
    
    private float firstLineRealHeight() {
        float firstLineRealHeight = 0.0f;
        if (this.firstLine != null) {
            final PdfChunk chunk = this.firstLine.getChunk(0);
            if (chunk != null) {
                final Image image = chunk.getImage();
                if (image != null) {
                    firstLineRealHeight = this.firstLine.getChunk(0).getImage().getScaledHeight();
                }
                else {
                    firstLineRealHeight = (this.useAscender ? this.firstLine.getAscender() : this.leading);
                }
            }
        }
        return firstLineRealHeight;
    }
    
    private float getBorderWidthInside(final int side) {
        float width = 0.0f;
        if (this.useBorderPadding) {
            switch (side) {
                case 4: {
                    width = this.getBorderWidthLeft();
                    break;
                }
                case 8: {
                    width = this.getBorderWidthRight();
                    break;
                }
                case 1: {
                    width = this.getBorderWidthTop();
                    break;
                }
                default: {
                    width = this.getBorderWidthBottom();
                    break;
                }
            }
            if (!this.isUseVariableBorders()) {
                width /= 2.0f;
            }
        }
        return width;
    }
    
    private float addImage(final Image i, float left, float right, final float extraHeight, final int alignment) {
        final Image image = Image.getInstance(i);
        if (image.getScaledWidth() > right - left) {
            image.scaleToFit(right - left, Float.MAX_VALUE);
        }
        this.flushCurrentLine();
        if (this.line == null) {
            this.line = new PdfLine(left, right, alignment, this.leading);
        }
        final PdfLine imageLine = this.line;
        right -= left;
        left = 0.0f;
        if ((image.getAlignment() & 0x2) == 0x2) {
            left = right - image.getScaledWidth();
        }
        else if ((image.getAlignment() & 0x1) == 0x1) {
            left += (right - left - image.getScaledWidth()) / 2.0f;
        }
        final Chunk imageChunk = new Chunk(image, left, 0.0f);
        imageLine.add(new PdfChunk(imageChunk, null));
        this.addLine(imageLine);
        return imageLine.height();
    }
    
    public ArrayList getLines(final float top, final float bottom) {
        float currentPosition = Math.min(this.getTop(), top);
        this.setTop(currentPosition + this.cellspacing);
        final ArrayList result = new ArrayList();
        if (this.getTop() < bottom) {
            return result;
        }
        int size = this.lines.size();
        boolean aboveBottom = true;
        for (int i = 0; i < size && aboveBottom; ++i) {
            this.line = this.lines.get(i);
            final float lineHeight = this.line.height();
            currentPosition -= lineHeight;
            if (currentPosition > bottom + this.cellpadding + this.getBorderWidthInside(2)) {
                result.add(this.line);
            }
            else {
                aboveBottom = false;
            }
        }
        float difference = 0.0f;
        if (!this.header) {
            if (aboveBottom) {
                this.lines = new ArrayList();
                this.contentHeight = 0.0f;
            }
            else {
                size = result.size();
                for (int j = 0; j < size; ++j) {
                    this.line = this.removeLine(0);
                    difference += this.line.height();
                }
            }
        }
        if (difference > 0.0f) {
            for (final Image image : this.images) {
                image.setAbsolutePosition(image.getAbsoluteX(), image.getAbsoluteY() - difference - this.leading);
            }
        }
        return result;
    }
    
    public ArrayList getImages(float top, final float bottom) {
        if (this.getTop() < bottom) {
            return new ArrayList();
        }
        top = Math.min(this.getTop(), top);
        final ArrayList result = new ArrayList();
        final Iterator i = this.images.iterator();
        while (i.hasNext() && !this.header) {
            final Image image = i.next();
            final float height = image.getAbsoluteY();
            if (top - height > bottom + this.cellpadding) {
                image.setAbsolutePosition(image.getAbsoluteX(), top - height);
                result.add(image);
                i.remove();
            }
        }
        return result;
    }
    
    boolean isHeader() {
        return this.header;
    }
    
    void setHeader() {
        this.header = true;
    }
    
    boolean mayBeRemoved() {
        return this.header || (this.lines.isEmpty() && this.images.isEmpty());
    }
    
    public int size() {
        return this.lines.size();
    }
    
    private float remainingLinesHeight() {
        if (this.lines.isEmpty()) {
            return 0.0f;
        }
        float result = 0.0f;
        for (int size = this.lines.size(), i = 0; i < size; ++i) {
            final PdfLine line = this.lines.get(i);
            result += line.height();
        }
        return result;
    }
    
    public float remainingHeight() {
        float result = 0.0f;
        for (final Image image : this.images) {
            result += image.getScaledHeight();
        }
        return this.remainingLinesHeight() + this.cellspacing + 2.0f * this.cellpadding + result;
    }
    
    public float leading() {
        return this.leading;
    }
    
    public int rownumber() {
        return this.rownumber;
    }
    
    public int rowspan() {
        return this.rowspan;
    }
    
    public float cellspacing() {
        return this.cellspacing;
    }
    
    public float cellpadding() {
        return this.cellpadding;
    }
    
    protected void processActions(final Element element, PdfAction action, final ArrayList allActions) {
        if (element.type() == 17) {
            final String url = ((Anchor)element).getReference();
            if (url != null) {
                action = new PdfAction(url);
            }
        }
        switch (element.type()) {
            case 11:
            case 12:
            case 13:
            case 15:
            case 16:
            case 17: {
                final Iterator i = ((ArrayList)element).iterator();
                while (i.hasNext()) {
                    this.processActions(i.next(), action, allActions);
                }
                break;
            }
            case 10: {
                allActions.add(action);
                break;
            }
            case 14: {
                final Iterator i = ((List)element).getItems().iterator();
                while (i.hasNext()) {
                    this.processActions(i.next(), action, allActions);
                }
                break;
            }
            default: {
                int n = element.getChunks().size();
                while (n-- > 0) {
                    allActions.add(action);
                }
                break;
            }
        }
    }
    
    public int getGroupNumber() {
        return this.groupNumber;
    }
    
    void setGroupNumber(final int number) {
        this.groupNumber = number;
    }
    
    @Override
    public Rectangle rectangle(final float top, final float bottom) {
        final Rectangle tmp = new Rectangle(this.getLeft(), this.getBottom(), this.getRight(), this.getTop());
        tmp.cloneNonPositionParameters(this);
        if (this.getTop() > top) {
            tmp.setTop(top);
            tmp.setBorder(this.border - (this.border & 0x1));
        }
        if (this.getBottom() < bottom) {
            tmp.setBottom(bottom);
            tmp.setBorder(this.border - (this.border & 0x2));
        }
        return tmp;
    }
    
    public void setUseAscender(final boolean use) {
        this.useAscender = use;
    }
    
    public boolean isUseAscender() {
        return this.useAscender;
    }
    
    public void setUseDescender(final boolean use) {
        this.useDescender = use;
    }
    
    public boolean isUseDescender() {
        return this.useDescender;
    }
    
    public void setUseBorderPadding(final boolean use) {
        this.useBorderPadding = use;
    }
    
    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }
}
