package com.me.mdm.agent.servlets.modern.mac;

import java.util.Arrays;
import java.io.OutputStream;
import com.adventnet.iam.security.SecurityUtil;
import java.util.logging.Level;
import com.dd.plist.Base64;
import com.me.mdm.server.util.MDMSecurityLogger;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.me.mdm.server.dep.AdminEnrollmentHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.core.enrollment.EnrollmentTemplateHandler;
import org.json.JSONObject;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.agent.servlets.auth.BasicAuthenticatedRequestServlet;

public class ModernMacEnrollmentServlet extends BasicAuthenticatedRequestServlet
{
    String servletPath;
    private Logger logger;
    public static List<Integer> PURPOSE;
    
    public ModernMacEnrollmentServlet() {
        this.servletPath = "/mdm/client/v1/modern/mac/";
        this.logger = Logger.getLogger("MDMModernMgmtLogger");
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
    public boolean authenticate(final String username, final String password, final HttpServletRequest request, final HttpServletResponse response, final JSONObject requestJSON) throws Exception {
        final String requestPath = request.getPathInfo();
        final String templateToken = requestPath.substring(1, requestPath.length());
        Boolean isValid = EnrollmentTemplateHandler.validateEnrollmentTemplateBasicAuthentication(templateToken, username, password);
        if (!isValid) {
            final String serialNumber = request.getHeader("SerialNumber");
            isValid = EnrollmentTemplateHandler.validateWithGenericID(username, password, templateToken, serialNumber);
        }
        return isValid;
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            final JSONObject requestJSON = new JSONObject();
            final String requestPath = request.getPathInfo();
            final String templateToken = requestPath.substring(1, requestPath.length());
            if (MDMEnrollmentUtil.getInstance().isAPNsConfigured() && MDMEnrollmentUtil.getInstance().isNATConfigured()) {
                if (this.authenticate(request, response, requestJSON)) {
                    if (templateToken != null) {
                        final AdminEnrollmentHandler dep = new AdminEnrollmentHandler();
                        final JSONObject requestObj = new JSONObject();
                        final JSONObject messageRequest = new JSONObject();
                        messageRequest.put("SerialNumber", (Object)((request.getHeader("SerialNumber") == null) ? "--" : request.getHeader("SerialNumber")));
                        messageRequest.put("TemplateToken", (Object)templateToken);
                        messageRequest.put("UDID", (Object)((request.getHeader("UDID") == null) ? "--" : request.getHeader("UDID")));
                        messageRequest.put("DeviceType", MDMInvDataPopulator.getModelType("Mac"));
                        messageRequest.put("IMEI", (Object)"--");
                        requestObj.put("MsgRequest", (Object)messageRequest);
                        requestObj.put("DevicePlatform", (Object)"iOS");
                        requestObj.put("isUntrustedDeviceAllowed", false);
                        MDMSecurityLogger.info(this.logger, "ModernMacEnrollmentServlet", "doPost", "ModernMacEnrollmentServlet : Request Object:{0}", requestObj);
                        final JSONObject responseJSON = dep.processDeviceProvisioningMessageForMacModernMgmt(requestObj);
                        final String status = String.valueOf(responseJSON.get("Status"));
                        if (status.equals("Acknowledged")) {
                            final JSONObject msgResponseJSON = responseJSON.getJSONObject("MsgResponse");
                            final String mobileConfigEncodedContent = String.valueOf(msgResponseJSON.get("MobileConfigContent"));
                            final byte[] mobileConfigDecodedContent = Base64.decode(mobileConfigEncodedContent);
                            response.setContentType("application/x-apple-aspen-config");
                            response.setHeader("Content-Disposition", "attachment;filename=mdm.mobileconfig");
                            final OutputStream os = (OutputStream)response.getOutputStream();
                            os.write(mobileConfigDecodedContent);
                            os.flush();
                            os.close();
                            this.logger.log(Level.INFO, "ModernMacEnrollmentServlet : Mobile config sent as response");
                        }
                    }
                }
                else {
                    this.logger.log(Level.WARNING, "MacModernMgmt : Path:{0}/{1}", new Object[] { SecurityUtil.getRequestPath(request), request.getQueryString() });
                    this.logger.log(Level.WARNING, "MacModernMgmt : UnAuthenticated request request received.");
                }
            }
            else {
                this.logger.log(Level.WARNING, "MacModernMgmt : Path:{0}/{1}", new Object[] { SecurityUtil.getRequestPath(request), request.getQueryString() });
                this.logger.log(Level.WARNING, "MacModernMgmt : Agent trying to enroll before prereqisists are met.");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "DeviceRegistrationServlet : Exception occured while handling messages - ", e);
        }
    }
    
    static {
        ModernMacEnrollmentServlet.PURPOSE = Arrays.asList(102);
    }
}
