package com.me.mdm.onpremise.server.settings;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadStatus;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.devicemanagement.framework.server.downloadmgr.SSLValidationType;
import com.me.devicemanagement.framework.server.downloadmgr.DownloadManager;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.util.CreatorDataPost;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class NATReachabilityTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    public static final String IS_NAT_REACHABLE = "is_nat_reachable";
    public static final String NAT_REACHABILITY_DETAILS = "nat_reachability_details";
    
    public void executeTask(final Properties props) {
        Boolean isNATExposed = Boolean.FALSE;
        final String natAddress = props.getProperty("natAddress");
        final String port = props.getProperty("port");
        try {
            CreatorDataPost.getInstance().resetCodes();
            final String fileName = MDMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "creator_properties.conf";
            final Properties creatorProperties = FileAccessUtil.readProperties(fileName);
            if (creatorProperties != null) {
                CreatorDataPost.creator_auth_token = creatorProperties.getProperty("request_demo_creator_auth_token");
                CreatorDataPost.creator_form_name = creatorProperties.getProperty("nat_validation_creator_form_name");
                CreatorDataPost.creator_owner_name = creatorProperties.getProperty("nat_validation_creator_owner_name");
                CreatorDataPost.creator_application_name = creatorProperties.getProperty("nat_validation_creator_application_name");
            }
            final ArrayList<String> fieldList = new ArrayList<String>();
            fieldList.add("NAT_INPUT");
            fieldList.add("NAT_RESULT");
            CreatorDataPost.setXmlFieldList((ArrayList)fieldList);
            final List list = new ArrayList();
            final String publicFQDN = "https://" + natAddress + ":" + port + "/mdm/serverstatus";
            list.add(publicFQDN);
            list.add("false");
            final List<List> valuesList = new ArrayList<List>();
            valuesList.add(list);
            CreatorDataPost.xmlFiledValues((List)valuesList);
            final int code = CreatorDataPost.getInstance().submitCreatorData();
            if (code == 200) {
                NATReachabilityTask.logger.log(Level.INFO, "NAT DATA POSTED SUCCESSFULLY TO CREATOR");
                try {
                    final String resultXML = CreatorDataPost.getResultData();
                    DMSecurityLogger.info(NATReachabilityTask.logger, NATReachabilityTask.class.getName(), "isNATExposed", "RESPONSE FROM CREATOR : {0}", (Object)resultXML);
                    final Properties properties = MDMUtil.getInstance().parseXMLToProperties(resultXML);
                    final Long formID = Long.parseLong(((Hashtable<K, String>)properties).get("ID"));
                    final String viewURL = "https://creator.zoho.com/api/json/" + CreatorDataPost.creator_application_name + "/view/MDM_Reachability_Check_For_FQDN_Report";
                    final String parameters = "authtoken=" + CreatorDataPost.creator_auth_token + "&scope=creatorapi&criteria=(ID=" + String.valueOf(formID) + ")&raw=true&zc_ownername=adventnetwebmaster";
                    final String getURL = viewURL + "?" + parameters;
                    final DownloadStatus downloadStatus = DownloadManager.getInstance().getURLResponseWithoutCookie(getURL, (String)null, new SSLValidationType[0]);
                    final int responseCode = downloadStatus.getStatus();
                    if (responseCode == 0) {
                        NATReachabilityTask.logger.log(Level.INFO, "SUCCESSFULLY GOT THE DATA FROM THE CREATOR");
                        final String responseContent = downloadStatus.getUrlDataBuffer();
                        if (JSONUtil.getInstance().isValidJSON(responseContent)) {
                            final JSONObject responseJSONObject = new JSONObject(responseContent);
                            final JSONArray jsonArray = responseJSONObject.getJSONArray(CreatorDataPost.creator_form_name);
                            final JSONObject contentObject = jsonArray.getJSONObject(0);
                            final String nat_result = contentObject.get("NAT_RESULT").toString();
                            final String nat_input = contentObject.get("NAT_INPUT").toString();
                            if ("true".equalsIgnoreCase(nat_result) && publicFQDN.equalsIgnoreCase(nat_input)) {
                                isNATExposed = Boolean.TRUE;
                            }
                        }
                    }
                }
                catch (final Exception exception) {
                    NATReachabilityTask.logger.log(Level.SEVERE, "200 Success code received from the creator, but the following exception ", exception);
                }
            }
        }
        catch (final Exception exception2) {
            NATReachabilityTask.logger.log(Level.SEVERE, "Exception in posting NAT data to creator", exception2);
        }
        finally {
            CreatorDataPost.getInstance().resetFields();
        }
        MDMUtil.updateSyMParameter("is_nat_reachable", isNATExposed.toString());
    }
    
    public static String isNATReachable() {
        final String isNATExposed = MDMUtil.getSyMParameter("is_nat_reachable");
        return (isNATExposed == null) ? "NA" : isNATExposed.toLowerCase();
    }
    
    public static void isNATexposed(final String natAddress, final int port) {
        final Properties props = new Properties();
        final HashMap taskInfoMap = new HashMap();
        taskInfoMap.put("taskName", "NATReachabilityTask");
        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
        taskInfoMap.put("poolName", "mdmPool");
        ((Hashtable<String, String>)props).put("natAddress", natAddress);
        ((Hashtable<String, String>)props).put("port", String.valueOf(port));
        ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.onpremise.server.settings.NATReachabilityTask", taskInfoMap, props);
    }
    
    static {
        NATReachabilityTask.logger = Logger.getLogger(NATReachabilityTask.class.getName());
    }
}
