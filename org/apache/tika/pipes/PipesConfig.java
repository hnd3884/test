package org.apache.tika.pipes;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaConfigException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public class PipesConfig extends PipesConfigBase
{
    private long maxWaitForClientMillis;
    
    public static PipesConfig load(final Path tikaConfig) throws IOException, TikaConfigException {
        final PipesConfig pipesConfig = new PipesConfig();
        try (final InputStream is = Files.newInputStream(tikaConfig, new OpenOption[0])) {
            pipesConfig.configure("pipes", is);
        }
        if (pipesConfig.getTikaConfig() == null) {
            throw new TikaConfigException("must specify at least a <tikaConfig> element in the <params> of <pipes>");
        }
        return pipesConfig;
    }
    
    private PipesConfig() {
        this.maxWaitForClientMillis = 60000L;
    }
    
    public long getMaxWaitForClientMillis() {
        return this.maxWaitForClientMillis;
    }
    
    public void setMaxWaitForClientMillis(final long maxWaitForClientMillis) {
        this.maxWaitForClientMillis = maxWaitForClientMillis;
    }
}
