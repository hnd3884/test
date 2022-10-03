package com.me.mdm.webclient.home;

import java.util.Hashtable;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.JSONObject;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import java.io.File;
import java.util.Properties;
import java.util.Map;
import java.util.logging.Level;
import com.me.mdm.server.android.agentmigrate.AgentMigrationHandler;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class MDMHomePageUtils
{
    static Logger logger;
    
    public static void homePageDetails(final HttpServletRequest request) {
        final Map dashDetails = new HashMap();
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            dashDetails.put("activeDevice", MDMEnrollmentUtil.getInstance().getEnrolledDeviceCount(customerID));
            dashDetails.put("pendingDevice", MDMEnrollmentUtil.getInstance().getYetToEnrollRequestCount(customerID));
            dashDetails.put("inactiveDevice", MDMUtil.getInstance().getInactiveDeviceCountByPeriod(7, customerID));
            dashDetails.put("uniqueUser", ManagedUserHandler.getInstance().getManagedUsersWithDevicesCount(customerID));
            dashDetails.put("blackListApp", MDMUtil.getInstance().getBlackListAppCount(CustomerInfoUtil.getInstance().getCustomerId()));
            final int safeMigrationDeviceCount = AgentMigrationHandler.getInstance().getYetToUpgradeDeviceCount();
            if (safeMigrationDeviceCount > 0) {
                request.setAttribute("migrationDeviceCount", (Object)safeMigrationDeviceCount);
            }
        }
        catch (final Exception exp) {
            MDMHomePageUtils.logger.log(Level.SEVERE, "Exception while loading dashbord data", exp);
        }
        request.setAttribute("dashDetails", (Object)dashDetails);
    }
    
    public static HashMap getGraphDetails() {
        final HashMap<String, Properties> mdmGraphs = new HashMap<String, Properties>();
        final Properties mdmDevices = new Properties();
        ((Hashtable<String, String>)mdmDevices).put("generatorClass", "com.adventnet.sym.server.mdm.graphs.MDMGraphDataProducerImpl");
        ((Hashtable<String, String>)mdmDevices).put("xmlLocation", "conf" + File.separator + "MDM" + File.separator + "graphs" + File.separator + "mdmDeviceType.xml");
        ((Hashtable<String, String>)mdmDevices).put("chartGenerator", "com.me.mdm.webclient.zohoCharts.ZohoChartJSONGeneratorImpl");
        mdmGraphs.put("mdmdevices", mdmDevices);
        final Properties mdmBYODSummary = new Properties();
        ((Hashtable<String, String>)mdmBYODSummary).put("generatorClass", "com.adventnet.sym.server.mdm.graphs.MDMGraphDataProducerImpl");
        ((Hashtable<String, String>)mdmBYODSummary).put("xmlLocation", "conf" + File.separator + "MDM" + File.separator + "graphs" + File.separator + "mdmBYODSummary.xml");
        ((Hashtable<String, String>)mdmBYODSummary).put("chartGenerator", "com.me.mdm.webclient.zohoCharts.ZohoChartJSONGeneratorImpl");
        mdmGraphs.put("mdmBYODSummary", mdmBYODSummary);
        final Properties mdmLastSeenBreakdownCount = new Properties();
        ((Hashtable<String, String>)mdmLastSeenBreakdownCount).put("generatorClass", "com.adventnet.sym.server.mdm.graphs.MDMGraphDataProducerImpl");
        ((Hashtable<String, String>)mdmLastSeenBreakdownCount).put("xmlLocation", "conf" + File.separator + "MDM" + File.separator + "graphs" + File.separator + "mdmLastSeenBreakdownCount.xml");
        ((Hashtable<String, String>)mdmLastSeenBreakdownCount).put("chartGenerator", "com.me.mdm.webclient.zohoCharts.ZohoChartJSONGeneratorImpl");
        mdmGraphs.put("mdmLastSeenBreakdownCount", mdmLastSeenBreakdownCount);
        final Properties mdmAppSummary = new Properties();
        ((Hashtable<String, String>)mdmAppSummary).put("generatorClass", "com.adventnet.sym.server.mdm.graphs.MDMGraphDataProducerImpl");
        ((Hashtable<String, String>)mdmAppSummary).put("xmlLocation", "conf" + File.separator + "MDM" + File.separator + "graphs" + File.separator + "mdmAppStatusSummary.xml");
        ((Hashtable<String, String>)mdmAppSummary).put("chartGenerator", "com.me.mdm.webclient.zohoCharts.ZohoChartJSONGeneratorImpl");
        mdmGraphs.put("mdmappsummary", mdmAppSummary);
        final Properties mdmPlatformSummary = new Properties();
        ((Hashtable<String, String>)mdmPlatformSummary).put("generatorClass", "com.adventnet.sym.server.mdm.graphs.MDMGraphDataProducerImpl");
        ((Hashtable<String, String>)mdmPlatformSummary).put("xmlLocation", "conf" + File.separator + "MDM" + File.separator + "graphs" + File.separator + "mdmOsSummary.xml");
        ((Hashtable<String, String>)mdmPlatformSummary).put("chartGenerator", "com.me.mdm.webclient.zohoCharts.ZohoChartJSONGeneratorImpl");
        mdmGraphs.put("mdmplatform", mdmPlatformSummary);
        return mdmGraphs;
    }
    
    public static void setCommonHomePageMessages(final String attributeName, final HttpServletRequest request) {
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        final MessageProvider msgPro = MessageProvider.getInstance();
        final Properties msgProperty = msgPro.getPropertiesList("MDM_HOME_PAGE", (Properties)null, customerID, request);
        request.setAttribute(attributeName, (Object)msgProperty);
    }
    
    public JSONObject getGdprWidgetProps() {
        final JSONObject json = new JSONObject();
        boolean showGdprwidget = false;
        long securePerc = 0L;
        String securemessage = "";
        try {
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            String showGdprWidgetStr = CustomerParamsHandler.getInstance().getParameterValue("showGdprWidget", (long)customerID);
            showGdprWidgetStr = ((showGdprWidgetStr == null) ? "false" : showGdprWidgetStr);
            if (!showGdprWidgetStr.equalsIgnoreCase("--")) {
                showGdprwidget = Boolean.parseBoolean(showGdprWidgetStr);
            }
            else {
                final String licenseType = LicenseProvider.getInstance().getLicenseType();
                if (!licenseType.equalsIgnoreCase("T") || (licenseType.equalsIgnoreCase("T") && MDMEnrollmentUtil.getInstance().getEnrolledDeviceCount(customerID) >= 10)) {
                    showGdprwidget = true;
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("showGdprWidget", "true", (long)customerID);
                }
                else {
                    showGdprwidget = false;
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("showGdprWidget", "false", (long)customerID);
                }
            }
            if (showGdprwidget) {
                securePerc = MDMApiFactoryProvider.getMDMGDPRSettingsAPI().getSecureSettings(customerID).get("SECURE_PERCENTAGE");
                if (securePerc < 40L) {
                    securemessage = "mdm.security.low_score";
                }
                else if (securePerc < 70L) {
                    securemessage = "mdm.security.med_score";
                }
                else {
                    securemessage = "mdm.security.high_score";
                }
            }
            json.put("showGdprWidget", showGdprwidget);
            json.put("securePercent", securePerc);
            json.put("secureMessage", (Object)securemessage);
        }
        catch (final Exception exp) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception while getting security data", exp);
        }
        return json;
    }
    
    static {
        MDMHomePageUtils.logger = Logger.getLogger(MDMHomePageUtils.class.getName());
    }
}
