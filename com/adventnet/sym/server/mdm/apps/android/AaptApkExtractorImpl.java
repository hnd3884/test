package com.adventnet.sym.server.mdm.apps.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import com.me.devicemanagement.framework.server.general.UtilAccessAPI;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class AaptApkExtractorImpl extends AndroidAPKExtractor
{
    private static final String AAPT_CMD_PATH;
    private static final String AAPT_CMD_ARGS = " dump badging ";
    private String manifestDetails;
    
    public AaptApkExtractorImpl() {
        this.manifestDetails = null;
        AaptApkExtractorImpl.PACKAGE_EXPRESSION = "package: name";
        AaptApkExtractorImpl.VERSION_EXPRESSION = "versionName";
        AaptApkExtractorImpl.MINIMUM_SDK_EXPRESSION = "sdkVersion";
    }
    
    @Override
    public synchronized JSONObject getAndroidAppsDetails(String apkPath) throws JSONException {
        apkPath = "\"" + apkPath + "\"";
        JSONObject apkProps = new JSONObject();
        try {
            this.execute(this.getCommandForAapt(apkPath));
            apkProps = this.getAPKProperties(this.getReqPropsForAddApp());
        }
        catch (final Exception ex) {
            try {
                apkProps = this.getErrorProps("mdm.app.apk_extraction_error@@@" + ex.getMessage());
            }
            catch (final Exception ex2) {
                Logger.getLogger(AaptApkExtractorImpl.class.getName()).log(Level.SEVERE, null, ex2);
            }
            this.logger.log(Level.WARNING, "Exception in extracting app details", ex);
        }
        if (!apkProps.has("extractError") && (!apkProps.has("PackageName") || !apkProps.has("VersionName"))) {
            this.logger.log(Level.WARNING, "Cannot extract app information. Manifest details: {0} | apkProps: {1}", new Object[] { this.manifestDetails, apkProps });
            apkProps = this.getErrorProps("Unknown error in executing the command");
            apkProps.put("extractError", (Object)"commandFailure");
        }
        return apkProps;
    }
    
    private String[] getCommandForAapt(final String apkFilePath) {
        final String osName = System.getProperty("os.name");
        final String commandArgs = AaptApkExtractorImpl.AAPT_CMD_PATH + " dump badging " + apkFilePath;
        final UtilAccessAPI utilAccessAPI = ApiFactoryProvider.getUtilAccessAPI();
        final String filePath = utilAccessAPI.getServerBinUrl() + File.separator + commandArgs;
        if (osName.contains("Windows")) {
            return new String[] { "cmd.exe", "/c", filePath };
        }
        if (osName.contains("Linux")) {
            return new String[] { "/bin/sh", "-c", filePath };
        }
        return null;
    }
    
    private void execute(final String[] command) throws Exception {
        final ProcessBuilder processBuilder = new ProcessBuilder(command);
        final Process process = processBuilder.start();
        final InputStream inputStream = process.getInputStream();
        final InputStream errorStream = process.getErrorStream();
        this.manifestDetails = this.getContentFromInputStream(inputStream);
        final String error = this.getContentFromInputStream(errorStream);
        try {
            this.logger.log(Level.INFO, "apk extraction process exited with status {0}", process.exitValue());
        }
        catch (final IllegalThreadStateException ex) {
            this.logger.log(Level.SEVERE, "process not comepleted", ex);
        }
        if (error != null && !error.isEmpty()) {
            throw new Exception(error);
        }
    }
    
    private String getContentFromInputStream(final InputStream stream) throws Exception {
        final StringBuilder content = new StringBuilder();
        String line = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(stream));
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
        }
        catch (final Exception e) {
            throw new Exception("Error while reading stream: " + e);
        }
        finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return content.toString();
    }
    
    @Override
    protected JSONObject getAPKProperties(final JSONObject requiredProperties) throws JSONException {
        final JSONObject apkProp = new JSONObject();
        final Iterator<String> myIter = requiredProperties.keys();
        while (myIter.hasNext()) {
            final String property = myIter.next();
            final String propertyName = String.valueOf(requiredProperties.get(property));
            apkProp.put(property, (Object)this.getPropertyValue(propertyName));
        }
        return apkProp;
    }
    
    @Override
    protected String getPropertyValue(final String propertyName) {
        String propertyValue = null;
        final String pattern = propertyName + "='([^']*)'";
        final Pattern r = Pattern.compile(pattern);
        final Matcher m = r.matcher(this.manifestDetails);
        if (m.find()) {
            propertyValue = m.group(1);
        }
        return propertyValue;
    }
    
    static {
        AAPT_CMD_PATH = "scripts" + File.separator + "aapt";
    }
}
