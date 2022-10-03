package org.apache.tika.pipes.fetcher;

import java.util.Set;
import org.apache.tika.exception.TikaException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import org.apache.tika.exception.TikaConfigException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Map;
import org.apache.tika.config.ConfigBase;

public class FetcherManager extends ConfigBase
{
    private final Map<String, Fetcher> fetcherMap;
    
    public static FetcherManager load(final Path p) throws IOException, TikaConfigException {
        try (final InputStream is = Files.newInputStream(p, new OpenOption[0])) {
            return ConfigBase.buildComposite("fetchers", FetcherManager.class, "fetcher", Fetcher.class, is);
        }
    }
    
    public FetcherManager(final List<Fetcher> fetchers) throws TikaConfigException {
        this.fetcherMap = new ConcurrentHashMap<String, Fetcher>();
        for (final Fetcher fetcher : fetchers) {
            final String name = fetcher.getName();
            if (name == null || name.trim().length() == 0) {
                throw new TikaConfigException("fetcher name must not be blank");
            }
            if (this.fetcherMap.containsKey(fetcher.getName())) {
                throw new TikaConfigException("Multiple fetchers cannot support the same prefix: " + fetcher.getName());
            }
            this.fetcherMap.put(fetcher.getName(), fetcher);
        }
    }
    
    public Fetcher getFetcher(final String fetcherName) throws IOException, TikaException {
        final Fetcher fetcher = this.fetcherMap.get(fetcherName);
        if (fetcher == null) {
            throw new IllegalArgumentException("Can't find fetcher for fetcherName: " + fetcherName + ". I've loaded: " + this.fetcherMap.keySet());
        }
        return fetcher;
    }
    
    public Set<String> getSupported() {
        return this.fetcherMap.keySet();
    }
}
