package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.util.CellRangeAddressBase;
import org.apache.poi.hssf.record.chart.UnitsRecord;
import org.apache.poi.hssf.record.chart.BeginRecord;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.chart.PlotGrowthRecord;
import org.apache.poi.hssf.record.chart.FrameRecord;
import org.apache.poi.hssf.record.chart.LineFormatRecord;
import org.apache.poi.hssf.record.chart.AreaFormatRecord;
import org.apache.poi.hssf.record.chart.EndRecord;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.hssf.record.chart.DataFormatRecord;
import org.apache.poi.hssf.record.chart.SeriesToChartGroupRecord;
import org.apache.poi.hssf.record.chart.SheetPropertiesRecord;
import org.apache.poi.hssf.record.chart.DefaultDataLabelTextPropertiesRecord;
import org.apache.poi.hssf.record.chart.FontIndexRecord;
import org.apache.poi.hssf.record.chart.AxisUsedRecord;
import org.apache.poi.hssf.record.chart.AxisParentRecord;
import org.apache.poi.hssf.record.chart.AxisRecord;
import org.apache.poi.hssf.record.chart.CategorySeriesAxisRecord;
import org.apache.poi.hssf.record.chart.AxisOptionsRecord;
import org.apache.poi.hssf.record.chart.TickRecord;
import org.apache.poi.hssf.record.chart.AxisLineFormatRecord;
import org.apache.poi.hssf.record.chart.PlotAreaRecord;
import org.apache.poi.hssf.record.chart.ChartFormatRecord;
import org.apache.poi.hssf.record.chart.BarRecord;
import org.apache.poi.hssf.record.chart.TextRecord;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.chart.FontBasisRecord;
import org.apache.poi.hssf.record.PrintSetupRecord;
import org.apache.poi.hssf.record.VCenterRecord;
import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.chart.SeriesIndexRecord;
import java.util.Iterator;
import org.apache.poi.hssf.record.chart.SeriesRecord;
import org.apache.poi.hssf.record.chart.LinkedDataRecord;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.ProtectRecord;
import org.apache.poi.hssf.record.FooterRecord;
import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.Record;
import java.util.ArrayList;
import org.apache.poi.hssf.record.chart.ValueRangeRecord;
import java.util.List;
import org.apache.poi.hssf.record.chart.SeriesTextRecord;
import org.apache.poi.hssf.record.chart.ChartTitleFormatRecord;
import org.apache.poi.hssf.record.chart.LegendRecord;
import org.apache.poi.hssf.record.chart.ChartRecord;

public final class HSSFChart
{
    private HSSFSheet sheet;
    private ChartRecord chartRecord;
    private LegendRecord legendRecord;
    private ChartTitleFormatRecord chartTitleFormat;
    private SeriesTextRecord chartTitleText;
    private List<ValueRangeRecord> valueRanges;
    private HSSFChartType type;
    private List<HSSFSeries> series;
    
    private HSSFChart(final HSSFSheet sheet, final ChartRecord chartRecord) {
        this.valueRanges = new ArrayList<ValueRangeRecord>();
        this.type = HSSFChartType.Unknown;
        this.series = new ArrayList<HSSFSeries>();
        this.chartRecord = chartRecord;
        this.sheet = sheet;
    }
    
    public void createBarChart(final HSSFWorkbook workbook, final HSSFSheet parentSheet) {
        final List<Record> records = new ArrayList<Record>();
        records.add(this.createMSDrawingObjectRecord());
        records.add(this.createOBJRecord());
        records.add(this.createBOFRecord());
        records.add(new HeaderRecord(""));
        records.add(new FooterRecord(""));
        records.add(this.createHCenterRecord());
        records.add(this.createVCenterRecord());
        records.add(this.createPrintSetupRecord());
        records.add(this.createFontBasisRecord1());
        records.add(this.createFontBasisRecord2());
        records.add(new ProtectRecord(false));
        records.add(this.createUnitsRecord());
        records.add(this.createChartRecord(0, 0, 30434904, 19031616));
        records.add(this.createBeginRecord());
        records.add(this.createSCLRecord((short)1, (short)1));
        records.add(this.createPlotGrowthRecord(65536, 65536));
        records.add(this.createFrameRecord1());
        records.add(this.createBeginRecord());
        records.add(this.createLineFormatRecord(true));
        records.add(this.createAreaFormatRecord1());
        records.add(this.createEndRecord());
        records.add(this.createSeriesRecord());
        records.add(this.createBeginRecord());
        records.add(this.createTitleLinkedDataRecord());
        records.add(this.createValuesLinkedDataRecord());
        records.add(this.createCategoriesLinkedDataRecord());
        records.add(this.createDataFormatRecord());
        records.add(this.createSeriesToChartGroupRecord());
        records.add(this.createEndRecord());
        records.add(this.createSheetPropsRecord());
        records.add(this.createDefaultTextRecord((short)2));
        records.add(this.createAllTextRecord());
        records.add(this.createBeginRecord());
        records.add(this.createFontIndexRecord(5));
        records.add(this.createDirectLinkRecord());
        records.add(this.createEndRecord());
        records.add(this.createDefaultTextRecord((short)3));
        records.add(this.createUnknownTextRecord());
        records.add(this.createBeginRecord());
        records.add(this.createFontIndexRecord(6));
        records.add(this.createDirectLinkRecord());
        records.add(this.createEndRecord());
        records.add(this.createAxisUsedRecord((short)1));
        this.createAxisRecords(records);
        records.add(this.createEndRecord());
        records.add(this.createDimensionsRecord());
        records.add(this.createSeriesIndexRecord(2));
        records.add(this.createSeriesIndexRecord(1));
        records.add(this.createSeriesIndexRecord(3));
        records.add(EOFRecord.instance);
        parentSheet.insertChartRecords(records);
        workbook.insertChartRecord();
    }
    
