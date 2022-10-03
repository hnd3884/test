package com.me.mdm.onpremise.api.support;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.onpremise.server.status.SysStatusHandler;
import com.me.devicemanagement.framework.server.util.Utils;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.support.SupportFileCreation;
import java.io.File;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.api.APIRequest;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.api.support.MDMSupportFacade;

public class MDMPSupportFacade extends MDMSupportFacade
{
    Logger logger;
    
    public MDMPSupportFacade() {
        this.logger = Logger.getLogger(MDMPSupportFacade.class.getCanonicalName());
    }
    
    public JSONObject getProductInfo() throws Exception {
        try {
            final JSONObject jsonObject = new JSONObject();
            this.supportPageDetails(jsonObject);
            return jsonObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getProductInfo() : ", e);
            throw e;
        }
    }
    
    public void downloadSupportFile(final APIRequest apiRequest) throws Exception {
        String filePath = SyMUtil.getInstallationDir();
        final String fileName = MDMApiFactoryProvider.getMdmCompressAPI().getSupportFileName();
        if (filePath == null) {
            throw new APIHTTPException("FILE002", new Object[0]);
        }
        filePath = filePath + File.separator + "logs" + File.separator + "supportLogs";
        if (new File(filePath + File.separator + fileName + "." + SupportFileCreation.getInstance().getExtension()).exists()) {
            filePath = filePath + File.separator + fileName + "." + SupportFileCreation.getInstance().getExtension();
            apiRequest.httpServletResponse.setContentType("application/x-7z-compressed");
        }
        else {
            if (!new File(filePath + fileName + ".zip").exists()) {
                this.logger.log(Level.SEVERE, "requested file does not exist");
                throw new APIHTTPException("FILE002", new Object[0]);
            }
            filePath = filePath + File.separator + fileName + ".zip";
            apiRequest.httpServletResponse.setContentType("application/zip");
        }
        apiRequest.httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        final InputStream is = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
        int read = 0;
        final byte[] bytes = new byte[4096];
        BufferedOutputStream buffOut = null;
        try {
            buffOut = new BufferedOutputStream((OutputStream)apiRequest.httpServletResponse.getOutputStream());
            while ((read = is.read(bytes)) != -1) {
                buffOut.write(bytes, 0, read);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in support file download", ex);
            throw new APIHTTPException("FILE001", new Object[0]);
        }
        finally {
            if (is != null) {
                is.close();
            }
            buffOut.flush();
            buffOut.close();
        }
    }
    
    private void supportPageDetails(final JSONObject jsonObject) throws Exception {
        final JSONObject systemInfo = new JSONObject();
        this.loadSystemInfo(systemInfo);
        jsonObject.put("system_info", (Object)systemInfo);
        final JSONObject installationInfo = new JSONObject();
        this.loadInstallationInfo(installationInfo);
        jsonObject.put("installation_info", (Object)installationInfo);
        final JSONObject databaseInfo = new JSONObject();
        this.loadDatabaseInfo(databaseInfo);
        jsonObject.put("database_info", (Object)databaseInfo);
    }
    
    private void loadSystemInfo(final JSONObject systemInfo) throws Exception {
        final InetAddress address = InetAddress.getLocalHost();
        systemInfo.put("host_name", (Object)address.getHostName());
        systemInfo.put("os_name", (Object)System.getProperty("os.name"));
        systemInfo.put("os_version", (Object)System.getProperty("os.version"));
        final String osArchitecture = com.me.devicemanagement.onpremise.server.util.SyMUtil.getDCOSArchitecture();
        if (osArchitecture != null) {
            systemInfo.put("os_arch", (Object)osArchitecture);
        }
        systemInfo.put("system_time", (Object)Utils.getTime(Long.valueOf(System.currentTimeMillis())));
        final String hosted_at = getServerHostedAt();
        systemInfo.put("hosted_at", (Object)hosted_at);
    }
    
    private static String getServerHostedAt() {
        String hosted_at = com.me.devicemanagement.onpremise.server.util.SyMUtil.getServerParameter("SYSTEM_HW_TYPE");
        if (hosted_at != null) {
            hosted_at = (hosted_at.equalsIgnoreCase("azure_virtual") ? "Azure Virtual Machine" : (hosted_at.equalsIgnoreCase("amazon_virtual") ? "Amazon Virtual Machine" : "On-premise"));
        }
        return hosted_at;
    }
    
    private void loadInstallationInfo(final JSONObject installationInfo) throws Exception {
        final String installationdateInLong = com.me.devicemanagement.onpremise.server.util.SyMUtil.getInstallationProperty("it");
        if (installationdateInLong != null && !installationdateInLong.equals("")) {
            installationInfo.put("build_date_millisec", (Object)installationdateInLong);
            final long instDate = new Long(installationdateInLong);
            installationInfo.put("build_date", (Object)Utils.getEventTime(Long.valueOf(instDate)));
        }
        else {
            installationInfo.put("build_date", (Object)"--");
        }
        installationInfo.put("server_start_time", (Object)Utils.getEventTime(new Long(SysStatusHandler.getServerStartTime())));
        installationInfo.put("port", (Object)("" + ((Hashtable<K, Object>)MDMUtil.getDCServerInfo()).get("SERVER_PORT")));
        installationInfo.put("working_dir", (Object)this.getInstallationDirectory());
    }
    
    private String getInstallationDirectory() throws Exception {
        final File f = new File(".");
        String path = f.getCanonicalPath();
        final File f2 = new File(path);
        path = f2.getParent();
        final int length = path.length();
        String originalName = "";
        if (length > 33) {
            final String[] resultNames = path.split("\\\\");
            String temp = "";
            for (int i = 0; i < resultNames.length; ++i) {
                if ((temp + resultNames[i]).length() > 33) {
                    originalName = originalName + temp + File.separator;
                    temp = resultNames[i];
                }
                else if (temp.equals("")) {
                    temp = resultNames[i];
                }
                else {
                    temp = temp + File.separator + resultNames[i];
                }
            }
            originalName += temp;
        }
        else {
            originalName = path;
        }
        return originalName;
    }
    
    private void loadDatabaseInfo(final JSONObject databaseInfo) throws Exception {
        final Map<String, String> dbProps = DBUtil.getDBServerProperties();
        for (final Map.Entry<String, String> entry : dbProps.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            final String newKey = key.replaceAll("\\.", "_");
            databaseInfo.put(newKey, (Object)value);
        }
    }
}
