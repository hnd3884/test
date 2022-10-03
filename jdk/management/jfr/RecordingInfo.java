package jdk.management.jfr;

import java.util.Iterator;
import java.util.List;
import javax.management.openmbean.TabularData;
import java.util.LinkedHashMap;
import javax.management.openmbean.CompositeData;
import java.time.Instant;
import java.time.Duration;
import jdk.jfr.internal.management.ManagementSupport;
import jdk.jfr.Recording;
import java.util.Map;

public final class RecordingInfo
{
    private final long id;
    private final String name;
    private final String state;
    private final boolean dumpOnExit;
    private final long size;
    private final boolean disk;
    private final long maxAge;
    private final long maxSize;
    private final long startTime;
    private final long stopTime;
    private final String destination;
    private final long durationInSeconds;
    private final Map<String, String> settings;
    
    RecordingInfo(final Recording recording) {
        this.id = recording.getId();
        this.name = recording.getName();
        this.state = recording.getState().toString();
        this.dumpOnExit = recording.getDumpOnExit();
        this.size = recording.getSize();
        this.disk = recording.isToDisk();
        final Duration maxAge = recording.getMaxAge();
        if (maxAge == null) {
            this.maxAge = 0L;
        }
        else {
            this.maxAge = maxAge.getSeconds();
        }
        this.maxSize = recording.getMaxSize();
        final Instant startTime = recording.getStartTime();
        this.startTime = ((startTime == null) ? 0L : startTime.toEpochMilli());
        final Instant stopTime = recording.getStopTime();
        this.stopTime = ((stopTime == null) ? 0L : stopTime.toEpochMilli());
        this.destination = ManagementSupport.getDestinationOriginalText(recording);
        final Duration duration = recording.getDuration();
        this.durationInSeconds = ((duration == null) ? 0L : duration.getSeconds());
        this.settings = recording.getSettings();
    }
    
    private RecordingInfo(final CompositeData compositeData) {
        this.id = (int)compositeData.get("id");
        this.name = (String)compositeData.get("name");
        this.state = (String)compositeData.get("state");
        this.dumpOnExit = (boolean)compositeData.get("dumpOnExit");
        this.size = (long)compositeData.get("size");
        this.disk = (boolean)compositeData.get("disk");
        this.maxAge = (long)compositeData.get("maxAge");
        this.maxSize = (long)compositeData.get("maxSize");
        this.startTime = (long)compositeData.get("startTime");
        this.stopTime = (long)compositeData.get("stopTime");
        this.destination = (String)compositeData.get("destination");
        this.durationInSeconds = (long)compositeData.get("duration");
        this.settings = new LinkedHashMap<String, String>();
        final Object value = compositeData.get("settings");
        if (value instanceof TabularData) {
            final TabularData tabularData = (TabularData)value;
            final List<String> indexNames = tabularData.getTabularType().getIndexNames();
            final int size = indexNames.size();
            final Iterator<?> iterator = tabularData.keySet().iterator();
            while (iterator.hasNext()) {
                final Object[] array = ((List)iterator.next()).toArray();
                for (int i = 0; i < size; ++i) {
                    final String s = indexNames.get(i);
                    final Object o = array[i];
                    if (o instanceof String) {
                        this.settings.put(s, (String)o);
                    }
                }
            }
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getId() {
        return this.id;
    }
    
    public boolean getDumpOnExit() {
        return this.dumpOnExit;
    }
    
    public long getMaxAge() {
        return this.maxAge;
    }
    
    public long getMaxSize() {
        return this.maxSize;
    }
    
    public String getState() {
        return this.state;
    }
    
    public long getStartTime() {
        return this.startTime;
    }
    
    public long getStopTime() {
        return this.stopTime;
    }
    
    public Map<String, String> getSettings() {
        return this.settings;
    }
    
    public String getDestination() {
        return this.destination;
    }
    
    @Override
    public String toString() {
        final Stringifier stringifier = new Stringifier();
        stringifier.add("name", this.name);
        stringifier.add("id", this.id);
        stringifier.add("maxAge", this.maxAge);
        stringifier.add("maxSize", this.maxSize);
        return stringifier.toString();
    }
    
    public long getSize() {
        return this.size;
    }
    
    public boolean isToDisk() {
        return this.disk;
    }
    
    public long getDuration() {
        return this.durationInSeconds;
    }
    
    public static RecordingInfo from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        return new RecordingInfo(compositeData);
    }
}
