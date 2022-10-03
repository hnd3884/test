package com.me.devicemanagement.onpremise.server.startupstatus;

import java.util.Properties;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.Messenger;
import com.me.devicemanagement.onpremise.start.DCStarter;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.io.File;
import java.util.logging.Logger;
import com.adventnet.mfw.message.MessageListener;
import com.me.devicemanagement.onpremise.start.startupstatus.StartupStatusNotifierApi;

public class StartupStatusNotifierImpl implements StartupStatusNotifierApi, MessageListener
{
    public static Logger logger;
    private static boolean notifierRunningStatus;
    private static int startupProgressPercentage;
    private static String productName;
    public String webclientPath;
    public String webclientJSPath;
    
    private StartupStatusNotifierImpl() {
        this.webclientPath = System.getProperty("server.home") + File.separator + "ServerStatusNotifier" + File.separator + "webclient.html";
        this.webclientJSPath = System.getProperty("server.home") + File.separator + "ServerStatusNotifier" + File.separator + "StartupStatusWebclient.js";
        try {
            StartupStatusNotifierImpl.productName = ProductUrlLoader.getInstance().getValue("displayname");
        }
        catch (final Exception e) {
            StartupStatusNotifierImpl.logger.log(Level.WARNING, "Unable to create the StartupStatusNotifier instance.", e);
        }
    }
    
    public static StartupStatusNotifierImpl getInstance() {
        if (DCStarter.getStartupStatusNotifier() == null) {
            DCStarter.setStartupStatusNotifier((StartupStatusNotifierApi)new StartupStatusNotifierImpl());
        }
        return (StartupStatusNotifierImpl)DCStarter.getStartupStatusNotifier();
    }
    
    public void subscribeStartupStatusNotification() {
        try {
            Messenger.subscribe("startupNotification", (MessageListener)getInstance(), true, (MessageFilter)null);
            Messenger.subscribe("splashNotification", (MessageListener)getInstance(), true, (MessageFilter)null);
            StartupStatusNotifierImpl.notifierRunningStatus = true;
            StartupStatusNotifierImpl.logger.log(Level.INFO, "Going to write default status in webclient file.");
            this.showStatus("Going to start Server", 0);
            StartupStatusNotifierImpl.logger.log(Level.INFO, "Going to launch console form StartupStatusNotifierImpl !!!");
            final String winUtilExe = System.getProperty("server.home") + File.separator + "bin" + File.separator + "dcwinutil.exe";
            WebServerUtil.executeCommand(new String[] { winUtilExe, "-invokeBrowser", "-System", new File(this.webclientPath).getCanonicalPath() });
        }
        catch (final Exception e) {
            StartupStatusNotifierImpl.logger.log(Level.WARNING, "Exception occurred while subscribing the splash notification ...", e);
        }
    }
    
    public boolean isStatusNotifierRunning() {
        return StartupStatusNotifierImpl.notifierRunningStatus;
    }
    
    public void removeStatusNotifier(final String forwardURL) {
        try {
            StartupStatusNotifierImpl.logger.log(Level.INFO, "Entering to removeStatusNotifier() method.");
            DCStarter.setStartupStatusNotifier((StartupStatusNotifierApi)null);
            StartupStatusNotifierImpl.notifierRunningStatus = false;
            final String webconsoleUrl = (forwardURL == null) ? (WebServerUtil.getServerProtocol() + "://" + WebServerUtil.getMachineName() + ":" + WebServerUtil.getWebServerPort() + "/") : forwardURL;
            final String forwardTOProductConsoleElement = "window.location.href = \"" + webconsoleUrl + "\";";
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(forwardTOProductConsoleElement);
            this.writeWebclientJSContent(stringBuilder);
        }
        catch (final Exception e) {
            StartupStatusNotifierImpl.logger.log(Level.SEVERE, "Exception occurred while writing webclient startup status content into file.", e);
        }
    }
    
    private void showStatus(final String statusString, final int percentage) {
        try {
            StartupStatusNotifierImpl.logger.log(Level.INFO, "statusString - " + statusString + " | currentProgressPercentage - " + percentage);
            final String productCode = EMSProductUtil.getEMSProductCode().get(0).toString().toLowerCase();
            final String defaultContent = "productName = \"" + StartupStatusNotifierImpl.productName + "\";\n" + "port = " + WebServerUtil.getWebServerPort() + ";\n" + "hostName =\"" + WebServerUtil.getMachineName() + "\";\n" + "protocol =\"" + WebServerUtil.getServerProtocol() + "\";\n" + "prodLogoPath = \"" + productCode + "_white_logo.png\";\n";
            final String startupStatusTxtElement = "document.getElementById(\"startupStatusTxt\").innerText =\"" + statusString + "\";\n";
            final String startupStatusProgressBarElement = "document.getElementById(\"startupStatusProgressBar\").style.width =\"" + percentage + "%\";\n";
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(defaultContent);
            stringBuilder.append(startupStatusTxtElement);
            stringBuilder.append(startupStatusProgressBarElement);
            this.writeWebclientJSContent(stringBuilder);
        }
        catch (final Exception e) {
            StartupStatusNotifierImpl.logger.log(Level.SEVERE, "Exception occurred while writing webclient startup status content into file.", e);
        }
    }
    
    private void writeWebclientJSContent(final StringBuilder fileContent) {
        try {
            Files.deleteIfExists(Paths.get(this.webclientJSPath, new String[0]));
            Files.createFile(Paths.get(this.webclientJSPath, new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
            Files.write(Paths.get(this.webclientJSPath, new String[0]), fileContent.toString().getBytes("UTF-8"), StandardOpenOption.WRITE);
            StartupStatusNotifierImpl.logger.log(Level.INFO, "Content write completed successfully into file.");
        }
        catch (final Exception e) {
            StartupStatusNotifierImpl.logger.log(Level.INFO, "Exception occurred while writing webclient content into file.", e);
        }
    }
    
    public void onMessage(final Object message) {
        if (this.isStatusNotifierRunning()) {
            final Properties props = (Properties)message;
            if (props.containsKey("TopicName") && props.getProperty("TopicName").equals("startupNotification")) {
                StartupStatusNotifierImpl.notifierRunningStatus = false;
                this.removeStatusNotifier(null);
            }
            else {
                final int currentProgressPercentage = props.containsKey("progress") ? Integer.valueOf(props.getProperty("progress")) : StartupStatusNotifierImpl.startupProgressPercentage;
                this.showStatus(props.getProperty("message"), currentProgressPercentage);
                StartupStatusNotifierImpl.startupProgressPercentage = currentProgressPercentage;
            }
        }
    }
    
    static {
        StartupStatusNotifierImpl.logger = Logger.getLogger("DCServiceLogger");
        StartupStatusNotifierImpl.notifierRunningStatus = false;
        StartupStatusNotifierImpl.startupProgressPercentage = 0;
        StartupStatusNotifierImpl.productName = "";
    }
}
