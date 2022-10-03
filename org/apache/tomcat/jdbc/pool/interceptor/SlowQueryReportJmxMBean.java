package org.apache.tomcat.jdbc.pool.interceptor;

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.CompositeData;

public interface SlowQueryReportJmxMBean
{
    CompositeData[] getSlowQueriesCD() throws OpenDataException;
}
