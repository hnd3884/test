package com.adventnet.management.log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Random;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.Enumeration;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Properties;

public class LogMgr
{
    protected static final String LOG_STARTED_STR = "Logging started";
    public static LogUser TOPOUSER;
    public static LogUser EVENTUSER;
    public static LogUser ALERTUSER;
    public static LogUser POLLUSER;
    public static LogUser MAPUSER;
    public static LogUser POLICYUSER;
    public static LogUser MISCUSER;
    public static LogUser ALERT_AUDITUSER;
    public static LogUser DISCOVERYUSER;
    public static LogUser TOPOERR;
    public static LogUser EVENTERR;
    public static LogUser ALERTERR;
    public static LogUser POLLERR;
    public static LogUser MAPERR;
    public static LogUser POLICYERR;
    public static LogUser MISCERR;
    private static String SystemInfo;
    private static Properties logUserList;
    private static Hashtable logFileWriterTable;
    private static String homeDir;
    private static String logFileName;
    private static Vector logDirVector;
    
    public static void appendHeaderInfo(final String s) {
        LogMgr.SystemInfo += s;
    }
    
    public static String getHeaderInfo() {
        return LogMgr.SystemInfo;
    }
    
    public static void init(final String homeDir, final String logFileName) throws ParserConfigurationException, IOException, SAXException {
        LogMgr.homeDir = homeDir;
        LogMgr.logFileName = logFileName;
        createDefaultLogUsers(LoggingXMLReader.getInstance(openFile(new File(LogMgr.homeDir + LogMgr.logFileName))).getLogUserProperties());
        LogMgr.TOPOUSER = assignCorrespondingLogUser(LogMgr.TOPOUSER, "TOPOUSER");
        LogMgr.TOPOERR = assignCorrespondingLogUser(LogMgr.TOPOERR, "TOPOERR");
        LogMgr.MAPUSER = assignCorrespondingLogUser(LogMgr.MAPUSER, "MAPUSER");
        LogMgr.MAPERR = assignCorrespondingLogUser(LogMgr.MAPERR, "MAPERR");
        LogMgr.POLLUSER = assignCorrespondingLogUser(LogMgr.POLLUSER, "POLLUSER");
        LogMgr.POLLERR = assignCorrespondingLogUser(LogMgr.POLLERR, "POLLERR");
        LogMgr.EVENTUSER = assignCorrespondingLogUser(LogMgr.EVENTUSER, "EVENTUSER");
        LogMgr.EVENTERR = assignCorrespondingLogUser(LogMgr.EVENTERR, "EVENTERR");
        LogMgr.ALERTUSER = assignCorrespondingLogUser(LogMgr.ALERTUSER, "ALERTUSER");
        LogMgr.ALERTERR = assignCorrespondingLogUser(LogMgr.ALERTERR, "ALERTERR");
        LogMgr.POLICYUSER = assignCorrespondingLogUser(LogMgr.POLICYUSER, "POLICYUSER");
        LogMgr.POLICYERR = assignCorrespondingLogUser(LogMgr.POLICYERR, "POLICYERR");
        LogMgr.MISCUSER = assignCorrespondingLogUser(LogMgr.MISCUSER, "MISCUSER");
        LogMgr.MISCERR = assignCorrespondingLogUser(LogMgr.MISCERR, "MISCERR");
        LogMgr.ALERT_AUDITUSER = assignCorrespondingLogUser(LogMgr.ALERT_AUDITUSER, "ALERT_AUDITUSER");
        LogMgr.DISCOVERYUSER = assignCorrespondingLogUser(LogMgr.DISCOVERYUSER, "DISCOVERYUSER");
    }
    
    public static void stop() {
        final Enumeration elements = LogMgr.logFileWriterTable.elements();
        while (elements.hasMoreElements()) {
            ((LogBaseWriter)elements.nextElement()).flush();
        }
    }
    
