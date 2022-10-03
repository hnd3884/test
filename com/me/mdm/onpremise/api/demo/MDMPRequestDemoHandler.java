package com.me.mdm.onpremise.api.demo;

import java.util.Hashtable;
import java.util.Date;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import java.util.List;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import java.util.Properties;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.util.CreatorDataPost;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMPRequestDemoHandler
{
    private Logger logger;
    
    public MDMPRequestDemoHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public JSONObject registerRequestDemo(final JSONObject jsonObject) throws Exception {
        CreatorDataPost.getInstance().resetCodes();
        final String fileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "creator_properties.conf";
        final Properties creatorProperties = FileAccessUtil.readProperties(fileName);
        if (creatorProperties != null) {
            CreatorDataPost.creator_auth_token = creatorProperties.getProperty("request_demo_creator_auth_token");
            CreatorDataPost.creator_form_name = creatorProperties.getProperty("request_demo_creator_form_name");
            CreatorDataPost.creator_owner_name = creatorProperties.getProperty("request_demo_creator_owner_name");
            CreatorDataPost.creator_application_name = creatorProperties.getProperty("request_demo_creator_application_name");
        }
        final ArrayList<String> fieldList = new ArrayList<String>();
        final JSONObject submitJSONObject = new JSONObject();
        final Properties additionalProperties = new Properties();
        fieldList.add("Email");
        fieldList.add("PerDtl");
        fieldList.add("Source");
        CreatorDataPost.xml_field_list = fieldList;
        final String name = String.valueOf(jsonObject.get("name"));
        final String email = String.valueOf(jsonObject.get("email"));
        final String phone = String.valueOf(jsonObject.get("phone"));
        final String country = String.valueOf(jsonObject.get("country"));
        final String countryCode = String.valueOf(jsonObject.get("country_code"));
        final boolean consent = jsonObject.optBoolean("consent", (boolean)Boolean.FALSE);
        final int mobileCount = jsonObject.optInt("managed_device");
        final String trackingCode = ProductUrlLoader.getInstance().getValue("trackingcode");
        final String source = String.valueOf(jsonObject.get("source"));
        final String loginUserName = String.valueOf(jsonObject.get("username"));
        final String offset = String.valueOf(jsonObject.get("offset"));
        final String dateTime = String.valueOf(jsonObject.get("datefordemo"));
        submitJSONObject.put("Name", (Object)name);
        submitJSONObject.put("Email", (Object)email);
        submitJSONObject.put("Phone", (Object)phone);
        submitJSONObject.put("Country", (Object)country);
        if (countryCode != null) {
            submitJSONObject.put("Country_Code", (Object)countryCode);
        }
        if (consent) {
            submitJSONObject.put("Consent", consent);
        }
        submitJSONObject.put("BrowserTimeZone", (Object)offset);
        submitJSONObject.put("LoggedInUser", (Object)loginUserName);
        ((Hashtable<String, Integer>)additionalProperties).put("MobileCount", mobileCount);
        Long scheduledTime = null;
        if (dateTime != null) {
            final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, HH:mm");
            final Date calcScTime = sdf.parse(dateTime);
            scheduledTime = calcScTime.getTime();
            submitJSONObject.put("DemoDate", (Object)scheduledTime);
        }
        final String perferedTime = dateTime.substring(dateTime.indexOf(",") + 1).trim();
        submitJSONObject.put("perferedTime", (Object)perferedTime);
        final String submitContentString = submitJSONObject.toString();
        final List list = new ArrayList();
        list.add(email);
        list.add(submitContentString);
        list.add(source);
        final List<List> valuesList = new ArrayList<List>();
        valuesList.add(list);
        CreatorDataPost.xml_filed_values = valuesList;
        this.logger.log(Level.INFO, "Posting data to creator: {0}", list);
        final int code = CreatorDataPost.getInstance().submitCreatorData();
        final JSONObject responseJSON = new JSONObject();
        final String failureParams = trackingCode + "-demopostFailure";
        if (code == 200) {
            this.logger.log(Level.INFO, "Status code obtained is 200..Connection established successfully");
            final String resultXML = CreatorDataPost.getResultData();
            this.logger.log(Level.INFO, "Demo Response: {0}", resultXML);
            final String result = resultXML.substring(resultXML.lastIndexOf("<status>") + 8, resultXML.lastIndexOf("</status>"));
            if ("success".equalsIgnoreCase(result)) {
                responseJSON.put("status", (Object)"success");
                responseJSON.put("message", (Object)I18N.getMsg("dc.request_demo.successMsg", new Object[0]));
            }
            else {
                responseJSON.put("status", (Object)result);
                responseJSON.put("message", (Object)failureParams);
            }
        }
        return responseJSON;
    }
}
