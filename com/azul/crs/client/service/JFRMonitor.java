package com.azul.crs.client.service;

import java.util.concurrent.TimeUnit;
import com.azul.crs.shared.models.VMArtifact;
import jdk.jfr.RecordingState;
import com.azul.crs.client.Inventory;
import java.util.Iterator;
import com.azul.crs.shared.models.Payload;
import com.azul.crs.client.Result;
import com.azul.crs.shared.models.VMArtifactChunk;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.time.Instant;
import java.nio.file.Path;
import jdk.jfr.Configuration;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.Map;
import jdk.jfr.Recording;
import com.azul.crs.client.Client;
import jdk.jfr.FlightRecorder;
import java.util.concurrent.ExecutorService;
import com.azul.crs.jfr.access.FlightRecorderAccess;
import jdk.jfr.FlightRecorderListener;

public class JFRMonitor implements ClientService, FlightRecorderListener, FlightRecorderAccess.FlightRecorderCallbacks
{
    private final ExecutorService executor;
    private static volatile JFRMonitor instance;
    private boolean running;
    private boolean isJfrInitialized;
    private final FlightRecorder fr;
    private Client client;
    private String params;
    private Recording theRecording;
    private Map<RecordingMirror, RecordingMirror> knownRecordings;
    private boolean noLifeTimeJfr;
    private int sequenceNumber;
    private final Object shutdownJfrMonitor;
    private FlightRecorderAccess access;
    private static final boolean DEBUG = true;
    private static final String SERVICE_NAME = "client.service.JFR";
    
    @Override
    public String serviceName() {
        return "client.service.JFR";
    }
    
