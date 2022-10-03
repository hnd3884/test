package com.me.mdm.server.apps.blacklist;

import com.adventnet.ds.query.SelectQuery;
import java.util.List;
import java.util.HashMap;

public interface BlacklistAppInterface
{
    void blacklistAppInResource(final HashMap p0) throws Exception;
    
    void removeBlacklistAppInResource(final HashMap p0) throws Exception;
    
    SelectQuery getDeviceList(final List p0);
}
