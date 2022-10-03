package com.me.ems.framework.common.api.v1.service;

import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import com.me.ems.framework.common.api.v1.model.helpermodel.FilterValueBean;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.me.ems.framework.common.api.v1.model.helpermodel.FilterCardBean;
import com.me.ems.framework.common.core.handler.DashboardHandler;
import com.adventnet.i18n.I18N;
import com.me.ems.framework.common.api.v1.model.helpermodel.DashCardBean;
import com.me.ems.framework.common.api.constants.DashboardConstants;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.ems.framework.common.api.utils.DashboardUtil;
import com.me.ems.framework.common.api.v1.model.DashCardAPIBean;
import java.util.Properties;
import java.util.logging.Logger;

public class DashCardService
{
    private static String sourceClass;
    private static Logger logger;
    
    public static DashCardService getInstance() {
        return new DashCardService();
    }
    
    public DashCardAPIBean formCardBean(final Properties cardProps) throws Exception {
        String dashBoardName = "";
        String viewName = "";
        if (cardProps.containsKey("viewName")) {
            viewName = ((Hashtable<K, String>)cardProps).get("viewName");
        }
        if (cardProps.containsKey("dashboardName")) {
            dashBoardName = ((Hashtable<K, String>)cardProps).get("dashboardName");
        }
        final Long dashboardId = DashboardUtil.getInstance().getDashBoardId(dashBoardName);
        final Long cardId = DashboardUtil.getInstance().getCardId(viewName);
        final DataObject cardDO = DashboardUtil.getInstance().getCardDO(dashboardId, cardId);
        final Row cardRow = cardDO.getRow("CardInfo");
        final Row dashRow = cardDO.getRow("DashboardCardInfoRel");
        final Row cardAttr = cardDO.getRow("CardDisplayAttributesInfo");
        ((Hashtable<String, Boolean>)cardProps).put("fullDashboard", false);
        if (cardProps.containsKey("parentViewName")) {
            final String parentViewName = ((Hashtable<K, String>)cardProps).get("parentViewName");
            final Long parentCardId = DashboardUtil.getInstance().getCardId(parentViewName);
            ((Hashtable<String, Long>)cardProps).put("parentCardId", parentCardId);
        }
        if (cardDO.containsTable("CardDisplayOrder")) {
            final Row customRow = cardDO.getRow("CardDisplayOrder");
            ((Hashtable<String, Boolean>)cardProps).put("isCustomOrder", true);
            ((Hashtable<String, Row>)cardProps).put("customRow", customRow);
        }
        final DashCardAPIBean resultBean = (DashCardAPIBean)this.formCardBean(cardRow, cardAttr, dashRow, cardProps);
        return resultBean;
    }
    
