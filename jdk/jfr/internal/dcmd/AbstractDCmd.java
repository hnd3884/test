package jdk.jfr.internal.dcmd;

import java.util.Iterator;
import java.time.Duration;
import java.util.Collections;
import java.util.function.Function;
import java.util.Comparator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import jdk.jfr.internal.Utils;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import jdk.jfr.internal.SecuritySupport;
import jdk.jfr.Recording;
import jdk.jfr.internal.JVM;
import jdk.jfr.FlightRecorder;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

abstract class AbstractDCmd
{
    private final StringWriter result;
    private final PrintWriter log;
    
    protected AbstractDCmd() {
        this.result = new StringWriter();
        this.log = new PrintWriter(this.result);
    }
    
    protected final FlightRecorder getFlightRecorder() {
        return FlightRecorder.getFlightRecorder();
    }
    
    public final String getResult() {
        return this.result.toString();
    }
    
    public String getPid() {
        return JVM.getJVM().getPid();
    }
    
    protected final SecuritySupport.SafePath resolvePath(final Recording recording, final String s) throws InvalidPathException {
        if (s == null) {
            return this.makeGenerated(recording, Paths.get(".", new String[0]));
        }
        final Path value = Paths.get(s, new String[0]);
        if (Files.isDirectory(value, new LinkOption[0])) {
            return this.makeGenerated(recording, value);
        }
        return new SecuritySupport.SafePath(value.toAbsolutePath().normalize());
    }
    
    private SecuritySupport.SafePath makeGenerated(final Recording recording, final Path path) {
        return new SecuritySupport.SafePath(path.toAbsolutePath().resolve(Utils.makeFilename(recording)).normalize());
    }
    
    protected final Recording findRecording(final String s) throws DCmdException {
        try {
            return this.findRecordingById(Integer.parseInt(s));
        }
        catch (final NumberFormatException ex) {
            return this.findRecordingByName(s);
        }
    }
    
    protected final void reportOperationComplete(final String s, final String s2, final SecuritySupport.SafePath safePath) {
        this.print(s);
        this.print(" recording");
        if (s2 != null) {
            this.print(" \"" + s2 + "\"");
        }
        if (safePath != null) {
            this.print(",");
            try {
                this.print(" ");
                this.printBytes(SecuritySupport.getFileSize(safePath));
            }
            catch (final IOException ex) {}
            this.println(" written to:", new Object[0]);
            this.println();
            this.printPath(safePath);
        }
        else {
            this.println(".", new Object[0]);
        }
    }
    
    protected final List<Recording> getRecordings() {
        final ArrayList list = new ArrayList((Collection<? extends E>)this.getFlightRecorder().getRecordings());
        Collections.sort((List<Object>)list, Comparator.comparing((Function<? super Object, ? extends Comparable>)Recording::getId));
        return list;
    }
    
    static String quoteIfNeeded(final String s) {
        if (s.contains(" ")) {
            return "\\\"" + s + "\\\"";
        }
        return s;
    }
    
    protected final void println() {
        this.log.println();
    }
    
    protected final void print(final String s) {
        this.log.print(s);
    }
    
    protected final void print(final String s, final Object... array) {
        this.log.printf(s, array);
    }
    
    protected final void println(final String s, final Object... array) {
        this.print(s, array);
        this.println();
    }
    
    protected final void printBytes(final long n) {
        this.print(Utils.formatBytes(n));
    }
    
    protected final void printTimespan(final Duration duration, final String s) {
        this.print(Utils.formatTimespan(duration, s));
    }
    
    protected final void printPath(final SecuritySupport.SafePath safePath) {
        if (safePath == null) {
            this.print("N/A");
            return;
        }
        try {
            this.printPath(SecuritySupport.getAbsolutePath(safePath).toPath());
        }
        catch (final IOException ex) {
            this.printPath(safePath.toPath());
        }
    }
    
    protected final void printPath(final Path path) {
        try {
            this.println(path.toAbsolutePath().toString(), new Object[0]);
        }
        catch (final SecurityException ex) {
            this.println(path.toString(), new Object[0]);
        }
    }
    
    private Recording findRecordingById(final int n) throws DCmdException {
        for (final Recording recording : this.getFlightRecorder().getRecordings()) {
            if (recording.getId() == n) {
                return recording;
            }
        }
        throw new DCmdException("Could not find %d.\n\nUse JFR.check without options to see list of all available recordings.", new Object[] { n });
    }
    
    private Recording findRecordingByName(final String s) throws DCmdException {
        for (final Recording recording : this.getFlightRecorder().getRecordings()) {
            if (s.equals(recording.getName())) {
                return recording;
            }
        }
        throw new DCmdException("Could not find %s.\n\nUse JFR.check without options to see list of all available recordings.", new Object[] { s });
    }
}
