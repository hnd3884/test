package org.apache.tika.pipes;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.nio.file.Path;
import java.util.List;
import org.apache.tika.config.ConfigBase;

public class PipesConfigBase extends ConfigBase
{
    public static final long DEFAULT_MAX_FOR_EMIT_BATCH = 100000L;
    public static final long DEFAULT_TIMEOUT_MILLIS = 60000L;
    public static final long DEFAULT_STARTUP_TIMEOUT_MILLIS = 240000L;
    public static final long DEFAULT_SHUTDOWN_CLIENT_AFTER_MILLS = 300000L;
    public static final int DEFAULT_NUM_CLIENTS = 4;
    public static final int DEFAULT_MAX_FILES_PROCESSED_PER_PROCESS = 10000;
    private long maxForEmitBatchBytes;
    private long timeoutMillis;
    private long startupTimeoutMillis;
    private long shutdownClientAfterMillis;
    private int numClients;
    private int maxFilesProcessedPerProcess;
    private List<String> forkedJvmArgs;
    private Path tikaConfig;
    private String javaPath;
    
    public PipesConfigBase() {
        this.maxForEmitBatchBytes = 100000L;
        this.timeoutMillis = 60000L;
        this.startupTimeoutMillis = 240000L;
        this.shutdownClientAfterMillis = 300000L;
        this.numClients = 4;
        this.maxFilesProcessedPerProcess = 10000;
        this.forkedJvmArgs = new ArrayList<String>();
        this.javaPath = "java";
    }
    
    public long getTimeoutMillis() {
        return this.timeoutMillis;
    }
    
    public void setTimeoutMillis(final long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
    
    public long getShutdownClientAfterMillis() {
        return this.shutdownClientAfterMillis;
    }
    
    public void setShutdownClientAfterMillis(final long shutdownClientAfterMillis) {
        this.shutdownClientAfterMillis = shutdownClientAfterMillis;
    }
    
    public int getNumClients() {
        return this.numClients;
    }
    
    public void setNumClients(final int numClients) {
        this.numClients = numClients;
    }
    
    public List<String> getForkedJvmArgs() {
        final List<String> ret = new ArrayList<String>();
        ret.addAll(this.forkedJvmArgs);
        return ret;
    }
    
    public void setForkedJvmArgs(final List<String> jvmArgs) {
        this.forkedJvmArgs = Collections.unmodifiableList((List<? extends String>)jvmArgs);
    }
    
    public int getMaxFilesProcessedPerProcess() {
        return this.maxFilesProcessedPerProcess;
    }
    
    public void setMaxFilesProcessedPerProcess(final int maxFilesProcessedPerProcess) {
        this.maxFilesProcessedPerProcess = maxFilesProcessedPerProcess;
    }
    
    public Path getTikaConfig() {
        return this.tikaConfig;
    }
    
    public void setTikaConfig(final Path tikaConfig) {
        this.tikaConfig = tikaConfig;
    }
    
    public void setTikaConfig(final String tikaConfig) {
        this.setTikaConfig(Paths.get(tikaConfig, new String[0]));
    }
    
    public String getJavaPath() {
        return this.javaPath;
    }
    
    public void setJavaPath(final String javaPath) {
        this.javaPath = javaPath;
    }
    
    public long getStartupTimeoutMillis() {
        return this.startupTimeoutMillis;
    }
    
    public long getMaxForEmitBatchBytes() {
        return this.maxForEmitBatchBytes;
    }
    
    public void setMaxForEmitBatchBytes(final long maxForEmitBatchBytes) {
        this.maxForEmitBatchBytes = maxForEmitBatchBytes;
    }
}
