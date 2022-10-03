package com.lowagie.text.pdf;

import com.lowagie.text.Phrase;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import java.awt.Color;
import com.lowagie.text.Rectangle;

public class PdfPRow
{
    public static final float BOTTOM_LIMIT = -1.07374182E9f;
    public static final float RIGHT_LIMIT = 20000.0f;
    protected PdfPCell[] cells;
    protected float[] widths;
    protected float[] extraHeights;
    protected float maxHeight;
    protected boolean calculated;
    private int[] canvasesPos;
    
    public PdfPRow(final PdfPCell[] cells) {
        this.maxHeight = 0.0f;
        this.calculated = false;
        this.cells = cells;
        this.widths = new float[cells.length];
        this.initExtraHeights();
    }
    
    public PdfPRow(final PdfPRow row) {
        this.maxHeight = 0.0f;
        this.calculated = false;
        this.maxHeight = row.maxHeight;
        this.calculated = row.calculated;
        this.cells = new PdfPCell[row.cells.length];
        for (int k = 0; k < this.cells.length; ++k) {
            if (row.cells[k] != null) {
                this.cells[k] = new PdfPCell(row.cells[k]);
            }
        }
        this.widths = new float[this.cells.length];
        System.arraycopy(row.widths, 0, this.widths, 0, this.cells.length);
        this.initExtraHeights();
    }
    
    public boolean setWidths(final float[] widths) {
        if (widths.length != this.cells.length) {
            return false;
        }
        System.arraycopy(widths, 0, this.widths, 0, this.cells.length);
        float total = 0.0f;
        this.calculated = false;
        for (int k = 0; k < widths.length; ++k) {
            final PdfPCell cell = this.cells[k];
            if (cell == null) {
                total += widths[k];
            }
            else {
                cell.setLeft(total);
                for (int last = k + cell.getColspan(); k < last; ++k) {
                    total += widths[k];
                }
                --k;
                cell.setRight(total);
                cell.setTop(0.0f);
            }
        }
        return true;
    }
    
    public void initExtraHeights() {
        this.extraHeights = new float[this.cells.length];
        for (int i = 0; i < this.extraHeights.length; ++i) {
            this.extraHeights[i] = 0.0f;
        }
    }
    
    public void setExtraHeight(final int cell, final float height) {
        if (cell < 0 || cell >= this.cells.length) {
            return;
        }
        this.extraHeights[cell] = height;
    }
    
    public float calculateHeights() {
        this.maxHeight = 0.0f;
        for (int k = 0; k < this.cells.length; ++k) {
            final PdfPCell cell = this.cells[k];
            float height = 0.0f;
            if (cell != null) {
                height = cell.getMaxHeight();
                if (height > this.maxHeight && cell.getRowspan() == 1) {
                    this.maxHeight = height;
                }
            }
        }
        this.calculated = true;
        return this.maxHeight;
    }
    
    public void writeBorderAndBackground(final float xPos, final float yPos, final float currentMaxHeight, final PdfPCell cell, final PdfContentByte[] canvases) {
        final Color background = cell.getBackgroundColor();
        if (background != null || cell.hasBorders()) {
            final float right = cell.getRight() + xPos;
            final float top = cell.getTop() + yPos;
            final float left = cell.getLeft() + xPos;
            final float bottom = top - currentMaxHeight;
            if (background != null) {
                final PdfContentByte backgr = canvases[1];
                backgr.setColorFill(background);
                backgr.rectangle(left, bottom, right - left, top - bottom);
                backgr.fill();
            }
            if (cell.hasBorders()) {
                final Rectangle newRect = new Rectangle(left, bottom, right, top);
                newRect.cloneNonPositionParameters(cell);
                newRect.setBackgroundColor(null);
                final PdfContentByte lineCanvas = canvases[2];
                lineCanvas.rectangle(newRect);
            }
        }
    }
    
    protected void saveAndRotateCanvases(final PdfContentByte[] canvases, final float a, final float b, final float c, final float d, final float e, final float f) {
        final int last = 4;
        if (this.canvasesPos == null) {
            this.canvasesPos = new int[last * 2];
        }
        for (int k = 0; k < last; ++k) {
            final ByteBuffer bb = canvases[k].getInternalBuffer();
            this.canvasesPos[k * 2] = bb.size();
            canvases[k].saveState();
            canvases[k].concatCTM(a, b, c, d, e, f);
            this.canvasesPos[k * 2 + 1] = bb.size();
        }
    }
    
