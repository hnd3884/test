package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import java.util.Iterator;
import java.util.Collections;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import java.util.ArrayList;
import java.util.List;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import java.util.HashMap;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import java.util.EnumMap;

public class XWPFTableCell implements IBody, ICell
{
    private static EnumMap<XWPFVertAlign, STVerticalJc.Enum> alignMap;
    private static HashMap<Integer, XWPFVertAlign> stVertAlignTypeMap;
    private final CTTc ctTc;
    protected List<XWPFParagraph> paragraphs;
    protected List<XWPFTable> tables;
    protected List<IBodyElement> bodyElements;
    protected IBody part;
    private XWPFTableRow tableRow;
    
    public XWPFTableCell(final CTTc cell, final XWPFTableRow tableRow, final IBody part) {
        this.ctTc = cell;
        this.part = part;
        this.tableRow = tableRow;
        if (cell.sizeOfPArray() < 1) {
            cell.addNewP();
        }
        this.bodyElements = new ArrayList<IBodyElement>();
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        final XmlCursor cursor = this.ctTc.newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            final XmlObject o = cursor.getObject();
            if (o instanceof CTP) {
                final XWPFParagraph p = new XWPFParagraph((CTP)o, this);
                this.paragraphs.add(p);
                this.bodyElements.add(p);
            }
            if (o instanceof CTTbl) {
                final XWPFTable t = new XWPFTable((CTTbl)o, this);
                this.tables.add(t);
                this.bodyElements.add(t);
            }
            if (o instanceof CTSdtBlock) {
                final XWPFSDT c = new XWPFSDT((CTSdtBlock)o, this);
                this.bodyElements.add(c);
            }
            if (o instanceof CTSdtRun) {
                final XWPFSDT c = new XWPFSDT((CTSdtRun)o, this);
                System.out.println(c.getContent().getText());
                this.bodyElements.add(c);
            }
        }
        cursor.dispose();
    }
    
    @Internal
    public CTTc getCTTc() {
        return this.ctTc;
    }
    
    @Override
    public List<IBodyElement> getBodyElements() {
        return Collections.unmodifiableList((List<? extends IBodyElement>)this.bodyElements);
    }
    
    public void setParagraph(final XWPFParagraph p) {
        if (this.ctTc.sizeOfPArray() == 0) {
            this.ctTc.addNewP();
        }
        this.ctTc.setPArray(0, p.getCTP());
    }
    
    @Override
    public List<XWPFParagraph> getParagraphs() {
        return this.paragraphs;
    }
    
    public XWPFParagraph addParagraph() {
        final XWPFParagraph p = new XWPFParagraph(this.ctTc.addNewP(), this);
        this.addParagraph(p);
        return p;
    }
    
    public void addParagraph(final XWPFParagraph p) {
        this.paragraphs.add(p);
    }
    
    public void removeParagraph(final int pos) {
        this.paragraphs.remove(pos);
        this.ctTc.removeP(pos);
    }
    
    @Override
    public XWPFParagraph getParagraph(final CTP p) {
        for (final XWPFParagraph paragraph : this.paragraphs) {
            if (p.equals(paragraph.getCTP())) {
                return paragraph;
            }
        }
        return null;
    }
    
    public XWPFTableRow getTableRow() {
        return this.tableRow;
    }
    
    public String getColor() {
        String color = null;
        final CTTcPr tcpr = this.ctTc.getTcPr();
        if (tcpr != null) {
            final CTShd ctshd = tcpr.getShd();
            if (ctshd != null) {
                color = ctshd.xgetFill().getStringValue();
            }
        }
        return color;
    }
    
    public void setColor(final String rgbStr) {
        final CTTcPr tcpr = this.getTcPr();
        final CTShd ctshd = tcpr.isSetShd() ? tcpr.getShd() : tcpr.addNewShd();
        ctshd.setColor((Object)"auto");
        ctshd.setVal(STShd.CLEAR);
        ctshd.setFill((Object)rgbStr);
    }
    
    public XWPFVertAlign getVerticalAlignment() {
        XWPFVertAlign vAlign = null;
        final CTTcPr tcpr = this.ctTc.getTcPr();
        if (tcpr != null) {
            final CTVerticalJc va = tcpr.getVAlign();
            if (va != null) {
                vAlign = XWPFTableCell.stVertAlignTypeMap.get(va.getVal().intValue());
            }
        }
        return vAlign;
    }
    
    public void setVerticalAlignment(final XWPFVertAlign vAlign) {
        final CTTcPr tcpr = this.getTcPr();
        final CTVerticalJc va = tcpr.addNewVAlign();
        va.setVal((STVerticalJc.Enum)XWPFTableCell.alignMap.get(vAlign));
    }
    
    @Override
    public XWPFParagraph insertNewParagraph(final XmlCursor cursor) {
        if (!this.isCursorInTableCell(cursor)) {
            return null;
        }
        final String uri = CTP.type.getName().getNamespaceURI();
        final String localPart = "p";
        cursor.beginElement(localPart, uri);
        cursor.toParent();
        final CTP p = (CTP)cursor.getObject();
        final XWPFParagraph newP = new XWPFParagraph(p, this);
        XmlObject o;
        for (o = null; !(o instanceof CTP) && cursor.toPrevSibling(); o = cursor.getObject()) {}
        if (!(o instanceof CTP) || o == p) {
            this.paragraphs.add(0, newP);
        }
        else {
            final int pos = this.paragraphs.indexOf(this.getParagraph((CTP)o)) + 1;
            this.paragraphs.add(pos, newP);
        }
        int i = 0;
        XmlCursor p2 = p.newCursor();
        cursor.toCursor(p2);
        p2.dispose();
        while (cursor.toPrevSibling()) {
            o = cursor.getObject();
            if (o instanceof CTP || o instanceof CTTbl) {
                ++i;
            }
        }
        this.bodyElements.add(i, newP);
        p2 = p.newCursor();
        cursor.toCursor(p2);
        p2.dispose();
        cursor.toEndToken();
        return newP;
    }
    
    @Override
    public XWPFTable insertNewTbl(final XmlCursor cursor) {
        if (this.isCursorInTableCell(cursor)) {
            final String uri = CTTbl.type.getName().getNamespaceURI();
            final String localPart = "tbl";
            cursor.beginElement(localPart, uri);
            cursor.toParent();
            final CTTbl t = (CTTbl)cursor.getObject();
            final XWPFTable newT = new XWPFTable(t, this);
            cursor.removeXmlContents();
            XmlObject o;
            for (o = null; !(o instanceof CTTbl) && cursor.toPrevSibling(); o = cursor.getObject()) {}
            if (!(o instanceof CTTbl)) {
                this.tables.add(0, newT);
            }
            else {
                final int pos = this.tables.indexOf(this.getTable((CTTbl)o)) + 1;
                this.tables.add(pos, newT);
            }
            int i = 0;
            XmlCursor cursor2 = t.newCursor();
            while (cursor2.toPrevSibling()) {
                o = cursor2.getObject();
                if (o instanceof CTP || o instanceof CTTbl) {
                    ++i;
                }
            }
            cursor2.dispose();
            this.bodyElements.add(i, newT);
            cursor2 = t.newCursor();
            cursor.toCursor(cursor2);
            cursor.toEndToken();
            cursor2.dispose();
            return newT;
        }
        return null;
    }
    
    private boolean isCursorInTableCell(final XmlCursor cursor) {
        final XmlCursor verify = cursor.newCursor();
        verify.toParent();
        final boolean result = verify.getObject() == this.ctTc;
        verify.dispose();
        return result;
    }
    
    @Override
    public XWPFParagraph getParagraphArray(final int pos) {
        if (pos >= 0 && pos < this.paragraphs.size()) {
            return this.paragraphs.get(pos);
        }
        return null;
    }
    
    @Override
    public POIXMLDocumentPart getPart() {
        return this.tableRow.getTable().getPart();
    }
    
    @Override
    public BodyType getPartType() {
        return BodyType.TABLECELL;
    }
    
    @Override
    public XWPFTable getTable(final CTTbl ctTable) {
        for (int i = 0; i < this.tables.size(); ++i) {
            if (this.getTables().get(i).getCTTbl() == ctTable) {
                return this.getTables().get(i);
            }
        }
        return null;
    }
    
    @Override
    public XWPFTable getTableArray(final int pos) {
        if (pos >= 0 && pos < this.tables.size()) {
            return this.tables.get(pos);
        }
        return null;
    }
    
    @Override
    public List<XWPFTable> getTables() {
        return Collections.unmodifiableList((List<? extends XWPFTable>)this.tables);
    }
    
    @Override
    public void insertTable(final int pos, final XWPFTable table) {
        this.bodyElements.add(pos, table);
        int i = 0;
        for (final CTTbl tbl : this.ctTc.getTblList()) {
            if (tbl == table.getCTTbl()) {
                break;
            }
            ++i;
        }
        this.tables.add(i, table);
    }
    
    public String getText() {
        final StringBuilder text = new StringBuilder();
        for (final XWPFParagraph p : this.paragraphs) {
            text.append(p.getText());
        }
        return text.toString();
    }
    
    public void setText(final String text) {
        final CTP ctP = (this.ctTc.sizeOfPArray() == 0) ? this.ctTc.addNewP() : this.ctTc.getPArray(0);
        final XWPFParagraph par = new XWPFParagraph(ctP, this);
        par.createRun().setText(text);
    }
    
    public String getTextRecursively() {
        final StringBuilder text = new StringBuilder(64);
        for (int i = 0; i < this.bodyElements.size(); ++i) {
            final boolean isLast = i == this.bodyElements.size() - 1;
            this.appendBodyElementText(text, this.bodyElements.get(i), isLast);
        }
        return text.toString();
    }
    
    private void appendBodyElementText(final StringBuilder text, final IBodyElement e, final boolean isLast) {
        if (e instanceof XWPFParagraph) {
            text.append(((XWPFParagraph)e).getText());
            if (!isLast) {
                text.append('\t');
            }
        }
        else if (e instanceof XWPFTable) {
            final XWPFTable eTable = (XWPFTable)e;
            for (final XWPFTableRow row : eTable.getRows()) {
                for (final XWPFTableCell cell : row.getTableCells()) {
                    final List<IBodyElement> localBodyElements = cell.getBodyElements();
                    for (int i = 0; i < localBodyElements.size(); ++i) {
                        final boolean localIsLast = i == localBodyElements.size() - 1;
                        this.appendBodyElementText(text, localBodyElements.get(i), localIsLast);
                    }
                }
            }
            if (!isLast) {
                text.append('\n');
            }
        }
        else if (e instanceof XWPFSDT) {
            text.append(((XWPFSDT)e).getContent().getText());
            if (!isLast) {
                text.append('\t');
            }
        }
    }
    
    @Override
    public XWPFTableCell getTableCell(final CTTc cell) {
        final XmlCursor cursor = cell.newCursor();
        cursor.toParent();
        XmlObject o = cursor.getObject();
        if (!(o instanceof CTRow)) {
            return null;
        }
        final CTRow row = (CTRow)o;
        cursor.toParent();
        o = cursor.getObject();
        cursor.dispose();
        if (!(o instanceof CTTbl)) {
            return null;
        }
        final CTTbl tbl = (CTTbl)o;
        final XWPFTable table = this.getTable(tbl);
        if (table == null) {
            return null;
        }
        final XWPFTableRow tr = table.getRow(row);
        if (tr == null) {
            return null;
        }
        return tr.getTableCell(cell);
    }
    
    @Override
    public XWPFDocument getXWPFDocument() {
        return this.part.getXWPFDocument();
    }
    
    public double getWidthDecimal() {
        return XWPFTable.getWidthDecimal(this.getTcWidth());
    }
    
    public TableWidthType getWidthType() {
        return XWPFTable.getWidthType(this.getTcWidth());
    }
    
    public void setWidth(final String widthValue) {
        XWPFTable.setWidthValue(widthValue, this.getTcWidth());
    }
    
    private CTTblWidth getTcWidth() {
        final CTTcPr tcPr = this.getTcPr();
        return tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();
    }
    
    protected CTTcPr getTcPr() {
        return this.ctTc.isSetTcPr() ? this.ctTc.getTcPr() : this.ctTc.addNewTcPr();
    }
    
    public void setWidthType(final TableWidthType widthType) {
        XWPFTable.setWidthType(widthType, this.getTcWidth());
    }
    
    public int getWidth() {
        return this.getTcWidth().getW().intValue();
    }
    
    static {
        (XWPFTableCell.alignMap = new EnumMap<XWPFVertAlign, STVerticalJc.Enum>(XWPFVertAlign.class)).put(XWPFVertAlign.TOP, STVerticalJc.Enum.forInt(1));
        XWPFTableCell.alignMap.put(XWPFVertAlign.CENTER, STVerticalJc.Enum.forInt(2));
        XWPFTableCell.alignMap.put(XWPFVertAlign.BOTH, STVerticalJc.Enum.forInt(3));
        XWPFTableCell.alignMap.put(XWPFVertAlign.BOTTOM, STVerticalJc.Enum.forInt(4));
        (XWPFTableCell.stVertAlignTypeMap = new HashMap<Integer, XWPFVertAlign>()).put(1, XWPFVertAlign.TOP);
        XWPFTableCell.stVertAlignTypeMap.put(2, XWPFVertAlign.CENTER);
        XWPFTableCell.stVertAlignTypeMap.put(3, XWPFVertAlign.BOTH);
        XWPFTableCell.stVertAlignTypeMap.put(4, XWPFVertAlign.BOTTOM);
    }
    
    public enum XWPFVertAlign
    {
        TOP, 
        CENTER, 
        BOTH, 
        BOTTOM;
    }
}
