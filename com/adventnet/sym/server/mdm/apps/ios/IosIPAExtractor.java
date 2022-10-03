package com.adventnet.sym.server.mdm.apps.ios;

import org.json.JSONException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.apache.tika.io.FilenameUtils;
import com.dd.plist.NSString;
import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.dd.plist.BinaryPropertyListParser;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.zoho.security.api.wrapper.ZipInputStreamWrapper;
import java.io.FileInputStream;
import java.util.regex.Pattern;
import java.util.List;
import com.zoho.security.validator.zip.ZipSanitizerRule;
import org.apache.tika.Tika;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import com.dd.plist.NSDate;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import org.bouncycastle.cms.CMSSignedData;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.dd.plist.NSDictionary;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.iam.security.IAMSecurityException;
import com.adventnet.i18n.I18N;
import com.me.mdm.webclient.i18n.MDMI18N;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import java.util.Calendar;
import com.me.mdm.server.deploy.MDMMetaDataUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.apps.EnterpriseAppExtractor;

public class IosIPAExtractor extends EnterpriseAppExtractor
{
    private static final String EXPIRYDATE = "expiryDate";
    private static final String ERROR = "error";
    private static final String WARNING = "warning";
    private String appFolderName;
    static Logger logger;
    
    public IosIPAExtractor() {
        this.appFolderName = ".*\\";
    }
    
    public JSONObject extractIos(final String fileSourceDestFileName) throws Exception {
        JSONObject iapProps = null;
        IosIPAExtractor.logger.log(Level.INFO, "File Source:{0}", fileSourceDestFileName);
        iapProps = this.getAppDetails(fileSourceDestFileName);
        IosIPAExtractor.logger.log(Level.INFO, "Data to APP upload ==>{0}", iapProps);
        return iapProps;
    }
    
