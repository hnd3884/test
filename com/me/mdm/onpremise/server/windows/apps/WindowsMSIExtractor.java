package com.me.mdm.onpremise.server.windows.apps;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.factory.MDMWinAppExtractorAPI;
import com.me.mdm.server.windows.apps.WindowsAppExtractor;

public class WindowsMSIExtractor extends WindowsAppExtractor implements MDMWinAppExtractorAPI
{
    Logger logger;
    
    public WindowsMSIExtractor() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject getMSIProperties(String msiPath) throws JSONException {
        JSONObject msiProps = null;
        final String properties = "ProductCode,ProductVersion,ProductName";
        final String scriptPath = ".." + File.separator + "mdm" + File.separator + "psscript" + File.separator + "winmsidetails" + File.separator + "Get-MSIFileInformation.ps1";
        try {
            msiPath = ApiFactoryProvider.getFileAccessAPI().getCanonicalPath(msiPath);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while obtaining canonical path", e);
        }
        Process psProcess = null;
        try {
            final String[] commandList = { "PowerShell", "-ExecutionPolicy", "Bypass", "-NoLogo", "-noninteractive", scriptPath, "-Path", "\"'" + msiPath + "'\"", "-Properties", "\"" + properties + "\"" };
            psProcess = MDMApiFactoryProvider.getMDMUtilAPI().exec(commandList);
            psProcess.waitFor();
            if (psProcess != null) {
                msiProps = new JSONObject(this.readFromPSStream(psProcess.getInputStream()));
            }
            this.logPSErrorStream(psProcess.getErrorStream());
        }
        catch (final InterruptedException | JSONException e2) {
            this.logger.log(Level.SEVERE, "Process builder for ProductCode interrupted", e2);
        }
        finally {
            psProcess.destroy();
        }
        final JSONObject xapProps = new JSONObject();
        if (msiProps == null) {
            msiProps = new JSONObject();
        }
        if (msiProps.has("ProductCode")) {
            xapProps.put("PackageName", (Object)StringUtils.strip(String.valueOf(msiProps.get("ProductCode")), "{}"));
        }
        if (msiProps.has("ProductVersion")) {
            xapProps.put("VersionName", (Object)String.valueOf(msiProps.get("ProductVersion")));
        }
        if (msiProps.has("ProductName")) {
            xapProps.put("ProductName", (Object)String.valueOf(msiProps.get("ProductName")));
        }
        xapProps.put("SUPPORTED_ARCH", (Object)this.getSupportedArchCode("x86"));
        xapProps.put("SUPPORTED_DEVICES", (Object)getSupportedDeviceCode("desktop"));
        return xapProps;
    }
    
    private String readFromPSStream(final InputStream psProcessStream) {
        BufferedReader bufferedReader = null;
        String data = "";
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(psProcessStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                data += line;
            }
        }
        catch (final IOException e) {
            this.logger.log(Level.SEVERE, "Exception while reading from poweshell stream", e);
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (final IOException ex) {
                    this.logger.log(Level.INFO, "Exception while closing PS BufferedStream", ex);
                }
            }
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (final IOException ex2) {
                    this.logger.log(Level.INFO, "Exception while closing PS BufferedStream", ex2);
                }
            }
        }
        return data;
    }
    
    private void logPSErrorStream(final InputStream psProcessErrorStream) {
        final String errorData = this.readFromPSStream(psProcessErrorStream);
        if (errorData != null && !errorData.trim().isEmpty()) {
            this.logger.log(Level.SEVERE, "Error on running the PSProcess\n{0}", errorData);
        }
    }
}
