package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.charts.ValueAxis;
import org.apache.poi.ss.usermodel.charts.ManualLayout;
import org.apache.poi.ss.usermodel.charts.ChartDataFactory;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDateAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.apache.poi.xssf.usermodel.charts.XSSFChartLegend;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTx;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.w3c.dom.Text;
import org.apache.poi.xssf.usermodel.charts.XSSFManualLayout;
import org.apache.poi.xssf.usermodel.charts.XSSFDateAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFCategoryAxis;
import org.apache.poi.xssf.usermodel.charts.XSSFValueAxis;
import org.apache.poi.ss.usermodel.charts.AxisPosition;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.charts.ChartData;
import org.apache.poi.xssf.usermodel.charts.XSSFChartDataFactory;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartSpace;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPageMargins;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPrintSettings;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.ArrayList;
import org.apache.poi.util.Removal;
import org.apache.poi.xssf.usermodel.charts.XSSFChartAxis;
import java.util.List;
import org.apache.poi.ss.usermodel.charts.ChartAxisFactory;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;

public final class XSSFChart extends XDDFChart implements Chart, ChartAxisFactory
{
    private XSSFGraphicFrame frame;
    @Deprecated
    @Removal(version = "4.2")
    List<XSSFChartAxis> axis;
    
    protected XSSFChart() {
        this.axis = new ArrayList<XSSFChartAxis>();
        this.createChart();
    }
    
    protected XSSFChart(final PackagePart part) throws IOException, XmlException {
        super(part);
        this.axis = new ArrayList<XSSFChartAxis>();
    }
    
    @Override
    protected POIXMLRelation getChartRelation() {
        return null;
    }
    
    @Override
    protected POIXMLRelation getChartWorkbookRelation() {
        return null;
    }
    
    @Override
    protected POIXMLFactory getChartFactory() {
        return null;
    }
    
    private void createChart() {
        final CTPlotArea plotArea = this.getCTPlotArea();
        plotArea.addNewLayout();
        this.chart.addNewPlotVisOnly().setVal(true);
        final CTPrintSettings printSettings = this.chartSpace.addNewPrintSettings();
        printSettings.addNewHeaderFooter();
        final CTPageMargins pageMargins = printSettings.addNewPageMargins();
        pageMargins.setB(0.75);
        pageMargins.setL(0.7);
        pageMargins.setR(0.7);
        pageMargins.setT(0.75);
        pageMargins.setHeader(0.3);
        pageMargins.setFooter(0.3);
        printSettings.addNewPageSetup();
    }
    
