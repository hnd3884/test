package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheField;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheFields;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Name;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheetSource;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.OutputStream;
import java.util.Date;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlException;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotCacheDefinition;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSSFPivotCacheDefinition extends POIXMLDocumentPart
{
    private CTPivotCacheDefinition ctPivotCacheDefinition;
    
    public XSSFPivotCacheDefinition() {
        this.ctPivotCacheDefinition = CTPivotCacheDefinition.Factory.newInstance();
        this.createDefaultValues();
    }
    
    protected XSSFPivotCacheDefinition(final PackagePart part) throws IOException {
        super(part);
        this.readFrom(part.getInputStream());
    }
    
    public void readFrom(final InputStream is) throws IOException {
        try {
            final XmlOptions options = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            options.setLoadReplaceDocumentElement((QName)null);
            this.ctPivotCacheDefinition = CTPivotCacheDefinition.Factory.parse(is, options);
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage(), (Throwable)e);
        }
    }
    
    @Internal
    public CTPivotCacheDefinition getCTPivotCacheDefinition() {
        return this.ctPivotCacheDefinition;
    }
    
    private void createDefaultValues() {
        this.ctPivotCacheDefinition.setCreatedVersion((short)3);
        this.ctPivotCacheDefinition.setMinRefreshableVersion((short)3);
        this.ctPivotCacheDefinition.setRefreshedVersion((short)3);
        this.ctPivotCacheDefinition.setRefreshedBy("Apache POI");
        this.ctPivotCacheDefinition.setRefreshedDate((double)new Date().getTime());
        this.ctPivotCacheDefinition.setRefreshOnLoad(true);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTPivotCacheDefinition.type.getName().getNamespaceURI(), "pivotCacheDefinition"));
        this.ctPivotCacheDefinition.save(out, xmlOptions);
        out.close();
    }
    
    public AreaReference getPivotArea(final Workbook wb) throws IllegalArgumentException {
        final CTWorksheetSource wsSource = this.ctPivotCacheDefinition.getCacheSource().getWorksheetSource();
        final String ref = wsSource.getRef();
        final String name = wsSource.getName();
        if (ref == null && name == null) {
            throw new IllegalArgumentException("Pivot cache must reference an area, named range, or table.");
        }
        if (ref != null) {
            return new AreaReference(ref, SpreadsheetVersion.EXCEL2007);
        }
        assert name != null;
        final Name range = wb.getName(name);
        if (range != null) {
            return new AreaReference(range.getRefersToFormula(), SpreadsheetVersion.EXCEL2007);
        }
        final XSSFSheet sheet = (XSSFSheet)wb.getSheet(wsSource.getSheet());
        for (final XSSFTable table : sheet.getTables()) {
            if (name.equals(table.getName())) {
                return new AreaReference(table.getStartCellReference(), table.getEndCellReference(), SpreadsheetVersion.EXCEL2007);
            }
        }
        throw new IllegalArgumentException("Name '" + name + "' was not found.");
    }
    
    protected void createCacheFields(final Sheet sheet) {
        final AreaReference ar = this.getPivotArea(sheet.getWorkbook());
        final CellReference firstCell = ar.getFirstCell();
        final CellReference lastCell = ar.getLastCell();
        final int columnStart = firstCell.getCol();
        final int columnEnd = lastCell.getCol();
        final Row row = sheet.getRow(firstCell.getRow());
        CTCacheFields cFields;
        if (this.ctPivotCacheDefinition.getCacheFields() != null) {
            cFields = this.ctPivotCacheDefinition.getCacheFields();
        }
        else {
            cFields = this.ctPivotCacheDefinition.addNewCacheFields();
        }
        for (int i = columnStart; i <= columnEnd; ++i) {
            final CTCacheField cf = cFields.addNewCacheField();
            if (i == columnEnd) {
                cFields.setCount((long)cFields.sizeOfCacheFieldArray());
            }
            cf.setNumFmtId(0L);
            cf.setName(row.getCell(i).getStringCellValue());
            cf.addNewSharedItems();
        }
    }
}
