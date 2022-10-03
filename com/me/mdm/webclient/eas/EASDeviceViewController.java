package com.me.mdm.webclient.eas;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.enrollment.EnrollmentSettingsHandler;
import com.me.mdm.server.easmanagement.EASMgmtConstants;
import com.me.mdm.server.easmanagement.EASMgmt;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.form.web.AjaxFormController;

public class EASDeviceViewController extends AjaxFormController
{
    public void processPostRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        super.processPostRendering(viewCtx, request, response);
    }
    
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        try {
            super.processPreRendering(viewCtx, request, response, viewUrl);
            final String viewName = viewCtx.getUniqueId();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            final String lastSyncedTime = null;
            boolean isSyncInProgress = false;
            final boolean isSessionInProgress = false;
            boolean isPolicyConfigured = false;
            boolean isPolicyApplied = false;
            final JSONObject CEADetailsRequest = new JSONObject();
            CEADetailsRequest.put((Object)"EASServerDetails", (Object)String.valueOf(Boolean.TRUE));
            CEADetailsRequest.put((Object)"EASSelectedMailbox", (Object)String.valueOf(Boolean.TRUE));
            final JSONObject CEAdetails = EASMgmt.getInstance().getCEAdetails(CEADetailsRequest);
            final Long serverID = (Long)CEAdetails.get((Object)"EAS_SERVER_ID");
            final JSONObject easJSON = new JSONObject();
            final Long policyId = (Long)CEAdetails.get((Object)"EAS_POLICY_ID");
            final Integer policyStatus = (Integer)CEAdetails.get((Object)"POLICY_STATUS");
            final Integer sessionStatus = (Integer)CEAdetails.get((Object)"SESSION_STATUS");
            final boolean isEASServerConfigured = serverID != null;
            if (serverID != null) {
                isPolicyConfigured = (policyId != null);
                isPolicyApplied = (policyStatus != null && policyStatus.equals(EASMgmtConstants.POLICY_ENFORCEMENT_DONE));
            }
            if (isEASServerConfigured) {
                isSyncInProgress = (sessionStatus != null && sessionStatus != 0);
            }
            final boolean isSelfEnrollEnabled = EnrollmentSettingsHandler.getInstance().isSelfEnrollmentEnabled(customerID);
            final String serverUrl = MDMEnrollmentUtil.getInstance().getServerBaseURL();
            final String selfenrollmentURL = serverUrl + "/mdm/enroll";
            if (viewName.equalsIgnoreCase("easSelfEnroll")) {
                final String copyPasteContent = request.getParameter("copyPasteContent");
                request.setAttribute("copyPasteContent", (Object)copyPasteContent);
            }
            if (viewName.equalsIgnoreCase("easConfig")) {
                easJSON.put((Object)"TASK_TYPE", (Object)"Add");
            }
            if (viewName.equalsIgnoreCase("easModify")) {
                easJSON.put((Object)"TASK_TYPE", (Object)"Update");
            }
            final String isDemoMode = String.valueOf(ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
            request.setAttribute("viewName", (Object)viewName);
            easJSON.put((Object)"selfenrollmentURL", (Object)selfenrollmentURL);
            easJSON.put((Object)"isPolicyApplied", (Object)isPolicyApplied);
            easJSON.put((Object)"isPolicyConfigured", (Object)isPolicyConfigured);
            easJSON.put((Object)"isSyncInProgress", (Object)isSyncInProgress);
            easJSON.put((Object)"isSelfEnrollEnabled", (Object)isSelfEnrollEnabled);
            easJSON.put((Object)"isDemoMode", (Object)isDemoMode);
            easJSON.put((Object)"CEAdetails", (Object)CEAdetails);
            request.setAttribute("easJSON", (Object)easJSON);
        }
        catch (final Exception ex) {
            EASMgmt.logger.log(Level.WARNING, "Exception occured in processPreRendering -  EASDeviceViewController", ex);
        }
        return viewUrl;
    }
}
