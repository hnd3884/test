package com.me.mdm.server.profiles;

import java.util.List;

public interface MDMCollectionNotApplicableListener
{
    List<Long> getNotApplicableDeviceList(final List p0, final Long p1, final List p2, final long p3);
    
    void setNotApplicableStatus(final List p0, final Long p1);
}
