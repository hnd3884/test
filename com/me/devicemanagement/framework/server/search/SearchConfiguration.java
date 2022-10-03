package com.me.devicemanagement.framework.server.search;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.logging.Logger;
import java.util.HashMap;

public class SearchConfiguration
{
    private static SearchConfiguration configuration;
    private boolean searchEnabled;
    private boolean searchDocsEnabled;
    private boolean searchSettingsEnabled;
    private boolean searchSpellCheckerEnabled;
    private boolean searchDocsFacetEnabled;
    private boolean searchSettingsFacetEnabled;
    private boolean searchScoreEnabled;
    private HashMap<String, Float> boostSettingsMap;
    private HashMap<String, Float> boostDocsMap;
    private static Logger logger;
    
    public SearchConfiguration() throws Exception {
        final HashMap<String, Boolean> advSearchBooleanPropsHashMap = AdvSearchCommonUtil.getAdvSearchBooleanPropsHashMap();
        this.searchEnabled = advSearchBooleanPropsHashMap.get("search.enabled");
        this.searchDocsEnabled = advSearchBooleanPropsHashMap.get("search.documents.enabled");
        this.searchSettingsEnabled = advSearchBooleanPropsHashMap.get("search.settings.enabled");
        this.searchSpellCheckerEnabled = advSearchBooleanPropsHashMap.get("search.spellchecker.enabled");
        this.searchDocsFacetEnabled = advSearchBooleanPropsHashMap.get("search.documents.faceting.enabled");
        this.searchSettingsFacetEnabled = advSearchBooleanPropsHashMap.get("search.settings.faceting.enabled");
        this.searchScoreEnabled = advSearchBooleanPropsHashMap.get("search.score.enabled");
        this.setBoostMapForSettings();
        this.setBoostMapForDocs();
    }
    
    public static synchronized SearchConfiguration getConfiguration() throws Exception {
        if (SearchConfiguration.configuration == null) {
            SearchConfiguration.configuration = new SearchConfiguration();
        }
        return SearchConfiguration.configuration;
    }
    
    public boolean isSearchEnabled() {
        return this.searchEnabled;
    }
    
    public boolean isSearchDocsEnabled() {
        return this.searchDocsEnabled;
    }
    
    public boolean isSearchSettingsEnabled() {
        return this.searchSettingsEnabled;
    }
    
    public boolean isSearchSpellCheckerEnabled() {
        return this.searchSpellCheckerEnabled;
    }
    
    public boolean isSearchDocsFacetEnabled() {
        return this.searchDocsFacetEnabled;
    }
    
    public boolean isSearchSettingsFacetEnabled() {
        return this.searchSettingsFacetEnabled;
    }
    
    public boolean isSearchScoreEnabled() {
        return this.searchScoreEnabled;
    }
    
    private void setBoostMapForDocs() throws Exception {
        this.boostDocsMap = new HashMap<String, Float>();
        final String searchDocsBoostFileName = AdvSearchCommonUtil.SEARCH_FILES_HOME + File.separator + "search-documents-boost.properties";
        final Properties searchDocsBoostProperties = FileAccessUtil.readProperties(searchDocsBoostFileName);
        if (searchDocsBoostProperties != null && !searchDocsBoostProperties.isEmpty()) {
            this.boostDocsMap = this.convertPropertyToCustomHashMap(searchDocsBoostProperties);
        }
        else {
            this.boostDocsMap.put("title", 1.0f);
            this.boostDocsMap.put("description", 1.0f);
            this.boostDocsMap.put("content", 1.0f);
        }
    }
    
    private void setBoostMapForSettings() throws Exception {
        this.boostSettingsMap = new HashMap<String, Float>();
        final String searchSettingsBoostFileName = AdvSearchCommonUtil.SEARCH_FILES_HOME + File.separator + "search-settings-boost.properties";
        final Properties searchSettingsBoostProperties = FileAccessUtil.readProperties(searchSettingsBoostFileName);
        if (searchSettingsBoostProperties != null && !searchSettingsBoostProperties.isEmpty()) {
            this.boostSettingsMap = this.convertPropertyToCustomHashMap(searchSettingsBoostProperties);
        }
        else {
            this.boostSettingsMap.put("title", 1.0f);
            this.boostSettingsMap.put("content", 1.0f);
            this.boostSettingsMap.put("additionalkeywords", 1.0f);
        }
    }
    
    private HashMap<String, Float> convertPropertyToCustomHashMap(final Properties props) throws Exception {
        final HashMap<String, Float> boostMap = new HashMap<String, Float>();
        final Enumeration<Object> keyEnumerator = ((Hashtable<Object, V>)props).keys();
        while (keyEnumerator.hasMoreElements()) {
            final String s = keyEnumerator.nextElement();
            boostMap.put(s, Float.valueOf(props.getProperty(s)));
        }
        return boostMap;
    }
    
    public HashMap<String, Float> getBoostSettingsMap() {
        return this.boostSettingsMap;
    }
    
    public HashMap<String, Float> getBoostDocsMap() {
        return this.boostDocsMap;
    }
    
    public static void updateSearchConfiguration(final String message) throws Exception {
        SearchConfiguration.configuration = new SearchConfiguration();
        if (SearchConfiguration.configuration.searchEnabled) {
            AdvSearchLogger.getInstance().printVersion(message);
        }
    }
    
    static {
        SearchConfiguration.logger = Logger.getLogger(SearchConfiguration.class.getName());
    }
}
