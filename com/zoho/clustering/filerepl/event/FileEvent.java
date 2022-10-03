package com.zoho.clustering.filerepl.event;

import java.util.logging.Level;
import java.util.Date;
import java.text.ParseException;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

public class FileEvent
{
    private static final SimpleDateFormat TIME_FORMATTER;
    private static final SimpleDateFormat DATE_FORMATTER;
    private static final SimpleDateFormat DATETIME_FORMATTER;
    private static final String FIELD_SEP = ",";
    private static final Pattern PATTERN_FIELD_SEP;
    private long timestamp;
    private Type type;
    private int baseDirId;
    private String filePath;
    private String newName;
    private static Logger logger;
    
    public FileEvent(final Type type, final int baseDirId, final String fileName, final String newName) {
        this(System.currentTimeMillis(), type, baseDirId, fileName, newName);
    }
    
    public FileEvent(final long timestamp, final Type type, final int baseDirId, final String fileName, final String newName) {
        this.timestamp = timestamp;
        this.type = type;
        this.baseDirId = baseDirId;
        this.filePath = fileName.replace('\\', '/');
        this.newName = ((newName == null) ? null : newName.replace('\\', '/'));
    }
    
    public FileEvent(final String eventStr) {
        final String[] parts = FileEvent.PATTERN_FIELD_SEP.split(eventStr);
        if (parts.length != 6 && parts.length != 7) {
            throw new IllegalArgumentException("Incorrect Event string [" + eventStr + "]");
        }
        try {
            this.timestamp = FileEvent.DATETIME_FORMATTER.parse(parts[1].trim() + "_" + parts[2].trim()).getTime();
        }
        catch (final ParseException exp) {
            throw new IllegalArgumentException("Incorrect Event string [" + eventStr + "].Not able to extract the 'timestamp' property");
        }
        this.type = Type.valueOf(parts[3].trim());
        try {
            this.baseDirId = Integer.parseInt(parts[4].trim());
        }
        catch (final NumberFormatException exp2) {
            throw new IllegalArgumentException("Incorrect Event string [" + eventStr + "].Expecting int value for 'groupId'");
        }
        this.filePath = parts[5].trim();
        if (parts.length == 7) {
            this.newName = parts[6].trim();
        }
    }
    
    @Override
    public String toString() {
        return this.serialize(null);
    }
    
    public String serialize(final EventLogPosition currPos) {
        final StringBuilder buff = new StringBuilder();
        final Date date = new Date(this.timestamp);
        if (currPos != null) {
            buff.append("#").append(currPos.serialize()).append(",");
        }
        buff.append(FileEvent.TIME_FORMATTER.format(date)).append(",");
        buff.append(FileEvent.DATE_FORMATTER.format(date)).append(",");
        buff.append(this.type).append(",");
        buff.append(this.baseDirId).append(",");
        buff.append(this.filePath).append(",");
        buff.append((this.newName == null) ? "" : this.newName);
        buff.append("\n");
        return buff.toString();
    }
    
