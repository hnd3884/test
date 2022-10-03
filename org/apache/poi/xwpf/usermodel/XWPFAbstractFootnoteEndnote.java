package org.apache.poi.xwpf.usermodel;

import java.math.BigInteger;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import java.util.Iterator;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.apache.poi.util.Internal;
import java.util.ArrayList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFtnEdn;
import java.util.List;

public abstract class XWPFAbstractFootnoteEndnote implements Iterable<XWPFParagraph>, IBody
{
    private List<XWPFParagraph> paragraphs;
    private List<XWPFTable> tables;
    private List<XWPFPictureData> pictures;
    private List<IBodyElement> bodyElements;
    protected CTFtnEdn ctFtnEdn;
    protected XWPFAbstractFootnotesEndnotes footnotes;
    protected XWPFDocument document;
    
    public XWPFAbstractFootnoteEndnote() {
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        this.pictures = new ArrayList<XWPFPictureData>();
        this.bodyElements = new ArrayList<IBodyElement>();
    }
    
    @Internal
    protected XWPFAbstractFootnoteEndnote(final XWPFDocument document, final CTFtnEdn body) {
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        this.pictures = new ArrayList<XWPFPictureData>();
        this.bodyElements = new ArrayList<IBodyElement>();
        this.ctFtnEdn = body;
        this.document = document;
        this.init();
    }
    
    @Internal
    protected XWPFAbstractFootnoteEndnote(final CTFtnEdn note, final XWPFAbstractFootnotesEndnotes footnotes) {
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        this.pictures = new ArrayList<XWPFPictureData>();
        this.bodyElements = new ArrayList<IBodyElement>();
        this.footnotes = footnotes;
        this.ctFtnEdn = note;
        this.document = footnotes.getXWPFDocument();
        this.init();
    }
    
