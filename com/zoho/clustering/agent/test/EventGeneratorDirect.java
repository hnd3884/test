package com.zoho.clustering.agent.test;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import com.zoho.clustering.util.FileUtil;
import java.util.logging.Level;
import com.zoho.clustering.agent.filerepl.ReplMaster;
import com.zoho.clustering.filerepl.event.EventLogger;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

public class EventGeneratorDirect implements Runnable
{
    private static Logger logger;
    private static final SimpleDateFormat TIME_FORMATTER;
    private static EventGeneratorDirect inst;
    private int baseDirId;
    private File baseDir;
    private EventLogger eventLogger;
    private int delayMillis;
    private Thread thread;
    
    public static void initialize(final ReplMaster replMaster, final int delayInSecs) {
        EventGeneratorDirect.inst = new EventGeneratorDirect(replMaster, delayInSecs);
        EventGeneratorDirect.logger.log(Level.INFO, "EventGeneratorDirect: initialized");
    }
    
    public static EventGeneratorDirect getInst() {
        if (EventGeneratorDirect.inst == null) {
            throw new IllegalStateException("Not yet initialized");
        }
        return EventGeneratorDirect.inst;
    }
    
    public EventGeneratorDirect(final ReplMaster replMaster, final int delayInSecs) {
        final Map.Entry<Integer, String> entry = replMaster.getDirectoryList().firstEntry();
        this.baseDirId = entry.getKey();
        FileUtil.assertDir(this.baseDir = new File(entry.getValue()));
        this.eventLogger = replMaster.getEventLogger();
        this.delayMillis = delayInSecs * 1000;
    }
    
    public void start() {
        if (this.thread != null) {
            throw new IllegalStateException("Already started.");
        }
        (this.thread = new Thread(this, "FileEvent-Generator")).start();
        EventGeneratorDirect.logger.log(Level.INFO, "EventGeneratorDirect: started");
    }
    
    public void stop() {
        if (this.thread != null && this.thread.isAlive()) {
            this.thread.interrupt();
            this.thread = null;
            EventGeneratorDirect.logger.log(Level.INFO, "EventGeneratorDirect: stopped");
        }
        else {
            EventGeneratorDirect.logger.log(Level.WARNING, "EventGeneratorDirect: already stopped");
        }
    }
    
    @Override
    public void run() {
        while (true) {
            final File[] files = new File[5];
            try {
                for (int i = 0; i < files.length; ++i) {
                    final Date date = new Date(System.currentTimeMillis());
                    this.createFile(files[i] = new File(this.baseDir, EventGeneratorDirect.TIME_FORMATTER.format(date) + ".txt"));
                }
                this.updateFile(files[0]);
                this.updateFile(files[2]);
                this.renameFile(files[4]);
            }
            catch (final InterruptedException ignored) {
                break;
            }
        }
    }
    
    private void createFile(final File file) throws InterruptedException {
        Thread.sleep(this.delayMillis);
        try {
            file.createNewFile();
            EventGeneratorDirect.logger.log(Level.FINE, "CREATE: {0}", file.getName());
            this.eventLogger.logCreateEvent(this.baseDirId, file.getName());
        }
        catch (final IOException exp) {
            exp.printStackTrace();
        }
    }
    
    private void updateFile(final File file) throws InterruptedException {
        Thread.sleep(this.delayMillis);
        this.writeToFile(file, file.getName());
        EventGeneratorDirect.logger.log(Level.FINE, "UPDATE: {0}", file.getName());
        this.eventLogger.logUpdateEvent(this.baseDirId, file.getName());
    }
    
    private void renameFile(final File file) throws InterruptedException {
        Thread.sleep(this.delayMillis);
        final File newFile = new File(file.getPath() + "~");
        file.renameTo(newFile);
        EventGeneratorDirect.logger.log(Level.FINE, "RENAME: {0} -> {1}", new Object[] { file.getName(), newFile.getName() });
        this.eventLogger.logRenameEvent(this.baseDirId, file.getName(), newFile.getName());
    }
    
    private void writeToFile(final File file, final String content) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
            fout.write(content.getBytes());
        }
        catch (final IOException exp) {
            throw new RuntimeException(exp);
        }
        finally {
            FileUtil.Close((Closeable)fout);
        }
    }
    
    static {
        EventGeneratorDirect.logger = Logger.getLogger(EventGeneratorDirect.class.getName());
        TIME_FORMATTER = new SimpleDateFormat("HH_mm_ss");
        EventGeneratorDirect.inst = null;
    }
}
