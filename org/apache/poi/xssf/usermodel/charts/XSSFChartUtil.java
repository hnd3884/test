package org.apache.poi.xssf.usermodel.charts;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.apache.poi.util.Removal;

@Deprecated
@Removal(version = "4.2")
class XSSFChartUtil
{
    private XSSFChartUtil() {
    }
    
    public static void buildAxDataSource(final CTAxDataSource ctAxDataSource, final ChartDataSource<?> dataSource) {
        if (dataSource.isNumeric()) {
            if (dataSource.isReference()) {
                buildNumRef(ctAxDataSource.addNewNumRef(), dataSource);
            }
            else {
                buildNumLit(ctAxDataSource.addNewNumLit(), dataSource);
            }
        }
        else if (dataSource.isReference()) {
            buildStrRef(ctAxDataSource.addNewStrRef(), dataSource);
        }
        else {
            buildStrLit(ctAxDataSource.addNewStrLit(), dataSource);
        }
    }
    
    public static void buildNumDataSource(final CTNumDataSource ctNumDataSource, final ChartDataSource<? extends Number> dataSource) {
        if (dataSource.isReference()) {
            buildNumRef(ctNumDataSource.addNewNumRef(), dataSource);
        }
        else {
            buildNumLit(ctNumDataSource.addNewNumLit(), dataSource);
        }
    }
    
    private static void buildNumRef(final CTNumRef ctNumRef, final ChartDataSource<?> dataSource) {
        ctNumRef.setF(dataSource.getFormulaString());
        final CTNumData cache = ctNumRef.addNewNumCache();
        fillNumCache(cache, dataSource);
    }
    
    private static void buildNumLit(final CTNumData ctNumData, final ChartDataSource<?> dataSource) {
        fillNumCache(ctNumData, dataSource);
    }
    
    private static void buildStrRef(final CTStrRef ctStrRef, final ChartDataSource<?> dataSource) {
        ctStrRef.setF(dataSource.getFormulaString());
        final CTStrData cache = ctStrRef.addNewStrCache();
        fillStringCache(cache, dataSource);
    }
    
    private static void buildStrLit(final CTStrData ctStrData, final ChartDataSource<?> dataSource) {
        fillStringCache(ctStrData, dataSource);
    }
    
    private static void fillStringCache(final CTStrData cache, final ChartDataSource<?> dataSource) {
        final int numOfPoints = dataSource.getPointCount();
        cache.addNewPtCount().setVal((long)numOfPoints);
        for (int i = 0; i < numOfPoints; ++i) {
            final Object value = dataSource.getPointAt(i);
            if (value != null) {
                final CTStrVal ctStrVal = cache.addNewPt();
                ctStrVal.setIdx((long)i);
                ctStrVal.setV(value.toString());
            }
        }
    }
    
    private static void fillNumCache(final CTNumData cache, final ChartDataSource<?> dataSource) {
        final int numOfPoints = dataSource.getPointCount();
        cache.addNewPtCount().setVal((long)numOfPoints);
        for (int i = 0; i < numOfPoints; ++i) {
            final Number value = (Number)dataSource.getPointAt(i);
            if (value != null) {
                final CTNumVal ctNumVal = cache.addNewPt();
                ctNumVal.setIdx((long)i);
                ctNumVal.setV(value.toString());
            }
        }
    }
}
