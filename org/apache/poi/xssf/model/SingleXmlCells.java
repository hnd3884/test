package org.apache.poi.xssf.model;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCell;
import java.util.Vector;
import org.apache.poi.xssf.usermodel.helpers.XSSFSingleXmlCell;
import java.util.List;
import java.io.OutputStream;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.SingleXmlCellsDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSingleXmlCells;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class SingleXmlCells extends POIXMLDocumentPart
{
    private CTSingleXmlCells singleXMLCells;
    
    public SingleXmlCells() {
        this.singleXMLCells = CTSingleXmlCells.Factory.newInstance();
    }
    
    public SingleXmlCells(final PackagePart part) throws IOException {
        super(part);
        this.readFrom(part.getInputStream());
    }
    
    public void readFrom(final InputStream is) throws IOException {
        try {
            final SingleXmlCellsDocument doc = SingleXmlCellsDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.singleXMLCells = doc.getSingleXmlCells();
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    public XSSFSheet getXSSFSheet() {
        return (XSSFSheet)this.getParent();
    }
    
    protected void writeTo(final OutputStream out) throws IOException {
        final SingleXmlCellsDocument doc = SingleXmlCellsDocument.Factory.newInstance();
        doc.setSingleXmlCells(this.singleXMLCells);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.writeTo(out);
        out.close();
    }
    
    public CTSingleXmlCells getCTSingleXMLCells() {
        return this.singleXMLCells;
    }
    
    public List<XSSFSingleXmlCell> getAllSimpleXmlCell() {
        final List<XSSFSingleXmlCell> list = new Vector<XSSFSingleXmlCell>();
        for (final CTSingleXmlCell singleXmlCell : this.singleXMLCells.getSingleXmlCellArray()) {
            list.add(new XSSFSingleXmlCell(singleXmlCell, this));
        }
        return list;
    }
}
