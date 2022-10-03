package com.google.api.client.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.logging.Level;

public final class LoggingStreamingContent implements StreamingContent
{
    private final StreamingContent content;
    private final int contentLoggingLimit;
    private final Level loggingLevel;
    private final Logger logger;
    
    public LoggingStreamingContent(final StreamingContent content, final Logger logger, final Level loggingLevel, final int contentLoggingLimit) {
        this.content = content;
        this.logger = logger;
        this.loggingLevel = loggingLevel;
        this.contentLoggingLimit = contentLoggingLimit;
    }
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        final LoggingOutputStream loggableOutputStream = new LoggingOutputStream(out, this.logger, this.loggingLevel, this.contentLoggingLimit);
        try {
            this.content.writeTo(loggableOutputStream);
        }
        finally {
            loggableOutputStream.getLogStream().close();
        }
        out.flush();
    }
}
