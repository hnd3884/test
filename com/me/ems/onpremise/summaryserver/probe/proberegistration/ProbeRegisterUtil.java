package com.me.ems.onpremise.summaryserver.probe.proberegistration;

import java.util.Hashtable;
import java.util.Arrays;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import org.json.JSONObject;
import com.me.ems.onpremise.summaryserver.startup.util.NSStartUpUtil;
import java.util.Map;
import com.me.ems.onpremise.summaryserver.probe.util.ProbeInstallUtil;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProbeRegisterUtil
{
    public static Logger logger;
    public static int installService;
    public static int removeService;
    
    public static void main(final String[] args) {
        try {
            ProbeRegisterUtil.logger.log(Level.INFO, "PARAMS LENGTH " + args.length);
            final Properties proxyProps = new Properties();
            if (args.length != 3 && args.length != 7) {
                ProbeRegisterUtil.logger.log(Level.SEVERE, "All Params not passed/ extra params found");
                System.exit(1);
            }
            final String port = args[0];
            final String host = args[1];
            final String installKey = args[2];
            if (args.length == 7) {
                final String proxyHost = args[4];
                final String proxyPort = args[3];
                final String proxyUser = args[5];
                final String proxyPass = args[6];
                ProbeRegisterUtil.logger.log(Level.INFO, "proxy host" + args[4]);
                ProbeRegisterUtil.logger.log(Level.INFO, "proxy port" + args[3]);
                ProbeRegisterUtil.logger.log(Level.INFO, "proxy user" + args[5]);
                ProbeRegisterUtil.logger.log(Level.INFO, "proxy pass" + args[6]);
                ((Hashtable<String, String>)proxyProps).put("proxyHost", proxyHost);
                ((Hashtable<String, String>)proxyProps).put("proxyPort", proxyPort);
                ((Hashtable<String, String>)proxyProps).put("proxyUser", proxyUser);
                ((Hashtable<String, String>)proxyProps).put("proxyPass", proxyPass);
            }
            final HashMap summaryDetail = new HashMap();
            summaryDetail.put("serverName", host);
            summaryDetail.put("portNumber", port);
            summaryDetail.put("protocol", "https");
            final HashMap probeDetail = new HashMap();
            probeDetail.put("installationKey", installKey);
            probeDetail.put("port", getProbePort());
            probeDetail.put("protocol", "https");
            final JSONObject response = ProbeInstallUtil.installProbe(probeDetail, summaryDetail, proxyProps, false);
            if (response.has("errorMsg")) {
                final String errorMsg = (String)response.get("errorMsg");
                ProbeRegisterUtil.logger.log(Level.INFO, "error message->" + errorMsg);
                if (errorMsg.contains("Connect Exception")) {
                    System.exit(300001);
                }
                else if (errorMsg.contains("Installation Key is not Valid")) {
                    System.exit(300002);
                }
                else if (errorMsg.contains("Key already used.Please add new Probe")) {
                    System.exit(300003);
                }
                else if (errorMsg.contains("Proxy Exception")) {
                    System.exit(300006);
                }
                else if (errorMsg.contains("Unable to Update Installation Details") || errorMsg.contains("Exception")) {
                    System.exit(300004);
                }
                else {
                    System.exit(300004);
                }
            }
            else {
                ProbeRegisterUtil.logger.log(Level.INFO, " Probe Registered in SummaryServer");
                createProbeLockFile();
                NSStartUpUtil.reinstallNSServer();
                System.exit(100000);
            }
            ProbeRegisterUtil.logger.log(Level.INFO, " all fine here");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void createProbeLockFile() {
        try {
            final File probeLockFile = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "probe.lock");
            if (!probeLockFile.exists()) {
                probeLockFile.createNewFile();
            }
        }
        catch (final Exception e) {
            ProbeRegisterUtil.logger.log(Level.SEVERE, "Error while creating Probe Lock File  " + e);
        }
    }
    
    private static int getProbePort() {
        int port = 0;
        try {
            final String webSettingsConf = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "websettings.conf";
            final Properties webSettings = FileAccessUtil.readProperties(webSettingsConf);
            final String probePort = webSettings.getProperty("https.port");
            port = Integer.parseInt(probePort);
        }
        catch (final Exception e) {
            ProbeRegisterUtil.logger.severe("Error while getting probe port due to " + e);
        }
        return port;
    }
    
    private static void changeCustomerWrapperProps() {
        try {
            final String customWrapperConf = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "custom_wrapperservice.conf";
            final Properties customWrapperConfProps = FileAccessUtil.readProperties(customWrapperConf);
            customWrapperConfProps.setProperty("wrapper.name", "probe_server_service");
            customWrapperConfProps.setProperty("wrapper.displayname", "ManageEngine UEMS - Probe Server");
            customWrapperConfProps.setProperty("wrapper.description", "ManageEngine UEMS - Probe Server");
            FileAccessUtil.storeProperties(customWrapperConfProps, customWrapperConf, false);
        }
        catch (final Exception e) {
            ProbeRegisterUtil.logger.severe("Error while setting probe props on custom_wrapperservice  " + e);
        }
    }
    
    private static void writeToXML(final File file, final Element root) throws Exception {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        final String encoding = "UTF-8";
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        final DOMSource source = new DOMSource(root);
        final StreamResult result = new StreamResult(writer);
        try {
            final Properties prop = new Properties();
            ((Hashtable<String, String>)prop).put("indent", "yes");
            ((Hashtable<String, String>)prop).put("encoding", encoding);
            ((Hashtable<String, String>)prop).put("method", "xml");
            transformer.setOutputProperties(prop);
            transformer.transform(source, result);
        }
        catch (final Exception e) {
            ProbeRegisterUtil.logger.severe("Error while writing xml  " + e);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    private static void callWrapperService(final int action) {
        try {
            String command = "";
            if (action == ProbeRegisterUtil.installService) {
                command = "-i";
            }
            else if (action == ProbeRegisterUtil.removeService) {
                command = "-r";
            }
            ProbeRegisterUtil.logger.log(Level.INFO, "calling product service " + command);
            final ProcessBuilder builder = new ProcessBuilder(Arrays.asList("cmd.exe", "/C", "DCService.bat", command));
            builder.directory(new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "bin" + File.separator));
            final Process process = builder.start();
            process.waitFor();
            ProbeRegisterUtil.logger.log(Level.INFO, "calling product service ended for " + action);
        }
        catch (final Exception e) {
            ProbeRegisterUtil.logger.log(Level.SEVERE, "Exception calling product service ", e);
        }
    }
    
    static {
        ProbeRegisterUtil.logger = Logger.getLogger("probeActionsLogger");
        ProbeRegisterUtil.installService = 0;
        ProbeRegisterUtil.removeService = 1;
    }
}
