package org.apache.poi.xssf.usermodel;

import org.apache.xmlbeans.XmlException;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCache;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSSFPivotCache extends POIXMLDocumentPart
{
    private CTPivotCache ctPivotCache;
    
    public XSSFPivotCache() {
        this.ctPivotCache = CTPivotCache.Factory.newInstance();
    }
    
    public XSSFPivotCache(final CTPivotCache ctPivotCache) {
        this.ctPivotCache = ctPivotCache;
    }
    
    protected XSSFPivotCache(final PackagePart part) throws IOException {
        super(part);
        this.readFrom(part.getInputStream());
    }
    
    protected void readFrom(final InputStream is) throws IOException {
        try {
            final XmlOptions options = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            options.setLoadReplaceDocumentElement((QName)null);
            this.ctPivotCache = CTPivotCache.Factory.parse(is, options);
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    public CTPivotCache getCTPivotCache() {
        return this.ctPivotCache;
    }
}
