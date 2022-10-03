package com.me.devicemanagement.framework.server.search;

import java.util.Hashtable;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.facet.LabelAndValue;
import org.apache.lucene.facet.FacetResult;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.index.Fields;
import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.TokenSources;
import java.util.Collection;
import java.util.Arrays;
import org.json.JSONArray;
import org.apache.lucene.facet.Facets;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesReaderState;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetCounts;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import java.util.Map;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.facet.FacetsCollector;
import org.apache.lucene.facet.sortedset.DefaultSortedSetDocValuesReaderState;
import org.apache.lucene.facet.DrillDownQuery;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.analysis.Analyzer;
import java.util.Locale;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import java.util.logging.Level;
import java.io.File;
import java.util.logging.Logger;

public class CompleteSearchUtil
{
    public static final String SERVER_HOME;
    private static Logger logger;
    private static Logger log;
    private static Logger advSearchErrorLogger;
    private static CompleteSearchUtil completeSearch;
    
    public static synchronized CompleteSearchUtil getInstance() {
        if (CompleteSearchUtil.completeSearch == null) {
            CompleteSearchUtil.completeSearch = new CompleteSearchUtil();
        }
        return CompleteSearchUtil.completeSearch;
    }
    
    public static String getDocMainIndexDir() throws Exception {
        String docMainIndexDir = null;
        try {
            final JSONObject jsonObject = getMainIndexDirJson();
            docMainIndexDir = AdvSearchCommonUtil.doc_index_dir + File.separator + jsonObject.getString("docMainIndex");
        }
        catch (final Exception ex) {
            CompleteSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "CompleteSearchUtil : Exception occurred - getDocMainIndexDir() :  ", ex);
        }
        return docMainIndexDir;
    }
    
    public static String getStaticActionMainIndexDir() throws Exception {
        String staticActionMainIndexDir = null;
        try {
            final JSONObject jsonObject = getMainIndexDirJson();
            staticActionMainIndexDir = AdvSearchCommonUtil.static_action_index_dir + File.separator + jsonObject.getString("staticMainIndex");
        }
        catch (final Exception ex) {
            CompleteSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "CompleteSearchUtil : Exception occurred - getStaticActionMainIndexDir() :  ", ex);
        }
        return staticActionMainIndexDir;
    }
    
    public static JSONObject getMainIndexDirJson() throws Exception {
        final String fileName = AdvSearchCommonUtil.search_main_index_file_name;
        return getJsonObjectFromFile(fileName);
    }
    
    public static JSONObject getJsonObjectFromFile(final String fileName) throws IOException {
        JSONObject jsonObject = null;
        InputStream inputStream = null;
        try {
            if (ApiFactoryProvider.getFileAccessAPI().isFileExists(fileName)) {
                inputStream = FileAccessUtil.getFileAsInputStream(fileName);
                final String jsonStr = IOUtils.toString(inputStream);
                jsonObject = new JSONObject(jsonStr);
            }
            else {
                CompleteSearchUtil.advSearchErrorLogger.log(Level.WARNING, "CompleteSearchUtil : getJsonObjectFromFile() :  File Not Found: " + fileName);
            }
        }
        catch (final Exception ex) {
            CompleteSearchUtil.advSearchErrorLogger.log(Level.SEVERE, "CompleteSearchUtil : Exception occurred - getJsonObjectFromFile() :  ", ex);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonObject;
    }
    
    public static JSONObject searchData(final Properties searchQueryProps, final TreeMap<String, Long> authorizedRoleMap, final String searchType) throws Exception {
        Directory indexDirectory = null;
        String[] fieldsToSearch = null;
        HashMap<String, Float> boostMap = new HashMap<String, Float>();
        ArrayList<String> resultFields = new ArrayList<String>();
        final Long loginID = Long.valueOf(String.valueOf(((Hashtable<K, Object>)searchQueryProps).get("LOGIN_ID")));
        final Locale userlocale = I18NUtil.getUserLocaleFromDB(loginID);
        Analyzer searchAnalyzer = null;
        final SearchConfiguration configuration = SearchConfiguration.getConfiguration();
        if (searchType.equalsIgnoreCase("docs")) {
            indexDirectory = (Directory)FSDirectory.open(Paths.get(getDocMainIndexDir(), new String[0]));
            fieldsToSearch = getDocsSearchFields();
            boostMap = configuration.getBoostDocsMap();
            resultFields = getDocsResultsFields();
            searchAnalyzer = AdvSearchCommonUtil.getAnalyzer("en_US");
        }
        else if (searchType.equalsIgnoreCase("sett")) {
            indexDirectory = (Directory)FSDirectory.open(Paths.get(getStaticActionMainIndexDir() + File.separator + userlocale.toString(), new String[0]));
            fieldsToSearch = getSettingsSearchFields();
            boostMap = configuration.getBoostSettingsMap();
            resultFields = getSettingsResultsFields();
            searchAnalyzer = AdvSearchCommonUtil.getAnalyzer(userlocale.toString());
        }
        final JSONObject searchData = searchIndexWithQueryParser(indexDirectory, searchQueryProps, fieldsToSearch, boostMap, resultFields, authorizedRoleMap, searchAnalyzer);
        return searchData;
    }
    
    public static JSONObject searchIndexWithQueryParser(final Directory indexDirectory, final Properties searchStringProps, final String[] fieldsToSearch, final HashMap<String, Float> boostMap, final ArrayList<String> resultFields, final TreeMap<String, Long> authorizedRoleMap, final Analyzer searchAnalyzer) throws Exception {
        final IndexReader reader = (IndexReader)DirectoryReader.open(indexDirectory);
        final IndexSearcher indexSearcher = new IndexSearcher(reader);
        final FacetsConfig fcConfig = new FacetsConfig();
        fcConfig.setIndexFieldName("selectedTabFilter", "TabFilter");
        final DrillDownQuery facetDrillDownQuery = new DrillDownQuery(fcConfig);
        final SortedSetDocValuesReaderState state = (SortedSetDocValuesReaderState)new DefaultSortedSetDocValuesReaderState(reader, "TabFilter");
        final FacetsCollector fc = new FacetsCollector();
        final Sort sort = new Sort(new SortField("selectedTabFilter", SortField.Type.STRING, false));
        final String searchString = String.valueOf(searchStringProps.getProperty("q"));
        final String selectedCategory = String.valueOf(searchStringProps.getProperty("category"));
        final Boolean searchFacetField = Boolean.valueOf(String.valueOf(((Hashtable<K, Object>)searchStringProps).get("searchFacets")));
        final MultiFieldQueryParser multiqueryParser = new MultiFieldQueryParser(fieldsToSearch, searchAnalyzer, (Map)boostMap);
        multiqueryParser.setDefaultOperator(getConfiguredParserOperator());
        final Query query = multiqueryParser.parse(MultiFieldQueryParser.escape(searchString));
        CompleteSearchUtil.logger.log(Level.FINE, "Type of query: " + query.getClass().getSimpleName());
        CompleteSearchUtil.logger.log(Level.FINE, "Query Construction is: " + query.toString());
        final BooleanQuery.Builder rootBuilder = new BooleanQuery.Builder();
        rootBuilder.add(query, BooleanClause.Occur.MUST);
        if (selectedCategory != null && !selectedCategory.isEmpty() && searchFacetField) {
            final String[] split;
            final String[] selectedCategoryArray = split = selectedCategory.split(",");
            for (final String selCatdata : split) {
                facetDrillDownQuery.add("selectedTabFilter", new String[] { selCatdata });
            }
            rootBuilder.add((Query)facetDrillDownQuery, BooleanClause.Occur.FILTER);
        }
        final Query searchQuery = (Query)rootBuilder.build();
        CompleteSearchUtil.logger.log(Level.FINE, "Final Query Construction is: " + searchQuery.toString());
        final TopDocs hits = FacetsCollector.search(indexSearcher, searchQuery, 1000, (Collector)fc);
        final Facets facets = (Facets)new SortedSetDocValuesFacetCounts(state, fc);
        final SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
        final Highlighter highlighter = new Highlighter((Formatter)htmlFormatter, (Scorer)new QueryScorer(query));
        final JSONObject searchResultJson = processSearchResults(hits, indexSearcher, multiqueryParser, highlighter, resultFields, facets, authorizedRoleMap, searchStringProps, query, searchAnalyzer);
        reader.close();
        return searchResultJson;
    }
    
    public static JSONObject processSearchResults(final TopDocs topDocs, final IndexSearcher searcher, final MultiFieldQueryParser multiqueryParser, final Highlighter highlighter, final ArrayList resultFields, final Facets facets, final TreeMap<String, Long> authorizedRoleMap, final Properties searchStringProps, final Query query, final Analyzer searchAnalyzer) throws CorruptIndexException, Exception {
        final JSONArray searchDataArray = new JSONArray();
        JSONArray finalSearchDataArray = new JSONArray();
        final JSONArray searchSelectedTabArray = new JSONArray();
        final JSONArray searchSelectedTabTreeArray = new JSONArray();
        final JSONArray searchOtherArray = new JSONArray();
        final JSONObject searchDataJson = new JSONObject();
        JSONArray searchFacetData = new JSONArray();
        JSONArray searchSpellerData = new JSONArray();
        final ScoreDoc[] hits = topDocs.scoreDocs;
        final String querySelectedTab = searchStringProps.getProperty("selectedTab");
        final String querySelectedTreeEle = searchStringProps.getProperty("selectedTreeElem");
        final Boolean searchSpellCheckerEnabled = Boolean.valueOf(String.valueOf(((Hashtable<K, Object>)searchStringProps).get("search.spellchecker.enabled")));
        final Boolean searchScoreEnabled = Boolean.valueOf(String.valueOf(((Hashtable<K, Object>)searchStringProps).get("search.score.enabled")));
        Boolean isSettingsSearch = Boolean.FALSE;
        if (String.valueOf(resultFields.get(5)).equalsIgnoreCase("Roles")) {
            isSettingsSearch = Boolean.TRUE;
        }
        CompleteSearchUtil.logger.log(Level.FINE, "Search Results Found Count : " + hits.length);
        for (int i = 0; i < hits.length; ++i) {
            Boolean checkUrlLinkToBeShown = Boolean.TRUE;
            String highLightedDataContent = "";
            String highLightedTitle = "";
            final ScoreDoc documentNode = hits[i];
            final int docId = documentNode.doc;
            final Document d = searcher.doc(docId);
            final JSONObject searchData = new JSONObject();
            Boolean isAllowedSettingForLicense = Boolean.TRUE;
            Boolean isAllowedDBName = Boolean.TRUE;
            if (isSettingsSearch) {
                final String licenseType = d.get(String.valueOf(resultFields.get(7)));
                final String licenseValue = d.get(String.valueOf(resultFields.get(8)));
                final String dbCheckType = d.get(String.valueOf(resultFields.get(9)));
                final String dbCheckValue = d.get(String.valueOf(resultFields.get(10)));
                if (licenseType != null && !licenseType.isEmpty()) {
                    ArrayList<String> licenseTypeList = new ArrayList<String>();
                    if (licenseValue != null && !licenseValue.isEmpty()) {
                        licenseTypeList = new ArrayList<String>(Arrays.asList(licenseValue.split(",")));
                    }
                    isAllowedSettingForLicense = getLicenseVerificationForSetting(licenseType, licenseTypeList);
                }
                if (dbCheckType != null && !dbCheckType.isEmpty()) {
                    ArrayList<String> dbCheckValueList = new ArrayList<String>();
                    if (dbCheckValue != null && !dbCheckValue.isEmpty()) {
                        dbCheckValueList = new ArrayList<String>(Arrays.asList(dbCheckValue.split(",")));
                    }
                    isAllowedDBName = getDatabaseVerificationForSetting(dbCheckType, dbCheckValueList);
                }
            }
            if (isAllowedSettingForLicense && isAllowedDBName) {
                final String title = d.get(String.valueOf(resultFields.get(0)));
                final String content = d.get(String.valueOf(resultFields.get(1)));
                String urllink = d.get(String.valueOf(resultFields.get(2)));
                final String selectedTab = d.get(String.valueOf(resultFields.get(3)));
                final String selectedTreeEle = d.get(String.valueOf(resultFields.get(4)));
                final String description = "";
                String rolesMapped = "";
                String categoryValue = "";
                final TokenStream descriptionTokenStream = null;
                TokenStream contentTokenStream = null;
                List<String> rolesMappedForSettings = null;
                final Fields vectors = searcher.getIndexReader().getTermVectors(docId);
                final int maxStartOffset = highlighter.getMaxDocCharsToAnalyze() - 1;
                final TokenStream titleTokenStream = TokenSources.getTokenStream(String.valueOf(resultFields.get(0)), vectors, title, searchAnalyzer, maxStartOffset);
                highLightedTitle = highlighter.getBestFragments(titleTokenStream, title, 4, " ").replaceAll("[\\t\\n\\r]", " ").replaceAll("^ +| +$|( )+", "$1");
                if (highLightedTitle.isEmpty() || ((Hashtable<K, Boolean>)searchStringProps).get("isapi")) {
                    highLightedTitle = title.replaceAll("[\\t\\n\\r]", " ").replaceAll("^ +| +$|( )+", "$1");
                }
                contentTokenStream = TokenSources.getTokenStream(String.valueOf(resultFields.get(1)), vectors, content, searchAnalyzer, maxStartOffset);
                highLightedDataContent = highlighter.getBestFragments(contentTokenStream, content, 8, " ").replaceAll("[\\t\\n\\r]", " ").replaceAll("^ +| +$|( )+", "$1");
                if (highLightedDataContent.isEmpty() || ((Hashtable<K, Boolean>)searchStringProps).get("isapi")) {
                    highLightedDataContent = content.replaceAll("[\\t\\n\\r]", " ").replaceAll("^ +| +$|( )+", "$1");
                }
                if (isSettingsSearch) {
                    rolesMapped = d.get(String.valueOf(resultFields.get(5)));
                    if (rolesMapped != null && !rolesMapped.isEmpty()) {
                        rolesMappedForSettings = new ArrayList<String>(Arrays.asList(rolesMapped.split(",")));
                        checkUrlLinkToBeShown = checkRolesForSettingsToBeShown(authorizedRoleMap, rolesMappedForSettings);
                    }
                    if (!checkUrlLinkToBeShown) {
                        urllink = "";
                    }
                    categoryValue = d.get(String.valueOf(resultFields.get(6)));
                    searchData.put("category", (Object)categoryValue);
                }
                searchData.put("normaltitle", (Object)title);
                searchData.put("title", (Object)highLightedTitle);
                searchData.put("description", (Object)highLightedDataContent.substring(0, (highLightedDataContent.length() < 250) ? highLightedDataContent.length() : 250));
                searchData.put("urllink", (Object)urllink);
                if (searchScoreEnabled) {
                    searchData.put("score", documentNode.score);
                    final Explanation docExplain = searcher.explain(query, docId);
                    searchData.put("scoreexplanation", (Object)docExplain.toHtml());
                }
                if (selectedTab != null && selectedTreeEle != null && !selectedTab.isEmpty() && !selectedTreeEle.isEmpty() && selectedTab.equalsIgnoreCase(querySelectedTab) && selectedTreeEle.equalsIgnoreCase(querySelectedTreeEle)) {
                    searchSelectedTabTreeArray.put((Object)searchData);
                }
                else if (selectedTab != null && !selectedTab.isEmpty() && selectedTab.equalsIgnoreCase(querySelectedTab)) {
                    searchSelectedTabArray.put((Object)searchData);
                }
                else {
                    searchOtherArray.put((Object)searchData);
                }
            }
        }
        if (searchSelectedTabTreeArray.length() > 0 && searchSelectedTabTreeArray.length() > 0) {
            for (int i = 0; i < searchSelectedTabTreeArray.length(); ++i) {
                searchDataArray.put(searchSelectedTabTreeArray.get(i));
            }
        }
        if (searchSelectedTabArray.length() > 0 && searchSelectedTabArray.length() > 0) {
            for (int i = 0; i < searchSelectedTabArray.length(); ++i) {
                searchDataArray.put(searchSelectedTabArray.get(i));
            }
        }
        if (searchOtherArray.length() > 0 && searchOtherArray.length() > 0) {
            for (int i = 0; i < searchOtherArray.length(); ++i) {
                searchDataArray.put(searchOtherArray.get(i));
            }
        }
        if (isSettingsSearch) {
            finalSearchDataArray = filterForDuplicates(searchDataArray);
        }
        else {
            finalSearchDataArray = searchDataArray;
        }
        searchFacetData = getFacetResultForFilter(facets, "selectedTabFilter");
        if (searchSpellCheckerEnabled) {
            searchSpellerData = getSpellerResultDataForQuery(multiqueryParser, searcher, searchStringProps);
        }
        searchDataJson.put("total", finalSearchDataArray.length());
        searchDataJson.put("searchdata", (Object)finalSearchDataArray);
        searchDataJson.put("facetdata", (Object)searchFacetData);
        searchDataJson.put("suggestdata", (Object)searchSpellerData);
        CompleteSearchUtil.logger.log(Level.FINE, "Search Data is:  " + searchDataJson.toString());
        return searchDataJson;
    }
    
    public static JSONArray filterForDuplicates(final JSONArray searchDataJsonArray) throws Exception {
        final JSONArray resultSearchDataArray = new JSONArray();
        final LinkedHashMap<String, ArrayList<HashMap<String, String>>> titleURLMap = new LinkedHashMap<String, ArrayList<HashMap<String, String>>>();
        for (int i = 0; i < searchDataJsonArray.length(); ++i) {
            final JSONObject searchJSONObj = searchDataJsonArray.getJSONObject(i);
            final String title = String.valueOf(searchJSONObj.get("normaltitle"));
            final String urlLink = String.valueOf(searchJSONObj.get("urllink"));
            Boolean urlLinkAvailable = Boolean.FALSE;
            if (urlLink != null && !urlLink.isEmpty()) {
                urlLinkAvailable = Boolean.TRUE;
            }
            if (!titleURLMap.containsKey(title)) {
                final HashMap<String, String> crossCheckDataValue = new HashMap<String, String>();
                crossCheckDataValue.put("arrayIndex", String.valueOf(i));
                crossCheckDataValue.put("urlLinkAvailable", String.valueOf(urlLinkAvailable));
                crossCheckDataValue.put("removeData", String.valueOf(Boolean.FALSE));
                final ArrayList<HashMap<String, String>> checkDataArrayList = new ArrayList<HashMap<String, String>>();
                checkDataArrayList.add(crossCheckDataValue);
                titleURLMap.put(title, checkDataArrayList);
            }
            else if (titleURLMap.containsKey(title)) {
                final HashMap<String, String> crossCheckDataValue = new HashMap<String, String>();
                crossCheckDataValue.put("arrayIndex", String.valueOf(i));
                crossCheckDataValue.put("urlLinkAvailable", String.valueOf(urlLinkAvailable));
                final ArrayList<HashMap<String, String>> checkDataArrayList = titleURLMap.get(title);
                final Iterator<HashMap<String, String>> iterator = checkDataArrayList.iterator();
                if (iterator.hasNext()) {
                    final HashMap<String, String> singleDataValue = iterator.next();
                    final Boolean urlLinkAvailableForExistingData = Boolean.valueOf(singleDataValue.get("urlLinkAvailable"));
                    if (!urlLinkAvailableForExistingData) {
                        if (urlLinkAvailable) {
                            singleDataValue.put("removeData", String.valueOf(Boolean.TRUE));
                            crossCheckDataValue.put("removeData", String.valueOf(Boolean.FALSE));
                        }
                        else {
                            crossCheckDataValue.put("removeData", String.valueOf(Boolean.TRUE));
                        }
                    }
                    else {
                        crossCheckDataValue.put("removeData", String.valueOf(Boolean.TRUE));
                    }
                }
                checkDataArrayList.add(crossCheckDataValue);
                titleURLMap.put(title, checkDataArrayList);
            }
        }
        for (int i = 0; i < searchDataJsonArray.length(); ++i) {
            final JSONObject searchJSONObj = searchDataJsonArray.getJSONObject(i);
            final String title = String.valueOf(searchJSONObj.get("normaltitle"));
            final ArrayList<HashMap<String, String>> checkDataArrayList2 = titleURLMap.get(title);
            for (final HashMap<String, String> singleDataValue2 : checkDataArrayList2) {
                final Integer arrayIndex = Integer.valueOf(singleDataValue2.get("arrayIndex"));
                final Boolean removeData = Boolean.valueOf(singleDataValue2.get("removeData"));
                if (!removeData && i == arrayIndex) {
                    resultSearchDataArray.put((Object)searchJSONObj);
                }
            }
        }
        return resultSearchDataArray;
    }
    
    public static Boolean checkRolesForSettingsToBeShown(final TreeMap<String, Long> authorizedRoleMap, final List<String> rolesMappedForSettings) throws Exception {
        Boolean urlToBeShown = Boolean.FALSE;
        final Iterator<String> iterator = rolesMappedForSettings.iterator();
        while (iterator.hasNext()) {
            final String role = String.valueOf(iterator.next()).trim();
            if (authorizedRoleMap.containsKey(role)) {
                urlToBeShown = Boolean.TRUE;
                break;
            }
        }
        return urlToBeShown;
    }
    
    public static Boolean getDatabaseVerificationForSetting(final String dbCheckStr, final ArrayList<String> dbCheckDataValueList) throws Exception {
        Boolean dbVerificationInfo = Boolean.FALSE;
        final String selectedDB = PersistenceInitializer.getConfigurationValue("DBName");
        if (dbCheckStr.equalsIgnoreCase("allowed")) {
            if (checkContainsEqualsIgnoreCase(dbCheckDataValueList, selectedDB)) {
                dbVerificationInfo = Boolean.TRUE;
            }
        }
        else if (dbCheckStr.equalsIgnoreCase("notallowed")) {
            if (!dbCheckDataValueList.isEmpty()) {
                if (!checkContainsEqualsIgnoreCase(dbCheckDataValueList, selectedDB)) {
                    dbVerificationInfo = Boolean.TRUE;
                }
            }
            else {
                dbVerificationInfo = Boolean.TRUE;
            }
        }
        return dbVerificationInfo;
    }
    
    public static boolean checkContainsEqualsIgnoreCase(final Collection<String> collStr, final String toCheckStr) throws Exception {
        for (final String strData : collStr) {
            if (toCheckStr.equalsIgnoreCase(strData)) {
                return true;
            }
        }
        return false;
    }
    
    public static Boolean getLicenseVerificationForSetting(final String licenseTypeStr, final ArrayList<String> licenseTypeList) throws Exception {
        Boolean licenseVerificationInfo = Boolean.FALSE;
        final String licenseType = LicenseProvider.getInstance().getLicenseType();
        final String productType = LicenseProvider.getInstance().getProductType();
        if (licenseTypeStr.equalsIgnoreCase("allowed")) {
            if (checkContainsEqualsIgnoreCase(licenseTypeList, licenseType) || checkContainsEqualsIgnoreCase(licenseTypeList, productType)) {
                licenseVerificationInfo = Boolean.TRUE;
            }
        }
        else if (licenseTypeStr.equalsIgnoreCase("notallowed")) {
            if (!licenseTypeList.isEmpty()) {
                if (!checkContainsEqualsIgnoreCase(licenseTypeList, licenseType) && !checkContainsEqualsIgnoreCase(licenseTypeList, productType)) {
                    licenseVerificationInfo = Boolean.TRUE;
                }
            }
            else {
                licenseVerificationInfo = Boolean.TRUE;
            }
        }
        return licenseVerificationInfo;
    }
    
    public static JSONArray getFacetResultForFilter(final Facets facets, final String filterCategory) throws Exception {
        final JSONArray searchFacetData = new JSONArray();
        if (facets != null) {
            final FacetResult fcResult = facets.getTopChildren(10, "selectedTabFilter", new String[0]);
            if (fcResult != null) {
                final String fcFilterLabel = fcResult.dim;
                final LabelAndValue[] fcLabelValueResults = fcResult.labelValues;
                CompleteSearchUtil.logger.log(Level.FINE, "Selected Tab Filters Availabe for the Query");
                for (int i = 0; i < fcLabelValueResults.length; ++i) {
                    final JSONObject searchFcData = new JSONObject();
                    final String tabFilterLabel = fcLabelValueResults[i].label;
                    final Long tabFilterValue = fcLabelValueResults[i].value.longValue();
                    CompleteSearchUtil.logger.log(Level.FINE, "Selected Tab Filter Title: " + tabFilterLabel + " Selected Tab Count is: " + tabFilterValue);
                    if (!tabFilterLabel.equalsIgnoreCase("123456789")) {
                        searchFcData.put("label", (Object)tabFilterLabel);
                        searchFcData.put("value", (Object)tabFilterValue);
                        searchFacetData.put((Object)searchFcData);
                    }
                }
            }
        }
        return searchFacetData;
    }
    
    public static JSONArray getSpellerResultDataForQuery(final MultiFieldQueryParser multiFieldQueryParser, final IndexSearcher indexSearcher, final Properties searchStringProps) throws Exception {
        final JSONArray searchSpellerData = new JSONArray();
        final String searchString = String.valueOf(searchStringProps.getProperty("q"));
        final String[] wordForSuggestions = searchString.split(" ");
        final FSDirectory spellDir = FSDirectory.open(Paths.get(AdvSearchCommonUtil.spell_dir, new String[0]));
        final SpellChecker spellChecker = new SpellChecker((Directory)spellDir);
        for (final String queryWord : wordForSuggestions) {
            if (!spellChecker.exist(queryWord) && queryWord != null && !queryWord.isEmpty()) {
                final String[] suggestions = spellChecker.suggestSimilar(queryWord, 20);
                if (suggestions != null && suggestions.length > 0) {
                    for (final String suggestWord : suggestions) {
                        final Query queryForSpeller = multiFieldQueryParser.parse(suggestWord);
                        final TopDocs spellerTopDocs = indexSearcher.search(queryForSpeller, 1000);
                        final ScoreDoc[] spellerScoreDocs = spellerTopDocs.scoreDocs;
                        if (spellerScoreDocs.length > 0) {
                            CompleteSearchUtil.logger.log(Level.INFO, "Did You Mean Suggestions" + suggestWord);
                            searchSpellerData.put((Object)suggestWord);
                        }
                    }
                }
                else {
                    CompleteSearchUtil.logger.log(Level.INFO, "No suggestions found for word:" + queryWord);
                }
            }
        }
        return searchSpellerData;
    }
    
    public static String[] getDocsSearchFields() throws Exception {
        return new String[] { "title", "content", "description", "selectedTabFilter" };
    }
    
    public static String[] getSettingsSearchFields() throws Exception {
        return new String[] { "title", "content", "additionalkeywords", "selectedTabFilter" };
    }
    
    public static ArrayList<String> getCommonResultsFields() throws Exception {
        final ArrayList<String> commonResultsFields = new ArrayList<String>();
        commonResultsFields.add("title");
        commonResultsFields.add("content");
        commonResultsFields.add("urllink");
        commonResultsFields.add("selectedTab");
        commonResultsFields.add("selectedTreeElem");
        return commonResultsFields;
    }
    
    public static ArrayList<String> getDocsResultsFields() throws Exception {
        final ArrayList<String> docsResultsFields = getCommonResultsFields();
        docsResultsFields.add("description");
        return docsResultsFields;
    }
    
    public static ArrayList<String> getSettingsResultsFields() throws Exception {
        final ArrayList<String> settingsResultFields = getCommonResultsFields();
        settingsResultFields.add("Roles");
        settingsResultFields.add("category");
        settingsResultFields.add("licenseType");
        settingsResultFields.add("licenseValue");
        settingsResultFields.add("dbCheckType");
        settingsResultFields.add("dbCheckValue");
        return settingsResultFields;
    }
    
    public static QueryParser.Operator getConfiguredParserOperator() throws Exception {
        final Properties advSearchProperties = AdvSearchCommonUtil.getAdvSearchPropertiesFromFile();
        if (advSearchProperties == null || advSearchProperties.isEmpty()) {
            return QueryParserBase.OR_OPERATOR;
        }
        final String operatorStr = advSearchProperties.getProperty("search.settings.default.operator", String.valueOf(QueryParserBase.OR_OPERATOR));
        if (operatorStr != null && !operatorStr.isEmpty() && operatorStr.equals(QueryParserBase.AND_OPERATOR)) {
            return QueryParserBase.AND_OPERATOR;
        }
        if (operatorStr != null && !operatorStr.isEmpty() && operatorStr.equals(QueryParserBase.OR_OPERATOR)) {
            return QueryParserBase.OR_OPERATOR;
        }
        return QueryParserBase.OR_OPERATOR;
    }
    
    static {
        SERVER_HOME = System.getProperty("server.home");
        CompleteSearchUtil.logger = Logger.getLogger("AdvSearchLogger");
        CompleteSearchUtil.log = Logger.getLogger(CompleteSearchUtil.class.getName());
        CompleteSearchUtil.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
        CompleteSearchUtil.completeSearch = null;
    }
}
