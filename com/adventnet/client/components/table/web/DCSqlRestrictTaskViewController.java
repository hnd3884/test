package com.adventnet.client.components.table.web;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.client.view.web.ViewContext;

public class DCSqlRestrictTaskViewController extends DCSqlViewController
{
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String criteria = super.getVariableValue(viewCtx, variableName);
        final HttpServletRequest request = viewCtx.getRequest();
        if ((variableName.equalsIgnoreCase("RESTRICT_TECH") || variableName.equalsIgnoreCase("RESTRICT_USER") || variableName.equalsIgnoreCase("RESTRICT_TASK_USER")) && !request.isUserInRole("Common_Write") && request.isUserInRole("RESTRICT_USER_TASKS")) {
            Long loginId = null;
            Long userId = null;
            try {
                loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            }
            catch (final Exception e) {
                Logger.getLogger(DCRestrictTaskRetriverAction.class.getName()).log(Level.SEVERE, "Exception occured in getting AuthUtilAPI", e);
            }
            String columnAlias = null;
            Long columnValue = null;
            if (variableName.equalsIgnoreCase("RESTRICT_TASK_USER") && userId != null) {
                columnAlias = "TaskToUserRel.USER_ID";
                columnValue = userId;
            }
            else if (variableName.equalsIgnoreCase("RESTRICT_TECH") && loginId != null) {
                columnAlias = "AaaLogin.LOGIN_ID";
                columnValue = loginId;
            }
            else if (variableName.equalsIgnoreCase("RESTRICT_USER") && userId != null) {
                columnAlias = "AaaLogin.LOGIN_ID";
                columnValue = userId;
            }
            if (columnAlias != null && columnValue != null) {
                final StringBuilder restrictTaskCriteria = new StringBuilder();
                restrictTaskCriteria.append(columnAlias);
                restrictTaskCriteria.append(" ");
                restrictTaskCriteria.append("=");
                restrictTaskCriteria.append(columnValue);
                if (criteria != null) {
                    criteria = " AND " + restrictTaskCriteria.toString();
                }
                else {
                    criteria = restrictTaskCriteria.toString();
                }
            }
        }
        else if ((variableName.equalsIgnoreCase("RESTRICT_TECH") || variableName.equalsIgnoreCase("RESTRICT_USER") || variableName.equalsIgnoreCase("RESTRICT_TASK_USER")) && criteria == null) {
            criteria = "(1=1)";
        }
        return criteria;
    }
}
