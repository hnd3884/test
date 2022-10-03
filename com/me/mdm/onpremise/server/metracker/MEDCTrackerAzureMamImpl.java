package com.me.mdm.onpremise.server.metracker;

import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.me.mdm.server.tracker.MDMCoreQuery;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import org.json.JSONObject;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.metrack.MEDMTracker;
import com.me.mdm.server.tracker.MDMTrackerConstants;

public class MEDCTrackerAzureMamImpl extends MDMTrackerConstants implements MEDMTracker
{
    private Logger logger;
    private String sourceClass;
    
    public MEDCTrackerAzureMamImpl() {
        this.logger = Logger.getLogger("METrackLog");
        this.sourceClass = "MEDCTrackerAzureMamImpl";
    }
    
    public Properties getTrackerProperties() {
        final Properties props = new Properties();
        try {
            final JSONObject p = new JSONObject();
            SyMLogger.info(this.logger, this.sourceClass, "getProperties", "MDM AzureMam implementation starts...");
            final SelectQuery selectQuery = MDMCoreQuery.getInstance().getAzureMamPropsCountQuery();
            final ArrayList<Column> groupByColumnsList = new ArrayList<Column>();
            groupByColumnsList.add(new Column("CustomerInfo", "CUSTOMER_ID"));
            final GroupByClause groupByColumn = new GroupByClause((List)groupByColumnsList);
            selectQuery.setGroupByClause(groupByColumn);
            final DMDataSetWrapper dataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
            while (dataSetWrapper.next()) {
                p.put("AZURE_MAM_POLICY_SUCCESS", dataSetWrapper.getValue("AZURE_MAM_POLICY_SUCCESS"));
            }
            props.setProperty("AZURE_MAM_POLICY", p.toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in  getAzureMamPropsCountQuery count", e);
        }
        return props;
    }
}