    public static HSSFChart[] getSheetCharts(final HSSFSheet sheet) {
        final List<HSSFChart> charts = new ArrayList<HSSFChart>();
        HSSFChart lastChart = null;
        HSSFSeries lastSeries = null;
        final List<RecordBase> records = sheet.getSheet().getRecords();
        for (final RecordBase r : records) {
            if (r instanceof ChartRecord) {
                lastSeries = null;
                lastChart = new HSSFChart(sheet, (ChartRecord)r);
                charts.add(lastChart);
            }
            else if (r instanceof LinkedDataRecord) {
                final LinkedDataRecord linkedDataRecord = (LinkedDataRecord)r;
                if (lastSeries != null) {
                    lastSeries.insertData(linkedDataRecord);
                }
            }
            if (lastChart == null) {
                continue;
            }
            if (r instanceof LegendRecord) {
                lastChart.legendRecord = (LegendRecord)r;
            }
            else if (r instanceof SeriesRecord) {
                final HSSFSeries series = new HSSFSeries((SeriesRecord)r);
                lastChart.series.add(series);
                lastSeries = series;
            }
            else if (r instanceof ChartTitleFormatRecord) {
                lastChart.chartTitleFormat = (ChartTitleFormatRecord)r;
            }
            else if (r instanceof SeriesTextRecord) {
                final SeriesTextRecord str = (SeriesTextRecord)r;
                if (lastChart.legendRecord == null && lastChart.series.size() > 0) {
                    final HSSFSeries series2 = lastChart.series.get(lastChart.series.size() - 1);
                    series2.seriesTitleText = str;
                }
                else {
                    lastChart.chartTitleText = str;
                }
            }
            else if (r instanceof ValueRangeRecord) {
                lastChart.valueRanges.add((ValueRangeRecord)r);
            }
            else {
                if (!(r instanceof Record)) {
                    continue;
                }
                final Record record = (Record)r;
                for (final HSSFChartType type : HSSFChartType.values()) {
                    if (type != HSSFChartType.Unknown) {
                        if (record.getSid() == type.getSid()) {
                            lastChart.type = type;
                            break;
                        }
                    }
                }
            }
        }
        return charts.toArray(new HSSFChart[0]);
    }
    
    public int getChartX() {
        return this.chartRecord.getX();
    }
    
    public int getChartY() {
        return this.chartRecord.getY();
    }
    
    public int getChartWidth() {
        return this.chartRecord.getWidth();
    }
    
    public int getChartHeight() {
        return this.chartRecord.getHeight();
    }
    
    public void setChartX(final int x) {
        this.chartRecord.setX(x);
    }
    
    public void setChartY(final int y) {
        this.chartRecord.setY(y);
    }
    
    public void setChartWidth(final int width) {
        this.chartRecord.setWidth(width);
    }
    
    public void setChartHeight(final int height) {
        this.chartRecord.setHeight(height);
    }
    
    public HSSFSeries[] getSeries() {
        return this.series.toArray(new HSSFSeries[0]);
    }
    
    public String getChartTitle() {
        if (this.chartTitleText != null) {
            return this.chartTitleText.getText();
        }
        return null;
    }
    
    public void setChartTitle(final String title) {
        if (this.chartTitleText != null) {
            this.chartTitleText.setText(title);
            return;
        }
        throw new IllegalStateException("No chart title found to change");
    }
    
    public void setValueRange(final int axisIndex, final Double minimum, final Double maximum, final Double majorUnit, final Double minorUnit) {
        final ValueRangeRecord valueRange = this.valueRanges.get(axisIndex);
        if (valueRange == null) {
            return;
        }
        if (minimum != null) {
            valueRange.setAutomaticMinimum(minimum.isNaN());
            valueRange.setMinimumAxisValue(minimum);
        }
        if (maximum != null) {
            valueRange.setAutomaticMaximum(maximum.isNaN());
            valueRange.setMaximumAxisValue(maximum);
        }
        if (majorUnit != null) {
            valueRange.setAutomaticMajor(majorUnit.isNaN());
            valueRange.setMajorIncrement(majorUnit);
        }
        if (minorUnit != null) {
            valueRange.setAutomaticMinor(minorUnit.isNaN());
            valueRange.setMinorIncrement(minorUnit);
        }
    }
    
    private SeriesIndexRecord createSeriesIndexRecord(final int index) {
        final SeriesIndexRecord r = new SeriesIndexRecord();
        r.setIndex((short)index);
        return r;
    }
    
