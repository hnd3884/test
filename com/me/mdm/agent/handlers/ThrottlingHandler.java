package com.me.mdm.agent.handlers;

import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SortColumn;
import org.json.JSONObject;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.DMDataSetWrapper;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThrottlingHandler
{
    private static final int THROTTLE_DEFAULT_DEVICE_COUNT = 3;
    private static final int NO_MIGRATION_NEEDED = 0;
    private static final int YET_TO_INITIATE_MIGRATION = 1;
    private static final int MIGRATION_COMPLETE = 2;
    private static final int SAMPLING_PHASE_INPROGRESS = 3;
    private static final int FIRST_MIGRATION_PHASE_TO_START = 4;
    private static final int FIRST_MIGRATION_PHASE_IN_PROGRESS = 5;
    private static final int SECOND_MIGRATION_PHASE_TO_START = 6;
    private static final int SECOND_MIGRATION_PHASE_IN_PROGRESS = 7;
    private static final int MAX_DEVICE_COUNT = 100000;
    private static final float FIRST_MIGRATION_PHASE_PERCENTAGE = 40.0f;
    private static final float FIRST_MIGRATION_PHASE_COMPLETION_PERCENTAGE = 80.0f;
    private static final String MIGRATION_COMPLETE_PARAM = "MigrationComplete";
    private static final int INACTIVE_DAYS = 90;
    private static final int NO_PROGRESS_THRESHOLD_DAYS = 3;
    private static final int NO_LAST_CONTACT_CRITERIA = -99999;
    private static final String GLOBAL_DISABLE = "global_disable";
    public Logger logger;
    
    public ThrottlingHandler() {
        this.logger = Logger.getLogger("MigrationEventLogger");
    }
    
    private void computeAndUpdateMigrationSummary(final Long customerID) throws Exception {
        final Long computerAt = System.currentTimeMillis();
        this.logger.log(Level.INFO, "[Migration][Summary][Update] : Migration Summary computed at <{0}>", computerAt);
        this.updateSummaryForPlatform(3, 1, customerID, computerAt);
        this.updateSummaryForPlatform(3, 2, customerID, computerAt);
        this.updateSummaryForPlatform(1, 2, customerID, computerAt);
        this.updateSummaryForPlatform(2, 1, customerID, computerAt);
        this.logger.log(Level.INFO, "[Migration][Summary][Update] : Migration Summary compution completed at <{0}>", System.currentTimeMillis());
    }
    
    private void updateSummaryForPlatform(final int platformType, final int cmdRepType, final Long customerID, final Long computerAt) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationStatus"));
        selectQuery.addJoin(new Join("MigrationStatus", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MigrationStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria cmdRepCriteria = new Criteria(Column.getColumn("MigrationStatus", "COMMAND_REPOSITORY_TYPE"), (Object)cmdRepType, 0);
        final Criteria pltformCriteria = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0);
        selectQuery.setCriteria(customerCriteria.and(cmdRepCriteria).and(pltformCriteria));
        final CaseExpression yetToApplyCount = new CaseExpression("YET_TO_APPLY_COUNT");
        yetToApplyCount.addWhen(new Criteria(Column.getColumn("MigrationStatus", "COMMAND_STATUS"), (Object)0, 0), (Object)new Column("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(yetToApplyCount, 4, "YET_TO_APPLY_COUNT"));
        final CaseExpression commandAddedCount = new CaseExpression("COMMAND_ADDED_COUNT");
        commandAddedCount.addWhen(new Criteria(Column.getColumn("MigrationStatus", "COMMAND_STATUS"), (Object)1, 0), (Object)new Column("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(commandAddedCount, 4, "COMMAND_ADDED_COUNT"));
        final CaseExpression initatedCount = new CaseExpression("INITATED_COUNT");
        initatedCount.addWhen(new Criteria(Column.getColumn("MigrationStatus", "COMMAND_STATUS"), (Object)2, 0), (Object)new Column("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(initatedCount, 4, "INITATED_COUNT"));
        final CaseExpression agentFailedCount = new CaseExpression("AGENT_FAILED_COUNT");
        agentFailedCount.addWhen(new Criteria(Column.getColumn("MigrationStatus", "COMMAND_STATUS"), (Object)3, 0), (Object)new Column("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(agentFailedCount, 4, "AGENT_FAILED_COUNT"));
        final CaseExpression successCount = new CaseExpression("SUCCESS_COUNT");
        successCount.addWhen(new Criteria(Column.getColumn("MigrationStatus", "COMMAND_STATUS"), (Object)5, 0), (Object)new Column("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(successCount, 4, "SUCCESS_COUNT"));
        final CaseExpression failedCount = new CaseExpression("FAILED_COUNT");
        failedCount.addWhen(new Criteria(Column.getColumn("MigrationStatus", "COMMAND_STATUS"), (Object)4, 0), (Object)new Column("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(MDMUtil.getInstance().getDistinctCountCaseExpressionColumn(failedCount, 4, "FAILED_COUNT"));
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dmDataSetWrapper.next()) {
            final Row migrationSumamry = new Row("MigrationSummary");
            migrationSumamry.set("PLATFORM", (Object)platformType);
            migrationSumamry.set("COMMAND_REPOSITORY_TYPE", (Object)cmdRepType);
            migrationSumamry.set("CUSTOMER_ID", (Object)customerID);
            migrationSumamry.set("COMPUTED_AT", (Object)computerAt);
            migrationSumamry.set("YET_TO_APPLY_COUNT", dmDataSetWrapper.getValue("YET_TO_APPLY_COUNT"));
            migrationSumamry.set("COMMAND_ADDED_COUNT", dmDataSetWrapper.getValue("COMMAND_ADDED_COUNT"));
            migrationSumamry.set("INITIATED_COUNT", dmDataSetWrapper.getValue("INITATED_COUNT"));
            migrationSumamry.set("SUCCESS_COUNT", dmDataSetWrapper.getValue("SUCCESS_COUNT"));
            migrationSumamry.set("FAILURE_COUNT", dmDataSetWrapper.getValue("FAILED_COUNT"));
            migrationSumamry.set("INITIATED_FAILED_COUNT", dmDataSetWrapper.getValue("AGENT_FAILED_COUNT"));
            final DataObject dataObject = (DataObject)new WritableDataObject();
            dataObject.addRow(migrationSumamry);
            MDMUtil.getPersistenceLite().update(dataObject);
            this.logger.log(Level.INFO, "[Migration][Summary][Update]: <{0}>", migrationSumamry.getAsJSON());
        }
    }
    
    private JSONObject getMigrationStatus(final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationSummary"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationSummary", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("MigrationSummary", "CUSTOMER_ID"), (Object)customerID, 0));
        final SortColumn sortColumn = new SortColumn("MigrationSummary", "COMPUTED_AT", false);
        selectQuery.addSortColumn(sortColumn);
        final Range range = new Range(0, 5);
        selectQuery.setRange(range);
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final JSONObject jsonObject = new JSONObject();
        final Row windowsSummary = dataObject.getRow("MigrationSummary", getCriteria(3, 1, "MigrationSummary"));
        if (windowsSummary != null) {
            jsonObject.put("3_1", (Object)windowsSummary.getAsJSON());
        }
        final Row windowsnativeSummary = dataObject.getRow("MigrationSummary", getCriteria(3, 2, "MigrationSummary"));
        if (windowsnativeSummary != null) {
            jsonObject.put("3_2", (Object)windowsnativeSummary.getAsJSON());
        }
        final Row iosSummary = dataObject.getRow("MigrationSummary", getCriteria(1, 1, "MigrationSummary"));
        if (iosSummary != null) {
            jsonObject.put("1_1", (Object)iosSummary.getAsJSON());
        }
        final Row iosnativeSummary = dataObject.getRow("MigrationSummary", getCriteria(1, 2, "MigrationSummary"));
        if (iosnativeSummary != null) {
            jsonObject.put("1_2", (Object)iosnativeSummary.getAsJSON());
        }
        final Row androidSummary = dataObject.getRow("MigrationSummary", getCriteria(2, 1, "MigrationSummary"));
        if (androidSummary != null) {
            jsonObject.put("2_1", (Object)androidSummary.getAsJSON());
        }
        return jsonObject;
    }
    
    private static Criteria getCriteria(final int platform, final int cmdrepType, final String tableName) {
        if (tableName.equals("MigrationSummary")) {
            return new Criteria(Column.getColumn("MigrationSummary", "PLATFORM"), (Object)platform, 0).and(new Criteria(Column.getColumn("MigrationSummary", "COMMAND_REPOSITORY_TYPE"), (Object)cmdrepType, 0));
        }
        if (tableName.equals("MigrationStatus")) {
            return new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platform, 0).and(new Criteria(Column.getColumn("MigrationStatus", "COMMAND_REPOSITORY_TYPE"), (Object)cmdrepType, 0));
        }
        return null;
    }
    
    private List pickEvaluationCandidatesForMigration(final int platform, final int cmdRepType, final Long customerID, final int numDevices, final int days) throws DataAccessException {
        final Long currentMillis = System.currentTimeMillis();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationStatus"));
        selectQuery.addJoin(new Join("MigrationStatus", "EnrollmentRequestToDevice", new String[] { "RESOURCE_ID" }, new String[] { "MANAGED_DEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("MigrationStatus", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MigrationStatus", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MigrationStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("EnrollmentRequestToDevice", "DeviceEnrollmentRequest", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "COMMAND_REPOSITORY_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "COMMAND_STATUS"));
        final Criteria criteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_TYPE"), (Object)3, 1);
        final Criteria YetToApplycriteria = new Criteria(Column.getColumn("MigrationStatus", "COMMAND_STATUS"), (Object)0, 0);
        Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        if (days > 0) {
            customerCriteria = customerCriteria.and(new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)new Long[] { currentMillis - days * 24 * 60 * 60 * 1000, currentMillis }, 14));
        }
        final SortColumn sortColumn = new SortColumn("AgentContact", "LAST_CONTACT_TIME", false);
        selectQuery.addSortColumn(sortColumn);
        Range range = new Range(0, numDevices);
        selectQuery.setRange(range);
        selectQuery.setCriteria(getCriteria(platform, cmdRepType, "MigrationStatus").and(criteria).and(YetToApplycriteria).and(customerCriteria));
        DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        Iterator iterator = dataObject.getRows("MigrationStatus");
        final List candidates = new ArrayList();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            candidates.add(row.get("RESOURCE_ID"));
        }
        if (candidates.size() < numDevices) {
            selectQuery.setCriteria(getCriteria(platform, cmdRepType, "MigrationStatus").and(criteria.negate()).and(YetToApplycriteria).and(customerCriteria));
            range = new Range(0, numDevices - candidates.size());
            selectQuery.setRange(range);
            dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            iterator = dataObject.getRows("MigrationStatus");
            while (iterator.hasNext() && candidates.size() < numDevices) {
                candidates.add(iterator.next().get("RESOURCE_ID"));
            }
        }
        this.logger.log(Level.INFO, "[Migration][Migrate] : picked Candidates {0} ", candidates);
        final BaseMigrationUtil baseMigrationUtil = BaseMigrationUtil.getInstance(platform);
        if (baseMigrationUtil != null) {
            baseMigrationUtil.addMigrationCommandForDevice(candidates, cmdRepType);
        }
        return candidates;
    }
    
    private int getMigrationPeriod(final Integer platform, final Integer cmdRepType, final JSONObject summary) {
        final JSONObject jsonObject = summary.getJSONObject(platform + "_" + cmdRepType);
        int status = 0;
        if (jsonObject != null) {
            final int yetToApplyCount = jsonObject.getInt("YET_TO_APPLY_COUNT".toLowerCase());
            final int successCount = jsonObject.getInt("SUCCESS_COUNT".toLowerCase());
            final int failedCount = jsonObject.getInt("FAILURE_COUNT".toLowerCase());
            final int commadnAddedCount = jsonObject.getInt("COMMAND_ADDED_COUNT".toLowerCase());
            final int initiatedCount = jsonObject.getInt("INITIATED_COUNT".toLowerCase());
            final int initiatedFailedCount = jsonObject.getInt("INITIATED_FAILED_COUNT".toLowerCase());
            final int inProgressCount = successCount + failedCount + commadnAddedCount + initiatedCount + initiatedFailedCount;
            final float inprogressPercentage = inProgressCount / (inProgressCount + (float)yetToApplyCount) * 100.0f;
            final float successPercentage = successCount / (float)inProgressCount * 100.0f;
            if (yetToApplyCount == 0 && inProgressCount - successCount == 0) {
                status = 2;
            }
            else if (yetToApplyCount > 0 && inProgressCount == 0) {
                status = 1;
            }
            else if (yetToApplyCount > 0 && inProgressCount <= 9 && successCount < 3) {
                status = 3;
            }
            else if (successCount >= 3 && inprogressPercentage < 40.0f) {
                status = 4;
            }
            else if (inprogressPercentage <= 40.0f && successPercentage < 80.0f) {
                status = 5;
            }
            else if (successPercentage >= 80.0f && yetToApplyCount != 0) {
                status = 6;
            }
            else if (inprogressPercentage > 40.0f) {
                status = 7;
            }
        }
        return status;
    }
    
    private List getInprogressInActiveDevices(final int platform, final int cmdRepType, final Long customerID) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationStatus"));
        selectQuery.addJoin(new Join("MigrationStatus", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MigrationStatus", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("MigrationStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "COMMAND_REPOSITORY_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "COMMAND_STATUS"));
        final Criteria commandAddedcriteria = new Criteria(Column.getColumn("MigrationStatus", "COMMAND_STATUS"), (Object)1, 0);
        Criteria customerCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
        customerCriteria = customerCriteria.and(new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)(System.currentTimeMillis() + 813934592L), 7));
        selectQuery.setCriteria(commandAddedcriteria.and(customerCriteria).and(getCriteria(platform, cmdRepType, "MigrationStatus")));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MigrationStatus");
        final List list = new ArrayList();
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            list.add(row.get("RESOURCE_ID"));
        }
        return list;
    }
    
    private void evaluateThrottlingPeriodStatus(final int platform, final int cmdRepType, final Long customerID, final JSONObject sumamry) throws Exception {
        final int status = this.getMigrationPeriod(platform, cmdRepType, sumamry);
        this.logger.log(Level.INFO, "[Migration][Period] : Migration period {0} for platform : {1} cmd Rep : {2} customer ID {3} ", new Object[] { status, platform, cmdRepType, customerID });
        final JSONObject jsonObject = sumamry.getJSONObject(platform + "_" + cmdRepType);
        final int successCount = jsonObject.getInt("SUCCESS_COUNT".toLowerCase());
        final int failedCount = jsonObject.getInt("FAILURE_COUNT".toLowerCase());
        final int commadnAddedCount = jsonObject.getInt("COMMAND_ADDED_COUNT".toLowerCase());
        final int initiatedCount = jsonObject.getInt("INITIATED_COUNT".toLowerCase());
        final int initiatedFailedCount = jsonObject.getInt("INITIATED_FAILED_COUNT".toLowerCase());
        final int inProgressCount = successCount + failedCount + commadnAddedCount + initiatedCount + initiatedFailedCount;
        final int yetToApplyCount = jsonObject.getInt("YET_TO_APPLY_COUNT".toLowerCase());
        if (status == 2 || status == 0) {
            return;
        }
        if (status == 1) {
            List candidates = this.pickEvaluationCandidatesForMigration(platform, cmdRepType, customerID, 3, 2);
            if (candidates.size() == 0) {
                this.logger.log(Level.INFO, "[Migration][init] : No Devices could be picked from last two days {0} {1} {2}", new Object[] { platform, cmdRepType, customerID });
                candidates = this.pickEvaluationCandidatesForMigration(platform, cmdRepType, customerID, 3, 45);
                if (candidates.size() == 0) {
                    this.pickEvaluationCandidatesForMigration(platform, cmdRepType, customerID, 100000, -99999);
                }
            }
        }
        else if (status == 3) {
            if (inProgressCount + 3 < 10) {
                this.pickEvaluationCandidatesForMigration(platform, cmdRepType, customerID, 3, -99999);
            }
        }
        else if (status == 4) {
            this.pickEvaluationCandidatesForMigration(platform, cmdRepType, customerID, yetToApplyCount / 3, -99999);
        }
        else if (status == 5) {
            final int inactiveCount = this.getInprogressInActiveDevices(platform, cmdRepType, customerID).size();
            final float successPercentage = successCount / (inProgressCount - (float)inactiveCount) * 100.0f;
            if (successPercentage >= 80.0f) {
                this.pickEvaluationCandidatesForMigration(platform, cmdRepType, customerID, 100000, -99999);
            }
        }
        else if (status == 6 || status == 7) {
            this.pickEvaluationCandidatesForMigration(platform, cmdRepType, customerID, 100000, -99999);
        }
    }
    
    private boolean hasNoProgress(final int platform, final int cmdRepType, final int days) throws Exception {
        Boolean hasNoProgress = Boolean.FALSE;
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationSummary"));
        subQuery.setCriteria(getCriteria(platform, cmdRepType, "MigrationSummary"));
        final SortColumn sortColumn = new SortColumn("MigrationSummary", "COMPUTED_AT", false);
        subQuery.addSortColumn(sortColumn);
        final Range range = new Range(0, days);
        subQuery.setRange(range);
        subQuery.addSelectColumn(Column.getColumn("MigrationSummary", "*"));
        final DerivedTable derivedTable = new DerivedTable("SUB_QUERY", (Query)subQuery);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl((Table)derivedTable);
        final List columns = new ArrayList();
        columns.add(Column.getColumn("SUB_QUERY", "FAILURE_COUNT"));
        columns.add(Column.getColumn("SUB_QUERY", "SUCCESS_COUNT"));
        columns.add(Column.getColumn("SUB_QUERY", "COMMAND_ADDED_COUNT"));
        columns.add(Column.getColumn("SUB_QUERY", "YET_TO_APPLY_COUNT"));
        columns.add(Column.getColumn("SUB_QUERY", "INITIATED_COUNT"));
        columns.add(Column.getColumn("SUB_QUERY", "INITIATED_FAILED_COUNT"));
        final GroupByClause groupByClause = new GroupByClause(columns);
        selectQuery.setGroupByClause(groupByClause);
        final Column column = new Column("SUB_QUERY", "MIGRATION_SUMMARY_ID").count();
        column.setColumnAlias("MIGRATION_SUMMARY_ID");
        selectQuery.addSelectColumn(column);
        final DMDataSetWrapper dmDataSetWrapper = DMDataSetWrapper.executeQuery((Object)selectQuery);
        while (dmDataSetWrapper.next()) {
            final int count = (int)dmDataSetWrapper.getValue("MIGRATION_SUMMARY_ID");
            if (count == days) {
                hasNoProgress = Boolean.TRUE;
                this.logger.log(Level.INFO, "[Migration][progress] : No progress in {0} {1}", new Object[] { platform, cmdRepType });
                break;
            }
        }
        return hasNoProgress;
    }
    
    private Boolean handleMigrationForType(final int platform, final int cmdRepType, final Long customerID, final JSONObject summary) throws Exception {
        final JSONObject jsonObject = summary.optJSONObject(platform + "_" + cmdRepType);
        Boolean isComplete = Boolean.FALSE;
        if (jsonObject != null) {
            final int successCount = jsonObject.getInt("SUCCESS_COUNT".toLowerCase());
            final int failedCount = jsonObject.getInt("FAILURE_COUNT".toLowerCase());
            final int commadnAddedCount = jsonObject.getInt("COMMAND_ADDED_COUNT".toLowerCase());
            final int initiatedCount = jsonObject.getInt("INITIATED_COUNT".toLowerCase());
            final int initiatedFailedCount = jsonObject.getInt("INITIATED_FAILED_COUNT".toLowerCase());
            final int inProgressCount = failedCount + commadnAddedCount + initiatedCount + initiatedFailedCount;
            final int yetToApplyCount = jsonObject.getInt("YET_TO_APPLY_COUNT".toLowerCase());
            if (failedCount == 0) {
                this.evaluateThrottlingPeriodStatus(platform, cmdRepType, customerID, summary);
            }
            final Boolean hasNoProgress = this.hasNoProgress(platform, cmdRepType, 3);
            if (hasNoProgress) {
                this.markInactiveAsSuccess(platform, cmdRepType, customerID);
                if (failedCount == 0 && successCount >= 3) {
                    this.pickEvaluationCandidatesForMigration(platform, cmdRepType, customerID, 100000, -1);
                }
                else if (this.getInprogressInActiveDevices(platform, cmdRepType, customerID).size() > inProgressCount / 2 && failedCount == 0) {
                    this.pickEvaluationCandidatesForMigration(platform, cmdRepType, customerID, (yetToApplyCount < 3) ? yetToApplyCount : (yetToApplyCount / 3), -99999);
                }
                this.logger.log(Level.INFO, "[Migration][Mailer] : No progress for {0} {1} {2} {3}", new Object[] { platform, cmdRepType, customerID, summary });
            }
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("ForceMigrate")) {
                this.pickEvaluationCandidatesForMigration(platform, cmdRepType, customerID, 100000, -1);
                this.markInactiveAsSuccess(platform, cmdRepType, customerID);
            }
            if (inProgressCount == 0 && yetToApplyCount == 0) {
                isComplete = Boolean.TRUE;
            }
        }
        else {
            isComplete = Boolean.TRUE;
        }
        return isComplete;
    }
    
    private void markInactiveAsSuccess(final int platform, final int cmdRepType, final Long customerID) throws DataAccessException {
        final List inActiveDevices = this.getInprogressInActiveDevices(platform, cmdRepType, customerID);
        final BaseMigrationUtil baseMigrationUtil = BaseMigrationUtil.getInstance(platform);
        if (baseMigrationUtil != null) {
            baseMigrationUtil.addMigrationCommandForDevice(inActiveDevices, cmdRepType);
        }
        final UpdateQuery updateQuery = (UpdateQuery)new UpdateQueryImpl("MigrationStatus");
        updateQuery.addJoin(new Join("MigrationStatus", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        updateQuery.setCriteria(new Criteria(Column.getColumn("MigrationStatus", "RESOURCE_ID"), (Object)inActiveDevices.toArray(), 8).and(getCriteria(platform, cmdRepType, "MigrationStatus")));
        updateQuery.setUpdateColumn("COMMAND_STATUS", (Object)5);
        MDMUtil.getPersistenceLite().update(updateQuery);
    }
    
    public void evaluateAndUpdateMigrationStatusForCustomer(final Long customerID) throws Exception {
        final Boolean globalDisableForOrg = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("isMigrationNotAllowed");
        Boolean globalDisable = Boolean.FALSE;
        final String globalDisableStr = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("global_disable");
        final String migStatusStr = CustomerParamsHandler.getInstance().getParameterValue("MigrationComplete", (long)customerID);
        Boolean migrationComplete = Boolean.FALSE;
        if (!MDMStringUtils.isEmpty(globalDisableStr)) {
            globalDisable = Boolean.parseBoolean(globalDisableStr);
        }
        if (!MDMStringUtils.isEmpty(migStatusStr)) {
            migrationComplete = Boolean.parseBoolean(migStatusStr);
        }
        this.logger.log(Level.INFO, "[Migration][Eval] : Migration params Migration complete : {0} globalDiable : {1}", new Object[] { migrationComplete, globalDisableForOrg });
        if (!globalDisableForOrg && !globalDisable && !migrationComplete) {
            this.identifyAndMarkFailedDevices();
            this.computeAndUpdateMigrationSummary(customerID);
            final JSONObject summary = this.getMigrationStatus(customerID);
            this.logger.log(Level.INFO, "[Migration][Eval] : Current Migration Status : <{0}> ", summary);
            final Boolean androidComplete = this.handleMigrationForType(2, 1, customerID, summary);
            final Boolean windowsComplete = this.handleMigrationForType(3, 1, customerID, summary);
            final Boolean windowsNativeComplete = this.handleMigrationForType(3, 2, customerID, summary);
            final Boolean iosNativeComplete = this.handleMigrationForType(1, 2, customerID, summary);
            if (androidComplete && windowsComplete && windowsNativeComplete && iosNativeComplete) {
                CustomerParamsHandler.getInstance().addOrUpdateParameter("MigrationComplete", "true", (long)customerID);
                this.logger.log(Level.INFO, "[Migration][Complete] : Migration Complete For Customer : {0} ", customerID);
            }
        }
    }
    
    private void identifyAndMarkFailedDevices() throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationStatus"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "COMMAND_REPOSITORY_TYPE"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "COMMAND_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "UPDATED_AT"));
        selectQuery.addJoin(new Join("MigrationStatus", "AgentContact", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        final Criteria criteria = new Criteria(Column.getColumn("MigrationStatus", "COMMAND_STATUS"), (Object)2, 0);
        final Criteria agentContactCriteria = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)(System.currentTimeMillis() - 172800000L), 7);
        selectQuery.setCriteria(criteria.and(agentContactCriteria));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        final Iterator iterator = dataObject.getRows("MigrationStatus");
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            row.set("COMMAND_STATUS", (Object)4);
            row.set("UPDATED_AT", (Object)System.currentTimeMillis());
            final Row histroyRow = new Row("MigrationHistory");
            histroyRow.set("ADDED_AT", (Object)System.currentTimeMillis());
            histroyRow.set("RESOURCE_ID", row.get("RESOURCE_ID"));
            histroyRow.set("COMMAND_REPOSITORY_TYPE", row.get("COMMAND_REPOSITORY_TYPE"));
            histroyRow.set("COMMAND_STATUS", (Object)4);
            dataObject.updateRow(row);
            dataObject.addRow(histroyRow);
            this.logger.log(Level.INFO, "[Migration][Eval] : Marking the device as Failed <{0}>", histroyRow.getAsJSON());
            this.logger.log(Level.INFO, "[Migration][Status][Parsable] : {0} {1} {2} {3}", new Object[] { histroyRow.get("RESOURCE_ID"), histroyRow.get("COMMAND_REPOSITORY_TYPE"), getStatusAsString(2), getStatusAsString(4) });
            this.logger.log(Level.INFO, "[Migration][Mailer] : A device was marked with Migration Failed : {0} {1}", new Object[] { histroyRow.get("RESOURCE_ID"), histroyRow.get("COMMAND_REPOSITORY_TYPE") });
        }
        MDMUtil.getPersistenceLite().update(dataObject);
    }
    
    public void addForMigrationIfNotAdded(final Long resourceID, final int cmdRepType) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MigrationStatus"));
        final Criteria criteria = new Criteria(Column.getColumn("MigrationStatus", "RESOURCE_ID"), (Object)resourceID, 0);
        final Criteria cmdRepCriteria = new Criteria(Column.getColumn("MigrationStatus", "COMMAND_REPOSITORY_TYPE"), (Object)cmdRepType, 0);
        selectQuery.setCriteria(criteria.and(cmdRepCriteria));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MigrationStatus", "COMMAND_REPOSITORY_TYPE"));
        try {
            final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
            if (dataObject.isEmpty()) {
                final Row row = new Row("MigrationStatus");
                row.set("RESOURCE_ID", (Object)resourceID);
                row.set("COMMAND_REPOSITORY_TYPE", (Object)cmdRepType);
                row.set("COMMAND_STATUS", (Object)0);
                row.set("ADDED_AT", (Object)System.currentTimeMillis());
                row.set("UPDATED_AT", (Object)System.currentTimeMillis());
                final Row histroyRow = new Row("MigrationHistory");
                histroyRow.set("ADDED_AT", (Object)System.currentTimeMillis());
                histroyRow.set("RESOURCE_ID", (Object)resourceID);
                histroyRow.set("COMMAND_STATUS", (Object)cmdRepType);
                histroyRow.set("COMMAND_STATUS", (Object)0);
                dataObject.addRow(row);
                dataObject.addRow(histroyRow);
                MDMUtil.getPersistenceLite().update(dataObject);
                this.logger.log(Level.INFO, "[Migration][new] : Device Missed in PPM added for Migration {0}", row.getAsJSON());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "[Migration][Error] : Failed to add device fro migration", e);
        }
    }
    
    protected static String getStatusAsString(final int status) {
        switch (status) {
            case 0: {
                return "YetToApply";
            }
            case 1: {
                return "CommandAdded";
            }
            case 2: {
                return "Initiated";
            }
            case 5: {
                return "Success";
            }
            case 4: {
                return "Failed";
            }
            case 3: {
                return "AgentFailed";
            }
            default: {
                return null;
            }
        }
    }
}
