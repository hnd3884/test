package com.me.mdm.webclient.search;

import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.search.SuggestSearchHandler;

public class SuggestSearchHandlerImpl implements SuggestSearchHandler
{
    protected static Logger out;
    
    public String getClassNameFromId(final String idName) {
        String className = null;
        if (idName.equals("advSearchSuggestionParams")) {
            className = "com.me.devicemanagement.framework.server.search.AdvSearchSuggestionQuery";
        }
        if (idName.equals("appstore")) {
            className = "com.adventnet.sym.server.mdm.apps.AppleAppStoreSearchHandler";
        }
        if (idName.equals("userSearchSuggestionParams")) {
            className = "com.adventnet.sym.server.mdm.enroll.MDMUserSearchSuggestionQuery";
        }
        if (idName.equals("iOSAppRepository")) {
            className = "com.adventnet.sym.server.mdm.apps.AppRepositorySearchHandler";
        }
        if (idName.equals("mdmDeviceSearchSuggestionParams")) {
            className = "com.me.mdm.server.search.MDMDeviceSearchSuggestionQuery";
        }
        else if (idName.equals("userOrgSearch")) {
            className = "com.me.devicemanagement.cloud.server.authentication.UserSearch";
        }
        return className;
    }
    
    static {
        SuggestSearchHandlerImpl.out = Logger.getLogger(SuggestSearchHandlerImpl.class.getName());
    }
}
