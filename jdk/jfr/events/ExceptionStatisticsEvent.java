package jdk.jfr.events;

import jdk.jfr.StackTrace;
import jdk.jfr.Description;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("jdk.ExceptionStatistics")
@Label("Exception Statistics")
@Category({ "Java Application", "Statistics" })
@Description("Number of objects derived from java.lang.Throwable that have been created")
@StackTrace(false)
public final class ExceptionStatisticsEvent extends AbstractJDKEvent
{
    @Label("Exceptions Created")
    public long throwables;
}
