package com.unboundid.util;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

final class FileValuePatternComponent extends ValuePatternComponent
{
    private static final long serialVersionUID = 2773328295435703361L;
    private final AtomicLong sequentialCounter;
    private final boolean sequential;
    private final String[] lines;
    private final Random seedRandom;
    private final ThreadLocal<Random> random;
    
    FileValuePatternComponent(final String path, final long seed, final boolean sequential) throws IOException {
        this.sequential = sequential;
        this.sequentialCounter = new AtomicLong(0L);
        this.seedRandom = new Random(seed);
        this.random = new ThreadLocal<Random>();
        final ArrayList<String> lineList = new ArrayList<String>(100);
        final BufferedReader reader = new BufferedReader(new FileReader(path));
        try {
            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                lineList.add(line);
            }
        }
        finally {
            reader.close();
        }
        if (lineList.isEmpty()) {
            throw new IOException(UtilityMessages.ERR_VALUE_PATTERN_COMPONENT_EMPTY_FILE.get());
        }
        lineList.toArray(this.lines = new String[lineList.size()]);
    }
    
    @Override
    void append(final StringBuilder buffer) {
        int index;
        if (this.sequential) {
            index = (int)(this.sequentialCounter.getAndIncrement() % this.lines.length);
        }
        else {
            Random r = this.random.get();
            if (r == null) {
                r = new Random(this.seedRandom.nextLong());
                this.random.set(r);
            }
            index = r.nextInt(this.lines.length);
        }
        buffer.append(this.lines[index]);
    }
    
    @Override
    boolean supportsBackReference() {
        return true;
    }
}
