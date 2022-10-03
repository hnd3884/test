package com.lowagie.text;

import java.awt.Color;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import java.util.Iterator;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;
import com.lowagie.text.pdf.PdfPCellEvent;

public class SimpleCell extends Rectangle implements PdfPCellEvent, TextElementArray
{
    public static final boolean ROW = true;
    public static final boolean CELL = false;
    private ArrayList content;
    private float width;
    private float widthpercentage;
    private float spacing_left;
    private float spacing_right;
    private float spacing_top;
    private float spacing_bottom;
    private float padding_left;
    private float padding_right;
    private float padding_top;
    private float padding_bottom;
    private int colspan;
    private int horizontalAlignment;
    private int verticalAlignment;
    private boolean cellgroup;
    protected boolean useAscender;
    protected boolean useDescender;
    protected boolean useBorderPadding;
    
    public SimpleCell(final boolean row) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.content = new ArrayList();
        this.width = 0.0f;
        this.widthpercentage = 0.0f;
        this.spacing_left = Float.NaN;
        this.spacing_right = Float.NaN;
        this.spacing_top = Float.NaN;
        this.spacing_bottom = Float.NaN;
        this.padding_left = Float.NaN;
        this.padding_right = Float.NaN;
        this.padding_top = Float.NaN;
        this.padding_bottom = Float.NaN;
        this.colspan = 1;
        this.horizontalAlignment = -1;
        this.verticalAlignment = -1;
        this.cellgroup = false;
        this.useAscender = false;
        this.useDescender = false;
        this.cellgroup = row;
        this.setBorder(15);
    }
    
    public void addElement(final Element element) throws BadElementException {
        if (this.cellgroup) {
            if (!(element instanceof SimpleCell)) {
                throw new BadElementException(MessageLocalization.getComposedMessage("you.can.only.add.cells.to.rows.no.objects.of.type.1", element.getClass().getName()));
            }
            if (((SimpleCell)element).isCellgroup()) {
                throw new BadElementException(MessageLocalization.getComposedMessage("you.can.t.add.one.row.to.another.row"));
            }
            this.content.add(element);
        }
        else {
            if (element.type() == 12 || element.type() == 11 || element.type() == 17 || element.type() == 10 || element.type() == 14 || element.type() == 50 || element.type() == 32 || element.type() == 33 || element.type() == 36 || element.type() == 34 || element.type() == 35) {
                this.content.add(element);
                return;
            }
            throw new BadElementException(MessageLocalization.getComposedMessage("you.can.t.add.an.element.of.type.1.to.a.simplecell", element.getClass().getName()));
        }
    }
    
    public Cell createCell(final SimpleCell rowAttributes) throws BadElementException {
        final Cell cell = new Cell();
        cell.cloneNonPositionParameters(rowAttributes);
        cell.softCloneNonPositionParameters(this);
        cell.setColspan(this.colspan);
        cell.setHorizontalAlignment(this.horizontalAlignment);
        cell.setVerticalAlignment(this.verticalAlignment);
        cell.setUseAscender(this.useAscender);
        cell.setUseBorderPadding(this.useBorderPadding);
        cell.setUseDescender(this.useDescender);
        for (final Element element : this.content) {
            cell.addElement(element);
        }
        return cell;
    }
    
    public PdfPCell createPdfPCell(final SimpleCell rowAttributes) {
        final PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        final SimpleCell tmp = new SimpleCell(false);
        tmp.setSpacing_left(this.spacing_left);
        tmp.setSpacing_right(this.spacing_right);
        tmp.setSpacing_top(this.spacing_top);
        tmp.setSpacing_bottom(this.spacing_bottom);
        tmp.cloneNonPositionParameters(rowAttributes);
        tmp.softCloneNonPositionParameters(this);
        cell.setCellEvent(tmp);
        cell.setHorizontalAlignment(rowAttributes.horizontalAlignment);
        cell.setVerticalAlignment(rowAttributes.verticalAlignment);
        cell.setUseAscender(rowAttributes.useAscender);
        cell.setUseBorderPadding(rowAttributes.useBorderPadding);
        cell.setUseDescender(rowAttributes.useDescender);
        cell.setColspan(this.colspan);
        if (this.horizontalAlignment != -1) {
            cell.setHorizontalAlignment(this.horizontalAlignment);
        }
        if (this.verticalAlignment != -1) {
            cell.setVerticalAlignment(this.verticalAlignment);
        }
        if (this.useAscender) {
            cell.setUseAscender(this.useAscender);
        }
        if (this.useBorderPadding) {
            cell.setUseBorderPadding(this.useBorderPadding);
        }
        if (this.useDescender) {
            cell.setUseDescender(this.useDescender);
        }
        float sp_left = this.spacing_left;
        if (Float.isNaN(sp_left)) {
            sp_left = 0.0f;
        }
        float sp_right = this.spacing_right;
        if (Float.isNaN(sp_right)) {
            sp_right = 0.0f;
        }
        float sp_top = this.spacing_top;
        if (Float.isNaN(sp_top)) {
            sp_top = 0.0f;
        }
        float sp_bottom = this.spacing_bottom;
        if (Float.isNaN(sp_bottom)) {
            sp_bottom = 0.0f;
        }
        float p = this.padding_left;
        if (Float.isNaN(p)) {
            p = 0.0f;
        }
        cell.setPaddingLeft(p + sp_left);
        p = this.padding_right;
        if (Float.isNaN(p)) {
            p = 0.0f;
        }
        cell.setPaddingRight(p + sp_right);
        p = this.padding_top;
        if (Float.isNaN(p)) {
            p = 0.0f;
        }
        cell.setPaddingTop(p + sp_top);
        p = this.padding_bottom;
        if (Float.isNaN(p)) {
            p = 0.0f;
        }
        cell.setPaddingBottom(p + sp_bottom);
        for (final Element element : this.content) {
            cell.addElement(element);
        }
        return cell;
    }
    
    @Override
    public void cellLayout(final PdfPCell cell, final Rectangle position, final PdfContentByte[] canvases) {
        float sp_left = this.spacing_left;
        if (Float.isNaN(sp_left)) {
            sp_left = 0.0f;
        }
        float sp_right = this.spacing_right;
        if (Float.isNaN(sp_right)) {
            sp_right = 0.0f;
        }
        float sp_top = this.spacing_top;
        if (Float.isNaN(sp_top)) {
            sp_top = 0.0f;
        }
        float sp_bottom = this.spacing_bottom;
        if (Float.isNaN(sp_bottom)) {
            sp_bottom = 0.0f;
        }
        final Rectangle rect = new Rectangle(position.getLeft(sp_left), position.getBottom(sp_bottom), position.getRight(sp_right), position.getTop(sp_top));
        rect.cloneNonPositionParameters(this);
        canvases[1].rectangle(rect);
        rect.setBackgroundColor(null);
        canvases[2].rectangle(rect);
    }
    
    public void setPadding(final float padding) {
        if (Float.isNaN(this.padding_right)) {
            this.setPadding_right(padding);
        }
        if (Float.isNaN(this.padding_left)) {
            this.setPadding_left(padding);
        }
        if (Float.isNaN(this.padding_top)) {
            this.setPadding_top(padding);
        }
        if (Float.isNaN(this.padding_bottom)) {
            this.setPadding_bottom(padding);
        }
    }
    
    public int getColspan() {
        return this.colspan;
    }
    
    public void setColspan(final int colspan) {
        if (colspan > 0) {
            this.colspan = colspan;
        }
    }
    
    public float getPadding_bottom() {
        return this.padding_bottom;
    }
    
    public void setPadding_bottom(final float padding_bottom) {
        this.padding_bottom = padding_bottom;
    }
    
    public float getPadding_left() {
        return this.padding_left;
    }
    
    public void setPadding_left(final float padding_left) {
        this.padding_left = padding_left;
    }
    
    public float getPadding_right() {
        return this.padding_right;
    }
    
    public void setPadding_right(final float padding_right) {
        this.padding_right = padding_right;
    }
    
    public float getPadding_top() {
        return this.padding_top;
    }
    
    public void setPadding_top(final float padding_top) {
        this.padding_top = padding_top;
    }
    
    public float getSpacing_left() {
        return this.spacing_left;
    }
    
    public float getSpacing_right() {
        return this.spacing_right;
    }
    
    public float getSpacing_top() {
        return this.spacing_top;
    }
    
    public float getSpacing_bottom() {
        return this.spacing_bottom;
    }
    
    public void setSpacing(final float spacing) {
        this.spacing_left = spacing;
        this.spacing_right = spacing;
        this.spacing_top = spacing;
        this.spacing_bottom = spacing;
    }
    
    public void setSpacing_left(final float spacing) {
        this.spacing_left = spacing;
    }
    
    public void setSpacing_right(final float spacing) {
        this.spacing_right = spacing;
    }
    
    public void setSpacing_top(final float spacing) {
        this.spacing_top = spacing;
    }
    
    public void setSpacing_bottom(final float spacing) {
        this.spacing_bottom = spacing;
    }
    
    public boolean isCellgroup() {
        return this.cellgroup;
    }
    
    public void setCellgroup(final boolean cellgroup) {
        this.cellgroup = cellgroup;
    }
    
    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
    
    public void setHorizontalAlignment(final int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }
    
    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }
    
    public void setVerticalAlignment(final int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
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
    
    public boolean isUseAscender() {
        return this.useAscender;
    }
    
    public void setUseAscender(final boolean useAscender) {
        this.useAscender = useAscender;
    }
    
    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }
    
    public void setUseBorderPadding(final boolean useBorderPadding) {
        this.useBorderPadding = useBorderPadding;
    }
    
    public boolean isUseDescender() {
        return this.useDescender;
    }
    
    public void setUseDescender(final boolean useDescender) {
        this.useDescender = useDescender;
    }
    
    ArrayList getContent() {
        return this.content;
    }
    
    @Override
    public boolean add(final Object o) {
        try {
            this.addElement((Element)o);
            return true;
        }
        catch (final ClassCastException e) {
            return false;
        }
        catch (final BadElementException e2) {
            throw new ExceptionConverter(e2);
        }
    }
    
    @Override
    public int type() {
        return 20;
    }
}
