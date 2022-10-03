package jdk.jfr.internal.dcmd;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Map;
import jdk.jfr.SettingDescriptor;
import java.util.StringJoiner;
import jdk.jfr.EventType;
import java.util.Collection;
import java.time.Duration;
import jdk.jfr.internal.Utils;
import java.util.Iterator;
import java.util.List;
import jdk.jfr.Recording;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;

final class DCmdCheck extends AbstractDCmd
{
    public String execute(final String s, final Boolean b) throws DCmdException {
        this.executeInternal(s, b);
        return this.getResult();
    }
    
    private void executeInternal(final String s, Boolean false) throws DCmdException {
        if (Logger.shouldLog(LogTag.JFR_DCMD, LogLevel.DEBUG)) {
            Logger.log(LogTag.JFR_DCMD, LogLevel.DEBUG, "Executing DCmdCheck: name=" + s + ", verbose=" + false);
        }
        if (false == null) {
            false = Boolean.FALSE;
        }
        if (s != null) {
            this.printRecording(this.findRecording(s), false);
            return;
        }
        final List<Recording> recordings = this.getRecordings();
        if (!false && recordings.isEmpty()) {
            this.println("No available recordings.", new Object[0]);
            this.println();
            this.println("Use jcmd " + this.getPid() + " JFR.start to start a recording.", new Object[0]);
            return;
        }
        int n = 1;
        for (final Recording recording : recordings) {
            if (n == 0) {
                this.println();
                if (Boolean.TRUE.equals(false)) {
                    this.println();
                }
            }
            n = 0;
            this.printRecording(recording, false);
        }
    }
    
    private void printRecording(final Recording recording, final boolean b) {
        this.printGeneral(recording);
        if (b) {
            this.println();
            this.printSetttings(recording);
        }
    }
    
    private void printGeneral(final Recording recording) {
        this.print("Recording " + recording.getId() + ": name=" + recording.getName());
        final Duration duration = recording.getDuration();
        if (duration != null) {
            this.print(" duration=");
            this.printTimespan(duration, "");
        }
        final long maxSize = recording.getMaxSize();
        if (maxSize != 0L) {
            this.print(" maxsize=");
            this.print(Utils.formatBytesCompact(maxSize));
        }
        final Duration maxAge = recording.getMaxAge();
        if (maxAge != null) {
            this.print(" maxage=");
            this.printTimespan(maxAge, "");
        }
        this.print(" (" + recording.getState().toString().toLowerCase() + ")");
        this.println();
    }
    
    private void printSetttings(final Recording recording) {
        final Map<String, String> settings = recording.getSettings();
        for (final EventType eventType : sortByEventPath(this.getFlightRecorder().getEventTypes())) {
            final StringJoiner stringJoiner = new StringJoiner(",", "[", "]");
            stringJoiner.setEmptyValue("");
            for (final SettingDescriptor settingDescriptor : eventType.getSettingDescriptors()) {
                final String string = eventType.getName() + "#" + settingDescriptor.getName();
                if (settings.containsKey(string)) {
                    stringJoiner.add(settingDescriptor.getName() + "=" + (String)settings.get(string));
                }
            }
            final String string2 = stringJoiner.toString();
            if (!string2.isEmpty()) {
                this.print(" %s (%s)", eventType.getLabel(), eventType.getName());
                this.println();
                this.println("   " + string2, new Object[0]);
            }
        }
    }
    
    private static List<EventType> sortByEventPath(final Collection<EventType> collection) {
        final ArrayList list = new ArrayList();
        list.addAll(collection);
        Collections.sort((List<Object>)list, (Comparator<? super Object>)new Comparator<EventType>() {
            @Override
            public int compare(final EventType eventType, final EventType eventType2) {
                return eventType.getName().compareTo(eventType2.getName());
            }
        });
        return list;
    }
}