    private DimensionsRecord createDimensionsRecord() {
        final DimensionsRecord r = new DimensionsRecord();
        r.setFirstRow(0);
        r.setLastRow(31);
        r.setFirstCol((short)0);
        r.setLastCol((short)1);
        return r;
    }
    
    private HCenterRecord createHCenterRecord() {
        final HCenterRecord r = new HCenterRecord();
        r.setHCenter(false);
        return r;
    }
    
    private VCenterRecord createVCenterRecord() {
        final VCenterRecord r = new VCenterRecord();
        r.setVCenter(false);
        return r;
    }
    
    private PrintSetupRecord createPrintSetupRecord() {
        final PrintSetupRecord r = new PrintSetupRecord();
        r.setPaperSize((short)0);
        r.setScale((short)18);
        r.setPageStart((short)1);
        r.setFitWidth((short)1);
        r.setFitHeight((short)1);
        r.setLeftToRight(false);
        r.setLandscape(false);
        r.setValidSettings(true);
        r.setNoColor(false);
        r.setDraft(false);
        r.setNotes(false);
        r.setNoOrientation(false);
        r.setUsePage(false);
        r.setHResolution((short)0);
        r.setVResolution((short)0);
        r.setHeaderMargin(0.5);
        r.setFooterMargin(0.5);
        r.setCopies((short)15);
        return r;
    }
    
    private FontBasisRecord createFontBasisRecord1() {
        final FontBasisRecord r = new FontBasisRecord();
        r.setXBasis((short)9120);
        r.setYBasis((short)5640);
        r.setHeightBasis((short)200);
        r.setScale((short)0);
        r.setIndexToFontTable((short)5);
        return r;
    }
    
    private FontBasisRecord createFontBasisRecord2() {
        final FontBasisRecord r = this.createFontBasisRecord1();
        r.setIndexToFontTable((short)6);
        return r;
    }
    
    private BOFRecord createBOFRecord() {
        final BOFRecord r = new BOFRecord();
        r.setVersion(600);
        r.setType(20);
        r.setBuild(7422);
        r.setBuildYear(1997);
        r.setHistoryBitMask(16585);
        r.setRequiredVersion(106);
        return r;
    }
    
    private UnknownRecord createOBJRecord() {
        final byte[] data = { 21, 0, 18, 0, 5, 0, 2, 0, 17, 96, 0, 0, 0, 0, -72, 3, -121, 3, 0, 0, 0, 0, 0, 0, 0, 0 };
        return new UnknownRecord(93, data);
    }
    
    private UnknownRecord createMSDrawingObjectRecord() {
        final byte[] data = { 15, 0, 2, -16, -64, 0, 0, 0, 16, 0, 8, -16, 8, 0, 0, 0, 2, 0, 0, 0, 2, 4, 0, 0, 15, 0, 3, -16, -88, 0, 0, 0, 15, 0, 4, -16, 40, 0, 0, 0, 1, 0, 9, -16, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 10, -16, 8, 0, 0, 0, 0, 4, 0, 0, 5, 0, 0, 0, 15, 0, 4, -16, 112, 0, 0, 0, -110, 12, 10, -16, 8, 0, 0, 0, 2, 4, 0, 0, 0, 10, 0, 0, -109, 0, 11, -16, 54, 0, 0, 0, 127, 0, 4, 1, 4, 1, -65, 0, 8, 0, 8, 0, -127, 1, 78, 0, 0, 8, -125, 1, 77, 0, 0, 8, -65, 1, 16, 0, 17, 0, -64, 1, 77, 0, 0, 8, -1, 1, 8, 0, 8, 0, 63, 2, 0, 0, 2, 0, -65, 3, 0, 0, 8, 0, 0, 0, 16, -16, 18, 0, 0, 0, 0, 0, 4, 0, -64, 2, 10, 0, -12, 0, 14, 0, 102, 1, 32, 0, -23, 0, 0, 0, 17, -16, 0, 0, 0, 0 };
        return new UnknownRecord(236, data);
    }
    
    private void createAxisRecords(final List<Record> records) {
        records.add(this.createAxisParentRecord());
        records.add(this.createBeginRecord());
        records.add(this.createAxisRecord((short)0));
        records.add(this.createBeginRecord());
        records.add(this.createCategorySeriesAxisRecord());
        records.add(this.createAxisOptionsRecord());
        records.add(this.createTickRecord1());
        records.add(this.createEndRecord());
        records.add(this.createAxisRecord((short)1));
        records.add(this.createBeginRecord());
        records.add(this.createValueRangeRecord());
        records.add(this.createTickRecord2());
        records.add(this.createAxisLineFormatRecord((short)1));
        records.add(this.createLineFormatRecord(false));
        records.add(this.createEndRecord());
        records.add(this.createPlotAreaRecord());
        records.add(this.createFrameRecord2());
        records.add(this.createBeginRecord());
        records.add(this.createLineFormatRecord2());
        records.add(this.createAreaFormatRecord2());
        records.add(this.createEndRecord());
        records.add(this.createChartFormatRecord());
        records.add(this.createBeginRecord());
        records.add(this.createBarRecord());
        records.add(this.createLegendRecord());
        records.add(this.createBeginRecord());
        records.add(this.createTextRecord());
        records.add(this.createBeginRecord());
        records.add(this.createLinkedDataRecord());
        records.add(this.createEndRecord());
        records.add(this.createEndRecord());
        records.add(this.createEndRecord());
        records.add(this.createEndRecord());
    }
    
