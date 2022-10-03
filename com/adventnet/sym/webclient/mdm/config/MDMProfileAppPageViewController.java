package com.adventnet.sym.webclient.mdm.config;

import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.sym.server.mdm.config.MDMProfileAppFilterHandler;
import org.json.simple.JSONArray;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.form.web.AjaxFormController;

public class MDMProfileAppPageViewController extends AjaxFormController
{
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        super.processPreRendering(viewCtx, request, response, viewUrl);
        final String viewName = viewCtx.getUniqueId();
        if ("mdmGroupProfileFilter".equals(viewName)) {
            final JSONArray filterProfileArr = new JSONArray();
            request.setAttribute("filterProfiles", (Object)filterProfileArr);
        }
        if ("mdmAppFilter".equals(viewName)) {
            final String platformStr = request.getParameter("platformType");
            int platformType = 0;
            if (platformStr != null && !platformStr.equals("")) {
                platformType = Integer.parseInt(platformStr);
            }
            final JSONArray filterProfileArr2 = new JSONArray();
            if (platformType == 0) {
                final JSONObject osFilterGroup = MDMProfileAppFilterHandler.getInstance().getProfilePlatformFilterGroup(2);
                filterProfileArr2.add((Object)osFilterGroup);
            }
            final JSONObject appTypeFilterGroup = MDMProfileAppFilterHandler.getInstance().getAppTypeFilterGroup(platformType);
            filterProfileArr2.add((Object)appTypeFilterGroup);
            final JSONObject appLicenseTypeFilterGroup = MDMProfileAppFilterHandler.getInstance().getAppLicenseTypeFilterGroup();
            filterProfileArr2.add((Object)appLicenseTypeFilterGroup);
            if (platformType != 0) {
                final JSONObject appCategoryFilterGroup = MDMProfileAppFilterHandler.getInstance().getCategoryFilterGroup(platformType);
                filterProfileArr2.add((Object)appCategoryFilterGroup);
            }
            request.setAttribute("filterProfiles", (Object)filterProfileArr2);
        }
        if ("mdmProfileFilter".equals(viewName)) {
            final String platformStr = request.getParameter("platformType");
            int platformType = 0;
            if (platformStr != null && !platformStr.equals("")) {
                platformType = Integer.parseInt(platformStr);
            }
            final JSONArray filterProfileArr2 = new JSONArray();
            if (platformType == 0) {
                final JSONObject osFilterGroup = MDMProfileAppFilterHandler.getInstance().getProfilePlatformFilterGroup(1);
                filterProfileArr2.add((Object)osFilterGroup);
            }
            request.setAttribute("filterProfiles", (Object)filterProfileArr2);
        }
        if ("mdmDocumentFilter".equals(viewName)) {
            final JSONArray filterDocumentArr = new JSONArray();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            final JSONObject tagTypeFilterGroup = MDMProfileAppFilterHandler.getInstance().getDocTagTypeFilterGroup(customerID);
            filterDocumentArr.add((Object)tagTypeFilterGroup);
            final JSONObject docTypeFilterGroup = MDMProfileAppFilterHandler.getInstance().getDocTypeFilterGroup();
            filterDocumentArr.add((Object)docTypeFilterGroup);
            request.setAttribute("filterProfiles", (Object)filterDocumentArr);
        }
        return viewUrl;
    }
}
