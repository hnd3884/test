package jdk.jfr.internal.tool;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.time.temporal.TemporalAccessor;
import java.time.Instant;
import jdk.jfr.EventType;
import java.util.HashMap;
import jdk.jfr.internal.consumer.ChunkHeader;
import jdk.jfr.internal.consumer.RecordingInput;
import java.nio.file.Path;
import java.io.IOException;
import java.util.Deque;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;
import java.time.format.DateTimeFormatter;

final class Summary extends Command
{
    private final DateTimeFormatter DATE_FORMAT;
    
    Summary() {
        this.DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withLocale(Locale.UK).withZone(ZoneOffset.UTC);
    }
    
    @Override
    public String getName() {
        return "summary";
    }
    
    @Override
    public List<String> getOptionSyntax() {
        return Collections.singletonList("<file>");
    }
    
    @Override
    public void displayOptionUsage(final PrintStream printStream) {
        printStream.println("  <file>   Location of the recording file (.jfr) to display information about");
    }
    
    @Override
    public String getDescription() {
        return "Display general information about a recording file (.jfr)";
    }
    
    @Override
    public void execute(final Deque<String> deque) throws UserSyntaxException, UserDataException {
        this.ensureMaxArgumentCount(deque, 1);
        final Path jfrInputFile = this.getJFRInputFile(deque);
        try {
            this.printInformation(jfrInputFile);
        }
        catch (final IOException ex) {
            this.couldNotReadError(jfrInputFile, ex);
        }
    }
    
    private void printInformation(final Path path) throws IOException {
        long n = 0L;
        long n2 = 0L;
        try (final RecordingInput recordingInput = new RecordingInput(path.toFile())) {
            ChunkHeader nextHeader;
            final ChunkHeader chunkHeader = nextHeader = new ChunkHeader(recordingInput);
            String s = "jdk.";
            if (chunkHeader.getMajor() == 1) {
                s = "com.oracle.jdk.";
            }
            final HashMap<Long, Statistics> hashMap = new HashMap<Long, Statistics>();
            hashMap.put(0L, new Statistics(s + "Metadata"));
            hashMap.put(1L, new Statistics(s + "CheckPoint"));
            int max = 0;
            while (true) {
                final long end = nextHeader.getEnd();
                for (final EventType eventType : nextHeader.readMetadata().getEventTypes()) {
                    hashMap.computeIfAbsent(eventType.getId(), p1 -> new Statistics(eventType2.getName()));
                    max = Math.max(max, eventType.getName().length());
                }
                n += nextHeader.getDurationNanos();
                ++n2;
                recordingInput.position(nextHeader.getEventStart());
                while (recordingInput.position() < end) {
                    final long position = recordingInput.position();
                    final int int1 = recordingInput.readInt();
                    final Statistics statistics = hashMap.get(recordingInput.readLong());
                    if (statistics != null) {
                        final Statistics statistics2 = statistics;
                        ++statistics2.count;
                        final Statistics statistics3 = statistics;
                        statistics3.size += int1;
                    }
                    recordingInput.position(position + int1);
                }
                if (nextHeader.isLastChunk()) {
                    break;
                }
                nextHeader = nextHeader.nextHeader();
            }
            this.println();
            final long n3 = chunkHeader.getStartNanos() / 1000000000L;
            final long n4 = chunkHeader.getStartNanos() - n3 * 1000000000L;
            this.println(" Version: " + chunkHeader.getMajor() + "." + chunkHeader.getMinor());
            this.println(" Chunks: " + n2);
            this.println(" Start: " + this.DATE_FORMAT.format(Instant.ofEpochSecond(n3, n4)) + " (UTC)");
            this.println(" Duration: " + (n + 500000000L) / 1000000000L + " s");
            final ArrayList list = new ArrayList<Object>(hashMap.values());
            Collections.sort((List<E>)list, (statistics5, statistics6) -> Long.compare(statistics6.count, statistics5.count));
            this.println();
            final String s2 = "      Count  Size (bytes) ";
            final String s3 = " Event Type";
            final int max2 = Math.max(max, s3.length());
            this.println(s3 + this.pad(max2 - s3.length(), ' ') + s2);
            this.println(this.pad(max2 + s2.length(), '='));
            for (final Statistics statistics4 : list) {
                System.out.printf(" %-" + max2 + "s%10d  %12d\n", statistics4.name, statistics4.count, statistics4.size);
            }
        }
    }
    
    private String pad(final int n, final char c) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            sb.append(c);
        }
        return sb.toString();
    }
    
    private static class Statistics
    {
        String name;
        long count;
        long size;
        
        Statistics(final String name) {
            this.name = name;
        }
    }
}
