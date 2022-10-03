package com.me.mdm.server.util;

import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import java.util.Iterator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MDMFeatureParamsHandler
{
    private static Logger logger;
    private static final String FEATURE_PARAM_CACHE_NAME = "FEATURE_PARAMS_CACHE";
    private static MDMFeatureParamsHandler mdmFeatureParamsHandler;
    
    public static MDMFeatureParamsHandler getInstance() {
        if (MDMFeatureParamsHandler.mdmFeatureParamsHandler == null) {
            MDMFeatureParamsHandler.mdmFeatureParamsHandler = new MDMFeatureParamsHandler();
        }
        return MDMFeatureParamsHandler.mdmFeatureParamsHandler;
    }
    
    public Boolean isFeatureEnabled(final String featureName) {
        Boolean featureEnabled = null;
        try {
            final HashMap mdmFeatureParamsHash = getMDMFeatureParameters();
            if (mdmFeatureParamsHash.containsKey(featureName) && (mdmFeatureParamsHash.get(featureName).equals(Boolean.TRUE) || mdmFeatureParamsHash.get(featureName).toString().equals("true"))) {
                featureEnabled = Boolean.TRUE;
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMFeatureParamsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (featureEnabled == null) ? this.isFeatureEnabledInDB(featureName) : featureEnabled;
    }
    
    public Boolean isFeatureEnabledInDB(final String featureName) {
        Boolean featureEnabled = Boolean.FALSE;
        try {
            final HashMap mdmFeatureParamsHash = getMDMFeatureParametersFromDB();
            if (mdmFeatureParamsHash != null) {
                if (mdmFeatureParamsHash.containsKey(featureName) && (mdmFeatureParamsHash.get(featureName).equals(Boolean.TRUE) || mdmFeatureParamsHash.get(featureName).toString().equalsIgnoreCase("true"))) {
                    featureEnabled = Boolean.TRUE;
                    addOrUpdateMDMFeatureParamsCache(featureName, String.valueOf(featureEnabled));
                }
                else {
                    featureEnabled = Boolean.FALSE;
                    addOrUpdateMDMFeatureParamsCache(featureName, String.valueOf(featureEnabled));
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMFeatureParamsHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return featureEnabled;
    }
    
    public static HashMap<String, String> getMDMFeatureParameters() {
        HashMap<String, String> mdmFeatureParamsHash = getMDMFeatureParametersFromCache();
        if (mdmFeatureParamsHash == null || mdmFeatureParamsHash.size() <= 0) {
            mdmFeatureParamsHash = getMDMFeatureParametersFromDB();
            updateMDMFeatureParamsCache(mdmFeatureParamsHash);
        }
        return mdmFeatureParamsHash;
    }
    
    public static HashMap<String, String> getMDMFeatureParametersFromCache() {
        final Long startTime = System.currentTimeMillis();
        HashMap<String, String> mdmFeatureParamsHash = new HashMap<String, String>();
        final Object cacheObject = ApiFactoryProvider.getCacheAccessAPI().getCache("FEATURE_PARAMS_CACHE", 2);
        if (cacheObject != null) {
            mdmFeatureParamsHash = (HashMap)cacheObject;
        }
        return mdmFeatureParamsHash;
    }
    
    public static HashMap<String, Boolean> getMDMFeatureParamsForFeatureNames(final List<String> featureNames) {
        final HashMap<String, String> featureParamsInCache = getMDMFeatureParametersFromCache();
        final List<String> keysFoundInCache = new ArrayList<String>();
        final HashMap<String, Boolean> resultParams = new HashMap<String, Boolean>();
        for (final Map.Entry<String, String> cacheEntry : featureParamsInCache.entrySet()) {
            for (final String featureName : featureNames) {
                final String cacheKey = cacheEntry.getKey();
                if (cacheKey.equalsIgnoreCase(featureName)) {
                    final String cacheValue = String.valueOf(cacheEntry.getValue());
                    resultParams.put(cacheEntry.getKey(), Boolean.valueOf(cacheValue));
                    keysFoundInCache.add(cacheEntry.getKey().toLowerCase());
                    break;
                }
            }
        }
        featureNames.removeAll(keysFoundInCache);
        final List<String> featureNamesFoundInDb = new ArrayList<String>();
        if (!featureNames.isEmpty() && featureNames.size() > 0) {
            try {
                final DataObject mdmFeatureParamsDao = DataAccess.get("MDMFeatureParams", new Criteria(Column.getColumn("MDMFeatureParams", "FEATURE_NAME"), (Object)featureNames.toArray(new String[0]), 8, (boolean)Boolean.FALSE));
                if (!mdmFeatureParamsDao.isEmpty()) {
                    final Iterator<Row> mdmFeatureParamsIter = mdmFeatureParamsDao.getRows("MDMFeatureParams");
                    while (mdmFeatureParamsIter.hasNext()) {
                        final Row mdmFeatureParamRow = mdmFeatureParamsIter.next();
                        final String featureName2 = (String)mdmFeatureParamRow.get("FEATURE_NAME");
                        final Boolean featureVal = (Boolean)mdmFeatureParamRow.get("IS_FEATURE_ENABLED");
                        addOrUpdateMDMFeatureParamsCache(featureName2, String.valueOf(featureVal));
                        resultParams.put(featureName2, featureVal);
                        featureNamesFoundInDb.add(featureName2.toLowerCase());
                    }
                }
            }
            catch (final DataAccessException e) {
                MDMFeatureParamsHandler.logger.log(Level.SEVERE, "Exception in obtaining feature params from DB", (Throwable)e);
            }
        }
        featureNames.removeAll(featureNamesFoundInDb);
        if (!featureNames.isEmpty() && featureNames.size() > 0) {
            for (final String featureName3 : featureNames) {
                resultParams.put(featureName3, Boolean.FALSE);
            }
        }
        return resultParams;
    }
    
    public static HashMap getMDMFeatureParametersFromDB() {
        final Long startTime = System.currentTimeMillis();
        final HashMap mdmFeatureParamsHashMap = new HashMap();
        try {
            final DataObject mdmFeatureParamsDO = DataAccess.get("MDMFeatureParams", (Criteria)null);
            if (!mdmFeatureParamsDO.isEmpty()) {
                final Iterator mdmFeatureItr = mdmFeatureParamsDO.getRows("MDMFeatureParams");
                while (mdmFeatureItr.hasNext()) {
                    final Row mdmFeatureParamRow = mdmFeatureItr.next();
                    mdmFeatureParamsHashMap.put(mdmFeatureParamRow.get("FEATURE_NAME"), mdmFeatureParamRow.get("IS_FEATURE_ENABLED"));
                }
            }
            return mdmFeatureParamsHashMap;
        }
        catch (final Exception ex) {
            MDMFeatureParamsHandler.logger.log(Level.WARNING, "Caught exception while retrieving mdmFeature Parameters from DB.", ex);
            return null;
        }
    }
    
    @Deprecated
    public static void updateMDMFeatureParameterInDB(final String paramName, final String paramValue) throws Exception {
        final DataObject mdmFeatureParamsDO = DataAccess.get("MDMFeatureParams", (Criteria)null);
        final Criteria criteria = new Criteria(Column.getColumn("MDMFeatureParams", "FEATURE_NAME"), (Object)paramName, 0, false);
        Row mdmFeatureParamRow = mdmFeatureParamsDO.getRow("MDMFeatureParams", criteria);
        if (mdmFeatureParamRow == null) {
            mdmFeatureParamRow = new Row("MDMFeatureParams");
            mdmFeatureParamRow.set("FEATURE_NAME", (Object)paramName);
            mdmFeatureParamRow.set("IS_FEATURE_ENABLED", (Object)paramValue);
            mdmFeatureParamsDO.addRow(mdmFeatureParamRow);
            MDMFeatureParamsHandler.logger.log(Level.FINER, "Parameter added in DB:- param name: {0}  param value: {1}", new Object[] { paramName, paramValue });
        }
        else {
            mdmFeatureParamRow.set("IS_FEATURE_ENABLED", (Object)paramValue);
            mdmFeatureParamsDO.updateRow(mdmFeatureParamRow);
            MDMFeatureParamsHandler.logger.log(Level.FINER, "Parameter updated in DB:- param name: {0}  param value: {1}", new Object[] { paramName, paramValue });
        }
        DataAccess.update(mdmFeatureParamsDO);
    }
    
    private static void updateMDMFeatureParameterInDB(final String paramName, final boolean paramValue) {
        try {
            final SelectQuery fpQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDMFeatureParams"));
            fpQuery.addSelectColumn(Column.getColumn("MDMFeatureParams", "*"));
            fpQuery.setCriteria(new Criteria(Column.getColumn("MDMFeatureParams", "FEATURE_NAME"), (Object)paramName, 0));
            final DataObject mdmFeatureParamsDO = MDMUtil.getPersistence().get(fpQuery);
            Row mdmFeatureParamRow = null;
            if (mdmFeatureParamsDO != null && !mdmFeatureParamsDO.isEmpty()) {
                mdmFeatureParamRow = mdmFeatureParamsDO.getFirstRow("MDMFeatureParams");
            }
            if (mdmFeatureParamRow == null) {
                mdmFeatureParamRow = new Row("MDMFeatureParams");
                mdmFeatureParamRow.set("FEATURE_NAME", (Object)paramName);
                mdmFeatureParamRow.set("IS_FEATURE_ENABLED", (Object)paramValue);
                mdmFeatureParamsDO.addRow(mdmFeatureParamRow);
                MDMFeatureParamsHandler.logger.log(Level.INFO, "MDM Feature param: {0}, is added with value: {1}", new Object[] { paramName, paramValue });
            }
            else {
                mdmFeatureParamRow.set("IS_FEATURE_ENABLED", (Object)paramValue);
                mdmFeatureParamsDO.updateRow(mdmFeatureParamRow);
                MDMFeatureParamsHandler.logger.log(Level.INFO, "MDM Feature param: {0}, is updated with value: {1}", new Object[] { paramName, paramValue });
            }
            DataAccess.update(mdmFeatureParamsDO);
        }
        catch (final Exception e) {
            MDMFeatureParamsHandler.logger.log(Level.SEVERE, "Exception in updateMDMFeatureParameterInDB", e);
        }
    }
    
    @Deprecated
    public static void updateMDMFeatureParameter(final String paramName, final String paramValue) {
        try {
            updateMDMFeatureParameterInDB(paramName, paramValue);
            addOrUpdateMDMFeatureParamsCache(paramName, paramValue);
        }
        catch (final Exception ex) {
            MDMFeatureParamsHandler.logger.log(Level.WARNING, ex, () -> "Caught exception while updating Parameter:" + s + " in DB.");
        }
    }
    
    public static void updateMDMFeatureParameter(final String paramName, final boolean paramValue) {
        try {
            updateMDMFeatureParameterInDB(paramName, paramValue);
            addOrUpdateMDMFeatureParamsCache(paramName, String.valueOf(paramValue));
        }
        catch (final Exception ex) {
            MDMFeatureParamsHandler.logger.log(Level.WARNING, ex, () -> "Caught exception while updating Parameter:" + s + " in DB.");
        }
    }
    
    public static void deleteMDMFeatureParameter(final String paramKey) {
        try {
            deleteMDMFeatureParamFromCache(paramKey);
            final Criteria criteria = new Criteria(Column.getColumn("MDMFeatureParams", "FEATURE_NAME"), (Object)paramKey, 0, false);
            SyMUtil.getPersistence().delete(criteria);
        }
        catch (final Exception ex) {
            MDMFeatureParamsHandler.logger.log(Level.WARNING, ex, () -> "Caught exception while deleting mdmFeature Parameter:" + s + " from DB.");
        }
    }
    
    private static void addOrUpdateMDMFeatureParamsCache(final String paramKey, final String paramValue) {
        HashMap<String, String> mdmFeatureParamsHash = new HashMap<String, String>();
        try {
            final Object cacheObject = ApiFactoryProvider.getCacheAccessAPI().getCache("FEATURE_PARAMS_CACHE", 2);
            if (cacheObject != null) {
                mdmFeatureParamsHash = (HashMap)cacheObject;
            }
            mdmFeatureParamsHash.put(paramKey, paramValue);
            updateMDMFeatureParamsCache(mdmFeatureParamsHash);
        }
        catch (final Exception ex) {
            mdmFeatureParamsHash.remove(paramKey);
            updateMDMFeatureParamsCache(mdmFeatureParamsHash);
        }
    }
    
    private static void deleteMDMFeatureParamFromCache(final String paramKey) {
        HashMap<String, String> mdmFeatureParamsHash = new HashMap<String, String>();
        final Object cacheObject = ApiFactoryProvider.getCacheAccessAPI().getCache("FEATURE_PARAMS_CACHE", 2);
        if (cacheObject != null) {
            mdmFeatureParamsHash = (HashMap)cacheObject;
        }
        mdmFeatureParamsHash.remove(paramKey);
        updateMDMFeatureParamsCache(mdmFeatureParamsHash);
    }
    
    private static void updateMDMFeatureParamsCache(final HashMap<String, String> mdmFeatureParamsHash) {
        ApiFactoryProvider.getCacheAccessAPI().putCache("FEATURE_PARAMS_CACHE", (Object)mdmFeatureParamsHash, 2);
    }
    
    public Boolean isFeatureAvailableGlobally(final String featureName, final Boolean featureDefaultState) {
        Boolean featureEnabled = featureDefaultState;
        try {
            final String featureEnabledStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty(featureName);
            if (featureEnabledStr != null) {
                featureEnabled = Boolean.parseBoolean(featureEnabledStr);
            }
        }
        catch (final Exception ex) {
            MDMFeatureParamsHandler.logger.log(Level.WARNING, "Exception in getting isFeatureAvailableGlobally from property file", ex);
        }
        return (featureEnabled == null) ? featureDefaultState : featureEnabled;
    }
    
    static {
        MDMFeatureParamsHandler.logger = Logger.getLogger(MDMFeatureParamsHandler.class.getName());
        MDMFeatureParamsHandler.mdmFeatureParamsHandler = null;
    }
}
