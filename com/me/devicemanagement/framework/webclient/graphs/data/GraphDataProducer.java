package com.me.devicemanagement.framework.webclient.graphs.data;

import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.me.devicemanagement.framework.webclient.graphs.GraphEntry;
import java.util.LinkedList;
import javax.xml.xpath.XPathConstants;
import com.me.devicemanagement.framework.webclient.graphs.util.GraphUtil;
import org.w3c.dom.Node;
import java.util.HashMap;
import java.util.logging.Logger;
import org.w3c.dom.Document;

public abstract class GraphDataProducer
{
    private Document doc;
    public Logger logger;
    
    public GraphDataProducer() {
        this.logger = Logger.getLogger(GraphDataProducer.class.getName());
    }
    
    public HashMap<String, String> getChartProps(final String name, final HashMap parameterMap) throws Exception {
        final String xmlName = parameterMap.get("XML_NAME");
        this.initialize(xmlName);
        parameterMap.remove("XML_NAME");
        final String expression = "//graph[@name=\"" + name + "\"]/chart";
        final Node chartNode = (Node)GraphUtil.getInstance().evaluateXPath(expression, this.doc, XPathConstants.NODE);
        if (chartNode == null) {
            throw new Exception("No graph node for \"" + name + "\" in the XML " + xmlName);
        }
        return GraphUtil.getInstance().getNodeAttributes(chartNode);
    }
    
    public LinkedList<GraphEntry> getGraphColumns(final String name, final HashMap parameterMap) throws Exception {
        final LinkedList<GraphEntry> columns = new LinkedList<GraphEntry>();
        try {
            final String expression = "//graph[@name=\"" + name + "\"]/data/column";
            final GraphUtil graphUtil = GraphUtil.getInstance();
            final NodeList columnNodes = (NodeList)graphUtil.evaluateXPath(expression, this.doc, XPathConstants.NODESET);
            for (int j = 0; j < columnNodes.getLength(); ++j) {
                final Element column = (Element)columnNodes.item(j);
                final String columnName = column.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
                final String label = column.getElementsByTagName("label").item(0).getFirstChild().getNodeValue();
                final NodeList colorTag = column.getElementsByTagName("color");
                final String color = (colorTag.getLength() != 0) ? colorTag.item(0).getFirstChild().getNodeValue() : graphUtil.getColorFromColorMap(j);
                final NodeList actionTag = column.getElementsByTagName("action");
                final String action = (actionTag.getLength() != 0) ? actionTag.item(0).getFirstChild().getNodeValue() : null;
                final GraphEntry gEntry = new GraphEntry();
                gEntry.setName(columnName);
                gEntry.setLabel(I18N.getMsg(label, new Object[0]));
                gEntry.setColor(color);
                gEntry.setActionLink(action);
                if (columnName.equalsIgnoreCase("TOTAL")) {
                    columns.remove(gEntry);
                }
                else {
                    columns.add(gEntry);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting graph columns from XML for : " + name, e);
        }
        return columns;
    }
    
    public abstract HashMap<String, Long> getGraphValues(final String p0, final HashMap p1) throws Exception;
    
    public String getCardNameFromFilter(final Properties parameterProp) throws Exception {
        return null;
    }
    
    public LinkedList<GraphEntry> getGraphColumnsWithValues(final String name, final HashMap parameterMap) throws Exception {
        LinkedList<GraphEntry> columns = this.getGraphColumns(name, parameterMap);
        final HashMap<String, Long> values = this.getGraphValues(name, parameterMap);
        if (values != null && !values.isEmpty()) {
            for (final GraphEntry column : columns) {
                column.setValue(values.get(column.getName()));
            }
        }
        else if (values == null) {
            columns = null;
        }
        else {
            for (final GraphEntry column : columns) {
                column.setValue(0L);
            }
        }
        return columns;
    }
    
    private void initialize(final String xmlName) throws Exception {
        String graphXMLFile = SyMUtil.getInstallationDir() + File.separator + xmlName;
        if (CustomerInfoUtil.isSAS && File.separator.equals("/") && graphXMLFile.contains("\\")) {
            graphXMLFile = graphXMLFile.replaceAll("\\\\", File.separator);
        }
        final InputStream fis = new FileInputStream(graphXMLFile);
        final DocumentBuilder builder = DMSecurityUtil.getDocumentBuilder();
        this.doc = builder.parse(fis);
    }
}
