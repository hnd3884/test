package com.me.mdm.onpremise.api.settings;

import java.nio.file.Files;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import com.me.mdm.api.APIRequest;
import java.util.Map;
import java.util.LinkedHashMap;
import com.adventnet.sym.webclient.admin.fos.FosTrialLicense;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.persistence.fos.FOS;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.adventnet.sym.server.fos.FailoverServerUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class FOSSettingsFacade
{
    public Logger logger;
    
    public FOSSettingsFacade() {
        this.logger = Logger.getLogger(FOSSettingsFacade.class.getName());
    }
    
    public JSONObject optFOSTrial() {
        final JSONObject resultJson = new JSONObject();
        try {
            final Boolean result = FailoverServerUtil.getFosTrialLicense();
            resultJson.put("result", (Object)result);
            if (result) {
                MessageProvider.getInstance().hideMessage("FOS_NOT_PURCHASED");
            }
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting FOS Trial", ex);
            throw new APIHTTPException("FOS001", new Object[0]);
        }
        return resultJson;
    }
    
    public JSONObject getFOSDetails() {
        JSONObject resultJson = new JSONObject();
        try {
            resultJson = FailoverServerUtil.getInputConfigurations(resultJson);
            resultJson.put("is_fos_enabled", FOS.isEnabled());
            final Boolean isSlaveConfigured = FailoverServerUtil.isSlaveConfigured();
            final Boolean isFosEnabled = LicenseProvider.getInstance().isFosEnabled();
            Boolean isFosTrialEnabled = false;
            if (!isFosEnabled) {
                isFosTrialEnabled = FosTrialLicense.isFosTrialFlagEnabled();
                resultJson.put("trial_expiry_date", FosTrialLicense.getFosTrialExpiryPeriod());
            }
            if (isSlaveConfigured) {
                resultJson = FailoverServerUtil.getSlaveDetails(resultJson);
            }
            resultJson.put("is_slave_configured", (Object)isSlaveConfigured);
            resultJson.put("is_fos_trial_enabled", (Object)isFosTrialEnabled);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in getting FOS details...", ex);
            throw new APIHTTPException("FOS002", new Object[0]);
        }
        return resultJson;
    }
    
    public JSONObject saveFOSDetails(final JSONObject jsonObject) {
        final JSONObject resultJson = new JSONObject();
        try {
            final JSONObject body = jsonObject.getJSONObject("msg_body");
            final Map fosProperties = new LinkedHashMap();
            fosProperties.put("SecondaryServerIP", String.valueOf(body.get("secondary_ip")));
            fosProperties.put("PrimaryServerIP", String.valueOf(body.get("primary_ip")));
            fosProperties.put("PublicIP", String.valueOf(body.get("public_ip")));
            fosProperties.put("EMAIL_ID", String.valueOf(body.get("email")));
            fosProperties.put("EnableFos", Boolean.TRUE);
            final Boolean result = FailoverServerUtil.saveSecondaryServer(fosProperties);
            resultJson.put("success", (Object)result);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in saving FOS details...", ex);
            throw new APIHTTPException("FOS003", new Object[0]);
        }
        return resultJson;
    }
    
    public JSONObject editFOS(final JSONObject jsonObject) {
        final JSONObject resultJson = new JSONObject();
        try {
            final JSONObject body = jsonObject.getJSONObject("msg_body");
            final String email = String.valueOf(body.get("email"));
            final Boolean revertFOS = body.optBoolean("revertFOS");
            final Boolean result = FailoverServerUtil.updateFOS(body);
            resultJson.put("success", (Object)result);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "error in updating FOS details...", ex);
            throw new APIHTTPException("FOS004", new Object[0]);
        }
        return resultJson;
    }
    
    public void downloadConfigurationfile(final APIRequest apiRequest) {
        BufferedOutputStream buffOut = null;
        try {
            apiRequest.httpServletResponse.setContentType("application/bat");
            apiRequest.httpServletResponse.setHeader("Content-Disposition", "attachment; filename=Configure_Failover_Server.bat");
            final String path = File.separatorChar + "bin" + File.separatorChar + "Configure_Failover_Server.bat";
            final String filepath = System.getProperty("server.home") + path;
            buffOut = new BufferedOutputStream((OutputStream)apiRequest.httpServletResponse.getOutputStream());
            final File file = new File(filepath);
            final byte[] b = Files.readAllBytes(file.toPath());
            buffOut.write(b);
            buffOut.flush();
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while download sample file", exp);
            throw new APIHTTPException("FOS005", new Object[0]);
        }
        finally {
            try {
                buffOut.close();
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception while closing streams", e);
                throw new APIHTTPException("FOS005", new Object[0]);
            }
        }
    }
}
