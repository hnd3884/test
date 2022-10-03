package com.adventnet.client.view;

import com.zoho.conf.Configuration;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.themes.web.ThemesAPI;
import com.adventnet.client.cache.web.ClientDataObjectCache;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.util.StaticLists;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.cache.StaticCache;
import java.util.Map;
import com.adventnet.client.view.web.WebViewAPI;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;

public class UserPersonalizationAPI implements WebConstants
{
    private static final Logger LOGGER;
    private static boolean isPersonalizationRestricted;
    public static final String CUSTOMIZETYPE_NO = "NO";
    public static final String CUSTOMIZETYPE_PERSONALIZE = "PERSONALIZE";
    public static final String CUSTOMIZETYPE_UPDATE = "UPDATE";
    
    public static String getPersonalizedViewName(final Object viewName, final long accountId) {
        final Map compPersMapping = getMappings(accountId)[0];
        if (viewName instanceof String) {
            final Long mappedConfig = compPersMapping.get(WebViewAPI.getViewNameNo(viewName));
            return WebViewAPI.getViewName(mappedConfig);
        }
        final Long mappedConfig = compPersMapping.get(viewName);
        return WebViewAPI.getViewName(mappedConfig);
    }
    
    public static Long getPersonalizedViewNameNo(final Object viewName, final long accountId) {
        final Map compPersMapping = getMappings(accountId)[0];
        if (viewName instanceof String) {
            return compPersMapping.get(WebViewAPI.getViewNameNo(viewName));
        }
        return compPersMapping.get(viewName);
    }
    
    public static String getOriginalViewName(final Object viewName, final long accountId) {
        final Map persCompMapping = getMappings(accountId)[1];
        if (viewName instanceof String) {
            final Long origConfig = persCompMapping.get(WebViewAPI.getViewNameNo(viewName));
            return WebViewAPI.getViewName(origConfig);
        }
        final Long origConfig = persCompMapping.get(viewName);
        return WebViewAPI.getViewName(origConfig);
    }
    
    public static Long getOriginalViewNameNo(final Object viewName, final long accountId) {
        final Map persCompMapping = getMappings(accountId)[1];
        if (viewName instanceof String) {
            return persCompMapping.get(WebViewAPI.getViewNameNo(viewName));
        }
        return persCompMapping.get(viewName);
    }
    
