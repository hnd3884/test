package com.me.ems.framework.summaryscope.listener;

public interface SummaryScopeListener
{
    void scopeAdded(final SummaryScopeEvent p0);
    
    void scopeModified(final SummaryScopeEvent p0);
    
    void scopeDeleted(final SummaryScopeEvent p0);
    
    void invokeSummaryForAllManaged(final SummaryScopeEvent p0);
}
