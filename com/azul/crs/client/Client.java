package com.azul.crs.client;

import com.azul.crs.shared.models.VMArtifactChunk;
import java.io.File;
import java.util.Set;
import java.util.HashMap;
import com.azul.crs.shared.models.VMArtifact;
import com.azul.crs.shared.Utils;
import java.util.Collection;
import java.io.IOException;
import com.azul.crs.shared.models.VMInstance;
import com.azul.crs.shared.models.VMEvent;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;
import com.azul.crs.shared.models.Payload;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import com.azul.crs.client.service.EventService;
import com.azul.crs.client.service.UploadService;

public class Client
{
    private final ConnectionManager connectionManager;
    private final UploadService uploadService;
    private final EventService eventService;
    private final AtomicInteger nextArtifactId;
    private final AtomicLong nextArtifactChunkId;
    private static volatile long vmShutdownDeadline;
    private long vmShutdownDelay;
    private String vmId;
    
    private void validateProps(final Map<ClientProp, Object> props) {
        for (final ClientProp p : ClientProp.values()) {
            if (p.isMandatory() && props.get(p) == null) {
                throw new IllegalArgumentException("Invalid CRS properties file: missing value for " + p.value());
            }
        }
    }
    
    public Client(final Map<ClientProp, Object> props, final ClientListener listener) {
        this.nextArtifactId = new AtomicInteger();
        this.nextArtifactChunkId = new AtomicLong();
        this.validateProps(props);
        Payload.objectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.vmShutdownDelay = props.get(ClientProp.VM_SHUTDOWN_DELAY);
        this.eventService = EventService.getInstance(this);
        this.connectionManager = new ConnectionManager(props, this, new ConnectionManager.ConnectionListener() {
            @Override
            public void authenticated() {
                Client.this.vmId = Client.this.connectionManager.getVmId();
                listener.authenticated();
            }
            
            @Override
            public void syncFailed(final Result<Payload> reason) {
                listener.syncFailed(reason);
            }
        });
        this.uploadService = new UploadService(this);
    }
    
    public void postVMStart(final Map<String, Object> inventory, final long startTime) throws IOException {
        this.postVMEvent(new VMEvent<VMInstance>().eventType(VMEvent.Type.VM_CREATE).eventPayload(new VMInstance().agentVersion(this.getClientVersion()).owner(this.connectionManager.getMailbox()).inventory(inventory).startTime(startTime)));
    }
    
    public void patchInventory(final Map<String, Object> inventory) {
        this.postVMEvent(new VMEvent<VMInstance>().eventType(VMEvent.Type.VM_PATCH).eventPayload(new VMInstance().inventory(inventory)));
    }
    
    public void postVMEvent(final VMEvent event) {
        this.eventService.add(event.randomEventId());
    }
    
    public void postVMShutdown(final Collection<VMEvent> trailingEvents) {
        this.eventService.addAll(trailingEvents);
        this.eventService.add(new VMEvent().eventType(VMEvent.Type.VM_SHUTDOWN).eventTime(Utils.currentTimeMillis()));
    }
    
    public int createArtifactId() {
        return this.nextArtifactId.incrementAndGet();
    }
    
    public long createArtifactChunkId() {
        return this.nextArtifactChunkId.incrementAndGet();
    }
    
    public void postVMArtifact(final VMArtifact.Type type, final int artifactId, final Map<String, Object> attributes) {
        this.postVMEvent(new VMEvent<VMArtifact>().eventType(VMEvent.Type.VM_ARTIFACT_CREATE).eventPayload(new VMArtifact().artifactType(type).artifactId(artifactIdToString(artifactId)).metadata(attributes)));
    }
    
    public void postVMArtifactPatch(final VMArtifact.Type type, final int artifactId, final Map<String, Object> attributes) {
        this.postVMEvent(new VMEvent<VMArtifact>().eventType(VMEvent.Type.VM_ARTIFACT_PATCH).eventPayload(new VMArtifact().artifactType(type).artifactId(artifactIdToString(artifactId)).metadata(attributes)));
    }
    
    public void postVMArtifactData(final VMArtifact.Type type, final int artifactId, final byte[] data, final int size) {
        final Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("artifactId", artifactIdToString(artifactId));
        payload.put("data", new String(data, 0, 0, size));
        this.postVMEvent(new VMEvent<Map<String, Object>>().eventType(VMEvent.Type.VM_ARTIFACT_DATA).eventPayload(payload));
    }
    
    public void postVMArtifactChunk(final Set<String> artifactIds, final Map<String, Object> attributes, final File chunkData, final UploadListener<VMArtifactChunk> listener) {
        this.uploadService.post(new VMArtifactChunk().artifactIds(artifactIds).metadata(attributes), chunkData, listener);
    }
    
    public void finishChunkPost() {
        final long shutdownDeadline = Client.vmShutdownDeadline;
        this.uploadService.sync((shutdownDeadline > 0L) ? shutdownDeadline : Utils.nextTimeCount(this.vmShutdownDelay));
    }
    
    public static String artifactIdToString(final int artifactId) {
        return Integer.toString(artifactId, 36);
    }
    
    public void startup() throws IOException {
        this.connectionManager.start();
        this.eventService.start();
        this.uploadService.start();
    }
    
    public void connectionEstablished() {
        this.eventService.connectionEstablished();
        this.uploadService.connectionEstablished();
    }
    
    public static boolean isVMShutdownInitiated() {
        return Client.vmShutdownDeadline > 0L;
    }
    
    public static void setVMShutdownInitiated(final long deadline) {
        Client.vmShutdownDeadline = deadline;
    }
    
    public static long getVMShutdownDeadline() {
        return Client.vmShutdownDeadline;
    }
    
    public void shutdown(final long deadline) {
        this.eventService.stop(deadline);
        this.uploadService.stop(deadline);
    }
    
    public void cancel() {
        this.eventService.cancel();
        this.uploadService.cancel();
    }
    
    public String getVmId() {
        return this.vmId;
    }
    
    public String getClientVersion() throws IOException {
        return new Version().clientVersion();
    }
    
    public String getMailbox() {
        return this.connectionManager.getMailbox();
    }
    
    public String getRestAPI() {
        return this.connectionManager.getRestAPI();
    }
    
    public ConnectionManager getConnectionManager() {
        return this.connectionManager;
    }
    
    public enum ClientProp
    {
        API_URL("api.url", true), 
        API_MAILBOX("api.mailbox", true), 
        KS("ks", false), 
        HEAP_BUFFER_SIZE("heapBufferSize", false), 
        FILE_SYSTEM_BUFFER_SIZE("fileSystemBufferSize", false), 
        FILE_SYSTEM_BUFFER_LOCATION("fileSystemBufferLocation", false), 
        NUM_CONCURRENT_CONNECTIONS("numConcurrentConnections", false), 
        BACKUP_JFR_CHUNKS("backupJfrChunks", false), 
        VM_SHUTDOWN_DELAY("delayShutdownInternal", true);
        
        private final Object value;
        private final boolean mandatory;
        
        private ClientProp(final String value, final boolean mandatory) {
            this.value = value;
            this.mandatory = mandatory;
        }
        
        Object value() {
            return this.value;
        }
        
        boolean isMandatory() {
            return this.mandatory;
        }
    }
    
    public interface UploadListener<T extends Payload>
    {
        void uploadComplete(final T p0);
        
        void uploadFailed(final T p0, final Result<T> p1);
    }
    
    public interface ClientListener
    {
        void authenticated();
        
        void syncFailed(final Result<Payload> p0);
    }
}
