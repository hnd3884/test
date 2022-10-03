package com.me.mdm.onpremise.api.settings;

import com.me.devicemanagement.framework.server.logger.seconelinelogger.SecurityOneLineLogger;
import com.me.devicemanagement.framework.webclient.common.OnlineUrlLoader;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.onpremise.server.settings.NATReachabilityTask;
import java.util.Map;
import com.me.devicemanagement.onpremise.server.settings.nat.NATObject;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.onpremise.server.settings.nat.NATHandler;
import java.util.HashMap;
import org.json.JSONObject;
import java.util.logging.Logger;

public class NATSettingsFacade
{
    public Logger logger;
    
    public NATSettingsFacade() {
        this.logger = Logger.getLogger(NATSettingsFacade.class.getName());
    }
    
    public JSONObject getNATSettings() {
        try {
            final JSONObject resultJson = new JSONObject();
            HashMap NATDetails = new HashMap();
            NATDetails = NATHandler.getInstance().setNATvaluesInForm(NATDetails);
            resultJson.put("nat_address", NATDetails.get("NAT_ADDRESS"));
            resultJson.put("nat_https_port", NATDetails.get("NAT_HTTPS_PORT"));
            resultJson.put("https_port", NATDetails.get("HTTPS_PORT"));
            resultJson.put("server_address", NATDetails.get("SERVER_ADDRESS"));
            if ("true".equalsIgnoreCase(MDMUtil.getSyMParameter("forwarding_server_config"))) {
                resultJson.put("is_sgs_configured", true);
            }
            else {
                resultJson.put("is_sgs_configured", false);
            }
            return resultJson;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting NAT details...", ex);
            throw new APIHTTPException("NAT001", new Object[0]);
        }
    }
    
    public JSONObject saveNATSettings(final JSONObject jsonObject) {
        String remarks = "update-failed";
        try {
            final JSONObject body = jsonObject.getJSONObject("msg_body");
            final NATObject natobject = new NATObject();
            final JSONObject responseJSON = new JSONObject();
            final Map<String, Object> NATDetails = new HashMap<String, Object>();
            final String natAddress = String.valueOf(body.get("nat_address"));
            final Boolean isValidNatAddress = this.checkValidityOfNatAddress(natAddress);
            if (!isValidNatAddress) {
                throw new APIHTTPException("NAT005", new Object[0]);
            }
            final Integer natPort = body.getInt("nat_https_port");
            final Boolean updateWithError = body.getBoolean("update_with_error");
            natobject.givenNATAddress = natAddress;
            NATDetails.put("NAT_RDS_HTTPS_PORT", 0);
            NATDetails.put("NAT_FT_HTTPS_PORT", 0);
            NATDetails.put("NAT_NS_PORT", 0);
            NATDetails.put("NAT_CHAT_PORT", 0);
            NATDetails.put("NAT_ADDRESS", natAddress);
            NATDetails.put("NAT_HTTPS_PORT", natPort);
            final String update_safe = NATHandler.getInstance().checkIsNATUpdateSafe(natobject);
            if (update_safe.equalsIgnoreCase("update_safe") || updateWithError) {
                final NATObject natobj = NATHandler.getInstance().saveNATsettings((Map)NATDetails);
                final String result = natobj.statusOnSavingNATdetails;
                NATReachabilityTask.isNATexposed(natAddress, (int)natPort);
                NATHandler.getInstance().invokeNATChangedListeners(natobj);
                remarks = "update-success";
                return responseJSON.put("result", (Object)result);
            }
            final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount();
            this.logger.log(Level.SEVERE, "Already devices managed with this NAT will lose communication");
            if (managedDeviceCount > 0) {
                final String url = OnlineUrlLoader.getInstance().getValue("url.settings.nat_setings.modify_nat", true);
                throw new APIHTTPException("NAT003", new Object[] { url });
            }
            throw new APIHTTPException("NAT004", new Object[] { update_safe });
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in saving NAT Details", ex);
            throw new APIHTTPException("NAT002", new Object[0]);
        }
        finally {
            SecurityOneLineLogger.log("SETTINGS", "SAVE_NAT", remarks, Level.INFO);
        }
    }
    
    boolean checkValidityOfNatAddress(final String natAddress) {
        return !natAddress.contains("_");
    }
}