    protected void init() {
        final XmlCursor cursor = this.ctFtnEdn.newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            final XmlObject o = cursor.getObject();
            if (o instanceof CTP) {
                final XWPFParagraph p = new XWPFParagraph((CTP)o, this);
                this.bodyElements.add(p);
                this.paragraphs.add(p);
            }
            else if (o instanceof CTTbl) {
                final XWPFTable t = new XWPFTable((CTTbl)o, this);
                this.bodyElements.add(t);
                this.tables.add(t);
            }
            else {
                if (!(o instanceof CTSdtBlock)) {
                    continue;
                }
                final XWPFSDT c = new XWPFSDT((CTSdtBlock)o, this);
                this.bodyElements.add(c);
            }
        }
        cursor.dispose();
    }
    
    @Override
    public List<XWPFParagraph> getParagraphs() {
        return this.paragraphs;
    }
    
    @Override
    public Iterator<XWPFParagraph> iterator() {
        return this.paragraphs.iterator();
    }
    
    @Override
    public List<XWPFTable> getTables() {
        return this.tables;
    }
    
    public List<XWPFPictureData> getPictures() {
        return this.pictures;
    }
    
    @Override
    public List<IBodyElement> getBodyElements() {
        return this.bodyElements;
    }
    
    public CTFtnEdn getCTFtnEdn() {
        return this.ctFtnEdn;
    }
    
    public void setCTFtnEdn(final CTFtnEdn footnote) {
        this.ctFtnEdn = footnote;
    }
    
    @Override
    public XWPFTable getTableArray(final int pos) {
        if (pos >= 0 && pos < this.tables.size()) {
            return this.tables.get(pos);
        }
        return null;
    }
    
    @Override
    public void insertTable(final int pos, final XWPFTable table) {
        this.bodyElements.add(pos, table);
        int i = 0;
        for (final CTTbl tbl : this.ctFtnEdn.getTblList()) {
            if (tbl == table.getCTTbl()) {
                break;
            }
            ++i;
        }
        this.tables.add(i, table);
    }
    
    @Override
    public XWPFTable getTable(final CTTbl ctTable) {
        for (final XWPFTable table : this.tables) {
            if (table == null) {
                return null;
            }
            if (table.getCTTbl().equals(ctTable)) {
                return table;
            }
        }
        return null;
    }
    
    @Override
    public XWPFParagraph getParagraph(final CTP p) {
        for (final XWPFParagraph paragraph : this.paragraphs) {
            if (paragraph.getCTP().equals(p)) {
                return paragraph;
            }
        }
        return null;
    }
    
    @Override
    public XWPFParagraph getParagraphArray(final int pos) {
        if (pos >= 0 && pos < this.paragraphs.size()) {
            return this.paragraphs.get(pos);
        }
        return null;
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
        final XWPFTableRow tableRow = table.getRow(row);
        if (tableRow == null) {
            return null;
        }
        return tableRow.getTableCell(cell);
    }
    
    private boolean isCursorInFtn(final XmlCursor cursor) {
        final XmlCursor verify = cursor.newCursor();
        verify.toParent();
        return verify.getObject() == this.ctFtnEdn;
    }
    
    public POIXMLDocumentPart getOwner() {
        return this.footnotes;
    }
    
    @Override
    public XWPFTable insertNewTbl(XmlCursor cursor) {
        if (this.isCursorInFtn(cursor)) {
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
            cursor = t.newCursor();
            while (cursor.toPrevSibling()) {
                o = cursor.getObject();
                if (o instanceof CTP || o instanceof CTTbl) {
                    ++i;
                }
            }
            this.bodyElements.add(i, newT);
            final XmlCursor c2 = t.newCursor();
            cursor.toCursor(c2);
            cursor.toEndToken();
            c2.dispose();
            return newT;
        }
        return null;
    }
    
    @Override
    public XWPFParagraph insertNewParagraph(final XmlCursor cursor) {
        if (this.isCursorInFtn(cursor)) {
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
            cursor.toEndToken();
            p2.dispose();
            return newP;
        }
        return null;
    }
    
    public XWPFTable addNewTbl(final CTTbl table) {
        final CTTbl newTable = this.ctFtnEdn.addNewTbl();
        newTable.set((XmlObject)table);
        final XWPFTable xTable = new XWPFTable(newTable, this);
        this.tables.add(xTable);
        return xTable;
    }
    
    public XWPFParagraph addNewParagraph(final CTP paragraph) {
        final CTP newPara = this.ctFtnEdn.addNewP();
        newPara.set((XmlObject)paragraph);
        final XWPFParagraph xPara = new XWPFParagraph(newPara, this);
        this.paragraphs.add(xPara);
        return xPara;
    }
    
    @Override
    public XWPFDocument getXWPFDocument() {
        return this.document;
    }
    
    @Override
    public POIXMLDocumentPart getPart() {
        return this.footnotes;
    }
    
    @Override
    public BodyType getPartType() {
        return BodyType.FOOTNOTE;
    }
    
    public BigInteger getId() {
        return this.ctFtnEdn.getId();
    }
    
    public XWPFParagraph createParagraph() {
        final XWPFParagraph p = new XWPFParagraph(this.ctFtnEdn.addNewP(), this);
        this.paragraphs.add(p);
        this.bodyElements.add(p);
        if (p.equals(this.getParagraphs().get(0))) {
            this.ensureFootnoteRef(p);
        }
        return p;
    }
    
    public abstract void ensureFootnoteRef(final XWPFParagraph p0);
    
    public XWPFTable createTable() {
        final XWPFTable table = new XWPFTable(this.ctFtnEdn.addNewTbl(), this);
        if (this.bodyElements.size() == 0) {
            final XWPFParagraph p = this.createParagraph();
            this.ensureFootnoteRef(p);
        }
        this.bodyElements.add(table);
        this.tables.add(table);
        return table;
    }
    
    public XWPFTable createTable(final int rows, final int cols) {
        final XWPFTable table = new XWPFTable(this.ctFtnEdn.addNewTbl(), this, rows, cols);
        this.bodyElements.add(table);
        this.tables.add(table);
        return table;
    }
}
