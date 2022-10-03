package com.adventnet.sym.server.mdm.apps.android.apkextractor;

import java.io.IOException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Level;
import org.json.JSONException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.io.File;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class ApkExtractionHandler
{
    private JSONObject signingData;
    private JSONArray dangerousPermissions;
    private JSONArray restrictions;
    private JSONArray supportedScreens;
    private JSONObject permissions;
    private JSONObject icon;
    private String packageLabel;
    private String versionCode;
    private String packageIdentifier;
    private String minSdkVersion;
    private String targetSdkVersion;
    private String versionName;
    private boolean debugEnabled;
    private String signURL;
    private JSONObject keyToolSignData;
    private String tempFolderPath;
    private String folderPath;
    private String apkSourcePath;
    private static Logger logger;
    
    public ApkExtractionHandler(final String apkSourcePath) {
        this.signingData = new JSONObject();
        this.dangerousPermissions = new JSONArray();
        this.restrictions = new JSONArray();
        this.supportedScreens = new JSONArray();
        this.permissions = new JSONObject();
        this.icon = new JSONObject();
        this.packageLabel = null;
        this.versionCode = null;
        this.packageIdentifier = null;
        this.minSdkVersion = null;
        this.targetSdkVersion = null;
        this.versionName = null;
        this.debugEnabled = false;
        this.signURL = null;
        this.keyToolSignData = new JSONObject();
        this.apkSourcePath = apkSourcePath;
        final File apk = new File(apkSourcePath);
        final String folderName = apk.getName().replaceAll("[ \\.]", "-");
        this.tempFolderPath = apk.getParent() + File.separator + "temp";
        this.folderPath = apk.getParent() + File.separator + folderName;
    }
    
    public void initialize() throws Exception {
        final File apkFolder = new File(this.folderPath);
        final File tempFolder = new File(this.tempFolderPath);
        if (!apkFolder.exists()) {
            apkFolder.mkdir();
        }
        else {
            ApkExtractionHandler.logger.info("apk folder already exists");
        }
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }
        else {
            ApkExtractionHandler.logger.info("temp folder already exists");
        }
    }
    
    public void dumpManifest() throws Exception {
        final String manifestTxtPath = this.tempFolderPath + File.separator + "AndroidManifest.txt";
        final List<String> dumpCommand = new ArrayList<String>();
        dumpCommand.add(ApkExtractionUtilities.getAaptFilePath());
        dumpCommand.addAll(ApkExtractionUtilities.Commands.getAAPT_DUMP_XMLTREE());
        dumpCommand.add(this.apkSourcePath);
        dumpCommand.add("AndroidManifest.xml");
        ApkExtractionUtilities.execute(dumpCommand, new File(manifestTxtPath));
        ApkExtractionHandler.logger.info("Dumped Android Manifest to " + manifestTxtPath);
    }
    
    public void dumpResources() throws Exception {
        final String resourceTxtPath = this.tempFolderPath + File.separator + "resources.txt";
        final List<String> dumpCommand = new ArrayList<String>();
        dumpCommand.add(ApkExtractionUtilities.getAaptFilePath());
        dumpCommand.addAll(ApkExtractionUtilities.Commands.getDUMP_RESOURCE_COMMAND());
        dumpCommand.add(this.apkSourcePath);
        ApkExtractionUtilities.execute(dumpCommand, new File(resourceTxtPath));
        ApkExtractionHandler.logger.info("Dumped Resources to " + resourceTxtPath);
    }
    
    public void dumpAAPT() throws Exception {
        final String aaptFilePath = this.tempFolderPath + File.separator + "aaptdump.txt";
        final List<String> dumpCommand = new ArrayList<String>();
        dumpCommand.add(ApkExtractionUtilities.getAaptFilePath());
        dumpCommand.addAll(ApkExtractionUtilities.Commands.getAAPT_DUMP_BADGING());
        dumpCommand.add(this.apkSourcePath);
        ApkExtractionUtilities.execute(dumpCommand, new File(aaptFilePath));
        ApkExtractionHandler.logger.info("Dumped aapt output to " + aaptFilePath);
    }
    
    public void listResources() throws Exception {
        final String aaptFileListPath = this.tempFolderPath + File.separator + "filelist.txt";
        final List<String> dumpCommand = new ArrayList<String>();
        dumpCommand.add(ApkExtractionUtilities.getAaptFilePath());
        dumpCommand.addAll(ApkExtractionUtilities.Commands.getLIST_COMMAND());
        dumpCommand.add(this.apkSourcePath);
        ApkExtractionUtilities.execute(dumpCommand, new File(aaptFileListPath));
        ApkExtractionHandler.logger.info("File list output to" + aaptFileListPath);
    }
    
    public void getKeyToolSign() throws Exception {
        final String osName = System.getProperty("os.name");
        final String keyToolsignPath = this.tempFolderPath + File.separator + "keytoolsign.txt";
        String commandArgs = "";
        final List<String> keyToolCommand = new ArrayList<String>();
        if (osName.contains("Windows")) {
            commandArgs = MDMApiFactoryProvider.getMDMUtilAPI().getKeyToolPath();
            keyToolCommand.add(commandArgs);
            keyToolCommand.add(this.apkSourcePath);
        }
        else if (osName.contains("Linux")) {
            keyToolCommand.add("keytool");
            keyToolCommand.add("-printcert");
            keyToolCommand.add("-jarfile");
            keyToolCommand.add(this.apkSourcePath);
        }
        ApkExtractionUtilities.execute(keyToolCommand, new File(keyToolsignPath));
        ApkExtractionHandler.logger.info("File list output to" + keyToolsignPath);
    }
    
    public void parseAAPT() throws Exception {
        final AaptCommandParser aaptParser = new AaptCommandParser(this.apkSourcePath, this.tempFolderPath, this.folderPath);
        aaptParser.parse();
        this.setPackageIdentifier(aaptParser.getPackageIdentifier());
        this.setVersionName(aaptParser.getVersionName());
        this.setPackageLabel(aaptParser.getPackageLabel());
        this.setMinSdkVersion(aaptParser.getMinSdkVersion());
        this.setVersionCode(aaptParser.getVersionCode());
        this.setTargetSdkVersion(aaptParser.getTargetSdkVersion());
        this.setPermissions(aaptParser.getNormalPermissions(), aaptParser.getDangerousPermissions());
        this.setIcon(aaptParser.getIcon());
        this.setSupportedScreens(aaptParser.getSupportedScreens());
        this.setSignURL(aaptParser.getSignURL());
    }
    
    private void setPermissions(final JSONArray normalPermissions, final JSONArray dangerousPermissions) throws JSONException {
        this.permissions.put("normal", (Object)normalPermissions);
        this.permissions.put("dangerous", (Object)dangerousPermissions);
    }
    
    public void parseRestrictions() throws Exception {
        JSONArray restrictionsArray = new JSONArray();
        final ApkResourceParser resourceParser = new ApkResourceParser(this.tempFolderPath);
        resourceParser.findRestrictions();
        if (resourceParser.hasRestrictions()) {
            ApkExtractionHandler.logger.log(Level.INFO, "Found app restrictions");
            final String restrictionsFilePathId = resourceParser.getRestrictionsFilePathId();
            final String restrictionsFilePath = resourceParser.getStringResource(restrictionsFilePathId, false);
            if (restrictionsFilePath.matches("^res/xml[/a-zA-Z0-9_-]+.xml$")) {
                final String restrictionsTxtPath = this.tempFolderPath + File.separator + "restrictions.txt";
                final List<String> dumpCommand = new ArrayList<String>();
                dumpCommand.add(ApkExtractionUtilities.getAaptFilePath());
                dumpCommand.addAll(ApkExtractionUtilities.Commands.getAAPT_DUMP_XMLTREE());
                dumpCommand.add(this.apkSourcePath);
                dumpCommand.add(restrictionsFilePath);
                ApkExtractionUtilities.execute(dumpCommand, new File(restrictionsTxtPath));
                final ApkRestrictionsParser restrictionsParser = new ApkRestrictionsParser(restrictionsTxtPath, this.tempFolderPath);
                restrictionsParser.parse();
                restrictionsArray = restrictionsParser.getRestrictionsArray();
            }
            else {
                ApkExtractionHandler.logger.log(Level.WARNING, "Malicious file name for App restriction: {0} . Hence ignoring the restrictions", restrictionsFilePath);
            }
        }
        else {
            ApkExtractionHandler.logger.log(Level.INFO, "No restrictions found");
        }
        this.setRestristrictions(restrictionsArray);
    }
    
    public void parseSignature() {
        try {
            final ApkSignatureParser signatureParser = new ApkSignatureParser(this.tempFolderPath, this.apkSourcePath, this.signURL);
            signatureParser.parse();
            this.setSignatureInfo(signatureParser.getSignatureInfo());
            this.setDebugEnabled(signatureParser.isDebugEnabled());
            ApkExtractionHandler.logger.info("Parsed signatures");
        }
        catch (final IAMSecurityException e) {
            ApkExtractionHandler.logger.log(Level.WARNING, "apk failed sanity check for signature extraction. Harmless");
        }
        catch (final Exception e2) {
            ApkExtractionHandler.logger.log(Level.WARNING, "Exception when parsing signature. Harmless", e2);
        }
    }
    
    public void parseKeyToolSignature() {
        final ApkSignatureParser signatureParser = new ApkSignatureParser(this.tempFolderPath, this.apkSourcePath, this.signURL);
        try {
            this.getKeyToolSign();
            signatureParser.parseKeyToolSign();
            this.setKeyToolSignData(signatureParser.getKeyToolSignInfo());
        }
        catch (final Exception e) {
            ApkExtractionHandler.logger.log(Level.WARNING, "Exception when parsing signature. Harmless", e);
        }
    }
    
    public void deleteDumpFiles() throws IOException, Exception {
        ApiFactoryProvider.getFileAccessAPI().deleteDirectory(this.tempFolderPath);
        ApkExtractionHandler.logger.info("Deleted temporary folder");
    }
    
    private void setSignatureInfo(final JSONObject signatureInfo) throws JSONException {
        this.signingData = signatureInfo;
    }
    
    private void setSupportedScreens(final JSONArray supportedScreens) {
        this.supportedScreens = supportedScreens;
    }
    
    private void setPackageLabel(final String packageLabel) {
        this.packageLabel = packageLabel;
    }
    
    private void setVersionCode(final String versionCode) {
        this.versionCode = versionCode;
    }
    
    private void setPackageIdentifier(final String packageIdentifier) {
        this.packageIdentifier = packageIdentifier;
    }
    
    private void setMinSdkVersion(final String minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }
    
    private void setTargetSdkVersion(final String targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion;
    }
    
    private void setVersionName(final String versionName) {
        this.versionName = versionName;
    }
    
    private void setDebugEnabled(final boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }
    
    private void setIcon(final JSONObject icon) {
        this.icon = icon;
    }
    
    private void setRestristrictions(final JSONArray restrictionsArray) {
        this.restrictions = restrictionsArray;
    }
    
    public JSONObject getSigningData() {
        return this.signingData;
    }
    
    public JSONArray getDangerousPermissions() {
        return this.dangerousPermissions;
    }
    
    public JSONArray getRestrictions() {
        return this.restrictions;
    }
    
    public JSONArray getSupportedScreens() {
        return this.supportedScreens;
    }
    
    public JSONObject getPermissions() {
        return this.permissions;
    }
    
    public JSONObject getIcon() {
        return this.icon;
    }
    
    public String getPackageLabel() {
        return this.packageLabel;
    }
    
    public String getVersionCode() {
        return this.versionCode;
    }
    
    public String getPackageIdentifier() {
        return this.packageIdentifier;
    }
    
    public String getMinSdkVersion() {
        return this.minSdkVersion;
    }
    
    public String getTargetSdkVersion() {
        return this.targetSdkVersion;
    }
    
    public String getVersionName() {
        return this.versionName;
    }
    
    public boolean isDebugEnabled() {
        return this.debugEnabled;
    }
    
    public String getFolderPath() {
        return this.folderPath;
    }
    
    public String getSignURL() {
        return this.signURL;
    }
    
    public void setSignURL(final String signURL) {
        this.signURL = signURL;
    }
    
    public JSONObject getKeyToolSignData() {
        return this.keyToolSignData;
    }
    
    public void setKeyToolSignData(final JSONObject keyToolSignData) {
        this.keyToolSignData = keyToolSignData;
    }
    
    static {
        ApkExtractionHandler.logger = ApkExtractionUtilities.getLogger();
    }
}
