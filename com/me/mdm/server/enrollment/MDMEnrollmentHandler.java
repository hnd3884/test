package com.me.mdm.server.enrollment;

import java.util.Hashtable;
import ua_parser.Client;
import ua_parser.Parser;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.adventnet.i18n.I18N;
import java.net.URLDecoder;
import java.io.Reader;
import org.json.simple.parser.JSONParser;
import com.me.mdm.core.enrollment.AdminEnrollmentHandler;
import org.apache.commons.lang.StringEscapeUtils;
import com.me.mdm.core.enrollment.OutOfBoxEnrollmentSettingsHandler;
import java.util.List;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Properties;
import com.me.devicemanagement.framework.server.certificate.SSLCertificateUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Date;
import com.me.mdm.server.drp.MDMRegistrationHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Map;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import org.apache.commons.lang.WordUtils;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import java.io.IOException;
import javax.servlet.ServletException;
import org.json.JSONException;
import org.json.JSONObject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.util.logging.Logger;

public abstract class MDMEnrollmentHandler
{
    private static Logger logger;
    public static final String USER_AGENT = "userAgent";
    public static final String REQUEST_ACTION = "action";
    public static final String REQUEST_PATH = "servletPath";
    public static final String IS_SELF_ENROLLMENT = "isSelfEnrollment";
    public static final String SERVLET_CONTEXT = "servletContext";
    public static final String PATH_INFO = "pathInfo";
    public static final String KEY_OS_PLATFORM = "osPlatform";
    public static final String KEY_DEVICE_TYPE = "DeviceType";
    protected String servletPath;
    protected ServletContext servletContext;
    protected String action;
    protected String userAgent;
    protected boolean isSelfEnrollError;
    protected String pathInfo;
    protected EnrollRestrictionHandler enrollRestrictionHandler;
    protected static String platform;
    public static final int ERIDVALIDATOR_ENROLL_REQ_VALID = 1;
    public static final int ERIDVALIDATOR_ENROLL_REQ_OTHERPLATFORM = 2;
    public static final int ERIDVALIDATOR_ENROLL_REQ_ENROLLEDALREADY = 3;
    public static final int ERIDVALIDATOR_ENROLL_REQ_EXPIRED = 4;
    public static final int ERIDVALIDATOR_ENROLL_REQ_REVOKED = 5;
    public static final int ERIDVALIDATOR_ENROLL_REQ_ENTERED_INVALID = 6;
    public static final int ERIDVALIDATOR_ENROLL_REQ_NOT_IN_SESSION = 7;
    
    public MDMEnrollmentHandler() {
        this.servletPath = null;
        this.servletContext = null;
        this.action = null;
        this.userAgent = null;
        this.isSelfEnrollError = false;
        this.pathInfo = null;
        this.enrollRestrictionHandler = new EnrollRestrictionHandler();
    }
    
    public static MDMEnrollmentHandler getEnrollmentHandler(final String userAgent) {
        MDMEnrollmentHandler mdmEnrollmentHandler = null;
        if (isWindowsBrowser(userAgent)) {
            mdmEnrollmentHandler = new MDMWindowsEnrollmentHandler();
        }
        else if (isAndroidBrowser(userAgent)) {
            mdmEnrollmentHandler = new MDMAndroidEnrollmentHandler();
        }
        else if (isiOS(userAgent) || isMac(userAgent)) {
            mdmEnrollmentHandler = new MDMIOSEnrollmentHandler();
        }
        return mdmEnrollmentHandler;
    }
    
    public static boolean isSafariBrowser(final String userAgent) {
        return userAgent.contains("Safari") && userAgent.contains("Version") && !userAgent.contains("Android");
    }
    
    public static boolean isAppleWebView(final String userAgent) {
        return (isiOS(userAgent) || isMac(userAgent)) && !userAgent.contains("Safari") && !userAgent.contains("Version") && !userAgent.contains("Android");
    }
    
    public static boolean isiPad(final String userAgent) {
        return userAgent.contains("iPad");
    }
    
    public static boolean isiPod(final String userAgent) {
        return userAgent.contains("iPod");
    }
    
    public static boolean isiPhone(final String userAgent) {
        return userAgent.contains("iPhone");
    }
    
    public static boolean isMac(final String userAgent) {
        return userAgent.contains("Macintosh");
    }
    
    public static boolean isiOS(final String userAgent) {
        return isiPad(userAgent) || isiPod(userAgent) || isiPhone(userAgent);
    }
    
    public abstract void processQRSucess(final HttpServletRequest p0, final HttpServletResponse p1, final JSONObject p2) throws JSONException, ServletException, IOException;
    
    public abstract String getTermsPath(final HttpServletRequest p0, final HttpServletResponse p1, final Long p2) throws Exception;
    
