package com.me.mdm.webclient.common;

import org.json.JSONException;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMWebClientUtil
{
    private static MDMWebClientUtil mdmWebClientUtil;
    public Logger logger;
    
    public MDMWebClientUtil() {
        this.logger = Logger.getLogger(MDMWebClientUtil.class.getName());
    }
    
    public static MDMWebClientUtil getInstance() {
        if (MDMWebClientUtil.mdmWebClientUtil == null) {
            MDMWebClientUtil.mdmWebClientUtil = new MDMWebClientUtil();
        }
        return MDMWebClientUtil.mdmWebClientUtil;
    }
    
    public JSONObject encodeViewSearchParameters(final String[] searchValues, final String[] searchColumns) {
        final JSONObject responseJSON = new JSONObject();
        try {
            String encodedSearchText = null;
            String encodedSearchColumn = null;
            if (searchValues != null && searchValues.length != 0) {
                encodedSearchText = "";
                encodedSearchColumn = "";
                for (int i = 0; i < searchValues.length; ++i) {
                    try {
                        MDMUtil.getInstance().validateViewSearchText(searchColumns[i], searchValues[i]);
                    }
                    catch (final SyMException e) {
                        this.logger.log(Level.FINE, "Error getting search_value", (Throwable)e);
                        searchValues[i] = MDMUtil.getInstance().sanitizeViewSearchText(searchValues[i]);
                    }
                    final String searchText = DMIAMEncoder.encodeSQLForNonPatternContext(searchValues[i]);
                    final String searchColumn = DMIAMEncoder.encodeSQLForNonPatternContext(searchColumns[i]);
                    encodedSearchText = encodedSearchText + "," + searchText;
                    encodedSearchColumn = encodedSearchColumn + "," + searchColumn;
                }
                if (!MDMUtil.isStringEmpty(encodedSearchText)) {
                    encodedSearchText = encodedSearchText.substring(1);
                    encodedSearchColumn = encodedSearchColumn.substring(1);
                }
            }
            responseJSON.put("SEARCH_VALUE", (Object)encodedSearchText);
            responseJSON.put("SEARCH_COLUMN", (Object)encodedSearchColumn);
        }
        catch (final JSONException e2) {
            this.logger.log(Level.SEVERE, "encodeViewSearchParameters() -- Exception", (Throwable)e2);
        }
        return responseJSON;
    }
    
    static {
        MDMWebClientUtil.mdmWebClientUtil = null;
    }
}