    private static Map[] getMappings(final long accountId) {
        final String key = "PERSONALIZED_VIEW_MAPPING_" + accountId;
        Map[] mappings = (Map[])StaticCache.getFromCache(key);
        if (mappings == null) {
            try {
                final Criteria cr = new Criteria(new Column("PersonalizedViewMap", "ACCOUNT_ID"), (Object)new Long(accountId), 0);
                final DataObject dob = LookUpUtil.getPersistence().get("PersonalizedViewMap", cr);
                final HashMap<Long, Long> compPersMapping = new HashMap<Long, Long>();
                final HashMap<Long, Long> persCompMapping = new HashMap<Long, Long>();
                if (dob.containsTable("PersonalizedViewMap")) {
                    final Iterator<Row> ite = dob.getRows("PersonalizedViewMap");
                    while (ite.hasNext()) {
                        final Row r = ite.next();
                        final Long origviewname_no = (Long)r.get(1);
                        final Long persviewname_no = (Long)r.get(3);
                        compPersMapping.put(origviewname_no, persviewname_no);
                        persCompMapping.put(persviewname_no, origviewname_no);
                    }
                }
                mappings = new Map[] { compPersMapping, persCompMapping };
                StaticCache.addToCache(key, mappings, StaticLists.PERSVIEWMAP);
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return mappings;
    }
    
    public static List<String> getPersonalizedViewNames(final String origViewName) {
        final String key = "PERSONALIZED_VIEW_MAPPING_" + origViewName;
        List<String> persviewnames = (List<String>)StaticCache.getFromCache(key);
        if (persviewnames == null) {
            try {
                final Criteria cr = new Criteria(new Column("PersonalizedViewMap", "ORIGVIEWNAME"), (Object)WebViewAPI.getViewNameNo(origViewName), 0);
                final DataObject dob = LookUpUtil.getPersistence().get("PersonalizedViewMap", cr);
                if (dob.containsTable("PersonalizedViewMap")) {
                    persviewnames = new ArrayList<String>();
                    final Iterator<Row> ite = dob.getRows("PersonalizedViewMap");
                    while (ite.hasNext()) {
                        final Row r = ite.next();
                        final Long persviewname_no = (Long)r.get(3);
                        persviewnames.add(WebViewAPI.getViewName(persviewname_no));
                    }
                    StaticCache.addToCache(key, persviewnames, StaticLists.PERSVIEWMAP);
                }
                return persviewnames;
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return persviewnames;
    }
    
    public static boolean isPersonalizeOptionSet(final Object viewName) throws Exception {
        return !UserPersonalizationAPI.isPersonalizationRestricted && "PERSONALIZE".equals(WebViewAPI.getViewConfiguration(viewName).getFirstValue("ViewConfiguration", "CUSTOMIZETYPE"));
    }
    
    public static String getPersonalizedConfigName(final String origViewName, final long accountId) throws Exception {
        return isPersonalizeOptionSet(origViewName) ? (origViewName + "_PERSVIEW_" + accountId) : origViewName;
    }
    
    public static DataObject getPersonalizedView(final Object viewName, final long accountId) throws Exception {
        if (!isPersonalizeOptionSet(viewName)) {
            return WebViewAPI.getViewConfiguration(viewName);
        }
        final String personalizedViewName = getPersonalizedViewName(viewName, accountId);
        if (personalizedViewName == null) {
            return createPersonalizedView((WritableDataObject)WebViewAPI.getViewConfiguration(viewName), accountId);
        }
        return WebViewAPI.getViewConfiguration(personalizedViewName);
    }
    
    private static DataObject createPersonalizedView(WritableDataObject dao, final long accountId) throws Exception {
        final String viewName = (String)dao.getFirstValue("ViewConfiguration", 2);
        final String persViewName = getPersonalizedConfigName(viewName, accountId);
        dao = (WritableDataObject)PersistenceUtil.constructDO((DataObject)dao);
        final Row viewrow = dao.getFirstRow("ViewConfiguration");
        viewrow.set(2, (Object)persViewName);
        dao.updateRow(viewrow);
        final Row r = new Row("PersonalizedViewMap");
        r.set(1, (Object)WebViewAPI.getViewNameNo(viewName));
        r.set(2, (Object)new Long(accountId));
        r.set(3, dao.getFirstValue("ViewConfiguration", 1));
        dao.addRow(r);
        if (dao.containsTable("ACViewToGroupMapping")) {
            dao.deleteRows("ACViewToGroupMapping", (Row)null);
        }
        dao.clearOperations();
        return (DataObject)dao;
    }
    
    public static void updatePersonalizedView(final WritableDataObject personalizedView, final long accountId) throws Exception {
        final String viewName = (String)personalizedView.getFirstValue("ViewConfiguration", 2);
        final DataObject oldDO = WebViewAPI.getViewConfiguration(viewName);
        final String origViewName = getOriginalViewName(viewName, accountId);
        if (!oldDO.containsTable("ViewConfiguration") && origViewName == null) {
            if (!personalizedView.containsTable("PersonalizedViewMap")) {
                throw new RuntimeException("PersonalizedViewMap Table is not present and hence the view could not be updated.");
            }
            personalizedView.clearOperations();
            LookUpUtil.getPersistence().add((DataObject)personalizedView);
        }
        else {
            LookUpUtil.getPersistence().update((DataObject)personalizedView);
        }
        ClientDataObjectCache.clearCacheForView(viewName);
    }
    
    public static void removePersonalizedView(final String viewName, final long accountId) throws Exception {
        final Row r = new Row("PersonalizedViewMap");
        r.set("ORIGVIEWNAME", (Object)WebViewAPI.getViewNameNo(viewName));
        r.set("ACCOUNT_ID", (Object)new Long(accountId));
        LookUpUtil.getPersistence().delete(r);
    }
    
    public static String getUserPreference(final String prefName, final long accountId) throws Exception {
        final Row userPrefRow = new Row("ACUserPreference");
        userPrefRow.set(1, (Object)new Long(accountId));
        userPrefRow.set(2, (Object)prefName);
        final DataObject dao = LookUpUtil.getUserPersistence().get("ACUserPreference", userPrefRow);
        if (!dao.isEmpty()) {
            return (String)dao.getFirstValue("ACUserPreference", "VALUE");
        }
        return null;
    }
    
    public static void setUserPreference(final String prefName, final long accountId, final String value) throws Exception {
        final Row prefRow = new Row("ACUserPreference");
        prefRow.set(1, (Object)new Long(accountId));
        prefRow.set(2, (Object)prefName);
        final DataObject dao = LookUpUtil.getUserPersistence().get("ACUserPreference", prefRow);
        Row r = dao.getRow("ACUserPreference");
        if (r == null) {
            r = new Row("ACUserPreference");
            r.set("ACCOUNT_ID", (Object)new Long(accountId));
            r.set("PREFNAME", (Object)prefName);
            r.set("VALUE", (Object)value);
            dao.addRow(r);
        }
        else {
            r.set("VALUE", (Object)value);
            dao.updateRow(r);
        }
        LookUpUtil.getUserPersistence().update(dao);
    }
    
    public static void setClientModTimeForUser(final long accountId, final long time) throws Exception {
        final Row r = new Row("ACUserClientState");
        r.set(1, (Object)new Long(accountId));
        final DataObject dao = LookUpUtil.getUserPersistence().get("ACUserClientState", r);
        Row modRow = dao.getRow("ACUserClientState");
        if (modRow == null) {
            modRow = r;
            dao.addRow(modRow);
        }
        r.set(3, (Object)new Long(time));
        LookUpUtil.getUserPersistence().update(dao);
    }
    
    public static void changeThemeForAccount(final String themeName, final long accountId) {
        try {
            Row row = new Row("ACUserClientState");
            row.set(1, (Object)new Long(accountId));
            final DataObject selectedThemeDO = LookUpUtil.getUserPersistence().get("ACUserClientState", row);
            final Row r = selectedThemeDO.getRow("ACUserClientState");
            if (r != null) {
                row = r;
            }
            row.set(2, (Object)ThemesAPI.getThemeNameNo(themeName));
            row.set(3, (Object)new Long(System.currentTimeMillis()));
            if (r == null) {
                selectedThemeDO.addRow(row);
            }
            else {
                selectedThemeDO.updateRow(row);
            }
            LookUpUtil.getUserPersistence().update(selectedThemeDO);
        }
        catch (final Exception e) {
            throw new RuntimeException("Exception while updating theme for account", e);
        }
        StaticCache.addToCache("SELTHEME_" + accountId, themeName);
    }
    
    public static boolean isPersonalizeEnabled(final HttpServletRequest request) {
        return "TRUE".equals(request.getSession().getAttribute("PERSONALIZE"));
    }
    
    public static void toggleViewPersonalization() {
        if (Configuration.getBoolean("development.mode", "false")) {
            UserPersonalizationAPI.isPersonalizationRestricted = !UserPersonalizationAPI.isPersonalizationRestricted;
        }
        else {
            UserPersonalizationAPI.LOGGER.warning("View Personalization can be toggled only in development mode");
        }
    }
    
    public static boolean isViewPersonalizationDisabled() {
        return UserPersonalizationAPI.isPersonalizationRestricted;
    }
    
    static {
        LOGGER = Logger.getLogger(UserPersonalizationAPI.class.getName());
        UserPersonalizationAPI.isPersonalizationRestricted = false;
    }
}