    private JFRMonitor(final Client client, final String params) {
        this.executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                final Thread t = new Thread(r);
                t.setName("CRSJFRMonitor");
                t.setDaemon(true);
                return t;
            }
        });
        this.shutdownJfrMonitor = new Object();
        this.client = client;
        this.params = params;
        FlightRecorder fr = null;
        if (FlightRecorder.isAvailable()) {
            try {
                fr = FlightRecorder.getFlightRecorder();
            }
            catch (final IllegalStateException ex) {
                this.logger().warning("Cannot initialize. Disabling JFR monitoring. %s", ex);
            }
        }
        else {
            this.logger().info("JFR is not available in this VM", new Object[0]);
        }
        this.fr = fr;
        if (fr != null) {
            FlightRecorder.addListener((FlightRecorderListener)this);
        }
    }
    
    public static JFRMonitor getInstance(final Client client, final String params) {
        if (JFRMonitor.instance == null) {
            synchronized (JFRMonitor.class) {
                if (JFRMonitor.instance == null) {
                    JFRMonitor.instance = new JFRMonitor(client, params);
                }
            }
        }
        if (!Objects.equals(client, JFRMonitor.instance.client) || !Objects.equals(params, JFRMonitor.instance.params)) {
            throw new IllegalArgumentException("client.service.JFR: service instance with other parameters is created already");
        }
        return JFRMonitor.instance;
    }
    
    private void setParams() {
        if (this.params == null || "".equals(this.params)) {
            this.theRecording = new Recording();
            this.logger().info("started lifetime recording with empty configuration", new Object[0]);
        }
        else if ("disable".equals(this.params)) {
            this.noLifeTimeJfr = true;
        }
        else {
            try {
                this.theRecording = new Recording(Configuration.create(new File(this.params).toPath()));
                this.logger().info("started lifetime recording with configuration from " + this.params, new Object[0]);
            }
            catch (final Exception ex) {
                this.logger().error("cannot read or parse specified JFR configuration file " + this.params + ". recording stopped", new Object[0]);
                this.noLifeTimeJfr = true;
            }
        }
        if (!this.noLifeTimeJfr) {
            this.theRecording.setName("lifetime recording");
            this.theRecording.start();
        }
    }
    
    @Override
    public void start() {
        if (this.fr == null) {
            return;
        }
        this.setParams();
        this.running = true;
    }
    
    private void send(final Object chunk, final Path path, final Instant startTime, final Instant endTime, final long size, final Set<RecordingMirror> recordings) {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("startTime", startTime.toEpochMilli());
        attributes.put("endTime", endTime.toEpochMilli());
        attributes.put("sequenceNumber", Integer.toString(this.sequenceNumber++));
        this.logger().info("sending chunk data from " + path, new Object[0]);
        final Set<String> artifactsId = new HashSet<String>(recordings.size());
        for (final RecordingMirror r : recordings) {
            artifactsId.add(Client.artifactIdToString(r.id));
        }
        this.client.postVMArtifactChunk(artifactsId, attributes, path.toFile(), new Client.UploadListener<VMArtifactChunk>() {
            @Override
            public void uploadComplete(final VMArtifactChunk request) {
                this.release();
            }
            
            @Override
            public void uploadFailed(final VMArtifactChunk request, final Result<VMArtifactChunk> result) {
                JFRMonitor.this.logger().error("Failed to send recording chunk %s: %s%s", path, result, Client.isVMShutdownInitiated() ? "(expected during shutdown if timeout is exceeded)" : "");
                this.release();
            }
            
            private void release() {
                JFRMonitor.this.logger().trace("releasing chunk %s", chunk);
                try {
                    JFRMonitor.this.access.releaseRepositoryChunk(chunk);
                }
                catch (final FlightRecorderAccess.AccessException shouldnothappen) {
                    shouldnothappen.printStackTrace();
                }
            }
        });
    }
    
    private void send(final RecordingMirror recording) {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("tags", Inventory.instanceTags());
        attributes.put("startTime", recording.startTime.toEpochMilli());
        attributes.put("state", recording.state.toString());
        if (recording.state == RecordingState.STOPPED || recording.state == RecordingState.CLOSED) {
            attributes.put("stopTime", recording.stopTime.toEpochMilli());
        }
        attributes.put("name", recording.name);
        attributes.put("destination", recording.destination);
        recording.id = this.client.createArtifactId();
        this.client.postVMArtifact(VMArtifact.Type.JFR, recording.id, attributes);
        this.logger().info("posted recording artifact " + recording.id, new Object[0]);
    }
    
    private void patch(final RecordingMirror original, final RecordingMirror recording) {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("state", recording.state.toString());
        if (recording.state == RecordingState.STOPPED || recording.state == RecordingState.CLOSED) {
            attributes.put("stopTime", recording.stopTime.toEpochMilli());
        }
        this.client.postVMArtifactPatch(VMArtifact.Type.JFR, original.id, attributes);
        this.logger().info("patching recording " + original.id, new Object[0]);
    }
    
    @Override
    public void stop(final long deadline) {
        if (this.isJfrInitialized) {
            synchronized (this.shutdownJfrMonitor) {
                if (this.isJfrInitialized) {
                    this.logger().info("waiting for jfr to shutdown", new Object[0]);
                    try {
                        this.shutdownJfrMonitor.wait();
                    }
                    catch (final InterruptedException ex) {}
                    this.logger().info("unblocked CRS client shutdown", new Object[0]);
                }
            }
        }
    }
    
    public void finishJoin() {
        if (!this.running) {
            return;
        }
        this.logger().info("shutting down JFR", new Object[0]);
        if (!this.isJfrInitialized) {
            this.logger().error("invalid shutdown sequence. expecting functional JFR", new Object[0]);
            return;
        }
        if (!this.noLifeTimeJfr && this.theRecording.getState() == RecordingState.RUNNING) {
            this.theRecording.stop();
        }
        this.running = false;
        this.executor.shutdown();
        try {
            if (!this.executor.isTerminated()) {
                this.logger().info("awaiting flush to cloud", new Object[0]);
                System.out.flush();
            }
            this.executor.awaitTermination(60L, TimeUnit.SECONDS);
        }
        catch (final InterruptedException ex) {}
        this.client.finishChunkPost();
        synchronized (this.shutdownJfrMonitor) {
            this.isJfrInitialized = false;
            this.shutdownJfrMonitor.notify();
        }
        this.logger().info("JFR tracking finished", new Object[0]);
    }
    
    public void recorderInitialized(final FlightRecorder recorder) {
        try {
            this.access = FlightRecorderAccess.getAccess(recorder, this);
            this.isJfrInitialized = true;
        }
        catch (final FlightRecorderAccess.AccessException ex) {
            this.logger().error("cannot install associate to JFR " + ex.getCause().toString(), new Object[0]);
        }
    }
    
    public void recordingStateChanged(final Recording recording) {
        this.logger().info("recording " + recording.getName() + " state changed " + recording.getState(), new Object[0]);
        final RecordingMirror rm = new RecordingMirror(recording);
        if (this.knownRecordings == null) {
            this.initKnownRecordins();
        }
        this.executor.submit(new Runnable() {
            @Override
            public void run() {
                JFRMonitor.this.logger().info("recording " + rm.name + " state changed " + rm.state, new Object[0]);
                switch (rm.state) {
                    case RUNNING: {
                        if (!JFRMonitor.this.knownRecordings.keySet().contains(rm)) {
                            JFRMonitor.this.knownRecordings.put(rm, rm);
                            JFRMonitor.this.send(rm);
                            break;
                        }
                        break;
                    }
                    case STOPPED:
                    case CLOSED: {
                        final RecordingMirror original = JFRMonitor.this.knownRecordings.remove(rm);
                        if (original != null) {
                            JFRMonitor.this.patch(original, rm);
                        }
                        JFRMonitor.this.logger().info("stopped recording " + rm.name, new Object[0]);
                        break;
                    }
                }
            }
        });
    }
    
    public void nextChunk(final Object chunk, final Path path, final Instant startTime, final Instant endTime, final long size, final Recording ignoreMe) {
        try {
            this.access.useRepositoryChunk(chunk);
        }
        catch (final FlightRecorderAccess.AccessException shouldnothappen) {
            shouldnothappen.printStackTrace();
        }
        if (!this.isJfrInitialized) {
            this.logger().error("Out of order chunk notification. Ignored", new Object[0]);
            return;
        }
        this.logger().info("scheduling chunk " + path, new Object[0]);
        if (this.knownRecordings == null) {
            this.initKnownRecordins();
        }
        this.executor.submit(new Runnable() {
            @Override
            public void run() {
                if (!JFRMonitor.this.isJfrInitialized) {
                    JFRMonitor.this.logger().error("out of order processing of chunk", new Object[0]);
                    return;
                }
                final Set<RecordingMirror> relatedRecordings = new HashSet<RecordingMirror>(JFRMonitor.this.knownRecordings.size());
                for (final RecordingMirror r : JFRMonitor.this.knownRecordings.keySet()) {
                    if (!r.equals(ignoreMe)) {
                        relatedRecordings.add(r);
                        JFRMonitor.this.logger().info("got chunk for recording " + r.name, new Object[0]);
                    }
                    else {
                        JFRMonitor.this.logger().info("got chunk for ignored recording " + r.name, new Object[0]);
                    }
                }
                if (relatedRecordings.isEmpty()) {
                    JFRMonitor.this.logger().error("found chunk which does not relate to any of " + JFRMonitor.this.knownRecordings.size() + " recordings", new Object[0]);
                }
                else {
                    JFRMonitor.this.send(chunk, path, startTime, (endTime == null) ? Instant.now() : endTime, size, relatedRecordings);
                }
            }
        });
    }
    
    private synchronized void initKnownRecordins() {
        if (this.knownRecordings == null) {
            this.knownRecordings = new HashMap<RecordingMirror, RecordingMirror>();
            for (final Recording r : this.fr.getRecordings()) {
                this.recordingStateChanged(r);
            }
        }
    }
    
    private static class RecordingMirror
    {
        Recording r;
        String name;
        RecordingState state;
        Instant startTime;
        Instant stopTime;
        int id;
        String destination;
        
        RecordingMirror(final Recording r) {
            this.r = r;
            this.name = r.getName();
            this.state = r.getState();
            this.startTime = r.getStartTime();
            this.stopTime = r.getStopTime();
            this.destination = ((r.getDestination() != null) ? r.getDestination().toString() : "<unknown>");
        }
        
        @Override
        public int hashCode() {
            return this.r.hashCode();
        }
        
        public boolean equals(final Recording r) {
            return this.r.equals(r);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof RecordingMirror && this.r.equals(((RecordingMirror)obj).r);
        }
        
        @Override
        public String toString() {
            return Integer.toString(this.id);
        }
    }
}
