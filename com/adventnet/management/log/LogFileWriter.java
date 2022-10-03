package com.adventnet.management.log;

import java.text.DateFormat;
import java.util.Calendar;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.util.Properties;

public class LogFileWriter extends LogBaseWriter
{
    private static final String LOG_CONTINUED_STR = " ~~~~~~~~~~~~~~~ Logging continued ~~~~~~~~~~~~~~~ ";
    protected String HEADER;
    protected static String c_line_separator;
    protected String logDirectory;
    protected String logFileName;
    protected String currentLogFileName;
    protected int maxFileCount;
    protected int maxLinesInFile;
    protected int lineCountInCurrentFile;
    protected int cacheLineCount;
    protected int maxLinesInMemory;
    protected boolean useTimeStamp;
    protected String[] m_lines;
    NmsFileUtil nfu;
    private NmsPrintWriter fileWriter;
    private int date;
    private int month;
    private int year;
    
    public void setHeader(final String header) {
        this.HEADER = header;
    }
    
    public void init(final Properties properties) {
        final String property;
        if ((property = properties.getProperty("FileName")) != null) {
            this.logFileName = property;
        }
        final String property2;
        if ((property2 = properties.getProperty("MaxLines")) != null) {
            this.maxLinesInFile = Integer.parseInt(property2);
        }
        final String property3;
        if ((property3 = properties.getProperty("FileCount")) != null) {
            this.maxFileCount = Integer.parseInt(property3);
        }
        final String property4;
        if ((property4 = properties.getProperty("MaxLinesCached")) != null) {
            this.maxLinesInMemory = Integer.parseInt(property4);
        }
        final String property5;
        if ((property5 = properties.getProperty("LogsDirectory")) != null) {
            this.logDirectory = property5;
        }
        final String property6;
        if ((property6 = properties.getProperty("UseTimeStamp")) != null) {
            this.useTimeStamp = new Boolean(property6);
        }
        this.m_lines = new String[this.maxLinesInMemory];
        if (LogFileWriter.c_line_separator == null) {
            LogFileWriter.c_line_separator = System.getProperty("line.separator");
            if (LogFileWriter.c_line_separator == null) {
                LogFileWriter.c_line_separator = "\n";
            }
        }
        this.currentLogFileName = this.logFileName;
        this.assignDay();
        this.setTheStartUpValues();
        this.createFileWriter();
    }
    
    public LogFileWriter() {
        this.HEADER = null;
        this.logDirectory = "logs";
        this.logFileName = null;
        this.currentLogFileName = null;
        this.maxFileCount = 10;
        this.maxLinesInFile = 10000;
        this.lineCountInCurrentFile = 0;
        this.cacheLineCount = 0;
        this.maxLinesInMemory = 0;
        this.useTimeStamp = true;
        this.m_lines = null;
        this.fileWriter = null;
    }
    
    public LogFileWriter(final String s, final String s2, final int n, final int n2, final int n3, final boolean b) {
        this.HEADER = null;
        this.logDirectory = "logs";
        this.logFileName = null;
        this.currentLogFileName = null;
        this.maxFileCount = 10;
        this.maxLinesInFile = 10000;
        this.lineCountInCurrentFile = 0;
        this.cacheLineCount = 0;
        this.maxLinesInMemory = 0;
        this.useTimeStamp = true;
        this.m_lines = null;
        this.fileWriter = null;
        final Properties properties = new Properties();
        properties.setProperty("FileName", s2);
        properties.setProperty("MaxLines", "" + n2);
        properties.setProperty("FileCount", "" + n);
        properties.setProperty("MaxLinesCached", "" + n3);
        properties.setProperty("LogsDirectory", s);
        properties.setProperty("UseTimeStamp", "" + b);
        this.init(properties);
    }
    
    private void setTheStartUpValues() {
        final File file = new File(this.logDirectory);
        file.mkdirs();
        this.nfu = new NmsFileUtil(file);
        final String fileMax = this.nfu.getFileMax(this.logFileName);
        try {
            if (fileMax == null || fileMax.equalsIgnoreCase("INVALID_FORMAT")) {
                throw new Exception();
            }
            if (fileMax.equalsIgnoreCase("NOT_PRESENT")) {
                this.currentLogFileName = this.logFileName;
            }
            else {
                this.currentLogFileName = this.nfu.getFileNext(fileMax);
            }
        }
        catch (final Exception ex) {
            System.err.println(" Directory Not Present or Invalid Format " + ex.toString());
        }
    }
    
    private void howManyToDelete() {
        int i = this.nfu.getFileCount(this.logFileName);
        try {
            if (i == -1 || i == -2) {
                throw new Exception();
            }
        }
        catch (final Exception ex) {
            System.err.println(" Directory Not Present or Invalid Format " + ex.toString());
        }
        String s = this.logFileName;
        if (i > this.maxFileCount) {
            while (i > this.maxFileCount) {
                if (this.deleteFile(s)) {
                    s = this.nfu.getFileNext(s);
                }
                else {
                    s = this.nfu.getFileNext(s);
                }
                i = this.nfu.getFileCount(this.logFileName);
                if (this.nfu.getFileIndex(s) > this.nfu.getFileIndex(this.nfu.getFileMax(this.logFileName))) {
                    break;
                }
            }
        }
    }
    
    private boolean deleteFile(final String s) {
        final File file = new File(new File(this.logDirectory), s);
        try {
            if (file.exists()) {
                return file.delete();
            }
        }
        catch (final Exception ex) {
            System.err.println("Exception in deletion of log files" + ex.toString());
        }
        return false;
    }
    