    @Override
    public synchronized JSONObject getAppDetails(final String ipaPath) throws Exception {
        final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
        JSONObject ipaProps = new JSONObject();
        String destPath = null;
        try {
            final File file = new File(ipaPath);
            final File ipaDirectory = new File(file.getParent());
            final String ipdDir = ipaDirectory.toString();
            final String webappsDir = MDMMetaDataUtil.getInstance().getClientDataParentDir();
            final Calendar cal = Calendar.getInstance();
            final String sourceFolder = "AppIcon_" + cal.getTimeInMillis();
            destPath = webappsDir + File.separator + "MDM" + File.separator + "apprepository" + File.separator + customerID + File.separator + "appupload" + File.separator + sourceFolder;
            final File iconFile = new File(destPath);
            iconFile.mkdirs();
            final JSONArray metaData = this.extractInfoAndPPFilesFromIPA(ipaPath, destPath);
            ipaProps = this.decryptAndValidateProvProfiles(metaData, destPath, ipaProps);
            ipaProps.put("metaData", (Object)metaData);
            ipaProps.put("metaDataLoc", (Object)destPath);
            String infoPlistPath = this.getInfoPlistPath(metaData);
            if (infoPlistPath != null) {
                infoPlistPath = destPath + File.separator + infoPlistPath;
                final String infoplistPath = this.decryptInfoPlist(infoPlistPath);
                ipaProps = this.getPropertiesFromPlist(infoplistPath, ipaProps);
                String[] extractedIcons = new String[2];
                for (int i = 0; i < extractedIcons.length; ++i) {
                    extractedIcons[i] = "";
                }
                if (ipaProps.optJSONArray("IconName") != null && ipaProps.optJSONArray("IconName").length() > 0) {
                    extractedIcons = this.extractIconsFromipa(destPath, ipaProps.optJSONArray("IconName"), ipaPath);
                }
                ipaProps.put("smallAppIcon", (Object)extractedIcons[0]);
                ipaProps.put("largeAppIcon", (Object)extractedIcons[1]);
                final JSONArray iconArray = new JSONArray();
                final JSONObject smallIconsJSON = new JSONObject();
                final JSONObject largeIconsJSON = new JSONObject();
                smallIconsJSON.put("icon_path", (Object)extractedIcons[0]);
                smallIconsJSON.put("icon_size", (Object)"small");
                largeIconsJSON.put("icon_path", (Object)extractedIcons[1]);
                largeIconsJSON.put("icon_size", (Object)"large");
                iconArray.put((Object)smallIconsJSON);
                iconArray.put((Object)largeIconsJSON);
                ipaProps.put("icon", (Object)iconArray);
                if (ipaProps.has("PackageName")) {
                    if (String.valueOf(ipaProps.get("PackageName")).trim().equalsIgnoreCase("--") || String.valueOf(ipaProps.get("PackageName")).trim().equalsIgnoreCase("")) {
                        ipaProps = new JSONObject();
                        ipaProps.put("error", (Object)"dc.mdm.app.bundle_identifier_empty");
                    }
                }
                else {
                    ipaProps.put("error", (Object)"dc.mdm.app.app_details_could_not_be_found");
                }
            }
            else {
                String errorURL = "$(mdmUrl)/kb/mdm-enterprise-app-upload-error.html?$(traceurl)&pgSrc=$(pageSource)#info-plist";
                errorURL = "\"" + MDMUtil.replaceProductUrlLoaderValuesinText(errorURL, "AppUploadPage") + "\"";
                final String error = MDMI18N.getI18Nmsg("dc.mdm.ipavalidation.INFO_PLIST_NOT_EXIST") + MDMI18N.getI18Nmsg("mdm.learn_more", new Object[] { errorURL });
                ipaProps.put("error", (Object)error);
                IosIPAExtractor.logger.log(Level.WARNING, "The info.plist file is not found in the uploaded app");
            }
        }
        catch (final IAMSecurityException se) {
            IosIPAExtractor.logger.log(Level.WARNING, "Exception in getAppDetailsFromAppFile(){0}", (Throwable)se);
            ipaProps.put("error", (Object)(I18N.getMsg("dc.mdm.app.ios_app_details_could_not_be_found", new Object[0]) + " Error:IAMSecurityError"));
        }
        catch (final Exception e) {
            IosIPAExtractor.logger.log(Level.WARNING, "Exception in getAppDetailsFromAppFile(){0}", e);
            ipaProps.put("error", (Object)"dc.mdm.app.ios_app_details_could_not_be_found");
        }
        finally {
            if (ipaProps.has("error") && !MDMStringUtils.isEmpty(destPath)) {
                IosIPAExtractor.logger.log(Level.WARNING, "Exception while parsing IPA so removing the folder :{0}", destPath);
                ApiFactoryProvider.getFileAccessAPI().deleteDirectory(destPath);
            }
        }
        IosIPAExtractor.logger.log(Level.WARNING, "ipaProps {0} ", ipaProps);
        return ipaProps;
    }
    
    public NSDictionary decryptProvProfile(final String dest) throws Exception {
        InputStream in = null;
        try {
            in = FileAccessUtil.readFileFromServer(dest);
            final CMSSignedData signedData = new CMSSignedData(in);
            final Object content = signedData.getSignedContent().getContent();
            final NSDictionary rootDict = (NSDictionary)DMSecurityUtil.parsePropertyList((byte[])content);
            return rootDict;
        }
        catch (final Exception e) {
            IosIPAExtractor.logger.log(Level.WARNING, "Exception in Decrypting Provision file decryptProvision()", e);
            throw e;
        }
        finally {
            try {
                in.close();
            }
            catch (final Exception e2) {
                IosIPAExtractor.logger.log(Level.WARNING, "Excepton in decryptProvProfile", e2);
                throw e2;
            }
        }
    }
    
