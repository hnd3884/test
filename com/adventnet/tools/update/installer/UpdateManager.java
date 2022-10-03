package com.adventnet.tools.update.installer;

import com.adventnet.tools.update.XmlData;
import javax.swing.JFrame;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import com.zoho.tools.util.NetworkUtil;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.io.PrintStream;
import java.util.logging.LogManager;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import com.adventnet.tools.update.FeaturePrdVersionInfo;
import com.adventnet.tools.update.FeatureCompInfo;
import java.util.Vector;
import com.adventnet.tools.update.UpdateData;
import com.adventnet.tools.update.XmlParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.awt.Font;
import com.adventnet.tools.update.FeatureVersionComp;
import java.util.ArrayList;
import java.util.StringTokenizer;
import com.zoho.tools.util.CryptoHelper;
import com.zoho.tools.util.ProxyProperties;
import com.zoho.tools.util.FileUtil;
import java.io.IOException;
import com.adventnet.tools.update.CommonUtil;
import java.util.logging.Level;
import com.zoho.tools.CertificateUtil;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;
import java.util.Hashtable;
import java.util.logging.Logger;

public class UpdateManager implements UpdateManagerInterface
{
    private static Logger out;
    private static String confProductName;
    private static String confProductVersion;
    private static String confSubProductName;
    private static String helpXmlFilePath;
    private static String helpHtmlFilePath;
    private String message;
    public static final int OK = 0;
    public static final int OKBUTNEEDSPATCH = 1;
    public static final int NEEDSPATCH = 2;
    public static final int UNKNOWN = 3;
    private static String updateFilePath;
    private static boolean commandLineBoolean;
    private static boolean fromMainBoolean;
    private static UpdateManagerUI ui;
    private static String language;
    private static String country;
    private static String localePropertiesFileName;
    private static ParameterObject po;
    private String homeDir;
    private String option;
    private String patchVersion;
    private String installDir;
    private static String patchPath;
    private String certPath;
    private boolean displayNameAsVersion;
    private boolean actVersion;
    private boolean dispVersion;
    private boolean cgui;
    private static boolean performPreValidation;
    private static boolean isInvokedForAutoApplyOfPatches;
    private static String validationClass;
    private static String alreadyCompletedPrePostClassName;
    private static UpdateState ustate;
    private static Hashtable<String, String> productStats;
    private static Properties generalProps;
    private static int alreadyAppliedPatchesCount;
    private static int successfullyAppliedPatchesCount;
    private static boolean rotateKeys;
    private static boolean isInvokedFromAutoUpgrade;
    private static String patchesCompatibilityVerifier_implementationClass;
    private static InstanceConfig instanceConfig;
    private final FilenameFilter ppmFileFilter;
    
    public static UpdateState getUpdateState() {
        return UpdateManager.ustate;
    }
    
    static void resetUpdateState() {
        UpdateManager.ustate = new UpdateState();
    }
    
    public static void setResourceBundle(final String bundle) {
        UpdateManager.localePropertiesFileName = bundle;
    }
    
    public static UpdateManagerUI getUi() {
        return UpdateManager.ui;
    }
    
    public static String getLanguage() {
        return UpdateManager.language.toLowerCase();
    }
    
    public static String getCountry() {
        return UpdateManager.country;
    }
    
