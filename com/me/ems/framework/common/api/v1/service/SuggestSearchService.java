package com.me.ems.framework.common.api.v1.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import com.me.devicemanagement.framework.webclient.search.SuggestSearchHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.search.SuggestQueryIfc;
import org.apache.commons.lang.StringUtils;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SuggestSearchService
{
    private static Logger advSearchErrorLogger;
    
    public Map getSearchSuggestionList(final String searchValue, final String selectedSearchParamId) {
        final HashMap hashMap = new HashMap();
        final SuggestSearchHandler suggestSearchHandler = ApiFactoryProvider.getSuggestSearchHandler();
        final String className = suggestSearchHandler.getClassNameFromId("advSearchSuggestionParams");
        List dataList = null;
        try {
            if (StringUtils.isNotEmpty(className) && StringUtils.isNotEmpty(searchValue)) {
                final SuggestQueryIfc suggestQuery = (SuggestQueryIfc)Class.forName(className).newInstance();
                dataList = suggestQuery.getSuggestDataAPI(searchValue, selectedSearchParamId);
            }
            else {
                SuggestSearchService.advSearchErrorLogger.log(Level.WARNING, "Values cannot be empty for Suggest Class Name or queryText.");
            }
        }
        catch (final Exception ex) {
            SuggestSearchService.advSearchErrorLogger.log(Level.SEVERE, "SuggestSearch : Exception occurred - showSearchList() :  ", ex);
        }
        if (dataList != null) {
            final ByteArrayOutputStream outNode = null;
            hashMap.put("suggestions", dataList);
        }
        return hashMap;
    }
    
    static {
        SuggestSearchService.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
    }
}
