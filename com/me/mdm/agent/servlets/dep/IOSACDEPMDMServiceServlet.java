package com.me.mdm.agent.servlets.dep;

import java.util.logging.Level;
import org.json.simple.JSONObject;
import java.util.HashMap;
import com.me.mdm.core.enrollment.AppleConfiguratorEnrollmentHandler;
import com.me.mdm.api.message.MDMMessageProvider;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.tracker.mics.MICSMailerAPI;
import com.me.mdm.server.tracker.mics.MICSFeatureTrackerUtil;
import com.me.mdm.server.tracker.mics.MICSAppleEnrollmentFeatureController;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.adventnet.iam.security.SecurityUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.UserAuthenticatedRequestServlet;

public class IOSACDEPMDMServiceServlet extends UserAuthenticatedRequestServlet
{
    public Logger logger;
    
    public IOSACDEPMDMServiceServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.doPost(request, response, deviceRequest);
    }
    
    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.doPost(request, response, deviceRequest);
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            DMSecurityLogger.info(this.logger, "IOSACDEPMDMServiceServlet", "doPost", "URL hit form Apple Configurator: {0}", (Object)(SecurityUtil.getRequestURI(request) + ((request.getQueryString() != null) ? ("?" + request.getQueryString()) : "")));
            DMSecurityLogger.info(this.logger, "IOSACDEPMDMServiceServlet", "doPost", "URL hit form Apple Configurator: {0}", (Object)IOUtils.toString((InputStream)request.getInputStream()));
            final String zapikey = request.getParameter("zapikey");
            final String templateToken = request.getParameter("templateToken");
            MICSFeatureTrackerUtil.appleAdminEnrollmentStart(MICSAppleEnrollmentFeatureController.EnrollmentType.APPLE_CONFIGURATOR);
            final String userAgent = request.getHeader("user-agent");
            if (userAgent.contains("Apple") && userAgent.contains("Configurator")) {
                SyMUtil.updateSyMParameter("APPLE_CONFIG_2_CONFIGURED", "true");
                final org.json.JSONObject jO = new EnrollmentTemplateHandler().getEnrollmentTemplateForTemplateToken(templateToken);
                final Long customerID = jO.getLong("CUSTOMER_ID");
                final Long userID = jO.getLong("ADDED_USER");
                MDMMessageProvider.getInstance().closeMsgForCustomerUser(userID, "APPLE_CONFIG_URL_MIGRATE", customerID);
            }
            final AppleConfiguratorEnrollmentHandler appleConfiguratorEnrollmentHandler = new AppleConfiguratorEnrollmentHandler();
            final HashMap params = new HashMap();
            params.put("zapikey", zapikey);
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put((Object)JSONObject.escape("dep_enrollment_url"), (Object)appleConfiguratorEnrollmentHandler.constructUrl("/mdm/client/v1/ios/ac/" + templateToken, params));
            params.put("templateToken", templateToken);
            jsonObject.put((Object)JSONObject.escape("dep_anchor_certs_url"), (Object)appleConfiguratorEnrollmentHandler.constructUrl("/mdm/client/v1/ios/trustcert", params));
            jsonObject.put((Object)JSONObject.escape("trust_profile_url"), (Object)appleConfiguratorEnrollmentHandler.constructUrl("/mdm/client/v1/ios/trustprofile", params));
            response.setContentType("application/json;charset=UTF8");
            response.setContentLength(jsonObject.toString().length());
            DMSecurityLogger.info(this.logger, "IOSACDEPMDMServiceServlet", "doPost", "IOSACDEPMDMServiceServlet Response Data: {0}", (Object)jsonObject.toString());
            response.getWriter().println(jsonObject.toString());
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in IOSACDEPMDMServiceServlet.. ", exp);
        }
    }
}
