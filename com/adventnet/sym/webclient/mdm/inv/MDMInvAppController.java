package com.adventnet.sym.webclient.mdm.inv;

import java.util.HashMap;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.form.web.AjaxFormController;

public class MDMInvAppController extends AjaxFormController
{
    public void processPostRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        super.processPostRendering(viewCtx, request, response);
    }
    
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        try {
            super.processPreRendering(viewCtx, request, response, viewUrl);
            final String sRes = request.getParameter("RESOURCE_ID");
            if (sRes != null) {
                final int platform = Integer.parseInt(request.getParameter("platform"));
                viewCtx.getRequest().setAttribute("platform", (Object)MDMUtil.getInstance().getPlatformColumnValue(platform, null));
                final String tab = request.getParameter("status");
                String statusTab = "";
                if (tab != null) {
                    statusTab = tab;
                }
                viewCtx.getRequest().setAttribute("status", (Object)statusTab);
                final Long resId = Long.parseLong(sRes);
                viewCtx.getRequest().setAttribute("deviceName", (Object)ManagedDeviceHandler.getInstance().getDeviceName(resId));
                viewCtx.getRequest().setAttribute("agentType", (Object)ManagedDeviceHandler.getInstance().getAgentType(resId));
                viewCtx.getRequest().setAttribute("resourceId", (Object)resId);
                viewCtx.getRequest().setAttribute("osversion", DBUtil.getValueFromDB("MdDeviceInfo", "RESOURCE_ID", (Object)resId, "OS_VERSION"));
                viewCtx.getRequest().setAttribute("total", (Object)request.getParameter("total"));
                viewCtx.getRequest().setAttribute("blacklist", (Object)request.getParameter("blacklist"));
                viewCtx.getRequest().setAttribute("whitelist", (Object)request.getParameter("whitelist"));
                viewCtx.getRequest().setAttribute("platformVal", (Object)platform);
            }
            final String sAppId = request.getParameter("APP_ID");
            if (sAppId != null) {
                final Long appId = Long.parseLong(sAppId);
                final HashMap appDetails = MDMUtil.getInstance().getAppDetails(appId);
                viewCtx.getRequest().setAttribute("appName", appDetails.get("APP_NAME"));
                viewCtx.getRequest().setAttribute("appId", (Object)appId);
                viewCtx.getRequest().setAttribute("appversion", appDetails.get("APP_VERSION"));
                viewCtx.getRequest().setAttribute("identifier", appDetails.get("IDENTIFIER"));
                viewCtx.getRequest().setAttribute("platform", appDetails.get("PLATFORM_TYPE"));
            }
            final String sAppGroupID = request.getParameter("APP_GROUP_ID");
            if (sAppGroupID != null) {
                final Long appGroupID = Long.parseLong(sAppGroupID);
                final HashMap appDetails2 = MDMUtil.getInstance().getAppGroupDetails(appGroupID);
                viewCtx.getRequest().setAttribute("appName", appDetails2.get("GROUP_DISPLAY_NAME"));
                viewCtx.getRequest().setAttribute("appGroupID", (Object)appGroupID);
                viewCtx.getRequest().setAttribute("identifier", appDetails2.get("IDENTIFIER"));
                viewCtx.getRequest().setAttribute("platform", appDetails2.get("PLATFORM_TYPE"));
                final Long blacklistCount = Long.parseLong(request.getParameter("blacklist"));
                final Long whitelistCount = Long.parseLong(request.getParameter("whitelist"));
                final Long installationCount = Long.parseLong(request.getParameter("installation"));
                final String filterType = request.getParameter("FilterType");
                viewCtx.getRequest().setAttribute("blacklistCount", (Object)blacklistCount);
                viewCtx.getRequest().setAttribute("whitelistCount", (Object)whitelistCount);
                viewCtx.getRequest().setAttribute("installationCount", (Object)installationCount);
                viewCtx.getRequest().setAttribute("deviceFilter", (Object)filterType);
            }
            final String isBlacklistTab = request.getParameter("select");
            boolean isBlacklistTabSelec = false;
            if (isBlacklistTab != null && isBlacklistTab.trim().equalsIgnoreCase("blacklist")) {
                isBlacklistTabSelec = true;
            }
            viewCtx.getRequest().setAttribute("isBlacklistAppSelec", (Object)isBlacklistTabSelec);
            AppSettingsDataHandler.getInstance().setAppViewFilterAttribute(viewCtx.getRequest());
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return viewUrl;
    }
}
