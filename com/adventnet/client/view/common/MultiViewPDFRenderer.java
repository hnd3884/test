package com.adventnet.client.view.common;

import javax.servlet.http.HttpServletRequest;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import javax.servlet.ServletContext;

public interface MultiViewPDFRenderer
{
    void generatePDF(final String[] p0, final ServletContext p1, final PdfWriter p2, final Document p3, final HttpServletRequest p4) throws Exception;
}
