package com.me.mdm.server.privacy;

import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.Criteria;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.i18n.I18N;
import java.util.logging.Logger;

public class PrivacyCustomMessageHandler
{
    Logger logger;
    private static PrivacyCustomMessageHandler handler;
    
    public PrivacyCustomMessageHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static PrivacyCustomMessageHandler getInstance() {
        if (PrivacyCustomMessageHandler.handler == null) {
            PrivacyCustomMessageHandler.handler = new PrivacyCustomMessageHandler();
        }
        return PrivacyCustomMessageHandler.handler;
    }
    
    public String getCustomMessage(final String customMessageName, final Long customerId) {
        String customMessage = null;
        try {
            customMessage = I18N.getMsg(this.getDefaultMessage(customMessageName), new Object[0]);
            final Criteria customerCriteria = this.getCustomerCriteria(customerId);
            final Criteria customCriteria = this.getCustomerMessageName(customMessageName);
            final DataObject DO = MDMUtil.getPersistence().get("MDMPrivacyCustomMsg", customerCriteria.and(customCriteria));
            if (!DO.isEmpty()) {
                final Row msgRow = DO.getRow("MDMPrivacyCustomMsg");
                customMessage = (String)msgRow.get("MESSAGE");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getCustomMessage", e);
        }
        return customMessage;
    }
    
    public HashMap<String, String> getCustomMessage(final Long customerId) {
        final HashMap<String, String> customMessage = this.getDefaultMessageHash();
        try {
            final Criteria customerCriteria = this.getCustomerCriteria(customerId);
            final DataObject DO = MDMUtil.getPersistence().get("MDMPrivacyCustomMsg", customerCriteria);
            if (!DO.isEmpty()) {
                final Iterator item = DO.getRows("MDMPrivacyCustomMsg");
                while (item.hasNext()) {
                    final Row msgRow = item.next();
                    final String customMsg = (String)msgRow.get("MESSAGE");
                    final String customMsgName = (String)msgRow.get("MESSAGE_NAME");
                    customMessage.put(customMsgName.toLowerCase(), customMsg);
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getCustomMessage", e);
        }
        return customMessage;
    }
    
    public void addOrUpdateCustomMessage(final String msgName, final String msg, final Long customerId) {
        try {
            final Criteria customerCriteria = this.getCustomerCriteria(customerId);
            final Criteria customCriteria = this.getCustomerMessageName(msgName);
            final DataObject DO = MDMUtil.getPersistence().get("MDMPrivacyCustomMsg", customerCriteria.and(customCriteria));
            Row msgRow = null;
            if (!DO.isEmpty()) {
                msgRow = DO.getRow("MDMPrivacyCustomMsg");
                msgRow.set("MESSAGE", (Object)msg);
                DO.updateRow(msgRow);
                MDMUtil.getPersistence().update(DO);
            }
            else {
                msgRow = new Row("MDMPrivacyCustomMsg");
                msgRow.set("MESSAGE_NAME", (Object)msgName);
                msgRow.set("MESSAGE", (Object)msg);
                msgRow.set("CUSTOMER_ID", (Object)customerId);
                DO.addRow(msgRow);
                MDMUtil.getPersistence().add(DO);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getCustomMessage", e);
        }
    }
    
    private HashMap<String, String> getDefaultMessageHash() {
        final HashMap<String, String> privacyMessage = new HashMap<String, String>();
        privacyMessage.put("FetchIMEI".toLowerCase(), "mdm.privacy.defaultMsg.fetch_IMEI");
        privacyMessage.put("FetchImsi".toLowerCase(), "mdm.privacy.defaultMsg.fetch_Imsi");
        privacyMessage.put("FetchSerialNumber".toLowerCase(), "mdm.privacy.defaultMsg.fetch_serial_number");
        privacyMessage.put("FetchDeviceName".toLowerCase(), "mdm.privacy.defaultMsg.fetch_device_name");
        privacyMessage.put("FetchPhoneNumber".toLowerCase(), "mdm.privacy.defaultMsg.fetch_phone_number");
        privacyMessage.put("FetchAppInfo".toLowerCase(), "mdm.privacy.defaultMsg.fetch_installed_app");
        privacyMessage.put("FetchLocation".toLowerCase(), "mdm.privacy.defaultMsg.fetch_location");
        privacyMessage.put("FetchMacAddress".toLowerCase(), "mdm.privacy.defaultMsg.fetch_mac_address");
        privacyMessage.put("FetchWifiSSID".toLowerCase(), "mdm.privacy.defaultMsg.fetch_wifi_ssid");
        return privacyMessage;
    }
    
    private String getDefaultMessage(final String customMessageName) {
        return this.getDefaultMessageHash().get(customMessageName.toLowerCase());
    }
    
    private Criteria getCustomerCriteria(final Long customerId) {
        final Criteria cCustomerId = new Criteria(new Column("MDMPrivacyCustomMsg", "CUSTOMER_ID"), (Object)customerId, 0);
        return cCustomerId;
    }
    
    private Criteria getCustomerMessageName(final String msgName) {
        final Criteria cCustomerId = new Criteria(new Column("MDMPrivacyCustomMsg", "MESSAGE_NAME"), (Object)msgName, 0, false);
        return cCustomerId;
    }
    
    static {
        PrivacyCustomMessageHandler.handler = null;
    }
}
