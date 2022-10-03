package com.adventnet.tools.update;

import java.util.Hashtable;
import java.util.Properties;
import java.io.PrintStream;
import com.adventnet.tools.update.installer.MessageConstants;
import java.applet.Applet;
import com.adventnet.tools.update.installer.Utility;
import java.util.Locale;
import java.nio.file.DirectoryStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.util.logging.Level;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.io.File;
import com.adventnet.tools.update.installer.BuilderResourceBundle;
import java.util.logging.Logger;

public class CommonUtil
{
    private static final Logger LOGGER;
    public static Object[] objArray;
    public static int optionLength;
    private static BuilderResourceBundle resourceBundle;
    
    public static int parseOption(final String option) {
        if (option == null) {
            return -1;
        }
        for (int i = 0; i < CommonUtil.optionLength; ++i) {
            if (option.equalsIgnoreCase((String)CommonUtil.objArray[i])) {
                return i;
            }
        }
        return -1;
    }
    
    public static String convertfilenameToOsFilename(final String filename) {
        String newFilename = null;
        final char thisOsFileSeperator = File.separatorChar;
        if (filename == null) {
            return null;
        }
        char checkForIndex;
        if (thisOsFileSeperator == '/') {
            checkForIndex = '\\';
        }
        else {
            checkForIndex = '/';
        }
        newFilename = filename.replace(checkForIndex, thisOsFileSeperator);
        return newFilename;
    }
    
    public static void createAllSubDirectories(final String oldFilepath) {
        final String filepath = convertfilenameToOsFilename(oldFilepath);
        final File t_filePath = new File(filepath);
        if (filepath.indexOf("/") != -1 || filepath.indexOf("\\") != -1) {
            if (!t_filePath.exists()) {
                String subDirectory;
                if (filepath.indexOf("/") != -1) {
                    subDirectory = filepath.substring(0, filepath.lastIndexOf("/"));
                }
                else {
                    subDirectory = filepath.substring(0, filepath.lastIndexOf("\\"));
                }
                createAllSubDirectories(subDirectory);
                final File temp = new File(subDirectory);
                temp.mkdir();
            }
        }
        else if (!t_filePath.exists()) {
            final File temp2 = new File(filepath);
            temp2.mkdir();
        }
    }
    
    public static void deleteFiles(final String dirName) {
        final File directoryToList = new File(dirName);
        try {
            if (Files.isDirectory(directoryToList.toPath(), new LinkOption[0])) {
                final List<Path> folderContentsPaths = getFolderContentsPaths(directoryToList);
                for (final Path p : folderContentsPaths) {
                    deleteFiles(p.toFile().getCanonicalPath());
                }
            }
            Files.deleteIfExists(directoryToList.toPath());
        }
        catch (final IOException ioe) {
            CommonUtil.LOGGER.log(Level.SEVERE, "Deleting \"" + directoryToList.getAbsolutePath() + "\" Failed.", ioe);
            throw new RuntimeException(ioe);
        }
    }
    
    private static List<Path> getFolderContentsPaths(final File directoryFile) throws IOException {
        final List<Path> subFilesPath = new ArrayList<Path>();
        final DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(directoryFile.toPath());
        try {
            for (final Path p : newDirectoryStream) {
                subFilesPath.add(p);
            }
        }
        finally {
            newDirectoryStream.close();
        }
        return subFilesPath;
    }
    
    public static void displayURL(String url, final int displayType) {
        if (displayType == 2) {
            url = "file://" + convertfilenameToOsFilename(url);
        }
        displayURL(url);
    }
    
