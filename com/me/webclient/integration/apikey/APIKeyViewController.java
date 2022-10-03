package com.me.webclient.integration.apikey;

import com.adventnet.persistence.PersistenceInitializer;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class APIKeyViewController extends MDMEmberSqlViewController
{
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String key = super.getVariableValue(viewCtx, variableName);
        if (variableName != null && variableName.equals("KEY")) {
            if (DBUtil.getActiveDBName().equalsIgnoreCase("mssql")) {
                key = "ZOHO_CERT";
            }
            else {
                key = PersistenceInitializer.getConfigurationValue("ECTag");
            }
        }
        return key;
    }
}
