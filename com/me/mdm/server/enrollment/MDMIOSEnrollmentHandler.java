package com.me.mdm.server.enrollment;

import com.adventnet.sym.server.mdm.util.VersionChecker;
import com.me.mdm.core.auth.APIKey;
import java.util.Locale;
import com.adventnet.sym.server.mdm.terms.MDMTermsHandler;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.io.Reader;
import org.json.simple.parser.JSONParser;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.mdm.server.enrollment.ios.MDMProfileInstallationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.io.OutputStream;
import com.adventnet.sym.server.mdm.ios.payload.PayloadSigningFactory;
import com.me.mdm.server.enrollment.ios.IOSMobileConfigHandler;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.enrollment.ios.IOSEnrollRestrictionHandler;
import javax.servlet.ServletContext;
import org.json.JSONObject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class MDMIOSEnrollmentHandler extends MDMEnrollmentHandler
{
    private static Logger logger;
    
    @Override
    public void initEnrollemnt(final HttpServletRequest request, final HttpServletResponse response, final JSONObject enrollData) {
        try {
            this.servletPath = String.valueOf(enrollData.get("servletPath"));
            this.servletContext = (ServletContext)enrollData.get("servletContext");
            this.action = enrollData.optString("action", (String)null);
            this.userAgent = String.valueOf(enrollData.get("userAgent"));
            this.pathInfo = enrollData.optString("pathInfo", (String)null);
            this.enrollRestrictionHandler = new IOSEnrollRestrictionHandler();
            MDMIOSEnrollmentHandler.platform = "iOS";
            final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
            if (MDMEnrollmentHandler.isMac(this.userAgent) && request.getQueryString() != null && request.getQueryString().contains("isMacIpad=true")) {
                this.userAgent = this.userAgent.replaceAll("Macintosh", "iPad");
            }
            this.setCommonRequestAttibutes(request);
            if (this.servletPath.indexOf(".mob") != -1) {
                this.action = this.servletPath.substring(1, this.servletPath.indexOf(".mob"));
            }
            else if (this.action == null && (this.pathInfo == null || this.pathInfo.equals("/"))) {
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "iOS_Enrollment_Url_Access_Count");
                }
                this.action = "selfEnroll";
            }
            else if (this.action == null) {
                this.action = "";
            }
            this.setServerDetails(request);
            if (this.isInvalidBrowser(request, response)) {
                return;
            }
            if (this.pathInfo != null && !this.pathInfo.equals("/")) {
                this.action = "";
            }
            if (MDMEnrollmentHandler.isiOS(this.userAgent)) {
                setShowNewEnrollmentWorkFlowAttibute(request);
            }
            if (this.action.equalsIgnoreCase("success")) {
                this.getEnrollSuccessPage(request, response);
            }
            else if (this.action.equalsIgnoreCase("getEnrollStatus")) {
                this.getEnrollStatus(request, response);
            }
            else if (this.action.equalsIgnoreCase("download")) {
                this.showMobileConfigDownloadPage(request, response);
            }
            else if (this.action.equalsIgnoreCase("getconfig")) {
                this.downloadMobileConfig(request, response);
            }
            else if (this.action.equalsIgnoreCase("refetchconfig")) {
                this.refetchMobileConfig(request, response);
            }
            else if (this.action.equalsIgnoreCase("getDownloadStatus")) {
                MDMIOSEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'getDownloadStatus' action called...");
                final Object downloadStatus = WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "mobileConfigDownloadCompleted");
                response.getWriter().println((downloadStatus != null && downloadStatus.equals("true")) ? "true" : "false");
            }
            else if (this.action.equalsIgnoreCase("SelfEnrollGettingStarted")) {
                MDMIOSEnrollmentHandler.logger.log(Level.INFO, "MDMEnrollServlet: 'SelfEnrollGettingStarted' action called...");
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "iOS_Self_Enroll_Url_Access_Count");
                }
                request.setAttribute("isSelfEnroll", (Object)true);
                if (MDMEnrollmentHandler.isiPad(this.userAgent)) {
                    this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/tabletGettingStartedPage.jsp").forward((ServletRequest)request, (ServletResponse)response);
                }
                else if (MDMEnrollmentHandler.isiPhone(this.userAgent) || MDMEnrollmentHandler.isiPod(this.userAgent) || MDMEnrollmentHandler.isMac(this.userAgent)) {
                    this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/phoneGettingStartedPage.jsp").forward((ServletRequest)request, (ServletResponse)response);
                }
            }
            else if (this.action.equalsIgnoreCase("SelfEnrollADCredential")) {
                final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
                final Boolean showOwnedByCriteria = EnrollmentSettingsHandler.getInstance().selfEnrollmentGrouphasOwnedBySpecificSettings(MDMGroupHandler.getInstance().getSelfEnrollmentConfiguredGroups(customerId));
                request.setAttribute("showOwnedByFilter", (Object)showOwnedByCriteria);
                this.discoverSelfEnrollRequest(request, response);
            }
            else if (this.action.equalsIgnoreCase("install")) {
                this.discoverServerEnrollRequest(request, response);
            }
            else if (this.action.equalsIgnoreCase("selfEnroll")) {
                this.discoverAndRedirectToGettingStartedPage(request, response);
            }
            else if (this.action.equalsIgnoreCase("Authenticate")) {
                this.authenticate(request, response);
            }
            else if (this.action.equalsIgnoreCase("showAdminEnrollAuthPage")) {
                this.showAdminEnrollADAuthPage(request, response);
            }
            else if (this.action.equalsIgnoreCase("AuthenticateAdminEnrollADCredentials")) {
                this.authenticateAdminEnrollADCredentials(request, response, 1);
            }
            else if (this.action.equalsIgnoreCase("AdminEnrollPostAuthenticateUser")) {
                this.processAdminEnrollPostAuthenticateUser(request, response);
            }
            else {
                this.showEnrollByInviteLoginPage(request, response);
            }
        }
        catch (final JSONException ex) {
            MDMIOSEnrollmentHandler.logger.log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final Exception ex2) {
            MDMIOSEnrollmentHandler.logger.log(Level.WARNING, "Exception occurred while enrolling ios devce", ex2);
        }
    }
    
    @Override
    public void parseValidEnrollReq(final JSONObject jsonResponse, final HttpServletRequest request, final HttpServletResponse response) throws JSONException, ServletException, IOException {
        final String isQRSrc = request.getParameter("et");
        if (MDMStringUtils.isEmpty(isQRSrc)) {
            final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
            if (customerID != null) {
                MEMDMTrackParamManager.getInstance().incrementTrackValue(customerID, "QR_Enrollment_Module", "IOS_ENROLLMENTLINK_BROWSER_SRC");
            }
        }
        this.parseEnrollReq(jsonResponse, request, response);
    }
    
    private void discoverAndRedirectToGettingStartedPage(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        MDMIOSEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'selfEnroll' action called...");
        final boolean isEnrollAllowed = this.isEnrollmentAllowed(request);
        if (!isEnrollAllowed && (MDMEnrollmentHandler.isiPhone(this.userAgent) || MDMEnrollmentHandler.isiPod(this.userAgent) || MDMEnrollmentHandler.isiPad(this.userAgent) || MDMEnrollmentHandler.isMac(this.userAgent))) {
            this.loadErrorPage(request, response);
            return;
        }
        this.servletContext.getRequestDispatcher("/mdm/enroll?actionToCall=SelfEnrollGettingStarted").forward((ServletRequest)request, (ServletResponse)response);
    }
    
    private void showEnrollByInviteLoginPage(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
        if (evaluatorApi != null) {
            evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "iOS_Enrollment_Url_Access_Count");
        }
        this.discoverServerEnrollRequest(request, response);
    }
    
    private void downloadMobileConfig(final Long enrollmentRequestID, final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException, Exception {
        final int osVersion = this.userAgent.contains("OS 4_") ? 4 : 5;
        final int deviceType = getDeviceType(this.userAgent);
        byte[] bytes = IOSMobileConfigHandler.getInstance().generateMobileConfig(enrollmentRequestID, deviceType, osVersion);
        if (!this.userAgent.contains("OS 4_") && !this.userAgent.contains("OS 5_") && !this.userAgent.contains("OS 6_")) {
            bytes = PayloadSigningFactory.getInstance().signPayload(new String(bytes, "UTF-8"));
        }
        response.setContentType("application/x-apple-aspen-config");
        response.setHeader("Content-Disposition", "attachment;filename=mdm.mobileconfig");
        final OutputStream os = (OutputStream)response.getOutputStream();
        os.write(bytes);
        os.flush();
        os.close();
        WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "validationComplete");
        WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "mobileConfigDownloadCompleted", (Object)"true");
    }
    
    private void downloadMobileConfig(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException, Exception {
        MDMIOSEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'getconfig' action called...");
        WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "mobileConfigDownloadCompleted", (Object)"false");
        final String validLogin = (String)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "validationComplete");
        final Long enrollmentRequestID = (Long)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid");
        final Integer enrollmentType = (Integer)DBUtil.getValueFromDB("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID, "ENROLLMENT_TYPE");
        if (enrollmentType != 3) {
            final int eridValidStatus = this.validateInvitationEnrollmentRequest(enrollmentRequestID, 1);
            if (eridValidStatus != 1) {
                this.loadInvitationIdErrorPage(request, response, eridValidStatus);
                return;
            }
        }
        if (validLogin != null) {
            if (validLogin.equalsIgnoreCase("true")) {
                try {
                    this.downloadMobileConfig(enrollmentRequestID, request, response);
                }
                catch (final Exception e) {
                    MDMIOSEnrollmentHandler.logger.log(Level.WARNING, "Exception in MDMEnrollServlet: getconfig action {0}", e);
                }
                return;
            }
        }
        try {
            this.discoverServerEnrollRequest(request, response);
        }
        catch (final Exception e) {
            MDMIOSEnrollmentHandler.logger.log(Level.SEVERE, "Exception while redirecting to discover again {0}", e);
        }
    }
    
    private void refetchMobileConfig(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException, Exception {
        final String sEnrollmentReqId = request.getParameter("erid");
        final Long erid = Long.valueOf(sEnrollmentReqId);
        final boolean isDownloadAllowed = MDMiOSEntrollmentUtil.getInstance().getReenrollReq(erid);
        if (isDownloadAllowed) {
            MEMDMTrackParamManager.getInstance().incrementTrackValue(MDMiOSEntrollmentUtil.getInstance().getCustomerIdForErid(erid), "Enrollment_Module", "IOS_RE_ESTABLISH_COUNT");
            try {
                final List<Long> eridAsList = new ArrayList<Long>();
                eridAsList.add(erid);
                final Long rescId = ManagedDeviceHandler.getInstance().getResourceIDFromErid(eridAsList).getJSONArray("ResourceID").getLong(0);
                final int modelType = MDMUtil.getInstance().getModelTypeFromDB(rescId);
                if (modelType == 2) {
                    this.userAgent = this.userAgent.replaceAll("Macintosh", "iPad");
                }
                this.downloadMobileConfig(erid, request, response);
            }
            catch (final Exception e) {
                MDMIOSEnrollmentHandler.logger.log(Level.WARNING, "Exception in MDMEnrollServlet: refetch action {0}", e);
            }
        }
        else {
            MDMIOSEnrollmentHandler.logger.log(Level.INFO, "User trying to refetch mobile config but action is not allowed!.. ERID: {0}", sEnrollmentReqId);
        }
    }
    
    private boolean isInvalidBrowser(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        boolean validMobileBrowser = false;
        boolean validTabletBrowser = false;
        if (MDMEnrollmentHandler.isiPhone(this.userAgent) || MDMEnrollmentHandler.isiPod(this.userAgent) || MDMEnrollmentHandler.isMac(this.userAgent)) {
            validMobileBrowser = true;
        }
        else if (MDMEnrollmentHandler.isiPad(this.userAgent)) {
            validTabletBrowser = true;
        }
        if (!validMobileBrowser && !validTabletBrowser) {
            this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/otherInvalidBrowser.jsp").forward((ServletRequest)request, (ServletResponse)response);
            return true;
        }
        if (!MDMEnrollmentHandler.isSafariBrowser(this.userAgent) && !MDMEnrollmentHandler.isAppleWebView(this.userAgent)) {
            request.setAttribute("browserValid", (Object)false);
            if (validMobileBrowser) {
                request.setAttribute("userAgentType", (Object)"phone");
            }
            else {
                request.setAttribute("userAgentType", (Object)"tablet");
            }
            this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/phoneInvalidBrowser.jsp").forward((ServletRequest)request, (ServletResponse)response);
            return true;
        }
        return false;
    }
    
    private void showSelfEnrollGettingStartedPage(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        MDMIOSEnrollmentHandler.logger.log(Level.INFO, "MDMEnrollServlet: 'SelfEnrollGettingStarted' action called...");
        final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
        if (evaluatorApi != null) {
            evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "iOS_Self_Enroll_Url_Access_Count");
        }
        request.setAttribute("isSelfEnroll", (Object)true);
        if (MDMEnrollmentHandler.isiPad(this.userAgent)) {
            this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/tabletGettingStartedPage.jsp").forward((ServletRequest)request, (ServletResponse)response);
        }
        else if (MDMEnrollmentHandler.isiPhone(this.userAgent) || MDMEnrollmentHandler.isiPod(this.userAgent) || MDMEnrollmentHandler.isMac(this.userAgent)) {
            this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/phoneGettingStartedPage.jsp").forward((ServletRequest)request, (ServletResponse)response);
        }
    }
    
    private void showMobileConfigDownloadPage(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException, Exception {
        MDMIOSEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'download' action called...");
        final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
        if (evaluatorApi != null) {
            evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "iOS_Profile_Download_Count");
        }
        final int eridValidStatus = this.validateiOSInvitationEnrollmentRequestFromSession(request, response);
        if (eridValidStatus != 1) {
            this.loadInvitationIdErrorPage(request, response, eridValidStatus);
            return;
        }
        final String validLogin = (String)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "validationComplete");
        if (validLogin != null) {
            if (validLogin.equalsIgnoreCase("true")) {
                request.setAttribute("downloadTime", (Object)MDMUtil.getCurrentTimeInMillis());
                request.setAttribute("platform", (Object)1);
                if (MDMEnrollmentHandler.isiPad(this.userAgent)) {
                    request.setAttribute("userAgentType", (Object)"tablet");
                }
                else {
                    request.setAttribute("userAgentType", (Object)"phone");
                }
                final Long enrollmentRequestID = (Long)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid");
                final String path = this.getTermsPath(request, response, enrollmentRequestID);
                request.setAttribute("path", (Object)path);
                if (ManagedDeviceHandler.getInstance().getPlatformForErid((long)enrollmentRequestID) == 0) {
                    this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmPlatformNeutralConfigDownload.jsp").forward((ServletRequest)request, (ServletResponse)response);
                    return;
                }
                this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmConfigDownload.jsp").forward((ServletRequest)request, (ServletResponse)response);
                return;
            }
        }
        try {
            this.discoverServerEnrollRequest(request, response);
        }
        catch (final Exception e) {
            MDMIOSEnrollmentHandler.logger.log(Level.SEVERE, "Exception while redirecting to discover again {0}", e);
        }
    }
    
    private void getEnrollSuccessPage(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException, Exception {
        MDMIOSEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'success' action called...");
        WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "validationComplete");
        WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "CERTIFICATE_CREATION_DATE");
        WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "FQDN");
        final JSONObject requestDetails = new JSONObject();
        Long enrollmentRequestID = (Long)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid");
        if (enrollmentRequestID == null) {
            enrollmentRequestID = -1L;
        }
        final JSONObject msgRequest = new JSONObject();
        msgRequest.put("ENROLLMENT_REQUEST_ID", (Object)enrollmentRequestID);
        final String downloadtime = request.getParameter("downloadTime");
        msgRequest.put("ProfileDownloadTime", Long.parseLong(downloadtime));
        requestDetails.put("MsgRequestType", (Object)"getEnrollStatus");
        requestDetails.put("MsgRequest", (Object)msgRequest);
        final JSONObject responseJSON = MDMProfileInstallationHandler.getInstance().getMDMEnrollmentStatus(requestDetails);
        request.setAttribute("enrollmentStatus", (Object)responseJSON);
        SYMClientUtil.writeJsonFormattedResponse(response);
        response.getWriter().println(responseJSON.toString());
    }
    
    private void getEnrollStatus(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final JSONObject requestDetails = new JSONObject(((org.json.simple.JSONObject)new JSONParser().parse((Reader)request.getReader())).toJSONString());
        final JSONObject responseJSON = MDMProfileInstallationHandler.getInstance().getMDMEnrollmentStatus(requestDetails);
        SYMClientUtil.writeJsonFormattedResponse(response);
        response.getWriter().println(responseJSON.toString());
    }
    
    protected int validateiOSInvitationEnrollmentRequestFromSession(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final Long enrollmentRequestID = (Long)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid");
        if (enrollmentRequestID == null) {
            return 7;
        }
        return this.validateInvitationEnrollmentRequest(enrollmentRequestID, 1);
    }
    
    @Override
    public void processQRSucess(final HttpServletRequest request, final HttpServletResponse response, final JSONObject responseJSON) throws JSONException, ServletException, IOException {
        if (responseJSON.get("AuthMode").toString().equalsIgnoreCase("OTP")) {
            WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "validationComplete", (Object)"true");
            WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "erid", responseJSON.get("ENROLLMENT_REQUEST_ID"));
            WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "cust", responseJSON.get("CUSTOMER_ID"));
            request.setAttribute("downloadTime", (Object)MDMUtil.getCurrentTimeInMillis());
        }
        responseJSON.put("MessageResponse", (Object)new JSONObject());
    }
    
    @Override
    public String getTermsPath(final HttpServletRequest request, final HttpServletResponse response, final Long enrollmentRequestID) throws JSONException, ServletException, IOException {
        final Locale locale = request.getLocale();
        String localecode = null;
        try {
            localecode = locale.toString();
        }
        catch (final Exception exp) {
            MDMIOSEnrollmentHandler.logger.log(Level.SEVERE, "Exception while getting device lang", exp);
        }
        final int ownedby = MDMEnrollmentUtil.getInstance().getOwnedByforEnrollmentRequest(enrollmentRequestID);
        final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(request.getParameterMap());
        final String path = MDMTermsHandler.getInstance().getPathForOwnedBy(ownedby, localecode, key, enrollmentRequestID);
        return path;
    }
    
    public static void setShowNewEnrollmentWorkFlowAttibute(final HttpServletRequest request) {
        final VersionChecker versionChecker = new VersionChecker();
        final String osVersionUserAgent = MDMEnrollmentHandler.getOSVersionFromUserAgent(request);
        final String userAgentString = request.getHeader("User-Agent");
        if (!MDMEnrollmentHandler.isiPad(userAgentString) && !MDMEnrollmentHandler.isiPhone(userAgentString)) {
            return;
        }
        final String enrollmentChangeVersion = "12.2.0.0";
        boolean shownewSteps = false;
        if ((osVersionUserAgent != null && versionChecker.isGreaterIncludeTrailingZeros(osVersionUserAgent, enrollmentChangeVersion)) || enrollmentChangeVersion.equalsIgnoreCase(osVersionUserAgent)) {
            shownewSteps = true;
        }
        request.setAttribute("showNewiOS12EnrollmentSteps", (Object)shownewSteps);
    }
    
    public static int getDeviceType(final String userAgent) {
        if (MDMEnrollmentHandler.isMac(userAgent)) {
            return 3;
        }
        if (MDMEnrollmentHandler.isiPad(userAgent)) {
            return 2;
        }
        return 1;
    }
    
    static {
        MDMIOSEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
