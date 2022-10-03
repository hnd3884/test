package com.me.devicemanagement.framework.webclient.search;

import java.lang.reflect.Method;
import java.io.IOException;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import org.json.JSONArray;
import java.util.Collection;
import com.adventnet.i18n.I18N;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.search.SearchConfiguration;
import java.net.URLEncoder;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;
import com.me.devicemanagement.framework.server.common.DMApplicationHandler;
import java.io.Serializable;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Range;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.search.AdvSearchConstants;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import org.json.JSONObject;
import java.util.Locale;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Hashtable;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.search.AdvSearchProductSpecificHandler;

public class AdvSearchProductSpecificHandlerImpl implements AdvSearchProductSpecificHandler
{
    public static final int DESKTOP_SEARCH_PARAM_TYPE = 0;
    private static final String COMMON_METRACK_IMPL_CLASS = "com.me.devicemanagement.onpremise.server.metrack.METrackerUtil";
    public static final int MDM_SEARCH_PARAM_TYPE = 1;
    private static final String INVENTORY_ROLE_NAME = "Inventory_Read";
    private static final String PATCH_ROLE_NAME = "PatchMgmt_Read";
    private static final String SOM_ROLE_NAME = "SOM_Read";
    private static final String SETTINGS_ROLE_NAME = "Settings_Read";
    private static Logger logger;
    private long inventoryParamId;
    private long patchParamId;
    private long somParamId;
    private long settingsParamId;
    private static final String DEFAULT_SEARCH_PARAMS = "DEFAULT_SEARCH_PARAMS";
    
    public AdvSearchProductSpecificHandlerImpl() throws Exception {
        this.inventoryParamId = 0L;
        this.patchParamId = 0L;
        this.somParamId = 0L;
        this.settingsParamId = 0L;
        final Hashtable defaultSearchParams = (Hashtable)ApiFactoryProvider.getCacheAccessAPI().getCache("DEFAULT_SEARCH_PARAMS", 2);
        if (defaultSearchParams == null || defaultSearchParams.size() == 0) {
            this.getCompParamId();
        }
    }
    
    public static JSONObject getSearchSettingsFeaturesJSON(final Locale locale) throws Exception {
        final JSONObject searchSettingsJsonObj = new JSONObject();
        searchSettingsJsonObj.put("name", (Object)I18NUtil.getJSMsgFromLocale(locale, "dm.advsearch.features.articles", new Object[0]));
        searchSettingsJsonObj.put("id", (Object)AdvSearchConstants.FEATURES_ARTICLES_PARAM_ID);
        searchSettingsJsonObj.put("category", (Object)"FeaturesAndArticles");
        return searchSettingsJsonObj;
    }
    
