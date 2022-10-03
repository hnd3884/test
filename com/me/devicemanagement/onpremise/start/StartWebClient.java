package com.me.devicemanagement.onpremise.start;

import java.util.Hashtable;
import com.me.devicemanagement.onpremise.start.util.InstallUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class StartWebClient implements InvokeClass
{
    private static final Logger LOGGER;
    
    @Override
    public void executeProgram(final Properties additionalParams, final String[] args) {
        final String confFileName = ((Hashtable<K, String>)additionalParams).get("client_startup_settings_filename");
        if (confFileName != null) {
            try {
                final Properties props = StartupUtil.getProperties(confFileName);
                final String launchClient = props.getProperty("LAUNCH_BROWSER_CLIENT");
                if (launchClient != null && launchClient.equalsIgnoreCase("false")) {
                    StartWebClient.LOGGER.log(Level.INFO, "LAUNCH_BROWSER_CLIENT is set to false.");
                    return;
                }
            }
            catch (final Exception ex) {
                StartWebClient.LOGGER.log(Level.SEVERE, "Caught exception while retrieving client parameter LAUNCH_BROWSER_CLIENT." + ex);
            }
        }
        String command = ((Hashtable<K, String>)additionalParams).get("command");
        final int webPort = InstallUtil.getWebServerPort();
        command += new Integer(webPort).toString();
        try {
            final ProcessBuilder builder = new ProcessBuilder(new String[] { command });
            builder.start();
        }
        catch (final Exception e) {
            throw new RuntimeException("Error occured while trying to start ServiceDesk Client", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(StartWebClient.class.getName());
    }
}
