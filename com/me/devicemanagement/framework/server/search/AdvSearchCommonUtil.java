package com.me.devicemanagement.framework.server.search;

import java.lang.reflect.Method;
import java.util.List;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.Properties;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import java.util.logging.Logger;
import org.apache.lucene.analysis.util.CharArraySet;

public class AdvSearchCommonUtil
{
    public static final String SERVER_HOME;
    public static final CharArraySet ENGLISH_CUSTOM_STOP_WORDS_SET;
    public static String search_index_home;
    public static String static_action_index_dir;
    public static String doc_index_dir;
    public static String spell_dir;
    public static final String SEARCH_FILES_HOME;
    public static final String SEARCH_INDEX_FILES_HOME;
    public static final String STATIC_ACTION_FILES_HOME;
    public static final String SPELL_DICTIONARY_HOME;
    public static String search_main_index_file_name;
    private static AdvSearchCommonUtil advSearchCommonUtil;
    private static Logger advSearchErrorLogger;
    public static Boolean isProductInEMS;
    public static String productCode;
    
    public static synchronized AdvSearchCommonUtil getInstance() {
        if (AdvSearchCommonUtil.advSearchCommonUtil == null) {
            AdvSearchCommonUtil.advSearchCommonUtil = new AdvSearchCommonUtil();
        }
        return AdvSearchCommonUtil.advSearchCommonUtil;
    }
    
    public static Analyzer getAnalyzer(final String analyzerStr) throws Exception {
        if (analyzerStr.equalsIgnoreCase("en_US")) {
            return (Analyzer)new EnglishAnalyzer(AdvSearchCommonUtil.ENGLISH_CUSTOM_STOP_WORDS_SET);
        }
        if (analyzerStr.equalsIgnoreCase("pt_BR")) {
            return (Analyzer)new BrazilianAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("zh_CN")) {
            return (Analyzer)new CJKAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("da_DK")) {
            return (Analyzer)new DanishAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("nl_NL")) {
            return (Analyzer)new DutchAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("fi_FI")) {
            return (Analyzer)new FinnishAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("fr_FR")) {
            return (Analyzer)new FrenchAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("de_DE")) {
            return (Analyzer)new GermanAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("it_IT")) {
            return (Analyzer)new ItalianAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("ja_JP")) {
            return (Analyzer)new CJKAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("nb_NO")) {
            return (Analyzer)new NorwegianAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("pl_PL")) {
            return (Analyzer)new StandardAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("pt_PT")) {
            return (Analyzer)new PortugueseAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("ru_RU")) {
            return (Analyzer)new RussianAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("es_ES")) {
            return (Analyzer)new SpanishAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("sv_SE")) {
            return (Analyzer)new SwedishAnalyzer();
        }
        if (analyzerStr.equalsIgnoreCase("tr_TR")) {
            return (Analyzer)new TurkishAnalyzer();
        }
        return (Analyzer)new StandardAnalyzer();
    }
    
    public static Properties getAdvSearchPropertiesFromFile() {
        Properties advSearchProperties = new Properties();
        try {
            final String advSearchPropertyFileName = AdvSearchCommonUtil.SEARCH_FILES_HOME + File.separator + "adv-search.properties";
            advSearchProperties = FileAccessUtil.readProperties(advSearchPropertyFileName);
            if (SyMUtil.isSummaryServer()) {
                advSearchProperties.setProperty("search.enabled", "false");
            }
            return advSearchProperties;
        }
        catch (final Exception ex) {
            AdvSearchCommonUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchCommonUtil : Exception occurred - getAdvSearchPropertiesFromFile() :  ", ex);
            return advSearchProperties;
        }
    }
    
    public static String getAdvSearchIndexDirectory() {
        final Properties advSearchProperties = getAdvSearchPropertiesFromFile();
        final String searchIndexDir = advSearchProperties.getProperty("search.index.dir");
        return searchIndexDir;
    }
    
    public static String validateSearchInput(final String inpStr, final Boolean canBeNullorEmpty) throws Exception {
        if (inpStr != null && !inpStr.isEmpty() && !inpStr.equalsIgnoreCase("null")) {
            return inpStr;
        }
        if (canBeNullorEmpty) {
            return "";
        }
        AdvSearchCommonUtil.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchCommonUtil : validateSearchInput() : Input String is Malformed and contains '' or '...: " + inpStr);
        throw new Exception("Input String is Malformed and contains '' or '...:" + inpStr);
    }
    