    public UpdateManager(final String confPath, final String homePath) {
        this.message = "";
        this.homeDir = ".";
        this.option = null;
        this.patchVersion = null;
        this.installDir = null;
        this.certPath = null;
        this.displayNameAsVersion = true;
        this.actVersion = false;
        this.dispVersion = false;
        this.cgui = false;
        this.ppmFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".ppm");
            }
        };
        String homeDir = homePath;
        if (homeDir == null || homeDir.equals(".")) {
            homeDir = System.getProperty("user.dir");
        }
        UpdateManagerUtil.setHomeDirectory(homeDir);
        UpdateManager.commandLineBoolean = true;
        final String confFileLocation = UpdateManagerUtil.getHomeDirectory() + File.separator + confPath;
        readConfFile(confFileLocation);
    }
    
    @Override
    public void init() {
        final String confFileLocation = UpdateManagerUtil.getHomeDirectory() + File.separator + UpdateManager.updateFilePath;
        final boolean confBoolean = readConfFile(confFileLocation);
        if (!confBoolean) {
            return;
        }
        try {
            if (!CertificateUtil.isKeyStoreExists(UpdateManagerUtil.getHomeDirectory() + File.separator + "conf")) {
                autoImportCertificate(UpdateManagerUtil.getHomeDirectory());
            }
        }
        catch (final Exception e) {
            UpdateManager.out.log(Level.SEVERE, "Problem while importing certificate : " + e.getMessage(), e);
        }
        i18N();
        UpdateManager.ustate = new UpdateState();
        if (UpdateManager.commandLineBoolean) {
            UpdateManager.out.info(CommonUtil.getString("Invoking UpdateManager in CMD mode"));
            this.invokeUpdate();
        }
        else {
            UpdateManager.out.info(CommonUtil.getString("Invoking UpdateManager in UI mode"));
            UpdateManager.ui = new UpdateManagerUI(UpdateManager.confProductName, UpdateManager.confSubProductName, UpdateManager.fromMainBoolean);
        }
    }
    
    @Override
    public void setVisible(final boolean visible) {
        if (UpdateManager.ui != null) {
            UpdateManager.ui.setVisible(visible);
        }
    }
    
    public UpdateManager() {
        this.message = "";
        this.homeDir = ".";
        this.option = null;
        this.patchVersion = null;
        this.installDir = null;
        this.certPath = null;
        this.displayNameAsVersion = true;
        this.actVersion = false;
        this.dispVersion = false;
        this.cgui = false;
        this.ppmFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".ppm");
            }
        };
    }
    
    public UpdateManager(final String[] args) {
        this.message = "";
        this.homeDir = ".";
        this.option = null;
        this.patchVersion = null;
        this.installDir = null;
        this.certPath = null;
        this.displayNameAsVersion = true;
        this.actVersion = false;
        this.dispVersion = false;
        this.cgui = false;
        this.ppmFileFilter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".ppm");
            }
        };
        this.initialize(args);
        try {
            final String instanceConfigPath = UpdateManagerUtil.getHomeDirectory() + File.separator + UpdateManager.updateFilePath + File.separator + "um_instance.config";
            UpdateManager.instanceConfig = UpdateManagerUtil.getInstanceConfig(instanceConfigPath);
        }
        catch (final Exception e) {
            UpdateManager.out.log(Level.SEVERE, "Problem while reading/generating instance config : " + e.getMessage(), e);
        }
    }
    
    public void initialize(final String[] args) {
        UpdateManager.productStats = null;
        UpdateManager.productStats = new Hashtable<String, String>();
        final String usage = "Options:\n [-help -> Displays the available options.]\n\n\tMandatory arguments:\n\t [-u (Directory path where update_conf.xml is present)]\n\tOptional arguments:\n\t [-h (The product home directory path)]\n\t [-g -> To invoke Update Manger in GUI mode (default mode)]\n\n\tCommand Line Options:\n\t [-c -> To invoke Update Manager in command line]\n\t\t [i -> Install the patch]\n\t\t [u -> Uninstall the patch]\n\n\t\t [c -> import certificate]\n\t\t [v -> to view the installed ServicePack versions]\n\t [-ppmPath (Path of Patch(or PPM) file)]\n\t [-certPath (Certificate file path)]\n\t [-version (Display name of specific verion to uninstall)]\n\t [-actversion (Version of the patch to uninstall)]\n\t [-language (language(lowercase two-letter ISO-639 code))]\n\t [-country (country(uppercase two-letter ISO-3166 code))]\n\t [-fileName (Path of the properties file for Update Manager)]\n\t [-ppmInfo (Path of Patch(or PPM) file)]\n ";
        for (int length = args.length, i = 0; i < length; ++i) {
            if (args[i].trim().equals("-help")) {
                ConsoleOut.println(usage);
                System.exit(0);
            }
            else if (args[i].trim().equals("-u")) {
                if (++i < length) {
                    UpdateManager.updateFilePath = args[i].trim();
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().equals("-h")) {
                if (++i < length) {
                    this.homeDir = args[i].trim();
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().equals("-c")) {
                UpdateManager.commandLineBoolean = true;
            }
            else if (args[i].trim().equals("-option")) {
                if (++i < length) {
                    this.option = args[i].trim();
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().equals("-certPath")) {
                if (++i < length) {
                    this.certPath = args[i].trim();
                    try {
                        this.certPath = new File(this.certPath).getCanonicalPath();
                    }
                    catch (final IOException ioe) {
                        ConsoleOut.print("Problem while forming CanonicalPath of the certificate file. Try with proper path or contact support. \n" + ioe.getMessage());
                        System.exit(0);
                    }
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().equals("-cgui")) {
                this.cgui = true;
            }
            else if (args[i].trim().equals("-ppmPath")) {
                if (++i < length) {
                    UpdateManager.patchPath = args[i].trim();
                    try {
                        UpdateManager.patchPath = new File(UpdateManager.patchPath).getCanonicalPath();
                    }
                    catch (final IOException ioe) {
                        ConsoleOut.print("Problem while forming CanonicalPath of the patch file. Try with proper path or contact support.\n" + ioe.getMessage());
                        System.exit(0);
                    }
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().equals("-actversion")) {
                this.actVersion = true;
                if (++i < length && !this.dispVersion) {
                    this.displayNameAsVersion = false;
                    this.patchVersion = args[i].trim();
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().equals("-version")) {
                this.dispVersion = true;
                if (++i < length && !this.actVersion) {
                    this.patchVersion = args[i].trim();
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().equals("-g")) {
                UpdateManager.commandLineBoolean = false;
            }
            else if (args[i].trim().equals("-language")) {
                if (++i < length) {
                    UpdateManager.language = args[i].trim();
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().equals("-country")) {
                if (++i < length) {
                    UpdateManager.country = args[i].trim();
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().equals("-fileName")) {
                if (++i < length) {
                    UpdateManager.localePropertiesFileName = args[i].trim();
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().startsWith("-D")) {
                final String str = args[i].trim();
                final int index = str.indexOf("=");
                if (index != -1) {
                    final String key = str.substring(2, index);
                    final String value = str.substring(str.lastIndexOf("=") + 1);
                    ((Hashtable<String, String>)System.getProperties()).put(key, value);
                }
            }
            else if (args[i].trim().startsWith("-ppmInfo")) {
                if (++i < length) {
                    final String ppmPath = args[i].trim();
                    this.displayPPMInfo(ppmPath);
                    System.exit(0);
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().startsWith("-skip.prevalidation")) {
                if (++i < length) {
                    UpdateManager.performPreValidation = !Boolean.parseBoolean(args[i].trim());
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().startsWith("-alreadyCompletedPrePostClassName")) {
                if (++i < length) {
                    UpdateManager.alreadyCompletedPrePostClassName = args[i].trim();
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().startsWith("-alreadyAppliedPatchesCount")) {
                if (++i < length) {
                    UpdateManager.alreadyAppliedPatchesCount = Integer.parseInt(args[i].trim());
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().equalsIgnoreCase("-a")) {
                UpdateManager.isInvokedForAutoApplyOfPatches = true;
            }
            else if (args[i].trim().equalsIgnoreCase("-au")) {
                UpdateManager.isInvokedFromAutoUpgrade = true;
                UpdateManager.isInvokedForAutoApplyOfPatches = true;
                UpdateManager.commandLineBoolean = true;
            }
            else if (args[i].trim().startsWith("-pcv") && UpdateManager.isInvokedFromAutoUpgrade) {
                if (++i < length) {
                    UpdateManager.patchesCompatibilityVerifier_implementationClass = args[i].trim();
                }
                else {
                    ConsoleOut.println(usage);
                    System.exit(0);
                }
            }
            else if (args[i].trim().startsWith("-rotate.keys")) {
                UpdateManager.rotateKeys = true;
            }
            else {
                ConsoleOut.println(usage);
                System.exit(0);
            }
        }
        if (this.homeDir == null || this.homeDir.equals(".")) {
            this.homeDir = System.getProperty("user.dir");
        }
        UpdateManagerUtil.setHomeDirectory(this.homeDir);
    }
    
    public void invokeNewUM(final String ppmpath) {
        UpdateManager.fromMainBoolean = true;
        this.init();
        UpdateManager.ui.init();
        UpdateManager.ui.setPPMPath(ppmpath);
        UpdateManager.ui.showUI(true);
        try {
            if (!CertificateUtil.isKeyStoreExists(UpdateManagerUtil.getHomeDirectory() + File.separator + "conf")) {
                autoImportCertificate(UpdateManagerUtil.getHomeDirectory());
            }
        }
        catch (final Exception e) {
            UpdateManager.out.log(Level.SEVERE, "Problem while importing certificate : " + e.getMessage(), e);
        }
        if (getAlreadyCompletedPrePostClassName() != null && (!CertificateUtil.isKeyStoreExists(this.homeDir + File.separator + "conf") || PatchIntegrityVerifier.verifyPatch(ppmpath, UpdateManagerUtil.getHomeDirectory() + File.separator + "conf", getInstanceConfig().getKeyStorePassword()) == PatchIntegrityState.SIGNATURE_DOES_NOT_MATCH)) {
            UpdateManager.ui.importCertificateActionPerformed();
        }
        if (UpdateManager.ui.validateTheFile(ppmpath, UpdateManager.performPreValidation)) {
            this.continueInstallation(ppmpath);
        }
    }
    
    public void continueInstallation(final String ppmpath) {
        UpdateManager.ui.callActionPerformed(ppmpath);
    }
    
    static void i18N() {
        if (UpdateManager.language == null) {
            UpdateManager.language = System.getProperty("user.language");
        }
        if (UpdateManager.country == null) {
            UpdateManager.country = System.getProperty("user.region");
        }
        final String[] param = { "RESOURCE_LOCALE", "RESOURCE_PROPERTIES" };
        int i = 0;
        final String[] lpArray = new String[4];
        lpArray[i++] = "-RESOURCE_LOCALE";
        lpArray[i++] = UpdateManager.language + "_" + UpdateManager.country;
        lpArray[i++] = "-RESOURCE_PROPERTIES";
        lpArray[i++] = UpdateManager.localePropertiesFileName;
        UpdateManager.po = new ParameterObject(param, lpArray);
        Utility.parseAndSetParameters(param, lpArray);
        CommonUtil.setResourceBundle(UpdateManager.localePropertiesFileName, UpdateManager.language, UpdateManager.country);
    }
    
    public static void main(final String[] args) {
        final UpdateManager um = new UpdateManager(args);
        try {
            initLog(um.homeDir, UpdateManager.updateFilePath);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        if (UpdateManager.confProductName == null) {
            final String confFileLocation = UpdateManagerUtil.getHomeDirectory() + File.separator + UpdateManager.updateFilePath;
            final boolean confBoolean = readConfFile(confFileLocation);
            if (!confBoolean) {
                throw new IllegalArgumentException("Problem while reading update_conf.xml file.");
            }
        }
        if (UpdateManager.rotateKeys) {
            rotateKeys();
        }
        if (um.cgui && UpdateManager.patchPath != null) {
            um.invokeNewUM(UpdateManager.patchPath);
        }
        else {
            UpdateManager.fromMainBoolean = true;
            um.init();
            um.setVisible(true);
        }
    }
    
    private static void rotateKeys() {
        InstanceConfig newInstanceConfig = null;
        final String instanceConfigPath = UpdateManagerUtil.getHomeDirectory() + File.separator + UpdateManager.updateFilePath + File.separator + "um_instance.config";
        final String oldInstanceConfigPath = UpdateManagerUtil.getHomeDirectory() + File.separator + UpdateManager.updateFilePath + File.separator + "um_instance.config.old";
        boolean isRotated = false;
        try {
            FileUtil.moveDirectory(new File(instanceConfigPath), new File(oldInstanceConfigPath));
            newInstanceConfig = InstanceConfig.getNewInstance();
            InstanceConfig.write(instanceConfigPath, newInstanceConfig);
            isRotated = true;
        }
        catch (final Exception e) {
            UpdateManager.out.log(Level.SEVERE, "Unable to rename instance config file. Hence rotation not possible. :: " + e.getMessage(), e);
            if (new File(oldInstanceConfigPath).exists() && !new File(instanceConfigPath).exists()) {
                try {
                    FileUtil.moveDirectory(new File(oldInstanceConfigPath), new File(instanceConfigPath));
                }
                catch (final IOException ioException) {
                    UpdateManager.out.log(Level.SEVERE, "Unable to revert back to original state. :: " + ioException.getMessage(), ioException);
                }
            }
        }
        if (isRotated) {
            final boolean isKeyStoreRotated = CertificateUtil.rotateKeyStorePassword(UpdateManagerUtil.getHomeDirectory() + File.separator + UpdateManager.updateFilePath, UpdateManager.instanceConfig.getKeyStorePassword(), newInstanceConfig.getKeyStorePassword());
            try {
                if (isKeyStoreRotated) {
                    FileUtil.deleteFiles(oldInstanceConfigPath);
                    final ProxyProperties properties = ProxyProperties.read();
                    UpdateManager.instanceConfig = newInstanceConfig;
                    CryptoHelper.initialize(UpdateManager.instanceConfig.getEncryptionKey());
                    if (properties != null) {
                        ProxyProperties.write(properties);
                        UpdateManager.out.log(Level.INFO, "Proxy re-encrypted successfully");
                    }
                }
                else {
                    FileUtil.deleteFiles(instanceConfigPath);
                    FileUtil.moveDirectory(new File(oldInstanceConfigPath), new File(instanceConfigPath));
                }
            }
            catch (final Exception e2) {
                UpdateManager.out.log(Level.SEVERE, "Exception occurred while rotating instance config " + e2.getMessage(), e2);
            }
        }
    }
    
    public void invokeUpdate() {
        final UpdateManagerCMD comm = new UpdateManagerCMD(UpdateManager.confProductName, UpdateManager.confSubProductName, UpdateManager.performPreValidation);
        UpdateManager.out.info(CommonUtil.getString("UpdateManager ProductName:") + " " + UpdateManager.confProductName);
        UpdateManager.out.info(CommonUtil.getString("UpdateManager ProductVersion:") + " " + UpdateManager.confProductVersion);
        UpdateManager.out.info(CommonUtil.getString("UpdateManager ProductContext:") + " " + UpdateManager.confSubProductName);
        if (this.option != null) {
            UpdateManagerUtil.setAutoCloseOnSuccessfulCompletion(true);
            if (this.option.equalsIgnoreCase("i")) {
                if (this.homeDir != null && UpdateManager.patchPath != null) {
                    UpdateManagerUtil.setCMDPatchPath(UpdateManager.patchPath);
                    comm.cmdInstallProcesss(this.homeDir, this.option, UpdateManager.patchPath, this.patchVersion);
                    this.callExit();
                    return;
                }
            }
            else if (this.option.equalsIgnoreCase("u")) {
                if (this.homeDir != null && this.patchVersion != null) {
                    comm.cmdInstallProcesss(this.homeDir, this.option, UpdateManager.patchPath, this.patchVersion, this.displayNameAsVersion);
                    this.callExit();
                    return;
                }
            }
            else {
                if (!this.option.equalsIgnoreCase("c")) {
                    comm.cmdInstallProcesss(this.homeDir, this.option, UpdateManager.patchPath, this.patchVersion);
                    return;
                }
                if (this.homeDir != null && this.certPath != null) {
                    comm.cmdImportCertificateProcesss(this.homeDir, this.certPath);
                    this.callExit();
                    return;
                }
            }
        }
        final String dirToUnzip = UpdateManagerUtil.getHomeDirectory();
        final File contextFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "insdet.tmp");
        final File revertFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "revdet.tmp");
        if (contextFile.exists()) {
            final ArrayList instDetails = this.readTempFile(contextFile);
            int i;
            for (int size = i = instDetails.size(); i > 0; --i) {
                final String string = instDetails.get(i - 1);
                final StringTokenizer st = new StringTokenizer(string, ",");
                final String ppmName = st.nextToken();
                final ArrayList conList = new ArrayList();
                conList.add(UpdateManager.confSubProductName);
                while (st.hasMoreTokens()) {
                    conList.add(st.nextToken());
                }
                final String ppmPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + ppmName;
                comm.installPatch(dirToUnzip, ppmPath, conList);
            }
            final File tmpFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "insdet.tmp");
            final File sumFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "smarttmp.txt");
            this.deleteFile(tmpFile);
            this.deleteFile(sumFile);
            final int status = UpdateManagerUtil.getExitStatus();
            if (status == 0) {
                ConsoleOut.println("\n" + CommonUtil.getString("Patch installed successfully"));
            }
            this.callExit();
        }
        else if (revertFile.exists()) {
            final ArrayList revDetails = this.readTempFile(revertFile);
            final String ppmName2 = null;
            if (!revDetails.isEmpty()) {
                for (int size2 = revDetails.size(), j = 0; j < size2; ++j) {
                    String version = revDetails.get(j);
                    if (version.startsWith("FP ")) {
                        version = version.substring(3);
                    }
                    else {
                        final ArrayList fplist = this.getFPSDependentOnSP(version, dirToUnzip);
                        if (fplist != null) {
                            for (int len = fplist.size(), k = 0; k < len; ++k) {
                                comm.cmdInstallProcesss(dirToUnzip, "u", null, fplist.get(k));
                            }
                        }
                        version = this.getVersionToUninstall(version, dirToUnzip);
                    }
                    comm.cmdInstallProcesss(dirToUnzip, "u", null, version);
                }
            }
            final File revFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "revdet.tmp");
            final File sumFile = new File(dirToUnzip + File.separator + "Patch" + File.separator + "smarttmp.txt");
            this.deleteFile(sumFile);
            this.deleteFile(revFile);
            this.callExit();
        }
        else if (isInvokedForAutoApplyOfPatches()) {
            try {
                if (!CertificateUtil.isKeyStoreExists(UpdateManagerUtil.getHomeDirectory() + File.separator + "conf")) {
                    autoImportCertificate(UpdateManagerUtil.getHomeDirectory());
                }
            }
            catch (final Exception e) {
                UpdateManager.out.log(Level.SEVERE, "Problem while importing certificate : " + e.getMessage(), e);
            }
            final AutoApplyPatches autoApplyPatches = new AutoApplyPatches();
            if (isInvokedFromAutoUpgrade() && UpdateManager.patchesCompatibilityVerifier_implementationClass != null) {
                try {
                    try (final UpdateManagerClassLoader updateManagerClassLoader = UpdateManagerClassLoader.getInstance()) {
                        final PatchesCompatibilityVerifier pcv = (PatchesCompatibilityVerifier)updateManagerClassLoader.loadClass(UpdateManager.patchesCompatibilityVerifier_implementationClass).newInstance();
                        autoApplyPatches.applyPatches(pcv);
                        UpdateManager.out.info("UpdateManager exiting as it is invoked from AutoUpgrade");
                        this.callExit();
                    }
                    return;
                }
                catch (final Throwable t) {
                    UpdateManager.out.log(Level.SEVERE, "Problem while loading patches compatibility class", t);
                    throw new RuntimeException(t);
                }
            }
            try {
                if (UpdateManagerUtil.isContainerOfPatches(UpdateManager.patchPath)) {
                    final boolean success = comm.applyContainerOfPatches(UpdateManager.patchPath);
                    UpdateManager.out.log(Level.INFO, "Patch Apply Status : {0}.", success ? "Success" : "Failure");
                    this.callExit();
                }
            }
            catch (final IOException e2) {
                UpdateManager.out.log(Level.SEVERE, "Problem while checking isContainerOfPatches", e2);
                throw new RuntimeException(e2);
            }
        }
        else {
            comm.commandLineInstall();
        }
    }
    
    private ArrayList getFPSDependentOnSP(final String version, final String dirToUnzip) {
        if (!version.equals("uninstallall")) {
            return null;
        }
        final String[] strArr = getAllServicePackVersions(dirToUnzip);
        if (strArr == null) {
            return null;
        }
        final String specsPath = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final int size = strArr.length;
        final ArrayList newList = new ArrayList();
        for (final String spVersion : strArr) {
            final String[] fpVersions = vProfile.getTheFPVersions();
            if (fpVersions == null) {
                return null;
            }
            final String[] trunVersionArray = { spVersion.substring(spVersion.lastIndexOf("-") + 1) };
            for (final String fpVer : fpVersions) {
                final FeatureVersionComp fvc = vProfile.getVersionCompatibility(fpVer);
                if (fvc != null) {
                    final String patchOption = fvc.getCompPatchOption();
                    final String patchVersion = fvc.getCompPatchVersion();
                    if (patchOption != null) {
                        if (patchVersion != null) {
                            final VersionChecker vChecker = new VersionChecker();
                            final int opt = CommonUtil.parseOption(patchOption);
                            final boolean status = vChecker.checkVersionCompatible(patchVersion, trunVersionArray, opt);
                            if (status) {
                                final String dipVer = vProfile.getTheAdditionalDetail(fpVer, "DisplayName");
                                newList.add(dipVer);
                            }
                        }
                    }
                }
            }
        }
        if (!newList.isEmpty()) {
            return newList;
        }
        return null;
    }
    
    private String getVersionToUninstall(String version, final String dirToUnzip) {
        final String[] strArr = getAllServicePackVersions(dirToUnzip);
        if (strArr == null) {
            return version;
        }
        final String specsPath = dirToUnzip + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final int len = strArr.length;
        if (version.equals("uninstallall")) {
            version = this.getDisplayName(vProfile, strArr[0]);
            return version;
        }
        for (int i = 0; i < len; ++i) {
            final String ver = strArr[i];
            if (ver.equals(version)) {
                return this.getDisplayName(vProfile, strArr[i + 1]);
            }
        }
        return this.getDisplayName(vProfile, strArr[len - 1]);
    }
    
    private void deleteFile(final File file) {
        if (file.exists()) {
            file.delete();
        }
    }
    
    private void callExit() {
        final int status = UpdateManagerUtil.getExitStatus();
        if (status == 0) {
            if (System.getProperty("IsWebUpdate") == null) {
                System.exit(0);
            }
        }
        else if (System.getProperty("IsWebUpdate") == null) {
            System.exit(2);
        }
        else {
            UpdateManagerUtil.setTaskStatus(false);
        }
    }
    
    public static boolean readConfFile(final String updatePath) {
        final String updateFilePath = updatePath;
        final File confPath = new File(updateFilePath + File.separator + "update_conf.xml");
        if (!confPath.exists()) {
            ConsoleOut.println(CommonUtil.getString("UpdateManager conf file does not exists"));
            return false;
        }
        final UpdateManagerParser parser = new UpdateManagerParser(confPath.toString());
        UpdateManager.generalProps = parser.getGeneralProps();
        UpdateManager.confProductName = ((Hashtable<K, String>)UpdateManager.generalProps).get("ProductName");
        UpdateManager.confProductVersion = ((Hashtable<K, String>)UpdateManager.generalProps).get("ProductVersion");
        UpdateManager.confSubProductName = ((Hashtable<K, String>)UpdateManager.generalProps).get("SubProductName");
        UpdateManagerUtil.setHelpXmlFilePath(UpdateManager.helpXmlFilePath = ((Hashtable<K, String>)UpdateManager.generalProps).get("HelpXmlFilePath"));
        UpdateManagerUtil.setHelpHtmlFilePath(UpdateManager.helpHtmlFilePath = ((Hashtable<K, String>)UpdateManager.generalProps).get("HelpHtmlFilePath"));
        if (UpdateManager.generalProps.getProperty("EnableUninstalltion") != null) {
            UpdateManagerUtil.setAllowUninstalltion(Boolean.valueOf(UpdateManager.generalProps.getProperty("EnableUninstalltion")));
        }
        else {
            UpdateManagerUtil.setAllowUninstalltion(true);
        }
        if (!UpdateManager.commandLineBoolean) {
            try {
                final String font = ((Hashtable<K, String>)UpdateManager.generalProps).get("Font");
                if (font != null) {
                    UpdateManagerUtil.setFont(new Font(font, 0, 12));
                    UpdateManagerUtil.setBoldFont(new Font(font, 1, 12));
                }
            }
            catch (final Error error) {}
        }
        if (UpdateManager.generalProps.getProperty("Language") != null) {
            UpdateManager.language = ((Hashtable<K, String>)UpdateManager.generalProps).get("Language");
        }
        if (UpdateManager.generalProps.getProperty("Country") != null) {
            UpdateManager.country = ((Hashtable<K, String>)UpdateManager.generalProps).get("Country");
        }
        final String lpfn = ((Hashtable<K, String>)UpdateManager.generalProps).get("PropertiesFileName");
        if (lpfn != null) {
            UpdateManager.localePropertiesFileName = lpfn;
        }
        final String deploy = ((Hashtable<K, String>)UpdateManager.generalProps).get("EnableDeploymentTool");
        UpdateManagerUtil.enableDeploymentTool(Boolean.valueOf(deploy));
        if (UpdateManager.generalProps.getProperty("custom_patch_validator") != null) {
            UpdateManager.validationClass = UpdateManager.generalProps.getProperty("custom_patch_validator");
        }
        if (UpdateManager.generalProps.get("AutoCloseDelayTimeInSeconds") != null) {
            UpdateManagerUtil.setAutoCloseDelayTimeInSeconds(Long.parseLong(((Hashtable<K, String>)UpdateManager.generalProps).get("AutoCloseDelayTimeInSeconds")));
        }
        return true;
    }
    
    private static Hashtable getAttributeList(final Node node) {
        final Hashtable attrList = new Hashtable();
        final NamedNodeMap nodemap = node.getAttributes();
        final int length = nodemap.getLength();
        int index = 0;
        while (index < length) {
            final Node n = nodemap.item(index++);
            final String attrName = n.getNodeName();
            final String attrValue = n.getNodeValue();
            attrList.put(attrName, attrValue);
        }
        return attrList;
    }
    
    public String getCurrentPatchVersion() {
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] ver = vProfile.getTheVersions();
        if (ver != null) {
            final int length = ver.length;
            return ver[length - 1];
        }
        return null;
    }
    
    public static String getCurrentServicePackVersion() {
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] ver = vProfile.getTheVersions();
        if (ver != null) {
            final int length = ver.length;
            return ver[length - 1];
        }
        return null;
    }
    
    public static String getLastRevertedVersion() {
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String ver = vProfile.getLastRevertedVersion();
        if (ver != null) {
            return ver;
        }
        return null;
    }
    
    public static String getFirstInstalledVersion() {
        final String[] strArr = getAllServicePackVersions(UpdateManagerUtil.getHomeDirectory());
        if (strArr != null) {
            final int len = strArr.length;
            return strArr[0];
        }
        return null;
    }
    
    public String[] getInstallDetails(final String productName, final String productVersion, final String module) {
        if (!productName.equals(UpdateManager.confProductName)) {
            return null;
        }
        if (!productVersion.equals(UpdateManager.confProductVersion)) {
            return null;
        }
        final String currentVersion = this.getCurrentPatchVersion();
        if (currentVersion == null) {
            return null;
        }
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] obj = vProfile.getTheContext(currentVersion);
        final String patchName = vProfile.getTheAdditionalDetail(currentVersion, "PatchName");
        if (obj == null) {
            return null;
        }
        final ArrayList contextList = new ArrayList();
        for (int i = 0; i < obj.length; ++i) {
            contextList.add(obj[i]);
        }
        return this.readCurrentInfFile(contextList, module, currentVersion, patchName);
    }
    
    public String getProductName() {
        return UpdateManager.confProductName;
    }
    
    public static String getMajorVersion(final String path) {
        if (UpdateManager.confProductVersion == null) {
            readConfFile(UpdateManagerUtil.getHomeDirectory() + File.separator + path);
        }
        final int index = UpdateManager.confProductVersion.indexOf(".");
        if (index != -1) {
            return UpdateManager.confProductVersion.substring(0, index);
        }
        return UpdateManager.confProductVersion;
    }
    
    public static String getMinorVersion(final String path) {
        if (UpdateManager.confProductVersion == null) {
            readConfFile(UpdateManagerUtil.getHomeDirectory() + File.separator + path);
        }
        final int index = UpdateManager.confProductVersion.indexOf(".");
        if (index != -1) {
            return UpdateManager.confProductVersion.substring(index + 1);
        }
        return UpdateManager.confProductVersion;
    }
    
    public static String getServicePackVersionAlone(final String path) {
        final String spversion = getCurrentServicePackVersion();
        if (spversion == null) {
            return null;
        }
        return spversion.substring(spversion.lastIndexOf("-") + 1);
    }
    
    public static String getProductName(final String path) {
        readConfFile(UpdateManagerUtil.getHomeDirectory() + File.separator + path);
        return UpdateManager.confProductName;
    }
    
    public static String getProductVersion(final String path) {
        readConfFile(UpdateManagerUtil.getHomeDirectory() + File.separator + path);
        return UpdateManager.confProductVersion;
    }
    
    public String getProductVersion() {
        return UpdateManager.confProductVersion;
    }
    
    public static String getSubProductName(final String path) {
        readConfFile(UpdateManagerUtil.getHomeDirectory() + File.separator + path);
        return UpdateManager.confSubProductName;
    }
    
    public String getSubProductName() {
        return UpdateManager.confSubProductName;
    }
    
    private String[] readCurrentInfFile(final ArrayList installedContext, final String module, final String currentVersion, final String patchName) {
        final String temp = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + currentVersion + File.separator + "inf.xml";
        String[] contextList = { null };
        final String ppmName = patchName;
        contextList[0] = ppmName;
        try {
            final XmlParser xmlParser = new XmlParser(temp);
            final Hashtable hash = xmlParser.getXmlData().getContextTable();
            final Object[] enum1 = hash.keySet().toArray();
            for (int size = enum1.length, i = 0; i < size; ++i) {
                final String contextName = (String)enum1[i];
                final UpdateData updateData = hash.get(contextName);
                final String type = updateData.getContextType();
                if (type.equals("Optional")) {
                    final Vector depenVec = updateData.getDependencyVector();
                    if (depenVec.contains(UpdateManager.confSubProductName) && depenVec.contains(module) && installedContext.contains(contextName)) {
                        final int len = contextList.length;
                        final String[] tmp = new String[len + 1];
                        System.arraycopy(contextList, 0, tmp, 0, len);
                        tmp[len] = contextName;
                        contextList = tmp;
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return contextList;
    }
    
    public int isCompatible(final String productName, final String productVersion, final String installedVersion, final String[] contexts) {
        final String displayVersion = installedVersion;
        if (!productName.equals(UpdateManager.confProductName)) {
            this.message = CommonUtil.getString("The Service Pack is for") + " " + UpdateManager.confProductName + CommonUtil.getString(". It is not compatible with the current application:") + productName;
            return 3;
        }
        if (!productVersion.equals(UpdateManager.confProductVersion)) {
            this.message = CommonUtil.getString("The Service Pack is for") + " " + UpdateManager.confProductName + " " + UpdateManager.confProductVersion + CommonUtil.getString(". It is not compatible with the current application:") + productName + " " + productVersion;
            return 3;
        }
        final String currentVersion = this.getCurrentPatchVersion();
        final String currentDisplayVersion = this.getCurrentPatchDisplayVersion();
        if (currentVersion == null && installedVersion == null) {
            this.message = CommonUtil.getString("The Service Pack is compatible with the current application");
            return 0;
        }
        if (currentVersion == null && installedVersion != null) {
            this.message = displayVersion + " " + CommonUtil.getString("is installed in the current application but no Service Pack is installed in your server.");
            return 3;
        }
        if (currentVersion != null) {
            if (currentVersion.equals(installedVersion)) {
                if (this.isContextCompatible(contexts, currentVersion)) {
                    this.message = CommonUtil.getString("The Service Pack is compatible with the current application.");
                    return 0;
                }
                return 1;
            }
            else {
                if (installedVersion == null) {
                    this.message = CommonUtil.getString("No Service Pack is installed in the current application but") + " " + currentDisplayVersion + " " + CommonUtil.getString("is installed in the server.");
                    return 2;
                }
                if (this.isVersionCompatible(installedVersion)) {
                    this.message = displayVersion + " " + CommonUtil.getString("is installed in the current application and") + " " + currentDisplayVersion + " " + CommonUtil.getString("is installed in the server. They are compatible.");
                    return 1;
                }
                final VersionChecker vChecker = new VersionChecker();
                final String currentVer = currentVersion.substring(currentVersion.lastIndexOf("-") + 1);
                final String installedVer = installedVersion.substring(installedVersion.lastIndexOf("-") + 1);
                final int type = vChecker.checkGreater(currentVer, installedVer);
                if (type == 0) {
                    this.message = displayVersion + " " + CommonUtil.getString("is installed in the current application and") + " " + currentDisplayVersion + " " + CommonUtil.getString("is installed in the server. But they are not compatible.");
                    return 2;
                }
                if (type == 1) {
                    this.message = displayVersion + " " + CommonUtil.getString("is installed in the current application and") + " " + currentDisplayVersion + " " + CommonUtil.getString("is installed in the server.");
                    return 3;
                }
            }
        }
        return 3;
    }
    
    public int isCompatible(final String productName, final String productVersion, final String installedVersion, final String displayVersion, final String[] contexts, String[] fpVersions, final String[] indfpVersions) {
        fpVersions = this.mergeFPVersions(fpVersions, indfpVersions);
        if (!productName.equals(UpdateManager.confProductName)) {
            this.message = CommonUtil.getString("The Service Pack / Feature Pack is for") + " " + UpdateManager.confProductName + CommonUtil.getString(". It is not compatible with the current application:") + productName;
            return 3;
        }
        final String currentVersion = this.getCurrentPatchVersion();
        final String currentDisplayVersion = this.getCurrentPatchDisplayVersion();
        String[] currentFPVersions = this.getFeaturePackVersions(this.homeDir);
        final String[] currentIndFPVersions = this.getIndependentFeaturePackVersions(this.homeDir);
        currentFPVersions = this.mergeFPVersions(currentFPVersions, currentIndFPVersions);
        if (currentVersion == null && installedVersion == null && fpVersions == null && currentFPVersions == null) {
            this.message = CommonUtil.getString("The Service Pack/ Feature Pack is compatible with the current application");
            return 0;
        }
        if (currentVersion == null && currentFPVersions == null && installedVersion != null && fpVersions == null) {
            this.message = displayVersion + " " + CommonUtil.getString("is installed in the current application but no Service Pack / Feature Pack is installed in your server.");
            return 3;
        }
        if (currentVersion == null && currentFPVersions == null && installedVersion == null && fpVersions != null) {
            final String formattedFPVersions = this.getFormattedFPVersions(fpVersions);
            this.message = CommonUtil.getString("\nFeature Pack:") + " " + formattedFPVersions + " " + CommonUtil.getString("\nis installed in the current application but no Service Pack / Feature Pack is installed in your server.");
            return 3;
        }
        if (currentVersion == null && currentFPVersions == null && installedVersion != null && fpVersions != null) {
            final String formattedFPVersions = this.getFormattedFPVersions(fpVersions);
            this.message = CommonUtil.getString("Service Pack:") + " " + displayVersion + CommonUtil.getString("\nFeature Pack:") + " " + formattedFPVersions + " " + CommonUtil.getString("\nis installed in the current application but no Service Pack / Feature Pack is installed in your server.");
            return 3;
        }
        if (currentVersion != null) {
            if (currentVersion.equals(installedVersion)) {
                final int fpState = this.getFPVersionsState(currentFPVersions, fpVersions);
                if (fpState == 0) {
                    if (this.isContextCompatible(contexts, currentVersion)) {
                        this.message = CommonUtil.getString("The Service Pack is compatible with the current application.");
                        return 0;
                    }
                    this.message = CommonUtil.getString("The Service Pack is not compatible with the current application.");
                    return 1;
                }
                else {
                    if (fpState == 2) {
                        final String[] diffVersions = this.getFPVersions(currentFPVersions, fpVersions);
                        final String formatString = this.getFormattedFPVersions(diffVersions);
                        this.message = CommonUtil.getString("Feature Pack(s):") + " " + formatString + " " + CommonUtil.getString("is not installed in the client.");
                        return 2;
                    }
                    if (fpState == 3) {
                        final String[] st = this.getFPVersions(fpVersions, currentFPVersions);
                        final String fString = this.getFormattedFPVersions(st);
                        this.message = CommonUtil.getString("Feature Pack(s):") + " " + fString + " " + CommonUtil.getString("is not installed in the server.");
                        return 3;
                    }
                    this.message = CommonUtil.getString("The Service Pack / Feature Pack is compatible with the current application.");
                    return 0;
                }
            }
            else if (installedVersion == null) {
                if (fpVersions == null) {
                    this.message = CommonUtil.getString("No Service Pack / Feature Pack is installed in the current application but") + " " + currentDisplayVersion + " " + CommonUtil.getString("is installed in the server.");
                    return 2;
                }
                this.message = CommonUtil.getString("No Service Pack is installed in the current application but") + " " + currentDisplayVersion + " " + CommonUtil.getString("is installed in the server.");
                return 2;
            }
            else {
                if (this.isVersionCompatible(installedVersion)) {
                    this.message = displayVersion + " " + CommonUtil.getString("is installed in the current application and") + " " + currentDisplayVersion + " " + CommonUtil.getString("is installed in the server. They are compatible.");
                    return 1;
                }
                final String currentVer = currentVersion.substring(currentVersion.lastIndexOf("-") + 1);
                final String installedVer = installedVersion.substring(installedVersion.lastIndexOf("-") + 1);
                final int type = this.checkGreater(currentVersion, installedVersion);
                if (type == 0) {
                    this.message = displayVersion + " " + CommonUtil.getString("is installed in the current application and") + " " + currentDisplayVersion + " " + CommonUtil.getString("is installed in the server. But they are not compatible.");
                    return 2;
                }
                if (type == 1) {
                    this.message = displayVersion + " " + CommonUtil.getString("is installed in the current application and") + " " + currentDisplayVersion + " " + CommonUtil.getString("is installed in the server.");
                    return 3;
                }
                return 3;
            }
        }
        else {
            if (currentFPVersions != null && fpVersions == null && installedVersion == null) {
                this.message = CommonUtil.getString("No Service Pack / Feature Pack is installed in the current application but") + " " + currentFPVersions[currentFPVersions.length - 1] + CommonUtil.getString("is installed in the server.");
                return 2;
            }
            if (installedVersion == null) {
                final String[] diffVersions2 = this.getFPVersions(currentFPVersions, fpVersions);
                final int len = diffVersions2.length;
                if (len != 0) {
                    final String str = this.getFormattedFPVersions(diffVersions2);
                    this.message = CommonUtil.getString("The following Feature Pack is installed in the server\n") + str;
                    return 2;
                }
                final String[] rdiffVersions = this.getFPVersions(fpVersions, currentFPVersions);
                final int rlen = rdiffVersions.length;
                if (rlen != 0) {
                    final String rstr = this.getFormattedFPVersions(rdiffVersions);
                    this.message = CommonUtil.getString("The following Feature Pack is installed in the current application\n") + rstr;
                    return 3;
                }
                this.message = CommonUtil.getString("The Service Pack / Feature Pack is compatible with the current application.");
                return 0;
            }
            else {
                final String[] diffVersions2 = this.getFPVersions(currentFPVersions, fpVersions);
                final int len = diffVersions2.length;
                if (len != 0) {
                    final String str = this.getFormattedFPVersions(diffVersions2);
                    this.message = CommonUtil.getString("The Service Pack " + installedVersion + CommonUtil.getString(" is installed in the current application and no Service Pack is installed in the server. The following Feature Pack(s) is installed in the server ") + str);
                    return 3;
                }
                this.message = CommonUtil.getString("The Service Pack " + installedVersion + CommonUtil.getString(" is installed in the current application and no Service Pack is installed in the server\n"));
                return 3;
            }
        }
    }
    
    public int checkGreater(final String currentVersion, final String installedVersion) {
        final String currentVer = currentVersion.substring(currentVersion.lastIndexOf("-") + 1);
        final String installedVer = installedVersion.substring(installedVersion.lastIndexOf("-") + 1);
        final VersionChecker vChecker = new VersionChecker();
        int type = vChecker.checkGreater(currentVer, installedVer);
        if (type != 0) {
            final int installedPatchMajorVersion = this.getPatchMajorVersion(installedVersion);
            final int currentPatchMajorVersion = this.getPatchMajorVersion(currentVersion);
            if (currentPatchMajorVersion > installedPatchMajorVersion) {
                type = 0;
            }
        }
        return type;
    }
    
    public boolean isContextCompatible(final String[] contexts) {
        return this.isContextCompatible(contexts, null);
    }
    
    public int getPatchMajorVersion(final String patchVersion) {
        int majorVersion = -1;
        try {
            final String patchVersionInf = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + patchVersion + File.separator + "inf.xml";
            final XmlParser xmlParser = new XmlParser(patchVersionInf);
            final String version = xmlParser.getXmlData().getProductVersion();
            final int index = version.indexOf(".");
            if (index != -1) {
                majorVersion = Integer.parseInt(version.substring(0, index));
            }
            else {
                majorVersion = Integer.parseInt(version);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return majorVersion;
    }
    
    public boolean isContextCompatible(final String[] contexts, final String currentVersion) {
        String temp = null;
        if (currentVersion == null) {
            temp = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "inf.xml";
        }
        else {
            temp = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + currentVersion + File.separator + "inf.xml";
        }
        String[] contextList = new String[0];
        try {
            final XmlParser xmlParser = new XmlParser(temp);
            final Hashtable hash = xmlParser.getXmlData().getContextTable();
            final Object[] enum1 = hash.keySet().toArray();
            for (int size = enum1.length, i = 0; i < size; ++i) {
                final String contextName = (String)enum1[i];
                final UpdateData updateData = hash.get(contextName);
                final String type = updateData.getContextType();
                if (type.equals("Optional")) {
                    final Vector depenVec = updateData.getDependencyVector();
                    if (depenVec.contains(UpdateManager.confSubProductName)) {
                        for (int len = contexts.length, j = 0; j < len; ++j) {
                            if (contexts[j].equals(contextName)) {
                                final int leng = contextList.length;
                                final String[] tmp = new String[leng + 1];
                                System.arraycopy(contextList, 0, tmp, 0, leng);
                                tmp[leng] = contextName;
                                contextList = tmp;
                            }
                        }
                    }
                }
            }
            final ArrayList latestVersionContext = this.getCurrentPatchContexts();
            for (final String cont : contextList) {
                if (!latestVersionContext.contains(cont)) {
                    this.message = CommonUtil.getString("The current application has the optional") + " " + cont + " " + CommonUtil.getString("context installed.But the server does not have this context. You must install this context in the server and then run the application.");
                    return false;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean isVersionCompatible(final String installedVersion) {
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] ver = vProfile.getTheVersions();
        String previousVers = null;
        if (ver != null) {
            final int length = ver.length;
            previousVers = ver[length - 1];
        }
        final String temp = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + previousVers + File.separator + "inf.xml";
        if (!new File(temp).exists()) {
            return false;
        }
        try {
            final XmlParser xmlParser = new XmlParser(temp);
            final ArrayList list = xmlParser.getXmlData().getFeatureCompatibility();
            if (list == null || list.isEmpty()) {
                return false;
            }
            for (int size = list.size(), i = 0; i < size; ++i) {
                final FeatureCompInfo fpc = list.get(i);
                final String pName = fpc.getProductName();
                if (pName.equals(UpdateManager.confProductName)) {
                    return this.productVersionCheck(fpc, installedVersion);
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            return false;
        }
        return false;
    }
    
    private boolean productVersionCheck(final FeatureCompInfo fcomp, final String installedVersion) {
        final FeatureCompInfo fpc = fcomp;
        final Object[] obj = fpc.getPrdVersionInfo();
        if (obj == null) {
            return false;
        }
        final int s = obj.length;
        if (s == 0) {
            return false;
        }
        for (int j = 0; j < s; ++j) {
            final FeaturePrdVersionInfo fc = (FeaturePrdVersionInfo)obj[j];
            final String pVersion = fc.getProductVersion();
            if (pVersion.equals(UpdateManager.confProductVersion)) {
                return this.productPatchVersionCheck(fc, installedVersion);
            }
        }
        return false;
    }
    
    private boolean productPatchVersionCheck(final FeaturePrdVersionInfo fcheck, final String installedVersion) {
        final FeaturePrdVersionInfo fc = fcheck;
        final FeatureVersionComp fvc = fc.getFeatureVersionComp();
        if (fvc == null) {
            return false;
        }
        final String patchVersion = fvc.getCompPatchVersion();
        final String patchOption = fvc.getCompPatchOption();
        if (patchVersion != null && patchOption != null) {
            final boolean notPresent = true;
            final VersionChecker vChecker = new VersionChecker();
            final String installVersion = installedVersion.substring(installedVersion.lastIndexOf("-") + 1);
            final String[] instVer = { installVersion };
            final int opt = CommonUtil.parseOption(patchOption);
            final boolean bool = vChecker.checkVersionCompatible(patchVersion, instVer, opt);
            if (bool) {
                return true;
            }
        }
        return false;
    }
    
    public ArrayList getCurrentPatchContexts() {
        final String currentVersion = this.getCurrentPatchVersion();
        if (currentVersion == null) {
            return null;
        }
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] obj = vProfile.getTheContext(currentVersion);
        if (obj == null) {
            return null;
        }
        final ArrayList contextList = new ArrayList();
        for (int i = 0; i < obj.length; ++i) {
            final String cont = obj[i];
            if (!cont.equals(UpdateManager.confSubProductName)) {
                contextList.add(cont);
            }
        }
        return contextList;
    }
    
    public String getPatchFileName(final String currentVersion) {
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String patchName = vProfile.getTheAdditionalDetail(currentVersion, "PatchName");
        return patchName;
    }
    
    public String getPatchFilePath(final String productName, final String productVersion) {
        if (!productName.equals(UpdateManager.confProductName)) {
            return null;
        }
        if (!productVersion.equals(UpdateManager.confProductVersion)) {
            return null;
        }
        final String patchVersion = this.getCurrentPatchVersion();
        final String ppmName = this.getPatchFileName(patchVersion);
        final String path = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + ppmName;
        return path;
    }
    
    public ArrayList readTempFile(final File f) {
        final ArrayList list = new ArrayList();
        try {
            final InputStreamReader finp = new InputStreamReader(new FileInputStream(f));
            final BufferedReader lData = new BufferedReader(finp);
            String dataBuffer;
            while ((dataBuffer = lData.readLine()) != null) {
                if (!dataBuffer.trim().equals("")) {
                    final String string = dataBuffer.trim();
                    list.add(string);
                }
            }
            finp.close();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public String getModuleName(final String productName, final String productVersion) {
        if (!productName.equals(UpdateManager.confProductName)) {
            return null;
        }
        if (!productVersion.equals(UpdateManager.confProductVersion)) {
            return null;
        }
        return UpdateManager.confSubProductName;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    private boolean displayPPMInfo(final String fileName) {
        String productName = null;
        String productVersion = null;
        String patchVersion = null;
        String patchDescription = null;
        ZipFile zipFile = null;
        try {
            final File pFile = new File(fileName);
            if (!pFile.exists()) {
                ConsoleOut.println("The specified file does not exist.");
                return false;
            }
            zipFile = new ZipFile(fileName);
            final Enumeration entries = zipFile.entries();
            final ZipEntry zEntry = zipFile.getEntry("inf.xml");
            if (zEntry == null) {
                ConsoleOut.println("The format of the specified file is not supported.");
                return false;
            }
            final InputStream inps = zipFile.getInputStream(zEntry);
            final XmlParser xmlParser = new XmlParser(inps);
            final Hashtable contextTable = xmlParser.getXmlData().getContextTable();
            final Enumeration enum1 = contextTable.keys();
            productName = xmlParser.getXmlData().getProductName();
            ConsoleOut.println("The Product Name is \t: " + productName);
            productVersion = xmlParser.getXmlData().getProductVersion();
            ConsoleOut.println("The Product Version is \t: " + productVersion);
            patchVersion = xmlParser.getXmlData().getPatchVersion();
            ConsoleOut.println("The Patch Version is \t: " + patchVersion);
            patchDescription = xmlParser.getXmlData().getPatchDescription();
            ConsoleOut.println("The Patch Description is: " + patchDescription);
        }
        catch (final Exception exp) {
            ConsoleOut.println("The format of the specified file is not supported.");
            return false;
        }
        finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            }
            catch (final IOException ioe) {
                UpdateManager.out.log(Level.SEVERE, ioe.getMessage(), ioe);
            }
        }
        return true;
    }
    
    private String[] mergeFPVersions(String[] fpVersions, final String[] indfpVersions) {
        if (indfpVersions != null) {
            if (fpVersions == null) {
                fpVersions = indfpVersions;
            }
            else {
                for (int inLen = indfpVersions.length, g = 0; g < inLen; ++g) {
                    final int leng = fpVersions.length;
                    final String[] tmp = new String[leng + 1];
                    System.arraycopy(fpVersions, 0, tmp, 0, leng);
                    tmp[leng] = indfpVersions[g];
                    fpVersions = tmp;
                }
            }
        }
        return fpVersions;
    }
    
    public ArrayList getPPMDetails(final String productName, final String productVersion, final String patchVersion, final String module, String[] fpVersions, final String[] indfpVersions, final int status) {
        fpVersions = this.mergeFPVersions(fpVersions, indfpVersions);
        if (!productName.equals(UpdateManager.confProductName)) {
            return null;
        }
        final String currentVersion = patchVersion;
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        String[] spVersions = null;
        String[] curfpVersions = null;
        VersionProfile vProfile = null;
        if (status == 3) {
            if (new File(specsPath).exists()) {
                vProfile = VersionProfile.getInstance();
                vProfile.readDocument(specsPath, false, false);
                spVersions = vProfile.getTheVersions();
                curfpVersions = vProfile.getTheFPVersions();
            }
            String[] currentFPVersions = this.getFeaturePackVersions(this.homeDir);
            final String[] currentIndFPVersions = this.getIndependentFeaturePackVersions(this.homeDir);
            currentFPVersions = this.mergeFPVersions(currentFPVersions, currentIndFPVersions);
            final String currentSP = getCurrentServicePackVersion();
            return this.getVersionsToUninstall(currentSP, spVersions, currentFPVersions, currentVersion, fpVersions);
        }
        if (!new File(specsPath).exists()) {
            return null;
        }
        vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        spVersions = vProfile.getTheVersions();
        curfpVersions = vProfile.getTheFPVersions();
        if (spVersions == null && curfpVersions == null) {
            return null;
        }
        if (curfpVersions != null) {
            String[] modArray = null;
            final int leng = curfpVersions.length;
            int inc = 0;
            modArray = new String[leng * 2];
            for (final String vers : curfpVersions) {
                final String dispVer = vProfile.getTheAdditionalDetail(vers, "DisplayName");
                modArray[inc] = vers;
                modArray[inc + 1] = dispVer;
                inc += 2;
            }
            curfpVersions = modArray;
        }
        ArrayList versionList = new ArrayList();
        if (spVersions != null) {
            int i;
            final int size = i = spVersions.length;
            while (i > 0) {
                String version = spVersions[i - 1];
                String contentType = vProfile.getTheAdditionalDetail(version, "ContentType");
                if (contentType == null || contentType.equals("")) {
                    contentType = "Consolidated";
                }
                final String patchName = vProfile.getTheAdditionalDetail(version, "PatchName");
                final String[] obj = vProfile.getTheContext(version);
                final ArrayList contextList = new ArrayList();
                for (int j = 0; j < obj.length; ++j) {
                    contextList.add(obj[j]);
                }
                final String[] versionInfo = this.readCurrentInfFile(contextList, module, version, patchName);
                if (currentVersion != null && version.equals(currentVersion)) {
                    if (!contentType.equalsIgnoreCase("Consolidated")) {
                        int k;
                        for (int csize = k = spVersions.length; k > 0; --k) {
                            final String cversion = spVersions[k - 1];
                            String ct = vProfile.getTheAdditionalDetail(cversion, "ContentType");
                            if (ct == null || ct.equals("")) {
                                ct = "Consolidated";
                            }
                            if (ct.equalsIgnoreCase("Consolidated")) {
                                version = cversion;
                                break;
                            }
                        }
                    }
                    if (curfpVersions != null) {
                        versionList = this.getFPDetails(vProfile, version, versionList, module, fpVersions);
                        break;
                    }
                    break;
                }
                else {
                    if (contentType.equalsIgnoreCase("Consolidated")) {
                        versionList = this.getFPDetails(vProfile, version, versionList, module, fpVersions);
                        versionList.add(versionInfo);
                        versionList = this.checkForNC(vProfile, currentVersion, versionList, module);
                        break;
                    }
                    versionList = this.getFPDetails(vProfile, version, versionList, module, fpVersions);
                    versionList.add(versionInfo);
                    --i;
                }
            }
            final String[] indFP = this.getIndependentFeaturePackVersions(this.homeDir);
            if (indFP != null) {
                String[] indepFP = null;
                if (indfpVersions != null) {
                    indepFP = this.getFPVersions(indFP, indfpVersions);
                }
                else {
                    indepFP = indFP;
                }
                for (int siz = indepFP.length, m = 0; m < siz; m += 2) {
                    final String ver = indepFP[m];
                    final boolean bool = this.isHigherVersionFPPresent(ver, curfpVersions);
                    if (!bool) {
                        final String patchName2 = vProfile.getTheAdditionalDetail(ver, "PatchName");
                        final String[] obj2 = vProfile.getTheContext(ver);
                        final ArrayList contextList2 = new ArrayList();
                        for (int j2 = 0; j2 < obj2.length; ++j2) {
                            contextList2.add(obj2[j2]);
                        }
                        final String[] versionInfo2 = this.readCurrentInfFile(contextList2, module, ver, patchName2);
                        versionList.add(versionInfo2);
                    }
                }
            }
        }
        else if (curfpVersions != null) {
            versionList = this.getFPsToDownload(curfpVersions, fpVersions, vProfile, versionList, module);
        }
        return versionList;
    }
    
    private boolean isHigherVersionFPPresent(final String version, final String[] curfpVersions) {
        boolean isPresent = false;
        final String fpName = getFeaturePackInfo(UpdateManagerUtil.getHomeDirectory(), version, "FeatureName");
        for (int l = 0; l < curfpVersions.length; l += 2) {
            final String vers = curfpVersions[l];
            final String name = getFeaturePackInfo(UpdateManagerUtil.getHomeDirectory(), vers, "FeatureName");
            if (name.equals(fpName)) {
                final VersionChecker vChecker = new VersionChecker();
                final String first = version.substring(version.lastIndexOf("-") + 1);
                final String second = vers.substring(vers.lastIndexOf("-") + 1);
                if (!first.equals(second)) {
                    final int type = vChecker.checkGreater(first, second);
                    if (type == 1) {
                        isPresent = true;
                    }
                }
            }
        }
        return isPresent;
    }
    
    private ArrayList getVersionsToUninstall(final String currentSP, final String[] currentSPVersions, final String[] currentFPVersions, final String remoteSPVersion, final String[] remoteFPVersions) {
        ArrayList list = new ArrayList();
        if (currentSP == null) {
            if (remoteSPVersion != null) {
                list = this.getVersionList(remoteFPVersions, currentFPVersions, list);
                list.add("uninstallall");
            }
            else {
                list = this.getVersionList(remoteFPVersions, currentFPVersions, list);
            }
            return list;
        }
        if (currentSP.equals(remoteSPVersion)) {
            list = this.getVersionList(remoteFPVersions, currentFPVersions, list);
        }
        else {
            list = this.getVersionList(remoteFPVersions, currentFPVersions, list);
            list.add(currentSP);
        }
        return list;
    }
    
    private ArrayList getVersionList(final String[] remoteFPVersions, final String[] currentFPVersions, final ArrayList list) {
        final String[] fpVers = this.getFPVersions(remoteFPVersions, currentFPVersions);
        if (fpVers != null) {
            for (int k = 0; k < fpVers.length; k += 2) {
                list.add("FP " + fpVers[k + 1]);
            }
        }
        return list;
    }
    
    private ArrayList getFPsToDownload(String[] curfpVersions, final String[] fpVersions, final VersionProfile vProfile, final ArrayList versionList, final String module) {
        if (fpVersions != null) {
            curfpVersions = this.getFPVersions(curfpVersions, fpVersions);
        }
        int j;
        for (int len = j = curfpVersions.length; j > 0; j -= 2) {
            final String fpv = curfpVersions[j - 2];
            final String[] fobj = vProfile.getTheContext(fpv);
            final ArrayList fpContextList = new ArrayList();
            for (int l = 0; l < fobj.length; ++l) {
                fpContextList.add(fobj[l]);
            }
            final String fpPatchName = vProfile.getTheAdditionalDetail(fpv, "PatchName");
            final String[] fpVersionInfo = this.readCurrentInfFile(fpContextList, module, fpv, fpPatchName);
            versionList.add(fpVersionInfo);
        }
        return versionList;
    }
    
    private ArrayList getFPDetails(final VersionProfile vProfile, final String spVersion, final ArrayList versionList, final String module, final String[] remotefpVersions) {
        final String[] fpVersions = vProfile.getTheFPVersions();
        if (fpVersions == null) {
            return versionList;
        }
        final String[] trunVersionArray = { spVersion.substring(spVersion.lastIndexOf("-") + 1) };
        final int len = fpVersions.length;
        final ArrayList newList = new ArrayList();
        for (final String fpVer : fpVersions) {
            final boolean isPresent = this.isVersionPresent(remotefpVersions, fpVer);
            if (!isPresent) {
                final FeatureVersionComp fvc = vProfile.getVersionCompatibility(fpVer);
                if (fvc != null) {
                    final String patchOption = fvc.getCompPatchOption();
                    final String patchVersion = fvc.getCompPatchVersion();
                    if (patchOption != null) {
                        if (patchVersion != null) {
                            final VersionChecker vChecker = new VersionChecker();
                            final int opt = CommonUtil.parseOption(patchOption);
                            final boolean status = vChecker.checkVersionCompatible(patchVersion, trunVersionArray, opt);
                            if (status) {
                                final String patchName = vProfile.getTheAdditionalDetail(fpVer, "PatchName");
                                final String[] obj = vProfile.getTheContext(fpVer);
                                final ArrayList contextList = new ArrayList();
                                for (int j = 0; j < obj.length; ++j) {
                                    contextList.add(obj[j]);
                                }
                                final String[] versionInfo = this.readCurrentInfFile(contextList, module, fpVer, patchName);
                                newList.add(versionInfo);
                            }
                        }
                    }
                }
            }
        }
        if (!newList.isEmpty()) {
            int k;
            for (int s = k = newList.size(); k > 0; --k) {
                versionList.add(newList.get(k - 1));
            }
        }
        return versionList;
    }
    
    private ArrayList checkForNC(final VersionProfile vProfile, final String currentVersion, final ArrayList versionList, final String module) {
        if (currentVersion == null) {
            return versionList;
        }
        final String[] spVersions = vProfile.getTheVersions();
        final int size = spVersions.length;
        int index = 0;
        for (int j = 0; j < size; ++j) {
            final String version = spVersions[j];
            if (version.equals(currentVersion)) {
                index = j;
                break;
            }
        }
        final ArrayList newList = new ArrayList();
        for (int i = index + 1; i < size; ++i) {
            final String version2 = spVersions[i];
            final String contentType = vProfile.getTheAdditionalDetail(version2, "ContentType");
            if (contentType.equalsIgnoreCase("Non-Consolidated")) {
                final String patchName = vProfile.getTheAdditionalDetail(version2, "PatchName");
                final String[] obj = vProfile.getTheContext(version2);
                final ArrayList contextList = new ArrayList();
                for (int k = 0; k < obj.length; ++k) {
                    contextList.add(obj[k]);
                }
                final String[] versionInfo = this.readCurrentInfFile(contextList, module, version2, patchName);
                newList.add(versionInfo);
            }
            else if (contentType.equalsIgnoreCase("Consolidated")) {
                break;
            }
        }
        if (!newList.isEmpty()) {
            int l;
            for (int s = l = newList.size(); l > 0; --l) {
                versionList.add(newList.get(l - 1));
            }
        }
        return versionList;
    }
    
    public String getVersionToRevert(final String ppmName) {
        final String installDir = UpdateManagerUtil.getHomeDirectory();
        final String specsPath = installDir + File.separator + "Patch" + File.separator + "specs.xml";
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        String spVersion = null;
        final String[] vect = vProfile.getTheVersions();
        if (vect == null) {
            return null;
        }
        for (int len = vect.length, l = 0; l < len; ++l) {
            final String oriversion = vect[l];
            final String dName = vProfile.getTheAdditionalDetail(oriversion, "PatchName");
            if (ppmName == null) {
                spVersion = oriversion;
                spVersion = this.getDisplayName(vProfile, spVersion);
                break;
            }
            if (dName.equals(ppmName)) {
                spVersion = vect[l + 1];
                spVersion = this.getDisplayName(vProfile, spVersion);
                break;
            }
        }
        return spVersion;
    }
    
    private String getDisplayName(final VersionProfile vProfile, String spVersion) {
        final String dispName = vProfile.getTheAdditionalDetail(spVersion, "DisplayName");
        if (dispName != null && !dispName.trim().equals("")) {
            spVersion = dispName;
        }
        return spVersion;
    }
    
    public static String[] getAllServicePackVersions(final String home) {
        final String specsPath = home + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] ver = vProfile.getTheVersions();
        if (ver != null) {
            return ver;
        }
        return null;
    }
    
    public static String[] getAllFeaturePackVersions(final String home) {
        final String specsPath = home + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] ver = vProfile.getTheFPVersions();
        if (ver != null) {
            return ver;
        }
        return null;
    }
    
    public static String getCurrentFeaturePackVersion(final String home) {
        final String specsPath = home + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] ver = vProfile.getTheFPVersions();
        if (ver != null) {
            final int length = ver.length;
            return ver[length - 1];
        }
        return null;
    }
    
    public static String getFeaturePackInfo(final String home, final String versionName, final String key) {
        final String specsPath = home + File.separator + "Patch" + File.separator + "specs.xml";
        final File specFile = new File(specsPath);
        if (!specFile.exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String value = vProfile.getTheAdditionalDetail(versionName, key);
        return value;
    }
    
    public String getCurrentPatchDisplayVersion() {
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] ver = vProfile.getTheVersions();
        if (ver != null) {
            final int length = ver.length;
            String spVersion = ver[length - 1];
            spVersion = this.getDisplayName(vProfile, spVersion);
            return spVersion;
        }
        return null;
    }
    
    public static Properties getCurrentPatchGeneralProperties(final String home) {
        Properties props = null;
        final String infPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "inf.xml";
        if (!new File(infPath).exists()) {
            return null;
        }
        try {
            final XmlParser xmlParser = new XmlParser(infPath);
            props = xmlParser.getXmlData().getGeneralProps();
        }
        catch (final Exception exp) {
            return null;
        }
        return props;
    }
    
    public static Properties getPatchGeneralProperties(final String home, final String versionName) {
        Properties props = null;
        final String infPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + versionName + File.separator + "inf.xml";
        if (!new File(infPath).exists()) {
            return null;
        }
        try {
            final XmlParser xmlParser = new XmlParser(infPath);
            props = xmlParser.getXmlData().getGeneralProps();
        }
        catch (final Exception exp) {
            return null;
        }
        return props;
    }
    
    public String[] getFeaturePackVersions(final String home) {
        final String specsPath = home + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        String latestSP = null;
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] ver = vProfile.getTheVersions();
        if (ver != null) {
            final int length = ver.length;
            latestSP = ver[length - 1];
        }
        String contentType = vProfile.getTheAdditionalDetail(latestSP, "ContentType");
        if (contentType == null || contentType.equals("")) {
            contentType = "Consolidated";
        }
        if (!contentType.equalsIgnoreCase("Consolidated")) {
            int k;
            for (int csize = k = ver.length; k > 0; --k) {
                final String cversion = ver[k - 1];
                String ct = vProfile.getTheAdditionalDetail(cversion, "ContentType");
                if (ct == null || ct.equals("")) {
                    ct = "Consolidated";
                }
                if (ct.equalsIgnoreCase("Consolidated")) {
                    latestSP = cversion;
                    break;
                }
            }
        }
        final String[] fpVersions = vProfile.getTheFPVersions();
        if (latestSP == null && fpVersions == null) {
            return null;
        }
        final ArrayList fpList = new ArrayList();
        if (latestSP == null || fpVersions == null) {
            return null;
        }
        final String[] trunVersionArray = { latestSP.substring(latestSP.lastIndexOf("-") + 1) };
        for (final String fpVer : fpVersions) {
            final FeatureVersionComp fvc = vProfile.getVersionCompatibility(fpVer);
            if (fvc != null) {
                final String patchOption = fvc.getCompPatchOption();
                final String patchVersion = fvc.getCompPatchVersion();
                if (patchOption != null) {
                    if (patchVersion != null) {
                        final VersionChecker vChecker = new VersionChecker();
                        final int opt = CommonUtil.parseOption(patchOption);
                        final boolean status = vChecker.checkVersionCompatible(patchVersion, trunVersionArray, opt);
                        if (status) {
                            final String displayName = vProfile.getTheAdditionalDetail(fpVer, "DisplayName");
                            fpList.add(fpVer);
                            fpList.add(displayName);
                        }
                    }
                }
            }
        }
        if (fpList.isEmpty()) {
            return null;
        }
        final int size = fpList.size();
        final String[] arr = new String[size];
        for (int j = 0; j < size; ++j) {
            arr[j] = fpList.get(j);
        }
        return arr;
    }
    
    private String getFormattedFPVersions(final String[] fpVersions) {
        final int len = fpVersions.length;
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < len; i += 2) {
            buf.append(fpVersions[i + 1]);
            if (i + 2 != len) {
                buf.append(",");
            }
        }
        return buf.toString();
    }
    
    public String[] getIndependentFeaturePackVersions(final String home) {
        final ArrayList fpList = new ArrayList();
        final String specsPath = home + File.separator + "Patch" + File.separator + "specs.xml";
        if (!new File(specsPath).exists()) {
            return null;
        }
        final VersionProfile vProfile = VersionProfile.getInstance();
        vProfile.readDocument(specsPath, false, false);
        final String[] fpVersions = vProfile.getTheFPVersions();
        if (fpVersions == null) {
            return null;
        }
        for (final String fpVer : fpVersions) {
            final String displayName = vProfile.getTheAdditionalDetail(fpVer, "DisplayName");
            final FeatureVersionComp fvc = vProfile.getVersionCompatibility(fpVer);
            if (fvc == null) {
                fpList.add(fpVer);
                fpList.add(displayName);
            }
            else {
                final String patchOption = fvc.getCompPatchOption();
                final String patchVersion = fvc.getCompPatchVersion();
                if (patchOption == null || patchVersion == null) {
                    fpList.add(fpVer);
                    fpList.add(displayName);
                }
            }
        }
        if (fpList.isEmpty()) {
            return null;
        }
        final int size = fpList.size();
        final String[] arr = new String[size];
        for (int j = 0; j < size; ++j) {
            arr[j] = fpList.get(j);
        }
        return arr;
    }
    
    private int getFPVersionsState(final String[] currentVersions, final String[] fpVersions) {
        if (currentVersions == null && fpVersions == null) {
            return 0;
        }
        if (currentVersions == null && fpVersions != null) {
            return 3;
        }
        String[] diffVersions = this.getFPVersions(currentVersions, fpVersions);
        diffVersions = this.getTheDifference(diffVersions);
        final int len = diffVersions.length;
        if (len == 0) {
            final String[] rdiffVersions = this.getFPVersions(fpVersions, currentVersions);
            final int rlen = rdiffVersions.length;
            if (rlen != 0) {
                return 3;
            }
            return 0;
        }
        else {
            if (len != 0) {
                return 2;
            }
            if (currentVersions.length < fpVersions.length) {
                return 3;
            }
            return 0;
        }
    }
    
    private String[] getTheDifference(final String[] diffVersions) {
        String[] diffArray = new String[0];
        final String[] indepFP = diffVersions;
        final VersionProfile vProfile = VersionProfile.getInstance();
        final String specsPath = UpdateManagerUtil.getHomeDirectory() + File.separator + "Patch" + File.separator + "specs.xml";
        vProfile.readDocument(specsPath, false, false);
        String[] curfpVersions = vProfile.getTheFPVersions();
        if (curfpVersions != null) {
            String[] modArray = null;
            final int leng = curfpVersions.length;
            int inc = 0;
            modArray = new String[leng * 2];
            for (final String vers : curfpVersions) {
                final String dispVer = vProfile.getTheAdditionalDetail(vers, "DisplayName");
                modArray[inc] = vers;
                modArray[inc + 1] = dispVer;
                inc += 2;
            }
            curfpVersions = modArray;
        }
        if (indepFP != null) {
            for (int siz = indepFP.length, i = 0; i < siz; i += 2) {
                final String ver = indepFP[i];
                final boolean bool = this.isHigherVersionFPPresent(ver, curfpVersions);
                if (!bool) {
                    final int leng2 = diffArray.length;
                    final String[] tmp = new String[leng2 + 2];
                    System.arraycopy(diffArray, 0, tmp, 0, leng2);
                    tmp[leng2] = indepFP[i];
                    tmp[leng2 + 1] = indepFP[i + 1];
                    diffArray = tmp;
                }
            }
        }
        return diffArray;
    }
    
    private String[] getFPVersions(final String[] currentVersions, final String[] fpVersions) {
        String[] diffArray = new String[0];
        if (currentVersions == null) {
            return diffArray;
        }
        for (int len = currentVersions.length, i = 0; i < len; i += 2) {
            final String currVer = currentVersions[i];
            final boolean isPresent = this.isVersionPresent(fpVersions, currVer);
            if (!isPresent) {
                final int leng = diffArray.length;
                final String[] tmp = new String[leng + 2];
                System.arraycopy(diffArray, 0, tmp, 0, leng);
                tmp[leng] = currentVersions[i];
                tmp[leng + 1] = currentVersions[i + 1];
                diffArray = tmp;
            }
        }
        return diffArray;
    }
    
    private boolean isVersionPresent(final String[] fpVersions, final String version) {
        if (fpVersions == null) {
            return false;
        }
        for (int len = fpVersions.length, i = 0; i < len; i += 2) {
            final String ver = fpVersions[i];
            if (ver.equals(version)) {
                return true;
            }
        }
        return false;
    }
    
    public static void initLog(final String homeDir, final String updateConfPath) throws Exception {
        if (System.getProperty("log.enabled", "false").equals("false")) {
            final UpdateManagerParser logParse = new UpdateManagerParser(homeDir + File.separator + updateConfPath + File.separator + "update_conf.xml");
            final Properties logFileProp = logParse.getGeneralProps();
            String logFilePath = homeDir + File.separator + "logs";
            String level = "INFO";
            String maxLimit = "10000000";
            String fileCount = "10";
            String fileName = "updatemanagerlog%g.txt";
            String logFile = null;
            String formatterClass = "com.adventnet.tools.update.installer.log.LogFormatter";
            if (logFileProp.getProperty("LogsDirectory") != null) {
                logFilePath = homeDir + File.separator + logFileProp.getProperty("LogsDirectory");
            }
            new File(logFilePath).mkdirs();
            if (logFileProp.getProperty("FileName") != null) {
                fileName = logFileProp.getProperty("FileName");
                if (fileName.equals("updatemgrlog.txt")) {
                    fileName = "updatemanagerlog%g.txt";
                }
            }
            if (logFile == null) {
                logFile = logFilePath + File.separator + fileName;
            }
            if (logFileProp.getProperty("Level") != null) {
                level = logFileProp.getProperty("Level");
            }
            if (logFileProp.getProperty("MaxLimit") != null) {
                maxLimit = logFileProp.getProperty("MaxLimit");
            }
            if (logFileProp.getProperty("FileCount") != null) {
                fileCount = logFileProp.getProperty("FileCount");
            }
            if (logFileProp.getProperty("FormtterClass") != null) {
                formatterClass = logFileProp.getProperty("FormtterClass");
            }
            final Properties props = new Properties();
            props.setProperty("handlers", "java.util.logging.FileHandler");
            props.setProperty(".level", level);
            props.setProperty("java.util.logging.FileHandler.pattern", logFile);
            props.setProperty("java.util.logging.FileHandler.limit", maxLimit);
            props.setProperty("java.util.logging.FileHandler.count", fileCount);
            props.setProperty("java.util.logging.FileHandler.formatter", formatterClass);
            FileOutputStream fos = null;
            InputStream in = null;
            try {
                fos = new FileOutputStream(logFilePath + File.separator + "upmgrlog.props");
                props.store(fos, "Logger for UpdateManager");
                in = new FileInputStream(logFilePath + File.separator + "upmgrlog.props");
                LogManager.getLogManager().readConfiguration(in);
                UpdateManager.out.log(Level.INFO, "\n\n");
            }
            finally {
                if (in != null) {
                    in.close();
                }
                if (fos != null) {
                    fos.close();
                }
                System.setProperty("log.enabled", "true");
            }
        }
        System.setOut(new DummyLogger(true));
        System.setErr(new DummyLogger(false));
    }
    
    public static String getUpdateConfPath() {
        return UpdateManager.updateFilePath;
    }
    
    public static String getValidatorClass() {
        readConfFile(UpdateManagerUtil.getHomeDirectory() + File.separator + UpdateManager.updateFilePath);
        return UpdateManager.validationClass;
    }
    
    public static void displayMessage(final String dispmessage, final String popUpTile, final int messageType) {
        if (UpdateManager.commandLineBoolean) {
            ConsoleOut.println(dispmessage);
        }
        else {
            JOptionPane.showMessageDialog(UpdateManagerUtil.getParent(), CommonUtil.getString(dispmessage), CommonUtil.getString(popUpTile), messageType);
        }
    }
    
    public static boolean isGUI() {
        return !UpdateManager.commandLineBoolean;
    }
    
    public static void setProductStats(final String key, final String value) {
        UpdateManager.productStats.put(key, value);
    }
    
    public static String getProductStat(final String key) {
        if (UpdateManager.productStats != null && UpdateManager.productStats.get(key) != null) {
            return UpdateManager.productStats.get(key);
        }
        return null;
    }
    
    public static Hashtable<String, String> getProductStats() {
        return UpdateManager.productStats;
    }
    
    public static void clearProductStats() {
        if (UpdateManager.productStats != null) {
            UpdateManager.productStats.clear();
        }
    }
    
    public static void removeProductStat(final String key) {
        if (UpdateManager.productStats != null && UpdateManager.productStats.get(key) != null) {
            UpdateManager.productStats.remove(key);
        }
    }
    
    public static String getUpdateConfProperty(final String key) {
        String updateConfPath = getUpdateConfPath();
        if (System.getProperty("server.home") != null) {
            updateConfPath = System.getProperty("server.home") + File.separator + getUpdateConfPath();
        }
        readConfFile(updateConfPath);
        if (UpdateManager.generalProps != null && UpdateManager.generalProps.getProperty(key) != null) {
            return UpdateManager.generalProps.getProperty(key);
        }
        return null;
    }
    
    protected static String getAlreadyCompletedPrePostClassName() {
        return UpdateManager.alreadyCompletedPrePostClassName;
    }
    
    public static void cleanupUnWantedInfo() {
        if (UpdateManager.alreadyCompletedPrePostClassName != null) {
            UpdateManager.alreadyCompletedPrePostClassName = null;
        }
        if (!UpdateManager.performPreValidation) {
            UpdateManager.performPreValidation = true;
        }
    }
    
    public static int getSuccessfullyAppliedPatchesCount() {
        if (UpdateManager.alreadyAppliedPatchesCount > 0) {
            return UpdateManager.alreadyAppliedPatchesCount + UpdateManager.successfullyAppliedPatchesCount;
        }
        return UpdateManager.successfullyAppliedPatchesCount;
    }
    
    public static boolean isInvokedForAutoApplyOfPatches() {
        return UpdateManager.isInvokedForAutoApplyOfPatches;
    }
    
    public static boolean isInvokedFromAutoUpgrade() {
        return UpdateManager.isInvokedFromAutoUpgrade;
    }
    
    public static String patchesCompatibilityVerifierImplementationClass() {
        return UpdateManager.patchesCompatibilityVerifier_implementationClass;
    }
    
    public static InstanceConfig getInstanceConfig() {
        if (UpdateManager.instanceConfig == null) {
            throw new IllegalStateException("Instance Config is not initialized");
        }
        return UpdateManager.instanceConfig;
    }
    
    static void autoImportCertificate(final String homeDir) {
        final String address = "https://www.manageengine.com/certificate/ppmsigner_publickey.crt";
        final Path certTempDir = Paths.get(homeDir, "Patch", "certTempDir");
        final Path certFileFilePath = Paths.get(certTempDir.toString(), "ppmsigner_publickey.crt");
        final String certFile = certFileFilePath.toString();
        HttpURLConnection conn = null;
        try {
            if (Files.exists(certTempDir, new LinkOption[0])) {
                FileUtil.deleteFiles(certTempDir.toString());
                UpdateManager.out.log(Level.INFO, "Certificate TempDirectory deleted inside Patch Directory :: {0}", Files.exists(certTempDir, new LinkOption[0]));
            }
            UpdateManager.out.log(Level.INFO, "Certificate Temporary directory created :: {0}", certTempDir.toFile().mkdirs());
            final ProxyProperties properties = NetworkUtil.getProxy();
            if (NetworkUtil.urlReachable(address, null)) {
                conn = NetworkUtil.getConnection(address, null);
                NetworkUtil.downloadFile(certFile, conn);
            }
            else if (properties != null && NetworkUtil.urlReachable(address, properties)) {
                conn = NetworkUtil.getConnection(address, NetworkUtil.getProxy());
                NetworkUtil.downloadFile(certFile, conn);
            }
            else {
                UpdateManager.out.info("Net Connection not active or the site not reachable, so skipping auto import of certificate from manage-engine site.");
            }
        }
        catch (final Exception e) {
            UpdateManager.out.log(Level.SEVERE, "Exception occurred while downloading certificate from configured URL,", e);
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            if (Files.exists(certFileFilePath, new LinkOption[0])) {
                CertificateUtil.importCertificate(certFile, homeDir + File.separator + "conf", getInstanceConfig().getKeyStorePassword());
                final String alias = CertificateUtil.getAlias(certFile, homeDir + File.separator + "conf", getInstanceConfig().getKeyStorePassword());
                UpdateManager.out.log(Level.INFO, "Certificate imported with alias {0}.", alias);
                UpdateManagerUtil.audit("Certificate imported with alias " + alias);
            }
        }
        catch (final Exception e) {
            UpdateManager.out.log(Level.WARNING, MessageConstants.IMPORT_CERTIFICATE_FAILED.getMessage(), e);
            try {
                FileUtil.deleteFiles(certTempDir.toString());
            }
            catch (final IOException e2) {
                UpdateManager.out.log(Level.INFO, "Deletion of downloaded certificate {0} failed. The certificate could be held by some other process(es)", certFileFilePath.toFile().getAbsolutePath());
            }
        }
        finally {
            try {
                FileUtil.deleteFiles(certTempDir.toString());
            }
            catch (final IOException e3) {
                UpdateManager.out.log(Level.INFO, "Deletion of downloaded certificate {0} failed. The certificate could be held by some other process(es)", certFileFilePath.toFile().getAbsolutePath());
            }
        }
    }
    
    static boolean isPreValidationRequired() {
        return UpdateManager.performPreValidation;
    }
    
    public static void setIsInvokedFromAutoApplyOfPatches(final boolean value) {
        UpdateManager.isInvokedForAutoApplyOfPatches = value;
    }
    
    public static int getAlreadyAppliedPatchesCount() {
        return UpdateManager.alreadyAppliedPatchesCount;
    }
    
    public static void incrementSuccessfullyAppliedPatchesCount() {
        ++UpdateManager.successfullyAppliedPatchesCount;
    }
    
    public static boolean isPatchAppliedSuccessfully(final Path patchFilePath) {
        final XmlData infXmlData = UpdateManagerUtil.getInfXmlData(patchFilePath.toString());
        final ArrayList contexts = new ArrayList();
        final Hashtable contextTable = infXmlData.getContextTable();
        final Enumeration keys = contextTable.keys();
        while (keys.hasMoreElements()) {
            if (keys.nextElement().equals("NoContext")) {
                contexts.add("NoContext");
            }
        }
        if (contexts.isEmpty()) {
            contexts.add(UpdateManager.confSubProductName);
        }
        final ApplyPatch applyPatch = new ApplyPatch(contexts, UpdateManagerUtil.getHomeDirectory(), infXmlData.getPatchVersion(), null, isGUI(), null);
        return applyPatch.isPatchAlreadyInstalled();
    }
    
    public static String getPatchPath() {
        return UpdateManager.patchPath;
    }
    
    public static void setPatchPath(final String patchPath) {
        UpdateManager.patchPath = patchPath;
    }
    
    static {
        UpdateManager.out = Logger.getLogger(UpdateManager.class.getName());
        UpdateManager.confProductName = null;
        UpdateManager.confProductVersion = null;
        UpdateManager.confSubProductName = null;
        UpdateManager.helpXmlFilePath = null;
        UpdateManager.helpHtmlFilePath = null;
        UpdateManager.updateFilePath = ".";
        UpdateManager.commandLineBoolean = false;
        UpdateManager.fromMainBoolean = false;
        UpdateManager.ui = null;
        UpdateManager.language = null;
        UpdateManager.country = null;
        UpdateManager.localePropertiesFileName = "UpdateManagerResources";
        UpdateManager.po = null;
        UpdateManager.patchPath = null;
        UpdateManager.performPreValidation = true;
        UpdateManager.isInvokedForAutoApplyOfPatches = false;
        UpdateManager.validationClass = null;
        UpdateManager.alreadyCompletedPrePostClassName = null;
        UpdateManager.ustate = null;
        UpdateManager.productStats = null;
        UpdateManager.generalProps = null;
        UpdateManager.alreadyAppliedPatchesCount = 0;
        UpdateManager.successfullyAppliedPatchesCount = 0;
        UpdateManager.rotateKeys = false;
        UpdateManager.isInvokedFromAutoUpgrade = false;
        UpdateManager.patchesCompatibilityVerifier_implementationClass = null;
    }
    
    private static class DummyLogger extends PrintStream
    {
        Logger logger;
        
        public DummyLogger(final boolean isSysOut) {
            super(System.err);
            this.logger = null;
            if (isSysOut) {
                this.logger = Logger.getLogger("SYSOUT");
            }
            else {
                this.logger = Logger.getLogger("SYSERR");
            }
        }
        
        @Override
        public void println(final String message) {
            this.log(message);
        }
        
        @Override
        public void println(final Object message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final long message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final int message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final float message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final double message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final char[] message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final char message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void println(final boolean message) {
            this.log(message ? "true" : "false");
        }
        
        @Override
        public void println() {
        }
        
        @Override
        public void print(final String message) {
            this.log(message);
        }
        
        @Override
        public void print(final Object message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final long message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final int message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final float message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final double message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final char[] message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final char message) {
            this.log(String.valueOf(message));
        }
        
        @Override
        public void print(final boolean message) {
            this.log(message ? "true" : "false");
        }
        
        @Override
        public void flush() {
        }
        
        @Override
        public void close() {
        }
        
        @Override
        public boolean checkError() {
            return false;
        }
        
        @Override
        protected void setError() {
        }
        
        @Override
        public void write(final int b) {
            this.log(String.valueOf(b));
        }
        
        @Override
        public void write(final byte[] buf, final int off, final int len) {
            this.log(new String(buf, off, len));
        }
        
        private void log(final String logMessage) {
            this.logger.log(Level.INFO, logMessage);
        }
    }
}