    public Object formCardBean(final Row cardRow, final Row cardAttr, final Row dashCardRelRow, final Properties additionalProps) throws Exception {
        final Properties cardProps = new Properties();
        final Boolean fullDashboard = ((Hashtable<K, Boolean>)additionalProps).get("fullDashboard");
        Boolean isCustomOrder = false;
        Boolean showFilter = false;
        Boolean isCardFilterApplied = false;
        Boolean isResourceDashboard = false;
        Long resourceId = -1L;
        String dashboardName = "";
        String selectedViewName = null;
        Boolean renderCardData = true;
        Long parentCardId = -1L;
        if (additionalProps.get("isCustomOrder") != null) {
            isCustomOrder = ((Hashtable<K, Boolean>)additionalProps).get("isCustomOrder");
        }
        if (additionalProps.get("showFilter") != null) {
            final Integer filter = ((Hashtable<K, Integer>)additionalProps).get("showFilter");
            if (filter.equals(DashboardConstants.SHOW_FILTER)) {
                showFilter = true;
            }
        }
        if (additionalProps.get("isCardFilterApplied") != null) {
            isCardFilterApplied = ((Hashtable<K, Boolean>)additionalProps).get("isCardFilterApplied");
        }
        if (additionalProps.get("isResourceDashboard") != null) {
            isResourceDashboard = ((Hashtable<K, Boolean>)additionalProps).get("isResourceDashboard");
            resourceId = ((Hashtable<K, Long>)additionalProps).get("resourceId");
        }
        if (additionalProps.get("dashboardName") != null) {
            dashboardName = ((Hashtable<K, String>)additionalProps).get("dashboardName");
        }
        if (additionalProps.get("selected") != null) {
            selectedViewName = ((Hashtable<K, String>)additionalProps).get("selected");
        }
        if (additionalProps.get("renderCardData") != null) {
            renderCardData = ((Hashtable<K, Boolean>)additionalProps).get("renderCardData");
        }
        DashCardBean dashCardBean;
        if (fullDashboard) {
            dashCardBean = new DashCardBean();
        }
        else {
            dashCardBean = new DashCardAPIBean();
        }
        if (additionalProps.get("parentCardId") != null) {
            parentCardId = ((Hashtable<K, Long>)additionalProps).get("parentCardId");
        }
        final Long cardId = (Long)cardRow.get("CARD_ID");
        Long dashboardId = -1L;
        String displayKey = (String)cardRow.get("DISPLAY_KEY");
        displayKey = I18N.getMsg(displayKey, new Object[0]);
        final String cardType = (String)cardRow.get("CARD_TYPE");
        final String viewName = (String)cardRow.get("CARD_NAME");
        final Integer minHeight = (Integer)cardAttr.get("MINHEIGHT");
        final Integer minWidth = (Integer)cardAttr.get("MINWIDTH");
        final Integer maxHeight = (Integer)cardAttr.get("MAXHEIGHT");
        final Integer maxWidth = (Integer)cardAttr.get("MAXWIDTH");
        final Boolean moveBool = !(boolean)cardAttr.get("CANMOVE");
        final Boolean resizeBool = !(boolean)cardAttr.get("CANRESIZE");
        final Boolean lockBool = (Boolean)cardAttr.get("LOCK");
        Long displayOrder = 0L;
        Integer xPos = 0;
        Integer yPos = 0;
        Integer height = 6;
        Integer width = 6;
        if (isCustomOrder) {
            final Row customRow = ((Hashtable<K, Row>)additionalProps).get("customRow");
            displayOrder = (Long)customRow.get("DISPLAY_ORDER");
            xPos = (Integer)customRow.get("XPOS");
            yPos = (Integer)customRow.get("YPOS");
            height = (Integer)customRow.get("HEIGHT");
            width = (Integer)customRow.get("WIDTH");
            dashboardId = (Long)dashCardRelRow.get("DASHBOARD_ID");
        }
        else if (dashCardRelRow != null) {
            dashboardId = (Long)dashCardRelRow.get("DASHBOARD_ID");
            displayOrder = (Long)dashCardRelRow.get("DISPLAY_ORDER");
            xPos = (Integer)dashCardRelRow.get("XPOS");
            yPos = (Integer)dashCardRelRow.get("YPOS");
            height = (Integer)dashCardRelRow.get("HEIGHT");
            width = (Integer)dashCardRelRow.get("WIDTH");
        }
        final Properties filterProps = new Properties();
        ((Hashtable<String, Boolean>)filterProps).put("showFilter", showFilter);
        ((Hashtable<String, Boolean>)filterProps).put("isCardFilterApplied", isCardFilterApplied);
        if (isCardFilterApplied) {
            ((Hashtable<String, Object>)filterProps).put("filterId", ((Hashtable<K, Object>)additionalProps).get("filterId"));
            ((Hashtable<String, Object>)filterProps).put("filterValueId", ((Hashtable<K, Object>)additionalProps).get("filterValueId"));
        }
        ((Hashtable<String, Boolean>)filterProps).put("fullDashboard", fullDashboard);
        ((Hashtable<String, Long>)filterProps).put("cardId", cardId);
        ((Hashtable<String, Long>)filterProps).put("dashboardId", dashboardId);
        if (parentCardId != -1L) {
            ((Hashtable<String, Long>)filterProps).put("parentCardId", parentCardId);
        }
        final FilterCardBean filterData = this.getFilterDataForCard(filterProps);
        if (showFilter) {
            ((Hashtable<String, FilterCardBean>)cardProps).put("filterData", filterData);
            ((Hashtable<String, Long>)cardProps).put("filterId", filterData.getFilterID());
            ((Hashtable<String, Long>)cardProps).put("valueId", filterData.getSelectedValueId());
            ((Hashtable<String, String>)cardProps).put("filterValue", filterData.getFilterValue());
        }
        ((Hashtable<String, String>)cardProps).put("viewName", viewName);
        ((Hashtable<String, Long>)cardProps).put("cardId", cardId);
        ((Hashtable<String, Boolean>)cardProps).put("isResourceDashboard", isResourceDashboard);
        if (isResourceDashboard) {
            ((Hashtable<String, Long>)cardProps).put("resourceId", resourceId);
        }
        ((Hashtable<String, Boolean>)cardProps).put("isCardFilterApplied", isCardFilterApplied);
        ((Hashtable<String, Long>)cardProps).put("dashboardId", dashboardId);
        ((Hashtable<String, Boolean>)cardProps).put("showFilter", showFilter);
        final JSONObject metaData = DashboardHandler.getInstance().getChartMetaData(cardProps);
        if (selectedViewName != null) {
            ((Hashtable<String, String>)cardProps).put("selectedViewName", selectedViewName);
        }
        if (renderCardData) {
            if (cardType.equals("GRAPH")) {
                if (additionalProps.get("decodeChartData") != null) {
                    ((Hashtable<String, Object>)cardProps).put("decodeChartData", ((Hashtable<K, Object>)additionalProps).get("decodeChartData"));
                }
                final JSONObject graphJSON = DashboardHandler.getInstance().getModuleGraphJSON(cardProps);
                dashCardBean.setCardData(graphJSON);
            }
            else if (cardType.equals("HTML_COUNTS") || cardType.equals("TOP_COUNTS")) {
                final JSONObject countJSON = DashboardHandler.getInstance().getModuleHTMLCountJSON(cardProps);
                dashCardBean.setCardData(countJSON);
            }
            else if (cardType.equals("HTML_FEED")) {
                final JSONObject htmlFeedJSON = DashboardHandler.getInstance().getModuleHTMLFeedJSON(cardProps);
                dashCardBean.setCardData(htmlFeedJSON);
            }
            else if (cardType.equals("HTML_TABLE") || cardType.equals("CC")) {
                final String cardDataStr = DashboardHandler.getInstance().getModuleHTMLTableJSON(cardProps);
                dashCardBean.setCardData(cardDataStr);
            }
            else if (cardType.equalsIgnoreCase("MULTI_CARD") || cardType.equals("MULTI_SINGLE_CARD")) {
                final JSONArray cardData = DashboardHandler.getInstance().getModuleMultiGraphJSON(cardProps, additionalProps);
                dashCardBean.setCardData(cardData);
            }
            else if (cardType.equalsIgnoreCase("Cc")) {
                dashCardBean.setCardData(DashboardUtil.getInstance().getCCTypeCardDetails(cardId));
            }
            else if (cardType.equalsIgnoreCase("HTML")) {
                dashCardBean.setCardData(DashboardUtil.getInstance().getModuleHTMLData(cardId));
            }
        }
        else {
            dashCardBean.setCardData(viewName);
        }
        dashCardBean.setCardId(cardId);
        dashCardBean.setCardType(cardType);
        dashCardBean.setCardTitle(displayKey);
        dashCardBean.setDisplayOrder(Integer.parseInt(displayOrder.toString()));
        dashCardBean.setDashboardId(dashboardName);
        dashCardBean.setMinHeight(minHeight);
        dashCardBean.setMinWidth(minWidth);
        dashCardBean.setMaxHeight(maxHeight);
        dashCardBean.setMaxWidth(maxWidth);
        dashCardBean.setMeta(metaData);
        dashCardBean.setLock(lockBool);
        dashCardBean.setNoMove(moveBool);
        dashCardBean.setNoResize(resizeBool);
        dashCardBean.setxPos(xPos);
        dashCardBean.setyPos(yPos);
        dashCardBean.setHeight(height);
        dashCardBean.setWidth(width);
        dashCardBean.setViewName(viewName);
        return dashCardBean;
    }
    