    protected void restoreCanvases(final PdfContentByte[] canvases) {
        for (int last = 4, k = 0; k < last; ++k) {
            final ByteBuffer bb = canvases[k].getInternalBuffer();
            final int p1 = bb.size();
            canvases[k].restoreState();
            if (p1 == this.canvasesPos[k * 2 + 1]) {
                bb.setSize(this.canvasesPos[k * 2]);
            }
        }
    }
    
    public static float setColumn(final ColumnText ct, final float left, final float bottom, float right, float top) {
        if (left > right) {
            right = left;
        }
        if (bottom > top) {
            top = bottom;
        }
        ct.setSimpleColumn(left, bottom, right, top);
        return top;
    }
    
    public void writeCells(int colStart, int colEnd, float xPos, final float yPos, final PdfContentByte[] canvases) {
        if (!this.calculated) {
            this.calculateHeights();
        }
        if (colEnd < 0) {
            colEnd = this.cells.length;
        }
        else {
            colEnd = Math.min(colEnd, this.cells.length);
        }
        if (colStart < 0) {
            colStart = 0;
        }
        if (colStart >= colEnd) {
            return;
        }
        int newStart;
        for (newStart = colStart; newStart >= 0 && this.cells[newStart] == null; --newStart) {
            if (newStart > 0) {
                xPos -= this.widths[newStart - 1];
            }
        }
        if (newStart < 0) {
            newStart = 0;
        }
        if (this.cells[newStart] != null) {
            xPos -= this.cells[newStart].getLeft();
        }
        for (int k = newStart; k < colEnd; ++k) {
            final PdfPCell cell = this.cells[k];
            if (cell != null) {
                final float currentMaxHeight = this.maxHeight + this.extraHeights[k];
                this.writeBorderAndBackground(xPos, yPos, currentMaxHeight, cell, canvases);
                Image img = cell.getImage();
                float tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                if (cell.getHeight() <= currentMaxHeight) {
                    switch (cell.getVerticalAlignment()) {
                        case 6: {
                            tly = cell.getTop() + yPos - currentMaxHeight + cell.getHeight() - cell.getEffectivePaddingTop();
                            break;
                        }
                        case 5: {
                            tly = cell.getTop() + yPos + (cell.getHeight() - currentMaxHeight) / 2.0f - cell.getEffectivePaddingTop();
                            break;
                        }
                    }
                }
                if (img != null) {
                    if (cell.getRotation() != 0) {
                        img = Image.getInstance(img);
                        img.setRotation(img.getImageRotation() + (float)(cell.getRotation() * 3.141592653589793 / 180.0));
                    }
                    boolean vf = false;
                    if (cell.getHeight() > currentMaxHeight) {
                        img.scalePercent(100.0f);
                        final float scale = (currentMaxHeight - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom()) / img.getScaledHeight();
                        img.scalePercent(scale * 100.0f);
                        vf = true;
                    }
                    float left = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                    if (vf) {
                        switch (cell.getHorizontalAlignment()) {
                            case 1: {
                                left = xPos + (cell.getLeft() + cell.getEffectivePaddingLeft() + cell.getRight() - cell.getEffectivePaddingRight() - img.getScaledWidth()) / 2.0f;
                                break;
                            }
                            case 2: {
                                left = xPos + cell.getRight() - cell.getEffectivePaddingRight() - img.getScaledWidth();
                                break;
                            }
                        }
                        tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                    }
                    img.setAbsolutePosition(left, tly - img.getScaledHeight());
                    try {
                        canvases[3].addImage(img);
                    }
                    catch (final DocumentException e) {
                        throw new ExceptionConverter(e);
                    }
                }
                else if (cell.getRotation() == 90 || cell.getRotation() == 270) {
                    final float netWidth = currentMaxHeight - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom();
                    final float netHeight = cell.getWidth() - cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight();
                    ColumnText ct = ColumnText.duplicate(cell.getColumn());
                    ct.setCanvases(canvases);
                    ct.setSimpleColumn(0.0f, 0.0f, netWidth + 0.001f, -netHeight);
                    try {
                        ct.go(true);
                    }
                    catch (final DocumentException e2) {
                        throw new ExceptionConverter(e2);
                    }
                    float calcHeight = -ct.getYLine();
                    if (netWidth <= 0.0f || netHeight <= 0.0f) {
                        calcHeight = 0.0f;
                    }
                    if (calcHeight > 0.0f) {
                        if (cell.isUseDescender()) {
                            calcHeight -= ct.getDescender();
                        }
                        ct = ColumnText.duplicate(cell.getColumn());
                        ct.setCanvases(canvases);
                        ct.setSimpleColumn(-0.003f, -0.001f, netWidth + 0.003f, calcHeight);
                        if (cell.getRotation() == 90) {
                            final float pivotY = cell.getTop() + yPos - currentMaxHeight + cell.getEffectivePaddingBottom();
                            float pivotX = 0.0f;
                            switch (cell.getVerticalAlignment()) {
                                case 6: {
                                    pivotX = cell.getLeft() + xPos + cell.getWidth() - cell.getEffectivePaddingRight();
                                    break;
                                }
                                case 5: {
                                    pivotX = cell.getLeft() + xPos + (cell.getWidth() + cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight() + calcHeight) / 2.0f;
                                    break;
                                }
                                default: {
                                    pivotX = cell.getLeft() + xPos + cell.getEffectivePaddingLeft() + calcHeight;
                                    break;
                                }
                            }
                            this.saveAndRotateCanvases(canvases, 0.0f, 1.0f, -1.0f, 0.0f, pivotX, pivotY);
                        }
                        else {
                            final float pivotY = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                            float pivotX = 0.0f;
                            switch (cell.getVerticalAlignment()) {
                                case 6: {
                                    pivotX = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                                    break;
                                }
                                case 5: {
                                    pivotX = cell.getLeft() + xPos + (cell.getWidth() + cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight() - calcHeight) / 2.0f;
                                    break;
                                }
                                default: {
                                    pivotX = cell.getLeft() + xPos + cell.getWidth() - cell.getEffectivePaddingRight() - calcHeight;
                                    break;
                                }
                            }
                            this.saveAndRotateCanvases(canvases, 0.0f, -1.0f, 1.0f, 0.0f, pivotX, pivotY);
                        }
                        try {
                            ct.go();
                        }
                        catch (final DocumentException e3) {
                            throw new ExceptionConverter(e3);
                        }
                        finally {
                            this.restoreCanvases(canvases);
                        }
                    }
                }
                else {
                    final float fixedHeight = cell.getFixedHeight();
                    float rightLimit = cell.getRight() + xPos - cell.getEffectivePaddingRight();
                    float leftLimit = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                    if (cell.isNoWrap()) {
                        switch (cell.getHorizontalAlignment()) {
                            case 1: {
                                rightLimit += 10000.0f;
                                leftLimit -= 10000.0f;
                                break;
                            }
                            case 2: {
                                if (cell.getRotation() == 180) {
                                    rightLimit += 20000.0f;
                                    break;
                                }
                                leftLimit -= 20000.0f;
                                break;
                            }
                            default: {
                                if (cell.getRotation() == 180) {
                                    leftLimit -= 20000.0f;
                                    break;
                                }
                                rightLimit += 20000.0f;
                                break;
                            }
                        }
                    }
                    final ColumnText ct2 = ColumnText.duplicate(cell.getColumn());
                    ct2.setCanvases(canvases);
                    float bry = tly - (currentMaxHeight - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom());
                    if (fixedHeight > 0.0f && cell.getHeight() > currentMaxHeight) {
                        tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                        bry = cell.getTop() + yPos - currentMaxHeight + cell.getEffectivePaddingBottom();
                    }
                    if ((tly > bry || ct2.zeroHeightElement()) && leftLimit < rightLimit) {
                        ct2.setSimpleColumn(leftLimit, bry - 0.001f, rightLimit, tly);
                        if (cell.getRotation() == 180) {
                            final float shx = leftLimit + rightLimit;
                            final float shy = yPos + yPos - currentMaxHeight + cell.getEffectivePaddingBottom() - cell.getEffectivePaddingTop();
                            this.saveAndRotateCanvases(canvases, -1.0f, 0.0f, 0.0f, -1.0f, shx, shy);
                        }
                        try {
                            ct2.go();
                        }
                        catch (final DocumentException e4) {
                            throw new ExceptionConverter(e4);
                        }
                        finally {
                            if (cell.getRotation() == 180) {
                                this.restoreCanvases(canvases);
                            }
                        }
                    }
                }
                final PdfPCellEvent evt = cell.getCellEvent();
                if (evt != null) {
                    final Rectangle rect = new Rectangle(cell.getLeft() + xPos, cell.getTop() + yPos - currentMaxHeight, cell.getRight() + xPos, cell.getTop() + yPos);
                    evt.cellLayout(cell, rect, canvases);
                }
            }
        }
    }
    
