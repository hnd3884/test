package com.me.devicemanagement.onpremise.server.util;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.util.logging.Level;
import org.w3c.dom.Element;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.File;
import org.apache.commons.lang.math.NumberUtils;
import java.util.HashMap;
import java.util.logging.Logger;

public class ServerProfileUtil
{
    private static final String JREMAX = "JreMax";
    private static final String JREMIN = "JreMin";
    private static final String APACHETHREADS = "ApacheThreads";
    private static final String POSTGRESMEMORY = "PostgresMemory";
    private static final String DBCONNECTIONCOUNT = "MaxConnections";
    public static final String TOMCATMAXTHREADS = "maxThread";
    public static final String TOMCATMAXCONNECTIONS = "maxConnections";
    public static final String TOMCATACCEPTCOUNT = "acceptCount";
    static final String CHATBOTJREMAX = "chatBoxJreMax";
    private static final String REDIS_MAX_MEMORY = "redisMaxMemory";
    private static final String REDIS_ENABLE = "redisEnabled";
    public static final String SEVENZIP_CORE = "sevenZipCore";
    private static Logger logger;
    private static String serverProfilesFilePath;
    private static int cpuPhysicalCoreCount;
    private static int eligibleProfile;
    
    public static HashMap<String, Integer> getProfileParameters() throws Exception {
        final String profileNumberString = SyMUtil.getServerParameter("serverProfileSet");
        int profileNumber = 0;
        if (NumberUtils.isNumber(profileNumberString)) {
            profileNumber = Integer.parseInt(profileNumberString);
        }
        else {
            profileNumber = eligibleProfileBasedOnHardware();
        }
        return getProfileParameters(profileNumber);
    }
    
    public static HashMap<String, Integer> getProfileParameter() throws Exception {
        final int profileNumber = eligibleProfileBasedOnHardware();
        return getProfileParameters(profileNumber);
    }
    
    public static HashMap<String, Integer> getProfileParameters(final int profileNumber) throws Exception {
        try {
            final HashMap<String, Integer> profileParameters = new HashMap<String, Integer>();
            final File fXmlFile = new File(System.getProperty("server.home") + ServerProfileUtil.serverProfilesFilePath);
            final DocumentBuilder dBuilder = XMLUtils.getDocumentBuilderInstance();
            final Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            final NodeList nList = doc.getElementsByTagName("profile");
            final Node nNode = nList.item(profileNumber);
            final Element eElement = (Element)nNode;
            final Element jvm = getChild(eElement, "jvm");
            final Element apache = getChild(eElement, "apache");
            final Element postgres = getChild(eElement, "postgres");
            final Element tomcat = getChild(eElement, "tomcat");
            final Element chatbot = getChild(eElement, "chatbot");
            final Element redis = getChild(eElement, "redis");
            final Element sevenZip = getChild(eElement, "sevenZip");
            profileParameters.put("JreMax", Integer.parseInt(jvm.getAttribute("JreMax")));
            profileParameters.put("JreMin", Integer.parseInt(jvm.getAttribute("JreMin")));
            profileParameters.put("ApacheThreads", Integer.parseInt(apache.getAttribute("ApacheThreads")));
            profileParameters.put("PostgresMemory", Integer.parseInt(postgres.getAttribute("PostgresMemory")));
            profileParameters.put("MaxConnections", Integer.parseInt(postgres.getAttribute("MaxConnections")));
            profileParameters.put("maxThread", Integer.parseInt(tomcat.getAttribute("maxThread")));
            profileParameters.put("maxConnections", Integer.parseInt(tomcat.getAttribute("maxConnections")));
            profileParameters.put("acceptCount", Integer.parseInt(tomcat.getAttribute("acceptCount")));
            profileParameters.put("chatBoxJreMax", Integer.parseInt(chatbot.getAttribute("chatBoxJreMax")));
            profileParameters.put("redisMaxMemory", Integer.parseInt(redis.getAttribute("redisMaxMemory")));
            profileParameters.put("redisEnabled", Integer.parseInt(redis.getAttribute("redisEnabled")));
            profileParameters.put("sevenZipCore", Integer.parseInt(sevenZip.getAttribute("sevenZipCore")));
            return profileParameters;
        }
        catch (final Exception e) {
            ServerProfileUtil.logger.log(Level.SEVERE, "Error while getProfileParameters ", e);
            throw e;
        }
    }
    
