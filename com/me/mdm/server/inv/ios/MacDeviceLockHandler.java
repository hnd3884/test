package com.me.mdm.server.inv.ios;

import java.util.Hashtable;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.server.alerts.MDMAlertConstants;
import java.util.Properties;
import com.me.mdm.server.alerts.MDMAlertMailGeneratorUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MacDeviceLockHandler extends AppleDeviceLockHandler
{
    private static final Logger LOGGER;
    
    @Override
    public void checkAndSendEmail(final JSONObject emailDetails) {
        try {
            final Long resourceId = (Long)emailDetails.get("resourceId");
            final Long customerId = (Long)emailDetails.get("customerId");
            final SelectQuery lockscreenMessageQuery = this.getLockscreenMessageQuery(resourceId);
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(lockscreenMessageQuery);
            if (!dataObject.isEmpty()) {
                final Row lockMessageRow = dataObject.getFirstRow("MdDeviceLockMessage");
                final boolean needToSend = (boolean)lockMessageRow.get("SEND_MAIL_TO_USER");
                if (needToSend) {
                    final String unlockPin = (String)lockMessageRow.get("UNLOCK_PIN");
                    final Row userDetails = dataObject.getFirstRow("ManagedUser");
                    final String userEmailAddress = (String)userDetails.get("EMAIL_ADDRESS");
                    final Row managedUserRow = dataObject.getFirstRow("userResourceDetail");
                    final Row deviceRow = dataObject.getFirstRow("Resource");
                    final MDMAlertMailGeneratorUtil alertMailGeneratorUtil = new MDMAlertMailGeneratorUtil();
                    final Properties mailProperties = new Properties();
                    ((Hashtable<String, Object>)mailProperties).put("$device_name$", deviceRow.get("NAME"));
                    ((Hashtable<String, Object>)mailProperties).put("$user_name$", managedUserRow.get("NAME"));
                    ((Hashtable<String, String>)mailProperties).put("$passcode$", unlockPin);
                    ((Hashtable<String, String>)mailProperties).put("$user_emailid$", userEmailAddress);
                    alertMailGeneratorUtil.sendMail(MDMAlertConstants.DEVICE_LOCK_PIN_ALERT, "MDM_DEVICE_LOCK_PIN_ALERT", customerId, mailProperties);
                    MacDeviceLockHandler.LOGGER.log(Level.INFO, "Sending the mail for the resource: {0} for unlockpin.", new Object[] { resourceId });
                }
            }
        }
        catch (final JSONException ex) {
            MacDeviceLockHandler.LOGGER.log(Level.SEVERE, "Exception while getting the resource details", (Throwable)ex);
        }
        catch (final Exception e) {
            MacDeviceLockHandler.LOGGER.log(Level.SEVERE, "Exception in adding mail to device", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