    public void parseQREnrollmentRequest(final JSONObject jsonResponse, final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        Long customerId = null;
        try {
            final JSONObject msgResponse = jsonResponse.getJSONObject("MsgResponse");
            final Long enrollmentRequestID = (Long)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid");
            if (enrollmentRequestID != null) {
                customerId = MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(enrollmentRequestID);
            }
            else {
                customerId = CustomerInfoUtil.getInstance().getDefaultCustomer();
            }
            final String qrSrc = request.getParameter("scanSrc");
            if (!MDMStringUtils.isEmpty(qrSrc) && customerId != null) {
                if (qrSrc.equalsIgnoreCase(String.valueOf(1))) {
                    MDMEnrollmentUtil.addOrIncrementMETrackQRParamManagerValuesForPlatform(this.getPlatformTypeFromUseragent(), customerId, "QR_IOS_SCANNED_MAIL", "QR_ANDROID_SCANNED_MAIL", null);
                }
                else if (qrSrc.equalsIgnoreCase(String.valueOf(2))) {
                    MDMEnrollmentUtil.addOrIncrementMETrackQRParamManagerValuesForPlatform(this.getPlatformTypeFromUseragent(), customerId, "QR_IOS_SCANNED_SERVER", "QR_ANDROID_SCANNED_SERVER", null);
                }
                else if (qrSrc.equalsIgnoreCase(String.valueOf(3))) {
                    MDMEnrollmentUtil.addOrIncrementMETrackQRParamManagerValuesForPlatform(this.getPlatformTypeFromUseragent(), customerId, "QR_IOS_SCANNED_MAIL_URL", "QR_ANDROID_SCANNED_MAIL_URL", null);
                }
            }
            if (msgResponse.has("EnrollmentRequestID") && msgResponse.has("AuthMode")) {
                final Long erid = msgResponse.optLong("EnrollmentRequestID");
                final String otpFromQR = request.getParameter("token");
                final String authMode = String.valueOf(msgResponse.get("AuthMode"));
                final HashMap managedUserInfo = ManagedUserHandler.getInstance().getManagedUserDetailsForRequest(erid);
                final String otpInServer = MDMEnrollmentOTPHandler.getInstance().getOTPPassword(erid);
                final int otpFailedAttempts = MDMEnrollmentOTPHandler.getInstance().getFailedAttempts(erid);
                int platformType = managedUserInfo.get("PLATFORM_TYPE");
                if (platformType == 0) {
                    platformType = getPlatformTypeFromUseragent(this.userAgent);
                }
                request.setAttribute("DISPLAY_NAME", (Object)WordUtils.capitalize(managedUserInfo.get("DISPLAY_NAME").toString()));
                request.setAttribute("EMAIL_ADDRESS", managedUserInfo.get("EMAIL_ADDRESS"));
                request.setAttribute("platform", (Object)platformType);
                if (MDMStringUtils.isEmpty(otpInServer) || MDMStringUtils.isEmpty(otpFromQR) || !otpInServer.equals(otpFromQR) || otpFailedAttempts > 3) {
                    MDMEnrollmentRequestHandler.getInstance().updateEnrollFailedStatus(enrollmentRequestID, "dc.mdm.enroll.opt_val_failed", 12004);
                    MDMEnrollmentRequestHandler.getInstance().incrementOTPFailedAttemptCount(erid);
                    MDMEnrollmentUtil.addOrIncrementMETrackQRParamManagerValuesForPlatform(this.getPlatformTypeFromUseragent(), customerId, "QR_IOS_ERROR", "QR_ANDROID_ERROR", null);
                    request.setAttribute("QRError", (Object)true);
                    this.loadErrorPage(request, response);
                    return;
                }
                final JSONObject responseJSON = new JSONObject();
                responseJSON.put("PlatformType", managedUserInfo.get("PLATFORM_TYPE"));
                responseJSON.put("ENROLLMENT_REQUEST_ID", managedUserInfo.get("ENROLLMENT_REQUEST_ID"));
                responseJSON.put("CUSTOMER_ID", managedUserInfo.get("CUSTOMER_ID"));
                responseJSON.put("Status", (Object)"Acknowledged");
                responseJSON.put("AuthMode", (Object)authMode);
                WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "erid", (Object)erid);
                request.setAttribute("erid", (Object)erid);
                this.processQRSucess(request, response, responseJSON);
                request.setAttribute("responseJSON", (Object)responseJSON.toString());
                final String s = authMode;
                switch (s) {
                    case "OTP": {
                        MDMEnrollmentUtil.addOrIncrementMETrackQRParamManagerValuesForPlatform(this.getPlatformTypeFromUseragent(), customerId, "QR_IOS_OTP", "QR_ANDROID_OTP", null);
                        this.loadQRLandingPage(request, response);
                        return;
                    }
                    case "ActiveDirectory":
                    case "Combined": {
                        MDMEnrollmentUtil.addOrIncrementMETrackQRParamManagerValuesForPlatform(this.getPlatformTypeFromUseragent(), customerId, "QR_IOS_NON_OTP", "QR_ANDROID_NON_OTP", null);
                        msgResponse.put("AuthMode", (Object)"ActiveDirectory");
                        jsonResponse.put("MsgResponse", (Object)msgResponse);
                        this.parseValidEnrollReq(jsonResponse, request, response);
                    }
                }
            }
            else {
                this.parseValidEnrollReq(jsonResponse, request, response);
            }
        }
        catch (final Exception ex) {
            try {
                MDMEnrollmentUtil.addOrIncrementMETrackQRParamManagerValuesForPlatform(this.getPlatformTypeFromUseragent(), customerId, "QR_IOS_ERROR", "QR_ANDROID_ERROR", null);
                SyMLogger.log("MDMEnrollment", Level.SEVERE, "Exception in : processQRSucess for " + this.getPlatformTypeString(), (Throwable)ex);
                request.setAttribute("QRError", (Object)true);
                this.loadErrorPage(request, response);
            }
            catch (final IOException ex2) {
                Logger.getLogger(MDMEnrollmentHandler.class.getName()).log(Level.SEVERE, null, ex2);
            }
        }
    }
    
    public static boolean isAndroidBrowser(final String userAgent) {
        return userAgent.contains("Android");
    }
    
    public static boolean isWindowsBrowser(final String userAgent) {
        return userAgent.contains("Windows");
    }
    
    protected Map<Integer, String> getAgentDownloadURL(final int platformType, final Long customerId) {
        final Map<Integer, String> downloadUrl = new HashMap<Integer, String>();
        try {
            final Criteria custId = new Criteria(Column.getColumn("MDAgentDownloadInfo", "CUSTOMER_ID"), (Object)customerId, 0);
            final Criteria platformCri = new Criteria(Column.getColumn("MDAgentDownloadInfo", "PLATFORM_TYPE"), (Object)platformType, 0);
            final Criteria criteria = custId.and(platformCri);
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MDAgentDownloadInfo"));
            sQuery.addSelectColumn(Column.getColumn("MDAgentDownloadInfo", "*"));
            sQuery.setCriteria(criteria);
            final DataObject urlDO = MDMUtil.getPersistence().get(sQuery);
            if (urlDO != null && !urlDO.isEmpty()) {
                final Iterator urlIterator = urlDO.getRows("MDAgentDownloadInfo");
                while (urlIterator.hasNext()) {
                    final Row row = urlIterator.next();
                    downloadUrl.put((Integer)row.get("AGENT_TYPE"), (String)row.get("DOWNLOAD_URL"));
                }
            }
        }
        catch (final Exception ex) {
            MDMEnrollmentHandler.logger.log(Level.INFO, "Exception occured while getting agent download url");
        }
        return downloadUrl;
    }
    
    public abstract void initEnrollemnt(final HttpServletRequest p0, final HttpServletResponse p1, final JSONObject p2);
    
    private boolean parseDiscoverResponse(final JSONObject jsonResponse, final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final String status = String.valueOf(jsonResponse.get("Status"));
            final JSONObject msgResponse = jsonResponse.getJSONObject("MsgResponse");
            final String enrollmentType = request.getParameter("et");
            WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "erid");
            if (status != null && status.equals("Error")) {
                final Integer errorCode = msgResponse.getInt("ErrorCode");
                switch (errorCode) {
                    case 51011: {
                        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
                        if (!isMSP) {
                            request.setAttribute("selfEnrollEnable", (Object)false);
                            break;
                        }
                        break;
                    }
                    case 51012: {
                        request.setAttribute("apnsConfigure", (Object)false);
                        break;
                    }
                    case 12012: {
                        request.setAttribute("sufficientLicense", (Object)false);
                        break;
                    }
                    case 12001: {
                        request.setAttribute("enrollidValid", (Object)false);
                        request.setAttribute("errorMessage", (Object)String.valueOf(msgResponse.get("ErrorKey")));
                        break;
                    }
                    case 52004: {
                        request.setAttribute("isSSLIssue", (Object)true);
                        break;
                    }
                    case 52005: {
                        request.setAttribute("isSSLIssue", (Object)true);
                        break;
                    }
                    case 51016: {
                        if (isiOS(this.userAgent)) {
                            request.setAttribute("apnsExpired", (Object)true);
                            break;
                        }
                        break;
                    }
                }
                this.loadErrorPage(request, response);
                return false;
            }
            if (MDMStringUtils.isEmpty(enrollmentType) || !enrollmentType.equalsIgnoreCase("1")) {
                this.parseValidEnrollReq(jsonResponse, request, response);
                return true;
            }
            this.parseQREnrollmentRequest(jsonResponse, request, response);
        }
        catch (final JSONException ex) {
            MDMEnrollmentHandler.logger.log(Level.SEVERE, "Error while parsing discover response : {0}", (Throwable)ex);
        }
        return false;
    }
    
    protected final boolean isEnrollmentAllowed(final HttpServletRequest request) {
        final boolean isError = false;
        try {
            final String userAgent = request.getHeader("user-agent");
            final Long enrollmentRequestID = (Long)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid");
            final Long customerId = MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(enrollmentRequestID);
            try {
                final String action = request.getParameter("actionToCall");
                if ((action != null && action.equalsIgnoreCase("selfEnroll")) || (action == null && (this.pathInfo == null || this.pathInfo.equals("/")))) {
                    this.enrollRestrictionHandler.allowEnrollment(customerId, true, null);
                }
                else {
                    this.enrollRestrictionHandler.allowEnrollment(customerId, false, null);
                }
            }
            catch (final SyMException ex) {
                switch (ex.getErrorCode()) {
                    case 12012: {
                        request.setAttribute("sufficientLicense", (Object)false);
                        return isError;
                    }
                    case 51012: {
                        if (isiOS(userAgent) || isMac(userAgent)) {
                            request.setAttribute("apnsConfigure", (Object)false);
                            return isError;
                        }
                        break;
                    }
                    case 51016: {
                        if (isiOS(userAgent) || isMac(userAgent)) {
                            request.setAttribute("apnsExpired", (Object)true);
                            return isError;
                        }
                        break;
                    }
                    case 51011: {
                        final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
                        if (!isMSP) {
                            request.setAttribute("selfEnrollEnable", (Object)false);
                            return isError;
                        }
                        break;
                    }
                    case 52004: {
                        request.setAttribute("isSSLIssue", (Object)true);
                        request.setAttribute("hostNameIssue", (Object)true);
                        return isError;
                    }
                    case 52005: {
                        request.setAttribute("isSSLIssue", (Object)true);
                        request.setAttribute("certChainIssue", (Object)true);
                        return isError;
                    }
                    case 52006: {
                        request.setAttribute("isSSLIssue", (Object)true);
                        request.setAttribute("certExpired", (Object)true);
                        return isError;
                    }
                }
            }
        }
        catch (final Exception ex2) {
            MDMEnrollmentHandler.logger.log(Level.WARNING, "Exception in isSelfEnrollErrorMessage", ex2);
        }
        return true;
    }
    
    public JSONObject constructDiscoverRequest(final String platform, final Boolean includeBasicValidation, final Long enrollmentRequestID) throws JSONException {
        final JSONObject requestJSON = new JSONObject();
        requestJSON.put("MsgRequestType", (Object)"Discover");
        requestJSON.put("DevicePlatform", (Object)platform);
        final JSONObject msgRequestJSON = new JSONObject();
        msgRequestJSON.put("RegistrationType", (Object)"MDMRegistration");
        msgRequestJSON.put("IncludeBasicValidations", (Object)includeBasicValidation);
        if (enrollmentRequestID != null) {
            msgRequestJSON.put("EnrollmentRequestID", (Object)enrollmentRequestID);
        }
        requestJSON.put("MsgRequest", (Object)msgRequestJSON);
        return requestJSON;
    }
    
    public abstract void parseValidEnrollReq(final JSONObject p0, final HttpServletRequest p1, final HttpServletResponse p2) throws JSONException, ServletException, IOException;
    
    protected final void loadErrorPage(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (isiPhone(this.userAgent) || isiPod(this.userAgent) || isAndroidBrowser(this.userAgent) || isWindowsBrowser(this.userAgent)) {
            request.setAttribute("userAgentType", (Object)"phone");
        }
        else {
            request.setAttribute("userAgentType", (Object)"tablet");
        }
        if (!response.isCommitted()) {
            this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/phoneInvalidBrowser.jsp").forward((ServletRequest)request, (ServletResponse)response);
        }
    }
    
    private final void loadQRLandingPage(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException, JSONException, Exception {
        request.setAttribute("isQREnrollment", (Object)"true");
        if (isiPad(this.userAgent)) {
            request.setAttribute("userAgentType", (Object)"tablet");
        }
        else {
            request.setAttribute("userAgentType", (Object)"phone");
        }
        final JSONObject json = new JSONObject((String)request.getAttribute("responseJSON"));
        final Long enrollmentRequestID = json.getLong("ENROLLMENT_REQUEST_ID");
        final String path = this.getTermsPath(request, response, enrollmentRequestID);
        request.setAttribute("path", (Object)path);
        if (!response.isCommitted()) {
            if (ManagedDeviceHandler.getInstance().getPlatformForErid(enrollmentRequestID) == 0) {
                this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmPlatformNeutralConfigDownload.jsp").forward((ServletRequest)request, (ServletResponse)response);
            }
            else {
                this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/mdmConfigDownload.jsp").forward((ServletRequest)request, (ServletResponse)response);
            }
        }
    }
    
    protected boolean discoverServerEnrollRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        MDMEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'install' action called...");
        String sEnrollRequestID = request.getParameter("erid");
        if ((sEnrollRequestID == null || sEnrollRequestID.isEmpty() || !isNumeric(sEnrollRequestID)) && this.pathInfo != null) {
            sEnrollRequestID = this.pathInfo.trim().substring(1, this.pathInfo.toString().length());
        }
        if ((sEnrollRequestID == null || sEnrollRequestID.isEmpty() || !isNumeric(sEnrollRequestID)) && WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid") != null) {
            sEnrollRequestID = String.valueOf(WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid"));
        }
        final int eridValidStatus = this.validateInvitationEnrollmentRequest(Long.valueOf(sEnrollRequestID), this.getPlatformTypeFromUseragent());
        if (eridValidStatus != 1 && eridValidStatus != 2) {
            this.loadInvitationIdErrorPage(request, response, eridValidStatus);
            return false;
        }
        if (sEnrollRequestID == null || sEnrollRequestID.isEmpty() || !isNumeric(sEnrollRequestID)) {
            request.setAttribute("enrollidValid", (Object)false);
            this.loadErrorPage(request, response);
            return false;
        }
        try {
            WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "validationComplete");
            final JSONObject discoverResponseJSON = MDMRegistrationHandler.getInstance(MDMEnrollmentHandler.platform).processMessage(this.constructDiscoverRequest(MDMEnrollmentHandler.platform, true, Long.valueOf(sEnrollRequestID)));
            return this.parseDiscoverResponse(discoverResponseJSON, request, response);
        }
        catch (final Exception e) {
            MDMEnrollmentHandler.logger.log(Level.SEVERE, "Exception during install: {0}", e);
            return false;
        }
    }
    
    protected final boolean discoverSelfEnrollRequest(final HttpServletRequest request, final HttpServletResponse response) {
        MDMEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'SelfEnrollADCredential' action called...");
        try {
            WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "validationComplete");
            final JSONObject discoverResponseJSON = MDMRegistrationHandler.getInstance(MDMEnrollmentHandler.platform).processMessage(this.constructDiscoverRequest(MDMEnrollmentHandler.platform, true, null));
            return this.parseDiscoverResponse(discoverResponseJSON, request, response);
        }
        catch (final Exception ex) {
            MDMEnrollmentHandler.logger.log(Level.SEVERE, "Exception in SelfEnrollADCredential: {0}", ex.getMessage());
            return false;
        }
    }
    
    protected static boolean isNumeric(final String sEnrollRequestID) {
        try {
            Long.valueOf(sEnrollRequestID);
        }
        catch (final NumberFormatException exp) {
            return false;
        }
        return true;
    }
    
    protected void setServerDetails(final HttpServletRequest request) throws Exception {
        Date sslCreationDate = (Date)ApiFactoryProvider.getCacheAccessAPI().getCache("SSL_CREATION_DATE", 1);
        if (sslCreationDate == null) {
            sslCreationDate = SSLCertificateUtil.getInstance().getSSLCertificateCreationDate();
            ApiFactoryProvider.getCacheAccessAPI().putCache("SSL_CREATION_DATE", (Object)sslCreationDate, 1);
        }
        String FQDN = (String)ApiFactoryProvider.getCacheAccessAPI().getCache("FQDN", 1);
        if (FQDN == null || FQDN.isEmpty()) {
            final Properties natProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
            if (natProps != null && !natProps.isEmpty()) {
                FQDN = ((Hashtable<K, String>)natProps).get("NAT_ADDRESS");
                ApiFactoryProvider.getCacheAccessAPI().putCache("FQDN", (Object)((Hashtable<K, String>)natProps).get("NAT_ADDRESS"), 1);
            }
        }
        request.setAttribute("FQDN", (Object)FQDN);
        request.setAttribute("CERTIFICATE_CREATION_DATE", (Object)sslCreationDate.getTime());
    }
    
    public void parseEnrollReq(final JSONObject jsonResponse, final HttpServletRequest request, final HttpServletResponse response) throws JSONException, ServletException, IOException {
        final JSONObject msgResponse = jsonResponse.getJSONObject("MsgResponse");
        final String authMode = String.valueOf(msgResponse.get("AuthMode"));
        request.setAttribute("authMode", (Object)authMode);
        request.setAttribute("platformStr", (Object)this.getPlatformTypeString());
        if (msgResponse.has("EnrollmentRequestID")) {
            request.setAttribute("EnrollmentRequestID", msgResponse.get("EnrollmentRequestID"));
            final Properties properties = ManagedUserHandler.getInstance().getManagedUserDetailsForRequestAsProperties(msgResponse.getLong("EnrollmentRequestID"));
            final Long mobileUserResourceID = ((Hashtable<K, Long>)properties).get("MANAGED_USER_ID");
            final HashMap managedUserInfo = ManagedUserHandler.getInstance().getManagedUserDetails(mobileUserResourceID);
            if (managedUserInfo.get("NAME") != null) {
                request.setAttribute("userName", managedUserInfo.get("NAME"));
            }
            if (managedUserInfo.get("DOMAIN_NETBIOS_NAME") != null) {
                request.setAttribute("DomainNameList", managedUserInfo.get("DOMAIN_NETBIOS_NAME"));
            }
        }
        else {
            request.setAttribute("isSelfEnroll", (Object)true);
            final JSONArray domainNameList = msgResponse.getJSONArray("DomainNameList");
            final List list = new ArrayList();
            for (int i = 0; i < domainNameList.length(); ++i) {
                list.add(domainNameList.get(i));
            }
            request.setAttribute("DomainNameList", (Object)list);
            request.setAttribute("ownedByOption", (Object)msgResponse.optString("OwnedBy"));
        }
        if (isiPad(this.userAgent)) {
            request.setAttribute("userAgentType", (Object)"tablet");
        }
        else {
            request.setAttribute("userAgentType", (Object)"phone");
        }
        this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/phoneLoginPage.jsp").forward((ServletRequest)request, (ServletResponse)response);
    }
    
    protected void processAdminEnrollPostAuthenticateUser(final HttpServletRequest request, final HttpServletResponse response) {
        MDMEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'processAdminEnrollPostAuthenticateUser' action called...");
        try {
            final String validLogin = (String)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "validationComplete");
            if (!Boolean.parseBoolean(validLogin)) {
                request.setAttribute("enrollidValid", (Object)false);
                request.setAttribute("ERID_ERROR_CODE", (Object)7);
                this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/phoneInvalidBrowser.jsp").forward((ServletRequest)request, (ServletResponse)response);
            }
            else {
                final String deviceForEnrollId = request.getParameter("deviceForEnrollmentId");
                final String managedUserId = request.getParameter("managedUserId");
                final JSONObject jsonObject = OutOfBoxEnrollmentSettingsHandler.getInstance().getPostAuthInputJSON(Long.parseLong(deviceForEnrollId), Long.parseLong(managedUserId));
                if (isiPad(this.userAgent)) {
                    request.setAttribute("userAgentType", (Object)"tablet");
                }
                else {
                    request.setAttribute("userAgentType", (Object)"phone");
                }
                final JSONObject responseJSON = OutOfBoxEnrollmentSettingsHandler.getInstance().processPostAdAuth(jsonObject);
                if (responseJSON.has("EnrollmentReqID") && jsonObject.has("PlatformType")) {
                    final String url = "/getconfig.mob?isMacIpad=";
                    responseJSON.put("platform", jsonObject.getInt("PlatformType"));
                    responseJSON.put("url", (Object)url);
                    responseJSON.put("httpMethod", (Object)"GET");
                    if (jsonObject.getInt("PlatformType") == 1) {
                        WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "erid", (Object)responseJSON.getLong("EnrollmentReqID"));
                    }
                }
                request.setAttribute("responseJSON", (Object)StringEscapeUtils.escapeJavaScript(responseJSON.toString()));
                this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/adminenroll/postAuthenticateUser.jsp").forward((ServletRequest)request, (ServletResponse)response);
            }
        }
        catch (final Exception exp) {
            MDMEnrollmentHandler.logger.log(Level.SEVERE, "Exception in processAdminEnrollPostAuthenticateUser", exp);
        }
    }
    
    protected void showAdminEnrollADAuthPage(final HttpServletRequest request, final HttpServletResponse response) {
        MDMEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'showAdminEnrollADAuthPage' action called...");
        try {
            final Long deviceForEnrollId = Long.valueOf(request.getParameter("deviceForEnrollmentId"));
            Long customerid = AdminEnrollmentHandler.getCustomerIdFromDeviceForEnrollId(deviceForEnrollId);
            if (customerid == -1L) {
                customerid = CustomerInfoUtil.getInstance().getCustomerId();
            }
            final List<String> domainList = MDMEnrollmentUtil.getInstance().getDomainNamesWithoutGSuite(customerid);
            request.setAttribute("deviceForEnrollId", (Object)deviceForEnrollId);
            request.setAttribute("DomainNameList", (Object)domainList);
            if (isiPad(this.userAgent)) {
                request.setAttribute("userAgentType", (Object)"tablet");
            }
            else {
                request.setAttribute("userAgentType", (Object)"phone");
            }
            this.servletContext.getRequestDispatcher("/jsp/mdm/enroll/adminenroll/adminEnrollAuthPage.jsp").forward((ServletRequest)request, (ServletResponse)response);
        }
        catch (final Exception exp) {
            MDMEnrollmentHandler.logger.log(Level.SEVERE, "Exception on showAdminEnrollADAuthPage.. ", exp);
        }
    }
    
    protected void authenticateAdminEnrollADCredentials(final HttpServletRequest request, final HttpServletResponse response, final int platform) {
        MDMEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'authenticateAdminEnrollADCredentials' action called...");
        try {
            final JSONParser parser = new JSONParser();
            final org.json.simple.JSONObject authenticateJSON = (org.json.simple.JSONObject)parser.parse((Reader)request.getReader());
            final JSONObject requestJSON = new JSONObject(authenticateJSON.toString());
            final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
            final String domainName = String.valueOf(msgRequestJSON.get("domainName"));
            final String userName = String.valueOf(msgRequestJSON.get("userName"));
            final String adPassword = String.valueOf(msgRequestJSON.get("adPassword"));
            String userEmail = String.valueOf(msgRequestJSON.get("emailAddress"));
            final Long deviceForEnrollId = Long.parseLong(msgRequestJSON.get("deviceForEnrollId").toString());
            userEmail = URLDecoder.decode(userEmail, "UTF-8");
            userEmail = userEmail.replaceAll("\\+", "%2b");
            userEmail = URLDecoder.decode(userEmail, "UTF-8");
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("Status", (Object)"Acknowledged");
            if (!MDMUtil.getInstance().isValidEmail(userEmail)) {
                responseJSON.put("isUserCredentialsValid", false);
                responseJSON.put("errorMsg", (Object)I18N.getMsg("dc.mdm.safe_device_mgmt.invalid_email", new Object[0]));
            }
            else {
                final Long customerID = AdminEnrollmentHandler.getCustomerIdFromDeviceForEnrollId(deviceForEnrollId);
                final Boolean isValidCredentials = IdpsFactoryProvider.getIdpsAccessAPI(domainName, customerID).validatePassword(domainName, userName, adPassword, customerID);
                if (isValidCredentials) {
                    final JSONObject managedUserJSON = new JSONObject();
                    managedUserJSON.put("DOMAIN_NETBIOS_NAME", (Object)domainName);
                    managedUserJSON.put("NAME", (Object)userName);
                    managedUserJSON.put("EMAIL_ADDRESS", (Object)userEmail);
                    managedUserJSON.put("CUSTOMER_ID", (Object)customerID);
                    final Long managedUserId = ManagedUserHandler.getInstance().addOrUpdateAndGetUserId(managedUserJSON);
                    responseJSON.put("isUserCredentialsValid", true);
                    responseJSON.put("MANAGED_USER_ID", (Object)String.valueOf(managedUserId));
                    WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "validationComplete", (Object)"true");
                }
                else {
                    responseJSON.put("isUserCredentialsValid", false);
                    responseJSON.put("errorMsg", (Object)I18N.getMsg("mdm.ad.auth_failed", new Object[0]));
                }
            }
            SYMClientUtil.writeJsonFormattedResponse(response);
            response.getWriter().println(responseJSON.toString());
        }
        catch (final Exception exp) {
            MDMEnrollmentHandler.logger.log(Level.SEVERE, "Exception in authenticateAdminEnrollADCredentials", exp);
        }
    }
    
    protected void authenticate(final HttpServletRequest request, final HttpServletResponse response) {
        MDMEnrollmentHandler.logger.log(Level.FINE, "MDMEnrollServlet: 'Authenticate' action called...");
        final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
        if (evaluatorApi != null) {
            final int platform = this.getPlatformTypeFromUseragent();
            String trackParam = null;
            if (platform == 1) {
                trackParam = "iOS_Auth_Page_Count";
            }
            else if (platform == 2) {
                trackParam = "Android_App_Auth_Page_Count";
            }
            else if (platform == 3) {
                trackParam = "Windows_App_Auth_Page_Count";
            }
            evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", trackParam);
        }
        try {
            final JSONParser parser = new JSONParser();
            final org.json.simple.JSONObject authenticateJSON = (org.json.simple.JSONObject)parser.parse((Reader)request.getReader());
            final JSONObject responseJSON = MDMRegistrationHandler.getInstance(this.getPlatformTypeString()).processMessage(new JSONObject(authenticateJSON.toJSONString()));
            final String status = String.valueOf(responseJSON.get("Status"));
            final JSONObject msgResponse = responseJSON.getJSONObject("MsgResponse");
            if (status != null && status.equals("Acknowledged")) {
                WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "erid", (Object)msgResponse.getLong("EnrollmentRequestID"));
                WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "validationComplete", (Object)"true");
                WebclientAPIFactoryProvider.getSessionAPI().addToSession(request, "cust", (Object)msgResponse.getLong("CustomerID"));
            }
            SYMClientUtil.writeJsonFormattedResponse(response);
            response.getWriter().println(responseJSON);
        }
        catch (final Exception e) {
            MDMEnrollmentHandler.logger.log(Level.SEVERE, "Exception in Authenticate", e);
        }
    }
    
    private int getPlatformTypeFromUseragent() {
        if (isWindowsBrowser(this.userAgent)) {
            return 3;
        }
        if (isAndroidBrowser(this.userAgent)) {
            return 2;
        }
        if (isiOS(this.userAgent) || isMac(this.userAgent)) {
            return 1;
        }
        return -1;
    }
    
    private String getPlatformTypeString() {
        switch (this.getPlatformTypeFromUseragent()) {
            case 1: {
                return "iOS";
            }
            case 2: {
                return "android";
            }
            case 3: {
                return "windows";
            }
            default: {
                return null;
            }
        }
    }
    
    public void setCommonRequestAttibutes(final HttpServletRequest request) {
        final Long enrollmentRequestID = (Long)WebclientAPIFactoryProvider.getSessionAPI().getSessionAttribute(request, "erid");
        final Long customerID = MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(enrollmentRequestID);
        request.setAttribute("platform", (Object)this.getPlatformTypeFromUseragent());
        request.setAttribute("organisation_name", (Object)WordUtils.capitalize(MDMApiFactoryProvider.getMDMUtilAPI().getOrgName(customerID)));
    }
    
    public int validateInvitationEnrollmentRequest(final Long erid, final int platform) {
        try {
            final SelectQuery invitationQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
            invitationQuery.addJoin(new Join("DeviceEnrollmentRequest", "OTPPassword", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 1));
            invitationQuery.setCriteria(new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
            invitationQuery.addSelectColumn(new Column("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"));
            invitationQuery.addSelectColumn(new Column("DeviceEnrollmentRequest", "PLATFORM_TYPE"));
            invitationQuery.addSelectColumn(new Column("DeviceEnrollmentRequest", "REQUEST_STATUS"));
            invitationQuery.addSelectColumn(new Column("DeviceEnrollmentRequest", "OWNED_BY"));
            invitationQuery.addSelectColumn(new Column("OTPPassword", "ENROLLMENT_REQUEST_ID", "OTPPASSWORD.ENROLLMENT_REQUEST_ID"));
            invitationQuery.addSelectColumn(new Column("OTPPassword", "EXPIRE_TIME"));
            final DataObject dataObject = MDMUtil.getPersistence().get(invitationQuery);
            final Long maxErid = (Long)DBUtil.getMaxOfValue("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID", (Criteria)null);
            final Iterator iterator = dataObject.getRows("DeviceEnrollmentRequest");
            int ownedBy = -1;
            if (iterator.hasNext()) {
                final Row row = dataObject.getRow("DeviceEnrollmentRequest", new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)erid, 0));
                ownedBy = (int)row.get("OWNED_BY");
            }
            if (dataObject.isEmpty()) {
                if (maxErid != null && erid != null && erid < maxErid) {
                    return 5;
                }
                if (erid == null) {
                    return 7;
                }
                return 6;
            }
            else {
                final Row enrollmentRow = dataObject.getRow("DeviceEnrollmentRequest");
                final Row otpRow = dataObject.getRow("OTPPassword");
                final int eridPlatform = (int)enrollmentRow.get("PLATFORM_TYPE");
                final int eridStatus = (int)enrollmentRow.get("REQUEST_STATUS");
                if (eridPlatform != platform && eridPlatform != 0) {
                    return 2;
                }
                if (otpRow == null || eridStatus == 3) {
                    return 3;
                }
                if ((long)otpRow.get("EXPIRE_TIME") > System.currentTimeMillis()) {
                    return 1;
                }
                if (EnrollmentSettingsHandler.getInstance().isSelfEnrollmentEnabled(CustomerInfoUtil.getInstance().getCustomerId()) && ownedBy == 2) {
                    return 1;
                }
                return 4;
            }
        }
        catch (final Exception ex) {
            MDMEnrollmentHandler.logger.log(Level.SEVERE, "Exception during install: {0}", ex.getMessage());
            return -1;
        }
    }
    
    protected void loadInvitationIdErrorPage(final HttpServletRequest request, final HttpServletResponse response, final int eridStatus) throws ServletException, IOException {
        request.setAttribute("enrollidValid", (Object)false);
        request.setAttribute("platform", (Object)this.getPlatformTypeFromUseragent());
        request.setAttribute("ERID_ERROR_CODE", (Object)eridStatus);
        this.loadErrorPage(request, response);
    }
    
    public static int getPlatformTypeFromUseragent(final String userAgent) {
        if (isWindowsBrowser(userAgent)) {
            return 3;
        }
        if (isAndroidBrowser(userAgent)) {
            return 2;
        }
        if (isiOS(userAgent) || isMac(userAgent)) {
            return 1;
        }
        return -1;
    }
    
    public static String getOSVersionFromUserAgent(final String userAgent) {
        try {
            final Parser uagent = new Parser();
            final Client uaClient = uagent.parse(userAgent);
            final String majorVersion = uaClient.os.major;
            final String minorVersion = (uaClient.os.minor == null) ? "0" : uaClient.os.minor;
            final String patchVersion = (uaClient.os.patch == null) ? "0" : uaClient.os.patch;
            final String patchMinor = (uaClient.os.patchMinor == null) ? "0" : uaClient.os.patchMinor;
            final String osVersion = majorVersion + "." + minorVersion + "." + patchVersion + "." + patchMinor;
            return osVersion;
        }
        catch (final Exception ex) {
            MDMEnrollmentHandler.logger.log(Level.SEVERE, "Exception in getOSVersion", ex);
            return "0.0.0.0";
        }
    }
    
    public static String getOSVersionFromUserAgent(final HttpServletRequest request) {
        return getOSVersionFromUserAgent(request.getHeader("User-Agent"));
    }
    
    public static boolean isPlatformIndependentRequest(final Long enrollmentRequestId) {
        boolean isPlatformIndependentRequest = false;
        try {
            final int platformType = ManagedDeviceHandler.getInstance().getPlatformForErid(enrollmentRequestId);
            if (platformType == 0) {
                isPlatformIndependentRequest = true;
            }
        }
        catch (final Exception e) {
            MDMEnrollmentHandler.logger.log(Level.SEVERE, "Exception in isPlatformIndependentRequest", e);
        }
        return isPlatformIndependentRequest;
    }
    
    static {
        MDMEnrollmentHandler.logger = Logger.getLogger("MDMEnrollment");
        MDMEnrollmentHandler.platform = null;
    }
}
