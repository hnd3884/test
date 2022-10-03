package com.me.ems.framework.home.core;

import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.TreeMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import java.util.Set;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.function.Predicate;
import java.util.Collection;
import java.util.LinkedHashSet;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.server.quicklaunch.core.QuickLaunchUtil;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import java.util.logging.Level;
import java.io.Reader;
import java.io.FileReader;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.simple.parser.JSONParser;
import java.util.Map;
import java.util.logging.Logger;
import org.json.simple.JSONArray;

public class HomePageUtil
{
    private JSONArray configArray;
    private static Logger logger;
    private static HomePageUtil homePageUtil;
    private static Map<String, Object> memCache;
    private static final String HOME_PAGE_DETAILS = "HOME_PAGE_DETAILS";
    
    public static HomePageUtil getInstance() {
        if (HomePageUtil.homePageUtil == null) {
            HomePageUtil.homePageUtil = new HomePageUtil();
        }
        return HomePageUtil.homePageUtil;
    }
    
    private JSONArray getConfigArray() {
        if (this.configArray == null) {
            try {
                final JSONParser parser = new JSONParser();
                this.configArray = (JSONArray)parser.parse((Reader)new FileReader(SyMUtil.getInstallationDir().concat(File.separator).concat("conf/homepage-configurations.json")));
            }
            catch (final Exception ex) {
                HomePageUtil.logger.log(Level.SEVERE, "Exception in getting quickLaunchConfigurations ", ex);
            }
        }
        return this.configArray;
    }
    
