package com.adventnet.sym.webclient.mdm.apps.ios;

import java.util.Hashtable;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.HashMap;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.mdm.server.alerts.MDMAlertConstants;
import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.apps.AppInstallationStatusHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class IOSAppCatalogServlet extends DeviceAuthenticatedRequestServlet
{
    public Logger logger;
    public Logger accesslogger;
    String separator;
    String iPHONE;
    String iPAD;
    String mac;
    
    public IOSAppCatalogServlet() {
        this.logger = Logger.getLogger("MDMAppCatalogLogger");
        this.accesslogger = Logger.getLogger("MDMAppCatalogAccess");
        this.separator = "\t";
        this.iPHONE = "iPhone";
        this.iPAD = "iPad";
        this.mac = "Mac";
    }
    
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            final String strData = MDMUtil.getInstance().readRequest(request);
            this.logger.log(Level.INFO, "IOSAppCatalogServlet => Received data", strData);
            final String servletPath = request.getServletPath();
            final String action = servletPath.substring(1, servletPath.contains("mobapps") ? servletPath.indexOf(".mobapps") : servletPath.indexOf(".mobileapps"));
            final String userAgent = request.getHeader("user-agent");
            final String sUDID = request.getParameter("udid");
            final String sappId = request.getParameter("appId");
            final String filterCri = request.getParameter("filterCri");
            final String searchValue = request.getParameter("searchCri");
            boolean isIOS7AndAbove = false;
            final int serverVersion = MDMDeviceAPIKeyGenerator.getInstance().isClientVersion2_0(request.getServletPath()) ? APIKey.VERSION_2_0 : APIKey.VERSION_1_0;
            request.setAttribute("SERVER_VERSION", (Object)serverVersion);
            final int filterValue = -1;
            if (sUDID == null) {
                response.setContentType("text/plain");
                response.getWriter().println("enable_cookie");
                return;
            }
            this.logger.log(Level.INFO, "IOSAppCatalogServlet => Received data: sUDID: {0}", sUDID);
            this.logger.log(Level.INFO, "IOSAppCatalogServlet => Received data: servletPath: {0}", servletPath);
            this.logger.log(Level.INFO, "IOSAppCatalogServlet => Received data: userAgent: {0}", userAgent);
            Long resourceId = null;
            Long appId = null;
            String accessMessage = null;
            String deviceType = null;
            isIOS7AndAbove = (!userAgent.contains("OS 4_") && !userAgent.contains("OS 5_") && !userAgent.contains("OS 6_"));
            if (userAgent.contains(this.iPAD)) {
                deviceType = this.iPAD;
            }
            else if (userAgent.contains(this.iPHONE) || userAgent.contains(this.mac)) {
                deviceType = this.iPHONE;
            }
            if (sUDID != null) {
                request.setAttribute("udid", (Object)sUDID);
                resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID);
            }
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
            final int deviceModel = MDMUtil.getInstance().getModelTypeFromDB(resourceId);
            if (action.equalsIgnoreCase("showAppsList")) {
                response.setContentType("text/html");
                final String isNativeAgent = request.getParameter("isNativeAgent");
                if (isNativeAgent != null) {
                    request.setAttribute("isNativeAgent", (Object)isNativeAgent);
                }
                request.setAttribute("iosAppCatalog", (Object)true);
                this.getServletContext().getRequestDispatcher("/jsp/mdm/ember/deviceClientIndex.jsp").forward((ServletRequest)request, (ServletResponse)response);
                accessMessage = "show_app_list" + this.separator + deviceType + this.separator + resourceId + this.separator + "--";
            }
            else if (action.equalsIgnoreCase("installAppReq") && sappId != null) {
                appId = Long.valueOf(sappId);
                final Long appGroupId = (Long)DBUtil.getValueFromDB("MdAppToGroupRel", "APP_ID", (Object)appId, "APP_GROUP_ID");
                final AppInstallationStatusHandler updater = new AppInstallationStatusHandler();
                updater.updateAppInstallationStatusFromDevice(resourceId, appGroupId, appId, 1, "dc.db.mdm.apps.status.Installing", 0);
                final Long collectionId = MDMUtil.getInstance().getCollectionIDfromAppID(appId);
                MDMUtil.getInstance().wakeUpDeviceToInstallApp(resourceId, collectionId);
                final Criteria bundleCri = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)"com.manageengine.mdm.iosagent", 0);
                final Criteria customerIdCri = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerID, 0);
                final Criteria platformTypeCri = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)1, 0);
                final Criteria cri = bundleCri.and(customerIdCri).and(platformTypeCri);
                final DataObject nativeAgentDO = MDMUtil.getPersistence().get("MdAppGroupDetails", cri);
                if (!nativeAgentDO.isEmpty()) {
                    final Row nativeAgentRow = nativeAgentDO.getFirstRow("MdAppGroupDetails");
                    final Long nativeAgentAppGroupId = (Long)nativeAgentRow.get("APP_GROUP_ID");
                    if (appGroupId == (long)nativeAgentAppGroupId) {
                        final List resourceList = new ArrayList();
                        resourceList.add(resourceId);
                        DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(resourceList, 2);
                        final String sAppUpgrade = request.getParameter("isAppUpgrade");
                        if (!isIOS7AndAbove && (sAppUpgrade == null || !sAppUpgrade.equalsIgnoreCase("true"))) {
                            final String authPassword = IosNativeAppHandler.getInstance().generateEnrollmentId(resourceId);
                            final String deviceUDID = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(resourceId);
                            final HashMap userInfoMap = ManagedUserHandler.getInstance().getManagedUserDetailsForDevice(deviceUDID);
                            final String userName = userInfoMap.get("NAME");
                            final String userEmail = userInfoMap.get("EMAIL_ADDRESS");
                            final MDMAlertMailGeneratorUtil mailGenerator = new MDMAlertMailGeneratorUtil(this.logger);
                            final Properties serverProps = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties();
                            final Properties enrolAuthlMailProperties = new Properties();
                            ((Hashtable<String, String>)enrolAuthlMailProperties).put("$user_name$", userName);
                            ((Hashtable<String, Object>)enrolAuthlMailProperties).put("$server_name$", ((Hashtable<K, Object>)serverProps).get("NAT_ADDRESS"));
                            ((Hashtable<String, Object>)enrolAuthlMailProperties).put("$server_port$", ((Hashtable<K, Object>)serverProps).get("NAT_HTTPS_PORT"));
                            ((Hashtable<String, String>)enrolAuthlMailProperties).put("$enrollment_id$", authPassword);
                            ((Hashtable<String, String>)enrolAuthlMailProperties).put("$user_emailid$", userEmail);
                            mailGenerator.sendMail(MDMAlertConstants.ENROLLMENT_AUTHENTICATION_TEMPLATE, "MDM", customerID, enrolAuthlMailProperties);
                        }
                    }
                }
                accessMessage = "install_app_request" + this.separator + deviceType + this.separator + resourceId + this.separator + appId;
            }
            this.accesslogger.log(Level.INFO, accessMessage);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occoured in processRequest for IOSAppCatalogServlet..", ex);
        }
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            this.logger.log(Level.INFO, "IOSAppCatalogServlet => Received data -> doGet()");
            this.processRequest(request, response);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            this.logger.log(Level.INFO, "IOSAppCatalogServlet => Received data -> doPost()");
            this.processRequest(request, response);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
