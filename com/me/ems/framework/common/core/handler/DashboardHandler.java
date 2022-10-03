package com.me.ems.framework.common.core.handler;

import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import java.io.FileInputStream;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.io.File;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import javax.xml.xpath.XPathConstants;
import com.me.devicemanagement.framework.webclient.graphs.util.GraphUtil;
import org.w3c.dom.NodeList;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.i18n.I18N;
import java.util.List;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.Encoder;
import com.me.devicemanagement.framework.webclient.graphs.GraphBean;
import com.me.ems.framework.common.api.utils.DashboardUtil;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import org.json.simple.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;

public class DashboardHandler
{
    private static DashboardHandler dashboardHandler;
    private static String sourceClass;
    private static Logger logger;
    
    public static synchronized DashboardHandler getInstance() {
        if (DashboardHandler.dashboardHandler == null) {
            DashboardHandler.dashboardHandler = new DashboardHandler();
        }
        return DashboardHandler.dashboardHandler;
    }
    
    public JSONObject getModuleGraphJSON(final Properties graphProperties) throws Exception {
        final String sourceMethod = "getModuleGraphJSON";
        String graphName = ((Hashtable<K, String>)graphProperties).get("viewName");
        final JSONObject graphJSON = new JSONObject();
        SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Fetch the zoho chart graph data for the card: " + graphName);
        SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, graphName + " properties::" + graphProperties.toString());
        try {
            Long cardId = -1L;
            Boolean isResourceDashboard = false;
            Boolean isCardFilterApplied = false;
            Boolean decodeChartData = false;
            if (graphProperties.get("isCardFilterApplied") != null) {
                isCardFilterApplied = ((Hashtable<K, Boolean>)graphProperties).get("isCardFilterApplied");
            }
            if (graphProperties.get("isResourceDashboard") != null) {
                isResourceDashboard = Boolean.parseBoolean(((Hashtable<K, Object>)graphProperties).get("isResourceDashboard").toString());
            }
            if (graphProperties.get("decodeChartData") != null) {
                decodeChartData = ((Hashtable<K, Boolean>)graphProperties).get("decodeChartData");
            }
            if (graphProperties.get("cardId") != null) {
                cardId = ((Hashtable<K, Long>)graphProperties).get("cardId");
            }
            else {
                cardId = DashboardUtil.getInstance().getCardId(graphName);
            }
            Long resourceId = -1L;
            String params = null;
            if (isResourceDashboard) {
                resourceId = Long.parseLong(((Hashtable<K, Object>)graphProperties).get("resourceId").toString());
                params = "resourceId=" + resourceId;
            }
            if (isCardFilterApplied) {
                graphName = DashboardUtil.getInstance().getCardNameFromFilter(graphProperties);
            }
            final HashMap graphHash = DashboardUtil.getInstance().getGraphDetails(cardId);
            final GraphBean graphBean = new GraphBean();
            final String graphType = graphHash.get("DEFAULT_CHART_TYPE");
            graphBean.setGraphType(graphType);
            graphBean.setGraphValues(graphName, graphHash.get("XML_FILE"), graphHash.get("GEN_CLASS"), graphHash.get("CHART_IMPL"), params);
            String chartJSONData = graphBean.getEncodedGraphData();
            if (decodeChartData) {
                chartJSONData = Encoder.convertFromBase(chartJSONData);
            }
            graphJSON.put((Object)"chartData", (Object)chartJSONData);
            SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Card Data for card: " + graphName + " has been formed successfully");
        }
        catch (final Exception ex) {
            SyMLogger.error(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Exception while fetching the card data for the graph: " + graphName, ex);
            throw ex;
        }
        return graphJSON;
    }
    
