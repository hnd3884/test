package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.usermodel.CreationHelper;

public class XSSFCreationHelper implements CreationHelper
{
    private final XSSFWorkbook workbook;
    
    @Internal
    public XSSFCreationHelper(final XSSFWorkbook wb) {
        this.workbook = wb;
    }
    
    public XSSFRichTextString createRichTextString(final String text) {
        final XSSFRichTextString rt = new XSSFRichTextString(text);
        rt.setStylesTableReference(this.workbook.getStylesSource());
        return rt;
    }
    
    public XSSFDataFormat createDataFormat() {
        return this.workbook.createDataFormat();
    }
    
    public XSSFColor createExtendedColor() {
        return XSSFColor.from(CTColor.Factory.newInstance(), this.workbook.getStylesSource().getIndexedColors());
    }
    
    public XSSFHyperlink createHyperlink(final HyperlinkType type) {
        return new XSSFHyperlink(type);
    }
    
    public XSSFFormulaEvaluator createFormulaEvaluator() {
        return new XSSFFormulaEvaluator(this.workbook);
    }
    
    public XSSFClientAnchor createClientAnchor() {
        return new XSSFClientAnchor();
    }
    
    public AreaReference createAreaReference(final String reference) {
        return new AreaReference(reference, this.workbook.getSpreadsheetVersion());
    }
    
    public AreaReference createAreaReference(final CellReference topLeft, final CellReference bottomRight) {
        return new AreaReference(topLeft, bottomRight, this.workbook.getSpreadsheetVersion());
    }
}
