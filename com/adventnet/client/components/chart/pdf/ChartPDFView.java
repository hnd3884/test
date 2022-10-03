package com.adventnet.client.components.chart.pdf;

import com.lowagie.text.Element;
import java.awt.Graphics2D;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfContentByte;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.lowagie.text.ImgTemplate;
import java.awt.geom.Rectangle2D;
import com.lowagie.text.pdf.BaseFont;
import java.awt.Font;
import com.lowagie.text.pdf.FontMapper;
import org.jfree.chart.JFreeChart;
import com.adventnet.client.components.chart.util.ChartUtil;
import com.adventnet.client.view.pdf.DefaultPDFTheme;
import com.lowagie.text.Image;
import com.adventnet.client.view.pdf.PDFTheme;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Document;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.ServletContext;
import java.util.logging.Logger;
import com.adventnet.client.components.chart.web.ChartConstants;
import com.adventnet.client.view.pdf.PDFView;

public class ChartPDFView extends PDFView implements ChartConstants
{
    private Logger logger;
    
    public ChartPDFView() {
        this.logger = Logger.getLogger(ChartPDFView.class.getName());
    }
    
    protected Image getImage(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        final DefaultPDFTheme themeObj = (DefaultPDFTheme)theme;
        final DataObject viewConfigDO = vc.getModel().getViewConfiguration();
        final Row chartConfigRow = viewConfigDO.getRow("ChartViewConfig");
        String graphTypeVal = vc.getRequest().getParameter("GRAPHTYPE");
        if (graphTypeVal == null) {
            graphTypeVal = (String)chartConfigRow.get(3);
        }
        String strWidth = ChartUtil.getPropValue(vc, graphTypeVal, "PDFWIDTH");
        if (strWidth == null) {
            strWidth = ChartUtil.getPropValue(vc, graphTypeVal, "WIDTH");
        }
        String strHeight = ChartUtil.getPropValue(vc, graphTypeVal, "PDFHEIGHT");
        if (strHeight == null) {
            strHeight = ChartUtil.getPropValue(vc, graphTypeVal, "HEIGHT");
        }
        float width = -1.0f;
        float height = -1.0f;
        if (strWidth != null) {
            width = Float.parseFloat(strWidth);
        }
        if (strHeight != null) {
            height = Float.parseFloat(strHeight);
        }
        if (width == -1.0f) {
            width = doc.getPageSize().getWidth() - (doc.leftMargin() + doc.rightMargin() + 20.0f);
        }
        if (height == -1.0f) {
            height = (doc.getPageSize().getHeight() - (doc.topMargin() + doc.bottomMargin() + 20.0f)) / 2.0f;
        }
        final JFreeChart chart = (JFreeChart)vc.getViewModel();
        final FontMapper fm = (FontMapper)new FontMapper() {
            public BaseFont awtToPdf(final Font font) {
                try {
                    return themeObj.getBaseFont();
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
            
            public Font pdfToAwt(final BaseFont bf, final int i) {
                return null;
            }
        };
        final PdfContentByte cb = pdfWriter.getDirectContent();
        final PdfTemplate tp = cb.createTemplate(width, height);
        final Graphics2D g2 = tp.createGraphics(width, height, fm);
        final Rectangle2D r2D = new Rectangle2D.Double(0.0, 0.0, width, height);
        chart.draw(g2, r2D);
        g2.dispose();
        return (Image)new ImgTemplate(tp);
    }
    
    public Element getView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        if (parent == doc) {
            this.addInView(sc, vc, parent, doc, pdfWriter, theme, boxType);
            return null;
        }
        this.init(sc, vc, parent, doc, pdfWriter, theme, boxType);
        return (Element)this.getImage(sc, vc, parent, doc, pdfWriter, theme, boxType);
    }
    
    public void addInView(final ServletContext sc, final ViewContext vc, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme, final String boxType) throws Exception {
        final Image image = this.getImage(sc, vc, parent, doc, pdfWriter, theme, boxType);
        doc.add((Element)image);
    }
    
    public boolean canAddInParent(final ServletContext sc, final ViewContext viewContext, final Object parent, final Document doc, final PdfWriter pdfWriter, final PDFTheme theme) throws Exception {
        return true;
    }
}
