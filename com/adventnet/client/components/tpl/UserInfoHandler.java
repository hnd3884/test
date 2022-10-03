package com.adventnet.client.components.tpl;

import com.zoho.authentication.AuthenticationUtil;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.tpl.TemplateAPI;

public class UserInfoHandler implements TemplateAPI.VariableHandler
{
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) {
        if (variableName.equals("ACCOUNT_ID")) {
            return String.valueOf(WebClientUtil.getAccountId());
        }
        if (variableName.equals("LOGIN_NAME")) {
            return WebClientUtil.isNewAuthPropertySet() ? AuthenticationUtil.getLoginName() : WebClientUtil.getAuthImpl().getLoginName();
        }
        if (variableName.equals("USER_ID")) {
            return String.valueOf(WebClientUtil.isNewAuthPropertySet() ? AuthenticationUtil.getUserID() : WebClientUtil.getAuthImpl().getUserID());
        }
        if (variableName.equals("LOGIN_ID")) {}
        throw new RuntimeException("Unknown variable " + variableName);
    }
}
