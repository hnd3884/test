package com.me.mdm.server.apps.appupdatepolicy;

import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.device.api.model.apps.AppUpdatePolicyModel;
import java.util.logging.Logger;

public class AppUpdatePolicyModifySchedulerHandler
{
    private static AppUpdatePolicyModifySchedulerHandler appUpdatePolicyModifySchedulerHandler;
    private Logger logger;
    private static final String SCHEDULE_ASSOCIATED_COUNT = "SCHEDULE_ASSOCIATED_COUNT";
    
    public AppUpdatePolicyModifySchedulerHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public static AppUpdatePolicyModifySchedulerHandler getInstance() {
        if (AppUpdatePolicyModifySchedulerHandler.appUpdatePolicyModifySchedulerHandler == null) {
            AppUpdatePolicyModifySchedulerHandler.appUpdatePolicyModifySchedulerHandler = new AppUpdatePolicyModifySchedulerHandler();
        }
        return AppUpdatePolicyModifySchedulerHandler.appUpdatePolicyModifySchedulerHandler;
    }
    
    public void validateAndRemoveExistingSchedule(final AppUpdatePolicyModel appUpdatePolicyModel) throws Exception {
        final Long profileId = appUpdatePolicyModel.getProfileId();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
        selectQuery.addJoin(new Join("Profile", "RecentPubProfileToColln", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentPubProfileToColln", "AppUpdatePolicyCollnToScheduleRepo", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1));
        selectQuery.addJoin(new Join("AppUpdatePolicyCollnToScheduleRepo", "ScheduleRepository", new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 1));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)profileId, 0));
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppUpdatePolicyCollnToScheduleRepo"));
        subQuery.addJoin(new Join("AppUpdatePolicyCollnToScheduleRepo", "RecentPubProfileToColln", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final Column collectionCountColumn = new Column("AppUpdatePolicyCollnToScheduleRepo", "COLLECTION_ID").count();
        collectionCountColumn.setColumnAlias("SCHEDULE_ASSOCIATED_COUNT");
        final Column scheduleIdColumn = new Column("AppUpdatePolicyCollnToScheduleRepo", "SCHEDULE_ID");
        scheduleIdColumn.setColumnAlias("SCHEDULE_ID");
        final Column groupByColumn = new Column("AppUpdatePolicyCollnToScheduleRepo", "SCHEDULE_ID");
        final List groupByColumnList = new ArrayList();
        groupByColumnList.add(groupByColumn);
        subQuery.addSelectColumn(collectionCountColumn);
        subQuery.addSelectColumn(scheduleIdColumn);
        subQuery.setGroupByClause(new GroupByClause(groupByColumnList));
        final DerivedTable derivedTable = new DerivedTable("SCHEDULE_ASSOCIATED_COUNT", (Query)subQuery);
        selectQuery.addJoin(new Join(Table.getTable("AppUpdatePolicyCollnToScheduleRepo"), (Table)derivedTable, new String[] { "SCHEDULE_ID" }, new String[] { "SCHEDULE_ID" }, 1));
        selectQuery.addSelectColumn(new Column("SCHEDULE_ASSOCIATED_COUNT", "SCHEDULE_ASSOCIATED_COUNT"));
        selectQuery.addSelectColumn(new Column("AppUpdatePolicyCollnToScheduleRepo", "SCHEDULE_ID"));
        selectQuery.addSelectColumn(new Column("ScheduleRepository", "SCHEDULE_NAME"));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        if (dmDataSetWrapper.next()) {
            final Long scheduleId = (Long)dmDataSetWrapper.getValue("SCHEDULE_ID");
            final Integer associatedCount = (Integer)dmDataSetWrapper.getValue("SCHEDULE_ASSOCIATED_COUNT");
            final String scheduleName = (String)dmDataSetWrapper.getValue("SCHEDULE_NAME");
            if (scheduleId != null && associatedCount == 1) {
                this.logger.log(Level.INFO, "Removing schedule {0} {1}", new Object[] { scheduleName, scheduleId });
                ApiFactoryProvider.getSchedulerAPI().removeScheduler(scheduleName);
                DataAccess.delete("ScheduleRepository", new Criteria(Column.getColumn("ScheduleRepository", "SCHEDULE_ID"), (Object)scheduleId, 0));
            }
        }
    }
    
    static {
        AppUpdatePolicyModifySchedulerHandler.appUpdatePolicyModifySchedulerHandler = null;
    }
}