    @Override
    protected void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTChartSpace.type.getName().getNamespaceURI(), "chartSpace", "c"));
        final PackagePart part = this.getPackagePart();
        try (final OutputStream out = part.getOutputStream()) {
            this.chartSpace.save(out, xmlOptions);
        }
    }
    
    public XSSFGraphicFrame getGraphicFrame() {
        return this.frame;
    }
    
    protected void setGraphicFrame(final XSSFGraphicFrame frame) {
        this.frame = frame;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFChartDataFactory getChartDataFactory() {
        return XSSFChartDataFactory.getInstance();
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFChart getChartAxisFactory() {
        return this;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public void plot(final ChartData data, final ChartAxis... chartAxis) {
        data.fillChart((Chart)this, chartAxis);
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFValueAxis createValueAxis(final AxisPosition pos) {
        final long id = this.axis.size() + 1;
        final XSSFValueAxis valueAxis = new XSSFValueAxis(this, id, pos);
        if (this.axis.size() == 1) {
            final ChartAxis ax = (ChartAxis)this.axis.get(0);
            ax.crossAxis((ChartAxis)valueAxis);
            valueAxis.crossAxis(ax);
        }
        this.axis.add(valueAxis);
        return valueAxis;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFCategoryAxis createCategoryAxis(final AxisPosition pos) {
        final long id = this.axis.size() + 1;
        final XSSFCategoryAxis categoryAxis = new XSSFCategoryAxis(this, id, pos);
        if (this.axis.size() == 1) {
            final ChartAxis ax = (ChartAxis)this.axis.get(0);
            ax.crossAxis((ChartAxis)categoryAxis);
            categoryAxis.crossAxis(ax);
        }
        this.axis.add(categoryAxis);
        return categoryAxis;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFDateAxis createDateAxis(final AxisPosition pos) {
        final long id = this.axis.size() + 1;
        final XSSFDateAxis dateAxis = new XSSFDateAxis(this, id, pos);
        if (this.axis.size() == 1) {
            final ChartAxis ax = (ChartAxis)this.axis.get(0);
            ax.crossAxis((ChartAxis)dateAxis);
            dateAxis.crossAxis(ax);
        }
        this.axis.add(dateAxis);
        return dateAxis;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public List<? extends XSSFChartAxis> getAxis() {
        if (this.axis.isEmpty() && this.hasAxis()) {
            this.parseAxis();
        }
        return this.axis;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFManualLayout getManualLayout() {
        return new XSSFManualLayout(this);
    }
    
    public XSSFRichTextString getTitleText() {
        if (!this.chart.isSetTitle()) {
            return null;
        }
        final CTTitle title = this.chart.getTitle();
        final StringBuilder text = new StringBuilder(64);
        final XmlObject[] selectPath;
        final XmlObject[] t = selectPath = title.selectPath("declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' .//a:t");
        for (final XmlObject element : selectPath) {
            final NodeList kids = element.getDomNode().getChildNodes();
            for (int count = kids.getLength(), n = 0; n < count; ++n) {
                final Node kid = kids.item(n);
                if (kid instanceof Text) {
                    text.append(kid.getNodeValue());
                }
            }
        }
        return new XSSFRichTextString(text.toString());
    }
    
    public String getTitleFormula() {
        if (!this.chart.isSetTitle()) {
            return null;
        }
        final CTTitle title = this.chart.getTitle();
        if (!title.isSetTx()) {
            return null;
        }
        final CTTx tx = title.getTx();
        if (!tx.isSetStrRef()) {
            return null;
        }
        return tx.getStrRef().getF();
    }
    
    public void setTitleFormula(final String formula) {
        CTTitle ctTitle;
        if (this.chart.isSetTitle()) {
            ctTitle = this.chart.getTitle();
        }
        else {
            ctTitle = this.chart.addNewTitle();
        }
        CTTx tx;
        if (ctTitle.isSetTx()) {
            tx = ctTitle.getTx();
        }
        else {
            tx = ctTitle.addNewTx();
        }
        if (tx.isSetRich()) {
            tx.unsetRich();
        }
        CTStrRef strRef;
        if (tx.isSetStrRef()) {
            strRef = tx.getStrRef();
        }
        else {
            strRef = tx.addNewStrRef();
        }
        strRef.setF(formula);
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public XSSFChartLegend getOrCreateLegend() {
        return new XSSFChartLegend(this);
    }
    
    @Deprecated
    @Removal(version = "4.2")
    private boolean hasAxis() {
        final CTPlotArea ctPlotArea = this.chart.getPlotArea();
        final int totalAxisCount = ctPlotArea.sizeOfValAxArray() + ctPlotArea.sizeOfCatAxArray() + ctPlotArea.sizeOfDateAxArray() + ctPlotArea.sizeOfSerAxArray();
        return totalAxisCount > 0;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    private void parseAxis() {
        this.parseCategoryAxis();
        this.parseDateAxis();
        this.parseValueAxis();
    }
    
    @Deprecated
    @Removal(version = "4.2")
    private void parseCategoryAxis() {
        for (final CTCatAx catAx : this.chart.getPlotArea().getCatAxArray()) {
            this.axis.add(new XSSFCategoryAxis(this, catAx));
        }
    }
    
    @Deprecated
    @Removal(version = "4.2")
    private void parseDateAxis() {
        for (final CTDateAx dateAx : this.chart.getPlotArea().getDateAxArray()) {
            this.axis.add(new XSSFDateAxis(this, dateAx));
        }
    }
    
    @Deprecated
    @Removal(version = "4.2")
    private void parseValueAxis() {
        for (final CTValAx valAx : this.chart.getPlotArea().getValAxArray()) {
            this.axis.add(new XSSFValueAxis(this, valAx));
        }
    }
}
