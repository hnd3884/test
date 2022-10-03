package com.unboundid.util;

import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

final class HTTPValuePatternComponent extends ValuePatternComponent
{
    private static final long serialVersionUID = 8879412445617836376L;
    private final String[] lines;
    private final Random seedRandom;
    private final ThreadLocal<Random> random;
    
    HTTPValuePatternComponent(final String url, final long seed) throws IOException {
        this.seedRandom = new Random(seed);
        this.random = new ThreadLocal<Random>();
        final ArrayList<String> lineList = new ArrayList<String>(100);
        final URL parsedURL = new URL(url);
        final HttpURLConnection urlConnection = (HttpURLConnection)parsedURL.openConnection();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
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
        Random r = this.random.get();
        if (r == null) {
            r = new Random(this.seedRandom.nextLong());
            this.random.set(r);
        }
        buffer.append(this.lines[r.nextInt(this.lines.length)]);
    }
    
    @Override
    boolean supportsBackReference() {
        return true;
    }
}
