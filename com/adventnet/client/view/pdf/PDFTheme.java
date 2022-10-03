package com.adventnet.client.view.pdf;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfWriter;
import javax.servlet.ServletContext;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;

public interface PDFTheme
{
    Document getDocument(final ViewContext p0);
    
    void startPDFDoc(final ServletContext p0, final ViewContext p1, final Document p2, final PdfWriter p3);
    
    void endPDFDoc(final ServletContext p0, final ViewContext p1, final Document p2, final PdfWriter p3);
    
    Element updateThemeAttributes(final ServletContext p0, final ViewContext p1, final Document p2, final PdfWriter p3, final Element p4, final String p5);
    
    PdfPCell renderCellToAddInLayout(final ServletContext p0, final ViewContext p1, final Document p2, final PdfWriter p3, final PdfPCell p4, final String p5);
}
