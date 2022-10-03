package org.apache.poi.xssf.streaming;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.CreationHelper;

public class SXSSFCreationHelper implements CreationHelper
{
    private static final POILogger logger;
    private final SXSSFWorkbook wb;
    private final XSSFCreationHelper helper;
    
    @Internal
    public SXSSFCreationHelper(final SXSSFWorkbook workbook) {
        this.helper = new XSSFCreationHelper(workbook.getXSSFWorkbook());
        this.wb = workbook;
    }
    
    public XSSFRichTextString createRichTextString(final String text) {
        SXSSFCreationHelper.logger.log(3, new Object[] { "SXSSF doesn't support Rich Text Strings, any formatting information will be lost" });
        return new XSSFRichTextString(text);
    }
    
    public SXSSFFormulaEvaluator createFormulaEvaluator() {
        return new SXSSFFormulaEvaluator(this.wb);
    }
    
    public DataFormat createDataFormat() {
        return (DataFormat)this.helper.createDataFormat();
    }
    
    public Hyperlink createHyperlink(final HyperlinkType type) {
        return (Hyperlink)this.helper.createHyperlink(type);
    }
    
    public ExtendedColor createExtendedColor() {
        return this.helper.createExtendedColor();
    }
    
    public ClientAnchor createClientAnchor() {
        return (ClientAnchor)this.helper.createClientAnchor();
    }
    
    public AreaReference createAreaReference(final String reference) {
        return new AreaReference(reference, this.wb.getSpreadsheetVersion());
    }
    
    public AreaReference createAreaReference(final CellReference topLeft, final CellReference bottomRight) {
        return new AreaReference(topLeft, bottomRight, this.wb.getSpreadsheetVersion());
    }
    
    static {
        logger = POILogFactory.getLogger((Class)SXSSFCreationHelper.class);
    }
}
