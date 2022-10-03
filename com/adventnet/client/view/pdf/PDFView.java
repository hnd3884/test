package com.adventnet.client.view.pdf;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.client.view.common.ExportAuditModel;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfWriter;
import com.adventnet.client.view.web.ViewContext;
import com.lowagie.text.Document;
import javax.servlet.ServletContext;
import com.adventnet.client.view.common.PostExportHandler;

public abstract class PDFView implements PostExportHandler
{
    protected ServletContext sc;
    protected Document doc;
    protected ViewContext vc;
    protected PdfWriter pdfWriter;
    protected PDFTheme theme;
    protected Object parent;
    protected String boxType;
    
    public PDFView() {
        this.sc = null;
        this.doc = null;
        this.vc = null;
        this.pdfWriter = null;
        this.theme = null;
        this.parent = null;
        this.boxType = null;
    }
    
    protected void init(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) {
        this.sc = sc;
        this.doc = doc;
        this.vc = vc;
        this.pdfWriter = pdfWriter;
        this.theme = theme;
        this.parent = parent;
        this.boxType = boxType;
    }
    
    public abstract Element getView(final ServletContext p0, final ViewContext p1, final Object p2, final Document p3, final PdfWriter p4, final PDFTheme p5, final String p6) throws Exception;
    
    public void addInView(final ServletContext sc, final ViewContext viewContext, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
    }
    
    public boolean canAddInParent(final ServletContext sc, final ViewContext viewContext, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme) throws Exception {
        return false;
    }
    
    @Override
    public void auditExport(final ExportAuditModel auditModel) {
        Logger.getLogger(PDFView.class.getName()).log(Level.FINEST, "Post Export Handling can be done here. Do nothing");
    }
}
