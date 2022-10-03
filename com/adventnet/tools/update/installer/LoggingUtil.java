package com.adventnet.tools.update.installer;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.Frame;
import javax.swing.JFrame;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.PrintWriter;

public class LoggingUtil
{
    private PrintWriter logWriter;
    static File logFile;
    private ErrorDialog ed;
    
    public LoggingUtil() {
        this.logWriter = null;
        this.ed = null;
    }
    
    public void init(String filePath) {
        if (filePath == null || filePath.trim().equals("")) {
            filePath = "logs" + File.separator + "Log.txt";
        }
        LoggingUtil.logFile = new File(filePath);
        if (LoggingUtil.logFile.exists()) {
            LoggingUtil.logFile.delete();
        }
        createParentDirsIfNeeded(LoggingUtil.logFile);
        try {
            this.logWriter = new PrintWriter(new FileOutputStream(LoggingUtil.logFile.getPath(), true), true);
        }
        catch (final Exception excp) {
            System.err.println("Log File Not Found" + LoggingUtil.logFile.getPath());
        }
    }
    
    public ErrorDialog getInstanceForErrorDialog(final JFrame frame) {
        if (this.ed != null) {
            return this.ed;
        }
        return this.ed = new ErrorDialog(frame, true);
    }
    
    public void log(final String message) {
        this.logWriter.println(message);
    }
    
    public void fail(final String message, final Throwable thro) {
        this.logWriter.println(message);
        thro.printStackTrace(this.logWriter);
    }
    
    public void showError(final JFrame frame) {
        this.ed = this.getInstanceForErrorDialog(frame);
        try {
            final BufferedReader in = new BufferedReader(new FileReader(LoggingUtil.logFile.getPath()));
            for (String t = in.readLine(); t != null; t = in.readLine()) {
                this.ed.add(t);
            }
            this.ed.show();
        }
        catch (final Exception ex) {}
    }
    
    public static boolean createParentDirsIfNeeded(final File fileArg) {
        final File parentFile = fileArg.getParentFile();
        return parentFile.exists() || parentFile.mkdirs();
    }
    
    static {
        LoggingUtil.logFile = null;
    }
}
