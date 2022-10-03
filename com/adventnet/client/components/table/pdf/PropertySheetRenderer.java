package com.adventnet.client.components.table.pdf;

import com.adventnet.client.util.pdf.PDFUtil;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import com.adventnet.client.components.table.web.TableViewModel;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.Element;
import com.adventnet.client.view.pdf.PDFTheme;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.ServletContext;
import com.adventnet.client.components.table.web.TableIterator;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.TableConstants;

public class PropertySheetRenderer extends AbstractTableRenderer implements TableConstants
{
    protected Logger logger;
    TableIterator globalTableIterator;
    
    public PropertySheetRenderer() {
        this.logger = Logger.getLogger(PropertySheetRenderer.class.getName());
        this.globalTableIterator = null;
    }
    
    public Element getView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        if (parent == doc) {
            this.addInView(sc, vc, parent, doc, pdfWriter, theme, boxType);
            return null;
        }
        this.init(sc, vc, parent, doc, pdfWriter, theme, boxType);
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
    
    public void addInView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        this.init(sc, vc, parent, doc, pdfWriter, theme, boxType);
        final ViewContext viewContext = vc;
        final TableViewModel viewModel = (TableViewModel)viewContext.getViewModel();
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        (this.globalTableIterator = viewModel.getNewTableIterator()).reset();
        final PdfPTable parentTab = this.getParentTable(1);
        if (!this.globalTableIterator.hasNextRow()) {
            final Phrase ph = new Phrase();
            ph.add((Object)theme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)new Chunk("No Data"), "tableHeader"));
            parentTab.addCell(ph);
            doc.add((Element)parentTab);
            return;
        }
        PdfPTable dataTable = null;
        while (this.globalTableIterator.nextRow()) {
            dataTable = this.getMetaTable(2);
            this.globalTableIterator.setCurrentColumn(-1);
            while (this.globalTableIterator.nextColumn()) {
                this.globalTableIterator.initTransCtxForCurrentCell("Cell");
                theme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)dataTable.getDefaultCell(), "headerCell");
                dataTable.addCell(this.getHeaderCell(transContext, "headerCell"));
                theme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)dataTable.getDefaultCell(), "evenRow");
                dataTable.addCell(this.getCell(transContext, "evenRow"));
            }
            dataTable.addCell(this.getEmptyCell(dataTable));
            dataTable.addCell(this.getEmptyCell(dataTable));
            final PdfPTable viewChild = this.renderRowTable(dataTable);
            parentTab.addCell(new PdfPCell(viewChild));
        }
        doc.add((Element)parentTab);
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
    
    protected PdfPTable generatePDF() throws Exception {
        final TableViewModel viewModel = (TableViewModel)this.vc.getViewModel();
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        (this.globalTableIterator = viewModel.getTableIterator()).reset();
        final PdfPTable parentTab = this.getParentTable(1);
        if (!this.globalTableIterator.hasNextRow()) {
            final Phrase ph = new Phrase();
            ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk("No Data"), "tableHeader"));
            parentTab.addCell(ph);
        }
        while (this.globalTableIterator.nextRow()) {
            final PdfPTable dataTable = this.getMetaTable(2);
            this.globalTableIterator.setCurrentColumn(-1);
            while (this.globalTableIterator.nextColumn()) {
                this.globalTableIterator.initTransCtxForCurrentCell("Cell");
                this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)dataTable.getDefaultCell(), "headerCell");
                dataTable.addCell(this.getHeaderCell(transContext, "headerCell"));
                this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)dataTable.getDefaultCell(), "evenRow");
                dataTable.addCell(this.getCell(transContext, "evenRow"));
            }
            dataTable.addCell(this.getEmptyCell(dataTable));
            dataTable.addCell(this.getEmptyCell(dataTable));
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