    public static HashMap<String, Boolean> getAdvSearchBooleanPropsHashMap() throws Exception {
        final Properties advSearchProperties = getAdvSearchPropertiesFromFile();
        final HashMap<String, Boolean> advSearchPropsHashMap = new HashMap<String, Boolean>();
        Boolean searchEnabled = Boolean.FALSE;
        Boolean searchDocsEnabled = Boolean.FALSE;
        Boolean searchSettingsEnabled = Boolean.FALSE;
        Boolean searchSpellCheckerEnabled = Boolean.FALSE;
        Boolean searchDocsFacetEnabled = Boolean.FALSE;
        Boolean searchSettingsFacetEnabled = Boolean.FALSE;
        Boolean searchScoreEnabled = Boolean.FALSE;
        if (advSearchProperties != null && !advSearchProperties.isEmpty()) {
            searchEnabled = Boolean.valueOf(advSearchProperties.getProperty("search.enabled"));
            searchDocsEnabled = Boolean.valueOf(advSearchProperties.getProperty("search.documents.enabled"));
            searchSettingsEnabled = Boolean.valueOf(advSearchProperties.getProperty("search.settings.enabled"));
            searchSpellCheckerEnabled = Boolean.valueOf(advSearchProperties.getProperty("search.spellchecker.enabled"));
            searchDocsFacetEnabled = Boolean.valueOf(advSearchProperties.getProperty("search.documents.faceting.enabled"));
            searchSettingsFacetEnabled = Boolean.valueOf(advSearchProperties.getProperty("search.settings.faceting.enabled"));
            searchScoreEnabled = Boolean.valueOf(advSearchProperties.getProperty("search.score.enabled"));
        }
        advSearchPropsHashMap.put("search.enabled", searchEnabled);
        advSearchPropsHashMap.put("search.documents.enabled", searchDocsEnabled);
        advSearchPropsHashMap.put("search.settings.enabled", searchSettingsEnabled);
        advSearchPropsHashMap.put("search.spellchecker.enabled", searchSpellCheckerEnabled);
        advSearchPropsHashMap.put("search.documents.faceting.enabled", searchDocsFacetEnabled);
        advSearchPropsHashMap.put("search.settings.faceting.enabled", searchSettingsFacetEnabled);
        advSearchPropsHashMap.put("search.score.enabled", searchScoreEnabled);
        return advSearchPropsHashMap;
    }
    
    public Properties readGeneralProperties(final String genPropsFile, final String productCode) {
        Properties properties = new Properties();
        try {
            properties = readProperties(genPropsFile);
            if (!productCode.isEmpty()) {
                final String fileName = new File(genPropsFile).getName();
                final String dir = new File(genPropsFile).getParentFile().toString();
                final String prodFile = dir + File.separator + productCode.toLowerCase() + "_" + fileName;
                properties.putAll(readProperties(prodFile));
            }
        }
        catch (final Exception ex) {
            AdvSearchCommonUtil.advSearchErrorLogger.log(Level.SEVERE, "Caught exception while reading properties from file: " + genPropsFile, ex);
        }
        return properties;
    }
    
