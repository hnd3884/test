package org.apache.poi.xssf.usermodel.charts;

import org.apache.poi.ss.usermodel.charts.ScatterChartData;
import org.apache.poi.ss.usermodel.charts.LineChartData;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.charts.ChartDataFactory;

@Deprecated
@Removal(version = "4.2")
public class XSSFChartDataFactory implements ChartDataFactory
{
    private static XSSFChartDataFactory instance;
    
    private XSSFChartDataFactory() {
    }
    
    public XSSFScatterChartData createScatterChartData() {
        return new XSSFScatterChartData();
    }
    
    public XSSFLineChartData createLineChartData() {
        return new XSSFLineChartData();
    }
    
    public static XSSFChartDataFactory getInstance() {
        if (XSSFChartDataFactory.instance == null) {
            XSSFChartDataFactory.instance = new XSSFChartDataFactory();
        }
        return XSSFChartDataFactory.instance;
    }
}
