package com.me.mdm.onpremise.server.enrollment.authentication;

import java.util.Hashtable;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.idps.core.factory.IdpsFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;
import com.me.mdm.core.ios.adep.ADEPAuthHandler;

public class ADEPActiveDirectoryAuthHandler extends ADEPAuthHandler
{
    public JSONObject authenticate(final JSONObject msgRequestJSON) {
        final JSONObject responseJSON = new JSONObject();
        try {
            responseJSON.put("Status", (Object)"Error");
            final String userName = String.valueOf(msgRequestJSON.get("UserName"));
            final String userPassword = String.valueOf(msgRequestJSON.get("Password"));
            String strNetBIOSName = String.valueOf(msgRequestJSON.get("DomainName"));
            if (!MDMStringUtils.isEmpty(strNetBIOSName) && !MDMStringUtils.isEmpty(userName)) {
                if (IdpsFactoryProvider.getIdpsAccessAPI(strNetBIOSName, CustomerInfoUtil.getInstance().getDefaultCustomer()).validatePassword(strNetBIOSName, userName, userPassword, CustomerInfoUtil.getInstance().getDefaultCustomer())) {
                    responseJSON.put("Status", (Object)"Acknowledged");
                    msgRequestJSON.put("DomainName", (Object)strNetBIOSName);
                    return responseJSON;
                }
            }
            else if (!MDMStringUtils.isEmpty(userName)) {
                final List domainList = MDMEnrollmentUtil.getInstance().getDomainPropsForUserAuthentication(CustomerInfoUtil.getInstance().getDefaultCustomer());
                final TreeMap domainListMap = MDMEnrollmentUtil.getInstance().getDomainListAsTreeMap(domainList);
                final Iterator<String> domainIterator = domainListMap.keySet().iterator();
                while (domainIterator.hasNext()) {
                    strNetBIOSName = domainIterator.next();
                    if (IdpsFactoryProvider.getIdpsAccessAPI(strNetBIOSName, CustomerInfoUtil.getInstance().getDefaultCustomer()).validatePassword(strNetBIOSName, userName, userPassword, CustomerInfoUtil.getInstance().getDefaultCustomer())) {
                        responseJSON.put("Status", (Object)"Acknowledged");
                        msgRequestJSON.put("DomainName", (Object)strNetBIOSName);
                        return responseJSON;
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(ADEPActiveDirectoryAuthHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return responseJSON;
    }
    
    public void getAuthenticatedUserDetails(final JSONObject json) {
        try {
            final String sDomainName = String.valueOf(json.get("DomainName"));
            final String sUserName = String.valueOf(json.get("UserName"));
            final String sPassword = String.valueOf(json.get("Password"));
            if (!sDomainName.equalsIgnoreCase("MDM")) {
                final List propList = new ArrayList();
                propList.add("sAMAccountName");
                propList.add("mail");
                propList.add("givenName");
                propList.add("initials");
                propList.add("sn");
                propList.add("displayName");
                propList.add("userPrincipalName");
                final Properties aduser = IdpsFactoryProvider.getIdpsAccessAPI(sDomainName, CustomerInfoUtil.getInstance().getCustomerId()).getThisADUserProperties(sDomainName, sUserName, sPassword, propList, CustomerInfoUtil.getInstance().getDefaultCustomer());
                if (aduser != null) {
                    if (aduser.contains("sAMAccountName") && !MDMStringUtils.isEmpty(aduser.getProperty("sAMAccountName"))) {
                        json.put("UserName", (Object)((Hashtable<K, String>)aduser).get("sAMAccountName"));
                    }
                    else if (aduser.contains("userPrincipalName") && !MDMStringUtils.isEmpty(aduser.getProperty("userPrincipalName"))) {
                        json.put("UserName", (Object)((Hashtable<K, String>)aduser).get("userPrincipalName"));
                    }
                    if (aduser.containsKey("mail")) {
                        json.put("Email", (Object)((Hashtable<K, String>)aduser).get("mail"));
                    }
                    if (aduser.containsKey("givenName")) {
                        json.put("FIRST_NAME", (Object)((Hashtable<K, String>)aduser).get("givenName"));
                    }
                    if (aduser.containsKey("initials")) {
                        json.put("MIDDLE_NAME", (Object)((Hashtable<K, String>)aduser).get("initials"));
                    }
                    if (aduser.containsKey("sn")) {
                        json.put("LAST_NAME", (Object)((Hashtable<K, String>)aduser).get("sn"));
                    }
                    if (aduser.containsKey("displayName")) {
                        json.put("DISPLAY_NAME", (Object)((Hashtable<K, String>)aduser).get("displayName"));
                    }
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(ADEPActiveDirectoryAuthHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