    private static LogUser assignCorrespondingLogUser(LogUser logUser, final String s) {
        final LogUser logUser2 = ((Hashtable<K, LogUser>)LogMgr.logUserList).get(s);
        if (logUser2 != null) {
            logUser = logUser2;
        }
        return logUser;
    }
    
    private static void createDefaultLogUsers(final Properties properties) {
        final Enumeration<Object> keys = ((Hashtable<Object, V>)properties).keys();
        while (keys.hasMoreElements()) {
            final String s = keys.nextElement();
            final Properties properties2 = ((Hashtable<K, Properties>)properties).get(s);
            getModuleKeys(s, properties2);
            createLogUser(properties2, false);
        }
    }
    
    public static Vector getRelativeLogDirectories() {
        return (Vector)LogMgr.logDirVector.clone();
    }
    
    private static String getDeFaultProperty(final Properties properties, final String s, final String s2) {
        final String property = properties.getProperty(s);
        if (property == null) {
            return s2;
        }
        final String trim = property.trim();
        if (trim.length() <= 0) {
            return s2;
        }
        return trim;
    }
    
    public static boolean createLogUser(final Properties properties, final boolean b) {
        final String deFaultProperty = getDeFaultProperty(properties, "LogsDirectory", "logs");
        String s = properties.getProperty("FileName");
        if (s == null) {
            s = getRandomFileName();
            properties.setProperty("FileName", s);
        }
        if (LogMgr.logFileWriterTable.get(s) != null) {
            System.err.println("Failed to create a log module as a log module already with the name " + s);
            return false;
        }
        String property = properties.getProperty("LogFileWriterClassName");
        if (property == null) {
            property = "com.adventnet.management.log.LogFileWriter";
        }
        properties.setProperty("LogsDirectory", LogMgr.homeDir + deFaultProperty);
        LogBaseWriter logBaseWriter;
        try {
            logBaseWriter = (LogBaseWriter)Class.forName(property).newInstance();
        }
        catch (final Exception ex) {
            System.err.println("Error whiile instantiating class " + property);
            ex.printStackTrace();
            System.err.println("Defaulting to com.adventnet.management.log.LogFileWriter");
            try {
                logBaseWriter = (LogBaseWriter)Class.forName("com.adventnet.management.log.LogFileWriter").newInstance();
            }
            catch (final Exception ex2) {
                return false;
            }
        }
        logBaseWriter.init(properties);
        if (!LogMgr.logDirVector.contains(deFaultProperty)) {
            LogMgr.logDirVector.addElement(deFaultProperty);
        }
        logBaseWriter.log(" ~~~~~~~~~~~~~~~ Logging started ~~~~~~~~~~~~~~~");
        logBaseWriter.log("Messages on ********" + DateFormat.getDateInstance(0).format(Calendar.getInstance().getTime()) + "********");
        logBaseWriter.log(LogMgr.SystemInfo);
        if (logBaseWriter instanceof LogFileWriter) {
            ((LogFileWriter)logBaseWriter).setHeader(LogMgr.SystemInfo);
        }
        LogMgr.logFileWriterTable.put(s, logBaseWriter);
        final String[] array = ((Hashtable<K, String[]>)properties).get("DisplayName");
        final String[] array2 = ((Hashtable<K, String[]>)properties).get("Name");
        final Integer[] array3 = ((Hashtable<K, Integer[]>)properties).get("LogLevel");
        final Boolean[] array4 = ((Hashtable<K, Boolean[]>)properties).get("Logging");
        for (int i = 0; i < array2.length; ++i) {
            final LogUser logUser = new LogUser(array[i], array3[i], logBaseWriter);
            logUser.setStatus(array4[i]);
            if (LogMgr.logUserList.get(array2[i]) == null) {
                ((Hashtable<String, LogUser>)LogMgr.logUserList).put(array2[i], logUser);
            }
            else {
                System.err.println("LogUser already exists with the key " + array2[i]);
            }
        }
        properties.setProperty("LogsDirectory", deFaultProperty);
        try {
            if (b) {
                LogXmlWriter.write(properties, LogMgr.homeDir + LogMgr.logFileName);
            }
        }
        catch (final Exception ex3) {
            System.out.println(ex3);
        }
        return true;
    }
    
