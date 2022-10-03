package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import java.util.Iterator;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ooxml.POIXMLException;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import java.io.OutputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDateAx;
import java.util.Collections;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import java.util.HashMap;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurface3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurfaceChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRadarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPie3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLine3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDoughnutChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTArea3DChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaChart;
import java.util.Map;
import java.util.LinkedList;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import java.util.Optional;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import java.util.function.Function;
import org.apache.poi.xddf.usermodel.text.XDDFTextBody;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTView3D;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurface;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.apache.poi.util.Internal;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.openxmlformats.schemas.drawingml.x2006.chart.ChartSpaceDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.ArrayList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartSpace;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xddf.usermodel.text.TextContainer;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public abstract class XDDFChart extends POIXMLDocumentPart implements TextContainer
{
    public static final int DEFAULT_WIDTH = 500000;
    public static final int DEFAULT_HEIGHT = 500000;
    public static final int DEFAULT_X = 10;
    public static final int DEFAULT_Y = 10;
    private XSSFWorkbook workbook;
    private int chartIndex;
    private POIXMLDocumentPart documentPart;
    protected List<XDDFChartAxis> axes;
    protected final CTChartSpace chartSpace;
    protected final CTChart chart;
    private long seriesCount;
    
    protected XDDFChart() {
        this.chartIndex = 0;
        this.documentPart = null;
        this.axes = new ArrayList<XDDFChartAxis>();
        this.seriesCount = 0L;
        this.chartSpace = CTChartSpace.Factory.newInstance();
        (this.chart = this.chartSpace.addNewChart()).addNewPlotArea();
    }
    
    protected XDDFChart(final PackagePart part) throws IOException, XmlException {
        super(part);
        this.chartIndex = 0;
        this.documentPart = null;
        this.axes = new ArrayList<XDDFChartAxis>();
        this.seriesCount = 0L;
        this.chartSpace = ChartSpaceDocument.Factory.parse(part.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS).getChartSpace();
        this.chart = this.chartSpace.getChart();
    }
    
    @Internal
    public CTChartSpace getCTChartSpace() {
        return this.chartSpace;
    }
    
    @Internal
    public CTChart getCTChart() {
        return this.chart;
    }
    
    @Internal
    protected CTPlotArea getCTPlotArea() {
        return this.chart.getPlotArea();
    }
    
    public void clear() {
        this.axes.clear();
        this.seriesCount = 0L;
        if (this.workbook != null) {
            this.workbook.removeSheetAt(0);
            this.workbook.createSheet();
        }
        this.chart.set((XmlObject)CTChart.Factory.newInstance());
        this.chart.addNewPlotArea();
    }
    
    public boolean isPlotOnlyVisibleCells() {
        return this.chart.isSetPlotVisOnly() && this.chart.getPlotVisOnly().getVal();
    }
    
    public void setPlotOnlyVisibleCells(final boolean only) {
        if (!this.chart.isSetPlotVisOnly()) {
            this.chart.setPlotVisOnly(CTBoolean.Factory.newInstance());
        }
        this.chart.getPlotVisOnly().setVal(only);
    }
    
    public void setFloor(final int thickness) {
        if (!this.chart.isSetFloor()) {
            this.chart.setFloor(CTSurface.Factory.newInstance());
        }
        this.chart.getFloor().getThickness().setVal((long)thickness);
    }
    
    public void setBackWall(final int thickness) {
        if (!this.chart.isSetBackWall()) {
            this.chart.setBackWall(CTSurface.Factory.newInstance());
        }
        this.chart.getBackWall().getThickness().setVal((long)thickness);
    }
    
    public void setSideWall(final int thickness) {
        if (!this.chart.isSetSideWall()) {
            this.chart.setSideWall(CTSurface.Factory.newInstance());
        }
        this.chart.getSideWall().getThickness().setVal((long)thickness);
    }
    
    public void setAutoTitleDeleted(final boolean deleted) {
        if (!this.chart.isSetAutoTitleDeleted()) {
            this.chart.setAutoTitleDeleted(CTBoolean.Factory.newInstance());
        }
        this.chart.getAutoTitleDeleted().setVal(deleted);
    }
    
    public void displayBlanksAs(final DisplayBlanks as) {
        if (as == null) {
            if (this.chart.isSetDispBlanksAs()) {
                this.chart.unsetDispBlanksAs();
            }
        }
        else if (this.chart.isSetDispBlanksAs()) {
            this.chart.getDispBlanksAs().setVal(as.underlying);
        }
        else {
            this.chart.addNewDispBlanksAs().setVal(as.underlying);
        }
    }
    
    public Boolean getTitleOverlay() {
        if (this.chart.isSetTitle()) {
            final CTTitle title = this.chart.getTitle();
            if (title.isSetOverlay()) {
                return title.getOverlay().getVal();
            }
        }
        return null;
    }
    
    public void setTitleOverlay(final boolean overlay) {
        if (!this.chart.isSetTitle()) {
            this.chart.addNewTitle();
        }
        new XDDFTitle(this, this.chart.getTitle()).setOverlay(overlay);
    }
    
    public void setTitleText(final String text) {
        if (!this.chart.isSetTitle()) {
            this.chart.addNewTitle();
        }
        new XDDFTitle(this, this.chart.getTitle()).setText(text);
    }
    
    public XDDFTitle getTitle() {
        if (this.chart.isSetTitle()) {
            return new XDDFTitle(this, this.chart.getTitle());
        }
        return null;
    }
    
    public XDDFView3D getOrAddView3D() {
        CTView3D view3D;
        if (this.chart.isSetView3D()) {
            view3D = this.chart.getView3D();
        }
        else {
            view3D = this.chart.addNewView3D();
        }
        return new XDDFView3D(view3D);
    }
    
    public XDDFTextBody getFormattedTitle() {
        if (!this.chart.isSetTitle()) {
            return null;
        }
        return new XDDFTitle(this, this.chart.getTitle()).getBody();
    }
    
    @Override
    public <R> Optional<R> findDefinedParagraphProperty(final Function<CTTextParagraphProperties, Boolean> isSet, final Function<CTTextParagraphProperties, R> getter) {
        return Optional.empty();
    }
    
    @Override
    public <R> Optional<R> findDefinedRunProperty(final Function<CTTextCharacterProperties, Boolean> isSet, final Function<CTTextCharacterProperties, R> getter) {
        return Optional.empty();
    }
    
    public XDDFShapeProperties getOrAddShapeProperties() {
        final CTPlotArea plotArea = this.getCTPlotArea();
        CTShapeProperties properties;
        if (plotArea.isSetSpPr()) {
            properties = plotArea.getSpPr();
        }
        else {
            properties = plotArea.addNewSpPr();
        }
        return new XDDFShapeProperties(properties);
    }
    
    public void deleteShapeProperties() {
        if (this.getCTPlotArea().isSetSpPr()) {
            this.getCTPlotArea().unsetSpPr();
        }
    }
    
    public XDDFChartLegend getOrAddLegend() {
        return new XDDFChartLegend(this.chart);
    }
    
    public void deleteLegend() {
        if (this.chart.isSetLegend()) {
            this.chart.unsetLegend();
        }
    }
    
    public XDDFManualLayout getOrAddManualLayout() {
        return new XDDFManualLayout(this.getCTPlotArea());
    }
    
    protected long incrementSeriesCount() {
        return this.seriesCount++;
    }
    
    public void plot(final XDDFChartData data) {
        final XSSFSheet sheet = this.getSheet();
        for (int idx = 0; idx < data.getSeriesCount(); ++idx) {
            final XDDFChartData.Series series = data.getSeries(idx);
            series.plot();
            final XDDFDataSource<?> categoryDS = series.getCategoryData();
            final XDDFNumericalDataSource<? extends Number> valuesDS = series.getValuesData();
            if (!categoryDS.isCellRange() && !valuesDS.isCellRange() && !categoryDS.isLiteral()) {
                if (!valuesDS.isLiteral()) {
                    this.fillSheet(sheet, categoryDS, valuesDS);
                }
            }
        }
    }
    
    public List<XDDFChartData> getChartSeries() {
        final List<XDDFChartData> series = new LinkedList<XDDFChartData>();
        final CTPlotArea plotArea = this.getCTPlotArea();
        final Map<Long, XDDFChartAxis> categories = this.getCategoryAxes();
        final Map<Long, XDDFValueAxis> values = this.getValueAxes();
        for (int i = 0; i < plotArea.sizeOfAreaChartArray(); ++i) {
            final CTAreaChart areaChart = plotArea.getAreaChartArray(i);
            series.add(new XDDFAreaChartData(this, areaChart, categories, values));
        }
        for (int i = 0; i < plotArea.sizeOfArea3DChartArray(); ++i) {
            final CTArea3DChart areaChart2 = plotArea.getArea3DChartArray(i);
            series.add(new XDDFArea3DChartData(this, areaChart2, categories, values));
        }
        for (int i = 0; i < plotArea.sizeOfBarChartArray(); ++i) {
            final CTBarChart barChart = plotArea.getBarChartArray(i);
            series.add(new XDDFBarChartData(this, barChart, categories, values));
        }
        for (int i = 0; i < plotArea.sizeOfBar3DChartArray(); ++i) {
            final CTBar3DChart barChart2 = plotArea.getBar3DChartArray(i);
            series.add(new XDDFBar3DChartData(this, barChart2, categories, values));
        }
        for (int i = 0; i < plotArea.sizeOfDoughnutChartArray(); ++i) {
            final CTDoughnutChart doughnutChart = plotArea.getDoughnutChartArray(i);
            series.add(new XDDFDoughnutChartData(this, doughnutChart));
        }
        for (int i = 0; i < plotArea.sizeOfLineChartArray(); ++i) {
            final CTLineChart lineChart = plotArea.getLineChartArray(i);
            series.add(new XDDFLineChartData(this, lineChart, categories, values));
        }
        for (int i = 0; i < plotArea.sizeOfLine3DChartArray(); ++i) {
            final CTLine3DChart lineChart2 = plotArea.getLine3DChartArray(i);
            series.add(new XDDFLine3DChartData(this, lineChart2, categories, values));
        }
        for (int i = 0; i < plotArea.sizeOfPieChartArray(); ++i) {
            final CTPieChart pieChart = plotArea.getPieChartArray(i);
            series.add(new XDDFPieChartData(this, pieChart));
        }
        for (int i = 0; i < plotArea.sizeOfPie3DChartArray(); ++i) {
            final CTPie3DChart pieChart2 = plotArea.getPie3DChartArray(i);
            series.add(new XDDFPie3DChartData(this, pieChart2));
        }
        for (int i = 0; i < plotArea.sizeOfRadarChartArray(); ++i) {
            final CTRadarChart radarChart = plotArea.getRadarChartArray(i);
            series.add(new XDDFRadarChartData(this, radarChart, categories, values));
        }
        for (int i = 0; i < plotArea.sizeOfScatterChartArray(); ++i) {
            final CTScatterChart scatterChart = plotArea.getScatterChartArray(i);
            series.add(new XDDFScatterChartData(this, scatterChart, categories, values));
        }
        for (int i = 0; i < plotArea.sizeOfSurfaceChartArray(); ++i) {
            final CTSurfaceChart surfaceChart = plotArea.getSurfaceChartArray(i);
            series.add(new XDDFSurfaceChartData(this, surfaceChart, categories, values));
        }
        for (int i = 0; i < plotArea.sizeOfSurface3DChartArray(); ++i) {
            final CTSurface3DChart surfaceChart2 = plotArea.getSurface3DChartArray(i);
            series.add(new XDDFSurface3DChartData(this, surfaceChart2, categories, values));
        }
        this.seriesCount = series.size();
        return series;
    }
    
    public void clearChartSeries() {
        final CTPlotArea plotArea = this.getCTPlotArea();
        for (int i = plotArea.sizeOfAreaChartArray(); i > 0; --i) {
            plotArea.removeAreaChart(i - 1);
        }
        for (int i = plotArea.sizeOfArea3DChartArray(); i > 0; --i) {
            plotArea.removeArea3DChart(i - 1);
        }
        for (int i = plotArea.sizeOfBarChartArray(); i > 0; --i) {
            plotArea.removeBarChart(i - 1);
        }
        for (int i = plotArea.sizeOfBar3DChartArray(); i > 0; --i) {
            plotArea.removeBar3DChart(i - 1);
        }
        for (int i = plotArea.sizeOfBubbleChartArray(); i > 0; --i) {
            plotArea.removeBubbleChart(i - 1);
        }
        for (int i = plotArea.sizeOfDoughnutChartArray(); i > 0; --i) {
            plotArea.removeDoughnutChart(i - 1);
        }
        for (int i = plotArea.sizeOfLineChartArray(); i > 0; --i) {
            plotArea.removeLineChart(i - 1);
        }
        for (int i = plotArea.sizeOfLine3DChartArray(); i > 0; --i) {
            plotArea.removeLine3DChart(i - 1);
        }
        for (int i = plotArea.sizeOfOfPieChartArray(); i > 0; --i) {
            plotArea.removeOfPieChart(i - 1);
        }
        for (int i = plotArea.sizeOfPieChartArray(); i > 0; --i) {
            plotArea.removePieChart(i - 1);
        }
        for (int i = plotArea.sizeOfPie3DChartArray(); i > 0; --i) {
            plotArea.removePie3DChart(i - 1);
        }
        for (int i = plotArea.sizeOfRadarChartArray(); i > 0; --i) {
            plotArea.removeRadarChart(i - 1);
        }
        for (int i = plotArea.sizeOfScatterChartArray(); i > 0; --i) {
            plotArea.removeScatterChart(i - 1);
        }
        for (int i = plotArea.sizeOfStockChartArray(); i > 0; --i) {
            plotArea.removeStockChart(i - 1);
        }
        for (int i = plotArea.sizeOfSurfaceChartArray(); i > 0; --i) {
            plotArea.removeSurfaceChart(i - 1);
        }
        for (int i = plotArea.sizeOfSurface3DChartArray(); i > 0; --i) {
            plotArea.removeSurface3DChart(i - 1);
        }
    }
    
    private Map<Long, XDDFChartAxis> getCategoryAxes() {
        final CTPlotArea plotArea = this.getCTPlotArea();
        final int sizeOfArray = plotArea.sizeOfCatAxArray();
        final Map<Long, XDDFChartAxis> axes = new HashMap<Long, XDDFChartAxis>(sizeOfArray);
        for (int i = 0; i < sizeOfArray; ++i) {
            final CTCatAx category = plotArea.getCatAxArray(i);
            axes.put(category.getAxId().getVal(), new XDDFCategoryAxis(category));
        }
        return axes;
    }
    
    private Map<Long, XDDFValueAxis> getValueAxes() {
        final CTPlotArea plotArea = this.getCTPlotArea();
        final int sizeOfArray = plotArea.sizeOfValAxArray();
        final Map<Long, XDDFValueAxis> axes = new HashMap<Long, XDDFValueAxis>(sizeOfArray);
        for (int i = 0; i < sizeOfArray; ++i) {
            final CTValAx values = plotArea.getValAxArray(i);
            axes.put(values.getAxId().getVal(), new XDDFValueAxis(values));
        }
        return axes;
    }
    
    public XDDFValueAxis createValueAxis(final AxisPosition pos) {
        final XDDFValueAxis valueAxis = new XDDFValueAxis(this.getCTPlotArea(), pos);
        this.addAxis(valueAxis);
        return valueAxis;
    }
    
    public XDDFSeriesAxis createSeriesAxis(final AxisPosition pos) {
        final XDDFSeriesAxis seriesAxis = new XDDFSeriesAxis(this.getCTPlotArea(), pos);
        this.addAxis(seriesAxis);
        return seriesAxis;
    }
    
    public XDDFCategoryAxis createCategoryAxis(final AxisPosition pos) {
        final XDDFCategoryAxis categoryAxis = new XDDFCategoryAxis(this.getCTPlotArea(), pos);
        this.addAxis(categoryAxis);
        return categoryAxis;
    }
    
    public XDDFDateAxis createDateAxis(final AxisPosition pos) {
        final XDDFDateAxis dateAxis = new XDDFDateAxis(this.getCTPlotArea(), pos);
        this.addAxis(dateAxis);
        return dateAxis;
    }
    
    private void addAxis(final XDDFChartAxis newAxis) {
        if (this.axes.size() == 1) {
            final XDDFChartAxis axis = this.axes.get(0);
            axis.crossAxis(newAxis);
            newAxis.crossAxis(axis);
            axis.setCrosses(AxisCrosses.AUTO_ZERO);
            newAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        }
        this.axes.add(newAxis);
    }
    
    public XDDFChartData createData(final ChartTypes type, final XDDFChartAxis category, final XDDFValueAxis values) {
        Map<Long, XDDFChartAxis> categories = null;
        Map<Long, XDDFValueAxis> mapValues = null;
        if (ChartTypes.PIE != type && ChartTypes.PIE3D != type) {
            categories = Collections.singletonMap(category.getId(), category);
            mapValues = Collections.singletonMap(values.getId(), values);
        }
        final CTPlotArea plotArea = this.getCTPlotArea();
        switch (type) {
            case AREA: {
                return new XDDFAreaChartData(this, plotArea.addNewAreaChart(), categories, mapValues);
            }
            case AREA3D: {
                return new XDDFArea3DChartData(this, plotArea.addNewArea3DChart(), categories, mapValues);
            }
            case BAR: {
                return new XDDFBarChartData(this, plotArea.addNewBarChart(), categories, mapValues);
            }
            case BAR3D: {
                return new XDDFBar3DChartData(this, plotArea.addNewBar3DChart(), categories, mapValues);
            }
            case DOUGHNUT: {
                return new XDDFDoughnutChartData(this, plotArea.addNewDoughnutChart());
            }
            case LINE: {
                return new XDDFLineChartData(this, plotArea.addNewLineChart(), categories, mapValues);
            }
            case LINE3D: {
                return new XDDFLine3DChartData(this, plotArea.addNewLine3DChart(), categories, mapValues);
            }
            case PIE: {
                return new XDDFPieChartData(this, plotArea.addNewPieChart());
            }
            case PIE3D: {
                return new XDDFPie3DChartData(this, plotArea.addNewPie3DChart());
            }
            case RADAR: {
                return new XDDFRadarChartData(this, plotArea.addNewRadarChart(), categories, mapValues);
            }
            case SCATTER: {
                return new XDDFScatterChartData(this, plotArea.addNewScatterChart(), categories, mapValues);
            }
            case SURFACE: {
                return new XDDFSurfaceChartData(this, plotArea.addNewSurfaceChart(), categories, mapValues);
            }
            case SURFACE3D: {
                return new XDDFSurface3DChartData(this, plotArea.addNewSurface3DChart(), categories, mapValues);
            }
            default: {
                return null;
            }
        }
    }
    
    public List<? extends XDDFChartAxis> getAxes() {
        if (this.axes.isEmpty() && this.hasAxes()) {
            this.parseAxes();
        }
        return this.axes;
    }
    
    private boolean hasAxes() {
        final CTPlotArea ctPlotArea = this.getCTPlotArea();
        final int totalAxisCount = ctPlotArea.sizeOfValAxArray() + ctPlotArea.sizeOfCatAxArray() + ctPlotArea.sizeOfDateAxArray() + ctPlotArea.sizeOfSerAxArray();
        return totalAxisCount > 0;
    }
    
    private void parseAxes() {
        for (final CTCatAx catAx : this.getCTPlotArea().getCatAxArray()) {
            this.axes.add(new XDDFCategoryAxis(catAx));
        }
        for (final CTDateAx dateAx : this.getCTPlotArea().getDateAxArray()) {
            this.axes.add(new XDDFDateAxis(dateAx));
        }
        for (final CTSerAx serAx : this.getCTPlotArea().getSerAxArray()) {
            this.axes.add(new XDDFSeriesAxis(serAx));
        }
        for (final CTValAx valAx : this.getCTPlotArea().getValAxArray()) {
            this.axes.add(new XDDFValueAxis(valAx));
        }
    }
    
    public void setValueRange(final int axisIndex, final Double minimum, final Double maximum, final Double majorUnit, final Double minorUnit) {
        final XDDFChartAxis axis = (XDDFChartAxis)this.getAxes().get(axisIndex);
        if (axis == null) {
            return;
        }
        if (minimum != null) {
            axis.setMinimum(minimum);
        }
        if (maximum != null) {
            axis.setMaximum(maximum);
        }
        if (majorUnit != null) {
            axis.setMajorUnit(majorUnit);
        }
        if (minorUnit != null) {
            axis.setMinorUnit(minorUnit);
        }
    }
    
    public PackageRelationship createRelationshipInChart(final POIXMLRelation chartRelation, final POIXMLFactory chartFactory, final int chartIndex) {
        this.documentPart = this.createRelationship(chartRelation, chartFactory, chartIndex, true).getDocumentPart();
        return this.addRelation(null, chartRelation, this.documentPart).getRelationship();
    }
    
    private PackagePart createWorksheetPart(final POIXMLRelation chartRelation, final POIXMLRelation chartWorkbookRelation, final POIXMLFactory chartFactory) throws InvalidFormatException {
        final PackageRelationship xlsx = this.createRelationshipInChart(chartWorkbookRelation, chartFactory, this.chartIndex);
        this.setExternalId(xlsx.getId());
        return this.getTargetPart(xlsx);
    }
    
    public void saveWorkbook(final XSSFWorkbook workbook) throws IOException, InvalidFormatException {
        PackagePart worksheetPart = this.getWorksheetPart();
        if (worksheetPart == null) {
            final POIXMLRelation chartRelation = this.getChartRelation();
            final POIXMLRelation chartWorkbookRelation = this.getChartWorkbookRelation();
            final POIXMLFactory chartFactory = this.getChartFactory();
            if (chartRelation == null || chartWorkbookRelation == null || chartFactory == null) {
                throw new InvalidFormatException("unable to determine chart relations");
            }
            worksheetPart = this.createWorksheetPart(chartRelation, chartWorkbookRelation, chartFactory);
        }
        try (final OutputStream xlsOut = worksheetPart.getOutputStream()) {
            this.setWorksheetPartCommitted();
            workbook.write(xlsOut);
        }
    }
    
    protected abstract POIXMLRelation getChartRelation();
    
    protected abstract POIXMLRelation getChartWorkbookRelation();
    
    protected abstract POIXMLFactory getChartFactory();
    
    protected void fillSheet(final XSSFSheet sheet, final XDDFDataSource<?> categoryData, final XDDFNumericalDataSource<?> valuesData) {
        for (int numOfPoints = categoryData.getPointCount(), i = 0; i < numOfPoints; ++i) {
            final XSSFRow row = this.getRow(sheet, i + 1);
            final Object category = categoryData.getPointAt(i);
            if (category != null) {
                this.getCell(row, categoryData.getColIndex()).setCellValue(category.toString());
            }
            final Number value = valuesData.getPointAt(i);
            if (value != null) {
                this.getCell(row, valuesData.getColIndex()).setCellValue(value.doubleValue());
            }
        }
    }
    
    private XSSFRow getRow(final XSSFSheet sheet, final int index) {
        final XSSFRow row = sheet.getRow(index);
        if (row == null) {
            return sheet.createRow(index);
        }
        return row;
    }
    
    private XSSFCell getCell(final XSSFRow row, final int index) {
        final XSSFCell cell = row.getCell(index);
        if (cell == null) {
            return row.createCell(index);
        }
        return cell;
    }
    
    public void importContent(final XDDFChart other) {
        this.chart.set((XmlObject)other.chart);
    }
    
    @Override
    protected void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTChartSpace.type.getName().getNamespaceURI(), "chartSpace", "c"));
        if (this.workbook != null) {
            try {
                this.saveWorkbook(this.workbook);
            }
            catch (final InvalidFormatException e) {
                throw new POIXMLException(e);
            }
        }
        final PackagePart part = this.getPackagePart();
        try (final OutputStream out = part.getOutputStream()) {
            this.chartSpace.save(out, xmlOptions);
        }
    }
    
    public CellReference setSheetTitle(final String title, final int column) {
        final XSSFSheet sheet = this.getSheet();
        final XSSFRow row = this.getRow(sheet, 0);
        final XSSFCell cell = this.getCell(row, column);
        cell.setCellValue(title);
        return new CellReference(sheet.getSheetName(), 0, column, true, true);
    }
    
    public String formatRange(final CellRangeAddress range) {
        final XSSFSheet sheet = this.getSheet();
        return (sheet == null) ? null : range.formatAsString(sheet.getSheetName(), true);
    }
    
    private XSSFSheet getSheet() {
        XSSFSheet sheet = null;
        try {
            sheet = this.getWorkbook().getSheetAt(0);
        }
        catch (final InvalidFormatException | IOException ex) {}
        return sheet;
    }
    
    private PackagePart getWorksheetPart() throws InvalidFormatException {
        for (final RelationPart part : this.getRelationParts()) {
            if ("http://schemas.openxmlformats.org/officeDocument/2006/relationships/package".equals(part.getRelationship().getRelationshipType())) {
                return this.getTargetPart(part.getRelationship());
            }
        }
        return null;
    }
    
    private void setWorksheetPartCommitted() throws InvalidFormatException {
        for (final RelationPart part : this.getRelationParts()) {
            if ("http://schemas.openxmlformats.org/officeDocument/2006/relationships/package".equals(part.getRelationship().getRelationshipType())) {
                part.getDocumentPart().setCommitted(true);
                break;
            }
        }
    }
    
    public XSSFWorkbook getWorkbook() throws IOException, InvalidFormatException {
        if (this.workbook == null) {
            try {
                final PackagePart worksheetPart = this.getWorksheetPart();
                if (worksheetPart == null) {
                    (this.workbook = new XSSFWorkbook()).createSheet();
                }
                else {
                    this.workbook = new XSSFWorkbook(worksheetPart.getInputStream());
                }
            }
            catch (final NotOfficeXmlFileException e) {
                (this.workbook = new XSSFWorkbook()).createSheet();
            }
        }
        return this.workbook;
    }
    
    public void setWorkbook(final XSSFWorkbook workbook) {
        this.workbook = workbook;
    }
    
    public void setExternalId(final String id) {
        this.getCTChartSpace().addNewExternalData().setId(id);
    }
    
    protected int getChartIndex() {
        return this.chartIndex;
    }
    
    public void setChartIndex(final int chartIndex) {
        this.chartIndex = chartIndex;
    }
}
