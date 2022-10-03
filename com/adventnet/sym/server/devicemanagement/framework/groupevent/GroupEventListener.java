package com.adventnet.sym.server.devicemanagement.framework.groupevent;

import java.util.List;

public interface GroupEventListener
{
    void onGroupEventCompleted(final GroupEventProperties p0) throws Exception;
    
    void onGroupEventTimeOut(final GroupEventProperties p0) throws Exception;
    
    void onActionCompleted(final GroupEventProperties p0) throws Exception;
    
    void onActionCleaned(final List<GroupEventProperties> p0) throws Exception;
}
