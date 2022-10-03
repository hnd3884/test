package com.me.ems.summaryserver.listener;

public interface ProbeMgmtListener
{
    void probeAdded(final ProbeMgmtEvent p0);
    
    void probeInstalled(final ProbeMgmtEvent p0);
    
    void probeDeleted(final ProbeMgmtEvent p0);
    
    void probeModified(final ProbeMgmtEvent p0);
    
    void probeBackToLive(final ProbeMgmtEvent p0);
}
