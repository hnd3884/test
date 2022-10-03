package com.adventnet.client.components.chart.pdf;

import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import javax.swing.table.TableModel;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.adventnet.client.util.pdf.PDFUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfPTable;
import com.adventnet.i18n.I18N;
import com.adventnet.client.components.chart.util.ChartUtil;
import com.adventnet.client.components.chart.table.GraphData;
import com.lowagie.text.Element;
import com.adventnet.client.view.pdf.PDFTheme;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.ServletContext;
import java.util.logging.Logger;

public class ChartTabPDFView extends ChartPDFView
{
    private Logger logger;
    
    public ChartTabPDFView() {
        this.logger = Logger.getLogger(ChartTabPDFView.class.getName());
    }
    
    protected Element handleView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        final Image image = this.getImage(sc, vc, parent, doc, pdfWriter, theme, boxType);
        final GraphData graphData = (GraphData)vc.getTransientState("GRAPHCCDATA");
        final TableModel tm = graphData.getData();
        PdfPTable pTab = null;
        if (tm != null) {
            pTab = ChartUtil.getTable(tm, sc, vc, parent, doc, pdfWriter, theme, boxType);
        }
        final DataObject viewConfigDO = vc.getModel().getViewConfiguration();
        final Row chartConfigRow = viewConfigDO.getRow("ChartViewConfig");
        String graphTypeVal = vc.getRequest().getParameter("GRAPHTYPE");
        if (graphTypeVal == null) {
            graphTypeVal = (String)chartConfigRow.get(3);
        }
        String title = ChartUtil.getPropValue(vc, graphTypeVal, "HEAD_LABEL");
        if (title == null) {
            title = ChartUtil.getPropValue(vc, graphTypeVal, "CHART_TITLE");
        }
        if (title == null) {
            title = vc.getTitle();
        }
        title = I18N.getMsg(title, new Object[0]);
        final PdfPTable pTable = new PdfPTable(1);
        if (title != null) {
            final Element element = theme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)new Chunk(title), "title");
            final PdfPCell titleCell = PDFUtil.setElementInCell(element);
            theme.updateThemeAttributes(sc, vc, doc, pdfWriter, (Element)titleCell, "title");
            titleCell.setColspan(1);
            pTable.addCell(titleCell);
            pTable.setHeaderRows(1);
        }
        PdfPCell pcell = new PdfPCell(image);
        pcell.setBorderWidth(0.0f);
        pTable.addCell(pcell);
        if (pTab != null) {
            pcell = new PdfPCell(pTab);
            pTable.addCell(pcell);
        }
        return (Element)pTable;
    }
    
    @Override
    public Element getView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        if (parent == doc) {
            this.addInView(sc, vc, parent, doc, pdfWriter, theme, boxType);
            return null;
        }
        this.init(sc, vc, parent, doc, pdfWriter, theme, boxType);
        return this.handleView(sc, vc, parent, doc, pdfWriter, theme, boxType);
    }
    
    @Override
    public void addInView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        final Element element = this.handleView(sc, vc, parent, doc, pdfWriter, theme, boxType);
        doc.add(element);
    }
    
    @Override
    public boolean canAddInParent(final ServletContext sc, final ViewContext viewContext, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme) throws Exception {
        return true;
    }
}
