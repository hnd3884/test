package com.me.mdm.onpremise.server.admin;

import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.common.FlashMessage;

public class MDMPFlashMessage extends FlashMessage
{
    private static Logger logger;
    
    public MDMPFlashMessage() {
        this.updatesAnalyzer = new MDMPUpdatesAnalyzer();
    }
    
    public String getDivideByKey() {
        return "NoOfDeviceDivideByValue";
    }
    
    public String getDivideByServerKey() {
        return "noofmobile";
    }
    
    static {
        MDMPFlashMessage.logger = Logger.getLogger(MDMPFlashMessage.class.getName());
    }
}