    private LinkedDataRecord createLinkedDataRecord() {
        final LinkedDataRecord r = new LinkedDataRecord();
        r.setLinkType((byte)0);
        r.setReferenceType((byte)1);
        r.setCustomNumberFormat(false);
        r.setIndexNumberFmtRecord((short)0);
        r.setFormulaOfLink(null);
        return r;
    }
    
    private TextRecord createTextRecord() {
        final TextRecord r = new TextRecord();
        r.setHorizontalAlignment((byte)2);
        r.setVerticalAlignment((byte)2);
        r.setDisplayMode((short)1);
        r.setRgbColor(0);
        r.setX(-37);
        r.setY(-60);
        r.setWidth(0);
        r.setHeight(0);
        r.setAutoColor(true);
        r.setShowKey(false);
        r.setShowValue(false);
        r.setVertical(false);
        r.setAutoGeneratedText(true);
        r.setGenerated(true);
        r.setAutoLabelDeleted(false);
        r.setAutoBackground(true);
        r.setRotation((short)0);
        r.setShowCategoryLabelAsPercentage(false);
        r.setShowValueAsPercentage(false);
        r.setShowBubbleSizes(false);
        r.setShowLabel(false);
        r.setIndexOfColorValue((short)77);
        r.setDataLabelPlacement((short)0);
        r.setTextRotation((short)0);
        return r;
    }
    
    private LegendRecord createLegendRecord() {
        final LegendRecord r = new LegendRecord();
        r.setXAxisUpperLeft(3542);
        r.setYAxisUpperLeft(1566);
        r.setXSize(437);
        r.setYSize(213);
        r.setType((byte)3);
        r.setSpacing((byte)1);
        r.setAutoPosition(true);
        r.setAutoSeries(true);
        r.setAutoXPositioning(true);
        r.setAutoYPositioning(true);
        r.setVertical(true);
        r.setDataTable(false);
        return r;
    }
    
    private BarRecord createBarRecord() {
        final BarRecord r = new BarRecord();
        r.setBarSpace((short)0);
        r.setCategorySpace((short)150);
        r.setHorizontal(false);
        r.setStacked(false);
        r.setDisplayAsPercentage(false);
        r.setShadow(false);
        return r;
    }
    
    private ChartFormatRecord createChartFormatRecord() {
        final ChartFormatRecord r = new ChartFormatRecord();
        r.setXPosition(0);
        r.setYPosition(0);
        r.setWidth(0);
        r.setHeight(0);
        r.setVaryDisplayPattern(false);
        return r;
    }
    
    private PlotAreaRecord createPlotAreaRecord() {
        return new PlotAreaRecord();
    }
    
    private AxisLineFormatRecord createAxisLineFormatRecord(final short format) {
        final AxisLineFormatRecord r = new AxisLineFormatRecord();
        r.setAxisType(format);
        return r;
    }
    
    private ValueRangeRecord createValueRangeRecord() {
        final ValueRangeRecord r = new ValueRangeRecord();
        r.setMinimumAxisValue(0.0);
        r.setMaximumAxisValue(0.0);
        r.setMajorIncrement(0.0);
        r.setMinorIncrement(0.0);
        r.setCategoryAxisCross(0.0);
        r.setAutomaticMinimum(true);
        r.setAutomaticMaximum(true);
        r.setAutomaticMajor(true);
        r.setAutomaticMinor(true);
        r.setAutomaticCategoryCrossing(true);
        r.setLogarithmicScale(false);
        r.setValuesInReverse(false);
        r.setCrossCategoryAxisAtMaximum(false);
        r.setReserved(true);
        return r;
    }
    
    private TickRecord createTickRecord1() {
        final TickRecord r = new TickRecord();
        r.setMajorTickType((byte)2);
        r.setMinorTickType((byte)0);
        r.setLabelPosition((byte)3);
        r.setBackground((byte)1);
        r.setLabelColorRgb(0);
        r.setZero1(0);
        r.setZero2(0);
        r.setZero3((short)45);
        r.setAutorotate(true);
        r.setAutoTextBackground(true);
        r.setRotation((short)0);
        r.setAutorotate(true);
        r.setTickColor((short)77);
        return r;
    }
    
    private TickRecord createTickRecord2() {
        final TickRecord r = this.createTickRecord1();
        r.setZero3((short)0);
        return r;
    }
    
    private AxisOptionsRecord createAxisOptionsRecord() {
        final AxisOptionsRecord r = new AxisOptionsRecord();
        r.setMinimumCategory((short)(-28644));
        r.setMaximumCategory((short)(-28715));
        r.setMajorUnitValue((short)2);
        r.setMajorUnit((short)0);
        r.setMinorUnitValue((short)1);
        r.setMinorUnit((short)0);
        r.setBaseUnit((short)0);
        r.setCrossingPoint((short)(-28644));
        r.setDefaultMinimum(true);
        r.setDefaultMaximum(true);
        r.setDefaultMajor(true);
        r.setDefaultMinorUnit(true);
        r.setIsDate(true);
        r.setDefaultBase(true);
        r.setDefaultCross(true);
        r.setDefaultDateSettings(true);
        return r;
    }
    
