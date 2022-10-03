package com.me.devicemanagement.onpremise.server.general;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.Properties;
import com.me.devicemanagement.onpremise.start.util.DCLogUtil;
import java.util.logging.Logger;

public class DCOSArchitecture
{
    public static final String OS_ARCHITECTURE = "os.arch.bit";
    public static final String SERVER_ARCHITECTURE = "server.arch.bit";
    public static final String SERVER_OS_ARCHITECTURE_FILE = "serverOsArch.props";
    private static final String SERVER_OS_ARCHITECTURE_FILEPATH;
    private static Logger logger;
    
    public static void main(final String[] args) {
        DCLogUtil.initLogger();
        try {
            Properties serverOsArchProp = new Properties();
            final String osArchitecture = SyMUtil.getDCOSArchitecture();
            final String serverDCArchitecture = StartupUtil.dcProductArch();
            final String osArchFile = System.getProperty("server.home") + File.separator + DCOSArchitecture.SERVER_OS_ARCHITECTURE_FILEPATH;
            if (new File(osArchFile).exists()) {
                serverOsArchProp = FileAccessUtil.readProperties(osArchFile);
                final String osArchitectureFromFile = serverOsArchProp.getProperty("os.arch.bit");
                final String serverDCArchitectureFromFile = serverOsArchProp.getProperty("server.arch.bit");
                DCOSArchitecture.logger.log(Level.INFO, "From the properties file [serverOsArch.conf]:- OS Architecture : {0} and Server Architecture : {1} ", new Object[] { osArchitectureFromFile, serverDCArchitectureFromFile });
                if (osArchitectureFromFile != null && serverDCArchitectureFromFile != null && osArchitectureFromFile.equals(osArchitecture) && serverDCArchitectureFromFile.equals(serverDCArchitecture)) {
                    DCOSArchitecture.logger.log(Level.INFO, "OS Architecture : {0} and Server Architecture : {1} matches the properties present in the file serverOsArch.conf", new Object[] { osArchitectureFromFile, serverDCArchitectureFromFile });
                    setSystemProps(osArchitectureFromFile, serverDCArchitectureFromFile);
                    return;
                }
                DCOSArchitecture.logger.log(Level.INFO, "OS and Server Architecture doesn't match the properties present in the file serverOsArch.conf");
            }
            DCOSArchitecture.logger.log(Level.INFO, "Present OS Architecture : {0} and Server Architecture : {1} ", new Object[] { osArchitecture, serverDCArchitecture });
            serverOsArchProp.setProperty("os.arch.bit", osArchitecture);
            serverOsArchProp.setProperty("server.arch.bit", serverDCArchitecture);
            DCOSArchitecture.logger.log(Level.INFO, "OS and Server Architecture Properties : {0}", serverOsArchProp);
            setSystemProps(osArchitecture, serverDCArchitecture);
            FileAccessUtil.storeProperties(serverOsArchProp, osArchFile, false);
            DCOSArchitecture.logger.log(Level.INFO, "OS and Server Architecture stored in {0}", osArchFile);
        }
        catch (final Exception ex) {
            DCOSArchitecture.logger.log(Level.SEVERE, "Exception while setting Server OS Architecture", ex);
        }
    }
    
    private static void setSystemProps(final String osArch, final String serverDCArch) {
        System.setProperty("os.arch.bit", osArch);
        System.setProperty("server.arch.bit", serverDCArch);
    }
    
    static {
        SERVER_OS_ARCHITECTURE_FILEPATH = "conf" + File.separator + "serverOsArch.props";
        DCOSArchitecture.logger = Logger.getLogger(DCOSArchitecture.class.getName());
    }
}
