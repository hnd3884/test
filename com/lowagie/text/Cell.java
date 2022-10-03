package com.lowagie.text;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public class Cell extends Rectangle implements TextElementArray
{
    protected ArrayList arrayList;
    protected int horizontalAlignment;
    protected int verticalAlignment;
    protected float width;
    protected boolean percentage;
    protected int colspan;
    protected int rowspan;
    float leading;
    protected boolean header;
    protected int maxLines;
    String showTruncation;
    protected boolean useAscender;
    protected boolean useDescender;
    protected boolean useBorderPadding;
    protected boolean groupChange;
    
    public Cell() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.arrayList = null;
        this.horizontalAlignment = -1;
        this.verticalAlignment = -1;
        this.percentage = false;
        this.colspan = 1;
        this.rowspan = 1;
        this.leading = Float.NaN;
        this.maxLines = Integer.MAX_VALUE;
        this.useAscender = false;
        this.useDescender = false;
        this.groupChange = true;
        this.setBorder(-1);
        this.setBorderWidth(0.5f);
        this.arrayList = new ArrayList();
    }
    
    public Cell(final boolean dummy) {
        this();
        this.arrayList.add(new Paragraph(0.0f));
    }
    
    public Cell(final String content) {
        this();
        try {
            this.addElement(new Paragraph(content));
        }
        catch (final BadElementException ex) {}
    }
    
    public Cell(final Element element) throws BadElementException {
        this();
        if (element instanceof Phrase) {
            this.setLeading(((Phrase)element).getLeading());
        }
        this.addElement(element);
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
        return 20;
    }
    
    @Override
    public ArrayList getChunks() {
        final ArrayList tmp = new ArrayList();
        final Iterator i = this.arrayList.iterator();
        while (i.hasNext()) {
            tmp.addAll(i.next().getChunks());
        }
        return tmp;
    }
    
    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }
    
    public void setHorizontalAlignment(final int value) {
        this.horizontalAlignment = value;
    }
    
    public void setHorizontalAlignment(final String alignment) {
        this.setHorizontalAlignment(ElementTags.alignmentValue(alignment));
    }
    
    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }
    
    public void setVerticalAlignment(final int value) {
        this.verticalAlignment = value;
    }
    
    public void setVerticalAlignment(final String alignment) {
        this.setVerticalAlignment(ElementTags.alignmentValue(alignment));
    }
    
    public void setWidth(final float value) {
        this.width = value;
    }
    
    public void setWidth(String value) {
        if (value.endsWith("%")) {
            value = value.substring(0, value.length() - 1);
            this.percentage = true;
        }
        this.width = (float)Integer.parseInt(value);
    }
    
    @Override
    public float getWidth() {
        return this.width;
    }
    
    public String getWidthAsString() {
        String w = String.valueOf(this.width);
        if (w.endsWith(".0")) {
            w = w.substring(0, w.length() - 2);
        }
        if (this.percentage) {
            w += "%";
        }
        return w;
    }
    
    public void setColspan(final int value) {
        this.colspan = value;
    }
    
    public int getColspan() {
        return this.colspan;
    }
    
    public void setRowspan(final int value) {
        this.rowspan = value;
    }
    
    public int getRowspan() {
        return this.rowspan;
    }
    
    public void setLeading(final float value) {
        this.leading = value;
    }
    
    public float getLeading() {
        if (Float.isNaN(this.leading)) {
            return 16.0f;
        }
        return this.leading;
    }
    
    public void setHeader(final boolean value) {
        this.header = value;
    }
    
    public boolean isHeader() {
        return this.header;
    }
    
    public void setMaxLines(final int value) {
        this.maxLines = value;
    }
    
    public int getMaxLines() {
        return this.maxLines;
    }
    
    public void setShowTruncation(final String value) {
        this.showTruncation = value;
    }
    
    public String getShowTruncation() {
        return this.showTruncation;
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
    
    public boolean getGroupChange() {
        return this.groupChange;
    }
    
    public void setGroupChange(final boolean value) {
        this.groupChange = value;
    }
    
    public int size() {
        return this.arrayList.size();
    }
    
    public Iterator getElements() {
        return this.arrayList.iterator();
    }
    
    public void clear() {
        this.arrayList.clear();
    }
    
    public boolean isEmpty() {
        switch (this.size()) {
            case 0: {
                return true;
            }
            case 1: {
                final Element element = this.arrayList.get(0);
                switch (element.type()) {
                    case 10: {
                        return ((Chunk)element).isEmpty();
                    }
                    case 11:
                    case 12:
                    case 17: {
                        return ((Phrase)element).isEmpty();
                    }
                    case 14: {
                        return ((List)element).isEmpty();
                    }
                    default: {
                        return false;
                    }
                }
                break;
            }
            default: {
                return false;
            }
        }
    }
    
    void fill() {
        if (this.size() == 0) {
            this.arrayList.add(new Paragraph(0.0f));
        }
    }
    
    public boolean isTable() {
        return this.size() == 1 && this.arrayList.get(0).type() == 22;
    }
    
    public void addElement(final Element element) throws BadElementException {
        if (this.isTable()) {
            final Table table = this.arrayList.get(0);
            final Cell tmp = new Cell(element);
            tmp.setBorder(0);
            tmp.setColspan(table.getColumns());
            table.addCell(tmp);
            return;
        }
        switch (element.type()) {
            case 15:
            case 20:
            case 21: {
                throw new BadElementException(MessageLocalization.getComposedMessage("you.can.t.add.listitems.rows.or.cells.to.a.cell"));
            }
            case 14: {
                final List list = (List)element;
                if (Float.isNaN(this.leading)) {
                    this.setLeading(list.getTotalLeading());
                }
                if (list.isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            }
            case 11:
            case 12:
            case 17: {
                final Phrase p = (Phrase)element;
                if (Float.isNaN(this.leading)) {
                    this.setLeading(p.getLeading());
                }
                if (p.isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            }
            case 10: {
                if (((Chunk)element).isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            }
            case 22: {
                final Table table2 = new Table(3);
                final float[] widths = { 0.0f, ((Table)element).getWidth(), 0.0f };
                switch (((Table)element).getAlignment()) {
                    case 0: {
                        widths[0] = 0.0f;
                        widths[2] = 100.0f - widths[1];
                        break;
                    }
                    case 1: {
                        widths[2] = (widths[0] = (100.0f - widths[1]) / 2.0f);
                        break;
                    }
                    case 2: {
                        widths[0] = 100.0f - widths[1];
                        widths[2] = 0.0f;
                        break;
                    }
                }
                table2.setWidths(widths);
                if (this.arrayList.isEmpty()) {
                    table2.addCell(getDummyCell());
                }
                else {
                    final Cell tmp2 = new Cell();
                    tmp2.setBorder(0);
                    tmp2.setColspan(3);
                    final Iterator i = this.arrayList.iterator();
                    while (i.hasNext()) {
                        tmp2.add(i.next());
                    }
                    table2.addCell(tmp2);
                }
                Cell tmp2 = new Cell();
                tmp2.setBorder(0);
                table2.addCell(tmp2);
                table2.insertTable((Table)element);
                tmp2 = new Cell();
                tmp2.setBorder(0);
                table2.addCell(tmp2);
                table2.addCell(getDummyCell());
                this.clear();
                this.arrayList.add(table2);
                return;
            }
            default: {
                this.arrayList.add(element);
            }
        }
    }
    
    @Override
    public boolean add(final Object o) {
        try {
            this.addElement((Element)o);
            return true;
        }
        catch (final ClassCastException cce) {
            throw new ClassCastException(MessageLocalization.getComposedMessage("you.can.only.add.objects.that.implement.the.element.interface"));
        }
        catch (final BadElementException bee) {
            throw new ClassCastException(bee.getMessage());
        }
    }
    
    private static Cell getDummyCell() {
        final Cell cell = new Cell(true);
        cell.setColspan(3);
        cell.setBorder(0);
        return cell;
    }
    
    public PdfPCell createPdfPCell() throws BadElementException {
        if (this.rowspan > 1) {
            throw new BadElementException(MessageLocalization.getComposedMessage("pdfpcells.can.t.have.a.rowspan.gt.1"));
        }
        if (this.isTable()) {
            return new PdfPCell(this.arrayList.get(0).createPdfPTable());
        }
        final PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(this.verticalAlignment);
        cell.setHorizontalAlignment(this.horizontalAlignment);
        cell.setColspan(this.colspan);
        cell.setUseBorderPadding(this.useBorderPadding);
        cell.setUseDescender(this.useDescender);
        cell.setLeading(this.getLeading(), 0.0f);
        cell.cloneNonPositionParameters(this);
        cell.setNoWrap(this.getMaxLines() == 1);
        final Iterator i = this.getElements();
        while (i.hasNext()) {
            Element e = i.next();
            if (e.type() == 11 || e.type() == 12) {
                final Paragraph p = new Paragraph((Phrase)e);
                p.setAlignment(this.horizontalAlignment);
                e = p;
            }
            cell.addElement(e);
        }
        return cell;
    }
    
    @Override
    public float getTop() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }
    
    @Override
    public float getBottom() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }
    
    @Override
    public float getLeft() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }
    
    @Override
    public float getRight() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }
    
    public float top(final int margin) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }
    
    public float bottom(final int margin) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }
    
    public float left(final int margin) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }
    
    public float right(final int margin) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }
    
    public void setTop(final int value) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.are.attributed.automagically.see.the.faq"));
    }
    
    public void setBottom(final int value) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.are.attributed.automagically.see.the.faq"));
    }
    
    public void setLeft(final int value) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.are.attributed.automagically.see.the.faq"));
    }
    
    public void setRight(final int value) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.are.attributed.automagically.see.the.faq"));
    }
}