    private CategorySeriesAxisRecord createCategorySeriesAxisRecord() {
        final CategorySeriesAxisRecord r = new CategorySeriesAxisRecord();
        r.setCrossingPoint((short)1);
        r.setLabelFrequency((short)1);
        r.setTickMarkFrequency((short)1);
        r.setValueAxisCrossing(true);
        r.setCrossesFarRight(false);
        r.setReversed(false);
        return r;
    }
    
    private AxisRecord createAxisRecord(final short axisType) {
        final AxisRecord r = new AxisRecord();
        r.setAxisType(axisType);
        return r;
    }
    
    private AxisParentRecord createAxisParentRecord() {
        final AxisParentRecord r = new AxisParentRecord();
        r.setAxisType((short)0);
        r.setX(479);
        r.setY(221);
        r.setWidth(2995);
        r.setHeight(2902);
        return r;
    }
    
    private AxisUsedRecord createAxisUsedRecord(final short numAxis) {
        final AxisUsedRecord r = new AxisUsedRecord();
        r.setNumAxis(numAxis);
        return r;
    }
    
    private LinkedDataRecord createDirectLinkRecord() {
        final LinkedDataRecord r = new LinkedDataRecord();
        r.setLinkType((byte)0);
        r.setReferenceType((byte)1);
        r.setCustomNumberFormat(false);
        r.setIndexNumberFmtRecord((short)0);
        r.setFormulaOfLink(null);
        return r;
    }
    
    private FontIndexRecord createFontIndexRecord(final int index) {
        final FontIndexRecord r = new FontIndexRecord();
        r.setFontIndex((short)index);
        return r;
    }
    
    private TextRecord createAllTextRecord() {
        final TextRecord r = new TextRecord();
        r.setHorizontalAlignment((byte)2);
        r.setVerticalAlignment((byte)2);
        r.setDisplayMode((short)1);
        r.setRgbColor(0);
        r.setX(-37);
        r.setY(-60);
        r.setWidth(0);
        r.setHeight(0);
        r.setAutoColor(true);
        r.setShowKey(false);
        r.setShowValue(true);
        r.setVertical(false);
        r.setAutoGeneratedText(true);
        r.setGenerated(true);
        r.setAutoLabelDeleted(false);
        r.setAutoBackground(true);
        r.setRotation((short)0);
        r.setShowCategoryLabelAsPercentage(false);
        r.setShowValueAsPercentage(false);
        r.setShowBubbleSizes(false);
        r.setShowLabel(false);
        r.setIndexOfColorValue((short)77);
        r.setDataLabelPlacement((short)0);
        r.setTextRotation((short)0);
        return r;
    }
    
    private TextRecord createUnknownTextRecord() {
        final TextRecord r = new TextRecord();
        r.setHorizontalAlignment((byte)2);
        r.setVerticalAlignment((byte)2);
        r.setDisplayMode((short)1);
        r.setRgbColor(0);
        r.setX(-37);
        r.setY(-60);
        r.setWidth(0);
        r.setHeight(0);
        r.setAutoColor(true);
        r.setShowKey(false);
        r.setShowValue(false);
        r.setVertical(false);
        r.setAutoGeneratedText(true);
        r.setGenerated(true);
        r.setAutoLabelDeleted(false);
        r.setAutoBackground(true);
        r.setRotation((short)0);
        r.setShowCategoryLabelAsPercentage(false);
        r.setShowValueAsPercentage(false);
        r.setShowBubbleSizes(false);
        r.setShowLabel(false);
        r.setIndexOfColorValue((short)77);
        r.setDataLabelPlacement((short)11088);
        r.setTextRotation((short)0);
        return r;
    }
    
    private DefaultDataLabelTextPropertiesRecord createDefaultTextRecord(final short categoryDataType) {
        final DefaultDataLabelTextPropertiesRecord r = new DefaultDataLabelTextPropertiesRecord();
        r.setCategoryDataType(categoryDataType);
        return r;
    }
    
    private SheetPropertiesRecord createSheetPropsRecord() {
        final SheetPropertiesRecord r = new SheetPropertiesRecord();
        r.setChartTypeManuallyFormatted(false);
        r.setPlotVisibleOnly(true);
        r.setDoNotSizeWithWindow(false);
        r.setDefaultPlotDimensions(true);
        r.setAutoPlotArea(false);
        return r;
    }
    
    private SeriesToChartGroupRecord createSeriesToChartGroupRecord() {
        return new SeriesToChartGroupRecord();
    }
    
    private DataFormatRecord createDataFormatRecord() {
        final DataFormatRecord r = new DataFormatRecord();
        r.setPointNumber((short)(-1));
        r.setSeriesIndex((short)0);
        r.setSeriesNumber((short)0);
        r.setUseExcel4Colors(false);
        return r;
    }
    
