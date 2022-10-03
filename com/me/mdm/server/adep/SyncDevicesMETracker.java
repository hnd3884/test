package com.me.mdm.server.adep;

import java.util.Iterator;
import com.me.mdm.server.metracker.MEMDMTrackParamManager;
import com.adventnet.ds.query.SelectQuery;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.logging.Level;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.me.mdm.server.tracker.MDMTrackerUtil;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class SyncDevicesMETracker
{
    static Logger LOGGER;
    
    public void trackSyncedDevicesCount() {
        DMDataSetWrapper dataSet = null;
        try {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppleDEPDeviceForEnrollment"));
            sQuery.addJoin(new Join("AppleDEPDeviceForEnrollment", "DeviceForEnrollment", new String[] { "ENROLLMENT_DEVICE_ID" }, new String[] { "ENROLLMENT_DEVICE_ID" }, 2));
            final Criteria likeTV = new Criteria(new Column("AppleDEPDeviceForEnrollment", "MODEL_NAME"), (Object)"*TV*", 2);
            final Criteria likeMac = new Criteria(new Column("AppleDEPDeviceForEnrollment", "MODEL_NAME"), (Object)"*Mac*", 2);
            final Criteria likePhone = new Criteria(new Column("AppleDEPDeviceForEnrollment", "MODEL_NAME"), (Object)"*Phone*", 2);
            final Criteria likePad = new Criteria(new Column("AppleDEPDeviceForEnrollment", "MODEL_NAME"), (Object)"*Pad*", 2);
            final Criteria likePod = new Criteria(new Column("AppleDEPDeviceForEnrollment", "MODEL_NAME"), (Object)"*Pod*", 2);
            final CaseExpression caseTV = new CaseExpression("DEP_HISTORY_COUNT_TV");
            caseTV.addWhen(likeTV, (Object)new Column("AppleDEPDeviceForEnrollment", "MODEL_NAME"));
            final CaseExpression caseMac = new CaseExpression("DEP_HISTORY_COUNT_MAC");
            caseMac.addWhen(likeMac, (Object)new Column("AppleDEPDeviceForEnrollment", "MODEL_NAME"));
            final CaseExpression casePhone = new CaseExpression("DEP_HISTORY_COUNT_PHONE");
            casePhone.addWhen(likePhone, (Object)new Column("AppleDEPDeviceForEnrollment", "MODEL_NAME"));
            final CaseExpression casePad = new CaseExpression("DEP_HISTORY_COUNT_PAD");
            casePad.addWhen(likePad, (Object)new Column("AppleDEPDeviceForEnrollment", "MODEL_NAME"));
            final CaseExpression casePod = new CaseExpression("DEP_HISTORY_COUNT_POD");
            casePod.addWhen(likePod, (Object)new Column("AppleDEPDeviceForEnrollment", "MODEL_NAME"));
            final MDMTrackerUtil trackerUtil = new MDMTrackerUtil();
            sQuery.addSelectColumn(trackerUtil.getCountCaseExpressionColumn(caseTV, 4, "DEP_HISTORY_COUNT_TV"));
            sQuery.addSelectColumn(trackerUtil.getCountCaseExpressionColumn(caseMac, 4, "DEP_HISTORY_COUNT_MAC"));
            sQuery.addSelectColumn(trackerUtil.getCountCaseExpressionColumn(casePhone, 4, "DEP_HISTORY_COUNT_PHONE"));
            sQuery.addSelectColumn(trackerUtil.getCountCaseExpressionColumn(casePad, 4, "DEP_HISTORY_COUNT_PAD"));
            sQuery.addSelectColumn(trackerUtil.getCountCaseExpressionColumn(casePod, 4, "DEP_HISTORY_COUNT_POD"));
            sQuery.addSelectColumn(new Column("DeviceForEnrollment", "CUSTOMER_ID"));
            final ArrayList<Column> groupByColumnsList = new ArrayList<Column>();
            groupByColumnsList.add(new Column("DeviceForEnrollment", "CUSTOMER_ID"));
            final GroupByClause groupByColumn = new GroupByClause((List)groupByColumnsList);
            sQuery.setGroupByClause(groupByColumn);
            SyncDevicesMETracker.LOGGER.log(Level.INFO, "TRACK SYNC DEVICES QUERY {0}", RelationalAPI.getInstance().getSelectSQL((Query)sQuery));
            dataSet = DMDataSetWrapper.executeQuery((Object)sQuery);
            final JSONArray countDetails = new JSONArray();
            final JSONObject perCustomerDetails = new JSONObject();
            while (dataSet.next()) {
                SyncDevicesMETracker.LOGGER.log(Level.INFO, "CUSTOMER ID {0}, TV Count {1}, Mac Count {2}, Phone Count {3}, Pad Count {4}, Pod Count {5}", new Object[] { dataSet.getValue("CUSTOMER_ID"), dataSet.getValue("DEP_HISTORY_COUNT_TV"), dataSet.getValue("DEP_HISTORY_COUNT_MAC"), dataSet.getValue("DEP_HISTORY_COUNT_PHONE"), dataSet.getValue("DEP_HISTORY_COUNT_PAD"), dataSet.getValue("DEP_HISTORY_COUNT_POD") });
                perCustomerDetails.put("DEP_HISTORY_COUNT_TV", (Object)dataSet.getValue("DEP_HISTORY_COUNT_TV").toString());
                perCustomerDetails.put("DEP_HISTORY_COUNT_MAC", (Object)dataSet.getValue("DEP_HISTORY_COUNT_MAC").toString());
                perCustomerDetails.put("DEP_HISTORY_COUNT_PHONE", (Object)dataSet.getValue("DEP_HISTORY_COUNT_PHONE").toString());
                perCustomerDetails.put("DEP_HISTORY_COUNT_PAD", (Object)dataSet.getValue("DEP_HISTORY_COUNT_PAD").toString());
                perCustomerDetails.put("DEP_HISTORY_COUNT_POD", (Object)dataSet.getValue("DEP_HISTORY_COUNT_POD").toString());
                perCustomerDetails.put("CUSTOMER_ID", (Object)dataSet.getValue("CUSTOMER_ID").toString());
                countDetails.put((Object)perCustomerDetails);
            }
            this.addCountHistoryToMETracking(countDetails);
        }
        catch (final Exception e) {
            SyncDevicesMETracker.LOGGER.log(Level.SEVERE, "Error while trackSyncedDevicesCount() ", e);
        }
    }
    
    private void addCountHistoryToMETracking(final JSONArray countDetails) throws Exception {
        for (int i = 0; i < countDetails.length(); ++i) {
            final JSONObject json = countDetails.getJSONObject(i);
            final Iterator<String> keys = json.keys();
            final Long customerID = Long.parseLong(json.get("CUSTOMER_ID").toString());
            while (keys.hasNext()) {
                final String key = keys.next();
                if (!key.equals("CUSTOMER_ID")) {
                    final Integer existingCount = Integer.parseInt(MEMDMTrackParamManager.getInstance().getTrackParamValueFromDB(customerID, "Enrollment_Module", key, "0"));
                    final Integer newCount = Integer.parseInt(json.get(key).toString());
                    SyncDevicesMETracker.LOGGER.log(Level.INFO, "addCountHistoryToMETracking() {0} existing, new: {1} {2}", new Object[] { key, existingCount.toString(), newCount.toString() });
                    if (newCount <= existingCount) {
                        continue;
                    }
                    MEMDMTrackParamManager.getInstance().addOrUpdateTrackParam(customerID, "Enrollment_Module", key, newCount.toString());
                }
            }
        }
    }
    
    static {
        SyncDevicesMETracker.LOGGER = Logger.getLogger("MDMEnrollment");
    }
}
