package com.me.mdm.webclient.support;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import java.io.File;
import com.adventnet.sym.server.mdm.command.DeviceInvCommandHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.HashMap;
import com.me.mdm.server.support.SupportFileCreation;
import java.util.logging.Level;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.logging.Logger;

public class MDMSupport
{
    private static String fs;
    private Logger out;
    private static MDMSupport mdmSupport;
    
    private MDMSupport() {
        this.out = Logger.getLogger(MDMSupport.class.getName());
    }
    
    public static MDMSupport getInstance() {
        if (MDMSupport.mdmSupport == null) {
            MDMSupport.mdmSupport = new MDMSupport();
        }
        return MDMSupport.mdmSupport;
    }
    
    public ArrayList<Long> uploadAgentLogs(final JSONArray deviceList) throws Exception {
        this.out.log(Level.INFO, "---Inside MDM upload Agent Logs----");
        final ArrayList<Long> agentResIDs = new ArrayList<Long>();
        final SupportFileCreation supportObj = SupportFileCreation.getInstance();
        supportObj.setArrMDMAgentLogUploadList(new HashMap());
        final int len = deviceList.length();
        if (len != 0) {
            this.out.log(Level.INFO, "Mobile Device choosen : {0}", deviceList);
            supportObj.setMdmAgentLogInitiatedCount(0);
            supportObj.setMdmAgentLogUplodedCount(0);
            for (int waitTime = 0; waitTime < len; ++waitTime) {
                final JSONObject deviceDetails = deviceList.getJSONObject(waitTime);
                final Long resourceID = JSONUtil.optLongForUVH(deviceDetails, "device_id", Long.valueOf(0L));
                deviceDetails.put("device_id", (Object)resourceID);
                if (resourceID != 0L) {
                    agentResIDs.add(resourceID);
                    final Integer platformType = (Integer)deviceDetails.get("platform_type_id");
                    final String udid = (String)deviceDetails.get("udid");
                    final String resourceName = (String)deviceDetails.get("device_name");
                    final Long customerId = deviceDetails.getLong("customer_id");
                    try {
                        if (platformType == 2) {
                            supportObj.addDeviceToTheList(deviceDetails);
                            supportObj.setMdmAgentLogInitiatedCount(supportObj.getMdmAgentLogInitiatedCount() + DeviceInvCommandHandler.getInstance().uploadAgentLog(resourceID));
                        }
                        else if (platformType == 1) {
                            final String serverHome = System.getProperty("server.home");
                            final String iOSAgentLogs = serverHome + MDMSupport.fs + "mdm-logs" + MDMSupport.fs + customerId + MDMSupport.fs + resourceName + "_" + udid;
                            final File agentDir = new File(iOSAgentLogs);
                            if (!agentDir.exists()) {
                                supportObj.addDeviceToTheList(deviceDetails);
                                supportObj.setMdmAgentLogInitiatedCount(supportObj.getMdmAgentLogInitiatedCount() + 1);
                            }
                        }
                    }
                    catch (final Exception var21) {
                        this.out.log(Level.WARNING, var21, () -> "Error while initiating agent log collection command for device " + n);
                    }
                }
            }
        }
        else {
            this.out.log(Level.INFO, "No devices are choosen ..");
        }
        this.out.log(Level.INFO, "All Device List ..{0}", supportObj.getArrMDMAgentLogUploadList());
        return agentResIDs;
    }
    
    public void setRequestProps(final HttpServletRequest request, final Properties requestProp) {
        final ArrayList<String> requestAttributeNames = Collections.list((Enumeration<String>)request.getAttributeNames());
        final HashMap<String, String> requestAttribute = new HashMap<String, String>();
        for (final String key : requestAttributeNames) {
            requestAttribute.put(key, request.getAttribute(key).toString());
        }
        requestAttribute.put("User-Agent", request.getHeader("User-Agent"));
        ((Hashtable<String, HashMap<String, String>>)requestProp).put("request", requestAttribute);
    }
    
    public void setRequestProps(final JSONObject request, final Properties requestProp) throws Exception {
        final Iterator it = request.keys();
        while (it.hasNext()) {
            final String key = it.next();
            ((Hashtable<String, String>)requestProp).put(key, String.valueOf(request.get(key)));
        }
    }
    
    public void setRequestProps(final HttpServletRequest request, final JSONObject requestData) throws Exception {
        ArrayList<String> requestAttributeNames = Collections.list((Enumeration<String>)request.getAttributeNames());
        for (final String key : requestAttributeNames) {
            final String tempKey = key.toLowerCase();
            requestData.put(tempKey, request.getAttribute(key));
        }
        requestAttributeNames = Collections.list((Enumeration<String>)request.getParameterNames());
        for (final String key : requestAttributeNames) {
            final String tempKey = key.toLowerCase();
            requestData.put(tempKey, (Object)request.getParameter(key));
        }
        this.keyChanger(requestData, "dblockfileupload", "db_lock_file_upload");
        this.keyChanger(requestData, "serverlogupload", "server_log_upload");
        this.keyChanger(requestData, "mdmlogupload", "mdm_log_upload");
        String userAgent = "";
        try {
            userAgent = request.getHeader("User-Agent");
        }
        catch (final UnsupportedOperationException var9) {
            userAgent = request.getAttribute("User-Agent").toString();
        }
        requestData.put("user_agent", (Object)userAgent);
    }
    
    private void keyChanger(final JSONObject requestData, final String oldKey, final String newKey) throws Exception {
        requestData.put(newKey, (Object)(requestData.has(oldKey) ? String.valueOf(requestData.get(oldKey)) : "false"));
        requestData.remove(oldKey);
    }
    
    static {
        MDMSupport.mdmSupport = null;
    }
}
