package com.me.mdm.onpremise.server.service;

import java.util.logging.Level;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.ems.onpremise.server.util.ServerSettingsUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.webserver.WebServerControllerService;

public class MDMPWebServerControllerService extends WebServerControllerService
{
    private static Logger logger;
    
    public void start() throws Exception {
        super.start();
        final boolean clientSetting = ServerSettingsUtil.getDefaultClientSettings();
        if (clientSetting) {
            WebServerUtil.openBrowserUsingDCWinutil();
        }
        else {
            MDMPWebServerControllerService.logger.log(Level.INFO, "Start webclient on start-up option has been disabled.");
        }
    }
    
    static {
        MDMPWebServerControllerService.logger = Logger.getLogger("WebServerControllerLogger");
    }
}
