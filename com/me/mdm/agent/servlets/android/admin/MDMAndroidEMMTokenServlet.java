package com.me.mdm.agent.servlets.android.admin;

import com.me.idps.core.api.DirectoryAPIFacade;
import org.json.JSONObject;
import java.io.IOException;
import java.util.logging.Level;
import com.me.mdm.core.enrollment.AndroidQREnrollmentHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.UserAuthenticatedRequestServlet;

public class MDMAndroidEMMTokenServlet extends UserAuthenticatedRequestServlet
{
    public Logger logger;
    
    public MDMAndroidEMMTokenServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) {
        try {
            if (!MDMFeatureParamsHandler.getInstance().isFeatureEnabled("isAndroidEMMBYODEnrollment")) {
                response.sendError(500, "Required FeatureParam not enabled . Not proceeding to validate request");
                return;
            }
            final Long customerID = Long.valueOf(request.getParameter("customerId"));
            final String zapikey = request.getParameter("zapikey");
            final Long loginID = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().authenticateUser(zapikey);
            final Long userID = DMUserHandler.getUserIdForLoginId(loginID);
            final String templateToken = request.getParameter("templateToken");
            if (this.validate(customerID, userID, templateToken)) {
                final JSONObject templateJSON = new EnrollmentTemplateHandler().getEnrollmentTemplateForTemplateToken(templateToken);
                final Long templateUserID = JSONUtil.optLongForUVH(templateJSON, "ADDED_USER", (Long)null);
                final String responseString = new AndroidQREnrollmentHandler().getQREnrollmentProfile(templateUserID, customerID);
                response.setContentType("application/json;charset=UTF8");
                response.getWriter().write(responseString);
            }
            else {
                this.logger.log(Level.WARNING, "MDMAndroidEMMTokenServlet:Unable to validate request for zapikey, customerID");
                response.sendError(403, "User not authorized to perform this request");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "MDMAndroidEMMTokenServlet:Exception while processing request ", ex);
            try {
                response.sendError(500, "MDM Unable to process this request");
            }
            catch (final IOException e) {
                this.logger.log(Level.SEVERE, "MDMAndroidEMMTokenServlet:Exception while sending error in  request ", e);
            }
        }
    }
    
    private boolean validate(final Long customerID, final Long userID, final String templateToken) {
        boolean isValidRequest = false;
        try {
            this.logger.log(Level.FINE, "MDMAndroidEMMTokenServlet:UserID received from zapikey:{0}", userID);
            if (userID != null) {
                isValidRequest = DirectoryAPIFacade.getInstance().isUserCustomerRelevant(userID, customerID);
                if (isValidRequest) {
                    isValidRequest = templateToken.equals(new EnrollmentTemplateHandler().getTemplateTokenForUserId(userID, 22, customerID));
                    if (!isValidRequest) {
                        this.logger.log(Level.WARNING, "MDMAndroidEMMTokenServlet:Unable to verify if template token");
                        isValidRequest = true;
                    }
                }
                else {
                    this.logger.log(Level.WARNING, "MDMAndroidEMMTokenServlet:Unable to verify if user:{0} belongs for customerID:{1}", new Object[] { userID, customerID });
                }
            }
            else {
                this.logger.log(Level.WARNING, "MDMAndroidEMMTokenServlet:Unable to get userID from zapikey for this request:");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "MDMAndroidEMMTokenServlet:Exception while processing  validate", ex);
            isValidRequest = false;
        }
        return isValidRequest;
    }
}