    public Type getType() {
        return this.type;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    private void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
    
    public int getBaseDirId() {
        return this.baseDirId;
    }
    
    public String getFilePath() {
        return this.filePath;
    }
    
    public String getNewName() {
        return this.newName;
    }
    
    private boolean isSameFileEvent(final FileEvent other) {
        return this.baseDirId == other.getBaseDirId() && (this.filePath.equals(other.getFilePath()) || (this.type == Type.RENAME && this.newName.equals(other.getFilePath())));
    }
    
    public boolean merge(final FileEvent other) {
        if (!this.isSameFileEvent(other)) {
            return false;
        }
        switch (this.type) {
            case CREATE: {
                return this.merge_handleCreateState(other);
            }
            case DELETE: {
                return this.merge_handleDeleteState(other);
            }
            case UPDATE: {
                return this.merge_handleUpdateState(other);
            }
            case RENAME: {
                return this.merge_handleRenameState(other);
            }
            case NO_OP: {
                return this.merge_handleNoOpState(other);
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean merge_handleCreateState(final FileEvent event) {
        switch (event.getType()) {
            case CREATE: {
                this.handleSequenceError(event);
                return true;
            }
            case DELETE: {
                this.type = Type.NO_OP;
                return true;
            }
            case RENAME: {
                this.filePath = event.getNewName();
                return true;
            }
            default: {
                return true;
            }
        }
    }
    
    private boolean merge_handleDeleteState(final FileEvent event) {
        switch (event.getType()) {
            case NO_OP: {
                return true;
            }
            case CREATE: {
                this.timestamp = event.getTimestamp();
                this.type = Type.CREATE;
                return true;
            }
            default: {
                this.handleSequenceError(event);
                return true;
            }
        }
    }
    
    private boolean merge_handleUpdateState(final FileEvent event) {
        switch (event.getType()) {
            case CREATE: {
                this.handleSequenceError(event);
                return true;
            }
            case DELETE: {
                this.type = Type.DELETE;
                return true;
            }
            case RENAME: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    private boolean merge_handleRenameState(final FileEvent event) {
        switch (event.getType()) {
            case CREATE: {
                this.handleSequenceError(event);
                return true;
            }
            case DELETE: {
                this.type = Type.DELETE;
                this.newName = null;
                return true;
            }
            case UPDATE: {
                return event.getTimestamp() - this.timestamp < 11L;
            }
            case RENAME: {
                this.newName = event.getNewName();
                return true;
            }
            default: {
                return true;
            }
        }
    }
    
    private boolean merge_handleNoOpState(final FileEvent event) {
        this.timestamp = event.getTimestamp();
        this.type = event.getType();
        this.baseDirId = event.getBaseDirId();
        this.filePath = event.getFilePath();
        this.newName = event.getNewName();
        return true;
    }
    
    private void handleSequenceError(final FileEvent event) {
        FileEvent.logger.log(Level.WARNING, "Wrong Sequence [" + this.type + "," + event.getType() + "].Ignoring the Event [" + event + "]");
    }
    
    static {
        TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss:SSS");
        DATE_FORMATTER = new SimpleDateFormat("MM-dd-yyyy");
        DATETIME_FORMATTER = new SimpleDateFormat("HH:mm:ss:SSS_MM-dd-yyyy");
        PATTERN_FIELD_SEP = Pattern.compile(",");
        FileEvent.logger = Logger.getLogger(FileEvent.class.getName());
    }
    
    public enum Type
    {
        CREATE, 
        UPDATE, 
        DELETE, 
        RENAME, 
        NO_OP;
    }
    
    public static class Test
    {
        private static Logger logger;
        
        public static void main(final String[] args) throws InterruptedException {
            final FileEvent[] events = { rename("tt.1", "tt.2"), rename("tt.2", "tt.3"), rename("tt.3", "tt.4") };
            FileEvent tmp = events[0];
            for (int i = 1; i < events.length; ++i) {
                final FileEvent event = events[i];
                final boolean merged = tmp.merge(event);
                if (!merged) {
                    Test.logger.log(Level.INFO, tmp.toString());
                    tmp = event;
                }
            }
            Test.logger.log(Level.INFO, tmp.toString());
        }
        
        public static FileEvent create(final String fileName) {
            return new FileEvent(Type.CREATE, 1, fileName, null);
        }
        
        public static FileEvent update(final String fileName) {
            return new FileEvent(Type.UPDATE, 1, fileName, null);
        }
        
        public static FileEvent delete(final String fileName) {
            return new FileEvent(Type.DELETE, 1, fileName, null);
        }
        
        public static FileEvent rename(final String oldName, final String newName) {
            return new FileEvent(Type.RENAME, 1, oldName, newName);
        }
        
        static {
            Test.logger = Logger.getLogger(Test.class.getName());
        }
    }
}