    public boolean isCalculated() {
        return this.calculated;
    }
    
    public float getMaxHeights() {
        if (this.calculated) {
            return this.maxHeight;
        }
        return this.calculateHeights();
    }
    
    public void setMaxHeights(final float maxHeight) {
        this.maxHeight = maxHeight;
    }
    
    float[] getEventWidth(final float xPos) {
        int n = 0;
        for (int k = 0; k < this.cells.length; ++k) {
            if (this.cells[k] != null) {
                ++n;
            }
        }
        final float[] width = new float[n + 1];
        n = 0;
        width[n++] = xPos;
        for (int i = 0; i < this.cells.length; ++i) {
            if (this.cells[i] != null) {
                width[n] = width[n - 1] + this.cells[i].getWidth();
                ++n;
            }
        }
        return width;
    }
    
    public PdfPRow splitRow(final PdfPTable table, final int rowIndex, final float new_height) {
        final PdfPCell[] newCells = new PdfPCell[this.cells.length];
        final float[] fixHs = new float[this.cells.length];
        final float[] minHs = new float[this.cells.length];
        boolean allEmpty = true;
        for (int k = 0; k < this.cells.length; ++k) {
            float newHeight = new_height;
            final PdfPCell cell = this.cells[k];
            if (cell == null) {
                int index = rowIndex;
                if (table.rowSpanAbove(index, k)) {
                    newHeight += table.getRowHeight(index);
                    while (table.rowSpanAbove(--index, k)) {
                        newHeight += table.getRowHeight(index);
                    }
                    final PdfPRow row = table.getRow(index);
                    if (row != null && row.getCells()[k] != null) {
                        (newCells[k] = new PdfPCell(row.getCells()[k])).consumeHeight(newHeight);
                        newCells[k].setRowspan(row.getCells()[k].getRowspan() - rowIndex + index);
                        allEmpty = false;
                    }
                }
            }
            else {
                fixHs[k] = cell.getFixedHeight();
                minHs[k] = cell.getMinimumHeight();
                final Image img = cell.getImage();
                final PdfPCell newCell = new PdfPCell(cell);
                if (img != null) {
                    if (newHeight > cell.getEffectivePaddingBottom() + cell.getEffectivePaddingTop() + 2.0f) {
                        newCell.setPhrase(null);
                        allEmpty = false;
                    }
                }
                else {
                    final ColumnText ct = ColumnText.duplicate(cell.getColumn());
                    final float left = cell.getLeft() + cell.getEffectivePaddingLeft();
                    final float bottom = cell.getTop() + cell.getEffectivePaddingBottom() - newHeight;
                    final float right = cell.getRight() - cell.getEffectivePaddingRight();
                    final float top = cell.getTop() - cell.getEffectivePaddingTop();
                    float y = 0.0f;
                    switch (cell.getRotation()) {
                        case 90:
                        case 270: {
                            y = setColumn(ct, bottom, left, top, right);
                            break;
                        }
                        default: {
                            y = setColumn(ct, left, bottom, cell.isNoWrap() ? 20000.0f : right, top);
                            break;
                        }
                    }
                    int status;
                    try {
                        status = ct.go(true);
                    }
                    catch (final DocumentException e) {
                        throw new ExceptionConverter(e);
                    }
                    final boolean thisEmpty = ct.getYLine() == y;
                    if (thisEmpty) {
                        newCell.setColumn(ColumnText.duplicate(cell.getColumn()));
                        ct.setFilledWidth(0.0f);
                    }
                    else if ((status & 0x1) == 0x0) {
                        newCell.setColumn(ct);
                        ct.setFilledWidth(0.0f);
                    }
                    else {
                        newCell.setPhrase(null);
                    }
                    allEmpty = (allEmpty && thisEmpty);
                }
                newCells[k] = newCell;
                cell.setFixedHeight(newHeight);
            }
        }
        if (allEmpty) {
            for (int k = 0; k < this.cells.length; ++k) {
                final PdfPCell cell2 = this.cells[k];
                if (cell2 != null) {
                    if (fixHs[k] > 0.0f) {
                        cell2.setFixedHeight(fixHs[k]);
                    }
                    else {
                        cell2.setMinimumHeight(minHs[k]);
                    }
                }
            }
            return null;
        }
        this.calculateHeights();
        final PdfPRow split = new PdfPRow(newCells);
        split.widths = this.widths.clone();
        split.calculateHeights();
        return split;
    }
    
    public PdfPCell[] getCells() {
        return this.cells;
    }
}