    public synchronized void log(String string) {
        String timeStamp = " ";
        if (this.useTimeStamp) {
            timeStamp = this.getTimeStamp();
        }
        string = timeStamp + string;
        if (this.maxLinesInMemory > 0) {
            if (this.cacheLineCount < this.maxLinesInMemory) {
                this.m_lines[this.cacheLineCount] = string;
                ++this.cacheLineCount;
            }
            else {
                this.flush();
                this.m_lines[this.cacheLineCount] = string;
                ++this.cacheLineCount;
            }
            return;
        }
        if (this.lineCountInCurrentFile >= this.maxLinesInFile) {
            this.lineCountInCurrentFile = 0;
            this.changeFile();
        }
        if (this.checkDay()) {
            this.logOne(this.getDayStartMessage());
        }
        this.logOne(string);
        this.flushFileContents();
    }
    
    public synchronized void logException(final Throwable t) {
        this.flush();
        if (this.checkDay()) {
            this.logOne(this.getDayStartMessage());
        }
        this.fileWriter.lineCount = 1;
        t.printStackTrace(this.fileWriter);
        this.lineCountInCurrentFile += this.fileWriter.lineCount;
        this.flushFileContents();
    }
    
    public synchronized void logStackTrace() {
        this.flush();
        final Throwable t = new Throwable();
        if (this.checkDay()) {
            this.logOne(this.getDayStartMessage());
        }
        this.fileWriter.lineCount = 1;
        t.printStackTrace(this.fileWriter);
        this.lineCountInCurrentFile += this.fileWriter.lineCount;
        this.flushFileContents();
    }
    
    protected synchronized void flush() {
        if (this.cacheLineCount == 0) {
            return;
        }
        if (this.lineCountInCurrentFile >= this.maxLinesInFile) {
            this.lineCountInCurrentFile = 0;
            this.changeFile();
        }
        for (int i = 0; i < this.m_lines.length; ++i) {
            if (this.checkDay()) {
                this.logOne(this.getDayStartMessage());
            }
            if (this.m_lines[i] != null) {
                this.logOne(this.m_lines[i]);
            }
        }
        this.m_lines = new String[this.maxLinesInMemory];
        this.flushFileContents();
        this.cacheLineCount = 0;
    }
    
    private void createFileWriter() {
        try {
            if (this.fileWriter != null) {
                this.fileWriter.close();
            }
            this.fileWriter = new NmsPrintWriter(new FileOutputStream(this.logDirectory + "/" + this.currentLogFileName), true);
            this.howManyToDelete();
        }
        catch (final Exception ex) {
            System.err.println("LogFileWriter: error opening " + this.currentLogFileName + ":" + ex);
            ex.printStackTrace();
        }
    }
    
    private void logOne(final String s) {
        final String string = s + LogFileWriter.c_line_separator;
        try {
            this.fileWriter.write(string, 0, string.length());
            ++this.lineCountInCurrentFile;
        }
        catch (final Exception ex) {
            System.err.println("LogFileWriter: error closing " + this.currentLogFileName + ":" + ex);
            ex.printStackTrace();
        }
    }
    
    private void changeFile() {
        final String fileNext = this.nfu.getFileNext(this.currentLogFileName);
        try {
            if (fileNext.equalsIgnoreCase("INVALID_FORMAT")) {
                throw new Exception();
            }
            this.currentLogFileName = fileNext;
        }
        catch (final Exception ex) {
            System.err.println("Invalid Format for log files ");
        }
        this.createFileWriter();
        final String timeStamp = this.getTimeStamp();
        this.logOne(timeStamp + " ~~~~~~~~~~~~~~~ Logging continued ~~~~~~~~~~~~~~~ ");
        this.logOne(timeStamp + LogMgr.getHeaderInfo());
        this.flushFileContents();
    }
    
    private void flushFileContents() {
        this.fileWriter.flush();
    }
    
    public void setMaxLinesInFile(final int maxLinesInFile) {
        this.maxLinesInFile = maxLinesInFile;
    }
    
    public void setMaxFileCount(final int maxFileCount) {
        this.maxFileCount = maxFileCount;
    }
    
    public void useTimeStamp(final boolean useTimeStamp) {
        this.useTimeStamp = useTimeStamp;
    }
    
    public void setCacheLineSize(final int maxLinesInMemory) {
        this.flush();
        this.maxLinesInMemory = maxLinesInMemory;
        this.m_lines = new String[this.maxLinesInMemory];
    }
    
    private void assignDay() {
        final Calendar instance = Calendar.getInstance();
        this.date = instance.get(5);
        this.month = instance.get(2) + 1;
        this.year = instance.get(1);
    }
    
    protected boolean checkDay() {
        final Calendar instance = Calendar.getInstance();
        if (instance.get(1) > this.year || instance.get(2) > this.month || instance.get(5) > this.date) {
            this.date = instance.get(5);
            this.month = instance.get(2) + 1;
            this.year = instance.get(1);
            return true;
        }
        return false;
    }
    
    private String getDayStartMessage() {
        return "Messages on ********" + DateFormat.getDateInstance(0).format(Calendar.getInstance().getTime()) + "********";
    }
    
    protected String getTimeStamp() {
        final Calendar instance = Calendar.getInstance();
        return this.add0(instance.get(11)) + ":" + this.add0(instance.get(12)) + ":" + this.add0(instance.get(13)) + ":" + this.add00(instance.get(14)) + " ";
    }
    
    private String add0(final int n) {
        if (n < 10) {
            return "0" + n;
        }
        return "" + n;
    }
    
    private String add00(final int n) {
        if (n < 10) {
            return "00" + n;
        }
        if (n < 100) {
            return "0" + n;
        }
        return "" + n;
    }
    
    static {
        LogFileWriter.c_line_separator = null;
    }
}
