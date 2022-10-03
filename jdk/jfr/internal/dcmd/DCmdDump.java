package jdk.jfr.internal.dcmd;

import java.time.format.DateTimeParseException;
import jdk.jfr.internal.Utils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.LocalDateTime;
import jdk.jfr.internal.PlatformRecording;
import jdk.jfr.internal.SecuritySupport;
import jdk.jfr.internal.WriteableUserPath;
import jdk.jfr.internal.PlatformRecorder;
import jdk.jfr.Recording;
import java.nio.file.InvalidPathException;
import java.io.IOException;
import jdk.jfr.internal.PrivateAccess;
import java.time.temporal.TemporalAmount;
import java.time.Duration;
import java.time.Instant;
import jdk.jfr.FlightRecorder;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;

final class DCmdDump extends AbstractDCmd
{
    public String execute(final String s, final String s2, Long value, Long value2, final String s3, final String s4, final Boolean b) throws DCmdException {
        if (Logger.shouldLog(LogTag.JFR_DCMD, LogLevel.DEBUG)) {
            Logger.log(LogTag.JFR_DCMD, LogLevel.DEBUG, "Executing DCmdDump: name=" + s + ", filename=" + s2 + ", maxage=" + value + ", maxsize=" + value2 + ", begin=" + s3 + ", end" + s4 + ", path-to-gc-roots=" + b);
        }
        if (FlightRecorder.getFlightRecorder().getRecordings().isEmpty()) {
            throw new DCmdException("No recordings to dump from. Use JFR.start to start a recording.", new Object[0]);
        }
        if (value != null) {
            if (s4 != null || s3 != null) {
                throw new DCmdException("Dump failed, maxage can't be combined with begin or end.", new Object[0]);
            }
            if (value < 0L) {
                throw new DCmdException("Dump failed, maxage can't be negative.", new Object[0]);
            }
            if (value == 0L) {
                value = 4611686018427387903L;
            }
        }
        if (value2 != null) {
            if (value2 < 0L) {
                throw new DCmdException("Dump failed, maxsize can't be negative.", new Object[0]);
            }
            if (value2 == 0L) {
                value2 = 4611686018427387903L;
            }
        }
        Instant instant = this.parseTime(s3, "begin");
        final Instant time = this.parseTime(s4, "end");
        if (instant != null && time != null && time.isBefore(instant)) {
            throw new DCmdException("Dump failed, begin must preceed end.", new Object[0]);
        }
        if (value != null) {
            instant = Instant.now().minus((TemporalAmount)Duration.ofNanos(value));
        }
        Recording recording = null;
        if (s != null) {
            recording = this.findRecording(s);
        }
        final PlatformRecorder platformRecorder = PrivateAccess.getInstance().getPlatformRecorder();
        try {
            synchronized (platformRecorder) {
                this.dump(platformRecorder, recording, s, s2, value2, b, instant, time);
            }
        }
        catch (final IOException | InvalidPathException ex) {
            throw new DCmdException("Dump failed. Could not copy recording data. %s", new Object[] { ((Throwable)ex).getMessage() });
        }
        return this.getResult();
    }
    
    public void dump(final PlatformRecorder platformRecorder, final Recording recording, final String s, final String s2, final Long n, final Boolean b, final Instant instant, final Instant instant2) throws DCmdException, IOException {
        try (final PlatformRecording snapShot = this.newSnapShot(platformRecorder, recording, b)) {
            snapShot.filter(instant, instant2, n);
            if (snapShot.getChunks().isEmpty()) {
                throw new DCmdException("Dump failed. No data found in the specified interval.", new Object[0]);
            }
            WriteableUserPath destination = null;
            if (recording != null) {
                destination = PrivateAccess.getInstance().getPlatformRecording(recording).getDestination();
            }
            if (s2 != null || (s2 == null && destination == null)) {
                destination = new WriteableUserPath(this.resolvePath(recording, s2).toPath());
            }
            snapShot.dumpStopped(destination);
            this.reportOperationComplete("Dumped", s, new SecuritySupport.SafePath(destination.getRealPathText()));
        }
    }
    
    private Instant parseTime(final String s, final String s2) throws DCmdException {
        if (s == null) {
            return null;
        }
        try {
            return Instant.parse(s);
        }
        catch (final DateTimeParseException ex) {
            try {
                return ZonedDateTime.of(LocalDateTime.parse(s), ZoneId.systemDefault()).toInstant();
            }
            catch (final DateTimeParseException ex2) {
                try {
                    final LocalTime parse = LocalTime.parse(s);
                    LocalDate localDate = LocalDate.now();
                    final Instant instant = ZonedDateTime.of(localDate, parse, ZoneId.systemDefault()).toInstant();
                    final Instant now = Instant.now();
                    if (instant.isAfter(now) && !instant.isBefore(now.plusSeconds(3600L))) {
                        localDate = localDate.minusDays(1L);
                    }
                    return ZonedDateTime.of(localDate, parse, ZoneId.systemDefault()).toInstant();
                }
                catch (final DateTimeParseException ex3) {
                    if (s.startsWith("-")) {
                        try {
                            return Instant.now().minus((TemporalAmount)Duration.ofNanos(Utils.parseTimespan(s.substring(1))));
                        }
                        catch (final NumberFormatException ex4) {}
                    }
                    throw new DCmdException("Dump failed, not a valid %s time.", new Object[] { s2 });
                }
            }
        }
    }
    
    private PlatformRecording newSnapShot(final PlatformRecorder platformRecorder, final Recording recording, final Boolean b) throws DCmdException, IOException {
        if (recording == null) {
            final PlatformRecording temporaryRecording = platformRecorder.newTemporaryRecording();
            platformRecorder.fillWithRecordedData(temporaryRecording, b);
            return temporaryRecording;
        }
        return PrivateAccess.getInstance().getPlatformRecording(recording).newSnapshotClone("Dumped by user", b);
    }
}
