package com.azul.crs.client.service;

import com.azul.crs.shared.Utils;
import com.azul.crs.util.logging.Logger;
import com.azul.crs.client.PerformanceMetrics;
import java.util.Map;
import java.util.HashMap;
import com.azul.crs.shared.models.VMEvent;
import com.azul.crs.client.Client;

public class FirstCallMonitor implements ClientService
{
    private static final FirstCallMonitor instance;
    private Client client;
    private volatile boolean started;
    private volatile boolean stopped;
    private long _count;
    
    private FirstCallMonitor() {
    }
    
    public static FirstCallMonitor getInstance(final Client client) {
        FirstCallMonitor.instance.client = client;
        return FirstCallMonitor.instance;
    }
    
    private static VMEvent methodEntryEvent(final int classId, final String methodName, final long eventTime) {
        final Map<String, String> payload = new HashMap<String, String>();
        payload.put("classId", Integer.toString(classId));
        payload.put("methodName", methodName);
        return new VMEvent<Map<String, String>>().randomEventId().eventType(VMEvent.Type.VM_METHOD_FIRST_CALLED).eventTime(eventTime).eventPayload(payload);
    }
    
    @Override
    public synchronized void start() {
        this.started = true;
    }
    
    @Override
    public synchronized void stop(final long deadline) {
        this.logger().debug("total methods invoked " + this._count, new Object[0]);
        PerformanceMetrics.logMethodEntries(this._count);
        this.started = false;
        this.stopped = true;
    }
    
    public void notifyMethodFirstCalled(final int classId, final String methodName) {
        ++this._count;
        if (this.stopped) {
            return;
        }
        if (!this.started) {
            this.logger().error("service is not yet started", new Object[0]);
            return;
        }
        Logger.getLogger(FirstCallMonitor.class).trace("Entered " + methodName, new Object[0]);
        final long eventTime = Utils.currentTimeMillis();
        this.client.postVMEvent(methodEntryEvent(classId, methodName, eventTime));
    }
    
    static {
        instance = new FirstCallMonitor();
    }
}
