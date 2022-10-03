package com.lowagie.text.pdf;

import com.lowagie.text.ElementListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import java.util.ArrayList;
import com.lowagie.text.Element;

public class MultiColumnText implements Element
{
    public static final float AUTOMATIC = -1.0f;
    private float desiredHeight;
    private float totalHeight;
    private boolean overflow;
    private float top;
    private ColumnText columnText;
    private ArrayList columnDefs;
    private boolean simple;
    private int currentColumn;
    private float nextY;
    private boolean columnsRightToLeft;
    private PdfDocument document;
    
    public MultiColumnText() {
        this(-1.0f);
    }
    
    public MultiColumnText(final float height) {
        this.simple = true;
        this.currentColumn = 0;
        this.nextY = -1.0f;
        this.columnsRightToLeft = false;
        this.columnDefs = new ArrayList();
        this.desiredHeight = height;
        this.top = -1.0f;
        this.columnText = new ColumnText(null);
        this.totalHeight = 0.0f;
    }
    
    public MultiColumnText(final float top, final float height) {
        this.simple = true;
        this.currentColumn = 0;
        this.nextY = -1.0f;
        this.columnsRightToLeft = false;
        this.columnDefs = new ArrayList();
        this.desiredHeight = height;
        this.top = top;
        this.nextY = top;
        this.columnText = new ColumnText(null);
        this.totalHeight = 0.0f;
    }
    
    public boolean isOverflow() {
        return this.overflow;
    }
    
    public void useColumnParams(final ColumnText sourceColumn) {
        this.columnText.setSimpleVars(sourceColumn);
    }
    
    public void addColumn(final float[] left, final float[] right) {
        final ColumnDef nextDef = new ColumnDef(left, right);
        if (!nextDef.isSimple()) {
            this.simple = false;
        }
        this.columnDefs.add(nextDef);
    }
    
    public void addSimpleColumn(final float left, final float right) {
        final ColumnDef newCol = new ColumnDef(left, right);
        this.columnDefs.add(newCol);
    }
    
    public void addRegularColumns(final float left, final float right, final float gutterWidth, final int numColumns) {
        float currX = left;
        final float width = right - left;
        final float colWidth = (width - gutterWidth * (numColumns - 1)) / numColumns;
        for (int i = 0; i < numColumns; ++i) {
            this.addSimpleColumn(currX, currX + colWidth);
            currX += colWidth + gutterWidth;
        }
    }
    
    public void addText(final Phrase phrase) {
        this.columnText.addText(phrase);
    }
    
    public void addText(final Chunk chunk) {
        this.columnText.addText(chunk);
    }
    
    public void addElement(final Element element) throws DocumentException {
        if (this.simple) {
            this.columnText.addElement(element);
        }
        else if (element instanceof Phrase) {
            this.columnText.addText((Phrase)element);
        }
        else {
            if (!(element instanceof Chunk)) {
                throw new DocumentException(MessageLocalization.getComposedMessage("can.t.add.1.to.multicolumntext.with.complex.columns", element.getClass()));
            }
            this.columnText.addText((Chunk)element);
        }
    }
    
