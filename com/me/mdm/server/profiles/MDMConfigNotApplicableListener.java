package com.me.mdm.server.profiles;

import java.util.List;

public interface MDMConfigNotApplicableListener
{
    List<Long> getNotApplicableDeviceList(final MDMConfigNotApplicable p0);
    
    void setNotApplicableStatus(final List p0, final Long p1);
}