    private static Properties readProperties(final String confFileName) throws Exception {
        final Properties props = new Properties();
        InputStream ism = null;
        try {
            if (new File(confFileName).exists()) {
                ism = new FileInputStream(confFileName);
                props.load(ism);
            }
        }
        catch (final Exception ex) {
            AdvSearchCommonUtil.advSearchErrorLogger.log(Level.SEVERE, "Caught exception while reading properties from file: " + confFileName, ex);
        }
        finally {
            try {
                if (ism != null) {
                    ism.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return props;
    }
    
    static {
        SERVER_HOME = System.getProperty("server.home");
        SEARCH_FILES_HOME = AdvSearchCommonUtil.SERVER_HOME + File.separator + "conf" + File.separator + "Search";
        SEARCH_INDEX_FILES_HOME = AdvSearchCommonUtil.SEARCH_FILES_HOME + File.separator + "indexProperties" + File.separator;
        STATIC_ACTION_FILES_HOME = AdvSearchCommonUtil.SEARCH_FILES_HOME + File.separator + "staticActionFiles" + File.separator;
        SPELL_DICTIONARY_HOME = AdvSearchCommonUtil.SEARCH_FILES_HOME + File.separator + "searchDictionary" + File.separator;
        AdvSearchCommonUtil.advSearchCommonUtil = null;
        AdvSearchCommonUtil.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
        AdvSearchCommonUtil.isProductInEMS = false;
        AdvSearchCommonUtil.productCode = "";
        final CharArraySet stopWordsCustom = StandardAnalyzer.STOP_WORDS_SET;
        final List customStopWords = Arrays.asList("how");
        final CharArraySet customStopSet = new CharArraySet((Collection)customStopWords, false);
        customStopSet.addAll((Collection)stopWordsCustom);
        ENGLISH_CUSTOM_STOP_WORDS_SET = CharArraySet.unmodifiableSet(customStopSet);
        Class c = null;
        try {
            c = Class.forName("com.me.devicemanagement.framework.server.util.EMSProductUtil");
            final Method m = c.getDeclaredMethod("getEMSProductCode", (Class[])null);
            final Method m2 = c.getDeclaredMethod("isEMSFlowSupportedForCurrentProduct", (Class[])null);
            final ArrayList o = (ArrayList)m.invoke(null, (Object[])null);
            AdvSearchCommonUtil.productCode = o.get(0);
            AdvSearchCommonUtil.isProductInEMS = (Boolean)m2.invoke(null, (Object[])null);
            AdvSearchCommonUtil.productCode = AdvSearchCommonUtil.productCode.toLowerCase();
        }
        catch (final ClassNotFoundException e) {
            AdvSearchCommonUtil.advSearchErrorLogger.log(Level.INFO, "Unable to  instantiate EMSProductCode" + e.getMessage());
        }
        catch (final NoSuchMethodException e2) {
            AdvSearchCommonUtil.advSearchErrorLogger.log(Level.INFO, "Unable to  instantiate EMSProductCode" + e2.getMessage());
        }
        catch (final IllegalAccessException e3) {
            AdvSearchCommonUtil.advSearchErrorLogger.log(Level.INFO, "Unable to  instantiate EMSProductCode" + e3.getMessage());
        }
        catch (final InvocationTargetException e4) {
            AdvSearchCommonUtil.advSearchErrorLogger.log(Level.INFO, "Unable to  instantiate EMSProductCode" + e4.getMessage());
        }
        catch (final Exception ex) {
            AdvSearchCommonUtil.advSearchErrorLogger.log(Level.INFO, "Unable to fetch ProductCode" + ex.getMessage());
        }
        if (!AdvSearchCommonUtil.productCode.isEmpty() && AdvSearchCommonUtil.isProductInEMS) {
            AdvSearchCommonUtil.search_index_home = AdvSearchCommonUtil.SERVER_HOME + File.separator + getAdvSearchIndexDirectory() + File.separator + AdvSearchCommonUtil.productCode + "_" + getAdvSearchIndexDirectory();
            AdvSearchCommonUtil.search_main_index_file_name = AdvSearchCommonUtil.SEARCH_FILES_HOME + File.separator + AdvSearchCommonUtil.productCode + "_" + "searchMainIndex" + ".json";
        }
        else {
            AdvSearchCommonUtil.search_index_home = AdvSearchCommonUtil.SERVER_HOME + File.separator + getAdvSearchIndexDirectory();
            AdvSearchCommonUtil.search_main_index_file_name = AdvSearchCommonUtil.SEARCH_FILES_HOME + File.separator + "searchMainIndex" + ".json";
        }
        AdvSearchCommonUtil.static_action_index_dir = AdvSearchCommonUtil.search_index_home + File.separator + "staticactionindex";
        AdvSearchCommonUtil.doc_index_dir = AdvSearchCommonUtil.search_index_home + File.separator + "docindex";
        AdvSearchCommonUtil.spell_dir = AdvSearchCommonUtil.search_index_home + File.separator + "spellindex";
    }
}
