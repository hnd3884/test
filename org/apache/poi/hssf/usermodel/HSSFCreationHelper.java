package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.hssf.record.common.ExtendedColor;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.usermodel.CreationHelper;

public class HSSFCreationHelper implements CreationHelper
{
    private final HSSFWorkbook workbook;
    
    @Internal(since = "3.15 beta 3")
    HSSFCreationHelper(final HSSFWorkbook wb) {
        this.workbook = wb;
    }
    
    @Override
    public HSSFRichTextString createRichTextString(final String text) {
        return new HSSFRichTextString(text);
    }
    
    @Override
    public HSSFDataFormat createDataFormat() {
        return this.workbook.createDataFormat();
    }
    
    @Override
    public HSSFHyperlink createHyperlink(final HyperlinkType type) {
        return new HSSFHyperlink(type);
    }
    
    @Override
    public HSSFExtendedColor createExtendedColor() {
        return new HSSFExtendedColor(new ExtendedColor());
    }
    
    @Override
    public HSSFFormulaEvaluator createFormulaEvaluator() {
        return new HSSFFormulaEvaluator(this.workbook);
    }
    
    @Override
    public HSSFClientAnchor createClientAnchor() {
        return new HSSFClientAnchor();
    }
    
    @Override
    public AreaReference createAreaReference(final String reference) {
        return new AreaReference(reference, this.workbook.getSpreadsheetVersion());
    }
    
    @Override
    public AreaReference createAreaReference(final CellReference topLeft, final CellReference bottomRight) {
        return new AreaReference(topLeft, bottomRight, this.workbook.getSpreadsheetVersion());
    }
}
