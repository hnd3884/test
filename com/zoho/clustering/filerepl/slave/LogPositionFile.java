package com.zoho.clustering.filerepl.slave;

import java.io.Closeable;
import com.zoho.clustering.util.FileUtil;
import com.zoho.clustering.filerepl.MyRandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import com.zoho.clustering.filerepl.event.EventLogPosition;

public class LogPositionFile
{
    private EventLogPosition value;
    private File file;
    
    public static void main(final String[] args) {
        final Logger logger = Logger.getLogger(LogPositionFile.class.getName());
        final LogPositionFile logPosFile = new LogPositionFile(new File("slave", "tt.pos"));
        logPosFile.setValue(EventLogPosition.START_VALUE);
        logger.log(Level.INFO, String.valueOf(logPosFile.getValue()));
    }
    
    public LogPositionFile(final File file) {
        this.file = file;
        if (file.exists()) {
            if (!file.isFile()) {
                throw new IllegalArgumentException("[" + file.getAbsolutePath() + "] is not a file");
            }
            this.value = this.readFromFile();
        }
        else {
            this.setValue(EventLogPosition.START_VALUE);
        }
    }
    
    public EventLogPosition getValue() {
        return this.value;
    }
    
    public void setValue(final EventLogPosition newValue) {
        this.writeToFile(newValue);
        this.value = newValue;
    }
    
    private EventLogPosition readFromFile() {
        MyRandomAccessFile raf = null;
        try {
            raf = new MyRandomAccessFile(this.file, "r");
            final String str = raf.readLine();
            return (str != null) ? new EventLogPosition(str.trim()) : EventLogPosition.START_VALUE;
        }
        finally {
            FileUtil.Close(raf);
        }
    }
    
    private void writeToFile(final EventLogPosition logPos) {
        MyRandomAccessFile raf = null;
        try {
            raf = new MyRandomAccessFile(this.file, "rw");
            raf.writeBytes(logPos.serialize());
        }
        finally {
            FileUtil.Close(raf);
        }
    }
}
