package com.me.mdm.server.metracker;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import com.me.mdm.apps.handler.AppsAutoDeployment;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

public class MEMDMTrackAgentDeploymentImpl extends MEMDMTrackerConstants
{
    private Properties mdmTrackerProperties;
    public Logger logger;
    
    public MEMDMTrackAgentDeploymentImpl() {
        this.mdmTrackerProperties = new Properties();
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public Properties getTrackerProperties() {
        try {
            if (!this.mdmTrackerProperties.isEmpty()) {
                this.mdmTrackerProperties = new Properties();
            }
            this.agentUsageRatios();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getting MDMTrackerProperties for agent deployment : ", e);
        }
        return this.mdmTrackerProperties;
    }
    
    public Integer getNumberOfDevices(final Long customerID, final Integer platform, final Integer supportedDevices) {
        int count = 0;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            Criteria criteria = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            if (platform == 1 && supportedDevices == 16) {
                criteria = new Criteria(Column.getColumn("ManagedDevice", "AGENT_TYPE"), (Object)8, 0);
            }
            if (customerID != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            }
            if (platform != null) {
                criteria = criteria.and(new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0));
            }
            query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID").distinct().count());
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            count = DBUtil.getRecordCount(query);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to get the number of MacOS devices in the server", e);
        }
        return count;
    }
    
    private Integer getAgentUsageCount(final Integer agentType) throws Exception {
        int count = 0;
        final String bundleidentifier = AppsAutoDeployment.getInstance().getAgentHandler(agentType).getBundleIdentifier();
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
            query.addJoin(new Join("ManagedDevice", "CollnToResources", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            query.addJoin(new Join("CollnToResources", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            query.addJoin(new Join("AppGroupToCollection", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            Criteria criteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleidentifier, 0);
            criteria = criteria.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
            query.setCriteria(criteria);
            query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID").distinct().count());
            count = DBUtil.getRecordCount(query);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to perform ME tracking for agent deployment", e);
            throw e;
        }
        return count;
    }
    
    private void agentUsageRatios() {
        final List<Integer> agentList = new ArrayList<Integer>() {
            {
                this.add(2);
            }
        };
        final List<JSONObject> usageRatio = new ArrayList<JSONObject>();
        final Iterator<Integer> iterator = agentList.iterator();
        try {
            while (iterator.hasNext()) {
                final Integer agentType = iterator.next();
                final Integer platformType = AppsAutoDeployment.getInstance().getAgentHandler(agentType).getPlatformType();
                final Integer supportedDevices = AppsAutoDeployment.getInstance().getAgentHandler(agentType).getSupportedDevices();
                final Integer totalDevices = this.getNumberOfDevices(null, platformType, supportedDevices);
                final JSONObject agentJson = new JSONObject();
                agentJson.put("AGENT_ID", (Object)agentType);
                Double ratio;
                if (totalDevices != 0) {
                    ratio = Double.parseDouble(this.getAgentUsageCount(agentType).toString()) / Double.parseDouble(totalDevices.toString());
                }
                else {
                    ratio = -1.0;
                }
                agentJson.put("MDM_AGENT_USAGE_RATIO", (Object)ratio);
                usageRatio.add(agentJson);
            }
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("MDM_AGENT_USAGE_RATIO_LIST", (Object)new JSONArray((Collection)usageRatio));
            this.mdmTrackerProperties.setProperty("MDM_AGENT_USAGE_RATIO", jsonObject.toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Unable to perform ME tracking for agent deployment", e);
        }
    }
}
