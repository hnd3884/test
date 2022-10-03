package com.azul.crs.client.service;

import com.azul.crs.shared.models.VMEvent;
import com.azul.crs.shared.Utils;
import com.azul.crs.client.Client;

public class HeartbeatService implements ClientService
{
    private static final long HEARTBEAT_SLEEP = 5000L;
    private static final int HEARTBEAT_LOG_COUNT = 300;
    private static final long HEARTBEAT_STOP = 60000L;
    private static HeartbeatService instance;
    private Client client;
    private Thread thread;
    private volatile boolean running;
    
    private HeartbeatService() {
    }
    
    public static HeartbeatService getInstance(final Client client) {
        HeartbeatService.instance.client = client;
        return HeartbeatService.instance;
    }
    
    private void run() {
        long heartbeatCount = 0L;
        while (this.running) {
            try {
                Thread.sleep(5000L);
                final long lastHeardTime = Utils.currentTimeMillis();
                this.client.postVMEvent(new VMEvent().randomEventId().eventType(VMEvent.Type.VM_HEARTBEAT).eventTime(lastHeardTime));
                if (++heartbeatCount % 300L != 0L) {
                    continue;
                }
                this.logger().info("CRS client heartbeats: lastHeardTime=%s, count=%,d\n", lastHeardTime, heartbeatCount);
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    @Override
    public synchronized void start() {
        if (this.running) {
            throw new IllegalStateException(this.serviceName() + " is running already");
        }
        (this.thread = new Thread(this::run)).setDaemon(true);
        this.thread.setName("CRSHeartbeat");
        this.running = true;
        this.thread.start();
    }
    
    @Override
    public synchronized void stop(final long deadline) {
        if (!this.running) {
            return;
        }
        try {
            this.running = false;
            this.thread.interrupt();
            this.thread.join(60000L);
        }
        catch (final InterruptedException ex) {}
    }
    
    static {
        HeartbeatService.instance = new HeartbeatService();
    }
}
