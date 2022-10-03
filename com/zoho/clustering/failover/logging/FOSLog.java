package com.zoho.clustering.failover.logging;

import com.zoho.clustering.failover.FOSMain;
import java.util.logging.LogRecord;
import com.zoho.clustering.util.logger.LogFormatter;
import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.FileHandler;

public class FOSLog
{
    public static class Handler extends FileHandler
    {
        public Handler() throws IOException, SecurityException {
            this.setFormatter(new Formatter());
        }
    }
    
    public static class Formatter extends LogFormatter
    {
        @Override
        public String format(final LogRecord record) {
            final StringBuilder buff = new StringBuilder();
            buff.append(LogFormatter.brackets(this.getModeString())).append("|");
            buff.append(super.format(record));
            return buff.toString();
        }
        
        private String getModeString() {
            try {
                return FOSMain.getFOS().getMode().toString();
            }
            catch (final IllegalStateException exp) {
                return null;
            }
        }
    }
}
