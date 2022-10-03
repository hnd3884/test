package com.me.ems.onpremise.summaryserver.common;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Logger;

public class SSCommonDetails
{
    private static Logger logger;
    private static Properties summaryServerProps;
    
    public static String getSpecificValue(final String keyName) {
        return SSCommonDetails.summaryServerProps.getProperty(keyName);
    }
    
    static {
        SSCommonDetails.logger = Logger.getLogger(SSCommonDetails.class.getName());
        SSCommonDetails.summaryServerProps = new Properties();
        try {
            final String summaryServerConf = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "summaryServer.conf";
            FileAccessUtil.storeProperties(SSCommonDetails.summaryServerProps, summaryServerConf, true);
        }
        catch (final Exception e) {
            SSCommonDetails.logger.log(Level.SEVERE, "Exception while loading SummaryServerProperties in probe", e);
        }
    }
}
