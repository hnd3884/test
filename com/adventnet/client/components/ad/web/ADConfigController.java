package com.adventnet.client.components.ad.web;

import com.adventnet.persistence.WritableDataObject;
import com.adventnet.authentication.util.AuthUtil;
import java.util.Map;
import com.adventnet.client.view.web.WebViewAPI;
import java.util.regex.Pattern;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.Row;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.form.web.AjaxFormController;

public class ADConfigController extends AjaxFormController
{
    public static String PATTERN_STR;
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        final String adId = viewCtx.getRequest().getParameter("AD_ID");
        if (adId != null) {
            final Row adRow = new Row("ActiveDirectoryInfo");
            adRow.set("AD_ID", (Object)new Long(adId));
            final DataObject dataObject = LookUpUtil.getPersistence().get("ActiveDirectoryInfo", adRow);
            viewCtx.getRequest().setAttribute("AD_DETAILS", (Object)dataObject);
        }
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        final String adId = viewCtx.getRequest().getParameter("AD_ID");
        DataObject dataObject = null;
        if (adId != null) {
            final Row adRow = new Row("ActiveDirectoryInfo");
            adRow.set("AD_ID", (Object)new Long(adId));
            dataObject = LookUpUtil.getPersistence().get("ActiveDirectoryInfo", adRow);
        }
        final String serverName = "";
        final String domainName = "";
        final String userName = "";
        final String requestServerName = request.getParameter("serverName");
        final String requestDomainName = request.getParameter("domainName");
        final String requestUserName = request.getParameter("userName");
        boolean inputsValid = false;
        if (requestServerName != null && requestUserName != null && requestDomainName != null && Pattern.matches(ADConfigController.PATTERN_STR, requestServerName) && Pattern.matches(ADConfigController.PATTERN_STR, requestUserName) && Pattern.matches(ADConfigController.PATTERN_STR, requestDomainName)) {
            inputsValid = true;
        }
        if (!inputsValid) {
            request.setAttribute("AD_DETAILS", (Object)dataObject);
            return WebViewAPI.sendResponse(request, response, false, "Please enter valid values for all fields", (Map)null);
        }
        if (request.getParameter("submit") != null) {
            if (dataObject != null && dataObject.containsTable("ActiveDirectoryInfo")) {
                final Row row = dataObject.getFirstRow("ActiveDirectoryInfo");
                if (!request.getParameter("serverName").equals("")) {
                    row.set("SERVERNAME", (Object)request.getParameter("serverName"));
                }
                if (!request.getParameter("domainName").equals("")) {
                    row.set("DEFAULTDOMAIN", (Object)request.getParameter("domainName"));
                }
                if (!request.getParameter("userName").equals("")) {
                    row.set("USERNAME", (Object)request.getParameter("userName"));
                }
                row.set("PASSWORD", (Object)AuthUtil.encryptString(request.getParameter("password")));
                dataObject.updateRow(row);
                LookUpUtil.getPersistence().update(dataObject);
            }
            else {
                dataObject = (DataObject)new WritableDataObject();
                final Row row = new Row("ActiveDirectoryInfo");
                if (!request.getParameter("serverName").equals("")) {
                    row.set("SERVERNAME", (Object)request.getParameter("serverName"));
                }
                if (!request.getParameter("domainName").equals("")) {
                    row.set("DEFAULTDOMAIN", (Object)request.getParameter("domainName"));
                }
                if (!request.getParameter("userName").equals("")) {
                    row.set("USERNAME", (Object)request.getParameter("userName"));
                }
                row.set("PASSWORD", (Object)AuthUtil.encryptString(request.getParameter("password")));
                dataObject.addRow(row);
                LookUpUtil.getPersistence().add(dataObject);
            }
        }
        request.setAttribute("AD_DETAILS", (Object)dataObject);
        return WebViewAPI.sendResponse(request, response, true, "Configuration successfully updated", (Map)null);
    }
    
    static {
        ADConfigController.PATTERN_STR = "[a-zA-Z0-9_\\-\\.,\\ \\\\\\/@]*";
    }
}
