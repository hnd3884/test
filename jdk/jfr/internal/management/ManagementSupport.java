package jdk.jfr.internal.management;

import jdk.jfr.internal.WriteableUserPath;
import jdk.jfr.internal.PrivateAccess;
import jdk.jfr.Recording;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;
import java.time.Duration;
import java.util.Collections;
import jdk.jfr.internal.MetadataRepository;
import jdk.jfr.internal.instrument.JDKEvents;
import java.util.ArrayList;
import jdk.jfr.internal.JVMSupport;
import jdk.jfr.internal.Utils;
import jdk.jfr.EventType;
import java.util.List;

public final class ManagementSupport
{
    public static List<EventType> getEventTypes() {
        Utils.checkAccessFlightRecorder();
        if (JVMSupport.isNotAvailable()) {
            return new ArrayList<EventType>();
        }
        JDKEvents.initialize();
        return Collections.unmodifiableList((List<? extends EventType>)MetadataRepository.getInstance().getRegisteredEventTypes());
    }
    
    public static long parseTimespan(final String s) {
        return Utils.parseTimespan(s);
    }
    
    public static final String formatTimespan(final Duration duration, final String s) {
        return Utils.formatTimespan(duration, s);
    }
    
    public static void logError(final String s) {
        Logger.log(LogTag.JFR, LogLevel.ERROR, s);
    }
    
    public static String getDestinationOriginalText(final Recording recording) {
        final WriteableUserPath destination = PrivateAccess.getInstance().getPlatformRecording(recording).getDestination();
        return (destination == null) ? null : destination.getOriginalText();
    }
}
