package com.me.devicemanagement.framework.server.api;

import java.util.Properties;
import java.util.ArrayList;

public interface ADSyncAPI
{
    void proccessFetchedADData(final Long p0, final ArrayList<Properties> p1, final String p2, final Long p3, final int p4, final int p5, final int p6, final int p7, final boolean p8, final boolean p9) throws Exception;
}