    private String validateExpiry(final NSDictionary rootDict) {
        try {
            final NSDate expiryDate = (NSDate)rootDict.objectForKey("ExpirationDate");
            final Date expiryDt = expiryDate.getDate();
            final Date currentdate = new Date(System.currentTimeMillis());
            if (currentdate.compareTo(expiryDt) > 0) {
                IosIPAExtractor.logger.log(Level.INFO, "CurrentDate is after ExpiryDate-- IPA file is expired file ");
                return ((NSDictionary)rootDict.objectForKey("Entitlements")).objectForKey("application-identifier").toString();
            }
        }
        catch (final Exception e) {
            IosIPAExtractor.logger.log(Level.SEVERE, "Exception in Decrypting Provision file decryptProvision()", e);
            throw e;
        }
        return null;
    }
    
    private String getInfoPlistPath(final JSONArray metaData) {
        try {
            for (int i = 0; i < metaData.length(); ++i) {
                final JSONObject json = (JSONObject)metaData.get(i);
                if (json.opt("MAIN_FILE_LOCATION") != null) {
                    return (String)json.opt("INFO_PLIST");
                }
            }
        }
        catch (final Exception ex) {
            IosIPAExtractor.logger.log(Level.SEVERE, "Exception in getInfoPlistPath()", ex);
        }
        return null;
    }
    
    private JSONObject decryptAndValidateProvProfiles(final JSONArray metaData, final String destPath, final JSONObject ipaProps) throws Exception {
        final ArrayList expiryAppIds = new ArrayList();
        for (int i = 0; i < metaData.length(); ++i) {
            final JSONObject json = (JSONObject)metaData.get(i);
            if (json.opt("FILE_LOCATION") != null) {
                final String ppName = (String)json.get("PP");
                final NSDictionary dict = this.decryptProvProfile(destPath + File.separator + ppName);
                final String provProfileAppId = this.validateExpiry(dict);
                if (provProfileAppId != null) {
                    final NSDate expiryDate = (NSDate)dict.objectForKey("ExpirationDate");
                    final Date expiryDt = expiryDate.getDate();
                    final Long millis = expiryDt.getTime();
                    expiryAppIds.add(provProfileAppId);
                    String error = MDMI18N.getI18Nmsg("dc.mdm.ipavalidation.IPA_EXPIRED", new Object[] { MDMUtil.getDate((long)millis) });
                    String errorURL = "$(mdmUrl)/kb/mdm-enterprise-app-upload-error.html?$(traceurl)&pgSrc=$(pageSource)#app-expired";
                    errorURL = "\"" + MDMUtil.replaceProductUrlLoaderValuesinText(errorURL, "AppUploadPage") + "\"";
                    error += MDMI18N.getI18Nmsg("mdm.learn_more", new Object[] { errorURL });
                    IosIPAExtractor.logger.log(Level.WARNING, "The uploaded app is expired on {0}", MDMUtil.getDate((long)millis));
                    ipaProps.put("error", (Object)error);
                }
            }
            else if (json.opt("MAIN_FILE_LOCATION") != null) {
                final String ppName = (String)json.opt("PP");
                if (ppName != null) {
                    final NSDictionary dict = this.decryptProvProfile(destPath + File.separator + ppName);
                    final String provProfileAppId = this.validateExpiry(dict);
                    if (provProfileAppId != null) {
                        final NSDate expiryDate = (NSDate)dict.objectForKey("ExpirationDate");
                        final Date expiryDt = expiryDate.getDate();
                        final Long millis = expiryDt.getTime();
                        expiryAppIds.add(provProfileAppId);
                        String error = MDMI18N.getI18Nmsg("dc.mdm.ipavalidation.IPA_EXPIRED", new Object[] { MDMUtil.getDate((long)millis) });
                        String errorURL = "$(mdmUrl)/kb/mdm-enterprise-app-upload-error.html?$(traceurl)&pgSrc=$(pageSource)#app-expired";
                        errorURL = "\"" + MDMUtil.replaceProductUrlLoaderValuesinText(errorURL, "AppUploadPage") + "\"";
                        error += MDMI18N.getI18Nmsg("mdm.learn_more", new Object[] { errorURL });
                        IosIPAExtractor.logger.log(Level.WARNING, "The uploaded app is expired on {0}", MDMUtil.getDate((long)millis));
                        ipaProps.put("error", (Object)error);
                    }
                }
                else {
                    ipaProps.put("error", (Object)"dc.mdm.ipavalidation.PROVISION_FILE_NOT_EXIST");
                }
            }
        }
        if (!expiryAppIds.isEmpty()) {
            ipaProps.put("EXPIRED_APPIDS", (Collection)expiryAppIds);
        }
        return ipaProps;
    }
    
