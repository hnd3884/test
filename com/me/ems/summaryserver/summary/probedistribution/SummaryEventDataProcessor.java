package com.me.ems.summaryserver.summary.probedistribution;

import java.util.Hashtable;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class SummaryEventDataProcessor implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    @Override
    public void executeTask(final Properties props) {
        SummaryEventDataProcessor.logger.log(Level.INFO, "SummaryEventDataProcessor EXECUTION entered");
        final Long probeID = ((Hashtable<K, Long>)props).get("probeId");
        if (props.containsKey("isTableData")) {
            SummaryEventDataProcessor.logger.log(Level.INFO, "SummaryEventDataProcessor EXECUTION STARTED for tabledata");
            final String tableName = ((Hashtable<K, String>)props).get("tableName");
            ProbeMgmtFactoryProvider.getSummaryServerSyncAPI().syncSSTableData(tableName, null, probeID);
            SummaryEventDataProcessor.logger.log(Level.INFO, "SummaryEventDataProcessor EXECUTION STARTED");
        }
        else {
            SummaryEventDataProcessor.logger.log(Level.INFO, "SummaryEventDataProcessor EXECUTION STARTED for events");
            final List<Integer> eventList = ((Hashtable<K, List<Integer>>)props).get("eventList");
            final SummaryEventDataHandler summaryEventDataHandler = SummaryEventDataHandler.getInstance();
            summaryEventDataHandler.processSummaryEventData(probeID, summaryEventDataHandler.getEventListCriteria(eventList));
        }
        SummaryEventDataProcessor.logger.log(Level.INFO, "SummaryEventDataProcessor EXECUTION ENDED");
    }
    
    static {
        SummaryEventDataProcessor.logger = Logger.getLogger("probeActionsLogger");
    }
}
