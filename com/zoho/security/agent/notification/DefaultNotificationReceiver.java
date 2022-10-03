package com.zoho.security.agent.notification;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import com.zoho.security.agent.Components;
import java.util.logging.Logger;

public class DefaultNotificationReceiver implements NotificationReceiver
{
    static NotificationReceiver appfirewallNotification;
    static NotificationReceiver propertyNotification;
    static NotificationReceiver hashNotification;
    static NotificationReceiver inventoryNotification;
    static NotificationReceiver attackDiscoveryNotification;
    static final Logger LOGGER;
    
    public static NotificationReceiver getInstance(final Components.COMPONENT component) {
        switch (component) {
            case APPFIREWALL: {
                if (DefaultNotificationReceiver.appfirewallNotification == null) {
                    DefaultNotificationReceiver.appfirewallNotification = new AppFirewallNotification();
                }
                return DefaultNotificationReceiver.appfirewallNotification;
            }
            case HASH: {
                if (DefaultNotificationReceiver.hashNotification == null) {
                    DefaultNotificationReceiver.hashNotification = new HashNotification();
                }
                return DefaultNotificationReceiver.hashNotification;
            }
            case PROPERTY: {
                if (DefaultNotificationReceiver.propertyNotification == null) {
                    DefaultNotificationReceiver.propertyNotification = new PropertyNotification();
                }
                return DefaultNotificationReceiver.propertyNotification;
            }
            case INVENTORY: {
                if (DefaultNotificationReceiver.inventoryNotification == null) {
                    DefaultNotificationReceiver.inventoryNotification = new InventoryNotification();
                }
                return DefaultNotificationReceiver.inventoryNotification;
            }
            case ATTACK_DISCOVERY: {
                if (DefaultNotificationReceiver.attackDiscoveryNotification == null) {
                    DefaultNotificationReceiver.attackDiscoveryNotification = new WAFAttackDiscoveryNotification();
                }
                return DefaultNotificationReceiver.attackDiscoveryNotification;
            }
            default: {
                return null;
            }
        }
    }
    
    static List<String> getXMLHashesAsList(final JSONArray jsonArray) {
        final List<String> list = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject hashObj = jsonArray.getJSONObject(i);
            list.add(hashObj.getString("HASH"));
        }
        return list;
    }
    
    @Override
    public boolean receive(final Components.COMPONENT component, final Components.COMPONENT_NAME subComponent, final JSONObject dataObj) {
        return false;
    }
    
    @Override
    public Object getRecentDataOnChange(final JSONObject propertyObj, final Components.COMPONENT_NAME subComponent) {
        return null;
    }
    
    @Override
    public boolean isChangePushEnabled(final Components.COMPONENT_NAME subComponent) {
        return false;
    }
    
    static {
        DefaultNotificationReceiver.appfirewallNotification = null;
        DefaultNotificationReceiver.propertyNotification = null;
        DefaultNotificationReceiver.hashNotification = null;
        DefaultNotificationReceiver.inventoryNotification = null;
        DefaultNotificationReceiver.attackDiscoveryNotification = null;
        LOGGER = Logger.getLogger(DefaultNotificationReceiver.class.getName());
    }
}