    private static Element getChild(final Element parent, final String name) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof Element && name.equals(child.getNodeName())) {
                return (Element)child;
            }
        }
        return null;
    }
    
    public static int eligibleProfileBasedOnHardware() {
        if (ServerProfileUtil.eligibleProfile != -1) {
            return ServerProfileUtil.eligibleProfile;
        }
        try {
            final int computerCores = cpuPhysicalCoreCount();
            final long sysRAMMemory = ((OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
            if (sysRAMMemory > 0L && computerCores != 0) {
                final Float sysRAMMemValue = (Float)sysRAMMemory;
                Float computerRam = sysRAMMemValue / 1.07374182E9f;
                final File serverProfilesFile = new File(System.getProperty("server.home") + ServerProfileUtil.serverProfilesFilePath);
                final DocumentBuilder dBuilder = XMLUtils.getDocumentBuilderInstance();
                final Document doc = dBuilder.parse(serverProfilesFile);
                doc.getDocumentElement().normalize();
                final NodeList profilesList = doc.getElementsByTagName("profile");
                for (int profileNumber = profilesList.getLength() - 1; profileNumber >= 1; --profileNumber) {
                    final Node profileNode = profilesList.item(profileNumber);
                    if (profileNode.getNodeType() == 1) {
                        final Element eElement = (Element)profileNode;
                        final Element criteria = getChild(eElement, "criteria");
                        final int reqRam = Integer.parseInt(criteria.getAttribute("ReqRam"));
                        final int reqCores = Integer.parseInt(criteria.getAttribute("ReqCores"));
                        computerRam = Float.valueOf(Math.round(computerRam));
                        ServerProfileUtil.logger.info(reqRam + " < " + computerRam + " && " + reqCores + " < " + computerCores);
                        if (reqRam <= computerRam && reqCores <= computerCores) {
                            ServerProfileUtil.logger.info(" returning profile no = " + profileNumber);
                            return ServerProfileUtil.eligibleProfile = profileNumber;
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            ServerProfileUtil.logger.log(Level.SEVERE, "Error while eligibleProfileBasedOnHardware ", e);
        }
        return ServerProfileUtil.eligibleProfile = 0;
    }
    
    private static int cpuPhysicalCoreCount() {
        if (ServerProfileUtil.cpuPhysicalCoreCount != -1) {
            return ServerProfileUtil.cpuPhysicalCoreCount;
        }
        BufferedReader in = null;
        ServerProfileUtil.cpuPhysicalCoreCount = 0;
        try {
            final String[] command = { "wmic", "cpu", "get", "NumberOfCores,NumberOfLogicalProcessors/Format:List" };
            final ProcessBuilder builder = new ProcessBuilder(command);
            final Process child = builder.start();
            child.getOutputStream().close();
            in = new BufferedReader(new InputStreamReader(child.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains("NumberOfCores")) {
                    ServerProfileUtil.cpuPhysicalCoreCount += Integer.parseInt(line.substring(14));
                }
            }
            if (ServerProfileUtil.cpuPhysicalCoreCount == 0) {
                ServerProfileUtil.logger.info("Unable to get data from wmic, using runtime instead.");
                ServerProfileUtil.cpuPhysicalCoreCount = Runtime.getRuntime().availableProcessors();
            }
            ServerProfileUtil.logger.info("Number of cores = " + ServerProfileUtil.cpuPhysicalCoreCount);
        }
        catch (final Exception e) {
            ServerProfileUtil.logger.log(Level.SEVERE, "Exception getting CPU count ", e);
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e2) {
                ServerProfileUtil.logger.log(Level.SEVERE, "Exception finally block ", e2);
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (final IOException e3) {
                ServerProfileUtil.logger.log(Level.SEVERE, "Exception finally block ", e3);
            }
        }
        return ServerProfileUtil.cpuPhysicalCoreCount;
    }
    
    static {
        ServerProfileUtil.logger = Logger.getLogger(ServerProfileUtil.class.getName());
        ServerProfileUtil.serverProfilesFilePath = File.separator + "conf" + File.separator + "serverProfiles.xml";
        ServerProfileUtil.cpuPhysicalCoreCount = -1;
        ServerProfileUtil.eligibleProfile = -1;
    }
}
