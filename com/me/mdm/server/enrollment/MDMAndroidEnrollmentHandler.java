package com.me.mdm.server.enrollment;

import java.util.Hashtable;
import com.me.mdm.server.settings.MdAgentDownloadInfoData;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.core.auth.APIKey;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import java.net.URLEncoder;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.OutputStream;
import java.io.InputStream;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Properties;
import com.adventnet.sym.webclient.mdm.enroll.SAFEDeviceList;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import java.util.Map;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.server.settings.MDMAgentSettingsHandler;
import javax.servlet.ServletContext;
import org.json.JSONObject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class MDMAndroidEnrollmentHandler extends MDMEnrollmentHandler
{
    private Logger logger;
    
    public MDMAndroidEnrollmentHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public void initEnrollemnt(final HttpServletRequest request, final HttpServletResponse response, final JSONObject enrollData) {
        try {
            this.servletPath = String.valueOf(enrollData.get("servletPath"));
            this.servletContext = (ServletContext)enrollData.get("servletContext");
            this.action = enrollData.optString("action", (String)null);
            this.userAgent = String.valueOf(enrollData.get("userAgent"));
            this.pathInfo = enrollData.optString("pathInfo", (String)null);
            MDMAndroidEnrollmentHandler.platform = "android";
            this.setCommonRequestAttibutes(request);
            if (this.action != null && this.action.equalsIgnoreCase("download")) {
                this.showAndroidAuthSucessJSONDownloadPagee(request, response);
            }
            else if (this.action != null && this.action.equalsIgnoreCase("Authenticate")) {
                this.authenticate(request, response);
            }
            else if (this.action != null && this.action.equalsIgnoreCase("SelfEnrollADCredential")) {
                this.discoverSelfEnrollRequest(request, response);
            }
            else if (this.action != null && this.action.equalsIgnoreCase("showAdminEnrollAuthPage")) {
                this.showAdminEnrollADAuthPage(request, response);
            }
            else if (this.action != null && this.action.equalsIgnoreCase("AuthenticateAdminEnrollADCredentials")) {
                this.authenticateAdminEnrollADCredentials(request, response, 2);
            }
            else if (this.action != null && this.action.equalsIgnoreCase("AdminEnrollPostAuthenticateUser")) {
                this.processAdminEnrollPostAuthenticateUser(request, response);
            }
            else {
                final int downloadMode = MDMAgentSettingsHandler.getInstance().getAndroidAgentDownloadMode();
                switch (downloadMode) {
                    case 1:
                    case 2:
                    case 4: {
                        this.logger.log(Level.INFO, "Android Agent Download Mode : {0}", downloadMode);
                        this.handleDownloadFromServer(request, response);
                        break;
                    }
                    case 3: {
                        this.logger.log(Level.INFO, "Android Agent Download Mode : Playstore Download ({0})", downloadMode);
                        this.handleStoreDownload(request, response);
                        break;
                    }
                    default: {
                        this.logger.log(Level.INFO, "Android Agent Download Mode : Default mode - Playstore download ({0})", downloadMode);
                        this.handleStoreDownload(request, response);
                        break;
                    }
                }
            }
        }
        catch (final JSONException ex) {
            Logger.getLogger(MDMAndroidEnrollmentHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
        }
        catch (final Exception ex2) {
            Logger.getLogger(MDMAndroidEnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex2);
        }
    }
    
    private void handleStoreDownload(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            final Long customerID = (this.pathInfo != null && !this.pathInfo.trim().equalsIgnoreCase("/")) ? MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(Long.parseLong(this.pathInfo.substring(1))) : CustomerInfoUtil.getInstance().getCustomerId();
            final int downloadMode = MDMAgentSettingsHandler.getInstance().getAndroidDefaultDownloadMode();
            final Map<Integer, String> downloadURL = this.getAndroidAgentDownloadUrl(customerID, 2, downloadMode);
            final String safeDownloadURL = downloadURL.get(3);
            final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
            request.setAttribute("androidURL", (Object)(downloadURL.get(2) + "&MDMSrc=3"));
            request.setAttribute("safeURL", (Object)(safeDownloadURL + "&MDMSrc=3"));
            if (this.action == null && (this.pathInfo == null || this.pathInfo.equals("/"))) {
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Android_Enrollment_Url_Access_Count");
                }
                this.isSelfEnrollError = !this.isEnrollmentAllowed(request);
                if (this.isSelfEnrollError) {
                    this.loadErrorPage(request, response);
                }
                else {
                    this.enrollInformation(request);
                    this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmPlayStoreDownload.jsp").forward((ServletRequest)request, (ServletResponse)response);
                }
            }
            else {
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Android_Enrollment_Url_Access_Count");
                }
                if (this.discoverServerEnrollRequest(request, response)) {
                    this.enrollInformation(request);
                    this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmPlayStoreDownload.jsp").forward((ServletRequest)request, (ServletResponse)response);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred whild handling playstore download", ex);
        }
    }
    
    private void enrollInformation(final HttpServletRequest request) throws Exception {
        final Boolean isSelfEnrollment = true;
        final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
        if (evaluatorApi != null) {
            evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Android_Self_Enroll_Url_Access_Count");
        }
        final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
        final String serverIP = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
        final int serverPort = ((Hashtable<K, Integer>)natProps).get("NAT_HTTPS_PORT");
        final Boolean isSAFE = SAFEDeviceList.getInstance().isSAFEDevice(this.userAgent);
        request.setAttribute("serverIP", (Object)serverIP);
        request.setAttribute("serverPort", (Object)serverPort);
        request.setAttribute("isSelfEnrollment", (Object)isSelfEnrollment);
        request.setAttribute("isSAFE", (Object)isSAFE);
        final int above4_2 = SAFEDeviceList.getInstance().isSAFEAbove4_2(this.userAgent);
        request.setAttribute("is4_2Above", (Object)above4_2);
    }
    
    private void handleDownloadFromServer(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            final String isApp = request.getParameter("isApp");
            final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
            if (this.servletPath.indexOf(".mob") != -1) {
                final String mobAction = this.servletPath.substring(1, this.servletPath.indexOf(".mob"));
                this.servletContext.getRequestDispatcher("/mdm/enroll?actionToCall=" + mobAction).forward((ServletRequest)request, (ServletResponse)response);
                return;
            }
            if (this.action == null && (this.pathInfo == null || this.pathInfo.equals("/")) && MDMStringUtils.isEmpty(isApp)) {
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Android_Enrollment_Url_Access_Count");
                }
                this.servletContext.getRequestDispatcher("/mdm/enroll?actionToCall=selfEnroll").forward((ServletRequest)request, (ServletResponse)response);
                return;
            }
            if (this.action == null && (this.pathInfo == null || this.pathInfo.equals("/")) && !MDMStringUtils.isEmpty(isApp)) {
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Android_Enrollment_Url_Access_Count");
                }
                this.discoverSelfEnrollmentFromApp(request, response);
            }
            else if (this.action == null) {
                this.action = "";
            }
            if (this.pathInfo != null && this.action.equalsIgnoreCase("")) {
                if (evaluatorApi != null) {
                    evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Android_Enrollment_Url_Access_Count");
                }
                if (this.discoverServerEnrollRequest(request, response)) {
                    this.servletContext.getRequestDispatcher("/mdm/enroll" + this.pathInfo + "?actionToCall=downloadAgent").forward((ServletRequest)request, (ServletResponse)response);
                }
                return;
            }
            if (this.action.equalsIgnoreCase("getCertificateDownloadStatus")) {
                this.logger.log(Level.FINE, "MDMEnrollServlet: 'getCertificateDownloadStatus' action called...");
                String downloadStatus = (String)request.getAttribute("certificateDownloadCompleted");
                if (downloadStatus == null) {
                    downloadStatus = "false";
                }
                else {
                    request.removeAttribute("certificateDownloadCompleted");
                }
                response.getWriter().println(downloadStatus);
            }
            else if (this.action.equalsIgnoreCase("getAgentDownloadStatus")) {
                this.logger.log(Level.FINE, "MDMEnrollServlet: 'getAgentDownloadStatus' action called...");
                String downloadStatus = (String)request.getAttribute("apkDownloadCompleted");
                if (downloadStatus == null) {
                    downloadStatus = "false";
                }
                response.getWriter().println(downloadStatus);
            }
            else if (this.action.equalsIgnoreCase("agentDownloadSuccess")) {
                try {
                    final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
                    final String serverIP = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
                    final int serverPort = ((Hashtable<K, Integer>)natProps).get("NAT_HTTPS_PORT");
                    request.setAttribute("serverIP", (Object)serverIP);
                    request.setAttribute("serverPort", (Object)serverPort);
                    this.logger.log(Level.FINE, "MDMEnrollServlet: 'agentDownloadSuccess' action called...");
                    this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmAPKDownloadSuccess.jsp").forward((ServletRequest)request, (ServletResponse)response);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            else if (this.action.equalsIgnoreCase("downloadAgent")) {
                try {
                    final String downloadPath = "/jsp/mdm/enroll/mdmSelectAPK.jsp";
                    final boolean isAndroidAgent = true;
                    request.setAttribute("isAndroidAgent", (Object)isAndroidAgent);
                    request.setAttribute("above4_2", (Object)2);
                    final String downloadURL = this.getHTTPSDownloadURL();
                    request.setAttribute("downloadURL", (Object)downloadURL);
                    final String agentName = "MDMAndroidAgent.apk";
                    final String httpsDownloadURL = "/agent/" + agentName;
                    request.setAttribute("appHttpsUrl", (Object)httpsDownloadURL);
                    request.setAttribute("appExternalLink", (Object)this.getExternalAppUrl(2));
                    request.setAttribute("httpPingImgUrl", (Object)(this.getHttpTestImgUrl() + "?" + System.currentTimeMillis()));
                    request.setAttribute("manageenginePingImgUrl", (Object)(this.getExternalTestImgUrl() + "?" + System.currentTimeMillis()));
                    final String isForwardingServerEnable = MDMUtil.getSyMParameter("forwarding_server_config");
                    boolean isEnableForwrdingServer = false;
                    if (isForwardingServerEnable != null) {
                        isEnableForwrdingServer = Boolean.valueOf(isForwardingServerEnable);
                    }
                    request.setAttribute("isEnableForwrdingServer", (Object)isEnableForwrdingServer);
                    final Boolean isThirdPartyEnabled = ApiFactoryProvider.getServerSettingsAPI().getCertificateType() == 2;
                    this.logger.log(Level.FINE, "is thrid party enabled {0}", isThirdPartyEnabled);
                    request.setAttribute("isThirdPartyEnabled", (Object)isThirdPartyEnabled);
                    final int downloadMode = MDMAgentSettingsHandler.getInstance().getAndroidAgentDownloadMode();
                    if (downloadMode == 4) {
                        final Long customerID = (this.pathInfo != null && !this.pathInfo.trim().equalsIgnoreCase("/")) ? MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(Long.parseLong(this.pathInfo.substring(1))) : CustomerInfoUtil.getInstance().getCustomerId();
                        final Map<Integer, String> downloadURLS = this.getAndroidAgentDownloadUrl(customerID, 2, downloadMode);
                        final String safeDownloadURL = downloadURLS.containsKey(3) ? downloadURLS.get(3) : "https://play.google.com/store/apps/details?id=com.manageengine.mdm.android";
                        final String androidDownloadURL = downloadURLS.containsKey(2) ? downloadURLS.get(2) : "https://play.google.com/store/apps/details?id=com.manageengine.mdm.android";
                        request.setAttribute("androidURL", (Object)(androidDownloadURL + "&MDMSrc=3"));
                        request.setAttribute("safeURL", (Object)(safeDownloadURL + "&MDMSrc=3"));
                    }
                    request.setAttribute("downloadMode", (Object)downloadMode);
                    final Properties natProps2 = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
                    final String serverIP2 = ((Hashtable<K, String>)natProps2).get("NAT_ADDRESS");
                    final int serverPort2 = ((Hashtable<K, Integer>)natProps2).get("NAT_HTTPS_PORT");
                    request.setAttribute("serverIP", (Object)serverIP2);
                    request.setAttribute("serverPort", (Object)serverPort2);
                    this.logger.log(Level.INFO, "Forwarding Server : {0}", isEnableForwrdingServer);
                    this.logger.log(Level.INFO, "Third party certificate : {0}", isThirdPartyEnabled);
                    this.logger.log(Level.FINE, "MDMEnrollServlet: 'downloadAndroidAgent' action called...");
                    this.servletContext.getRequestDispatcher(downloadPath).forward((ServletRequest)request, (ServletResponse)response);
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "Exception occurred in android enrollment action call = downloadAgent");
                }
            }
            else if (this.action.equalsIgnoreCase("installCertificate")) {
                try {
                    this.logger.log(Level.FINE, "MDMEnrollServlet: 'installCertificate' action called...");
                    response.setContentType("application/x-x509-ca-cert");
                    response.setHeader("Content-Disposition", "attachment;filename=server.crt");
                    final String server_home = System.getProperty("server.home");
                    final String sPayLoadFilePath = this.getApacheCrtDir();
                    final InputStream is = ApiFactoryProvider.getFileAccessAPI().readFile(sPayLoadFilePath);
                    int read = 0;
                    final byte[] bytes = new byte[4096];
                    final OutputStream os = (OutputStream)response.getOutputStream();
                    while ((read = is.read(bytes)) != -1) {
                        this.logger.log(Level.FINE, "MDMEnrollServlet: bytes {0}", bytes);
                        os.write(bytes, 0, read);
                    }
                    os.flush();
                    os.close();
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
                request.setAttribute("certificateDownloadCompleted", (Object)"true");
            }
            else if (this.action.equalsIgnoreCase("DownloadAPK")) {
                this.logger.log(Level.FINE, "MDMEnrollServlet: DownloadAPK ");
                final String agentFileName = "MDMAndroidAgent.apk";
                request.setAttribute("apkDownloadCompleted", (Object)"true");
                response.setContentType("application/vnd.android.package-archive");
                response.setHeader("Content-Disposition", "attachment;filename=" + agentFileName);
                final String sPayLoadFilePath = "/agent/" + agentFileName;
                final ServletContext ctx = this.servletContext;
                final InputStream is2 = ctx.getResourceAsStream(sPayLoadFilePath);
                int read2 = 0;
                final byte[] bytes2 = new byte[4096];
                final OutputStream os2 = (OutputStream)response.getOutputStream();
                while ((read2 = is2.read(bytes2)) != -1) {
                    os2.write(bytes2, 0, read2);
                }
                os2.flush();
                os2.close();
                is2.close();
                this.logger.log(Level.FINE, "DownloadAPK: completed ");
            }
            else if (this.action.equalsIgnoreCase("DownloadCert")) {
                try {
                    final String isAndroid = request.getParameter("isAndroidAgent");
                    final String above4_2 = request.getParameter("above4_2");
                    final String downloadPath2 = "/jsp/mdm/enroll/mdmCertificateDownload.jsp";
                    if (isAndroid != null) {
                        request.setAttribute("isAndroidAgent", (Object)Boolean.valueOf(isAndroid));
                    }
                    if (above4_2 != null) {
                        request.setAttribute("above4_2", (Object)Integer.valueOf(above4_2));
                    }
                    this.servletContext.getRequestDispatcher(downloadPath2).forward((ServletRequest)request, (ServletResponse)response);
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if (this.action.equalsIgnoreCase("certDownloadSuccess")) {
                try {
                    this.logger.log(Level.FINE, "MDMEnrollServlet: 'certDownloadSuccess' action called...");
                    this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmAndroidAgentDownload.jsp").forward((ServletRequest)request, (ServletResponse)response);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            else if (this.action.equalsIgnoreCase("selfEnroll")) {
                this.logger.log(Level.FINE, "MDMEnrollServlet: 'selfEnroll' action called...");
                request.setAttribute("fromInstallAction", (Object)"true");
                this.isSelfEnrollError = !this.isEnrollmentAllowed(request);
                if (this.isSelfEnrollError) {
                    this.loadErrorPage(request, response);
                }
                else {
                    try {
                        final String androidEnrollmentUrl = "/mdm/enroll?actionToCall=downloadAgent";
                        request.setAttribute("androidEnrollmentUrl", (Object)androidEnrollmentUrl);
                        this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmSelfEnrollTablet.jsp").forward((ServletRequest)request, (ServletResponse)response);
                    }
                    catch (final Exception ex) {
                        this.logger.log(Level.WARNING, "Exception while getting Android enroll properties", ex);
                    }
                }
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.WARNING, "Exception occured whiled hadling the download from server", ex2);
        }
    }
    
    private static Boolean isValidIp(final String serverIP) {
        final String ipAddPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        final Pattern ipPattern = Pattern.compile(ipAddPattern);
        final Matcher matcher = ipPattern.matcher(serverIP);
        return matcher.matches();
    }
    
    private String getApacheCrtDir() {
        String baseDir = "";
        try {
            baseDir = SSLCertificateUtil.getInstance().getServerCertificateFilePath();
            baseDir = baseDir.replace("/", "\\");
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while getting webserver properties.. ", e);
        }
        return baseDir;
    }
    
    private String getHTTPSDownloadURL() {
        String downloadURL = "";
        try {
            downloadURL = MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 2);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while build URL", ex);
        }
        return downloadURL;
    }
    
    @Override
    public void parseValidEnrollReq(final JSONObject jsonResponse, final HttpServletRequest request, final HttpServletResponse response) throws JSONException, ServletException, IOException {
        try {
            final String isApp = request.getParameter("isApp");
            final String isQRSrc = request.getParameter("et");
            final Long enrollmentRequestID = (Long)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid");
            Long customerId = MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(enrollmentRequestID);
            if (customerId == null) {
                customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
            }
            if (MDMStringUtils.isEmpty(isApp) && MDMStringUtils.isEmpty(isQRSrc)) {
                this.enrollInformation(request);
                if (customerId != null) {
                    MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "QR_Enrollment_Module", "ANDROID_ENROLLMENTLINK_BROWSER_SRC");
                }
            }
            else {
                if (!MDMStringUtils.isEmpty(isApp) && customerId != null) {
                    MEMDMTrackParamManager.getInstance().incrementTrackValue(customerId, "QR_Enrollment_Module", "ANDROID_ENROLLMENTLINK_APP_SRC");
                }
                this.parseEnrollReq(jsonResponse, request, response);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(MDMAndroidEnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getHttpTestImgUrl() {
        String url = ApiFactoryProvider.getUtilAccessAPI().getHttpServerPingURL();
        if (url != null) {
            url += "/images/spacer.png";
        }
        return url;
    }
    
    public String getExternalTestImgUrl() throws Exception {
        final Properties props = MDMUtil.getDCServerInfo();
        final String url = "https://www.manageengine.com/mobile-device-management/help/mobile-device-manager.png";
        return url;
    }
    
    public String getExternalAppUrl(final int appType) throws Exception {
        FileInputStream inputStream = null;
        final String confDir = System.getProperty("server.home") + File.separator + "conf";
        final Properties appUrls = new Properties();
        final File appConf = new File(confDir + File.separator + "mdmagentdetails.conf");
        inputStream = new FileInputStream(appConf.getCanonicalPath());
        appUrls.load(inputStream);
        String appUrl = null;
        switch (appType) {
            case 2: {
                appUrl = appUrls.getProperty("AndroidMEMDMAppUrl");
                break;
            }
            case 6: {
                appUrl = appUrls.getProperty("AndroidNFCAppUrl");
                break;
            }
        }
        return appUrl;
    }
    
    @Override
    public void processQRSucess(final HttpServletRequest request, final HttpServletResponse response, final JSONObject responseJSON) throws JSONException, ServletException, IOException, UnsupportedOperationException {
        final String isApp = request.getParameter("isApp");
        if (isApp != null) {
            if (Boolean.parseBoolean(isApp)) {
                final JSONObject responseMessage = this.getAndroidonAuthSuccessJSON(Long.parseLong(String.valueOf(WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid"))));
                responseJSON.put("MessageResponse", (Object)responseMessage);
                WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "erid");
                return;
            }
        }
        try {
            request.setAttribute("appDownloadURL", (Object)(MDMEnrollmentUtil.getInstance().getServerBaseURL() + "/mdm/enroll" + "/" + responseJSON.getLong("ENROLLMENT_REQUEST_ID") + "?actionToCall=downloadAgent"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error in Android processQRSucess : ", ex);
        }
        MEMDMTrackParamManager.getInstance().incrementTrackValue(responseJSON.optLong("CUSTOMER_ID"), "QR_Enrollment_Module", "QR_ANDROID_NON_APP_SRC");
        request.setAttribute("androidNonAppQRSrc", (Object)true);
        this.loadErrorPage(request, response);
    }
    
    public JSONObject getAndroidonAuthSuccessJSON(final Long erid) {
        final JSONObject sucessJSON = new JSONObject();
        final HashMap userDetailsJSON = ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(erid);
        final JSONObject enrollmentDetailsJSON = MDMEnrollmentRequestHandler.getInstance().getEnrollmentDetails(erid);
        try {
            sucessJSON.put("device_erid", (Object)erid.toString());
            sucessJSON.put("device_customerid", (Object)userDetailsJSON.get("CUSTOMER_ID").toString());
            sucessJSON.put("device_username", (Object)URLEncoder.encode(userDetailsJSON.get("NAME"), "UTF-8"));
            sucessJSON.put("device_useremail", (Object)URLEncoder.encode(userDetailsJSON.get("EMAIL_ADDRESS"), "UTF-8"));
            final JSONObject apiJSON = new JSONObject();
            apiJSON.put("ENROLLMENT_REQUEST_ID", (Object)erid);
            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(apiJSON);
            sucessJSON.put("Services", (Object)key.toClientJSON());
            sucessJSON.put("serverName", enrollmentDetailsJSON.get("server_name"));
            sucessJSON.put("portNumber", enrollmentDetailsJSON.get("server_port"));
            final JSONObject jsonObject = sucessJSON;
            final String s = "IsOnPremise";
            CustomerInfoUtil.getInstance();
            jsonObject.put(s, !CustomerInfoUtil.isSAS());
            sucessJSON.put("IsLanguagePackEnabled", LicenseProvider.getInstance().isLanguagePackEnabled());
            sucessJSON.put("device_ownedby", enrollmentDetailsJSON.get("owned_by"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception getAndroidonAuthSuccessJSON", ex);
        }
        return sucessJSON;
    }
    
    private void showAndroidAuthSucessJSONDownloadPagee(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException, JSONException {
        request.setAttribute("platform", (Object)2);
        request.setAttribute("userAgentType", (Object)"tablet");
        final String sEnrollRequestID = String.valueOf(WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid"));
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("MessageResponse", (Object)this.getAndroidonAuthSuccessJSON(Long.valueOf(sEnrollRequestID)));
        request.setAttribute("responseJSON", (Object)responseJSON);
        if (ManagedDeviceHandler.getInstance().getPlatformForErid(Long.valueOf(sEnrollRequestID)) == 0) {
            this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmPlatformNeutralConfigDownload.jsp").forward((ServletRequest)request, (ServletResponse)response);
        }
        else {
            this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmConfigDownload.jsp").forward((ServletRequest)request, (ServletResponse)response);
        }
    }
    
    private void discoverSelfEnrollmentFromApp(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final boolean isEnrollAllowed = this.isEnrollmentAllowed(request);
        if (!isEnrollAllowed) {
            this.loadErrorPage(request, response);
            return;
        }
        request.setAttribute("isSelfEnroll", (Object)true);
        this.servletContext.getRequestDispatcher("/mdm/enroll?actionToCall=SelfEnrollADCredential").forward((ServletRequest)request, (ServletResponse)response);
    }
    
    @Override
    public String getTermsPath(final HttpServletRequest request, final HttpServletResponse response, final Long enrollmentRequestId) throws Exception {
        return "";
    }
    
    public Map<Integer, String> addAndroidAgentDetails(final Long customerId, final int platformType, final int downloadMode) {
        final MdAgentDownloadInfoData androidData = new MdAgentDownloadInfoData();
        androidData.addCustomerID(customerId).addDownloadMode(downloadMode).addPlatform(platformType);
        final MdAgentDownloadInfoData[] agentDownloadInfoDataArray = { androidData };
        MDMAgentSettingsHandler.getInstance().addMDAgentDownloadInfo(agentDownloadInfoDataArray);
        return this.getAgentDownloadURL(platformType, customerId);
    }
    
    public Map<Integer, String> getAndroidAgentDownloadUrl(final Long customerId, final int platformType, final int downloadMode) {
        Map<Integer, String> downloadUrl = new HashMap<Integer, String>();
        downloadUrl = this.getAgentDownloadURL(platformType, customerId);
        if (downloadUrl.isEmpty()) {
            downloadUrl = this.addAndroidAgentDetails(customerId, platformType, downloadMode);
        }
        return downloadUrl;
    }
}