    private LinkedDataRecord createCategoriesLinkedDataRecord() {
        final LinkedDataRecord r = new LinkedDataRecord();
        r.setLinkType((byte)2);
        r.setReferenceType((byte)2);
        r.setCustomNumberFormat(false);
        r.setIndexNumberFmtRecord((short)0);
        final Area3DPtg p = new Area3DPtg(0, 31, 1, 1, false, false, false, false, 0);
        r.setFormulaOfLink(new Ptg[] { p });
        return r;
    }
    
    private LinkedDataRecord createValuesLinkedDataRecord() {
        final LinkedDataRecord r = new LinkedDataRecord();
        r.setLinkType((byte)1);
        r.setReferenceType((byte)2);
        r.setCustomNumberFormat(false);
        r.setIndexNumberFmtRecord((short)0);
        final Area3DPtg p = new Area3DPtg(0, 31, 0, 0, false, false, false, false, 0);
        r.setFormulaOfLink(new Ptg[] { p });
        return r;
    }
    
    private LinkedDataRecord createTitleLinkedDataRecord() {
        final LinkedDataRecord r = new LinkedDataRecord();
        r.setLinkType((byte)0);
        r.setReferenceType((byte)1);
        r.setCustomNumberFormat(false);
        r.setIndexNumberFmtRecord((short)0);
        r.setFormulaOfLink(null);
        return r;
    }
    
    private SeriesRecord createSeriesRecord() {
        final SeriesRecord r = new SeriesRecord();
        r.setCategoryDataType((short)1);
        r.setValuesDataType((short)1);
        r.setNumCategories((short)32);
        r.setNumValues((short)31);
        r.setBubbleSeriesType((short)1);
        r.setNumBubbleValues((short)0);
        return r;
    }
    
    private EndRecord createEndRecord() {
        return new EndRecord();
    }
    
    private AreaFormatRecord createAreaFormatRecord1() {
        final AreaFormatRecord r = new AreaFormatRecord();
        r.setForegroundColor(16777215);
        r.setBackgroundColor(0);
        r.setPattern((short)1);
        r.setAutomatic(true);
        r.setInvert(false);
        r.setForecolorIndex((short)78);
        r.setBackcolorIndex((short)77);
        return r;
    }
    
    private AreaFormatRecord createAreaFormatRecord2() {
        final AreaFormatRecord r = new AreaFormatRecord();
        r.setForegroundColor(12632256);
        r.setBackgroundColor(0);
        r.setPattern((short)1);
        r.setAutomatic(false);
        r.setInvert(false);
        r.setForecolorIndex((short)22);
        r.setBackcolorIndex((short)79);
        return r;
    }
    
    private LineFormatRecord createLineFormatRecord(final boolean drawTicks) {
        final LineFormatRecord r = new LineFormatRecord();
        r.setLineColor(0);
        r.setLinePattern((short)0);
        r.setWeight((short)(-1));
        r.setAuto(true);
        r.setDrawTicks(drawTicks);
        r.setColourPaletteIndex((short)77);
        return r;
    }
    
    private LineFormatRecord createLineFormatRecord2() {
        final LineFormatRecord r = new LineFormatRecord();
        r.setLineColor(8421504);
        r.setLinePattern((short)0);
        r.setWeight((short)0);
        r.setAuto(false);
        r.setDrawTicks(false);
        r.setUnknown(false);
        r.setColourPaletteIndex((short)23);
        return r;
    }
    
    private FrameRecord createFrameRecord1() {
        final FrameRecord r = new FrameRecord();
        r.setBorderType((short)0);
        r.setAutoSize(false);
        r.setAutoPosition(true);
        return r;
    }
    
    private FrameRecord createFrameRecord2() {
        final FrameRecord r = new FrameRecord();
        r.setBorderType((short)0);
        r.setAutoSize(true);
        r.setAutoPosition(true);
        return r;
    }
    
    private PlotGrowthRecord createPlotGrowthRecord(final int horizScale, final int vertScale) {
        final PlotGrowthRecord r = new PlotGrowthRecord();
        r.setHorizontalScale(horizScale);
        r.setVerticalScale(vertScale);
        return r;
    }
    
    private SCLRecord createSCLRecord(final short numerator, final short denominator) {
        final SCLRecord r = new SCLRecord();
        r.setDenominator(denominator);
        r.setNumerator(numerator);
        return r;
    }
    
    private BeginRecord createBeginRecord() {
        return new BeginRecord();
    }
    
    private ChartRecord createChartRecord(final int x, final int y, final int width, final int height) {
        final ChartRecord r = new ChartRecord();
        r.setX(x);
        r.setY(y);
        r.setWidth(width);
        r.setHeight(height);
        return r;
    }
    
    private UnitsRecord createUnitsRecord() {
        final UnitsRecord r = new UnitsRecord();
        r.setUnits((short)0);
        return r;
    }
    
