package com.adventnet.client.components.layout.grid.pdf;

import com.lowagie.text.pdf.PdfPCell;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.util.pdf.PDFUtil;
import com.lowagie.text.pdf.PdfPTable;
import com.adventnet.client.components.layout.grid.web.GridModel;
import com.lowagie.text.Element;
import com.adventnet.client.view.pdf.PDFTheme;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.ServletContext;
import com.adventnet.client.view.pdf.PDFView;

public class GridLayoutPDFRenderer extends PDFView
{
    public Element getView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        this.init(sc, vc, parent, doc, pdfWriter, theme, boxType);
        final GridModel viewModel = (GridModel)vc.getViewModel();
        final HttpServletRequest request = vc.getRequest();
        boolean addInDoc = false;
        if (parent instanceof Document && parent == doc) {
            addInDoc = true;
        }
        PdfPTable gridTable = null;
        if (!addInDoc) {
            gridTable = new PdfPTable(1);
        }
        final GridModel.GridIterator iter = viewModel.getIterator();
        while (iter.next()) {
            final String viewName = iter.getCurrentView();
            final ViewContext cvc = PDFUtil.getViewCtx(request, (Object)viewName);
            String cBoxType = iter.getBoxType();
            if (cBoxType == null) {
                cBoxType = boxType;
            }
            final Element ob = PDFUtil.includeView(sc, cvc, (Object)gridTable, doc, pdfWriter, theme, cBoxType, addInDoc);
            if (ob != null) {
                PdfPCell cell = PDFUtil.setElementInCell(ob);
                cell = theme.renderCellToAddInLayout(sc, vc, doc, pdfWriter, cell, cBoxType);
                if (addInDoc) {
                    gridTable = new PdfPTable(1);
                }
                gridTable.addCell(cell);
                if (!addInDoc) {
                    continue;
                }
                gridTable.setSpacingAfter(0.0f);
                gridTable.setSpacingBefore(0.0f);
                gridTable.setWidthPercentage(100.0f);
                doc.add((Element)gridTable);
            }
        }
        if (!addInDoc) {
            gridTable.setSpacingAfter(0.0f);
            gridTable.setSpacingBefore(0.0f);
            gridTable.setWidthPercentage(100.0f);
            return (Element)gridTable;
        }
        return null;
    }
}
