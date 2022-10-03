package com.adventnet.client.components.tab.web;

import java.util.logging.Level;
import com.adventnet.persistence.Persistence;
import com.adventnet.client.view.ViewModel;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.client.util.web.WebClientUtil;
import java.util.List;
import com.adventnet.client.util.DataUtils;
import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.client.components.util.web.PersonalizationUtil;
import java.util.logging.Logger;

public class TabPersonalizationUtil
{
    private static Logger log;
    
    public static void addNewTab(final String viewName, final long accountId, final String tabName, final String tabUrl) throws Exception {
        final String newTabName = PersonalizationUtil.genNameFromTitle(tabName);
        PersonalizationUtil.isViewPresent(newTabName);
        final String navigationGridName = tabName + " Navigation Bar";
        final String contentAreaViewName = tabName + " Content Area";
        addNewGridView(accountId, navigationGridName);
        addNewContentAreaView(accountId, contentAreaViewName, newTabName);
        final Row viewConfiguration = new Row("ViewConfiguration");
        viewConfiguration.set(2, (Object)newTabName);
        viewConfiguration.set(6, (Object)tabName);
        viewConfiguration.set("ACCOUNT_ID", (Object)new Long(accountId));
        final WritableDataObject newDO = new WritableDataObject();
        newDO.addRow(viewConfiguration);
        final Row webViewConfig = new Row("WebViewConfig");
        webViewConfig.set(1, viewConfiguration.get(1));
        webViewConfig.set(2, (Object)tabUrl);
        final Row tiledView1 = new Row("TiledView");
        tiledView1.set(1, viewConfiguration.get(1));
        tiledView1.set(2, (Object)"NAVIGATIONBAR");
        tiledView1.set(3, (Object)WebViewAPI.getViewNameNo((Object)PersonalizationUtil.genNameFromTitle(navigationGridName)));
        final Row tiledView2 = new Row("TiledView");
        tiledView2.set(1, viewConfiguration.get(1));
        tiledView2.set(2, (Object)"DEFAULTCONTENT");
        tiledView2.set(3, (Object)WebViewAPI.getViewNameNo((Object)PersonalizationUtil.genNameFromTitle(contentAreaViewName)));
        final Row mapping = new Row("ACViewToGroupMapping");
        mapping.set("VIEWNAME", viewConfiguration.get(1));
        mapping.set("GROUPNAME", (Object)"Tabs");
        newDO.addRow(webViewConfig);
        newDO.addRow(tiledView1);
        newDO.addRow(tiledView2);
        newDO.addRow(mapping);
        LookUpUtil.getPersistence().add((DataObject)newDO);
        addNewTabToView(viewName, accountId, newTabName);
    }
    
    public static void addNewContentAreaView(final long accountId, final String contentAreaName, final String id) throws Exception {
        PersonalizationUtil.createUpdateViewFromDummy("_DUMMY_CONTENTAREA_TABLELAYOUT", PersonalizationUtil.genNameFromTitle(contentAreaName), contentAreaName, accountId);
    }
    
    public static void addNewGridView(final long accountId, final String gridViewName) throws Exception {
        PersonalizationUtil.createUpdateViewFromDummy("_DUMMY_NAVIG_GRID", PersonalizationUtil.genNameFromTitle(gridViewName), gridViewName, accountId);
    }
    
