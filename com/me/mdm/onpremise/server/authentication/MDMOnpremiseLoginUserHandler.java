package com.me.mdm.onpremise.server.authentication;

import java.util.Random;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.mailmanager.MailDetails;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.List;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.server.twofactor.TwoFactorAction;
import java.util.Collection;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.mdm.server.factory.MDMLoginUserAPI;

public class MDMOnpremiseLoginUserHandler implements MDMLoginUserAPI
{
    private Logger logger;
    
    public MDMOnpremiseLoginUserHandler() {
        this.logger = Logger.getLogger(MDMOnpremiseLoginUserHandler.class.getName());
    }
    
    public void addUserOnCustomerAddition(final Properties customerDetails) {
        try {
            final JSONObject addUserObj = new JSONObject();
            final String role_ID = DMUserHandler.getRoleID("Customer Administrator");
            final List<Long> roleIdsList = DMUserHandler.getRoleList(role_ID);
            final JSONArray roleIdsListArray = new JSONArray((Collection)roleIdsList);
            final String sCustomerIDs = ((Hashtable<K, Object>)customerDetails).get("CUSTOMER_ID").toString();
            final String name = ((Hashtable<K, Object>)customerDetails).get("CUSTOMER_NAME").toString().toLowerCase().replaceAll(" ", "");
            addUserObj.put("userName", (Object)name);
            addUserObj.put("loginName", (Object)name);
            final String password = this.getRandomPassword();
            addUserObj.put("password", (Object)password);
            addUserObj.put("role_ID", (Object)role_ID);
            addUserObj.put("USER_EMAIL_ID", (Object)((Hashtable<K, Object>)customerDetails).get("CUSTOMER_EMAIL").toString());
            addUserObj.put("USER_PH_NO", (Object)((Hashtable<K, Object>)customerDetails).get("LANDLINE").toString());
            addUserObj.put("USER_LOCALE", (Object)"en_US");
            addUserObj.put("sCustomerIDs", (Object)sCustomerIDs);
            addUserObj.put("mdmScope", 0);
            final String[] cgList = new String[0];
            addUserObj.put("cgList", (Object)cgList);
            addUserObj.put("roleIdsList", (Object)roleIdsListArray);
            addUserObj.put("isTwoFactorEnabledGlobaly", TwoFactorAction.isTwoFactorEnabledGlobaly());
            addUserObj.put("summaryGroupID", DMUserHandler.getSummaryGroupID(role_ID));
            MDMPUserHandler.getInstance().addUserForMDM(addUserObj);
            this.sendMail(addUserObj);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while adding user ", e);
        }
    }
    
    private void sendMail(final JSONObject addUserObj) throws Exception {
        final Hashtable<String, String> mailSenderDetails = ApiFactoryProvider.getMailSettingAPI().getMailSenderDetails();
        final String fromName = mailSenderDetails.get("mail.fromName");
        final String fromAddress = mailSenderDetails.get("mail.fromAddress");
        final String strToAddress = addUserObj.optString("USER_EMAIL_ID");
        final String userName = addUserObj.optString("userName");
        final String password = addUserObj.optString("password");
        final MailDetails mailDetails = new MailDetails(fromAddress, strToAddress);
        mailDetails.bodyContent = getMailDescription(userName, password);
        mailDetails.senderDisplayName = fromName;
        mailDetails.subject = "MDM MSP Registration Request";
        ApiFactoryProvider.getMailSettingAPI().addToMailQueue(mailDetails, 0);
    }
    
    private String getRandomPassword() {
        final SecureRandom random = new SecureRandom();
        return new BigInteger(32, random).toString(16);
    }
    
    private static String getMailDescription(final String userName, final String password) {
        final String serverUrl = ApiFactoryProvider.getUtilAccessAPI().getServerURL();
        final StringBuffer stf = new StringBuffer();
        stf.append(" <html>\n<head>\n<style type=\"text/css\">\nbody{\n\tfont:12px Lato;\n}\n</style>\n</head>\n<body>\n<h3> Registration Mail </h3>\n<div>\n<a href=\"" + serverUrl + "\">Login to MDM MSP Server Using following <a>\n</br>User Name : " + userName + "\n</br>Password : " + password + "\n</div>\n</body></html>");
        return stf.toString();
    }
}
