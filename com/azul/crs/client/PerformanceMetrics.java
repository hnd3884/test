package com.azul.crs.client;

import com.azul.crs.util.logging.Logger;
import java.util.Iterator;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceMetrics
{
    private final AtomicLong communicationMillis;
    private final AtomicLong numBytesOut;
    private final AtomicLong numBytesIn;
    private long shutdownMillis;
    private final AtomicLong preShutdownMillis;
    private final AtomicLong numEvents;
    private final AtomicLong numEventBatches;
    private final AtomicLong[] numEventHistogram;
    private final AtomicLong numConnections;
    private final AtomicLong handshakeMillis;
    private final AtomicInteger maxQueueLength;
    private final AtomicLong numBytesInArtifacts;
    private final AtomicLong numClassLoads;
    private final AtomicLong numMethodEntries;
    private static final Map<String, Number> fieldDesc;
    private static PerformanceMetrics instance;
    
    public PerformanceMetrics() {
        this.communicationMillis = new AtomicLong();
        this.numBytesOut = new AtomicLong();
        this.numBytesIn = new AtomicLong();
        this.preShutdownMillis = new AtomicLong();
        this.numEvents = new AtomicLong();
        this.numEventBatches = new AtomicLong();
        this.numEventHistogram = new AtomicLong[20];
        this.numConnections = new AtomicLong();
        this.handshakeMillis = new AtomicLong();
        this.maxQueueLength = new AtomicInteger();
        this.numBytesInArtifacts = new AtomicLong();
        this.numClassLoads = new AtomicLong();
        this.numMethodEntries = new AtomicLong();
    }
    
    static void init() {
        PerformanceMetrics.instance = new PerformanceMetrics();
        for (int i = 0; i < PerformanceMetrics.instance.numEventHistogram.length; ++i) {
            PerformanceMetrics.instance.numEventHistogram[i] = new AtomicLong();
        }
        for (final Field f : PerformanceMetrics.class.getDeclaredFields()) {
            if (Number.class.isAssignableFrom(f.getType())) {
                try {
                    PerformanceMetrics.fieldDesc.put(f.getName(), (Number)f.get(PerformanceMetrics.instance));
                }
                catch (final IllegalAccessException shouldNotHappen) {
                    shouldNotHappen.printStackTrace();
                }
            }
        }
    }
    
    static void logNetworkTime(final long duration) {
        PerformanceMetrics.instance.communicationMillis.addAndGet(duration);
    }
    
    static void logHandshakeTime(final long duration) {
        PerformanceMetrics.instance.handshakeMillis.addAndGet(duration);
        PerformanceMetrics.instance.numConnections.incrementAndGet();
    }
    
    static void logBytes(final long in, final long out) {
        PerformanceMetrics.instance.numBytesIn.addAndGet(in);
        PerformanceMetrics.instance.numBytesOut.addAndGet(out);
    }
    
    static void logShutdown(final long duration) {
        PerformanceMetrics.instance.shutdownMillis = duration;
    }
    
    public static void logEventBatch(final long size) {
        PerformanceMetrics.instance.numEvents.addAndGet(size);
        PerformanceMetrics.instance.numEventBatches.incrementAndGet();
        PerformanceMetrics.instance.numEventHistogram[(int)(Math.log((double)size) / Math.log(2.0))].incrementAndGet();
    }
    
    public static void logClassLoads(final long count) {
        PerformanceMetrics.instance.numClassLoads.addAndGet(count);
    }
    
    public static void logMethodEntries(final long count) {
        PerformanceMetrics.instance.numMethodEntries.addAndGet(count);
    }
    
    public static void logEventQueueLength(final int size) {
        final AtomicInteger l = PerformanceMetrics.instance.maxQueueLength;
        int prev;
        do {
            prev = l.get();
        } while (prev < size && !l.compareAndSet(prev, size));
    }
    
    public static Map logPreShutdown(final long duration) {
        PerformanceMetrics.instance.preShutdownMillis.set(duration);
        return PerformanceMetrics.instance.toEventPayload();
    }
    
    public static void logArtifactBytes(final long bytes) {
        PerformanceMetrics.instance.numBytesInArtifacts.addAndGet(bytes);
    }
    
    private Map toEventPayload() {
        final Map<String, String> data = new HashMap<String, String>();
        for (final Map.Entry<String, Number> e : PerformanceMetrics.fieldDesc.entrySet()) {
            data.put(e.getKey(), e.getValue().toString());
        }
        return data;
    }
    
    static void report() {
        final Logger logger = Logger.getLogger(PerformanceMetrics.class);
        if (logger.isEnabled(Logger.Level.INFO)) {
            final StringBuilder numEventsHistogram = new StringBuilder();
            for (int i = 0; i < PerformanceMetrics.instance.numEventHistogram.length; ++i) {
                numEventsHistogram.append(PerformanceMetrics.instance.numEventHistogram[i].get()).append(' ');
            }
            logger.info("total communication duration %.3fs\nnumber of connections established %d, %.3fs spent in handshake\ntotal bytes in %.3fM\ntotal event data bytes out %.3fM\ntotal artifacts bytes %.3fM\nmaximum queue length %d\nshutdown delay %.3fs (pre %.3fs)\nclasses loaded %d\nmethods invoked %d\nevents sent %d batches %d [%s]\n", PerformanceMetrics.instance.communicationMillis.get() / 1000.0, PerformanceMetrics.instance.numConnections.get(), PerformanceMetrics.instance.handshakeMillis.get() / 1000.0, PerformanceMetrics.instance.numBytesIn.get() / 1024.0 / 1024.0, PerformanceMetrics.instance.numBytesOut.get() / 1024.0 / 1024.0, PerformanceMetrics.instance.numBytesInArtifacts.get() / 1024.0 / 1024.0, PerformanceMetrics.instance.maxQueueLength.get(), PerformanceMetrics.instance.shutdownMillis / 1000.0, PerformanceMetrics.instance.preShutdownMillis.get() / 1000.0, PerformanceMetrics.instance.numClassLoads.get(), PerformanceMetrics.instance.numMethodEntries.get(), PerformanceMetrics.instance.numEvents.get(), PerformanceMetrics.instance.numEventBatches.get(), numEventsHistogram.toString());
        }
    }
    
    static {
        fieldDesc = new HashMap<String, Number>();
    }
}
