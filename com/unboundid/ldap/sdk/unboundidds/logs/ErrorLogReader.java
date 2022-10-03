package com.unboundid.ldap.sdk.unboundidds.logs;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Closeable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ErrorLogReader implements Closeable
{
    private final BufferedReader reader;
    
    public ErrorLogReader(final String path) throws IOException {
        this.reader = new BufferedReader(new FileReader(path));
    }
    
    public ErrorLogReader(final File file) throws IOException {
        this.reader = new BufferedReader(new FileReader(file));
    }
    
    public ErrorLogReader(final Reader reader) {
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader)reader;
        }
        else {
            this.reader = new BufferedReader(reader);
        }
    }
    
    public ErrorLogMessage read() throws IOException, LogException {
        while (true) {
            final String line = this.reader.readLine();
            if (line == null) {
                return null;
            }
            if (line.isEmpty()) {
                continue;
            }
            if (line.charAt(0) == '#') {
                continue;
            }
            return new ErrorLogMessage(line);
        }
    }
    
    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
