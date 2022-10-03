package com.me.devicemanagement.onpremise.server.general;

import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.logging.Level;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.general.InstallationTrackingAPI;

public class InstallationTrackingImpl implements InstallationTrackingAPI
{
    private static final Logger LOGGER;
    
    public void writeServerInfoProps(final Properties props) {
        try {
            final String confFileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "server_info.props";
            final String logsFileName = SyMUtil.getInstallationDir() + File.separator + "logs" + File.separator + "server_info.props";
            InstallationTrackingImpl.LOGGER.log(Level.FINEST, "Product conf file Name:{0}", confFileName);
            FileAccessUtil.storeProperties(props, confFileName, true);
            FileAccessUtil.storeProperties(props, logsFileName, true);
            InstallationTrackingImpl.LOGGER.log(Level.INFO, "Have written the Properties{0}in{1}", new Object[] { props, confFileName });
        }
        catch (final Exception ex) {
            InstallationTrackingImpl.LOGGER.log(Level.WARNING, "Exception while writing props in server_info.props files");
        }
    }
    
    public void writeInstallProps(final Properties props) {
        com.me.devicemanagement.onpremise.server.util.SyMUtil.writeInstallProps(props);
    }
    
    static {
        LOGGER = Logger.getLogger(InstallationTrackingImpl.class.getName());
    }
}
