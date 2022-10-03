package com.me.mdm.chrome.agent;

import com.me.mdm.chrome.agent.commands.profiles.payloads.UserVerifiedAccessPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.ExtensionInstallSourcesPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.ChromeBrowserRestrictionsPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.ManagedBookmarkPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.UserRestrictionPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.URLWhitelistBlacklistPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.PowerIdleSettingPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.ManagedGuestSessionPayloadHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.DeviceVerifiedAccessPayloadHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.CertificatePayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.VPNPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.EthernetPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.WifiPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.RestrictionPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.KioskPayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.PayloadRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.managedguestsession.PublicSessionManager;
import com.me.mdm.chrome.agent.commands.profiles.payloads.privacy.PrivacyManager;
import com.me.mdm.chrome.agent.commands.profiles.payloads.ExtensionInstallSources.ExtensionInstallSourceManager;
import org.json.JSONException;
import com.me.mdm.chrome.agent.commands.profiles.UserONCPayload;
import com.me.mdm.chrome.agent.commands.profiles.DeviceONCPayload;
import com.me.mdm.chrome.agent.commands.profiles.ONCPayload;
import com.me.mdm.chrome.agent.commands.profiles.payloads.verifiedaccess.UserVerifiedAccessManger;
import com.me.mdm.chrome.agent.commands.profiles.payloads.verifiedaccess.DeviceVerifiedAccessManager;
import com.me.mdm.chrome.agent.commands.profiles.payloads.verifiedaccess.VerifiedAccessManager;
import com.me.mdm.chrome.agent.commands.profiles.payloads.urlfilter.BookmarkManager;
import com.me.mdm.chrome.agent.commands.profiles.payloads.urlfilter.URLFilterManager;
import com.me.mdm.chrome.agent.commands.profiles.payloads.userrestriction.UserRestrictionManager;
import com.me.mdm.chrome.agent.commands.inventory.security.SecurityCommandRequestHandler;
import com.me.mdm.chrome.agent.commands.inventory.SystemActivityInfo;
import com.me.mdm.chrome.agent.commands.inventory.CustomFieldsInfo;
import com.me.mdm.chrome.agent.commands.inventory.AppsInfo;
import com.me.mdm.chrome.agent.commands.inventory.SecurityInfo;
import com.me.mdm.chrome.agent.commands.inventory.HardwareDetails;
import java.io.IOException;
import com.me.mdm.chrome.agent.commands.inventory.NetworkInfo;
import com.me.mdm.chrome.agent.commands.inventory.InventoryInfo;
import com.me.mdm.chrome.agent.enrollment.ChromeDeviceMDMEnrollmentProcessor;
import com.me.mdm.chrome.agent.enrollment.ChromeDeviceEnrollmentProcessor;
import com.me.mdm.chrome.agent.commands.inventory.AssetProcessRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.payloads.PrivacyCommandHandler;
import com.me.mdm.chrome.agent.commands.profile.osupdate.OSUpdateProcessRequestHandler;
import com.me.mdm.chrome.agent.Appmgmt.AppMgmtRequestHandler;
import com.me.mdm.chrome.agent.commands.profiles.ProfileRequestHandler;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.core.MDMServerContext;
import com.me.mdm.chrome.agent.core.ProcessRequestHandler;
import com.me.mdm.chrome.agent.core.DefaultProcessRequestHandler;
import com.me.mdm.chrome.agent.core.MDMAdapter;
import com.me.mdm.chrome.agent.core.MDMContainer;
import org.json.JSONObject;
import java.util.logging.Logger;

public class ChromeDeviceManager
{
    public Logger logger;
    private static ChromeDeviceManager manager;
    
    public ChromeDeviceManager() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public static ChromeDeviceManager getInstance() {
        return ChromeDeviceManager.manager;
    }
    
    public void startDeviceAgentWakeup(final String udid, final JSONObject esaData) throws Exception {
        final Context context = new DevicerContext(udid, esaData);
        final MDMContainer container = new MDMContainer(context);
        final MDMAdapter adapter = new MDMAdapter();
        adapter.setContainer(container);
        adapter.setRequestHandler(new DefaultProcessRequestHandler());
        adapter.initializeMDMServerContext(new MDMServerContext(context.getCustomerId(), context.getUdid()));
        adapter.start();
    }
    
    public void startUserAgentWakeup(final String guid, final JSONObject esaData) throws Exception {
        final Context context = new UserContext(guid, esaData);
        final MDMContainer container = new MDMContainer(context);
        final MDMAdapter adapter = new MDMAdapter();
        adapter.setContainer(container);
        adapter.setRequestHandler(new DefaultProcessRequestHandler());
        adapter.initializeMDMServerContext(new MDMServerContext(context.getCustomerId(), context.getUdid()));
        adapter.start();
    }
    