    public static void displayURL(String url) {
        final String WIN_ID = "Windows";
        final String WIN_PATH = "rundll32";
        final String WIN_FLAG = "url.dll,FileProtocolHandler";
        final String UNIX_PATH = "netscape";
        final String UNIX_FLAG = "-remote openURL";
        final boolean windows = isWindowsPlatform();
        String cmd = null;
        try {
            if (windows) {
                if (url.startsWith("file")) {
                    url = url.replace('/', '\\');
                    final String curl = url = "file://" + url.substring(7);
                }
                cmd = WIN_PATH + " " + WIN_FLAG + " " + url;
                final Process p = Runtime.getRuntime().exec(cmd);
            }
            else {
                openURL(url);
            }
        }
        catch (final IOException x) {
            System.err.println("Could not invoke browser, command=" + cmd);
            System.err.println("Caught: " + x);
            final Thread th = new Thread(new BrowserInvoker(null, url));
            th.setPriority(3);
            th.start();
        }
    }
    
    public static boolean isWindowsPlatform() {
        final String osName = System.getProperty("os.name").trim();
        return osName.toLowerCase(Locale.ENGLISH).contains("windows");
    }
    
    public static void setResourceBundle(final String localePropertiesFileName, final String language, final String country) {
        CommonUtil.resourceBundle = Utility.getBundle(localePropertiesFileName, language + "_" + country, null);
    }
    
    public static BuilderResourceBundle getResourceBundle() {
        return CommonUtil.resourceBundle;
    }
    
    private static void initI18N() {
        final String language = System.getProperty("user.language");
        final String country = System.getProperty("user.region");
        final String localePropertiesFileName = "UpdateManagerResources";
        setResourceBundle(localePropertiesFileName, language, country);
    }
    
    public static void setResourceBundle(final BuilderResourceBundle bundle) {
        if (bundle != null) {
            CommonUtil.resourceBundle = bundle;
        }
        else {
            initI18N();
        }
    }
    
    public static String getString(final String key) {
        if (CommonUtil.resourceBundle == null) {
            return key;
        }
        return CommonUtil.resourceBundle.getString(key);
    }
    
    public static String getString(final String key, final String defaultValue) {
        if (key == null) {
            return null;
        }
        final String resourceValue = getString(key);
        if (resourceValue.equals(key)) {
            return defaultValue;
        }
        return resourceValue;
    }
    
    public static String getString(final MessageConstants messageConstants) {
        return getString(messageConstants.getKey(), messageConstants.getMessage());
    }
    
    public static void printOut(final String mess) {
        final String message = getString(mess);
        final PrintStream appoutStream = getApplicationStream("OUT");
        if (appoutStream != null) {
            appoutStream.println(message);
        }
        else {
            System.out.println(message);
        }
    }
    
    public static PrintStream getApplicationStream(final String type) {
        final Properties sysProps = System.getProperties();
        if (type.equals("ERROR")) {
            return ((Hashtable<K, PrintStream>)sysProps).get("syserr.stream");
        }
        if (type.equals("OUT")) {
            return ((Hashtable<K, PrintStream>)sysProps).get("sysout.stream");
        }
        return null;
    }
    
    public static void printError(final String mess) {
        final String message = getString(mess);
        final PrintStream apperrStream = getApplicationStream("ERROR");
        if (apperrStream != null) {
            apperrStream.println(message);
        }
        else {
            System.err.println(message);
        }
    }
    
    private static void openURL(final String url) {
        try {
            final String[] browsers = { "firefox", "google-chrome", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; ++count) {
                if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0) {
                    browser = browsers[count];
                }
                Runtime.getRuntime().exec(new String[] { browser, url });
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(CommonUtil.class.getName());
        CommonUtil.objArray = new Object[] { "EQ", "GT", "GE", "LT", "LE" };
        CommonUtil.optionLength = 5;
        CommonUtil.resourceBundle = null;
    }
    
    static class BrowserInvoker implements Runnable
    {
        Process p;
        String url;
        
        public BrowserInvoker(final Process pArg, final String urlArg) {
            this.p = pArg;
            this.url = urlArg;
        }
        
        @Override
        public void run() {
            int exitCode = -1;
            if (this.p != null) {
                try {
                    exitCode = this.p.waitFor();
                }
                catch (final Exception e) {
                    System.err.println(e);
                }
            }
        }
    }
}
