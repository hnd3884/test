package org.apache.poi.xwpf.usermodel;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.apache.poi.util.IOUtils;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.OutputStream;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.ooxml.POIXMLFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import java.util.Collections;
import org.apache.poi.util.Internal;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.ArrayList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;
import java.util.List;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public abstract class XWPFHeaderFooter extends POIXMLDocumentPart implements IBody
{
    List<XWPFParagraph> paragraphs;
    List<XWPFTable> tables;
    List<XWPFPictureData> pictures;
    List<IBodyElement> bodyElements;
    CTHdrFtr headerFooter;
    XWPFDocument document;
    
    XWPFHeaderFooter(final XWPFDocument doc, final CTHdrFtr hdrFtr) {
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        this.pictures = new ArrayList<XWPFPictureData>();
        this.bodyElements = new ArrayList<IBodyElement>();
        if (doc == null) {
            throw new NullPointerException();
        }
        this.document = doc;
        this.headerFooter = hdrFtr;
        this.readHdrFtr();
    }
    
    protected XWPFHeaderFooter() {
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        this.pictures = new ArrayList<XWPFPictureData>();
        this.bodyElements = new ArrayList<IBodyElement>();
        this.headerFooter = CTHdrFtr.Factory.newInstance();
        this.readHdrFtr();
    }
    
    public XWPFHeaderFooter(final POIXMLDocumentPart parent, final PackagePart part) {
        super(parent, part);
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        this.pictures = new ArrayList<XWPFPictureData>();
        this.bodyElements = new ArrayList<IBodyElement>();
        this.document = (XWPFDocument)this.getParent();
        if (this.document == null) {
            throw new NullPointerException();
        }
    }
    
    @Override
    protected void onDocumentRead() throws IOException {
        for (final POIXMLDocumentPart poixmlDocumentPart : this.getRelations()) {
            if (poixmlDocumentPart instanceof XWPFPictureData) {
                final XWPFPictureData xwpfPicData = (XWPFPictureData)poixmlDocumentPart;
                this.pictures.add(xwpfPicData);
                this.document.registerPackagePictureData(xwpfPicData);
            }
        }
    }
    
    @Internal
    public CTHdrFtr _getHdrFtr() {
        return this.headerFooter;
    }
    
    @Override
    public List<IBodyElement> getBodyElements() {
        return Collections.unmodifiableList((List<? extends IBodyElement>)this.bodyElements);
    }
    
    @Override
    public List<XWPFParagraph> getParagraphs() {
        return Collections.unmodifiableList((List<? extends XWPFParagraph>)this.paragraphs);
    }
    
    @Override
    public List<XWPFTable> getTables() throws ArrayIndexOutOfBoundsException {
        return Collections.unmodifiableList((List<? extends XWPFTable>)this.tables);
    }
    
    public String getText() {
        final StringBuilder t = new StringBuilder(64);
        for (int i = 0; i < this.paragraphs.size(); ++i) {
            if (!this.paragraphs.get(i).isEmpty()) {
                final String text = this.paragraphs.get(i).getText();
                if (text != null && text.length() > 0) {
                    t.append(text);
                    t.append('\n');
                }
            }
        }
        for (int i = 0; i < this.tables.size(); ++i) {
            final String text = this.tables.get(i).getText();
            if (text != null && text.length() > 0) {
                t.append(text);
                t.append('\n');
            }
        }
        for (final IBodyElement bodyElement : this.getBodyElements()) {
            if (bodyElement instanceof XWPFSDT) {
                t.append(((XWPFSDT)bodyElement).getContent().getText() + '\n');
            }
        }
        return t.toString();
    }
    
    public void setHeaderFooter(final CTHdrFtr headerFooter) {
        this.headerFooter = headerFooter;
        this.readHdrFtr();
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
    
    public List<XWPFParagraph> getListParagraph() {
        return this.paragraphs;
    }
    
    public List<XWPFPictureData> getAllPictures() {
        return Collections.unmodifiableList((List<? extends XWPFPictureData>)this.pictures);
    }
    
    public List<XWPFPictureData> getAllPackagePictures() {
        return this.document.getAllPackagePictures();
    }
    
    public String addPictureData(final byte[] pictureData, final int format) throws InvalidFormatException {
        XWPFPictureData xwpfPicData = this.document.findPackagePictureData(pictureData, format);
        final POIXMLRelation relDesc = XWPFPictureData.RELATIONS[format];
        if (xwpfPicData == null) {
            final int idx = this.document.getNextPicNameNumber(format);
            xwpfPicData = (XWPFPictureData)this.createRelationship(relDesc, XWPFFactory.getInstance(), idx);
            final PackagePart picDataPart = xwpfPicData.getPackagePart();
            try (final OutputStream out = picDataPart.getOutputStream()) {
                out.write(pictureData);
            }
            catch (final IOException e) {
                throw new POIXMLException(e);
            }
            this.document.registerPackagePictureData(xwpfPicData);
            this.pictures.add(xwpfPicData);
            return this.getRelationId(xwpfPicData);
        }
        if (!this.getRelations().contains(xwpfPicData)) {
            final RelationPart rp = this.addRelation(null, XWPFRelation.IMAGES, xwpfPicData);
            this.pictures.add(xwpfPicData);
            return rp.getRelationship().getId();
        }
        return this.getRelationId(xwpfPicData);
    }
    
    public String addPictureData(final InputStream is, final int format) throws InvalidFormatException, IOException {
        final byte[] data = IOUtils.toByteArray(is);
        return this.addPictureData(data, format);
    }
    
    public XWPFPictureData getPictureDataByID(final String blipID) {
        final POIXMLDocumentPart relatedPart = this.getRelationById(blipID);
        if (relatedPart != null && relatedPart instanceof XWPFPictureData) {
            return (XWPFPictureData)relatedPart;
        }
        return null;
    }
    
    public XWPFParagraph createParagraph() {
        final XWPFParagraph paragraph = new XWPFParagraph(this.headerFooter.addNewP(), this);
        this.paragraphs.add(paragraph);
        this.bodyElements.add(paragraph);
        return paragraph;
    }
    
    public XWPFTable createTable(final int rows, final int cols) {
        final XWPFTable table = new XWPFTable(this.headerFooter.addNewTbl(), this, rows, cols);
        this.tables.add(table);
        this.bodyElements.add(table);
        return table;
    }
    
    public void removeParagraph(final XWPFParagraph paragraph) {
        if (this.paragraphs.contains(paragraph)) {
            final CTP ctP = paragraph.getCTP();
            final XmlCursor c = ctP.newCursor();
            c.removeXml();
            c.dispose();
            this.paragraphs.remove(paragraph);
            this.bodyElements.remove(paragraph);
        }
    }
    
    public void removeTable(final XWPFTable table) {
        if (this.tables.contains(table)) {
            final CTTbl ctTbl = table.getCTTbl();
            final XmlCursor c = ctTbl.newCursor();
            c.removeXml();
            c.dispose();
            this.tables.remove(table);
            this.bodyElements.remove(table);
        }
    }
    
    public void clearHeaderFooter() {
        final XmlCursor c = this.headerFooter.newCursor();
        c.removeXmlContents();
        c.dispose();
        this.paragraphs.clear();
        this.tables.clear();
        this.bodyElements.clear();
    }
    
    @Override
    public XWPFParagraph insertNewParagraph(final XmlCursor cursor) {
        if (this.isCursorInHdrF(cursor)) {
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
    
    @Override
    public XWPFTable insertNewTbl(final XmlCursor cursor) {
        if (this.isCursorInHdrF(cursor)) {
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
    
    private boolean isCursorInHdrF(final XmlCursor cursor) {
        final XmlCursor verify = cursor.newCursor();
        verify.toParent();
        final boolean result = verify.getObject() == this.headerFooter;
        verify.dispose();
        return result;
    }
    
    public POIXMLDocumentPart getOwner() {
        return this;
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
        for (final CTTbl tbl : this.headerFooter.getTblArray()) {
            if (tbl == table.getCTTbl()) {
                break;
            }
            ++i;
        }
        this.tables.add(i, table);
    }
    
    public void readHdrFtr() {
        this.bodyElements = new ArrayList<IBodyElement>();
        this.paragraphs = new ArrayList<XWPFParagraph>();
        this.tables = new ArrayList<XWPFTable>();
        final XmlCursor cursor = this.headerFooter.newCursor();
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
        }
        cursor.dispose();
    }
    
    @Override
    public XWPFTableCell getTableCell(final CTTc cell) {
        final XmlCursor cursor = cell.newCursor();
        cursor.toParent();
        XmlObject o = cursor.getObject();
        if (!(o instanceof CTRow)) {
            cursor.dispose();
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
        return tableRow.getTableCell(cell);
    }
    
    @Override
    public XWPFDocument getXWPFDocument() {
        if (this.document != null) {
            return this.document;
        }
        return (XWPFDocument)this.getParent();
    }
    
    public void setXWPFDocument(final XWPFDocument doc) {
        this.document = doc;
    }
    
    @Override
    public POIXMLDocumentPart getPart() {
        return this;
    }
    
    @Override
    protected void prepareForCommit() {
        if (this.bodyElements.size() == 0) {
            this.createParagraph();
        }
        for (final XWPFTable tbl : this.tables) {
            for (final XWPFTableRow row : tbl.tableRows) {
                for (final XWPFTableCell cell : row.getTableCells()) {
                    if (cell.getBodyElements().size() == 0) {
                        cell.addParagraph();
                    }
                }
            }
        }
        super.prepareForCommit();
    }
}
