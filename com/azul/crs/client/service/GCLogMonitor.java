package com.azul.crs.client.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import com.azul.crs.shared.models.VMArtifact;
import java.util.Iterator;
import java.util.List;
import com.azul.crs.client.Inventory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import com.azul.crs.client.Client;

public class GCLogMonitor implements ClientService
{
    private static long CHECK_DELAY;
    private static int BUFFER_SIZE;
    private final Client client;
    private final long startTime;
    private final AtomicLong reported;
    private FileTailer tailer;
    private volatile boolean running;
    
    @Override
    public String serviceName() {
        return "client.service.GCLog";
    }
    
    private GCLogMonitor(final Client client, final long startTime) {
        this.client = client;
        this.startTime = startTime;
        this.reported = new AtomicLong();
    }
    
    public static GCLogMonitor getInstance(final Client client, final long startTime) {
        return new GCLogMonitor(client, startTime);
    }
    
    private static Map<String, ?> gclogOptions() {
        final Map<String, Object> options = new HashMap<String, Object>();
        final List<String> jvmArgs = Inventory.jvmArgs();
        for (final String arg : jvmArgs) {
            for (final Option o : Option.values()) {
                if (o.matchAndSet(arg, options)) {
                    break;
                }
            }
        }
        return options;
    }
    
    private FileTailerListener gclogListener(final int artifactId) {
        return new FileTailerListener() {
            @Override
            public void handle(final byte[] data, final int size) {
                GCLogMonitor.this.client.postVMArtifactData(VMArtifact.Type.GC_LOG, artifactId, data, size);
                final long reported = GCLogMonitor.this.reported.addAndGet(size);
                GCLogMonitor.this.logger().info("appended GC log artifact %s: size=%,d bytes, reported=%,d bytes", artifactId, size, reported);
            }
            
            @Override
            public void handle(final Exception ex) {
                GCLogMonitor.this.logger().error("failed to tail GC log file: %s", ex.toString());
            }
            
            @Override
            public void fileRotated(final String details) {
                GCLogMonitor.this.logger().info("GC log file rotated: " + details, new Object[0]);
            }
            
            @Override
            public void fileNotFound() {
                GCLogMonitor.this.logger().info("GC log file not found", new Object[0]);
            }
            
            @Override
            public void interrupted() {
                GCLogMonitor.this.logger().info("GC log tailing interrupted", new Object[0]);
            }
        };
    }
    
    @Override
    public synchronized void start() {
        if (this.running) {
            throw new IllegalStateException(this.serviceName() + " is running already");
        }
        final Map<String, ?> options = gclogOptions();
        final String gclogFileName = (String)options.get(Option.LOG_GC.flag());
        if (gclogFileName == null) {
            return;
        }
        if (gclogFileName.indexOf("%t") >= 0 || gclogFileName.indexOf("%p") >= 0) {
            this.logger().info("unsupported '%' macros in GC log file name", new Object[0]);
            return;
        }
        final int artifactId = this.client.createArtifactId();
        this.logger().info("created VM artifact: " + artifactId, new Object[0]);
        final File gclogFile = new File(gclogFileName);
        final Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("name", gclogFile.getName());
        metadata.put("tags", Inventory.instanceTags());
        metadata.put("options", options);
        this.client.postVMArtifact(VMArtifact.Type.GC_LOG, artifactId, metadata);
        final FileTailerListener listener = this.gclogListener(artifactId);
        if (Boolean.TRUE.equals(options.get(Option.USE_GC_LOG_FILE_ROTATION.flag()))) {
            final String logCountStr = (String)options.get(Option.NUMBER_OF_GC_LOG_FILES.flag());
            final int logCount = Integer.parseInt(logCountStr);
            this.logger().info("GC log rotation requested: logCount=" + logCount, new Object[0]);
            this.tailer = new GCRotatingLogTailer.Builder(gclogFile).serviceName(this.serviceName()).listener(listener).delayTimeout(GCLogMonitor.CHECK_DELAY).bufSize(GCLogMonitor.BUFFER_SIZE).logCount(logCount).startTime(this.startTime).build();
        }
        else {
            this.tailer = new FileTailer.Builder<FileTailer.Builder<FileTailer.Builder<FileTailer.Builder>>>(gclogFile).serviceName(this.serviceName()).listener(listener).delayTimeout(GCLogMonitor.CHECK_DELAY).bufSize(GCLogMonitor.BUFFER_SIZE).build();
        }
        this.running = true;
        this.tailer.start();
    }
    
    @Override
    public synchronized void stop(final long deadline) {
        if (!this.running) {
            return;
        }
        this.tailer.stop(deadline);
        this.running = false;
        this.logger().info("GC log monitor stopped: reported=%,d bytes", this.reported.get());
    }
    
    static {
        GCLogMonitor.CHECK_DELAY = 1000L;
        GCLogMonitor.BUFFER_SIZE = 102400;
    }
    
    enum Option
    {
        LOG_GC("-X(loggc):(\\S+)"), 
        PRINT_GC("-XX:\\+(PrintGC)"), 
        PRINT_GC_DETAILS("-XX:\\+(PrintGCDetails)"), 
        PRINT_GC_TIME_STAMPS("-XX:\\+(PrintGCTimeStamps)"), 
        PRINT_GC_DATE_STAMPS("-XX:\\+(PrintGCDateStamps)"), 
        PRINT_HEAP_AT_GC("-XX:\\+(PrintHeapAtGC)"), 
        USE_GC_LOG_FILE_ROTATION("-XX:\\+(UseGCLogFileRotation)"), 
        NUMBER_OF_GC_LOG_FILES("-XX:(NumberOfGCLogFiles)=(\\S+)"), 
        GC_LOG_FILE_SIZE("-XX:(GCLogFileSize)=(\\S+)");
        
        private final Pattern pattern;
        private final String flag;
        
        private Option(final String regex) {
            this.flag = regex.substring(regex.indexOf(40) + 1, regex.indexOf(41));
            this.pattern = Pattern.compile(regex);
        }
        
        public Pattern pattern() {
            return this.pattern;
        }
        
        public String flag() {
            return this.flag;
        }
        
        public boolean matchAndSet(final String s, final Map<String, Object> options) {
            final Matcher matcher = this.pattern.matcher(s);
            if (matcher.matches()) {
                final String name = matcher.group(1);
                final Object value = (matcher.groupCount() > 1) ? matcher.group(2) : Boolean.valueOf(true);
                options.put(name, value);
                return true;
            }
            return false;
        }
    }
}
