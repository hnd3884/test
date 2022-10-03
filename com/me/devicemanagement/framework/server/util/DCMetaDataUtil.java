package com.me.devicemanagement.framework.server.util;

import java.io.File;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DCMetaDataUtil
{
    protected static DCMetaDataUtil cfgUtilfw;
    protected static final String CLIENT_DATA_DIR_NAME = "client-data";
    public static final String COLLECTION_DATA_DIR_NAME = "collections";
    protected static final String DC_TEMP = "dc-temp";
    protected static final String SCRIPT_REPOSITORY_DIR_NAME = "scripts-repository";
    protected static final String PATCH_RESOURCES_DIR_NAME = "patch-resources";
    protected static final String VULNERABLE_RESOURCES_DIR_NAME = "vulnerable-resources";
    protected static final String CRITERIA_GROUP_DIR_NAME = "criteria-group";
    public static final String DOMAINS_DIR_NAME = "domains";
    public static final String META_DATA = "metaData";
    public static final String COMPUTER_META_DATA = "ComputerMetaData";
    public static final String COMPUTER_AA_META_DATA = "ComputerAAMetaData";
    public static final String USER_META_DATA = "UserMetaData";
    public static final String USER_AA_META_DATA = "UserAAMetaData";
    public static final String TRASH_META_DATA = "TrashMetaData";
    public static final String COMPUTER_MAC_META_DATA = "ComputerMacMetaData";
    public static final String COMPUTER_MAC_AA_META_DATA = "ComputerMacAAMetaData";
    public static final String USER_MAC_META_DATA = "UserMacMetaData";
    public static final String USER_MAC_AA_META_DATA = "UserMacAAMetaData";
    public static final String COMPUTER_LINUX_META_DATA = "ComputerLinuxMetaData";
    public static final String COMPUTER_LINUX_AA_META_DATA = "ComputerLinuxAAMetaData";
    public static final String USER_LINUX_META_DATA = "UserLinuxMetaData";
    public static final String USER_LINUX_AA_META_DATA = "UserLinuxAAMetaData";
    public static final String CUSTOM_GROUPS_META_DATA = "CustomGroupsMetaData";
    public static final String CG_META_DATA = "CGMetaData";
    public static final String CG_DELETED_META_DATA = "DeletedCGMetaData";
    public static final String AUTO_UNINSTALL_DATA = "AutoUninstallSWData";
    public static final String INVENTORY_GLOBAL_FILTER = "ProhibitedSWGlobalFilter";
    public static final String INVENTORY_APPROVED_REJECTED_DATA = "ProhibitedSWData";
    public static final String UNINSTALL_SW_DATA = "SilentUninstallSWData";
    public static final String SW_NOTIFICATION = "SoftwareNotificationData";
    public static final String DIFF_NOTIFICATION = "DiffNotificationData";
    public static final String AGENT_COMPONENT_SETTINGS = "AgentComponentSettings";
    public static final String INVENTORY_SCAN_DATA = "InvScanData";
    public static final String INV_FILE_SCAN_RULES = "InvFileScanRules";
    public static final String GLOBAL_META_DATA = "globalmetadata";
    public static final String GLOBAL_META_DATA_XML = "global-meta-data.xml";
    public static final String SOFTWARE_DETAILS_XML = "SoftwareDetails.xml";
    public static final String BOUNDARY_LIST = "boundary-list";
    public static final String INVENTORY_SW_METERING_DATA = "swmeter-rules";
    public static final String USB_SETTINGS_DATA = "usb-settings";
    public static final String USB_ALERT_SETTINGS_DATA = "usb-alert-settings";
    public static final String SERVER_DATA_DIR_NAME = "server-data";
    public static final String CRITERIA_CUSTOMGROUP_META_DATA = "CriteriaGroupMetaData";
    public static final String DEPLOY_TEMPLATES_DATA = "deployment-policies";
    public static final String DECLINED_FAMILY = "declined-family";
    public static final String DECLINED_APP = "declined-applications";
    public static final String DECLINED_PATCH = "declined-patches";
    public static final String APPROVED_PATCH = "approved-patches";
    public static final String COLL_PATCH_DATA = "collnpatchversion";
    public static final Integer DS_PREGLOBAL_DATA;
    public static final Integer DS_GLOBAL_DATA;
    public static final Integer DS_CUSTOMER_WISE_DATA;
    public static final Integer DS_DOMAIN_WISE_DATA;
    public static final Integer DATA_STATUS_FAILED;
    public static final Integer DATA_STATUS_RUNNING;
    public static final String HELP_DESK_APPNAME = "HelpDesk";
    protected static final String MDM = "mdm";
    protected static final String PROFILES = "profiles";
    public static final String CUSTOMER_DATA = "customer-data";
    public static final String CUSTOMER_LOGO = "logo";
    public static final String MOBILECONFIG_DIR = "mobileconfigs";
    public static final String COLLECTIONS_DIR = "collections";
    public static final String SW_PUBLISH = "SWPackagePublish";
    public static final String COMPUTER_SSP_META_DATA = "SSPComputerMetaData";
    public static final String USER_SSP_META_DATA = "SSPUserMetaData";
    public static final String COMPUTER_BLOCK_EXEC_META_DATA = "BlockExecComputerMetaData";
    protected Logger logger;
    
    public DCMetaDataUtil() {
        this.logger = Logger.getLogger(DCMetaDataUtil.class.getName());
    }
    
    public static synchronized DCMetaDataUtil getInstance() {
        if (DCMetaDataUtil.cfgUtilfw == null) {
            DCMetaDataUtil.cfgUtilfw = new DCMetaDataUtil();
        }
        return DCMetaDataUtil.cfgUtilfw;
    }
    
    public String getClientDataParentDir() {
        String baseDir = System.getProperty("server.home");
        this.logger.log(Level.FINEST, "Server Home from System props: " + baseDir);
        CustomerInfoUtil.getInstance();
        if (CustomerInfoUtil.isSAS()) {
            baseDir = new File(baseDir).getParent();
        }
        else {
            baseDir = baseDir + File.separator + "webapps" + File.separator + "DesktopCentral";
        }
        return baseDir;
    }
    
    public String getClientDataDirRelative() {
        return "client-data";
    }
    
    public String getClientDataDirRelative(final Long customerId) {
        final String customer_clientdata_dir = "client-data" + File.separator + customerId;
        return customer_clientdata_dir;
    }
    
    public String getClientDataDir() {
        final String clientDataDir = this.getClientDataParentDir() + File.separator + "client-data";
        return clientDataDir;
    }
    
    public String getClientDataDir(final Long customerId) {
        final String clientDataDir = this.getClientDataParentDir() + File.separator + this.getClientDataDirRelative(customerId);
        return clientDataDir;
    }
    
    public String getCustomerDataFolderPath(final Long customerID) {
        final String filePath = this.getClientDataDir(customerID) + File.separator + "customer-data";
        return filePath;
    }
    
    public String getCustomerLogoFolderPath(final Long customerID) {
        final String filePath = this.getCustomerDataFolderPath(customerID) + File.separator + "logo";
        return filePath;
    }
    
    static {
        DCMetaDataUtil.cfgUtilfw = null;
        DS_PREGLOBAL_DATA = new Integer(50);
        DS_GLOBAL_DATA = new Integer(100);
        DS_CUSTOMER_WISE_DATA = new Integer(200);
        DS_DOMAIN_WISE_DATA = new Integer(300);
        DATA_STATUS_FAILED = new Integer(2);
        DATA_STATUS_RUNNING = new Integer(1);
    }
}
