package com.azul.crs.client.service;

import com.azul.crs.shared.Utils;
import com.azul.crs.client.PerformanceMetrics;
import java.util.Map;
import java.util.HashMap;
import com.azul.crs.shared.models.VMEvent;
import java.nio.file.Path;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import com.azul.crs.util.logging.Logger;
import java.io.PrintWriter;
import com.azul.crs.client.Client;

public class ClassLoadMonitor implements ClientService
{
    private static ClassLoadMonitor instance;
    private Client client;
    private volatile boolean started;
    private volatile boolean stopped;
    private long _count;
    private final PrintWriter traceOut;
    private static final char[] digit;
    
    private ClassLoadMonitor() {
        PrintWriter out = null;
        if (this.logger().isEnabled(Logger.Level.TRACE)) {
            try {
                final Path traceOutFileName = Files.createTempFile("CRSClassLoadMonitor", ".log", (FileAttribute<?>[])new FileAttribute[0]);
                this.logger().trace("writing ClassLoadMonitor trace to file %s", traceOutFileName);
                out = new PrintWriter(Files.newBufferedWriter(traceOutFileName, new OpenOption[0]));
            }
            catch (final IOException ignored) {
                ignored.printStackTrace();
            }
        }
        this.traceOut = out;
    }
    
    public static ClassLoadMonitor getInstance(final Client client) {
        ClassLoadMonitor.instance.client = client;
        return ClassLoadMonitor.instance;
    }
    
    private VMEvent classLoadEvent(final String className, final String hashString, final int classId, final int loaderId, final String source, final long eventTime) {
        final Map<String, String> payload = new HashMap<String, String>();
        payload.put("className", className);
        payload.put("hash", hashString);
        payload.put("classId", Integer.toString(classId));
        payload.put("loaderId", Integer.toString(loaderId));
        if (source != null) {
            payload.put("source", source);
        }
        return new VMEvent<Map<String, String>>().randomEventId().eventType(VMEvent.Type.VM_CLASS_LOADED).eventTime(eventTime).eventPayload(payload);
    }
    
    @Override
    public synchronized void start() {
        this.started = true;
    }
    
    @Override
    public synchronized void stop(final long deadline) {
        this.logger().debug("total classes loaded count " + this._count, new Object[0]);
        PerformanceMetrics.logClassLoads(this._count);
        if (this.traceOut != null) {
            this.traceOut.close();
        }
        this.started = false;
        this.stopped = true;
    }
    
    private static String encodeToString(final byte[] hash) {
        final char[] str = new char[hash.length * 2];
        for (int i = 0; i < hash.length; ++i) {
            final byte b = hash[i];
            str[i * 2] = ClassLoadMonitor.digit[b >>> 4 & 0xF];
            str[i * 2 + 1] = ClassLoadMonitor.digit[b & 0xF];
        }
        return new String(str);
    }
    
    public void notifyClassLoad(final String className, final byte[] hash, final int classId, final int loaderId, final String source) {
        ++this._count;
        if (this.stopped) {
            return;
        }
        if (!this.started) {
            Logger.getLogger(ClassLoadMonitor.class).error("service is not yet started", new Object[0]);
        }
        final long eventTime = Utils.currentTimeMillis();
        final String hashString = encodeToString(hash);
        this.client.postVMEvent(this.classLoadEvent(className, hashString, classId, loaderId, source, eventTime));
        if (this.traceOut != null) {
            this.traceOut.printf("%s [%d:%d]", className, loaderId, classId);
        }
    }
    
    static {
        ClassLoadMonitor.instance = new ClassLoadMonitor();
        digit = "0123456789abcdef".toCharArray();
    }
}
