package com.lowagie.text;

import java.awt.Color;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import java.util.Iterator;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;
import com.lowagie.text.pdf.PdfPTableEvent;

public class SimpleTable extends Rectangle implements PdfPTableEvent, TextElementArray
{
    private ArrayList content;
    private float width;
    private float widthpercentage;
    private float cellspacing;
    private float cellpadding;
    private int alignment;
    
    public SimpleTable() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.content = new ArrayList();
        this.width = 0.0f;
        this.widthpercentage = 0.0f;
        this.setBorder(15);
        this.setBorderWidth(2.0f);
    }
    
    public void addElement(final SimpleCell element) throws BadElementException {
        if (!element.isCellgroup()) {
            throw new BadElementException(MessageLocalization.getComposedMessage("you.can.t.add.cells.to.a.table.directly.add.them.to.a.row.first"));
        }
        this.content.add(element);
    }
    
    public Table createTable() throws BadElementException {
        if (this.content.isEmpty()) {
            throw new BadElementException(MessageLocalization.getComposedMessage("trying.to.create.a.table.without.rows"));
        }
        SimpleCell row = this.content.get(0);
        int columns = 0;
        for (final SimpleCell cell : row.getContent()) {
            columns += cell.getColspan();
        }
        final float[] widths = new float[columns];
        final float[] widthpercentages = new float[columns];
        final Table table = new Table(columns);
        table.setAlignment(this.alignment);
        table.setSpacing(this.cellspacing);
        table.setPadding(this.cellpadding);
        table.cloneNonPositionParameters(this);
        final Iterator rows = this.content.iterator();
        while (rows.hasNext()) {
            row = rows.next();
            int pos = 0;
            for (final SimpleCell cell : row.getContent()) {
                table.addCell(cell.createCell(row));
                if (cell.getColspan() == 1) {
                    if (cell.getWidth() > 0.0f) {
                        widths[pos] = cell.getWidth();
                    }
                    if (cell.getWidthpercentage() > 0.0f) {
                        widthpercentages[pos] = cell.getWidthpercentage();
                    }
                }
                pos += cell.getColspan();
            }
        }
        float sumWidths = 0.0f;
        for (int j = 0; j < columns; ++j) {
            if (widths[j] == 0.0f) {
                sumWidths = 0.0f;
                break;
            }
            sumWidths += widths[j];
        }
        if (sumWidths > 0.0f) {
            table.setWidth(sumWidths);
            table.setLocked(true);
            table.setWidths(widths);
        }
        else {
            for (int j = 0; j < columns; ++j) {
                if (widthpercentages[j] == 0.0f) {
                    sumWidths = 0.0f;
                    break;
                }
                sumWidths += widthpercentages[j];
            }
            if (sumWidths > 0.0f) {
                table.setWidths(widthpercentages);
            }
        }
        if (this.width > 0.0f) {
            table.setWidth(this.width);
            table.setLocked(true);
        }
        else if (this.widthpercentage > 0.0f) {
            table.setWidth(this.widthpercentage);
        }
        return table;
    }
    
    public PdfPTable createPdfPTable() throws DocumentException {
        if (this.content.isEmpty()) {
            throw new BadElementException(MessageLocalization.getComposedMessage("trying.to.create.a.table.without.rows"));
        }
        SimpleCell row = this.content.get(0);
        int columns = 0;
        for (final SimpleCell cell : row.getContent()) {
            columns += cell.getColspan();
        }
        final float[] widths = new float[columns];
        final float[] widthpercentages = new float[columns];
        final PdfPTable table = new PdfPTable(columns);
        table.setTableEvent(this);
        table.setHorizontalAlignment(this.alignment);
        final Iterator rows = this.content.iterator();
        while (rows.hasNext()) {
            row = rows.next();
            int pos = 0;
            for (final SimpleCell cell : row.getContent()) {
                if (Float.isNaN(cell.getSpacing_left())) {
                    cell.setSpacing_left(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(cell.getSpacing_right())) {
                    cell.setSpacing_right(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(cell.getSpacing_top())) {
                    cell.setSpacing_top(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(cell.getSpacing_bottom())) {
                    cell.setSpacing_bottom(this.cellspacing / 2.0f);
                }
                cell.setPadding(this.cellpadding);
                table.addCell(cell.createPdfPCell(row));
                if (cell.getColspan() == 1) {
                    if (cell.getWidth() > 0.0f) {
                        widths[pos] = cell.getWidth();
                    }
                    if (cell.getWidthpercentage() > 0.0f) {
                        widthpercentages[pos] = cell.getWidthpercentage();
                    }
                }
                pos += cell.getColspan();
            }
        }
        float sumWidths = 0.0f;
        for (int j = 0; j < columns; ++j) {
            if (widths[j] == 0.0f) {
                sumWidths = 0.0f;
                break;
            }
            sumWidths += widths[j];
        }
        if (sumWidths > 0.0f) {
            table.setTotalWidth(sumWidths);
            table.setWidths(widths);
        }
        else {
            for (int j = 0; j < columns; ++j) {
                if (widthpercentages[j] == 0.0f) {
                    sumWidths = 0.0f;
                    break;
                }
                sumWidths += widthpercentages[j];
            }
            if (sumWidths > 0.0f) {
                table.setWidths(widthpercentages);
            }
        }
        if (this.width > 0.0f) {
            table.setTotalWidth(this.width);
        }
        if (this.widthpercentage > 0.0f) {
            table.setWidthPercentage(this.widthpercentage);
        }
        return table;
    }
    
    @Override
    public void tableLayout(final PdfPTable table, final float[][] widths, final float[] heights, final int headerRows, final int rowStart, final PdfContentByte[] canvases) {
        final float[] width = widths[0];
        final Rectangle rect = new Rectangle(width[0], heights[heights.length - 1], width[width.length - 1], heights[0]);
        rect.cloneNonPositionParameters(this);
        final int bd = rect.getBorder();
        rect.setBorder(0);
        canvases[1].rectangle(rect);
        rect.setBorder(bd);
        rect.setBackgroundColor(null);
        canvases[2].rectangle(rect);
    }
    
    public float getCellpadding() {
        return this.cellpadding;
    }
    
    public void setCellpadding(final float cellpadding) {
        this.cellpadding = cellpadding;
    }
    
    public float getCellspacing() {
        return this.cellspacing;
    }
    
    public void setCellspacing(final float cellspacing) {
        this.cellspacing = cellspacing;
    }
    
    public int getAlignment() {
        return this.alignment;
    }
    
    public void setAlignment(final int alignment) {
        this.alignment = alignment;
    }
    
    @Override
    public float getWidth() {
        return this.width;
    }
    
    public void setWidth(final float width) {
        this.width = width;
    }
    
    public float getWidthpercentage() {
        return this.widthpercentage;
    }
    
    public void setWidthpercentage(final float widthpercentage) {
        this.widthpercentage = widthpercentage;
    }
    
    @Override
    public int type() {
        return 22;
    }
    
    @Override
    public boolean isNestable() {
        return true;
    }
    
    @Override
    public boolean add(final Object o) {
        try {
            this.addElement((SimpleCell)o);
            return true;
        }
        catch (final ClassCastException e) {
            return false;
        }
        catch (final BadElementException e2) {
            throw new ExceptionConverter(e2);
        }
    }
}
