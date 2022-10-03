package com.me.idps.op;

import com.me.idps.core.sync.synch.DirSingletonQueue;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.scheduler.SchedulerConstants;
import com.me.idps.core.upgrade.AzureOAuth210902;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.idps.core.IdpsPostStartupUpgradeHandler;
import com.me.idps.core.factory.IdpsFactoryProvider;
import java.util.Properties;
import com.me.idps.core.util.DirectoryUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.sql.Connection;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.CaseExpression;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.idps.core.factory.IdpsProdEnvAPI;

public class IdpsOPEnvImpl extends IdpsProdEnvAPI
{
    @Override
    public void normalizeDB() {
        try {
            final Long currentTime = System.currentTimeMillis();
            final DCQueueData qData = new DCQueueData();
            qData.queueData = "";
            qData.postTime = currentTime;
            qData.fileName = String.valueOf(currentTime);
            DCQueueHandler.getQueue("dirDB-task").addToQueue(qData);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
    
    private Column getOpCountCol() {
        final Criteria criteria = new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)2, 0);
        final CaseExpression ce = new CaseExpression("OP_COUNT");
        ce.addWhen(criteria, (Object)Column.getColumn("DMDomain", "DOMAIN_ID"));
        return IdpsUtil.getInstance().getDistinctIntegerCountCaseExpressionColumn((Column)ce);
    }
    
    @Override
    public SelectQuery getOPcountQuery(final Long customerID) {
        final Column custCol = Column.getColumn("CustomerInfo", "CUSTOMER_ID");
        final Criteria custCri = (customerID != null) ? new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0) : null;
        final SelectQuery opCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        opCountQuery.addJoin(new Join("CustomerInfo", "DMDomain", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 1));
        opCountQuery.addSelectColumn(custCol);
        opCountQuery.addSelectColumn(this.getOpCountCol());
        if (custCri != null) {
            opCountQuery.setCriteria(custCri);
        }
        opCountQuery.setGroupByClause(new GroupByClause((List)new ArrayList(Arrays.asList(custCol))));
        return opCountQuery;
    }
    
    @Override
    public void reset(final Connection connection, final Long customerID, final boolean hardReset) {
        if (hardReset) {
            final List<String> resetQueueNames = new ArrayList<String>(Arrays.asList("adAsync-task", "adProc-task", "adRetreiver-task", "adTemp-task", "adCoreDB-task"));
            for (int i = 0; i < resetQueueNames.size(); ++i) {
                final String queueName = resetQueueNames.get(i);
                try {
                    ApiFactoryProvider.getFileAccessAPI().deleteDirectory(DirectoryUtil.getInstance().getQueueFolderPath(queueName));
                }
                catch (final Exception e) {
                    IDPSlogger.ERR.log(Level.SEVERE, "exception in deleting dir of " + queueName, e);
                }
            }
            for (int i = 0; i < resetQueueNames.size(); ++i) {
                final String queueName = resetQueueNames.get(i);
                try {
                    ApiFactoryProvider.getFileAccessAPI().createDirectory(DirectoryUtil.getInstance().getQueueFolderPath(queueName));
                }
                catch (final Exception e) {
                    IDPSlogger.ERR.log(Level.SEVERE, "exception in deleting dir of " + queueName, e);
                }
            }
            new DmDomainTask().executeTask(null);
        }
        IdpsFactoryProvider.getIdpsAccessAPI(2).doHealthCheck(connection);
    }
    
    @Override
    public void handleServerStartup() {
        IdpsPostStartupUpgradeHandler.handleUpgrade();
        try {
            final Long[] customerIDs = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            for (int index = 0; customerIDs != null && index < customerIDs.length; ++index) {
                final Long customerID = customerIDs[index];
                new AzureOAuth210902().handleUpgrade(customerID);
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
        DirectoryUtil.getInstance().initiateResetHandling();
    }
    
    @Override
    public String getSchemaName() {
        return "---";
    }
    
    @Override
    public String getDomainName(final String domain, final String summaryReason) {
        return domain;
    }
    
    @Override
    public void startADSyncScheduler(final Long customerId) {
        ApiFactoryProvider.getSchedulerAPI().setSchedulerState((boolean)SchedulerConstants.ENABLE, "IDPsyncTaskScheduler");
    }
    
    @Override
    protected void stopADSyncScheduler(final Long customerId) {
        ApiFactoryProvider.getSchedulerAPI().setSchedulerState((boolean)SchedulerConstants.DISABLE, "IDPsyncTaskScheduler");
    }
    
    @Override
    public boolean isManualVAdisabled(final boolean fromDB) {
        String vacuumAndAnalyseStopped = null;
        try {
            if (fromDB) {
                vacuumAndAnalyseStopped = SyMUtil.getSyMParameterFromDB("STOP_VACUUM_AND_ANALYSE");
            }
            else {
                vacuumAndAnalyseStopped = SyMUtil.getSyMParameter("STOP_VACUUM_AND_ANALYSE");
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.FINE, null, ex);
        }
        return String.valueOf(Boolean.TRUE).equalsIgnoreCase(String.valueOf(Boolean.valueOf(vacuumAndAnalyseStopped)));
    }
    
    @Override
    public long[] allocatePKs(final DirSingletonQueue dirSingletonQueue, final int numOfPKsrequired) throws Exception {
        return OPpkSynchAllocator.getInstance().allocatePKs(dirSingletonQueue, numOfPKsrequired);
    }
}
