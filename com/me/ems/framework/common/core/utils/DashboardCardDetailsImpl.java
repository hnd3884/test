package com.me.ems.framework.common.core.utils;

import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.framework.common.api.utils.DashboardUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;

public class DashboardCardDetailsImpl implements DashboardCardDetailsAPI
{
    @Override
    public HashMap getGraphTypeCardDetails(final Long cardId, final HashMap additionalProps) throws Exception {
        final HashMap graphData = new HashMap();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CardGenerationInfo"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("CardGenerationInfo", "CARD_ID"), (Object)cardId, 0));
        selectQuery.addSelectColumn(Column.getColumn("CardGenerationInfo", "*"));
        final String cardName = DashboardUtil.getInstance().getCardName(cardId);
        final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row graphRow = dataObject.getFirstRow("CardGenerationInfo");
            graphData.put("GRAPH_NAME", cardName);
            graphData.put("GEN_CLASS", additionalProps.get("GEN_CLASS"));
            graphData.put("XML_FILE", (additionalProps.get("XML_FILE") != null) ? additionalProps.get("XML_FILE") : "");
            graphData.put("CHART_IMPL", (additionalProps.get("CHART_IMPL") != null) ? additionalProps.get("CHART_IMPL") : "");
            graphData.put("HIDE_TOGGLE", additionalProps.get("HIDE_TOGGLE"));
            graphData.put("DEFAULT_CHART_TYPE", additionalProps.get("DEFAULT_CHART_TYPE"));
        }
        return graphData;
    }
    
    @Override
    public String getCCTypeCardDetails(final Long cardId, final HashMap additionalProps) throws Exception {
        return DashboardUtil.getInstance().getCardName(cardId).concat(".cc");
    }
    
    @Override
    public Properties getHTMLTypeCardDetails(final Long cardId, final HashMap additionalProps) throws Exception {
        final Properties htmlDataProps = new Properties();
        return htmlDataProps;
    }
}