    public Map<String, Object> getHomePageQuickLinks(final String locale) throws APIException {
        try {
            final String productCode = EMSProductUtil.getEMSProductCode().get(0).toString();
            final String cacheString = "HOME_PAGE_DETAILS".concat("_").concat(productCode).concat("_").concat(locale);
            Map<String, Object> responseMap = HomePageUtil.memCache.get(cacheString);
            if (responseMap != null) {
                return responseMap;
            }
            responseMap = new HashMap<String, Object>(3);
            String basePath = "";
            final JSONArray configArray = this.getConfigArray();
            final Iterator iterator = configArray.iterator();
            Object object = null;
            while (iterator.hasNext()) {
                object = iterator.next();
                final Map productMap = (Map)object;
                final String allowedProductCodes = productMap.containsKey("productCodes") ? productMap.get("productCodes") : "";
                if (allowedProductCodes.contains(productCode)) {
                    basePath = productMap.get("filePath");
                    break;
                }
            }
            if (basePath == null || basePath.equals("")) {
                HomePageUtil.logger.log(Level.SEVERE, "No file available for this product code in homepage-configurations.json");
                throw new APIException("GENERIC0002", "No file available for this product code in homepage-configurations", new String[0]);
            }
            final List<Map<String, Object>> homePageQuickLinks = QuickLaunchUtil.getQuickLaunchLinks(basePath, null);
            for (final Map<String, Object> moduleMap : homePageQuickLinks) {
                final String id = moduleMap.get("id");
                if ("home".equals(id)) {
                    final List<Map<String, Object>> subLinks = moduleMap.get("subLinks");
                    final List<Map<String, Object>> communityList = subLinks.stream().filter(object -> object.get("id").equals("community")).collect((Collector<? super Object, ?, List<Map<String, Object>>>)Collectors.toList());
                    responseMap.put("community", communityList);
                    subLinks.remove(communityList.get(0));
                    responseMap.put("usefulLinks", subLinks);
                }
                else {
                    responseMap.put(id, moduleMap);
                }
            }
            HomePageUtil.memCache.put(cacheString, responseMap);
            return responseMap;
        }
        catch (final Exception ex) {
            HomePageUtil.logger.log(Level.SEVERE, "Error in HomePageUtil :: getHomePageQuickLinks()", ex);
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
    }
    
    public List<DashCardBean> getHomePageDashboardCards(final Long loginID, final Long customerID) throws APIException {
        try {
            final DataObject cachedUserEntryDO = this.getUserEntryDO(loginID, customerID);
            final DataObject summaryObj = this.getDefaultDataObject(loginID);
            final boolean isUserEntryPresent = cachedUserEntryDO != null && cachedUserEntryDO.size("HomePageSummaryDisplayOrder") > 0;
            final Set<String> uniqueSummaryDisplayKey = new HashSet<String>();
            final List<DashCardBean> dashCardBeans = new ArrayList<DashCardBean>();
            final List applicableGraphNames = this.getApplicableGraphNames();
            final DataObject userEntryDO = (DataObject)new WritableDataObject();
            int positionCount = 1;
            if (isUserEntryPresent) {
                final Set<Long> defaultSummaryIDs = this.getDefaultSummaryIDs(summaryObj);
                Long summaryID = null;
                final Predicate<Long> isQualifiedForRemoval = summaryID -> !set.contains(summaryID);
                final Set<Long> userOrderSummaryIDs = new LinkedHashSet<Long>();
                final Set<Long> finalUserOrderSummaryIDs = new LinkedHashSet<Long>();
                final Iterator<Row> userDisplayOrderIter = cachedUserEntryDO.getRows("HomePageSummaryDisplayOrder");
                userDisplayOrderIter.forEachRemaining(row -> set2.add(row.get("SUMMARY_ID")));
                final Set<Long> clonedSummaryIDs = new LinkedHashSet<Long>(userOrderSummaryIDs);
                final boolean isRemoved = clonedSummaryIDs.removeIf(isQualifiedForRemoval);
                final boolean isAdded = clonedSummaryIDs.addAll(defaultSummaryIDs);
                final boolean isChangeOccurred = isRemoved || isAdded;
                final Iterator<Long> iterator = clonedSummaryIDs.iterator();
                while (iterator.hasNext()) {
                    summaryID = iterator.next();
                    final Row summaryRow = summaryObj.getRow("HomePageSummary", new Criteria(Column.getColumn("HomePageSummary", "SUMMARY_ID"), (Object)summaryID, 0));
                    positionCount = this.populateDashCardBeans(applicableGraphNames, summaryRow, uniqueSummaryDisplayKey, finalUserOrderSummaryIDs, dashCardBeans, positionCount, isChangeOccurred, userEntryDO, loginID, customerID);
                }
                if (!userOrderSummaryIDs.equals(finalUserOrderSummaryIDs)) {
                    final DataObject clearedDO = this.clearUserEntry(loginID, customerID);
                    clearedDO.append(userEntryDO);
                    SyMUtil.getPersistence().update(clearedDO);
                }
            }
            else {
                final Set<Long> summaryIDs = this.getDefaultSummaryIDs(summaryObj);
                final Set<Long> finalUserOrderSummaryIDs2 = new LinkedHashSet<Long>();
                for (final Long summaryID2 : summaryIDs) {
                    final Row summaryRow2 = summaryObj.getRow("HomePageSummary", new Criteria(Column.getColumn("HomePageSummary", "SUMMARY_ID"), (Object)summaryID2, 0));
                    positionCount = this.populateDashCardBeans(applicableGraphNames, summaryRow2, uniqueSummaryDisplayKey, finalUserOrderSummaryIDs2, dashCardBeans, positionCount, true, userEntryDO, loginID, customerID);
                }
                HomePageUtil.logger.log(Level.FINE, "Final User Order Summary IDs {0}", finalUserOrderSummaryIDs2);
                SyMUtil.getPersistence().add(userEntryDO);
            }
            return dashCardBeans;
        }
        catch (final Exception ex) {
            HomePageUtil.logger.log(Level.SEVERE, "Exception Occurred while fetching DashCards for HomePage ", ex);
            throw new APIException("GENERIC0005");
        }
    }
    
    private int populateDashCardBeans(final List applicableGraphNames, final Row summaryRow, final Set<String> uniqueSummaryDisplayKey, final Set<Long> finalUserOrderSummaryIDs, final List<DashCardBean> dashCardBeans, int positionCount, final boolean isChangeOccurred, final DataObject userEntryDO, final Long loginID, final Long customerID) throws Exception {
        final String cardDisplayKey = (String)summaryRow.get("DISPLAY_KEY");
        if (uniqueSummaryDisplayKey.contains(cardDisplayKey)) {
            return positionCount;
        }
        uniqueSummaryDisplayKey.add(cardDisplayKey);
        final Long cardID = (Long)summaryRow.get("SUMMARY_ID");
        final String cardName = (String)summaryRow.get("VIEW_NAME");
        final String cardDescription = (String)summaryRow.get("SUMMARY_NAME");
        final String s;
        String cardType = s = (String)summaryRow.get("SUMMARY_TYPE");
        switch (s) {
            case "GRAPH": {
                cardType = "graph";
                break;
            }
            case "HTML": {
                cardType = "property";
                break;
            }
            case "CC": {
                cardType = "view";
                break;
            }
        }
        if (cardType.equals("graph") && !applicableGraphNames.contains(cardName)) {
            return positionCount;
        }
        if (isChangeOccurred) {
            final Row userSummaryDisplayOrder = new Row("HomePageSummaryDisplayOrder");
            userSummaryDisplayOrder.set("CUSTOMER_ID", (Object)customerID);
            userSummaryDisplayOrder.set("LOGIN_ID", (Object)loginID);
            userSummaryDisplayOrder.set("SUMMARY_ID", summaryRow.get("SUMMARY_ID"));
            userSummaryDisplayOrder.set("DISPLAY_ORDER", (Object)positionCount);
            userEntryDO.addRow(userSummaryDisplayOrder);
        }
        final DashCardBean bean = new DashCardBean();
        bean.setCardID(cardID);
        bean.setCardName(cardName);
        bean.setCardDescription(cardDescription);
        bean.setCardDisplayName(I18N.getMsg(cardDisplayKey, new Object[0]));
        bean.setCardType(cardType);
        bean.setCardPosition(positionCount++);
        dashCardBeans.add(bean);
        finalUserOrderSummaryIDs.add(cardID);
        return positionCount;
    }
    
    private Criteria getUserAndCustomerCriteria(final Long loginID, final Long customerID) {
        final Criteria userCriteria = new Criteria(new Column("HomePageSummaryDisplayOrder", "LOGIN_ID"), (Object)loginID, 0);
        final Criteria customerCriteria = new Criteria(new Column("HomePageSummaryDisplayOrder", "CUSTOMER_ID"), (Object)customerID, 0);
        return userCriteria.and(customerCriteria);
    }
    
    private DataObject getUserEntryDO(final Long loginID, final Long customerID) throws Exception {
        final Criteria userAndCustomerCriteria = this.getUserAndCustomerCriteria(loginID, customerID);
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("HomePageSummaryDisplayOrder"));
        query.addSelectColumn(new Column("HomePageSummaryDisplayOrder", "LOGIN_ID"));
        query.addSelectColumn(new Column("HomePageSummaryDisplayOrder", "SUMMARY_ID"));
        query.addSelectColumn(new Column("HomePageSummaryDisplayOrder", "DISPLAY_ORDER"));
        query.addSelectColumn(new Column("HomePageSummaryDisplayOrder", "CUSTOMER_ID"));
        query.addSelectColumn(new Column("HomePageSummary", "SUMMARY_ID"));
        query.addSelectColumn(new Column("HomePageSummary", "SUMMARY_TYPE"));
        query.addSelectColumn(new Column("HomePageSummary", "VIEW_NAME"));
        query.addSelectColumn(new Column("HomePageSummary", "DISPLAY_KEY"));
        query.addSelectColumn(new Column("HomePageSummary", "SUMMARY_NAME"));
        query.addJoin(new Join("HomePageSummaryDisplayOrder", "HomePageSummary", new String[] { "SUMMARY_ID" }, new String[] { "SUMMARY_ID" }, 2));
        final SortColumn sortColumn = new SortColumn(new Column("HomePageSummaryDisplayOrder", "DISPLAY_ORDER"), true);
        query.addSortColumn(sortColumn);
        query.setCriteria(userAndCustomerCriteria);
        return SyMUtil.getCachedPersistence().get(query);
    }
    
