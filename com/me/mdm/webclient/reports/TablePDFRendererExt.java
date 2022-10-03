package com.me.mdm.webclient.reports;

import java.util.logging.Level;
import com.lowagie.text.pdf.PdfPTable;
import java.awt.Color;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.export.pdf.TablePDFRenderer;

public class TablePDFRendererExt extends TablePDFRenderer
{
    private Logger logger;
    private Color bluestrip;
    
    public TablePDFRendererExt() {
        this.logger = Logger.getLogger(TablePDFRendererExt.class.getName());
        this.bluestrip = new Color(686010);
    }
    
    public void generateHeaders(final PdfPTable dataTable) throws Exception {
        try {
            super.generateHeaders(dataTable);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in generateHeaders", ex);
        }
        if (dataTable.size() > 2) {
            dataTable.deleteRow(0);
        }
        dataTable.setWidthPercentage(100.0f);
        dataTable.getDefaultCell().setBorder(0);
    }
}
