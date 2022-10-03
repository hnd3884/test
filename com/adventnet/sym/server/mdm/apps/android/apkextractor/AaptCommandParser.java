package com.adventnet.sym.server.mdm.apps.android.apkextractor;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.List;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Level;
import java.util.HashMap;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.ArrayList;
import java.io.File;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.regex.Pattern;

public class AaptCommandParser
{
    private Pattern packageNamePattern;
    private Pattern packageLabelPattern;
    private Pattern versionNamePattern;
    private Pattern permissionsPattern;
    private Pattern iconPattern;
    private Pattern minSdkPattern;
    private Pattern targetSdkPattern;
    private Pattern versionCodePattern;
    private Pattern supportedScreensPattern;
    private Pattern signURLPattern;
    private String packageIdentifier;
    private String versionName;
    private String versionCode;
    private String minSdkVersion;
    private String targetSdkVersion;
    private String packageLabel;
    private String iconURL;
    private String signURL;
    private JSONObject icon;
    private JSONArray normalPermissions;
    private JSONArray dangerousPermissions;
    private JSONArray supportedScreensList;
    private String tempFolderPath;
    private String aaptFilePath;
    private String apkPath;
    private String folderPath;
    private String fileListPath;
    private static Logger logger;
    
    public AaptCommandParser(final String apkPath, final String tempFolderPath, final String folderPath) {
        this.packageNamePattern = Pattern.compile("package: name='([a-zA-Z0-9\\._]+)'");
        this.packageLabelPattern = Pattern.compile("application-label[a-z-]*:'(.+?)'.*$");
        this.versionNamePattern = Pattern.compile("versionName='(.+?)'.*$");
        this.permissionsPattern = Pattern.compile("uses-permission: ?(name=)?'(android.permission.[A-Z_0-9]+)'");
        this.iconPattern = Pattern.compile("icon='(.+?)'.*$");
        this.minSdkPattern = Pattern.compile("sdkVersion:'([0-9]+)'");
        this.targetSdkPattern = Pattern.compile("targetSdkVersion:'([0-9]+)'");
        this.versionCodePattern = Pattern.compile("versionCode='([0-9]+)'");
        this.supportedScreensPattern = Pattern.compile("supports-screens:([ a-zA-Z']+)");
        this.signURLPattern = Pattern.compile("META-INF/[a-zA-Z0-9_]+.[D|R]SA");
        this.packageIdentifier = null;
        this.versionName = null;
        this.versionCode = null;
        this.minSdkVersion = null;
        this.targetSdkVersion = null;
        this.packageLabel = null;
        this.iconURL = null;
        this.signURL = null;
        this.icon = new JSONObject();
        this.normalPermissions = new JSONArray();
        this.dangerousPermissions = new JSONArray();
        this.supportedScreensList = new JSONArray();
        this.tempFolderPath = null;
        this.aaptFilePath = null;
        this.apkPath = null;
        this.fileListPath = null;
        this.apkPath = apkPath;
        this.tempFolderPath = tempFolderPath;
        this.folderPath = folderPath;
    }
    
