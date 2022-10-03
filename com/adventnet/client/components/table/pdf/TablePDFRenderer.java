package com.adventnet.client.components.table.pdf;

import com.lowagie.text.Phrase;
import com.lowagie.text.Chunk;
import com.adventnet.client.util.pdf.PDFUtil;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.lowagie.text.pdf.PdfPCell;
import com.adventnet.client.components.table.web.TableIterator;
import java.util.logging.Level;
import com.adventnet.client.components.table.web.TableViewModel;
import com.lowagie.text.pdf.PdfPTable;
import com.adventnet.persistence.DataObject;
import com.lowagie.text.Element;
import com.adventnet.client.view.pdf.PDFTheme;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.ServletContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.TableConstants;

public class TablePDFRenderer extends AbstractTableRenderer implements TableConstants
{
    private static final Logger LOGGER;
    private int noOfExportableColumns;
    
    public TablePDFRenderer() {
        this.noOfExportableColumns = 0;
    }
    
    public Element getView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        if (parent == doc) {
            this.addInView(sc, vc, parent, doc, pdfWriter, theme, boxType);
            return null;
        }
        final DataObject data = vc.getModel().getViewConfiguration();
        if (data.containsTable("ACTableViewConfig") && data.getFirstValue("ACTableViewConfig", "DISPLAYTYPE").equals("PropertySheet")) {
            final PropertySheetRenderer ps = new PropertySheetRenderer();
            return ps.getView(sc, vc, parent, doc, pdfWriter, theme, boxType);
        }
        if (data.containsTable("ACTableViewConfig") && data.getFirstValue("ACTableViewConfig", "PSCONFIGLIST") != null) {
            final TableACPSRenderer ps2 = new TableACPSRenderer();
            return ps2.getView(sc, vc, parent, doc, pdfWriter, theme, boxType);
        }
        this.init(sc, vc, parent, doc, pdfWriter, theme, boxType);
        final PdfPTable pTable = this.generatePDF();
        return (Element)pTable;
    }
    
    public void addInView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        final DataObject data = vc.getModel().getViewConfiguration();
        if (data.containsTable("ACTableViewConfig") && data.getFirstValue("ACTableViewConfig", "DISPLAYTYPE").equals("PropertySheet")) {
            final PropertySheetRenderer ps = new PropertySheetRenderer();
            ps.addInView(sc, vc, parent, doc, pdfWriter, theme, boxType);
            return;
        }
        if (data.containsTable("ACTableViewConfig") && data.getFirstValue("ACTableViewConfig", "PSCONFIGLIST") != null) {
            final TableACPSRenderer ps2 = new TableACPSRenderer();
            ps2.addInView(sc, vc, parent, doc, pdfWriter, theme, boxType);
            return;
        }
        this.init(sc, vc, parent, doc, pdfWriter, theme, boxType);
        final PdfPTable dataTable = this.getMetaTable();
        final TableViewModel viewModel = (TableViewModel)vc.getViewModel();
        this.transContext = viewModel.getTableTransformerContext();
        (this.globalTableIterator = viewModel.getNewTableIterator()).reset();
        boolean oddRow = true;
        if (!this.globalTableIterator.hasNextRow()) {
            PdfPTable pTab = this.generateIfNullTable(this.getMetaTable(), viewModel);
            pTab = this.renderCellInDoc(pTab);
            dataTable.setHeaderRows(0);
            dataTable.setSpacingAfter(0.0f);
            doc.add((Element)pTab);
            return;
        }
        dataTable.setComplete(false);
        int count = 0;
        while (this.globalTableIterator.nextRow()) {
            final String tcClass = oddRow ? "oddRow" : "evenRow";
            oddRow = !oddRow;
            theme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)dataTable.getDefaultCell(), tcClass);
            this.globalTableIterator.setCurrentColumn(-1);
            while (this.globalTableIterator.nextColumn()) {
                this.globalTableIterator.initTransCtxForCurrentCell("Cell");
                if (this.transContext.isExportable()) {
                    this.addCellToTable(this.getCell(this.transContext, tcClass), dataTable);
                }
            }
            if (++count % 10 == 0) {
                doc.add((Element)dataTable);
            }
        }
        dataTable.setComplete(true);
        doc.add((Element)dataTable);
    }
    
    public PdfPTable getMetaTable() throws Exception {
        final TableViewModel viewModel = (TableViewModel)this.vc.getViewModel();
        final TableIterator iterator = viewModel.getNewTableIterator();
        while (iterator.nextColumn()) {
            if (viewModel.getTableTransformerContext().isExportable()) {
                ++this.noOfExportableColumns;
            }
        }
        TablePDFRenderer.LOGGER.log(Level.FINER, "Number of columns to export is {0}", this.noOfExportableColumns);
        final PdfPTable dataTable = new PdfPTable(this.noOfExportableColumns);
        this.generateHeaders(dataTable);
        dataTable.setSpacingAfter(0.0f);
        dataTable.setSpacingBefore(0.0f);
        dataTable.setWidthPercentage(100.0f);
        return dataTable;
    }
    
    private PdfPTable renderCellInDoc(final PdfPTable pTab) throws Exception {
        PdfPCell cell = new PdfPCell();
        cell.addElement((Element)pTab);
        cell = this.theme.renderCellToAddInLayout(this.sc, this.vc, this.doc, this.pdfWriter, cell, this.boxType);
        final PdfPTable table = new PdfPTable(1);
        table.addCell(cell);
        table.setWidthPercentage(100.0f);
        table.setSpacingBefore(0.0f);
        return table;
    }
    
    public boolean canAddInParent(final ServletContext sc, final ViewContext viewContext, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme) throws Exception {
        return true;
    }
    
    protected PdfPTable generatePDF() throws Exception {
        PdfPTable dataTable = this.getMetaTable();
        this.generateRows(dataTable);
        dataTable = this.generateIfNullTable(dataTable);
        return dataTable;
    }
    
    protected void generateRows(final PdfPTable dataTable) throws Exception {
        final TableViewModel viewModel = (TableViewModel)this.vc.getViewModel();
        final TableTransformerContext txformerContext = viewModel.getTableTransformerContext();
        final TableIterator iter = viewModel.getNewTableIterator();
        iter.reset();
        while (iter.nextRow()) {
            final int rowIndex = txformerContext.getRowIndex();
            final String tcClass = (rowIndex % 2 == 1) ? "oddRow" : "evenRow";
            this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)dataTable.getDefaultCell(), tcClass);
            iter.setCurrentColumn(-1);
            while (iter.nextColumn()) {
                iter.initTransCtxForCurrentCell("Cell");
                if (txformerContext.isExportable()) {
                    this.addCellToTable(this.getCell(txformerContext, tcClass), dataTable);
                }
            }
        }
    }
    
    protected void generateHeaders(final PdfPTable dataTable) throws Exception {
        final TableViewModel viewModel = (TableViewModel)this.vc.getViewModel();
        int headerCount = 0;
        String title = null;
        try {
            title = (String)this.vc.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", 6);
        }
        catch (final DataAccessException exp) {
            TablePDFRenderer.LOGGER.log(Level.FINE, "Exception while fetching title for rendering title in pdf :: {0}", exp.getMessage());
        }
        if (title != null && this.boxType != null) {
            final PdfPCell titleCell = PDFUtil.getTitleElementInCell(this.sc, this.vc, this.parent, this.doc, this.pdfWriter, this.theme, this.boxType);
            if (titleCell != null) {
                titleCell.setColspan(this.noOfExportableColumns);
                dataTable.addCell(titleCell);
                ++headerCount;
            }
        }
        final TableTransformerContext tContext = viewModel.getTableTransformerContext();
        final TableIterator iter = viewModel.getNewTableIterator();
        iter.reset();
        this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)dataTable.getDefaultCell(), "tableHeader");
        while (iter.nextColumn()) {
            iter.initTransCtxForCurrentCell("Header");
            try {
                if (!tContext.isExportable()) {
                    continue;
                }
                String value = tContext.getRenderedAttributes().get("VALUE");
                if (value != null && value.equals("&nbsp;")) {
                    value = "";
                }
                final Chunk headerChk = new Chunk(value);
                this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)headerChk, "tableHeader");
                final Phrase ph = new Phrase(headerChk);
                this.addCellToTable(ph, dataTable);
                continue;
            }
            catch (final DataAccessException e) {
                e.printStackTrace();
                throw e;
            }
            break;
        }
        dataTable.setHeaderRows(++headerCount);
    }
    
    static {
        LOGGER = Logger.getLogger(TablePDFRenderer.class.getName());
    }
}
