package com.me.devicemanagement.framework.webclient.api.util;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class ApiSQLViewController
{
    public String setVariableValues(final APIRequest apiRequest, String queryString) {
        queryString = ApiFactoryProvider.getUtilAccessAPI().setUserVariables(queryString);
        return queryString;
    }
    
    public String getSortString(final APIRequest apiRequest) {
        return null;
    }
}
