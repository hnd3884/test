package org.apache.tika.pipes.async;

import org.apache.tika.exception.TikaConfigException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import org.apache.tika.pipes.PipesReporter;
import org.apache.tika.pipes.PipesConfigBase;

public class AsyncConfig extends PipesConfigBase
{
    private long emitWithinMillis;
    private long emitMaxEstimatedBytes;
    private int queueSize;
    private int numEmitters;
    private PipesReporter pipesReporter;
    
    public AsyncConfig() {
        this.emitWithinMillis = 10000L;
        this.emitMaxEstimatedBytes = 100000L;
        this.queueSize = 10000;
        this.numEmitters = 1;
        this.pipesReporter = PipesReporter.NO_OP_REPORTER;
    }
    
    public static AsyncConfig load(final Path p) throws IOException, TikaConfigException {
        final AsyncConfig asyncConfig = new AsyncConfig();
        try (final InputStream is = Files.newInputStream(p, new OpenOption[0])) {
            asyncConfig.configure("async", is);
        }
        if (asyncConfig.getTikaConfig() == null) {
            asyncConfig.setTikaConfig(p);
        }
        return asyncConfig;
    }
    
    public long getEmitWithinMillis() {
        return this.emitWithinMillis;
    }
    
    public void setEmitWithinMillis(final long emitWithinMillis) {
        this.emitWithinMillis = emitWithinMillis;
    }
    
    public long getEmitMaxEstimatedBytes() {
        return this.emitMaxEstimatedBytes;
    }
    
    public void setEmitMaxEstimatedBytes(final long emitMaxEstimatedBytes) {
        this.emitMaxEstimatedBytes = emitMaxEstimatedBytes;
    }
    
    public void setNumEmitters(final int numEmitters) {
        this.numEmitters = numEmitters;
    }
    
    public int getQueueSize() {
        return this.queueSize;
    }
    
    public void setQueueSize(final int queueSize) {
        this.queueSize = queueSize;
    }
    
    public int getNumEmitters() {
        return this.numEmitters;
    }
    
    public PipesReporter getPipesReporter() {
        return this.pipesReporter;
    }
    
    public void setPipesReporter(final PipesReporter pipesReporter) {
        this.pipesReporter = pipesReporter;
    }
}