    public float write(final PdfContentByte canvas, final PdfDocument document, float documentY) throws DocumentException {
        this.document = document;
        this.columnText.setCanvas(canvas);
        if (this.columnDefs.isEmpty()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("multicolumntext.has.no.columns"));
        }
        this.overflow = false;
        float currentHeight = 0.0f;
        boolean done = false;
        try {
            while (!done) {
                if (this.top == -1.0f) {
                    this.top = document.getVerticalPosition(true);
                }
                else if (this.nextY == -1.0f) {
                    this.nextY = document.getVerticalPosition(true);
                }
                final ColumnDef currentDef = this.columnDefs.get(this.getCurrentColumn());
                this.columnText.setYLine(this.top);
                float[] left = currentDef.resolvePositions(4);
                float[] right = currentDef.resolvePositions(8);
                if (document.isMarginMirroring() && document.getPageNumber() % 2 == 0) {
                    final float delta = document.rightMargin() - document.left();
                    left = left.clone();
                    right = right.clone();
                    for (int i = 0; i < left.length; i += 2) {
                        final float[] array = left;
                        final int n = i;
                        array[n] -= delta;
                    }
                    for (int i = 0; i < right.length; i += 2) {
                        final float[] array2 = right;
                        final int n2 = i;
                        array2[n2] -= delta;
                    }
                }
                currentHeight = Math.max(currentHeight, this.getHeight(left, right));
                if (currentDef.isSimple()) {
                    this.columnText.setSimpleColumn(left[2], left[3], right[0], right[1]);
                }
                else {
                    this.columnText.setColumns(left, right);
                }
                final int result = this.columnText.go();
                if ((result & 0x1) != 0x0) {
                    done = true;
                    this.top = this.columnText.getYLine();
                }
                else if (this.shiftCurrentColumn()) {
                    this.top = this.nextY;
                }
                else {
                    this.totalHeight += currentHeight;
                    if (this.desiredHeight != -1.0f && this.totalHeight >= this.desiredHeight) {
                        this.overflow = true;
                        break;
                    }
                    documentY = this.nextY;
                    this.newPage();
                    currentHeight = 0.0f;
                }
            }
        }
        catch (final DocumentException ex) {
            ex.printStackTrace();
            throw ex;
        }
        if (this.desiredHeight == -1.0f && this.columnDefs.size() == 1) {
            currentHeight = documentY - this.columnText.getYLine();
        }
        return currentHeight;
    }
    
    private void newPage() throws DocumentException {
        this.resetCurrentColumn();
        if (this.desiredHeight == -1.0f) {
            final float n = -1.0f;
            this.nextY = n;
            this.top = n;
        }
        else {
            this.top = this.nextY;
        }
        this.totalHeight = 0.0f;
        if (this.document != null) {
            this.document.newPage();
        }
    }
    
    private float getHeight(final float[] left, final float[] right) {
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (int i = 0; i < left.length; i += 2) {
            min = Math.min(min, left[i + 1]);
            max = Math.max(max, left[i + 1]);
        }
        for (int i = 0; i < right.length; i += 2) {
            min = Math.min(min, right[i + 1]);
            max = Math.max(max, right[i + 1]);
        }
        return max - min;
    }
    
    @Override
    public boolean process(final ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (final DocumentException de) {
            return false;
        }
    }
    
    @Override
    public int type() {
        return 40;
    }
    
    @Override
    public ArrayList getChunks() {
        return null;
    }
    
    @Override
    public boolean isContent() {
        return true;
    }
    
    @Override
    public boolean isNestable() {
        return false;
    }
    
    private float getColumnBottom() {
        if (this.desiredHeight == -1.0f) {
            return this.document.bottom();
        }
        return Math.max(this.top - (this.desiredHeight - this.totalHeight), this.document.bottom());
    }
    
    public void nextColumn() throws DocumentException {
        this.currentColumn = (this.currentColumn + 1) % this.columnDefs.size();
        this.top = this.nextY;
        if (this.currentColumn == 0) {
            this.newPage();
        }
    }
    
    public int getCurrentColumn() {
        if (this.columnsRightToLeft) {
            return this.columnDefs.size() - this.currentColumn - 1;
        }
        return this.currentColumn;
    }
    
    public void resetCurrentColumn() {
        this.currentColumn = 0;
    }
    
    public boolean shiftCurrentColumn() {
        if (this.currentColumn + 1 < this.columnDefs.size()) {
            ++this.currentColumn;
            return true;
        }
        return false;
    }
    
    public void setColumnsRightToLeft(final boolean direction) {
        this.columnsRightToLeft = direction;
    }
    
    public void setSpaceCharRatio(final float spaceCharRatio) {
        this.columnText.setSpaceCharRatio(spaceCharRatio);
    }
    
    public void setRunDirection(final int runDirection) {
        this.columnText.setRunDirection(runDirection);
    }
    
    public void setArabicOptions(final int arabicOptions) {
        this.columnText.setArabicOptions(arabicOptions);
    }
    
    public void setAlignment(final int alignment) {
        this.columnText.setAlignment(alignment);
    }
    
    private class ColumnDef
    {
        private float[] left;
        private float[] right;
        
        ColumnDef(final float[] newLeft, final float[] newRight) {
            this.left = newLeft;
            this.right = newRight;
        }
        
        ColumnDef(final float leftPosition, final float rightPosition) {
            (this.left = new float[4])[0] = leftPosition;
            this.left[1] = MultiColumnText.this.top;
            this.left[2] = leftPosition;
            if (MultiColumnText.this.desiredHeight == -1.0f || MultiColumnText.this.top == -1.0f) {
                this.left[3] = -1.0f;
            }
            else {
                this.left[3] = MultiColumnText.this.top - MultiColumnText.this.desiredHeight;
            }
            (this.right = new float[4])[0] = rightPosition;
            this.right[1] = MultiColumnText.this.top;
            this.right[2] = rightPosition;
            if (MultiColumnText.this.desiredHeight == -1.0f || MultiColumnText.this.top == -1.0f) {
                this.right[3] = -1.0f;
            }
            else {
                this.right[3] = MultiColumnText.this.top - MultiColumnText.this.desiredHeight;
            }
        }
        
        float[] resolvePositions(final int side) {
            if (side == 4) {
                return this.resolvePositions(this.left);
            }
            return this.resolvePositions(this.right);
        }
        
        private float[] resolvePositions(final float[] positions) {
            if (!this.isSimple()) {
                positions[1] = MultiColumnText.this.top;
                return positions;
            }
            if (MultiColumnText.this.top == -1.0f) {
                throw new RuntimeException("resolvePositions called with top=AUTOMATIC (-1).  Top position must be set befure lines can be resolved");
            }
            positions[1] = MultiColumnText.this.top;
            positions[3] = MultiColumnText.this.getColumnBottom();
            return positions;
        }
        
        private boolean isSimple() {
            return this.left.length == 4 && this.right.length == 4 && this.left[0] == this.left[2] && this.right[0] == this.right[2];
        }
    }
}
