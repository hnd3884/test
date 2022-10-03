package com.azul.crs.client.service;

import java.io.IOException;
import com.azul.crs.client.Response;
import com.azul.crs.shared.models.Payload;
import com.azul.crs.client.ConnectionManager;
import com.azul.crs.client.PerformanceMetrics;
import java.util.Collection;
import com.azul.crs.shared.models.VMEvent;
import com.azul.crs.client.Client;

public class EventService implements ClientService
{
    private static final int MAX_QUEUE_SIZE = 50000;
    private static final int MAX_WORKERS = 1;
    private static final int BATCH_SIZE = 1000;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_SLEEP = 100L;
    private final Client client;
    private final QueueService<VMEvent> queue;
    private static final boolean DEBUG = false;
    
    private EventService(final Client client) {
        this.client = client;
        this.queue = (QueueService<VMEvent>)new QueueService.Builder<VMEvent>().maxQueueSize(50000).maxBatchSize(1000).maxWorkers(1).processBatch(this::postWithRetries).stopMarker(new VMEvent()).name("EVENT").build();
    }
    
    public static EventService getInstance(final Client client) {
        return new EventService(client);
    }
    
    public void add(final VMEvent event) {
        this.queue.add(event);
    }
    
    public void addAll(final Collection<VMEvent> events) {
        this.queue.addAll(events);
    }
    
    @Override
    public String serviceName() {
        return "client.service.Events";
    }
    
    @Override
    public void start() {
    }
    
    @Override
    public void stop(final long deadline) {
        this.queue.stop(deadline);
    }
    
    public void cancel() {
        this.queue.cancel();
    }
    
    public void connectionEstablished() {
        this.queue.start();
    }
    
    private void postWithRetries(final String workerId, final Collection<VMEvent> batch) {
        this.logger().info("event worker tries to post batch of %,d VM events", batch.size());
        PerformanceMetrics.logEventBatch(batch.size());
        this.client.getConnectionManager().requestWithRetries(new ConnectionManager.ResponseSupplier<Payload>() {
            @Override
            public Response<Payload> get() throws IOException {
                return EventService.this.client.getConnectionManager().sendVMEventBatch(batch);
            }
        }, "postEventBatch", 3, 100L);
    }
}