    public JSONObject getModuleHTMLCountJSON(final Properties graphProperties) throws Exception {
        final String sourceMethod = "getModuleHTMLCountJSON";
        final JSONObject resultObj = new JSONObject();
        JSONArray countArray = new JSONArray();
        final String cardName = ((Hashtable<K, String>)graphProperties).get("viewName");
        Long cardId = -1L;
        try {
            if (graphProperties.get("cardId") != null) {
                cardId = ((Hashtable<K, Long>)graphProperties).get("cardId");
            }
            else {
                cardId = DashboardUtil.getInstance().getCardId(cardName);
            }
            final HashMap cardHash = DashboardUtil.getInstance().getGraphDetails(cardId);
            final String implClassName = cardHash.get("GEN_CLASS");
            final CardCountDataProvider cardObj = CardCountDataProvider.getInstance(implClassName);
            countArray = cardObj.getCountDataArray(cardName, graphProperties);
        }
        catch (final Exception ex) {
            SyMLogger.error(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Exception while fetching the card data for the graph: " + cardName, ex);
        }
        resultObj.put((Object)"countData", (Object)countArray);
        return resultObj;
    }
    
    public JSONObject getModuleHTMLFeedJSON(final Properties graphProperties) throws Exception {
        final String sourceMethod = "getModuleHTMLFeedJSON";
        final String cardName = ((Hashtable<K, String>)graphProperties).get("viewName");
        SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Fetch the zoho chart graph data for the card: " + cardName);
        SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, cardName + " properties::" + graphProperties.toString());
        Long cardId = -1L;
        String resultStr = "{}";
        try {
            if (graphProperties.get("cardId") != null) {
                cardId = ((Hashtable<K, Long>)graphProperties).get("cardId");
            }
            else {
                cardId = DashboardUtil.getInstance().getCardId(cardName);
            }
            final HashMap cardHash = DashboardUtil.getInstance().getGraphDetails(cardId);
            final String implClassName = cardHash.get("GEN_CLASS");
            final FeedDataProvider feedObj = FeedDataProvider.getInstance(implClassName);
            final List<HashMap> feeds = feedObj.getFeeds();
            resultStr = JSONValue.toJSONString((Object)feeds);
        }
        catch (final Exception ex) {
            SyMLogger.error(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Exception while fetching the card data for the graph: " + cardName, ex);
        }
        final JSONObject resultObj = new JSONObject();
        final JSONArray resultArr = (JSONArray)new JSONParser().parse(resultStr);
        resultObj.put((Object)"feedData", (Object)resultArr);
        return resultObj;
    }
    
    public String getModuleHTMLTableJSON(final Properties graphProperties) throws Exception {
        final String sourceMethod = "getModuleHTMLTableJSON";
        final String viewName = ((Hashtable<K, String>)graphProperties).get("viewName");
        SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Fetch the zoho chart graph data for the card: " + viewName);
        SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, viewName + " properties::" + graphProperties.toString());
        SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Card Data for card: " + viewName + " has been formed successfully");
        return viewName;
    }
    
