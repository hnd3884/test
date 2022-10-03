package org.apache.tika.pipes.emitter;

import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tika.exception.TikaConfigException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Map;
import org.apache.tika.config.ConfigBase;

public class EmitterManager extends ConfigBase
{
    private final Map<String, Emitter> emitterMap;
    
    public static EmitterManager load(final Path tikaConfigPath) throws IOException, TikaConfigException {
        try (final InputStream is = Files.newInputStream(tikaConfigPath, new OpenOption[0])) {
            return ConfigBase.buildComposite("emitters", EmitterManager.class, "emitter", Emitter.class, is);
        }
    }
    
    private EmitterManager() {
        this.emitterMap = new ConcurrentHashMap<String, Emitter>();
    }
    
    public EmitterManager(final List<Emitter> emitters) {
        this.emitterMap = new ConcurrentHashMap<String, Emitter>();
        for (final Emitter emitter : emitters) {
            if (this.emitterMap.containsKey(emitter.getName())) {
                throw new IllegalArgumentException("Multiple emitters cannot support the same name: " + emitter.getName());
            }
            this.emitterMap.put(emitter.getName(), emitter);
        }
    }
    
    public Set<String> getSupported() {
        return this.emitterMap.keySet();
    }
    
    public Emitter getEmitter(final String emitterName) {
        final Emitter emitter = this.emitterMap.get(emitterName);
        if (emitter == null) {
            throw new IllegalArgumentException("Can't find emitter for prefix: " + emitterName);
        }
        return emitter;
    }
}
