package org.apache.tika.parser;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractExternalProcessParser extends AbstractParser
{
    private static final long serialVersionUID = 7186985395903074255L;
    private static final ConcurrentHashMap<String, Process> PROCESS_MAP;
    
    protected String register(final Process p) {
        final String id = UUID.randomUUID().toString();
        AbstractExternalProcessParser.PROCESS_MAP.put(id, p);
        return id;
    }
    
    protected Process release(final String id) {
        return AbstractExternalProcessParser.PROCESS_MAP.remove(id);
    }
    
    static {
        PROCESS_MAP = new ConcurrentHashMap<String, Process>();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> AbstractExternalProcessParser.PROCESS_MAP.forEachValue(1L, Process::destroyForcibly)));
    }
}