    public static String getRandomFileName() {
        final Random random = new Random();
        final StringBuffer sb = new StringBuffer("random_");
        for (int i = 0; i < 3; ++i) {
            sb.append((char)(97 + random.nextInt(26)));
        }
        sb.append(".txt");
        return sb.toString();
    }
    
    public static LogBaseWriter getLogBaseWriter(final String s) {
        return LogMgr.logFileWriterTable.get(s);
    }
    
    private static void getModuleKeys(final String s, final Properties properties) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s);
        final int n = stringTokenizer.countTokens() / 4;
        final String[] array = new String[n];
        final String[] array2 = new String[n];
        final Boolean[] array3 = new Boolean[n];
        final Integer[] array4 = new Integer[n];
        for (int i = 0; i < n; ++i) {
            array[i] = stringTokenizer.nextToken();
            array2[i] = stringTokenizer.nextToken();
            array4[i] = new Integer(stringTokenizer.nextToken());
            array3[i] = new Boolean(stringTokenizer.nextToken());
        }
        ((Hashtable<String, String[]>)properties).put("Name", array);
        ((Hashtable<String, String[]>)properties).put("DisplayName", array2);
        ((Hashtable<String, Integer[]>)properties).put("LogLevel", array4);
        ((Hashtable<String, Boolean[]>)properties).put("Logging", array3);
    }
    
    public static LogUser getLogUser(final String s) {
        return ((Hashtable<K, LogUser>)LogMgr.logUserList).get(s);
    }
    
    private static InputStream openFile(final File file) throws IOException {
        InputStream resourceAsStream;
        if (System.getProperty("JavaWebStart") != null) {
            System.out.println("Java Web Start mode in Logging: " + file);
            resourceAsStream = LogMgr.class.getClassLoader().getResourceAsStream(file.getName());
        }
        else {
            resourceAsStream = new FileInputStream(file);
        }
        return resourceAsStream;
    }
    
    static {
        LogMgr.TOPOUSER = new DefaultLogUser(null, 3, null);
        LogMgr.EVENTUSER = new DefaultLogUser(null, 3, null);
        LogMgr.ALERTUSER = new DefaultLogUser(null, 3, null);
        LogMgr.POLLUSER = new DefaultLogUser(null, 3, null);
        LogMgr.MAPUSER = new DefaultLogUser(null, 3, null);
        LogMgr.POLICYUSER = new DefaultLogUser(null, 3, null);
        LogMgr.MISCUSER = new DefaultLogUser(null, 3, null);
        LogMgr.ALERT_AUDITUSER = new DefaultLogUser(null, 3, null);
        LogMgr.DISCOVERYUSER = new DefaultLogUser(null, 3, null);
        LogMgr.TOPOERR = new DefaultLogUser(null, 3, null);
        LogMgr.EVENTERR = new DefaultLogUser(null, 3, null);
        LogMgr.ALERTERR = new DefaultLogUser(null, 3, null);
        LogMgr.POLLERR = new DefaultLogUser(null, 3, null);
        LogMgr.MAPERR = new DefaultLogUser(null, 3, null);
        LogMgr.POLICYERR = new DefaultLogUser(null, 3, null);
        LogMgr.MISCERR = new DefaultLogUser(null, 3, null);
        LogMgr.SystemInfo = "";
        LogMgr.logUserList = new Properties();
        LogMgr.logFileWriterTable = new Hashtable();
        LogMgr.homeDir = "./";
        LogMgr.logFileName = null;
        LogMgr.logDirVector = new Vector();
    }
}
