package com.me.mdm.chrome.agent;

import java.io.IOException;
import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.google.chromedevicemanagement.v1.model.Device;
import com.google.api.services.directory.model.ChromeOsDevice;

public class DevicerContext extends Context
{
    private ChromeOsDevice chromeDeviceDirectoryModel;
    private Device cromeDeviceCMPAModel;
    Logger logger;
    
    public DevicerContext(final String udid, final JSONObject esaDetails) throws JSONException, Exception {
        super(udid, esaDetails);
        this.chromeDeviceDirectoryModel = null;
        this.cromeDeviceCMPAModel = null;
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    @Override
    public String getCMPAEnterpriseAndUDID() {
        return "enterprises/" + this.getEnterpriseId() + "/devices/" + this.getUdid();
    }
    
    public ChromeOsDevice getChromeOsDeviceFromDirectory() throws IOException {
        if (this.chromeDeviceDirectoryModel == null) {
            this.chromeDeviceDirectoryModel = (ChromeOsDevice)this.getDirectoryService().chromeosdevices().get(this.getEnterpriseId(), this.getUdid()).execute();
            this.logger.log(Level.INFO, "GetChromeOsDeviceFromDirectory {0}", this.chromeDeviceDirectoryModel.toPrettyString());
        }
        return this.chromeDeviceDirectoryModel;
    }
    
    public Device getChromeOsDeviceFromCMPA() throws IOException {
        if (this.cromeDeviceCMPAModel == null) {
            this.cromeDeviceCMPAModel = (Device)this.getCMPAService().enterprises().devices().get(this.getCMPAEnterpriseAndUDID()).execute();
            this.logger.log(Level.INFO, "GetChromeOsDeviceFromCMPA {0}", this.cromeDeviceCMPAModel.toPrettyString());
        }
        return this.cromeDeviceCMPAModel;
    }
}