    public FilterCardBean getFilterDataForCard(final Properties filterProps) throws Exception {
        final FilterCardBean filterData = new FilterCardBean();
        final Boolean fullDashboard = ((Hashtable<K, Boolean>)filterProps).get("fullDashboard");
        final Boolean showFilter = ((Hashtable<K, Boolean>)filterProps).get("showFilter");
        Boolean isCardFilterApplied = ((Hashtable<K, Boolean>)filterProps).get("isCardFilterApplied");
        Long filterCardId;
        final Long cardId = filterCardId = ((Hashtable<K, Long>)filterProps).get("cardId");
        final Long dashboardId = ((Hashtable<K, Long>)filterProps).get("dashboardId");
        Long filterID = -1L;
        Long filterValueID = -1L;
        String filterValue = "";
        Long parentCardId = -1L;
        if (filterProps.containsKey("parentCardId")) {
            parentCardId = (filterCardId = ((Hashtable<K, Long>)filterProps).get("parentCardId"));
        }
        if (showFilter) {
            final DataObject cardFilterDO = DashboardUtil.getInstance().getCardFilterDO(filterCardId);
            final Row filterRow = cardFilterDO.getFirstRow("CardFilters");
            filterID = (Long)filterRow.get("FILTER_ID");
            String filterDispKey = (String)filterRow.get("FILTER_DISPLAY_KEY");
            filterDispKey = I18N.getMsg(filterDispKey, new Object[0]);
            final Criteria filterCrit = new Criteria(Column.getColumn("CardFilterValues", "FILTER_ID"), (Object)filterID, 0);
            final Iterator valueRows = cardFilterDO.getRows("CardFilterValues", filterCrit);
            final ArrayList<FilterValueBean> valueArray = new ArrayList<FilterValueBean>();
            while (valueRows.hasNext()) {
                final Row valueRow = valueRows.next();
                final FilterValueBean valueObj = new FilterValueBean();
                final Long valueID = (Long)valueRow.get("VALUE_ID");
                String i18NKey = (String)valueRow.get("VALUE_DISPLAY_KEY");
                final String valueName = (String)valueRow.get("VALUE_NAME");
                i18NKey = I18N.getMsg(i18NKey, new Object[0]);
                valueObj.setFilterValueID(valueID);
                valueObj.setValueDisplayKey(i18NKey);
                valueObj.setValueName(valueName);
                valueArray.add(valueObj);
            }
            filterData.setSelectedValueId(valueArray.get(0).getFilterValueID());
            filterData.setFilterID(filterID);
            filterData.setFilterDisplayKey(filterDispKey);
            filterData.setFilterValues(valueArray);
            if (fullDashboard || (!fullDashboard && !isCardFilterApplied)) {
                final HashMap<String, Long> userFilterMap = DashboardUtil.getInstance().getUserFilterValues(dashboardId, cardId);
                if (!userFilterMap.isEmpty()) {
                    isCardFilterApplied = true;
                    filterID = userFilterMap.get("filterId");
                    final Long selectedValueId = userFilterMap.get("valueId");
                    filterData.setSelectedValueId(selectedValueId);
                    filterValueID = selectedValueId;
                    filterValue = DashboardUtil.getInstance().getValueName(filterID, filterValueID);
                }
                else {
                    isCardFilterApplied = false;
                }
            }
            else {
                isCardFilterApplied = true;
                filterID = ((Hashtable<K, Long>)filterProps).get("filterId");
                filterValueID = ((Hashtable<K, Long>)filterProps).get("filterValueId");
                filterValue = DashboardUtil.getInstance().getValueName(filterID, filterValueID);
                filterData.setSelectedValueId(filterValueID);
            }
            filterData.setFilterValue(filterValue);
        }
        return filterData;
    }
    
    static {
        DashCardService.sourceClass = DashCardService.class.getName();
        DashCardService.logger = Logger.getLogger(DashCardService.class.getName());
    }
}
