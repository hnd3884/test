package com.me.devicemanagement.framework.server.csv;

import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class CSVProcessingResumeTask implements SchedulerExecutionInterface
{
    private static Logger logger;
    
    @Override
    public void executeTask(final Properties props) {
        CSVProcessingResumeTask.logger.log(Level.INFO, "Executing CSVProcessingResumeTask");
        try {
            this.clearAllInProgressStatus();
        }
        catch (final Exception e) {
            CSVProcessingResumeTask.logger.log(Level.INFO, "Exception in executeTask:{0}", e);
        }
        CSVProcessingResumeTask.logger.log(Level.INFO, "Finished Executing CSVProcessingResumeTask");
    }
    
    private void clearAllInProgressStatus() {
        try {
            final Long[] customerIds = CustomerInfoUtil.getInstance().getCustomerIdsFromDB();
            if (customerIds != null) {
                for (final Long customerId : customerIds) {
                    final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CSVOperation"));
                    sQuery.addJoin(new Join("CSVOperation", "CSVInprogressOperation", new String[] { "OPERATION_ID" }, new String[] { "OPERATION_ID" }, 2));
                    sQuery.addJoin(new Join("CSVInprogressOperation", "CSVInputParams", new String[] { "INSTANCE_ID" }, new String[] { "INSTANCE_ID" }, 2));
                    sQuery.addSelectColumn(Column.getColumn("CSVOperation", "OPERATION_ID", "CSVOPERATION.OPERATION_ID"));
                    sQuery.addSelectColumn(Column.getColumn("CSVOperation", "CSV_TASK_CLASS"));
                    sQuery.addSelectColumn(Column.getColumn("CSVInprogressOperation", "INSTANCE_ID", "CSVINPROGRESSOPERATION.INSTANCE_ID"));
                    sQuery.addSelectColumn(Column.getColumn("CSVInprogressOperation", "OPERATION_ID", "CSVINPROGRESSOPERATION.OPERATION_ID"));
                    sQuery.addSelectColumn(Column.getColumn("CSVInprogressOperation", "CUSTOMER_ID"));
                    sQuery.addSelectColumn(Column.getColumn("CSVInputParams", "PARAM_ID"));
                    sQuery.addSelectColumn(Column.getColumn("CSVInputParams", "INSTANCE_ID", "CSVINPUTPARAMS.INSTANCE_ID"));
                    sQuery.addSelectColumn(Column.getColumn("CSVInputParams", "PARAMETER_NAME"));
                    sQuery.addSelectColumn(Column.getColumn("CSVInputParams", "PARAMETER_VALUE"));
                    sQuery.setCriteria(new Criteria(Column.getColumn("CSVInprogressOperation", "CUSTOMER_ID"), (Object)customerId, 0));
                    final DataObject dobj = SyMUtil.getPersistence().get(sQuery);
                    final Iterator<Row> iter = dobj.getRows("CSVOperation");
                    while (iter.hasNext()) {
                        final Row r = iter.next();
                        final Long operationID = (Long)r.get("OPERATION_ID");
                        final String taskClassName = (String)r.get("CSV_TASK_CLASS");
                        if (taskClassName == null) {
                            continue;
                        }
                        final String taskName = (String)r.get("LABEL");
                        final Properties taskProps = new Properties();
                        final HashMap taskInfoMap = new HashMap();
                        taskInfoMap.put("taskName", taskName);
                        taskInfoMap.put("schedulerTime", System.currentTimeMillis());
                        taskInfoMap.put("poolName", "mdmPool");
                        final Row opInstanceRow = dobj.getRow("CSVInprogressOperation", new Criteria(Column.getColumn("CSVInprogressOperation", "CUSTOMER_ID"), (Object)customerId, 0).and(new Criteria(Column.getColumn("CSVInprogressOperation", "OPERATION_ID"), (Object)operationID, 0)));
                        if (opInstanceRow == null) {
                            continue;
                        }
                        final Iterator<Row> propIter = dobj.getRows("CSVInputParams", new Criteria(Column.getColumn("CSVInputParams", "INSTANCE_ID"), opInstanceRow.get("INSTANCE_ID"), 0));
                        while (propIter.hasNext()) {
                            final Row propRow = propIter.next();
                            taskProps.put(propRow.get("PARAMETER_NAME"), propRow.get("PARAMETER_VALUE"));
                        }
                        ((Hashtable<String, String>)taskProps).put("customerID", String.valueOf(customerId));
                        ((Hashtable<String, String>)taskProps).put("operationID", String.valueOf(operationID));
                        CSVProcessingResumeTask.logger.log(Level.INFO, "Executing task for customer {0} in class: {1} Props: {2}", new Object[] { customerId, taskClassName, taskProps });
                        try {
                            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously(taskClassName, taskInfoMap, taskProps);
                        }
                        catch (final Exception exp) {
                            CSVProcessingResumeTask.logger.log(Level.WARNING, "Exception occurred during the schdule mdm command : {0}", exp);
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            CSVProcessingResumeTask.logger.log(Level.INFO, "Exception while clearing In Progress..{0}", ex);
        }
    }
    
    static {
        CSVProcessingResumeTask.logger = Logger.getLogger("MDMLogger");
    }
}