    public static void addNewTabToView(String tabName, final long accountId, final String newViewName) throws Exception {
        if (UserPersonalizationAPI.getOriginalViewName((Object)tabName, accountId) != null) {
            tabName = UserPersonalizationAPI.getOriginalViewName((Object)tabName, accountId);
        }
        final DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)tabName, accountId);
        final String personalizedViewName = UserPersonalizationAPI.getPersonalizedConfigName(tabName, accountId);
        final int index = DataUtils.getMaxIndex(dataObject, "ACTabChildConfig", 3);
        final Row newTab = new Row("ACTabChildConfig");
        newTab.set(1, (Object)WebViewAPI.getViewNameNo((Object)personalizedViewName));
        newTab.set(2, (Object)WebViewAPI.getViewNameNo((Object)newViewName));
        newTab.set(3, (Object)new Integer(index + 1));
        dataObject.addRow(newTab);
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
    }
    
    public static void addNewTabView(final long accountId, final String tabViewName) throws Exception {
        final Row navigationRow = new Row("ViewConfiguration");
        navigationRow.set(2, (Object)tabViewName.replace(' ', '_'));
        navigationRow.set(6, (Object)tabViewName);
        navigationRow.set(3, (Object)WebViewAPI.getUIComponentNameNo("ACTab"));
        navigationRow.set("CUSTOMIZETYPE", (Object)"PERSONALIZE");
        navigationRow.set("ACCOUNT_ID", (Object)new Long(accountId));
        final Row gridLayoutConfig = new Row("UINavigationConfig");
        gridLayoutConfig.set("VIEWNAME", navigationRow.get(1));
        final Row featureParamsRow = new Row("FeatureParams");
        featureParamsRow.set(1, navigationRow.get(1));
        featureParamsRow.set(2, (Object)"WEB_VIEW");
        featureParamsRow.set(3, (Object)"verticaltab");
        final Row mapping = new Row("ACViewToGroupMapping");
        mapping.set("VIEWNAME", navigationRow.get(1));
        mapping.set("GROUPNAME", (Object)"Navigation");
        final WritableDataObject navDO = new WritableDataObject();
        navDO.addRow(navigationRow);
        navDO.addRow(gridLayoutConfig);
        navDO.addRow(featureParamsRow);
        navDO.addRow(mapping);
        LookUpUtil.getPersistence().add((DataObject)navDO);
    }
    
    public static void addNewViewToGrid(final String gridName, final long accountId, final String viewName) throws Exception {
        PersonalizationUtil.isViewPresent(viewName);
        addNewTabView(accountId, viewName);
        final DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)gridName, accountId);
        final int index = DataUtils.getMaxIndex(dataObject, "ACGridLayoutChildConfig", 3);
        final String personalizedViewName = UserPersonalizationAPI.getPersonalizedConfigName(gridName, accountId);
        final Row newTab = new Row("ACGridLayoutChildConfig");
        newTab.set(1, (Object)WebViewAPI.getViewNameNo((Object)personalizedViewName));
        newTab.set(2, (Object)WebViewAPI.getViewNameNo((Object)viewName.replace(' ', '_')));
        newTab.set(3, (Object)new Integer(index + 1));
        dataObject.addRow(newTab);
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
    }
    
    public static List<Row> getTabChildConfigForView(Object viewName) throws Exception {
        final long acc_id = WebClientUtil.getAccountId();
        final ViewModel model = (ViewModel)WebViewAPI.getConfigModel(viewName, false);
        if (viewName instanceof String) {
            viewName = WebViewAPI.getViewNameNo(viewName);
        }
        List<Row> children = (List<Row>)model.getCompiledData((Object)("SORTEDTABLIST:" + acc_id));
        if (children != null) {
            return children;
        }
        final Row row = new Row("ACUserTabChildConfig");
        row.set("VIEWNAME", viewName);
        row.set("ACCOUNT_ID", (Object)acc_id);
        final Persistence pers = LookUpUtil.getPersistence();
        final DataObject daob = pers.get("ACUserTabChildConfig", row);
        children = new ArrayList<Row>();
        if (!daob.isEmpty()) {
            final List<Row> userBasedTabChildRows = DataUtils.getSortedList(daob, "ACUserTabChildConfig", "CHILDINDEX");
            final DataObject tabChildDo = model.getViewConfiguration();
            for (int idx = 0; idx < userBasedTabChildRows.size(); ++idx) {
                final Row userTabChildConfigRow = userBasedTabChildRows.get(idx);
                final Object childViewName = userTabChildConfigRow.get(2);
                final Object childIndex = userTabChildConfigRow.get(3);
                final Object displayName = userTabChildConfigRow.get("DISPLAYNAME");
                Row tabChildRow = new Row("ACTabChildConfig");
                final Criteria criteria = new Criteria(new Column("ACTabChildConfig", 2), childViewName, 0);
                criteria.and(new Column("ACTabChildConfig", 1), viewName, 0);
                tabChildRow = (Row)tabChildDo.getRow("ACTabChildConfig", criteria).clone();
                tabChildRow.set(3, childIndex);
                if (displayName != null) {
                    tabChildRow.set("DISPLAYNAME", displayName);
                }
                children.add(tabChildRow);
            }
            return children;
        }
        if (model.getCompiledData((Object)"SORTEDTABLIST") != null) {
            return (List)model.getCompiledData((Object)"SORTEDTABLIST");
        }
        children = DataUtils.getSortedList(model.getViewConfiguration(), "ACTabChildConfig", "CHILDINDEX");
        model.addCompiledData((Object)"SORTEDTABLIST", (Object)children);
        return children;
    }
    
    private static void personalizeTabForUser(final long accountId, Object viewName, final List childViewList) throws Exception {
        TabPersonalizationUtil.log.log(Level.FINER, "viewname{0}", viewName);
        if (viewName instanceof String) {
            viewName = WebViewAPI.getViewNameNo(viewName);
        }
        try {
            final DataObject daob = (DataObject)new WritableDataObject();
            for (int idx = 0; idx < childViewList.size(); ++idx) {
                Object childViewName = childViewList.get(idx);
                TabPersonalizationUtil.log.log(Level.FINER, "childname{0}", childViewName);
                final Row acUserTabChildRow = new Row("ACUserTabChildConfig");
                acUserTabChildRow.set("ACCOUNT_ID", (Object)accountId);
                acUserTabChildRow.set("VIEWNAME", viewName);
                if (childViewName instanceof String) {
                    childViewName = WebViewAPI.getViewNameNo(childViewName);
                }
                acUserTabChildRow.set("CHILDVIEWNAME", childViewName);
                acUserTabChildRow.set("CHILDINDEX", (Object)idx);
                daob.addRow(acUserTabChildRow);
            }
            final Row row = new Row("ACUserTabChildConfig");
            row.set("VIEWNAME", viewName);
            row.set("ACCOUNT_ID", (Object)accountId);
            final Persistence pers = LookUpUtil.getPersistence();
            final DataObject userTabChildDO = pers.get("ACUserTabChildConfig", row);
            if (userTabChildDO.isEmpty()) {
                pers.add(daob);
            }
            else {
                final DataObject userTabChildren = userTabChildDO.diff(daob);
                pers.update(userTabChildren);
            }
        }
        catch (final Exception e) {
            throw new Exception("Exception occurred while inserting a row of ACUserTabChildConfig Table :" + e);
        }
    }
    
    public static void setDisplayNameForTab(final long account_id, Object viewName, Object childViewName, final String displayName) throws Exception {
        try {
            if (viewName instanceof String) {
                viewName = WebViewAPI.getViewNameNo(viewName);
            }
            if (childViewName instanceof String) {
                childViewName = WebViewAPI.getViewNameNo(childViewName);
            }
            final Persistence pers = LookUpUtil.getPersistence();
            final ViewModel model = (ViewModel)WebViewAPI.getConfigModel(viewName, false);
            Row tabChildRow = new Row("ACUserTabChildConfig");
            tabChildRow.set(1, viewName);
            tabChildRow.set(2, childViewName);
            tabChildRow.set(4, (Object)account_id);
            DataObject tabChildDo = pers.get("ACUserTabChildConfig", tabChildRow);
            if (tabChildDo.isEmpty()) {
                final List childConfigList = getTabChildConfigForView(viewName);
                final List childViewNames = new ArrayList();
                for (int idx = 0; idx < childConfigList.size(); ++idx) {
                    final Object childView = childConfigList.get(idx).get("CHILDVIEWNAME");
                    childViewNames.add(childView);
                }
                personalizeTabForUser(account_id, viewName, childViewNames);
                tabChildDo = pers.get("ACUserTabChildConfig", tabChildRow);
            }
            tabChildRow = tabChildDo.getRow("ACUserTabChildConfig");
            tabChildRow.set("DISPLAYNAME", (Object)displayName);
            tabChildDo.updateRow(tabChildRow);
            pers.update(tabChildDo);
            model.addCompiledData((Object)("SORTEDTABLIST:" + account_id), (Object)null);
        }
        catch (final Exception e) {
            throw new Exception("Exception occurred while update a row of ACUserTabChildPreference Table: " + e);
        }
    }
    
    public static void setDefaultSelectedViewForTab(final long accountId, Object viewName, Object defaultSelectedView) throws Exception {
        final Persistence persistence = LookUpUtil.getPersistence();
        try {
            if (viewName instanceof Long) {
                viewName = WebViewAPI.getViewName(viewName);
            }
            if (defaultSelectedView == null) {
                final Row userPreferenceRow = new Row("ACUserPreference");
                userPreferenceRow.set(1, (Object)accountId);
                userPreferenceRow.set(2, (Object)(viewName + ":defaultSelectedView"));
                persistence.delete(userPreferenceRow);
            }
            else {
                if (defaultSelectedView instanceof Long) {
                    defaultSelectedView = WebViewAPI.getViewName(defaultSelectedView);
                }
                boolean isViewPresent = false;
                final List<Row> childConfigList = getTabChildConfigForView(viewName);
                for (int idx = 0; idx < childConfigList.size(); ++idx) {
                    Object childViewName = childConfigList.get(idx).get(2);
                    childViewName = WebViewAPI.getViewName(childViewName);
                    if (defaultSelectedView.equals(childViewName)) {
                        isViewPresent = true;
                        break;
                    }
                }
                if (!isViewPresent) {
                    throw new Exception("defaultSelectedView should be present in  child views :");
                }
                Row userPreferenceRow2 = new Row("ACUserPreference");
                userPreferenceRow2.set(1, (Object)accountId);
                userPreferenceRow2.set(2, (Object)(viewName + ":defaultSelectedView"));
                DataObject daob = (DataObject)new WritableDataObject();
                daob = persistence.get("ACUserPreference", userPreferenceRow2);
                if (!daob.isEmpty()) {
                    userPreferenceRow2 = daob.getFirstRow("ACUserPreference");
                    userPreferenceRow2.set(3, defaultSelectedView);
                    daob.updateRow(userPreferenceRow2);
                    persistence.update(daob);
                }
                else {
                    userPreferenceRow2.set(3, defaultSelectedView);
                    daob.addRow(userPreferenceRow2);
                    persistence.add(daob);
                }
            }
            final ViewModel model = (ViewModel)WebViewAPI.getConfigModel(viewName, false);
            model.addCompiledData((Object)("SORTEDTABLIST:" + accountId), (Object)null);
        }
        catch (final Exception e) {
            throw new Exception("Exception occurred while insert/update defaultSelectedView for Tab :" + e);
        }
    }
    
    static {
        TabPersonalizationUtil.log = Logger.getLogger(TabPersonalizationUtil.class.getName());
    }
}
