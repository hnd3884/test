package com.me.devicemanagement.framework.webclient.graphs.util;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartRenderingInfo;
import java.io.OutputStream;
import org.jfree.chart.ChartUtilities;
import java.awt.Paint;
import java.awt.Color;
import com.me.devicemanagement.framework.webclient.graphs.DCChartPostProcessor;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.Map;
import org.jfree.data.general.Dataset;
import com.me.devicemanagement.framework.webclient.graphs.DCGraphDatasetImpl;
import java.io.ByteArrayOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import javax.xml.namespace.QName;
import org.w3c.dom.Document;
import java.net.URLDecoder;
import com.me.devicemanagement.framework.webclient.graphs.GraphEntry;
import java.util.LinkedList;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.graphs.data.GraphDataProducer;
import java.util.HashMap;
import com.me.devicemanagement.framework.webclient.graphs.GraphBean;
import java.util.ArrayList;
import java.util.logging.Logger;

public class GraphUtil
{
    private static GraphUtil util;
    Logger logger;
    ArrayList<String> colorMap;
    
    public GraphUtil() {
        this.logger = Logger.getLogger(GraphUtil.class.getName());
        this.colorMap = null;
        this.initializeColorMap();
    }
    
    public static GraphUtil getInstance() {
        if (GraphUtil.util == null) {
            GraphUtil.util = new GraphUtil();
        }
        return GraphUtil.util;
    }
    
    private void initializeColorMap() {
        (this.colorMap = new ArrayList<String>()).add("FEA500");
        this.colorMap.add("F6F167");
        this.colorMap.add("48B14B");
        this.colorMap.add("2DAFEC");
        this.colorMap.add("0586C2");
        this.colorMap.add("FB6764");
        this.colorMap.add("F06439");
        this.colorMap.add("5EA6EC");
        this.colorMap.add("FFB840");
        this.colorMap.add("F3951C");
        this.colorMap.add("8D81B7");
        this.colorMap.add("D0651E");
        this.colorMap.add("00ABC2");
        this.colorMap.add("E06288");
        this.colorMap.add("96A233");
        this.colorMap.add("B289B2");
        this.colorMap.add("FF00FF");
        this.colorMap.add("70DB93");
        this.colorMap.add("F4A460");
        this.colorMap.add("FA8072");
        this.colorMap.add("778899");
        this.colorMap.add("800080");
        this.colorMap.add("9ACD32");
        this.colorMap.add("7B68EE");
        this.colorMap.add("DC143C");
    }
    
    public String getColorFromColorMap(final int index) {
        return this.colorMap.get(index);
    }
    
    public void populateGraphBean(final GraphBean bean, final String graphName, final String fileName, final String generatorClass, final String params) throws Exception {
        HashMap<String, String> parameterMap = new HashMap<String, String>();
        if (params != null && !params.equalsIgnoreCase("")) {
            parameterMap = this.getParameterMap(params);
        }
        parameterMap.put("XML_NAME", fileName);
        this.getGraphBean(bean, graphName, generatorClass, parameterMap);
    }
    
    private GraphBean getGraphBean(final GraphBean gBean, final String graphName, final String generatorClass, final HashMap parameterMap) throws Exception {
        try {
            gBean.setName(graphName);
            final GraphDataProducer graphDataProducer = (GraphDataProducer)Class.forName(generatorClass).newInstance();
            final HashMap<String, String> chartProps = graphDataProducer.getChartProps(graphName, parameterMap);
            gBean.setTitle(I18N.getMsg((String)chartProps.get("title"), new Object[0]));
            gBean.setXaxisLabel(I18N.getMsg((String)chartProps.get("xaxislabel"), new Object[0]));
            gBean.setYaxisLabel(I18N.getMsg((String)chartProps.get("yaxislabel"), new Object[0]));
            final LinkedList<GraphEntry> columns = graphDataProducer.getGraphColumnsWithValues(graphName, parameterMap);
            gBean.setGraphEntries(columns);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while generating GraphBean for " + graphName + " :: ", e);
            throw e;
        }
        return gBean;
    }
    
    private HashMap getParameterMap(final String paramString) {
        final HashMap map = new HashMap();
        for (final String param : paramString.split(";")) {
            final String[] pair = param.split("=");
            String value = pair[1];
            if (value != null) {
                value = URLDecoder.decode(value);
            }
            map.put(pair[0], value);
        }
        return map;
    }
    
