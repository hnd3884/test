package com.me.ems.summaryserver.summary.listener;

import com.me.ems.summaryserver.summary.probedistribution.SummaryEventDataHandler;
import com.me.ems.summaryserver.listener.ProbeMgmtEvent;
import com.me.ems.summaryserver.listener.ProbeMgmtListener;

public class SSSyncListenerImpl implements ProbeMgmtListener
{
    @Override
    public void probeAdded(final ProbeMgmtEvent probeMgmtEvent) {
    }
    
    @Override
    public void probeInstalled(final ProbeMgmtEvent probeMgmtEvent) {
        final SummaryEventDataHandler summaryEventDataHandler = SummaryEventDataHandler.getInstance();
        summaryEventDataHandler.processSummaryEventData(probeMgmtEvent.getProbeID());
        summaryEventDataHandler.syncTablesData(probeMgmtEvent.getProbeID());
    }
    
    @Override
    public void probeDeleted(final ProbeMgmtEvent probeMgmtEvent) {
    }
    
    @Override
    public void probeModified(final ProbeMgmtEvent probeMgmtEvent) {
    }
    
    @Override
    public void probeBackToLive(final ProbeMgmtEvent probeMgmtEvent) {
    }
}
