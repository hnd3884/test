package com.adventnet.client.components.layout.table.pdf;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.lowagie.text.pdf.PdfPCell;
import com.adventnet.client.util.pdf.PDFUtil;
import com.lowagie.text.pdf.PdfPTable;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.client.components.layout.table.web.TableLayoutModel;
import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.client.util.web.WebClientUtil;
import java.util.List;
import com.lowagie.text.Element;
import com.adventnet.client.view.pdf.PDFTheme;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.ServletContext;
import java.util.logging.Logger;
import com.adventnet.client.view.pdf.PDFView;

public class TableLayoutPDFRenderer extends PDFView
{
    private Logger out;
    
    public TableLayoutPDFRenderer() {
        this.out = Logger.getLogger(TableLayoutPDFRenderer.class.getName());
    }
    
    public Element getView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        this.init(sc, vc, parent, doc, pdfWriter, theme, boxType);
        final List configList = (List)vc.getModel().getCompiledData((Object)"SORTEDTABLELIST");
        Object originalViewName = vc.getModel().getViewNameNo();
        Object viewName = UserPersonalizationAPI.getPersonalizedViewNameNo(originalViewName, WebClientUtil.getAccountId());
        if (viewName == null) {
            viewName = originalViewName;
        }
        final Object tempName = UserPersonalizationAPI.getOriginalViewNameNo(viewName, WebClientUtil.getAccountId());
        if (tempName != null) {
            originalViewName = tempName;
        }
        final TableLayoutModel viewModel = (TableLayoutModel)vc.getViewModel();
        final TableLayoutModel.TableLayoutIterator iter = viewModel.getIterator();
        int colSize = 0;
        int tempColSize = 0;
        while (iter.next()) {
            final int colSpan = (int)iter.get(5);
            tempColSize += colSpan;
            if (iter.isRowEnd()) {
                if (tempColSize > colSize) {
                    colSize = tempColSize;
                }
                tempColSize = 0;
            }
        }
        int rowSize = 0;
        if (configList.size() > 0) {
            final Row row = configList.get(configList.size() - 1);
            final int rowIdx = (int)row.get(3);
            final int rowSpan = (int)row.get(6);
            rowSize = rowIdx + rowSpan;
        }
        this.out.log(Level.FINE, " rowSize " + rowSize + "colSize " + colSize);
        final DataObject configDO = vc.getModel().getViewConfiguration();
        if (parent instanceof Document && parent == doc) {
            for (int i = 0; i < rowSize; ++i) {
                final Criteria rowCriteria = new Criteria(new Column("ACTableLayoutChildConfig", "ROWINDEX"), (Object)new Integer(i), 0);
                Criteria columnCriteria = null;
                for (int j = 0; j < colSize; ++j) {
                    final Criteria criteria = new Criteria(new Column("ACTableLayoutChildConfig", "COLUMNINDEX"), (Object)new Integer(j), 0);
                    columnCriteria = ((columnCriteria == null) ? criteria : columnCriteria.or(criteria));
                }
                final Criteria criteria2 = rowCriteria.and(columnCriteria);
                Iterator iterator = configDO.getRows("ACTableLayoutChildConfig", criteria2);
                int size = 0;
                Object lastRow = null;
                while (iterator.hasNext()) {
                    ++size;
                    lastRow = iterator.next();
                }
                if (size > 1) {
                    final PdfPTable pTab = new PdfPTable(size);
                    iterator = configDO.getRows("ACTableLayoutChildConfig", criteria2);
                    while (iterator.hasNext()) {
                        final Row row2 = iterator.next();
                        final String cBoxType = (String)row2.get("SHOWINBOX");
                        final int colSpan2 = (int)row2.get(5);
                        final Object[] result = this.includeView(row2, pTab, cBoxType, false);
                        final Element pdfObject = (Element)result[0];
                        final ViewContext cvc = (ViewContext)result[1];
                        PdfPCell cell = null;
                        if (pdfObject != null) {
                            cell = PDFUtil.setElementInCell(pdfObject);
                            cell = theme.renderCellToAddInLayout(sc, vc, doc, pdfWriter, cell, cBoxType);
                        }
                        else {
                            cell = new PdfPCell();
                            cell = theme.renderCellToAddInLayout(sc, vc, doc, pdfWriter, cell, (String)null);
                        }
                        cell.setColspan(colSpan2);
                        pTab.addCell(cell);
                    }
                    pTab.setWidthPercentage(100.0f);
                    doc.add((Element)pTab);
                }
                else if (size == 1) {
                    final Row row3 = (Row)lastRow;
                    final PdfPTable pTab2 = new PdfPTable(1);
                    final String cBoxType = (String)row3.get("SHOWINBOX");
                    final Object[] result2 = this.includeView(row3, pTab2, cBoxType, true);
                    final Element pdfObject2 = (Element)result2[0];
                    final ViewContext cvc2 = (ViewContext)result2[1];
                    if (pdfObject2 != null) {
                        PdfPCell cell2 = PDFUtil.setElementInCell(pdfObject2);
                        cell2 = theme.renderCellToAddInLayout(sc, vc, doc, pdfWriter, cell2, cBoxType);
                        pTab2.addCell(cell2);
                        pTab2.setWidthPercentage(100.0f);
                        doc.add((Element)pTab2);
                    }
                }
            }
            return null;
        }
        final int[][] rowSpanMap = new int[rowSize][colSize];
        final PdfPTable pTab3 = new PdfPTable(colSize);
        for (int k = 0; k < rowSize; ++k) {
            for (int j = 0; j < colSize; ++j) {
                final Row pkRow = new Row("ACTableLayoutChildConfig");
                pkRow.set(1, viewName);
                pkRow.set(3, (Object)new Integer(k));
                pkRow.set(4, (Object)new Integer(j));
                final Row matchingRow = configDO.getRow("ACTableLayoutChildConfig", pkRow);
                if (matchingRow != null) {
                    final int colspan = (int)matchingRow.get(5);
                    final int rowspan = (int)matchingRow.get(6);
                    final String cBoxType2 = (String)matchingRow.get("SHOWINBOX");
                    this.out.log(Level.FINE, " I : " + k + " J : " + j + " colSpan : " + colspan + " rowspan : " + rowspan);
                    this.out.log(Level.FINE, " matchingRow {0}", matchingRow);
                    final Object[] result3 = this.includeView(matchingRow, pTab3, cBoxType2, false);
                    final Element ob = (Element)result3[0];
                    final ViewContext cvc3 = (ViewContext)result3[1];
                    PdfPCell cell3 = null;
                    if (ob != null) {
                        cell3 = PDFUtil.setElementInCell(ob);
                        cell3 = theme.renderCellToAddInLayout(sc, vc, doc, pdfWriter, cell3, cBoxType2);
                    }
                    else {
                        cell3 = new PdfPCell();
                        cell3 = theme.renderCellToAddInLayout(sc, vc, doc, pdfWriter, cell3, (String)null);
                    }
                    cell3.setColspan(colspan);
                    pTab3.addCell(cell3);
                    for (int l = 0; l < colspan; ++l) {
                        rowSpanMap[k][l + j] = 1;
                    }
                }
                else if (rowSpanMap[k][j] == 0) {
                    final PdfPCell cell4 = theme.renderCellToAddInLayout(sc, vc, doc, pdfWriter, new PdfPCell(), (String)null);
                    pTab3.addCell(cell4);
                }
            }
        }
        pTab3.setSpacingAfter(0.0f);
        pTab3.setSpacingBefore(0.0f);
        pTab3.setWidthPercentage(100.0f);
        return (Element)pTab3;
    }
    
    private Object[] includeView(final Row row, final Object par, String cBoxType, final boolean writeInDoc) throws Exception {
        final HttpServletRequest request = this.vc.getRequest();
        final Long viewNameNo = (Long)row.get(2);
        Object originalChildName = UserPersonalizationAPI.getOriginalViewNameNo((Object)viewNameNo, WebClientUtil.getAccountId());
        if (originalChildName == null) {
            originalChildName = viewNameNo;
        }
        final ViewContext cvc = PDFUtil.getViewCtx(request, originalChildName);
        if (cBoxType == null && this.boxType != null) {
            cBoxType = this.boxType;
        }
        return new Object[] { PDFUtil.includeView(this.sc, cvc, par, this.doc, this.pdfWriter, this.theme, cBoxType, writeInDoc), cvc };
    }
}
