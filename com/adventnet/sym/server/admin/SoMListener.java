package com.adventnet.sym.server.admin;

import com.me.devicemanagement.framework.server.admin.SoMEvent;
import com.me.devicemanagement.framework.server.admin.DomainListener;

public interface SoMListener extends DomainListener
{
    void computersAdded(final SoMEvent[] p0);
    
    void computersDeleted(final SoMEvent[] p0);
    
    void computerDuplicated(final SoMEvent[] p0);
    
    void computersAgentStatusChanged(final SoMEvent[] p0);
    
    void firstComputerAdded(final SoMEvent[] p0);
    
    void allComputersDeleted(final SoMEvent[] p0);
    
    void computerAgentMoved(final SoMEvent[] p0);
    
    void computersManaged(final SoMEvent[] p0);
    
    void computersNotManaged(final SoMEvent[] p0);
    
    void computerRenamed(final SoMEvent[] p0);
}
