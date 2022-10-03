package com.me.ems.framework.server.tabcomponents.core;

import com.me.ems.framework.common.api.utils.APIException;
import com.adventnet.persistence.Row;
import com.adventnet.i18n.I18N;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.function.Supplier;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumMap;
import java.util.Arrays;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Collections;
import com.adventnet.persistence.DataAccess;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.util.Collection;
import com.me.ems.framework.server.quicklaunch.core.QuickLaunchUtil;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

public class TabComponentUtil
{
    private static final Logger LOGGER;
    private static final Function<Map<ServerAPIConstants.TabAttribute, Object>, String> TAB_ID_FUNCTION;
    
    public static TabProvider getTabProviderImpl(final String productCode) {
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("TabProvider"));
            selectQuery.addSelectColumn(Column.getColumn("TabProvider", "*"));
            final Criteria criteria = new Criteria(Column.getColumn("TabProvider", "EMS_PRODUCT_CODE"), (Object)productCode, 0);
            selectQuery.setCriteria(criteria);
            final DataObject productClassDO = SyMUtil.getCachedPersistence().get(selectQuery);
            final String className = (String)productClassDO.getFirstRow("TabProvider").get("CLASS_NAME");
            return (TabProvider)Class.forName(className).newInstance();
        }
        catch (final InstantiationException e) {
            TabComponentUtil.LOGGER.log(Level.SEVERE, "InstantiationException During Instantiation for " + productCode, e);
        }
        catch (final IllegalAccessException e2) {
            TabComponentUtil.LOGGER.log(Level.SEVERE, "IllegalAccessException During Instantiation for " + productCode, e2);
        }
        catch (final ClassNotFoundException e3) {
            TabComponentUtil.LOGGER.log(Level.SEVERE, "ClassNotFoundException  during Instantiation for " + productCode, e3);
        }
        catch (final DataAccessException e4) {
            TabComponentUtil.LOGGER.log(Level.SEVERE, "DataAccessException during fetching className for " + productCode, (Throwable)e4);
        }
        return null;
    }
    
    private static List<Map<ServerAPIConstants.TabAttribute, Object>> loadProductTabs() throws Exception {
        final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
        return getTabProviderImpl(productCode).getProductSpecificTabComponents();
    }
    
    public static List<Map<ServerAPIConstants.TabAttribute, Object>> getApplicableTabs(final User user) throws Exception {
        final Predicate<Map<ServerAPIConstants.TabAttribute, Object>> roleChecker = tabObj -> {
            final String roles = tabObj.getOrDefault(ServerAPIConstants.TabAttribute.roles, "");
            final boolean allUser = roles.isEmpty();
            return allUser || QuickLaunchUtil.checkLinkApplicableForUser(user2, roles);
        };
        final Function<Map<ServerAPIConstants.TabAttribute, Object>, Map<ServerAPIConstants.TabAttribute, Object>> attributeFilter = (Function<Map<ServerAPIConstants.TabAttribute, Object>, Map<ServerAPIConstants.TabAttribute, Object>>)(tabObj -> {
            tabObj.remove(ServerAPIConstants.TabAttribute.roles);
            tabObj.computeIfPresent(ServerAPIConstants.TabAttribute.displayName, (tabAttribute, i18Key) -> getI18NMsg(i18Key.toString()));
            tabObj.put(ServerAPIConstants.TabAttribute.canBeReordered, !tabObj.get(ServerAPIConstants.TabAttribute.tabID).equals("Home"));
            tabObj.remove(ServerAPIConstants.TabAttribute.toolTip, "");
            tabObj.remove(ServerAPIConstants.TabAttribute.iconURL, "");
            tabObj.remove(ServerAPIConstants.TabAttribute.tabOrder);
            return tabObj;
        });
        final List<Map<ServerAPIConstants.TabAttribute, Object>> tabComponents = loadProductTabs();
        final List<Map<ServerAPIConstants.TabAttribute, Object>> customTabDataList = getCustomTabDataFromDB(user.getUserID());
        if (!customTabDataList.isEmpty()) {
            tabComponents.addAll(customTabDataList);
        }
        final List<Map<ServerAPIConstants.TabAttribute, Object>> applicableTabList = tabComponents.stream().filter((Predicate<? super Object>)roleChecker).map((Function<? super Object, ?>)attributeFilter).collect((Collector<? super Object, ?, List<Map<ServerAPIConstants.TabAttribute, Object>>>)Collectors.toList());
        return applicableTabList;
    }
    
    public static List<Map<ServerAPIConstants.TabAttribute, Object>> getCustomTabDataFromDB(final Long userID) throws Exception {
        final DataObject customTabDO = DataAccess.get(getCustomTabSelectQuery(userID));
        if (customTabDO.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Map<String, Object>> customTabDataFromDB = DBUtil.getTableAsListOfMap(customTabDO, "CustomTabData");
        if (customTabDataFromDB.isEmpty()) {
            return Collections.emptyList();
        }
        final List<Map<ServerAPIConstants.TabAttribute, Object>> typeChangedList = customTabDataFromDB.stream().map((Function<? super Object, ?>)TabComponentUtil::changeToTabAttributeFormat).collect((Collector<? super Object, ?, List<Map<ServerAPIConstants.TabAttribute, Object>>>)toList(customTabDataFromDB.size()));
        return typeChangedList;
    }
    
    public static SelectQuery getCustomTabSelectQuery(final Long userID) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomTabData"));
        query.addSelectColumns((List)Arrays.asList(new Column("CustomTabData", "TAB_ID"), new Column("CustomTabData", "DISPLAY_NAME"), new Column("CustomTabData", "URL"), new Column("CustomTabData", "TOOLTIP")));
        if (userID != null) {
            final Criteria userCriteria = new Criteria(Column.getColumn("CustomTabData", "USER_ID"), (Object)userID, 0);
            query.setCriteria(userCriteria);
        }
        return query;
    }
    
    private static Map<ServerAPIConstants.TabAttribute, Object> changeToTabAttributeFormat(final Map<String, Object> customTabData) {
        final Map<ServerAPIConstants.TabAttribute, Object> typeChangedMap = new EnumMap<ServerAPIConstants.TabAttribute, Object>(ServerAPIConstants.TabAttribute.class);
        typeChangedMap.put(ServerAPIConstants.TabAttribute.tabID, customTabData.get("TAB_ID").toString());
        typeChangedMap.put(ServerAPIConstants.TabAttribute.displayName, customTabData.get("DISPLAY_NAME"));
        typeChangedMap.put(ServerAPIConstants.TabAttribute.url, customTabData.get("URL"));
        typeChangedMap.put(ServerAPIConstants.TabAttribute.canBeReordered, Boolean.TRUE);
        typeChangedMap.put(ServerAPIConstants.TabAttribute.toolTip, customTabData.getOrDefault("TOOLTIP", ""));
        typeChangedMap.put(ServerAPIConstants.TabAttribute.isCustomTab, Boolean.TRUE);
        typeChangedMap.put(ServerAPIConstants.TabAttribute.iconURL, "");
        return typeChangedMap;
    }
    
    public static void setIsNewTabKeyForTabs(final List<Map<ServerAPIConstants.TabAttribute, Object>> applicableTabList, final Long userID) throws Exception {
        final String userOrderFromDB = TabComponentCacheUtil.getTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.USER_TO_TAB_ORDER);
        final String newTabs = TabComponentCacheUtil.getTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.NEW_TABS);
        if (userOrderFromDB != null) {
            final Set<String> tabIDSetFromPreference = new HashSet<String>(Arrays.asList(userOrderFromDB.split(",")));
            final Set<String> newTabsFromServer = applicableTabList.stream().map((Function<? super Object, ?>)TabComponentUtil.TAB_ID_FUNCTION).filter(tabID -> !set.contains(tabID)).collect((Collector<? super Object, ?, Set<String>>)Collectors.toSet());
            final int capacity = (int)(newTabsFromServer.size() / 0.75f) + 1;
            final Set<String> newTabsSet = (newTabs == null) ? new HashSet<String>(capacity) : new HashSet<String>(Arrays.asList(newTabs.split(",")));
            final boolean changeInNewTabSet = newTabsSet.addAll(newTabsFromServer);
            if (newTabsSet.isEmpty()) {
                applicableTabList.forEach(tabObject -> tabObject.put(ServerAPIConstants.TabAttribute.isNewTab, Boolean.FALSE));
            }
            else {
                applicableTabList.forEach(tabObject -> tabObject.put(ServerAPIConstants.TabAttribute.isNewTab, set2.contains(tabObject.get(ServerAPIConstants.TabAttribute.tabID))));
            }
            if (changeInNewTabSet) {
                TabComponentCacheUtil.addOrUpdateTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.NEW_TABS, String.join(",", newTabsSet));
            }
        }
    }
    
    public static void sortTabsBasedOnUserPreference(final List<Map<ServerAPIConstants.TabAttribute, Object>> applicableTabList, final Long userID) throws Exception {
        final String userOrderFromDB = TabComponentCacheUtil.getTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.USER_TO_TAB_ORDER);
        final boolean hasUserCustomized = Boolean.parseBoolean(TabComponentCacheUtil.getTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.HAS_USER_CUSTOMIZED));
        final List<String> tabIDsFromServer = applicableTabList.stream().map((Function<? super Object, ?>)TabComponentUtil.TAB_ID_FUNCTION).collect((Collector<? super Object, ?, List<String>>)toList(applicableTabList.size()));
        List<String> finalList;
        if (userOrderFromDB != null && hasUserCustomized) {
            final Set<String> userOrderSet = Arrays.stream(userOrderFromDB.split(",")).filter(tabIDsFromServer::contains).collect((Collector<? super String, ?, Set<String>>)Collectors.toCollection((Supplier<R>)LinkedHashSet::new));
            userOrderSet.addAll(tabIDsFromServer);
            finalList = new ArrayList<String>(userOrderSet);
            applicableTabList.sort(Comparator.comparingInt(tabObj -> list.indexOf(tabObj.get(ServerAPIConstants.TabAttribute.tabID))));
        }
        else {
            finalList = tabIDsFromServer;
        }
        final String finalIDs = String.join(",", finalList);
        if (!finalIDs.equals(userOrderFromDB)) {
            TabComponentCacheUtil.addOrUpdateTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.USER_TO_TAB_ORDER, finalIDs);
        }
    }
    
    public static String getI18NMsg(final String key) {
        try {
            return I18N.getMsg(key, new Object[0]);
        }
        catch (final Exception ex) {
            TabComponentUtil.LOGGER.log(Level.SEVERE, "Exception while getting value of I18N key", ex);
            return key;
        }
    }
    
    public static Set<String> getTabIdsFromServer(final User user) throws Exception {
        return getApplicableTabs(user).stream().map((Function<? super Object, ?>)TabComponentUtil.TAB_ID_FUNCTION).collect((Collector<? super Object, ?, Set<String>>)Collectors.toSet());
    }
    
    public static String addCustomTabToDB(final String displayName, final String url, final String toolTip, final Long userID) throws Exception {
        final Row customTab = new Row("CustomTabData");
        customTab.set("DISPLAY_NAME", (Object)displayName);
        customTab.set("URL", (Object)url);
        customTab.set("TOOLTIP", (Object)toolTip);
        customTab.set("USER_ID", (Object)userID);
        DataAccess.generateValues(customTab);
        final DataObject dataObject = DataAccess.constructDataObject();
        dataObject.addRow(customTab);
        SyMUtil.getPersistenceLite().add(dataObject);
        return customTab.get("TAB_ID").toString();
    }
    
    public static void updateCustomTabToDB(final String customTabID, final String displayName, final String url, final String toolTip, final Long userID) throws APIException {
        try {
            final Criteria tabCriteria = new Criteria(Column.getColumn("CustomTabData", "TAB_ID"), (Object)Long.parseLong(customTabID), 0);
            final Criteria userIDCriteria = new Criteria(Column.getColumn("CustomTabData", "USER_ID"), (Object)userID, 0);
            final DataObject customTabDO = DataAccess.get("CustomTabData", tabCriteria.and(userIDCriteria));
            if (customTabDO == null || customTabDO.isEmpty()) {
                throw new APIException("TAB002");
            }
            final Row customTabRow = customTabDO.getFirstRow("CustomTabData");
            customTabRow.set("DISPLAY_NAME", (displayName == null) ? customTabRow.get("DISPLAY_NAME") : displayName);
            customTabRow.set("URL", (url == null) ? customTabRow.get("URL") : url);
            customTabRow.set("TOOLTIP", (toolTip == null) ? customTabRow.get("TOOLTIP") : toolTip);
            customTabDO.updateRow(customTabRow);
            SyMUtil.getPersistenceLite().update(customTabDO);
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception ex) {
            TabComponentUtil.LOGGER.log(Level.SEVERE, "Exception occurred", ex);
            throw new APIException("GENERIC0002", ex.getLocalizedMessage(), new String[0]);
        }
    }
    
    public static void deleteCustomTabFromDB(final String customTabID, final Long userID) throws APIException {
        try {
            final Row customTab = new Row("CustomTabData");
            customTab.set("TAB_ID", (Object)Long.valueOf(customTabID));
            customTab.set("USER_ID", (Object)userID);
            SyMUtil.getPersistenceLite().delete(customTab);
        }
        catch (final Exception ex) {
            TabComponentUtil.LOGGER.log(Level.SEVERE, "Exception occurred", ex);
            throw new APIException("GENERIC0002", ex.getLocalizedMessage(), new String[0]);
        }
    }
    
    public static void deleteAllCustomTabsForUser(final Long userID) {
        try {
            final Criteria userCustomTabCriteria = new Criteria(Column.getColumn("CustomTabData", "USER_ID"), (Object)userID, 0);
            DataAccess.delete("CustomTabData", userCustomTabCriteria);
        }
        catch (final Exception ex) {
            TabComponentUtil.LOGGER.log(Level.SEVERE, "Exception occurred while clearing custom tabs for user", ex);
        }
    }
    
    public static void addTabAtAParticularPosition(Integer position, final String customTabID, final Long userID) throws APIException {
        try {
            final String userOrderFromDB = TabComponentCacheUtil.getTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.USER_TO_TAB_ORDER);
            final List<String> userOrderList = Arrays.asList(userOrderFromDB.split(","));
            userOrderList.remove(customTabID);
            position = ((position == null || position <= 0) ? userOrderList.size() : (position - 1));
            userOrderList.add(position, customTabID);
            TabComponentCacheUtil.addOrUpdateTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.USER_TO_TAB_ORDER, String.join(",", userOrderList));
        }
        catch (final IndexOutOfBoundsException ex) {
            TabComponentUtil.LOGGER.log(Level.SEVERE, "Position Does not exist", ex);
            throw new APIException("TAB004");
        }
        catch (final Exception ex2) {
            TabComponentUtil.LOGGER.log(Level.SEVERE, "Exception occurred", ex2);
            throw new APIException("GENERIC0002", ex2.getLocalizedMessage(), new String[0]);
        }
    }
    
    public static <T> Collector<T, ?, List<T>> toList(final int size) {
        return Collectors.toCollection(() -> new ArrayList(n));
    }
    
    public static String getHomePageUrlString(final String queryParams) {
        final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
        final String homeUrl = getTabProviderImpl(productCode).getHomePageUrl();
        if (queryParams != null) {
            final StringBuilder builder = new StringBuilder(homeUrl);
            builder.insert(homeUrl.indexOf("#"), queryParams);
            return builder.toString();
        }
        return homeUrl;
    }
    
    static {
        LOGGER = Logger.getLogger(TabComponentUtil.class.getName());
        TAB_ID_FUNCTION = (tabObj -> tabObj.get(ServerAPIConstants.TabAttribute.tabID));
    }
}
