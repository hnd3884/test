package com.me.mdm.mdmmigration;

import java.util.Hashtable;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MigrationFetchTask implements SchedulerExecutionInterface
{
    public void executeTask(final Properties properties) {
        final Long config_id = Long.parseLong(((Hashtable<K, Object>)properties).get("config_id").toString());
        final int service_id = Integer.parseInt(((Hashtable<K, Object>)properties).get("service_id").toString());
        final Long customer_id = Long.parseLong(((Hashtable<K, Object>)properties).get("customer_id").toString());
        final Long user_id = Long.parseLong(((Hashtable<K, Object>)properties).get("user_id").toString());
        final String type = ((Hashtable<K, Object>)properties).get("type").toString();
        if (type.equalsIgnoreCase("FETCH_ALL")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllDevices(config_id, service_id, customer_id, user_id);
            if (service_id == 6 || service_id == 3) {
                new APIServiceDataHandler().setMigrationSuccessStatus(config_id, customer_id, "DEVICES_STATUS");
            }
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllUsers(config_id, service_id, customer_id, user_id);
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllGroups(config_id, service_id, customer_id, user_id);
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllApps(config_id, service_id, customer_id, user_id);
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllProfiles(config_id, service_id, customer_id, user_id);
            if (service_id == 6 || service_id == 3) {
                new APIServiceDataHandler().setMigrationSuccessStatus(config_id, customer_id, "GROUPS_STATUS");
            }
        }
        else if (type.equalsIgnoreCase("FETCH_DEVICES")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllDevices(config_id, service_id, customer_id, user_id);
            if (service_id == 6 || service_id == 3) {
                new APIServiceDataHandler().setMigrationSuccessStatus(config_id, customer_id, "DEVICES_STATUS");
            }
        }
        else if (type.equalsIgnoreCase("FETCH_USERS")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllUsers(config_id, service_id, customer_id, user_id);
        }
        else if (type.equalsIgnoreCase("FETCH_GROUPS")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllGroups(config_id, service_id, customer_id, user_id);
            if (service_id == 6 || service_id == 3) {
                new APIServiceDataHandler().setMigrationSuccessStatus(config_id, customer_id, "GROUPS_STATUS");
            }
        }
        else if (type.equalsIgnoreCase("FETCH_APPS")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllApps(config_id, service_id, customer_id, user_id);
        }
        else if (type.equalsIgnoreCase("FETCH_PROFILES")) {
            MigrationAPIRequestHandler.getInstance(service_id).fetchAllProfiles(config_id, service_id, customer_id, user_id);
        }
    }
}