    public Object evaluateXPath(final String expression, final Document doc, final QName returnType) {
        Object obj = null;
        final XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            final XPathExpression expr = xpath.compile(expression);
            obj = expr.evaluate(doc, returnType);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while evaluating XPath Expression :: ", e);
        }
        return obj;
    }
    
    public HashMap<String, String> getNodeAttributes(final Node node) {
        final NamedNodeMap attributeMap = node.getAttributes();
        final HashMap<String, String> props = new HashMap<String, String>();
        for (int size = attributeMap.getLength(), i = 0; i < size; ++i) {
            final Node nodeItem = attributeMap.item(i);
            props.put(nodeItem.getNodeName(), nodeItem.getNodeValue());
        }
        return props;
    }
    
    public GraphEntry getHtmlColumn(final String name, final String label, final String actionLink, final Object count) {
        final GraphEntry column = new GraphEntry();
        column.setName(name);
        column.setLabel(label);
        long value = 0L;
        if (count instanceof Integer) {
            value = (int)count;
        }
        column.setValue(value);
        column.setActionLink(actionLink);
        return column;
    }
    
    public ByteArrayOutputStream getGraphImageStream(final String graphName, final String graphXMLFile, final String generatorClass) {
        return this.getGraphImageStream(graphName, graphXMLFile, generatorClass, null, null);
    }
    
    public ByteArrayOutputStream getGraphImageStream(final String graphName, final String graphXMLFile, final String generatorClass, final String graphParams) {
        return this.getGraphImageStream(graphName, graphXMLFile, generatorClass, graphParams, null);
    }
    
    public ByteArrayOutputStream getGraphImageStream(final String graphName, final String graphXMLFile, final String generatorClass, final String graphParams, final HashMap displayProps) {
        JFreeChart chart = null;
        final ByteArrayOutputStream imgStream = new ByteArrayOutputStream();
        try {
            final GraphBean gBean = new GraphBean();
            gBean.setGraphValues(graphName, graphXMLFile, generatorClass, graphParams);
            final DCGraphDatasetImpl dataSetImpl = new DCGraphDatasetImpl();
            final HashMap params = new HashMap();
            String chartType = "Bar3D";
            int chartWidth = 300;
            int chartHeight = 200;
            if (displayProps != null && !displayProps.isEmpty()) {
                chartType = (displayProps.containsKey("TYPE") ? displayProps.get("TYPE") : chartType);
                chartWidth = (displayProps.containsKey("WIDTH") ? displayProps.get("WIDTH") : chartWidth);
                chartHeight = (displayProps.containsKey("HEIGHT") ? displayProps.get("HEIGHT") : chartHeight);
            }
            params.put("chartType", chartType);
            params.put("graphData", gBean.getGraphEntries());
            final Dataset dataSet = (Dataset)dataSetImpl.produceDataset(params);
            if (chartType.equalsIgnoreCase("Bar3D")) {
                final DefaultCategoryDataset categoryDataset = (DefaultCategoryDataset)dataSet;
                chart = ChartFactory.createBarChart3D(gBean.getTitle(), gBean.getXaxisLabel(), gBean.getYaxisLabel(), (CategoryDataset)categoryDataset, PlotOrientation.VERTICAL, true, false, false);
            }
            else if (chartType.equalsIgnoreCase("Bar")) {
                final DefaultCategoryDataset categoryDataset = (DefaultCategoryDataset)dataSet;
                chart = ChartFactory.createBarChart(gBean.getTitle(), gBean.getXaxisLabel(), gBean.getYaxisLabel(), (CategoryDataset)categoryDataset, PlotOrientation.VERTICAL, true, false, false);
            }
            else if (chartType.equalsIgnoreCase("Pie3D")) {
                final DefaultPieDataset pieDataset = (DefaultPieDataset)dataSet;
                chart = ChartFactory.createPieChart3D(gBean.getTitle(), (PieDataset)pieDataset, true, true, false);
            }
            else if (chartType.equalsIgnoreCase("Pie")) {
                final DefaultPieDataset pieDataset = (DefaultPieDataset)dataSet;
                chart = ChartFactory.createPieChart(gBean.getTitle(), (PieDataset)pieDataset, true, true, false);
            }
            final DCChartPostProcessor chartPostProcessor = new DCChartPostProcessor();
            chartPostProcessor.processChart(chart, params);
            chart.setBackgroundPaint((Paint)Color.WHITE);
            ChartUtilities.writeChartAsJPEG((OutputStream)imgStream, 1.0f, chart, chartWidth, chartHeight, (ChartRenderingInfo)null);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while generating graph image stream for graph name " + graphName, e);
        }
        return imgStream;
    }
}
