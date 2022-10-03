package com.me.mdm.server.agent;

import com.dd.plist.NSObject;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.mdm.core.auth.APIKey;
import com.dd.plist.NSDictionary;
import org.json.JSONException;
import com.me.mdm.server.drp.MDMRegistrationHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;

public abstract class DiscoveryServiceHandler
{
    private static DiscoveryServiceHandler discoveryServiceHandler;
    protected static final String URLS = "urls";
    
    protected DiscoveryServiceHandler() {
    }
    
    public static DiscoveryServiceHandler getInstance() {
        if (DiscoveryServiceHandler.discoveryServiceHandler == null) {
            try {
                if (CustomerInfoUtil.isSAS) {
                    DiscoveryServiceHandler.discoveryServiceHandler = (DiscoveryServiceHandler)Class.forName("com.me.mdmcloud.server.agent.DiscoveryServiceHandlerCloudImpl").newInstance();
                }
                else {
                    DiscoveryServiceHandler.discoveryServiceHandler = (DiscoveryServiceHandler)Class.forName("com.me.mdm.onpremise.server.agent.DiscoveryServiceHandlerOnpremiseImpl").newInstance();
                }
            }
            catch (final ClassNotFoundException ce) {
                Logger.getLogger(DiscoveryServiceHandler.class.getName()).log(Level.SEVERE, "ClassNotFoundException  during Instantiation for MDMADSyncAPI... ", ce);
            }
            catch (final InstantiationException ie) {
                Logger.getLogger(DiscoveryServiceHandler.class.getName()).log(Level.SEVERE, "InstantiationException During Instantiation  for MDMADSyncAPI...", ie);
            }
            catch (final IllegalAccessException ie2) {
                Logger.getLogger(DiscoveryServiceHandler.class.getName()).log(Level.SEVERE, "IllegalAccessException During Instantiation  for MDMADSyncAPI...", ie2);
            }
            catch (final Exception ex) {
                Logger.getLogger(DiscoveryServiceHandler.class.getName()).log(Level.SEVERE, "Exception During Instantiation  for MDMADSyncAPI...", ex);
            }
        }
        return DiscoveryServiceHandler.discoveryServiceHandler;
    }
    
    public JSONObject getLatestAgentCommDetails(final JSONObject requestJSON) {
        try {
            final String platformType = String.valueOf(requestJSON.get("DevicePlatform"));
            final String agentType = String.valueOf(requestJSON.get("AgentType"));
            final int platformvalue = MDMRegistrationHandler.getInstance(platformType).getPlatformConstant(platformType);
            final JSONObject json = new JSONObject();
            switch (platformvalue) {
                case 1: {
                    json.put("IOSNativeAppServlet", (Object)"/mdm/client/v1/nativeappserver");
                    json.put("DeviceRegistrationServlet", (Object)"/mdm/client/v1/drs");
                    json.put("IOSCheckInServlet", (Object)"/mdm/client/v1/ioscheckin");
                    json.put("AppCatalogServlet", (Object)"/showAppsList.mobileapps");
                    json.put("MDMLogUploaderServlet", (Object)"/mdm/client/v1/mdmLogUploader");
                    break;
                }
                case 2: {
                    if (agentType.equalsIgnoreCase("NFCEnrollmentApp")) {
                        json.put("AndroidAdminAuthServlet", (Object)"/mdm/client/v1/auth");
                        json.put("AndroidAdminMsgServlet", (Object)"/mdm/client/v1/admin/msg");
                        json.put("AndroidAdminCmdServlet", (Object)"/mdm/client/v1/admin/cmd");
                        break;
                    }
                    json.put("AndroidCheckinServlet", (Object)"/mdm/client/v1/androidcheckin");
                    json.put("SafeCheckinServlet", (Object)"/mdm/client/v1/safecheckin");
                    json.put("AndroidNativeAppServlet", (Object)"/mdm/client/v1/androidnativeapp");
                    json.put("DEPServlet", (Object)"/mdm/client/v1/dep");
                    json.put("MDMLogUploaderServlet", (Object)"/mdm/client/v1/mdmLogUploader");
                    json.put("MDMFileUploadServlet", (Object)"/mdm/client/v1/fileupload");
                    break;
                }
            }
            final JSONObject responseJSON = new JSONObject();
            final JSONObject msgResponsJSON = new JSONObject();
            msgResponsJSON.put("urls", (Object)json);
            responseJSON.put("MessageResponse", (Object)msgResponsJSON);
            responseJSON.put("Status", (Object)"Acknowledged");
            responseJSON.put("MessageType", (Object)"ServiceDiscovery");
            return responseJSON;
        }
        catch (final JSONException ex) {
            Logger.getLogger(DiscoveryServiceHandler.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            return new JSONObject();
        }
    }
    
    public NSDictionary setIOSAgentCommDetails(final NSDictionary dict, final APIKey key) {
        final NSDictionary servicesDict = new NSDictionary();
        final Boolean isProfessional = LicenseProvider.getInstance().getMDMLicenseAPI().isProfessionalLicenseEdition();
        final NSDictionary urlsDict = new NSDictionary();
        urlsDict.put("IOSNativeAppServlet", (Object)"/mdm/client/v1/nativeappserver");
        urlsDict.put("DeviceRegistrationServlet", (Object)"/mdm/client/v1/drs");
        urlsDict.put("IOSCheckInServlet", (Object)"/mdm/client/v1/ioscheckin");
        urlsDict.put("AppCatalogServlet", (Object)"/showAppsList.mobileapps");
        urlsDict.put("MDMLogUploaderServlet", (Object)"/mdm/client/v1/mdmLogUploader");
        if (isProfessional) {
            urlsDict.put("mdmDocsServlet", (Object)"/mdm/client/v1/docs");
        }
        servicesDict.put("urls", (NSObject)urlsDict);
        if (key != null && key.getVersion() == APIKey.VERSION_2_0) {
            servicesDict.put("token_name", (Object)key.getKeyName());
            servicesDict.put("token_value", (Object)key.getKeyValue());
        }
        else {
            servicesDict.put("token_name", (Object)"");
            servicesDict.put("token_value", (Object)"");
        }
        return servicesDict;
    }
    
    protected void putIfKeyIsPresentInConfig(final NSDictionary dict, final String key, final Object obj) {
        if (((NSDictionary)dict.get((Object)"Configuration")).containsKey(key)) {
            ((NSDictionary)dict.get((Object)"Configuration")).put(key, obj);
        }
    }
    
    static {
        DiscoveryServiceHandler.discoveryServiceHandler = null;
    }
}
