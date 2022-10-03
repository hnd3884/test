package com.me.mdm.server.enrollment;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import org.json.JSONException;
import com.me.mdm.server.windows.apps.WpCompanyHubAppHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.ServletContext;
import org.json.JSONObject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class MDMWindowsEnrollmentHandler extends MDMEnrollmentHandler
{
    public static final int INVALID_WINDOWS_DEVICE = 0;
    public static final int WINDOWS_PHONE_OR_MOBILE_DEVICE = 1;
    public static final int WINDOWS_DESKTOP_DEVICE_ABOVE_10_0 = 2;
    private static Logger logger;
    
    @Override
    public void initEnrollemnt(final HttpServletRequest request, final HttpServletResponse response, final JSONObject enrollData) {
        try {
            this.servletPath = String.valueOf(enrollData.get("servletPath"));
            this.servletContext = (ServletContext)enrollData.get("servletContext");
            this.action = enrollData.optString("action", (String)null);
            this.userAgent = String.valueOf(enrollData.get("userAgent"));
            this.pathInfo = enrollData.optString("pathInfo", (String)null);
            MDMWindowsEnrollmentHandler.platform = "windowsPhone";
            final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
            if (this.servletPath.indexOf(".mob") != -1) {
                final String mobAction = this.servletPath.substring(1, this.servletPath.indexOf(".mob"));
                this.servletContext.getRequestDispatcher("/mdm/enroll?actionToCall=" + mobAction).forward((ServletRequest)request, (ServletResponse)response);
                return;
            }
            if (this.action == null && (this.pathInfo == null || this.pathInfo.equals("/"))) {
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Windows_Enrollment_Url_Access_Count");
                }
                this.servletContext.getRequestDispatcher("/mdm/enroll?actionToCall=selfEnroll").forward((ServletRequest)request, (ServletResponse)response);
                return;
            }
            if (this.action == null) {
                this.action = "";
            }
            boolean validMobileBrowser = false;
            final boolean validTabletBrowser = false;
            boolean validDesktopBrowser = false;
            if (this.userAgent.contains("Mobile") || this.userAgent.contains("Phone")) {
                validMobileBrowser = true;
            }
            else if (this.userAgent.contains("Windows NT") && this.isWindows10OrAbove(this.userAgent) && this.isSelfEnrollOrWindowsEnrollmentRequest(this.pathInfo) && !Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone"))) {
                validDesktopBrowser = true;
            }
            if (!validMobileBrowser && !validTabletBrowser && !validDesktopBrowser) {
                this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/otherInvalidBrowser.jsp").forward((ServletRequest)request, (ServletResponse)response);
                return;
            }
            if (this.pathInfo != null) {
                this.action = "";
            }
            if (this.action.equalsIgnoreCase("install")) {
                this.discoverServerEnrollRequest(request, response);
            }
            else if (this.action.equalsIgnoreCase("downloadXAP")) {
                MDMWindowsEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: downloadXAP ");
                final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
                WpCompanyHubAppHandler.getInstance().downloadWpCompanyHubApp(request, response, customerId, true);
                MDMWindowsEnrollmentHandler.logger.log(Level.FINE, "downloadXAP: completed ");
            }
            else if (this.action.equalsIgnoreCase("selfEnroll")) {
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Windows_Self_Enroll_Url_Access_Count");
                }
                this.discoverSelfEnrollRequest(request, response);
            }
            else {
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Windows_Enrollment_Url_Access_Count");
                }
                this.discoverServerEnrollRequest(request, response);
            }
        }
        catch (final JSONException jsEx) {
            MDMWindowsEnrollmentHandler.logger.log(Level.WARNING, "Excepton occurred while getting value from JSON", (Throwable)jsEx);
        }
        catch (final Exception ex) {
            MDMWindowsEnrollmentHandler.logger.log(Level.WARNING, "Exception occurred while windows enrollment", ex);
        }
    }
    
    private boolean isWindowsabove80(final String userAgent) {
        boolean isWindowsAbove80 = false;
        if (MDMEnrollmentHandler.isWindowsBrowser(userAgent) && !userAgent.contains("Windows Phone 7.") && !userAgent.contains("Windows Phone 8.0") && !userAgent.contains("Windows Phone 9.")) {
            isWindowsAbove80 = true;
        }
        return isWindowsAbove80;
    }
    
    private boolean isWindows10OrAbove(final String userAgent) {
        boolean isWindows10OrAbove = Boolean.FALSE;
        if (MDMEnrollmentHandler.isWindowsBrowser(userAgent) && (userAgent.contains("Windows Phone 10.") || userAgent.contains("Windows NT 10."))) {
            isWindows10OrAbove = Boolean.TRUE;
        }
        return isWindows10OrAbove;
    }
    
    private int getDeviceType(final String userAgent) {
        int deviceType = 0;
        if (userAgent.contains("Windows Phone")) {
            deviceType = 1;
        }
        else if (userAgent.contains("Windows NT") && this.isWindows10OrAbove(userAgent)) {
            deviceType = 2;
        }
        return deviceType;
    }
    
    private String getImageNameSuffix(final int deviceType) {
        String fileSuffix = "";
        if (deviceType == 2) {
            fileSuffix = "desktop";
        }
        return fileSuffix;
    }
    
    private Boolean isSelfEnrollOrWindowsEnrollmentRequest(String pathInfo) {
        if (pathInfo == null || pathInfo.trim().equalsIgnoreCase("") || pathInfo.replaceAll("/", "").trim().equalsIgnoreCase("")) {
            return Boolean.TRUE;
        }
        pathInfo = pathInfo.replaceAll("/", "");
        final Long erid = Long.parseLong(pathInfo);
        final JSONObject eridProps = MDMEnrollmentRequestHandler.getInstance().getEnrollmentRequestProperties(erid);
        final Integer platformType = eridProps.optInt("DeviceEnrollmentRequest.PLATFORM_TYPE");
        if (platformType == 3 || platformType == 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public void parseValidEnrollReq(final JSONObject jsonResponse, final HttpServletRequest request, final HttpServletResponse response) throws JSONException, ServletException, IOException {
        final JSONObject msgResponse = jsonResponse.getJSONObject("MsgResponse");
        final String authMode = String.valueOf(msgResponse.get("AuthMode"));
        final Long erid = msgResponse.optLong("EnrollmentRequestID", -1L);
        final boolean isWindowsAbove80 = this.isWindowsabove80(this.userAgent);
        final boolean isWindows10OrAbove = this.isWindows10OrAbove(this.userAgent);
        final int deviceType = this.getDeviceType(this.userAgent);
        request.setAttribute("isWindowsAbove80", (Object)isWindowsAbove80);
        request.setAttribute("isWindows10OrAbove", (Object)isWindows10OrAbove);
        request.setAttribute("isAppBasedEnrollment", (Object)Boolean.valueOf(MDMUtil.getSyMParameter("IsAppBasedEnrollmentForWindowsPhone")));
        request.setAttribute("storeAppUrl", (Object)"http://www.windowsphone.com/s?appid=551ab9a7-413b-4b79-8142-74550af0c72e");
        request.setAttribute("deviceType", (Object)deviceType);
        request.setAttribute("imageFileSuffix", (Object)this.getImageNameSuffix(deviceType));
        try {
            final Long customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
            String serverUrl = MDMEnrollmentUtil.getInstance().getServerBaseURL() + "/mdm/client/v1/wpdiscover/" + customerId;
            if (erid == -1L) {
                final APIKey key = MDMApiFactoryProvider.getMdmPurposeAPIKeyGenerator().generateAPIKey(new JSONObject().put("PURPOSE_KEY", 51));
                serverUrl = serverUrl + "?" + key.getAsURLParams();
            }
            if (MDMEnrollmentHandler.isPlatformIndependentRequest(erid)) {
                final JSONObject apiKeyJson = new JSONObject();
                apiKeyJson.put("ENROLLMENT_REQUEST_ID", (Object)erid);
                serverUrl = MDMDeviceAPIKeyGenerator.getInstance().getAPIKey(apiKeyJson).appendAsURLParams(serverUrl);
            }
            request.setAttribute("serverUrl", (Object)serverUrl);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmWindowsEnroll.jsp").forward((ServletRequest)request, (ServletResponse)response);
    }
    
    @Override
    public void processQRSucess(final HttpServletRequest request, final HttpServletResponse response, final JSONObject responseJSON) throws JSONException, ServletException, IOException {
        new UnsupportedOperationException("WINDOWS_QR_ENROLLMENT_NOT_SUPPORTED");
    }
    
    @Override
    public String getTermsPath(final HttpServletRequest request, final HttpServletResponse response, final Long enrollmentRequestId) throws Exception {
        return "";
    }
    
    static {
        MDMWindowsEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
    }
}