    private DataObject getDefaultDataObject(final Long loginID) throws Exception {
        final String productType = LicenseProvider.getInstance().getProductType();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("UserSummaryMapping"));
        query.addSelectColumn(new Column((String)null, "*"));
        query.addJoin(new Join("UserSummaryMapping", "SummaryGroup", new String[] { "SUMMARYGROUP_ID" }, new String[] { "SUMMARYGROUP_ID" }, 2));
        query.addJoin(new Join("SummaryGroup", "HomePageSummary", new String[] { "SUMMARY_ID" }, new String[] { "SUMMARY_ID" }, 2));
        query.addJoin(new Join("HomePageSummary", "HomeTabModuleOrder", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
        query.addJoin(new Join("HomeTabModuleOrder", "DCUserModuleExtn", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
        query.addJoin(new Join("DCUserModuleExtn", "UMModule", new String[] { "MODULE_ID" }, new String[] { "DC_MODULE_ID" }, 2));
        Criteria criteria = new Criteria(Column.getColumn("UserSummaryMapping", "LOGIN_ID"), (Object)loginID, 0);
        if (productType.equalsIgnoreCase("Standard")) {
            criteria = criteria.and(new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"S", 12));
        }
        else if (productType.equalsIgnoreCase("TOOLSADDON")) {
            criteria = criteria.and(new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"T", 12));
        }
        final SortColumn sortColumn = new SortColumn(new Column("HomeTabModuleOrder", "DISPLAY_ORDER"), true);
        final SortColumn sortColumn2 = new SortColumn(new Column("HomePageSummary", "DISPLAY_ORDER"), true);
        query.setCriteria(criteria);
        query.addSortColumn(sortColumn);
        query.addSortColumn(sortColumn2);
        ApiFactoryProvider.getHomePageHandler().handleSummarySelectQuery(query);
        return SyMUtil.getCachedPersistence().get(query);
    }
    
    public Set<Long> getDefaultSummaryIDs(final Long loginID) throws Exception {
        final DataObject defaultDO = this.getDefaultDataObject(loginID);
        return this.getDefaultSummaryIDs(defaultDO);
    }
    
    private Set<Long> getDefaultSummaryIDs(final DataObject defaultDO) throws Exception {
        final Set<Long> defaultSummaryIDs = new LinkedHashSet<Long>();
        final Iterator userModuleIterator = defaultDO.getRows("DCUserModuleExtn");
        while (userModuleIterator.hasNext()) {
            final Row moduleRow = userModuleIterator.next();
            final Long moduleID = (Long)moduleRow.get("MODULE_ID");
            final Criteria moduleCriteria = new Criteria(Column.getColumn("HomePageSummary", "MODULE_ID"), (Object)moduleID, 0);
            final Iterator<Row> moduleIterator = defaultDO.getRows("HomePageSummary", moduleCriteria);
            final Map<Long, Long> moduleMap = new TreeMap<Long, Long>();
            final Consumer<Row> rowConsumer = row -> {
                final Long n = map.put(row.get("SUMMARY_ID"), row.get("DISPLAY_ORDER"));
                return;
            };
            moduleIterator.forEachRemaining(rowConsumer);
            final Set<Long> summaryList = moduleMap.entrySet().stream().sorted((Comparator<? super Object>)Map.Entry.comparingByValue()).map((Function<? super Object, ?>)Map.Entry::getKey).collect((Collector<? super Object, ?, Set<Long>>)Collectors.toCollection((Supplier<R>)LinkedHashSet::new));
            defaultSummaryIDs.addAll(summaryList);
        }
        return defaultSummaryIDs;
    }
    
    private DataObject clearUserEntry(final Long loginID, final Long customerID) throws Exception {
        final Criteria userEntryExistCriteria = this.getUserAndCustomerCriteria(loginID, customerID);
        final DataObject userEntryExistDO = SyMUtil.getPersistenceLite().get("HomePageSummaryDisplayOrder", userEntryExistCriteria);
        if (userEntryExistDO != null && userEntryExistDO.size("HomePageSummaryDisplayOrder") > 0) {
            userEntryExistDO.deleteRows("HomePageSummaryDisplayOrder", userEntryExistCriteria);
        }
        return userEntryExistDO;
    }
    
    public void updateDashCardPosition(final Map<Long, Integer> cardToPositionMap, final Long loginID, final Long customerID) throws Exception {
        final DataObject userEntryExistDO = this.clearUserEntry(loginID, customerID);
        for (final Map.Entry<Long, Integer> entrySet : cardToPositionMap.entrySet()) {
            final Row userSummaryDisplayOrder = new Row("HomePageSummaryDisplayOrder");
            userSummaryDisplayOrder.set("CUSTOMER_ID", (Object)customerID);
            userSummaryDisplayOrder.set("LOGIN_ID", (Object)loginID);
            userSummaryDisplayOrder.set("SUMMARY_ID", (Object)entrySet.getKey());
            userSummaryDisplayOrder.set("DISPLAY_ORDER", (Object)entrySet.getValue());
            userEntryExistDO.addRow(userSummaryDisplayOrder);
        }
        SyMUtil.getPersistence().update(userEntryExistDO);
    }
    
    private List getApplicableGraphNames() throws DataAccessException {
        final SelectQuery graphDetailsSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("GraphDetails"));
        graphDetailsSelect.addSelectColumn(Column.getColumn("GraphDetails", "GRAPH_ID"));
        graphDetailsSelect.addSelectColumn(Column.getColumn("GraphDetails", "GRAPH_NAME"));
        final DataObject graphDetails = SyMUtil.getCachedPersistence().get(graphDetailsSelect);
        final Iterator graphDetailsIterator = graphDetails.getRows("GraphDetails");
        final List graphNames = DBUtil.getColumnValuesAsList(graphDetailsIterator, "GRAPH_NAME");
        return graphNames;
    }
    
    static {
        HomePageUtil.logger = Logger.getLogger(HomePageUtil.class.getName());
        HomePageUtil.memCache = new HashMap<String, Object>(2);
    }
}
