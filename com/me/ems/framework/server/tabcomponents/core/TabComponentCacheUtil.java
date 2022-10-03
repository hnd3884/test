package com.me.ems.framework.server.tabcomponents.core;

import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.util.Collection;
import java.util.EnumMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.function.Function;
import java.util.Arrays;
import java.util.Map;
import com.me.devicemanagement.framework.server.util.SyMUtil;

public class TabComponentCacheUtil
{
    public static String getTabComponentUserParameter(final Long userID, final ServerAPIConstants.TabComponentCacheParam cacheParam) throws Exception {
        final Map<ServerAPIConstants.TabComponentCacheParam, String> tabComponentParamsHash = getTabComponentUserParametersFromCache(userID);
        String paramValue = tabComponentParamsHash.get(cacheParam);
        if (paramValue == null) {
            paramValue = SyMUtil.getUserParameter(userID, cacheParam.toString());
            tabComponentParamsHash.put(cacheParam, paramValue);
            addOrUpdateTabComponentParamsCache(cacheParam, paramValue, userID);
        }
        return paramValue;
    }
    
    public static void addOrUpdateTabComponentUserParameter(final Long userID, final ServerAPIConstants.TabComponentCacheParam cacheParam, final String paramValue) throws Exception {
        SyMUtil.updateUserParameter(userID, cacheParam.toString(), paramValue);
        final Map<ServerAPIConstants.TabComponentCacheParam, String> tabComponentParamsHash = getTabComponentUserParametersFromCache(userID);
        tabComponentParamsHash.put(cacheParam, paramValue);
        updateTabComponentParamsCache(tabComponentParamsHash, userID);
    }
    
    public static void deleteTabComponentUserParameters(final Long userID, final ServerAPIConstants.TabComponentCacheParam... cacheParams) {
        final Object[] paramArray = Arrays.stream(cacheParams).map((Function<? super ServerAPIConstants.TabComponentCacheParam, ?>)Enum::toString).toArray();
        SyMUtil.deleteUserParameters(userID, paramArray);
        deleteTabComponentParamsFromCache(userID, cacheParams);
    }
    
    private static String getTabComponentCacheString(final Long userID) {
        if (CustomerInfoUtil.isSAS()) {
            return "TAB_COMPONENTS";
        }
        return "TAB_COMPONENTS".concat("_").concat(userID.toString());
    }
    
    private static String getNewTabCounterCacheString(final String tabID) {
        return "COUNTER".concat("_").concat(tabID);
    }
    
    private static Map<ServerAPIConstants.TabComponentCacheParam, String> getTabComponentUserParametersFromCache(final Long userID) {
        final String tabComponentCacheString = getTabComponentCacheString(userID);
        Map<ServerAPIConstants.TabComponentCacheParam, String> tabComponentParamsHash = (Map<ServerAPIConstants.TabComponentCacheParam, String>)ApiFactoryProvider.getCacheAccessAPI().getCache(tabComponentCacheString, 3);
        if (tabComponentParamsHash == null) {
            tabComponentParamsHash = new EnumMap<ServerAPIConstants.TabComponentCacheParam, String>(ServerAPIConstants.TabComponentCacheParam.class);
        }
        return tabComponentParamsHash;
    }
    
    private static void addOrUpdateTabComponentParamsCache(final ServerAPIConstants.TabComponentCacheParam paramKey, final String paramValue, final Long userID) {
        final String tabComponentCacheString = getTabComponentCacheString(userID);
        Map<ServerAPIConstants.TabComponentCacheParam, String> tabComponentParamsHash = (Map<ServerAPIConstants.TabComponentCacheParam, String>)ApiFactoryProvider.getCacheAccessAPI().getCache(tabComponentCacheString, 3);
        if (tabComponentParamsHash == null) {
            tabComponentParamsHash = new EnumMap<ServerAPIConstants.TabComponentCacheParam, String>(ServerAPIConstants.TabComponentCacheParam.class);
        }
        tabComponentParamsHash.put(paramKey, paramValue);
        updateTabComponentParamsCache(tabComponentParamsHash, userID);
    }
    
    private static void deleteTabComponentParamsFromCache(final Long userID, final ServerAPIConstants.TabComponentCacheParam... paramKeys) {
        final String tabComponentCacheString = getTabComponentCacheString(userID);
        Map<ServerAPIConstants.TabComponentCacheParam, String> tabComponentParamsHash = (Map<ServerAPIConstants.TabComponentCacheParam, String>)ApiFactoryProvider.getCacheAccessAPI().getCache(tabComponentCacheString, 3);
        if (tabComponentParamsHash == null) {
            tabComponentParamsHash = new EnumMap<ServerAPIConstants.TabComponentCacheParam, String>(ServerAPIConstants.TabComponentCacheParam.class);
        }
        else {
            tabComponentParamsHash.keySet().removeAll(Arrays.asList(paramKeys));
        }
        updateTabComponentParamsCache(tabComponentParamsHash, userID);
    }
    
    private static void updateTabComponentParamsCache(final Map<ServerAPIConstants.TabComponentCacheParam, String> tabComponentParamsHash, final Long userID) {
        final String tabComponentCacheString = getTabComponentCacheString(userID);
        if (tabComponentParamsHash.isEmpty()) {
            ApiFactoryProvider.getCacheAccessAPI().removeCache(tabComponentCacheString, 3);
        }
        else {
            ApiFactoryProvider.getCacheAccessAPI().putCache(tabComponentCacheString, tabComponentParamsHash, 3);
        }
    }
    
    public static boolean incrementCounterForNewTab(final String tabID, final Long userID) throws Exception {
        final String cacheString = getNewTabCounterCacheString(tabID);
        final int defaultCounter = FrameworkConfigurations.getFrameworkConfigurations().getInt("defaultNewTabClicks");
        if (CustomerInfoUtil.isSAS()) {
            Integer counter = (Integer)ApiFactoryProvider.getCacheAccessAPI().getCache(cacheString, 3);
            counter = ((counter == null) ? 1 : (counter + 1));
            if (counter >= defaultCounter) {
                ApiFactoryProvider.getCacheAccessAPI().removeCache(cacheString, 3);
                return Boolean.FALSE;
            }
            ApiFactoryProvider.getCacheAccessAPI().putCache(cacheString, counter, 3);
            return Boolean.TRUE;
        }
        else {
            final String userSpecificCacheString = cacheString.concat(userID.toString());
            Integer counter2 = (Integer)ApiFactoryProvider.getCacheAccessAPI().getCache(userSpecificCacheString);
            if (counter2 == null) {
                final String counterStringFromDB = SyMUtil.getUserParameter(userID, cacheString);
                counter2 = ((counterStringFromDB == null) ? 0 : Integer.parseInt(counterStringFromDB));
            }
            ++counter2;
            if (counter2 >= defaultCounter) {
                ApiFactoryProvider.getCacheAccessAPI().removeCache(userSpecificCacheString);
                SyMUtil.deleteUserParameter(userID, cacheString);
                return Boolean.FALSE;
            }
            ApiFactoryProvider.getCacheAccessAPI().putCache(userSpecificCacheString, counter2);
            SyMUtil.updateUserParameter(userID, cacheString, counter2.toString());
            return Boolean.TRUE;
        }
    }
}
