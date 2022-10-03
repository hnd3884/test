package jdk.management.jfr;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.lang.management.PlatformManagedObject;

public interface FlightRecorderMXBean extends PlatformManagedObject
{
    public static final String MXBEAN_NAME = "jdk.management.jfr:type=FlightRecorder";
    
    long newRecording() throws IllegalStateException, SecurityException;
    
    long takeSnapshot();
    
    long cloneRecording(final long p0, final boolean p1) throws IllegalArgumentException, SecurityException;
    
    void startRecording(final long p0) throws IllegalStateException, SecurityException;
    
    boolean stopRecording(final long p0) throws IllegalArgumentException, IllegalStateException, SecurityException;
    
    void closeRecording(final long p0) throws IOException;
    
    long openStream(final long p0, final Map<String, String> p1) throws IOException;
    
    void closeStream(final long p0) throws IOException;
    
    byte[] readStream(final long p0) throws IOException;
    
    Map<String, String> getRecordingOptions(final long p0) throws IllegalArgumentException;
    
    Map<String, String> getRecordingSettings(final long p0) throws IllegalArgumentException;
    
    void setConfiguration(final long p0, final String p1) throws IllegalArgumentException;
    
    void setPredefinedConfiguration(final long p0, final String p1) throws IllegalArgumentException;
    
    void setRecordingSettings(final long p0, final Map<String, String> p1) throws IllegalArgumentException;
    
    void setRecordingOptions(final long p0, final Map<String, String> p1) throws IllegalArgumentException;
    
    List<RecordingInfo> getRecordings();
    
    List<ConfigurationInfo> getConfigurations();
    
    List<EventTypeInfo> getEventTypes();
    
    void copyTo(final long p0, final String p1) throws IOException, SecurityException;
}
