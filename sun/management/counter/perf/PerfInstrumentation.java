package sun.management.counter.perf;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import sun.management.counter.Units;
import java.util.TreeMap;
import sun.management.counter.Counter;
import java.util.SortedMap;
import java.nio.ByteBuffer;

public class PerfInstrumentation
{
    private ByteBuffer buffer;
    private Prologue prologue;
    private long lastModificationTime;
    private long lastUsed;
    private int nextEntry;
    private SortedMap<String, Counter> map;
    
    public PerfInstrumentation(final ByteBuffer buffer) {
        this.prologue = new Prologue(buffer);
        (this.buffer = buffer).order(this.prologue.getByteOrder());
        final int majorVersion = this.getMajorVersion();
        final int minorVersion = this.getMinorVersion();
        if (majorVersion < 2) {
            throw new InstrumentationException("Unsupported version: " + majorVersion + "." + minorVersion);
        }
        this.rewind();
    }
    
    public int getMajorVersion() {
        return this.prologue.getMajorVersion();
    }
    
    public int getMinorVersion() {
        return this.prologue.getMinorVersion();
    }
    
    public long getModificationTimeStamp() {
        return this.prologue.getModificationTimeStamp();
    }
    
    void rewind() {
        this.buffer.rewind();
        this.buffer.position(this.prologue.getEntryOffset());
        this.nextEntry = this.buffer.position();
        this.map = new TreeMap<String, Counter>();
    }
    
    boolean hasNext() {
        return this.nextEntry < this.prologue.getUsed();
    }
    
    Counter getNextCounter() {
        if (!this.hasNext()) {
            return null;
        }
        if (this.nextEntry % 4 != 0) {
            throw new InstrumentationException("Entry index not properly aligned: " + this.nextEntry);
        }
        if (this.nextEntry < 0 || this.nextEntry > this.buffer.limit()) {
            throw new InstrumentationException("Entry index out of bounds: nextEntry = " + this.nextEntry + ", limit = " + this.buffer.limit());
        }
        this.buffer.position(this.nextEntry);
        final PerfDataEntry perfDataEntry = new PerfDataEntry(this.buffer);
        this.nextEntry += perfDataEntry.size();
        Counter counter = null;
        final PerfDataType type = perfDataEntry.type();
        if (type == PerfDataType.BYTE) {
            if (perfDataEntry.units() == Units.STRING && perfDataEntry.vectorLength() > 0) {
                counter = new PerfStringCounter(perfDataEntry.name(), perfDataEntry.variability(), perfDataEntry.flags(), perfDataEntry.vectorLength(), perfDataEntry.byteData());
            }
            else if (perfDataEntry.vectorLength() > 0) {
                counter = new PerfByteArrayCounter(perfDataEntry.name(), perfDataEntry.units(), perfDataEntry.variability(), perfDataEntry.flags(), perfDataEntry.vectorLength(), perfDataEntry.byteData());
            }
            else {
                assert false;
            }
        }
        else if (type == PerfDataType.LONG) {
            if (perfDataEntry.vectorLength() == 0) {
                counter = new PerfLongCounter(perfDataEntry.name(), perfDataEntry.units(), perfDataEntry.variability(), perfDataEntry.flags(), perfDataEntry.longData());
            }
            else {
                counter = new PerfLongArrayCounter(perfDataEntry.name(), perfDataEntry.units(), perfDataEntry.variability(), perfDataEntry.flags(), perfDataEntry.vectorLength(), perfDataEntry.longData());
            }
        }
        else {
            assert false;
        }
        return counter;
    }
    
    public synchronized List<Counter> getAllCounters() {
        while (this.hasNext()) {
            final Counter nextCounter = this.getNextCounter();
            if (nextCounter != null) {
                this.map.put(nextCounter.getName(), nextCounter);
            }
        }
        return new ArrayList<Counter>(this.map.values());
    }
    
    public synchronized List<Counter> findByPattern(final String s) {
        while (this.hasNext()) {
            final Counter nextCounter = this.getNextCounter();
            if (nextCounter != null) {
                this.map.put(nextCounter.getName(), nextCounter);
            }
        }
        final Matcher matcher = Pattern.compile(s).matcher("");
        final ArrayList list = new ArrayList();
        for (final Map.Entry entry : this.map.entrySet()) {
            matcher.reset((CharSequence)entry.getKey());
            if (matcher.lookingAt()) {
                list.add(entry.getValue());
            }
        }
        return list;
    }
}