    private void getCompParamId() throws Exception {
        final RelationalAPI relationalAPI = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            final SelectQuery selectQuery = this.getBaseSearchParamQuery();
            final Column column = Column.getColumn("SearchParams", "PARAM_NAME");
            final Criteria compNameCri = new Criteria(column, (Object)"dc.js.common.COMPUTER_NAME", 0);
            final Criteria deviceNameCri = new Criteria(column, (Object)"dc.js.common.DEVICE_NAME", 0);
            final Criteria cri = selectQuery.getCriteria().and(compNameCri.or(deviceNameCri));
            selectQuery.setCriteria(cri);
            conn = relationalAPI.getConnection();
            ds = relationalAPI.executeQuery((Query)selectQuery, conn);
            while (ds.next()) {
                if (ds.getValue("PARAM_NAME").equals("dc.js.common.COMPUTER_NAME")) {
                    if (ds.getValue("NAME").equals("Inventory_Read")) {
                        this.inventoryParamId = (long)ds.getValue("PARAM_ID");
                    }
                    if (ds.getValue("NAME").equals("PatchMgmt_Read")) {
                        this.patchParamId = (long)ds.getValue("PARAM_ID");
                    }
                    if (ds.getValue("NAME").equals("SOM_Read")) {
                        this.somParamId = (long)ds.getValue("PARAM_ID");
                    }
                    if (!ds.getValue("NAME").equals("Settings_Read")) {
                        continue;
                    }
                    this.settingsParamId = (long)ds.getValue("PARAM_ID");
                }
            }
            final Hashtable defaultSearchParams = new Hashtable();
            defaultSearchParams.put("Inventory_Read", this.inventoryParamId);
            defaultSearchParams.put("PatchMgmt_Read", this.patchParamId);
            defaultSearchParams.put("SOM_Read", this.somParamId);
            defaultSearchParams.put("Settings_Read", this.settingsParamId);
            ApiFactoryProvider.getCacheAccessAPI().putCache("DEFAULT_SEARCH_PARAMS", defaultSearchParams, 2);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Default Computer param id for inventory_read is : " + this.inventoryParamId);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Default Computer param id for som_read is : " + this.somParamId);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Default Computer param id for patch_read is : " + this.patchParamId);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Default Computer param id for settings_read is : " + this.settingsParamId);
        }
        catch (final Exception e) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Exception thrown while getting search param id" + e.toString());
        }
        finally {
            ds.close();
            this.closeConnection(conn);
        }
    }
    
    @Override
    public JSONObject getSettingsSearchParamsUtil() throws Exception {
        return this.getSettingsSearchParamsUtil(new Locale("en", "US"));
    }
    
    private JSONObject getSettingsSearchParamsUtil(final Locale locale) throws Exception {
        JSONObject jObj = new JSONObject();
        final SelectQuery defaultSettingsSearchParamQuery = this.getBaseSearchParamQuery();
        final Criteria settingsDocsNameCri = new Criteria(Column.getColumn("SearchParams", "PARAM_NAME"), (Object)"dm.advsearch.features.articles", 0);
        defaultSettingsSearchParamQuery.setCriteria(defaultSettingsSearchParamQuery.getCriteria().and(settingsDocsNameCri));
        jObj = this.setSettingsSearchParams(defaultSettingsSearchParamQuery, locale);
        return jObj;
    }
    
    private SelectQuery getAllSearchParamQuery() {
        SelectQuery paramQuery = null;
        try {
            paramQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SearchParams"));
            final Join joinHie = new Join("SearchParams", "SearchHierarchyRel", new String[] { "PARAM_ID" }, new String[] { "LOWER_PARAM_ID" }, 2);
            final Join joinrole = new Join("SearchParams", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            paramQuery.addJoin(joinrole);
            paramQuery.addJoin(joinHie);
            paramQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_NAME"));
            paramQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_ID"));
            paramQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_TYPE"));
            paramQuery.addSelectColumn(Column.getColumn("SearchHierarchyRel", "HIERARCHY_LEVEL"));
            paramQuery.addSelectColumn(Column.getColumn("SearchHierarchyRel", "UPPER_PARAM_ID"));
            paramQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
            final Criteria visibleCri = new Criteria(Column.getColumn("SearchParams", "IS_VISIBLE"), (Object)true, 0);
            final Criteria searchLevelCri = new Criteria(Column.getColumn("SearchHierarchyRel", "HIERARCHY_LEVEL"), (Object)1, 0);
            final Criteria cri = visibleCri.and(searchLevelCri);
            paramQuery.setCriteria(cri);
            final SortColumn sortColumn = new SortColumn("SearchHierarchyRel", "DISPLAY_ORDER", true);
            paramQuery.addSortColumn(sortColumn);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search :  Param Search Query      :", RelationalAPI.getInstance().getSelectSQL((Query)paramQuery));
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - getAllSearchParamQuery", ex);
        }
        return paramQuery;
    }
    
    private SelectQuery getBaseSearchParamQuery() {
        SelectQuery baseSearchQuery = null;
        try {
            baseSearchQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SearchParams"));
            final Join joinsearch = new Join("SearchParams", "SearchHierarchyRel", new String[] { "PARAM_ID" }, new String[] { "UPPER_PARAM_ID" }, 2);
            final Join joinrole = new Join("SearchParams", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            baseSearchQuery.addJoin(joinsearch);
            baseSearchQuery.addJoin(joinrole);
            baseSearchQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_ID"));
            baseSearchQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_NAME"));
            baseSearchQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_TYPE"));
            baseSearchQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
            baseSearchQuery.addSelectColumn(Column.getColumn("SearchHierarchyRel", "HIERARCHY_LEVEL"));
            final Criteria visibleCri = new Criteria(Column.getColumn("SearchParams", "IS_VISIBLE"), (Object)true, 0);
            final Criteria baseCri = new Criteria(Column.getColumn("SearchHierarchyRel", "HIERARCHY_LEVEL"), (Object)0, 0);
            final boolean isMsp = CustomerInfoUtil.getInstance().isMSP();
            Criteria productCri = null;
            final Criteria allProductCri = new Criteria(Column.getColumn("SearchParams", "PRODUCT_ID"), (Object)0, 0);
            final Criteria mspProductCri = new Criteria(Column.getColumn("SearchParams", "PRODUCT_ID"), (Object)2, 0);
            final Criteria enterpriseProductCri = new Criteria(Column.getColumn("SearchParams", "PRODUCT_ID"), (Object)1, 0);
            if (isMsp) {
                productCri = allProductCri.or(mspProductCri);
            }
            else {
                productCri = allProductCri.or(enterpriseProductCri);
            }
            final Criteria cri = visibleCri.and(baseCri).and(productCri);
            baseSearchQuery.setCriteria(cri);
            final SortColumn sortColumn = new SortColumn("SearchHierarchyRel", "DISPLAY_ORDER", true);
            baseSearchQuery.addSortColumn(sortColumn);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Base Param Search Query      :", RelationalAPI.getInstance().getSelectSQL((Query)baseSearchQuery));
        }
        catch (final Exception ex) {}
        return baseSearchQuery;
    }
    
    @Override
    public SelectQuery getSearchParamQuery(final int searchLevel, final Long searchParamId) {
        SelectQuery paramQuery = null;
        try {
            paramQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SearchParams"));
            final Join joinHie = new Join("SearchParams", "SearchHierarchyRel", new String[] { "PARAM_ID" }, new String[] { "LOWER_PARAM_ID" }, 2);
            final Join joinrole = new Join("SearchParams", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            paramQuery.addJoin(joinrole);
            paramQuery.addJoin(joinHie);
            paramQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_NAME"));
            paramQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_ID"));
            paramQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_TYPE"));
            paramQuery.addSelectColumn(Column.getColumn("SearchHierarchyRel", "HIERARCHY_LEVEL"));
            paramQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
            if (searchParamId != null) {
                final Criteria levelCri = new Criteria(Column.getColumn("SearchHierarchyRel", "HIERARCHY_LEVEL"), (Object)(searchLevel + 1), 0);
                final Criteria paramCri = new Criteria(Column.getColumn("SearchHierarchyRel", "UPPER_PARAM_ID"), (Object)searchParamId, 0);
                final Criteria visibleCri = new Criteria(Column.getColumn("SearchParams", "IS_VISIBLE"), (Object)true, 0);
                final Criteria cri = levelCri.and(paramCri).and(visibleCri);
                paramQuery.setCriteria(cri);
            }
            final SortColumn sortColumn = new SortColumn("SearchHierarchyRel", "DISPLAY_ORDER", true);
            paramQuery.addSortColumn(sortColumn);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search :  Param Search Query      :", RelationalAPI.getInstance().getSelectSQL((Query)paramQuery));
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - getSearchQuery", ex);
        }
        return paramQuery;
    }
    
    @Override
    public SelectQuery getSearchHistoryParamQuery(final Long loginId) {
        SelectQuery searchHistoryQuery = null;
        try {
            searchHistoryQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SearchParams"));
            final Join joinRole = new Join("SearchParams", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
            final Join joinHis = new Join("SearchParams", "SearchHistory", new String[] { "PARAM_ID" }, new String[] { "PARAM_ID" }, 2);
            final Join joinLogin = new Join("SearchHistory", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2);
            searchHistoryQuery.addJoin(joinRole);
            searchHistoryQuery.addJoin(joinHis);
            searchHistoryQuery.addJoin(joinLogin);
            searchHistoryQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_NAME"));
            searchHistoryQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_ID"));
            searchHistoryQuery.addSelectColumn(Column.getColumn("SearchHistory", "SEARCH_TEXT"));
            searchHistoryQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
            final Criteria visibleCri = new Criteria(Column.getColumn("SearchParams", "IS_VISIBLE"), (Object)true, 0);
            final Criteria loginCri = new Criteria(Column.getColumn("SearchHistory", "LOGIN_ID"), (Object)loginId, 0);
            final Criteria cri = visibleCri.and(loginCri);
            searchHistoryQuery.setCriteria(cri);
            final SortColumn sortColumn = new SortColumn("SearchHistory", "SEARCH_TIME", false);
            final Range range = new Range(0, 10);
            searchHistoryQuery.setRange(range);
            searchHistoryQuery.addSortColumn(sortColumn);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search :  History Search Query       :", RelationalAPI.getInstance().getSelectSQL((Query)searchHistoryQuery));
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - getSearchHistoryParamQuery", ex);
        }
        return searchHistoryQuery;
    }
    
    @Override
    public SelectQuery getSearchHistoryParamQueryForParamID(final Long loginId, final Long searchParamID) {
        SelectQuery searchHistoryQueryWithParamID = null;
        try {
            searchHistoryQueryWithParamID = (SelectQuery)new SelectQueryImpl(Table.getTable("SearchHistory"));
            final Column searchTextCol = Column.getColumn("SearchHistory", "SEARCH_TEXT");
            final Column searchTimeCol = Column.getColumn("SearchHistory", "SEARCH_TIME").maximum();
            searchTimeCol.setColumnAlias("SEARCH_TIME");
            searchHistoryQueryWithParamID.addSelectColumn(searchTextCol);
            searchHistoryQueryWithParamID.addSelectColumn(searchTimeCol);
            final Criteria searchParamCri = new Criteria(Column.getColumn("SearchHistory", "PARAM_ID"), (Object)searchParamID, 0);
            final Criteria loginCri = new Criteria(Column.getColumn("SearchHistory", "LOGIN_ID"), (Object)loginId, 0);
            final Criteria cri = searchParamCri.and(loginCri);
            searchHistoryQueryWithParamID.setCriteria(cri);
            final SortColumn sortColumn = new SortColumn(searchTimeCol, false);
            final Range range = new Range(0, 10);
            searchHistoryQueryWithParamID.setRange(range);
            searchHistoryQueryWithParamID.addSortColumn(sortColumn);
            final List groupByColumns = new ArrayList();
            groupByColumns.add(searchTextCol);
            final GroupByClause groupByClause = new GroupByClause(groupByColumns);
            searchHistoryQueryWithParamID.setGroupByClause(groupByClause);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search :  History Search Query for LoginId & serachParamID      :", RelationalAPI.getInstance().getSelectSQL((Query)searchHistoryQueryWithParamID));
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - getSearchHistoryParamQuery", ex);
        }
        return searchHistoryQueryWithParamID;
    }
    
    @Override
    public void startProcessingHistoryDetailsUpdate(final Properties searchHistoryDataProps) throws Exception {
        try {
            final Long searchParamId = Long.valueOf(String.valueOf(((Hashtable<K, Object>)searchHistoryDataProps).get("searchParamId")));
            final Long loginId = Long.valueOf(String.valueOf(((Hashtable<K, Object>)searchHistoryDataProps).get("LOGIN_ID")));
            final String searchText = String.valueOf(((Hashtable<K, Object>)searchHistoryDataProps).get("searchTextDecode"));
            this.updateSearchHistory(searchParamId, loginId, searchText);
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.SEVERE, "Advanced Search : Exception occured - startProcessingHistoryDetailsUpdate()", ex);
            throw ex;
        }
    }
    
    @Override
    public boolean updateSearchHistory(final Long searchParamId, final Long loginId, final String searchText) throws Exception {
        final boolean status = false;
        try {
            final DataObject dObj = SyMUtil.getPersistence().constructDataObject();
            final Row hisRow = new Row("SearchHistory");
            hisRow.set("PARAM_ID", (Object)searchParamId);
            hisRow.set("SEARCH_TEXT", (Object)searchText);
            hisRow.set("LOGIN_ID", (Object)loginId);
            hisRow.set("SEARCH_TIME", (Object)System.currentTimeMillis());
            dObj.addRow(hisRow);
            SyMUtil.getPersistence().add(dObj);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.INFO, "Advanced Search : Search History table has been updated");
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.SEVERE, "Advanced Search : Exception occured - updateSearchHistory()", ex);
            throw ex;
        }
        return status;
    }
    
    @Override
    public boolean deleteSearchHistory(final Long loginId) {
        boolean status = false;
        try {
            final SelectQuery searchHistoryDelQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SearchHistory"));
            searchHistoryDelQuery.addSelectColumn(Column.getColumn("SearchHistory", "HISTORY_ID"));
            searchHistoryDelQuery.addSelectColumn(Column.getColumn("SearchHistory", "SEARCH_TIME"));
            final Criteria cri = new Criteria(Column.getColumn("SearchHistory", "LOGIN_ID"), (Object)loginId, 0);
            searchHistoryDelQuery.setCriteria(cri);
            final Range range = new Range(11, 1);
            searchHistoryDelQuery.setRange(range);
            final SortColumn sortColumn = new SortColumn("SearchHistory", "SEARCH_TIME", false);
            searchHistoryDelQuery.addSortColumn(sortColumn);
            final DataObject dObj = SyMUtil.getPersistence().get(searchHistoryDelQuery);
            if (!dObj.isEmpty()) {
                dObj.deleteRows("SearchHistory", (Criteria)null);
                SyMUtil.getPersistence().update(dObj);
                status = true;
            }
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Unwanted Search History rows of loginId" + loginId + "has been deleted");
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - deleteSearchHistory", ex);
        }
        return status;
    }
    
    @Override
    public HashMap getBaseParamDetails(final Long searchParamId) {
        String baseParamName = "";
        int baseParamType = 1;
        HashMap<String, Serializable> baseParamDetailMap = null;
        try {
            final SelectQuery baseNameQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SearchParams"));
            final Join joinsearch = new Join("SearchParams", "SearchHierarchyRel", new String[] { "PARAM_ID" }, new String[] { "UPPER_PARAM_ID" }, 2);
            baseNameQuery.addJoin(joinsearch);
            baseNameQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_ID"));
            baseNameQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_NAME"));
            baseNameQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_TYPE"));
            final Criteria baseCri = new Criteria(Column.getColumn("SearchHierarchyRel", "LOWER_PARAM_ID"), (Object)searchParamId, 0);
            baseNameQuery.setCriteria(baseCri);
            final DataObject dObj = SyMUtil.getPersistence().get(baseNameQuery);
            baseParamName = (String)dObj.getFirstValue("SearchParams", "PARAM_NAME");
            baseParamType = (int)dObj.getFirstValue("SearchParams", "PARAM_TYPE");
            baseParamDetailMap = new HashMap<String, Serializable>();
            baseParamDetailMap.put("PARAM_NAME", baseParamName);
            baseParamDetailMap.put("PARAM_TYPE", baseParamType);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Received baseParamName of searchParamId " + searchParamId + " is ", baseParamName);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Received baseParamType of searchParamId " + searchParamId + " is ", baseParamType);
        }
        catch (final Exception ex) {}
        return baseParamDetailMap;
    }
    
    @Override
    public boolean isComputerOrDevice(final Long searchParamId) {
        boolean isComputerOrDevice = false;
        try {
            final Criteria paramCri = new Criteria(Column.getColumn("SearchParams", "PARAM_ID"), (Object)searchParamId, 0);
            final Criteria visibleCri = new Criteria(Column.getColumn("SearchParams", "IS_VISIBLE"), (Object)true, 0);
            final Criteria compNameCri = new Criteria(Column.getColumn("SearchParams", "PARAM_NAME"), (Object)"dc.js.common.COMPUTER_NAME", 0);
            final Criteria deviceNameCri = new Criteria(Column.getColumn("SearchParams", "PARAM_NAME"), (Object)"dc.js.common.DEVICE_NAME", 0);
            final Criteria compDevicecri = compNameCri.or(deviceNameCri);
            final Criteria cri = paramCri.and(visibleCri).and(compDevicecri);
            final DataObject dObj = SyMUtil.getPersistence().get("SearchParams", cri);
            if (!dObj.isEmpty()) {
                isComputerOrDevice = true;
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - gerRoleNameForSearchParam", ex);
        }
        return isComputerOrDevice;
    }
    
    @Override
    public DataObject getCompDeviceDetailParam(final Long searchParamId) {
        DataObject dObj = null;
        try {
            final SelectQuery compDeviceQuery = this.getSearchParamQuery(0, searchParamId);
            compDeviceQuery.addSelectColumn(Column.getColumn("SearchCriteria", "VIEW_NAME"));
            compDeviceQuery.addSelectColumn(Column.getColumn("SearchCriteria", "CRITERIA_ID"));
            compDeviceQuery.addSelectColumn(Column.getColumn("SearchCriteria", "PARAM_ID"));
            compDeviceQuery.addSelectColumn(Column.getColumn("SearchCriteria", "TABLE_NAME"));
            compDeviceQuery.addSelectColumn(Column.getColumn("SearchCriteria", "COLUMN_NAME"));
            compDeviceQuery.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
            final Join joinCri = new Join("SearchParams", "SearchCriteria", new String[] { "PARAM_ID" }, new String[] { "PARAM_ID" }, 2);
            compDeviceQuery.addJoin(joinCri);
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : ComputerDeviceQuery  :", compDeviceQuery);
            dObj = SyMUtil.getPersistence().get(compDeviceQuery);
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - getComputerDetailParam", ex);
        }
        return dObj;
    }
    
    @Override
    public String getSelectedTab(final String baseParamName, final int baseParamType) {
        String selectedTab = "";
        try {
            if (baseParamName.equalsIgnoreCase("dc.js.common.PATCH") && baseParamType == 0) {
                selectedTab = "PatchMgmt";
            }
            if (baseParamName.equalsIgnoreCase("dc.js.common.CONFIG") && baseParamType == 0) {
                selectedTab = "Configurations";
            }
            else if (baseParamName.equalsIgnoreCase("dc.js.common.SD") && baseParamType == 0) {
                selectedTab = "SWDeploy";
            }
            else if (baseParamName.equalsIgnoreCase("dc.js.common.INV") && baseParamType == 0) {
                selectedTab = "Inventory";
            }
            else if (baseParamName.equalsIgnoreCase("dc.js.common.TOOLS") && baseParamType == 0) {
                selectedTab = "Tools";
            }
            else if (baseParamName.equalsIgnoreCase("dc.js.common.REP") && baseParamType == 0) {
                selectedTab = "Reports";
            }
            else if ((baseParamName.equalsIgnoreCase("dc.js.common.SOM") || baseParamName.equalsIgnoreCase("dc.js.common.GENERAL") || baseParamName.equalsIgnoreCase("dc.js.common.CUSTOM_FIELDS") || baseParamName.equalsIgnoreCase("dc.js.common.AD")) && baseParamType == 0) {
                selectedTab = "Admin";
            }
            else if (baseParamName.equalsIgnoreCase("dc.js.common.RDS") && baseParamType == 0) {
                selectedTab = "Tools";
            }
            else if (baseParamName.equalsIgnoreCase("dc.js.common.COMPUTER_NAME") && baseParamType == 0) {
                selectedTab = "Home";
            }
            else if (baseParamType == 1) {
                if (DMApplicationHandler.isMdmProduct()) {
                    if (baseParamName.equalsIgnoreCase("dc.js.common.INV")) {
                        selectedTab = "Asset";
                    }
                    else if (baseParamName.equalsIgnoreCase("dc.js.common.DEVICE_NAME")) {
                        selectedTab = "Home";
                    }
                }
                else {
                    selectedTab = "MDM";
                }
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - getBaseName", ex);
        }
        return selectedTab;
    }
    
    private Map setDefaultSearchParams(final List roles, final SelectQuery defaultSearchParamQuery, final Locale locale) {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        String searchParamName = "";
        Long searchParamId = null;
        Map jObj = null;
        int i = 0;
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)defaultSearchParamQuery, conn);
            jObj = new HashMap();
            Boolean inv = false;
            Boolean patch = false;
            while (ds.next()) {
                final String roleName = (String)ds.getValue("NAME");
                final boolean isRole = roles.contains(roleName);
                searchParamName = (String)ds.getValue("PARAM_NAME");
                searchParamId = (Long)ds.getValue("PARAM_ID");
                if (isRole) {
                    if (roleName.equalsIgnoreCase("Inventory_Read")) {
                        inv = true;
                    }
                    if (roleName.equalsIgnoreCase("PatchMgmt_Read")) {
                        patch = true;
                    }
                    if ((searchParamName.equalsIgnoreCase("dc.js.common.COMPUTER_NAME") || searchParamName.equalsIgnoreCase("dc.js.common.DEVICE_NAME")) && i != 0) {
                        continue;
                    }
                    ++i;
                    jObj.put("name", I18NUtil.getJSMsgFromLocale(locale, searchParamName, new Object[0]));
                    jObj.put("id", Long.valueOf(String.valueOf(searchParamId)));
                    if (!searchParamName.equals("dm.advsearch.features.articles")) {
                        continue;
                    }
                    jObj.put("category", "FeaturesAndArticles");
                }
            }
            if (jObj.toString().contains("dc.js.common.COMPUTER_NAME")) {
                jObj = this.addSearchParamId(inv, patch, jObj);
            }
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Default Search Parameter List   :", jObj);
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setDefaultSearchParams", ex);
        }
        finally {
            try {
                ds.close();
            }
            catch (final Exception e) {
                AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setSearchParams", e);
            }
            this.closeConnection(conn);
        }
        return jObj;
    }
    
    private JSONObject setSettingsSearchParams(final SelectQuery defaultSearchParamQuery, final Locale locale) {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        String searchParamName = "";
        Long searchParamId = null;
        final JSONObject jObj = new JSONObject();
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)defaultSearchParamQuery, conn);
            while (ds.next()) {
                searchParamName = (String)ds.getValue("PARAM_NAME");
                searchParamId = (Long)ds.getValue("PARAM_ID");
                jObj.put("name", (Object)I18NUtil.getJSMsgFromLocale(locale, searchParamName, new Object[0]));
                jObj.put("id", (Object)searchParamId);
                if (searchParamName.equals("dm.advsearch.features.articles")) {
                    jObj.put("category", (Object)"FeaturesAndArticles");
                }
            }
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : setSettingsSearchParams()...  :", jObj);
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setSettingsSearchParams", ex);
        }
        finally {
            try {
                ds.close();
            }
            catch (final Exception e) {
                AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setSearchParams", e);
            }
            this.closeConnection(conn);
        }
        return jObj;
    }
    
    private Map addSearchParamId(final Boolean inv, final Boolean patch, final Map map) throws Exception {
        final Hashtable defaultSearchParams = (Hashtable)ApiFactoryProvider.getCacheAccessAPI().getCache("DEFAULT_SEARCH_PARAMS", 2);
        if (defaultSearchParams == null || defaultSearchParams.size() == 0) {
            this.getCompParamId();
        }
        if (map != null && !map.isEmpty()) {
            if (inv && patch) {
                map.put("paramName", "dc.js.common.COMPUTER_NAME");
                map.put("paramId", String.valueOf(defaultSearchParams.get("Settings_Read")));
            }
            else if (inv && !patch) {
                map.put("paramName", "dc.js.common.COMPUTER_NAME");
                map.put("paramId", String.valueOf(defaultSearchParams.get("Inventory_Read")));
            }
            else if (!inv && patch) {
                map.put("paramName", "dc.js.common.COMPUTER_NAME");
                map.put("paramId", String.valueOf(defaultSearchParams.get("PatchMgmt_Read")));
            }
            else if (!inv && !patch) {
                map.put("paramName", "dc.js.common.COMPUTER_NAME");
                map.put("paramId", String.valueOf(defaultSearchParams.get("SOM_Read")));
            }
        }
        return map;
    }
    
    @Override
    public ArrayList setSearchParams(final SelectQuery paramQuery, final ArrayList paramList, final boolean isHistory) {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        final String roleName = "";
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)paramQuery, conn);
            while (ds.next()) {
                final JSONObject jObj = new JSONObject();
                jObj.put("name", ds.getValue("PARAM_NAME"));
                jObj.put("id", ds.getValue("PARAM_ID"));
                if (isHistory) {
                    jObj.put("searchText", ds.getValue("SEARCH_TEXT"));
                }
                else {
                    jObj.put("Level", ds.getValue("HIERARCHY_LEVEL"));
                }
                paramList.add(jObj);
                AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Search Parameters List  :", paramList);
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setSearchParams", ex);
        }
        finally {
            try {
                ds.close();
            }
            catch (final Exception e) {
                AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setSearchParams", e);
            }
            this.closeConnection(conn);
        }
        return paramList;
    }
    
    @Override
    public Set setSearchHistoryDataUtil(final SelectQuery paramQuery) {
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        final Set<String> searchTextSet = new LinkedHashSet<String>();
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)paramQuery, conn);
            while (ds.next()) {
                final String searchTextStr = String.valueOf(ds.getValue("SEARCH_TEXT"));
                if (searchTextStr != null && !searchTextStr.isEmpty()) {
                    searchTextSet.add(searchTextStr);
                }
            }
            if (searchTextSet != null) {
                AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Search Parameters List  :", searchTextSet.toString());
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setSearchHistoryDataUtil...", ex);
        }
        finally {
            try {
                ds.close();
            }
            catch (final Exception e) {
                AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setSearchParams", e);
            }
            this.closeConnection(conn);
        }
        return searchTextSet;
    }
    
    @Override
    public HashMap getViewDetailMap(final Iterator itr, String searchText, final Locale locale) {
        String viewName = "";
        Long criteriaId = null;
        String displayName = "";
        String viewUrl = "";
        String viewParam = "";
        HashMap<String, String> viewMap = null;
        try {
            final String productType = LicenseProvider.getInstance().getProductType();
            final Row row = itr.next();
            viewName = (String)row.get("VIEW_NAME");
            criteriaId = (Long)row.get("CRITERIA_ID");
            viewMap = new HashMap<String, String>();
            if (viewName != null && !viewName.equals("")) {
                displayName = this.getComputerDeviceDisplayName(locale, viewName);
                if (!this.getExcludeList(productType, locale).contains(displayName)) {
                    viewMap.put("viewName", viewName);
                    viewMap.put("displayName", displayName);
                    searchText = URLEncoder.encode(searchText, "UTF-8");
                    viewParam = "searchText=" + searchText + "&criteriaId=" + criteriaId;
                    viewUrl = this.appendSearchParam(viewName, viewParam);
                    AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Search View Details Map  :", viewMap);
                    viewMap.put("viewParam", viewUrl);
                }
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - closeConnection", ex);
        }
        return viewMap;
    }
    
    private HashMap<String, ArrayList<JSONObject>> setAllSearchParams(final SelectQuery paramQuery, final Locale locale) throws Exception {
        final HashMap<String, ArrayList<JSONObject>> allSearchParamHashMap = new HashMap<String, ArrayList<JSONObject>>();
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)paramQuery, conn);
            final List<String> roles = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
            while (ds.next()) {
                final String upperParamID = String.valueOf(ds.getValue("UPPER_PARAM_ID"));
                final String paramName = String.valueOf(ds.getValue("PARAM_NAME"));
                ArrayList<JSONObject> paramList = new ArrayList<JSONObject>();
                final String role = (String)ds.getValue("NAME");
                if (roles.contains(role)) {
                    if (allSearchParamHashMap.containsKey(upperParamID)) {
                        paramList = allSearchParamHashMap.get(upperParamID);
                    }
                    final JSONObject jObj = new JSONObject();
                    jObj.put("name", (Object)I18NUtil.getJSMsgFromLocale(locale, String.valueOf(ds.getValue("PARAM_NAME")), new Object[0]));
                    jObj.put("id", (Object)Long.valueOf(String.valueOf(ds.getValue("PARAM_ID"))));
                    paramList.add(jObj);
                    allSearchParamHashMap.put(upperParamID, paramList);
                }
            }
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : setAllSearchParams() Search Parameters List  :", allSearchParamHashMap.toString());
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setSearchParams", ex);
        }
        finally {
            try {
                ds.close();
            }
            catch (final Exception e) {
                AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setSearchParams", e);
            }
            this.closeConnection(conn);
        }
        return allSearchParamHashMap;
    }
    
    private ArrayList<Map> setBaseSearchParams(final SelectQuery baseSearchQuery, final Boolean isHistoryParamNeeded, final List roles, final Locale locale) throws Exception {
        final ArrayList<Map> searchBaseParamList = new ArrayList<Map>();
        final RelationalAPI relapi = RelationalAPI.getInstance();
        HashMap jObj = null;
        Connection conn = null;
        String searchParamName = "";
        String searchTitle = "";
        int searchParamType = 0;
        int selectedSearchParamType = -1;
        Long searchParamId = null;
        int compCount = 0;
        int deviceCount = 0;
        int headerCount = 0;
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)baseSearchQuery, conn);
            final List<String> excludeList = this.getExcludeList(locale);
            Boolean inv = false;
            Boolean patch = false;
            while (ds.next()) {
                final String roleName = (String)ds.getValue("NAME");
                searchParamName = (String)ds.getValue("PARAM_NAME");
                searchParamType = (int)ds.getValue("PARAM_TYPE");
                searchParamId = (Long)ds.getValue("PARAM_ID");
                boolean isRole = roles.contains(roleName);
                if (searchParamName.equalsIgnoreCase("dm.advsearch.features.articles")) {
                    isRole = SearchConfiguration.getConfiguration().isSearchEnabled();
                }
                if (isRole && !excludeList.contains(searchParamName)) {
                    if (roleName.equalsIgnoreCase("Inventory_Read")) {
                        inv = true;
                    }
                    if (roleName.equalsIgnoreCase("PatchMgmt_Read")) {
                        patch = true;
                    }
                    if (selectedSearchParamType != searchParamType && !searchParamName.equalsIgnoreCase("dm.advsearch.features.articles") && (!searchParamName.equalsIgnoreCase("dc.js.common.DEVICE_NAME") || headerCount > 0)) {
                        searchTitle = this.getTitleForSearch(searchParamType);
                        jObj = new HashMap();
                        jObj.put("type", searchParamType);
                        jObj.put("title", I18NUtil.getJSMsgFromLocale(locale, searchTitle, new Object[0]));
                        jObj.put("isTitle", true);
                        searchBaseParamList.add(jObj);
                        ++headerCount;
                    }
                    if (searchParamName.equalsIgnoreCase("dc.js.common.COMPUTER_NAME") && ++compCount != 1) {
                        continue;
                    }
                    if (searchParamName.equalsIgnoreCase("dc.js.common.DEVICE_NAME") && ++deviceCount != 1) {
                        continue;
                    }
                    selectedSearchParamType = searchParamType;
                    jObj = new HashMap();
                    jObj.put("name", I18NUtil.getJSMsgFromLocale(locale, searchParamName, new Object[0]));
                    if (searchParamName.equalsIgnoreCase("dm.advsearch.features.articles")) {
                        jObj.put("category", "FeaturesAndArticles");
                    }
                    jObj.put("type", searchParamType);
                    jObj.put("id", searchParamId);
                    searchBaseParamList.add(jObj);
                }
            }
            for (final Map jobj : searchBaseParamList) {
                if (jObj.containsKey("dc.js.common.COMPUTER_NAME")) {
                    this.addSearchParamId(inv, patch, jobj);
                }
            }
            if (isHistoryParamNeeded) {
                jObj = new HashMap();
                jObj.put("searchParamName", "dc.js.common.SEARCH_HISTORY");
                searchBaseParamList.add(jObj);
            }
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.FINE, "Advanced Search : Base Parameters List  :", searchBaseParamList);
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setBaseSearchParams", ex);
        }
        finally {
            try {
                ds.close();
            }
            catch (final Exception e) {
                AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setSearchParams", e);
            }
            this.closeConnection(conn);
        }
        return searchBaseParamList;
    }
    
    private List<String> getExcludeList(final Locale locale) {
        final ArrayList<String> list = new ArrayList<String>();
        try {
            final String productType = LicenseProvider.getInstance().getProductType();
            if (productType.equalsIgnoreCase("TOOLSADDON")) {
                list.add(I18NUtil.getMsgFromLocale(locale, "dc.js.common.CUSTOM_FIELDS", new Object[0]));
            }
        }
        catch (final Exception e) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - getExcludeList()", e);
        }
        return list;
    }
    
    private String getTitleForSearch(final int searchParamType) {
        String searchTitle = "";
        try {
            if (searchParamType == 0) {
                searchTitle = "dc.js.common.COMPUTERS";
            }
            else if (searchParamType == 1) {
                searchTitle = "dc.js.common.DEVICES";
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - getTitleForSearch", ex);
        }
        return searchTitle;
    }
    
    private void closeConnection(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - closeConnection", ex);
        }
    }
    
    @Override
    public HashMap getViewDetailMap(final HttpServletRequest request, final Iterator itr, final String searchText) {
        return this.getViewDetailMap(itr, searchText, request.getLocale());
    }
    
    private List<String> getExcludeList(final String productType, final Locale locale) throws Exception {
        final ArrayList<String> list = new ArrayList<String>();
        if (productType.equalsIgnoreCase("TOOLSADDON")) {
            list.add(I18NUtil.getMsgFromLocale(locale, "dc.common.INVENTORY", new Object[0]));
            list.add(I18NUtil.getMsgFromLocale(locale, "desktopcentral.common.patch", new Object[0]));
            list.add(I18NUtil.getMsgFromLocale(locale, "dc.mdm.general.enrollment", new Object[0]));
        }
        return list;
    }
    
    private String appendSearchParam(final String viewName, String url) {
        try {
            int reportCategory = 0;
            int selectedTreeElem = 0;
            String patchTypeParam = "";
            if (viewName.equalsIgnoreCase("AllGroupsViewSearch")) {
                reportCategory = 3;
                selectedTreeElem = 300;
            }
            else if (viewName.equalsIgnoreCase("AllUsersViewSearch")) {
                reportCategory = 1;
                selectedTreeElem = 100;
            }
            else if (viewName.equalsIgnoreCase("AllContainerViewSearch")) {
                reportCategory = 6;
                selectedTreeElem = 600;
            }
            else if (viewName.equalsIgnoreCase("ApplicablePatchViewSearch")) {
                patchTypeParam = "applicable";
            }
            if (patchTypeParam == null || patchTypeParam.isEmpty()) {
                url = url + "&reportCategory=" + reportCategory + "&selectedTreeElem=" + selectedTreeElem;
            }
            else {
                url = url + "&reportCategory=" + reportCategory + "&selectedTreeElem=" + selectedTreeElem + "&patchTypeParam=" + patchTypeParam;
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - setReportParams", ex);
        }
        return url;
    }
    
    private String getComputerDeviceDisplayName(final Locale locale, final String viewName) {
        String displayName = "";
        try {
            if (viewName.equalsIgnoreCase("InvComputersSummarySearch")) {
                displayName = I18N.getMsg("dc.common.INVENTORY", new Object[0]);
            }
            if (viewName.equalsIgnoreCase("SoMManagedComputersSearch")) {
                displayName = I18N.getMsg("dc.common.SOM", new Object[0]);
            }
            if (viewName.equalsIgnoreCase("systemPMHealthViewTableSearch")) {
                displayName = I18N.getMsg("desktopcentral.common.patch", new Object[0]);
            }
            if (viewName.equalsIgnoreCase("EnrollmentRequestSearch")) {
                displayName = I18N.getMsg("dc.mdm.general.enrollment", new Object[0]);
            }
            if (viewName.equalsIgnoreCase("DeviceListSearch")) {
                displayName = I18N.getMsg("dc.common.INVENTORY", new Object[0]);
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occured - getComputerDisplayName", ex);
        }
        return displayName;
    }
    
    @Override
    public JSONObject getSearchParamsListAsJsonObjectFromCache(final HttpServletRequest request) throws Exception {
        return new JSONObject();
    }
    
    @Override
    public JSONObject getSearchParamsListAsJsonObjectFromCache(final HashMap map, final Locale locale) throws Exception {
        HashMap searchParamsWithRolesLicenseMap = new HashMap();
        final String selectedTab = map.get("selectedTab");
        final String userKey = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID() + "_" + ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID() + "_" + ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        final String cacheName = userKey + "_" + selectedTab + "_" + locale.toLanguageTag();
        searchParamsWithRolesLicenseMap = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheName, 3);
        final Boolean needToFetchSearchParams = this.checkIfSearchParamsListIsToFetchedFromDB(searchParamsWithRolesLicenseMap);
        if (needToFetchSearchParams) {
            searchParamsWithRolesLicenseMap = this.fetchFromDBAndCacheSearchParamsListAsJsonObject(map, locale);
        }
        final JSONObject searchParamsListJsonObj = new JSONObject(searchParamsWithRolesLicenseMap.get("searchParamsList").toString());
        return searchParamsListJsonObj;
    }
    
    private Boolean checkIfSearchParamsListIsToFetchedFromDB(final HashMap searchParamsWithRolesLicenseMap) throws Exception {
        Boolean needToFetchSearchParamsFromDB = Boolean.TRUE;
        if (searchParamsWithRolesLicenseMap != null && !searchParamsWithRolesLicenseMap.isEmpty()) {
            final String licenseType = LicenseProvider.getInstance().getLicenseType();
            final String productType = LicenseProvider.getInstance().getProductType();
            final List<String> userRoleList = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
            if (searchParamsWithRolesLicenseMap.containsKey("licenseType") && String.valueOf(searchParamsWithRolesLicenseMap.get("licenseType")).equalsIgnoreCase(licenseType) && searchParamsWithRolesLicenseMap.containsKey("ProductType") && String.valueOf(searchParamsWithRolesLicenseMap.get("ProductType")).equalsIgnoreCase(productType) && searchParamsWithRolesLicenseMap.containsKey("userRolesList")) {
                final List<String> searchParamsUserRoleList = searchParamsWithRolesLicenseMap.get("userRolesList");
                if (userRoleList.size() == searchParamsUserRoleList.size() && userRoleList.containsAll(searchParamsUserRoleList)) {
                    needToFetchSearchParamsFromDB = Boolean.FALSE;
                }
            }
        }
        return needToFetchSearchParamsFromDB;
    }
    
    private HashMap fetchFromDBAndCacheSearchParamsListAsJsonObject(final HashMap map, final Locale locale) throws Exception {
        final HashMap searchParamsWithRolesLicenseMap = new HashMap();
        final JSONObject searchParamsListAsJsonObject = this.getSearchParamsListAsJsonObject(map, locale);
        final String selectedTab = String.valueOf(map.get("selectedTab"));
        final String userKey = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID() + "_" + ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID() + "_" + ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        final String cacheName = userKey + "_" + selectedTab + "_" + locale.toLanguageTag();
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        final String productType = LicenseProvider.getInstance().getProductType();
        final List<String> userRoleList = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
        searchParamsWithRolesLicenseMap.put("searchParamsList", searchParamsListAsJsonObject);
        searchParamsWithRolesLicenseMap.put("userRolesList", userRoleList);
        searchParamsWithRolesLicenseMap.put("licenseType", licenseType);
        searchParamsWithRolesLicenseMap.put("ProductType", productType);
        ApiFactoryProvider.getCacheAccessAPI().putCache(cacheName, searchParamsWithRolesLicenseMap);
        return searchParamsWithRolesLicenseMap;
    }
    
    public JSONObject getSearchParamsListAsJsonObject(final HashMap map, final Locale locale) throws Exception {
        String authKey = null;
        final JSONObject datajson = new JSONObject();
        final ArrayList<JSONObject> searchCompleteArrayList = new ArrayList<JSONObject>();
        final Boolean isSearchEnabled = SearchConfiguration.getConfiguration().isSearchEnabled();
        if (isSearchEnabled) {
            authKey = map.get("authToken");
        }
        if (authKey != null && !authKey.isEmpty() && isSearchEnabled) {
            datajson.put("authParamKey", (Object)authKey);
            final JSONObject settingsSearchParamJsonObj = this.getSettingsSearchParamsUtil(locale);
            datajson.put("selectedParam", (Object)settingsSearchParamJsonObj);
        }
        else {
            final Map defaultSearchParamJsonObj = this.getDefaultSearchParamsUtil(map.get("roles"), locale);
            datajson.put("selectedParam", defaultSearchParamJsonObj);
        }
        final ArrayList<JSONObject> searchParamArrayList = this.getSearchParamListWithInnerList(map, locale);
        searchCompleteArrayList.addAll(searchParamArrayList);
        final JSONArray searchParamsArray = new JSONArray((Collection)searchCompleteArrayList);
        datajson.put("paramList", (Object)searchParamsArray);
        return datajson;
    }
    
    @Override
    public String getAuthKey(final HttpServletRequest request) {
        String authKey = null;
        Long currentlyLoggedInUserLoginId = null;
        try {
            currentlyLoggedInUserLoginId = SYMClientUtil.getLoginId(request);
            final String sUserName = DMUserHandler.getDCUser(currentlyLoggedInUserLoginId);
            final String sDomainName = DMUserHandler.getDCUserDomain(currentlyLoggedInUserLoginId);
            final HashMap<String, HashMap> userLoginData = DMUserHandler.getLoginDataForUser(sUserName, sDomainName);
            if (userLoginData != null && !userLoginData.isEmpty() && userLoginData.containsKey("auth_data")) {
                final HashMap authKeyMap = userLoginData.get("auth_data");
                if ((authKeyMap != null & !authKeyMap.isEmpty()) && authKeyMap.containsKey("auth_token")) {
                    authKey = String.valueOf(authKeyMap.get("auth_token"));
                }
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.SEVERE, "Exception while getting AuthToken from Request parameter: ", ex);
            throw ex;
        }
        return authKey;
    }
    
    @Override
    public JSONObject getDefaultSearchParamsUtil(final HttpServletRequest httpServletRequest) throws Exception {
        return new JSONObject(this.getDefaultSearchParamsUtil(ApiFactoryProvider.getAuthUtilAccessAPI().getRoles()));
    }
    
    @Override
    public Map getDefaultSearchParamsUtil(final List roles) {
        return this.getDefaultSearchParamsUtil(roles, new Locale("en", "US"));
    }
    
    private Map getDefaultSearchParamsUtil(final List roles, final Locale locale) {
        final SelectQuery defaultSearchParamQuery = this.getBaseSearchParamQuery();
        final Criteria compNameCri = new Criteria(Column.getColumn("SearchParams", "PARAM_NAME"), (Object)"dc.js.common.COMPUTER_NAME", 0);
        final Criteria deviceNameCri = new Criteria(Column.getColumn("SearchParams", "PARAM_NAME"), (Object)"dc.js.common.DEVICE_NAME", 0);
        final Criteria cri = defaultSearchParamQuery.getCriteria().and(compNameCri.or(deviceNameCri));
        defaultSearchParamQuery.setCriteria(cri);
        final Map jObj = this.setDefaultSearchParams(roles, defaultSearchParamQuery, locale);
        return jObj;
    }
    
    @Override
    public ArrayList getBaseSearchParamsUtil(final HttpServletRequest request, final Boolean isHistoryParamNeeded) throws Exception {
        return new ArrayList();
    }
    
    @Override
    public ArrayList getBaseSearchParamsUtil(final Boolean isHistoryParamNeeded, final List roles, final Locale locale) throws Exception {
        final SelectQuery baseSearchQuery = this.getBaseSearchParamQuery();
        final ArrayList<Map> searchBaseParamList = this.setBaseSearchParams(baseSearchQuery, isHistoryParamNeeded, roles, locale);
        return searchBaseParamList;
    }
    
    private ArrayList<JSONObject> getSearchParamListWithInnerList(final HashMap map, final Locale locale) throws Exception {
        final ArrayList<JSONObject> allsearchParamArrayList = new ArrayList<JSONObject>();
        final ArrayList searchParamsArrayList = this.getBaseSearchParamsUtil(false, map.get("roles"), locale);
        final HashMap<String, ArrayList<JSONObject>> allInnerSearchParamHashMap = this.getInnerSearchParamsList(locale);
        for (int i = 0; i < searchParamsArrayList.size(); ++i) {
            final JSONObject jsObj = new JSONObject((Map)searchParamsArrayList.get(i));
            if (jsObj != null && jsObj.length() > 0) {
                if (jsObj.toString().contains("id")) {
                    final String searchParamId = jsObj.get("id").toString();
                    final String searchparamName = jsObj.get("name").toString();
                    if (!searchparamName.equalsIgnoreCase(I18NUtil.getJSMsgFromLocale(locale, "dc.js.common.COMPUTER_NAME", new Object[0])) && !searchparamName.equalsIgnoreCase(I18NUtil.getJSMsgFromLocale(locale, "dc.js.common.DEVICE_NAME", new Object[0])) && searchParamId != null && !searchParamId.isEmpty() && allInnerSearchParamHashMap.containsKey(searchParamId)) {
                        jsObj.put("subParams", (Collection)allInnerSearchParamHashMap.get(searchParamId));
                    }
                }
                allsearchParamArrayList.add(jsObj);
            }
        }
        return allsearchParamArrayList;
    }
    
    private HashMap<String, ArrayList<JSONObject>> getInnerSearchParamsList(final Locale locale) throws Exception {
        final SelectQuery allParamSearchQuery = this.getAllSearchParamQuery();
        final HashMap<String, ArrayList<JSONObject>> allInnerSearchParamHashMap = this.setAllSearchParams(allParamSearchQuery, locale);
        return allInnerSearchParamHashMap;
    }
    
    @Override
    public Boolean disableAdvSearchForCertainEdition() {
        Boolean isEnable = Boolean.FALSE;
        try {
            final String licenseVersion = LicenseProvider.getInstance().getLicenseVersion();
            final String productType = LicenseProvider.getInstance().getProductType();
            if (licenseVersion != null && (productType.equalsIgnoreCase("TOOLSADDON") || CustomerInfoUtil.isPMP() || CustomerInfoUtil.isVMPProduct() || productType.equalsIgnoreCase("Tools_Professional") || productType.equalsIgnoreCase("Tools_Standard"))) {
                isEnable = Boolean.FALSE;
            }
            else {
                isEnable = Boolean.TRUE;
            }
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.INFO, "onChange License AdvSearch Enable: ", this.updateMainIndexFile(isEnable, "onChange Product: " + productType));
        }
        catch (final Exception e) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.SEVERE, "Exception while onChange License while enabling AdvSearch", e);
        }
        return isEnable;
    }
    
    @Override
    public Boolean updateMainIndexFile(final Boolean isEnable, final String message) throws Exception {
        final String advSearchPropertyFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "Search" + File.separator + "adv-search.properties";
        try {
            final Properties props = new Properties();
            props.setProperty("search.settings.enabled", isEnable.toString());
            props.setProperty("search.documents.enabled", isEnable.toString());
            props.setProperty("search.enabled", isEnable.toString());
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(advSearchPropertyFileName)) {
                FileAccessUtil.storeProperties(props, advSearchPropertyFileName, Boolean.TRUE, message);
            }
        }
        catch (final IOException ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.SEVERE, "AdvSearchProductSpecificHandlerImpl - Exception occurred - updateMainIndexFile() : ", ex);
        }
        finally {
            SearchConfiguration.updateSearchConfiguration(message);
        }
        return isEnable;
    }
    
    @Override
    public JSONObject getLastSearchParam(final Long loginId) {
        return new JSONObject();
    }
    
    @Override
    public JSONObject getLastSearchParam(final Long loginId, final Locale locale) {
        final JSONObject lastSearchParamJSONObj = new JSONObject();
        final SelectQuery searchHistoryQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("SearchHistory"));
        searchHistoryQuery.addSelectColumn(Column.getColumn("SearchHistory", "PARAM_ID"));
        searchHistoryQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_ID"));
        searchHistoryQuery.addSelectColumn(Column.getColumn("SearchParams", "PARAM_NAME"));
        searchHistoryQuery.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
        final Join join = new Join("SearchHistory", "SearchParams", new String[] { "PARAM_ID" }, new String[] { "PARAM_ID" }, 2);
        searchHistoryQuery.addJoin(join);
        final Join joinrole = new Join("SearchParams", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2);
        searchHistoryQuery.addJoin(joinrole);
        final Criteria cri = new Criteria(Column.getColumn("SearchHistory", "LOGIN_ID"), (Object)loginId, 0);
        searchHistoryQuery.setCriteria(cri);
        final SortColumn sortColumn = new SortColumn("SearchHistory", "SEARCH_TIME", false);
        searchHistoryQuery.addSortColumn(sortColumn);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relapi.getConnection();
            ds = relapi.executeQuery((Query)searchHistoryQuery, conn);
            final List<String> roles = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
            if (ds.next()) {
                final String role = String.valueOf(ds.getValue("NAME"));
                if (roles.contains(role)) {
                    final String paramName = String.valueOf(ds.getValue("PARAM_NAME"));
                    final String paramId = String.valueOf(ds.getValue("PARAM_ID"));
                    if (paramName != null && !paramName.isEmpty() && paramId != null && !paramId.isEmpty()) {
                        lastSearchParamJSONObj.put("name", (Object)I18NUtil.getJSMsgFromLocale(locale, paramName, new Object[0]));
                        if (paramName.equalsIgnoreCase("dm.advsearch.features.articles")) {
                            lastSearchParamJSONObj.put("category", (Object)"FeaturesAndArticles");
                        }
                        lastSearchParamJSONObj.put("id", (Object)paramId);
                    }
                }
            }
        }
        catch (final Exception ex) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.WARNING, "Advanced Search : Exception occurred - getLastSearchParam", ex);
            try {
                if (conn != null) {
                    conn.close();
                }
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final Exception e) {
                AdvSearchProductSpecificHandlerImpl.logger.log(Level.SEVERE, "Exception while closing the connection");
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final Exception e2) {
                AdvSearchProductSpecificHandlerImpl.logger.log(Level.SEVERE, "Exception while closing the connection");
            }
        }
        return lastSearchParamJSONObj;
    }
    
    @Override
    public JSONObject getProductInfo() throws Exception {
        final JSONObject json = new JSONObject();
        final LicenseProvider licenseProvider = LicenseProvider.getInstance();
        final String meTrackID = getMEDCTrackId();
        final String licenseType = licenseProvider.getLicenseType();
        final String productType = licenseProvider.getProductType();
        json.put("meTrackID", (Object)meTrackID);
        json.put("licenseType", (Object)licenseType);
        json.put("productType", (Object)productType);
        return json;
    }
    
    public static String getMEDCTrackId() {
        try {
            CustomerInfoUtil.getInstance();
            if (!CustomerInfoUtil.isSAS()) {
                final Object instance = Class.forName("com.me.devicemanagement.onpremise.server.metrack.METrackerUtil").newInstance();
                final Method method = instance.getClass().getMethod("getMEDCTrackId", (Class<?>[])new Class[0]);
                return (String)method.invoke(instance, new Object[0]);
            }
        }
        catch (final Exception e) {
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.SEVERE, "getMEDCTrackId - Error while executing method");
            AdvSearchProductSpecificHandlerImpl.logger.log(Level.SEVERE, null, e);
        }
        return "--";
    }
    
    static {
        AdvSearchProductSpecificHandlerImpl.logger = Logger.getLogger(AdvSearchProductSpecificHandlerImpl.class.getName());
    }
}
