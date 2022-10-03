package com.zoho.clustering.failover;

public interface FOSHandler
{
    void onStart(final FOS.Mode p0);
    
    void onStop(final FOS.Mode p0, final ErrorCode p1);
    
    void onSlaveTakeover();
}