    public JSONArray getModuleMultiGraphJSON(final Properties cardProperties, final Properties additionalProps) throws Exception {
        final String sourceMethod = "getModuleMultiGraphJSON";
        JSONArray jsonArray = new JSONArray();
        final String cardName = ((Hashtable<K, String>)cardProperties).get("viewName");
        SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Fetch the zoho chart graph data for the multi-card: " + cardName);
        SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, cardName + " properties::" + cardProperties.toString());
        SyMLogger.info(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Card Data for card: " + cardName + " has been formed successfully");
        Long cardId = -1L;
        try {
            if (cardProperties.get("cardId") != null) {
                cardId = ((Hashtable<K, Long>)cardProperties).get("cardId");
            }
            else {
                cardId = DashboardUtil.getInstance().getCardId(cardName);
            }
            final HashMap cardHash = DashboardUtil.getInstance().getGraphDetails(cardId);
            final String implClassName = cardHash.get("GEN_CLASS");
            final MultiCardDataProvider multiCardDataProvider = MultiCardDataProvider.getInstance(implClassName);
            jsonArray = multiCardDataProvider.getMultiCardData(cardName, cardProperties, additionalProps);
        }
        catch (final Exception ex) {
            SyMLogger.error(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Exception while fetching the card data for the graph: " + cardName, ex);
        }
        return jsonArray;
    }
    
    private JSONObject setDefaultMetaData(final Properties cardProps) throws Exception {
        final JSONObject metaJSON = new JSONObject();
        final String noDataHeading = I18N.getMsg("dc.common.NO_DATA_AVAILABLE", new Object[0]);
        final String noDataSubText = I18N.getMsg("dc.common.NO_DATA_AVAILABLE", new Object[0]);
        metaJSON.put((Object)"noDataImg", (Object)"");
        metaJSON.put((Object)"noDataHeading", (Object)noDataHeading);
        metaJSON.put((Object)"noDataSubText", (Object)noDataSubText);
        final Boolean showFilter = ((Hashtable<K, Boolean>)cardProps).get("showFilter");
        if (showFilter && cardProps.containsKey("filterData")) {
            metaJSON.put((Object)"filterData", ((Hashtable<K, Object>)cardProps).get("filterData"));
            metaJSON.put((Object)"showFilter", (Object)true);
        }
        metaJSON.put((Object)"redirectToIndex", (Object)2);
        return metaJSON;
    }
    
    public JSONObject getChartMetaData(final Properties cardProps) throws Exception {
        final String sourceMethod = "getChartMetaData";
        final JSONObject metaJSON = this.setDefaultMetaData(cardProps);
        final String cardName = ((Hashtable<K, String>)cardProps).get("viewName");
        Long cardId = -1L;
        try {
            if (cardProps.get("cardId") != null) {
                cardId = ((Hashtable<K, Long>)cardProps).get("cardId");
            }
            else {
                cardId = DashboardUtil.getInstance().getCardId(cardName);
            }
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CardGenerationInfo"));
            selectQuery.addSelectColumn(Column.getColumn("CardGenerationInfo", "*"));
            selectQuery.setCriteria(new Criteria(new Column("CardGenerationInfo", "CARD_ID"), (Object)cardId, 0));
            final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
            if (dataObject != null && !dataObject.isEmpty()) {
                final Row row = DataAccess.get(selectQuery).getFirstRow("CardGenerationInfo");
                final String xmlFilePath = (String)row.get("XML_FILE_PATH");
                final Document doc = this.initialize(xmlFilePath);
                final String expression = "//card[@name=\"" + cardName + "\"]/cardmetadata";
                final NodeList columnNodes = (NodeList)GraphUtil.getInstance().evaluateXPath(expression, doc, XPathConstants.NODESET);
                if (columnNodes == null) {
                    throw new Exception("No card meta data node for \"" + cardName + "\" in the XML " + xmlFilePath);
                }
                for (int j = 0; j < columnNodes.getLength(); ++j) {
                    final Node parent = columnNodes.item(j);
                    final NodeList childList = parent.getChildNodes();
                    for (int i = 0; i < childList.getLength(); ++i) {
                        final Node child = childList.item(i);
                        if (child.getNodeType() == 1) {
                            String key = child.getNodeName();
                            String value = child.getTextContent();
                            value = I18N.getMsg(value, new Object[0]);
                            metaJSON.put((Object)key, (Object)value);
                            key += value;
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(DashboardHandler.logger, DashboardHandler.sourceClass, sourceMethod, "Exception while fetching the meta data for the chart: " + cardName, ex);
        }
        return metaJSON;
    }
    
    private Document initialize(final String xmlName) throws Exception {
        String graphXMLFile = SyMUtil.getInstallationDir() + File.separator + xmlName;
        if (CustomerInfoUtil.isSAS && File.separator.equals("/") && graphXMLFile.contains("\\")) {
            graphXMLFile = graphXMLFile.replaceAll("\\\\", File.separator);
        }
        final InputStream fis = new FileInputStream(graphXMLFile);
        final DocumentBuilder builder = DMSecurityUtil.getDocumentBuilder();
        final Document doc = builder.parse(fis);
        return doc;
    }
    
    static {
        DashboardHandler.sourceClass = DashboardHandler.class.getName();
        DashboardHandler.logger = Logger.getLogger(DashboardHandler.sourceClass);
    }
}
