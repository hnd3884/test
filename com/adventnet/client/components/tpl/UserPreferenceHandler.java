package com.adventnet.client.components.tpl;

import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.tpl.TemplateAPI;

public class UserPreferenceHandler implements TemplateAPI.VariableHandler
{
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) throws Exception {
        return UserPersonalizationAPI.getUserPreference(variableName, WebClientUtil.getAccountId());
    }
}