    private void validateIPAandSetBaseFolderPath(final String ipaPath) throws IOException, IAMSecurityException {
        ZipFile zipFile = null;
        try {
            final String contentType = new Tika().detect(new File(ipaPath));
            if (!contentType.equals("application/x-itunes-ipa")) {
                IosIPAExtractor.logger.log(Level.SEVERE, "Given content type is not application/x-itunes-ipa , detected as :{0}", contentType);
                throw new IAMSecurityException("Given content type is not application/x-itunes-ipa , detected as" + contentType);
            }
            ZipInputStreamWrapper wrapper = null;
            try {
                final List allowedExtensions = new ArrayList();
                allowedExtensions.add("plist");
                allowedExtensions.add("mobileprovision");
                allowedExtensions.add("jpeg");
                allowedExtensions.add("png");
                final ZipSanitizerRule maximumSizeRule = new ZipSanitizerRule("maximumSizeRule", "sanitize", -1L, -1, 25000, allowedExtensions, (List)null, (Pattern)null, (Pattern)null);
                wrapper = new ZipInputStreamWrapper((InputStream)new FileInputStream(ipaPath), maximumSizeRule);
            }
            catch (final IAMSecurityException se) {
                if (se.getMessage().equals("ZIPSANITIZER_INVALID_FILE_EXTENSION") || se.getMessage().equals("ZIPSANITIZER_INVALID_CONTENT_TYPE_FOUND")) {
                    IosIPAExtractor.logger.log(Level.WARNING, "Safe exception in IPA extraction due to IAMSec exception", (Throwable)se);
                }
                else {
                    if (!se.getMessage().equals("ZIPSANITIZER_FILES_SIZE_EXCEEDED") || !MDMFeatureParamsHandler.getInstance().isFeatureEnabled("IgnoreFileSizeExceededIpaValidation")) {
                        IosIPAExtractor.logger.log(Level.WARNING, "IAMSecurityException while parsing IPA", (Throwable)se);
                        throw se;
                    }
                    IosIPAExtractor.logger.log(Level.WARNING, "Safe File size Exceeded Exception in IPA Extraction due to IAMSec exception", (Throwable)se);
                }
            }
            finally {
                if (wrapper != null) {
                    wrapper.close();
                }
            }
            String filePath = "";
            zipFile = new ZipFile(new File(ipaPath));
            final Enumeration<? extends ZipEntry> zipFiles = zipFile.entries();
            while (zipFiles.hasMoreElements()) {
                filePath = ((ZipEntry)zipFiles.nextElement()).getName();
                if (filePath.contains(".app")) {
                    this.appFolderName = filePath.substring(filePath.indexOf("/") + 1, filePath.indexOf(".app"));
                    break;
                }
            }
        }
        catch (final IAMSecurityException se2) {
            IosIPAExtractor.logger.log(Level.SEVERE, "IAMSecurityException while getting ipa file", (Throwable)se2);
            throw se2;
        }
        catch (final Exception e) {
            IosIPAExtractor.logger.log(Level.SEVERE, "Exception while getting ipa file", e);
        }
        finally {
            if (zipFile != null) {
                zipFile.close();
            }
        }
    }
    
    private JSONArray restructureMetaInfo(JSONArray metaData) {
        try {
            for (int i = 0; i < metaData.length(); ++i) {
                final JSONObject json = (JSONObject)metaData.get(i);
                final String fileLocation = (String)json.opt("FILE_LOCATION");
                if (fileLocation != null && json.length() < 3) {
                    metaData = JSONUtil.getInstance().removeByPos(metaData, i);
                    --i;
                }
            }
        }
        catch (final Exception ex) {}
        return metaData;
    }
    
