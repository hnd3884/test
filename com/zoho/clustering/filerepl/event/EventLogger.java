package com.zoho.clustering.filerepl.event;

import java.io.EOFException;
import java.io.Closeable;
import java.util.logging.Level;
import java.io.File;
import com.zoho.clustering.util.FileUtil;
import com.zoho.clustering.filerepl.MyRandomAccessFile;
import com.zoho.clustering.filerepl.ErrorHandler;
import java.util.logging.Logger;

public class EventLogger
{
    private static Logger logger;
    public static final String FILENAME_PREFIX = "file-events.";
    public static final String INDEX_FILENAME = "curr-file.id";
    private String eventLogDir;
    private int maxBytesPerLog;
    private boolean deleteOldEvents;
    private ErrorHandler errorHandler;
    private IDFile fidFile;
    private MyRandomAccessFile writer;
    private int bytesCount;
    
    public static void main(final String[] args) {
        final EventLogger obj = new EventLogger(args[0], 5000, Boolean.valueOf(args[1]), null);
        EventLogPosition offset = new EventLogPosition("1:0");
        while (true) {
            System.out.print("\noffset: " + offset);
            final EventList eventList = obj.getEvents(offset, 100);
            if (eventList.getEvents().size() == 0) {
                break;
            }
            offset = eventList.getNextPos();
        }
    }
    
    public EventLogger(final String eventLogDir, final int maxBytesPerLog, final boolean deleteOldEvents, final ErrorHandler errorHandler) {
        this.eventLogDir = eventLogDir;
        this.maxBytesPerLog = maxBytesPerLog;
        this.deleteOldEvents = deleteOldEvents;
        this.errorHandler = errorHandler;
        FileUtil.assertOrCreateDir(eventLogDir);
        this.fidFile = new IDFile(new File(eventLogDir, "curr-file.id"), false);
        this.initializeWriter();
    }
    
    public String getEventLogDir() {
        return this.eventLogDir;
    }
    
    public int getMaxBytesPerLog() {
        return this.maxBytesPerLog;
    }
    
    private void initializeWriter() {
        final File file = new File(this.eventLogDir, "file-events." + this.fidFile.getValue());
        this.writer = new MyRandomAccessFile(file, "rw");
        final long pos = this.writer.length();
        if (pos > 0L) {
            this.writer.seek(pos);
            this.bytesCount = (int)pos;
        }
    }
    
    public void logCreateEvent(final int baseDirId, final String fileName) {
        this.logEvent(new FileEvent(FileEvent.Type.CREATE, baseDirId, fileName, null));
    }
    
    public void logUpdateEvent(final int baseDirId, final String fileName) {
        this.logEvent(new FileEvent(FileEvent.Type.UPDATE, baseDirId, fileName, null));
    }
    
    public void logDeleteEvent(final int baseDirId, final String fileName) {
        this.logEvent(new FileEvent(FileEvent.Type.DELETE, baseDirId, fileName, null));
    }
    
    public void logRenameEvent(final int baseDirId, final String oldName, final String newName) {
        this.logEvent(new FileEvent(FileEvent.Type.RENAME, baseDirId, oldName, newName));
    }
    
    public synchronized void logEvent(final FileEvent event) {
        EventLogger.logger.log(Level.FINE, "logEvent({0})", event.toString());
        try {
            final EventLogPosition currPos = this.getCurrentPosition();
            final String entry = event.serialize(currPos);
            this.writer.writeUTF(entry);
            this.bytesCount += entry.length();
            if (this.bytesCount > this.maxBytesPerLog) {
                this.doLogRollover();
            }
        }
        catch (final RuntimeException exp) {
            EventLogger.logger.log(Level.SEVERE, "logEvent error", exp);
            this.errorHandler.handleError(exp);
        }
    }
    
    private synchronized void doLogRollover() {
        this.bytesCount = 0;
        this.fidFile.incrValue();
        this.reconfigureWriter();
    }
    
    public synchronized EventLogPosition getCurrentPosition() {
        return new EventLogPosition(this.fidFile.getValue(), this.writer.getFilePointer());
    }
    
    public synchronized void resetLog() {
        FileUtil.Close(this.writer);
        final File[] listFiles;
        final File[] files = listFiles = new File(this.eventLogDir).listFiles();
        for (final File file : listFiles) {
            if (!file.delete()) {
                throw new RuntimeException("ResetLog() failed. Not able to delete the file [" + file.getAbsolutePath() + "]");
            }
        }
        this.bytesCount = 0;
        this.fidFile.setValue(1);
        this.reconfigureWriter();
    }
    
    private void reconfigureWriter() {
        FileUtil.Close(this.writer);
        final File file = new File(this.eventLogDir, "file-events." + this.fidFile.getValue());
        if (file.exists()) {
            throw new RuntimeException("Problem with flog. The file [" + file.getAbsolutePath() + "] is already present.");
        }
        this.writer = new MyRandomAccessFile(file, "rw");
    }
    
    public EventList getEvents(final EventLogPosition offset, final int noOfRecords) throws IllegalArgumentException {
        final EventList eventList = new EventList();
        int fileIndex = offset.fileIndex();
        if (fileIndex > this.fidFile.getValue()) {
            throw new IllegalArgumentException("Wrong LogPosition. No Such fileIndex [" + fileIndex + "]");
        }
        MyRandomAccessFile raf = null;
        try {
            final File file = new File(this.eventLogDir, "file-events." + fileIndex);
            if (!file.exists()) {
                throw new IllegalArgumentException("Requested events at " + offset + "are not available.File [" + file.getName() + "] is removed as per the 'cleanup' configuration.");
            }
            raf = new MyRandomAccessFile(file, "r");
            raf.seek(offset.byteIndex());
            int i = 0;
            while (i < noOfRecords) {
                String line = null;
                try {
                    line = raf.readUTF();
                }
                catch (final EOFException exp) {
                    line = null;
                }
                if (line != null) {
                    eventList.addEvent(line);
                    ++i;
                }
                else {
                    if (fileIndex >= this.fidFile.getValue()) {
                        break;
                    }
                    final File nextFile = new File(this.eventLogDir, "file-events." + (fileIndex + 1));
                    if (!nextFile.exists()) {
                        break;
                    }
                    FileUtil.Close(raf);
                    this.deleteEventFile(raf);
                    raf = new MyRandomAccessFile(nextFile, "r");
                    ++fileIndex;
                }
            }
            eventList.setNextPos(new EventLogPosition(fileIndex, raf.getFilePointer()));
            eventList.makeImmutable();
            return eventList;
        }
        finally {
            FileUtil.Close(raf);
        }
    }
    
    private void deleteEventFile(final MyRandomAccessFile raf) {
        if (this.deleteOldEvents) {
            final File rafFile = raf.getFile();
            if (rafFile.delete()) {
                EventLogger.logger.log(Level.INFO, "Deleted the FileEvent file [" + rafFile.getName() + "]");
            }
            else {
                EventLogger.logger.log(Level.WARNING, "The FileEvent file [" + rafFile.getAbsolutePath() + "] cannot be deleted");
            }
        }
    }
    
    static {
        EventLogger.logger = Logger.getLogger(EventLogger.class.getName());
    }
}
