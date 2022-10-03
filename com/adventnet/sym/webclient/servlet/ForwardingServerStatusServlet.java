package com.adventnet.sym.webclient.servlet;

import java.util.Hashtable;
import java.io.Reader;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.devicemanagement.onpremise.server.authentication.APIKeyUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.Iterator;
import java.util.Map;
import java.io.PrintWriter;
import com.me.devicemanagement.framework.server.general.InstallationTrackingAPI;
import com.me.mdm.onpremise.server.util.MDMPFwsUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import com.adventnet.sym.server.util.SyMUtil;
import com.me.devicemanagement.onpremise.server.util.FwsUtil;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.HashMap;
import java.io.File;
import org.json.JSONException;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import java.util.Base64;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class ForwardingServerStatusServlet extends HttpServlet
{
    String sourceClass;
    private static final String PRODUCT_NAME = "ProductName";
    private static final String HTTP_PORT = "httpPort";
    private static final String HTTPS_PORT = "httpsPort";
    private static final String VALID_LICENSE = "isLicenseValid";
    private static final String FS_REACHABLE = "isFSReachable";
    private static final String NAT_SAVED = "nat_saved";
    private static final String MAIL_ID = "support_mail";
    private static final String PRODUCT_CODE = "ProductCode";
    private static final String FS_HELP_LINK = "fsHelpLink";
    private static final String CHECK_UPDATE_URL = "checkUpdateURL";
    private static final String FS_UPDATES_LINK = "fsUpdatesLink";
    private static final String IS_RECONFIGURE_WINDOWS_NEEDED = "reconfigureNeeded";
    private static final String NOT_SUPPORTED = "not_supported";
    private static final String BUILD_NUMBER = "BuildNumber";
    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String UNAUTHENTICATED_REQUEST = "UNAUTHENTICATED REQUEST";
    private static final String NO_AUTH_TOKEN = "AUTH TOKEN NOT EXIST";
    private static final String SGS_AUTH_TOKEN = "SGS_AUTH_TOKEN";
    private static final String FWS_SETTINGS_FILE;
    private static Logger logger;
    
    public ForwardingServerStatusServlet() {
        this.sourceClass = "ForwardingServerStatusServlet";
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String action = request.getParameter("action");
        final InstallationTrackingAPI installationTracking = ApiFactoryProvider.installationTrackingAPIImpl();
        final Properties serverInfoProps = new Properties();
        final String authToken = request.getHeader("Authorization");
        if (authToken == null) {
            response.sendError(401, "AUTH TOKEN NOT EXIST");
            return;
        }
        final Long loginID = this.getLoginIDIFValidAuthToken(authToken);
        if (loginID == null) {
            response.sendError(401, "UNAUTHENTICATED REQUEST");
            return;
        }
        if (!DMUserHandler.isUserInAdminRole(loginID)) {
            response.sendError(403, "UNAUTHENTICATED REQUEST");
            return;
        }
        if (action != null && action.equalsIgnoreCase("getServerDetails")) {
            try {
                final JSONObject tokenBody = this.extractJSONFromBody(request, response);
                if (tokenBody.keySet().contains("sgskey")) {
                    String encodedToken = String.valueOf(tokenBody.get("sgskey"));
                    if (encodedToken != null && !encodedToken.isEmpty()) {
                        encodedToken = new String(Base64.getDecoder().decode(encodedToken));
                        if (encodedToken.length() > 0 && !encodedToken.equalsIgnoreCase("null")) {
                            SecurityUtil.updateAdvancedSecurityDetail("SGS_AUTH_TOKEN", encodedToken);
                            ForwardingServerStatusServlet.logger.log(Level.INFO, "Successfully Updated SGS Token to the DB.");
                        }
                        else {
                            ForwardingServerStatusServlet.logger.log(Level.SEVERE, "AUTH-TOKEN CANNOT BE DECODE FROM SGS REQUEST");
                        }
                    }
                    else {
                        ForwardingServerStatusServlet.logger.log(Level.SEVERE, "NO AUTH-TOKEN FOUND IN SGS REQUEST");
                    }
                }
                else {
                    ForwardingServerStatusServlet.logger.log(Level.SEVERE, "NO AUTH-TOKEN FOUND IN SGS REQUEST");
                }
            }
            catch (final Exception ex) {
                ForwardingServerStatusServlet.logger.log(Level.SEVERE, "Exception while retriving SGS Key {0}", ex);
            }
            final JSONObject serverDetails = new JSONObject();
            Properties webServerProps = null;
            try {
                webServerProps = WebServerUtil.getWebServerSettings();
                final ProductUrlLoader urlLoader = ProductUrlLoader.getInstance();
                serverDetails.put("ProductName", (Object)urlLoader.getValue("productname"));
                serverDetails.put("ProductCode", (Object)urlLoader.getValue("productcode"));
                serverDetails.put("support_mail", (Object)I18N.getMsg(urlLoader.getValue("supportmailid"), new Object[0]));
                serverDetails.put("httpPort", (Object)webServerProps.getProperty("http.port"));
                serverDetails.put("httpsPort", (Object)webServerProps.getProperty("https.port"));
                serverDetails.put("isLicenseValid", (Object)"true");
                final String fs_license_valid = "true";
                final Properties natProps = NATHandler.getNATConfigurationProperties();
                if (natProps == null || natProps.isEmpty()) {
                    serverDetails.put("nat_saved", (Object)"false");
                }
                else {
                    serverDetails.put("nat_saved", (Object)"true");
                }
                serverDetails.put("isFSReachable", (Object)"true");
                serverDetails.put("fsHelpLink", (Object)(urlLoader.getValue("mdmUrl") + "/help/configuring_mobile_device_manager/mdm_nat_settings.html#Installing_the_Certificates"));
                serverDetails.put("checkUpdateURL", (Object)(urlLoader.getValue("mdmUrl") + "/mdm-forwarding-server-download.html"));
                serverDetails.put("BuildNumber", (Object)urlLoader.getValue("buildnumber"));
                serverDetails.put("reconfigureNeeded", (Object)"true");
            }
            catch (final JSONException e) {
                ForwardingServerStatusServlet.logger.log(Level.WARNING, "JSON Exception while writing port values to properties {0} ", (Throwable)e);
            }
            catch (final Exception e2) {
                ForwardingServerStatusServlet.logger.log(Level.WARNING, "Exception while getting server details {0} ", e2);
            }
            response.setContentType("application/json");
            final PrintWriter out = response.getWriter();
            out.print(serverDetails);
            out.flush();
        }
        else if (request.getParameterMap().containsKey("fwdstatus")) {
            ForwardingServerStatusServlet.logger.log(Level.INFO, "Request data : {0}", request.getParameter("fwdstatus"));
            final String data = request.getParameter("fwdstatus");
            String serverStatus = "false";
            String installed = "false";
            String uninstalled = "false";
            try {
                final String confFilePath = System.getProperty("server.home") + File.separator + ForwardingServerStatusServlet.FWS_SETTINGS_FILE;
                if (!new File(confFilePath).exists()) {
                    new File(confFilePath).createNewFile();
                }
                final Map fwsProps = new HashMap();
                final JSONObject responseJSON = new JSONObject(data);
                ForwardingServerStatusServlet.logger.log(Level.INFO, "Response JSON{0}", responseJSON.toString());
                if (responseJSON != null && responseJSON.length() > 0) {
                    final Iterator keysItr = responseJSON.keys();
                    while (keysItr.hasNext()) {
                        final String key = keysItr.next();
                        fwsProps.put(key, String.valueOf(responseJSON.get(key)));
                    }
                    serverStatus = String.valueOf(responseJSON.get("fws_status"));
                    if (serverStatus.equalsIgnoreCase("true")) {
                        installed = "true";
                        fwsProps.put("installationTime", DateTimeUtil.longdateToString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
                    }
                    else {
                        uninstalled = "true";
                        fwsProps.put("uninstallationTime", DateTimeUtil.longdateToString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
                    }
                }
                ForwardingServerStatusServlet.logger.log(Level.INFO, "Props to regenerate{0} in conf file{1}", new Object[] { fwsProps, confFilePath });
                FileAccessUtil.writeMapAsPropertiesIntoFile(fwsProps, confFilePath, "");
                FwsUtil.regenerateProps();
                SyMUtil.updateSyMParameter("forwarding_server_config", serverStatus.toString());
                final Properties natProps2 = NATHandler.getNATConfigurationProperties();
                String serverName = null;
                if (FwsUtil.fsProps.getProperty("publicIP") != null) {
                    serverName = FwsUtil.fsProps.getProperty("publicIP");
                }
                else {
                    serverName = natProps2.getProperty("NAT_ADDRESS");
                }
                final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
                if (installed.equals("true")) {
                    final String remarksText = I18N.getMsg("mdm.fws.install.status.log", new Object[] { serverName });
                    DCEventLogUtil.getInstance().addEvent(9001, userName, (HashMap)null, remarksText, (Object)null, true);
                    ForwardingServerStatusServlet.logger.log(Level.INFO, "Secure gateway Server is installed successfully");
                }
                if (uninstalled.equals("true")) {
                    final String remarksText = I18N.getMsg("mdm.fws.uninstall.status.log", new Object[0]);
                    DCEventLogUtil.getInstance().addEvent(9002, userName, (HashMap)null, remarksText, (Object)null, true);
                    ForwardingServerStatusServlet.logger.log(Level.INFO, "Secure gateway Server is uninstalled successfully");
                }
            }
            catch (final JSONException ex2) {
                if (data.equals("true") || data.equals("false")) {
                    MDMUtil.updateSyMParameter("forwarding_server_config", data);
                }
                else {
                    MDMUtil.updateSyMParameter("forwarding_server_config", "false");
                }
            }
            catch (final Exception e3) {
                ForwardingServerStatusServlet.logger.log(Level.WARNING, "Exception while updatin forwarding server details {0} ", e3);
            }
            final Properties fwsProps2 = FwsUtil.fsProps;
            ((Hashtable<String, String>)serverInfoProps).put("forwarding_server_config", SyMUtil.getSyMParameter("forwarding_server_config"));
            ((Hashtable<String, String>)serverInfoProps).put("FsBuildNumber ", fwsProps2.getProperty("buildNumber"));
            ForwardingServerStatusServlet.logger.log(Level.INFO, "Props to going to update in server_info.props is {0}", serverInfoProps);
            if (installationTracking != null) {
                installationTracking.writeServerInfoProps(serverInfoProps);
            }
            MDMPFwsUtil.showOrHideSgsIncompatibilityMessage();
        }
    }
    
    private Long getLoginIDIFValidAuthToken(final String authToken) {
        Long loginID = null;
        if (authToken == null || authToken.trim().equals("")) {
            return loginID;
        }
        try {
            final JSONObject properties = new JSONObject();
            properties.put("API_KEY", (Object)authToken);
            final JSONObject userDetails = APIKeyUtil.getNewInstance().getUserDetails(properties);
            if (String.valueOf(userDetails.get("status")).equals("success")) {
                final Long userId = userDetails.getLong("USER_ID");
                loginID = DMUserHandler.getLoginIdForUserId(userId);
                return loginID;
            }
        }
        catch (final Exception ex) {
            ForwardingServerStatusServlet.logger.log(Level.SEVERE, "Exception occurred while fetching loginID from AuthToken", ex);
        }
        return loginID;
    }
    
    public JSONObject extractJSONFromBody(final HttpServletRequest request, final HttpServletResponse response) {
        Reader reader = null;
        try {
            final StringBuilder requestData = new StringBuilder();
            reader = SYMClientUtil.getInstance().getProperEncodedReader(request, reader);
            int read = 0;
            final char[] chBuf = new char[2000];
            while ((read = reader.read(chBuf)) > -1) {
                requestData.append(chBuf, 0, read);
            }
            if (requestData.length() > 0) {
                return new JSONObject(requestData.toString());
            }
            ForwardingServerStatusServlet.logger.log(Level.WARNING, "Empty request body ");
        }
        catch (final Exception ex) {
            ForwardingServerStatusServlet.logger.log(Level.SEVERE, "Exception while reader object in extractJSONFromBody {0}", ex);
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException e) {
                ForwardingServerStatusServlet.logger.log(Level.SEVERE, "Exception while closing the reader object {0}", e);
            }
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException e2) {
                ForwardingServerStatusServlet.logger.log(Level.SEVERE, "Exception while closing the reader object {0}", e2);
            }
        }
        return new JSONObject();
    }
    
    static {
        FWS_SETTINGS_FILE = "conf" + File.separator + "fwsSettings.conf";
        ForwardingServerStatusServlet.logger = Logger.getLogger("MDMLogger");
    }
}