    public HSSFSeries createSeries() throws Exception {
        final ArrayList<RecordBase> seriesTemplate = new ArrayList<RecordBase>();
        boolean seriesTemplateFilled = false;
        int idx = 0;
        int deep = 0;
        int chartRecordIdx = -1;
        int chartDeep = -1;
        int lastSeriesDeep = -1;
        int endSeriesRecordIdx = -1;
        int seriesIdx = 0;
        final List<RecordBase> records = this.sheet.getSheet().getRecords();
        for (final RecordBase record : records) {
            ++idx;
            if (record instanceof BeginRecord) {
                ++deep;
            }
            else if (record instanceof EndRecord) {
                --deep;
                if (lastSeriesDeep == deep) {
                    lastSeriesDeep = -1;
                    endSeriesRecordIdx = idx;
                    if (!seriesTemplateFilled) {
                        seriesTemplate.add(record);
                        seriesTemplateFilled = true;
                    }
                }
                if (chartDeep == deep) {
                    break;
                }
            }
            if (record instanceof ChartRecord) {
                if (record == this.chartRecord) {
                    chartRecordIdx = idx;
                    chartDeep = deep;
                }
            }
            else if (record instanceof SeriesRecord && chartRecordIdx != -1) {
                ++seriesIdx;
                lastSeriesDeep = deep;
            }
            if (lastSeriesDeep != -1 && !seriesTemplateFilled) {
                seriesTemplate.add(record);
            }
        }
        if (endSeriesRecordIdx == -1) {
            return null;
        }
        idx = endSeriesRecordIdx + 1;
        HSSFSeries newSeries = null;
        final ArrayList<RecordBase> clonedRecords = new ArrayList<RecordBase>();
        for (final RecordBase record2 : seriesTemplate) {
            Record newRecord = null;
            if (record2 instanceof BeginRecord) {
                newRecord = new BeginRecord();
            }
            else if (record2 instanceof EndRecord) {
                newRecord = new EndRecord();
            }
            else if (record2 instanceof SeriesRecord) {
                final SeriesRecord seriesRecord = ((SeriesRecord)record2).copy();
                newSeries = new HSSFSeries(seriesRecord);
                newRecord = seriesRecord;
            }
            else if (record2 instanceof LinkedDataRecord) {
                final LinkedDataRecord linkedDataRecord = ((LinkedDataRecord)record2).copy();
                if (newSeries != null) {
                    newSeries.insertData(linkedDataRecord);
                }
                newRecord = linkedDataRecord;
            }
            else if (record2 instanceof DataFormatRecord) {
                final DataFormatRecord dataFormatRecord = ((DataFormatRecord)record2).copy();
                dataFormatRecord.setSeriesIndex((short)seriesIdx);
                dataFormatRecord.setSeriesNumber((short)seriesIdx);
                newRecord = dataFormatRecord;
            }
            else if (record2 instanceof SeriesTextRecord) {
                final SeriesTextRecord seriesTextRecord = ((SeriesTextRecord)record2).copy();
                if (newSeries != null) {
                    newSeries.setSeriesTitleText(seriesTextRecord);
                }
                newRecord = seriesTextRecord;
            }
            else if (record2 instanceof Record) {
                newRecord = ((Record)record2).copy();
            }
            if (newRecord != null) {
                clonedRecords.add(newRecord);
            }
        }
        if (newSeries == null) {
            return null;
        }
        for (final RecordBase record2 : clonedRecords) {
            records.add(idx++, record2);
        }
        return newSeries;
    }
    
    public boolean removeSeries(final HSSFSeries remSeries) {
        int deep = 0;
        int chartDeep = -1;
        int lastSeriesDeep = -1;
        int seriesIdx = -1;
        boolean removeSeries = false;
        boolean chartEntered = false;
        boolean result = false;
        final List<RecordBase> records = this.sheet.getSheet().getRecords();
        final Iterator<RecordBase> iter = records.iterator();
        while (iter.hasNext()) {
            final RecordBase record = iter.next();
            if (record instanceof BeginRecord) {
                ++deep;
            }
            else if (record instanceof EndRecord) {
                --deep;
                if (lastSeriesDeep == deep) {
                    lastSeriesDeep = -1;
                    if (removeSeries) {
                        removeSeries = false;
                        result = true;
                        iter.remove();
                    }
                }
                if (chartDeep == deep) {
                    break;
                }
            }
            if (record instanceof ChartRecord) {
                if (record == this.chartRecord) {
                    chartDeep = deep;
                    chartEntered = true;
                }
            }
            else if (record instanceof SeriesRecord) {
                if (chartEntered) {
                    if (remSeries.series == record) {
                        lastSeriesDeep = deep;
                        removeSeries = true;
                    }
                    else {
                        ++seriesIdx;
                    }
                }
            }
            else if (record instanceof DataFormatRecord && chartEntered && !removeSeries) {
                final DataFormatRecord dataFormatRecord = (DataFormatRecord)record;
                dataFormatRecord.setSeriesIndex((short)seriesIdx);
                dataFormatRecord.setSeriesNumber((short)seriesIdx);
            }
            if (removeSeries) {
                iter.remove();
            }
        }
        return result;
    }
    
    public HSSFChartType getType() {
        return this.type;
    }
    
