package com.adventnet.sym.server.admin;

import com.me.devicemanagement.framework.server.admin.SoMEvent;

public abstract class AbstractSoMListenerImpl implements SoMListener
{
    public void domainsAdded(final SoMEvent[] somEventArr) {
    }
    
    public void domainsDeleted(final SoMEvent[] somEventArr) {
    }
    
    public void domainsUpdated(final SoMEvent[] somEventArr) {
    }
    
    @Override
    public void computersAdded(final SoMEvent[] somEventArr) {
    }
    
    @Override
    public void computersDeleted(final SoMEvent[] somEventArr) {
    }
    
    @Override
    public void computerDuplicated(final SoMEvent[] somEventArr) {
    }
    
    @Override
    public void computersAgentStatusChanged(final SoMEvent[] somEventArr) {
    }
    
    @Override
    public void firstComputerAdded(final SoMEvent[] somEventArr) {
    }
    
    @Override
    public void allComputersDeleted(final SoMEvent[] somEventArr) {
    }
    
    @Override
    public void computerAgentMoved(final SoMEvent[] somEventArr) {
    }
    
    @Override
    public void computersManaged(final SoMEvent[] somEventArr) {
    }
    
    @Override
    public void computersNotManaged(final SoMEvent[] somEventArr) {
    }
    
    public void domainsManaged(final SoMEvent[] somEventArr) {
    }
    
    public void domainsNotManaged(final SoMEvent[] somEventArr) {
    }
    
    @Override
    public void computerRenamed(final SoMEvent[] somEventArr) {
    }
}
