package com.me.ems.framework.common.api.v1.service;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.graphs.GraphBean;
import com.me.ems.framework.common.api.v1.model.Graph;
import java.util.HashMap;
import java.util.logging.Logger;

public class GraphService
{
    private static Logger logger;
    
    public static Graph getGraphBean(final HashMap<String, Object> graphDetails, final String params) {
        final GraphBean graphBean = new GraphBean();
        try {
            final String graphName = graphDetails.get("GRAPH_NAME");
            final String generatorClass = graphDetails.get("GENERATOR_CLASS");
            final String xmlFile = graphDetails.get("XML_FILE");
            final Object chartGenerator = graphDetails.get("CHART_IMPL");
            final String graphType = graphDetails.getOrDefault("GRAPH_TYPE", "bar");
            graphBean.setGraphType(graphType);
            graphBean.setGraphValues(graphName, xmlFile, generatorClass, (chartGenerator == null) ? "" : ((String)chartGenerator), params);
        }
        catch (final Exception e) {
            GraphService.logger.log(Level.SEVERE, "Exception while getting Graph Bean from graphDetails", e);
        }
        return convertToDCGraphBean(graphBean);
    }
    
    private static Graph convertToDCGraphBean(final GraphBean graphBean) {
        final Graph dcGraph = new Graph();
        try {
            dcGraph.setEncodedGraphData(graphBean.getEncodedGraphData());
            dcGraph.setEncodedUserTheme(graphBean.getEncodedUserTheme());
            dcGraph.setGraphType(graphBean.getGraphType());
            dcGraph.setName(graphBean.getName());
            dcGraph.setTitle(graphBean.getTitle());
            dcGraph.setXaxisLabel(graphBean.getXaxisLabel());
            dcGraph.setYaxisLabel(graphBean.getYaxisLabel());
        }
        catch (final Exception e) {
            GraphService.logger.log(Level.SEVERE, "Exception while Converting To DCGraph Bean", e);
        }
        return dcGraph;
    }
    
    public static HashMap<String, Object> getGraphDetails(final String graphName) {
        final HashMap<String, Object> graphDetails = new HashMap<String, Object>();
        try {
            final SelectQuery graphDetailsSelect = getGraphDetailsSelectQuery(graphName);
            final DataObject graphDetailsDO = SyMUtil.getPersistence().get(graphDetailsSelect);
            if (graphDetailsDO != null && !graphDetailsDO.isEmpty()) {
                final Row graphDetailRow = graphDetailsDO.getFirstRow("GraphDetails");
                graphDetails.put("GRAPH_NAME", graphDetailRow.get("GRAPH_NAME"));
                graphDetails.put("GENERATOR_CLASS", graphDetailRow.get("GENERATOR_CLASS"));
                graphDetails.put("XML_FILE", (graphDetailRow.get("XML_FILE") != null) ? graphDetailRow.get("XML_FILE") : "");
                graphDetails.put("CHART_IMPL", (graphDetailRow.get("CHART_IMPL") != null) ? graphDetailRow.get("CHART_IMPL") : "");
                graphDetails.put("HIDE_TOGGLE", graphDetailRow.get("CHART_IMPL"));
            }
        }
        catch (final Exception e) {
            GraphService.logger.log(Level.SEVERE, "Exception while getting Graph Details from GraphName", e);
        }
        return graphDetails;
    }
    
    private static SelectQuery getGraphDetailsSelectQuery(final String graphName) {
        final SelectQuery graphDetailsSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("GraphDetails"));
        graphDetailsSelect.addSelectColumn(Column.getColumn("GraphDetails", "*"));
        final Criteria graphNameCriteria = new Criteria(Column.getColumn("GraphDetails", "GRAPH_NAME"), (Object)graphName, 0);
        graphDetailsSelect.setCriteria(graphNameCriteria);
        return graphDetailsSelect;
    }
    
    static {
        GraphService.logger = Logger.getLogger(GraphService.class.getName());
    }
}