    public ProcessRequestHandler getProcessRequestHandler(final String handlerKey, final String scope, final Context context) {
        this.logger.log(Level.INFO, " Received Command : {0}", handlerKey);
        if (handlerKey == null) {
            return null;
        }
        if (context instanceof UserContext) {
            if (handlerKey.equals("InstallProfile") || handlerKey.equals("RemoveProfile")) {
                return new ProfileRequestHandler();
            }
            if (handlerKey.equals("InstallApplication") || handlerKey.equals("RemoveApplication")) {
                return new AppMgmtRequestHandler();
            }
        }
        else if (scope.equals("device") && context instanceof DevicerContext) {
            if (handlerKey.equals("AssetScan")) {
                return this.getAssetProcessRequestHandler();
            }
            if (handlerKey.equals("EnableLostMode") || handlerKey.equals("DisableLostMode") || handlerKey.equals("Deprovision") || handlerKey.equalsIgnoreCase("CorporateWipe") || handlerKey.equalsIgnoreCase("RemoveDevice") || handlerKey.equalsIgnoreCase("RestartDevice")) {
                return this.getSecurityCommandHandler();
            }
            if (handlerKey.equals("InstallProfile") || handlerKey.equals("RemoveProfile")) {
                return new ProfileRequestHandler();
            }
            if (handlerKey.equals("ChromeOsUpdatePolicy") || handlerKey.equals("RemoveChromeOsUpdatePolicy")) {
                return new OSUpdateProcessRequestHandler();
            }
            if (handlerKey.equals("SyncPrivacySettings")) {
                return new PrivacyCommandHandler();
            }
        }
        return null;
    }
    
    protected ProcessRequestHandler getAssetProcessRequestHandler() {
        return new AssetProcessRequestHandler();
    }
    
    public ChromeDeviceEnrollmentProcessor getEnrollmentProcessor() {
        return new ChromeDeviceMDMEnrollmentProcessor();
    }
    
    public InventoryInfo getNetworkInfo(final Context context) throws IOException {
        return new NetworkInfo(context);
    }
    
    public InventoryInfo getHardwareDetails(final Context context) throws IOException {
        return new HardwareDetails(context);
    }
    
    public InventoryInfo getSecurityInfo(final Context context) throws IOException {
        return new SecurityInfo(context);
    }
    
    public InventoryInfo getAppInfo(final Context context) throws IOException {
        return new AppsInfo(context);
    }
    
    public InventoryInfo getCustomFieldsInfo(final Context context) throws IOException {
        return new CustomFieldsInfo(context);
    }
    
    public InventoryInfo getSystemActivityInfo(final Context context) throws IOException {
        return new SystemActivityInfo(context);
    }
    
    public ProcessRequestHandler getSecurityCommandHandler() {
        return new SecurityCommandRequestHandler();
    }
    
    public UserRestrictionManager getUserRestrictionManager() {
        return new UserRestrictionManager();
    }
    
    public URLFilterManager getURLFilterManager() {
        return new URLFilterManager();
    }
    
    public BookmarkManager getBookmarkManager() {
        return new BookmarkManager();
    }
    
    public VerifiedAccessManager getVerifiedAccessManager(final Context context) {
        if (context instanceof DevicerContext) {
            return new DeviceVerifiedAccessManager();
        }
        return new UserVerifiedAccessManger();
    }
    
    public ONCPayload getONCPayload(final Context context) throws JSONException, IOException {
        if (context instanceof DevicerContext) {
            return new DeviceONCPayload(context);
        }
        return new UserONCPayload(context);
    }
    
    public ExtensionInstallSourceManager getExtensionInstallSourceManager() {
        return new ExtensionInstallSourceManager();
    }
    
    public PrivacyManager getPrivacyManager() {
        return new PrivacyManager();
    }
    
    public PublicSessionManager getPublicSessionManager() {
        return new PublicSessionManager();
    }
    
    public PayloadRequestHandler getPayloadRequestHandler(final Context context, final String payloadType) {
        this.logger.log(Level.INFO, " payload Type : {0}", payloadType);
        if (payloadType == null) {
            return null;
        }
        if (context instanceof DevicerContext) {
            if (payloadType.equalsIgnoreCase("Kiosk")) {
                return new KioskPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("Restrictions")) {
                return new RestrictionPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("Wifi")) {
                return new WifiPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("Ethernet")) {
                return new EthernetPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("VPN")) {
                return new VPNPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("Certificate") || payloadType.equalsIgnoreCase("Certificate")) {
                return new CertificatePayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("VerifiedAccess")) {
                return new DeviceVerifiedAccessPayloadHandler();
            }
            if (payloadType.equalsIgnoreCase("ManagedGuestSession")) {
                return new ManagedGuestSessionPayloadHandler();
            }
        }
        else if (context instanceof UserContext) {
            if (payloadType.equalsIgnoreCase("PowerIdleManagement")) {
                return new PowerIdleSettingPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("WebContent")) {
                return new URLWhitelistBlacklistPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("UserRestrictions")) {
                return new UserRestrictionPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("Wifi")) {
                return new WifiPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("Ethernet")) {
                return new EthernetPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("VPN")) {
                return new VPNPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("Certificate") || payloadType.equalsIgnoreCase("Certificate")) {
                return new CertificatePayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("ManagedBookMarks")) {
                return new ManagedBookmarkPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("ChromeBrowserRestriction")) {
                return new ChromeBrowserRestrictionsPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("ExtensionInstallSources")) {
                return new ExtensionInstallSourcesPayloadRequestHandler();
            }
            if (payloadType.equalsIgnoreCase("VerifiedAccess")) {
                return new UserVerifiedAccessPayloadRequestHandler();
            }
        }
        return null;
    }
    
    static {
        ChromeDeviceManager.manager = new ChromeDeviceManager();
    }
}
