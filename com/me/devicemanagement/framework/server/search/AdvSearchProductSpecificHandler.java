package com.me.devicemanagement.framework.server.search;

import java.util.Map;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.search.AdvSearchProductSpecificHandlerImpl;
import java.util.Locale;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.util.Properties;
import com.adventnet.ds.query.SelectQuery;

public interface AdvSearchProductSpecificHandler
{
    SelectQuery getSearchParamQuery(final int p0, final Long p1);
    
    SelectQuery getSearchHistoryParamQuery(final Long p0);
    
    SelectQuery getSearchHistoryParamQueryForParamID(final Long p0, final Long p1);
    
    void startProcessingHistoryDetailsUpdate(final Properties p0) throws Exception;
    
    boolean updateSearchHistory(final Long p0, final Long p1, final String p2) throws Exception;
    
    boolean deleteSearchHistory(final Long p0);
    
    HashMap getBaseParamDetails(final Long p0);
    
    boolean isComputerOrDevice(final Long p0);
    
    DataObject getCompDeviceDetailParam(final Long p0);
    
    String getSelectedTab(final String p0, final int p1);
    
    ArrayList setSearchParams(final SelectQuery p0, final ArrayList p1, final boolean p2);
    
    Set setSearchHistoryDataUtil(final SelectQuery p0);
    
    HashMap getViewDetailMap(final HttpServletRequest p0, final Iterator p1, final String p2);
    
    default HashMap getViewDetailMap(final Iterator itr, final String searchText, final Locale locale) {
        try {
            return new AdvSearchProductSpecificHandlerImpl().getViewDetailMap(itr, searchText, locale);
        }
        catch (final Exception ex) {
            Logger.getLogger("AdvSearchError").log(Level.SEVERE, "Exception in getting view detail", ex);
            return new HashMap();
        }
    }
    
    JSONObject getSearchParamsListAsJsonObjectFromCache(final HttpServletRequest p0) throws Exception;
    
    default JSONObject getSearchParamsListAsJsonObjectFromCache(final HashMap map, final Locale locale) throws Exception {
        return new AdvSearchProductSpecificHandlerImpl().getSearchParamsListAsJsonObjectFromCache(map, locale);
    }
    
    JSONObject getDefaultSearchParamsUtil(final HttpServletRequest p0) throws Exception;
    
    default Map getDefaultSearchParamsUtil(final List roles) throws Exception {
        return new AdvSearchProductSpecificHandlerImpl().getDefaultSearchParamsUtil(roles);
    }
    
    ArrayList getBaseSearchParamsUtil(final HttpServletRequest p0, final Boolean p1) throws Exception;
    
    default ArrayList getBaseSearchParamsUtil(final Boolean isHistoryParamNeeded, final List roles, final Locale locale) throws Exception {
        return new AdvSearchProductSpecificHandlerImpl().getBaseSearchParamsUtil(isHistoryParamNeeded, roles, locale);
    }
    
    JSONObject getSettingsSearchParamsUtil() throws Exception;
    
    String getAuthKey(final HttpServletRequest p0);
    
    Boolean disableAdvSearchForCertainEdition();
    
    Boolean updateMainIndexFile(final Boolean p0, final String p1) throws Exception;
    
    JSONObject getLastSearchParam(final Long p0);
    
    default JSONObject getLastSearchParam(final Long loginId, final Locale userLocale) {
        try {
            return new AdvSearchProductSpecificHandlerImpl().getLastSearchParam(loginId, userLocale);
        }
        catch (final Exception ex) {
            Logger.getLogger("AdvSearchError").log(Level.SEVERE, "Exception in gettings LastSearchParam", ex);
            return null;
        }
    }
    
    JSONObject getProductInfo() throws Exception;
}
