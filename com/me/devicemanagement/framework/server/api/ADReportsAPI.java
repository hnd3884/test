package com.me.devicemanagement.framework.server.api;

import java.util.ArrayList;

public interface ADReportsAPI
{
    void convertADDataAndAddToQueue(final ArrayList p0, final String p1, final int p2, final int p3, final int p4, final int p5, final boolean p6, final boolean p7);
    
    void convertDCUserDDataAndAddToQueue(final ArrayList p0, final String p1, final int p2, final int p3, final int p4, final int p5, final boolean p6, final boolean p7);
}