    private Boolean addMetaInfo(final String filePath, final String filename, final JSONArray metaData) {
        try {
            String fileLocationKey = "FILE_LOCATION";
            final String mainAppFolderPath = "payload/" + this.appFolderName + ".app";
            if (filePath.equalsIgnoreCase(mainAppFolderPath)) {
                fileLocationKey = "MAIN_FILE_LOCATION";
            }
            for (int i = 0; i < metaData.length(); ++i) {
                final JSONObject json = (JSONObject)metaData.get(i);
                final String fileLocation = (String)json.opt(fileLocationKey);
                if (fileLocation != null && fileLocation.equalsIgnoreCase(filePath)) {
                    if (filename.contains("info")) {
                        json.put("INFO_PLIST", (Object)filename);
                    }
                    else {
                        json.put("PP", (Object)filename);
                    }
                    return true;
                }
            }
            final JSONObject json2 = new JSONObject();
            json2.put(fileLocationKey, (Object)filePath);
            if (filename.contains("info")) {
                json2.put("INFO_PLIST", (Object)filename);
            }
            else {
                json2.put("PP", (Object)filename);
            }
            metaData.put((Object)json2);
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public String decryptInfoPlist(final String encrytedPath) throws Exception {
        String decrptFilePath = encrytedPath + ".xml.plist";
        final File infoPlistFile = new File(encrytedPath);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(infoPlistFile);
            final NSObject ns = BinaryPropertyListParser.parse((InputStream)fileInputStream);
            fileInputStream.close();
            FileAccessUtil.writeFileInServer(decrptFilePath, ns.toXMLPropertyList().getBytes());
        }
        catch (final Exception ex) {
            IosIPAExtractor.logger.log(Level.SEVERE, "Exception in decryptInfoPlist()", ex);
            if (!(ex instanceof Exception) || !ex.getMessage().contains("The given data is no binary property list.")) {
                throw ex;
            }
            decrptFilePath = encrytedPath;
        }
        finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return decrptFilePath;
    }
    
    private JSONArray getAppIconFileNamesFromRootDict(final NSDictionary rootDict, final String rootDictkey) {
        final JSONArray ipaIconName = new JSONArray();
        try {
            final NSDictionary CFBundleIcons = (NSDictionary)rootDict.objectForKey(rootDictkey);
            if (CFBundleIcons != null) {
                final NSDictionary CFBundlePrimaryIcon = (NSDictionary)CFBundleIcons.objectForKey("CFBundlePrimaryIcon");
                if (CFBundlePrimaryIcon != null) {
                    final NSArray CFBundleIconFiles = (NSArray)CFBundlePrimaryIcon.objectForKey("CFBundleIconFiles");
                    if (CFBundleIconFiles != null) {
                        IosIPAExtractor.logger.log(Level.WARNING, "key2 {0}", CFBundleIconFiles.objectAtIndex(0));
                        ipaIconName.put((Object)String.valueOf(CFBundleIconFiles.objectAtIndex(0)));
                        ipaIconName.put((Object)String.valueOf(CFBundleIconFiles.objectAtIndex(CFBundleIconFiles.count() - 1)));
                    }
                }
            }
        }
        catch (final Exception e) {
            IosIPAExtractor.logger.log(Level.SEVERE, "Error while getting bundle icons getAppIconFileNamesFromRootDict :  ", e);
        }
        return ipaIconName;
    }
    
    public JSONObject getPropertiesFromPlist(final String decryptedPath, final JSONObject ipaProp) {
        JSONArray smallLargeIconNamesArray = new JSONArray();
        IosIPAExtractor.logger.log(Level.WARNING, "decryptedPath {0}", decryptedPath);
        try {
            String bundleIdentifier = "--";
            String version = "--";
            String appName = null;
            String supportedDevice = "--";
            InputStream is = null;
            NSDictionary rootDict;
            try {
                is = FileAccessUtil.readFileFromServer(decryptedPath);
                rootDict = (NSDictionary)DMSecurityUtil.parsePropertyList(is);
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    }
                    catch (final IOException ex) {
                        IosIPAExtractor.logger.log(Level.SEVERE, "Exception while closing plist InputStream", ex);
                    }
                }
            }
            smallLargeIconNamesArray = this.getAppIconFileNamesFromRootDict(rootDict, "CFBundleIcons");
            try {
                final String smallIconName = smallLargeIconNamesArray.optString(0, (String)null);
                if (smallIconName == null || smallIconName.isEmpty()) {
                    IosIPAExtractor.logger.log(Level.INFO, "Small icon name is empty for CFBundleIcons , so going to try with CFBundleIcons~ipad");
                    smallLargeIconNamesArray = this.getAppIconFileNamesFromRootDict(rootDict, "CFBundleIcons~ipad");
                }
            }
            catch (final Exception ex2) {
                IosIPAExtractor.logger.log(Level.SEVERE, "Error while getting bundle icons in CFBundleIcons:  ", ex2);
            }
            final NSString nsbundle = (NSString)rootDict.objectForKey("CFBundleIdentifier");
            if (nsbundle != null) {
                bundleIdentifier = nsbundle.toString();
            }
            else {
                bundleIdentifier = null;
            }
            final NSString nsversion = (NSString)rootDict.objectForKey("CFBundleVersion");
            if (nsversion != null) {
                version = nsversion.toString();
            }
            else {
                final NSString nsShortversion = (NSString)rootDict.objectForKey("CFBundleShortVersionString");
                if (nsShortversion != null) {
                    version = nsShortversion.toString();
                }
            }
            final NSString bundleDisplayName = (NSString)rootDict.objectForKey("CFBundleDisplayName");
            if (bundleDisplayName != null && !bundleDisplayName.toString().equals("")) {
                appName = bundleDisplayName.toString();
            }
            else {
                final NSString bundleName = (NSString)rootDict.objectForKey("CFBundleName");
                if (bundleName != null) {
                    appName = bundleName.toString();
                }
            }
            final NSArray supportedDev = (NSArray)rootDict.objectForKey("UIDeviceFamily");
            int supportedCombo = 0;
            if (supportedDev != null) {
                final ArrayList<Long> supported = new ArrayList<Long>();
                for (int i = 0; i < supportedDev.count(); ++i) {
                    supported.add(i, Long.parseLong(supportedDev.objectAtIndex(i).toString()));
                    supportedCombo |= Integer.parseInt(supportedDev.objectAtIndex(i).toString());
                }
                supportedDevice = supported.toString();
            }
            else {
                supportedDevice = null;
                supportedCombo = -1;
            }
            if (bundleIdentifier != null) {
                ipaProp.put("PackageName", (Object)bundleIdentifier);
            }
            ipaProp.put("VersionName", (Object)version);
            ipaProp.put("IconName", (Object)smallLargeIconNamesArray);
            ipaProp.put("APP_NAME", (Object)appName);
            ipaProp.put("SUPPORTED_DEVICE", (Object)supportedDevice);
            ipaProp.put("SUPPORTED_DEVICES", supportedCombo);
            IosIPAExtractor.logger.log(Level.INFO, "ipaProp {0}", ipaProp);
        }
        catch (final Exception exp) {
            IosIPAExtractor.logger.log(Level.SEVERE, "Unable to get the ipa properties {0}", exp);
        }
        return ipaProp;
    }
    
    public JSONArray extractInfoAndPPFilesFromIPA(final String ipaPath, final String destPath) throws Exception {
        ZipFile zipFIle = null;
        int index = 0;
        JSONArray meatData = new JSONArray();
        this.validateIPAandSetBaseFolderPath(ipaPath);
        try {
            String pathName = "";
            zipFIle = new ZipFile(new File(ipaPath));
            final Enumeration<? extends ZipEntry> zipFiles = zipFIle.entries();
            while (zipFiles.hasMoreElements()) {
                final ZipEntry zipEntry = (ZipEntry)zipFiles.nextElement();
                pathName = zipEntry.getName();
                final String fileName = FilenameUtils.getName(pathName);
                if (fileName.equalsIgnoreCase("Info.plist")) {
                    final Boolean infoPlistStatus = this.extract(zipFIle, destPath, "info_" + index + ".plist", zipEntry);
                    if (infoPlistStatus) {
                        final String infoPlistPath = pathName.substring(0, pathName.lastIndexOf("/"));
                        this.addMetaInfo(infoPlistPath, "info_" + index + ".plist", meatData);
                        ++index;
                    }
                }
                if (fileName.equalsIgnoreCase("embedded.mobileprovision")) {
                    final Boolean ppStatus = this.extract(zipFIle, destPath, "embedded_" + index + ".mobileprovision", zipEntry);
                    if (!ppStatus) {
                        continue;
                    }
                    final String infoPlistPath = pathName.substring(0, pathName.lastIndexOf("/"));
                    this.addMetaInfo(infoPlistPath, "embedded_" + index + ".mobileprovision", meatData);
                    ++index;
                }
            }
            meatData = this.restructureMetaInfo(meatData);
        }
        catch (final Exception e) {
            IosIPAExtractor.logger.log(Level.WARNING, "Exception while getting ipa file", e);
            throw e;
        }
        finally {
            if (zipFIle != null) {
                zipFIle.close();
            }
        }
        return meatData;
    }
    
    public boolean extract(final ZipFile zipFile, String destFile, final String destfileName, final ZipEntry zipEntry) throws IOException {
        boolean extractionStatus = false;
        InputStream zis = null;
        OutputStream out = null;
        try {
            this.validateIPAContentsFormat(zipEntry, new Tika().detect(zipFile.getInputStream(zipEntry)));
            zis = zipFile.getInputStream(zipEntry);
            destFile = destFile + File.separator + destfileName;
            out = new FileOutputStream(destFile);
            final byte[] buffer = new byte[8192];
            int len;
            while ((len = zis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            IosIPAExtractor.logger.log(Level.INFO, "Successfully extracted...");
            extractionStatus = true;
        }
        catch (final IOException ioe) {
            IosIPAExtractor.logger.log(Level.SEVERE, "IOException while extracting...", ioe);
            throw ioe;
        }
        catch (final Exception e) {
            IosIPAExtractor.logger.log(Level.SEVERE, "Exception while extracting...", e);
            throw e;
        }
        finally {
            if (zis != null) {
                try {
                    zis.close();
                }
                catch (final IOException ioe2) {
                    IosIPAExtractor.logger.log(Level.SEVERE, "IOException while closing the InputStream...", ioe2);
                }
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (final IOException ioe2) {
                    IosIPAExtractor.logger.log(Level.SEVERE, "IOException while closing the output stream...", ioe2);
                }
            }
        }
        return extractionStatus;
    }
    
    public String[] extractIconsFromipa(final String iconFileName, final JSONArray iconArray, final String ipaPath) throws JSONException {
        final File iconFile = new File(iconFileName);
        final File ipafile = new File(ipaPath);
        final String[] extractedFiles = { "", "" };
        final ArrayList<String> iconFiles = new ArrayList<String>();
        final String smallIconFile = String.valueOf(iconArray.get(0));
        final String largeIconFile = String.valueOf(iconArray.get(iconArray.length() - 1));
        ZipFile zipFile = null;
        try {
            try {
                String smallIconName = "";
                String largeIconName = "";
                zipFile = new ZipFile(new File(ipaPath));
                final Enumeration<? extends ZipEntry> zipFiles = zipFile.entries();
                while (zipFiles.hasMoreElements()) {
                    final ZipEntry ze = (ZipEntry)zipFiles.nextElement();
                    final String pathName = ze.getName();
                    final String fileName = FilenameUtils.getName(pathName);
                    if (fileName.contains(smallIconFile) && smallIconName.equals("")) {
                        this.validateIPAContentsFormat(ze, new Tika().detect(zipFile.getInputStream(ze)));
                        smallIconName = pathName;
                    }
                    if (fileName.contains(largeIconFile) && largeIconName.equals("")) {
                        this.validateIPAContentsFormat(ze, new Tika().detect(zipFile.getInputStream(ze)));
                        largeIconName = pathName;
                    }
                    if (!smallIconName.equals("") && !largeIconName.equals("")) {
                        break;
                    }
                }
                if (!smallIconName.equals("")) {
                    iconFiles.add(smallIconName);
                }
                if (!largeIconName.equals("") && !largeIconFile.equalsIgnoreCase(smallIconFile)) {
                    iconFiles.add(largeIconName);
                }
            }
            catch (final Exception e) {
                IosIPAExtractor.logger.log(Level.WARNING, " Exception in extracting icon from ipa file:", e);
            }
            finally {
                if (zipFile != null) {
                    zipFile.close();
                }
            }
            for (int i = 0; i < iconFiles.size(); ++i) {
                if (ApiFactoryProvider.getZipUtilAPI().unzip(ipafile.toString(), iconFile.toString(), false, false, new String[] { iconFiles.get(i) }) && iconFile.list().length > 0) {
                    final String[] list;
                    final String[] filesList = list = iconFile.list();
                    for (final String iconImagePath : list) {
                        if (iconFiles.get(i).contains(iconImagePath) && !iconImagePath.contains(".plist") && !iconImagePath.contains(".mobileprovision") && !iconImagePath.contains(".xml")) {
                            extractedFiles[i] = iconFile.toString() + File.separator + iconImagePath;
                        }
                    }
                }
            }
            if (iconFile.list().length == 0) {
                iconFile.delete();
            }
            if (extractedFiles[1].equals("") && !extractedFiles[0].equals("")) {
                extractedFiles[1] = extractedFiles[0];
            }
        }
        catch (final Exception e) {
            IosIPAExtractor.logger.log(Level.WARNING, " Exception in extracting icon from ipa file:", e);
        }
        IosIPAExtractor.logger.log(Level.WARNING, " Extracted icon from ipa file 0th file :{0}", extractedFiles);
        return extractedFiles;
    }
    
    private void validateIPAContentsFormat(final ZipEntry ze, final String detectedType) {
        final String filePath = ze.getName();
        final String fileName = FilenameUtils.getName(filePath);
        if (fileName.equalsIgnoreCase("embedded.mobileprovision")) {
            if (!detectedType.equals("application/pkcs7-signature")) {
                throw new IAMSecurityException("Given embedded.mobileprovision is not application/pkcs7-signature :" + filePath + " -- detected format:" + detectedType);
            }
        }
        else if (fileName.equalsIgnoreCase("info.plist")) {
            if (!detectedType.equals("application/x-bplist") && !detectedType.equals("application/xml") && !detectedType.equals("application/x-plist")) {
                throw new IAMSecurityException("Given info.plist is not application/x-bplist or application/xml format :" + filePath + " -- detected format:" + detectedType);
            }
        }
        else if (filePath.endsWith(".jpeg")) {
            if (!detectedType.equals("image/jpeg")) {
                throw new IAMSecurityException("Given icon file -jpeg is not image/jpeg format :" + filePath + " -- detected format:" + detectedType);
            }
        }
        else {
            if (!filePath.endsWith(".png")) {
                throw new IAMSecurityException("Unknown file is getting attempted to extract:" + filePath + " -- detected format:" + detectedType);
            }
            if (!detectedType.equals("image/png")) {
                throw new IAMSecurityException("Given icon file -png is not image/png format :" + filePath + " -- detected format:" + detectedType);
            }
        }
    }
    
    static {
        IosIPAExtractor.logger = Logger.getLogger("MDMAppMgmtLogger");
    }
}
