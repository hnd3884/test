package com.azul.crs.client.service;

import com.azul.crs.client.Response;
import java.io.IOException;
import com.azul.crs.client.Result;
import java.io.InputStream;
import java.io.FileInputStream;
import com.azul.crs.client.PerformanceMetrics;
import java.util.Collection;
import com.azul.crs.shared.Utils;
import java.io.File;
import com.azul.crs.shared.models.VMArtifactChunk;
import com.azul.crs.client.Client;

public class UploadService implements ClientService
{
    private static final int MAX_QUEUE_SIZE = 50000;
    private static final int MAX_WORKERS = 1;
    private static final int BATCH_SIZE = 1;
    private boolean isConnected;
    private final QueueService<Job> queue;
    private final Client client;
    
    public UploadService(final Client client) {
        this.queue = new QueueService.Builder<Job>().maxQueueSize(50000).maxBatchSize(1).maxWorkers(1).processBatch(this::send).stopMarker(new Job(null, null, null)).name("UPLOAD").build();
        this.client = client;
    }
    
    @Override
    public void start() {
    }
    
    @Override
    public void stop(final long deadline) {
        if (Utils.currentTimeCount() < deadline) {
            this.logger().info("awaiting artifact data to flush to the cloud", new Object[0]);
            this.queue.stop(deadline);
        }
        else {
            this.logger().debug("skipping flush of artifact data to the cloud because no time left", new Object[0]);
        }
    }
    
    public void cancel() {
        this.queue.cancel();
    }
    
    public void connectionEstablished() {
        this.isConnected = true;
        this.logger().trace("connection established, sending artifact data to the cloud", new Object[0]);
        this.queue.start();
    }
    
    public void post(final VMArtifactChunk chunk, final File chunkData, final Client.UploadListener<VMArtifactChunk> listener) {
        this.queue.add(new Job(chunk, chunkData, listener));
    }
    
    public void sync(final long deadline) {
        if (Utils.currentTimeCount() < deadline) {
            this.logger().trace("syncing artifact data to the cloud", new Object[0]);
            this.queue.sync(deadline);
        }
        else {
            this.logger().debug("not syncing artifact data to the cloud because no time left", new Object[0]);
        }
    }
    
    private void send(final String workerId, final Collection<Job> jobs) {
        final Job job = jobs.iterator().next();
        final VMArtifactChunk chunk = job.getChunk();
        try {
            final File data = job.getData();
            PerformanceMetrics.logArtifactBytes(data.length());
            this.logger().trace("uploading " + data.getName(), new Object[0]);
            final Response<VMArtifactChunk> response = this.client.getConnectionManager().sendVMArtifactChunk(chunk, new FileInputStream(data));
            this.logger().trace("upload finished", new Object[0]);
            if (response.successful()) {
                job.getListener().uploadComplete(chunk);
            }
            else {
                job.getListener().uploadFailed(chunk, new Result<VMArtifactChunk>(response));
            }
        }
        catch (final IOException e) {
            job.getListener().uploadFailed(chunk, new Result<VMArtifactChunk>(e));
        }
    }
    
    private class Job
    {
        private VMArtifactChunk chunk;
        private File data;
        private Client.UploadListener<VMArtifactChunk> listener;
        
        public Job(final VMArtifactChunk chunk, final File data, final Client.UploadListener<VMArtifactChunk> listener) {
            this.chunk = chunk;
            this.data = data;
            this.listener = listener;
        }
        
        public VMArtifactChunk getChunk() {
            return this.chunk;
        }
        
        public File getData() {
            return this.data;
        }
        
        public Client.UploadListener<VMArtifactChunk> getListener() {
            return this.listener;
        }
    }
}
