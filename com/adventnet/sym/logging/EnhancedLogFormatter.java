package com.adventnet.sym.logging;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class EnhancedLogFormatter extends SimpleFormatter
{
    @Override
    public synchronized String format(final LogRecord record) {
        final String message = this.formatMessage(record);
        return LoggerUtil.defaultLogFormatter(record, message);
    }
}
