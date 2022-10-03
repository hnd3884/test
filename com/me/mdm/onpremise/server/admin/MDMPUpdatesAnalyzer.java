package com.me.mdm.onpremise.server.admin;

import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.common.UpdatesAnalyzer;

public class MDMPUpdatesAnalyzer extends UpdatesAnalyzer
{
    private static Logger logger;
    
    public Object fetchServerValueFor(final String conditionKey) {
        MDMPUpdatesAnalyzer.logger.log(Level.FINE, "Going to Fetch the Server Value for Key:{0}", conditionKey);
        final Object serverValue = super.fetchServerValueFor(conditionKey);
        if (serverValue == null) {
            switch (conditionKey) {
                case "noofmobile": {
                    final String noOfComp = LicenseProvider.getInstance().getNoOfMobileDevicesManaged();
                    if (noOfComp != null && !noOfComp.equalsIgnoreCase("unlimited")) {
                        return Long.valueOf(noOfComp);
                    }
                    MDMPUpdatesAnalyzer.logger.log(Level.INFO, "Couldn't Fetch Value for No of Computer as the value is either null or unlimited");
                    break;
                }
            }
            return null;
        }
        return serverValue;
    }
    
    public String fetchDataTypeFor(final String conditionKey) {
        MDMPUpdatesAnalyzer.logger.log(Level.FINE, "Going to Fetch DataType for conditon:{0}", conditionKey);
        final String dataType = super.fetchDataTypeFor(conditionKey);
        if (dataType != null) {
            return dataType;
        }
        switch (conditionKey) {
            case "noofmobile": {
                return "Long";
            }
            default: {
                MDMPUpdatesAnalyzer.logger.log(Level.INFO, "Couldn''t Fetch DataType for{0} as no Proper Definition was found", conditionKey);
                return "";
            }
        }
    }
    
    static {
        MDMPUpdatesAnalyzer.logger = Logger.getLogger(MDMPUpdatesAnalyzer.class.getName());
    }
}
