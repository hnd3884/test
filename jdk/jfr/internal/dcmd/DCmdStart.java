package jdk.jfr.internal.dcmd;

import jdk.jfr.internal.JVM;
import java.nio.file.Path;
import java.time.Duration;
import java.nio.file.InvalidPathException;
import jdk.jfr.internal.SecuritySupport;
import jdk.jfr.internal.PrivateAccess;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import jdk.jfr.Recording;
import jdk.jfr.FlightRecorder;
import jdk.jfr.internal.OldObjectSample;
import java.text.ParseException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import jdk.jfr.internal.jfc.JFC;
import java.util.HashMap;
import java.util.Arrays;
import jdk.jfr.internal.Logger;
import jdk.jfr.internal.LogLevel;
import jdk.jfr.internal.LogTag;

final class DCmdStart extends AbstractDCmd
{
    public String execute(final String name, final String[] array, final Long n, final Long n2, final Boolean b, final String s, final Long n3, final Long n4, Boolean true, final Boolean b2) throws DCmdException {
        if (Logger.shouldLog(LogTag.JFR_DCMD, LogLevel.DEBUG)) {
            Logger.log(LogTag.JFR_DCMD, LogLevel.DEBUG, "Executing DCmdStart: name=" + name + ", settings=" + ((array != null) ? Arrays.asList(array) : "(none)") + ", delay=" + n + ", duration=" + n2 + ", disk=" + b + ", filename=" + s + ", maxage=" + n3 + ", maxsize=" + n4 + ", dumponexit =" + true + ", path-to-gc-roots=" + b2);
        }
        if (name != null) {
            try {
                Integer.parseInt(name);
                throw new DCmdException("Name of recording can't be numeric", new Object[0]);
            }
            catch (final NumberFormatException ex) {}
        }
        if (n2 == null && Boolean.FALSE.equals(true) && s != null) {
            throw new DCmdException("Filename can only be set for a time bound recording or if dumponexit=true. Set duration/dumponexit or omit filename.", new Object[0]);
        }
        if (array.length == 1 && array[0].length() == 0) {
            throw new DCmdException("No settings specified. Use settings=none to start without any settings", new Object[0]);
        }
        final HashMap settings = new HashMap();
        for (final String s2 : array) {
            try {
                settings.putAll(JFC.createKnown(s2).getSettings());
            }
            catch (final FileNotFoundException ex2) {
                throw new DCmdException("Could not find settings file'" + s2 + "'", new Object[] { ex2 });
            }
            catch (final IOException | ParseException ex3) {
                throw new DCmdException("Could not parse settings file '" + array[0] + "'", new Object[] { ex3 });
            }
        }
        OldObjectSample.updateSettingPathToGcRoots(settings, b2);
        if (n2 != null && n2 < 1000000000L) {
            throw new DCmdException("Could not start recording, duration must be at least 1 second.", new Object[0]);
        }
        if (n != null && n < 1000000000L) {
            throw new DCmdException("Could not start recording, delay must be at least 1 second.", new Object[0]);
        }
        if (!FlightRecorder.isInitialized() && n == null) {
            this.initializeWithForcedInstrumentation(settings);
        }
        final Recording recording = new Recording();
        if (name != null) {
            recording.setName(name);
        }
        if (b != null) {
            recording.setToDisk(b);
        }
        recording.setSettings(settings);
        SecuritySupport.SafePath resolvePath = null;
        if (s != null) {
            try {
                if (true == null) {
                    true = Boolean.TRUE;
                }
                final Path value = Paths.get(s, new String[0]);
                if (Files.isDirectory(value, new LinkOption[0]) && Boolean.TRUE.equals(true)) {
                    PrivateAccess.getInstance().getPlatformRecording(recording).setDumpOnExitDirectory(new SecuritySupport.SafePath(value));
                }
                else {
                    resolvePath = this.resolvePath(recording, s);
                    recording.setDestination(resolvePath.toPath());
                }
            }
            catch (final IOException | InvalidPathException ex4) {
                recording.close();
                throw new DCmdException("Could not start recording, not able to write to file %s. %s ", new Object[] { s, ((Throwable)ex4).getMessage() });
            }
        }
        if (n3 != null) {
            recording.setMaxAge(Duration.ofNanos(n3));
        }
        if (n4 != null) {
            recording.setMaxSize(n4);
        }
        if (n2 != null) {
            recording.setDuration(Duration.ofNanos(n2));
        }
        if (true != null) {
            recording.setDumpOnExit(true);
        }
        if (n != null) {
            final Duration ofNanos = Duration.ofNanos(n);
            recording.scheduleStart(ofNanos);
            this.print("Recording " + recording.getId() + " scheduled to start in ");
            this.printTimespan(ofNanos, " ");
            this.print(".");
        }
        else {
            recording.start();
            this.print("Started recording " + recording.getId() + ".");
        }
        if (recording.isToDisk() && n2 == null && n3 == null && n4 == null) {
            this.print(" No limit specified, using maxsize=250MB as default.");
            recording.setMaxSize(262144000L);
        }
        if (resolvePath != null && n2 != null) {
            this.println(" The result will be written to:", new Object[0]);
            this.println();
            this.printPath(resolvePath);
        }
        else {
            this.println();
            this.println();
            final String s3 = (n2 == null) ? "dump" : "stop";
            final String s4 = (s == null) ? "filename=FILEPATH " : "";
            String s5 = "name=" + recording.getId();
            if (name != null) {
                s5 = "name=" + AbstractDCmd.quoteIfNeeded(name);
            }
            this.print("Use jcmd " + this.getPid() + " JFR." + s3 + " " + s5 + " " + s4 + "to copy recording data to file.");
            this.println();
        }
        return this.getResult();
    }
    
    private void initializeWithForcedInstrumentation(final Map<String, String> map) {
        if (!this.hasJDKEvents(map)) {
            return;
        }
        final JVM jvm = JVM.getJVM();
        try {
            jvm.setForceInstrumentation(true);
            FlightRecorder.getFlightRecorder();
        }
        finally {
            jvm.setForceInstrumentation(false);
        }
    }
    
    private boolean hasJDKEvents(final Map<String, String> map) {
        final String[] array = { "FileRead", "FileWrite", "SocketRead", "SocketWrite", "JavaErrorThrow", "JavaExceptionThrow", "FileForce" };
        for (int length = array.length, i = 0; i < length; ++i) {
            if ("true".equals(map.get("jdk." + array[i] + "#enabled"))) {
                return true;
            }
        }
        return false;
    }
}
