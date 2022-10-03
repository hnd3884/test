package com.me.ems.framework.common.api.utils;

import java.util.Hashtable;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.FileNotFoundException;
import java.io.File;
import org.json.JSONObject;
import org.json.JSONArray;
import com.me.ems.framework.common.core.utils.DashboardCardDetailsAPI;
import com.adventnet.persistence.DataAccess;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.List;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.SortColumn;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.persistence.Row;
import java.util.Properties;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DashboardUtil
{
    private static DashboardUtil dashboardUtil;
    private static String sourceClass;
    private static Logger logger;
    
    private DashboardUtil() {
    }
    
    public static DashboardUtil getInstance() {
        if (DashboardUtil.dashboardUtil == null) {
            DashboardUtil.dashboardUtil = new DashboardUtil();
        }
        return DashboardUtil.dashboardUtil;
    }
    
    public DataObject getCardFilterUserDO(final Long custId, final Long techId, final Long dashboardId, final Long cardId) throws Exception {
        final String sourceMethod = "getCardFilterUserDO";
        DataObject dataObject = SyMUtil.getPersistence().constructDataObject();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CardFilterUser"));
            query.addJoin(new Join("CardFilterUser", "DashboardCardInfoRel", new String[] { "CARD_DASHBOARD_RELATION_ID" }, new String[] { "CARD_DASHBOARD_RELATION_ID" }, 2));
            Criteria criteria = new Criteria(Column.getColumn("CardFilterUser", "CUSTOMER_ID"), (Object)custId, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("CardFilterUser", "LOGIN_ID"), (Object)techId, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("DashboardCardInfoRel", "DASHBOARD_ID"), (Object)dashboardId, 0));
            criteria = criteria.and(new Criteria(Column.getColumn("DashboardCardInfoRel", "CARD_ID"), (Object)cardId, 0));
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("CardFilterUser", "*"));
            dataObject = SyMUtil.getPersistence().get(query);
        }
        catch (final Exception ex) {
            SyMLogger.error(DashboardUtil.logger, DashboardUtil.sourceClass, sourceMethod, "Exception while fetching the card filter DO for dashboardId: " + dashboardId + " and cardId:" + cardId, ex);
        }
        return dataObject;
    }
    
    public void updateCardFilterUser(final Long custId, final Long techId, final Properties cardProps) throws Exception {
        final String sourceMethod = "updateCardFilterUser";
        String dashBoardName = "";
        String viewName = "";
        try {
            if (cardProps.containsKey("viewName")) {
                viewName = ((Hashtable<K, String>)cardProps).get("viewName");
            }
            if (cardProps.containsKey("dashboardName")) {
                dashBoardName = ((Hashtable<K, String>)cardProps).get("dashboardName");
            }
            final Long dashboardId = this.getDashBoardId(dashBoardName);
            final Long cardId = this.getCardId(viewName);
            final Long cardDashboardRelationID = this.getDashboardCardRelId(dashboardId, cardId);
            final Long filterId = ((Hashtable<K, Long>)cardProps).get("filterId");
            final Long valueId = ((Hashtable<K, Long>)cardProps).get("filterValueId");
            final DataObject dataObject = this.getCardFilterUserDO(custId, techId, dashboardId, cardId);
            if (!dataObject.isEmpty()) {
                final Row filterRow = dataObject.getFirstRow("CardFilterUser");
                filterRow.set("FILTER_ID", (Object)filterId);
                filterRow.set("VALUE_ID", (Object)valueId);
                dataObject.updateRow(filterRow);
            }
            else {
                final Row filterRow = new Row("CardFilterUser");
                filterRow.set("CUSTOMER_ID", (Object)custId);
                filterRow.set("LOGIN_ID", (Object)techId);
                filterRow.set("CARD_DASHBOARD_RELATION_ID", (Object)cardDashboardRelationID);
                filterRow.set("FILTER_ID", (Object)filterId);
                filterRow.set("VALUE_ID", (Object)valueId);
                dataObject.addRow(filterRow);
            }
            SyMUtil.getPersistence().update(dataObject);
        }
        catch (final Exception ex) {
            SyMLogger.error(DashboardUtil.logger, DashboardUtil.sourceClass, sourceMethod, "Exception while updating the card filter user DO for dashboardId: " + dashBoardName + " and cardId:" + viewName, ex);
            throw ex;
        }
    }
    
    public Long getDashBoardId(final String dashBoardName) throws Exception {
        Long dashBoardId = -1L;
        final Criteria dashCriteria = new Criteria(Column.getColumn("Dashboards", "DASHBOARD_NAME"), (Object)dashBoardName, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("Dashboards", dashCriteria);
        if (!dataObject.isEmpty()) {
            final Row dashRow = dataObject.getFirstRow("Dashboards");
            dashBoardId = (Long)dashRow.get("DASHBOARD_ID");
        }
        return dashBoardId;
    }
    
    public String getCardName(final Long cardId) throws Exception {
        String cardName = "";
        final Criteria criteria = new Criteria(Column.getColumn("CardInfo", "CARD_ID"), (Object)cardId, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("CardInfo", criteria);
        if (!dataObject.isEmpty()) {
            final Row cardRow = dataObject.getFirstRow("CardInfo");
            cardName = (String)cardRow.get("CARD_NAME");
        }
        return cardName;
    }
    
    public Long getCardId(final String viewName) throws Exception {
        Long cardId = -1L;
        final Criteria criteria = new Criteria(Column.getColumn("CardInfo", "CARD_NAME"), (Object)viewName, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("CardInfo", criteria);
        if (!dataObject.isEmpty()) {
            final Row cardRow = dataObject.getFirstRow("CardInfo");
            cardId = (Long)cardRow.get("CARD_ID");
        }
        return cardId;
    }
    
    public Long getDashboardCardRelId(final Long dashBoardID, final Long cardID) throws Exception {
        Long cardDashboardID = -1L;
        final Criteria criteria = new Criteria(Column.getColumn("DashboardCardInfoRel", "CARD_ID"), (Object)cardID, 0);
        final Criteria criteria2 = new Criteria(Column.getColumn("DashboardCardInfoRel", "DASHBOARD_ID"), (Object)dashBoardID, 0);
        final DataObject dataObject = SyMUtil.getPersistence().get("DashboardCardInfoRel", criteria.and(criteria2));
        if (!dataObject.isEmpty()) {
            final Row cardRow = dataObject.getFirstRow("DashboardCardInfoRel");
            cardDashboardID = (Long)cardRow.get("CARD_DASHBOARD_RELATION_ID");
        }
        return cardDashboardID;
    }
    
    public DataObject getCardDO(final Long dashboardId, final Long cardId) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CardInfo"));
        Criteria criteria = new Criteria(Column.getColumn("CardInfo", "CARD_ID"), (Object)cardId, 0);
        if (dashboardId != null && !dashboardId.equals(-1L)) {
            final Join dashJoin = new Join("CardInfo", "DashboardCardInfoRel", new String[] { "CARD_ID" }, new String[] { "CARD_ID" }, 2);
            final Join cardAttrJoin = new Join("CardInfo", "CardDisplayAttributesInfo", new String[] { "CARD_ID" }, new String[] { "CARD_ID" }, 2);
            final Criteria dashCrit = new Criteria(Column.getColumn("DashboardCardInfoRel", "DASHBOARD_ID"), (Object)dashboardId, 0);
            query.addJoin(dashJoin);
            query.addJoin(cardAttrJoin);
            criteria = criteria.and(dashCrit);
            final Long custId = CustomerInfoUtil.getInstance().getCustomerId();
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final SelectQuery custOrderQuery = this.getDashBoardCustomOrder(dashboardId, custId, loginId);
            final DataObject custQueryDataObject = SyMUtil.getPersistence().get(custOrderQuery);
            if (!custQueryDataObject.isEmpty()) {
                query.addJoin(new Join("DashboardCardInfoRel", "CardDisplayOrder", new String[] { "CARD_DASHBOARD_RELATION_ID" }, new String[] { "CARD_DASHBOARD_RELATION_ID" }, 2));
                query.addSelectColumn(Column.getColumn("CardDisplayOrder", "*"));
                query.addSortColumn(new SortColumn(new Column("CardDisplayOrder", "DISPLAY_ORDER"), true));
                criteria = criteria.and(custOrderQuery.getCriteria());
            }
            query.addSelectColumn(Column.getColumn("DashboardCardInfoRel", "*"));
        }
        query.setCriteria(criteria);
        query.addSelectColumn(Column.getColumn("CardInfo", "*"));
        query.addSelectColumn(Column.getColumn("CardDisplayAttributesInfo", "*"));
        final DataObject dataObject = SyMUtil.getPersistence().get(query);
        return dataObject;
    }
    
    private SelectQuery getDashBoardCustomOrder(final Long dashBoardID, final Long custID, final Long loginID) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CardDisplayOrder"));
        selectQuery.addJoin(new Join("CardDisplayOrder", "DashboardCardInfoRel", new String[] { "CARD_DASHBOARD_RELATION_ID" }, new String[] { "CARD_DASHBOARD_RELATION_ID" }, 2));
        Criteria customOrderCrit = new Criteria(Column.getColumn("DashboardCardInfoRel", "DASHBOARD_ID"), (Object)dashBoardID, 0);
        customOrderCrit = customOrderCrit.and(new Criteria(Column.getColumn("CardDisplayOrder", "CUSTOMER_ID"), (Object)custID, 0));
        customOrderCrit = customOrderCrit.and(new Criteria(Column.getColumn("CardDisplayOrder", "LOGIN_ID"), (Object)loginID, 0));
        selectQuery.setCriteria(customOrderCrit);
        selectQuery.addSelectColumn(Column.getColumn("CardDisplayOrder", "*"));
        return selectQuery;
    }
    
    public DataObject getDashBoardDO(final Long dashboardID) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Dashboards"));
        Criteria dashCrit = new Criteria(Column.getColumn("Dashboards", "DASHBOARD_ID"), (Object)dashboardID, 0);
        selectQuery.addJoin(new Join("Dashboards", "DashboardCardInfoRel", new String[] { "DASHBOARD_ID" }, new String[] { "DASHBOARD_ID" }, 2));
        selectQuery.addJoin(new Join("DashboardCardInfoRel", "CardInfo", new String[] { "CARD_ID" }, new String[] { "CARD_ID" }, 2));
        selectQuery.addJoin(new Join("DashboardCardInfoRel", "CardDisplayAttributesInfo", new String[] { "CARD_ID" }, new String[] { "CARD_ID" }, 2));
        selectQuery.addJoin(new Join("CardInfo", "CardInfoRoleMapping", new String[] { "CARD_ID" }, new String[] { "CARD_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("CardInfo", "*"));
        selectQuery.addSelectColumn(Column.getColumn("DashboardCardInfoRel", "*"));
        selectQuery.addSelectColumn(Column.getColumn("CardInfo", "*"));
        selectQuery.addSelectColumn(Column.getColumn("CardDisplayAttributesInfo", "*"));
        final Long custId = CustomerInfoUtil.getInstance().getCustomerId();
        final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        final SelectQuery custOrderQuery = this.getDashBoardCustomOrder(dashboardID, custId, loginId);
        final DataObject custQueryDataObject = SyMUtil.getPersistence().get(custOrderQuery);
        if (!custQueryDataObject.isEmpty()) {
            selectQuery.addJoin(new Join("DashboardCardInfoRel", "CardDisplayOrder", new String[] { "CARD_DASHBOARD_RELATION_ID" }, new String[] { "CARD_DASHBOARD_RELATION_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("CardDisplayOrder", "*"));
            selectQuery.addSortColumn(new SortColumn(new Column("CardDisplayOrder", "DISPLAY_ORDER"), true));
            dashCrit = dashCrit.and(custOrderQuery.getCriteria());
        }
        else {
            selectQuery.addSortColumn(new SortColumn(new Column("DashboardCardInfoRel", "DISPLAY_ORDER"), true));
        }
        final List<String> roles = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
        final List<Long> roleIdsList = DMUserHandler.getRoleIdsFromRoleName(roles);
        if (roleIdsList.size() > 0) {
            dashCrit = dashCrit.and(new Criteria(Column.getColumn("CardInfoRoleMapping", "ROLE_ID"), (Object)roleIdsList.toArray(), 8));
        }
        final String productType = LicenseProvider.getInstance().getProductType();
        String productTypeStr = "E";
        if (productType.equals("Enterprise")) {
            productTypeStr = "E";
        }
        else if (productType.equals("Professional")) {
            productTypeStr = "P";
        }
        else if (productType.equals("Patch")) {
            productTypeStr = "Patch";
        }
        else if (productType.equals("ToolsAddOn")) {
            productTypeStr = "T";
        }
        else if (productType.equals("Standard")) {
            productTypeStr = "S";
        }
        else if (productType.equals("UEM")) {
            productTypeStr = "UEM";
        }
        dashCrit = dashCrit.and(new Criteria(Column.getColumn("CardInfo", "LICENSE_TYPE"), (Object)productTypeStr, 12).or(new Criteria(Column.getColumn("CardInfo", "LICENSE_TYPE"), (Object)"all", 0)));
        selectQuery.setCriteria(dashCrit);
        return SyMUtil.getPersistence().get(selectQuery);
    }
    
    public HashMap getGraphDetails(final Long cardId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CardGenerationInfo"));
        selectQuery.addSelectColumn(Column.getColumn("CardGenerationInfo", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("CardGenerationInfo", "CARD_ID"), (Object)cardId, 0));
        final DataObject dataObject = DataAccess.get(selectQuery);
        String generatorClass = "";
        String xmlPath = "";
        HashMap map = null;
        if (dataObject != null) {
            final Row row = dataObject.getFirstRow("CardGenerationInfo");
            generatorClass = (String)row.get("DATA_GENERATOR_CLASS");
            if (generatorClass == null || generatorClass.isEmpty()) {
                generatorClass = "com.me.ems.framework.common.core.utils.DashboardCardDetailsImpl";
            }
            xmlPath = (String)row.get("XML_FILE_PATH");
            map = this.getCardProperties(xmlPath);
        }
        return ((DashboardCardDetailsAPI)Class.forName(generatorClass).newInstance()).getGraphTypeCardDetails(cardId, map);
    }
    
    public String getCCTypeCardDetails(final Long cardId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CardGenerationInfo"));
        selectQuery.addSelectColumn(Column.getColumn("CardGenerationInfo", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("CardGenerationInfo", "CARD_ID"), (Object)cardId, 0));
        final DataObject dataObject = DataAccess.get(selectQuery);
        String generatorClass = "";
        String xmlPath = "";
        HashMap map = null;
        if (dataObject != null) {
            final Row row = dataObject.getFirstRow("CardGenerationInfo");
            generatorClass = (String)row.get("DATA_GENERATOR_CLASS");
            if (generatorClass == null || generatorClass.isEmpty()) {
                generatorClass = "com.me.ems.framework.common.core.utils.DashboardCardDetailsImpl";
            }
            xmlPath = (String)row.get("XML_FILE_PATH");
            map = this.getCardProperties(xmlPath);
        }
        return ((DashboardCardDetailsAPI)Class.forName(generatorClass).newInstance()).getCCTypeCardDetails(cardId, map);
    }
    
    public Properties getModuleHTMLData(final Long cardId) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CardGenerationInfo"));
        selectQuery.addSelectColumn(Column.getColumn("CardGenerationInfo", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("CardGenerationInfo", "CARD_ID"), (Object)cardId, 0));
        final DataObject dataObject = DataAccess.get(selectQuery);
        String generatorClass = "";
        String xmlPath = "";
        HashMap map = null;
        if (dataObject != null) {
            final Row row = dataObject.getFirstRow("CardGenerationInfo");
            generatorClass = (String)row.get("DATA_GENERATOR_CLASS");
            if (generatorClass == null || generatorClass.isEmpty()) {
                generatorClass = "com.me.ems.framework.common.core.utils.DashboardCardDetailsImpl";
            }
            xmlPath = (String)row.get("XML_FILE_PATH");
            map = this.getCardProperties(xmlPath);
        }
        return ((DashboardCardDetailsAPI)Class.forName(generatorClass).newInstance()).getHTMLTypeCardDetails(cardId, map);
    }
    
    public void updateDashboardDO(final Long dashboardId, final JSONArray dashCardJSONArr) throws Exception {
        final Long custId = CustomerInfoUtil.getInstance().getCustomerId();
        final Long techId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        Criteria criteria = new Criteria(Column.getColumn("CardDisplayOrder", "CUSTOMER_ID"), (Object)custId, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("CardDisplayOrder", "LOGIN_ID"), (Object)techId, 0));
        Criteria filterCriteria = new Criteria(Column.getColumn("CardFilterUser", "CUSTOMER_ID"), (Object)custId, 0);
        filterCriteria = filterCriteria.and(new Criteria(Column.getColumn("CardFilterUser", "LOGIN_ID"), (Object)techId, 0));
        final DataObject cardDispDO = DataAccess.get("CardDisplayOrder", criteria);
        final DataObject filterUserDO = SyMUtil.getPersistence().constructDataObject();
        DataAccess.delete("CardFilterUser", filterCriteria);
        for (int i = 0; i < dashCardJSONArr.length(); ++i) {
            final JSONObject resultObj = dashCardJSONArr.getJSONObject(i);
            final Long cardId = (Long)resultObj.get("cardId");
            final Integer xPos = (Integer)resultObj.get("xPos");
            final Integer yPos = (Integer)resultObj.get("yPos");
            final Integer height = (Integer)resultObj.get("height");
            final Integer width = (Integer)resultObj.get("width");
            final Boolean showFilter = (Boolean)resultObj.get("showFilter");
            if (showFilter) {
                final Long filterId = (Long)resultObj.get("filterId");
                final Long valueId = (Long)resultObj.get("valueId");
                final Row filterRow = new Row("CardFilterUser");
                filterRow.set("CUSTOMER_ID", (Object)custId);
                filterRow.set("LOGIN_ID", (Object)techId);
                filterRow.set("CARD_DASHBOARD_RELATION_ID", (Object)this.getDashboardCardRelId(dashboardId, cardId));
                filterRow.set("FILTER_ID", (Object)filterId);
                filterRow.set("VALUE_ID", (Object)valueId);
                filterUserDO.addRow(filterRow);
            }
            final Row row = new Row("CardDisplayOrder");
            row.set("CUSTOMER_ID", (Object)custId);
            row.set("LOGIN_ID", (Object)techId);
            row.set("CARD_DASHBOARD_RELATION_ID", (Object)this.getDashboardCardRelId(dashboardId, cardId));
            Boolean isUpdateRow = false;
            if (cardDispDO.findRow(row) != null) {
                isUpdateRow = true;
            }
            row.set("DISPLAY_ORDER", (Object)i);
            row.set("XPOS", (Object)xPos);
            row.set("YPOS", (Object)yPos);
            row.set("HEIGHT", (Object)height);
            row.set("WIDTH", (Object)width);
            if (isUpdateRow) {
                cardDispDO.updateRow(row);
            }
            else {
                cardDispDO.addRow(row);
            }
        }
        SyMUtil.getPersistence().update(cardDispDO);
        SyMUtil.getPersistence().update(filterUserDO);
    }
    
    public DataObject getCardFilterDO(final Long cardID) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CardFilterRel"));
        final Criteria criteria = new Criteria(Column.getColumn("CardFilterRel", "CARD_ID"), (Object)cardID, 0);
        final Join cardFilterJoin = new Join("CardFilterRel", "CardFilters", new String[] { "FILTER_ID" }, new String[] { "FILTER_ID" }, 2);
        final Join filterValuesJoin = new Join("CardFilterRel", "CardFilterValues", new String[] { "FILTER_ID" }, new String[] { "FILTER_ID" }, 2);
        final SortColumn filterCol = new SortColumn(Column.getColumn("CardFilterValues", "FILTER_ID"), false);
        query.addSortColumn(filterCol);
        query.addSelectColumn(Column.getColumn("CardFilterRel", "*"));
        query.addSelectColumn(Column.getColumn("CardFilters", "*"));
        query.addSelectColumn(Column.getColumn("CardFilterValues", "*"));
        query.addJoin(cardFilterJoin);
        query.addJoin(filterValuesJoin);
        query.setCriteria(criteria);
        return SyMUtil.getPersistence().get(query);
    }
    
    public HashMap<String, Long> getUserFilterValues(final Long dashboardId, final Long cardId) throws Exception {
        final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
        final Long custId = CustomerInfoUtil.getInstance().getCustomerId();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CardFilterUser"));
        selectQuery.addSelectColumn(Column.getColumn("CardFilterUser", "*"));
        selectQuery.addJoin(new Join("CardFilterUser", "DashboardCardInfoRel", new String[] { "CARD_DASHBOARD_RELATION_ID" }, new String[] { "CARD_DASHBOARD_RELATION_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("CardFilterUser", "LOGIN_ID"), (Object)loginId, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("CardFilterUser", "CUSTOMER_ID"), (Object)custId, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("DashboardCardInfoRel", "DASHBOARD_ID"), (Object)dashboardId, 0));
        criteria = criteria.and(new Criteria(Column.getColumn("DashboardCardInfoRel", "CARD_ID"), (Object)cardId, 0));
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = DataAccess.get(selectQuery);
        final HashMap<String, Long> filterMap = new HashMap<String, Long>();
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("CardFilterUser");
            final Long filterId = (Long)row.get("FILTER_ID");
            final Long valueId = (Long)row.get("VALUE_ID");
            filterMap.put("filterId", filterId);
            filterMap.put("valueId", valueId);
        }
        return filterMap;
    }
    
    public String getFilterName(final Long filterID) throws Exception {
        final Criteria filterCrit = new Criteria(Column.getColumn("CardFilters", "FILTER_ID"), (Object)filterID, 0);
        final String filterName = (String)DataAccess.get("CardFilters", filterCrit).getFirstRow("CardFilters").get("FILTER_NAME");
        return filterName;
    }
    
    public String getValueName(final Long filterID, final Long valueID) throws Exception {
        final Criteria filterCrit = new Criteria(Column.getColumn("CardFilterValues", "FILTER_ID"), (Object)filterID, 0);
        final Criteria valueCrit = new Criteria(Column.getColumn("CardFilterValues", "VALUE_ID"), (Object)valueID, 0);
        final String valueName = (String)DataAccess.get("CardFilterValues", filterCrit.and(valueCrit)).getFirstRow("CardFilterValues").get("VALUE_NAME");
        return valueName;
    }
    
    public String getFavouriteDashboard(final Long loginID, final Long customerID) throws Exception {
        String dashBoardName = "";
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("FavDashboard"));
        Criteria criteria = new Criteria(Column.getColumn("FavDashboard", "LOGIN_ID"), (Object)loginID, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("FavDashboard", "CUSTOMER_ID"), (Object)customerID, 0));
        final Join dashJoin = new Join("FavDashboard", "Dashboards", new String[] { "DASHBOARD_ID" }, new String[] { "DASHBOARD_ID" }, 2);
        query.setCriteria(criteria);
        query.addJoin(dashJoin);
        query.addSelectColumn(Column.getColumn("FavDashboard", "*"));
        query.addSelectColumn(Column.getColumn("Dashboards", "*"));
        final DataObject dataObject = SyMUtil.getPersistence().get(query);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getFirstRow("Dashboards");
            dashBoardName = (String)row.get("DASHBOARD_NAME");
        }
        return dashBoardName;
    }
    
    public void updateFavouriteDashboard(final Long loginId, final Long customerId, final Long dashboardId) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("FavDashboard"));
        Criteria criteria = new Criteria(Column.getColumn("FavDashboard", "LOGIN_ID"), (Object)loginId, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("FavDashboard", "CUSTOMER_ID"), (Object)customerId, 0));
        query.setCriteria(criteria);
        query.addSelectColumn(Column.getColumn("FavDashboard", "*"));
        final DataObject dataObject = SyMUtil.getPersistence().get(query);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getRow("FavDashboard");
            row.set("DASHBOARD_ID", (Object)dashboardId);
            dataObject.updateRow(row);
        }
        else {
            final Row row = new Row("FavDashboard");
            row.set("CUSTOMER_ID", (Object)customerId);
            row.set("LOGIN_ID", (Object)loginId);
            row.set("DASHBOARD_ID", (Object)dashboardId);
            dataObject.addRow(row);
        }
        SyMUtil.getPersistence().update(dataObject);
    }
    
    public String getCardNameFromFilter(final Properties graphProperties) throws Exception {
        final String sourceMethod = "getCardNameFromFilter";
        final String viewName = ((Hashtable<K, String>)graphProperties).get("viewName");
        final Long filterId = ((Hashtable<K, Long>)graphProperties).get("filterId");
        final String valueName = ((Hashtable<K, String>)graphProperties).get("filterValue");
        final String filterName = this.getFilterName(filterId);
        return viewName;
    }
    
    private HashMap getCardProperties(String xmlFilePath) throws IOException, ParserConfigurationException, SAXException {
        final HashMap map = new HashMap();
        if (!xmlFilePath.startsWith("\\\\")) {
            xmlFilePath = "\\\\" + xmlFilePath;
        }
        final String fileURL = System.getProperty("server.home") + xmlFilePath;
        final File file = new File(fileURL);
        if (!file.exists()) {
            throw new FileNotFoundException("File: " + fileURL + "doesnt exists");
        }
        final DocumentBuilder docBuilder = XMLUtils.getDocumentBuilderInstance();
        final Document doc = docBuilder.parse(file);
        final NodeList user = doc.getElementsByTagName("cardProperties");
        final Node node1 = user.item(0);
        for (int i = 0; i < node1.getChildNodes().getLength(); ++i) {
            final Node node2 = node1.getChildNodes().item(i);
            DashboardUtil.logger.info(node2.getNodeName());
            DashboardUtil.logger.info(node2.getTextContent());
            map.put(node2.getNodeName(), node2.getTextContent());
        }
        return map;
    }
    
    static {
        DashboardUtil.dashboardUtil = null;
        DashboardUtil.sourceClass = DashboardUtil.class.getName();
        DashboardUtil.logger = Logger.getLogger(DashboardUtil.sourceClass);
    }
}