    public enum HSSFChartType
    {
        Area {
            @Override
            public short getSid() {
                return 4122;
            }
        }, 
        Bar {
            @Override
            public short getSid() {
                return 4119;
            }
        }, 
        Line {
            @Override
            public short getSid() {
                return 4120;
            }
        }, 
        Pie {
            @Override
            public short getSid() {
                return 4121;
            }
        }, 
        Scatter {
            @Override
            public short getSid() {
                return 4123;
            }
        }, 
        Unknown {
            @Override
            public short getSid() {
                return 0;
            }
        };
        
        public abstract short getSid();
    }
    
    public static class HSSFSeries
    {
        private SeriesRecord series;
        private SeriesTextRecord seriesTitleText;
        private LinkedDataRecord dataName;
        private LinkedDataRecord dataValues;
        private LinkedDataRecord dataCategoryLabels;
        private LinkedDataRecord dataSecondaryCategoryLabels;
        
        HSSFSeries(final SeriesRecord series) {
            this.series = series;
        }
        
        void insertData(final LinkedDataRecord data) {
            switch (data.getLinkType()) {
                case 0: {
                    this.dataName = data;
                    break;
                }
                case 1: {
                    this.dataValues = data;
                    break;
                }
                case 2: {
                    this.dataCategoryLabels = data;
                    break;
                }
                case 3: {
                    this.dataSecondaryCategoryLabels = data;
                    break;
                }
                default: {
                    throw new IllegalStateException("Invalid link type: " + data.getLinkType());
                }
            }
        }
        
        void setSeriesTitleText(final SeriesTextRecord seriesTitleText) {
            this.seriesTitleText = seriesTitleText;
        }
        
        public short getNumValues() {
            return this.series.getNumValues();
        }
        
        public short getValueType() {
            return this.series.getValuesDataType();
        }
        
        public String getSeriesTitle() {
            if (this.seriesTitleText != null) {
                return this.seriesTitleText.getText();
            }
            return null;
        }
        
        public void setSeriesTitle(final String title) {
            if (this.seriesTitleText != null) {
                this.seriesTitleText.setText(title);
                return;
            }
            throw new IllegalStateException("No series title found to change");
        }
        
        public LinkedDataRecord getDataName() {
            return this.dataName;
        }
        
        public LinkedDataRecord getDataValues() {
            return this.dataValues;
        }
        
        public LinkedDataRecord getDataCategoryLabels() {
            return this.dataCategoryLabels;
        }
        
        public LinkedDataRecord getDataSecondaryCategoryLabels() {
            return this.dataSecondaryCategoryLabels;
        }
        
        public SeriesRecord getSeries() {
            return this.series;
        }
        
        private CellRangeAddressBase getCellRange(final LinkedDataRecord linkedDataRecord) {
            if (linkedDataRecord == null) {
                return null;
            }
            int firstRow = 0;
            int lastRow = 0;
            int firstCol = 0;
            int lastCol = 0;
            for (final Ptg ptg : linkedDataRecord.getFormulaOfLink()) {
                if (ptg instanceof AreaPtgBase) {
                    final AreaPtgBase areaPtg = (AreaPtgBase)ptg;
                    firstRow = areaPtg.getFirstRow();
                    lastRow = areaPtg.getLastRow();
                    firstCol = areaPtg.getFirstColumn();
                    lastCol = areaPtg.getLastColumn();
                }
            }
            return new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        }
        
        public CellRangeAddressBase getValuesCellRange() {
            return this.getCellRange(this.dataValues);
        }
        
        public CellRangeAddressBase getCategoryLabelsCellRange() {
            return this.getCellRange(this.dataCategoryLabels);
        }
        
        private Integer setVerticalCellRange(final LinkedDataRecord linkedDataRecord, final CellRangeAddressBase range) {
            if (linkedDataRecord == null) {
                return null;
            }
            final List<Ptg> ptgList = new ArrayList<Ptg>();
            final int rowCount = range.getLastRow() - range.getFirstRow() + 1;
            final int colCount = range.getLastColumn() - range.getFirstColumn() + 1;
            for (final Ptg ptg : linkedDataRecord.getFormulaOfLink()) {
                if (ptg instanceof AreaPtgBase) {
                    final AreaPtgBase areaPtg = (AreaPtgBase)ptg;
                    areaPtg.setFirstRow(range.getFirstRow());
                    areaPtg.setLastRow(range.getLastRow());
                    areaPtg.setFirstColumn(range.getFirstColumn());
                    areaPtg.setLastColumn(range.getLastColumn());
                    ptgList.add(areaPtg);
                }
            }
            linkedDataRecord.setFormulaOfLink(ptgList.toArray(new Ptg[0]));
            return rowCount * colCount;
        }
        
        public void setValuesCellRange(final CellRangeAddressBase range) {
            final Integer count = this.setVerticalCellRange(this.dataValues, range);
            if (count == null) {
                return;
            }
            this.series.setNumValues((short)(int)count);
        }
        
        public void setCategoryLabelsCellRange(final CellRangeAddressBase range) {
            final Integer count = this.setVerticalCellRange(this.dataCategoryLabels, range);
            if (count == null) {
                return;
            }
            this.series.setNumCategories((short)(int)count);
        }
    }
}
