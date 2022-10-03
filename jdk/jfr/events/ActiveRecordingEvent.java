package jdk.jfr.events;

import jdk.jfr.Timestamp;
import jdk.jfr.DataAmount;
import jdk.jfr.Timespan;
import jdk.jfr.StackTrace;
import jdk.jfr.Category;
import jdk.jfr.Label;
import jdk.jfr.Name;

@Name("jdk.ActiveRecording")
@Label("Flight Recording")
@Category({ "Flight Recorder" })
@StackTrace(false)
public final class ActiveRecordingEvent extends AbstractJDKEvent
{
    @Label("Id")
    public long id;
    @Label("Name")
    public String name;
    @Label("Destination")
    public String destination;
    @Label("Max Age")
    @Timespan("MILLISECONDS")
    public long maxAge;
    @Label("Max Size")
    @DataAmount
    public long maxSize;
    @Label("Start Time")
    @Timestamp("MILLISECONDS_SINCE_EPOCH")
    public long recordingStart;
    @Label("Recording Duration")
    @Timespan("MILLISECONDS")
    public long recordingDuration;
}