    public void parse() throws Exception {
        AaptCommandParser.logger.info("Parsing aapt output");
        this.aaptFilePath = this.tempFolderPath + File.separator + "aaptdump.txt";
        this.fileListPath = this.tempFolderPath + File.separator + "filelist.txt";
        this.packageIdentifier = ApkExtractionUtilities.parseSingleLine(this.packageNamePattern, this.aaptFilePath);
        this.versionName = ApkExtractionUtilities.parseSingleLine(this.versionNamePattern, this.aaptFilePath);
        this.iconURL = ApkExtractionUtilities.parseSingleLine(this.iconPattern, this.aaptFilePath);
        this.packageLabel = ApkExtractionUtilities.parseSingleLine(this.packageLabelPattern, this.aaptFilePath);
        this.targetSdkVersion = ApkExtractionUtilities.parseSingleLine(this.targetSdkPattern, this.aaptFilePath);
        this.minSdkVersion = ApkExtractionUtilities.parseSingleLine(this.minSdkPattern, this.aaptFilePath);
        this.versionCode = ApkExtractionUtilities.parseSingleLine(this.versionCodePattern, this.aaptFilePath);
        this.signURL = ApkExtractionUtilities.parseSingleLine(this.signURLPattern, this.fileListPath, 0);
        AaptCommandParser.logger.info("Parsing icon file");
        if (this.iconURL != null && this.iconURL.endsWith(".xml")) {
            AaptCommandParser.logger.info("Icon is defined in xml");
            final Pattern attributePatern = Pattern.compile("[ ]+A:.+android:([a-zA-Z]+)[0-9a-fA-FxX()]+=@([0-9a-fA-FxX]*)");
            final Pattern configPatern = Pattern.compile("(ldpi|mdpi|hdpi|xhdpi|xxhdpi|xxxhdpi|anydpi)");
            final Pattern resPatern = Pattern.compile("[ a-z0-9()]+\"(.*)\"");
            final String manifestTxtPath = this.tempFolderPath + File.separator + "AndroidManifest.txt";
            final List<String> dumpCommand = new ArrayList<String>();
            dumpCommand.add(ApkExtractionUtilities.getAaptFilePath());
            dumpCommand.addAll(ApkExtractionUtilities.Commands.getAAPT_DUMP_XMLTREE());
            dumpCommand.add(this.apkPath);
            dumpCommand.add("AndroidManifest.xml");
            ApkExtractionUtilities.execute(dumpCommand, new File(manifestTxtPath));
            String resourceId = null;
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileReader = new FileReader(manifestTxtPath);
                bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("E: application")) {
                        while ((line = bufferedReader.readLine()) != null && line.matches("[ ]+A:.+android:([a-zA-Z]+)[0-9a-fA-FxX()]+=@([0-9a-fA-FxX]*)")) {
                            if (line.contains("icon(")) {
                                final Matcher m = attributePatern.matcher(line);
                                if (!m.find()) {
                                    continue;
                                }
                                resourceId = m.group(2);
                            }
                        }
                        break;
                    }
                }
                bufferedReader.close();
                fileReader = new FileReader(this.tempFolderPath + File.separator + "resources.txt");
                bufferedReader = new BufferedReader(fileReader);
                String lastLine = "";
                final HashMap<String, String> iconConfigMap = new HashMap<String, String>();
                if (resourceId != null) {
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.contains(resourceId)) {
                            final String temp = bufferedReader.readLine();
                            Matcher i = resPatern.matcher(temp);
                            if (i.find()) {
                                final String resourceString = i.group(1);
                                i = configPatern.matcher(lastLine);
                                if (i.find()) {
                                    iconConfigMap.put(i.group(0), resourceString);
                                }
                            }
                        }
                        lastLine = line;
                    }
                    final String bestIcon = this.getBestIconPath(iconConfigMap);
                    if (bestIcon != null) {
                        final String extrURLfg = ApkExtractionUtilities.unzip(this.apkPath, bestIcon, this.folderPath, 1);
                        this.icon.put("icon_path", (Object)extrURLfg);
                    }
                }
                else {
                    AaptCommandParser.logger.log(Level.INFO, "Unable to find icon image");
                }
            }
            catch (final IAMSecurityException e) {
                AaptCommandParser.logger.log(Level.WARNING, "zip failed sanitization for icon extraction. Harmless for now");
            }
            catch (final Exception e2) {
                AaptCommandParser.logger.log(Level.WARNING, "Exception while fetching the icon", e2);
            }
            finally {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        }
        else {
            try {
                AaptCommandParser.logger.info("Icon file is simple file");
                final String extrURLicon = ApkExtractionUtilities.unzip(this.apkPath, this.iconURL, this.folderPath, 1);
                this.icon.put("icon_path", (Object)extrURLicon);
            }
            catch (final IAMSecurityException e3) {
                AaptCommandParser.logger.log(Level.WARNING, "apk failed sanity check for icon extraction. Harmless for now");
            }
            catch (final Exception e4) {
                AaptCommandParser.logger.log(Level.WARNING, "Exception while fetching the icon", e4);
            }
        }
        AaptCommandParser.logger.info("Extracted icon url" + this.icon);
        final String supportedScreens = ApkExtractionUtilities.parseSingleLine(this.supportedScreensPattern, this.aaptFilePath);
        if (supportedScreens.contains("'large'")) {
            this.supportedScreensList.put((Object)"Phone");
        }
        if (supportedScreens.contains("'xlarge'")) {
            this.supportedScreensList.put((Object)"Tablet");
        }
        AaptCommandParser.logger.info("Parsing permissions");
        final ArrayList<String> permissions = ApkExtractionUtilities.parseMultiLines(this.permissionsPattern, this.aaptFilePath, 2);
        for (final String permission : permissions) {
            if (isDangerousPermission(permission)) {
                this.dangerousPermissions.put((Object)permission);
            }
            else {
                this.normalPermissions.put((Object)permission);
            }
        }
        AaptCommandParser.logger.info("Parsed aapt output");
    }
    
    private String getBestIconPath(final HashMap<String, String> configMap) {
        AaptCommandParser.logger.log(Level.INFO, "Configs Map {0}", configMap);
        if (configMap.containsKey("xxxhdpi")) {
            return configMap.get("xxxhdpi");
        }
        if (configMap.containsKey("xxhdpi")) {
            return configMap.get("xxhdpi");
        }
        if (configMap.containsKey("xhdpi")) {
            return configMap.get("xhdpi");
        }
        if (configMap.containsKey("hdpi")) {
            return configMap.get("hdpi");
        }
        if (configMap.containsKey("mdpi")) {
            return configMap.get("mdpi");
        }
        if (configMap.containsKey("ldpi")) {
            return configMap.get("ldpi");
        }
        if (configMap.containsKey("anydpi")) {
            return null;
        }
        return null;
    }
    
    public static boolean isDangerousPermission(final String permission) {
        for (final String dp : ApkExtractionUtilities.Defaults.DANGEROUS_PERMISSIONS) {
            if (dp.equals(permission)) {
                return true;
            }
        }
        return false;
    }
    
    public String getPackageLabel() {
        return this.packageLabel;
    }
    
    public String getVersionName() {
        return this.versionName;
    }
    
    public String getVersionCode() {
        return this.versionCode;
    }
    
    public String getPackageIdentifier() {
        return this.packageIdentifier;
    }
    
    public String getTargetSdkVersion() {
        return this.targetSdkVersion;
    }
    
    public String getMinSdkVersion() {
        return this.minSdkVersion;
    }
    
    public JSONArray getNormalPermissions() {
        return this.normalPermissions;
    }
    
    public JSONArray getDangerousPermissions() {
        return this.dangerousPermissions;
    }
    
    public JSONArray getSupportedScreens() {
        return this.supportedScreensList;
    }
    
    public JSONObject getIcon() {
        return this.icon;
    }
    
    public String getIconURL() {
        return this.iconURL;
    }
    
    public String getSignURL() {
        return this.signURL;
    }
    
    public void setSignURL(final String signURL) {
        this.signURL = signURL;
    }
    
    static {
        AaptCommandParser.logger = ApkExtractionUtilities.getLogger();
    }
}
