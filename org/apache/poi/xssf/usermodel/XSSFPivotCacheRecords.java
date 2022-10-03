package org.apache.poi.xssf.usermodel;

import java.io.OutputStream;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlException;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheRecords;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSSFPivotCacheRecords extends POIXMLDocumentPart
{
    private CTPivotCacheRecords ctPivotCacheRecords;
    
    public XSSFPivotCacheRecords() {
        this.ctPivotCacheRecords = CTPivotCacheRecords.Factory.newInstance();
    }
    
    protected XSSFPivotCacheRecords(final PackagePart part) throws IOException {
        super(part);
        this.readFrom(part.getInputStream());
    }
    
    protected void readFrom(final InputStream is) throws IOException {
        try {
            final XmlOptions options = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            options.setLoadReplaceDocumentElement((QName)null);
            this.ctPivotCacheRecords = CTPivotCacheRecords.Factory.parse(is, options);
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    @Internal
    public CTPivotCacheRecords getCtPivotCacheRecords() {
        return this.ctPivotCacheRecords;
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTPivotCacheRecords.type.getName().getNamespaceURI(), "pivotCacheRecords"));
        this.ctPivotCacheRecords.save(out, xmlOptions);
        out.close();
    }
}
