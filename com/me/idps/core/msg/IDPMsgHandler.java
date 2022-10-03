package com.me.idps.core.msg;

import java.util.Hashtable;
import com.me.idps.core.util.IdpsUtil;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import java.util.Properties;
import com.me.idps.core.crud.DMDomainDataHandler;
import com.me.devicemanagement.framework.webclient.message.MsgHandler;

public class IDPMsgHandler implements MsgHandler
{
    private String getPasswordChangedRead() {
        String domains = "";
        try {
            final List domainList = DMDomainDataHandler.getInstance().getAllDMManagedProps(null);
            for (int i = 0; i < domainList.size(); ++i) {
                final Properties props = domainList.get(i);
                final String domainName = ((Hashtable<K, String>)props).get("NAME");
                final Boolean validationStatus = ((Hashtable<K, Boolean>)props).get("VALIDATION_STATUS");
                if (validationStatus.equals(Boolean.FALSE)) {
                    if (domains.length() == 0) {
                        domains = domainName;
                    }
                    else {
                        domains = domains + ", " + domainName;
                    }
                }
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception ", e);
        }
        return domains;
    }
    
    private String getPasswordChangedDomainWrite() {
        String domains = "";
        try {
            final List domainList = DMDomainDataHandler.getInstance().getAllDMManagedProps(null);
            for (int i = 0; i < domainList.size(); ++i) {
                final Properties props = domainList.get(i);
                final String domainName = ((Hashtable<K, String>)props).get("NAME");
                final Boolean validationStatus = ((Hashtable<K, Boolean>)props).get("VALIDATION_STATUS");
                if (validationStatus.equals(Boolean.FALSE)) {
                    final String domainEditURL = "&nbsp;<a href=\"#\" onclick=\"javascript:editDomainDetails('" + domainName + "');\">" + domainName + "</a>";
                    if (domains.length() == 0) {
                        domains = domainEditURL;
                    }
                    else {
                        domains = domains + "," + domainEditURL;
                    }
                }
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception ", e);
        }
        return domains;
    }
    
    private Long getCustomerID(final Properties userDefined, final HttpServletRequest request) {
        if (userDefined != null && userDefined.get("CUSTOMER_ID") != null) {
            return ((Hashtable<K, Long>)userDefined).get("CUSTOMER_ID");
        }
        return MSPWebClientUtil.getCustomerID(request);
    }
    
    public Properties modifyMsgProperty(final Properties msgProperties, final Properties userDefined, final HttpServletRequest request) {
        try {
            final Object[] args = null;
            final Long customerID = this.getCustomerID(userDefined, request);
            final String msgName = ((Hashtable<K, String>)msgProperties).get("MSG_NAME");
            final String msgTitle = ((Hashtable<K, String>)msgProperties).get("MSG_TITLE");
            String msgContent = ((Hashtable<K, String>)msgProperties).get("MSG_CONTENT");
            if (!IdpsUtil.isStringEmpty(msgName)) {
                if (msgName.equalsIgnoreCase("IDP_PASSWORD_CHANGED_WRITE")) {
                    final String domains = this.getPasswordChangedDomainWrite();
                    msgContent = msgContent.replace("{0}", domains);
                    ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                }
                else if (msgName.equalsIgnoreCase("IDP_PASSWORD_CHANGED_READ")) {
                    final String domains = this.getPasswordChangedRead();
                    msgContent = msgContent.replace("{0}", domains);
                    ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                }
                else if (msgName.equalsIgnoreCase("IDP_AZURE_OAUTH_MSG")) {
                    msgContent = msgContent.replace("{0}", "");
                    ((Hashtable<String, String>)msgProperties).put("MSG_CONTENT", msgContent);
                }
            }
        }
        catch (final Exception e) {
            IDPSlogger.ERR.log(Level.SEVERE, "Exception in IDPMsgHandler", e);
        }
        return msgProperties;
    }
}
