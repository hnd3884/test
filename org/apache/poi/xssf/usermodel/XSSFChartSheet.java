package org.apache.poi.xssf.usermodel;

import java.io.ByteArrayOutputStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import java.io.OutputStream;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import java.io.IOException;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.ChartsheetDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheet;

public class XSSFChartSheet extends XSSFSheet
{
    private static final byte[] BLANK_WORKSHEET;
    protected CTChartsheet chartsheet;
    
    protected XSSFChartSheet(final PackagePart part) {
        super(part);
    }
    
    @Override
    protected void read(final InputStream is) throws IOException {
        super.read(new ByteArrayInputStream(XSSFChartSheet.BLANK_WORKSHEET));
        try {
            this.chartsheet = ChartsheetDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS).getChartsheet();
        }
        catch (final XmlException e) {
            throw new POIXMLException((Throwable)e);
        }
    }
    
    public CTChartsheet getCTChartsheet() {
        return this.chartsheet;
    }
    
    @Override
    protected CTDrawing getCTDrawing() {
        return this.chartsheet.getDrawing();
    }
    
    @Override
    protected CTLegacyDrawing getCTLegacyDrawing() {
        return this.chartsheet.getLegacyDrawing();
    }
    
    @Override
    protected void write(final OutputStream out) throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTChartsheet.type.getName().getNamespaceURI(), "chartsheet"));
        this.chartsheet.save(out, xmlOptions);
    }
    
    private static byte[] blankWorksheet() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            new XSSFSheet().write(out);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }
    
    static {
        BLANK_WORKSHEET = blankWorksheet();
    }
}
