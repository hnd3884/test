package com.adventnet.client.components.table.pdf;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import com.adventnet.client.util.pdf.PDFUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.Row;
import com.adventnet.client.components.table.web.TableViewModel;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.Element;
import com.adventnet.client.view.pdf.PDFTheme;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.ServletContext;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.adventnet.client.components.table.web.TableIterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.TableConstants;

public class TableACPSRenderer extends AbstractTableRenderer implements TableConstants
{
    protected Logger logger;
    int colSize;
    int rowSize;
    int[][] rowSpanMap;
    Long psConfig;
    DataObject psdo;
    TableIterator globalTableIterator;
    TableTransformerContext transContext;
    
    public TableACPSRenderer() {
        this.logger = Logger.getLogger(TableACPSRenderer.class.getName());
        this.colSize = 0;
        this.rowSize = 0;
        this.rowSpanMap = null;
        this.psConfig = null;
        this.psdo = null;
        this.globalTableIterator = null;
        this.transContext = null;
    }
    
    public Element getView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        if (parent == doc) {
            this.addInView(sc, vc, parent, doc, pdfWriter, theme, boxType);
            return null;
        }
        this.init(sc, vc, parent, doc, pdfWriter, theme, boxType);
        this.initACPSConfig();
        final PdfPTable pTable = this.generatePDF();
        return (Element)pTable;
    }
    
    private PdfPCell getEmptyCell(final PdfPTable pTab) {
        final PdfPCell cell = pTab.getDefaultCell();
        cell.setHorizontalAlignment(0);
        cell.setPadding(3.0f);
        cell.setBorderWidth(0.0f);
        cell.setBorder(0);
        return cell;
    }
    
    private void initACPSConfig() throws Exception {
        final String uniqueId = this.vc.getUniqueId();
        final TableViewModel viewModel = (TableViewModel)this.vc.getViewModel();
        this.transContext = viewModel.getTableTransformerContext();
        this.psConfig = (Long)viewModel.getTableViewConfigRow().get("PSCONFIGLIST");
        this.psdo = (DataObject)this.vc.getModel().getCompiledData((Object)("PSDO" + uniqueId));
        if (this.psdo == null) {
            final Row customViewRow = new Row("CustomViewConfiguration");
            customViewRow.set("CVNAME", (Object)"PSCV");
            final DataObject customViewDO = LookUpUtil.getPersistence().get("CustomViewConfiguration", customViewRow);
            final long queryID = (long)customViewDO.getFirstValue("CustomViewConfiguration", "QUERYID");
            final SelectQuery query = QueryUtil.getSelectQuery(queryID);
            final Criteria crit = new Criteria(new Column("ACPSConfiguration", "CONFIGNAME"), (Object)this.psConfig, 0);
            query.setCriteria(crit);
            this.psdo = LookUpUtil.getPersistence().get(query);
            this.vc.getModel().addCompiledData((Object)("PSDO" + uniqueId), (Object)this.psdo);
        }
        final Iterator iterator = this.psdo.getRows("ACPSConfiguration");
        final ArrayList dataList = new ArrayList();
        int rowIdx = -1;
        while (iterator.hasNext()) {
            final Row requiredRow = iterator.next();
            rowIdx = (int)requiredRow.get(3);
            final int columnSpan = (int)requiredRow.get(4);
            if (rowIdx >= dataList.size()) {
                while (rowIdx != dataList.size() - 1) {
                    dataList.add(new Integer(0));
                }
            }
            Integer colSpan = dataList.get(rowIdx);
            colSpan = new Integer(colSpan + columnSpan);
            dataList.set(rowIdx, colSpan);
        }
        this.rowSize = rowIdx + 1;
        Collections.sort((List<Comparable>)dataList);
        this.colSize = dataList.get(dataList.size() - 1);
        this.logger.log(Level.INFO, "COLSIZE : " + this.colSize);
        this.rowSpanMap = new int[this.rowSize][this.colSize];
        final PdfPTable pTab = new PdfPTable(this.colSize);
        for (int i = 0; i < this.rowSize; ++i) {
            for (int j = 0; j < this.colSize; ++j) {
                final Row pkRow = new Row("ACPSConfiguration");
                pkRow.set(1, (Object)this.psConfig);
                pkRow.set(3, (Object)new Integer(i));
                pkRow.set(2, (Object)new Integer(j));
                final Row matchingRow = this.psdo.getRow("ACPSConfiguration", pkRow);
                if (matchingRow != null) {
                    final int colspan = (int)matchingRow.get(4);
                    final int rowspan = (int)matchingRow.get(5);
                    for (int l = 0; l < colspan; ++l) {
                        this.rowSpanMap[i][l + j] = 1;
                    }
                }
            }
        }
    }
    
    public void addInView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        this.init(sc, vc, parent, doc, pdfWriter, theme, boxType);
        this.initACPSConfig();
        final TableViewModel viewModel = (TableViewModel)vc.getViewModel();
        this.transContext = viewModel.getTableTransformerContext();
        (this.globalTableIterator = viewModel.getNewTableIterator()).reset();
        final PdfPTable parentTab = this.getParentTable(1);
        if (!this.globalTableIterator.hasNextRow()) {
            final Phrase ph = new Phrase();
            ph.add((Object)theme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)new Chunk("No Data"), "tableHeader"));
            parentTab.addCell(ph);
            doc.add((Element)parentTab);
            return;
        }
        final int rowIDX = 0;
        PdfPTable dataTable = this.getMetaTable(this.colSize);
        while (this.globalTableIterator.nextRow()) {
            dataTable = this.getMetaTable(this.colSize);
            this.constructTable(dataTable, rowIDX);
        }
    }
    
    private PdfPTable getParentTable(final int noOfColumn) {
        final PdfPTable dataTable = new PdfPTable(noOfColumn);
        if (this.boxType != null) {
            final TableViewModel viewModel = (TableViewModel)this.vc.getViewModel();
            final Object[] viewColumns = viewModel.getViewColumns();
            final int colSize = viewColumns.length;
            final PdfPCell titleCell = PDFUtil.getTitleElementInCell(this.sc, this.vc, this.parent, this.doc, this.pdfWriter, this.theme, this.boxType);
            if (titleCell != null) {
                titleCell.setColspan(colSize);
                dataTable.addCell(titleCell);
                dataTable.setHeaderRows(1);
            }
        }
        dataTable.setSpacingAfter(0.0f);
        dataTable.setSpacingBefore(0.0f);
        dataTable.setWidthPercentage(100.0f);
        return dataTable;
    }
    
    private PdfPTable getMetaTable(final int noOfColumn) {
        final PdfPTable dataTable = new PdfPTable(noOfColumn);
        dataTable.setSpacingAfter(0.0f);
        dataTable.setSpacingBefore(0.0f);
        dataTable.setWidthPercentage(100.0f);
        return dataTable;
    }
    
    private PdfPTable renderRowTable(final PdfPTable pTab) throws Exception {
        PdfPTable table = null;
        final Element cElement = this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)pTab, "PropSheetRowTable");
        if (cElement instanceof PdfPTable) {
            table = (PdfPTable)cElement;
        }
        else {
            table = new PdfPTable(1);
            PdfPCell cell = null;
            if (cElement instanceof PdfPCell) {
                cell = (PdfPCell)cElement;
            }
            else {
                cell = new PdfPCell();
                cell.addElement(cElement);
            }
            table.addCell(cell);
        }
        return table;
    }
    
    public boolean canAddInParent(final ServletContext sc, final ViewContext viewContext, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme) throws Exception {
        return true;
    }
    
    private void constructTable(final PdfPTable dataTable, final int rowIDX) throws Exception {
        int count = 0;
        dataTable.setComplete(false);
        for (int i = rowIDX; i < this.rowSize; ++i) {
            for (int j = 0; j < this.colSize; ++j) {
                final Row pkRow = new Row("ACPSConfiguration");
                pkRow.set(1, (Object)this.psConfig);
                pkRow.set(3, (Object)new Integer(i));
                pkRow.set(2, (Object)new Integer(j));
                final Row matchingRow = this.psdo.getRow("ACPSConfiguration", pkRow);
                if (matchingRow != null) {
                    final int colSpan = (int)matchingRow.get(4);
                    final int rowSpan = (int)matchingRow.get(5);
                    final String dataType = (String)matchingRow.get(6);
                    final String dataValue = (String)matchingRow.get(7);
                    PdfPCell cell = null;
                    if ("Label".equals(dataType)) {
                        cell = new PdfPCell(new Phrase(dataValue));
                        this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)cell, "labelCell");
                    }
                    else if ("FieldName".equals(dataType)) {
                        this.globalTableIterator.setCurrentColumn(dataValue);
                        this.globalTableIterator.initTransCtxForCurrentCell("Cell");
                        cell = new PdfPCell(this.getHeaderCell(this.transContext, "headCell"));
                        this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)cell, "oddRow");
                    }
                    else if ("FieldValue".equals(dataType)) {
                        this.globalTableIterator.setCurrentColumn(dataValue);
                        this.globalTableIterator.initTransCtxForCurrentCell("Cell");
                        cell = new PdfPCell(this.getCell(this.transContext, "evenRow"));
                        this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)cell, "evenRow");
                    }
                    cell.setColspan(colSpan);
                    dataTable.addCell(cell);
                }
                else if (this.rowSpanMap[i][j] == 0) {
                    final PdfPCell cell2 = this.theme.renderCellToAddInLayout(this.sc, this.vc, this.doc, this.pdfWriter, new PdfPCell(), (String)null);
                    dataTable.addCell(cell2);
                }
            }
            if (++count % 10 == 0) {
                this.doc.add((Element)dataTable);
            }
        }
        dataTable.addCell(this.getEmptyCell(dataTable));
        dataTable.addCell(this.getEmptyCell(dataTable));
        dataTable.setComplete(true);
        this.doc.add((Element)dataTable);
    }
    
    protected PdfPTable generatePDF() throws Exception {
        final String uniqueId = this.vc.getUniqueId();
        final TableViewModel viewModel = (TableViewModel)this.vc.getViewModel();
        final TableNavigatorModel tableModel = (TableNavigatorModel)viewModel.getTableModel();
        (this.globalTableIterator = viewModel.getTableIterator()).reset();
        final HttpServletRequest request = this.vc.getRequest();
        final PdfPTable parentTab = this.getParentTable(1);
        if (!this.globalTableIterator.hasNextRow()) {
            final Phrase ph = new Phrase();
            ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk("No Data"), "tableHeader"));
            parentTab.addCell(ph);
        }
        final int rowIDX = 0;
        while (this.globalTableIterator.nextRow()) {
            final PdfPTable dataTable = this.getMetaTable(this.colSize);
            this.constructTable(dataTable, rowIDX);
            final PdfPTable viewChild = this.renderRowTable(dataTable);
            parentTab.addCell(new PdfPCell(viewChild));
        }
        return parentTab;
    }
    
    protected Phrase getHeaderCell(final TableTransformerContext transContext, final String tClass) throws Exception {
        final String propertyName = transContext.getPropertyName();
        final Phrase ph = new Phrase();
        if (propertyName != null) {
            ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk(propertyName), tClass));
        }
        return ph;
    }
}
